import { postForm, getToken, clearToken } from './auth.js';
export function requireAuth() {
  const token = getToken();
  if (!token) window.location.href = 'login.html';
  return token;
}
export function logout() {
  const token = requireAuth();
  postForm('/api/auth/logout', { token });
  clearToken();
  window.location.href = 'login.html';
}