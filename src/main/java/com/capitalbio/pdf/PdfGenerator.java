package com.capitalbio.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import com.capitalbio.pdf.impl.ChineseSplitCharater;
import com.capitalbio.pdf.impl.FilletCellEvent;
import com.capitalbio.pdf.impl.FilletTableEvent;
import com.capitalbio.pdf.model.CatalogModel;
import com.capitalbio.pdf.model.CellModel;
import com.capitalbio.pdf.model.ChapterModel;
import com.capitalbio.pdf.model.ComplexModel;
import com.capitalbio.pdf.model.DocModel;
import com.capitalbio.pdf.model.ImageModel;
import com.capitalbio.pdf.model.LineModel;
import com.capitalbio.pdf.model.ListModel;
import com.capitalbio.pdf.model.Model;
import com.capitalbio.pdf.model.NewPageModel;
import com.capitalbio.pdf.model.RefModel;
import com.capitalbio.pdf.model.RowModel;
import com.capitalbio.pdf.model.SectionModel;
import com.capitalbio.pdf.model.TableModel;
import com.capitalbio.pdf.model.TextModel;
import com.capitalbio.pdf.operation.EventUtil;
import com.capitalbio.pdf.util.NumUtil;
import com.capitalbio.pdf.util.SealPdfUtil;
import com.capitalbio.pdf.util.SplitAndMosaicPdfUtil;
import com.capitalbio.pdf.util.StringUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.draw.LineSeparator;

public class PdfGenerator {

	private CatalogModel catalogModel;
	private Map<String, Integer> chapterMap = new HashMap<String, Integer>();
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	private String fileName;
	private String chartDirName;
	private String tempFileName;
	private int allPageNumber = 0;
	private PdfWriter writerTmp;
	private DocModel docModel;
	private int catalogSecondPageNum;

	public void generatePdf(DocModel docModel, String reportType) throws Exception {

		createDocument(docModel, tempFileName, reportType);

		if (StringUtils.isNotEmpty(docModel.getGetPagechapterName())) {

		}

		PdfReader reader1 = new PdfReader(tempFileName);
		List<HashMap<String, Object>> list = SimpleBookmark.getBookmark(reader1);

		int cals = 0;
		int cale = 0;
		int hPage = 0;
		String isCol = "false";
		Map<String, Object> map = null;
		int del = 0;
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			map = list.get(i);
			if (map.get("Title").equals("目录") && isCol == "false") {
				cals = Integer
						.parseInt(map.get("Page").toString().substring(0, map.get("Page").toString().indexOf(" "))) - 1; // 9
				cale = Integer.parseInt(list.get(i + 1).get("Page").toString().substring(0,
						list.get(i + 1).get("Page").toString().indexOf(" "))); // 14
				isCol = "true";
			}
			if (StringUtils.isNotEmpty(docModel.getGetPagechapterName())
					&& map.get("Title").equals(docModel.getGetPagechapterName().trim()) && hPage == 0) {
				hPage = Integer
						.parseInt(map.get("Page").toString().substring(0, map.get("Page").toString().indexOf(" ")));
			}
			if (map.get("Title").equals("致客户")) {
				del = i;
			}
		}

		int calt = Integer.parseInt(list.get(list.size() - 1).get("Page").toString().substring(0,
				list.get(list.size() - 1).get("Page").toString().indexOf(" ")));
		String pageIndex = "";
		if (isCol.equals("true")) {
			list.remove(list.size() - 1);
			if (cals == 0) {
				pageIndex = calt + "-" + (calt + cale - cals - 2) + "," + cale + "-" + (calt - 1);
			} else {
				pageIndex = "1-" + cals + "," + calt + "-" + (calt + cale - cals - 2) + "," + cale + "-" + (calt - 1);
			}
		} else {
			pageIndex = "1-" + cals + "," + calt + "-" + (calt + cale - cals - 2) + "," + cale + "-" + (calt - 1);
		}

		// 删除不需要展示的书签
		if (StringUtils.isNotEmpty(docModel.getNotDiplayBookMarksChapter())) {
			String notDiplayBookMarksChapter = docModel.getNotDiplayBookMarksChapter();
			String[] notDiplayBookMarksChapterArr = notDiplayBookMarksChapter.split(",");
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> mapTemp = list.get(i);
				String title = mapTemp.get("Title").toString();
				for (int k = 0; k < notDiplayBookMarksChapterArr.length; k++) {
					if (title.equals(notDiplayBookMarksChapterArr[k])) {
						list.remove(mapTemp);
					}
				}
			}
		}

		if (hPage != 0) {
			// 盖章
			String tmpFilePath = fileName.replace(".pdf", "tmp.pdf");
			reader1.selectPages(pageIndex);
			PdfStamper stamper = new PdfStamper(reader1, new FileOutputStream(tmpFilePath));
			stamper.setOutlines(list);
			stamper.close();
			reader1.close();
			// 食物不耐受14模块
			if (reportType.equals("foodIntolerance14HB")) {
				// 先拼接再盖章
				String firstFilePath = tempFileName.replace("temp.pdf", "foodIntolerance14_begin.pdf");
				String tFile = fileName.replace(".pdf", "foodIntolerance14_tmp.pdf");
				SplitAndMosaicPdfUtil.mosaicTwoPdf(firstFilePath, tmpFilePath, tFile);
				tmpFilePath = tFile;
				// 盖章
				hPage = 2;
			}
			SealPdfUtil.sealPdf(hPage, tmpFilePath, fileName, reportType);
		} else {
			reader1.selectPages(pageIndex);
			PdfStamper stamper = new PdfStamper(reader1, new FileOutputStream(fileName));
			stamper.setOutlines(list);
			stamper.close();
			reader1.close();
		}

	}

	/**
	 * 给pdf文件添加印章
	 * 
	 * @param InPdfFile
	 *            要加印章的原pdf文件路径
	 * @param outPdfFile
	 *            加了印章后要输出的路径
	 * @param markImagePath
	 *            印章图片路径
	 * @param page
	 *            pdf的第几页加印章
	 * @throws Exception
	 */
	public static void addPdfMark(String InPdfFile, String outPdfFile, String markImagePath, int page, int x, int y,
			int weight, int height) throws Exception {
		PdfReader reader = new PdfReader(InPdfFile, "PDF".getBytes());
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));
		Image img = Image.getInstance(markImagePath);// 选择图片
		img.scaleAbsolute(weight, height);// 控制图片大小
		img.setAbsolutePosition(x, y);// 控制图片位置
		PdfContentByte under = stamp.getUnderContent(page); // 第几页加印章
		under.addImage(img);
		stamp.close();// 关闭

		File tempfile = new File(InPdfFile);

		if (tempfile.exists()) {
			tempfile.delete();
		}

	}

	private void createDocument(DocModel docModel, String fileName, String reportType) throws Exception {
		float[] margins = docModel.getMargins();

		this.setDocModel(docModel);
		Document doc = new Document(docModel.getPageSize(), margins[0], margins[1], margins[2], margins[3]);
		if (reportType.equals("7")) {
			doc = new Document(docModel.getPageSize().rotate(), margins[0], margins[1], margins[2], margins[3]);
		}
		float[] leftMargins = docModel.getleftMargins();
		/*
		 * if(leftMargins!=null){ doc = new Document(docModel.getPageSize(),
		 * leftMargins[0], leftMargins[1], leftMargins[2], leftMargins[3]); }
		 */
		FileOutputStream out = new FileOutputStream(fileName);
		PdfWriter writer = PdfWriter.getInstance(doc, out);

		HeaderFooter header = new HeaderFooter();
		if (docModel.getleftMargins() != null && !"".equals(docModel.getleftMargins())) {
			header.setLeftMargins(docModel.getleftMargins());
		}
		if (docModel.getrightMargins() != null && !"".equals(docModel.getrightMargins())) {
			header.setRightMargins(docModel.getrightMargins());
		}
		// --修改页眉开始页
		if (docModel.getHeader() != null && docModel.getHeader().getBeginNum() != null) {
			header.setBeginNum(Integer.parseInt(docModel.getHeader().getBeginNum()));
		}
		header.setReportType(reportType);
		header.setGenerator(this);
		header.setHeaderModel(docModel.getHeader());
		header.setAllPage(allPageNumber);
		writer.setPageEvent(header);
		PdfPageEventSelf pps = new PdfPageEventSelf();
		pps.setGenerator(this);
		writer.setPageEvent(pps);
		writer.setStrictImageSequence(true);
		writerTmp = writer;
		doc.open();

		List<Model> models = docModel.getModels();
		ChapterModel chapterModel = null;
		Chapter chapter = null;

		for (Model model : models) {
			if (model.getClass().isAssignableFrom(ChapterModel.class)) {
				chapterModel = (ChapterModel) model;

				if (StringUtils.isNotEmpty(chapterModel.getHengBan()) && chapterModel.getHengBan().equals("true")) {
					RectangleReadOnly only = new RectangleReadOnly(842f, 595f);
					doc.setPageSize(only);
					if (StringUtils.isNotEmpty(chapterModel.getMtop())
							&& StringUtils.isNotEmpty(chapterModel.getMbottom())
							&& StringUtils.isNotEmpty(chapterModel.getMleft())
							&& StringUtils.isNotEmpty(chapterModel.getMright())) {

						doc.setMargins(Float.parseFloat(chapterModel.getMtop()),
								Float.parseFloat(chapterModel.getMbottom()), Float.parseFloat(chapterModel.getMleft()),
								Float.parseFloat(chapterModel.getMright()));
					} else {
						doc.setMargins(margins[2], margins[3], margins[0], margins[1]);
					}
				} else {
					if (reportType.equals("7")) {
						doc.setPageSize(docModel.getPageSize().rotate());
					} else {
						doc.setPageSize(docModel.getPageSize());
					}
					doc.setMargins(margins[0], margins[1], margins[2], margins[3]);
				}

				chapter = generateChapter(chapterModel, doc, writer);
				if (StringUtils.isNotEmpty(chapterModel.getRealPage()) && chapterModel.getRealPage().equals("false")) {
					continue;
				}
				doc.add(chapter);
			} else if (model.getClass().isAssignableFrom(CatalogModel.class)) {
				CatalogModel catalogModel = (CatalogModel) model;
				this.catalogModel = catalogModel;
				float widthLine = doc.getPageSize().getWidth() - doc.leftMargin() - doc.rightMargin();
				chapter = generateCatalog(catalogModel, widthLine);
				doc.add(chapter);
			}
		}
		doc.setPageSize(docModel.getPageSize());
		doc.setMargins(margins[0], margins[1], margins[2], margins[3]);
		doc.close();
		out.close();
		writer.close();
	}

	private Chapter generateChapter(ChapterModel chapterModel, Document doc, PdfWriter writer) throws Exception {
		Font font = null;
		if (chapterModel.getFontName() == null && chapterModel.getFontSize() == null) {
			font = StyleUtils.getChapterFont();
		} else {
			font = chapterModel.getFont();
		}
		Chunk chunk = new Chunk(chapterModel.getChapterName(), font);
		chunk.setLocalDestination(chapterModel.getId());
		Paragraph cTitle = new Paragraph(chunk);
		if (chapterModel.getAlign() == null) {
			cTitle.setAlignment(Element.ALIGN_CENTER);
		} else {
			if (chapterModel.getAlign().equals("left")) {
				cTitle.setAlignment(Element.ALIGN_LEFT);
			} else if (chapterModel.getAlign().equals("right")) {
				cTitle.setAlignment(Element.ALIGN_RIGHT);
			}

		}
		// 设置每章节标题下的间距
		// spacingafter 为空时 标题下的间距是8f
		if (StringUtils.isNotEmpty(chapterModel.getSpacingAfter())) {
			cTitle.setSpacingAfter(Float.parseFloat(chapterModel.getSpacingAfter()));
		} else {
			cTitle.setSpacingAfter(8f);
		}
		int chapterNumber = chapterModel.getChapterNumber();
		Chapter chapter = new Chapter(cTitle, chapterNumber);

		// Chapter chapter = new Chapter(chapterNumber);

		if (chapterModel.getIsNewP() != null && chapterModel.getIsNewP().contains("false")) {
			chapter.setTriggerNewPage(false);
		}
		if (chapterNumber <= 0) {
			chapter.setNumberDepth(0);
		}
		// chapter.setBookmarkOpen(false);//树形结构默认打开，如需树形结构关闭将chapter.setBookmarkOpen(false)
		for (Model model : chapterModel.getModels()) {
			if (model.getClass().isAssignableFrom(TextModel.class)) {
				TextModel textModel = (TextModel) model;
				if (StringUtils.isNotEmpty(textModel.getHasSpacing()) && textModel.getHasSpacing().equals("false")) {
					Chunk chunkText = generateSimpleText(textModel);
					chapter.add(chunkText);
				} else {
					List<Paragraph> paraList = generateText(textModel);

					for (Paragraph para : paraList) {
						chapter.add(para);
						chapter.setTriggerNewPage(true);
					}
				}
			} else if (model.getClass().isAssignableFrom(SectionModel.class)) {
				SectionModel sectionModel = (SectionModel) model;
				generateSection(sectionModel, chapter, doc);
			} else if (model.getClass().isAssignableFrom(ImageModel.class)) {
				ImageModel imageModel = (ImageModel) model;
				Image image = generateImage(imageModel);
				if (StringUtils.isNotEmpty(imageModel.getBg()) && imageModel.getBg().equals("true")) {
					// TODO
					writer.getDirectContent().addImage(image);
					// writer.add(image);
				} else {
					chapter.add(image);
				}

				// if(StringUtils.isNotEmpty(imageModel.getSingle())
				// && imageModel.getSingle().equals("false")){
				// Paragraph paragraph = new Paragraph();
				// if (image != null) {
				// paragraph.add(image);
				// if(imageModel.getSpacing()!=0){
				// paragraph.setSpacingAfter(imageModel.getSpacing());
				// }
				// if(imageModel.getSpacingB()!=0){
				// paragraph.setSpacingBefore(imageModel.getSpacingB());
				// }
				//
				// }
				// }else{
				// if (image != null) {
				// chapter.add(image);
				// }
				// }
			} else if (model.getClass().isAssignableFrom(ComplexModel.class)) {
				ComplexModel complexModel = (ComplexModel) model;
				Paragraph paragraph = new Paragraph();
				Phrase phrase = generateComplex(complexModel);
				paragraph.add(phrase);
				if (complexModel.getIndent() != null) {
					paragraph.setFirstLineIndent(StyleUtils.getIndent(complexModel.getIndent()));
				} else {
					paragraph.setFirstLineIndent(20);
				}
				if (StringUtils.isNotEmpty(complexModel.getAlign())) {
					if ("center".equals(complexModel.getAlign())) {
						paragraph.setAlignment(Element.ALIGN_CENTER);
					}
				}
				String spacing = complexModel.getSpacing();
				if (StringUtils.isNotEmpty(spacing)) {
					paragraph.setSpacingAfter(Float.parseFloat(spacing));
				} else {
					paragraph.setSpacingAfter(8f);
				}
				if (StringUtils.isNotEmpty(complexModel.getLeading())) {
					paragraph.setLeading(Float.parseFloat(complexModel.getLeading()));
				}
				if (StringUtils.isNotEmpty(complexModel.getSpacingB())) {
					paragraph.setSpacingBefore(Float.parseFloat(complexModel.getSpacingB()));
				}
				if (StringUtils.isNotEmpty(complexModel.getIndentationLeft())) {
					paragraph.setIndentationLeft(Float.parseFloat(complexModel.getIndentationLeft()));
				}
				if (StringUtils.isNotEmpty(complexModel.getIndentationRight())) {
					paragraph.setIndentationRight(Float.parseFloat(complexModel.getIndentationRight()));
				}

				chapter.add(paragraph);
			} else if (model.getClass().isAssignableFrom(TableModel.class)) {
				TableModel tableModel = (TableModel) model;
				PdfPTable table = generateTable(tableModel, false);
				chapter.add(table);
			} else if (model.getClass().isAssignableFrom(ListModel.class)) {
				ListModel listModel = (ListModel) model;
				com.itextpdf.text.List list = generateList(listModel);
				chapter.add(list);
			} else if (model.getClass().isAssignableFrom(NewPageModel.class)) {
				boolean result = doc.newPage();
				NewPageModel newPageModel = (NewPageModel) model;
				if (StringUtils.isNotEmpty(newPageModel.getIsNew()) && newPageModel.getIsNew().equals("true")) {
					chapter.newPage();
				}
			} else if (model.getClass().isAssignableFrom(LineModel.class)) {
				LineModel lineModel = (LineModel) model;
				LineSeparator line = generateLine(lineModel);
				Chunk chunkLine = new Chunk(line);
				chapter.add(chunkLine);
			}
		}
		return chapter;
	}

	/**
	 * 生成横线
	 * 
	 * @param lineModel
	 * @return
	 */
	private LineSeparator generateLine(LineModel lineModel) {
		LineSeparator line = new LineSeparator();
		// 颜色
		if (StringUtils.isNotEmpty(lineModel.getColor())) {
			line.setLineColor(StyleUtils.getColor(lineModel.getColor()));
		}
		// 位置
		if (StringUtils.isNotEmpty(lineModel.getAlign())) {
			line.setAlignment(StyleUtils.getAlign(lineModel.getAlign()));
		} else {
			// 默认居中
			line.setAlignment(1);
		}
		// 宽度
		if (StringUtils.isNotEmpty(lineModel.getWidth())) {
			line.setLineWidth(Float.parseFloat(lineModel.getWidth()));
		} else {
			// 默认为0.5宽度
			line.setLineWidth(0.5f);
		}
		// 占比
		if (StringUtils.isNotEmpty(lineModel.getPercentage())) {
			line.setPercentage(Float.parseFloat(lineModel.getPercentage()));
		} else {
			// 默认百分百
			line.setPercentage(100f);
		}
		// 偏移
		if (StringUtils.isNotEmpty(lineModel.getOffset())) {
			line.setOffset(Float.parseFloat(lineModel.getOffset()));
		} else {
			// 默认0
			line.setOffset(0f);
		}
		//
		return line;
	}

	private void generateSection(SectionModel sectionModel, Section parent, Document doc) throws Exception {
		Font font = null;
		if (sectionModel.getFontName() == null && sectionModel.getFontSize() == null) {
			font = StyleUtils.getSectionFont();
		} else {
			font = sectionModel.getFont();
		}
		Chunk chunk = new Chunk(sectionModel.getSectionName(), font);
		chunk.setLocalDestination(sectionModel.getId());// 链接跳转
		if (!"".equals(sectionModel.getBgcolor()) && sectionModel.getBgcolor() != null) {
			float num = 420 - chunk.getWidthPoint();
			BaseColor baseColor = StyleUtils.getColor(sectionModel.getBgcolor());
			chunk.setBackground(baseColor, 20, 3, num + 20, 3);
		}

		Paragraph cTitle = new Paragraph(chunk);
		cTitle.setSpacingAfter(8f);
		Section section = parent.addSection(cTitle);
		if (!"".equals(sectionModel.isNewPage())) {
			section.setTriggerNewPage(sectionModel.isNewPage());
		}
		section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
		if (!"".equals(sectionModel.getDepth())) {
			section.setNumberDepth(sectionModel.getDepth());
		}
		if (StringUtils.isNotEmpty(sectionModel.getAlign()) && sectionModel.getAlign().equals("center")) {
			cTitle.setAlignment(Element.ALIGN_CENTER);
		}

		for (Model model : sectionModel.getModels()) {
			if (model.getClass().getName().equals(TextModel.class.getName())) {
				TextModel textModel = (TextModel) model;
				List<Paragraph> paraList = generateText(textModel);
				for (Paragraph para : paraList) {
					section.add(para);
				}
			} else if (model.getClass().getName().equals(SectionModel.class.getName())) {
				SectionModel subSectionModel = (SectionModel) model;
				generateSection(subSectionModel, section, doc);
			} else if (model.getClass().isAssignableFrom(ImageModel.class)) {
				ImageModel imageModel = (ImageModel) model;
				Image image = generateImage(imageModel);
				if (image != null) {
					section.add(image);
				}
			} else if (model.getClass().isAssignableFrom(ComplexModel.class)) {
				ComplexModel complexModel = (ComplexModel) model;
				Paragraph paragraph = new Paragraph();
				Phrase phrase = generateComplex(complexModel);
				paragraph.add(phrase);
				if (complexModel.getIndent() != null) {
					paragraph.setFirstLineIndent(StyleUtils.getIndent(complexModel.getIndent()));
				} else {
					paragraph.setFirstLineIndent(20);
				}
				if (StringUtils.isNotEmpty(complexModel.getAlign())) {
					if ("center".equals(complexModel.getAlign())) {
						paragraph.setAlignment(Element.ALIGN_CENTER);
					}
				}
				paragraph.setSpacingAfter(8f);
				section.add(paragraph);
			} else if (model.getClass().isAssignableFrom(TableModel.class)) {
				TableModel tableModel = (TableModel) model;
				PdfPTable table = generateTable(tableModel, false);
				section.add(table);
			} else if (model.getClass().isAssignableFrom(ListModel.class)) {
				ListModel listModel = (ListModel) model;
				com.itextpdf.text.List list = generateList(listModel);
				section.add(list);
			} else if (model.getClass().isAssignableFrom(NewPageModel.class)) {
				doc.newPage();
			}
		}
	}

	private PdfPTable generateTable(TableModel tableModel, boolean inCell) throws Exception {
		PdfPTable table = null;
		if (tableModel.getCols() != null) {
			table = new PdfPTable(tableModel.getCols());
		} else {
			int cols = tableModel.getColNumber();
			table = new PdfPTable(cols);
		}
		float borderWidth = 0;
		if (StringUtils.isNotEmpty(tableModel.getBorderWidth())) {
			borderWidth = Float.parseFloat(tableModel.getBorderWidth());
		}

		// cell 跨页处理：
		// table.setSplitLate(true);
		// table.setSplitRows(true);
		if (StringUtils.isNotEmpty(tableModel.getKeepTogether()) && tableModel.getKeepTogether().equals("true")) {
			table.setKeepTogether(true);// 表格在一页
		}

		if (!inCell) {
			if (tableModel.getLeading() > 0) {
				table.setSpacingBefore(tableModel.getLeading());// 设置表格上面空白宽度
			} else {
				table.setSpacingBefore(10f);// 设置表格上面空白宽度
			}
			if (StringUtils.isNotEmpty(tableModel.getHeaderRowNum())) {
				table.setHeaderRows(Integer.parseInt(tableModel.getHeaderRowNum()));
			} else {
				table.setHeaderRows(1);
			}
		}
		if (tableModel.getWidth() > 0) {
			table.setWidthPercentage(tableModel.getWidth());
		} else {
			table.setWidthPercentage(100);
		}
		if (tableModel.getBorder() >= 0) {
			table.getDefaultCell().setBorder(tableModel.getBorder());
		}
		RowModel headerModel = tableModel.getHeader();
		// TODO

		BaseColor borderColor = new BaseColor(221, 221, 221);
		if (tableModel.getBorderColor() != null && !"".equals(tableModel.getBorderColor())) {
			borderColor = StyleUtils.getColor(tableModel.getBorderColor());
		}
		table.getDefaultCell().setBorderColor(borderColor);
		if (headerModel != null) {
			for (CellModel cellModel : headerModel.getCells()) {
				PdfPCell cell = generateCell(cellModel, headerModel, tableModel);
				// cell.setMinimumHeight(14);
				// TODO
				if (borderWidth > 0) {

					cell.setBorderWidth(borderWidth);
				}
				table.addCell(cell);
			}
		}
		// for (RowModel rowModel:tableModel.getRows()) {
		// for (CellModel cellModel:rowModel.getCells()) {
		// PdfPCell cell = generateCell(cellModel, rowModel, tableModel);
		//// cell.setMinimumHeight(14);
		//
		// table.addCell(cell);
		// }
		// }

		String[] split = null;
		if (tableModel.getRowSpanNum() != null && tableModel.getRowSpanNum().length() > 0) {
			split = tableModel.getRowSpanNum().split(",");
		}
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		Map<Integer, Map<Integer, Integer>> mapAll = new HashMap<Integer, Map<Integer, Integer>>();
		if (split != null && split.length > 0) {
			for (int s = 0; s < split.length; s++) {
				int k = 0;
				int h = 0;
				int rowSpanNum = 0;
				rowSpanNum = Integer.parseInt(split[s]) - 1;
				map = new HashMap<Integer, Integer>();
				for (int i = 0; i < tableModel.getRows().size(); i++) {
					RowModel rowModel = tableModel.getRows().get(i);
					if (split != null
							&& ((i < tableModel.getRows().size() - 1
									&& !rowModel.getCells()
											.get(rowSpanNum).getText().equals(tableModel.getRows().get(i + 1).getCells()
													.get(rowSpanNum).getText()))
									|| i == tableModel.getRows().size() - 1)) {
						h = i - k + 1;
						map.put(k, h);
						k = i + 1;
					}
				}
				mapAll.put(rowSpanNum, map);
			}
		}
		if (split != null && mapAll.size() > 0) {
			for (int i = 0; i < tableModel.getRows().size(); i++) {
				RowModel rowModel = tableModel.getRows().get(i);
				for (int j = 0; j < rowModel.getCells().size(); j++) {
					CellModel cellModel = rowModel.getCells().get(j);
					// if( j==rowSpanNum && map.get(i)!=null){
					// cellModel.setRowspan(map.get(i));
					// }
					if (mapAll.get(j) != null && mapAll.get(j).get(i) != null) {
						cellModel.setRowspan(mapAll.get(j).get(i));
					}
					// if(map.get(i)!=null || j!=rowSpanNum){
					// PdfPCell cell = generateCell(cellModel, rowModel,
					// tableModel);
					// table.addCell(cell);
					// }
					if (mapAll.get(j) == null || mapAll.get(j).get(i) != null) {
						PdfPCell cell = generateCell(cellModel, rowModel, tableModel);
						table.addCell(cell);
					}
				}
			}
		} else {
			for (RowModel rowModel : tableModel.getRows()) {
				for (CellModel cellModel : rowModel.getCells()) {
					PdfPCell cell = generateCell(cellModel, rowModel, tableModel);
					// cell.setMinimumHeight(14);
					if (borderWidth > 0) {
						cell.setBorderWidth(borderWidth);
					}
					table.addCell(cell);
				}
			}
		}
		if (StringUtils.isNotEmpty(tableModel.getSpacing())) {
			table.setSpacingAfter(Float.parseFloat(tableModel.getSpacing()));
		}
		if (StringUtils.isNotEmpty(tableModel.getSpacingB())) {
			table.setSpacingBefore(Float.parseFloat(tableModel.getSpacingB()));
		}
		// if(tableModel.getZ()!=null && tableModel.getH()!=null){
		// table.setTotalWidth(700);
		// table.writeSelectedRows(0, -1, 70, 170, writer.getDirectContent());
		// }
		// 设置圆角表格事件
		if (StringUtils.isNotEmpty(tableModel.getEventType())) {
			EventUtil eventUtil = new EventUtil();
			String eventType = tableModel.getEventType();
			FilletTableEvent tableEvent = null;
			if (eventType.contains(",")) {
				// 同时使用边框和背景两个事件
				String[] eventTypeArr = eventType.split(",");
				for (int i = 0; i < eventTypeArr.length; i++) {
					String type = eventTypeArr[i];
					tableEvent = eventUtil.setFilletTableEvent(tableModel, type);
					table.setTableEvent(tableEvent);
				}
			} else {
				// 单独使用一个事件
				tableEvent = eventUtil.setFilletTableEvent(tableModel, eventType);
				table.setTableEvent(tableEvent);
			}
		}
		if (StringUtils.isNotEmpty(tableModel.getAlign())) {
			table.setHorizontalAlignment(StyleUtils.getAlign(tableModel.getAlign()));
		}
		return table;
	}

	private PdfPCell generateCell(CellModel cellModel, RowModel rowModel, TableModel parentTableModel)
			throws Exception {
		TableModel tableModel = cellModel.getTableModel();
		ImageModel imageModel = cellModel.getImageModel();
		TextModel textModel = cellModel.getTextModel();
		ComplexModel complexModel = cellModel.getComplexModel();
		ListModel listModel = cellModel.getListModel();
		String isNew = "";
		if (cellModel.getIsNew() != null) {
			isNew = cellModel.getIsNew();
		}
		PdfPCell cell = null;
		if (tableModel != null) {
			PdfPTable table = generateTable(tableModel, true);
			cell = new PdfPCell(table);
		} else if (imageModel != null && imageModel.getType() == null) {
			Image image = generateImage(imageModel);
			if (image != null) {
				cell = new PdfPCell(image);
			} else {
				cell = new PdfPCell();
			}
		} else if (textModel != null && (isNew.equals("") || isNew.equals("no"))) {

			// Paragraph para = new Paragraph(textModel.getText(),
			// textModel.getFont());
			Paragraph para = new Paragraph();
			Chunk chunk = new Chunk(textModel.getText(), textModel.getFont());
			chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
			para.add(chunk);

			if (textModel.getSup() != null || textModel.getSub() != null || textModel.getText().equals("~")
					|| textModel.getText().equals("×")) {
				para = new Paragraph();
				List<Paragraph> paraList = generateText(textModel);
				for (int i = 0; i < paraList.size(); i++) {
					para.add(paraList.get(i));
				}
			}
			if (!StringUtils.isNotEmpty(cellModel.getSingleHeight())) {
				para.setSpacingAfter(10f);
			}

			if (textModel.getIndent() != null) {
				para.setFirstLineIndent(StyleUtils.getIndent(textModel.getIndent()));
				cell = new PdfPCell();
				cell.addElement(para);
			} else {
				cell = new PdfPCell(para);
			}

			cell.setUseBorderPadding(true);
			if (!StringUtils.isNotEmpty(cellModel.getSingleHeight())) {
				cell.setExtraParagraphSpace(3f);
			}
			if (cellModel.getLeading() > 0) {
				cell.setLeading(cellModel.getLeading(), 1f);
			} else if (StringUtils.isNotEmpty(cellModel.getSingleHeight())
					&& cellModel.getSingleHeight().equals("true")) {
			} else {
				cell.setLeading(1f, 1f);

			}
			// cell.setFixedHeight(100f);
			cell.setSpaceCharRatio(1);
			if (cellModel.getPadding() > 0) {
				cell.setPaddingTop(cellModel.getPadding());
				cell.setPaddingBottom(cellModel.getPadding());
				cell.setPaddingLeft(5);
				cell.setPaddingRight(5);
			} else if (StringUtils.isNotEmpty(cellModel.getSingleHeight())
					&& cellModel.getSingleHeight().equals("true")) {
			} else {
				cell.setPadding(5);
			}
		} else if (textModel != null && isNew.equals("yes")) {
			Paragraph para = null;
			cell = new PdfPCell();
			for (Model model : cellModel.getModels()) {
				if (model.getClass().isAssignableFrom(TextModel.class)) {
					TextModel textModelTmp = (TextModel) model;
					Paragraph paraTmp = new Paragraph();
					List<Paragraph> paraList = generateText(textModelTmp);
					for (int i = 0; i < paraList.size(); i++) {
						cell.addElement(paraList.get(i));
					}
				}
			}

			cell.setUseBorderPadding(true);
			if (!StringUtils.isNotEmpty(cellModel.getSingleHeight())) {
				cell.setExtraParagraphSpace(3f);
			}
			if (cellModel.getLeading() > 0) {
				cell.setLeading(cellModel.getLeading(), 1f);
			} else if (StringUtils.isNotEmpty(cellModel.getSingleHeight())
					&& cellModel.getSingleHeight().equals("true")) {
			} else {
				cell.setLeading(1f, 1f);

			}

			cell.setSpaceCharRatio(1);

			if (cellModel.getPadding() > 0) {
				cell.setPaddingTop(cellModel.getPadding());
				cell.setPaddingBottom(cellModel.getPadding());
				cell.setPaddingLeft(5);
				cell.setPaddingRight(5);
			} else if (StringUtils.isNotEmpty(cellModel.getSingleHeight())
					&& cellModel.getSingleHeight().equals("true")) {
			} else {
				cell.setPadding(5);
			}
		} else if (complexModel != null) {
			Phrase phrase = generateComplex(complexModel);
			cell = new PdfPCell(phrase);
		} else if (listModel != null) {
			com.itextpdf.text.List list = generateList(listModel);
			cell = new PdfPCell();
			cell.addElement(list);
		} else {
			String text = cellModel.getText();

			Paragraph para = new Paragraph();
			if (text != null) {
				Chunk chunk = null;
				chunk = new Chunk(text, cellModel.getFont());
				chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
				para.add(chunk);
			}
			if (imageModel != null) {
				if ("risk".equals(imageModel.getType())) {
					String fontSize = cellModel.getFontSize();
					List<Chunk> chunks = generateRisk(imageModel, fontSize);
					for (Chunk chunk : chunks) {
						para.add(chunk);
					}
				} else if ("freq".equals(imageModel.getType())) {
					List<Chunk> chunks = generateFreq(imageModel.getName());
					for (Chunk chunk : chunks) {
						para.add(chunk);
					}
				} else if ("star".equals(imageModel.getType()) || "circle".equals(imageModel.getType())) {
					String fontSize = cellModel.getFontSize();
					List<Chunk> chunks = generateStar(imageModel, fontSize);
					for (Chunk chunk : chunks) {
						para.add(chunk);
					}
				}
			}
			cell = new PdfPCell(para);
			cell.setPadding(3);
		}
		if (cellModel.getAlign() != null) {
			cell.setHorizontalAlignment(StyleUtils.getAlign(cellModel.getAlign()));
		}
		if (cellModel.getvAlign() != null) {
			switch (cellModel.getvAlign()) {
			case "bottom":
				cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				break;
			case "top":
				cell.setVerticalAlignment(Element.ALIGN_TOP);
				break;
			case "right":
				cell.setVerticalAlignment(Element.ALIGN_RIGHT);
				break;
			case "left":
				cell.setVerticalAlignment(Element.ALIGN_LEFT);
				break;
			case "middle":
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				break;
			default:
				break;
			}
		} else {
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		if (cellModel.getPadding() <= 0) {
			// 上内距
			if (StringUtils.isNotEmpty(cellModel.getPaddingTop())) {
				cell.setPaddingTop(Float.parseFloat(cellModel.getPaddingTop()));
			}
			// 下内距
			if (StringUtils.isNotEmpty(cellModel.getPaddingBottom())) {
				cell.setPaddingBottom(Float.parseFloat(cellModel.getPaddingBottom()));
			}
			// 左内距
			if (StringUtils.isNotEmpty(cellModel.getPaddingLeft())) {
				cell.setPaddingLeft(Float.parseFloat(cellModel.getPaddingLeft()));
			}
			// 右内距
			if (StringUtils.isNotEmpty(cellModel.getPaddingRight())) {
				cell.setPaddingRight(Float.parseFloat(cellModel.getPaddingRight()));
			}
		} else {
			cell.setPadding(cellModel.getPadding());
		}
		if (rowModel.getBgcolor() != null) {
			cell.setBackgroundColor(StyleUtils.getColor(rowModel.getBgcolor()));
		}
		if (cellModel.getBgcolor() != null) {
			cell.setBackgroundColor(StyleUtils.getColor(cellModel.getBgcolor()));
		}
		if (cellModel.getColspan() > 0) {
			cell.setColspan(cellModel.getColspan());
		}
		if (cellModel.getRowspan() > 0) {
			cell.setRowspan(cellModel.getRowspan());
		}
		if (rowModel.getHeight() > 0) {
			cell.setFixedHeight(rowModel.getHeight());
		}
		if (cellModel.getBorder() >= 0) {
			cell.setBorder(cellModel.getBorder());
		}
		BaseColor borderColor = new BaseColor(221, 221, 221);
		if (parentTableModel.getBorderColor() != null && !"".equals(parentTableModel.getBorderColor())) {
			borderColor = StyleUtils.getColor(parentTableModel.getBorderColor());
		}
		cell.setBorderColor(borderColor);
		if (parentTableModel.getBorder() >= 0) {
			cell.setBorder(parentTableModel.getBorder());
		}

		// 下面这两个属性的处理正在测试中，暂时不能用。
		BaseColor borderColorTop = new BaseColor(221, 221, 221);
		if (cellModel.getBorderColorTop() != null && !"".equals(cellModel.getBorderColorTop())) {
			borderColorTop = StyleUtils.getColor(cellModel.getBorderColorTop().toString());
			cell.setBorderColorTop(borderColorTop);
			cell.setBorderWidthTop(0.5f);
		}

		BaseColor borderColorBottom = new BaseColor(221, 221, 221);
		if (cellModel.getBorderColorBottom() != null && !"".equals(cellModel.getBorderColorBottom())) {
			borderColorBottom = StyleUtils.getColor(cellModel.getBorderColorBottom().toString());
			cell.setBorderColorBottom(borderColorBottom);
			cell.setBorderWidthBottom(0.5f);
		}

		BaseColor borderColorLeft = new BaseColor(221, 221, 221);
		if (cellModel.getBorderColorLeft() != null && !"".equals(cellModel.getBorderColorLeft())) {
			borderColorLeft = StyleUtils.getColor(cellModel.getBorderColorLeft().toString());
			cell.setBorderColorLeft(borderColorLeft);
			cell.setBorderWidthLeft(0.5f);
		}

		BaseColor borderColorRight = new BaseColor(221, 221, 221);
		if (cellModel.getBorderColorRight() != null && !"".equals(cellModel.getBorderColorRight())) {
			borderColorRight = StyleUtils.getColor(cellModel.getBorderColorRight().toString());
			cell.setBorderColorRight(borderColorRight);
			cell.setBorderWidthRight(0.5f);
		}

		// 设置圆角单元格事件
		if (StringUtils.isNotEmpty(cellModel.getEventType())) {
			EventUtil eventUtil = new EventUtil();
			String eventType = cellModel.getEventType();
			FilletCellEvent cellEvent = null;
			if (eventType.contains(",")) {
				// 同时使用边框和背景两个事件
				String[] eventTypeArr = eventType.split(",");
				for (int i = 0; i < eventTypeArr.length; i++) {
					String type = eventTypeArr[i];
					cellEvent = eventUtil.setFilletCellEvent(cellModel, type);
					cell.setCellEvent(cellEvent);
				}
			} else {
				// 单独使用一个事件
				cellEvent = eventUtil.setFilletCellEvent(cellModel, eventType);
				cell.setCellEvent(cellEvent);
			}
		}
		// 单元格中文字的行间距 适应complex等
		// 分两个元素 前一个是固定间距 后一个是增加间距
		// 暂时使用15,0 前一个设置为15 后一个设置为0
		// 注意 所有后来增加的属性 都不要设置默认值
		// 通过判断 在有值传入的时候才能设置对应的参数
		if (StringUtils.isNotEmpty(cellModel.getFixedAndMultipliedLeading())) {
			String fixedAndMultipliedLeading = cellModel.getFixedAndMultipliedLeading();
			String[] fixedAndMultipliedLeadingArr = fixedAndMultipliedLeading.split(",");
			cell.setLeading(Integer.parseInt(fixedAndMultipliedLeadingArr[0]),
					Integer.parseInt(fixedAndMultipliedLeadingArr[1]));
		}
		// cell 跨页处理：
		cell.setUseAscender(true);
		return cell;
	}

	private com.itextpdf.text.List generateList(ListModel listModel) throws Exception {
		com.itextpdf.text.List list = new com.itextpdf.text.List(false);

		Font symbolFont = listModel.getFont();
		BaseColor symbolColor = null;
		if (StringUtils.isEmpty(listModel.getColor())) {
			symbolColor = new BaseColor(40, 40, 40);
		} else {
			symbolColor = StyleUtils.getColor(listModel.getColor());
		}
		symbolFont.setColor(symbolColor);
		// 图形的大小，默认是6
		if (StringUtils.isNotEmpty(listModel.getSize())) {
			symbolFont.setSize(Float.parseFloat(listModel.getSize()));
		} else {
			symbolFont.setSize(6);
		}
		Chunk symbolChunk = new Chunk(listModel.getSymbol(), symbolFont);
		// 图形的上移
		if (StringUtils.isNotEmpty(listModel.getRise())) {
			symbolChunk.setTextRise(Float.parseFloat(listModel.getRise()));
		}

		list.setListSymbol(symbolChunk);

		if (listModel.getIndent() != null) {
			list.setIndentationLeft(StyleUtils.getIndent(listModel.getIndent()));
		} else {
			list.setIndentationLeft(20);
		}

		for (Model model : listModel.getModels()) {
			if (model.getClass().getName().equals(TextModel.class.getName())) {
				TextModel textModel = (TextModel) model;
				String text = textModel.getText();
				StringTokenizer st = new StringTokenizer(text, "\r\n");
				while (st.hasMoreTokens()) {
					Chunk chunk = new Chunk(st.nextToken(), textModel.getFont());
					if (textModel.getWordSpace() != 0) {
						chunk.setCharacterSpacing(textModel.getWordSpace());
					}
					ListItem listItem = new ListItem(chunk);
					listItem.setSpacingAfter(5f);
					if (textModel.getLeading() != 0) {
						listItem.setLeading(textModel.getLeading());
					}
					list.add(listItem);
				}
			} else if (model.getClass().isAssignableFrom(ImageModel.class)) {
				ImageModel imageModel = (ImageModel) model;
				Image image = generateImage(imageModel);
				if (image != null) {
					list.add(image);
				}
			} else if (model.getClass().isAssignableFrom(ComplexModel.class)) {
				ComplexModel complexModel = (ComplexModel) model;
				Phrase phrase = generateComplex(complexModel);
				ListItem listItem = new ListItem(phrase);
				listItem.setSpacingAfter(5f);
				list.add(listItem);
			}
		}
		return list;
	}

	private Phrase generateComplex(ComplexModel complexModel) throws Exception {
		Phrase phrase = new Phrase();

		for (Model model : complexModel.getModels()) {
			if (model.getClass().isAssignableFrom(TextModel.class)) {
				TextModel textModel = (TextModel) model;
				// Chunk chunk = generateInlineText(textModel);
				// if(complexModel.getHeight() > 0){
				// chunk.setLineHeight(complexModel.getHeight());
				// }
				// phrase.add(chunk);
				// if(textModel.getLeading()!=0){
				// phrase.setLeading(textModel.getLeading());
				// }

				if (StringUtils.isNotEmpty(textModel.getHasSpacing()) && textModel.getHasSpacing().equals("false")) {
					Chunk chunkText = generateSimpleText(textModel);
					phrase.add(chunkText);
				} else {
					float leading = textModel.getLeading();
					List<Paragraph> generate = generateText(textModel);
					if (generate != null && generate.size() > 0) {
						for (int i = 0; i < generate.size(); i++) {
							phrase.add(generate.get(i));
							// phrase.addAll(generate);
						}
					}
					if (leading > 0) {
						phrase.setLeading(leading);
					}
				}
			} else if (model.getClass().isAssignableFrom(ImageModel.class)) {
				ImageModel imageModel = (ImageModel) model;
				if ("risk".equals(imageModel.getType())) {
					String fontSize = complexModel.getFontSize();
					List<Chunk> list = generateRisk(imageModel, fontSize);
					for (Chunk chunk : list) {
						if (complexModel.getHeight() > 0) {
							chunk.setLineHeight(complexModel.getHeight());
						}
						phrase.add(chunk);
					}
				} else if (imageModel.getType() != null && imageModel.getType().contains("tar")) {
					String fontSize = imageModel.getFontSize();
					List<Chunk> list = generateStar(imageModel, fontSize);
					for (Chunk chunk : list) {
						if (complexModel.getHeight() > 0) {
							chunk.setLineHeight(complexModel.getHeight());
						}
						phrase.add(chunk);
					}
				} else if ("freq".equals(imageModel.getType())) {
					List<Chunk> list = generateFreq(imageModel.getName());
					for (Chunk chunk : list) {
						if (complexModel.getHeight() > 0) {
							chunk.setLineHeight(complexModel.getHeight());
						}
						phrase.add(chunk);
					}

				} else if ("star".equals(imageModel.getType()) || "circle".equals(imageModel.getType())) {
					String fontSize = imageModel.getFontSize();
					List<Chunk> chunks = generateStar(imageModel, fontSize);
					for (Chunk chunk : chunks) {
						phrase.add(chunk);
					}
				} else {
					Image image = generateImage(imageModel);
					if (image != null) {
						Chunk chunk = new Chunk(image, 0, 0);
						if (complexModel.getHeight() > 0) {
							chunk.setLineHeight(complexModel.getHeight());
						}
						phrase.add(chunk);
					}
				}

			} else if (model.getClass().isAssignableFrom(LineModel.class)) {
				LineModel lineModel = (LineModel) model;
				LineSeparator line = generateLine(lineModel);
				phrase.add(line);
			}
		}
		return phrase;
	}

	private List<Chunk> generateRisk(ImageModel imageModel, String fontSize) {
		String value = imageModel.getName();
		String shape = imageModel.getShape();

		String text = "■";
		if (!"".equals(shape) && "circle".equals(shape)) {
			text = "●";
		}
		List<Chunk> list = new ArrayList<Chunk>();
		if ("".equals(fontSize) || fontSize == null) {
			fontSize = "8";
		}
		Font darkGreenFont = StyleUtils.getFont(Integer.parseInt(fontSize), new BaseColor(2, 135, 10));
		Font lightGreenFont = StyleUtils.getFont(Integer.parseInt(fontSize), new BaseColor(194, 250, 163));
		Font lightGrayFont = StyleUtils.getFont(Integer.parseInt(fontSize), new BaseColor(205, 205, 200));
		Font orangeFont = StyleUtils.getFont(Integer.parseInt(fontSize), new BaseColor(242, 174, 0));
		Font lightorangeFont = StyleUtils.getFont(Integer.parseInt(fontSize), new BaseColor(246, 226, 181));
		Font redFont = StyleUtils.getFont(Integer.parseInt(fontSize), BaseColor.RED);
		if ("0".equals(value)) {
			for (int i = 0; i < 5; i++) {
				Chunk chunk = new Chunk(text, lightGrayFont);
				list.add(chunk);
			}
		} else if ("1".equals(value)) {
			Chunk chunk = new Chunk(text, darkGreenFont);
			list.add(chunk);
			for (int i = 0; i < 4; i++) {
				chunk = new Chunk(text, lightGrayFont);
				list.add(chunk);
			}
		} else if ("2".equals(value)) {
			for (int i = 0; i < 2; i++) {
				Chunk chunk = new Chunk(text, darkGreenFont);
				list.add(chunk);
			}
			for (int i = 0; i < 3; i++) {
				Chunk chunk = new Chunk(text, lightGrayFont);
				list.add(chunk);
			}
		} else if ("3".equals(value)) {
			for (int i = 0; i < 3; i++) {
				Chunk chunk = new Chunk(text, orangeFont);
				list.add(chunk);
			}
			for (int i = 0; i < 2; i++) {
				Chunk chunk = new Chunk(text, lightGrayFont);
				list.add(chunk);
			}
		} else if ("4".equals(value)) {
			for (int i = 0; i < 4; i++) {
				Chunk chunk = new Chunk(text, redFont);
				list.add(chunk);
			}
			Chunk chunk = new Chunk(text, lightGrayFont);
			list.add(chunk);
		} else if ("5".equals(value)) {
			for (int i = 0; i < 5; i++) {
				Chunk chunk = new Chunk(text, redFont);
				list.add(chunk);
			}
		}
		if (list != null && list.size() > 0 && StringUtils.isNotEmpty(imageModel.getRiskRise())) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setTextRise(Float.parseFloat(imageModel.getRiskRise()));
			}
		}
		return list;
	}

	private List<Chunk> generateStar(ImageModel imageModel, String fontSize) {
		String value = imageModel.getName();
		String color = imageModel.getColor();
		String type = imageModel.getType();
		String text = "";
		if (type.equals("star")) {
			text = "★";
		} else if (type.equals("emptyStar")) {
			text = "☆";
		} else if (type.equals("circle")) {
			text = "●";
		}

		List<Chunk> list = new ArrayList<Chunk>();
		if ("".equals(fontSize) || fontSize == null) {
			fontSize = "8";
		}
		Font colorFont = StyleUtils.getFont(Integer.parseInt(fontSize), StyleUtils.getColor(color));
		if (Integer.parseInt(value) > 0) {
			int num = Integer.parseInt(value);
			for (int i = 0; i < num; i++) {
				Chunk chunk = new Chunk(text, colorFont);
				list.add(chunk);
			}
		}

		if (list != null && list.size() > 0 && StringUtils.isNotEmpty(imageModel.getRiskRise())) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setTextRise(Float.parseFloat(imageModel.getRiskRise()));
			}
		}
		return list;
	}

	private List<Chunk> generateFreq(String value) {
		String text = "■";
		List<Chunk> list = new ArrayList<Chunk>();
		Font blueFont = StyleUtils.getFont(6, BaseColor.BLUE);
		Font lightBlueFont = StyleUtils.getFont(6, new BaseColor(224, 255, 255));
		if ("0".equals(value)) {
			for (int i = 0; i < 5; i++) {
				Chunk chunk = new Chunk(text, lightBlueFont);
				list.add(chunk);
			}
		} else if ("1".equals(value)) {
			Chunk chunk1 = new Chunk(text, blueFont);
			list.add(chunk1);
			for (int i = 0; i < 4; i++) {
				Chunk chunk = new Chunk(text, lightBlueFont);
				list.add(chunk);
			}
		} else if ("2".equals(value)) {
			for (int i = 0; i < 2; i++) {
				Chunk chunk = new Chunk(text, blueFont);
				list.add(chunk);
			}
			for (int i = 0; i < 3; i++) {
				Chunk chunk = new Chunk(text, lightBlueFont);
				list.add(chunk);
			}
		} else if ("3".equals(value)) {
			for (int i = 0; i < 3; i++) {
				Chunk chunk = new Chunk(text, blueFont);
				list.add(chunk);
			}
			for (int i = 0; i < 2; i++) {
				Chunk chunk = new Chunk(text, lightBlueFont);
				list.add(chunk);
			}
		} else if ("4".equals(value)) {
			for (int i = 0; i < 4; i++) {
				Chunk chunk = new Chunk(text, blueFont);
				list.add(chunk);
			}
			Chunk chunk = new Chunk(text, lightBlueFont);
			list.add(chunk);
		} else if ("5".equals(value)) {
			for (int i = 0; i < 5; i++) {
				Chunk chunk = new Chunk(text, blueFont);
				list.add(chunk);
			}
		}
		return list;
	}

	private Image generateImage(ImageModel imageModel) throws Exception {
		String filename = imageModel.getName();
		if (filename == null || filename.length() == 0) {
			return null;
		}
		String pathName = chartDirName + filename;
		File file = new File(pathName);
		if (!file.exists()) {
			return null;
		}
		Image image = Image.getInstance(pathName);

		if (imageModel.getAlign() != null) {
			image.setAlignment(StyleUtils.getAlign(imageModel.getAlign()));
		}
		if (imageModel.getWidth() > 0) {
			image.scaleAbsoluteWidth(imageModel.getWidth());
		}
		if (imageModel.getHeight() > 0) {
			image.scaleAbsoluteHeight(imageModel.getHeight());
		}
		if (StringUtils.isNotEmpty(imageModel.getZ()) && StringUtils.isNotEmpty(imageModel.getH())) {
			image.setAbsolutePosition(Float.parseFloat(imageModel.getZ()), Float.parseFloat(imageModel.getH()));
		}

		if (imageModel.getSpacing() != 0.0) {
			image.setSpacingAfter(imageModel.getSpacing());
		}
		// if(imageModel.getSpacingB()!=0.0){
		// image.setSpacingBefore(-100);
		// }
		if (StringUtils.isNotEmpty(imageModel.getIndentLeft())) {
			image.setIndentationLeft(Float.parseFloat(imageModel.getIndentLeft()));
		}
		return image;
	}

	private List<Paragraph> generateText(TextModel textModel) {
		List<Paragraph> paraList = new ArrayList<Paragraph>();
		String text = textModel.getText();
		// chunk.setLocalDestination(chapterModel.getId());

		// 新版本目录页 设置目录页码
		if (StringUtils.isNotEmpty(textModel.getPageNumFromName())) {
			String PageNumFromName = textModel.getPageNumFromName();
			if (chapterMap.get(PageNumFromName) == null) {
				// 第一次目录页无法设置值直接赋值0
				text = "0";
			} else {
				// 第二次目录页
				String pageNumFromName = chapterMap.get(PageNumFromName).toString();
				// 判断是否存在页码不是从逻辑开始开始的情况
				if (StringUtils.isNotEmpty(textModel.getBeginPageNum())) {
					int beginPageNum = Integer.parseInt(textModel.getBeginPageNum());
					// 数字模式
					if (StringUtils.isNotEmpty(textModel.getNumModel()) && textModel.getNumModel().equals("1")) {
						pageNumFromName = NumUtil
								.addZeroToSingleDigit(Integer.parseInt(pageNumFromName) - beginPageNum);
					} else {
						pageNumFromName = (Integer.parseInt(pageNumFromName) - beginPageNum) + "";
					}
				}
				text = pageNumFromName;
				//
				if (text.equals("0")) {
					text = "1";
				}
			}
		}

		StringTokenizer st = new StringTokenizer(text, "\r\n");
		String fontName = textModel.getFontName();

		String fourFootsStar = "\u2726";
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			Paragraph para = new Paragraph();
			if ((textModel.getSup() != null && !"".equals(textModel.getSup()))
					|| (textModel.getSub() != null && !"".equals(textModel.getSub())) || text.contains("®")
					|| text.contains("©") || text.contains("~") || text.contains("×") || text.contains(fourFootsStar)) {
				String s = "";
				textModel.setFontName("ss");
				if (StringUtils.isNotEmpty(textModel.getSup())) {
					s = textModel.getSup();
				}
				if (StringUtils.isNotEmpty(textModel.getSub())) {
					s = textModel.getSub();
				}
				if (text.contains("®")) {
					s = "®";
				}
				if (text.contains("©")) {
					s = "©";
				}
				if (text.contains("~")) {
					s = "~";
				}
				if (text.contains("×")) {
					s = "×";
				}
				if (text.contains(fourFootsStar)) {
					s = fourFootsStar;
				}
				Chunk chunk = new Chunk(s);
				chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
				// chunk.setSplitCharacter(splitCharacter);
				// chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
				if (textModel.getSup() != null && !"".equals(textModel.getSup())) {
					if (textModel.getRise() != 0) {
						chunk.setTextRise(textModel.getRise());
					} else {
						chunk.setTextRise(3);
					}
				}
				if (textModel.getSub() != null && !"".equals(textModel.getSub())) {
					if (textModel.getRise() != 0) {
						chunk.setTextRise(textModel.getRise());
					} else {
						chunk.setTextRise(-3);
					}
				}
				Font font = textModel.getFont();

				// TODO
				if (s.equals("T") || s.equals("M")) {

				} else {

					font.setSize(6f);
				}
				chunk.setFont(font);
				textModel.setFontName(fontName);
				// String [] arr=str.split(s);
				// 使用多字符 分割字符串
				String[] arr = StringUtil.splitByMoreStr(str, s);
				if (arr.length == 0 && str.length() == 1) {
					para.add(chunk);
				}
				for (int i = 0; i < arr.length; i++) {
					Chunk strchunk = new Chunk(arr[i], textModel.getFont());
					// strchunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
					if (textModel.getWordSpace() != 0) {
						strchunk.setCharacterSpacing(textModel.getWordSpace());
					}
					para.add(strchunk);
					if (str.endsWith(s)) {
						if (i < arr.length) {
							para.add(chunk);
						}
					} else {
						if (i < arr.length - 1) {
							para.add(chunk);
						}
					}

				}
			} else if (StringUtils.isNotEmpty(textModel.getUnderLine())) {
				String s = "";
				String strUnder = textModel.getUnderLine();
				String[] arrUnder = strUnder.split(",");
				String splitStr = "";
				for (int k = 0; k < arrUnder.length; k++) {
					if (k == arrUnder.length - 1) {

						splitStr += arrUnder[k];
					} else {

						splitStr += arrUnder[k] + "|";
					}
				}
				String[] arr = str.split(splitStr);
				if (arr == null || arr.length == 0) {
					Chunk chunk = new Chunk(str);
					chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
					chunk.setUnderline(0.5f, -0.8f);
					Font font = textModel.getFont();
					chunk.setFont(font);
					textModel.setFontName(fontName);
					para.add(chunk);
				} else {
					for (int j = 0; j < arr.length; j++) {

						Chunk strchunk = new Chunk(arr[j], textModel.getFont());
						strchunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
						if (textModel.getWordSpace() != 0) {
							strchunk.setCharacterSpacing(textModel.getWordSpace());
						}
						para.add(strchunk);
						if (j < arrUnder.length) {
							Chunk chunk = new Chunk(arrUnder[j]);
							chunk.setUnderline(0.5f, -0.8f);
							Font font = textModel.getFont();
							chunk.setFont(font);
							textModel.setFontName(fontName);
							para.add(chunk);
						}
					}
				}
			} else {
				Chunk chunk = new Chunk(str, textModel.getFont());
				chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);

				if (textModel.getWordSpace() != 0) {
					chunk.setCharacterSpacing(textModel.getWordSpace());
				}
				chunk.setWordSpacing(-1f);
				if (textModel.getRise() != 0) {
					chunk.setTextRise(textModel.getRise());
				}
				if (StringUtils.isNotEmpty(textModel.getGenericTag())) {
					chunk.setGenericTag(textModel.getGenericTag());
				}
				para.add(chunk);
				// para = new Paragraph(chunk);
			}

			if (textModel.getAlign() != null) {
				para.setAlignment(StyleUtils.getAlign(textModel.getAlign()));
			}
			if (textModel.getIndent() != null) {
				para.setFirstLineIndent(StyleUtils.getIndent(textModel.getIndent()));
			} else {
				para.setFirstLineIndent(20);
			}
			// TODO
			// TODO 段落缩进
			if (textModel.getIndentationLeft() != null) {
				para.setIndentationLeft(StyleUtils.getIndent(textModel.getIndentationLeft()));
			}
			if (textModel.getIndentationRight() != null) {
				para.setIndentationRight(StyleUtils.getIndent(textModel.getIndentationRight()));
			}
			if (textModel.getSpacing() != 0.0) {
				para.setSpacingAfter(textModel.getSpacing());
			} else {
				para.setSpacingAfter(5f);
			}

			if (textModel.getSpacingB() != 0.0) {
				para.setSpacingBefore(textModel.getSpacingB());
			}
			if (textModel.getLeading() != 0) {
				para.setLeading(textModel.getLeading());
			}
			if (textModel.getZ() != null && textModel.getH() != null) {
				ColumnText.showTextAligned(writerTmp.getDirectContent(), 1, para, Integer.parseInt(textModel.getZ()),
						Integer.parseInt(textModel.getH()), 0);
			}

			// 判断这个text 是否需要点击跳转
			if (StringUtils.isNotEmpty(textModel.getId())) {
				String id = textModel.getId();
				List<Chunk> listChunks = para.getChunks();
				for (Chunk chunkItem : listChunks) {
					// 添加要跳转的页的name
					chunkItem.setLocalGoto(id);
				}
			}
			paraList.add(para);
		}
		return paraList;
	}

	// TODO 纯文本
	private Chunk generateSimpleText(TextModel textModel) {
		String text = textModel.getText();
		Chunk chunk = new Chunk(text, textModel.getFont());
		chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
		if (textModel.getWordSpace() != 0) {
			chunk.setCharacterSpacing(textModel.getWordSpace());
		}
		chunk.setWordSpacing(-1f);
		if (textModel.getRise() != 0) {
			chunk.setTextRise(textModel.getRise());
		}
		return chunk;
	}

	private Chunk generateInlineText(TextModel textModel) {

		String text = textModel.getText();
		Chunk chunk = new Chunk(text, textModel.getFont());
		chunk.setSplitCharacter(ChineseSplitCharater.SplitCharacter);
		if (textModel.getBgcolor() != null) {
			BaseColor color = StyleUtils.getColor(textModel.getBgcolor());
			if (color != null) {
				// chunk.setBackground(color);
				chunk.setBackground(color, 0, 2, 50, 2);
			}
		}
		return chunk;
	}

	/**
	 * @param args
	 */

	protected Chapter generateCatalog(CatalogModel catalogModel, float widthLine) throws Exception {
		Font font = null;
		if (catalogModel.getFontName() == null && catalogModel.getFontSize() == null) {
			font = StyleUtils.getChapterFont();
		} else {
			font = catalogModel.getFont();
		}
		Chunk chunk = new Chunk(catalogModel.getName(), font);
		chunk.setLocalDestination(catalogModel.getId());
		Paragraph cTitle = new Paragraph(chunk);
		cTitle.setAlignment(Element.ALIGN_CENTER);
		cTitle.setSpacingAfter(12f);
		Chapter chapter = new Chapter(cTitle, 0);
		chapter.setNumberDepth(0);

		List<RefModel> models = catalogModel.getRefList();
		if (models.size() > 0) {
			float[] cols = new float[] { 84, 11, 5 };
			PdfPTable table = new PdfPTable(cols);

			table.setWidthPercentage(100);
			if (catalogModel.getWidPer() > 0) {
				table.setWidthPercentage(catalogModel.getWidPer());
			}
			table.getDefaultCell().setBorder(0);

			float width = widthLine * cols[0] / (cols[0] + cols[1] + cols[2]);
			for (RefModel ref : models) {
				generateCatalogRef(ref, table, width);
			}
			chapter.add(table);
		}

		for (Model model : catalogModel.getModels()) {
			if (model.getClass().isAssignableFrom(TextModel.class)) {
				TextModel textModel = (TextModel) model;
				// if(StringUtils.isNotEmpty(textModel.getPageNumFromName())
				// && sectionMap.get(textModel.getPageNumFromName())==null){
				// //第一次目录页无法设置值直接跳过
				// continue;
				// }
				List<Paragraph> paraList = generateText(textModel);
				for (Paragraph para : paraList) {
					chapter.add(para);
				}
			} else if (model.getClass().isAssignableFrom(ComplexModel.class)) {
				ComplexModel complexModel = (ComplexModel) model;
				Paragraph paragraph = new Paragraph();
				Phrase phrase = generateComplex(complexModel);
				paragraph.add(phrase);
				if (complexModel.getIndent() != null) {
					paragraph.setFirstLineIndent(StyleUtils.getIndent(complexModel.getIndent()));
				} else {
					paragraph.setFirstLineIndent(20);
				}
				if (StringUtils.isNotEmpty(complexModel.getAlign())) {
					if ("center".equals(complexModel.getAlign())) {
						paragraph.setAlignment(Element.ALIGN_CENTER);
					}
				}
				String spacing = complexModel.getSpacing();
				if (StringUtils.isNotEmpty(spacing)) {
					paragraph.setSpacingAfter(Float.parseFloat(spacing));
				} else {
					paragraph.setSpacingAfter(8f);
				}
				if (StringUtils.isNotEmpty(complexModel.getLeading())) {
					paragraph.setLeading(Float.parseFloat(complexModel.getLeading()));
				}
				if (StringUtils.isNotEmpty(complexModel.getSpacingB())) {
					paragraph.setSpacingBefore(Float.parseFloat(complexModel.getSpacingB()));
				}
				if (StringUtils.isNotEmpty(complexModel.getIndentationLeft())) {
					paragraph.setIndentationLeft(Float.parseFloat(complexModel.getIndentationLeft()));
				}
				if (StringUtils.isNotEmpty(complexModel.getIndentationRight())) {
					paragraph.setIndentationRight(Float.parseFloat(complexModel.getIndentationRight()));
				}
				chapter.add(paragraph);
			} else if (model.getClass().isAssignableFrom(TableModel.class)) {
				TableModel tableModel = (TableModel) model;
				PdfPTable table = generateTable(tableModel, false);
				chapter.add(table);
			} else if (model.getClass().isAssignableFrom(ImageModel.class)) {
				ImageModel imageModel = (ImageModel) model;
				Image image = generateImage(imageModel);
				if (image != null) {
					chapter.add(image);
				}
			}
		}

		return chapter;
	}

	private void generateCatalogRef(RefModel ref, PdfPTable table, float widthLine)
			throws UnsupportedEncodingException {
		float wNum = 1;
		Chunk nameChunk = new Chunk(ref.getName(), ref.getFont()).setLocalGoto(ref.getId());
		PdfPCell titleCell = new PdfPCell();
		Paragraph titlePara = new Paragraph();
		float nameWith = nameChunk.getWidthPoint();
		if (ref.getDepth() > 0) {
			Chunk indentChunk = new Chunk("    ");
			for (int i = 0; i < ref.getDepth(); i++) {
				titlePara.add(indentChunk);
				nameWith += indentChunk.getWidthPoint();
			}
		}
		titlePara.add(nameChunk);

		Chunk dot = new Chunk(".").setTextRise(3).setLocalGoto(ref.getId());
		if (StringUtils.isNotEmpty(ref.getIsAll()) && ref.getIsAll().equals("true")) {
			dot.setFont(ref.getFont());
		}
		if (ref.getWidthPer() > 0) {
			wNum = ref.getWidthPer();
		}
		double num = 0;// 368
		if (ref.getImage() != null) {
			num = (widthLine * wNum - nameWith) / dot.getWidthPoint() - 0.5;// 368

		} else {
			num = (widthLine * wNum * 1.13 - nameWith) / dot.getWidthPoint() - 1.5;// 这里的1.135是100/84
																					// 乘以
																					// （84+11）/100
		}
		for (int i = 0; i < num; i++) {
			titlePara.add(dot);
		}
		titleCell.addElement(titlePara);
		titleCell.setBorder(0);
		titleCell.setPadding(0);
		titleCell.setNoWrap(true);
		if (ref.getFixheight() != 0) {
			titleCell.setFixedHeight(ref.getFixheight());
		}
		table.addCell(titleCell);
		if (ref.getRefSpacing() > 0) {
			float refSpacing = ref.getRefSpacing();
			table.setSpacingBefore(refSpacing);
			table.setSpacingAfter(-refSpacing);
		}

		ImageModel imageModel = ref.getImage();
		PdfPCell riskCell = new PdfPCell();
		Paragraph riskPara = new Paragraph();
		if (imageModel != null && "risk".equals(imageModel.getType())) {
			String fontSize = ref.getFontSize();
			List<Chunk> list = generateRisk(imageModel, fontSize);
			for (Chunk chunk : list) {
				riskPara.add(chunk.setLocalGoto(ref.getId()));
			}
		}
		riskCell.addElement(riskPara);
		riskCell.setBorder(0);
		riskCell.setBackgroundColor(new BaseColor(255, 255, 255));
		riskCell.setPadding(0);
		riskCell.setNoWrap(true);
		if (ref.getFixheight() != 0) {
			riskCell.setFixedHeight(ref.getFixheight());
		}
		table.addCell(riskCell);

		PdfPCell pageCell = new PdfPCell();
		Paragraph para = new Paragraph();
		Font symbolFont = StyleUtils.getDefaultFont();
		symbolFont.setSize(10);
		if (StringUtils.isNotEmpty(ref.getIsAll()) && ref.getIsAll().equals("true")) {
			Font font = ref.getFont();
			symbolFont = font;
		}
		int begin = ref.getBegin();
		if (begin > 0) {
			begin = begin - 1;
		} else {
			begin = 0;
		}
		// 页眉开始页
		int beginNum = 0;
		if (ref.getBeginNum() != null && !ref.getBeginNum().equals("")) {
			beginNum = Integer.parseInt(ref.getBeginNum());
		}
		Chunk pageChunk = new Chunk("" + (ref.getPageNumber() - begin - beginNum), symbolFont);

		para.add(pageChunk.setLocalGoto(ref.getId()));
		pageCell.addElement(para);
		pageCell.setBorder(0);
		pageCell.setPadding(0);
		pageCell.setNoWrap(true);
		if (ref.getFixheight() != 0) {
			pageCell.setFixedHeight(ref.getFixheight());
		}
		table.addCell(pageCell);

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getChartDirName() {
		return chartDirName;
	}

	public void setChartDirName(String chartDirName) {
		this.chartDirName = chartDirName;
	}

	public String getTempFileName() {
		return tempFileName;
	}

	public void setTempFileName(String tempFileName) {
		this.tempFileName = tempFileName;
	}

	public Map<String, Integer> getChapterMap() {
		return chapterMap;
	}

	public void setChapterMap(Map<String, Integer> chapterMap) {
		this.chapterMap = chapterMap;
	}

	public Map<String, Integer> getSectionMap() {
		return sectionMap;
	}

	public void setSectionMap(Map<String, Integer> sectionMap) {
		this.sectionMap = sectionMap;
	}

	public int getAllPageNumber() {
		return allPageNumber;
	}

	public void setAllPageNumber(int allPageNumber) {
		this.allPageNumber = allPageNumber;
	}

	public CatalogModel getCatalogModel() {
		return catalogModel;
	}

	public void setCatalogModel(CatalogModel catalogModel) {
		this.catalogModel = catalogModel;
	}

	public DocModel getDocModel() {
		return docModel;
	}

	public void setDocModel(DocModel docModel) {
		this.docModel = docModel;
	}

	public int getCatalogSecondPageNum() {
		return catalogSecondPageNum;
	}

	public void setCatalogSecondPageNum(int catalogSecondPageNum) {
		this.catalogSecondPageNum = catalogSecondPageNum;
	}

}
