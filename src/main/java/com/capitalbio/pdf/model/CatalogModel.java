package com.capitalbio.pdf.model;

import java.util.List;

import com.capitalbio.pdf.StyleUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

public class CatalogModel extends Model {
	String name;
	String fontName;
	String fontSize;
	String color;
	List<Model> models;
	List<RefModel> refList;
	float widPer;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RefModel> getRefList() {
		return refList;
	}

	public void setRefList(List<RefModel> refList) {
		this.refList = refList;
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

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public float getWidPer() {
		return widPer;
	}

	public void setWidPer(float widPer) {
		this.widPer = widPer;
	}
    
	
}
