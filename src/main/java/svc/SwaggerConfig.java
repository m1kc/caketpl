package svc;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private ApiInfo apiInfo() {
        return new ApiInfo(
            "caketpl",
            "A simple template renderer.",
            "v1",
            null,
            new Contact("Max Musatov", "https://github.com/m1kc", "m1kc@yandex.ru"),
            null,
            null,
            Collections.emptyList()
        );
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            //.apis(RequestHandlerSelectors.any())
            .apis(RequestHandlerSelectors.basePackage("svc"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
    }
}