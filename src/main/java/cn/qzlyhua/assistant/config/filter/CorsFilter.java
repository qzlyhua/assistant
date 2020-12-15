package cn.qzlyhua.assistant.config.filter;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 跨域配置
 *
 * @author yanghua
 */
@Slf4j
@Data
@WebFilter(filterName = "corsFilter", urlPatterns = {"/*"})
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Value("${app.origin.address}")
    private String originAddress;

    private List<String> mccDomain;

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String originHeader = httpServletRequest.getHeader("Origin") != null ?
                httpServletRequest.getHeader("Origin").replace(" ", "")
                : httpServletRequest.getHeader("Origin");
        String header = StrUtil.isNotBlank(originAddress) ? originAddress : originHeader;
        log.info("Access-Control-Allow-Origin:{}", header);
        httpServletResponse.setHeader("Access-Control-Allow-Origin", header);
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "authorization,Accept, Origin, XRequestedWith, Content-Type,Content-type, LastModified");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,HEAD,PUT,DELETE,OPTIONS,CONNECT");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, httpServletResponse);
    }
}
