package ru.pasvitas.bots.reposter.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.responses.GetUploadServerResponse;
import com.vk.api.sdk.objects.photos.responses.GetWallUploadServerResponse;
import com.vk.api.sdk.objects.photos.responses.SaveWallPhotoResponse;
import com.vk.api.sdk.objects.photos.responses.WallUploadResponse;
import com.vk.api.sdk.objects.wall.responses.PostResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pasvitas.bots.reposter.model.BotMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class VkServiceImpl implements VkService {

    private final UserActor userActor;
    private final VkApiClient vkApiClient;

    @Value("${vk.groupId}")
    private Integer groupId;

    @Override
    public void postMessage(BotMessage message) throws ClientException, ApiException, IOException {
        List<String> attaches = new ArrayList<>();
        GetWallUploadServerResponse getUploadServerResponse = vkApiClient
                .photos()
                .getWallUploadServer(userActor)
                .groupId(-groupId)
                .execute();

        for (String image : message.images()) {
            URL url = new URL(image);
            BufferedImage img = ImageIO.read(url);
            File file = new File("downloaded.jpg");
            ImageIO.write(img, "jpg", file);
            WallUploadResponse wallUploadResponse = vkApiClient
                    .upload()
                    .photoWall(getUploadServerResponse.getUploadUrl().toString(), file)
                    .execute();
            List<SaveWallPhotoResponse> photos = vkApiClient.photos().saveWallPhoto(
                    userActor,
                    wallUploadResponse.getPhoto()
            ).groupId(-groupId).server(wallUploadResponse.getServer()).hash(wallUploadResponse.getHash()).execute();
            String imageUrl = "photo" + photos.get(0).getOwnerId() + "_" + photos.get(0).getId();
            attaches.add(imageUrl);
            file.delete();
        }


        PostResponse response = vkApiClient
                .wall()
                .post(userActor)
                .ownerId(groupId)
                .fromGroup(true)
                .message(message.text())
                .attachments(attaches.toArray(new String [0]))
                .execute();
        log.info("VK done");
    }
}
