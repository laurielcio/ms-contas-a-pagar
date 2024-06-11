package br.com.laurielcio.contabil.config;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigParameters;

@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Contas a pagar")
                .displayName("Contas a Pagar")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return new SwaggerUiConfigParameters(swaggerUiConfigProperties);
    }

}
