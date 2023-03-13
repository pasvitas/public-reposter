package ru.pasvitas.bots.reposter.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pasvitas.bots.reposter.model.BotMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class TelegramServiceImpl extends TelegramLongPollingBot {

    private final Map<String, List<String>> futures = new ConcurrentHashMap<>();

    private final RepostService repostService;

    @Value("${telegram.username}")
    private String botUsername;

    @Value("${telegram.token}")
    private String botToken;

    @Value("${telegram.channelId}")
    private Long channelId;


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getChannelPost().getChat().getId().equals(channelId)) {
            if (update.getChannelPost() != null && update.getChannelPost().isChannelMessage()
                    && (update.getChannelPost().hasText() || update.getChannelPost().getCaption() != null || update.getChannelPost().hasPhoto())) {
                if (update.getChannelPost().hasPhoto()) {
                    if (update.getChannelPost().getMediaGroupId() != null) {
                        List<String> photos;
                        if (!futures.containsKey(update.getChannelPost().getMediaGroupId())) {
                            List<String> data = new ArrayList<>();
                            AtomicReference<String> text = new AtomicReference<>();
                            text.set(update.getChannelPost().getCaption() != null ? update.getChannelPost().getCaption()
                                    : update.getChannelPost().hasText() ? update.getChannelPost().getText() : "");
                            futures.put(update.getChannelPost().getMediaGroupId(), data);
                            CompletableFuture.runAsync(() -> {
                                try {
                                    Thread.sleep(60000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                log.info("Process");
                                String newText =
                                        update.getChannelPost().getCaption() != null ? update.getChannelPost().getCaption()
                                                : update.getChannelPost().hasText() ? update.getChannelPost().getText() : "";
                                if (!newText.equals("")) {
                                    text.set(newText);
                                }
                                repostService.repostMessage(new BotMessage(text.get() == null ? "" : text.get(), futures.get(update.getChannelPost().getMediaGroupId())));
                                futures.remove(update.getChannelPost().getMediaGroupId());
                            });
                        }
                        photos = futures.get(update.getChannelPost().getMediaGroupId());
                        photos.add(getPhotoUrl(update));
                    } else {
                        List<String> photos = new ArrayList<>();
                        photos.add(getPhotoUrl(update));
                        repostService.repostMessage(new BotMessage(update.getChannelPost().getCaption() != null ? update.getChannelPost().getCaption() : "", photos));
                    }
                } else {
                    repostService.repostMessage(new BotMessage(update.getChannelPost().hasText() ? update.getChannelPost().getText() : "", new ArrayList<>()));
                }
            }
        }
        else {
            log.info("Id not found");
        }
    }

    private String getPhotoUrl(Update update) {
        String attach = "";
        int fileSize = 0;
        for (PhotoSize photo : update.getChannelPost().getPhoto()) {
            if (attach.isEmpty() || fileSize < photo.getFileSize()) {
                attach = photo.getFileId();
                fileSize = photo.getFileSize();
            }
        }
        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(attach);
        try {
            attach = execute(uploadedFile).getFilePath();
        } catch (TelegramApiException e) {
            log.error("Error on getting file: {}", e.getMessage(), e);
        }
        return "https://api.telegram.org/file/bot" + botToken + "/" + attach;
    }
}
