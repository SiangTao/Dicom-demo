package dicom;

import dicom.entity.TagInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

/**
 建立一个Map用于存储Tag作为key，和其Attribute(VR、VL、VF)作为value，做到一一对应
 * @author tsa
 * @time 2023-8-3
 */
public class AttributeMap {

    private static final Logger logger = LogManager.getLogger(AttributeMap.class);

    /** 使用LinkedHashMap，可以做到有序存储，便于输出*/
    public Map<String, Attribute> attributemap = new LinkedHashMap<>();

    private AttributeTag attributeTag;
    private Attribute attribute;

    public AttributeMap(){
        this.attributeTag = null;
        this.attribute = null;
    }

    public AttributeMap(AttributeTag attributeTag, Attribute attribute) {
        this.attributeTag = attributeTag;
        this.attribute = attribute;
    }

    /**
     * 在Map中添加数据
     * @author
     * @param tag,attribute 传入的Tag和attribute类(包括了VR、VL、VF)
     */
    public void addObject(AttributeTag tag, Attribute attribute) {

        if (tag == null) {
            logger.error("Tag为空！");
        }
        if (attribute == null) {
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

        attributemap.put(s, attribute);
    }

    public void addObject(String groupelement, Attribute attribute) {

        if (groupelement == null) {
            logger.error("Group和element为空！");
        }
        if (attribute == null) {
            logger.error("数据部分为空！");
        }

        attributemap.put(groupelement, attribute);
    }

    /**
     * 根据格式打印Map
     * @author
     */
    public void print() {
        Set<String> set = attributemap.keySet();
        logger.info("开始解析：");
        for (String groupelement : set) {
            Attribute a = attributemap.get(groupelement);
            logger.info("Tag:"+groupelement + "   VR:" + a.getVR() + "   VL:" + String.format("%6d", a.getVL()) + "   VF:" + a.getVF());
            // System.out.println(groupelement + "   VR:" + a.getVR() + "   VL:" + String.format("%4d", a.getVL()) + "   VF:" + a.getVF());
        }
        logger.info("解析结束");
    }

    /**
     * 将Map中的数据保存至对象List中，便于返回至前端
     * @author
     * @return List<TagInfo>
     */
    public List<TagInfo> MapToObjectArr(){
        List<TagInfo> list=new ArrayList<>();
        Set<String> set = attributemap.keySet();
        for (String groupelement : set) {
            TagInfo tagInfo=new TagInfo();
            Attribute a = attributemap.get(groupelement);
            tagInfo.setGroup(groupelement.substring(0,4));
            tagInfo.setElement(groupelement.substring(5,9));
            tagInfo.setVr(a.getVR());
            tagInfo.setSize(String.valueOf(a.getVL()));
            tagInfo.setVf(a.getVF());
            list.add(tagInfo);
        }
        return list;
    }

    public Set<String> getKey(){
        Set<String> set = attributemap.keySet();
        return  set;
    }
}
