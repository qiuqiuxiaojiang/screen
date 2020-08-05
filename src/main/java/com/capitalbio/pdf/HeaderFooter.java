package com.capitalbio.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.pdf.model.HeaderItemModel;
import com.capitalbio.pdf.model.HeaderModel;
import com.capitalbio.pdf.model.ImageModel;
import com.capitalbio.pdf.util.RomanNumberUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;





public class HeaderFooter extends PdfPageEventHelper {
	HeaderModel headerModel;
	PdfGenerator generator;
	String currentChapter="";
	boolean isHengBan=false;
	int beginNum=0;
	float[] leftMargins;
	float[] rightMargins;
	int allPage = 0;
	
	int k1=0;
	PdfTemplate templateTotal;
	String flagPage = "";
	int beginNo=0;//表示从第几页开始显示页眉页脚 注 总页数也改变
	Font fontTemp;
	int ml = 0;
	int m = 0;
	int p = 0;
	int[] mls = new int[10];
	int sl = 0;
	String[] strs = new String[10];
	boolean chapterEnd=false;
	boolean chapterStart=false;
	String reportType;
	int chapterPageNumDiff;
	int catalogPageNum = 0; 
	boolean catalogFirst = true;
	boolean catalogSecond = false;
	@Override
	public void onChapterEnd(PdfWriter writer, Document document, float position)
	  {
		chapterEnd=true;
		chapterStart=false;
	  }
	
	
	@Override
	public void onChapter(PdfWriter writer, Document document,
			float paragraphPosition, Paragraph title) {
		chapterStart=true;
		chapterEnd=false;
		chapterPageNumDiff = writer.getPageNumber()-1;
		generator.getChapterMap().put(title.getContent(), writer.getPageNumber());
	
		currentChapter = title.getContent();
		
		if(StringUtils.isNotEmpty(headerModel.getSubstring()) && currentChapter.length()>Integer.parseInt(headerModel.getSubstring())){
			currentChapter = currentChapter.substring(Integer.parseInt(headerModel.getSubstring()));
		}

	}
	
	@Override
	 public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text)
	  {
	  }
	@Override
	public void onSection(PdfWriter writer, Document document,
			float paragraphPosition, int depth, Paragraph title) {
		title.getAccessibleAttributes();
		generator.getChapterMap().put(title.getContent(), writer.getPageNumber());
		generator.getSectionMap().put(title.getContent(), writer.getPageNumber());
	}
	
	@Override
	public void onOpenDocument(PdfWriter writer,Document document){
		templateTotal = writer.getDirectContent().createTemplate(50,15);
    }
	
	
	
	Map<Object, Object> map = new HashMap<Object, Object>();
	 int pageNumber33=0;
	 int pageNumber44=0;
	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		int chapterPageNum=writer.getPageNumber()-chapterPageNumDiff;
		
		
		if (headerModel == null) {
			return;
		}
		String chapterName="";
		if(StringUtils.isNotEmpty(headerModel.getItemName())){
			chapterName=headerModel.getItemName();
		}
		String beginName="";
		if(StringUtils.isNotEmpty(headerModel.getBeginName())){
			beginName=headerModel.getBeginName();
		}
		
		List<HeaderItemModel> items = headerModel.getItems();
		int begin=headerModel.getBegin();
		if(!beginName.equals("")&&!currentChapter.equals("") && beginName.contains(currentChapter)){
			isHengBan=true;
			beginNo=writer.getPageNumber();
			headerModel.setBeginName(beginName.replace(currentChapter, ""));//可以多个横板chapter 但是赋值时 以逗号隔开
		}
		begin=beginNo;
		
		//使用start属性 设置页眉页脚开始的页数
		int start =0;
		if(StringUtils.isNotEmpty(headerModel.getStart())){
			start = Integer.parseInt(headerModel.getStart());
		}
		headerItemLoop:for (HeaderItemModel itemModel:items) {
			
			//判断区分不同的页眉页脚
			if(!chapterName.equals("") && chapterName.equals(currentChapter) && StringUtils.isEmpty(itemModel.getItemName())){
				continue;
			}
			if(!chapterName.equals(currentChapter) && StringUtils.isNotEmpty(itemModel.getItemName()) ){
				continue;
			}
			if(beginName.equals(currentChapter)){
				beginName="";
			}
			
//			begin=beginNo;
			PdfDictionary info = writer.getInfo();
			PdfPageEvent pageEvent = writer.getPageEvent();
			float x = itemModel.getX();
			float y = itemModel.getY();
			
			int pageNumber22 = writer.getPageNumber();
			
			
			//奇偶页属性设置
			if(StringUtils.isNotEmpty(itemModel.getExceptChapterName())){
				//需要跳过指定的chapter
				String exceptChapterName = itemModel.getExceptChapterName();
				String[] exceptChapterNameArr = exceptChapterName.split(",");
				for(int i=0;i<exceptChapterNameArr.length;i++){
					if(currentChapter.equals(exceptChapterNameArr[i])){
						continue headerItemLoop;
					}
				}
				
			}
			if(StringUtils.isNotEmpty(itemModel.getCludeChapterName())){
				//指定只出现在哪个chapter
				String cludeChapterName = itemModel.getCludeChapterName();
				String[] cludeChapterNameArr = cludeChapterName.split(",");
				for(int i=0;i<cludeChapterNameArr.length;i++){
					if(currentChapter.equals(cludeChapterNameArr[i])){
						break;
					}
					if(!currentChapter.equals(cludeChapterNameArr[i]) 
							&& i==(cludeChapterNameArr.length-1)){
						continue headerItemLoop;
					}
				}
				
			}
			//配置在第几页出现
			
			if(StringUtils.isNotEmpty(itemModel.getPageNumStr())){
				//需指定 需要在第几页出现
				String pageNumStr = itemModel.getPageNumStr();
				String[] pageNumStrArr = pageNumStr.split(",");
				for(int i=0;i<pageNumStrArr.length;i++){
					if(chapterPageNum==Integer.parseInt(pageNumStrArr[i])){
						break;
					}
					if(chapterPageNum!=Integer.parseInt(pageNumStrArr[i])
							&& i==(pageNumStrArr.length-1)){
						continue headerItemLoop;
					}
				}
				
			}
			
			if(StringUtils.isNotEmpty(itemModel.getStartPage())){
				//需从指定页面开始
				int starPage=Integer.parseInt(itemModel.getStartPage());
				//当前页码
				int pN = writer.getPageNumber();
				if(starPage>pN){
					continue;
				}
			}
			
			if(StringUtils.isNotEmpty(itemModel.getChapterStar())){
				//需判定是否跳过章节开始页 为true跳过章节开头 
				if(itemModel.getChapterStar().equals("true")){
					if(chapterStart){
						continue;
					}
				}else if (itemModel.getChapterStar().equals("false")){
					if(!chapterStart){
						continue;
					}
				}
			}
			if(StringUtils.isNotEmpty(itemModel.getChapterEnd())){
				//需判定是否跳过章节结束页 为true 跳过章节结束 
				if(itemModel.getChapterEnd().equals("true")){
					if(chapterEnd){
						continue;
					}
				}else if (itemModel.getChapterEnd().equals("false")){
					if(!chapterEnd){
						continue;
					}
				}
			}
			//只在最后一页出现
			if (StringUtils.isNotEmpty(itemModel.getLastPage()) && itemModel.getLastPage().equals("true")) {
				if (!chapterEnd) {
					continue;
				}
			}
			//使用start属性 设置页眉页脚开始的页数
			if (StringUtils.isNotEmpty(itemModel.getStartNum())) {
				int startNum = Integer.parseInt(itemModel.getStartNum());
				if (pageNumber22<startNum) {
					continue;
				} else {
					pageNumber22=pageNumber22-startNum+1;
				}
			} else if(pageNumber22<start){
				continue;
			}else{
				pageNumber22=pageNumber22-start+1;
			}
			
			pageNumber33=pageNumber22;
			//处理目录的奇偶属性乱的问题
			if(StringUtils.isNotEmpty(headerModel.getEven()) && headerModel.getEven().equals("true")){
				if(currentChapter.equals("目录") && k1==0){
					pageNumber44=pageNumber22;
					k1=k1+1;
				}
				if(currentChapter.equals("目录") && k1!=0){
					pageNumber33=pageNumber44;
				}
			}
			
			//奇偶页的页边距不同
			int pageNumTemp = pageNumber33+beginNum;
			if (currentChapter.equals("目录") ){
				if (!catalogFirst && catalogSecond) {
					pageNumTemp = catalogPageNum;
				}
				if (catalogPageNum == 0 && catalogFirst) {
					catalogPageNum = pageNumTemp;
					//第二次目录时 需要提前一页（doc.newPage()原因）设置奇偶属性 所以需要PdfPageEventSelf类 传入第一次出现目录的页码（相当于奇偶值）
					generator.setCatalogSecondPageNum(pageNumTemp);
					catalogFirst = false;
				}
			}
			if (!currentChapter.equals("目录")) {
				if (!catalogFirst ) {
					catalogSecond = true;
				}
			}
			if (pageNumTemp% 2 == 1) {
				if (rightMargins != null && !isHengBan) {
					document.setMargins(rightMargins[0], rightMargins[1], rightMargins[2], rightMargins[3]);
//					document.setMargins(leftMargins[0], leftMargins[1], leftMargins[2], leftMargins[3]);
				}
				if (StringUtils.isNotEmpty(itemModel.getIsEven()) && "false".equals(itemModel.getIsEven())) {
					x = itemModel.getZ();
					y = itemModel.getH();
				}
			}
			if (pageNumTemp% 2 == 0) {
				if (leftMargins != null && !isHengBan) {
					document.setMargins(leftMargins[0], leftMargins[1], leftMargins[2], leftMargins[3]);
//					document.setMargins(rightMargins[0], rightMargins[1], rightMargins[2], rightMargins[3]);
				}
				if (StringUtils.isNotEmpty(itemModel.getIsEven()) && "true".equals(itemModel.getIsEven())) {
					x = itemModel.getZ();
					y = itemModel.getH();
				}
			}
			
			fontTemp = itemModel.getFont();
			//begin是从第几页开始加页面页脚
			if(begin!=0){
				if(pageNumber22<begin && !chapterName.equals(currentChapter)){
					continue;
				}
			}
//			if(chapterName.equals(currentChapter)){
//				begin=0;
//			}
			
			
			//up
			ImageModel imageModel = itemModel.getImageModel();
			
			
			
			if (imageModel != null) {
				try {
					String chartName = imageModel.getName();
					if (chartName==null || chartName.length()<=1) {
						continue;
					}
					Image image = Image.getInstance(generator.getChartDirName()+chartName);
					if (imageModel.getWidth() > 0) {
						image.scaleAbsoluteWidth(imageModel.getWidth());
					}
					if (imageModel.getHeight() > 0) {
						image.scaleAbsoluteHeight(imageModel.getHeight());
					}
 
					
					float xh = x;
					float yw = y;
					
					if(itemModel.getPage()!=0){
						if(itemModel.getPage()==writer.getPageNumber()){
							image.setAbsolutePosition(xh, yw);
							if(StringUtils.isNotEmpty(itemModel.getIsBG())
									&& itemModel.getIsBG().equals("true")){
								writer.getDirectContentUnder().addImage(image, true);
							}else{
								writer.getDirectContent().addImage(image, true);
							}
						}
					}else{
						image.setAbsolutePosition(xh, yw);
						if(StringUtils.isNotEmpty(itemModel.getIsBG())
								&& itemModel.getIsBG().equals("true")){
							writer.getDirectContentUnder().addImage(image, true);
						}else{
							writer.getDirectContent().addImage(image, true);
						}
//						writer.getDirectContent().addTemplate(templateTotal, x, y);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Font font = itemModel.getFont();
				String text = itemModel.getText();
				if (text.indexOf("@title@") >= 0) {
					text = text.replaceAll("@title@", currentChapter);
				} 
				
				float pagex = 0;
				float pagey = 0;
				if (text.indexOf("@pageNum@/") >= 0) {
					flagPage = "1";
				}else if(("@pageNum@".equals(text) || "第@pageNum@页".equals(text) || "第 @pageNum@ 页".equals(text)) &&  StringUtils.isEmpty(itemModel.getItemName()) ){
					flagPage = "2";
				}else if("第@pageNum@页，共".equals(text)){
					flagPage = "";
				}
				if (text.indexOf("@pageNum@") >= 0) {
					int pageNumber = writer.getPageNumber();
					
					//从开始加页眉页脚的地方为第一页
					if(begin!=0 && begin>1){
					   pageNumber = pageNumber-(begin-1)-beginNum;
					}
					if(start!=0 && start>1){
						pageNumber = pageNumber-(start-1);
					}
					
					//TODO 重构第二页目录的页码
					if (currentChapter.equals("目录") ){
						if (!catalogFirst && catalogSecond) {
							pageNumber = catalogPageNum;
						}
						
					}
					
					if(currentChapter.equals("目录") && generator != null 
							&& generator.getCatalogModel() != null 
							&& generator.getCatalogModel().getRefList() != null 
							&& generator.getCatalogModel().getRefList().size()>0 
							&& generator.getCatalogModel().getRefList().get(0) != null
							&& generator.getCatalogModel().getRefList().get(0).getPageNumber() == 0){
						mls[ml++] = pageNumber;
						// strs[sl++] = itemModel.getIsEven();
						
					}else if(currentChapter.equals("目录") 
							&& generator != null 
							&& generator.getCatalogModel() != null 
							&& generator.getCatalogModel().getRefList() != null 
							&& generator.getCatalogModel().getRefList().size()>0 
							&& generator.getCatalogModel().getRefList().get(0) != null
							&& generator.getCatalogModel().getRefList().get(0).getPageNumber() != 0){
						if(StringUtils.isNotEmpty(itemModel.getNumericType()) && itemModel.getNumericType().equals("roman")){
							text = text.replaceAll("@pageNum@", ""+RomanNumberUtil.toRnums(mls[m++]));
						}else{
							text = text.replaceAll("@pageNum@", ""+mls[m++]);
							
						}
					}else{
						if(StringUtils.isNotEmpty(itemModel.getNumericType()) && itemModel.getNumericType().equals("roman")){
							text = text.replaceAll("@pageNum@", ""+RomanNumberUtil.toRnums(pageNumber));
						}else{
							if(reportType.equals("beautySkinCare")){
								if(pageNumber<10){
									text = text.replaceAll("@pageNum@", "0"+pageNumber);
								}else{
									text = text.replaceAll("@pageNum@", ""+pageNumber);
								}
							}else{
								
								text = text.replaceAll("@pageNum@", ""+pageNumber);
							}
						}
					}

					pagex = itemModel.getX();
					pagey = itemModel.getY();
					int pns = Integer.toString(pageNumber).length();
					if(flagPage.equals("1")){
						writer.getDirectContent().addTemplate(templateTotal, pagex+font.getSize()/3+2*pns, pagey-1);
					}else{
						writer.getDirectContent().addTemplate(templateTotal, pagex+font.getSize()/3*5+2*pns, pagey-1);
					}
					fontTemp = font;
				} 
				
				if(StringUtils.isNotEmpty(itemModel.getIsEven())){
					if(currentChapter.equals("目录") 
							&& generator != null 
							&& generator.getCatalogModel() != null 
							&& generator.getCatalogModel().getRefList() != null
							&& generator.getCatalogModel().getRefList().size()>0
							&& generator.getCatalogModel().getRefList().get(0) != null
							&& generator.getCatalogModel().getRefList().get(0).getPageNumber() != 0){
						if (StringUtils.isNotEmpty(itemModel.getIsEven()) && "false".equals(itemModel.getIsEven())) {
							int k = m-1 ;
							int n = mls[k];
							Chunk chunk = new Chunk(text, font);
							if(itemModel.getBgcolor()!=null && !"".equals(itemModel.getBgcolor())){
								BaseColor borderColor = StyleUtils.getColor(itemModel.getBgcolor());
								if(itemModel.getWidth()!=0){
									if (n != 0 && n % 2 == 1) {
										chunk.setBackground(borderColor, itemModel.getWidth(), 5, 2, 1);
									}
									if (n != 0 && n % 2 == 0){
										chunk.setBackground(borderColor,2, 5, itemModel.getWidth(), 1);
									}
									//chunk.setBackground(color, extraLeft, extraBottom, extraRight, extraTop)
								}else{
									chunk.setBackground(borderColor);
								}
							}
							
							String link = itemModel.getLink();
							if (link != null) {
								chunk.setAnchor(link);
							}
							Phrase phrase = new Phrase(chunk);
							
							int align = StyleUtils.getAlign(itemModel.getAlign());
							
							if (n != 0 && n % 2 == 0) {
								ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, itemModel.getZ(), itemModel.getH(), 0);
							}
							if (n != 0 && n % 2 == 1){
								ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, itemModel.getX(), itemModel.getY(), 0);
							}
						}
					}else{
						String supStr=" ";
						if(StringUtils.isNotEmpty(itemModel.getSup())){
							supStr=itemModel.getSup();
						}
						String[] testArr = text.split(supStr);
						List<String> listSup = new ArrayList<String>();
						if(StringUtils.isNotEmpty(itemModel.getSup())){
							listSup.add(testArr[0]);
							listSup.add(supStr);
							listSup.add(testArr[1]);
						}else{
							listSup.add(text);
						}
						
						Phrase phrase = new Phrase();
						for(int i=0;i<listSup.size();i++){
							
							Chunk chunk = new Chunk();
							if(i==1){
								font.setSize(5f);
								chunk = new Chunk(supStr, font);
								chunk.setTextRise(5);
							}else{
								
								chunk = new Chunk(listSup.get(i), font);
							}
							
							if(itemModel.getBgcolor()!=null && !"".equals(itemModel.getBgcolor())){
								BaseColor borderColor = StyleUtils.getColor(itemModel.getBgcolor());
								if(itemModel.getWidth()!=0){
									if (pageNumber22 % 2 == 0) {
										chunk.setBackground(borderColor, itemModel.getWidth(), 5, 2, 1);
									}else{
										chunk.setBackground(borderColor,2, 5, itemModel.getWidth(), 1);
									}
									//chunk.setBackground(color, extraLeft, extraBottom, extraRight, extraTop)
								}else{
									chunk.setBackground(borderColor);
								}
							}
							
							String link = itemModel.getLink();
							if (link != null) {
								chunk.setAnchor(link);
							}
							phrase.add(chunk);
							font = itemModel.getFont();
						}
						
						
						int align = StyleUtils.getAlign(itemModel.getAlign());
						ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, x, y, 0);
					}
				}else{
					String supStr=" ";
					if(StringUtils.isNotEmpty(itemModel.getSup())){
						supStr=itemModel.getSup();
					}
					String[] testArr = text.split(supStr);
					List<String> listSup = new ArrayList<String>();
					if(StringUtils.isNotEmpty(itemModel.getSup())){
						listSup.add(testArr[0]);
						listSup.add(supStr);
						listSup.add(testArr[1]);
					}else{
						listSup.add(text);
					}
					
					Phrase phrase = new Phrase();
					for(int i=0;i<listSup.size();i++){
						Chunk chunk = new Chunk();
						if(i==1){
							font.setSize(5f);
							chunk = new Chunk(supStr, font);
							chunk.setTextRise(5);
						}else{
							
							chunk = new Chunk(listSup.get(i), font);
						}
						if(itemModel.getBgcolor()!=null && !"".equals(itemModel.getBgcolor())){
							BaseColor borderColor = StyleUtils.getColor(itemModel.getBgcolor());
							if(itemModel.getWidth()!=0){
								if (pageNumber22 % 2 == 1) {
									chunk.setBackground(borderColor, itemModel.getWidth(), 5, 2, 1);
								}else{
									chunk.setBackground(borderColor,2, 5, itemModel.getWidth(), 1);
								}
								//chunk.setBackground(color, extraLeft, extraBottom, extraRight, extraTop)
							}else{
								chunk.setBackground(borderColor);
							}
						}
						
						String link = itemModel.getLink();
						if (link != null) {
							chunk.setAnchor(link);
						}
						phrase.add(chunk);
						font = itemModel.getFont();
					}
					
					int align = StyleUtils.getAlign(itemModel.getAlign());
					ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, x, y, 0);
					
					
					
//					Chunk chunk = new Chunk(text, font);
//					if(itemModel.getBgcolor()!=null && !"".equals(itemModel.getBgcolor())){
//						BaseColor borderColor = StyleUtils.getColor(itemModel.getBgcolor());
//						if(itemModel.getWidth()!=0){
//							if (pageNumber22 % 2 == 1) {
//								chunk.setBackground(borderColor, itemModel.getWidth(), 5, 2, 1);
//							}else{
//								chunk.setBackground(borderColor,2, 5, itemModel.getWidth(), 1);
//							}
//							//chunk.setBackground(color, extraLeft, extraBottom, extraRight, extraTop)
//						}else{
//							chunk.setBackground(borderColor);
//						}
//					}
//					
//					String link = itemModel.getLink();
//					if (link != null) {
//						chunk.setAnchor(link);
//					}
//					Phrase phrase = new Phrase(chunk);
//					
//					int align = StyleUtils.getAlign(itemModel.getAlign());
//					ColumnText.showTextAligned(writer.getDirectContent(), align, phrase, x, y, 0);
				}
			}
		}
		if (currentChapter.equals("目录") ){
			if (!catalogFirst && catalogSecond) {
				catalogPageNum++;
			}
		}
		if(chapterEnd){
			chapterEnd=false;
		}
		if(chapterStart){
			chapterStart=false;
		}
	}
	
	public void onCloseDocument(PdfWriter writer,Document document){
		int allPageNumber = writer.getPageNumber()-1;
		if(headerModel != null ){
		if(headerModel.getBegin() != 0 && headerModel.getBegin() > 1){
			allPageNumber = allPageNumber- 1;//00000
		}
		if(beginNo!=0) {
			allPageNumber=allPageNumber-(allPageNumber-beginNo)-1;
		}
		generator.setAllPageNumber(allPageNumber);
		templateTotal.beginText();  
//			templateTotal.setFontAndSize(BaseFont.createFont("/font/simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED), 8);
		templateTotal.setFontAndSize(fontTemp.getBaseFont(), fontTemp.getSize());
		
		String apn = "";
		if(flagPage.equals("1")){
			apn = allPageNumber+"";
		}else if(flagPage.equals("2")){
			apn="";
		}else{
			apn = allPageNumber+"页";
		}
		int apns = Integer.toString(allPageNumber).length();
		if(apns == 1) apns = 4;
		templateTotal.showTextAligned(PdfContentByte.ALIGN_CENTER, apn, fontTemp.getSize()/3*5-apns+2, 1, 0);
		templateTotal.endText();
		}
		
	}
	
	private void addImage(PdfWriter writer,List<String> listImage){
		if (listImage!=null && listImage.size()>0){
			Image image;
			try {
				image = Image.getInstance(generator.getChartDirName()+"images/beautySkinCare/"+listImage.get(0));
				image.scaleAbsoluteWidth(Float.parseFloat(listImage.get(1)));
				image.scaleAbsoluteHeight(Float.parseFloat(listImage.get(2)));
				image.setAbsolutePosition(Float.parseFloat(listImage.get(3)), Float.parseFloat(listImage.get(4)));
				writer.getDirectContent().addImage(image, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public HeaderModel getHeaderModel() {
		return headerModel;
	}
	public void setHeaderModel(HeaderModel headerModel) {
		this.headerModel = headerModel;
	}

	public void setGenerator(PdfGenerator generator) {
		this.generator = generator;
	}

	public void setAllPage(int allPage) {
		this.allPage = allPage;
	}

	public float[] getLeftMargins() {
		return leftMargins;
	}

	public void setLeftMargins(float[] leftMargins) {
		this.leftMargins = leftMargins;
	}

	public float[] getRightMargins() {
		return rightMargins;
	}

	public void setRightMargins(float[] rightMargins) {
		this.rightMargins = rightMargins;
	}

	public int getBeginNum() {
		return beginNum;
	}

	public void setBeginNum(int beginNum) {
		this.beginNum = beginNum;
	}


	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
}
