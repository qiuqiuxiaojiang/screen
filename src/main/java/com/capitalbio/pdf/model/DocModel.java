package com.capitalbio.pdf.model;

import java.util.List;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;

public class DocModel {
//	Rectangle pageSize = new Rectangle(36, 54, 0, 788);
	Rectangle pageSize = PageSize.A4;
	String margin = "36,36,80,60";
	String leftMargin = "";
	String rightMargin = "";
	List<Model> models;
//	CatalogModel catalog;
	HeaderModel header;
	
	String getPagechapterName="";//要取某一页的章节的name
	String notDiplayBookMarksChapter;//包含chaper的name属性以英文逗号分隔，表示该chapter不在pdf书签显示
	
	public Rectangle getPageSize() {
		return pageSize;
	}
	public void setPageSize(Rectangle pageSize) {
		this.pageSize = pageSize;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
//	public CatalogModel getCatalog() {
//		return catalog;
//	}
//	public void setCatalog(CatalogModel catalog) {
//		this.catalog = catalog;
//	}
	public HeaderModel getHeader() {
		return header;
	}
	public void setHeader(HeaderModel header) {
		this.header = header;
	}
	
	public String getMargin() {
		return margin;
	}
	public void setMargin(String margin) {
		this.margin = margin;
	}
	public String getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(String leftMargin) {
		this.leftMargin = leftMargin;
	}
	public String getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(String rightMargin) {
		this.rightMargin = rightMargin;
	}
	public float[] getMargins() {
		float[] margins = new float[]{36f,36f, 80f, 60f};
		if (margin == null || margin.length() == 0) {
			return margins;
		}
		String[] strs = margin.split(",");
		if (strs.length != 4) {
			return margins;
		}
		try {
			margins[0] = Float.parseFloat(strs[0]);
		} catch (Exception e){}
		try {
			margins[1] = Float.parseFloat(strs[1]);
		} catch (Exception e){}
		try {
			margins[2] = Float.parseFloat(strs[2]);
		} catch (Exception e){}
		try {
			margins[3] = Float.parseFloat(strs[3]);
		} catch (Exception e){}
		return margins;
	}
	public float[] getleftMargins() {
		
		if (leftMargin == null || leftMargin.length() == 0) {
			return null;
		}
		String[] strs = leftMargin.split(",");
		if (strs.length != 4) {
			return null;
		}
		float[] margins = new float[4];
		try {
			margins[0] = Float.parseFloat(strs[0]);
		} catch (Exception e){}
		try {
			margins[1] = Float.parseFloat(strs[1]);
		} catch (Exception e){}
		try {
			margins[2] = Float.parseFloat(strs[2]);
		} catch (Exception e){}
		try {
			margins[3] = Float.parseFloat(strs[3]);
		} catch (Exception e){}
		return margins;
	}
	public float[] getrightMargins() {
		
		if (rightMargin == null || rightMargin.length() == 0) {
			return null;
		}
		String[] strs = rightMargin.split(",");
		if (strs.length != 4) {
			return null;
		}
		float[] margins = new float[4];
		try {
			margins[0] = Float.parseFloat(strs[0]);
		} catch (Exception e){}
		try {
			margins[1] = Float.parseFloat(strs[1]);
		} catch (Exception e){}
		try {
			margins[2] = Float.parseFloat(strs[2]);
		} catch (Exception e){}
		try {
			margins[3] = Float.parseFloat(strs[3]);
		} catch (Exception e){}
		return margins;
	}
	public String getGetPagechapterName() {
		return getPagechapterName;
	}
	public void setGetPagechapterName(String getPagechapterName) {
		this.getPagechapterName = getPagechapterName;
	}
	public String getNotDiplayBookMarksChapter() {
		return notDiplayBookMarksChapter;
	}
	public void setNotDiplayBookMarksChapter(String notDiplayBookMarksChapter) {
		this.notDiplayBookMarksChapter = notDiplayBookMarksChapter;
	}
	
	
}
