package id.ac.ui.cs.advprog.ohioorder.resolver;

import id.ac.ui.cs.advprog.ohioorder.annotation.AuthenticatedAdmin;
import id.ac.ui.cs.advprog.ohioorder.annotation.AuthenticatedTableSession;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.bind.support.WebDataBinderFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserArgumentResolverTest {

    private AuthenticatedUserArgumentResolver resolver;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        resolver = new AuthenticatedUserArgumentResolver();
    }

    @Test
    void supportsParameter_WithAuthenticatedTableSessionAnnotation_ReturnsTrue() {
        // Arrange
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(false);
        when(methodParameter.hasParameterAnnotation(AuthenticatedTableSession.class)).thenReturn(true);

        // Act
        boolean result = resolver.supportsParameter(methodParameter);

        // Assert
        assertTrue(result);
        verify(methodParameter).hasParameterAnnotation(AuthenticatedAdmin.class);
        verify(methodParameter).hasParameterAnnotation(AuthenticatedTableSession.class);
    }

    @Test
    void supportsParameter_WithoutAnyAnnotation_ReturnsFalse() {
        // Arrange
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(false);
        when(methodParameter.hasParameterAnnotation(AuthenticatedTableSession.class)).thenReturn(false);

        // Act
        boolean result = resolver.supportsParameter(methodParameter);

        // Assert
        assertFalse(result);
        verify(methodParameter).hasParameterAnnotation(AuthenticatedAdmin.class);
        verify(methodParameter).hasParameterAnnotation(AuthenticatedTableSession.class);
    }

    @Test
    void resolveArgument_WithAuthenticatedAdminAnnotation_ReturnsAdminAttribute() {
        // Arrange
        Object expectedAdmin = new Object(); // This could be your actual Admin entity
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(true);
        when(httpServletRequest.getAttribute("authenticatedAdmin")).thenReturn(expectedAdmin);

        // Act
        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Assert
        assertEquals(expectedAdmin, result);
        verify(webRequest).getNativeRequest();
        verify(methodParameter).hasParameterAnnotation(AuthenticatedAdmin.class);
        verify(httpServletRequest).getAttribute("authenticatedAdmin");
    }

    @Test
    void resolveArgument_WithAuthenticatedTableSessionAnnotation_ReturnsTableSessionAttribute() {
        // Arrange
        Object expectedTableSession = new Object(); // This could be your actual TableSession entity
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(false);
        when(methodParameter.hasParameterAnnotation(AuthenticatedTableSession.class)).thenReturn(true);
        when(httpServletRequest.getAttribute("authenticatedTableSession")).thenReturn(expectedTableSession);

        // Act
        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Assert
        assertEquals(expectedTableSession, result);
        verify(webRequest).getNativeRequest();
        verify(methodParameter).hasParameterAnnotation(AuthenticatedAdmin.class);
        verify(methodParameter).hasParameterAnnotation(AuthenticatedTableSession.class);
        verify(httpServletRequest).getAttribute("authenticatedTableSession");
    }

    @Test
    void resolveArgument_WithNoMatchingAnnotation_ReturnsNull() {
        // Arrange
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(false);
        when(methodParameter.hasParameterAnnotation(AuthenticatedTableSession.class)).thenReturn(false);

        // Act
        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Assert
        assertNull(result);
        verify(webRequest).getNativeRequest();
        verify(methodParameter).hasParameterAnnotation(AuthenticatedAdmin.class);
        verify(methodParameter).hasParameterAnnotation(AuthenticatedTableSession.class);
        verify(httpServletRequest, never()).getAttribute(anyString());
    }

    @Test
    void resolveArgument_WithAuthenticatedAdminAnnotation_WhenAttributeIsNull_ReturnsNull() {
        // Arrange
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(true);
        when(httpServletRequest.getAttribute("authenticatedAdmin")).thenReturn(null);

        // Act
        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Assert
        assertNull(result);
        verify(httpServletRequest).getAttribute("authenticatedAdmin");
    }

    @Test
    void resolveArgument_WithAuthenticatedTableSessionAnnotation_WhenAttributeIsNull_ReturnsNull() {
        // Arrange
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(false);
        when(methodParameter.hasParameterAnnotation(AuthenticatedTableSession.class)).thenReturn(true);
        when(httpServletRequest.getAttribute("authenticatedTableSession")).thenReturn(null);

        // Act
        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Assert
        assertNull(result);
        verify(httpServletRequest).getAttribute("authenticatedTableSession");
    }

    @Test
    void resolveArgument_HttpServletRequestCast_WorksCorrectly() {
        // Arrange
        Object expectedAdmin = new Object();
        when(webRequest.getNativeRequest()).thenReturn(httpServletRequest);
        when(methodParameter.hasParameterAnnotation(AuthenticatedAdmin.class)).thenReturn(true);
        when(httpServletRequest.getAttribute("authenticatedAdmin")).thenReturn(expectedAdmin);

        // Act
        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Assert
        assertEquals(expectedAdmin, result);
        verify(webRequest).getNativeRequest();
    }

    // Integration-style test to verify the component annotation
    @Test
    void classAnnotation_HasComponentAnnotation() {
        // Assert
        assertTrue(AuthenticatedUserArgumentResolver.class.isAnnotationPresent(org.springframework.stereotype.Component.class));
    }

    // Test to verify the class implements the correct interface
    @Test
    void classImplementation_ImplementsHandlerMethodArgumentResolver() {
        // Assert
        assertTrue(org.springframework.web.method.support.HandlerMethodArgumentResolver.class
                .isAssignableFrom(AuthenticatedUserArgumentResolver.class));
    }
}