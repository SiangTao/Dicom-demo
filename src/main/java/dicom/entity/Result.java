package dicom.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: tsa
 * @Date: 2023/9/4 15:21
 */
@Data
@NoArgsConstructor   //无参构造
public class Result {
    private List<SQTagInfo> sqTagInfo;
    private List<TagInfo> tagInfo;
    private String url;

    public Result(List<SQTagInfo> sqTagInfo, List<TagInfo> tagInfo,String url) {
        this.sqTagInfo=sqTagInfo;
        this.tagInfo=tagInfo;
        this.url=url;
    }
}
