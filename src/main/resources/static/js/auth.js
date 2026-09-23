// 统一前端鉴权工具：
// 1) 自动为 fetch 注入 JWT Token（登录后 token 存 localStorage）
// 2) 接口返回 401 时自动跳转登录页
(function () {
    var originalFetch = window.fetch;
    window.fetch = function (url, options) {
        options = options || {};
        var token = localStorage.getItem('token');
        if (token) {
            var headers = options.headers;
            if (headers instanceof Headers) {
                if (!headers.has('Authorization')) headers.set('Authorization', 'Bearer ' + token);
            } else if (headers && typeof headers === 'object') {
                if (!headers.Authorization) options.headers = Object.assign({}, headers, { 'Authorization': 'Bearer ' + token });
            } else {
                options.headers = { 'Authorization': 'Bearer ' + token };
            }
        }
        return originalFetch.call(this, url, options).then(function (resp) {
            if (resp.status === 401) {
                var u = typeof url === 'string' ? url : (url && url.url) || '';
                if (u.indexOf('/api/auth/') === -1) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    if (window.location.pathname !== '/login') window.location.href = '/login';
                }
            }
            return resp;
        });
    };
})();

// 退出登录：清除本地凭证并跳转登录页
window.logout = function () {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
};
