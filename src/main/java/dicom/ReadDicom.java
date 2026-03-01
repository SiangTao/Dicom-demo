package dicom;

import dicom.entity.SQTagInfo;
import dicom.entity.TagInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;
import utils.RadixChangeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
建立一个读取Dicom文件的类，包含判断文件类型以及解析数据的功能
 * @author tsa
 * @time 2023-8-3
 */
public class ReadDicom {

    /** 存储文件十六进制的数据 */
    private static byte[] b;

    /** 定义日志文件 */
    private static final Logger logger = LogManager.getLogger(ReadDicom.class);

    /** 定义输出格式 */
    public static final String FORMAT_STR="%02x";

    private List<TagInfo>list;

    private List<SQTagInfo> sqTagInfoList;

    public ReadDicom(){
        list=null;
        sqTagInfoList=null;
    }

    /**
     * 在主函数中调取该方法可以对Dicom文件进行读取和解析
     * @author
     * @param file 用户上传的文件
     */
    public void readDicom(File file) {

        /* 判断用户上传是否是文件类型 */
        if(!IsFile(file)){
            return ;
        }

        /* 读取文件 */
        b=readdata(file);

        /* 判断是否是Dicom文件 */
        if(IsDicomfile(b)){

            /* 打印Dicom文件数据 */
            printDicom(b);

            /* 解读Dicom文件 */
            analysisDicom();

            /* 读取图片 */
            readPixel(file);
        }

    }


    /**
     * 判断上传的是文件还是目录
     * @author tsa
     * @param file 用户上传的文件
     * @return boolean
     */
    public boolean IsFile(File file){
        if(file.isFile()){
            logger.info("用户上传为文件");
            return true;
        }
        else {
            logger.error("用户上传非文件");
            return false;
        }
    }


    /**
     * 读取文件并存储至字节数组中，便于后续函数调用
     * @author tsa
     * @param file 用户上传的文件
     * @return byte[]
     */
    public byte[] readdata(File file){
        logger.info("开始读取文件");

        try (FileInputStream fis = new FileInputStream(file);
        ) {
            b = new byte[(int) file.length()];
            fis.read(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("读取文件完毕");
        return b;
    }


    /**
     * 判断文件类型是否是Dicom类型
     * @author tsa
     * @param b 文件的字节数组
     * @return boolean
     */
    public boolean IsDicomfile(byte[] b){

        if (b.length < 132) {
            logger.error("不是DICOM文件！");
            return false;
        }

        byte[] t = new byte[4];
        for (int i = 128; i < 132; i++) {
            t[i - 128] = b[i];
        }

        /* 调用工具类读取"DICM"*/
        String s = RadixChangeUtil.ByteToHex(t);

        if (!"4449434d".equals(s)) {
            logger.error("不是DICOM文件！");
            return false;
        }
        return true;
    }


    /**
     * 打印Dicom文件的十六进制数据
     * @author tsa
     * @param b 文件的字节数组
     */
    public void printDicom(byte[] b){
        logger.info("开始读取DICOM文件：");

        for (int i = 0; i < b.length; i++) {
            if (b[i] < 0) {
                int temp = b[i] + 256;
                System.out.print(String.format(FORMAT_STR, temp));
            } else {
                System.out.print(String.format(FORMAT_STR, b[i]));
            }
            if (i % 16 == 15) {
                System.out.println(";");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();

        logger.info("DICOM文件读取完毕！");
    }


    /**
     * @Description: 读取像素文件，使用Dcm4che库
     * @param src
     * @return void
     * @Author: tsa
     * @Date: 2023/8/7 14:11
     */
    public File readPixel(File src){

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 设置格式
        Long currentTime = System.currentTimeMillis();  // 获取当前时间戳
        String timeString = format.format(currentTime);  // 时间戳转为时间格式

        /* 获取文件名称以及后缀  */
        String fileName=src.getName();
        int suffix=fileName.lastIndexOf(".");
        String newfileName=fileName.substring(0,suffix)+timeString+".jpg";

        /* 文件保存路径  */
        String filepath="/Users/taosiang/dicom/images/"+newfileName;
        File out=new File(filepath);

        try {
            Dcm2jpgIOStreamOutput dcm2jpg = new Dcm2jpgIOStreamOutput();
            dcm2jpg.convert(src, out);
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return out;
    }

    /**
     * 对Dicom文件进行解析
     * @author tsa
     * @return List<TagInfo>
     */

    //void类型修改为List，方便满足读取Tag位后保存至TagInfo类中
    public void analysisDicom() {

        AttributeTag attributeTag = new AttributeTag();
        Attribute attribute = new Attribute(attributeTag);
        AttributeMap attributeMap = new AttributeMap(attributeTag, attribute);
        SQnestedAttributeMap sQnestedAttributeMap=new SQnestedAttributeMap(attributeTag,attributeMap);


        /* 每次读取对应内容后都会返回整形的location，代表了在字节数组中的下标，作为下个函数的参数传入 */
        int location = attribute.readVF(
                b, attribute.readVL(
                        b, attribute.readVR(
                                b, attributeTag.readTag(
                                        b, 132)), false,attributeTag,sQnestedAttributeMap)); /* 首次运行，将location设为132=128+4*/

        attributeMap.addObject(attributeTag, attribute);

        int temp=0, TEMP=0;
        /* 根据传回来的布尔值来判断是否结束读取*/
        while (!attributeTag.getisfinish()) {

            AttributeTag attributeTagtemp = new AttributeTag();
            Attribute attributetemp = new Attribute(attributeTagtemp);

            temp = attributetemp.readVR(b, attributeTagtemp.readTag(b, location));
            if (attributeTagtemp.getisfinish()) {
                break;
            }

            TEMP = attributetemp.readVL(b, temp, attributetemp.getspecialVR(),attributeTagtemp,sQnestedAttributeMap);
            location = attributetemp.readVF(b, TEMP);
            attributeMap.addObject(attributeTagtemp, attributetemp);
        }

        /* 打印解析后的信息*/
        attributeMap.print();
        sQnestedAttributeMap.printSQMap();

        this.list = attributeMap.MapToObjectArr();
        this.sqTagInfoList=sQnestedAttributeMap.SQMapToObjectArr();

    }

    public List<TagInfo> getTagInfo(){
        return list;
    }

    public List<SQTagInfo> getSQTagInfo(){
        return sqTagInfoList;
    }
}
