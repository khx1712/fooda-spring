package ohlim.fooda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig{
    private String version;
    private String title;

    /**
     * swagger API 문서 생성
     * @return
     */
    @Bean
    public Docket api(){
        version = "V1.0.0";
        title = "Fooda API " + version;

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(this.apiInfo(title, version));
    }

    /**
     * swagger 정보
     * @param title 이름
     * @param version 버전
     * @return
     */
    private ApiInfo apiInfo(String title, String version){
        return new ApiInfoBuilder()
                .title(title)
                .description("Swagger를 통해 생성한 API Docs")
                .version(version)
                .build();
    }

}
