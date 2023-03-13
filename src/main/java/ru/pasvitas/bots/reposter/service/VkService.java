package ru.pasvitas.bots.reposter.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import java.io.IOException;
import java.net.MalformedURLException;
import ru.pasvitas.bots.reposter.model.BotMessage;

public interface VkService {
    void postMessage(BotMessage message) throws ClientException, ApiException, IOException;
}
