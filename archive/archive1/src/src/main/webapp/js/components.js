// 任务列表组件
const TaskList = {
    props: {
        tasks: {
            type: Array,
            required: true
        },
        categories: {
            type: Array,
            required: true
        }
    },
    template: `
        <div class="task-list">
            <!-- 搜索和过滤 -->
            <div class="filter-container">
                <el-input
                    v-model="searchKeyword"
                    placeholder="搜索任务"
                    class="search-input"
                    @input="handleSearch">
                    <template #prefix>
                        <el-icon><search /></el-icon>
                    </template>
                </el-input>
                
                <el-select v-model="filterCategory" placeholder="按分类筛选" @change="handleFilter">
                    <el-option label="全部分类" value=""></el-option>
                    <el-option
                        v-for="category in categories"
                        :key="category.categoryId"
                        :label="category.name"
                        :value="category.categoryId">
                    </el-option>
                </el-select>

                <el-select v-model="filterStatus" placeholder="按状态筛选" @change="handleFilter">
                    <el-option label="全部状态" value=""></el-option>
                    <el-option label="待处理" value="PENDING"></el-option>
                    <el-option label="进行中" value="IN_PROGRESS"></el-option>
                    <el-option label="已完成" value="COMPLETED"></el-option>
                </el-select>

                <el-select v-model="filterPriority" placeholder="按优先级筛选" @change="handleFilter">
                    <el-option label="全部优先级" value=""></el-option>
                    <el-option label="高" value="3"></el-option>
                    <el-option label="中" value="2"></el-option>
                    <el-option label="低" value="1"></el-option>
                </el-select>
            </div>

            <!-- 新建任务按钮 -->
            <el-button type="primary" @click="showCreateDialog" class="mb-20">
                <el-icon><plus /></el-icon>新建任务
            </el-button>

            <!-- 任务列表 -->
            <el-card v-for="task in filteredTasks" :key="task.taskId" class="task-item">
                <div class="task-header">
                    <div class="task-title">
                        {{ task.title }}
                        <el-tag :type="getStatusType(task.status)" size="small">
                            {{ getStatusText(task.status) }}
                        </el-tag>
                        <el-tag :type="getPriorityType(task.priority)" size="small">
                            {{ getPriorityText(task.priority) }}
                        </el-tag>
                    </div>
                    <div class="task-actions">
                        <el-button-group>
                            <el-button size="small" @click="handleEdit(task)">
                                <el-icon><edit /></el-icon>
                            </el-button>
                            <el-button size="small" type="danger" @click="handleDelete(task)">
                                <el-icon><delete /></el-icon>
                            </el-button>
                        </el-button-group>
                    </div>
                </div>
                <div class="task-content">{{ task.content }}</div>
                <div class="task-footer">
                    <div class="task-info">
                        <el-tag size="small" type="info">
                            {{ getCategoryName(task.categoryId) }}
                        </el-tag>
                        <span class="due-date" :class="{ 'is-overdue': isOverdue(task.dueDate) }">
                            截止日期：{{ formatDate(task.dueDate) }}
                        </span>
                    </div>
                    <div class="task-status">
                        <el-button
                            v-if="task.status !== 'COMPLETED'"
                            size="small"
                            type="success"
                            @click="handleComplete(task)">
                            标记完成
                        </el-button>
                    </div>
                </div>
            </el-card>

            <!-- 空状态 -->
            <el-empty v-if="filteredTasks.length === 0" description="暂无任务"></el-empty>

            <!-- 新建/编辑任务对话框 -->
            <el-dialog
                :title="dialogType === 'create' ? '新建任务' : '编辑任务'"
                v-model="dialogVisible"
                width="500px">
                <el-form :model="taskForm" :rules="taskRules" ref="taskFormRef" label-width="100px">
                    <el-form-item label="标题" prop="title">
                        <el-input v-model="taskForm.title"></el-input>
                    </el-form-item>
                    <el-form-item label="内容" prop="content">
                        <el-input type="textarea" v-model="taskForm.content" rows="3"></el-input>
                    </el-form-item>
                    <el-form-item label="分类" prop="categoryId">
                        <el-select v-model="taskForm.categoryId" placeholder="选择分类">
                            <el-option
                                v-for="category in categories"
                                :key="category.categoryId"
                                :label="category.name"
                                :value="category.categoryId">
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="优先级" prop="priority">
                        <el-select v-model="taskForm.priority" placeholder="选择优先级">
                            <el-option label="高" :value="3"></el-option>
                            <el-option label="中" :value="2"></el-option>
                            <el-option label="低" :value="1"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="截止日期" prop="dueDate">
                        <el-date-picker
                            v-model="taskForm.dueDate"
                            type="datetime"
                            placeholder="选择截止日期">
                        </el-date-picker>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="handleSubmit" :loading="loading">
                            确定
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            searchKeyword: '',
            filterCategory: '',
            filterStatus: '',
            filterPriority: '',
            dialogVisible: false,
            dialogType: 'create',
            loading: false,
            taskForm: {
                title: '',
                content: '',
                categoryId: '',
                priority: 2,
                dueDate: null
            },
            taskRules: {
                title: [
                    { required: true, message: '请输入任务标题', trigger: 'blur' },
                    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
                ],
                content: [
                    { required: true, message: '请输入任务内容', trigger: 'blur' }
                ],
                categoryId: [
                    { required: true, message: '请选择分类', trigger: 'change' }
                ],
                priority: [
                    { required: true, message: '请选择优先级', trigger: 'change' }
                ],
                dueDate: [
                    { required: true, message: '请选择截止日期', trigger: 'change' }
                ]
            }
        }
    },
    computed: {
        filteredTasks() {
            return this.tasks.filter(task => {
                const matchKeyword = !this.searchKeyword ||
                    task.title.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
                    task.content.toLowerCase().includes(this.searchKeyword.toLowerCase());
                
                const matchCategory = !this.filterCategory ||
                    task.categoryId === parseInt(this.filterCategory);
                
                const matchStatus = !this.filterStatus ||
                    task.status === this.filterStatus;
                
                const matchPriority = !this.filterPriority ||
                    task.priority === parseInt(this.filterPriority);
                
                return matchKeyword && matchCategory && matchStatus && matchPriority;
            });
        }
    },
    methods: {
        showCreateDialog() {
            this.dialogType = 'create';
            this.taskForm = {
                title: '',
                content: '',
                categoryId: '',
                priority: 2,
                dueDate: null
            };
            this.dialogVisible = true;
        },
        handleEdit(task) {
            this.dialogType = 'edit';
            this.taskForm = { ...task };
            this.dialogVisible = true;
        },
        async handleSubmit() {
            if (!this.$refs.taskFormRef) return;
            
            await this.$refs.taskFormRef.validate(async (valid) => {
                if (valid) {
                    this.loading = true;
                    try {
                        if (this.dialogType === 'create') {
                            await this.$emit('create', this.taskForm);
                        } else {
                            await this.$emit('update', this.taskForm);
                        }
                        this.dialogVisible = false;
                        this.$message.success(this.dialogType === 'create' ? '创建成功' : '更新成功');
                    } catch (error) {
                        this.$message.error(error.message || '操作失败');
                    } finally {
                        this.loading = false;
                    }
                }
            });
        },
        async handleDelete(task) {
            try {
                await this.$confirm('确认删除该任务？', '提示', {
                    type: 'warning'
                });
                await this.$emit('delete', task.taskId);
                this.$message.success('删除成功');
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error(error.message || '删除失败');
                }
            }
        },
        async handleComplete(task) {
            try {
                await this.$emit('update', {
                    ...task,
                    status: 'COMPLETED'
                });
                this.$message.success('任务已完成');
            } catch (error) {
                this.$message.error(error.message || '操作失败');
            }
        },
        handleSearch() {
            // 搜索逻辑已通过计算属性实现
        },
        handleFilter() {
            // 过滤逻辑已通过计算属性实现
        },
        getStatusType(status) {
            const types = {
                'PENDING': 'info',
                'IN_PROGRESS': 'warning',
                'COMPLETED': 'success'
            };
            return types[status] || 'info';
        },
        getStatusText(status) {
            const texts = {
                'PENDING': '待处理',
                'IN_PROGRESS': '进行中',
                'COMPLETED': '已完成'
            };
            return texts[status] || status;
        },
        getPriorityType(priority) {
            const types = {
                3: 'danger',
                2: 'warning',
                1: 'success'
            };
            return types[priority] || 'info';
        },
        getPriorityText(priority) {
            const texts = {
                3: '高',
                2: '中',
                1: '低'
            };
            return texts[priority] || '未知';
        },
        getCategoryName(categoryId) {
            const category = this.categories.find(c => c.categoryId === categoryId);
            return category ? category.name : '未分类';
        },
        formatDate(date) {
            return new Date(date).toLocaleString();
        },
        isOverdue(date) {
            return new Date(date) < new Date() && !this.task.status === 'COMPLETED';
        }
    }
};

// 分类管理组件
const CategoryManager = {
    props: {
        categories: {
            type: Array,
            required: true
        }
    },
    template: `
        <div class="category-list">
            <!-- 新建分类按钮 -->
            <el-button type="primary" @click="showCreateDialog" class="mb-20">
                <el-icon><plus /></el-icon>新建分类
            </el-button>

            <!-- 分类列表 -->
            <el-card v-for="category in categories" :key="category.categoryId" class="category-item">
                <div class="category-info">
                    <div class="category-name">{{ category.name }}</div>
                    <div class="category-description">{{ category.description }}</div>
                </div>
                <div class="category-actions">
                    <el-button-group>
                        <el-button size="small" @click="handleEdit(category)">
                            <el-icon><edit /></el-icon>
                        </el-button>
                        <el-button size="small" type="danger" @click="handleDelete(category)">
                            <el-icon><delete /></el-icon>
                        </el-button>
                    </el-button-group>
                </div>
            </el-card>

            <!-- 空状态 -->
            <el-empty v-if="categories.length === 0" description="暂无分类"></el-empty>

            <!-- 新建/编辑分类对话框 -->
            <el-dialog
                :title="dialogType === 'create' ? '新建分类' : '编辑分类'"
                v-model="dialogVisible"
                width="400px">
                <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="80px">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="categoryForm.name"></el-input>
                    </el-form-item>
                    <el-form-item label="描述" prop="description">
                        <el-input type="textarea" v-model="categoryForm.description" rows="3"></el-input>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="handleSubmit" :loading="loading">
                            确定
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    `,
    data() {
        return {
            dialogVisible: false,
            dialogType: 'create',
            loading: false,
            categoryForm: {
                name: '',
                description: ''
            },
            categoryRules: {
                name: [
                    { required: true, message: '请输入分类名称', trigger: 'blur' },
                    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
                ],
                description: [
                    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
                ]
            }
        }
    },
    methods: {
        showCreateDialog() {
            this.dialogType = 'create';
            this.categoryForm = {
                name: '',
                description: ''
            };
            this.dialogVisible = true;
        },
        handleEdit(category) {
            this.dialogType = 'edit';
            this.categoryForm = { ...category };
            this.dialogVisible = true;
        },
        async handleSubmit() {
            if (!this.$refs.categoryFormRef) return;
            
            await this.$refs.categoryFormRef.validate(async (valid) => {
                if (valid) {
                    this.loading = true;
                    try {
                        if (this.dialogType === 'create') {
                            await this.$emit('create', this.categoryForm);
                        } else {
                            await this.$emit('update', this.categoryForm);
                        }
                        this.dialogVisible = false;
                        this.$message.success(this.dialogType === 'create' ? '创建成功' : '更新成功');
                    } catch (error) {
                        this.$message.error(error.message || '操作失败');
                    } finally {
                        this.loading = false;
                    }
                }
            });
        },
        async handleDelete(category) {
            try {
                await this.$confirm('确认删除该分类？删除后相关任务将变为未分类状态。', '提示', {
                    type: 'warning'
                });
                await this.$emit('delete', category.categoryId);
                this.$message.success('删除成功');
            } catch (error) {
                if (error !== 'cancel') {
                    this.$message.error(error.message || '删除失败');
                }
            }
        }
    }
};

// 即将到期任务组件
const UpcomingTasks = {
    props: {
        tasks: {
            type: Array,
            required: true
        }
    },
    template: `
        <div class="upcoming-tasks">
            <!-- 时间范围选择 -->
            <div class="filter-container">
                <el-select v-model="days" placeholder="选择时间范围" @change="handleDaysChange">
                    <el-option label="最近3天" :value="3"></el-option>
                    <el-option label="最近7天" :value="7"></el-option>
                    <el-option label="最近15天" :value="15"></el-option>
                    <el-option label="最近30天" :value="30"></el-option>
                </el-select>
            </div>

            <!-- 即将到期任务列表 -->
            <el-card
                v-for="task in tasks"
                :key="task.taskId"
                class="upcoming-task-item"
                :class="{ 'urgent': isUrgent(task) }">
                <div class="task-header">
                    <div class="task-title">
                        {{ task.title }}
                        <el-tag :type="getPriorityType(task.priority)" size="small">
                            {{ getPriorityText(task.priority) }}
                        </el-tag>
                    </div>
                    <div class="task-actions">
                        <el-button size="small" @click="handleEdit(task)">
                            <el-icon><edit /></el-icon>
                        </el-button>
                    </div>
                </div>
                <div class="task-content">{{ task.content }}</div>
                <div class="task-footer">
                    <div class="task-info">
                        <span class="due-date" :class="{ 'is-overdue': isOverdue(task.dueDate) }">
                            截止日期：{{ formatDate(task.dueDate) }}
                        </span>
                        <span class="days-left">
                            剩余：{{ getDaysLeft(task.dueDate) }}天
                        </span>
                    </div>
                    <div class="task-status">
                        <el-button
                            v-if="task.status !== 'COMPLETED'"
                            size="small"
                            type="success"
                            @click="handleComplete(task)">
                            标记完成
                        </el-button>
                    </div>
                </div>
            </el-card>

            <!-- 空状态 -->
            <el-empty v-if="tasks.length === 0" description="暂无即将到期的任务"></el-empty>
        </div>
    `,
    data() {
        return {
            days: 7
        }
    },
    methods: {
        handleDaysChange() {
            this.$emit('days-change', this.days);
        },
        handleEdit(task) {
            this.$emit('edit', task);
        },
        async handleComplete(task) {
            try {
                await this.$emit('complete', task);
                this.$message.success('任务已完成');
            } catch (error) {
                this.$message.error(error.message || '操作失败');
            }
        },
        getPriorityType(priority) {
            const types = {
                3: 'danger',
                2: 'warning',
                1: 'success'
            };
            return types[priority] || 'info';
        },
        getPriorityText(priority) {
            const texts = {
                3: '高',
                2: '中',
                1: '低'
            };
            return texts[priority] || '未知';
        },
        formatDate(date) {
            return new Date(date).toLocaleString();
        },
        getDaysLeft(date) {
            const now = new Date();
            const dueDate = new Date(date);
            const diffTime = dueDate - now;
            return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        },
        isOverdue(date) {
            return new Date(date) < new Date();
        },
        isUrgent(task) {
            const daysLeft = this.getDaysLeft(task.dueDate);
            return daysLeft <= 3 && task.priority >= 2;
        }
    }
};

// 注册组件
const app = Vue.createApp({});
app.component('task-list', TaskList);
app.component('category-manager', CategoryManager);
app.component('upcoming-tasks', UpcomingTasks); 