package com.choose;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/27 下午1:10
 */

@Slf4j
public class MinioStorageStrategy implements StorageStrategy {
    private final static String SEPARATOR = "/";
    private final String endpoint;
    private final String bucket;
    private final MinioClient minioClient;


    public MinioStorageStrategy(String endpoint, String accessKey, String secretKey, String bucket, String readPath) {
        this.endpoint = endpoint;
        this.bucket = bucket;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }



    @Override
    public UploadVo upload(MultipartFile file) {

        if (Objects.isNull(file) || file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }

        // 上传照片
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFilename)) {
            return null;
        }
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filePath = builderFilePath("", fileName + postfix);
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("image/jpeg")
                    .bucket(bucket).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            StringBuilder urlPath = new StringBuilder();
            urlPath.append(endpoint);
            urlPath.append(SEPARATOR).append(bucket);
            urlPath.append(SEPARATOR);
            urlPath.append(filePath);

            if (!urlPath.isEmpty()) {
                UploadVo uploadVo = new UploadVo();
                uploadVo.setFileName(urlPath.toString());
                return uploadVo;
            }
        } catch (Exception e) {
            log.error("minio,put file error", e);
            throw new RuntimeException("上传文件失败");
        }
        // 可以记录日志或处理异常
        return null;
    }

    public String builderFilePath(String dirPath, String fileName) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if (!StringUtils.isEmpty(dirPath)) {
            stringBuilder.append(dirPath).append(SEPARATOR);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(SEPARATOR);
        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }

}