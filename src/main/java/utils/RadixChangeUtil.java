package utils;

public class RadixChangeUtil {

    /*
    计算机中存储都是补码！
    由于一个int由4个byte组成，而符号位是第一个byte中第一位
    但是如需将4个byte分配到对应byte数组中时，数组中每个元素是-128到127，就会导致原本不是符号位被当作符号位对待
    最终会得到其补码，所以需要再将补码还原成原码储存
     */
    public static byte[] IntToByte(int n) {
        byte[] a = new byte[4];
        //0xff代表了二进制 1111,1111，目的是只取低八位
        a[3] = (byte) (n & 0xff);
        a[2] = (byte) ((n >> 8) & 0xff);
        a[1] = (byte) ((n >> 16) & 0xff);
        a[0] = (byte) ((n >> 24) & 0xff);

        /*if(a[3]<0){a[3]= (byte) (~a[3]+1); }
        if(a[2]<0){a[3]= (byte) (~a[2]+1); }
        if(a[1]<0){a[3]= (byte) (~a[1]+1); }*/
        return a;
    }

    public static String[] ByteToBit(byte b[]) {
        int len = b.length;
        String[] s = new String[len];

        /*
            s[i]="" + (byte) ((b[i] >> 7) & 0x1) +
                    (byte) ((b[i] >> 6) & 0x1) +
                    (byte) ((b[i] >> 5) & 0x1) +
                    (byte) ((b[i] >> 4) & 0x1) +
                    (byte) ((b[i] >> 3) & 0x1) +
                    (byte) ((b[i] >> 2) & 0x1) +
                    (byte) ((b[i] >> 1) & 0x1) +
                    (byte) ((b[i] >> 0) & 0x1);

         */

        for (int i = 0; i < len; i++) {
            s[i] = "";
            for (int j = 12 - len - 1; j >= 0; j--) {
                s[i] = s[i] + (byte) ((b[i] >> j) & 0x1);
            }
        }
        return s;
    }

    //对4个8位位二进制字节数组再进行对半分割
    public static String ByteToHex(byte[] a) {
        int len = 8;
        byte[] b = new byte[len];
        for (int i = 3; i >= 0; i--) {

            //按读写习惯顺序存放
            b[i * 2 + 1] = (byte) (a[i] & 0xf);
            b[i * 2] = (byte) ((a[i] >> 4) & 0xf);
        }

        /*for(int i=0;i<8;i++){
            System.out.print(b[i]+" ");
        }*/

        int k = 0;
        String result = "";
        String[] number = {"a", "b", "c", "d", "e", "f"};
        while (k < len) {
            int p = b[k];
            if (p >= 10) {
                result = result + number[p - 10];
            } else {
                result = result + String.valueOf(p);
            }
            k++;
        }
        return result;
    }

    public static int HexToDEC(String sHex) {

        int p = Integer.parseInt(sHex, 16);

//        String[] temp;
//        for(int i=0;i<sHex.length();i++){
//
//        }

        return p;

    }
}
