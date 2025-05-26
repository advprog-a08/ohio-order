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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import table_session.TableSessionOuterClass;

import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AdminGrpcClient adminGrpcClient;
    private final TableSessionGrpcClient tableSessionGrpcClient;

    public AuthInterceptor(AdminGrpcClient adminGrpcClient, TableSessionGrpcClient tableSessionGrpcClient) {
        this.adminGrpcClient = adminGrpcClient;
        this.tableSessionGrpcClient = tableSessionGrpcClient;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true; // skip non-controller requests
        }

        boolean requiresAdmin = method.hasMethodAnnotation(RequireAdmin.class);
        boolean requiresCustomer = method.hasMethodAnnotation(RequireTableSession.class);

        if (!requiresAdmin && !requiresCustomer) {
            return true;
        }

        boolean adminAuthenticated = false;
        boolean tableSessionAuthenticated = false;

        if (requiresAdmin) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or invalid Authorization header");
                return false;
            }

            try {
                String token = authHeader.substring(7); // Remove "Bearer "
                AdminOuterClass.AdminResponse result = adminGrpcClient.verifyAdmin(token);

                Admin admin = new Admin(result.getAdmin().getEmail());
                request.setAttribute("authenticatedAdmin", admin);
                adminAuthenticated = true;
            } catch (RuntimeException e) {
                // Invalid admin token, continue to try table session
            }
        }

        if (requiresCustomer) {
            String sessionId = request.getHeader("X-Session-Id");
            if (sessionId != null && !sessionId.isEmpty()) {
                try {
                    TableSessionOuterClass.TableSessionResponse result = tableSessionGrpcClient.verifyTableSession(sessionId);

                    TableSession tableSession = new TableSession(
                            result.getTableSession().getId(),
                            result.getTableSession().getTableId(),
                            result.getTableSession().getOrderId(),
                            result.getTableSession().hasCheckoutId()
                                    ? Optional.of(result.getTableSession().getCheckoutId())
                                    : Optional.empty(),
                            result.getTableSession().getIsActive()
                    );

                    request.setAttribute("authenticatedTableSession", tableSession);
                    tableSessionAuthenticated = true;
                } catch (RuntimeException e) {
                    // Invalid table session, continue
                }
            }
        }

        if (adminAuthenticated || tableSessionAuthenticated) {
            return true;
        }

        response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized: valid admin or table session required");
        return false;
    }
}
