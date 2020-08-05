package com.capitalbio.pdf.model;

import java.util.List;

public class ListModel extends Model {
	String symbol = "● ";
	String size;
	String rise;
	List<Model> models;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getRise() {
		return rise;
	}
	public void setRise(String rise) {
		this.rise = rise;
	}
	
}
