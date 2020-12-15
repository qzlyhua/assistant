package cn.qzlyhua.assistant.controller.web;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author Yang Hua
 */
@Component
public class ErrorConfigurar implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
        ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/403.html");
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");

        registry.addErrorPages(error401Page, error403Page, error404Page, error500Page);
    }
}
