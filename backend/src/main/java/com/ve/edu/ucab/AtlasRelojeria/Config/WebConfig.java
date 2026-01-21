                                            package com.ve.edu.ucab.AtlasRelojeria.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourcePath = Paths.get("src/main/resources").toAbsolutePath().toString();
        
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + resourcePath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
} 