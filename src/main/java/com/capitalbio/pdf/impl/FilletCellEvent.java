package com.capitalbio.pdf.impl;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
/**
 * 类似于表格的类 未开发完成
 * @author admin
 *
 */
public class FilletCellEvent implements PdfPCellEvent{
	
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
	public int cIndex = -1;
	
	// shading 阴影 border 边框 默认为border
	String type="";
	private static final float PDF_PERCENT = 0.75f;
	@Override
	public void cellLayout(PdfPCell cell, Rectangle position,
		      PdfContentByte[] canvases) {
		String[] colorArr = color.split(",");
        if(type.equals("border")){
        	PdfContentByte background =canvases[PdfPTable.BASECANVAS];
        	if(cIndex != -1){
        		background = canvases[cIndex];
        	}
        	background.roundRectangle(position.getLeft()+x, position.getBottom()+y, position.getWidth()+w, position.getHeight()+h, radian);
        	background.setLineWidth(lineWidth);
        	if(colorArr.length==3){
        		background.setRGBColorStrokeF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]));
        	}else if(colorArr.length==4){
        		background.setCMYKColorStrokeF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]),Float.parseFloat(colorArr[3]));
        	}
        	background.stroke();
        }else if(type.equals("shading")){
        	PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
        	if(cIndex != -1){
        		cb = canvases[cIndex];
        	}
        	cb.roundRectangle(position.getLeft()+x, position.getBottom()+y, position.getWidth()+w, position.getHeight()+h, radian);
        	if(colorArr.length==3){
        		cb.setRGBColorFill(Integer.parseInt(colorArr[0]), Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2]));
        	}else if(colorArr.length==4){
        		cb.setCMYKColorFillF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]),Float.parseFloat(colorArr[3]));
        	}
            cb.fill();
        }else if(type.equals("slantLine")){
        	PdfContentByte cb = canvases[PdfPTable.LINECANVAS];
        	if(cIndex != -1){
        		cb = canvases[cIndex];
        	}
        	cb.moveTo(position.getLeft(), position.getTop());
        	cb.lineTo(position.getLeft() + position.getWidth(), position.getTop() - position.getHeight());
        	if(colorArr.length==3){
        		cb.setRGBColorFill(Integer.parseInt(colorArr[0]), Integer.parseInt(colorArr[1]), Integer.parseInt(colorArr[2]));
        	}else if(colorArr.length==4){
        		cb.setCMYKColorFillF(Float.parseFloat(colorArr[0]), Float.parseFloat(colorArr[1]), Float.parseFloat(colorArr[2]),Float.parseFloat(colorArr[3]));
        	}
        	cb.stroke();
            
        }
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


	public float getLineWidth() {
		return lineWidth;
	}


	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
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
