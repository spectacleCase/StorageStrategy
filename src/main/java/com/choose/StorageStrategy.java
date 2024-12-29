package com.choose;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 存储策略
 * </p>
 *
 * @author 桌角的眼镜
 * @version 1.0
 * @since 2024/11/27 下午1:03
 */
public interface StorageStrategy {

    public  UploadVo upload(MultipartFile filePath);

}