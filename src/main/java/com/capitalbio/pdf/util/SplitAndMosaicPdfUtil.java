package com.capitalbio.pdf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;

public class SplitAndMosaicPdfUtil {

	/***
	 * 将 tempFileName 路径的pdf拆分
	 * 1.将目录页页码设置正确
	 * 2.将横板页前的pdf设为 before.pdf
	 * 3.将横板页第一页pdf设为hengBan.pdf
	 * 4.将剩下的pdf设为after.pdf
	 * 5.调用检验所系统的接口 将hengBan.pdf 打包发送过去 接收到盖章了的横板pdf设为hengBanReal.pdf
	 * 
	 * chapterName是拆分的那一页所在的章节
	 * 注意： 这里只拆分该章节的第一页
	 * rootFileName 为生成pdf的根目录
	 * 
	 * @throws Exception 
	 * 
	 */
	 
	public static void splitPdf(String rootFileName,String tempFileName,String fileName,String chapterName) throws Exception{
		
		String beforePageSize="";
		String hengBanPageSize="";
		String afterPageSize="";
		
		PdfReader readerBefore = new PdfReader(tempFileName);
		List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(readerBefore) ;
		int cals = 0;
		int cale = 0;
		int cals1 = 0;
		String isCol = "false";
		String isColH = "false";
		Map<String, Object> map = null; 
		System.out.println(list.size());
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map = list.get(i);
			if(map.get("Title").equals("目录") && isCol=="false" ){
				cals = Integer.parseInt(map.get("Page").toString().substring(0, map.get("Page").toString().indexOf(" ")))-1; //9
				cale = Integer.parseInt(list.get(i+1).get("Page").toString().substring(0, list.get(i+1).get("Page").toString().indexOf(" "))); //14
				isCol = "true";
			}
			if(map.get("Title").equals(chapterName) && isColH=="false" ){
				cals1 = Integer.parseInt(map.get("Page").toString().substring(0, map.get("Page").toString().indexOf(" ")))-1; 
				isColH="true";
			}
//			System.out.println(map.get("Title")+"=====>"+cals1+"-"+cale1);
		}
		int calt = Integer.parseInt(list.get(list.size()-1).get("Page").toString().substring(0, list.get(list.size()-1).get("Page").toString().indexOf(" ")));
		String pageIndex = "";
		if(isCol.equals("true")){
			list.remove(list.size()-1);
			if(cals == 0){
//				pageIndex = calt+"-"+(calt+cale-cals-2)+","+cale+"-"+(cals1-1)+","+cals1+","+(cals1+1)+"-"+(calt-1);
				 beforePageSize=calt+"-"+(calt+cale-cals-2)+","+cale+"-"+cals1;
				 hengBanPageSize=cals1+1+"";
				 afterPageSize=(cals1+2)+"-"+(calt-1);
				
			}else{
//				pageIndex = "1-"+cals+","+calt+"-"+(calt+cale-cals-2)+","+cale+"-"+(cals1-1)+","+cals1+","+(cals1+1)+"-"+(calt-1);
				beforePageSize="1-"+cals+","+calt+"-"+(calt+cale-cals-2)+","+cale+"-"+cals1;
				 hengBanPageSize=cals1+1+"";
				 afterPageSize=(cals1+2)+"-"+(calt-1);
			}
		}else{
//			pageIndex = "1-"+cals+","+calt+"-"+(calt+cale-cals-2)+","+cals1+","+(cale-1)+"-"+(calt-1);
//			pageIndex = "1-"+cals+","+calt+"-"+(calt+cale-cals-2)+","+cale+(cals1-1)+","+cals1+","+(cals1+1)+"-"+(calt-1);
			beforePageSize="1-"+cals+","+calt+"-"+(calt+cale-cals-2)+","+cale+cals1;
			 hengBanPageSize=cals1+1+"";
			 afterPageSize=(cals1+2)+"-"+(calt-1);
		}
		readerBefore.selectPages(beforePageSize);
		PdfStamper stamper = new PdfStamper(readerBefore, new FileOutputStream(rootFileName+"/before.pdf"));
        stamper.setOutlines(list);
		stamper.close();
		readerBefore.close();
		
		
		PdfReader readerHengBan = new PdfReader(tempFileName);
		readerHengBan.selectPages(hengBanPageSize);
		PdfStamper stamperHB = new PdfStamper(readerHengBan, new FileOutputStream(rootFileName+"/hengBan.pdf"));
		stamperHB.setOutlines(list);
		stamperHB.close();
		readerHengBan.close();
		
		PdfReader readerAfter = new PdfReader(tempFileName);
		readerAfter.selectPages(afterPageSize);
		PdfStamper stamperAfter = new PdfStamper(readerAfter, new FileOutputStream(rootFileName+"/after.pdf"));
		stamperAfter.setOutlines(list);
		stamperAfter.close();
		readerAfter.close();
		//调用检验所系统接口
		
		
		mosaicPdf(null,fileName,rootFileName);
	}
	
	/**
	 * 
	 * @param fileName
	 * 
	 * 1.如果hengBanRealFile为空时，默认将 before.pdf hengBanReal.pdf after.pdf拼接在一起
	 * 2.拼接输出的路径为 fileName
	 * 
	 * 3.如果hengBanRealFile不为空时，将hengBanRealFile的pdf作为hengBanReal.pdf,然后照以上方法拼接和输出
	 * 
	 */
	public static void mosaicPdf(String hengBanRealFile,String fileName,String rootFileName){
		if(StringUtils.isEmpty(hengBanRealFile)){
			//内部调用 默认的三部分pdf拼接
			
			
			
			
		}
		 List<InputStream> list = new ArrayList<InputStream>();
	        try {
	            // Source pdfs
	            list.add(new FileInputStream(new File(rootFileName+"/before.pdf")));
	            list.add(new FileInputStream(new File(rootFileName+"/hengBan.pdf")));
	            list.add(new FileInputStream(new File(rootFileName+"/after.pdf")));
	            // Resulting pdf
	            OutputStream out = new FileOutputStream(new File(fileName));
	            
	            

		        Document document = new Document();
		        RectangleReadOnly only = new RectangleReadOnly(842f, 595f);
		        PdfWriter writer = PdfWriter.getInstance(document, out);
		        document.open();
		        PdfContentByte cb = writer.getDirectContent();
		        int k=0;
		        for (InputStream in : list) {
		            PdfReader reader = new PdfReader(in);
		            //设置横板pageSize
//		            if(k==0){
//		            	k++;
//		            }else{
//		            	 document.setPageSize(only);
//		            }
		            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
		            	document.setPageSize(reader.getPageSize(i));
		                document.newPage();
		                PdfImportedPage page = writer.getImportedPage(reader, i);
		                cb.addTemplate(page, 0, 0);
		            }
		        }
		        out.flush();
		        document.close();
		        out.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	        System.out.println("SUCCESS!");
		
	}
	/**
	 * 将参数的前两个pdf拼接起来 按第三个参数路径输出
	 * @param firstFile
	 * @param secondFile
	 * @param resultFile
	 */
	public static void mosaicTwoPdf(String firstFile,String secondFile,String resultFile){
		List<InputStream> list = new ArrayList<InputStream>();
		try {
			// Source pdfs
			list.add(new FileInputStream(new File(firstFile)));
			list.add(new FileInputStream(new File(secondFile)));
			// Resulting pdf
			OutputStream out = new FileOutputStream(new File(resultFile));
			
			
			
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			for (InputStream in : list) {
				PdfReader reader = new PdfReader(in);
				for (int i = 1; i <= reader.getNumberOfPages(); i++) {
					document.setPageSize(reader.getPageSize(i));
					document.newPage();
					PdfImportedPage page = writer.getImportedPage(reader, i);
					cb.addTemplate(page, 0, 0);
				}
			}
			out.flush();
			document.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("SUCCESS!");
		
	}
	public static void main(String[] args) throws Exception {
		PdfReader readerBefore = new PdfReader("E://shi1.pdf");
		List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(readerBefore) ;
		readerBefore.selectPages("2");
		PdfStamper stamper = new PdfStamper(readerBefore, new FileOutputStream("E://shi2.pdf"));
        stamper.setOutlines(list);
		stamper.close();
		readerBefore.close();
		
	}
}
