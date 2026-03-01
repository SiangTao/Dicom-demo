package dicom;

/**
 建立一个能读取Dicom文件中的Tag的函数，并将Tag中的group和element都传入Map中
 * @author tsa
 * @time 2023-8-3
 */
public class AttributeTag {

    /** Tag中的group和element */
    private String group;
    private String element;

    /** 判断是否读取至图像数据,读到将返回false */
    private boolean isFinish;

    public static final String FORMAT_STR="%02x";

    public AttributeTag() {
        this.group = "";
        this.element = "";
        this.isFinish = false;
    }

    public AttributeTag(String var1, String var2) {
        this.group = var1;
        this.element = var2;
        this.isFinish = false;
    }

    public boolean getisfinish() { return isFinish; }

    public String getGroup() {
        return this.group;
    }

    public String getElement() {
        return this.element;
    }


    /**
     * 读取4个byte，对应存储至group和element
     * @author tsa
     * @param b,location 文件的字节数组,location是传入字节数组下标的数值
     * @return int
     */
    public int readTag(byte[] b, int location) {

        /* 使用StringBuilder节省资源 */
        StringBuilder group = new StringBuilder();
        StringBuilder element = new StringBuilder();

        /* 默认是小端的方式读取 */
        for (int i = 1; i >= 0; i--) {

            /* 读取group */
            String Sgro = String.format(FORMAT_STR, b[location + i]); //直接以十六进制的格式存储
            StringBuilder grosb = new StringBuilder(Sgro);
            group.append(grosb);

            /* 读取element */
            String Sele = String.format(FORMAT_STR, b[location + 2 + i]);
            StringBuilder elesb = new StringBuilder(Sele);
            element.append(elesb);

            /* (e07f,1000)是像素数据开始的tag，读到此Tag即结束 */
            if ("7fe0".contentEquals(group) && "0010".contentEquals(element)) {
                this.isFinish = true;
            }
        }

        /* tag占4个byte */
        location += 4;

        this.group = group.toString();
        this.element = element.toString();

        return location;
    }
}
