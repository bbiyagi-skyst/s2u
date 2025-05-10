// public/js/repoview.js
import { getForm, postForm } from './auth.js';
import { requireAuth, logout } from './common.js';

(async () => {
  // 1) 토큰 & repoId 가져오기
  const token  = requireAuth();
  const params = new URLSearchParams(window.location.search);
  const repoId = params.get('id');
  console.log(repoId);
  // DOM 참조
  const nameDisplay = document.getElementById('name-display');
  const nameInput   = document.getElementById('name-input');
  const descInput   = document.getElementById('description');
  const abcInput    = document.getElementById('abc-input');
  const backBtn     = document.getElementById('back-btn');
  const logoutBtn   = document.getElementById('logout-btn');

  // 2) 기존 리포 조회 or 새 리포 초기화
  let repo;
  if (repoId != -1) {

    const res = await getForm('/api/repos/get', { token, repoId });
    if (!res.ok) {
      console.error('리포지토리 조회 실패:', res.status);
      return;
    }
    repo = await res.json();
    // 화면 채우기
    nameDisplay.textContent        = repo.name;
    nameInput.value                = repo.name;
    descInput.value                = repo.description;
    abcInput.value                 = repo.content;
    if (window.renderAbc) window.renderAbc();
  } else {
    console.log("Hello!");
    // 새 리포
    repo = null;
    nameDisplay.textContent = '새 레포지토리';
    nameInput.value         = '새 레포지토리';
    descInput.value         = '';
    abcInput.value          = '';
    if (window.renderAbc) window.renderAbc();
  }

  

  // 4) back-btn 클릭 시 (기존이면 update, 새로 만들면 create)
  backBtn.addEventListener('click', async () => {
    const name        = nameInput.value.trim();
    const description = descInput.value.trim();
    const content     = abcInput.value;

    let res;
    if (repoId != -1) {
      // UPDATE existing
      res = await postForm('/api/repos/update', {
        token,
        repoId,
        name,
        description,
        content
      });
      if (res.ok || res.status === 201) {
        // 완료 후 목록 페이지로
        window.location.href = `repoview.html?id=${repoId}`;
      } else {
        alert('저장에 실패했습니다. 상태 코드: ' + res.status);
      }
    } else {
      
      // CREATE new
      res = await postForm('/api/repos/create', {
        token,
        name,
        description,
        content
      });
      if (res.ok || res.status === 201) {
        // 완료 후 목록 페이지로
        console.log(res);
        repo = await res.json();
        window.location.href = `repoview.html?id=${repo.id}`;
      } else {
        alert('저장에 실패했습니다. 상태 코드: ' + res.status);
      }
    }

    
  });

  // 5) 로그아웃 버튼
  logoutBtn.addEventListener('click', async () => {
    await postForm('/api/auth/logout', { token });
    logout();
  });
})();
