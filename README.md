🏠 Airbnb Clone Backend – Spring Boot

A production-ready Airbnb Backend Clone built using Spring Boot, designed with Clean Architecture, RESTful API principles, and industry-standard security practices.
This backend provides core Airbnb functionalities including authentication, property management, bookings, reviews, role-based access control, and payment integration.

🚀 Features
🔐 Authentication & Authorization
   - User Signup & Login
   -JWT-based authentication
   -Secure password encryption using BCrypt

👥 Role-Based Access Control (RBAC)

    #ADMIN
    #HOST
    #GUEST
    #Role-specific API authorization using Spring Security

🏘️ Property Management

   #Create, update, and delete property listings
    #Upload multiple property images
    #Manage property availability
    #Hosts can manage multiple properties

🔍 Search & Browse

Search properties by:

    #Location
    #Price range
    #Availability dates
    #Number of guests
    #Pagination and sorting support

📅 Booking System

Book properties for selected dates
    Availability checks to prevent double booking
    Booking lifecycle:
    PENDING
    CONFIRMED
    CANCELLED

💳 Payment Gateway Integration(Stripe)

    Secure payment processing
    Booking confirmation after successful payment
    Payment status tracking

🧱 Technology Stack(Backend)

    Java 17
    Spring Boot
    Spring Security
    Spring Data JPA (Hibernate)
    
   * Database
    
    PostgreSQL / MySQL
    H2 Database (for testing)
    
   * Security
    
    JWT (JSON Web Token)
    BCrypt Password Encoder
    
   * Tools & Libraries
    
    Maven
    Lombok
    ModelMapper
    Swagger / OpenAPI
    Docker (optional)

    ⚙️ Prerequisites

Before running this project, ensure you have the following installed:
Java 17+
Maven 3.8+
PostgreSQL or MySQL
Docker (optional)
Basic knowledge of Spring Boot and REST APIs
