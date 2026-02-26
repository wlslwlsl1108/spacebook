# Java 17 이미지 사용
FROM eclipse-temurin:17-jdk

# 작업 디렉토리 생성
WORKDIR /app

# 전체 코드 복사
COPY . .

# gradlew 실행 권한 부여 (중요)
RUN chmod +x ./gradlew

# Gradle 빌드
RUN ./gradlew clean build -x test

# 8080 포트 오픈
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "build/libs/spacebook-0.0.1-SNAPSHOT.jar"]