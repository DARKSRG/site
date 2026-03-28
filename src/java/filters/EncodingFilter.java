package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Единая кодировка UTF-8 для запросов и ответов (формы POST, JSP, HTML).
 */
@WebFilter(urlPatterns = "/*")
public class EncodingFilter implements Filter {

    private static final String ENCODING = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) { }

    @Override
    public void destroy() { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(ENCODING);
        response.setCharacterEncoding(ENCODING);
        chain.doFilter(request, response);
    }
}
