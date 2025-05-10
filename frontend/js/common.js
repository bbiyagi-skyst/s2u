export function requireAuth() {
  const token = sessionStorage.getItem('token');
  if (!token) window.location.href = 'login.html';
  return token;
}

export function logout() {
  const token = requireAuth();
  postForm('/api/auth/logout', { token });
  clearToken();
  window.location.href = 'login.html';
}
