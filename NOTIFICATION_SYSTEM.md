# Notification System Implementation

## Overview
This document describes the comprehensive notification system implemented for the Application Portal Backend. The system provides both email and real-time web notifications to keep users informed about their application status changes.

## Features Implemented

### 1. User Creation Improvements
- Enhanced UserService with proper CRUD operations
- Improved validation and error handling
- Transaction management for data consistency
- Comprehensive logging for debugging

### 2. Notification System
- **Email Notifications**: Official communication channel for critical events
- **Web Notifications**: Real-time in-app notifications via WebSocket
- **User-Specific**: All notifications are targeted to specific users
- **Event-Driven**: Automatic triggering based on application status changes

### 3. Global Exception Handling
- Comprehensive error handling with user-friendly messages
- Structured error responses with timestamps and paths
- Security exception handling
- Validation error documentation
- Logging for debugging and monitoring

## Notification Triggers

The system automatically sends notifications for the following events:

1. **Application Submitted** (`APPLICATION_SUBMITTED`)
   - Triggered when user completes and submits application
   - Confirms successful submission
   - Provides application ID and submission timestamp

2. **Application Under Review** (`APPLICATION_UNDER_REVIEW`)
   - Triggered when admin changes status to UNDER_REVIEW
   - Informs user that review process has started

3. **Interview Scheduled** (`INTERVIEW_SCHEDULED`)
   - Triggered when admin schedules an interview
   - Includes interview details and instructions

4. **Application Accepted** (`APPLICATION_ACCEPTED`)
   - Triggered when admin accepts the application
   - Welcome message with next steps

5. **Application Rejected** (`APPLICATION_REJECTED`)
   - Triggered when admin rejects the application
   - Professional rejection message with encouragement

## API Endpoints

### User Notification Endpoints
```
GET /api/v1/notifications - Get all user notifications
GET /api/v1/notifications/unread - Get unread notifications
GET /api/v1/notifications/unread/count - Get unread count
PUT /api/v1/notifications/{id}/read - Mark notification as read
PUT /api/v1/notifications/read-all - Mark all as read
```

### Admin Application Management Endpoints
```
PUT /api/v1/applications/{id}/status - Update application status
PUT /api/v1/applications/{id}/schedule-interview - Schedule interview
PUT /api/v1/applications/{id}/accept - Accept application
PUT /api/v1/applications/{id}/reject - Reject application
```

### Application Progress Endpoints (Enhanced)
```
PUT /api/v1/applications/progress/{id}/submit - Submit application (triggers notification)
```

## Real-Time Notifications

### WebSocket Configuration
- Endpoint: `/ws`
- User-specific queues: `/user/{userId}/queue/notifications`
- SockJS fallback support for older browsers

### Frontend Integration
```javascript
// Connect to WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to user-specific notifications
    stompClient.subscribe('/user/queue/notifications', function(notification) {
        const data = JSON.parse(notification.body);
        displayNotification(data);
    });
});
```

## Database Schema

### Notifications Table
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    application_id BIGINT,
    application_status VARCHAR(50)
);
```

## Security Features

### User Authorization
- Users can only access their own notifications
- Application access validation in progress controller
- JWT-based authentication for all endpoints

### Data Protection
- User-specific notification queues
- Secure WebSocket connections
- Input validation and sanitization

## Error Handling

### Global Exception Handler
Provides structured error responses for:
- `NotFoundException` (404)
- `ValidationException` (400)
- `SecurityException` (403)
- `BusinessRuleException` (422)
- `DataIntegrityViolationException` (409)
- Generic exceptions (500)

### Error Response Format
```json
{
    "timestamp": "2024-01-15T10:30:00Z",
    "status": 404,
    "error": "Resource not found",
    "message": "Application not found with id: 123",
    "path": "/api/v1/applications/123",
    "success": false
}
```

## Configuration

### Email Configuration
Add to `application.com.igirerwanda.application_portal_backend.resources.properties`:
```com.igirerwanda.application_portal_backend.resources.properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.com.igirerwanda.application_portal_backend.resources.properties.mail.smtp.auth=true
spring.mail.com.igirerwanda.application_portal_backend.resources.properties.mail.smtp.starttls.enable=true
```

### WebSocket Configuration
Automatically configured via `WebSocketConfig` class.

## Usage Examples

### Triggering Notifications (Admin)
```java
// Accept application
ApplicationDto result = applicationService.acceptApplication(applicationId);
// Automatically sends email + web notification to user
```

### Receiving Notifications (Frontend)
```javascript
// Display real-time notification
function displayNotification(notification) {
    const toast = document.createElement('div');
    toast.className = 'notification-toast';
    toast.innerHTML = `
        <h4>${notification.title}</h4>
        <p>${notification.message}</p>
    `;
    document.body.appendChild(toast);
}
```

## Testing

### Manual Testing
1. Submit an application → Check email and web notification
2. Admin changes status → Verify user receives notification
3. Schedule interview → Confirm notification with details
4. Accept/Reject application → Validate appropriate messages

### Integration Testing
- Test WebSocket connections
- Verify email delivery
- Check notification persistence
- Validate user authorization

## Monitoring and Logging

### Logging Levels
- `INFO`: Successful operations
- `WARN`: Business rule violations, security issues
- `ERROR`: System errors, email failures

### Key Metrics to Monitor
- Notification delivery success rate
- WebSocket connection stability
- Email sending failures
- User engagement with notifications

## Future Enhancements

1. **Push Notifications**: Mobile app integration
2. **Notification Preferences**: User-configurable settings
3. **Batch Notifications**: Digest emails for multiple updates
4. **Rich Content**: HTML email templates with branding
5. **Analytics**: Notification engagement tracking
6. **Retry Mechanism**: Failed notification retry logic

## Dependencies Added

```xml
<!-- WebSocket for real-time notifications -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

## Conclusion

This notification system provides a robust, real-time communication channel between the application and users. It ensures users are always informed about their application status while maintaining security and providing excellent user experience through both email and web notifications.