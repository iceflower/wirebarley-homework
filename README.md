# kimyounggeun_backend

## 개요
와이어바알리(wirebarley) 채용 전형 중 하나인 과제전형을 진행하기 위한 레포입니다.

## 개발환경
- 스프링 부트 버전 : 3.4.1
- JVM 버전 : 21 (latest lts version)
- 코틀린 버전 : 1.9.25
- 사용한 IDE : 인텔리제이 얼티밋 2024.03
- 서버 포트 : 8080

## 실행방법
1. cli로 다음과 같은 명령어를 실행하면,  `build/libs/` 디렉토리에 `wirebarley-homework-20250105-01.jar` 라는 실행파일이 빌드됩니다.
```shell
./gradlew clean bootJar
```
2. 루트 디렉토리에 존재하는 docker-compose를 활용해 컨테이너 이미지를 작동시킵니다. 이때, 실행파일 실행에 필요한 레디스와 포스트그레SQL이 실행됩니다.
3. 레디스와 포스트그레SQL의 startup이 완료되면, 1번 과정에서 만들어진 실행파일을 실행시킵니다.

## DB 스키마 구조
- `src/main/kotlin/resources/postgres` 디랙토리에 위치한 `ddl.sql` 을 참고 부탁드립니다.
