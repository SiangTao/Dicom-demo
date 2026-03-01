package dicom.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: Service层
 * @Author: tsa
 * @Date: 2023/8/22 13:55
 */
public interface GetDicomService {
    public String getDicom(MultipartFile file);
}
