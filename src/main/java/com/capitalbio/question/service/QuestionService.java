package com.capitalbio.question.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.FileUtil;
import com.capitalbio.pdf.PdfGenerator;
import com.capitalbio.pdf.XmlReader;
import com.capitalbio.pdf.model.DocModel;
import com.capitalbio.pdf.util.FreemarkerUtil;
import com.capitalbio.pdf.util.ProjectPathUtil;
import com.capitalbio.question.dao.QuestionDAO;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

@Service
public class QuestionService extends BaseService{
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired private QuestionDAO questionDAO;
	
	@Autowired
	private FileManageService fileManageService;
	
	public Map<String, Object> generatePdf(Map<String, Object> mapData) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			String uniqueId = mapData.get("uniqueId").toString();
			String reportType = "ps";
			String templeteName = "question.ftl";
			String checkDate = (String)mapData.get("checkDate");
			Map<String, Object> pathMap = getPathMap(uniqueId, checkDate);
			/** pdf文件夹命名规则 姓名_身份证号 **/
			File pdfTargetDir = (File) pathMap.get("pdfTargetDir");
			if (pdfTargetDir.exists()) {
				/** 若文件夹已存在则清空文件夹 **/
				FileUtil.delFolder(pdfTargetDir.getPath());
			} 
			/** 文件夹不存在则创建文件夹 **/
			pdfTargetDir.mkdirs();
			File imgTargetDir = (File) pathMap.get("staticDir");
			File xmlDir = (File) pathMap.get("xmlDir");
			String pdfFileName = pathMap.get("pdfFileName").toString();
			String resultFileName = pathMap.get("resultFileName").toString();
			String pdfTmpFileName = pathMap.get("pdfTmpFileName").toString();
		
			convert(templeteName, xmlDir.getAbsolutePath(), mapData);

			XmlReader reader = new XmlReader();
			DocModel docModel = reader.parse(xmlDir.getAbsolutePath());
			PdfGenerator gen = new PdfGenerator();

			gen.setChartDirName(imgTargetDir.getAbsolutePath() + "/");
			gen.setFileName(pdfFileName);
			gen.setTempFileName(pdfTmpFileName);
			
			System.out.println("    " + new Date() + "  开始生成报告...请稍候");
			gen.generatePdf(docModel, reportType);
			System.out.println("    " + new Date() + "  生成报告成功，Cheers！");
			System.out.println("成功！" + pdfFileName);
			mapResult.put("fileName", resultFileName);
			return mapResult;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("生成报告出错！");
			mapResult.put("message", "生成报告出错！");
			return mapResult;
		}
	}
	
	/**
	 * 
	 * 
	 * @param uniqueId
	 * @param checkDate
	 * @param dirName projectScreening
	 * @return
	 */
	private Map<String,Object> getPathMap(String uniqueId, String checkDate){
		Map<String, Object> mapResult = new HashMap<String,Object>();
		/** 编译文件路径 **/
		String path = ProjectPathUtil.class.getClassLoader().getResource("").getPath();
		File webClassesDir = new File(path);
		/** 项目根路径 **/
		File webRootDir = webClassesDir.getParentFile().getParentFile();
		String dirName = "questionScreening";
		String pdfNameCategory = "ps";
		File staticDir = new File(webRootDir,"static");
		staticDir = new File(staticDir,"img");
		if (!staticDir.exists()) {
			staticDir = new File(webRootDir,"src");
			staticDir = new File(staticDir,"main");
			staticDir = new File(staticDir,"webapp");
			staticDir = new File(staticDir,"static");
		} else {
			staticDir = new File(webRootDir,"static");
		}
		
		/** 生成pdf文件路径 **/
		Date date = new Date();
		File pdfDir = new File(staticDir,"pdf");
		
		String resultFolderName = pdfDir+"/"+dirName+"/" + uniqueId;
		File pdfTargetDir = new File(resultFolderName);
		String dateStr = checkDate.replaceAll("-", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String xmlFileName = sdf.format(date) + "_model.xml";
		File xmlDir = new File(pdfTargetDir, xmlFileName);
		String resultFileName = "static/pdf/"+dirName+"/"+ uniqueId  + "/" + sdf1.format(date) + ".pdf";
		String pdfTmpFileName = pdfTargetDir + "/" + "tmp.pdf";
		String pdfFileName = pdfTargetDir + "/"+dateStr + "_" + pdfNameCategory + ".pdf";
		mapResult.put("pdfTargetDir", pdfTargetDir);
		mapResult.put("resultFileName", resultFileName);
		mapResult.put("pdfTmpFileName", pdfTmpFileName);
		mapResult.put("xmlDir", xmlDir);
		mapResult.put("pdfFileName", pdfFileName);
		mapResult.put("staticDir", staticDir);
		mapResult.put("dateStr", dateStr);
		return mapResult;
	}
	
	private static void convert(String templateName, String destFileName, Map<String, Object> map) throws Exception {
		Configuration conf = new Configuration();
		conf.setClassicCompatible(true);
		conf.setDefaultEncoding("UTF-8");
		conf.setClassForTemplateLoading(FreemarkerUtil.class, "/template");
		Template template = conf.getTemplate(templateName);
		template.setEncoding("UTF-8");
		template.setObjectWrapper(new DefaultObjectWrapper());
		Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), "UTF-8"));
		template.process(map, out);
		out.close();
	}
	
	/**
	 * 上传PDF文件
	 * @return
	 */
	public Map<String,Object> uploadPdf(String uniqueId, String checkDate) {
		logger.info("上传PDF文件--uniqueId=="+uniqueId);
		Map<String, Object> mapResult = new HashMap<String,Object>();
		Map<String, Object> pathMap = getPathMap(uniqueId, checkDate);
		String pdfFileName = pathMap.get("pdfFileName").toString();
		String dateStr = pathMap.get("dateStr").toString();
		File pdfFile = new File(pdfFileName);
		if (pdfFile.exists()) {
			/** 文件存在 **/
			String fileId = "";
				fileId = uniqueId + "_" + dateStr + "_ps.pdf";
			try {
				/** 上传 **/
				fileManageService.uploadFile(pdfFile, fileId);
				String fileName = fileManageService.getFileUrl(fileId);
				/** 删除临时文件夹 **/
				File pdfTargetDir = (File) pathMap.get("pdfTargetDir");
				FileUtil.delFolder(pdfTargetDir.getPath());
				System.out.println(fileId);
				System.out.println("上传PDF文件成功--fileId=="+fileId);
				mapResult.put("fileName", fileName);
				mapResult.put("fileId", fileId);
				return mapResult;
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("上传PDF文件失败");
			}
			return null;
		}
		return null;
	}
	
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return questionDAO;
	}

	@Override
	public String getCollName() {
		return "question";
	}
}
