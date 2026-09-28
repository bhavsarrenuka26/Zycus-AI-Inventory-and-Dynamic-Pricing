# StockPulse

**AI Inventory & Dynamic Pricing Engine**

StockPulse is an AI-powered inventory management system that monitors stock levels and demand, then generates dynamic pricing and reorder recommendations. Recommendations are reviewed and approved by a human before being applied.

## Features

- Product and inventory management
- Dynamic pricing recommendations
- Demand velocity analysis
- AI-powered pricing recommendations
- AI-powered reorder recommendations
- Automatic suggestions triggered by inventory changes
- Human approval and rejection of recommendations
- Inventory dashboard
- Recommendation dashboard
- Rule-based fallback when AI is unavailable
- Persistent H2 database
- REST APIs
- Backend unit and integration testing

## Tech Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Data JPA
- H2 Database
- Maven
- REST APIs

### Frontend

- React
- Vite
- JavaScript
- Tailwind CSS

### AI

- OpenAI-compatible LLM API
- Qwen-based model
- Rule-based fallback

## Architecture

```text
React Frontend
       |
       | REST API
       v
Spring Boot Backend
       |
       +-------------------+
       |                   |
       v                   v
  Inventory             Orders
  Management            / Sales
       |                   |
       +---------+---------+
                 |
                 v
       Inventory Change Event
                 |
                 v
        Decision Engine
          /           \
         /             \
        v               v
   Rule Engine       AI Engine
        \               /
         \             /
          v           v
       Recommendations
                 |
                 v
          Human Review
           /        \
          v          v
       Accept      Reject

##Project Structure
stock-pulse/
│
├── backend/
│   └── demo/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/example/demo/
│       │   │   │       ├── controller/
│       │   │   │       ├── service/
│       │   │   │       ├── repository/
│       │   │   │       ├── entity/
│       │   │   │       ├── dto/
│       │   │   │       ├── ai/
│       │   │   │       ├── rules/
│       │   │   │       └── event/
│       │   │   └── resources/
│       │   │       └── application.properties
│       │   │
│       │   └── test/
│       │
│       ├── pom.xml
│       └── mvnw.cmd
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── package.json
│   └── vite.config.js
│
├── ADR.md
└── README.md

Pricing Logic

The rule-based pricing engine follows these conditions:

If stock < reorder threshold
    Increase price by 10%

Else if demand velocity > 2 × category average
    Increase price by 5%

Else
    HOLD current price

API Endpoints
Products
POST   /products
GET    /products
PATCH  /products/{id}/stock
POST   /products/{id}/orders
Suggestions
POST   /products/{id}/suggest-pricing
POST   /products/{id}/suggest-reorder

GET    /pricing-suggestions
GET    /reorder-suggestions

PATCH  /pricing-suggestions/{id}
PATCH  /reorder-suggestions/{id}
Recommendation Workflow
Inventory / Order Change
          |
          v
    Event Published
          |
          v
 Asynchronous Processing
          |
          v
   Rule + AI Analysis
          |
          v
 Recommendation Created
          |
          v
     Human Review
       /       \
      /         \
   Accept      Reject
AI Integration

The backend uses an OpenAI-compatible API through a dedicated LLM gateway.

The API configuration is kept outside the source code.

LLM_API_KEY=your_api_key


## Database

![StockPulse Database](screenshots/database.png)
