# Auth Spring



## auth 프로젝트 환경설정

이 문서는 auth 프로젝트의 환경설정을 설명합니다. 프로젝트를 실행하기 위해 필요한 데이터베이스, 이메일, 파일 업로드, Redis, JWT 설정을 포함하고 있습니다.

## 1. 프로젝트 개요

프로젝트명: auth
개발환경: Spring Boot 3.4.1, Java 17
DBMS: PostgreSQL
빌드 시스템: Gradle
보안: Spring Security, JWT
캐시 시스템: Redis
이메일 인증: Gmail SMTP
파일 업로드: MultipartFile 

## 2.시스템 요구사항

구성 요소	버전
Java	17
Spring Boot	3.4.1
PostgreSQL	14+
Gradle	8.x
Redis	6+

## 3. 프로젝트 실행 방법

# 3.1 Gradle 빌드
./gradlew build
3️ 
# 3.2 Redis 실행 (필수)
Redis는 세션 관리 및 캐싱을 위해 필요합니다.
아래 방법 중 하나를 선택하여 Redis를 실행합니다.

- Redis를 설치 후 실행하는 방법

✅ Linux / Mac

brew install redis  # Mac (Homebrew 설치 필요)
sudo apt install redis-server  # Ubuntu
redis-server

✅ Windows

Redis 공식 사이트에서 Windows용 Redis 다운로드
redis-server.exe 실행

# 3.3 Spring Boot 애플리케이션 실행
✅ CLI에서 실행

./gradlew bootRun
✅ 또는 JAR 파일 실행

java -jar build/libs/auth-0.0.1-SNAPSHOT.jar
✅ Docker로 실행하는 경우 (선택 사항)

docker build -t auth-app .
docker run -p 8080:8080 --name auth-app -d auth-app
📌 8080 포트에서 서버가 실행됨 → 브라우저에서 확인

http://localhost:8080

## 4.환경 설정 (application.properties)

# 4.1 데이터베이스 (PostgreSQL) 설정
PostgreSQL을 사용하며, 기본 접속 정보는 다음과 같습니다.

spring.datasource.url=jdbc:postgresql://localhost:5432/auth_local
spring.datasource.username=myuser
spring.datasource.password=mypassword
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# 4.2 Redis 설정
Redis는 인증 및 세션 관리를 위해 사용됩니다.

spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=60s

Redis는 기본 포트 6379에서 실행되어야 함.
Docker 또는 로컬에서 Redis 실행 후 Spring Boot 실행

# 4.3 파일 업로드 설정
# 파일 업로드 활성화
spring.servlet.multipart.enabled=true

# 한개 파일에 대한 최대 크기 (10MB)
spring.servlet.multipart.max-file-size=10MB

# 전체 요청 크기 제한 (100MB)
spring.servlet.multipart.max-request-size=100MB

# 파일 저장 기본 경로
file.upload.base-directory=/Users/auth

# 사업자등록증 파일 저장 경로
file.upload.biz-directory=/Users/auth/upload_files/biz_lic_file

파일 업로드는 파일 한개 당 최대 10MB, 총 최대 100MB까지 가능.
운영체제에 따라 저장 경로를 적절히 수정해야 함.

## 5. 의존성 (build.gradle)

dependencies {
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-security'

    // PostgreSQL
    implementation 'org.postgresql:postgresql:42.7.2'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // 이메일
    implementation 'org.springframework.boot:spring-boot-starter-mail'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'

    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
    compileOnly 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

- 주요 라이브러리

DB: PostgreSQL + Spring Data JPA
보안: Spring Security + JWT
이메일: Spring Boot Mail
캐싱: Redis

## 6. 개발 시 유의 사항

✔ 환경 변수 사용:

비밀번호, JWT Secret Key 등의 민감한 정보는 .env 파일 또는 환경 변수로 관리하는 것이 보안상 안전합니다.
✔ 데이터베이스 설정:

운영 환경에서는 spring.jpa.hibernate.ddl-auto=update 대신 validate 또는 none을 사용하는 것이 권장됩니다.
✔ Redis 실행 필수:

Redis를 실행하지 않으면 캐싱 기능이 정상적으로 동작하지 않으므로, 반드시 실행해야 함.

## 7. 실행 방법

# 7.1 프로젝트 클론 또는 local-upload pull
git clone https://github.com/your-repo/user-auth-account-system.git
cd auth

# 7.2 Gradle 빌드
./gradlew build

# 7.3 Redis 실행 (필수)
redis-server  # 또는 Docker 사용: docker run --name redis -d -p 6379:6379 redis

# 7.4 Spring Boot 애플리케이션 실행
./gradlew bootRun