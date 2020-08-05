package com.capitalbio.pdf.model;

public class ImageModel extends Model {
	String name;
	String type;
	String shape;
	int width;
	int height;
	String z;
	String h;
	float spacing;//段后间距
	float spacingB;//段前间距
	String riskRise;
	String indentLeft;
	String single;//为false时 将图片放到paragraph中 可以控制上下移动
	String bg;//为true时 为writer设置的图片 
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}


	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public String getRiskRise() {
		return riskRise;
	}

	public void setRiskRise(String riskRise) {
		this.riskRise = riskRise;
	}

	public float getSpacingB() {
		return spacingB;
	}

	public void setSpacingB(float spacingB) {
		this.spacingB = spacingB;
	}

	public String getIndentLeft() {
		return indentLeft;
	}

	public void setIndentLeft(String indentLeft) {
		this.indentLeft = indentLeft;
	}

	public String getSingle() {
		return single;
	}

	public void setSingle(String single) {
		this.single = single;
	}

	public String getBg() {
		return bg;
	}

	public void setBg(String bg) {
		this.bg = bg;
	}

	public String getZ() {
		return z;
	}

	public void setZ(String z) {
		this.z = z;
	}

	public String getH() {
		return h;
	}

	public void setH(String h) {
		this.h = h;
	}
	
	
}
