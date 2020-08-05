package com.capitalbio.pdf.model;

import java.util.List;

import com.capitalbio.pdf.StyleUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;



public class ChapterModel extends Model {
	String chapterName;
	int chapterNumber;
	String fontName;
	String fontSize;
	String color;
	List<Model> models;
	String isNewP;
	String spacingAfter;
	String hengBan;
	String mtop="";
	String mbottom="";
	String mleft="";
	String mright="";
	String realPage="";
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public int getChapterNumber() {
		return chapterNumber;
	}
	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}
	public Font getFont() {
		Font font = StyleUtils.getFont(fontName);
		if (color != null) {
			BaseColor baseColor = StyleUtils.getColor(color);
			if (baseColor != null) {
				font.setColor(baseColor);
			}
		}
		
		if (fontSize != null) {
			try {
				float size = Float.parseFloat(fontSize);
				font.setSize(size);
			} catch (Exception e) {}
		}
		font.setStyle(Font.BOLD);
		return font;
	}
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getIsNewP() {
		return isNewP;
	}
	public void setIsNewP(String isNewP) {
		this.isNewP = isNewP;
	}
	public String getSpacingAfter() {
		return spacingAfter;
	}
	public void setSpacingAfter(String spacingAfter) {
		this.spacingAfter = spacingAfter;
	}
	public String getHengBan() {
		return hengBan;
	}
	public void setHengBan(String hengBan) {
		this.hengBan = hengBan;
	}
	public String getMtop() {
		return mtop;
	}
	public void setMtop(String mtop) {
		this.mtop = mtop;
	}
	public String getMbottom() {
		return mbottom;
	}
	public void setMbottom(String mbottom) {
		this.mbottom = mbottom;
	}
	public String getMleft() {
		return mleft;
	}
	public void setMleft(String mleft) {
		this.mleft = mleft;
	}
	public String getMright() {
		return mright;
	}
	public void setMright(String mright) {
		this.mright = mright;
	}
	public String getRealPage() {
		return realPage;
	}
	public void setRealPage(String realPage) {
		this.realPage = realPage;
	}
	
}
