package moe.lyrebird.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import moe.lyrebird.model.twitter.util.MediaEntityType;
import twitter4j.MediaEntity;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

import java.util.concurrent.CompletableFuture;

@Component
public class MediaEmbeddingService {

    private final CachedDataService cachedDataService;

    @Autowired
    public MediaEmbeddingService(final CachedDataService cachedDataService) {
        this.cachedDataService = cachedDataService;
    }

    public boolean isSupported(final MediaEntity entity) {
        final MediaEntityType entityType = MediaEntityType.fromTwitterType(entity.getType());
        return entityType != MediaEntityType.UNMANAGED;
    }

    public Node embed(MediaEntity entity) {
        switch (MediaEntityType.fromTwitterType(entity.getType())) {
            case PHOTO:
                return embedImage(entity.getMediaURLHttps());
            case UNMANAGED:
            default:
                throw new IllegalArgumentException("Twitter type "+entity.getType()+" is not supported!");
        }
    }

    private ImageView embedImage(final String imageUrl) {
        final ImageView container = new ImageView();
        container.setFitWidth(64);
        container.setFitHeight(64);
        CompletableFuture.supplyAsync(() -> cachedDataService.cachedImage(imageUrl))
                         .whenCompleteAsync((img, err) -> {
                             if (err == null && img != null) {
                                 container.setImage(img);
                             }
                         }, Platform::runLater);
        return container;
    }

}
