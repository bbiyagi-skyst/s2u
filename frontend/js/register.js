import { postForm } from './auth.js';

document.getElementById('register-submit').addEventListener('click', async (e) => {
  e.preventDefault();
  const username = document.getElementById('username').value;
  const email    = document.getElementById('email').value;
  const password = document.getElementById('password').value;

  const res = await postForm('/api/auth/register', { username, email, password });
  if (res.status === 201) {
    alert('회원가입 성공! 로그인 페이지로 이동합니다.');
    window.location.href = 'login.html';
  } else if (res.status === 409) {
    alert('이미 사용 중인 이메일입니다.');
  } else {
    const err = await res.json();
    alert('회원가입 실패: ' + (err.message || res.status));
  }
});
