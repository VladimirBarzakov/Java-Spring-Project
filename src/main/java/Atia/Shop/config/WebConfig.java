/*
 * Atia & Tiger technology 2019.
 */
package Atia.Shop.config;

import Atia.Shop.web.interceptors.HideBestBidInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author Vladimir Barzakov <Atia & Tiger technology>
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HideBestBidInterceptor()).addPathPatterns("/buyer/auctions/details**").excludePathPatterns("/admin/auctions/details**");
    }
    
}
