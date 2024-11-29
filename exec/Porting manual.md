# Push Of Life Porting Manual
이 매뉴얼은 linux 환경에서 Push Of Life를 clone하여 빌드 및 배포하는 방법에 대한 가이드입니다.

# 0. 요구사항
- Ubuntu 20.04.6 LTS
	- Docker version 27.1.1, build 6312585
	- Docker Compose version v2.29.1
	- openjdk 17.0.12 2024-07-16
		- 상세사항
			- OpenJDK Runtime Environment (build 17.0.12+7-Ubuntu-1ubuntu220.04)
			- OpenJDK 64-Bit Server VM (build 17.0.12+7-Ubuntu-1ubuntu220.04, mixed mode, sharing)
- 
# 1. 빌드
Push Of Life git repository 를 clone 후, `git_root/backend/PushOfLife` 로 이동하여 실행:
```bash
chmod +x gradlew
./gradlew build
```
# 2. 환경 변수 file 구성
다음 Template를 모두 채운 뒤 git root 폴더에 `.env` 이름으로 배치하세요.
```bash
# .env file

# Spring container environment variables
DB_CONNECTION=
DB_USERNAME=
DB_PASSWORD=
DB_DOMAIN=
JWT_SECRET=
REDIS_CONNECTION=
REDIS_PORT=
KAFKA_SERVER=
TOPIC_NAME=
SERVICE_KEY=
API_URL=
SPRING_SWAGGER_SERVER=

#notification 서버
TOPIC_NAME=
KAFKA_SERVER=
TEST_FCMKEY=
FIREBASE_KEYPATH=
NOTIFICATION_PORT=

# 3. Deploy
git root 에서 실행
```bash
docker compose -f ./docker-compose.yml up down
```
이후 실행
```bash
docker compose -f ./docker-compose.yml up -d
```

