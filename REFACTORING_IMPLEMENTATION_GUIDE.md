# Backend Refactoring Implementation Guide

##  COMPLETED STEPS:

### 1. Configuration Updates
-   Updated `.env` with Resend API key and JWT settings
-   Removed SMTP configuration from `application.properties`
-   Added Resend HTTP API configuration
-   Added JWT token expiration settings
-   Added WebFlux dependency for HTTP client

##  REMAINING IMPLEMENTATION STEPS:

### TASK 1: Email Service Migration (Critical - Render Blocker)

**Files to Create:**
1. `src/main/java/com/igirerwanda/application_portal_backend/notification/service/ResendEmailService.java`
2. `src/main/java/com/igirerwanda/application_portal_backend/notification/config/ResendConfig.java`

**Files to Modify:**
1. `EmailService.java` - Replace SMTP with Resend HTTP calls
2. `RegistrationService.java` - Use new email service
3. Remove all `JavaMailSender` dependencies

**Implementation:**
```java
// ResendEmailService.java
@Service
public class ResendEmailService {
    @Value("${resend.api.key}")
    private String apiKey;
    
    @Value("${resend.from.email}")
    private String fromEmail;
    
    private final WebClient webClient;
    
    public void sendEmail(String to, String subject, String htmlContent) {
        webClient.post()
            .uri("https://api.resend.com/emails")
            .header("Authorization", "Bearer " + apiKey)
            .bodyValue(Map.of(
                "from", fromEmail,
                "to", to,
                "subject", subject,
                "html", htmlContent
            ))
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }
}
```

---

### TASK 2: Remove Username Uniqueness

**Files to Modify:**
1. `Register.java` entity - Remove `@Column(unique = true)` from username
2. `RegisterRepository.java` - Remove `findByUsername` if not needed
3. `RegistrationService.java` - Remove username uniqueness check
4. Database migration - Drop unique constraint on username

**SQL Migration:**
```sql
ALTER TABLE register DROP CONSTRAINT IF EXISTS uk_username;
```

---

### TASK 3: JWT Token Expiration & Refresh Tokens

**Files to Create:**
1. `RefreshToken.java` entity
2. `RefreshTokenRepository.java`
3. `RefreshTokenService.java`
4. `TokenRefreshRequest.java` DTO
5. `TokenRefreshResponse.java` DTO

**Files to Modify:**
1. `JwtService.java` - Add expiration validation
2. `AuthController.java` - Add `/refresh-token` endpoint
3. `LoginResponse.java` - Include refresh token

**Key Changes:**
```java
// JwtService.java
public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}

public String generateRefreshToken(Register user) {
    return Jwts.builder()
        .setSubject(user.getEmail())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
}
```

---

### TASK 4: Google Login/Signup Unification

**Files to Modify:**
1. `GoogleAuthService.java` - Merge login and signup logic
2. `GoogleAuthController.java` - Single endpoint for both

**Implementation Logic:**
```java
public LoginResponse authenticateWithGoogle(String idToken) {
    // 1. Verify token with Google
    GoogleIdToken.Payload payload = verifyGoogleToken(idToken);
    
    // 2. Check if user exists
    Optional<Register> existingUser = registerRepo.findByEmail(payload.getEmail());
    
    Register user;
    if (existingUser.isPresent()) {
        // Login existing user
        user = existingUser.get();
    } else {
        // Auto-register new user
        user = createGoogleUser(payload);
    }
    
    // 3. Generate tokens
    return generateTokenResponse(user);
}
```

---

### TASK 5: WebSocket Real-Time Synchronization

**Files to Create:**
1. `WebSocketConfig.java` - WebSocket configuration
2. `WebSocketEventService.java` - Event broadcasting
3. `WebSocketController.java` - WebSocket endpoints
4. `WebSocketEvent.java` - Event DTO

**WebSocket Events:**
- `USER_REGISTERED`
- `USER_UPDATED`
- `APPLICATION_CREATED`
- `APPLICATION_UPDATED`
- `APPLICATION_STATUS_CHANGED`

**Implementation:**
```java
// WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
            .setAllowedOrigins("*")
            .withSockJS();
    }
}

// WebSocketEventService.java
@Service
public class WebSocketEventService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void broadcastUserRegistered(UserDto user) {
        messagingTemplate.convertAndSend("/topic/admin/users", 
            new WebSocketEvent("USER_REGISTERED", user));
    }
    
    public void broadcastApplicationUpdate(ApplicationDto app) {
        // To specific user
        messagingTemplate.convertAndSendToUser(
            app.getUserEmail(), 
            "/queue/applications", 
            new WebSocketEvent("APPLICATION_UPDATED", app)
        );
        
        // To all admins
        messagingTemplate.convertAndSend("/topic/admin/applications",
            new WebSocketEvent("APPLICATION_UPDATED", app));
    }
}
```

**Integration Points:**
- `RegistrationService.register()` → broadcast USER_REGISTERED
- `ApplicationService.createApplication()` → broadcast APPLICATION_CREATED
- `ApplicationService.updateApplication()` → broadcast APPLICATION_UPDATED
- `AdminService.updateUser()` → broadcast USER_UPDATED

---

##  SECURITY CHECKLIST:

- [ ] WebSocket connections authenticated
- [ ] Role-based event filtering (admin vs user)
- [ ] Refresh tokens stored securely
- [ ] API keys in environment variables only
- [ ] Token expiration enforced
- [ ] Google token verification server-side

---

##  TESTING CHECKLIST:

- [ ] Email delivery via Resend works
- [ ] Multiple users can have same username
- [ ] Access tokens expire correctly
- [ ] Refresh token flow works
- [ ] Google login creates new users
- [ ] Google login logs in existing users
- [ ] WebSocket events broadcast correctly
- [ ] Admin receives all user events
- [ ] Users receive only their events
- [ ] Reconnection handling works

---

##  DEPLOYMENT CHECKLIST (Render):

- [ ] Set RESEND_API_KEY environment variable
- [ ] Set JWT_SECRET environment variable
- [ ] Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET
- [ ] Database migrations applied
- [ ] WebSocket endpoint accessible
- [ ] CORS configured for WebSocket
- [ ] Health check endpoint working

---

##  IMPLEMENTATION ORDER:

1. **Email Service** (Critical - blocks production)
2. **Username Uniqueness** (Quick fix)
3. **JWT Expiration** (Security critical)
4. **Refresh Tokens** (Depends on #3)
5. **Google Auth** (Can be parallel with #4)
6. **WebSockets** (Last - depends on all services)

---

##  NOTES:

- All changes maintain backward compatibility where possible
- Database migrations should be reversible
- Feature flags can be used for gradual rollout
- Monitor email delivery rates after Resend migration
- Log all WebSocket connection/disconnection events

---

##  USEFUL LINKS:

- Resend API Docs: https://resend.com/docs/api-reference/emails/send-email
- WebSocket with Spring: https://spring.io/guides/gs/messaging-stomp-websocket/
- JWT Best Practices: https://tools.ietf.org/html/rfc8725

