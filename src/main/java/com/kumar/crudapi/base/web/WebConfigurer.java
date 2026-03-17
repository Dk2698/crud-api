package com.kumar.crudapi.base.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Slf4j
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory>, WebMvcConfigurer {

    @Override
    public void customize(WebServerFactory factory) {

    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FilterResolver());
        final SnakeCaseToCamelCaseSortResolver sortResolver = new SnakeCaseToCamelCaseSortResolver();
        sortResolver.setSortParameter("_sort");
        resolvers.add(sortResolver);
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver(sortResolver);
        pageableResolver.setPrefix("_");
        resolvers.add(pageableResolver);

    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableHandlerCustomizer() {
        return new PageableHandlerMethodArgumentResolverCustomizer() {
            @Override
            public void customize(PageableHandlerMethodArgumentResolver pageableResolver) {
                pageableResolver.setPageParameterName("_page");
                pageableResolver.setSizeParameterName("_size");
            }
        };
    }

    @Bean
    public SortHandlerMethodArgumentResolverCustomizer sortHandlerCustomizer() {
        return new SortHandlerMethodArgumentResolverCustomizer() {
            @Override
            public void customize(SortHandlerMethodArgumentResolver sortResolver) {
                sortResolver.setSortParameter("_sort");
            }
        };
    }
}
