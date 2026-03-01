package utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Description: 上传文件格式转换工具
 * @Author: tsa
 * @Date: 2023/8/30 9:42
 */
public class MutipartFileToFileUtil {
    /**
     * 将MultipartFile转换为File
     * @param multipartFile
     * @return File
     */
    public static File MultipartFileToFile(MultipartFile multipartFile){
        //获取文件名
        String fileName=multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        try{
            File file=File.createTempFile(fileName, prefix);
            //File file=new File(fileName, prefix);
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
