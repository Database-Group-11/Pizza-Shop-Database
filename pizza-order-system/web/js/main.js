// ========== API 配置 ==========
const API_BASE = 'http://localhost:8080/api';

// ========== 公共工具函数 ==========

// 获取当前登录用户
function getCurrentUser() {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
}

// 设置登录状态
function setCurrentUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user));
}

// 退出登录
function logout() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('cart');
    window.location.href = 'login.html';
}

// 显示错误消息
function showError(elementId, message) {
    const errorEl = document.getElementById(elementId);
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
        setTimeout(() => {
            errorEl.style.display = 'none';
        }, 3000);
    }
}

// ========== 购物车公共函数 ==========
function getCart() {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : [];
}

function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
    // 更新所有页面的购物车角标
    const cartCountSpans = document.querySelectorAll('#cartCount');
    const count = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
    cartCountSpans.forEach(span => {
        if (span) span.textContent = count;
    });
}

function addToCart(pizza, selectedToppings, quantity) {
    const cart = getCart();

    // 计算配料总价
    const toppingsTotal = selectedToppings.reduce((sum, t) => sum + (t.price * t.quantity), 0);
    const itemTotal = (pizza.basePrice + toppingsTotal) * quantity;

    const cartItem = {
        id: Date.now(),
        pizzaId: pizza.pizzaId,
        pizzaName: pizza.name,
        basePrice: pizza.basePrice,
        toppings: selectedToppings,
        quantity: quantity,
        total: itemTotal
    };

    cart.push(cartItem);
    saveCart(cart);
    alert(`已添加 ${quantity} 份 ${pizza.name} 到购物车`);
}

// ========== 状态映射（文档格式 → 中文显示）==========
function getStatusText(status) {
    const map = {
        'pending': '⏳ 待支付',
        'paid': '✅ 已支付',
        'preparing': '🍳 制作中',
        'delivering': '🚚 配送中',
        'completed': '🎉 已完成',
        'cancelled': '❌ 已取消'
    };
    return map[status] || status;
}

function getStatusClass(status) {
    const map = {
        'pending': 'status-pending',
        'paid': 'status-paid',
        'preparing': 'status-preparing',
        'delivering': 'status-delivering',
        'completed': 'status-completed',
        'cancelled': 'status-cancelled'
    };
    return map[status] || 'status-pending';
}

function formatDate(dateStr) {
    if (!dateStr) return '--';
    const d = new Date(dateStr);
    return `${d.getMonth()+1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2,'0')}`;
}

// ========== 登录页面逻辑 ==========
if (document.getElementById('loginForm')) {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;

        if (!phone || !password) {
            showError('errorMsg', '请填写手机号和密码');
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/customer/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ phone, password })
            });
            const result = await response.json();
            if (result.code === 200) {
                // 转换字段名（后端返回的是 customerId）
                const user = {
                    customerId: result.data.customerId,
                    name: result.data.name,
                    phone: result.data.phone,
                    address: result.data.address
                };
                setCurrentUser(user);
                window.location.href = 'menu.html';
            } else {
                showError('errorMsg', result.message || '登录失败');
            }
        } catch (error) {
            showError('errorMsg', '网络错误，请稍后重试');
        }
    });
}

// ========== 注册页面逻辑 ==========
if (document.getElementById('registerForm')) {
    const registerForm = document.getElementById('registerForm');

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const name = document.getElementById('name').value;
        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const address = document.getElementById('address').value;

        if (!name || !phone || !password || !address) {
            showError('errorMsg', '请填写所有字段');
            return;
        }

        if (password !== confirmPassword) {
            showError('errorMsg', '两次输入的密码不一致');
            return;
        }

        if (!/^1[3-9]\d{9}$/.test(phone)) {
            showError('errorMsg', '请输入正确的手机号');
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/customer/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, phone, password, address })
            });
            const result = await response.json();
            if (result.code === 200) {
                alert('注册成功！请登录');
                window.location.href = 'login.html';
            } else {
                showError('errorMsg', result.message || '注册失败');
            }
        } catch (error) {
            showError('errorMsg', '网络错误，请稍后重试');
        }
    });
}

// 导出函数供全局使用
window.getCurrentUser = getCurrentUser;
window.setCurrentUser = setCurrentUser;
window.logout = logout;
window.getCart = getCart;
window.saveCart = saveCart;
window.addToCart = addToCart;
window.getStatusText = getStatusText;
window.getStatusClass = getStatusClass;
window.formatDate = formatDate;
window.API_BASE = API_BASE;