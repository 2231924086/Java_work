// DOM元素
const loginTab = document.getElementById('login-tab');
const registerTab = document.getElementById('register-tab');
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');

// 登录表单元素
const loginUsername = document.getElementById('login-username');
const loginPassword = document.getElementById('login-password');
const loginBtn = document.getElementById('login-btn');
const loginBtnText = document.getElementById('login-btn-text');
const loginLoading = document.getElementById('login-loading');
const loginSuccess = document.getElementById('login-success');
const loginError = document.getElementById('login-error');

// 注册表单元素
const registerUsername = document.getElementById('register-username');
const registerEmail = document.getElementById('register-email');
const registerPassword = document.getElementById('register-password');
const registerConfirmPassword = document.getElementById('register-confirm-password');
const registerBtn = document.getElementById('register-btn');
const registerBtnText = document.getElementById('register-btn-text');
const registerLoading = document.getElementById('register-loading');
const registerSuccess = document.getElementById('register-success');
const registerError = document.getElementById('register-error');

// API端点
const API = {
    LOGIN: '/api/user/login',
    REGISTER: '/api/user/register',
    CHECK_USERNAME: '/api/user/checkUsername',
    CHECK_EMAIL: '/api/user/checkEmail'
};

// 标签切换
loginTab.addEventListener('click', () => {
    loginTab.classList.add('active');
    registerTab.classList.remove('active');
    loginForm.classList.add('active');
    registerForm.classList.remove('active');
    
    // 清除错误和成功消息
    clearMessages();
});

registerTab.addEventListener('click', () => {
    registerTab.classList.add('active');
    loginTab.classList.remove('active');
    registerForm.classList.add('active');
    loginForm.classList.remove('active');
    
    // 清除错误和成功消息
    clearMessages();
});

// 清除消息
function clearMessages() {
    loginSuccess.style.display = 'none';
    loginError.style.display = 'none';
    registerSuccess.style.display = 'none';
    registerError.style.display = 'none';
    
    // 清除表单错误状态
    document.querySelectorAll('.form-group').forEach(group => {
        group.classList.remove('error');
    });
}

// 表单验证
function validateLoginForm() {
    let isValid = true;
    
    // 验证用户名
    if (!loginUsername.value.trim()) {
        setError(loginUsername, '请输入用户名');
        isValid = false;
    } else {
        clearError(loginUsername);
    }
    
    // 验证密码
    if (!loginPassword.value) {
        setError(loginPassword, '请输入密码');
        isValid = false;
    } else {
        clearError(loginPassword);
    }
    
    return isValid;
}

function validateRegisterForm() {
    let isValid = true;
    
    // 验证用户名
    if (!registerUsername.value.trim() || registerUsername.value.length < 3 || registerUsername.value.length > 20) {
        setError(registerUsername, '用户名需要3-20个字符');
        isValid = false;
    } else {
        clearError(registerUsername);
    }
    
    // 验证邮箱
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!registerEmail.value.trim() || !emailRegex.test(registerEmail.value)) {
        setError(registerEmail, '请输入有效的邮箱地址');
        isValid = false;
    } else {
        clearError(registerEmail);
    }
    
    // 验证密码
    if (!registerPassword.value || registerPassword.value.length < 6) {
        setError(registerPassword, '密码需要至少6个字符');
        isValid = false;
    } else {
        clearError(registerPassword);
    }
    
    // 验证确认密码
    if (registerPassword.value !== registerConfirmPassword.value) {
        setError(registerConfirmPassword, '两次输入的密码不一致');
        isValid = false;
    } else {
        clearError(registerConfirmPassword);
    }
    
    return isValid;
}

// 设置错误提示
function setError(input, message) {
    const formGroup = input.parentElement;
    const errorMessage = formGroup.querySelector('.error-message');
    formGroup.classList.add('error');
    errorMessage.textContent = message;
}

// 清除错误提示
function clearError(input) {
    const formGroup = input.parentElement;
    formGroup.classList.remove('error');
}

// 显示加载状态
function setLoading(button, btnText, loadingSpinner, isLoading) {
    if (isLoading) {
        button.disabled = true;
        btnText.style.display = 'none';
        loadingSpinner.style.display = 'inline-block';
    } else {
        button.disabled = false;
        btnText.style.display = 'inline-block';
        loadingSpinner.style.display = 'none';
    }
}

// 检查用户名是否存在
async function checkUsernameExists(username) {
    try {
        const response = await fetch(`${API.CHECK_USERNAME}?username=${encodeURIComponent(username)}`);
        const data = await response.json();
        return data.exists;
    } catch (error) {
        console.error('检查用户名出错:', error);
        return false;
    }
}

// 检查邮箱是否存在
async function checkEmailExists(email) {
    try {
        const response = await fetch(`${API.CHECK_EMAIL}?email=${encodeURIComponent(email)}`);
        const data = await response.json();
        return data.exists;
    } catch (error) {
        console.error('检查邮箱出错:', error);
        return false;
    }
}

// 处理登录
loginBtn.addEventListener('click', async () => {
    // 清除之前的错误和成功消息
    loginSuccess.style.display = 'none';
    loginError.style.display = 'none';
    
    // 表单验证
    if (!validateLoginForm()) {
        return;
    }
    
    // 显示加载状态
    setLoading(loginBtn, loginBtnText, loginLoading, true);
    
    try {
        const formData = new URLSearchParams();
        formData.append('username', loginUsername.value);
        formData.append('password', loginPassword.value);
        
        const response = await fetch(API.LOGIN, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            // 登录成功
            loginSuccess.style.display = 'block';
            loginSuccess.textContent = '登录成功！正在跳转到主页...';
            // 跳转到主页
            setTimeout(() => {
                window.location.href = '/index.html';
            }, 1500);
        } else {
            // 登录失败
            loginError.style.display = 'block';
            loginError.textContent = data.message || '登录失败，请检查用户名和密码';
        }
    } catch (error) {
        console.error('登录出错:', error);
        loginError.style.display = 'block';
        loginError.textContent = '登录请求失败，请稍后再试';
    } finally {
        // 恢复按钮状态
        setLoading(loginBtn, loginBtnText, loginLoading, false);
    }
});

// 处理注册
registerBtn.addEventListener('click', async () => {
    // 清除之前的错误和成功消息
    registerSuccess.style.display = 'none';
    registerError.style.display = 'none';
    
    // 表单验证
    if (!validateRegisterForm()) {
        return;
    }
    
    // 显示加载状态
    setLoading(registerBtn, registerBtnText, registerLoading, true);
    
    try {
        // 检查用户名是否存在
        const usernameExists = await checkUsernameExists(registerUsername.value);
        if (usernameExists) {
            setError(registerUsername, '用户名已被使用');
            setLoading(registerBtn, registerBtnText, registerLoading, false);
            return;
        }
        
        // 检查邮箱是否存在
        const emailExists = await checkEmailExists(registerEmail.value);
        if (emailExists) {
            setError(registerEmail, '邮箱已被使用');
            setLoading(registerBtn, registerBtnText, registerLoading, false);
            return;
        }
        
        const formData = new URLSearchParams();
        formData.append('username', registerUsername.value);
        formData.append('email', registerEmail.value);
        formData.append('password', registerPassword.value);
        
        const response = await fetch(API.REGISTER, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData
        });
        
        const data = await response.json();
        
        if (data.success) {
            // 注册成功
            registerSuccess.style.display = 'block';
            registerSuccess.textContent = '注册成功！请登录';
            
            // 清空表单
            registerUsername.value = '';
            registerEmail.value = '';
            registerPassword.value = '';
            registerConfirmPassword.value = '';
            
            // 延迟切换到登录页
            setTimeout(() => {
                loginTab.click();
            }, 1500);
        } else {
            // 注册失败
            registerError.style.display = 'block';
            registerError.textContent = data.message || '注册失败，请稍后再试';
        }
    } catch (error) {
        console.error('注册出错:', error);
        registerError.style.display = 'block';
        registerError.textContent = '注册请求失败，请稍后再试';
    } finally {
        // 恢复按钮状态
        setLoading(registerBtn, registerBtnText, registerLoading, false);
    }
});

// 实时验证
registerUsername.addEventListener('blur', async () => {
    if (registerUsername.value.trim() && registerUsername.value.length >= 3 && registerUsername.value.length <= 20) {
        const exists = await checkUsernameExists(registerUsername.value);
        if (exists) {
            setError(registerUsername, '用户名已被使用');
        }
    }
});

registerEmail.addEventListener('blur', async () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (registerEmail.value.trim() && emailRegex.test(registerEmail.value)) {
        const exists = await checkEmailExists(registerEmail.value);
        if (exists) {
            setError(registerEmail, '邮箱已被使用');
        }
    }
});

// 按回车键提交表单
loginPassword.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        loginBtn.click();
    }
});

registerConfirmPassword.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        registerBtn.click();
    }
});