# Airbnb Clone - Hotel Booking System

A comprehensive hotel booking platform built with Spring Boot, featuring room management, inventory control, dynamic pricing, payment processing, and user authentication.

## 🌟 Features

### Core Functionality
- **User Management**: User registration, authentication, and profile management
- **Hotel Management**: Create, update, and manage hotel listings with contact information
- **Room Administration**: Manage room types, pricing, and inventory
- **Booking System**: Complete booking lifecycle from reservation to confirmation
- **Inventory Management**: Real-time room availability tracking with locking mechanisms
- **Dynamic Pricing**: Multiple pricing strategies including surge, holiday, occupancy, and urgency-based pricing
- **Payment Integration**: Stripe payment gateway with checkout sessions and webhooks
- **Reporting**: Generate hotel performance reports with revenue analytics

### Security & Authentication
- JWT-based authentication and authorization
- Role-based access control (Admin, Hotel Owner, User)
- Secure API endpoints with Spring Security
- Global exception handling and response formatting

## 🛠️ Tech Stack

### Backend
- **Spring Boot 4.0.6** - Main framework
- **Java 17** - Programming language
- **Spring Data JPA** - Database ORM
- **Spring Security** - Security framework
- **PostgreSQL** - Primary database
- **MySQL** - Alternative database support

### Libraries & Tools
- **ModelMapper 3.2.0** - Object mapping
- **JWT (jjwt 0.13.0)** - Token-based authentication
- **Stripe Java 32.1.0** - Payment processing
- **SpringDoc OpenAPI 3.0.3** - API documentation
- **Lombok** - Code generation
- **Maven** - Build tool

## 📁 Project Structure

```
src/main/java/com/project/airbnb/
├── AirbnbApplication.java          # Main application entry point
├── advice/                          # Global exception handling
│   ├── ApiError.java
│   ├── ApiResponse.java
│   ├── GlobalExceptionHandler.java
│   └── GlobalResponseHandler.java
├── config/                          # Configuration classes
│   ├── MapperConfig.java
│   └── StripeConfig.java
├── controller/                      # REST API controllers
│   ├── AuthController.java
│   ├── HotelBookingController.java
│   ├── HotelBrowseController.java
│   ├── HotelController.java
│   ├── InventoryController.java
│   ├── RoomAdminController.java
│   ├── UserController.java
│   └── WebHookController.java
├── dto/                             # Data transfer objects
│   ├── BookingDto.java
│   ├── HotelDto.java
│   ├── RoomDto.java
│   └── UserDto.java
├── entity/                          # Database entities
│   ├── Booking.java
│   ├── Hotel.java
│   ├── Room.java
│   ├── User.java
│   └── enums/                       # Enumerations
├── exception/                       # Custom exceptions
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── repository/                      # Data access layer
│   ├── BookingRepository.java
│   ├── HotelRepository.java
│   └── UserRepository.java
├── security/                        # Security configuration
│   ├── AuthService.java
│   ├── JWTAuthFilter.java
│   ├── JWTService.java
│   └── WebSecurityConfig.java
├── service/                         # Business logic
│   ├── BookingServiceImpl.java
│   ├── HotelServiceImpl.java
│   ├── CheckoutServiceImpl.java
│   └── UserServiceImpl.java
├── strategy/                        # Pricing strategies
│   ├── BasePriceStrategy.java
│   ├── HolidayPricingStrategy.java
│   ├── OccupancyPricingStrategy.java
│   ├── SurgePricingStrategy.java
│   ├── UrgencyPricingStrategy.java
│   └── PricingService.java
└── util/                            # Utility classes
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Stripe account (for payment processing)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd airbnb
   ```

2. **Configure database**
   - Create a PostgreSQL database named `airbnb`
   - Update database credentials in `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/airbnb
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. **Configure Stripe**
   - Get your Stripe API keys from the Stripe Dashboard
   - Update in `application.properties`:
     ```properties
     stripe.api.key=sk_test_your_stripe_key
     stripe.webhook.secret=whsec_your_webhook_secret
     ```

4. **Build the project**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:3501`

## 🔌 API Documentation

### Swagger UI
Once the application is running, access the interactive API documentation at:
```
http://localhost:3501/api/v1/swagger-ui.html
```

### Key API Endpoints

#### Authentication
- `POST /api/v1/auth/signup` - User registration
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/me` - Get current user profile

#### Hotel Management (Admin)
- `POST /api/v1/admin/hotels` - Create new hotel
- `GET /api/v1/admin/hotels` - Get all hotels
- `GET /api/v1/admin/hotels/{id}` - Get hotel by ID
- `PUT /api/v1/admin/hotels/{id}` - Update hotel
- `DELETE /api/v1/admin/hotels/{id}` - Delete hotel
- `PATCH /api/v1/admin/hotels/{id}/activate` - Activate hotel

#### Room Management
- `POST /api/v1/admin/rooms` - Add room to hotel
- `GET /api/v1/admin/rooms/{id}` - Get room details
- `PUT /api/v1/admin/rooms/{id}` - Update room

#### Inventory Management
- `POST /api/v1/inventory` - Update room inventory
- `GET /api/v1/inventory/{roomId}` - Get room availability

#### Booking System
- `POST /api/v1/bookings/initialize` - Initialize booking
- `POST /api/v1/bookings/{id}/guests` - Add guests to booking
- `POST /api/v1/bookings/{id}/payment` - Initiate payment
- `GET /api/v1/bookings/{id}/status` - Get booking status
- `DELETE /api/v1/bookings/{id}` - Cancel booking
- `GET /api/v1/bookings/my` - Get user's bookings

#### Hotel Browsing
- `GET /api/v1/hotels/search` - Search hotels
- `GET /api/v1/hotels/{id}/details` - Get hotel details

#### Webhooks
- `POST /api/v1/webhooks/stripe` - Stripe payment webhook

## 💡 Key Features Implementation

### Dynamic Pricing Strategies

The platform implements multiple pricing strategies that can be combined:

1. **Base Price Strategy** - Standard room pricing
2. **Holiday Pricing** - Increased rates during holidays
3. **Occupancy Pricing** - Dynamic pricing based on demand
4. **Surge Pricing** - High-demand period pricing
5. **Urgency Pricing** - Last-minute booking discounts

### Booking Lifecycle

1. **Initialize** - Check availability and reserve inventory
2. **Add Guests** - Add guest information to booking
3. **Payment** - Process payment via Stripe checkout
4. **Confirmation** - Finalize booking and update inventory
5. **Cancellation** - Cancel booking and refund payment

### Security Features

- JWT token-based authentication
- Role-based access control
- Password encryption
- Secure endpoint protection
- Global exception handling

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```

## 🔧 Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/airbnb
spring.datasource.username=pac_user
spring.datasource.password=pac_user
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Server Configuration
```properties
server.port=3501
server.servlet.context-path=/api/v1
```

### Security Configuration
```properties
jwt.secretKey=your-secret-key
frontend.url=http://localhost:3501
```

## 📊 Database Schema

The application uses the following main entities:

- **User** - User accounts and authentication
- **Hotel** - Hotel listings and information
- **Room** - Room types and specifications
- **Inventory** - Room availability tracking
- **Booking** - Booking records and status
- **Guest** - Guest information
- **Payment** - Payment transaction records

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Built with Spring Boot
- Payment processing powered by Stripe
- API documentation with SpringDoc OpenAPI

## 📞 Support

For support and questions, please open an issue in the repository.

---

**Note**: This is a clone project for educational purposes. Please ensure you have proper authorization for any production deployment and comply with Stripe's terms of service.