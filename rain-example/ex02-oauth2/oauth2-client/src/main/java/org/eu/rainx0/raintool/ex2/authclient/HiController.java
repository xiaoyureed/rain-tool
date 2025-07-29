package org.eu.rainx0.raintool.ex2.authclient;

import java.security.Principal;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author xiaoyu
 * @time 2025/7/15 10:54
 */
@RestController
public class HiController {
    /*
    *
    * 访问跳转顺序:
    * - client-app/
    * - client-app/oauth2/authorization/hi_registration
    * - auth-server/oauth2/authorize? \
    *       response_type=code&client_id=hi&scope=read \
    *       &state=ciA3uxahlmvu-3HcQrbvcU6Eiz7CttBBdhJzK8VwMN8= \
    *       &redirect_uri=http://localhost:9091/login/oauth2/code/hi_registration
    * - auth-server/login
    *       输入auth server 上的账号密码
    * - auth-server/oauth2/authorize? \
    *       ...
    * - client-app/login/oauth2/code/hi_registration? \
    *       code=4hCAEs5Xwb1382KDI-aIG99lVCtWD60UgFQ43kY8SJUKsmBCbI5gzwmNufruwarcV6BpvQUJ8cv68QjY2lcUrnjYMmx2gXVYS_qw4IjqBO67bTLvNX-V4qkAvp4C__OS \
    *       &state=ciA3uxahlmvu-3HcQrbvcU6Eiz7CttBBdhJzK8VwMN8=
    *
    * */
    @GetMapping("/")
    public String user(Principal principal) {

        String s = "principal in client app: " + principal.getName();
        System.out.println(s);
        return s;

    }

    @GetMapping("/hi")
    public String callResource(@RegisteredOAuth2AuthorizedClient("hi_registration") OAuth2AuthorizedClient client) {

        String token = client.getAccessToken().getTokenValue();
        System.out.println("------------------------");
        System.out.println(client);
        System.out.println(token);

        WebClient webClient = WebClient.create();
        String result = webClient.get()
            .uri("http://localhost:9092/hi")
            .headers(headers -> headers.setBearerAuth(token))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return result;
    }
}
