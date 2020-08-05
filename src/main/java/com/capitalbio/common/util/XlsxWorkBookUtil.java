package com.capitalbio.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalbio.common.service.FileManageService;


public class XlsxWorkBookUtil {
	@Autowired
	private FileManageService fileManageService;
	
	public String createExcel(String[] headerList, String[] columnList, Map<String, Object> result,
			String fileName, String[] keys, HttpServletResponse response) throws UnsupportedEncodingException, FileNotFoundException {
		String url = "";
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "downloads");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
		String uuid =  UUID.randomUUID().toString();
		File path = new File(dir, uuid);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
		
		File newFile = new File(path, fileName + ".xlsx");
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		
		// 表格第一行合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.length-1));
		
		/** 第一行 创建标题行 */
		Row row = sheet.createRow(0);
		Cell cellTitle = row.createCell(0);
		cellTitle.setCellValue(fileName);
		cellTitle.setCellStyle(setTitleStyle(wb));
		
		/** 第二行 */
		Row row2 = sheet.createRow(1);
        for (int i = 0; i < headerList.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(headerList[i]);
            cell.setCellStyle(setTitleStyle(wb));
        }
        
        
        for (int n = 0; n < keys.length; n ++) {
        	if (keys[n].equals("percList")) {
        		List<Double> list = (List<Double>) result.get(keys[n]);
    			Row rown = sheet.createRow(n + 2);
    	        Cell celln0 = rown.createCell(0);
    	        celln0.setCellValue(columnList[n]);
    	        celln0.setCellStyle(setContentStyle(wb));
    	        
    	        Double count = (double) 0;
    	        for (int i = 0; i < list.size(); i++) {
    	        	Cell cell = rown.createCell(i + 1);
    	        	cell.setCellValue(list.get(i) + "%");
    	        	cell.setCellStyle(setContentStyle(wb));
    	        	count += list.get(i);
    			}
    	        
    	        Cell cellCount = rown.createCell(list.size() + 1);
    	        cellCount.setCellValue(ParamUtils.doubleScale(count, 0) + "%");
    	        cellCount.setCellStyle(setContentStyle(wb));
        	} else if (keys[n].equals("countList")){
        		
        		List<Integer> list = (List<Integer>) result.get(keys[n]);
    			Row rown = sheet.createRow(n + 2);
    	        Cell celln0 = rown.createCell(0);
    	        celln0.setCellValue(columnList[n]);
    	        celln0.setCellStyle(setContentStyle(wb));
    	        
    	        int count = 0;
    	        for (int i = 0; i < list.size(); i++) {
    	        	Cell cell = rown.createCell(i + 1);
    	        	cell.setCellValue(list.get(i));
    	        	cell.setCellStyle(setContentStyle(wb));
    	        	count += list.get(i);
    			}
    	        
    	        Cell cellCount = rown.createCell(list.size() + 1);
    	        cellCount.setCellValue(count);
    	        cellCount.setCellStyle(setContentStyle(wb));
    	        
        	} else {
        		List<Integer> list = (List<Integer>) result.get(keys[n]);
    			Row rown = sheet.createRow(n + 2);
    	        Cell celln0 = rown.createCell(0);
    	        celln0.setCellValue(columnList[n] + "（人）");
    	        celln0.setCellStyle(setContentStyle(wb));
    	        
    	        int count = 0;
    	        for (int i = 0; i < list.size(); i++) {
    	        	Cell cell = rown.createCell(i + 1);
    	        	cell.setCellValue(list.get(i));
    	        	cell.setCellStyle(setContentStyle(wb));
    	        	count += list.get(i);
    			}
    	        
    	        Cell cellCount = rown.createCell(list.size() + 1);
    	        cellCount.setCellValue(count);
    	        cellCount.setCellStyle(setContentStyle(wb));
        	}
		}
      
        for (int k = 0; k < headerList.length; k++) {
            sheet.autoSizeColumn(k);
        }
        // 处理中文不能自动调整列宽的问题
       // setSizeColumn(sheet, headerList.length);

        /*
         * 写入到文件中
         */
        File file = new File(newFile.getAbsolutePath());
        FileOutputStream os = new FileOutputStream(file);
		try {
			
			wb.write(os);
	    
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null) wb.close();
				if (os != null) wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newFile.getAbsolutePath();
    	
	}
	
	public String createMzExcel(String[] headerList, List<Map<String, Object>> list, String fileName, HttpServletResponse response) throws UnsupportedEncodingException, FileNotFoundException {
		String url = "";
		
		String tempDir = PropertyUtils.getProperty("system.temp.dir");
		File dir = new File(tempDir, "downloads");
	    if(!dir.exists()){
	    	dir.mkdirs();
	    }
	    
		String uuid =  UUID.randomUUID().toString();
		File path = new File(dir, uuid);
	    if(!path.exists()){
	    	path.mkdirs();
	    }
		
		File newFile = new File(path, fileName + ".xlsx");
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet();
		
		// 表格第一行合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerList.length-1));
		
		/** 第一行 创建标题行 */
		Row row = sheet.createRow(0);
		Cell cellTitle = row.createCell(0);
		cellTitle.setCellValue(fileName);
		cellTitle.setCellStyle(setTitleStyle(wb));
		
		/** 第二行 */
		Row row2 = sheet.createRow(1);
        for (int i = 0; i < headerList.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue(headerList[i]);
            cell.setCellStyle(setTitleStyle(wb));
        }
        
        for (int n = 0; n < list.size(); n ++) {
        	Map<String, Object> map = list.get(n);
        	
        	Row rown = sheet.createRow(n + 2);
	        Cell celln0 = rown.createCell(0);
	        celln0.setCellValue(map.get("count").toString());
	        celln0.setCellStyle(setContentStyle(wb));
	        
	        Cell celln1 = rown.createCell(1);
	        celln1.setCellValue(map.get("syndrome").toString());
	        celln1.setCellStyle(setContentStyle(wb));
	        sheet.autoSizeColumn(1);
	        
	        Cell celln2 = rown.createCell(2);
	        celln2.setCellValue(map.get("num").toString());
	        celln2.setCellStyle(setContentStyle(wb));
	        
	        Cell celln3 = rown.createCell(3);
	        celln3.setCellValue(map.get("tnbCount").toString());
	        celln3.setCellStyle(setContentStyle(wb));
	        
	        Cell celln4 = rown.createCell(4);
	        celln4.setCellValue(map.get("tnbPerc").toString() + "%");
	        celln4.setCellStyle(setContentStyle(wb));
	        
	        Cell celln5 = rown.createCell(5);
	        celln5.setCellValue(map.get("gxyCount").toString());
	        celln5.setCellStyle(setContentStyle(wb));
	        
	        Cell celln6 = rown.createCell(6);
	        celln6.setCellValue(map.get("gxyPerc").toString() + "%");
	        celln6.setCellStyle(setContentStyle(wb));
	        
	        Cell celln7 = rown.createCell(7);
	        celln7.setCellValue(map.get("xzCount").toString());
	        celln7.setCellStyle(setContentStyle(wb));
	        
	        Cell celln8 = rown.createCell(8);
	        celln8.setCellValue(map.get("xzPerc").toString() + "%");
	        celln8.setCellStyle(setContentStyle(wb));
	        
	        Cell celln9 = rown.createCell(9);
	        celln9.setCellValue(map.get("fpCount").toString());
	        celln9.setCellStyle(setContentStyle(wb));
	        
	        Cell celln10 = rown.createCell(10);
	        celln10.setCellValue(map.get("fpPerc").toString() + "%");
	        celln10.setCellStyle(setContentStyle(wb));
        }
        
        for (int k = 0; k < headerList.length; k++) {
            sheet.autoSizeColumn(k);
        }
        // 处理中文不能自动调整列宽的问题
       // setSizeColumn(sheet, headerList.length);

        /*
         * 写入到文件中
         */
        File file = new File(newFile.getAbsolutePath());
        FileOutputStream os = new FileOutputStream(file);
		try {
			
			wb.write(os);
	    
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (wb != null) wb.close();
				if (os != null) wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return newFile.getAbsolutePath();
    	
	}
	
	// 自适应宽度(中文支持)
    private void setSizeColumn(XSSFSheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                XSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }
 
                if (currentRow.getCell(columnNum) != null) {
                    XSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }

	public void downloadExcel(HttpServletResponse response,String path, String fileName) throws IOException{
		response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"),"ISO8859-1"));
		
		OutputStream out = null;
		InputStream in = null;
		try {
			//3、输出流
			out = response.getOutputStream();
			
			//4、获取服务端生成的excel文件，这里的path等于4.8中的path
			in = new FileInputStream(new File(path));
			
			//5、输出文件
			int b;
			while((b=in.read())!=-1){
			    out.write(b);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally{
			if (in != null) in.close();
			if (out != null) out.close();
			
		}
	}

	
	public CellStyle setTitleStyle(XSSFWorkbook wb) {
		
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		
		Font font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);
		font.setBold(true);
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	
	public CellStyle setContentStyle(XSSFWorkbook wb) {
		
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 垂直居中
		
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框   
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		
		Font font = wb.createFont();
		font.setFontName("微软雅黑");
		font.setFontHeightInPoints((short) 9);
		cellStyle.setFont(font);
		return cellStyle;
	}
	
	
	
	
	
	/**
	 * 判断传入的内容长度 记录最长的长度
	 * 
	 * @param index
	 * @param map
	 * @param objValue
	 * @return
	 */
	public Map<Integer, Integer> getMaxWidthNum(int index, Map<Integer, Integer> map,Object objValue){
		if (objValue == null) {
			return map;
		}
		String strValue = objValue.toString();
		int length = 0;
		/** 需要换行的字符串 以换行符分隔取每段最长的长度 **/
		if (strValue.contains("\n")) {
			String[] arrValue = strValue.split("\\n");
			for (String string : arrValue) {
				if (string.getBytes().length > length) {
					length = string.getBytes().length;
				}
			}
		} else {
			length = strValue.getBytes().length;
		}
		length = length * 180;
		if (map.get(index) == null) {
			map.put(index, length);
		} else {
			if (map.get(index) < length) {
				map.put(index, length);
			}
		}
		return map;
	}
	
}