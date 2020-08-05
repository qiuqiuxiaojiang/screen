package com.capitalbio.pdf;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;

public class StyleUtils {
	public static int getAlign(String align) {
		if (align==null) {
			return Element.ALIGN_LEFT;
		}
		if ("left".equals(align)) {
			return Element.ALIGN_LEFT;
		} else if ("center".equals(align)) {
			return Element.ALIGN_CENTER;
		} else if ("right".equals(align)) {
			return Element.ALIGN_RIGHT;
		}
		return Element.ALIGN_LEFT;
	}
	public static BaseColor getColor(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		String[] colors = str.split(",");
		if (colors.length != 3 && colors.length != 4) {
			return null;
		}
		try {
			
			if(colors.length == 3){
				//RGB颜色
				int red = Integer.parseInt(colors[0]);
				int green = Integer.parseInt(colors[1]);
				int blue = Integer.parseInt(colors[2]);
				return new BaseColor(red, green, blue);
			}else {
				//CMYK颜色  intCyan,intMagenta,intYellow,intBlack
				float floatCyan = Float.parseFloat(colors[0]);
				float floatMagenta = Float.parseFloat(colors[1]);
				float floatYellow = Float.parseFloat(colors[2]);
				float floatBlack = Float.parseFloat(colors[3]);
				CMYKColor cmykColor = new CMYKColor(floatCyan, floatMagenta, floatYellow, floatBlack);
				return cmykColor;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Font getChapterFont() {
		Font font = getDefaultFont();
		font.setSize(20);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	public static Font getSectionFont() {
		Font font = getDefaultFont();
		font.setStyle(Font.BOLD);
		font.setSize(16);
		return font;
	}
	
	public static Font getDefaultFont() {
		Font font = null;
		try {
			BaseFont bfChinese = BaseFont.createFont("/font/simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);     
			font= new Font(bfChinese); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (font == null ) {
			font = FontFactory.getFont(FontFactory.HELVETICA);
		}
		
		font.setSize(10);
		return font;
	}
	
	public static Font getFont(String fontName){
		if (fontName == null) {
			return getDefaultFont();
		}
		try {
			if ("en".equals(fontName)) {
				Font font = FontFactory.getFont(FontFactory.HELVETICA);
				return font;
			} else if (fontName.equals("song")) {
				BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.NOT_EMBEDDED); 
				Font font= new Font(bfChinese);
				return font;
			} else  {
				String fontFileName = "/font/"+fontName+".ttf";
				BaseFont baseFont = BaseFont.createFont(fontFileName,BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);     
				Font font = new Font(baseFont);  
				return font;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getDefaultFont();
	}
	public static BaseFont getBaseFont(String fontName,int fontSize, BaseColor color){
		String fontFileName ="/font/msyh.ttf";
		try {
			if(StringUtils.isNotEmpty(fontFileName)){
				fontFileName = "/font/"+fontName+".ttf";
			}
			BaseFont baseFont = BaseFont.createFont(fontFileName,BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);     
			return baseFont;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	public static Font getFont(int fontSize, BaseColor color) {
		Font font = getDefaultFont();
		font.setSize(fontSize);
		font.setColor(color);
		return font;
		
	}
	
	public static float getIndent(String indent) {
		try {
			float fvalue = Float.parseFloat(indent);
			return fvalue;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int getFontStyle(String style) {
		if ("bold".equals(style)) {
			return Font.BOLD;
		} else if ("italic".equals(style)) {
			return Font.ITALIC;
		} else if ("bolditalic".equals(style)) {
			return Font.BOLDITALIC;
		} else if ("underline".equals(style)) {
			return Font.UNDERLINE;
		} else {
			return Font.NORMAL;
		}
	}

}
