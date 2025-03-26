# 1. Java 베이스 이미지 사용 (JDK 17 Slim 버전)
FROM openjdk:17-jdk-slim

# 2. 작업 디렉토리 생성 및 설정
WORKDIR /app

# 3. 빌드된 JAR 파일 복사
COPY build/libs/LocketUserService-0.0.1-SNAPSHOT.jar app.jar

# 4. 컨테이너에서 노출할 포트
EXPOSE 8082

# 5. 헬스체크 명령 추가 (actuator를 사용하는 경우)
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# 6. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]