package com.capitalbio.pdf.operation;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.pdf.impl.FilletCellEvent;
import com.capitalbio.pdf.impl.FilletTableEvent;
import com.capitalbio.pdf.model.CellModel;
import com.capitalbio.pdf.model.TableModel;


/**
 * 设置单元格以及表格事件 包括 边框以及背景
 * @author admin
 *
 */
public class EventUtil {

	/**
	 * 设置单元格事件 包括 边框以及背景
	 * @param cellModel
	 * @return
	 */
	public FilletCellEvent setFilletCellEvent(CellModel cellModel,String type){
		FilletCellEvent cellEvent = new FilletCellEvent();
		if(StringUtils.isNotEmpty(cellModel.getFilletBorderColor()) || 
				StringUtils.isNotEmpty(cellModel.getShadingColor())){
			String color = "";
			if(StringUtils.isNotEmpty(cellModel.getFilletBorderColor())){
				color = cellModel.getFilletBorderColor();
			}else if(StringUtils.isNotEmpty(cellModel.getShadingColor())){
				color = cellModel.getShadingColor();
			}
			cellEvent.setColor(color);
		}
		cellEvent.setType(type);
		if(StringUtils.isNotEmpty(cellModel.getShadingRadian())){
			cellEvent.setRadian(Float.parseFloat(cellModel.getShadingRadian()));
		}
		if(StringUtils.isNotEmpty(cellModel.getFilletBorderX())){
			cellEvent.setX(Float.parseFloat(cellModel.getShadingX()));
		}
		if(StringUtils.isNotEmpty(cellModel.getFilletBorderY())){
			cellEvent.setY(Float.parseFloat(cellModel.getShadingY()));
		}
		if(StringUtils.isNotEmpty(cellModel.getFilletBorderW())){
			cellEvent.setW(Float.parseFloat(cellModel.getShadingW()));
		}
		if(StringUtils.isNotEmpty(cellModel.getFilletBorderH())){
			cellEvent.setH(Float.parseFloat(cellModel.getShadingH()));
		}
		if(StringUtils.isNotEmpty(cellModel.getFilletCIndex())){
			cellEvent.setcIndex(Integer.parseInt(cellModel.getFilletCIndex()));
		}
		
		if(StringUtils.isNotEmpty(cellModel.getShadingRadian())){
			cellEvent.setRadian(Integer.parseInt(cellModel.getShadingRadian()));
		}
		if(StringUtils.isNotEmpty(cellModel.getShadingX())){
			cellEvent.setX(Integer.parseInt(cellModel.getShadingX()));
		}
		if(StringUtils.isNotEmpty(cellModel.getShadingY())){
			cellEvent.setY(Integer.parseInt(cellModel.getShadingY()));
		}
		if(StringUtils.isNotEmpty(cellModel.getShadingW())){
			cellEvent.setW(Integer.parseInt(cellModel.getShadingW()));
		}
		if(StringUtils.isNotEmpty(cellModel.getShadingH())){
			cellEvent.setH(Integer.parseInt(cellModel.getShadingH()));
		}
		if(StringUtils.isNotEmpty(cellModel.getShadingCIndex())){
			cellEvent.setcIndex(Integer.parseInt(cellModel.getShadingCIndex()));
		}
		return cellEvent;
	}
	/**
	 * 设置单元格事件 包括 边框以及背景
	 * @param tableModel
	 * @return
	 */
	public FilletTableEvent setFilletTableEvent(TableModel tableModel,String type){
		FilletTableEvent tableEvent = new FilletTableEvent();
		if(type.trim().equals("shading")){
			if(StringUtils.isNotEmpty(tableModel.getShadingColor())){
				String color = tableModel.getShadingColor();
				tableEvent.setColor(color);
			}
			tableEvent.setType(type);
			if(StringUtils.isNotEmpty(tableModel.getShadingRadian())){
				tableEvent.setRadian(Integer.parseInt(tableModel.getShadingRadian()));
			}
			if(StringUtils.isNotEmpty(tableModel.getShadingX())){
				tableEvent.setX(Integer.parseInt(tableModel.getShadingX()));
			}
			if(StringUtils.isNotEmpty(tableModel.getShadingY())){
				tableEvent.setY(Integer.parseInt(tableModel.getShadingY()));
			}
			if(StringUtils.isNotEmpty(tableModel.getShadingW())){
				tableEvent.setW(Integer.parseInt(tableModel.getShadingW()));
			}
			if(StringUtils.isNotEmpty(tableModel.getShadingH())){
				tableEvent.setH(Integer.parseInt(tableModel.getShadingH()));
			}
			if(StringUtils.isNotEmpty(tableModel.getShadingCIndex())){
				tableEvent.setcIndex(Integer.parseInt(tableModel.getShadingCIndex()));
			}
		}else if(type.trim().equals("border")){
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderColor())){
				String color = tableModel.getFilletBorderColor();
				tableEvent.setColor(color);
			}
			tableEvent.setType(type);
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderLineWidth())){
				tableEvent.setLineWidth(Float.parseFloat(tableModel.getFilletBorderLineWidth()));
			}
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderRadian())){
				tableEvent.setRadian(Float.parseFloat(tableModel.getFilletBorderRadian()));
			}
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderX())){
				tableEvent.setX(Float.parseFloat(tableModel.getFilletBorderX()));
			}
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderY())){
				tableEvent.setY(Float.parseFloat(tableModel.getFilletBorderY()));
			}
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderW())){
				tableEvent.setW(Float.parseFloat(tableModel.getFilletBorderW()));
			}
			if(StringUtils.isNotEmpty(tableModel.getFilletBorderH())){
				tableEvent.setH(Float.parseFloat(tableModel.getFilletBorderH()));
			}
			if(StringUtils.isNotEmpty(tableModel.getFilletCIndex())){
				tableEvent.setcIndex(Integer.parseInt(tableModel.getFilletCIndex()));
			}
		}
		return tableEvent;
	}
}
