package com.stn.storage.service;

import com.stn.storage.constant.Constant;
import com.stn.storage.entity.Image;
import com.stn.storage.helper.FileHelper;
import com.stn.storage.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
public class BackgroundTaskService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${storage.default-image.classpath}")
    private Resource resource;

    @Value("${storage.delete-image-period}")
    private Integer deleteImagePeriod;

    @Scheduled(fixedDelayString = "${storage.background-task.auto-remove-image.fixed-delay}", initialDelayString = "${storage.background-task.auto-remove-image.init-delay}")
    public void autoRemoveImages() {
        Collection<Image> imageCollection = imageRepository.findTop100ByIsDeletedFalse();
        if (imageCollection.isEmpty()) {
            log.info("No found images to be removed.");
            return;
        }
        try {
            URL url = resource.getURL();
            String filePath = url.getPath();
            imageCollection.forEach(image -> updateDataRemovedImage(image, filePath, resource.getFilename()));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private Boolean updateDataRemovedImage(Image image, String defaultImagePath, String filename) {
        if (checkIfImageIsValidToBeRemoved(image)) {
            try {
                removeImageFile(image.getPath());
                image.setIsDeleted(true);
                image.setPath(defaultImagePath);
                image.setFilename(FilenameUtils.removeExtension(filename));
                image.setExt(FileHelper.getExtensionFile(filename));
                imageRepository.save(image);
                log.info(String.format("Success updating data removed image [%s, %s]", image.getId(), image.getToken()));
                return true;
            } catch (Exception exception) {
                log.error(String.format("an error has occurred while removing/updating data image : %s", exception.getMessage()));
                return false;
            }
        }
        return false;
    }

    private Boolean checkIfImageIsValidToBeRemoved(Image image) {
        LocalDateTime created = image.getCreated();
        LocalDateTime maxLimitDatetime = created.plusDays(deleteImagePeriod);
        return LocalDateTime.now().isAfter(maxLimitDatetime);
    }

    private void removeImageFile(String filepath) {
        try {
            String fullPath = String.format("%s%s", Constant.PARENT_DIRECTORY, filepath);
            Files.deleteIfExists(Paths.get(fullPath));
            log.info(String.format("Success removed image [%s]", filepath));
        } catch (Exception exception) {
            log.error(String.format("an error has occurred while removing image file : %s", exception.getMessage()));
        }
    }
}
