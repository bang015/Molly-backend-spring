<img src="https://github.com/Jeongseonil/anesi/assets/137017329/53034a7c-d92b-4d0a-a415-de4e158e983c"  width="200"/>
<h2>🔎 프로젝트 정보</h2>
<div><b>📆 2023.12.19 ~ 2024.03.22 (94일)</b></div>
<br>
<div>웹 소셜 네트워크 서비스 Molly project의 Back-End Repository 입니다.</div>
<br>
<div><b>배포 주소</b> : https://molly.n-e.kr/</div>
<br>
<h2>🛠 Stacks</h2>
<div>
  <img src="https://img.shields.io/badge/springboot-339933?style=flat&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white"/>
  <img src="https://img.shields.io/badge/Cloudinary-3448C5?style=flat&logo=cloudinary&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySql-4479A1?style=flat&logo=mysql&logoColor=white"/>
</div>
<br>
<h2>💾 Database Schema Diagram</h2>

![drawSQL-image-export-2024-03-24](https://github.com/bang015/Molly-backend/assets/137017329/3cb947ec-3b91-41a3-a5a9-06e7de7eed63)
<br>
<h2>⚙ 핵심 기능</h2>
<h3>[사용자 인증 부분]</h3>
<ul>
  <li>회원가입 및 로그인 기능 구현 (비밀번호 암호화 저장)</li>
  <li>회원가입 시 이메일인증 / 이메일을 통한 비밀번호 재설정 링크 전송 구현</li>
  <li>JsonWebToken을 사용한 사용자 인증</li>
  <li>refreshToken을 구현하여 보안 강화</li>
  <li>회원정보 수정 기능 구현</li>
</ul>
<h3>[게시물 부분]</h3>
<ul>
  <li>게시물의 이미지를 Cloudinary를 통해 저장</li>
  <li>태그를 입력 시 해당 단어가 포함된 태그 중 많이 사용된 태그를 추천</li>
  <li>유저 별 게시물에 대한 좋아요 체크 기능 구현</li>
  <li>댓글과 대댓글 기능 구현</li>
  <li>메인/ 탐색/ 프로필 페이지에 필요한 게시물 조건에 따라 불러오는 라우터 구현</li>
  <li>게시물 수정과 삭제 기능 구현</li>
</ul>
<h3>[검색 부분]</h3>
<ul>
  <li>조건에 따라 유저, 게시물 태그 또는 전체 검색 기능구현</li>
  <li>Redis를 사용하여 간단하게 검색 내역 저장 및 삭제 기능 구현</li>
</ul>
<h3>[메시지 부분]</h3>
<ul>
  <li>WebSocket를 사용하여 실시간 채팅 기능 구현</li>
  <li>읽지 않은 메시지를 실시간으로 표시해주는 기능 구현</li>
</ul>
