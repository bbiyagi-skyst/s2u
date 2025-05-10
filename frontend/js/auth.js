const API_BASE = 'http://localhost:4000';

export async function postForm(path, data) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(data).toString(),
  });
  return res;
}

export async function getForm(path, params) {
  const qs = new URLSearchParams(params).toString();
  const res = await fetch(`${API_BASE}${path}?${qs}`, {
    method: 'GET',
  });
  return res;
}

export function saveToken(token) {
  sessionStorage.setItem('token', token);
}

export function getToken() {
  return sessionStorage.getItem('token');
}

export function clearToken() {
  sessionStorage.removeItem('token');
}
