package com.capitalbio.common.util;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>Title: ImageUtil </p>
 * <p>Description: </p>
 * <p>Email: icerainsoft@hotmail.com </p> 
 * @author Ares
 * @date 2014年10月28日 上午10:24:26
 */
public class ImageUtil {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    private static String DEFAULT_THUMB_PREVFIX = "thumb_";
    private static String DEFAULT_CUT_PREVFIX = "cut_";
    private static Boolean DEFAULT_FORCE = false;
    
    
    /**
     * <p>Title: cutImage</p>
     * <p>Description:  根据原图与裁切size截取局部图片</p>
     * @param srcImg    源图片
     * @param output    图片输出流
     * @param rect        需要截取部分的坐标和大小
     */
    public void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect){
        if(srcImg.exists()){
            java.io.FileInputStream fis = null;
            ImageInputStream iis = null;
            try {
                fis = new FileInputStream(srcImg);
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;
                // 获取图片后缀
                if(srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()+",") < 0){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return ;
                }
                // 将FileInputStream 转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);
                // 根据图片类型获取该种类型的ImageReader
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(iis,true);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0, param);
                ImageIO.write(bi, suffix, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fis != null) fis.close();
                    if(iis != null) iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            log.warn("the src image is not exist.");
        }
    }
    
    public void cutImage(File srcImg, OutputStream output, int x, int y, int width, int height){
        cutImage(srcImg, output, new java.awt.Rectangle(x, y, width, height));
    }
    
    public void cutImage(File srcImg, String destImgPath, java.awt.Rectangle rect){
        File destImg = new File(destImgPath);
        if(destImg.exists()){
            String p = destImg.getPath();
            try {
                if(!destImg.isDirectory()) p = destImg.getParent();
                if(!p.endsWith(File.separator)) p = p + File.separator;
                cutImage(srcImg, new java.io.FileOutputStream(p + DEFAULT_CUT_PREVFIX + "_" + new java.util.Date().getTime() + "_" + srcImg.getName()), rect);
            } catch (FileNotFoundException e) {
                log.warn("the dest image is not exist.");
            }
        }else log.warn("the dest image folder is not exist.");
    }
    
    public void cutImage(File srcImg, String destImg, int x, int y, int width, int height){
        cutImage(srcImg, destImg, new java.awt.Rectangle(x, y, width, height));
    }
    
    public void cutImage(String srcImg, String destImg, int x, int y, int width, int height){
        cutImage(new File(srcImg), destImg, new java.awt.Rectangle(x, y, width, height));
    }
    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 根据图片路径生成缩略图 </p>
     * @param imagePath    原图片路径
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param prevfix    生成缩略图的前缀
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public void thumbnailImage(File srcImg, OutputStream output, int w, int h, String prevfix, boolean force){
        if(srcImg.exists()){
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;
                // 获取图片后缀
                if(srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()+",") < 0){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return ;
                }
                log.debug("target image's size, width:{}, height:{}.",w,h);
                Image img = ImageIO.read(srcImg);
                // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                if(!force){
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if((width*1.0)/w < (height*1.0)/h){
                        if(width > w){
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w/(width*1.0)));
                            log.debug("change image's height, width:{}, height:{}.",w,h);
                        }
                    } else {
                        if(height > h){
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h/(height*1.0)));
                            log.debug("change image's width, width:{}, height:{}.",w,h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                // 将图片保存在原目录并加上前缀
                ImageIO.write(bi, suffix, output);
                output.close();
            } catch (IOException e) {
               log.error("generate thumbnail image failed.",e);
            }
        }else{
            log.warn("the src image is not exist.");
        }
    }
    public void thumbnailImage(File srcImg, int w, int h, String prevfix, boolean force){
        String p = srcImg.getAbsolutePath();
        try {
            if(!srcImg.isDirectory()) p = srcImg.getParent();
            if(!p.endsWith(File.separator)) p = p + File.separator;
            thumbnailImage(srcImg, new java.io.FileOutputStream(p + prevfix +srcImg.getName()), w, h, prevfix, force);
        } catch (FileNotFoundException e) {
            log.error("the dest image is not exist.",e);
        }
    }
    
    public void thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force){
        File srcImg = new File(imagePath);
        thumbnailImage(srcImg, w, h, prevfix, force);
    }
    
    public void thumbnailImage(String imagePath, int w, int h, boolean force){
        thumbnailImage(imagePath, w, h, DEFAULT_THUMB_PREVFIX, DEFAULT_FORCE);
    }
    
    public void thumbnailImage(String imagePath, int w, int h){
        thumbnailImage(imagePath, w, h, DEFAULT_FORCE);
    }
    
    public static byte[] thumbnailImage(InputStream input, int w, int h, boolean force) {
    	try {
        	BufferedImage img = ImageIO.read(input);
            if(!force){
                int width = img.getWidth();
                int height = img.getHeight();
                if((width*1.0)/w < (height*1.0)/h){
                    if(width > w){
                        h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w/(width*1.0)));
                    }
                } else {
                    if(height > h){
                        w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h/(height*1.0)));
                    }
                }
            }
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
            g.dispose();
            // 将图片保存在原目录并加上前缀
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi, "JPEG", out);
            return out.toByteArray();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    
    //压缩图片
    public static BufferedImage rize(BufferedImage srcBufImage, int width, int height){
        BufferedImage bufTarget = null;
        int type = srcBufImage.getType();
        double sx = (double) width / srcBufImage.getWidth();
        double sy = (double) height / srcBufImage.getHeight();
        if (type == BufferedImage.TYPE_CUSTOM){
            ColorModel cm = srcBufImage.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            bufTarget = new BufferedImage(cm, raster, alphaPremultiplied, null);
        }
        else{
            bufTarget = new BufferedImage(width, height, type);
        }
        Graphics2D g = bufTarget.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(srcBufImage, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return bufTarget;
    }
    
    public static void main(String[] args) {
        new ImageUtil().thumbnailImage("imgs/Tulips.jpg", 150, 100);
        new ImageUtil().cutImage("imgs/Tulips.jpg","imgs", 250, 70, 300, 400);
    }
    
	/**
     * 截取图片
     * @param imageFile 原图文件
     * @param x    截取时的x坐标
     * @param y    截取时的y坐标
     * @param desWidth   截取的宽度
     * @param desHeight   截取的高度
     * @param _H 等比缩放后图片高度
     * @param _W 等比缩放后图片宽度
     * @return byte[] 截取后图片字节数组
     */
    public static byte[] imgCut(MultipartFile imageFile, int x, int y, int desWidth, int desHeight, int _H, int _W) {
    	ByteArrayOutputStream out = null;
        try {
            Image img;
            ImageFilter cropFilter;
            
            //读取原图像
            BufferedImage bi = ImageIO.read(imageFile.getInputStream());
            int srcWidth = bi.getWidth(); // 原图宽度
            int srcHeight = bi.getHeight(); // 原图高度
           // System.out.println("srcwidth:"+srcWidth+" srcHeight:"+srcHeight);
            //若原图大小大于切片大小 ，则进行切割
            if (srcWidth >= desWidth && srcHeight >= desHeight) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight,Image.SCALE_DEFAULT);
                int x1 = x*srcWidth/_W;
                int y1 = y*srcHeight/_H;
                int w1 = desWidth*srcWidth/_W;
                int h1 = desHeight*srcHeight/_H;
                
                cropFilter = new CropImageFilter(x1, y1, w1, h1);
                img = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(image.getSource(), cropFilter));
                BufferedImage tag = new BufferedImage(w1, h1,
                        BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, null); //绘制缩小后的图
                g.dispose();
                
                out = new ByteArrayOutputStream();
                ImageIO.write(tag, "JPEG", out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
    
    //byte数组到图片
    public static Image byte2image(byte[] data){
    	 Image image = Toolkit.getDefaultToolkit().createImage(data);
		  try {
		  MediaTracker mt = new MediaTracker(new Label());
		  mt.addImage(image, 0);
		  mt.waitForAll();
		  } catch (InterruptedException e) {
		  e.printStackTrace();
		  }

		  return image;
	}


}