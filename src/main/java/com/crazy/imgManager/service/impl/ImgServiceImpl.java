package com.crazy.imgManager.service.impl;

import com.crazy.imgManager.common.ImgUtils;
import com.crazy.imgManager.service.ImgService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Random;

/**
 * Created by pcg on 16/3/14.
 */
public class ImgServiceImpl implements ImgService{

    private static final String rootDir = "upload";

    private static final String tempDir = "temp";

    private static final String fixDir = "fix";


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

        return System.currentTimeMillis() + new Random(50000).nextInt() + "." + suffix;
    }

    public HttpServletRequest getRequest(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    @Override
    public String uploadToTemp(String x,String y,String w,String h,String compressWid,String suffix,byte[] fileBytes) {
        HttpServletRequest request = getRequest();
        String realPath = request.getSession().getServletContext().getRealPath("/");
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
            if(!new File(cutPath).isDirectory()){
                new File(cutPath).mkdirs();
            }
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
            fixPaths += tempToFixSingle(s) + ",";
        }
        return fixPaths;
    }

    private String tempToFixSingle(String singleTempPath){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String refixStr = "";
        try{
            HttpServletRequest request = getRequest();
            String realPath = request.getSession().getServletContext().getRealPath("/");
            String tempStr = realPath + File.separator + singleTempPath;
            //非临时目录，不移动
            if(tempStr.indexOf(tempDir) == -1){
                return singleTempPath;
            }
            String singleFixName = singleTempPath.substring(singleTempPath.lastIndexOf("/") + 1);
            String singleFixPath = realPath + File.separator + rootDir + File.separator + fixDir;
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
}
