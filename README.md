# 🍽️ SplitBite – Smart Restaurant Bill Splitting Website

> A beginner-friendly, production-ready **Java Full Stack** web application designed to make dining expense splitting fair, accurate, and completely hassle-free.

---

## 📌 Project Overview

When dining out in a group, calculating who owes what is often messy—especially when some dishes are eaten by one person while others are shared among a few friends. 

**SplitBite** solves this problem by allowing users to:
1. Enter restaurant and ordered food items with prices and quantities.
2. Add group members (participants).
3. Split the bill **Equally** or assign dishes **Individually** (including multi-person shared dishes).
4. Automatically calculate taxes, item portions, and exact rounded amounts for each person.
5. Download **individual personalized PDF bills** or a **complete master group PDF bill** with a single click.

---

## 🛠️ Technologies Used

### Frontend
- **React.js (v18+)** – Component-based UI and reactive state management
- **Tailwind CSS** – Clean modern styling with light background (`#f8fafc`), dark navy typography (`#0f172a`), and emerald green accents (`#10b981`)
- **Lucide Icons** – Crisp, modern SVG icons
- **jsPDF & jsPDF-AutoTable** – Client-side instant PDF rendering & export

### Backend
- **Java 17 / 21**
- **Spring Boot 3 (Spring Web REST APIs)** – Layered Controller-Service-Repository architecture
- **Spring Data JPA & Hibernate** – ORM database persistence
- **OpenPDF (LibrePDF)** – Server-side enterprise PDF report generation

### Database
- **MySQL 8.0+** – Relational schema storing bills, food items, participants, and assignment mappings

---

## 📂 Project Structure

```text
SplitBite/
├── backend/                       # Java Spring Boot REST API
│   ├── src/main/java/com/splitbite/
│   │   ├── controller/            # REST API Endpoints (BillController)
│   │   ├── dto/                   # Request & Response Data Transfer Objects
│   │   ├── model/                 # JPA Entities (Bill, FoodItem, Participant, FoodAssignment)
│   │   ├── repository/            # Spring Data JPA interfaces
│   │   ├── service/               # Bill calculation logic & OpenPDF generation
│   │   └── SplitBiteApplication.java
│   ├── src/main/resources/
│   │   └── application.properties # Server port & MySQL configuration
│   └── pom.xml                    # Maven dependencies
│
├── frontend/                      # React.js Application
│   ├── src/
│   │   ├── components/            # Navbar, CreateBill, Participants, SplitOptions, Summary
│   │   ├── utils/                 # Accurate calculation math & PDF generator
│   │   ├── App.jsx                # Main orchestration component
│   │   ├── index.css              # Tailwind styling
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.js
│
├── database/
│   └── schema.sql                 # MySQL Database Schema DDL
│
├── .gitignore                     # Git ignore rules for node_modules, target, etc.
└── README.md                      # Documentation
```

---

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed on your machine:
- **Node.js (v18+)** & `npm`
- **Java Development Kit (JDK 17 or 21)**
- **MySQL Server 8.0+** (or MySQL Workbench)
- **Git**

---

### Step 1: Database Setup (MySQL)

1. Open **MySQL Workbench** or your MySQL command line.
2. Execute the script located in `database/schema.sql` or run:
```sql
CREATE DATABASE splitbite_db;
```
3. Update your database username and password in `backend/src/main/resources/application.properties` if different from the default:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

---

### Step 2: Running the Spring Boot Backend

Open your terminal in the `backend/` directory:

```bash
cd backend
# If Maven is installed:
mvn spring-boot:run

# Or run SplitBiteApplication.java directly in your IDE (IntelliJ IDEA / Eclipse / VS Code)
```

The backend server will start on: **`http://localhost:8080`**  
Test API health: **`http://localhost:8080/api/bills/health`**

---

### Step 3: Running the React Frontend

Open a new terminal in the `frontend/` directory:

```bash
cd frontend
npm install
npm run dev
```

Open your browser and navigate to: **`http://localhost:3000`**

---

## ✨ Features Implemented

- [x] **Feature 1: Create Bill** – Enter restaurant name, food items, price per item, and quantity. Shows live total bill banner.
- [x] **Feature 2: Add Participants** – Dynamically add/remove group members with avatar tags.
- [x] **Feature 3: Equal Bill Splitting** – Evenly divides the total among all participants with penny-accurate rounding.
- [x] **Feature 4: Individual Food Assignment** – Tag individuals to specific dishes or share a dish across multiple people with live portion calculation.
- [x] **Feature 5: Individual Bill Summary** – Displays clean breakdown cards with participant subtotal and final payable amounts.
- [x] **Feature 6: Individual PDF Download** – Download personalized, branded PDF bills for each participant.
- [x] **Feature 7: Download All Bills** – Export a complete master group summary PDF in one click.

---

## 🧮 Calculation Logic

1. **Equal Splitting:** $\text{Share} = \frac{\text{Total Bill}}{\text{Count of Participants}}$ (Adjusted for exact roundoff match).
2. **Individual Splitting:**
   $$\text{Dish Share} = \frac{\text{Item Price} \times \text{Item Quantity}}{\text{Number of people sharing the dish}}$$
3. **Accuracy Guarantee:** $\sum \text{Individual Amounts} = \text{Total Restaurant Bill}$.

---

## 📄 License
This project is open-source and available under the **MIT License**.
