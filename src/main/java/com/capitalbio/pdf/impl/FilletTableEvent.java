package com.capitalbio.pdf.impl;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
/**
 * 表格事件 
 * 对表格边框的颜色 宽度 扩展 以及 四角的弧度大小的设置
 * @author admin
 *
 */
public class FilletTableEvent implements PdfPTableEvent{
	//边框宽度
	public float lineWidth = 0.5f;
	//颜色
	private String color;
	//弧度大小
	public float radian = 8;
	//表格上下左右扩展数值  全为0时保持和表格一样   默认为0
	public float x =0;
	public float y =0;
	public float w =0;
	public float h =0;
	
	//PdfPTable.BASECANVAS----添加到这里的内容会处于表格下面
    //PdfPTable.BACKGROUNDCANVAS----背景色的层
    //PdfPTable.LINECANVAS----线的层
    //PdfPTable.TEXTCANVAS----表格中文本的层，其会位于表格上面
	public int cIndex = PdfPTable.LINECANVAS;
	
	// shading 阴影 border 边框 默认为border
	String type="";
	
	@Override
	public void tableLayout(PdfPTable table, float[][] widths, float[] heights,
			int headerRows, int rowStart, PdfContentByte[] canvases) {
		float[] width = widths[0];
        float x1 = width[0];
        float x2 = width[width.length - 1];
        float y1 = heights[0];
        float y2 = heights[heights.length - 1];
        String[] colorArr = color.split(",");
        if(type.equals("border")){
        	PdfContentByte background = canvases[PdfPTable.LINECANVAS];
        	background.roundRectangle(x1+x, y1+y, x2 - x1+w, y2 - y1+h, radian);
        	background.setLineWidth(lineWidth);
        	if(colorArr.length==3){
        		background.setRGBColorStrokeF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]));
        	}else if(colorArr.length==4){
        		background.setCMYKColorStrokeF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]),Float.parseFloat(colorArr[3]));
        	}
        	background.stroke();
        }else if(type.equals("shading")){
    		PdfContentByte background = canvases[PdfPTable.BASECANVAS];
    	    background.saveState();
    	    background.roundRectangle(x1+x, y1+y, x2 - x1+w, y2 - y1+h, radian);
    	    if(colorArr.length==3){
        		background.setRGBColorFill(Integer.parseInt(colorArr[0]), Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2]));
        	}else if(colorArr.length==4){
        		background.setCMYKColorFillF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]),Float.parseFloat(colorArr[3]));
        	}
    	    background.fill();
    	    background.restoreState();
        }
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}


	public float getRadian() {
		return radian;
	}

	public void setRadian(float radian) {
		this.radian = radian;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public int getcIndex() {
		return cIndex;
	}

	public void setcIndex(int cIndex) {
		this.cIndex = cIndex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	

}
