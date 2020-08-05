package com.capitalbio.pdf.model;

public class TextModel extends Model {
	String text;
	String sup;//上标
	String sub;//下标
	float spacing;//段后间距
	float spacingB;//段前间距
	String hasSpacing;
	int leading;//行间距
	int wordSpace;//字间距
	float rise;
	String z;
	String h;
	
	String underLine;//给这个变量的字符添加下划线  在一个text中如果有多个字符要下划线的情况，
	//中间以逗号分隔，注意要加下划线的字符不能包括逗号
	String indentationLeft;
	String indentationRight;
	String whole;
	String genericTag;//标签
	String pageNumFromName;//放入chapter或者section的name属性 text标签不设任何值   以该章节的页码替换
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSup() {
		return sup;
	}

	public void setSup(String sup) {
		this.sup = sup;
	}

	public float getSpacing() {
		return spacing;
	}

	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public int getLeading() {
		return leading;
	}

	public void setLeading(int leading) {
		this.leading = leading;
	}

	public int getWordSpace() {
		return wordSpace;
	}

	public void setWordSpace(int wordSpace) {
		this.wordSpace = wordSpace;
	}

	public float getRise() {
		return rise;
	}

	public void setRise(float rise) {
		this.rise = rise;
	}

	public float getSpacingB() {
		return spacingB;
	}

	public void setSpacingB(float spacingB) {
		this.spacingB = spacingB;
	}

	public String getUnderLine() {
		return underLine;
	}

	public void setUnderLine(String underLine) {
		this.underLine = underLine;
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

	public String getIndentationLeft() {
		return indentationLeft;
	}

	public void setIndentationLeft(String indentationLeft) {
		this.indentationLeft = indentationLeft;
	}

	public String getIndentationRight() {
		return indentationRight;
	}

	public void setIndentationRight(String indentationRight) {
		this.indentationRight = indentationRight;
	}

	public String getHasSpacing() {
		return hasSpacing;
	}

	public void setHasSpacing(String hasSpacing) {
		this.hasSpacing = hasSpacing;
	}

	public String getWhole() {
		return whole;
	}

	public void setWhole(String whole) {
		this.whole = whole;
	}

	public String getGenericTag() {
		return genericTag;
	}

	public void setGenericTag(String genericTag) {
		this.genericTag = genericTag;
	}

	public String getPageNumFromName() {
		return pageNumFromName;
	}

	public void setPageNumFromName(String pageNumFromName) {
		this.pageNumFromName = pageNumFromName;
	}

	
	
}
