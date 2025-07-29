package org.eu.rainx0.raintool.core.starter.web.confg;

import java.net.Inet4Address;
import java.util.Optional;

import org.eu.rainx0.raintool.core.starter.util.SpringContextTools;
import org.eu.rainx0.raintool.core.starter.web.util.IpTools;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;

/**
 * xiaoyureed@gmail.com
 */
@Configuration
@OpenAPIDefinition
@Slf4j
public class OpenAPIConfig {
    /*
     *
     * To see how to use springdoc please check :
     * https://juejin.cn/post/7080328458206707720
     * https://blog.lanweihong.com/posts/1527/
     * <p>
     * https://blog.csdn.net/shijizhe1/article/details/130495081
     * https://www.baeldung.com/spring-rest-openapi-documentation
     *
     * @Api → @Tag
     * @ApiIgnore → @Parameter(hidden = true) or @Operation(hidden = true) or @Hidden
     * @ApiImplicitParam → @Parameter
     * @ApiImplicitParams → @Parameters
     * @ApiModel → @Schema
     * @ApiModelProperty(hidden = true) → @Schema(accessMode = READ_ONLY)
     * @ApiModelProperty → @Schema
     * @ApiOperation(value = "foo", notes = "bar") → @Operation(summary = "foo", description = "bar")
     * @ApiParam → @Parameter
     * @ApiResponse(code = 404, message = "foo") → @ApiResponse(responseCode = "404", description = "foo")
     **/

    private static final String security_schema_name_http_bearer = "Bearer Authentication";

    @Autowired
    private WebProps webProps;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String port;

    @Value("${spring.h2.console.enabled}")
    private boolean h2ConsoleEnabled;
    @Value("${spring.h2.console.path}")
    private String h2ConsolePath;


    /**
     * 用来在代码中自定义springdoc 配置
     */
    // @Bean
    public SpringDocConfigProperties springDocConfigProperties(SpringDocConfigProperties config) {
        config.setEnableJavadoc(true);
        config.setEnableSpringSecurity(true);
        config.setShowActuator(true);
        config.setModelAndViewAllowed(true); //#运行modelAndView展示（返回页面）

        SpringDocConfigProperties.ApiDocs apiDocs = new SpringDocConfigProperties.ApiDocs();
        apiDocs.setEnabled(true);
        apiDocs.setPath("/v3/api-docs");
        apiDocs.setVersion(SpringDocConfigProperties.ApiDocs.OpenApiVersion.OPENAPI_3_1);
        config.setApiDocs(apiDocs);


//        config.setGroupConfigs();


        return config;
    }

    //    @Bean
//    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties(SwaggerUiConfigProperties config) {
        config.setEnabled(true);
        config.setDisplayRequestDuration(true);
        config.setDisableSwaggerDefaultUrl(true);
        config.setPath("/swagger-ui.html");
        config.setOperationsSorter("method");// api排序方式 alpha 字母 method http方法
//        config.setGroupsOrder(AbstractSwaggerUiConfigProperties.Direction.ASC);


        SwaggerUiConfigProperties.Csrf csrf = new SwaggerUiConfigProperties.Csrf();
        csrf.setEnabled(true);
        config.setCsrf(csrf);


        return config;
    }

    @Bean
    public ApplicationRunner openAPIConsole() {
        return args -> {
            String contextPath = Optional.ofNullable(SpringContextTools.getProperty("server.servlet.context-path")).orElse("");
            String swaggerUiPath = Optional.ofNullable(SpringContextTools.getProperty("springdoc.swagger-ui.path")).orElse("/swagger-ui.html");
            String host = IpTools.getLocalIpAddress();
            String profile = SpringContextTools.getPrettyActiveProfiles();
            String remoteUrl = "http://" + host + ":" + port + contextPath + swaggerUiPath;
            String localUrl = "http://localhost:" + port + contextPath + swaggerUiPath;
            String h2console = h2ConsoleEnabled ? "http://" + host + ":" + port + h2ConsolePath : "";

            log.info("\n----------------------------------------------------------\n" +
                    "\t Application: '{}' is running! \n" +
                    "\t Environment: {} \n" +
                    "\t Spring Doc:  {}\n" +
                    "\t              {}\n" +
                    "\t H2 console:  {}\n" +
                    "----------------------------------------------------------",
                applicationName,
                profile,
                remoteUrl,
                localUrl,
                h2console
            );
        };
    }

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth"; // spingdoc 的token 设置界面的输入项显示名称

        return new OpenAPI()
            .info(info())
            .externalDocs(externalDocumentation())
            .addSecurityItem(new SecurityRequirement()
                .addList(securitySchemeName)
            )
            .components(new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                )
            )
            ;
    }

    /**
     * 文档基本信息
     */
    private Info info() {
        return new Info()
            .title(applicationName)
            .description(applicationName + " APIs")
            .version(webProps.getSwagger().getVersion())
            .contact(new Contact().name("rainx0").email("xiaoyureed@gmail.com").url("xiaoyureed.github.io"))
            .license(license());
    }

    /**
     * 外部说明页面
     */
    private ExternalDocumentation externalDocumentation() {
        return new ExternalDocumentation()
            .description("额外的说明页面")
            .url("https://baidu.com"); // 可以是纯外部链接, 可以是一个 path, host 就是当前应用(没有 servlet.context-path前缀)
    }


    private License license() {
        return new License()
            .name("MIT")
            .url("https://opensource.org/licenses/MIT");
    }
}