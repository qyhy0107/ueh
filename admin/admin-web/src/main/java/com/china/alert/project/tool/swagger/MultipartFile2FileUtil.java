package com.china.alert.project.tool.swagger;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yanzeyang
 * date 2021/11/3 15:19
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger
 */
public class MultipartFile2FileUtil {
    /**
     * MultipartFile转换为File
     * @param multipartFile
     * @return
     * MultipartFile继承了InputStreamSource
     */
    public static File MultipartFile2File(MultipartFile multipartFile){
        //文件上传前的名称
        String fileName = multipartFile.getOriginalFilename();
        //new 一个 file
        File file = new File(fileName);
        OutputStream out = null;
        try{
            //获取文件流，以文件流的方式输出到新文件
//    InputStream in = multipartFile.getInputStream();
            //获取到一个file的输出流
            out = new FileOutputStream(file);
            //获取multipartFile的字节文件，并将字节文件遍历write到out输出流
            byte[] ss = multipartFile.getBytes();
            for(int i = 0; i < ss.length; i++){
                out.write(ss[i]);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            //进行判断关闭输出流
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
    public static void deleteFile(File file){
        File f = new File(file.toURI());
        f.delete();
    }
}
