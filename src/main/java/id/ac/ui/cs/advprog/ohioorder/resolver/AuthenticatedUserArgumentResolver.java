package id.ac.ui.cs.advprog.ohioorder.resolver;

import id.ac.ui.cs.advprog.ohioorder.annotation.AuthenticatedAdmin;
import id.ac.ui.cs.advprog.ohioorder.annotation.AuthenticatedTableSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedAdmin.class)
                || parameter.hasParameterAnnotation(AuthenticatedTableSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        if (parameter.hasParameterAnnotation(AuthenticatedAdmin.class)) {
            return request.getAttribute("authenticatedAdmin");
        }

        if (parameter.hasParameterAnnotation(AuthenticatedTableSession.class)) {
            return request.getAttribute("authenticatedTableSession");
        }

        return null;
    }
}
