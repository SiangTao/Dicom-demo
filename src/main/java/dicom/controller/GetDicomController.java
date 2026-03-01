package dicom.controller;

import dicom.service.GetDicomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @Description: 获得Tag位信息的接口，返回json至前端
 * @Author: tsa
 * @Date: 2023/8/22 13:59
 */

@CrossOrigin  //跨域访问
@RestController
public class GetDicomController {

    private static final Logger logger = LogManager.getLogger(GetDicomController.class);

    @Autowired
    private GetDicomService getDicomService;

    //RequestParam配置中还有required参数，默认为true，不填写则会报错
    @PostMapping("/getDicom")
    public String getDicom(@RequestParam(value="file",required=false) MultipartFile file){
        logger.info("/getDicom接口调用");
        return getDicomService.getDicom(file);
    }
}
