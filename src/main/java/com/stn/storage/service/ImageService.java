package com.stn.storage.service;

import com.stn.storage.constant.Constant;
import com.stn.storage.entity.Image;
import com.stn.storage.entity.model.FileAttribute;
import com.stn.storage.exception.BadRequestException;
import com.stn.storage.exception.NotFoundException;
import com.stn.storage.helper.FileHelper;
import com.stn.storage.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.util.*;

@Service
public class ImageService {
    private ImageRepository imageRepository;

    @Value("${storage.name}")
    private String storagePath;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void createDefaultDir() {
        FileHelper.autoCreateDir(Constant.PARENT_DIRECTORY + Constant.DIRECTORY_SEPARATOR + storagePath);
    }

    public ResponseEntity upload(MultipartFile[] files) {
        Map<String, Object> result = new HashMap<>();
        List<Image> data = new ArrayList<>();
        try {
            if (files.length != 0) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        FileAttribute fileAttribute = new FileAttribute(imageRepository, storagePath, file.getOriginalFilename());

                        FileOutputStream fileOutputStream = new FileOutputStream(fileAttribute.getFileAbsolutePath());
                        fileOutputStream.write(file.getBytes());
                        fileOutputStream.close();

                        data.add(fileAttribute.asImage());
                    }
                }
                imageRepository.saveAll(data);
                result.put("status", HttpStatus.OK.value());
                result.put("message", "File(s) has been uploaded successfully.");
                result.put("data", data);
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                result.put("status", HttpStatus.UNPROCESSABLE_ENTITY);
                result.put("message", "No File Uploaded.");
                return new ResponseEntity<>(result, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
            result.put("message", ex.getMessage());
            return new ResponseEntity<>(result, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public Image getImage(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("Invalid token.");
        }
        Optional<Image> optionalImage = imageRepository.findFirstByToken(token);
        if (!optionalImage.isPresent()) {
            throw new NotFoundException(String.format("Image with token %s does not exists.", token));
        }
        return optionalImage.get();
    }
}
