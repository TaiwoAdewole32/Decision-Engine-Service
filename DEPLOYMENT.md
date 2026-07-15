# Deployment & Running the System

## Prerequisites

- Java 21+
- Maven 3.8+
- Python 3.8+
- SQLite3 (included with system)

## Step 1: Start Java Backend

```bash
cd ruleengine
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

**Test it:**
```bash
curl -X POST http://localhost:8080/api/applications/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "test-001",
    "age": 35,
    "income": 65000,
    "creditScore": 720,
    "debtToIncome": 0.35,
    "hasLatePayments": false,
    "requestedAmount": 250000,
    "employmentStatus": "EMPLOYED"
  }'
```

## Step 2: Run Tests (Optional)

```bash
cd ruleengine
mvn test
```

**BiasPreventionTest** will verify demographics are never used in rules.

## Step 3: Start Python Audit Service

In a new terminal:

```bash
cd audit
pip install -r requirements.txt
python app.py
```

Audit service will start on `http://localhost:5000`

**Test it:**
```bash
curl http://localhost:5000/api/audit/report
```

## Full System Workflow

1. Submit applications to Java backend
2. Backend stores decisions + demographics in `decisions.db`
3. Query Python audit service for bias analysis
4. Review disparities report
5. Adjust rules in `ruleengine/src/main/resources/rules.json` if needed
6. Restart backend to reload rules

## Database Location

- **Development**: `ruleengine/decisions.db` (auto-created on first run)
- **Audit reads from**: Same location or set path in `audit/bias_audit.py`

## Troubleshooting

### "Cannot find decisions.db"
- Ensure at least one application has been evaluated
- Check `ruleengine/` directory for the file

### Java port 8080 already in use
```bash
lsof -i :8080  # Find process
kill -9 <PID>
```

### Python audit returns empty results
- Ensure Java backend has been running and decisions were stored
- Check that `audit/bias_audit.py` points to correct SQLite file

## Production Deployment

### Docker (Java Backend)

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY ruleengine /app
RUN mvn clean package
CMD ["java", "-jar", "target/ruleengine-0.0.1-SNAPSHOT.jar"]
```

```bash
docker build -t fair-loan-engine .
docker run -p 8080:8080 -v /data/decisions.db:/app/decisions.db fair-loan-engine
```

### Upgrade to PostgreSQL

1. Add PostgreSQL dependency to `ruleengine/pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

2. Update `application.properties`:
```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.datasource.url=jdbc:postgresql://localhost:5432/fairloan
spring.datasource.username=user
spring.datasource.password=pass
```

3. Update `audit/bias_audit.py`:
```python
import psycopg2
conn = psycopg2.connect("dbname=fairloan user=user password=pass")
```

## Monitoring

- Backend logs: Check console output or set up Spring Boot logging
- Audit results: Export via `/api/audit/report` endpoint
- Database: Query directly with `sqlite3 decisions.db` or tools like DB Browser

## Scaling

**Current Limits:**
- Designed for ~1000s of decisions per day
- Single-threaded rule evaluation

**For Higher Volume:**
- Move to PostgreSQL
- Add connection pooling (HikariCP)
- Implement async decision processing
- Add caching layer for rule evaluation
