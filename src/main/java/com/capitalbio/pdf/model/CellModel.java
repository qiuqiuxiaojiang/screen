package com.capitalbio.pdf.model;

import java.util.List;

public class CellModel extends Model {
	String text;
	TextModel textModel;
	ImageModel imageModel;
	TableModel tableModel;
	ComplexModel complexModel;
	ListModel listModel;
	int colspan;
	int rowspan;
	int border = -1;
	int Leading;
	int padding;
	String onlyPTB;
	String vAlign;
	String paddingTop;
	String paddingBottom;
	String paddingLeft;
	String paddingRight;
	List<Model> models;
	//是否是新版本的cell，新版cell 保留一个单元格下的多个text存在，由于之前旧版本的cell太多，修改旧版本的工作量太大，所以暂时使用版本来分别
	String isNew;
	//值为true时 这个文本被text包裹时  在表格中就取消上下边距 
	String singleHeight;
	
	String isShading;
	
	
	//下面这两个属性正在开发测试中，暂时不能用。
	String borderColorTop;
	String borderColorBottom;
	String borderColorLeft;
	String borderColorRight;
	String borderColor;
	
	//单元格中文字的行间距 适应complex等
	//分两个元素 前一个是固定间距 后一个是增加间距 
	//暂时使用15,0 前一个设置为15 后一个设置为0
	String fixedAndMultipliedLeading;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public TextModel getTextModel() {
		return textModel;
	}
	public void setTextModel(TextModel textModel) {
		this.textModel = textModel;
	}
	public ImageModel getImageModel() {
		return imageModel;
	}
	public void setImageModel(ImageModel imageModel) {
		this.imageModel = imageModel;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public TableModel getTableModel() {
		return tableModel;
	}
	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}
	public int getColspan() {
		return colspan;
	}
	public int getBorder() {
		return border;
	}
	public void setBorder(int border) {
		this.border = border;
	}
	public int getRowspan() {
		return rowspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	public ComplexModel getComplexModel() {
		return complexModel;
	}
	public void setComplexModel(ComplexModel complexModel) {
		this.complexModel = complexModel;
	}
	public ListModel getListModel() {
		return listModel;
	}
	public void setListModel(ListModel listModel) {
		this.listModel = listModel;
	}
	public int getLeading() {
		return Leading;
	}
	public void setLeading(int leading) {
		Leading = leading;
	}
	public int getPadding() {
		return padding;
	}
	public void setPadding(int padding) {
		this.padding = padding;
	}
	public String getBorderColorTop() {
		return borderColorTop;
	}
	public void setBorderColorTop(String borderColorTop) {
		this.borderColorTop = borderColorTop;
	}
	public String getBorderColorBottom() {
		return borderColorBottom;
	}
	public void setBorderColorBottom(String borderColorBottom) {
		this.borderColorBottom = borderColorBottom;
	}
	public String getvAlign() {
		return vAlign;
	}
	public void setvAlign(String vAlign) {
		this.vAlign = vAlign;
	}
	public String getSingleHeight() {
		return singleHeight;
	}
	public void setSingleHeight(String singleHeight) {
		this.singleHeight = singleHeight;
	}
	public String getPaddingTop() {
		return paddingTop;
	}
	public void setPaddingTop(String paddingTop) {
		this.paddingTop = paddingTop;
	}
	public String getPaddingBottom() {
		return paddingBottom;
	}
	public void setPaddingBottom(String paddingBottom) {
		this.paddingBottom = paddingBottom;
	}
	public String getPaddingLeft() {
		return paddingLeft;
	}
	public void setPaddingLeft(String paddingLeft) {
		this.paddingLeft = paddingLeft;
	}
	public String getPaddingRight() {
		return paddingRight;
	}
	public void setPaddingRight(String paddingRight) {
		this.paddingRight = paddingRight;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public String getIsNew() {
		return isNew;
	}
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
	public String getBorderColorLeft() {
		return borderColorLeft;
	}
	public void setBorderColorLeft(String borderColorLeft) {
		this.borderColorLeft = borderColorLeft;
	}
	public String getBorderColorRight() {
		return borderColorRight;
	}
	public void setBorderColorRight(String borderColorRight) {
		this.borderColorRight = borderColorRight;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public String getIsShading() {
		return isShading;
	}
	public void setIsShading(String isShading) {
		this.isShading = isShading;
	}
	public String getFixedAndMultipliedLeading() {
		return fixedAndMultipliedLeading;
	}
	public void setFixedAndMultipliedLeading(String fixedAndMultipliedLeading) {
		this.fixedAndMultipliedLeading = fixedAndMultipliedLeading;
	}
	
}
