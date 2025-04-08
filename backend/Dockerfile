# Amazon Corretto 기반 Java 환경 설정
FROM amazoncorretto:21-alpine

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY target/*.jar app.jar

# 컨테이너가 사용할 포트
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
