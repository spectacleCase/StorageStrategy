package com.choose;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


/**
 * <p>
 * 通过控制器
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/5/27 上午12:59
 */
@RestController
@Slf4j
@RequestMapping("/storage/common")
public class CommonController {

    @Resource
    private ResourceLoader resourceLoader;

    @Resource
    private StorageStrategy storageStrategy;

    @Value("${local.path}")
    private String uploadPhotoPath;


    @PostMapping("/v1/upload")
    public UploadVo upload(@RequestParam("file") MultipartFile file) {
        return storageStrategy.upload(file);
    }

    @GetMapping("/v1/getImage")
    public ResponseEntity<?> getImage(@RequestParam(name = "filename", required = true) String filename) {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("file:" + uploadPhotoPath + filename);
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}