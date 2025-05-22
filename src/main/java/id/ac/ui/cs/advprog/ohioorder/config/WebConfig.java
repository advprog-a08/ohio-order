package id.ac.ui.cs.advprog.ohioorder.config;

import id.ac.ui.cs.advprog.ohioorder.interceptor.AuthInterceptor;
import id.ac.ui.cs.advprog.ohioorder.resolver.AuthenticatedUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticatedUserArgumentResolver argumentResolver;
    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthenticatedUserArgumentResolver argumentResolver, AuthInterceptor authInterceptor) {
        this.argumentResolver = argumentResolver;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(argumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor);
    }
}
