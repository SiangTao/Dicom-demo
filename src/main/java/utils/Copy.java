package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;


public class Copy {
    public static final int SIZE=1024;
    private static final Logger logger = LogManager.getLogger(Copy.class);

    public static void copydir(File dir, File des) {

        if (!dir.exists()) {
            logger.error("当前目录不存在!");
            return;
        }

        //mkdirs可以创建父目录，解决mkdir对于父目录为空会返回false
        if (!des.exists()) {
            if (!des.mkdir()) {
                logger.error("创建目录失败!");
            }//如果目录不存在，创建目录
        }

        File[] files = dir.listFiles();//获取当前文件夹下所有file
        for (File file : files) {

            //System.out.println(file.toString());

            //判断是文件还是文件夹
            // isDirectory()可能会导致死循环
            if (file.isFile()) {
                StringBuffer dirsb = new StringBuffer(dir.getAbsolutePath()).
                        append( File.separator ).
                        append(file.getName());
                StringBuffer dessb = new StringBuffer(des.getAbsolutePath()).
                        append( File.separator ).
                        append(file.getName());
                copyFile(new File(dirsb.toString()), new File(dessb.toString()));
            }

            //递归地将子文件夹中文件先复制到父文件夹
            else {
                StringBuffer dirsb = new StringBuffer(dir + File.separator + file.getName());
                StringBuffer dessb = new StringBuffer(des + File.separator + file.getName());
                copydir(new File(String.valueOf(dirsb)), new File(String.valueOf(dessb)));
            }

        }
    }

    public static void copyFile(File src, File target) {

        try (FileInputStream fis = new FileInputStream(src); BufferedInputStream bis = new BufferedInputStream(fis);
             FileOutputStream fos = new FileOutputStream(target); BufferedOutputStream bos = new BufferedOutputStream(fos))
        {
            byte[] buff = new byte[SIZE];
            int len = 0;
            while ((len = bis.read(buff)) != -1) {
                bos.write(buff, 0, len);
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}

