---

## 🛠 사용 기술 및 라이브러리

- Kotlin
- Spring Boot
- Naver Cloud Platform
- Naver Clova Studio
- cloudtype 클라우드

---

## 🖥 담당 업무 및 기능

- 서버 구축
    - cloudtype 클라우드를 사용해 배포 진행
- API 개발
    - Clova Studio를 사용해 입력한 프롬프트 바탕의 문장 생성 후 비동기 실시간 stream 리턴 방식 으로 구현
    - 비동기 Reactor Flux 타입 리턴
    - 작성한 프롬프트와 사용자 입력에 따른 대화문 출력
    - [pollinations.ai](http://pollinations.ai) 를 사용해 입력한 동물을 프롬프트로 넣어 이미지 생성
    

---

## 🔍부가 설명

- [~~https://earth-laboratory.vercel.app/~~](https://earth-laboratory.vercel.app/)
- 프로젝트 소개 : [https://gentle-perigee-758.notion.site/d982946318904bcd95d8deddacb4304d](https://www.notion.so/d982946318904bcd95d8deddacb4304d?pvs=21)
- 시연 영상 : https://youtu.be/l_-xxZcOihM, https://youtu.be/IpOIWBLE2vw
- 네이버에서 제공하는 LLM 서비스인 Clova Studio를 활용한 프로젝트 입니다.
    - Clova Studio의 HCX-002 엔진을 이용했습니다.
    - Chat Completion에 stream 리턴을 위해 Non-Blocking을 위한 Reactor Mono, Flux에 대해 공부할 수 있었습니다.
- 서버 배포를 위해서 컨테인너 기반 PaaS 클라우드 서비스인 [cloudtype](https://cloudtype.io/)을 사용했습니다.
    - AWS, GCP를 사용해 서버 배포의 경험이 있었기에 새로운 플랫폼을 경험해 보기 위해 사용했습니다. 자체적으로 도커 이미지를 만들어줘 매우 간편하고 깃허브와 연동도 되어 쉽고 간단한 배포가 가능했습니다.
- 기존에 자바를 이용한 스프링 부트 개발을 해오다가, 처음으로 코틀린으로 스프링 프로젝트를 진행하였습니다. 코틀린 특유의 간결성과 null 안정성 때문에 앞으로 더 공부해보고 싶은 조합이었습니다.
