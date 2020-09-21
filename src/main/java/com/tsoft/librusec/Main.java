package com.tsoft.librusec;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.config.ConfigService;
import com.tsoft.librusec.service.library.LibraryReferenceGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Main {

    /**
     * I've tried do not use Spring Context here, so there are no any Beans (except @RestController).
     * Cons:
     *   - fast application start up
     * Pros:
     *   - @Cacheable won't work (no bean - no proxy)
     */

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || args[0].equalsIgnoreCase("-help")) {
            System.out.println("Usage: java -jar librusec.jar <Path to the library's folder (where zips are placed)>");
            System.exit(0);
        }

        LibraryReferenceGenerator generator = new LibraryReferenceGenerator();
        Config config = generator.prepareConfig(args[0]);
        if (config != null) {
            generator.generate(config);
        }

        SpringApplication.run(Main.class, args);
    }

    /** Serve the generated HTML-files as static content (located outside the application) */
    @Configuration
    @EnableWebMvc
    public static class GeneratedHtmlConfig implements WebMvcConfigurer {

        private final ConfigService configService = new ConfigService();

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            Config config = configService.getConfig();

            if (config != null) {
                registry
                    .addResourceHandler("/*.{html,css,ico,js}")
                    .addResourceLocations("file:" + config.getHtmlFolder() + "/");
            }
        }
    }
}
