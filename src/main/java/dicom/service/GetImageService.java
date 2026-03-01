package dicom.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: Service层
 * @Author: tsa
 * @Date: 2023/8/31 11:24
 */
public interface GetImageService {
    public String getImage(MultipartFile file);
}
