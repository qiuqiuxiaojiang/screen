package com.capitalbio.pdf.model;

import java.util.List;




public class HeaderModel extends Model {
	List<HeaderItemModel> items;
	int begin;
	//even 为true时 解决目录页奇偶页眉页脚乱的问题
	String Even;
	String substring;
	String itemName;
	String beginName;//表示从第几chapter开始显示页眉页脚 总页数同时改变（不再去管 第几页 而是从chapter开始）  beginName为chapter的name属性 
	
	String start;//表示从第几页开始有页眉页脚          begin属性和itemName结合使用于横版也报告
	
	public List<HeaderItemModel> getItems() {
		return items;
	}

	public void setItems(List<HeaderItemModel> items) {
		this.items = items;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public String getEven() {
		return Even;
	}

	public void setEven(String even) {
		Even = even;
	}

	public String getSubstring() {
		return substring;
	}

	public void setSubstring(String substring) {
		this.substring = substring;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getBeginName() {
		return beginName;
	}

	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}
	 
}
