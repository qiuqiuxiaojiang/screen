package com.capiltalbio.health.eye;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class EyeRecordLost {
	private String url = "mongodb://localhost:27017";
	private String dbName = "fuxin";
	public static void main(String[] args) throws Exception {
		EyeRecordLost erl = new EyeRecordLost();
		erl.check();
	}
	public void check() throws Exception{
		File file = new File("");
		XSSFWorkbook wb = new XSSFWorkbook(file);
		PrintStream ps = new PrintStream(new FileOutputStream(new File("")), true, "GBK");
		ps.println("编号,身份证号码,姓名,性别,年龄,眼象信息,找到记录");		
		DB db = getDB();
		XSSFSheet sheet = wb.getSheetAt(0);
		DBCollection coll = db.getCollection("eyeRecord");
		int rows = sheet.getPhysicalNumberOfRows();
 		for (int i = 1; i < rows; i++) {
			XSSFRow row = sheet.getRow(i);
			String uniqueId = getCellValue(row.getCell(0));
			String customerId = getCellValue(row.getCell(1));
			String name = getCellValue(row.getCell(2));
			String sex = getCellValue(row.getCell(3));
			String age = getCellValue(row.getCell(4));
			String eyeCheck = getCellValue(row.getCell(5));
			ps.print(uniqueId);
			ps.print(",");
			ps.print(customerId);
			ps.print(",");
			ps.print(name);
			ps.print(",");
			ps.print(sex);
			ps.print(",");
			ps.print(age);
			ps.print(",");
			ps.print(eyeCheck);
			ps.print(",");
			DBObject query = new BasicDBObject();
			query.put("uniqueId", uniqueId);
			DBObject obj = coll.findOne(query);
			boolean find = obj!=null;
			if (find) {
				ps.println("找到");
			} else {
				ps.println("未找到");
			}
 		}
 		ps.close();
 		wb.close();
	}
	public DB getDB() {
		try{
			String sURI = String.format(url);
			MongoClientURI uri = new MongoClientURI(sURI);
			MongoClient mongoClient = new MongoClient(uri);
			DB db = mongoClient.getDB(dbName);
			return db;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private String getCellValue(XSSFCell cell) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			double dvalue = cell.getNumericCellValue();
			long lvalue = Math.round(dvalue);
			return String.valueOf(lvalue);
		} else {
			
			return cell.getStringCellValue();
		}
	}
}
