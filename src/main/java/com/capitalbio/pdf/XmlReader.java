package com.capitalbio.pdf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.capitalbio.pdf.model.CatalogModel;
import com.capitalbio.pdf.model.CellModel;
import com.capitalbio.pdf.model.ChapterModel;
import com.capitalbio.pdf.model.ComplexModel;
import com.capitalbio.pdf.model.DocModel;
import com.capitalbio.pdf.model.HeaderItemModel;
import com.capitalbio.pdf.model.HeaderModel;
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


public class XmlReader {
	
	private Document getDoc(String fileName) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
		SAXReader reader = new SAXReader();
		Document document = reader.read(br);
		return document;
	}
	@SuppressWarnings("rawtypes")
	public DocModel parse(String fileName) throws Exception {
		Document doc = getDoc(fileName);
		Element root = doc.getRootElement();
		
		DocModel docModel = new DocModel();
		
		String margin = root.attributeValue("margin");
		docModel.setMargin(margin);
		String leftMargin = root.attributeValue("leftMargin");
		docModel.setLeftMargin(leftMargin);
		String rightMargin = root.attributeValue("rightMargin");
		docModel.setRightMargin(rightMargin);
		if(StringUtils.isNotEmpty(root.attributeValue("getPagechapterName"))){
			docModel.setGetPagechapterName(root.attributeValue("getPagechapterName"));
		}
		if(StringUtils.isNotEmpty(root.attributeValue("notDiplayBookMarksChapter"))){
			docModel.setNotDiplayBookMarksChapter(root.attributeValue("notDiplayBookMarksChapter"));
		}
		
		List<Model> models = new ArrayList<Model>();
		for (Iterator iter=root.elementIterator(); iter.hasNext();) {
			Element e = (Element)iter.next();
			if ("chapter".equals(e.getName())) {
				ChapterModel chapterModel = parseChapter(e);
				models.add(chapterModel);
			} else if ("catalog".equals(e.getName())) {
				CatalogModel catalogModel = parseCatalog(e);
				models.add(catalogModel);
			} else if ("header".equals(e.getName())) {
				HeaderModel headerModel = parseHeader(e);
				docModel.setHeader(headerModel);
			}
		}
		docModel.setModels(models);
		return docModel;
	}
	
	@SuppressWarnings("rawtypes")
	private HeaderModel parseHeader(Element e) {
		HeaderModel headerModel = new HeaderModel();
		if(e.attributeValue("begin")!=null){
			headerModel.setBegin(Integer.parseInt(e.attributeValue("begin")));
		}else{
			headerModel.setBegin(0);
		}
		if(e.attributeValue("beginNum")!=null && !e.attributeValue("beginNum").equals("")){
			headerModel.setBeginNum(e.attributeValue("beginNum"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("Even"))){
			headerModel.setEven(e.attributeValue("Even"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("substring"))){
			headerModel.setSubstring(e.attributeValue("substring"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("itemName"))){
			headerModel.setItemName(e.attributeValue("itemName"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("beginName"))){
			headerModel.setBeginName(e.attributeValue("beginName"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("start"))){
			headerModel.setStart(e.attributeValue("start"));
		}
		
		List<HeaderItemModel> items = new ArrayList<HeaderItemModel>();
		for (Iterator iter=e.elementIterator("headeritem"); iter.hasNext();) {
			Element he = (Element)iter.next();
			HeaderItemModel itemModel = new HeaderItemModel();
			copyAttributes(he, itemModel);
			itemModel.setPos(getFloatValue(he.attributeValue("pos")));
			itemModel.setX(getFloatValue(he.attributeValue("x")));
			itemModel.setWidth(getFloatValue(he.attributeValue("width")));
			itemModel.setY(getFloatValue(he.attributeValue("y")));
			itemModel.setLink(he.attributeValue("link"));
			itemModel.setText(he.getTextTrim());
			itemModel.setTop(he.attributeValue("top"));
			itemModel.setBgcolor(he.attributeValue("bgcolor"));
			if(StringUtils.isNotEmpty(he.attributeValue("z"))){
				itemModel.setZ(getFloatValue(he.attributeValue("z")));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("h"))){
				itemModel.setH(getFloatValue(he.attributeValue("h")));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("isEven"))){
				itemModel.setIsEven(he.attributeValue("isEven"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("itemName"))){
				itemModel.setItemName(he.attributeValue("itemName"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("sup"))){
				itemModel.setSup(he.attributeValue("sup"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("chapterStar"))){
				itemModel.setChapterStar(he.attributeValue("chapterStar"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("chapterEnd"))){
				itemModel.setChapterEnd(he.attributeValue("chapterEnd"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("startPage"))){
				itemModel.setStartPage(he.attributeValue("startPage"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("exceptChapterName"))){
				itemModel.setExceptChapterName(he.attributeValue("exceptChapterName"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("cludeChapterName"))){
				itemModel.setCludeChapterName(he.attributeValue("cludeChapterName"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("pageNumStr"))){
				itemModel.setPageNumStr(he.attributeValue("pageNumStr"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("isBG"))){
				itemModel.setIsBG(he.attributeValue("isBG"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("startNum"))){
				itemModel.setStartNum(he.attributeValue("startNum"));
			}
			if(StringUtils.isNotEmpty(he.attributeValue("lastPage"))){
				itemModel.setLastPage(he.attributeValue("lastPage"));
			}
			
			if(he.attributeValue("page")==null){
				itemModel.setPage(0);
			}else{
				itemModel.setPage(Integer.parseInt(he.attributeValue("page")));
			}
			
			Element ie = he.element("image");
			if (ie != null) {
				ImageModel imageModel = parseImage(ie);
				itemModel.setImageModel(imageModel); 
			}
			items.add(itemModel);
		}
		headerModel.setItems(items);
		return headerModel;
	}
	
	
	@SuppressWarnings("rawtypes")
	private ChapterModel parseChapter(Element e) {
		ChapterModel chapterModel = new ChapterModel();
		List<Model> models = new ArrayList<Model>();
		if(e.attributeValue("isNewP")!=null){
			chapterModel.setIsNewP(e.attributeValue("isNewP"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("spacingAfter"))){
			chapterModel.setSpacingAfter(e.attributeValue("spacingAfter"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("hengBan"))){
			chapterModel.setHengBan(e.attributeValue("hengBan"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("mtop"))){
			chapterModel.setMtop(e.attributeValue("mtop"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("mbottom"))){
			chapterModel.setMbottom(e.attributeValue("mbottom"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("mleft"))){
			chapterModel.setMleft(e.attributeValue("mleft"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("mright"))){
			chapterModel.setMright(e.attributeValue("mright"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("realPage"))){
			chapterModel.setRealPage(e.attributeValue("realPage"));
		}
		chapterModel.setChapterName(e.attributeValue("name"));
		chapterModel.setColor(e.attributeValue("color"));
		chapterModel.setFontName(e.attributeValue("fontName"));
		chapterModel.setFontSize(e.attributeValue("fontSize"));
		int chapterNumber = getIntValue(e.attributeValue("no"));
		chapterModel.setChapterNumber(chapterNumber);
		copyAttributes(e, chapterModel);
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("text".equals(se.getName())) {
				TextModel textModel = parseText(se);
				models.add(textModel);
			} else if ("image".equals(se.getName())) {
				ImageModel imageModel = parseImage(se);
				models.add(imageModel);
			} else if ("complex".equals(se.getName())) {
				ComplexModel complexModel = parseComplex(se);
				models.add(complexModel);
			} else if ("section".equals(se.getName())) {
				SectionModel sectionModel = parseSection(se);
				models.add(sectionModel);
			} else if ("table".equals(se.getName())) {
				TableModel tableModel = parseTable(se);
				models.add(tableModel);
			} else if ("list".equals(se.getName())) {
				ListModel listModel = parseList(se);
				models.add(listModel);
			} else if ("newpage".equals(se.getName())) {
				NewPageModel newPageModel = parseNewPage(se);
				models.add(newPageModel);
			}else if ("line".equals(se.getName())){
				LineModel lineModel = parseLine(se);
				models.add(lineModel);
			}
		}
		chapterModel.setModels(models);
		return chapterModel;
	}

	
	
	@SuppressWarnings("rawtypes")
	private SectionModel parseSection(Element e) {
		SectionModel sectionModel = new SectionModel();
		List<Model> models = new ArrayList<Model>();
		sectionModel.setSectionName(e.attributeValue("name"));
		sectionModel.setColor(e.attributeValue("color"));
		sectionModel.setFontName(e.attributeValue("fontName"));
		sectionModel.setFontSize(e.attributeValue("fontSize"));
		if(StringUtils.isNotEmpty(e.attributeValue("isNumber"))){
			sectionModel.setIsNumber(e.attributeValue("isNumber"));
		}
		if(e.attributeValue("isNewPage")!=null && !"".equals(e.attributeValue("isNewPage"))){
			sectionModel.setNewPage(Boolean.parseBoolean(e.attributeValue("isNewPage")));
		}
		
		if(e.attributeValue("depth")!=null && !"".equals(e.attributeValue("depth"))){
			sectionModel.setDepth(Integer.parseInt(e.attributeValue("depth")));
		}
		
		copyAttributes(e, sectionModel);
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("text".equals(se.getName())) {
				TextModel textModel = parseText(se);
				models.add(textModel);
			} else if ("image".equals(se.getName())) {
				ImageModel imageModel = parseImage(se);
				models.add(imageModel);
			} else if ("complex".equals(se.getName())) {
				ComplexModel complexModel = parseComplex(se);
				models.add(complexModel);
			} else if ("section".equals(se.getName())) {
				SectionModel subSectionModel = parseSection(se);
				models.add(subSectionModel);
			} else if ("table".equals(se.getName())) {
				TableModel tableModel = parseTable(se);
				models.add(tableModel);
			} else if ("list".equals(se.getName())) {
				ListModel listModel = parseList(se);
				models.add(listModel);
			} else if ("newpage".equals(se.getName())) {
				NewPageModel pageModel = new NewPageModel();
				models.add(pageModel);
			}
		}
		sectionModel.setModels(models);
		return sectionModel;
	}
	
	@SuppressWarnings("rawtypes")
	private ComplexModel parseComplex(Element e) {
		ComplexModel complexModel = new ComplexModel();
		if(StringUtils.isNotEmpty(e.attributeValue("align"))){
			complexModel.setAlign(e.attributeValue("align"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("leading"))){
			complexModel.setLeading(e.attributeValue("leading"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("spacing"))){
			complexModel.setSpacing(e.attributeValue("spacing"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("spacingB"))){
			complexModel.setSpacingB(e.attributeValue("spacingB"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("indentationLeft"))){
			complexModel.setIndentationLeft(e.attributeValue("indentationLeft"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("indentationRight"))){
			complexModel.setIndentationRight(e.attributeValue("indentationRight"));
		}
		
		int height = getIntValue(e.attributeValue("height"));
		if (height > 0) {
			complexModel.setHeight(height);
		}
		copyAttributes(e, complexModel);
		List<Model> models = new ArrayList<Model>();
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("text".equals(se.getName())) {
				TextModel textModel = parseText(se);
				models.add(textModel);
			} else if ("image".equals(se.getName())) {
				ImageModel imageModel = parseImage(se);
				models.add(imageModel);
			}else if ("line".equals(se.getName())) {
				LineModel lineModel = parseLine(se);
				models.add(lineModel);
			}
		}
		complexModel.setModels(models);
		return complexModel;
	}
	
	private ImageModel parseImage(Element e) {
		ImageModel imageModel = new ImageModel();
		copyAttributes(e, imageModel);
		String type = e.attributeValue("type");
		String shape = e.attributeValue("shape");
		int width = getIntValue(e.attributeValue("width"));
		if (width > 0) {
			imageModel.setWidth(width);
		}
		int height = getIntValue(e.attributeValue("height"));
		if (height > 0) {
			imageModel.setHeight(height);
		}
		if(StringUtils.isNotEmpty(e.attributeValue("riskRise"))){
			imageModel.setRiskRise(e.attributeValue("riskRise"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("z"))){
			imageModel.setZ(e.attributeValue("z"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("h"))){
			imageModel.setH(e.attributeValue("h"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("color"))){
			imageModel.setColor(e.attributeValue("color"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("indentLeft"))){
			imageModel.setIndentLeft(e.attributeValue("indentLeft"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("single"))){
			imageModel.setSingle(e.attributeValue("single"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("bg"))){
			imageModel.setBg(e.attributeValue("bg"));
		}
		imageModel.setType(type);
		imageModel.setShape(shape);
		imageModel.setWidth(width);
		imageModel.setHeight(height);
		imageModel.setSpacing(getFloatValue(e.attributeValue("spacing")));
		imageModel.setSpacingB(getFloatValue(e.attributeValue("spacingB")));
		String text = e.getTextTrim();
		imageModel.setName(text);
		return imageModel;
	}
	
	private TextModel parseText(Element e) {
		TextModel textModel = new TextModel();
		String a=e.getText().replaceAll("\n", "\r\n");
		copyAttributes(e,textModel);
		String text = a;
		if(StringUtils.isNotEmpty(e.attributeValue("whole")) && e.attributeValue("whole").equals("true")){
			
		}else{
			text = a.trim();
		}
		textModel.setText(text);
		textModel.setSup(e.attributeValue("sup"));
		textModel.setSub(e.attributeValue("sub"));
		textModel.setSpacing(getFloatValue(e.attributeValue("spacing")));
		textModel.setSpacingB(getFloatValue(e.attributeValue("spacingB")));
		if(StringUtils.isNotEmpty(e.attributeValue("rise"))){
			textModel.setRise(getFloatValue(e.attributeValue("rise")));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("underLine"))){
			textModel.setUnderLine(e.attributeValue("underLine"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("z"))){
			textModel.setZ(e.attributeValue("z"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("h"))){
			textModel.setH(e.attributeValue("h"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("indentationLeft"))){
			textModel.setIndentationLeft(e.attributeValue("indentationLeft"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("indentationRight"))){
			textModel.setIndentationRight(e.attributeValue("indentationRight"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("hasSpacing"))){
			textModel.setHasSpacing(e.attributeValue("hasSpacing"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("genericTag"))){
			textModel.setGenericTag(e.attributeValue("genericTag"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("id"))){
			textModel.setId(e.attributeValue("id"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("pageNumFromName"))){
			textModel.setPageNumFromName(e.attributeValue("pageNumFromName"));
		}
		
		int leading = 0;
		try {
			leading = Integer.parseInt(e.attributeValue("leading"));
		} catch (Exception ex) {}
		textModel.setLeading(leading);
		
		int wordSpace = 0;
		try {
			wordSpace = Integer.parseInt(e.attributeValue("wordSpace"));
		} catch (Exception ex) {}
		textModel.setWordSpace(wordSpace);
		
		return textModel;
	}
	
	@SuppressWarnings("rawtypes")
	private CatalogModel parseCatalog(Element e) {
		CatalogModel catalogModel = new CatalogModel();
		copyAttributes(e, catalogModel);
		String name = e.attributeValue("name");
		catalogModel.setColor(e.attributeValue("color"));
		catalogModel.setFontName(e.attributeValue("fontName"));
		catalogModel.setFontSize(e.attributeValue("fontSize"));
		catalogModel.setAlign(e.attributeValue("align"));
		catalogModel.setName(name);
		if(e.attributeValue("widPer")!=null ){
			catalogModel.setWidPer(Float.parseFloat(e.attributeValue("widPer")));
		}
		List<RefModel> refModels = new ArrayList<RefModel>();
		List<Model> models = new ArrayList<Model>();
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("ref".equals(se.getName())) {
				RefModel refModel = parseRef(se);
				refModels.add(refModel);
			}
			if ("text".equals(se.getName())) {
				TextModel textModel = parseText(se);
				models.add(textModel);
			} else if ("image".equals(se.getName())) {
				ImageModel imageModel = parseImage(se);
				models.add(imageModel);
			} else if ("complex".equals(se.getName())) {
				ComplexModel complexModel = parseComplex(se);
				models.add(complexModel);
			} else if ("section".equals(se.getName())) {
				SectionModel sectionModel = parseSection(se);
				models.add(sectionModel);
			} else if ("table".equals(se.getName())) {
				TableModel tableModel = parseTable(se);
				models.add(tableModel);
			} else if ("list".equals(se.getName())) {
				ListModel listModel = parseList(se);
				models.add(listModel);
			} else if ("newpage".equals(se.getName())) {
				NewPageModel pageModel = new NewPageModel();
				models.add(pageModel);
			}
		}
		catalogModel.setModels(models);
		catalogModel.setRefList(refModels);
		
		return catalogModel;
	}
	
	private RefModel parseRef(Element e) {
		RefModel refModel = new RefModel();
		copyAttributes(e, refModel);
		String name = e.attributeValue("name");
		refModel.setName(name);
		Element ie = e.element("image");
		if(StringUtils.isNotEmpty(e.attributeValue("isSplitting"))){
			refModel.setIsSplitting(e.attributeValue("isSplitting"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("isAll"))){
			refModel.setIsAll(e.attributeValue("isAll"));
		}
		if (ie != null) {
			ImageModel imageModel = parseImage(ie);
			refModel.setImage(imageModel);
		}
		int depth = 0;
		try {
			depth = Integer.parseInt(e.attributeValue("depth"));
		} catch (Exception ex) {}
		
		refModel.setDepth(depth);
		
		int fixheight = 0;
		try {
			fixheight = Integer.parseInt(e.attributeValue("fixheight"));
		} catch (Exception ex) {}
		
		if(e.attributeValue("refSpacing")!=null && !e.attributeValue("refSpacing").equals("")){
			float refSpacing = Float.parseFloat(e.attributeValue("refSpacing"));
			refModel.setRefSpacing(refSpacing);
		}
		if(e.attributeValue("widthPer")!=null &&  !e.attributeValue("widthPer").equals("")){
			float widthPer = Float.parseFloat(e.attributeValue("widthPer"));
			refModel.setWidthPer(widthPer);
		}
		if(e.attributeValue("begin")!=null){
			refModel.setBegin(Integer.parseInt(e.attributeValue("begin")));
		}else{
			refModel.setBegin(0);
		}
		if(e.attributeValue("beginNum")!=null && !e.attributeValue("beginNum").equals("")){
			refModel.setBeginNum(e.attributeValue("beginNum"));
		}
		refModel.setFixheight(fixheight);
		return refModel;
	}
	
	@SuppressWarnings("rawtypes")
	private TableModel parseTable(Element e) {
		TableModel tableModel = new TableModel();
		copyAttributes(e, tableModel);
		int border = getIntValue(e.attributeValue("border"));
		if (border >= 0) {
			tableModel.setBorder(border);
		}
		//isFilletBorder 是否是圆角表格
		if(StringUtils.isNotEmpty(e.attributeValue("isFilletBorder"))){
			tableModel.setIsFilletBorder(e.attributeValue("isFilletBorder"));
		}
		
		if(StringUtils.isNotEmpty(e.attributeValue("borderWidth"))){
			tableModel.setBorderWidth(e.attributeValue("borderWidth"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("spacing"))){
			tableModel.setSpacing(e.attributeValue("spacing"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("spacingB"))){
			tableModel.setSpacingB(e.attributeValue("spacingB"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("z"))){
			tableModel.setZ(e.attributeValue("z"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("h"))){
			tableModel.setH(e.attributeValue("h"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("headerRowNum"))){
			tableModel.setHeaderRowNum(e.attributeValue("headerRowNum"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("keepTogether"))){
			tableModel.setKeepTogether(e.attributeValue("keepTogether"));
		}
		
		
		int leading = getIntValue(e.attributeValue("leading"));
		if (leading >= 0) {
			tableModel.setLeading(leading);
		}
		if(e.attributeValue("rowSpanNum")!=null && !e.attributeValue("rowSpanNum").equals("")){
			tableModel.setRowSpanNum(e.attributeValue("rowSpanNum"));
		}
		String borderColor=e.attributeValue("borderColor");
		if (borderColor!=null && !"".equals(borderColor)) {
			tableModel.setBorderColor(borderColor);
		}
		
		List<RowModel> rows = new ArrayList<RowModel>();
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("row".equals(se.getName())) {
				RowModel rowModel = parseRow(se);
				rows.add(rowModel);
			} else if ("header".equals(se.getName())) {
				RowModel header = parseRow(se);
				tableModel.setHeader(header);
			}
		}
		tableModel.setRows(rows);
		String width = e.attributeValue("width");
		if (width != null) {
			tableModel.setWidth(getFloatValue(width));
		}
		String colstr = e.attributeValue("cols");
		if (colstr != null) {
			String[] colWidths = colstr.split(",");
			float[] cols = new float[colWidths.length];
			for (int i = 0; i < cols.length; i++) {
				cols[i] = getFloatValue(colWidths[i]);
			}
			tableModel.setCols(cols);
		}
		return tableModel;
	}
	
	@SuppressWarnings("rawtypes")
	private ListModel parseList(Element e) {
		ListModel listModel = new ListModel();
		copyAttributes(e, listModel);
		String symbol = e.attributeValue("symbol");
		if (symbol != null) {
			listModel.setSymbol(symbol);
		}
		
		String color = e.attributeValue("color");
		if (StringUtils.isNotEmpty(color)) {
			listModel.setColor(color);
		}
		
		String size = e.attributeValue("size");
		if (StringUtils.isNotEmpty(size)) {
			listModel.setSize(size);
		}
		
		String rise = e.attributeValue("rise");
		if (StringUtils.isNotEmpty(rise)) {
			listModel.setRise(rise);
		}
		
		List<Model> models = new ArrayList<Model>();
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("text".equals(se.getName())) {
				TextModel textModel = parseText(se);
				models.add(textModel);
			} else if ("image".equals(se.getName())) {
				ImageModel imageModel = parseImage(se);
				models.add(imageModel);
			} else if ("complex".equals(se.getName())) {
				ComplexModel complexModel = parseComplex(se);
				models.add(complexModel);
			}
		}
		listModel.setModels(models);
		return listModel;
	}
	
	private float getFloatValue(String value) {
		if (value == null) {
			return 0;
		}
		try {
			float fvalue = Float.parseFloat(value);
			return fvalue;
		} catch (Exception e) {}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	private RowModel parseRow(Element e) {
		RowModel rowModel = new RowModel();
		copyAttributes(e, rowModel);
		List<CellModel> cells = new ArrayList<CellModel>();
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("cell".equals(se.getName())) {
				CellModel cellModel = parseCell(se);
				cells.add(cellModel);
			}
		}
		rowModel.setHeight(getFloatValue(e.attributeValue("height")));
		rowModel.setCells(cells);
		return rowModel;
		
	}
	
	private CellModel parseCell(Element e) {
		CellModel cellModel = new CellModel();
		copyAttributes(e, cellModel);
		int border = getIntValue(e.attributeValue("border"));
		if (border >= 0) {
			cellModel.setBorder(border);
		}
		if(StringUtils.isNotEmpty(e.attributeValue("singleHeight"))){
			cellModel.setSingleHeight(e.attributeValue("singleHeight"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("paddingTop"))){
			cellModel.setPaddingTop(e.attributeValue("paddingTop"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("paddingBottom"))){
			cellModel.setPaddingBottom(e.attributeValue("paddingBottom"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("paddingLeft"))){
			cellModel.setPaddingLeft(e.attributeValue("paddingLeft"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("paddingRight"))){
			cellModel.setPaddingRight(e.attributeValue("paddingRight"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("isNew"))){
			cellModel.setIsNew(e.attributeValue("isNew"));
		}
		
		if(StringUtils.isNotEmpty(e.attributeValue("isShading"))){
			cellModel.setIsShading(e.attributeValue("isShading"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("fixedAndMultipliedLeading"))){
			cellModel.setFixedAndMultipliedLeading(e.attributeValue("fixedAndMultipliedLeading"));
		}
		
		
		//下面这两个属性的处理正在测试中，暂时不能用。
		String borderColorTop=e.attributeValue("borderColorTop");
		if (borderColorTop!=null && !"".equals(borderColorTop)) {
			cellModel.setBorderColorTop(borderColorTop);
		}
		String borderColorBottom=e.attributeValue("borderColorBottom");
		if (borderColorBottom!=null && !"".equals(borderColorBottom)) {
			cellModel.setBorderColorBottom(borderColorBottom);
		}
		String borderColorLeft=e.attributeValue("borderColorLeft");
		if (borderColorLeft!=null && !"".equals(borderColorLeft)) {
			cellModel.setBorderColorLeft(borderColorLeft);
		}
		String borderColorRight=e.attributeValue("borderColorRight");
		if (borderColorRight!=null && !"".equals(borderColorRight)) {
			cellModel.setBorderColorRight(borderColorRight);
		}
		String borderColor=e.attributeValue("borderColor");
		if (borderColor!=null && !"".equals(borderColor)) {
			cellModel.setBorderColor(borderColor);
		}
		//END
		
		Element ie = e.element("image");
		Element te = e.element("table");
		Element tee = e.element("text");
		Element complex = e.element("complex");
		Element list = e.element("list");
		if (te != null) {
			TableModel tableModel = parseTable(te);
			cellModel.setTableModel(tableModel);
		} else if (ie != null) {
			ImageModel imageModel = parseImage(ie);
			cellModel.setImageModel(imageModel);
		} else if(tee !=null){
			TextModel textModel=parseText(tee);
			cellModel.setTextModel(textModel);
		}else if(complex!=null){
			ComplexModel complexModel=parseComplex(complex);
			cellModel.setComplexModel(complexModel);
		}else if(list!=null){
			ListModel listModel=parseList(list);
			cellModel.setListModel(listModel);
		}else{
			cellModel.setText(e.getTextTrim());
		}
		int colspan= getIntValue(e.attributeValue("colspan"));
		if (colspan > 0) {
			cellModel.setColspan(colspan);
		}
		int rowspan= getIntValue(e.attributeValue("rowspan"));
		if (rowspan > 0) {
			cellModel.setRowspan(rowspan);
		}
		int Leading= getIntValue(e.attributeValue("Leading"));
		if (Leading > 0) {
			cellModel.setLeading(Leading);
		}
		int padding= getIntValue(e.attributeValue("padding"));
		if (padding > 0) {
			cellModel.setPadding(padding);
		}
		if(e.attributeValue("vAlign")!=null && !e.attributeValue("vAlign").equals("")){
			cellModel.setvAlign(e.attributeValue("vAlign"));
		}
		
		copyAttributes(e, cellModel);
		List<Model> models = new ArrayList<Model>();
		for (Iterator iter=e.elementIterator();iter.hasNext();) {
			Element se = (Element)iter.next();
			if ("text".equals(se.getName())) {
				TextModel textModel = parseText(se);
				models.add(textModel);
			} else if ("image".equals(se.getName())) {
				ImageModel imageModel = parseImage(se);
				models.add(imageModel);
			}
		}
		cellModel.setModels(models);
		
		return cellModel;
	}
	
	@SuppressWarnings("rawtypes")
	private LineModel parseLine(Element e) {
		LineModel lineModel = new LineModel();
		copyAttributes(e, lineModel);
		
		if(StringUtils.isNotEmpty(e.attributeValue("width"))){
			lineModel.setWidth(e.attributeValue("width"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("percentage"))){
			lineModel.setPercentage(e.attributeValue("percentage"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("offset"))){
			lineModel.setOffset(e.attributeValue("offset"));
		}
		if(StringUtils.isNotEmpty(e.attributeValue("dotted"))){
			lineModel.setDotted(e.attributeValue("dotted"));
		}
		
		return lineModel;
	}
	
	
	@SuppressWarnings("rawtypes")
	private NewPageModel parseNewPage(Element e) {
		NewPageModel newPageModel = new NewPageModel();
		
		if(StringUtils.isNotEmpty(e.attributeValue("isNew"))){
			newPageModel.setIsNew(e.attributeValue("isNew"));
		}
		
		return newPageModel;
	}
	
	
	private int getIntValue(String value) {
		if (value == null) {
			return -1;
		}
		try {
			int ivalue = Integer.parseInt(value);
			return ivalue;
		} catch (Exception ex) {}
		return -1;
	}
	
	private void copyAttributes(Element e, Model model) {
		model.setId(e.attributeValue("id"));
		model.setId(e.attributeValue("name"));
		model.setColor(e.attributeValue("color"));
		model.setBgcolor(e.attributeValue("bgcolor"));
		model.setAlign(e.attributeValue("align"));
		model.setIndent(e.attributeValue("indent"));
		model.setFontSize(e.attributeValue("fontSize"));
		model.setFontName(e.attributeValue("fontName"));
		model.setStyle(e.attributeValue("style"));
		model.setNumericType(e.attributeValue("numericType"));
		model.setBeginPageNum(e.attributeValue("beginPageNum"));
		model.setNumModel(e.attributeValue("numModel"));
		//表格边框圆角
		model.setFilletBorderColor(e.attributeValue("filletBorderColor"));
		model.setFilletBorderLineWidth(e.attributeValue("filletBorderLineWidth"));
		model.setFilletBorderRadian(e.attributeValue("filletBorderRadian"));
		model.setFilletBorderX(e.attributeValue("filletBorderX"));
		model.setFilletBorderY(e.attributeValue("filletBorderY"));
		model.setFilletBorderW(e.attributeValue("filletBorderW"));
		model.setFilletBorderH(e.attributeValue("filletBorderH"));
		model.setFilletCIndex(e.attributeValue("filletCIndex"));
		//表格背景圆角
		model.setShadingColor(e.attributeValue("shadingColor"));
		model.setShadingRadian(e.attributeValue("shadingRadian"));
		model.setShadingX(e.attributeValue("shadingX"));
		model.setShadingY(e.attributeValue("shadingY"));
		model.setShadingW(e.attributeValue("shadingW"));
		model.setShadingH(e.attributeValue("shadingH"));
		model.setShadingCIndex(e.attributeValue("shadingCIndex"));
		//表格边框或者背景的类型
		model.setEventType(e.attributeValue("eventType"));
		//
	}

}
