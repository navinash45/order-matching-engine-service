📘 Order Matching Engine – Technical Documentation
✅ Overview
This is a Java-based Order Matching Engine built using Spring Boot, featuring:
•	BUY/SELL order processing with in-memory matching logic
•	Prioritized order queues (by price and timestamp)
•	Swagger (OpenAPI 3.0) for interactive API docs
•	Rate limiting per IP using a sliding window algorithm
•	Modular design with support for in-memory or database repositories
________________________________________
🔍 Swagger API Documentation
The project integrates Springdoc OpenAPI to automatically generate interactive API documentation.
🔧 Setup
Dependency (in pom.xml):
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.4</version>
</dependency>
🧾 Access URLs
Resource	URL
Swagger UI	http://localhost:8080/swagger-ui/index.html
OpenAPI JSON	http://localhost:8080/v3/api-docs
📝 Example Annotations
@Operation(summary = "Place a new order", description = "Places a buy or sell order and matches if possible")
@ApiResponse(responseCode = "200", description = "Order placed successfully")
@PostMapping("/orders")
public ResponseEntity<OrderDto> addOrder(@RequestBody Order order) {
    return ResponseEntity.ok(orderService.addOrder(order));
}
________________________________________
🚦 Rate Limiting (Sliding Window)
To prevent abuse and overloading, the engine implements IP-based rate limiting.
⏱ Sliding Window Logic
•	Max: 15 requests per minute per IP
•	Timestamps tracked in Deque<Long> per IP
•	Automatically rejects requests with HTTP 429 Too Many Requests when limit exceeded
💻 Filter Implementation
@Component
public class RateLimitingFilter implements Filter {
    private static final int MAX_REQUESTS = 15;
    private static final long WINDOW_SECONDS = 60;
    private final Map<String, Deque<Long>> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String ip = ((HttpServletRequest) request).getRemoteAddr();
        long now = Instant.now().getEpochSecond();
        Deque<Long> timestamps = requestTimestamps.computeIfAbsent(ip, k -> new LinkedList<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() >= WINDOW_SECONDS) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= MAX_REQUESTS) {
                ((HttpServletResponse) response).setStatus(429);
                response.getWriter().write("Too many requests. Please try again later.");
                return;
            }
            timestamps.addLast(now);
        }

        chain.doFilter(request, response);
    }
}
________________________________________
🔗 REST API Endpoints
Method	Endpoint	Description
POST	/orders	Place a new buy/sell order
GET	/orders/{id}	Get an order by ID
GET	/orders/symbol/{symbol}	Get all orders for a specific symbol
✅ Request Example – Place Order
POST /orders
{
  "id": "order123",
  "orderType": "BUY",
  "price": 100.5,
  "quantity": 10,
  "remainingQuantity": 10,
  "symbol": "AAPL",
  "timestamp": 1719155000
}
Response:
{
  "id": "order123",
  "price": 100.5,
  "quantity": 10,
  "type": "BUY",
  "symbol": "AAPL",
  "timestamp": 1719155000,
  "status": "NONE"
}
________________________________________
🧪 Testing & Usage
1.	Run the application:
./mvnw spring-boot:run
2.	Test using Swagger UI:
Visit: http://localhost:8080/swagger-ui/index.html
3.	Test Rate Limiting:
Use a tool like curl or Postman to send more than 15 requests/minute from the same IP — it will return:
HTTP 429 Too Many Requests
Too many requests. Please try again later.
________________________________________
🛠 Recommendations & Future Improvements
Area	Suggestion
Persistence	Add database support using JPA + H2/Postgres
Security	Add JWT or basic auth for sensitive endpoints
Metrics/Logging	Use Micrometer + Prometheus for monitoring
Rate Limiting	Move to Redis/Guava for distributed scalability
Matching Engine	Add support for market/limit orders
________________________________________
📎 Technologies Used
•	Java 17
•	Spring Boot
•	Spring Web
•	Lombok
•	Swagger (Springdoc OpenAPI)
•	In-memory ConcurrentHashMap + PriorityQueue
•	Custom Rate Limiter (Sliding Window)
