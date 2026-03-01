package dicom;

import dicom.entity.SQTagInfo;
import dicom.entity.TagInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @Description:
 * @Author: tsa
 * @Date: 2023/9/1 14:53
 */
public class SQnestedAttributeMap {

    private static final Logger logger = LogManager.getLogger(SQnestedAttributeMap.class);

    //用于保存在SQ类型中存储的Tag位信息，key是最外层SQ的group和element，value则是SQ类型Tag下存储的所有Tag位
    private Map<String,AttributeMap> SQnestedAttributeMap= new LinkedHashMap<>();

    private AttributeTag attributeTag;
    private AttributeMap attributeMap;

    public SQnestedAttributeMap(AttributeTag attributeTag, AttributeMap attributeMap) {
        this.attributeTag = attributeTag;
        this.attributeMap = attributeMap;
    }

    /**
     * 在SQ嵌套中的Map添加数据
     * @author
     * @param tag,attribute 传入的Tag和attributeMap
     */
    public void addObject_SQ(AttributeTag tag, AttributeMap attributeMap) {

        if (tag == null) {
            logger.error("Tag为空！");
        }
        if (attributeMap == null) {
            logger.error("数据部分为空！");
        }

        /* 定义存储tag的格式 */
        StringBuilder sb = new StringBuilder();
        sb
                .append(tag.getGroup())
                .append(",")
                .append(tag.getElement());
        String s = "";
        s = sb.toString();

        SQnestedAttributeMap.put(s, attributeMap);
    }

    public void printSQMap() {
        Set<String> set = SQnestedAttributeMap.keySet();
        logger.info("开始解析SQ嵌套：");
        for (String groupelement : set) {
            AttributeMap a=SQnestedAttributeMap.get(groupelement);
            logger.info("当前SQ的Tag："+groupelement);
            a.print();
        }
        logger.info("解析结束SQ嵌套");
    }

    public List<SQTagInfo> SQMapToObjectArr(){
        List<SQTagInfo> list=new ArrayList<>();
        Set<String> set = SQnestedAttributeMap.keySet();
        for (String fathertag : set) {
            //SQTagInfo sqTagInfo=new SQTagInfo();
            /*sqTagInfo.setFatherGroup(fathertag.substring(0,4));
            sqTagInfo.setFatherElement(fathertag.substring(5,9));*/
            AttributeMap a=SQnestedAttributeMap.get(fathertag);
            Set<String> inner_set = a.attributemap.keySet();
            for(String groupelement : inner_set) {
                SQTagInfo sqTagInfo=new SQTagInfo();
                sqTagInfo.setFatherGroup(fathertag.substring(0,4));
                sqTagInfo.setFatherElement(fathertag.substring(5,9));
                Attribute attribute = a.attributemap.get(groupelement);
                sqTagInfo.setGroup(groupelement.substring(0, 4));
                sqTagInfo.setElement(groupelement.substring(5));
                sqTagInfo.setVr(attribute.getVR());
                sqTagInfo.setSize(String.valueOf(attribute.getVL()));
                sqTagInfo.setVf(attribute.getVF());
                list.add(sqTagInfo);
            }
        }
        return list;
    }
}
