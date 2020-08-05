package com.capitalbio.pdf.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

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
public class GeneratePdfService1 {

	/**
	 * 生成报告
	 * 
	 * @param mapData
	 * @return
	 */
	public Map<String, Object> generatePdf(Map<String, Object> mapData) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		String reportType = "ps";
		Map<String, Object> pathGeneratePdf = ProjectPathUtil.getPathGeneratePdf();
		File pdfDir = (File) pathGeneratePdf.get("pdfDir");
		File staticDir = (File) pathGeneratePdf.get("staticDir");
		File webRootDir = (File) pathGeneratePdf.get("webRootDir");

		File imgTargetDir = staticDir;
		String customerId = mapData.get("customerId").toString();
		String name = mapData.get("name").toString();
		/** pdf文件夹命名规则 姓名_身份证号 **/
		File pdfTargetDir = new File(new File(pdfDir, "projectScreening"), name + "_" + customerId);
		if (!pdfTargetDir.exists()) {
			pdfTargetDir.mkdirs();
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String xmlFileName = sdf.format(date) + "_model.xml";
		File xmlDir = new File(pdfTargetDir, xmlFileName);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String pdfFileName = pdfTargetDir + "/" + sdf1.format(date) + ".pdf";
		// static\pdf\projectScreening\沈闯_411122199209068279/20180510.pdf
		String resultFileName = "static/pdf/projectScreening/" + name + "_" + customerId + "/" + sdf1.format(date)
				+ ".pdf";
		String pdfTmpFileName = pdfTargetDir + "/" + "tmp.pdf";

		File pdfFile = new File(pdfFileName);
		if (pdfFile.exists()) {
			pdfFile.delete();
		}

		try {
			convert("projectScreening.ftl", xmlDir.getAbsolutePath(), mapData);

			XmlReader reader = new XmlReader();
			DocModel docModel = reader.parse(xmlDir.getAbsolutePath());
			PdfGenerator gen = new PdfGenerator();

			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString();// 获得本机IP
			System.out.println("IP" + ip);
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
}
