package com.crazy.imgManager.service;

/**
 * Created by pcg on 16/3/14.
 */
public interface ImgService {

    /**
     *
     * @param
     * @param x
     * @param y
     * @param w
     * @param h
     * @param compressWid 裁剪后的宽度
     * @return
     */

    public String uploadToTemp(String x,String y,String w,String h,String compressWid,String suffix,byte[] fileBytes);

    public String tempToFix(String tempPath);

    public String testHessian(String input,byte[] fileBytes);

    public String uploadToFix(String projectName,String suffix,byte[] fileBytes);

}
