package ru.pasvitas.bots.reposter.model;

import java.util.List;

public record BotMessage(
        String text,
        List<String> images
) {
}
