# WorkHub - Quarkus Backend

## Mô tả

Dự án backend sử dụng Quarkus + MySQL, chạy trong Docker container.

## Yêu cầu

- Docker & Docker Compose
- Maven 3.9+ (để build)
- Git

## Setup cho FE Developer

### 1. Clone project

```bash
git clone <repo-url>
cd WorkHub_Quarkus
```

### 2. Build & Run với Docker

```bash
# Chạy MySQL + Backend
docker-compose up -d

# Xem logs
docker-compose logs -f

# Xem trạng thái containers
docker-compose ps
```

### 3. Kiểm tra

| Service | URL |
|---------|-----|
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/q/swagger-ui |
| Health Check | http://localhost:8080/q/health |

### 4. Dừng services

```bash
# Dừng và xóa containers
docker-compose down

# Dừng nhưng giữ lại data
docker-compose stop

# Xóa hoàn toàn data (CẨN THẬN - mất hết dữ liệu)
docker-compose down -v
```

## Cấu trúc ports

| Port | Service |
|------|---------|
| 8080 | Backend API |
| 3306 | MySQL |

## Database

- **Host**: `localhost`
- **Port**: `3306`
- **Database**: `recruitment`
- **Username**: `root`
- **Password**: `123456789`

## Troubleshooting

### Backend không khởi động được

```bash
# Xem logs
docker-compose logs backend

# Restart backend
docker-compose restart backend
```

### MySQL không khởi động được

```bash
# Xóa volume và restart
docker-compose down -v
docker-compose up -d
```

### Xóa toàn bộ và bắt đầu lại

```bash
docker-compose down -v
docker system prune -f
docker-compose up -d --build
```

## Lệnh hữu ích

```bash
# Rebuild image
docker-compose up -d --build

# Exec vào container
docker exec -it workhub-backend /bin/bash
docker exec -it workhub-mysql mysql -uroot -p123456789

# Xem resource usage
docker stats
```

## Backend API Endpoints

Sau khi chạy, FE dev có thể truy cập:

- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/q/swagger-ui`