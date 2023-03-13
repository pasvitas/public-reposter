package ru.pasvitas.bots.reposter.config;

import club.minnced.discord.webhook.WebhookClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordBotConfig {

    @Value("${discord.webhookUrl}")
    private String url;

    @Bean
    public WebhookClient webhookClient() {
        return WebhookClient.withUrl(url);
    }

}
