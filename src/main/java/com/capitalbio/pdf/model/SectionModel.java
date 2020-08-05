package com.capitalbio.pdf.model;

import java.util.List;

import com.capitalbio.pdf.StyleUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class SectionModel extends Model {
	String sectionName;
	int sectionNumber;
	String fontName;
	String fontSize;
	String color;
	List<Model> models;
	int depth;
	boolean isNewPage;
	String isNumber;
	
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String chapterName) {
		this.sectionName = chapterName;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public int getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(int chapterNumber) {
		this.sectionNumber = chapterNumber;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
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
	public boolean isNewPage() {
		return isNewPage;
	}
	public void setNewPage(boolean isNewPage) {
		this.isNewPage = isNewPage;
	}
	public String getIsNumber() {
		return isNumber;
	}
	public void setIsNumber(String isNumber) {
		this.isNumber = isNumber;
	}
	
}
