import { postForm } from './auth.js';
import { requireAuth, logout } from './common.js';

// 초기화
const token = requireAuth();
document.getElementById('logout').addEventListener('click', logout);

(async function loadRepos() {
  const res = await postForm('/api/repos', { token });
  if (res.ok) {
    const repos = await res.json();
    const list = document.getElementById('repo-list');
    repos.forEach(r => {
      const li = document.createElement('li');
      const a = document.createElement('a');
      a.href = `repo.html?id=${r.id}`;
      a.textContent = r.name;
      li.appendChild(a);
      list.appendChild(li);
    });
  }
})();

// 생성
document.getElementById('create-submit').addEventListener('click', async () => {
  const name = document.getElementById('new-name').value;
  const description = document.getElementById('new-desc').value;
  const content = document.getElementById('new-content').value;
  const res = await postForm('/api/repos', { token, name, description, content });
  if (res.status === 201) window.location.reload();
});
