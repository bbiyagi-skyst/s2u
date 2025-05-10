const API_BASE = 'https://skyst-sample.jhyub.dev';

export async function postForm(path, data) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(data).toString(),
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
