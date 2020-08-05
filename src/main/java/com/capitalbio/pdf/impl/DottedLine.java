package com.capitalbio.pdf.impl;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class DottedLine extends LineSeparator{
	
	private float unitsOn; //虚线长度
	private float unitsOff;//虚线间隔长度
	private int cap;
	
	public DottedLine() {
		this.unitsOn = 5f;
		this.unitsOff = 5f;
		this.cap = 1;
		
	}
	
	
	public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y)
	   {
	     canvas.saveState();
	     canvas.setLineWidth(this.lineWidth);
	     canvas.setLineCap(cap);
	     canvas.setLineDash(unitsOn, unitsOff, this.unitsOff / 2.0F);
	     drawLine(canvas, llx, urx, y);
	     canvas.restoreState();
	   }


	

	public int getCap() {
		return cap;
	}

	public void setCap(int cap) {
		this.cap = cap;
	}

	public float getUnitsOn() {
		return unitsOn;
	}

	public void setUnitsOn(float unitsOn) {
		this.unitsOn = unitsOn;
	}

	public float getUnitsOff() {
		return unitsOff;
	}


	public void setUnitsOff(float unitsOff) {
		this.unitsOff = unitsOff;
	}
	
	
}
