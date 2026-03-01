package utils;

import java.io.*;

/**
 * @Description:将十六进制转为图像输出
 * @Author: tsa
 * @Date: 2023/8/7 13:41
 */
public class HexToImageUtil {
    public static void HexToImage(File src, File out) {

        try (FileInputStream fis = new FileInputStream(src);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr);
             FileOutputStream fos = new FileOutputStream(out);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            String str = null;
            StringBuilder sb = new StringBuilder();

            while ((str = br.readLine()) != null) {
                System.out.println(str);
                sb.append(str);
            }
            str = sb.toString();

            for (int i = 0; i < str.length(); i += 2) {
                String s = "";
                s = str.substring(i, i + 2);
                int n = Integer.parseInt(s, 16);
                if (n < 0) n += 256;
                bos.write(n);
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
