package com.capiltalbio.health.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class ImportData {
	@Autowired
	MongoBaseDAO mongoBaseDAO;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ImportData data = new ImportData();
		data.importCommunity();
	}
	
	public void importCommunity() throws FileNotFoundException, IOException {
		File file = new File("C:/Users/xiaoyanzhang/Desktop/盘龙区行政区域.xls");
		
		Workbook wb = null;
        try {
        	wb = new XSSFWorkbook(file);
        } catch (Exception ex) {
        	wb = new HSSFWorkbook(new FileInputStream(file));
        }
       
		//HSSFSheet sheet = wb.getSheetAt(0);
        Sheet sheet = wb.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        
        DB db = getDB();
        DBCollection coll = db.getCollection("communitys");
        int n = 1;
		for (int i = 1; i < rows; i++) {
			Row row = sheet.getRow(i);
			String street = getCellValue(row.getCell(0));
			short lastCellNum = row.getLastCellNum();
			for (int r = 1; r <= lastCellNum; r ++) {
				Map<String, Object> map = Maps.newHashMap();
				
				if (StringUtils.isEmpty(getCellValue(row.getCell(r)))) {
					continue;
				}
				map.put("community", getCellValue(row.getCell(r)));
				map.put("street", street);
				map.put("checkPlaceId", n);
				map.put("itemId", 2);//阜新：1   昆明：2
				DBObject dbObj= new BasicDBObject(map);
				coll.save(dbObj);
				
				n ++;
			}
		}
	}
	
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		}
		return cell.getStringCellValue();
	}
	
	public DB getDB() {
		try{
			String url = "mongodb://localhost:27017";
			String sURI = String.format(url);
			MongoClientURI uri = new MongoClientURI(sURI);
			MongoClient mongoClient = new MongoClient(uri);
			DB db = mongoClient.getDB("fuxin");
			return db;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
}
