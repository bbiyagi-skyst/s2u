import { postForm, saveToken } from './auth.js';

document.getElementById('login-submit').addEventListener('click', async (e) => {
  e.preventDefault();
  const email    = document.getElementById('email').value;
  const password = document.getElementById('password').value;

  const res = await postForm('/api/auth/login', { email, password });
  if (res.status === 200) {
    const { token } = await res.json();
    saveToken(token);
    window.location.href = 'index.html';
  } else {
    alert('로그인 실패: ' + res.status);
  }
});
