package ru.pasvitas.bots.reposter.config;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VkBotConfig {

    @Value("${vk.userId}")
    private Integer userId;

    @Value("${vk.token}")
    private String token;

    @Bean
    public UserActor userActor() {
        return new UserActor(userId, token);
    }

    @Bean
    public VkApiClient client() {
        TransportClient transportClient = new HttpTransportClient();
        return new VkApiClient(transportClient);
    }
}
