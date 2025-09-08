# 🔐 Secure Notes Application

A comprehensive, production-ready note management application featuring advanced security mechanisms, multi-layered authentication, and enterprise-grade audit capabilities.  
Built with modern **Java technologies** and following industry best practices for secure application development.

---

## ✨ Key Features

### 🔐 Advanced Security Implementation
- **JWT Authentication**: Configurable token expiration and refresh mechanisms  
- **OAuth2 Integration**: Seamless authentication with GitHub and Google providers  
- **Two-Factor Authentication (2FA)**: TOTP-based implementation with QR code generation  
- **Role-Based Access Control (RBAC)**: Granular permissions with USER and ADMIN roles  
- **CSRF Protection**: Cookie-based token repository for enhanced security  
- **Password Encryption**: Industry-standard BCrypt hashing algorithm  

### 📊 Audit & Compliance
- **Comprehensive Audit Logging**: Track all note operations (CREATE, UPDATE, DELETE)  
- **User Activity Monitoring**: Timestamped logs for compliance and security analysis  
- **Admin Dashboard**: Complete user management and system oversight capabilities  
- **Account Management**: Lockout, expiry, and credential lifecycle controls  

### 🏗️ Enterprise Architecture
- **RESTful API Design**: Proper HTTP status codes and comprehensive error handling  
- **Service Layer Architecture**: Clean separation of concerns with dependency injection  
- **JPA Entities**: Well-defined relationships and database constraints  
- **Environment Configuration**: Externalized properties for dev/staging/production  
- **Modular Codebase**: Following Spring Boot and enterprise development best practices  

---

## 🚀 Tech Stack
- **Backend**: Java, Spring Boot, Spring Security  
- **Database**: MySQL (configurable)  
- **Authentication**: JWT, OAuth2, TOTP  
- **Build Tool**: Maven 
- **Deployment**: Docker-ready with environment profiles  

---


