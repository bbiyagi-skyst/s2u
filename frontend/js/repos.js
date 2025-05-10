// public/js/repos.js
import { postForm, getForm } from './auth.js';
import { requireAuth, logout } from './common.js';

const token = requireAuth();

// 네비게이션 버튼
document.getElementById('logout').addEventListener('click', logout);
document.getElementById('new-repo-btn').addEventListener('click', () => {
  window.location.href = 'repo.html?id=-1';
});

(async () => {
  // 리포지토리 목록 요청
  const res = await postForm('/api/repos', { token });
  if (!res.ok) {
    alert('리포지토리 목록을 불러오는 데 실패했습니다.');
    return;
  }

  const repos = await res.json();
  const list = document.getElementById('repo-list');

  for (const r of repos) {
    // 작성자 이름 조회 (fallback: ID)
    let username = r.created_by;
    try {
      const userRes = await getForm('/api/users/get', {
        token,
        userId: r.created_by
      });
      if (userRes.ok) {
        const { username: name } = await userRes.json();
        username = name;
      }
    } catch (e) {
      console.error('작성자 정보 조회 오류:', e);
    }

    // 리스트 아이템 생성
    const li = document.createElement('li');
    // 전체 영역 클릭 가능
    li.addEventListener('click', () => {
      window.location.href = `repoview.html?id=${r.id}`;
    });

    // 리포지토리 이름
    const nameSpan = document.createElement('span');
    nameSpan.className = 'repo-name';
    nameSpan.textContent = r.name;
    li.appendChild(nameSpan);

    // 메타 정보
    const infoDiv = document.createElement('div');
    infoDiv.className = 'repo-info';
    infoDiv.textContent = `작성자: ${username} | 생성: ${r.created_at} | 수정: ${r.updated_at}`;
    li.appendChild(infoDiv);

    list.appendChild(li);
  }
})();
