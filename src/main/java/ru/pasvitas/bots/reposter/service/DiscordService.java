package ru.pasvitas.bots.reposter.service;

import ru.pasvitas.bots.reposter.model.BotMessage;

public interface DiscordService {
    void postMessage(BotMessage botMessage);
}
