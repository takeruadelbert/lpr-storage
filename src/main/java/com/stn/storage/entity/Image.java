package com.stn.storage.entity;

import com.stn.storage.helper.GeneralHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
@Data
@NoArgsConstructor
public class Image {
    @org.springframework.data.annotation.Id
    private String Id;

    private String path;
    private String token;
    private String filename;
    private String ext;

    public Image(String path, String filename, String ext) {
        this.path = path;
        this.filename = filename;
        this.ext = ext;
        this.token = GeneralHelper.generateToken();
    }
}
