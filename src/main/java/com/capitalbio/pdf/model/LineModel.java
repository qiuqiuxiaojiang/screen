package com.capitalbio.pdf.model;

public class LineModel extends Model{

	String width;
	String percentage;
	String offset;
	/**
	 * true代表虚线 没有或者false代表直线
	 */
	String dotted;
	
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getPercentage() {
		return percentage;
	}
	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public String getDotted() {
		return dotted;
	}
	public void setDotted(String dotted) {
		this.dotted = dotted;
	}
	
	
	
}
