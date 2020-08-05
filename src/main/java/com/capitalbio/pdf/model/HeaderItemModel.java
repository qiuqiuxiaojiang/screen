package com.capitalbio.pdf.model;


public class HeaderItemModel extends Model {
	String text;
	float pos;
	float x;
	float y;
	String link;
	String top;
	ImageModel imageModel;
	int page;
	float width;
	float height;
	//奇偶
	float z;
	float h;
	String isEven;
	String itemName;//赋值为chapter的name  为这个chapter设置不同的页眉页脚
	String sup;
	String chapterStar;//true 跳过章节开头 false 只出现在章节开头
	String chapterEnd;//true 跳过章节结束 false 只出现在章节结束
	String startPage;//int型 从该页开始出现
	String exceptChapterName;//以英文逗号分隔 每个元素为chapter的name属性 代表跳过该chapter
	String cludeChapterName;//以英文逗号分隔 每个元素为chapter的name属性 代表z只在该chapter出现
	String pageNumStr;//以英文逗号分隔 每个元素为当前chapter的页码  代表只在该chapter的第几页出现
	String isBG;//writer 有两个对象 getDirectContent()和getDirectContentUnder() 上下两层对象 
				// 为true时 设为下层对象getDirectContentUnder()   不设或者为false时 为上层对象getDirectContent()
	String startNum;
	String lastPage;
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getPos() {
		return pos;
	}
	public void setPos(float pos) {
		this.pos = pos;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public ImageModel getImageModel() {
		return imageModel;
	}
	public void setImageModel(ImageModel imageModel) {
		this.imageModel = imageModel;
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
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public float getWidth() {
		return width;
	}
	public void setWidth(float width) {
		this.width = width;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getZ() {
		return z;
	}
	public void setZ(float z) {
		this.z = z;
	}
	public float getH() {
		return h;
	}
	public void setH(float h) {
		this.h = h;
	}
	public String getIsEven() {
		return isEven;
	}
	public void setIsEven(String isEven) {
		this.isEven = isEven;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getSup() {
		return sup;
	}
	public void setSup(String sup) {
		this.sup = sup;
	}
	public String getChapterStar() {
		return chapterStar;
	}
	public void setChapterStar(String chapterStar) {
		this.chapterStar = chapterStar;
	}
	public String getStartPage() {
		return startPage;
	}
	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	public String getExceptChapterName() {
		return exceptChapterName;
	}
	public void setExceptChapterName(String exceptChapterName) {
		this.exceptChapterName = exceptChapterName;
	}
	public String getChapterEnd() {
		return chapterEnd;
	}
	public void setChapterEnd(String chapterEnd) {
		this.chapterEnd = chapterEnd;
	}
	public String getCludeChapterName() {
		return cludeChapterName;
	}
	public void setCludeChapterName(String cludeChapterName) {
		this.cludeChapterName = cludeChapterName;
	}
	public String getPageNumStr() {
		return pageNumStr;
	}
	public void setPageNumStr(String pageNumStr) {
		this.pageNumStr = pageNumStr;
	}
	public String getIsBG() {
		return isBG;
	}
	public void setIsBG(String isBG) {
		this.isBG = isBG;
	}
	public String getStartNum() {
		return startNum;
	}
	public void setStartNum(String startNum) {
		this.startNum = startNum;
	}
	public String getLastPage() {
		return lastPage;
	}
	public void setLastPage(String lastPage) {
		this.lastPage = lastPage;
	}
	
	
}
