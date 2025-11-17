# Benthos-Test

โปรเจคสำหรับทดสอบและพัฒนา **Hyper-Runtime** ซึ่งเป็น Benthos stream processing framework เวอร์ชันที่ปรับแต่งเพิ่มเติม พร้อมด้วย custom processors และ plugins หลากหลายตัว

## 📋 ภาพรวม

Benthos-Test เป็น comprehensive testing repository ที่ครอบคลุม:

- ✅ การทดสอบ custom Benthos processors (hp_transform, hp_function, hp_http, hp_resource, etc.)
- ✅ การ validate การทำงานร่วมกับ data sources ต่างๆ (Redis, MongoDB, RabbitMQ, Kafka)
- ✅ การพัฒนาและทดสอบ data transformation flows ด้วยภาษา Bloblang
- ✅ Quality assurance ด้วย SonarQube integration สำหรับ code analysis
- ✅ มากกว่า **131+ test cases** ที่ครอบคลุมหลากหลาย use cases

## 🛠 Technology Stack

### Core Technologies
- **Hyper-Runtime** (Benthos v8.3.2.5-5dd4273) - Stream processing engine
- **Docker & Docker Compose** - Container orchestration
- **Bloblang** - Data mapping และ transformation language

### Data Infrastructure
- **Redis** (ports 6379, 6380) - Cache และ queue management
- **MongoDB** (port 27017) + Mongo Express UI (port 8081)
- **RabbitMQ** (port 5672) + Management UI (port 15672)
- **Kafka** (port 9092) + Kafka UI (port 8080) + Zookeeper (port 2181)

### Development Tools
- **SonarQube** (port 9000) - Code quality analysis
- **Custom Bloblang Plugin** - SonarQube plugin สำหรับภาษา Bloblang (Java/Maven)

### Custom Processors/Plugins
- `hp_transform` - Data transformation processor
- `hp_function` - Function execution processor
- `hp_http` - HTTP request processor
- `hp_resource` - Resource management processor
- `hp_redis` - Redis operations
- `dump_log` - Logging processor
- `redis_list` - Redis list operations

## 📁 โครงสร้างโปรเจค

```
Benthos-Test/
│
├── docker/                          # Docker configurations
│   ├── kafka-test/                 # Kafka + Zookeeper + Kafka UI
│   ├── mongo-test/                 # MongoDB + Mongo Express
│   ├── redis-test/                 # Redis instances (dual setup)
│   ├── rabbitmq/                   # RabbitMQ with management
│   ├── sonarqube/                  # SonarQube + Bloblang plugin
│   └── benthos-document/           # Documentation server (port 8888)
│
├── try2test/                        # Main test directory
│   ├── docker-compose.yml          # Benthos runtime config
│   ├── flows/                      # Deployed flow configurations
│   ├── flows-work/                 # Working flow definitions (43 flows)
│   ├── test_hp_function/           # Function processor tests (12 cases)
│   ├── test_hp_http/               # HTTP processor tests (17 cases)
│   ├── test_hp_transform/          # Transform processor tests (39 cases)
│   ├── test_mongodb/               # MongoDB tests (20 cases)
│   ├── test_hp_redis/              # Redis processor tests
│   ├── test_hp_resource/           # Resource processor tests
│   └── data/                       # Test data (csv, output, test)
│
├── sonar-bloblang-plugin/          # SonarQube plugin source
│   └── target/                     # Compiled plugin JAR
│
├── redis_config.txt                # Redis configuration examples
├── redis_config_mongo_profile.txt  # MongoDB profile configs
└── sonar-project.properties        # SonarQube project config
```

## 🚀 Quick Start

### Prerequisites

- Docker และ Docker Compose ติดตั้งแล้ว
- Ports ที่ต้องเปิดว่าง: 6379, 5672, 27017, 9092, 8124, 8080, 8081, 8888, 9000

### 1. เริ่มต้น Infrastructure Services

#### Start Redis
```bash
cd docker/redis-test
docker-compose up -d
# Redis พร้อมใช้งานที่ localhost:6379 และ localhost:6380
```

#### Start MongoDB
```bash
cd docker/mongo-test
docker-compose up -d
# MongoDB: localhost:27017 (admin/password123)
# Mongo Express UI: http://localhost:8081 (admin/admin)
```

#### Start RabbitMQ
```bash
cd docker/rabbitmq
docker-compose up -d
# RabbitMQ: localhost:5672
# Management UI: http://localhost:15672 (guest/guest)
```

#### Start Kafka (Optional)
```bash
cd docker/kafka-test
docker-compose up -d
# Kafka: localhost:9092
# Kafka UI: http://localhost:8080
```

### 2. เริ่มต้น Benthos/Hyper-Runtime

```bash
cd try2test
docker-compose up -d
# Benthos runtime พร้อมใช้งานที่ localhost:8124
```

**Configuration Details:**
- Image: `wutichai/hyper-runtime:v8.3.2.5-5dd4273`
- Flows ถูก mount จาก: `./flows` → `/data/deployed`
- Data directories: `./data/csv`, `./data/output`, `./data/test`
- Redis connection: `redis://host.docker.internal:6379`
- Log format: JSON

### 3. ทดสอบการทำงาน

```bash
# ส่ง test request ไปที่ Benthos
curl -X POST http://localhost:8124/megw/apis/stream/test/v1/action \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}'
```

### 4. Stop Services

```bash
# Stop Benthos
cd try2test
docker-compose down

# Stop infrastructure services
cd docker/redis-test && docker-compose down
cd docker/mongo-test && docker-compose down
cd docker/rabbitmq && docker-compose down
cd docker/kafka-test && docker-compose down
```

## 🧪 Test Structure

แต่ละ test case ประกอบด้วย:
- `main.yaml` - Benthos pipeline configuration
- `resources/` - Processor resource definitions
- `models/` - Input/output JSON models (ใน flows-work)
- `source/` - Visual flow definitions (ใน flows-work)

### Test Coverage Summary

| Category | จำนวน Test Cases | คำอธิบาย |
|----------|-----------------|----------|
| **flows-work** | 43 cases | Integrated flow scenarios |
| **test_hp_function** | 12 cases | Function processor tests |
| **test_hp_http** | 17 cases | HTTP processor tests |
| **test_hp_transform** | 39 cases | Transformation tests |
| **test_mongodb** | 20 cases | MongoDB integration tests |
| **test_hp_redis** | 12+ cases | Redis processor tests |
| **test_hp_resource** | 16+ cases | Resource processor tests |
| **รวม** | **131+ cases** | |

## 🔧 Features & Functionalities

### 1. Stream Processing Pipeline
- HTTP server input/output processing
- Message queue integration (RabbitMQ, Kafka)
- Redis list operations (BLPOP, RPUSH)
- Generate-based input สำหรับ scheduled processing

### 2. Custom Processors
- **hp_transform**: Bloblang transformations with input/output mappings
- **hp_function**: Function encapsulation with nested processors
- **hp_http**: HTTP API calls with custom headers และ authentication
- **hp_resource**: Resource-based routing with success/failure paths
- **hp_redis**: Advanced Redis operations
- **dump_log**: Debugging และ logging utilities

### 3. Data Integration
- MongoDB CRUD operations (insert, search, update, delete)
- Redis caching และ queue management
- RabbitMQ topic/fanout/direct exchanges
- Kafka producer/consumer patterns
- HTTP API integration with mock services

### 4. Flow Management
- Visual flow definitions (flows.json, nodes.json)
- Node-based workflow orchestration
- Subflow support with exception handling
- Dynamic routing with goto_route patterns
- Filter input/output with metadata tracking

### 5. Security & Encryption
- AES GCM encode/decode
- RSA + 3DES encryption
- CIF decryption
- Secret validation workflows
- Environment variable encryption support

## 📊 SonarQube Integration (Optional)

### Start SonarQube

```bash
cd docker/sonarqube
docker-compose up -d
# SonarQube UI: http://localhost:9000
```

### Build และติดตั้ง Bloblang Plugin

```bash
cd docker/sonarqube
mvn clean package
# Plugin JAR: target/sonar-bloblang-plugin-1.0.0.jar
# Copy ไปยัง SonarQube extensions และ restart
```

### Run Analysis

```bash
sonar-scanner -Dsonar.token=sqa_23931d83e245e8ca078b078b053544122ae5cad1
```

**Supported file types:**
- `.blobl`
- `.bloblang`
- `.yaml` (Benthos config files)

### Metrics ที่เก็บ:
- Lines of code
- NCLOC (Non-Comment Lines of Code)
- Comments
- Code complexity
- Bloblang syntax validation

## 📚 เอกสารเพิ่มเติม

- [SonarQube Bloblang Plugin Documentation](docker/sonarqube/README.md)
- [Redis Configuration Examples](redis_config.txt)
- [MongoDB Profile Configs](redis_config_mongo_profile.txt)
- Benthos Official Docs: http://localhost:8888 (เมื่อเปิด documentation server)

## 🌐 Service Endpoints

| Service | URL | Credentials |
|---------|-----|-------------|
| Benthos Runtime | http://localhost:8124 | - |
| MongoDB | localhost:27017 | admin/password123 |
| Mongo Express | http://localhost:8081 | admin/admin |
| RabbitMQ | localhost:5672 | guest/guest |
| RabbitMQ Management | http://localhost:15672 | guest/guest |
| Kafka | localhost:9092 | - |
| Kafka UI | http://localhost:8080 | - |
| Redis | localhost:6379, 6380 | - |
| SonarQube | http://localhost:9000 | admin/admin |
| Benthos Docs | http://localhost:8888 | - |

## 🔑 Environment Variables

```bash
MAX_SPARED_THREADS=5
LOG_FORMAT=json
JSON_USE_NUMBER=false
HYPER_NODE_PATH=/data/deployed
HYPER_REDIS=redis://host.docker.internal:6379
```

## 📝 Git Status

**Current Branch:** `main`

**Recent Commits:**
- `8a8937d` - update
- `19fd40a` - update test
- `abd37c7` - Update docker-compose.yml
- `919f844` - add test case for exception
- `3649f7d` - add sonarqube

## 🤝 Contributing

โปรเจคนี้เป็น testing repository สำหรับการพัฒนาและทดสอบ Hyper-Runtime

## 📄 License

[ระบุ license ของโปรเจค]

---

**Hyper-Runtime Version:** v8.3.2.5-5dd4273
**Benthos Test Framework** - Comprehensive Stream Processing Testing Suite
