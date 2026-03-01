package dicom.service.impl;

import dicom.ReadDicom;
import dicom.service.GetImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.MutipartFileToFileUtil;
import utils.ServletUtils;

import java.io.File;

/**
 * @Description: 返回图片url至前端
 * @Author: tsa
 * @Date: 2023/8/31 11:41
 */

@Service
public class GetImageServiceimpl implements GetImageService {

    @Value("${file-save-path}")
    private String fileSavePath;

    @Override
    public String getImage(MultipartFile file) {
        File dicomfile= MutipartFileToFileUtil.MultipartFileToFile(file);
        ReadDicom readDicom=new ReadDicom();
        File out= readDicom.readPixel(dicomfile, fileSavePath);
        String fileName=out.getName();
        String url= ServletUtils.getImageUrl(fileName);
        return url;
    }
}
