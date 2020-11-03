package com.stn.storage.controller;

import com.stn.storage.entity.Image;
import com.stn.storage.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class ImageController {
    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/get-image")
    public Image getImage(@RequestBody Map<String, String> data) {
        String token = data.get("token");
        return imageService.getImage(token);
    }

    @PostMapping("/upload-image")
    public ResponseEntity upload(@RequestParam MultipartFile[] files) {
        return imageService.upload(files);
    }
}
