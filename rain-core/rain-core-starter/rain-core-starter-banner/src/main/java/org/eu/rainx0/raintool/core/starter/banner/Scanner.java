package org.eu.rainx0.raintool.core.starter.banner;

import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
@ComponentScan // This annotation here is used to scan the other beans
public class Scanner {
    {
        log.info(";;Banner starter loaded.");
    }


    // @Bean
    // public Banner banner() {
    //     return new Banner() {
    //         @Override
    //         public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
    //             String banner = """
    //               ____              _
    //              | __ )  __ _ _ __ | |__
    //              |  _ \\ / _` | '_ \\| '_ \\
    //              | |_) | (_| | | | | | | |
    //              |____/ \\__,_|_| |_|_| |_|
    //             """;
    //
    //             String artifactId = resolveArtifactId();
    //
    //             out.println(banner);
    //             out.println(" ArtifactId: " + artifactId);
    //         }
    //
    //         private String resolveArtifactId() {
    //             try {
    //                 Properties props = new Properties();
    //                 props.load(getClass().getClassLoader().getResourceAsStream("META-INF/maven/your.group.id/your-artifact-id/pom.properties"));
    //                 return props.getProperty("artifactId", "unknown");
    //             } catch (Exception e) {
    //                 return "unknown";
    //             }
    //         }
    //     };
    // }
}
