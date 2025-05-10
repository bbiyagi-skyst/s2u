import { getForm, postForm } from './auth.js';
import { requireAuth, logout } from './common.js';

  // 1) 인증 토큰 가져오기
  const token = requireAuth();
  // 5) 수정 버튼 핸들러 연결
  const editBtn = document.getElementById('edit-btn');
  console.log(token);

  // 2) 알고 있는 repoId (예: URLSearchParams에서 가져옴)
  const params = new URLSearchParams(window.location.search);
  const repoId = params.get('id'); // 또는 직접 '123'
  console.log(repoId);
  // 3) GET 요청
  const res = await getForm('/api/repos/get', { token, repoId });
  
  if (res.ok) {
      
    // 4) JSON 파싱 및 사용
    const repo = await res.json();
    console.log('조회된 리포:', repo);
    // 예: 화면에 렌더링
    document.getElementById('name-display').textContent = repo.name;
    document.getElementById('description').textContent = repo.description;
    document.getElementById('abc-input').textContent = repo.content;
    if (window.renderAbc) {
      window.renderAbc();
    }
    editBtn.addEventListener('click', async () => {
        // 5a) 사용자 정보 조회
        const userRes = await postForm('/api/auth/user', { token });
        if (!userRes.ok) {
          alert('인증이 필요합니다. 다시 로그인해주세요.');
          logout();
          return;
        }
        const me = await userRes.json();
    
        // 5b) 권한 확인: 작성자만
        if (me.id !== repo.created_by) {
          alert('수정 권한이 없습니다.');
          return;
        }
    
        // 5c) 권한 OK: 수정 페이지로 이동
        window.location.href = `repo.html?id=${encodeURIComponent(repoId)}`;
      });
      document.getElementById('delete-btn').addEventListener('click', async () => {
        const userRes = await postForm('/api/auth/user', { token });
        if (!userRes.ok) {
          alert('인증이 필요합니다. 다시 로그인해주세요.');
          logout();
          return;
        }
        const me = await userRes.json();
    
        // 5b) 권한 확인: 작성자만
        if (me.id !== repo.created_by) {
          alert('삭제 권한이 없습니다.');
          return;
        }

        const ok = confirm('정말 이 레포지토리를 삭제하시겠습니까?');
        if (!ok) return;
      
        try {
          // 삭제 API 호출
          const res = await postForm('/api/repos/delete', { token, repoId });
          if (res.status === 204) {
            // 삭제 성공 → 목록으로 이동
            window.location.href = 'index.html';
          } else {
            // 실패 시 상태코드 표시
            alert(`삭제에 실패했습니다. (status: ${res.status})`);
          }
        } catch (err) {
          console.error('삭제 중 오류 발생:', err);
          alert('삭제 중 오류가 발생했습니다.');
        }
      });

  } else {
    console.error('리포지토리 조회 실패:', res.status);
  }
  document.getElementById('back-btn').addEventListener('click', () => {
    window.location.href = 'index.html';
  });
  
  document.getElementById('logout-btn').addEventListener('click', async () => {
    await postForm('/api/auth/logout', { token });
    logout(); // common.js 에 정의된 clearToken + redirect
  });
  
  

  
  

  