package com.example.CourseWork.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        exposeDirectory("category-images",registry);
        exposeDirectory("product-images",registry);
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry){
        Path categoryImagesDir = Paths.get(dirName);
        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();

        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");

        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + categoryImagesPath + "/");
    }
}
