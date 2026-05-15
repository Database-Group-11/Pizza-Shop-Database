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
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartCountSpans.forEach(span => {
        if (span) span.textContent = count;
    });
}

function addToCart(pizza, selectedToppings, quantity) {
    const cart = getCart();

    // 计算配料总价
    const toppingsTotal = selectedToppings.reduce((sum, t) => sum + (t.price * t.quantity), 0);
    const itemTotal = (pizza.base_price + toppingsTotal) * quantity;

    const cartItem = {
        id: Date.now(),
        pizza_id: pizza.pizza_id,
        pizza_name: pizza.name,
        base_price: pizza.base_price,
        toppings: selectedToppings,
        quantity: quantity,
        total: itemTotal
    };

    cart.push(cartItem);
    saveCart(cart);
    alert(`已添加 ${quantity} 份 ${pizza.name} 到购物车`);
}

// ========== 模拟数据 ==========
const MOCK_PIZZAS = [
    { pizza_id: 1, name: '经典玛格丽特', description: '新鲜番茄 + 罗勒叶 + 马苏里拉芝士', base_price: 39, category: '经典', image: '图片1' },
    { pizza_id: 2, name: '超级至尊', description: '意式香肠 + 培根 + 牛肉粒 + 蘑菇', base_price: 59, category: '肉类', image: '图片2' },
    { pizza_id: 3, name: '田园素食', description: '青椒 + 玉米 + 蘑菇 + 橄榄', base_price: 45, category: '素食', image: '图片3' },
    { pizza_id: 4, name: '海鲜总汇', description: '鲜虾 + 鱿鱼 + 蟹柳 + 青椒', base_price: 69, category: '海鲜', image: '图片4' },
    { pizza_id: 5, name: '夏威夷风情', description: '火腿 + 菠萝 + 芝士', base_price: 49, category: '经典', image: '图片5' },
    { pizza_id: 6, name: '黑椒牛肉', description: '黑椒牛肉 + 洋葱 + 青椒', base_price: 55, category: '肉类', image: '图片6' },
    { pizza_id: 7, name: '四重芝士', description: '四种芝士混合，浓郁拉丝', base_price: 52, category: '经典', image: '图片7' },
    { pizza_id: 8, name: '蘑菇鸡肉', description: '鸡胸肉 + 蘑菇 + 玉米', base_price: 48, category: '肉类', image: '图片8' }
];

const MOCK_TOPPINGS = [
    { topping_id: 1, name: '加芝士', price: 8, stock_quantity: 100 },
    { topping_id: 2, name: '培根', price: 10, stock_quantity: 50 },
    { topping_id: 3, name: '蘑菇', price: 6, stock_quantity: 80 },
    { topping_id: 4, name: '青椒', price: 5, stock_quantity: 60 },
    { topping_id: 5, name: '玉米', price: 5, stock_quantity: 70 },
    { topping_id: 6, name: '洋葱', price: 4, stock_quantity: 90 }
];

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

        // 模拟登录成功
        const mockUser = {
            customer_id: 1,
            name: '测试用户',
            phone: phone,
            address: '北京市朝阳区xxx'
        };

        setCurrentUser(mockUser);
        window.location.href = 'menu.html';
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

        alert('注册成功！请登录');
        window.location.href = 'login.html';
    });
}

// ========== 菜单页逻辑 ==========
if (document.getElementById('pizzaGrid')) {
    let currentCategory = 'all';

    // 检查登录状态
    const user = getCurrentUser();
    const userNameSpan = document.getElementById('userNameDisplay');
    if (!user) {
        window.location.href = 'login.html';
    } else {
        if (userNameSpan) {
            userNameSpan.textContent = `欢迎，${user.name}`;
        }
    }

    // 更新购物车数量显示
    function updateCartCount() {
        const cart = getCart();
        const count = cart.reduce((sum, item) => sum + item.quantity, 0);
        const cartCountSpan = document.getElementById('cartCount');
        if (cartCountSpan) cartCountSpan.textContent = count;
    }

    // 渲染披萨列表
    function renderPizzas() {
        let filtered = MOCK_PIZZAS;
        if (currentCategory !== 'all') {
            filtered = MOCK_PIZZAS.filter(p => p.category === currentCategory);
        }

        const grid = document.getElementById('pizzaGrid');
        if (!grid) return;

        grid.innerHTML = filtered.map(pizza => `
            <div class="pizza-card">
                < img src="${pizza.image}" alt="${pizza.name}">
                <div class="info">
                    <h3>${pizza.name}</h3>
                    <div class="desc">${pizza.description}</div>
                    <div class="price">¥${pizza.base_price}</div>
                    <button onclick="viewPizzaDetail(${pizza.pizza_id})">定制 & 加入购物车</button>
                </div>
            </div>
        `).join('');
    }

    // 查看披萨详情
    window.viewPizzaDetail = function(pizzaId) {
        const pizza = MOCK_PIZZAS.find(p => p.pizza_id === pizzaId);
        if (pizza) {
            sessionStorage.setItem('selectedPizza', JSON.stringify(pizza));
            window.location.href = 'pizza_detail.html';
        }
    };

    // 分类切换
    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentCategory = btn.dataset.category;
            renderPizzas();
        });
    });

    // 退出登录
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            logout();
        });
    }

    // 初始化
    renderPizzas();
    updateCartCount();
}

// 导出函数供全局使用
window.getCurrentUser = getCurrentUser;
window.setCurrentUser = setCurrentUser;
window.logout = logout;
window.getCart = getCart;
window.saveCart = saveCart;
window.addToCart = addToCart;
window.MOCK_PIZZAS = MOCK_PIZZAS;
window.MOCK_TOPPINGS = MOCK_TOPPINGS;