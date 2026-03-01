package dicom.controller;

import dicom.service.GetImageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: tsa
 * @Date: 2023/8/28 17:09
 */

@CrossOrigin
@RestController
public class GetImageController {

    private static final Logger logger = LogManager.getLogger(GetImageController.class);

    @Autowired
    private GetImageService getImageService;

    @PostMapping("/getImage")
    public String getImage(@RequestParam(value="file",required=false) MultipartFile file){
        logger.info("/getImage接口调用");
        return getImageService.getImage(file);
    }

}
