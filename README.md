
<div align="center">
  <h1>Locket - Lock + Pocket</h1>
  <p>📚 결제 마이데이터 및 상품 최저가 정보를 결합한 스마트 소비 관리 앱  📚</p>
</div>

<br/>

<div align="center">
    <!-- 우리 앱 설명 이미지 넣기 (마땅한 이미지 생기면 수정해야함ㅎㅎ) -->
    <img 
        src="./assets/image/Locket_intro.png" 
        alt="Main"
        style="border-radius: 5px;"/>
</div>

<br/>
<div align="center">
  <a href="https://www.notion.so/API-1a4988e958dc81849b5dd85bb9bf960e">API 문서</a>
  &nbsp; | &nbsp;
  <a href="https://www.notion.so/1a4988e958dc8155a8a3cdacf3b1062b">요구사항 명세서</a>
  &nbsp; | &nbsp;
  <a href="https://www.notion.so/2-PJT-1a4988e958dc80ec934ae12bcae54c1f">Notion</a>
</div>
</br>

## ✍️ 프로젝트 개요

- **프로젝트명:** Locket
- **프로젝트 기간:** 2025.03.03 ~ 2025.04.11
- **SSAFY 12기 특화 프로젝트**

---

## ✍️ 프로젝트 소개

### 프로젝트 배경

최근 소비자들은 자신의 지출 습관을 파악하고 체계적으로 관리하려는 니즈가 증가하고 있습니다. 하지만 대부분의 가계부 앱은 단순한 기록 기능에 그치며, 실제로 내 소비 습관이 어떤지, 어디서 아끼고 개선할 수 있는지에 대한 분석은 제공되지 않습니다.

특히 MZ세대를 중심으로, 재테크와 소비를 동시에 관리하려는 흐름이 강해지면서, 실시간으로 소비 분석과 피드백을 받을 수 있는 지능형 가계부+핀테크 서비스에 대한 수요가 커지고 있습니다.

이러한 흐름에 맞춰, 우리는 소비 내역을 분석하고 유사 사용자와 비교하여 맞춤형 피드백을 제공하는 금융 분석 서비스를 개발하였습니다.


---

## 🚀 프로젝트 목표

1. **소비 패턴 분석 및 피드백 제공:** 
   - 사용자의 소비 데이터를 Elasticsearch 기반으로 저장 및 분석하여, 카테고리별 소비 비중, 요일별 지출 패턴, 월간 변화율 등을 시각화하고 피드백합니다.
    - 유사 연령대/직업군 사용자들과의 비교 분석을 통해, 과소비 항목을 진단하고 절약 방안을 제시합니다.

2. **가계부-핀테크 통합:** 
   - 단순 지출 기록을 넘어, 결제 API와의 연동을 통해 실시간으로 소비를 추적합니다.
    - 실제 카드 사용 내역을 기반으로 캐릭터 성장, 보상 시스템 등 게임 요소를 도입하여 사용자의 지속적인 동기부여를 유도합니다.

3. **실시간 알림 및 목표 관리:** 
   - 예산 초과 시 알림 제공, 목표 기반 소비 관리, 지출 상한 설정 등 다양한 기능을 통해 건강한 소비 습관 형성을 돕습니다.


## 📊 기대 효과

### 플랫폼
- **데이터 기반 개인 맞춤형 피드백 제공**: 사용자의 월별 소비 패턴과 비교 분석을 통해 의미 있는 인사이트 제공
- **유연한 서비스 확장성**: MSA 기반 구조와 Kafka, Redis, Elasticsearch의 도입으로 유연한 마이크로서비스 확장 및 고성능 유지

### 사용자
- **시각적 소비 인사이트 제공**: 도넛 차트, 트렌드 그래프 등을 통해 소비 내역을 직관적으로 확인
- **재미와 동기부여를 위한 게임화**: 캐릭터 성장 시스템 및 카드 사용 리워드로 지출 관리에 재미 요소 부여
- **건강한 소비 습관 형성 지원**: 목표 설정 기능과 실시간 피드백을 통해 소비 습관 개선 유도

---

 ## 📌 주요 기능

### 1️⃣ <b>로그인 페이지</b>
> 로그인 페이지

|                     **로그인 페이지**                      |
| :-----------------------------------------------------: | 
| <img src="./assets/image/Login (1).png"> |

<br>

### 2️⃣ <b>가계부 페이지</b>

> 가계부 페이지

|                     **가계부 페이지**                      |
| :-----------------------------------------------------: | 
| <img src="./assets/image/가계부.png"> |

<br>

### 3️⃣ <b>마이 페이지</b>

> 마이 페이지

|                     **마이 페이지**                      |
| :-----------------------------------------------------: | 
| <img src="./assets/image/마이페이지.png"> |

<br>

### 4️⃣ <b>메인 페이지</b>

> 메인 페이지

|                      **메인 페이지**                      |
| :--------------------------------------------------------: | 
| <img src="./assets/image/홈.png"> |

<br>

### 5️⃣ <b>영수증 페이지</b>

> 영수증 페이지

|                      **영수증 페이지**                      |
| :--------------------------------------------------------: | 
| <img src="./assets/image/영수증 등록.png"> |
<br>

### 6️⃣ <b>결제 페이지</b>

> 결제 페이지

|                      **결제 페이지**                      |
| :--------------------------------------------------------: | 
| <img src="./assets/image/결제화면.png" height="500"> |
<br>

### 7️⃣ <b>상품 페이지</b>

> 상품 페이지

|                      **상품 페이지**                      |
| :--------------------------------------------------------: | 
| <img src="./assets/image/가격변동그래프.png"> |
<br>

### 8️⃣ <b>알림 페이지</b>

> 알림 페이지

|                      **알림 페이지**                      |
| :--------------------------------------------------------: | 
| <img src="./assets/image/알림 화면.png" height="500"> |
<br>

<!-- <img src="./assets/gif/profile_edit.gif" height="400"> -->
---

## 🧑‍💻 팀원 소개

<table>
  <tr>
    <td align="center" style="width:140px; height:140px;">
      <a href="">
        <img src="./assets/image/character/ej.png" style="width:100%; height:100%; object-fit:cover;" />
        <br><br> 👑 손은주 <br>
      </a>
    </td>
    <td align="center" style="width:140px; height:140px;">
      <a href="">
        <img src="./assets/image/character/ih.png" style="width:100%; height:100%; object-fit:cover;" />
        <br><br> ⛑ 강인혁 <br>
      </a>
    </td>
    <td align="center" style="width:140px; height:140px;">
      <a href="">
        <img src="./assets/image/character/jm.png" style="width:100%; height:100%; object-fit:cover;" />
        <br><br> ⛑ 권정민 <br>
      </a>
    </td>
    <td align="center" style="width:140px; height:140px;">
      <a href="">
        <img src="./assets/image/character/sh.png" style="width:100%; height:100%; object-fit:cover;" />
        <br><br> ⛑ 안세호 <br>
      </a>
    </td>
    <td align="center" style="width:140px; height:140px;">
      <a href="">
        <img src="./assets/image/character/kh.png" style="width:100%; height:100%; object-fit:cover;" />
        <br><br> ⛑ 김기훈 <br>
      </a>
    </td>
    <td align="center" style="width:140px; height:140px;">
      <a href="">
        <img src="./assets/image/character/ay.png" style="width:100%; height:100%; object-fit:cover;" />
        <br><br> ⛑ 한아영 <br>
      </a>
    </td>
  </tr>
  <tr>
    <td align="center">UI/UX<br/>Backend</td>
    <td align="center">Backend</td>
    <td align="center">Infra<br/>Backend</td>
    <td align="center">Backend</td>
    <td align="center">UI/UX<br/>Android</td>
    <td align="center">UI/UX<br/>Android</td>
  </tr>
</table>




---

## ⚙️ 기술 스택

<table>
    <thead>
        <tr>
            <th>분류</th>
            <th>기술 스택</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>안드로이드</td>
            <td>
                <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white"/>
                <img src="https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat&logo=androidstudio&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td>백엔드</td>
            <td>
                <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white"/>
                <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white"/>
                <img src="https://img.shields.io/badge/FastAPI-009688?style=flat&logo=fastapi&logoColor=white"/>
                <img src="https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white"/><br/>
                <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white"/>
                <img src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white"/>
                <img src="https://img.shields.io/badge/Kafka-231F20?style=flat&logo=apachekafka&logoColor=white"/>
                <img src="https://img.shields.io/badge/Netflix_Eureka-F6C751?style=flat&logo=netflix&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td>데이터베이스</td>
            <td>
                <img src="https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white"/>
                <img src="https://img.shields.io/badge/Elasticsearch-005571?style=flat&logo=elasticsearch&logoColor=white"/>
                <img src="https://img.shields.io/badge/Redis-DD0031?style=flat&logo=redis&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td>인프라</td>
            <td>
                <img src="https://img.shields.io/badge/AWS_EC2-FF9900?style=flat&logo=amazonec2&logoColor=white"/>
                <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white"/>
                <img src="https://img.shields.io/badge/Docker_Swarm-2496ED?style=flat&logo=docker&logoColor=white"/>
                <img src="https://img.shields.io/badge/Docker_Compose-2496ED?style=flat&logo=docker&logoColor=white"/>
                <img src="https://img.shields.io/badge/cAdvisor-282C34?style=flat&logo=docker&logoColor=61DAFB"/>
                <img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white"/><br/>
                <img src="https://img.shields.io/badge/Ubuntu-E95420?style=flat&logo=ubuntu&logoColor=white"/>
                <img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white"/>
                <img src="https://img.shields.io/badge/Prometheus-E65251?style=flat&logo=prometheus&logoColor=white"/>
                <img src="https://img.shields.io/badge/Grafana-F46800?style=flat&logo=grafana&logoColor=white"/>
                <img src="https://img.shields.io/badge/Node_Exporter-00BFFF?style=flat&logo=prometheus&logoColor=white"/>
                <img src="https://img.shields.io/badge/Spring_Boot_Actuator-6DB33F?style=flat&logo=spring&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td>API</td>
            <td>
                <img src="https://img.shields.io/badge/OpenAI-412991?style=flat&logo=openai&logoColor=white"/>
                <img src="https://img.shields.io/badge/Clova_OCR-00C73C?style=flat&logo=naver&logoColor=white"/>
                <img src="https://img.shields.io/badge/Perplexity-6C47FF?style=flat&logo=perplexity&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td>디자인</td>
            <td>
                <img src="https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=figma&logoColor=white"/>
            </td>
        </tr>
        <tr>
            <td>협업 도구</td>
            <td>
                <img src="https://img.shields.io/badge/GitLab-FC6D26?style=flat&logo=gitlab&logoColor=white"/>
                <img src="https://img.shields.io/badge/Jira-0052CC?style=flat&logo=jira&logoColor=white"/>
                <img src="https://img.shields.io/badge/Notion-000000?style=flat&logo=notion&logoColor=white"/>
                <img src="https://img.shields.io/badge/Mattermost-0072C6?style=flat&logo=mattermost&logoColor=white"/>
                <img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white"/>
            </td>
        </tr>
    </tbody>
</table>



---

## 🔨 시스템 아키텍처

<img src="./assets/image/Locket_architecture.png" alt="Architecture"/>

---

## 📊 ERD

<img src="./assets/image/Locket_erd.png" alt="ERD"/>

<!-- ---


## 📂 문서 자료

- [포팅 메뉴얼]()
- [시연 시나리오]()
- [발표 자료]()
- [DATA]()

--- -->
