package com.bezro.shopRESTfulAPI.configs;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Optional;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme().name(securitySchemeName).type(SecurityScheme.Type.HTTP).scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info().title("Shop REST API").description("APIs for a shop").version("1.0")
                        .license(new License().name("Dev Team").url("https://github.com/ElenaBezro")))
                .externalDocs(new ExternalDocumentation().description("App Documentation").url("https://github.com/ElenaBezro/E-CommerceShopRESTfulAPI/tree/dev"));
    }

    public static final String SHOP_HEADER = "shop-header";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("shop-api").pathsToMatch("/api/**").build();
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {

        return (Operation operation, HandlerMethod handlerMethod) -> {
            Optional<List<Parameter>> isParameterPresent = Optional.ofNullable(operation.getParameters());
            Boolean isShopHeaderPresent = Boolean.FALSE;
            if (isParameterPresent.isPresent()) {
                isShopHeaderPresent = isParameterPresent.get().stream()
                        .anyMatch(param -> param.getName().equalsIgnoreCase(SHOP_HEADER));
            }
            if (Boolean.FALSE.equals(isShopHeaderPresent)) {
                Parameter remoteUser = new Parameter().in(ParameterIn.HEADER.toString()).schema(new StringSchema())
                        .name(SHOP_HEADER).description("Shop Header").required(true);
                operation.addParametersItem(remoteUser);
            }
            return operation;
        };
    }

}
