package beldon.maven.config;

import beldon.maven.config.properties.MavenProperties;
import beldon.maven.web.interceptors.MavenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Beldon
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MavenProperties mavenProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String contextPath = mavenProperties.getContextPath();
        registry.addInterceptor(mavenInterceptor()).addPathPatterns(contextPath + "/**");
    }

    @Bean
    public MavenInterceptor mavenInterceptor() {
        return new MavenInterceptor();
    }
}
