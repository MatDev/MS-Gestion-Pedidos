# Food Delivery System

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
  - [Microservices](#microservices)
  - [Additional Infrastructure](#additional-infrastructure)
- [System Flow](#system-flow)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)

## Overview
This project is a comprehensive food delivery system built using a microservices architecture. The system enables restaurants to manage their menus, customers to place orders, and delivery personnel to handle deliveries efficiently.

## Architecture

### Microservices

#### 1. User Service
- Manages user accounts (customers, restaurants, delivery personnel)
- Key functions: registration, authentication, profile management
- Database: **PostgreSQL**

#### 2. Menu Service
- Handles restaurant menu management
- Key functions: menu creation, updates, queries
- Database: **MongoDB** (optimized for dynamic, unstructured menu data)

#### 3. Order Service
- Manages order processing
- Key functions: order creation, status updates, history tracking
- Database: **MySQL** (optimized for transactional data)

#### 4. Delivery Service
- Handles delivery assignments and tracking
- Key functions: delivery assignment, real-time status updates
- Database: **Redis** (optimized for real-time state management)

### Additional Infrastructure

- **Spring Cloud Gateway**: Central request routing and management
- **Spring Cloud Config**: Centralized configuration management
- **Eureka Server**: Service discovery
- **Spring Security with OAuth2**: Authentication and authorization
- **RabbitMQ/Kafka**: Asynchronous service communication

## System Flow

1. Customer authenticates via User Service
2. Browses restaurant menus through Menu Service
3. Places order through Order Service
4. Delivery Service assigns order to available delivery personnel

## Technology Stack

### Backend
- **Framework**: Spring Boot
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Security**: Spring Security
- **ORM**: Hibernate
- **Message Broker**: RabbitMQ/Kafka

### Databases
- PostgreSQL (Users)
- MongoDB (Menus)
- MySQL (Orders)
- Redis (Real-time States)

### Frontend
- **Framework**: React/Angular
- **UI Components**: Bootstrap/TailwindCSS

### DevOps
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Monitoring**: Prometheus + Grafana
- **CI/CD**: Jenkins/GitHub Actions

## Getting Started

### Prerequisites
- JDK 17 or later
- Docker and Docker Compose
- Node.js (for frontend development)
- Maven or Gradle

### Installation
```bash
# Clone the repository
git clone [repository-url]

# Navigate to project directory
cd food-delivery-system

# Build the project
./mvnw clean install

# Start services using Docker Compose
docker-compose up
```

### Configuration
1. Configure database connections in `application.properties`
2. Set up environment variables for sensitive data
3. Configure service discovery settings

## License
[Add your license information here]

## Contributing
[Add contribution guidelines here]

## Contact
[Add contact information here]
