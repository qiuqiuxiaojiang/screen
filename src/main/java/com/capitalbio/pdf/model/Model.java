package com.capitalbio.pdf.model;

import com.capitalbio.pdf.StyleUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;



public class Model {
	String id;
	String name;
	String fontName;
	String fontSize;
	String align;
	String color;
	String bgcolor;
	String indent;
	String style;
	String beginNum;
	String numericType;//设置数字类型 这是暂时可以设置为 罗马字体 阿拉伯字体
	String beginPageNum;//设置目录中页码 从第几页开始 
	String numModel;//为1时  当text为数字时   个位数前加0 
	
	//圆角边框属性
	//表格边框属性
	String filletBorderLineWidth;
	//颜色RGB值
	String filletBorderColor;
	//弧度大小
	String filletBorderRadian;
	//表格上下左右扩展数值  全为0时保持和表格一样   默认为0
	String filletBorderX;
	String filletBorderY;
	String filletBorderW;
	String filletBorderH;
	//PdfPTable.BASECANVAS----添加到这里的内容会处于表格下面
    //PdfPTable.BACKGROUNDCANVAS----背景色的层
    //PdfPTable.LINECANVAS----线的层
    //PdfPTable.TEXTCANVAS----表格中文本的层，其会位于表格上面
	String filletCIndex ;
	
	//圆角背景颜色属性
	//颜色RGB值
	String shadingColor;
	//弧度大小
	String shadingRadian;
	//表格上下左右扩展数值  全为0时保持和表格一样   默认为0
	String shadingX;
	String shadingY;
	String shadingW;
	String shadingH;
	//PdfPTable.BASECANVAS----添加到这里的内容会处于表格下面
	//PdfPTable.BACKGROUNDCANVAS----背景色的层
	//PdfPTable.LINECANVAS----线的层
	//PdfPTable.TEXTCANVAS----表格中文本的层，其会位于表格上面
	String shadingCIndex ;
	
	//表格或者单元格事件 1.shading背景 2.border边框 3.shading,border背景加边框 
	String eventType = "";
	
//	String keepTogether;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBgcolor() {
		return bgcolor;
	}
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	
	public String getIndent() {
		return indent;
	}
	public void setIndent(String indent) {
		this.indent = indent;
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
		
		if (style != null) {
			font.setStyle(StyleUtils.getFontStyle(style));
		}
		return font;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getBeginNum() {
		return beginNum;
	}
	public void setBeginNum(String beginNum) {
		this.beginNum = beginNum;
	}
	public String getNumericType() {
		return numericType;
	}
	public void setNumericType(String numericType) {
		this.numericType = numericType;
	}
	public String getFilletBorderColor() {
		return filletBorderColor;
	}
	public void setFilletBorderColor(String filletBorderColor) {
		this.filletBorderColor = filletBorderColor;
	}
	public String getFilletBorderLineWidth() {
		return filletBorderLineWidth;
	}
	public void setFilletBorderLineWidth(String filletBorderLineWidth) {
		this.filletBorderLineWidth = filletBorderLineWidth;
	}
	public String getFilletBorderRadian() {
		return filletBorderRadian;
	}
	public void setFilletBorderRadian(String filletBorderRadian) {
		this.filletBorderRadian = filletBorderRadian;
	}
	public String getFilletBorderX() {
		return filletBorderX;
	}
	public void setFilletBorderX(String filletBorderX) {
		this.filletBorderX = filletBorderX;
	}
	public String getFilletBorderY() {
		return filletBorderY;
	}
	public void setFilletBorderY(String filletBorderY) {
		this.filletBorderY = filletBorderY;
	}
	public String getFilletBorderW() {
		return filletBorderW;
	}
	public void setFilletBorderW(String filletBorderW) {
		this.filletBorderW = filletBorderW;
	}
	public String getFilletBorderH() {
		return filletBorderH;
	}
	public void setFilletBorderH(String filletBorderH) {
		this.filletBorderH = filletBorderH;
	}
	public String getFilletCIndex() {
		return filletCIndex;
	}
	public void setFilletCIndex(String filletCIndex) {
		this.filletCIndex = filletCIndex;
	}
	public String getShadingColor() {
		return shadingColor;
	}
	public void setShadingColor(String shadingColor) {
		this.shadingColor = shadingColor;
	}
	public String getShadingRadian() {
		return shadingRadian;
	}
	public void setShadingRadian(String shadingRadian) {
		this.shadingRadian = shadingRadian;
	}
	public String getShadingX() {
		return shadingX;
	}
	public void setShadingX(String shadingX) {
		this.shadingX = shadingX;
	}
	public String getShadingY() {
		return shadingY;
	}
	public void setShadingY(String shadingY) {
		this.shadingY = shadingY;
	}
	public String getShadingW() {
		return shadingW;
	}
	public void setShadingW(String shadingW) {
		this.shadingW = shadingW;
	}
	public String getShadingH() {
		return shadingH;
	}
	public void setShadingH(String shadingH) {
		this.shadingH = shadingH;
	}
	public String getShadingCIndex() {
		return shadingCIndex;
	}
	public void setShadingCIndex(String shadingCIndex) {
		this.shadingCIndex = shadingCIndex;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBeginPageNum() {
		return beginPageNum;
	}
	public void setBeginPageNum(String beginPageNum) {
		this.beginPageNum = beginPageNum;
	}
	public String getNumModel() {
		return numModel;
	}
	public void setNumModel(String numModel) {
		this.numModel = numModel;
	}
	
	
//	public String getKeepTogether() {
//		return keepTogether;
//	}
//	public void setKeepTogether(String keepTogether) {
//		this.keepTogether = keepTogether;
//	}

	
	
	
}
