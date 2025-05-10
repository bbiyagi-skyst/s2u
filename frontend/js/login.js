import { postForm, saveToken } from './auth.js';


document.getElementById('login-submit').addEventListener('click', loginTodo);


async function loginTodo(e) {
  e.preventDefault();
  const email = document.getElementById('email').value.trim();
  const password = document.getElementById('password').value;
  const res = await postForm('/api/auth/login', { email, password });
  console.log(res);
  if (res.status === 200) {
    const { token } = await res.json();
    saveToken(token);
    window.location.href = 'index.html';
  } else if (res.status === 401) {
    alert('이메일 또는 비밀번호가 올바르지 않습니다.');
  } else {
    alert('로그인 중 오류가 발생했습니다: ' + res.status);
  }
}



