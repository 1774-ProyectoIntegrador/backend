package proyecto.dh.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RentStudio Backend")
                        .version("v1.0")
                        .description("Backend API for RentStudio App"))
                .servers(List.of(
                        new Server().url("https://apidh.jackmoon.dev").description("Servidor de Producción"),
                        new Server().url("http://localhost:6060").description("Local")
                ));
    }

    @Bean
    public OpenApiCustomizer customerGlobalHeaderOpenApiCustomizer() {
        return openApi -> openApi.getInfo().setTitle("RentStudio Backend");
    }
}
