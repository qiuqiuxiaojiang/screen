package com.capitalbio.pdf.model;

import java.util.List;



public class TableModel extends Model {
	RowModel header;
	List<RowModel> rows;
	float width;
	float[] cols;
	int border = -1;
	String borderColor;
	int leading;
	String borderWidth;
	//将表格动态的合并行
	// 例 "1,2,3" 表示  表的第一列第二列第三列的所有行重复的单元格自动合并
	String rowSpanNum;
	
	String spacing;
	String spacingB;
	String z;
	String h;
	//是否为圆角表格
	String isFilletBorder;
	String headerRowNum;//表示 表格头包含几行 默认是1行
	/**
	 * 保持在一页
	 * true   在一页
	 * false  不在一页
	 */
	String keepTogether;
	
	public RowModel getHeader() {
		return header;
	}
	public void setHeader(RowModel header) {
		this.header = header;
	}
	public List<RowModel> getRows() {
		return rows;
	}
	public void setRows(List<RowModel> rows) {
		this.rows = rows;
	}
	
	public int getColNumber() {
		List<CellModel> cells = null;
		if (header != null) {
			cells = header.getCells();
		} else if (rows.size() > 0){
			RowModel row = rows.get(0);
			cells = row.getCells();
		}
		
		if (cells != null) {
			int cols = 0;
			for (CellModel cell:cells) {
				if (cell.getColspan() > 0) {
					cols += cell.getColspan();
				} else {
					cols ++;
				}
			}
			return cols;
		}
		return 0;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float[] getCols() {
		return cols;
	}
	public void setCols(float[] cols) {
		this.cols = cols;
	}
	public int getBorder() {
		return border;
	}
	public void setBorder(int border) {
		this.border = border;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public int getLeading() {
		return leading;
	}
	public void setLeading(int leading) {
		this.leading = leading;
	}
	public String getRowSpanNum() {
		return rowSpanNum;
	}
	public void setRowSpanNum(String rowSpanNum) {
		this.rowSpanNum = rowSpanNum;
	}
	public String getBorderWidth() {
		return borderWidth;
	}
	public void setBorderWidth(String borderWidth) {
		this.borderWidth = borderWidth;
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
	public String getIsFilletBorder() {
		return isFilletBorder;
	}
	public void setIsFilletBorder(String isFilletBorder) {
		this.isFilletBorder = isFilletBorder;
	}
	public String getHeaderRowNum() {
		return headerRowNum;
	}
	public void setHeaderRowNum(String headerRowNum) {
		this.headerRowNum = headerRowNum;
	}
	public String getKeepTogether() {
		return keepTogether;
	}
	public void setKeepTogether(String keepTogether) {
		this.keepTogether = keepTogether;
	}
	
}
