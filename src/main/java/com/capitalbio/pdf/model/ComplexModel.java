package com.capitalbio.pdf.model;

import java.util.List;


public class ComplexModel extends Model{
	List<Model> models;
	int height;
	String leading;
	String spacing;
	String spacingB;
	String indentationLeft;
	String indentationRight;

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLeading() {
		return leading;
	}

	public void setLeading(String leading) {
		this.leading = leading;
	}

	public String getSpacing() {
		return spacing;
	}

	public void setSpacing(String spacing) {
		this.spacing = spacing;
	}

	public String getSpacingB() {
		return spacingB;
	}

	public void setSpacingB(String spacingB) {
		this.spacingB = spacingB;
	}

	public String getIndentationLeft() {
		return indentationLeft;
	}

	public void setIndentationLeft(String indentationLeft) {
		this.indentationLeft = indentationLeft;
	}

	public String getIndentationRight() {
		return indentationRight;
	}

	public void setIndentationRight(String indentationRight) {
		this.indentationRight = indentationRight;
	}
	
	
	
}
