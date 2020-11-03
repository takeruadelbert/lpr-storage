package com.stn.storage.controller;

import com.stn.storage.entity.Image;
import com.stn.storage.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storage")
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

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam MultipartFile[] files) {
        return imageService.upload(files);
    }

    @PostMapping("/upload-encoded")
    public ResponseEntity uploadEncoded(@RequestBody List<Map<String, String>> payload) {
        return imageService.uploadEncoded(payload);
    }
}
