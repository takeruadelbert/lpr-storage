package com.stn.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @org.springframework.data.annotation.Id
    private String Id;

    private String path;
    private String token;
    private String filename;
    private String ext;
}
