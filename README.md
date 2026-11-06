# 🍽️ SplitBite – Enterprise Smart Restaurant Bill Splitting & Settle Up Platform

> A production-grade **Java Full Stack** web application designed to solve group dining expense splitting, itemized proportional tax division (CGST & SGST), and clean **Splitwise-Style Debt Settlement Tracking**.

---

## 📌 Project Overview & Real-World Value

When dining out in a group, calculating who owes what is often chaotic—especially when some dishes are eaten by one person while others are shared among friends. Even after calculating shares, keeping track of who settled and who is still pending causes confusion.

**SplitBite solves both problems completely:**
1. **Accurate Itemized Split:** Divides dishes equally or individually, with exact proportional CGST (2.5%) + SGST (2.5%), service charges, and discounts.
2. **Debt Settlement Ledger ("Who Owes Whom"):** Displays a clean, transparent debt ledger showing exactly how much each friend owes the payer (creditor).
3. **Record Payments (Settle Up):** Allows recording payments via **Cash**, **UPI / GPay**, or **Bank Transfer** with timestamps and live progress tracking.
4. **Spring Security & User Accounts:** Any user can register an account or sign in to track their dining history and save past bills.
5. **Instant PDF Exports:** Download individual personalized itemized receipts or a master group PDF bill.

---

## 🛠️ Technology Stack & Architecture

### Backend (Java Enterprise)
- **Java 17 / 21**
- **Spring Boot 3.2+ (Spring Web REST APIs)** – Layered Controller-Service-Repository architecture
- **Spring Security 6 & JJWT 0.12.5** – Stateless token-based authentication with BCrypt password hashing
- **Spring Data JPA & Hibernate** – ORM persistence with cascading relationships and indexing
- **Springdoc OpenAPI 3.0 (Swagger UI)** – Interactive API documentation and playground (`/swagger-ui/index.html`)
- **Global Exception Handling** – `@RestControllerAdvice` with standard RFC 7807 error responses
- **OpenPDF** – Server-side PDF export

### Frontend
- **HTML5, Tailwind CSS, Plus Jakarta Sans** – Responsive, clean modern design
- **React.js 18+ & Vite** – Component-based SPA architecture
- **jsPDF & jsPDF-AutoTable** – Client-side instant PDF receipts generation

### Database & DevOps
- **MySQL 8.0+** – Relational schema (`users`, `bills`, `participants`, `food_items`, `food_assignments`)
- **Docker & Docker Compose** – Multi-container orchestration (MySQL + Spring Boot + React in one command)

---

## 📂 Project Structure

```text
SplitBite/
├── backend/                       # Java Spring Boot REST API
│   ├── src/main/java/com/splitbite/
│   │   ├── config/                # OpenApiConfig (Swagger UI & JWT Security scheme)
│   │   ├── controller/            # REST Endpoints (BillController, AuthController, SettlementController)
│   │   ├── dto/                   # DTOs (AuthRequest, AuthResponse, SettlementDto, BillCalculation)
│   │   ├── exception/             # GlobalExceptionHandler, ResourceNotFoundException, ApiErrorResponse
│   │   ├── model/                 # JPA Entities (User, Bill, Participant, FoodItem, FoodAssignment)
│   │   ├── repository/            # Spring Data JPA Repositories (UserRepository, BillRepository)
│   │   ├── security/              # JwtTokenProvider, JwtAuthenticationFilter, SecurityConfig
│   │   ├── service/               # BillCalculationService, PdfExportService
│   │   └── SplitBiteApplication.java
│   ├── src/main/resources/
│   │   └── application.properties # Server port & MySQL configuration
│   ├── Dockerfile                 # Multi-stage production container build
│   └── pom.xml                    # Maven dependencies
│
├── frontend/                      # Web Client
│   ├── src/                       # React components & utils
│   ├── standalone_app.html        # Complete standalone interactive web app (Open in any browser!)
│   ├── package.json
│   └── vite.config.js
│
├── database/
│   └── schema.sql                 # MySQL schema with users, settlements & indexes
│
├── docker-compose.yml             # Single-command local deployment
└── README.md
```

---

## 🚀 Getting Started & Running SplitBite

### Option 1: Instant Browser Run (No installation needed)
Simply double-click or open in your browser:
```text
C:\Users\91854\.gemini\antigravity\scratch\SplitBite\frontend\standalone_app.html
```
* Includes the complete full-stack feature set: Bill Creation, Individual Split, Settle Up Ledger, History, Analytics, and PDF Export!

### Option 2: Docker Compose (All services in one command)
```bash
docker compose up -d
```
- Backend API & Swagger: `http://localhost:8080/swagger-ui/index.html`
- MySQL Database: `localhost:3306`

### Option 3: Running Spring Boot Backend Manually
```bash
cd backend
mvn spring-boot:run
```
- API Health Check: `http://localhost:8080/api/bills/health`
- Interactive Swagger Playground: `http://localhost:8080/swagger-ui/index.html`


