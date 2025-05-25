package id.ac.ui.cs.advprog.ohioorder.interceptor;

import admin.AdminOuterClass;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireAdmin;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireTableSession;
import id.ac.ui.cs.advprog.ohioorder.grpc.AdminGrpcClient;
import id.ac.ui.cs.advprog.ohioorder.grpc.TableSessionGrpcClient;
import id.ac.ui.cs.advprog.ohioorder.model.Admin;
import id.ac.ui.cs.advprog.ohioorder.model.TableSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import table_session.TableSessionOuterClass;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

    @Mock
    private AdminGrpcClient adminGrpcClient;

    @Mock
    private TableSessionGrpcClient tableSessionGrpcClient;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    private AuthInterceptor authInterceptor;

    @BeforeEach
    void setUp() {
        authInterceptor = new AuthInterceptor(adminGrpcClient, tableSessionGrpcClient);
    }

    @Test
    void preHandle_NonHandlerMethod_ShouldReturnTrue() throws Exception {
        // Given
        Object nonHandlerMethod = new Object();

        // When
        boolean result = authInterceptor.preHandle(request, response, nonHandlerMethod);

        // Then
        assertTrue(result);
        verifyNoInteractions(adminGrpcClient, tableSessionGrpcClient, request, response);
    }

    @Test
    void preHandle_NoAnnotations_ShouldReturnTrue() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(false);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verifyNoInteractions(adminGrpcClient, tableSessionGrpcClient);
    }

    @Test
    void preHandle_RequireAdmin_ValidToken_ShouldReturnTrue() throws Exception {
        // Given
        String validToken = "valid-token";
        String authHeader = "Bearer " + validToken;
        String adminEmail = "admin@example.com";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        AdminOuterClass.Admin mockAdmin = AdminOuterClass.Admin.newBuilder()
                .setEmail(adminEmail)
                .build();
        AdminOuterClass.AdminResponse mockResponse = AdminOuterClass.AdminResponse.newBuilder()
                .setAdmin(mockAdmin)
                .build();
        when(adminGrpcClient.verifyAdmin(validToken)).thenReturn(mockResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verify(adminGrpcClient).verifyAdmin(validToken);
        verify(request).setAttribute(eq("authenticatedAdmin"), any(Admin.class));
        verifyNoInteractions(response);
    }

    @Test
    void preHandle_RequireAdmin_MissingAuthHeader_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
        verifyNoInteractions(adminGrpcClient);
    }

    @Test
    void preHandle_RequireAdmin_InvalidAuthHeaderFormat_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
        verifyNoInteractions(adminGrpcClient);
    }

    @Test
    void preHandle_RequireAdmin_EmptyAuthHeader_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn("");

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
        verifyNoInteractions(adminGrpcClient);
    }

    @Test
    void preHandle_RequireAdmin_OnlyBearerKeyword_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn("Bearer");

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
        verifyNoInteractions(adminGrpcClient);
    }

    @Test
    void preHandle_RequireAdmin_GrpcException_ShouldReturnFalse() throws Exception {
        // Given
        String validToken = "invalid-token";
        String authHeader = "Bearer " + validToken;
        String errorMessage = "Unauthorized: valid admin or table session required";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(adminGrpcClient.verifyAdmin(validToken)).thenThrow(new RuntimeException(errorMessage));

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(adminGrpcClient).verifyAdmin(validToken);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), errorMessage);
        verify(request, never()).setAttribute(any(), any());
    }

    @Test
    void preHandle_RequireTableSession_ValidSessionId_ShouldReturnTrue() throws Exception {
        // Given
        String sessionId = "valid-session-id";
        String tableId = "table-123";
        boolean isActive = true;

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(false);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        TableSessionOuterClass.TableSession mockTableSession = TableSessionOuterClass.TableSession.newBuilder()
                .setId(sessionId)
                .setTableId(tableId)
                .setIsActive(isActive)
                .build();
        TableSessionOuterClass.TableSessionResponse mockResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(mockTableSession)
                .build();
        when(tableSessionGrpcClient.verifyTableSession(sessionId)).thenReturn(mockResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verify(tableSessionGrpcClient).verifyTableSession(sessionId);
        verify(request).setAttribute(eq("authenticatedTableSession"), any(TableSession.class));
        verifyNoInteractions(response);
    }

    @Test
    void preHandle_RequireTableSession_MissingSessionId_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(false);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("X-Session-Id")).thenReturn(null);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: valid admin or table session required");
        verifyNoInteractions(tableSessionGrpcClient);
    }

    @Test
    void preHandle_RequireTableSession_EmptySessionId_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(false);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("X-Session-Id")).thenReturn("");

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: valid admin or table session required");
        verifyNoInteractions(tableSessionGrpcClient);
    }

    @Test
    void preHandle_RequireTableSession_GrpcException_ShouldReturnFalse() throws Exception {
        // Given
        String sessionId = "invalid-session-id";
        String errorMessage = "Unauthorized: valid admin or table session required";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(false);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);
        when(tableSessionGrpcClient.verifyTableSession(sessionId)).thenThrow(new RuntimeException(errorMessage));

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(tableSessionGrpcClient).verifyTableSession(sessionId);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), errorMessage);
        verify(request, never()).setAttribute(any(), any());
    }

    @Test
    void preHandle_BothAnnotations_ValidCredentials_ShouldProcessAdminFirst() throws Exception {
        // Given
        String validToken = "valid-token";
        String authHeader = "Bearer " + validToken;
        String adminEmail = "admin@example.com";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        AdminOuterClass.Admin mockAdmin = AdminOuterClass.Admin.newBuilder()
                .setEmail(adminEmail)
                .build();
        AdminOuterClass.AdminResponse mockResponse = AdminOuterClass.AdminResponse.newBuilder()
                .setAdmin(mockAdmin)
                .build();
        when(adminGrpcClient.verifyAdmin(validToken)).thenReturn(mockResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verify(adminGrpcClient).verifyAdmin(validToken);
        verify(request).setAttribute(eq("authenticatedAdmin"), any(Admin.class));
        // Should not check table session since admin auth succeeded
        verifyNoInteractions(tableSessionGrpcClient);
    }

    @Test
    void preHandle_BothAnnotations_InvalidAdminAuth_ShouldReturnFalse() throws Exception {
        // Given
        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
        // Should not check table session since admin auth failed
        verifyNoInteractions(tableSessionGrpcClient);
    }

    @Test
    void preHandle_BothAnnotations_InvalidAdmin_ValidTableSession_ShouldAuthenticateAsTableSession() throws Exception {
        // Given
        String token = "invalid-token";
        String sessionId = "valid-session-id";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        // Simulate admin failure
        when(adminGrpcClient.verifyAdmin(token)).thenThrow(new RuntimeException("Invalid token"));

        // Simulate table session success
        TableSessionOuterClass.TableSession tableSessionData = TableSessionOuterClass.TableSession.newBuilder()
                .setId("1")
                .setTableId("A1")
                .setIsActive(true)
                .build();
        TableSessionOuterClass.TableSessionResponse sessionResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(tableSessionData)
                .build();
        when(tableSessionGrpcClient.verifyTableSession(sessionId)).thenReturn(sessionResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verify(adminGrpcClient).verifyAdmin(token);
        verify(tableSessionGrpcClient).verifyTableSession(sessionId);
        verify(request).setAttribute(eq("authenticatedTableSession"), any(TableSession.class));
    }

    @Test
    void preHandle_BothAnnotations_InvalidAdminAndTableSession_ShouldReturnFalse() throws Exception {
        // Given
        String token = "invalid-token";
        String sessionId = "invalid-session";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        when(adminGrpcClient.verifyAdmin(token)).thenThrow(new RuntimeException("Bad token"));
        when(tableSessionGrpcClient.verifyTableSession(sessionId)).thenThrow(new RuntimeException("Bad session"));

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertFalse(result);
        verify(adminGrpcClient).verifyAdmin(token);
        verify(tableSessionGrpcClient).verifyTableSession(sessionId);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: valid admin or table session required");
    }

    @Test
    void preHandle_BothAnnotations_ValidAdminAndTableSession_ShouldHaveBoth() throws Exception {
        // Given
        String token = "valid-token";
        String sessionId = "valid-session";

        when(handlerMethod.hasMethodAnnotation(RequireAdmin.class)).thenReturn(true);
        when(handlerMethod.hasMethodAnnotation(RequireTableSession.class)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        AdminOuterClass.Admin mockAdmin = AdminOuterClass.Admin.newBuilder()
                .setEmail("admin@example.com")
                .build();
        AdminOuterClass.AdminResponse adminResponse = AdminOuterClass.AdminResponse.newBuilder()
                .setAdmin(mockAdmin)
                .build();
        when(adminGrpcClient.verifyAdmin(token)).thenReturn(adminResponse);

        // table session is valid but shouldn't matter
        TableSessionOuterClass.TableSession tableSessionData = TableSessionOuterClass.TableSession.newBuilder()
                .setId("1")
                .setTableId("A1")
                .setIsActive(true)
                .build();
        TableSessionOuterClass.TableSessionResponse sessionResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(tableSessionData)
                .build();
        when(tableSessionGrpcClient.verifyTableSession(sessionId)).thenReturn(sessionResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, handlerMethod);

        // Then
        assertTrue(result);
        verify(adminGrpcClient).verifyAdmin(token);
        verify(request).setAttribute(eq("authenticatedAdmin"), any(Admin.class));
        verify(request).setAttribute(eq("authenticatedTableSession"), any(TableSession.class));
    }

    // Test helper class for testing with actual annotations
    private static class TestController {
        @RequireAdmin
        public void adminMethod() {}

        @RequireTableSession
        public void tableSessionMethod() {}

        @RequireAdmin
        @RequireTableSession
        public void bothAnnotationsMethod() {}

        public void noAnnotationsMethod() {}
    }

    @Test
    void preHandle_RealAnnotations_RequireAdmin_ShouldWork() throws Exception {
        // Given
        Method method = TestController.class.getMethod("adminMethod");
        HandlerMethod realHandlerMethod = new HandlerMethod(new TestController(), method);

        String validToken = "valid-token";
        String authHeader = "Bearer " + validToken;
        String adminEmail = "admin@example.com";

        when(request.getHeader("Authorization")).thenReturn(authHeader);

        AdminOuterClass.Admin mockAdmin = AdminOuterClass.Admin.newBuilder()
                .setEmail(adminEmail)
                .build();
        AdminOuterClass.AdminResponse mockResponse = AdminOuterClass.AdminResponse.newBuilder()
                .setAdmin(mockAdmin)
                .build();
        when(adminGrpcClient.verifyAdmin(validToken)).thenReturn(mockResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, realHandlerMethod);

        // Then
        assertTrue(result);
        verify(adminGrpcClient).verifyAdmin(validToken);
        verify(request).setAttribute(eq("authenticatedAdmin"), any(Admin.class));
    }

    @Test
    void preHandle_RealAnnotations_RequireTableSession_ShouldWork() throws Exception {
        // Given
        Method method = TestController.class.getMethod("tableSessionMethod");
        HandlerMethod realHandlerMethod = new HandlerMethod(new TestController(), method);

        String sessionId = "valid-session-id";
        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);

        TableSessionOuterClass.TableSession mockTableSession = TableSessionOuterClass.TableSession.newBuilder()
                .setId(sessionId)
                .setTableId("table-123")
                .setIsActive(true)
                .build();
        TableSessionOuterClass.TableSessionResponse mockResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(mockTableSession)
                .build();
        when(tableSessionGrpcClient.verifyTableSession(sessionId)).thenReturn(mockResponse);

        // When
        boolean result = authInterceptor.preHandle(request, response, realHandlerMethod);

        // Then
        assertTrue(result);
        verify(tableSessionGrpcClient).verifyTableSession(sessionId);
        verify(request).setAttribute(eq("authenticatedTableSession"), any(TableSession.class));
    }

    @Test
    void preHandle_RealAnnotations_NoAnnotations_ShouldReturnTrue() throws Exception {
        // Given
        Method method = TestController.class.getMethod("noAnnotationsMethod");
        HandlerMethod realHandlerMethod = new HandlerMethod(new TestController(), method);

        // When
        boolean result = authInterceptor.preHandle(request, response, realHandlerMethod);

        // Then
        assertTrue(result);
        verifyNoInteractions(adminGrpcClient, tableSessionGrpcClient);
    }
}