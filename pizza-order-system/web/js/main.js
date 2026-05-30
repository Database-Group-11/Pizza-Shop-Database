// ========== API Config ==========
const API_BASE = 'http://localhost:8080';

// ========== Login page logic ==========
if (document.getElementById('loginForm')) {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;

        if (!phone || !password) {
            showError('errorMsg', 'Please enter phone number and password');
            return;
        }

        // ========== Temporary test account (bypasses backend) ==========
        // Enter 13800138000 as phone, any password to login
        if (phone === '13800138000') {
            const testUser = {
                customerId: 1,
                name: 'Test User',
                phone: '13800138000',
                address: '123 Test Road, Chaoyang District, Beijing'
            };
            setCurrentUser(testUser);
            window.location.href = 'menu.html';
            return;
        }
        // ========== Test account end ==========

        // Normal backend login (enable when backend is ready)
        try {
            const response = await fetch(`${API_BASE}/api/customer/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ phone, password }),
                credentials: 'include'
            });
            const result = await response.json();
            if (result.code === 200) {
                const user = {
                    customerId: result.data.customerId,
                    name: result.data.name,
                    phone: result.data.phone,
                    address: result.data.address
                };
                setCurrentUser(user);
                window.location.href = 'menu.html';
            } else {
                showError('errorMsg', result.message || 'Login failed');
            }
        } catch (error) {
            showError('errorMsg', 'Network error, please try again later');
        }
    });
}
// ========== Common utility functions ==========

// Get current logged-in user
function getCurrentUser() {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
}

// Set login state
function setCurrentUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user));
}

// Logout
function logout() {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('cart');
    window.location.href = 'login.html';
}

// Show error message
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

// ========== Shopping cart common functions ==========
function getCart() {
    const cart = localStorage.getItem('cart');
    return cart ? JSON.parse(cart) : [];
}

function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
    // Update cart badge on all pages
    const cartCountSpans = document.querySelectorAll('#cartCount');
    const count = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
    cartCountSpans.forEach(span => {
        if (span) span.textContent = count;
    });
}

function addToCart(pizza, selectedToppings, quantity) {
    const cart = getCart();

    // Ensure pizzaId exists (compatible with both pizza_id and pizzaId formats)
    const pizzaId = pizza.pizzaId || pizza.pizza_id;

    console.log('Add to cart - pizza:', pizza);
    console.log('Add to cart - pizzaId:', pizzaId);

    // Calculate total toppings price
    const toppingsTotal = selectedToppings.reduce((sum, t) => sum + (t.price * t.quantity), 0);
    const basePrice = pizza.basePrice || pizza.base_price || 0;
    const itemTotal = (basePrice + toppingsTotal) * quantity;

    const cartItem = {
        id: Date.now(),
        pizzaId: pizzaId,                    // ← Key: must save pizzaId
        pizzaName: pizza.name,
        basePrice: basePrice,
        toppings: selectedToppings,
        quantity: quantity,
        total: itemTotal
    };

    console.log('Item added to cart:', cartItem);

    cart.push(cartItem);
    saveCart(cart);
    alert(`Added ${quantity}x ${pizza.name} to cart`);
}

// ========== Status mapping (doc format → display text) ==========
function getStatusText(status) {
    const map = {
        'pending': '⏳ Pending',
        'paid': '✅ Paid',
        'preparing': '🍳 Preparing',
        'delivering': '🚚 Delivering',
        'completed': '🎉 Completed',
        'cancelled': '❌ Cancelled'
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

// ========== Login page logic ==========
if (document.getElementById('loginForm')) {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;

        if (!phone || !password) {
            showError('errorMsg', 'Please enter phone number and password');
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/api/customer/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ phone, password })
            });
            const result = await response.json();
            if (result.code === 200) {
                // Map field names (backend returns customerId)
                const user = {
                    customerId: result.data.customerId,
                    name: result.data.name,
                    phone: result.data.phone,
                    address: result.data.address
                };
                setCurrentUser(user);
                window.location.href = 'menu.html';
            } else {
                showError('errorMsg', result.message || 'Login failed');
            }
        } catch (error) {
            showError('errorMsg', 'Network error, please try again later');
        }
    });
}

// ========== Register page logic ==========
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
            showError('errorMsg', 'Please fill in all fields');
            return;
        }

        if (password !== confirmPassword) {
            showError('errorMsg', 'Passwords do not match');
            return;
        }

        if (!/^1[3-9]\d{9}$/.test(phone)) {
            showError('errorMsg', 'Please enter a valid phone number');
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/api/customer/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, phone, password, address })
            });
            const result = await response.json();
            if (result.code === 200) {
                alert('Registration successful! Please login');
                window.location.href = 'login.html';
            } else {
                showError('errorMsg', result.message || 'Registration failed');
            }
        } catch (error) {
            showError('errorMsg', 'Network error, please try again later');
        }
    });
}

// Export functions for global use
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

