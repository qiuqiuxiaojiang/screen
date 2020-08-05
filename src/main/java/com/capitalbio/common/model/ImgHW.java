package com.capitalbio.common.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Title: Result.java
 * @Package com.capitalbio.ehr.model
 * @Description: TODO(用一句话描述该文件做什么)
 * @author p 13633867331_163_com
 * @date 2015年8月2日 下午11:46:19
 * @version V1.0
 */
@XmlRootElement
public class ImgHW {

	private int height;	// 图片高度
	private int width; // 图片宽度
	private String result; // 结果
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
