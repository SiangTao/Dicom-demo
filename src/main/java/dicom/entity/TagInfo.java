package dicom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 返回至前端的实体类，包含了Tag位的信息
 * @Author: tsa
 * @Date: 2023/8/28 14:21
 */
@Data
@AllArgsConstructor //全参构造
@NoArgsConstructor   //无参构造
public class TagInfo {
    private String Group;
    private String Element;
    private String Vr;
    private String Size;
    private String Vf;
}
