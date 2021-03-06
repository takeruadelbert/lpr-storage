package com.stn.storage.service;

import com.ibm.cloud.objectstorage.services.s3.model.PutObjectResult;
import com.stn.storage.constant.Constant;
import com.stn.storage.entity.Image;
import com.stn.storage.entity.model.FileAttribute;
import com.stn.storage.exception.BadRequestException;
import com.stn.storage.exception.NotFoundException;
import com.stn.storage.helper.FileHelper;
import com.stn.storage.helper.GeneralHelper;
import com.stn.storage.helper.MimeHelper;
import com.stn.storage.repository.ImageRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

@Service
public class ImageService {
    private ImageRepository imageRepository;

    @Value("${storage.name}")
    private String storagePath;

    @Value("${storage.default-image.classpath}")
    private Resource resource;

    @Value("${storage.cloud.enable}")
    private Boolean useCloudStorage;

    private IBMCloudStorageService ibmCloudStorageService;

    @Autowired
    public ImageService(ImageRepository imageRepository, IBMCloudStorageService ibmCloudStorageService) {
        this.imageRepository = imageRepository;
        this.ibmCloudStorageService = ibmCloudStorageService;
    }

    @PostConstruct
    public void createDefaultDir() {
        FileHelper.autoCreateDir(Constant.PARENT_DIRECTORY + Constant.DIRECTORY_SEPARATOR + storagePath);
    }

    public ResponseEntity upload(MultipartFile[] files) {
        if (files.length <= 0) {
            throw new BadRequestException("Invalid file.");
        }
        Map<String, Object> result = new HashMap<>();
        List<Image> uploadedImages = new ArrayList<>();
        boolean isError = false;
        String message = "File(s) has successfully been uploaded.";
        for (MultipartFile file : files) {
            try {
                if (!file.isEmpty()) {
                    String filename = file.getOriginalFilename();
                    if (useCloudStorage) {
                        uploadedImages.add(storeToCloudStorage(filename, file.getBytes()));
                    } else {
                        FileAttribute fileAttribute = new FileAttribute(imageRepository, storagePath, filename);

                        FileOutputStream fileOutputStream = FileUtils.openOutputStream(new File(fileAttribute.getFileAbsolutePath()));
                        fileOutputStream.write(file.getBytes());
                        fileOutputStream.close();

                        uploadedImages.add(fileAttribute.asImage());
                    }
                } else {
                    message = "Nothing has been uploaded.";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isError = true;
                message = ex.getMessage();
                break;
            }
        }
        return showOutput(result, uploadedImages, isError, message);
    }

    public ResponseEntity uploadEncoded(List<Map<String, String>> payload) {
        if (payload.isEmpty()) {
            throw new BadRequestException("Invalid Payload.");
        }
        Map<String, Object> result = new HashMap<>();
        List<Image> uploadedImages = new ArrayList<>();
        boolean isError = false;
        String message = "Encoded file has successfully been uploaded.";
        for (Map<String, String> data : payload) {
            String filename = data.get("filename");
            String encodedFile = data.get("encoded_file");

            try {
                byte[] fileByteArray = Base64.getDecoder().decode(FileHelper.getRawDataFromEncodedBase64(encodedFile));
                if (useCloudStorage) {
                    uploadedImages.add(storeToCloudStorage(filename, fileByteArray));
                } else {
                    FileAttribute fileAttribute = new FileAttribute(imageRepository, storagePath, filename);
                    FileOutputStream fileOutputStream = FileUtils.openOutputStream(new File(fileAttribute.getFileAbsolutePath()));
                    fileOutputStream.write(fileByteArray);
                    fileOutputStream.close();

                    uploadedImages.add(fileAttribute.asImage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isError = true;
                message = ex.getMessage();
                break;
            }
        }
        return showOutput(result, uploadedImages, isError, message);
    }

    public ResponseEntity uploadViaURL(List<String> urls) {
        if (urls.isEmpty()) {
            throw new BadRequestException("Invalid data url.");
        }
        Map<String, Object> result = new HashMap<>();
        List<Image> uploadedImages = new ArrayList<>();
        boolean isError = false;
        String message = "URL file(s) has successfully been uploaded.";
        for (String url : urls) {
            try {
                URL dataURL = new URL(url);
                FileAttribute fileAttribute = new FileAttribute(imageRepository, storagePath, dataURL);
                ReadableByteChannel readableByteChannel = Channels.newChannel(dataURL.openStream());
                if (useCloudStorage) {
                    byte[] bytes = IOUtils.toByteArray(new URL(url));
                    String filename = String.format("%s.%s", fileAttribute.getName(), fileAttribute.getExt());
                    uploadedImages.add(storeToCloudStorage(filename, bytes));
                } else {
                    FileOutputStream fileOutputStream = FileUtils.openOutputStream(new File(fileAttribute.getFileAbsolutePath()));
                    FileChannel fileChannel = fileOutputStream.getChannel();
                    fileOutputStream.getChannel()
                            .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    fileOutputStream.close();

                    uploadedImages.add(fileAttribute.asImage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isError = true;
                message = ex.getMessage();
                break;
            }
        }
        return showOutput(result, uploadedImages, isError, message);
    }

    private ResponseEntity showOutput(Map<String, Object> result, List<Image> uploadedImages, boolean isError, String message) {
        if (isError) {
            result.put("status", HttpStatus.UNPROCESSABLE_ENTITY);
            result.put("message", message);
            return new ResponseEntity<>(result, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        imageRepository.saveAll(uploadedImages);
        result.put("data", uploadedImages);
        result.put("status", HttpStatus.OK.value());
        result.put("message", message);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public ResponseEntity getImage(String token) {
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("Invalid token.");
        }
        Map<String, Object> result = new HashMap<>();
        Optional<Image> optionalImage = imageRepository.findFirstByToken(token);
        if (!optionalImage.isPresent()) {
            throw new NotFoundException(String.format("Image with token %s does not exists.", token));
        }
        HttpStatus httpStatus = HttpStatus.OK;
        String message = "OK";
        String encodedFile = null;
        Image image = optionalImage.get();
        try {
            String pathFile = image.getIsDeleted() ? image.getPath() : Constant.PARENT_DIRECTORY + Constant.DIRECTORY_SEPARATOR + image.getPath();
            byte[] fileContent = FileUtils.readFileToByteArray(new File(pathFile));
            encodedFile = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException ex) {
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
            message = ex.getMessage();
        }
        result.put("status", httpStatus.value());
        result.put("message", message);
        result.put("data", encodedFile);
        return new ResponseEntity<>(result, httpStatus);
    }

    public ResponseEntity getFile(String token, boolean is_download) {
        Map<String, Object> result = new HashMap<>();
        Optional<Image> file = imageRepository.findFirstByToken(token);
        if (file.isPresent()) {
            try {
                Image image = file.get();
                HttpHeaders headers = new HttpHeaders();
                InputStream inputFile;
                if (file.get().getIsCloudStorage()) {
                    inputFile = ibmCloudStorageService.getObject(image.getToken());
                } else {
                    String path = image.getPath();
                    path = Constant.DIRECTORY_SEPARATOR + path;

                    String filename = image.getIsDeleted() ? path : Constant.PARENT_DIRECTORY + path;
                    inputFile = image.getIsDeleted() ? resource.getInputStream() : new FileInputStream(filename);
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = IOUtils.toByteArray(inputFile);
                outputStream.write(buffer, 0, buffer.length);

                String name = image.getFilename();
                String extension = image.getExt();
                String mimeType = MimeHelper.guess("." + extension);
                if (is_download) {
                    headers.set("Content-Type", "application/x-javascript; charset=utf-8");
                    headers.set("Content-Disposition", "attachment; filename=\"" + name + "");
                } else {
                    if (extension.matches("(.*)jpg(.*)") || extension.matches("(.*)jpeg(.*)") || extension.matches("(.*)png(.*)") || extension.matches("(.*)gif(.*)") || extension.matches("(.*)bmp(.*)")) {
                        headers.set("Content-Type", "image/" + extension);
                        headers.set("Content-Disposition", "inline; filename=\"" + name + "");
                    } else if (mimeType != null) {
                        headers.set("Content-Type", mimeType);
                        headers.set("Content-Disposition", "attachment; filename=\"" + name + "");
                    } else {
                        headers.set("Content-Type", "application/x-javascript; charset=utf-8");
                        headers.set("Content-Disposition", "attachment; filename=\"" + name + "");
                    }
                }
                return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("status", HttpStatus.NOT_FOUND.value());
                result.put("message", e.getMessage());
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
        } else {
            result.put("status", HttpStatus.NOT_FOUND.value());
            result.put("message", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    private Image storeToCloudStorage(String filename, byte[] bytes) {
        String fileNameWithoutExt = FileHelper.getNameFile(filename);
        String extFile = FileHelper.getExtensionFile(filename);
        String generatedToken = GeneralHelper.generateToken();
        PutObjectResult objectResult = ibmCloudStorageService.uploadObject(bytes, generatedToken);
        if (objectResult != null) {
            return new Image(fileNameWithoutExt, extFile, generatedToken, true);
        }
        return null;
    }
}
