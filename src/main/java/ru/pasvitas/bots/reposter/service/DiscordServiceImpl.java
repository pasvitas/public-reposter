package ru.pasvitas.bots.reposter.service;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import java.io.IOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pasvitas.bots.reposter.model.BotMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiscordServiceImpl implements DiscordService {

    private final WebhookClient webhookClient;

    @Override
    public void postMessage(BotMessage botMessage) {
        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        builder.setContent(botMessage.text());
        for (int i = 0; i < botMessage.images().size(); i++) {
            try {
                String[] split = botMessage.images().get(i).split("/");
                String fileName = split[split.length-1];
                builder.addFile(fileName, new URL(botMessage.images().get(i)).openStream());
            } catch (IOException e) {
                log.error("Exception on adding file {}: {}", botMessage.images().get(i), e.getMessage(), e);
            }
        }
        webhookClient.send(builder.build());
    }
}
