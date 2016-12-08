package com.crazy.imgManager.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.crazy.imgManager.common.ImgUtils;
import com.crazy.imgManager.service.ImgService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * Created by pcg on 16/3/14.
 */
public class ImgServiceImpl implements ImgService{

    private static String realPath = "/usr/local/deploy/upload";

    private static String rootDir = "/default";

    private static final String tempDir = "temp";

    private static final String fixDir = "fix";

    private static Random random = new Random(999);

    static {
        Properties pro = new Properties();
        InputStream in = ImgServiceImpl.class.getClassLoader().getResourceAsStream("/conf/setting.properties");
        try{
            pro.load(in);
            realPath = pro.getProperty("picAddress").trim();
            rootDir = pro.getProperty("picProject").trim();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void saveFile(String upPath,String uploadName,byte[] fileBytes){
        OutputStream outputStream = null;
        try{
            outputStream = new FileOutputStream(new File(upPath,uploadName));
            outputStream.write(fileBytes);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String getRandomFileName(String suffix){
        StringBuffer r = new StringBuffer();
        for(int i = 0;i<5;i++){
            r.append(random.nextInt(10));
        }
        return System.currentTimeMillis() + "-" + r.toString() + "." + suffix;
    }

    public HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    @Override
    public String uploadToTemp(String x,String y,String w,String h,String compressWid,String suffix,byte[] fileBytes) {
        System.out.println(realPath);
        String projectPath = rootDir + File.separator;
        String srcUploadPath = realPath + projectPath + tempDir;

        if(!new File(srcUploadPath).isDirectory()){
            new File(srcUploadPath).mkdirs();
        }
        String fixPath = null;
        try {

            String uploadName = getRandomFileName(suffix);
            this.saveFile(srcUploadPath, uploadName, fileBytes);
            //裁剪源文件
            String tempPath = srcUploadPath + File.separator + uploadName;
            //裁剪后文件
            String cutName = getRandomFileName(suffix);
            fixPath = projectPath + tempDir + File.separator + cutName;
            String cutPath = srcUploadPath + File.separator + cutName;
//            if(!new File(cutPath).isFile()){
//                new File(cutPath)
//            }
            Integer intX = Integer.parseInt(x);
            Integer intY = Integer.parseInt(y);
            if(w.indexOf(".") != -1){
                w = w.substring(0,w.indexOf("."));
            }
            if(h.indexOf(".")!=-1){
                h = h.substring(0,h.indexOf("."));
            }
            Integer intW = Integer.parseInt(w);
            Integer intH = Integer.parseInt(h);
            Integer intCompressWid = Integer.parseInt(compressWid);
            ImgUtils.imgCut(tempPath, cutPath, intX, intY, intW, intH, intCompressWid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fixPath;
    }

    @Override
    public String tempToFix(String tempPath){
        String fixPaths = "";
        for(String s : tempPath.split(",")){
            if(StringUtils.isEmpty(tempPath)){
                continue;
            }
            fixPaths += tempToFixSingle(s) + ",";
        }
        if(!StringUtils.isEmpty(fixPaths)){
            fixPaths = fixPaths.substring(0,fixPaths.length()-1);
        }
        return fixPaths;
    }

    private String tempToFixSingle(String singleTempPath){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String refixStr = "";
        try{
//            String realPath = request.getSession().getServletContext().getRealPath("/");
            String tempStr = realPath + File.separator + singleTempPath;
            //非临时目录，不移动
            if(tempStr.indexOf(tempDir) == -1){
                return singleTempPath;
            }
            String singleFixName = singleTempPath.substring(singleTempPath.lastIndexOf("/") + 1);
            String singleFixPath = realPath + File.separator + rootDir + File.separator + fixDir;
            if(!new File(singleFixPath).isDirectory()){
                new File(singleFixPath).mkdirs();
            }
            refixStr = rootDir + File.separator + fixDir + File.separator + singleFixName;
            //读取临时目录文件
            inputStream = new FileInputStream(tempStr);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);

            this.saveFile(singleFixPath,singleFixName,bytes);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return refixStr;
    }

    @Override
    public String testHessian(String input,byte[] fileBytes) {

        String suffix = "jpg";
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String realPath = request.getSession().getServletContext().getRealPath("/");
        System.out.println(realPath);
        String projectPath = rootDir + File.separator;
        String srcUploadPath = realPath + projectPath + tempDir;

        if(!new File(srcUploadPath).isDirectory()){
            new File(srcUploadPath).mkdirs();
        }
        String fixPath = null;
        try {

            String uploadName = System.currentTimeMillis() + new Random(50000).nextInt() + "." + suffix;

            OutputStream outputStream = new FileOutputStream(new File(srcUploadPath,uploadName));
            outputStream.write(fileBytes);
            fixPath = srcUploadPath + File.separator + uploadName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fixPath;
    }


    @Override
    public String uploadToFix(String projectName,String suffix, byte[] fileBytes) {
        String fileName = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentTime = sdf.format(new Date());
            String filePath = realPath + File.separator + projectName + File.separator + currentTime;
            File file = new File(filePath);
            if(!file.exists() && !file.isDirectory()){
                file.mkdirs();
            }
            fileName = getRandomFileName(suffix);
            //保存文件
            saveFile(filePath,fileName,fileBytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return fileName;
    }
}
