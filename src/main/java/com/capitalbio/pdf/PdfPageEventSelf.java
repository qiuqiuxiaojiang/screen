package com.capitalbio.pdf;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.pdf.model.DocModel;
import com.capitalbio.pdf.model.RefModel;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;


public class PdfPageEventSelf implements PdfPageEvent{
	
	PdfGenerator generator;
	
	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		
	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		
	}
	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		try {
			if (generator.getCatalogModel() != null) {
				if (generator.getCatalogModel() != null) {
					for (RefModel ref : generator.getCatalogModel().getRefList()) {
						String name = ref.getName();
						int depth = ref.getDepth();
						if (depth == 0) {
							Integer pageNumber = generator.getChapterMap().get(name);
							if (pageNumber != null) {
								ref.setPageNumber(pageNumber);
							}
						} else {
							if(StringUtils.isNotEmpty(ref.getIsSplitting()) && "true".equals(ref.getIsSplitting())){
								String spName = "高脂血症高胆固醇血症高低密度脂蛋白胆固醇血症高甘油三酯血症";
								if(name.length() >= 4 && spName.indexOf(name.substring(0,4)) > -1 ){
									Integer pageNumber = retrurnName(generator.getSectionMap(), name);
									if (pageNumber != null) {
										ref.setPageNumber(pageNumber);
									}
								}else{
									Integer pageNumber = generator.getSectionMap().get(name);
									if (pageNumber != null) {
										ref.setPageNumber(pageNumber);
									}
								}
							}else{
								Integer pageNumber = generator.getSectionMap().get(name);
								if (pageNumber != null) {
									ref.setPageNumber(pageNumber);
								}
							}
						}
					}
				}
				float width=document.getPageSize().getWidth()-document.leftMargin()-document.rightMargin();
				//判断是否需要设置奇偶页 内边距
					//先判断是否有leftMargins和rightMargins属性
				DocModel docModel = generator.getDocModel();
				if ((docModel.getleftMargins()!=null && !"".equals(docModel.getleftMargins()))
						|| (docModel.getrightMargins()!=null && !"".equals(docModel.getrightMargins()))) {
					
					int catalogSecondPageNum = generator.getCatalogSecondPageNum();
					if (catalogSecondPageNum % 2 == 0) {
						float[] rightMargins = docModel.getrightMargins();
						document.setMargins(rightMargins[0], rightMargins[1], rightMargins[2], rightMargins[3]);
					} else {
						float[] leftMargins = docModel.getleftMargins();
						document.setMargins(leftMargins[0], leftMargins[1], leftMargins[2], leftMargins[3]);
					}
					
				}
				document.add(generator.generateCatalog(generator.getCatalogModel(),width));
				document.newPage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	/**
	 * 处理目录分支
	 * @param map
	 * @return
	 */
	public Integer retrurnName(Map<String, Integer> map, String name){
		Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();  
		int count = 0;
        while (iterator.hasNext()) {  
            Map.Entry<String, Integer> entry = iterator.next();  
            if(entry.getKey().indexOf(name.substring(0, 4)) > -1){
            	count = entry.getValue();
            	break;
            }
        }  
        return count;
	}
	@Override
	public void onParagraph(PdfWriter writer, Document document,
			float paragraphPosition) {
		
	}

	@Override
	public void onParagraphEnd(PdfWriter writer, Document document,
			float paragraphPosition) {
		
	}

	@Override
	public void onChapter(PdfWriter writer, Document document,
			float paragraphPosition, Paragraph title) {
		
	}

	@Override
	public void onChapterEnd(PdfWriter writer, Document document,
			float paragraphPosition) {
	}

	@Override
	public void onSection(PdfWriter writer, Document document,
			float paragraphPosition, int depth, Paragraph title) {
		
	}

	@Override
	public void onSectionEnd(PdfWriter writer, Document document,
			float paragraphPosition) {
	}

	@Override
	public void onGenericTag(PdfWriter writer, Document document,
			Rectangle rect, String text) {
		
	}

	public PdfGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(PdfGenerator generator) {
		this.generator = generator;
	}

}
