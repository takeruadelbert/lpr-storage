package com.stn.storage.controller;

import com.stn.storage.service.BackgroundTaskService;
import com.stn.storage.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storage")
public class ImageController {
    private ImageService imageService;
    private BackgroundTaskService backgroundTaskService;

    public ImageController(ImageService imageService, BackgroundTaskService backgroundTaskService) {
        this.imageService = imageService;
        this.backgroundTaskService = backgroundTaskService;
    }

    @PostMapping("/get-image")
    public ResponseEntity getImage(@RequestBody Map<String, String> data) {
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

    @PostMapping("/upload-url")
    public ResponseEntity uploadViaURL(@RequestBody Map<String, List<String>> payload) {
        return imageService.uploadViaURL(payload.get("url"));
    }

    @GetMapping("/{token}")
    public ResponseEntity getFile(@PathVariable String token, @RequestParam(value = "dl", required = false) Integer downloadFlag) {
        boolean is_dl = false;
        if (downloadFlag != null) {
            is_dl = downloadFlag == 1;
        }
        return imageService.getFile(token, is_dl);
    }

    @PostMapping("/test")
    public void test() {
        backgroundTaskService.autoRemoveImages();
    }
}
