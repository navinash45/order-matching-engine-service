Order Matching Engine – Technical Documentation
 Overview
Java-based order matching engine with in-memory order processing, rate limiting, Swagger API docs, caching, and monitoring.
________________________________________
 Swagger API Documentation
•	Swagger UI:
http://localhost:8080/swagger-ui/index.html
•	OpenAPI JSON:
http://localhost:8080/v3/api-docs
________________________________________
 Rate Limiting
Limits to 15 requests per minute per IP; returns HTTP 429 when exceeded.
________________________________________
 REST API Endpoints
•	Place new order:
POST /orders
•	Get order by ID:
GET /orders/{id}
•	Get orders by symbol:
GET /orders/symbol/{symbol}
________________________________________
 Monitoring & Performance
Metrics (Micrometer)
Access metrics at:
http://localhost:8080/actuator/metrics
Specific examples:
•	http://localhost:8080/actuator/metrics/orders.total
•	http://localhost:8080/actuator/metrics/orders.matched
•	http://localhost:8080/actuator/metrics/jvm.memory.used
Supports integration with Prometheus/Grafana.
Caching (Caffeine)
Caches frequently accessed data for 10 minutes with a max of 1000 entries.
Actuator Endpoints
•	Health check:
http://localhost:8080/actuator/health
•	Metrics overview:
http://localhost:8080/actuator/metrics
•	Prometheus scrape (if enabled):
http://localhost:8080/actuator/prometheus
________________________________________
Future Improvements
•	Add database persistence
•	Secure endpoints with JWT/basic auth
•	Enhance metrics and monitoring dashboards
•	Use distributed rate limiting (Redis/Guava)
•	Support market and limit orders
