package com.capitalbio.pdf.model;

import java.util.List;

public class RowModel extends Model {
	List<CellModel> cells;
	float height;

	public List<CellModel> getCells() {
		return cells;
	}

	public void setCells(List<CellModel> cells) {
		this.cells = cells;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	
}
