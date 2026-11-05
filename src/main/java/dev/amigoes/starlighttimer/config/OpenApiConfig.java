package dev.amigoes.starlighttimer.config;

import dev.amigoes.starlighttimer.adapters.in.web.HeaderUsuarioAtualAdapter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI starlightTimerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("StarlightTimer API")
                .description("API REST do StarlightTimer")
                .version("v1"));
    }

    @Bean
    public OperationCustomizer addGlobalUserHeader() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(
                    new Parameter()
                            .in("header")
                            .name(HeaderUsuarioAtualAdapter.HEADER_USUARIO_ID)
                            .description("ID do Utilizador Atual (Cabeçalho Provisório M0)")
                            .required(false)
                            .schema(new StringSchema().format("uuid"))
            );
            return operation;
        };
    }
}