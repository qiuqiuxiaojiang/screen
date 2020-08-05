package com.capiltalbio.health.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Lists;

public class WriterExcelUtil {

	public static void main(String[] args) throws Exception {
        String path = "E://demo.xlsx";
        List<String> titles =Lists.newArrayList();
        titles.add("id");
        titles.add("name");
        titles.add("age");
        titles.add("birthday");
        titles.add("gender");
        titles.add("date");
        generateWorkbook(path, titles);
    }

    /**
     * 将数据写入指定path下的Excel文件中
     *
     * @param path   文件存储路径
     * @param name   sheet名
     * @param style  Excel类型
     * @param titles 标题串
     * @param values 内容集
     * @throws Exception 
     */
    private static void generateWorkbook(String path, List<String> titles) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        // 生成一个表格
        Sheet sheet = workbook.createSheet();
        
        // 设置表格默认列宽度为15个字节
       // sheet.setDefaultColumnWidth((short) 15);
        /*
         * 创建标题行
         */
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles.get(i));
        }
       
        /*
         * 写入到文件中
         */
        File file = new File(path);
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

    
}
