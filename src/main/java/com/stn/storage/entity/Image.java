package com.stn.storage.entity;

import com.stn.storage.helper.GeneralHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "images")
@Data
@NoArgsConstructor
public class Image {
    @org.springframework.data.annotation.Id
    private String id;

    private String path;
    private String token;
    private String filename;
    private String ext;
    private Boolean isDeleted = false;
    private LocalDateTime created;
    private Boolean isCloudStorage = false;

    public Image(String path, String filename, String ext) {
        this.path = path;
        this.filename = filename;
        this.ext = ext;
        this.token = GeneralHelper.generateToken();
        this.created = LocalDateTime.now();
    }

    public Image(String filename, String ext, String token, Boolean isCloudStorage) {
        this.filename = filename;
        this.ext = ext;
        this.token = token;
        this.created = LocalDateTime.now();
        this.isCloudStorage = isCloudStorage;
    }
}
