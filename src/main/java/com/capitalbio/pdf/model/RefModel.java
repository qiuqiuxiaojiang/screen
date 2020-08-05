package com.capitalbio.pdf.model;

public class RefModel extends Model{
	String name;
	int pageNumber;
	ImageModel image;
	int depth;
	int fixheight;
	String isSplitting;
	float refSpacing;
	float widthPer;
	int begin;
	String isAll;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public ImageModel getImage() {
		return image;
	}

	public void setImage(ImageModel image) {
		this.image = image;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getFixheight() {
		return fixheight;
	}

	public void setFixheight(int fixheight) {
		this.fixheight = fixheight;
	}

	public String getIsSplitting() {
		return isSplitting;
	}

	public void setIsSplitting(String isSplitting) {
		this.isSplitting = isSplitting;
	}

	public float getRefSpacing() {
		return refSpacing;
	}

	public void setRefSpacing(float refSpacing) {
		this.refSpacing = refSpacing;
	}

	public float getWidthPer() {
		return widthPer;
	}

	public void setWidthPer(float widthPer) {
		this.widthPer = widthPer;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public String getIsAll() {
		return isAll;
	}

	public void setIsAll(String isAll) {
		this.isAll = isAll;
	}
	
}
