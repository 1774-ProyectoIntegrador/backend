package proyecto.dh.common.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class TrailingSlashRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("/") && requestURI.length() > 1) {
            String newURI = requestURI.substring(0, requestURI.length() - 1);
            response.sendRedirect(newURI);
            return;
        }
        filterChain.doFilter(request, response);
    }
}