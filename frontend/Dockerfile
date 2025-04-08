# Node.js를 사용해 빌드
# frontend/Dockerfile
FROM node:18 as build
WORKDIR /app

# 의존성 설치
COPY package*.json ./
RUN npm install typescript@4.9.5 --save-dev
RUN npm install --legacy-peer-deps

# 🔥 소스 코드 명시적 복사
COPY public ./public
COPY src ./src
COPY tsconfig.json ./
COPY . .
# 👈 반드시 추가


# 빌드 전에 복사된 src 디렉토리 확인
#RUN echo "📁 /app/src 목록:" && ls -la /app/src && cat /app/src/__docker_test__.txt

# 빌드
RUN npm run build

# 결과물 복사
FROM alpine
WORKDIR /output
COPY --from=build /app/build /output

