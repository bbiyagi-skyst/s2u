import { postForm } from './auth.js';
import { requireAuth, logout } from './common.js';

const params = new URLSearchParams(location.search);
const repoId = params.get('id');
const token = requireAuth();

document.getElementById('logout').addEventListener('click', logout);
document.getElementById('back').addEventListener('click', () => window.location.href = 'index.html');

async function loadRepo() {
  const res = await postForm('/api/repos/get', { token, repoId });
  if (res.ok) {
    const repo = await res.json();
    document.getElementById('repo-name').textContent = repo.name;
    document.getElementById('edit-name').value = repo.name;
    document.getElementById('edit-desc').value = repo.description;
    document.getElementById('edit-content').value = repo.content;
  }
}
loadRepo();

// 업데이트
document.getElementById('update-submit').addEventListener('click', async () => {
  const name = document.getElementById('edit-name').value;
  const description = document.getElementById('edit-desc').value;
  const content = document.getElementById('edit-content').value;
  const res = await postForm('/api/repos/update', { token, repoId, name, description, content });
  if (res.ok) alert('저장되었습니다.');
});

// 삭제
document.getElementById('delete-submit').addEventListener('click', async () => {
  if (confirm('정말 삭제하시겠습니까?')) {
    const res = await postForm('/api/repos/delete', { token, repoId });
    if (res.status === 204) window.location.href = 'index.html';
  }
});
