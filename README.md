# GIVEME.md - ChatGPT를 활용한 리드미 생성 서비스
<img width="600" alt="Thumbnail" src="https://github.com/user-attachments/assets/1b13b63e-c86a-4ebc-b367-ebac35820c3f">

<br/>
졸업작품 'GIVEME.md'의 백엔드 리포지토리입니다.

## 🏫 프로젝트 소개

학부 내에서 프로젝트를 완료한 뒤 리드미 파일을 작성하지 않고 방치하는 사례가 많았습니다.
리드미가 제대로 작성되지 않은 레포지토리는 다른 사람들이 프로젝트 내용을 이해하기 어렵게 했으며, 나아가 취업 전형에서 지원자의 프로젝트를 평가하는데 부정적인 영향을 줄 수도 있어 보였습니다.
이러한 현상의 주된 원인이 리드미를 처음 작성할 때 어떤 내용을 포함해야 할지 모르는 점이나, 마크다운 문법에 익숙하지 않아 표 등의 콘텐츠를 만들기 위해 별도로 문법을 학습해야 하는 번거로움 등이라 생각했습니다.

**본 서비스를 통해 Github API와 ChatGPT API를 통해 자동으로 리드미 파일을 생성해 주어 이 문제를 해결하고, 학부생들이 더 쉽고 효율적으로 포트폴리오를 관리할 수 있도록 하고자 합니다.**

### 🗓️ 기간
* 기획 및 디자인: 2023.12 ~ 2024.2
* 개발: 2024.3 ~ 2024.5

### 🧑‍💻 나의 역할
* DB 설계
* API 작성
  * 깃허브 OAuth를 통한 로그인, JWT 인증/인가
  * 깃허브 API, ChatGPT API를 활용한 프로젝트 요약 및 리드미 생성
  * 생성한 리드미를 리포지토리에 커밋
  * 리드미 저장 및 수정
  * 단위 테스트 작성
* 백엔드 배포 - AWS EC2, RDS, ElastiCache
* Github Action과 Docker를 활용한 CD/CD

<br/>

## 💡 주요 기능

| 기능               | 내용                                     |
|:-----------------|:---------------------------------------|
| 깃허브 소셜 로그인       | 깃허브 계정으로 쉽게 로그인할 수 있습니다.               |
| JWT를 활용한 인증/인가   | 시설물의 예약 현황을 확인하고 원하는 시간에 예약을 할 수 있습니다. |
| 프로젝트 요약 및 리드미 생성 | LLM을 활용하여 빠르게 리드미를 생성할 수 있습니다.         |
| 리드미 커밋           | 생성한 리드미를 본인의 깃허브 리포지토리에 바로 커밋할 수 있습니다. |
| 리드미 저장 및 수정      | 생성한 리드미를 저장하고 원하는 대로 수정할 수 있습니다.       |

<br/>

## 🛠️ 기술스택
<img width="741" alt="image" src="https://github.com/user-attachments/assets/bbd68630-e4a4-4118-a26d-d7ebeb0cf851">
<img width="741" alt="image" src="https://github.com/user-attachments/assets/66a19031-8f0f-4ae5-849b-c02eb402d503">
<br/><br/>

## 📂 인프라
<img width="741" alt="image" src="https://github.com/user-attachments/assets/471afc23-7066-4bd8-b1ec-322549881bb3">
<img width="741" alt="image" src="https://github.com/user-attachments/assets/747dfaff-b88d-467c-aa48-6d7929779577">

<br/><br/>

## 📂 API 명세서
https://spicy-lillipilli-407.notion.site/API-13d6a72d193780eb89d0e7b17d07cdbd?pvs=73

<br/><br/>

## 👨‍👩‍👧‍👦 팀 구성