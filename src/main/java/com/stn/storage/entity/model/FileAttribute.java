package com.stn.storage.entity.model;

import com.stn.storage.constant.Constant;
import com.stn.storage.entity.Image;
import com.stn.storage.helper.DatetimeHelper;
import com.stn.storage.helper.FileHelper;
import com.stn.storage.repository.ImageRepository;
import lombok.Data;

import java.net.URL;
import java.util.Optional;

@Data
public class FileAttribute {
    private ImageRepository imageRepository;
    private String storagePath;
    private String name;
    private String ext;

    public FileAttribute(ImageRepository imageRepository, String storagePath, String filename) {
        this.imageRepository = imageRepository;
        this.storagePath = storagePath;
        this.name = com.google.common.io.Files.getNameWithoutExtension(filename).trim();
        this.ext = com.google.common.io.Files.getFileExtension(filename);
        appendFilenameIfExist();
    }

    public FileAttribute(URL url) {
        name = com.google.common.io.Files.getNameWithoutExtension(url.getFile());
        ext = FileHelper.removeRequestParamFromExtension(com.google.common.io.Files.getFileExtension(url.getFile()));
        appendFilenameIfExist();
    }

    public String getFullName() {
        return name + "." + ext;
    }

    public String getFileTargetPath() {
        return Constant.DIRECTORY_SEPARATOR + storagePath + Constant.DIRECTORY_SEPARATOR + name + "." + ext;
    }

    public String getFileAbsolutePath() {
        return Constant.PARENT_DIRECTORY + Constant.DIRECTORY_SEPARATOR + this.getFileTargetPath();
    }

    private void appendFilenameIfExist() {
        Optional<Image> temp = imageRepository.findByFilenameAndExt(name, ext);

        if (!temp.equals(Optional.empty())) {
            name += DatetimeHelper.getCurrentTimeStamp();
        }
    }

    public Image asImage() {
        return new Image(getFileTargetPath(), getName(), getExt());
    }
}
