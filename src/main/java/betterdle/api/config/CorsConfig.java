package betterdle.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Appliquer à toutes les routes
                .allowedOrigins("http://localhost:5173") // Autoriser le frontend Vite
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Méthodes autorisées
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
