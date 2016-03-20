package com.crazy.imgManager.common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

/**
 * Created by pcg on 16/3/3.
 */
public class ImgUtils {

    public static void imgCut(String srcImgPath,String cutPath,int x,int y,int width,int height,int compressWid){

        try {
            Image img;
            ImageFilter cropFilter;
            BufferedImage bi = ImageIO.read(new File(srcImgPath));
            int srcWidth = bi.getWidth();
            int srcHeight = bi.getHeight();

            double scale = Double.valueOf(srcWidth)/Double.valueOf(compressWid);
            double fw = scale*width;
            double fh = scale*height;
            double fx = scale*x;
            double fy = scale*y;
            width = (int)fw;
            height = (int)fh;
            x = (int)fx;
            y = (int)fy;

            if(srcWidth >= width && srcHeight >= height){
                Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
                cropFilter = new CropImageFilter(x, y, width, height);
                img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
                BufferedImage tag = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

                Graphics g = tag.getGraphics();
                g.drawImage(img,0,0,null);
                g.dispose();

                ImageIO.write(tag,"JPEG",new File(cutPath));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
