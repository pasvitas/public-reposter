package ru.pasvitas.bots.reposter.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pasvitas.bots.reposter.model.BotMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepostServiceImpl implements RepostService {

    private final VkService vkService;
    private final DiscordService discordService;

    @Override
    public void repostMessage(BotMessage botMessage) {
        log.info("Sending post {}", botMessage.text());
        try {
            vkService.postMessage(botMessage);
        } catch (ClientException e) {
            log.error("Error VK Client Message: {}", e.getMessage(), e);
        } catch (ApiException e) {
            log.error("Error VKAPi Message: {}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("Error IO message: {}", e.getMessage(), e);
        }
        log.info("VK Done");
        discordService.postMessage(botMessage);
        log.info("Discord done");
    }
}
