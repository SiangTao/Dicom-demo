package dicom.service.impl;

import com.alibaba.fastjson.JSONObject;
import dicom.ReadDicom;
import dicom.entity.Result;
import dicom.entity.SQTagInfo;
import dicom.entity.TagInfo;
import dicom.service.GetDicomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.MutipartFileToFileUtil;
import utils.ServletUtils;

import java.io.File;
import java.util.List;


/**
 * @Description: GetDicomService的实现层
 * @Author: tsa
 * @Date: 2023/8/22 13:58
 */

@Service
public class GetDicomServiceimpl implements GetDicomService {

    private static final Logger logger = LogManager.getLogger(GetDicomServiceimpl.class);
    @Override
    public String getDicom(MultipartFile file) {

        /* 使用MutipartFile工具类将其转为File */
        File dicomfile= MutipartFileToFileUtil.MultipartFileToFile(file);

        ReadDicom readDicom=new ReadDicom();
        readDicom.readDicom(dicomfile);

        List<TagInfo> list=readDicom.getTagInfo();
        List<SQTagInfo> sqTagInfo=readDicom.getSQTagInfo();
        File out= readDicom.readPixel(dicomfile);
        String fileName=out.getName();
        String url= ServletUtils.getImageUrl(fileName);
        Result result=new Result(sqTagInfo,list,url);

        String json=JSONObject.toJSONString(result);
        return json;
    }


}
