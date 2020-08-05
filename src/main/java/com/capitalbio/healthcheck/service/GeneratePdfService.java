package com.capitalbio.healthcheck.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.FileUtil;
import com.capitalbio.pdf.PdfGenerator;
import com.capitalbio.pdf.XmlReader;
import com.capitalbio.pdf.model.DocModel;
import com.capitalbio.pdf.util.FreemarkerUtil;
import com.capitalbio.pdf.util.ProjectPathUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/***
 * 生成报告
 * 
 * @author admin
 *
 */
@Service
public class GeneratePdfService {
	@Autowired
	private FileManageService fileManageService;
	private static final Logger logger = Logger.getLogger(GeneratePdfService.class);
	
	private static Map<String,String> mapResultDM = new HashMap<String,String>();
	static{
		mapResultDM.put("0", "检测信息缺失，无法判断");
		mapResultDM.put("1", "低_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估&lt;25分，且\n"
				+ "c. 3.9mmol/L≤血糖（空腹）&lt;6.1mmol/L 或 3.9mmol/L≤血糖（餐后2h）&lt;7.8mmol/L 或 3.9mmol/L≤随机血糖值&lt;11.1mmol/L");
		mapResultDM.put("2", "高_a.您的登记信息显示已被医生确诊为糖尿病，且"
				+ "b.3.9mmol/L &lt;血糖（空腹）&lt;16.7mmol/L 或血糖（餐后2h）为任一值或随机血糖为任一值");
		mapResultDM.put("3", "立即转诊_a.您的登记信息显示已被医生确诊为糖尿病，且"
				+ "b.血糖（空腹）≤3.9mmol/L 或血糖（空腹）≥16.7mmol/L ");
		
		mapResultDM.put("4", "高_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估≥25分，且\n"
				+ "c. 3.9mmol/L≤血糖（空腹）&lt;6.1mmol/L 或 3.9mmol/L≤血糖（餐后2h）&lt;7.8mmol/L 或 3.9mmol/L≤随机血糖值&lt;11.1mmol/L");
		mapResultDM.put("5", "立即转诊_a. 已患疾病未勾选“糖尿病”，且"
				+ "b. 糖尿病危险因素评估≥25分， 且"
				+ "c. 血糖（空腹）&lt;3.9mmol/L 或 血糖（餐后2h）&lt;3.9mmol/L 或 随机血糖值&lt;3.9mmol/L");
		
		mapResultDM.put("6", "高_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估&lt;25分， 且\n"
				+ "c. 6.1mmol/L≤血糖（空腹）&lt;7.0mmol/L 或 7.8mmol/L≤血糖（餐后2h）&lt;11.1mmol/L");
		mapResultDM.put("7", "高_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估≥25分， 且\n"
				+ "c. 6.1mmol/L≤血糖（空腹）&lt;7.0mmol/L 或 7.8mmol/L≤血糖（餐后2h）&lt;11.1mmol/L");
		
		mapResultDM.put("8", "提醒就医_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估&lt;25分，且\n"
				+ "c. 7.0mmol/L≤血糖（空腹）&lt;16.7mmol/L 或 血糖（餐后2h）≥11.1mmol/L 或 随机血糖值≥11.1mmol/L");
		mapResultDM.put("9", "提醒就医_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估≥25分，且\n"
				+ "c. 7.0mmol/L≤血糖（空腹）&lt;16.7mmol/L 或 血糖（餐后2h）≥11.1mmol/L 或 随机血糖值≥11.1mmol/L");
		
//		mapResultDM.put("10", "立即转诊_a. 已患疾病未勾选“糖尿病”，且\n"
//				+ "b. 糖尿病危险因素评估&lt;25分，且\n"
//				+ "c. 血糖（空腹）≥7.0mmol/L 或 血糖（餐后2h）≥11.1mmol/L 或 随机血糖值≥11.1mmol/L");
		mapResultDM.put("10", "提醒就医_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 血糖(空腹）≥16.7mmol/L");
		mapResultDM.put("11", "立即转诊_a. 已患疾病未勾选“糖尿病”，且\n"
				+ "b. 糖尿病危险因素评估&lt;25分，且\n"
				+ "c. 血糖（空腹）&lt;3.9mmol/L 或 血糖（餐后2h）&lt;3.9mmol/L 或 随机血糖值&lt;3.9mmol/L");
	}
	/**
	 * 生成报告
	 * 
	 * @param mapData
	 * @return
	 */
	public Map<String, Object> generatePdf(Map<String, Object> mapData,String category) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			String uniqueId = mapData.get("uniqueId").toString();
			String checkDate = "";
			String reportType = "ps";
			String templeteName = "";
			if (category.equals("projectScreening")) {
				checkDate = (String)mapData.get("checkDate");
				templeteName = "projectScreening.ftl";
			} else {
				checkDate = (String)mapData.get("checkDateJs");
				templeteName = "projectScreeningDetail.ftl";
			}
			Map<String, Object> pathMap = getPathMap(uniqueId,checkDate,category);
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

			/** 健康风险判断 **/
			if (category.equals("projectScreening")) {
				mapData = getHealthRisk(mapData);
			} else {
//				mapData = getHealthRiskDetail(mapData);
			}
		
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
	private Map<String,Object> getPathMap(String uniqueId,String checkDate,String category){
		Map<String, Object> mapResult = new HashMap<String,Object>();
		/** 编译文件路径 **/
		String path = ProjectPathUtil.class.getClassLoader().getResource("").getPath();
		File webClassesDir = new File(path);
		/** 项目根路径 **/
		File webRootDir = webClassesDir.getParentFile().getParentFile();
		String dirName = "projectScreening";
		String pdfNameCategory = "";
		if (category.equals("projectScreening")) {
			pdfNameCategory = "ps";
		} else {
			pdfNameCategory = "psd";
		}
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
//		String dateStr = sdf1.format(date);
		String dateStr = checkDate.replaceAll("-", "");
		String xmlFileName = sdf.format(date) + "_model.xml";
		File xmlDir = new File(pdfTargetDir, xmlFileName);
		String resultFileName = "static/pdf/"+dirName+"/"+ uniqueId  + "/" + sdf1.format(date) + ".pdf";
		String pdfTmpFileName = pdfTargetDir + "/" + "tmp.pdf";
		String pdfFileName = pdfTargetDir + "/" + dateStr + "_" + pdfNameCategory + ".pdf";
		mapResult.put("pdfTargetDir", pdfTargetDir);
		mapResult.put("resultFileName", resultFileName);
		mapResult.put("pdfTmpFileName", pdfTmpFileName);
		mapResult.put("xmlDir", xmlDir);
		mapResult.put("pdfFileName", pdfFileName);
		mapResult.put("staticDir", staticDir);
		mapResult.put("dateStr", dateStr);
		return mapResult;
	}

	/**
	 * 获取得分
	 * 
	 * @param key
	 * @param value
	 * @param mapAlgorithm
	 * @return
	 */
	private String getScore(String key, String value, Map<String, Object> mapAlgorithm, String sex) {

		double valueNum;
		try {
			valueNum = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return "0";
		}

		for (Entry<String, Object> entry : mapAlgorithm.entrySet()) {
			String rangStr = entry.getKey();
			String scoreStr = entry.getValue().toString();
			if (key.equals("waistline")) {
				if (!rangStr.contains(sex)) {
					continue;
				}
				rangStr = rangStr.replaceAll(sex + "-", "");
			}
			String[] rangArr = rangStr.split("-");
			double minNum = Double.parseDouble(rangArr[0]);
			double maxNum = Double.parseDouble(rangArr[1]);
			if ((valueNum >= minNum) && (valueNum <= maxNum)) {
				return scoreStr;
			}
		}

		return "0";
	}
	
	/**
	 * 获取初筛报告所需数据
	 * 计算 健康风险 中 血糖 血脂 血压
	 * 
	 * @param mapData
	 * @return
	 */
	private Map<String, Object> getHealthRisk(Map<String, Object> mapData) {
		/** 血糖情况 **/
		if (mapData.get("dmRiskType")!=null 
				&& StringUtils.isNotBlank(mapData.get("dmRiskType").toString())) {
			String dmRiskType = mapData.get("dmRiskType").toString();
			if (null != mapResultDM.get(dmRiskType)) {
				String strResultDM = mapResultDM.get(dmRiskType).toString();
				if (strResultDM.contains("_")){
					String[] arrResultDM = strResultDM.split("_");
					mapData.put("bloodSugarHealthRisk", arrResultDM[0]);
					mapData.put("bloodSugarResultDesc", arrResultDM[1]);
				}
			}
			
		}
		if (mapData.get("bloodPressureCondition")!=null && StringUtils.isNotBlank(mapData.get("bloodPressureCondition").toString())) {
			/** 血压情况 **/
			String bloodPressureCondition = mapData.get("bloodPressureCondition").toString();
			if (bloodPressureCondition.equals("正常")) {
				mapData.put("bloodPressureHealthRisk", "低");
				mapData.put("bloodPressureResultDesc", "a. 已患疾病未勾选“高血压”，且\nb. 收缩压&lt;130mmHg 且 舒张压&lt;85mmHg");
			}else if (bloodPressureCondition.equals("高血压患者")) {
				mapData.put("bloodPressureHealthRisk", "高");
				mapData.put("bloodPressureResultDesc", "您的登记信息显示已被医生确诊为高血压");
			}else if (bloodPressureCondition.equals("血压异常人群")) {
				mapData.put("bloodPressureHealthRisk", "高");
				mapData.put("bloodPressureResultDesc", "a. 已患疾病未勾选“高血压”，且\nb. 收缩压≥130mmHg 或 舒张压≥85mmHg");
			}
		}
		if (mapData.get("bloodLipidCondition")!=null && StringUtils.isNotBlank(mapData.get("bloodLipidCondition").toString())) {
			/** 血脂情况 **/
			String bloodLipidCondition = mapData.get("bloodLipidCondition").toString();
			if (bloodLipidCondition.equals("正常")) {
				mapData.put("bloodLipidHealthRisk", "低");
				mapData.put("bloodLipidResultDesc", "a. 已患疾病未勾选“高血脂”，且\nb. 总胆固醇TC&lt;5.2mmol/L 且 甘油三酯TG&lt;1.7mmol/L 且 高密度脂蛋白胆固醇HDL-C≥1.0mmol/L");
			}else if (bloodLipidCondition.equals("血脂异常患者")) {
				mapData.put("bloodLipidHealthRisk", "高");
				mapData.put("bloodLipidResultDesc", "您的登记信息显示已被医生确诊为血脂异常");
			}else if (bloodLipidCondition.equals("血脂异常高风险人群")) {
				mapData.put("bloodLipidHealthRisk", "高");
				mapData.put("bloodLipidResultDesc", "a. 已患疾病未勾选“高血脂”，且\nb. 总胆固醇 TC≥5.2mmol/L 或  甘油三酯 TG≥1.7mmol/L 或 高密度脂蛋白胆固醇 HDL-C&lt;1.0mmol/L（40mg/dI）");
			}
		}
		return mapData;
	}
	
	/**
	 * 获取初筛报告所需数据
	 * 计算 健康风险 中 血糖 血脂 血压
	 * 
	 * @param mapData
	 * @return
	 */
//	private Map<String, Object> getHealthRiskDetail(Map<String, Object> mapData) {
//		
//		
//		
//		return null;
//	}
	
//	/**20190703backup
//	 * 计算 健康风险 中 血糖 血脂 血压
//	 * 
//	 * @param mapData
//	 * @return
//	 */
//	private Map<String, Object> getHealthRisk(Map<String, Object> mapData) {
//		if (mapData.get("bloodSugarCondition")!=null && StringUtils.isNotBlank(mapData.get("bloodSugarCondition").toString())) {
//			/** 血糖情况 **/
//			String bloodSugarCondition = mapData.get("bloodSugarCondition").toString();
//			String dmRisk = mapData.get("dmRisk").toString();
//			if (bloodSugarCondition.equals("正常")) {
//				mapData.put("bloodSugarHealthRisk", "低");
////				mapData.put("bloodSugarResultDesc", "糖尿病危险因素评估＜25分，并且血糖（空腹）＜6.1mmol/L，且血糖（餐后2h）＜7.8mmol/L，且随机血糖值≤11.1mmol/L");
//				mapData.put("bloodSugarResultDesc", "糖尿病危险因素评估&lt;25分，且血糖（空腹）&lt;6.1mmol/L，且血糖（餐后2h）&lt;7.8mmol/L，且随机血糖&lt;11.1mmol/L");
//			}else if (bloodSugarCondition.equals("糖尿病患者")) {
//				mapData.put("bloodSugarHealthRisk", "高");
//				mapData.put("bloodSugarResultDesc", "您的登记信息显示已被医生确诊为糖尿病");
//			}else if (bloodSugarCondition.equals("血糖异常人群")) {
//				if (dmRisk.equals("高")) {
//					mapData.put("bloodSugarHealthRisk", "高");
//					mapData.put("bloodSugarResultDesc", "6.1mmol/L≤血糖（空腹）＜7.0mmol/L，或7.8mmol/L≤血糖（餐后2h）＜11.1mmol/L");
//				} else if (dmRisk.equals("提醒就医")) {
//					mapData.put("bloodSugarHealthRisk", "提醒就医");
//					mapData.put("bloodSugarResultDesc", "血糖（空腹）≥7.0mmol/L， 或血糖（空腹、餐后2h、随机）任一项＜3.9mmol/L，或血糖（空腹、餐后2h、随机）任一项≥11.1mmol/L");
//				}
////				mapData.put("bloodSugarHealthRisk", "高");
////				mapData.put("bloodSugarResultDesc", "血糖（空腹）≥6.1mmol/L，或血糖（餐后2h）≥7.8mmol/L，或随机血糖≥11.1mmol/L");
//			}else if (bloodSugarCondition.equals("糖尿病高风险人群")) {
//				if (dmRisk.equals("高")) {
//					mapData.put("bloodSugarHealthRisk", "高");
//					mapData.put("bloodSugarResultDesc", "糖尿病危险因素评估≥25分，且3.9mmol/L≤血糖（空腹）＜6.1mmol/L，且3.9mmol/L≤血糖（餐后2h）＜7.8mmol/L，且3.9mmol/L≤随机血糖值＜11.1mmol/L");
//				} else if (dmRisk.equals("提醒就医")) {
//					mapData.put("bloodSugarHealthRisk", "提醒就医");
//					mapData.put("bloodSugarResultDesc", "糖尿病危险因素评估≥25分， 且血糖（空腹 、餐后2h、随机）任一项＜3.9mmol/L");
//				}
////				mapData.put("bloodSugarHealthRisk", "高");
////				mapData.put("bloodSugarResultDesc", "糖尿病危险因素评估≥25分，且血糖（空腹）&lt;6.1mmol/L，且血糖（餐后2h）&lt;7.8mmol/L，且随机血糖&lt;11.1mmol/L");
//			}
////			else if (bloodSugarCondition.equals("血糖异常人群")) {
////				mapData.put("bloodSugarHealthRisk", "高");
////				mapData.put("bloodSugarResultDesc", "血糖（空腹）≥6.1mmol/L，和/或，血糖（餐后2h）≥7.8mmol/L，和/或，随机血糖≥11.1mmol/L");
////			}
//		}
//		if (mapData.get("bloodPressureCondition")!=null && StringUtils.isNotBlank(mapData.get("bloodPressureCondition").toString())) {
//			/** 血压情况 **/
//			String bloodPressureCondition = mapData.get("bloodPressureCondition").toString();
//			if (bloodPressureCondition.equals("正常")) {
//				mapData.put("bloodPressureHealthRisk", "低");
//				mapData.put("bloodPressureResultDesc", "收缩压&lt;130mmHg，且舒张压&lt;85mmHg");
//			}else if (bloodPressureCondition.equals("高血压患者")) {
//				mapData.put("bloodPressureHealthRisk", "高");
//				mapData.put("bloodPressureResultDesc", "您的登记信息显示已被医生确诊为高血压");
//			}else if (bloodPressureCondition.equals("血压异常人群")) {
//				mapData.put("bloodPressureHealthRisk", "高");
//				mapData.put("bloodPressureResultDesc", "收缩压≥130mmHg，或舒张压≥85mmHg");
//			}
//		}
//		if (mapData.get("bloodLipidCondition")!=null && StringUtils.isNotBlank(mapData.get("bloodLipidCondition").toString())) {
//			/** 血脂情况 **/
//			String bloodLipidCondition = mapData.get("bloodLipidCondition").toString();
//			if (bloodLipidCondition.equals("正常")) {
//				mapData.put("bloodLipidHealthRisk", "低");
//				mapData.put("bloodLipidResultDesc", "总胆固醇TC&lt;5.2mmol/L，且甘油三酯TG&lt;1.7mmol/L，且高密度脂蛋白胆固醇HDL-C≥1.0mmol/L");
//			}else if (bloodLipidCondition.equals("血脂异常患者")) {
//				mapData.put("bloodLipidHealthRisk", "高");
//				mapData.put("bloodLipidResultDesc", "您的登记信息显示已被医生确诊为血脂异常");
//			}else if (bloodLipidCondition.equals("血脂异常高风险人群")) {
//				mapData.put("bloodLipidHealthRisk", "高");
//				mapData.put("bloodLipidResultDesc", "总胆固醇TC≥5.2mmol/L， 或甘油三酯TG≥1.7mmol/L， 或高密度脂蛋白胆固醇HDL-C&lt;1.0mmol/L");
//			}
//		}
//		return mapData;
//	}

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
	public static void main(String[] args) {
//		String a = "123";
//		String substring = a.substring(1, a.length());
//		System.out.println(substring);
//		FileManageService fileManageService2 = new FileManageService();
//		try {
////			fileManageService2.uploadFile(new File("E:\\二次开发\\2018下半年\\社区健康筛查与健康管理系统\\开发目录\\code\\数据\\data.txt"), "data.txt");
//			String fileUrl = fileManageService2.getFileUrl("fatGuidance.pdf");
//			System.out.println(fileUrl);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/** 编译文件路径 **/
		String path = ProjectPathUtil.class.getClassLoader().getResource("").getPath();
		File webClassesDir = new File(path);
		/** 项目根路径 **/
		File webRootDir = webClassesDir.getParentFile().getParentFile();
		
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
	}
	
	/**
	 * 上传PDF文件
	 * @return
	 */
	public Map<String,Object> uploadPdf(String uniqueId,String checkDate,String category) {
		logger.info("上传PDF文件--uniqueId=="+uniqueId);
		Map<String, Object> mapResult = new HashMap<String,Object>();
		Map<String, Object> pathMap = getPathMap(uniqueId,checkDate,category);
		String pdfFileName = pathMap.get("pdfFileName").toString();
		String dateStr = pathMap.get("dateStr").toString();
		File pdfFile = new File(pdfFileName);
		if (pdfFile.exists()) {
			/** 文件存在 **/
			String fileId = "";
			if (category.equals("projectScreening")) {
				fileId = uniqueId + "_" + dateStr + "_ps.pdf";
			} else {
				fileId = uniqueId + "_psd.pdf";
			}
//			String fileId = uniqueId + "_" + dateStr + "_" + pdfNameCategory + ".pdf";
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
}
