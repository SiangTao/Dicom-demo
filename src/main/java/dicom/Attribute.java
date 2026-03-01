package dicom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 建立一个数据集类，其中包括所有元素(Tag、VR、VL、VF)
 * @author tsa
 * @time 2023-8-3
 */
public class Attribute {

    private AttributeTag tag;
    private static final Logger logger = LogManager.getLogger(Attribute.class);

    private SQnestedAttributeMap sQnestedAttributeMap;
    private Attribute attribute;

    protected String VR; //数据类型
    protected int VL; //数据长度
    protected String VF; //数据内容
    protected boolean specialVR; //特殊VR(OW、SQ等)
    protected boolean skip; //跳过正常readVF()环节
    protected boolean explicitVR; //默认显式
    protected boolean littleendian; //默认小端

    protected Attribute(AttributeTag var1) {
        this.tag = var1;
        this.VR = "";
        this.VL = 0;
        this.VR = "";
        this.specialVR = false;
        this.skip = false;
        this.explicitVR = true;
        this.littleendian = true;
    }

    public boolean getspecialVR() {
        return specialVR;
    }

    public void setSpecialVR(boolean specialVR) {
        this.specialVR = specialVR;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public boolean getSkip() {
        return skip;
    }

    public void setVF(String VF) {
        this.VF = VF;
    }

    public void setVL(int VL) {
        this.VL = VL;
    }

    public void setVR(String VR) {
        this.VR = VR;
    }

    public String getVR() {
        return VR;
    }

    public int getVL() {
        return VL;
    }

    public String getVF() {
        return VF;
    }

    /**
     * 判断是否是特殊数据类型
     * @author
     * @param VR 数据类型
     * @return boolean
     */
    public boolean IsSpecialVR(String VR) {
        if ("OB".equals(VR) || "OW".equals(VR) || "OF".equals(VR) || "UT".equals(VR) || "SQ".equals(VR) || "UN".equals(VR)) {
            return true;
        }
        return false;
    }

    /**
     * 读取VR
     * @author
     * @param b,location
     * @return int
     */
    public int readVR(byte[] b, int location) {

        /* 初始化 */
        setVF("");
        setSpecialVR(false);

        /* 根据16进制获取VR的ASCII，默认VR占取两个byte */
        char vr = (char) (b[location]);
        char vr1 = (char) (b[location + 1]);
        StringBuilder VRstringBuffer = new StringBuilder();
        VRstringBuffer.append(vr).append(vr1);
        setVR(VRstringBuffer.toString());
        location += 2;

        /* 判断VR是否特殊 */
        if (IsSpecialVR(getVR())) {
            setSpecialVR(true);

            location += 2; //特殊VR一共占取4个byte，其中后两位作为保留
        }

        return location;
    }

    /**
     * 读取VL
     * @author
     * @param b,location,specialVR,s
     * @return int
     */
    //把AttributeTag传到readSQ中，方便存储当前的Tag信息
    public int readVL(byte[] b, int location, boolean specialVR,AttributeTag attributeTag,SQnestedAttributeMap sQnestedAttributeMap) {

        /* 初始化 */
        setSkip(false);

        /* 默认VL占取两个byte */
        String sVL = "";
        sVL = String.format("%02x", b[location + 1]) + String.format("%02x", b[location]);
        location += 2;

        /* 特殊VR时，VL需要读取四位，在原有基础上读取到后两位 */
        if (specialVR) {
            StringBuilder temp = new StringBuilder();
            temp.append(String.format("%02x", b[location + 1]))
                    .append(String.format("%02x", b[location]))
                    .append(sVL);
            sVL = temp.toString();
            location += 2;
        }

        /* 当数据类型是SQ时，需要判断是否有嵌套;当VL为"ff ff ff ff"时代表有嵌套 */
        if ("SQ".equals(getVR()) && "ffffffff".equals(sVL)) {


            setSkip(true); //当进入嵌套时，使用特殊readSQ()函数，不再使用普通数据类型的readVF()

            //一个嵌套中仅需要一个AttributeMap,为了方便核实后续一个嵌套中是否存在多组相同的Tag
            AttributeMap attributeMap=new AttributeMap();
            while (isLoopSQ(b, location)) {
                location = readSQ(b, location,attributeTag,sQnestedAttributeMap,attributeMap);
            }

            /* isLoopSQ后location位置未变，手动增加 */
            location += 8; //8位是结束标志

        }

        /* 当SQ嵌套时，VL内容设置为9999 */
        if ("ffffffff".equals(sVL)) {
            setVL(9999);
        } else {
            setVL(Integer.parseInt(sVL, 16));
        }

        return location;

    }

    /**
     * 读取VF
     * @author
     * @param b,location
     * @return int
     */
    public int readVF(byte[] b, int location) {

        if (!getSkip()) {
            String s = "";
            for (int i = 0; i < getVL(); i++) {
                s = s + String.format("%02x", b[location + i]) + " ";
            }
            setVF(s);

        VF_Format format=new VF_Format();
        format.VRType(getVR());
        s=format.VFType(b,location,getVL());
        setVF(s);

            return location + getVL();
        }

        return location;
    }

    /**
     * 判断本层嵌套结束后是否存在第二层嵌套
     * @author
     * @param b,location
     * @return boolean
     */
    public boolean isLoopSQ(byte b[], int location) {

        String s1 = "";
        for (int i = 0; i < 8; i++) {
            s1 = s1 + String.format("%02x", b[location++]);
        }

        /* "fe ff dd e0"和"00 00 00 00"是最外层嵌套结束标志 */
        if ("feffdde000000000".equals(s1)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 当VR是SQ时，使用该函数
     * @author
     * @param b,location
     * @return int
     */
    //AttributeTag继续往readSQVF里传
    public int readSQ(byte[] b, int location,AttributeTag attributeTag,SQnestedAttributeMap sQnestedAttributeMap,AttributeMap attributeMap) {

        StringBuilder tagsb = new StringBuilder();//存储嵌套中的tag
        StringBuilder VLsb = new StringBuilder();//存储嵌套中的VL
        StringBuilder VFsb = new StringBuilder();//存储嵌套中的VF

        for (int i = 0; i < 4; i++) {
            tagsb.append(String.format("%02x", b[location + i]));
        }
        location += 4;

        /* "fe ff 00 e0"是嵌套中的tag，作为开始位 */
        if ("feff00e0".contentEquals(tagsb)) {

            /* 读取VL */
            for (int i = 0; i < 4; i++) {
                VLsb.append(String.format("%02x", b[location + i]));
            }
            location += 4;


            /* "ff ff ff ff"是嵌套中的VL值 */
            if ("ffffffff".contentEquals(VLsb)) {
                location=readSQVF_unknownlen(b,location,attributeTag,sQnestedAttributeMap,attributeMap); //情况一（大多数情况下），长度未知
            }

            /* 除了"ff ff ff ff"这种未定义数据长度的情况，还有写明VL的数值 */
            else {
                location=readSQVF_knownlen(b,location); //情况二，VL已知
            }

        } else {
            logger.error("SQ嵌套出错！");
        }

        return location;
    }

    /**
     * 当SQ嵌套下，VL为"ff ff ff ff"，即是未定义长度时使用此函数来寻找结束标志并记录VF
     * @author
     * @param b,location
     * @return int 返回location的值
     */
    public int readSQVF_unknownlen(byte[] b,int location,AttributeTag attributeTag_original,SQnestedAttributeMap sQnestedAttributeMap,AttributeMap attributeMap){
        StringBuilder VFsb=new StringBuilder();
        StringBuilder isover=new StringBuilder();

        AttributeTag attributeTag=new AttributeTag();
        attribute=new Attribute(attributeTag);
        //attributeMap=new AttributeMap(attributeTag,attribute);

        for(int j=0;j<8;j++){
            //先读取8位来判断是否是结束标识符
            isover.append(String.format("%02x", b[location++]));
        }
        while(!"feff0de000000000".contentEquals(isover)){
            //回到原位
            location=location-8;
            int start=location;

            //正常读取Tag的信息
            attributeTag=new AttributeTag();
            attribute=new Attribute(attributeTag);
            int x=attributeTag.readTag(b,location);
            int y=attribute.readVR(b,x);
            int z=attribute.readVL(b,y,attribute.getspecialVR(),attributeTag,sQnestedAttributeMap);
            location=attribute.readVF(b,z);

            //判断一个嵌套中是否存在多个item（item中Tag位可能相同，会导致Map中相同的Key的Value覆盖问题），所以检查Key和当前的Tag是否相同
            int num=0;
            for(String groupelement : attributeMap.getKey()){
                //Key必须去掉下方增加的“第几组”样式
                if(groupelement.substring(0,9).equals(attributeTag.getTag())){
                    num++;
                }
            }
            if(num>0){
                String newGroupele=attributeTag.getTag()+"_第"+num+"组";
                attributeMap.addObject(newGroupele,attribute);
            }
            //添加到Map中
            attributeMap.addObject(attributeTag,attribute);

            //在原先SQ类型的Tag中继续保存VL值，方便查看嵌套读取的Tag是否正确
            for(int i=start;i<location;i++){
                VFsb.append(String.format("%02x", b[i]));
            }

            //由于前面已经按规则读取过Tag、VR等，所以可以直接读取8位16进制数来判断，不用担心读错位的问题
            //先删除之前保存的8位16进制数，然后在读取8位，让while去判断是否读到结束符
            isover.delete(0,16);
            for(int j=0;j<8;j++){
                isover.append(String.format("%02x", b[location++]));
            }
        }
        //往SQ嵌套的Map中传
        sQnestedAttributeMap.addObject_SQ(attributeTag_original,attributeMap);

        /*for (int i = 0; ; i++) {
            *//* 读取VF值，当出现"fe ff 0d e0","00 00 00 00"则是嵌套结束标志 *//*
            VFsb.append(String.format("%02x", b[location++]));

            if (VFsb.indexOf("feff0de000000000") != -1) {
                break;
            }
        }

        VFsb.delete(VFsb.length() - 16, VFsb.length()); //删除结束标志
        */

        VF = VF + VFsb.toString(); //保存的是最外层的Value信息

        return location;
    }

    /**
     * 当SQ嵌套下，已知VF长度，VL是具体数值时
     * @author
     * @param b,location
     * @return int 返回location的值
     */
    public int readSQVF_knownlen(byte[] b,int location){
        String sVL = "";

        /* 读取VL */
        for (int i = 4; i > 0; i--) {
            sVL = sVL + String.valueOf(b[location + i]);
        }
        location += 4;
        int len = Integer.parseInt(sVL);

        String sVF = "";
        /* 读取VF */
        for (int i = 0; i < len; i++) {
            sVF = sVF + String.valueOf(b[location + i]);
        }

        VF = VF + sVF;
        location += len;
        return location;
    }

}
//dicom通讯
//本地网络上传和下载
