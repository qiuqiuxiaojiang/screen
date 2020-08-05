package com.capitalbio.eye.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.util.Constant;
import com.capitalbio.common.log.SpecialLog;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.service.SubmitClientService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.EncryptUtil;
import com.capitalbio.common.util.MD5Util;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.ZipUtils;
import com.capitalbio.eye.service.BasicInfoService;
import com.capitalbio.eye.service.EyeFileService;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.service.ReportService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings({ "rawtypes", "unchecked" }) 
@Controller
@RequestMapping("/eye")
public class RestEyeController {
	private Logger logger=LoggerFactory.getLogger(getClass());
	@Autowired SubmitClientService submitClientService;
	
	@Autowired AuthService authService;
	
	@Autowired EyeRecordService eyeRecordService;
	@Autowired BasicInfoService basicInfoService;
	
	@Autowired ReportService reportService;
	@Autowired FileManageService fileService;
	@Autowired EyeFileService eyeFileService;

	/**
	 * 输出错误消息，XML格式
	 * @param response
	 * @param message
	 * @throws Exception
	 */
	private void outputError(HttpServletResponse response, String message) {
		Element root = DocumentHelper.createElement("result");  
        Document document = DocumentHelper.createDocument(root);  
        root.addAttribute("code", "1");  
          
        Element element1 = root.addElement("message");  
        element1.addText(message);  
        response.setContentType("text/xml;charset=utf-8");
        response.setHeader("cache-control", "no-cache");
        try {
            XMLWriter w = new XMLWriter(response.getWriter());
            w.write(document);
            w.close();  
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.debug("output",e);
        }
		
	}
	
	/**
	 * 上传数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="upload",method=RequestMethod.POST)
	@ResponseBody
	@SpecialLog
	public void upload(HttpServletRequest request,HttpServletResponse response){
		String zipTempDir="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmss_");
		String dateDir= sdf.format(new Date());
		String tempName = dateDir+UUID.randomUUID().toString();
		Map<String,Object> messageObj = Maps.newHashMap();
		try{
			MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) request;
			MultipartFile uploadFile = mreq.getFile("file");
			String apikey = request.getParameter("apikey");
			Map<String, Object> obj = Maps.newHashMap();
			obj.put("apikey", apikey);
			Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
			if (appClient==null) {
				outputError(response, "未找到apikey");
				return;
			}
			if (uploadFile == null) {
				outputError(response,"file is null");
				return;
			}
			
			zipTempDir=PropertyUtils.getProperty("system.temp.dir");//上传和解压的临时文件夹
			File dir=new File(zipTempDir);
		    if(!dir.exists()){
		    	dir.mkdirs();
		    }
		    if (!dir.exists()) {
		    	outputError(response, "system config error");
		    	return;
		    }
		    
		    File outFile=new File(dir,tempName+".zip");
		    messageObj.put("zipFile", outFile.getAbsolutePath());
		    
		    //把zip文件上传到临时文件夹中
		    uploadFile.transferTo(outFile);
		    
		    File unzipDir = new File(dir, tempName);
		    unzipDir.mkdirs();
		    //解压zip,并返回解压后的目录
		    ZipUtils.unZip(unzipDir.getAbsolutePath(), outFile.getAbsolutePath());
		    
		    File[] files = unzipDir.listFiles();
		    if (files.length == 0) {
		    	outputError(response,"file is null");
		    }
		    File firstFile = files[0];
		    if (firstFile.isDirectory()) {
		    	unzipDir = firstFile;
		    }
		    
		    File basicInfoFile = new File(unzipDir, "info.xml");
		    if (!basicInfoFile.exists()) {
				outputError(response,"info.xml not exist");
				return;
		    }
		    //解析basicinof.xml
		    Map<String,Object> infoMap = Maps.newHashMap();
		    Map<String,Object> recordMap =parseBasicInfoXmlFile(unzipDir, basicInfoFile, outFile, infoMap);
		    if (recordMap == null) {
		    	outputError(response,"info.xml parse error");
		    }
		    String reportFileName = (String)recordMap.get("reportFileName");
		    File resultFile = new File(unzipDir, reportFileName);
		    if (resultFile.exists()) {
		    	parseResultXmlFile(resultFile, recordMap, messageObj);
		    }
		    String surveyFileName = (String)recordMap.get("surveyFileName");
		    File surveyFile = new File(unzipDir, surveyFileName);
		    if (surveyFile.exists()) {
		    	parseSurveyXmlFile(surveyFile, recordMap);
		    }
		    
		    outPutResult(response);
		    logEyeRecord(infoMap, recordMap, messageObj);
		    eyeRecordService.saveData("uploadLog", messageObj);
			cleanDir(zipTempDir,tempName);
		} catch(Exception e){
			e.printStackTrace();
			logger.debug("upload error",e);
		}
	}
	
	private Map<String,Object> logEyeRecord(Map<String,Object> infoMap, Map<String,Object> recordMap, Map<String,Object> messageObj) {
		String uniqueId = (String)recordMap.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			messageObj.put("name", infoMap.get("name"));
			messageObj.put("cardId", recordMap.get("cardId"));
		}
		messageObj.put("userId", recordMap.get("userId"));
		messageObj.put("visitId", recordMap.get("visitId"));
		messageObj.put("uniqueId", recordMap.get("uniqueId"));
		messageObj.put("checkPlace", recordMap.get("checkPlace"));
		messageObj.put("zipFile", recordMap.get("zipFile"));
		messageObj.put("zipFile_oss", recordMap.get("zipFile_oss"));
		messageObj.put("infoId", infoMap.get("id"));
		messageObj.put("recordId", recordMap.get("id"));
		return messageObj;
	}
	
	
	/**
	 * 下载目诊结果数据
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="download",method=RequestMethod.POST)
	@SpecialLog
	public void down(HttpServletRequest request,HttpServletResponse response) throws Exception{
		SAXReader reader = new SAXReader();
		Document doc = reader.read(request.getInputStream());
		Element root = doc.getRootElement();
		String apikey = root.attributeValue("apikey");
		String userId=root.element("userId").getTextTrim();
		String start = null;
		String end = null;
		Element visitTagElement = root.element("visittag");
		if (visitTagElement != null) {
			Element startElement = visitTagElement.element("startDate");
			if (startElement != null) {
				start=startElement.getTextTrim();
			}
			Element endElement = visitTagElement.element("endDate");
			if (endElement != null) {
				end=endElement.getTextTrim();
			}
		}
		Map<String, Object> obj = Maps.newHashMap();
		obj.put("apikey", apikey);
		Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
		if (appClient==null) {
			outputError(response, "未找到apikey");
			return;
		}
		Map<String,Object> basicMap=basicInfoService.getInfoByUserId(userId);
		if (basicMap == null) {
			outputError(response, "未找到对应的用户");
			return;
		}
		eyeRecordService.downFileByUser(userId, basicMap, request, response, start, end);
	}
	
	/**
	 * 根据上传数据时间查询对应的用户ID列表信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="queryUserList",method=RequestMethod.POST)
	@SpecialLog
	public void queryUserIdList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(request.getInputStream());
		Element root = doc.getRootElement();
		String apikey = root.attributeValue("apikey");
		Map<String, Object> obj = Maps.newHashMap();
		obj.put("apikey", apikey);
		Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
		if (appClient==null) {
			outputError(response, "未找到apikey");
			return;
		}
		Element visitTag = root.element("visittag");
		List<String> userIdList = Lists.newArrayList();
		if (visitTag != null) {
			Element startElement = visitTag.element("startDate");
			String start = null;
			if (startElement != null) {
				start=startElement.getTextTrim();
			}
			Element endElement = visitTag.element("endDate");
			String end = null;
			if (endElement != null) {
				end=endElement.getTextTrim();
			}
			
			userIdList = eyeRecordService.queryUserByVisitTime(start, end); 
		}
		
		
		Element otherTag = root.element("othertag");
		Map<String,Object> params = Maps.newHashMap();
		if (otherTag != null) {
			for (Iterator iter = otherTag.elementIterator("infoNode"); iter.hasNext();) {
				Element infoNode = (Element) iter.next();
				String key = infoNode.attributeValue("name");
				String value = infoNode.getTextTrim();
				params.put(key, value);
			}
			if (params.size() > 0) {
				List<String> userIdList2 = eyeRecordService.queryUserByInfo(params);
				userIdList.retainAll(userIdList2);
			}
		}
		
		Element outroot=DocumentHelper.createElement("result");
		Document document=DocumentHelper.createDocument(outroot);
		outroot.addAttribute("code","0");
		Element msg=outroot.addElement("message");
		msg.setText("success");
		for (String userId:userIdList) {
			Element userIdEle = outroot.addElement("userId");
			userIdEle.setText(userId);
		}
		response.setCharacterEncoding("text/xml;charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		XMLWriter writer=new XMLWriter(response.getOutputStream());
		writer.write(document);
		writer.flush();
		writer.close();

	}
	
	/**
	 * 根据条件查询已有报告的用户ID列表信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="queryReportUserList",method=RequestMethod.POST)
	@SpecialLog
	public void queryReportUserIdList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(request.getInputStream());
		Element root = doc.getRootElement();
		String apikey = root.attributeValue("apikey");
		Map<String, Object> obj = Maps.newHashMap();
		obj.put("apikey", apikey);
		Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
		if (appClient==null) {
			outputError(response, "未找到apikey");
			return;
		}
		String start = null;
		String end = null;
		Map<String,Object> params = Maps.newHashMap();
		for (Iterator iter = root.elementIterator("infoNode"); iter.hasNext();) {
			Element infoNode = (Element) iter.next();
			String key = infoNode.attributeValue("name");
			if (StringUtils.isEmpty(key)) {
				continue;
			}
			String value = infoNode.getTextTrim();
			if (key.equals("visitTimeStart")) {
				start = value;
			} else if (key.equals("visitTimeEnd")) {
				end = value;
			} else {
				params.put(key, value);
			}
		}
		List<String> userIdList = Lists.newArrayList();
		userIdList = eyeRecordService.queryReportUserByVisitTime(start, end); 
		List<String> userIdList2 = eyeRecordService.queryUserByInfo(params);
		userIdList.retainAll(userIdList2);
		
		Element outroot=DocumentHelper.createElement("result");
		Document document=DocumentHelper.createDocument(outroot);
		outroot.addAttribute("code","0");
		Element msg=outroot.addElement("message");
		msg.setText("success");
		for (String userId:userIdList) {
			Element userIdEle = outroot.addElement("userId");
			userIdEle.setText(userId);
		}
		response.setCharacterEncoding("text/xml;charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		XMLWriter writer=new XMLWriter(response.getOutputStream());
		writer.write(document);
		writer.flush();
		writer.close();

	}


	/**
	 * 根据用户ID获得用户基本信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="userinfo",method=RequestMethod.POST)
	@SpecialLog
	public void queryBasicInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(request.getInputStream());
		Element root = doc.getRootElement();
		String apikey = root.attributeValue("apikey");
		Map<String, Object> obj = Maps.newHashMap();
		obj.put("apikey", apikey);
		Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
		if (appClient==null) {
			outputError(response, "未找到apikey");
			return;
		}
		String userId=root.element("userId").getTextTrim();
		Map<String,Object> basicMap=basicInfoService.getInfoByUserId(userId);
		if (basicMap == null) {
			outputError(response, "未找到对应的用户");
			return;
		}

		Document document=DocumentHelper.createDocument();
		Element outroot=document.addElement("info");
		Element basicElement = outroot.addElement("basicinfo");
		Iterator iter = basicMap.entrySet().iterator(); 
		while (iter.hasNext()) { 
			Map.Entry entry = (Map.Entry) iter.next(); 
			Object key = entry.getKey(); 
			Object val = entry.getValue(); 
			if (!(key.equals("_id") || key.equals("id") || key.equals("ctime") || key.equals("utime"))) {
				Element tagName=basicElement.addElement(key.toString());
				tagName.setText(val.toString());
			}
		}
		outroot.addAttribute("code","0");
		Element msg=outroot.addElement("message");
		msg.setText("success");
		response.setCharacterEncoding("text/xml;charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		XMLWriter writer=new XMLWriter(response.getOutputStream());
		writer.write(document);
		writer.flush();
		writer.close();

	}
	
	/**
	 * 上传专家数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="uploadDoctor",method=RequestMethod.POST)
	@ResponseBody
	@SpecialLog
	public void uploadDoctor(HttpServletRequest request,HttpServletResponse response){
		String zipTempDir="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmss");
		String dateDir= sdf.format(new Date());
		String tempName = dateDir+UUID.randomUUID().toString();
		try{
			MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) request;
			MultipartFile uploadFile = mreq.getFile("file");
			String apikey = request.getParameter("apikey");
			Map<String, Object> obj = Maps.newHashMap();
			obj.put("apikey", apikey);
			Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
			if (appClient==null) {
				outputError(response, "未找到apikey");
				return;
			}
			if (uploadFile == null) {
				outputError(response,"file is null");
				return;
			}
			
			zipTempDir=PropertyUtils.getProperty("system.temp.dir");//上传和解压的临时文件夹
			File dir=new File(zipTempDir);
		    if(!dir.exists()){
		    	dir.mkdirs();
		    }
		    
		    File outFile=new File(dir,tempName+".zip");
		    
		    //把zip文件上传到临时文件夹中
		    uploadFile.transferTo(outFile);
		    
		    File unzipDir = new File(dir, tempName);
		    unzipDir.mkdirs();
		    //解压zip,并返回解压后的目录
		    ZipUtils.unZip(unzipDir.getAbsolutePath(), outFile.getAbsolutePath());
		    
		    File[] files = unzipDir.listFiles();
		    if (files.length == 0) {
		    	outputError(response,"file is null");
		    }
		    File firstFile = files[0];
		    if (firstFile.isDirectory()) {
		    	unzipDir = firstFile;
		    }
		    
		    File basicInfoFile = new File(unzipDir, "info.xml");
		    if (!basicInfoFile.exists()) {
				outputError(response,"info.xml not exist");
				return;
		    }
		    //解析basicinof.xml
		    Map<String,Object> recordMap =parseDoctorInfoXmlFile(unzipDir, basicInfoFile, outFile);
		    String expertId = (String)recordMap.get("expertId");
		    String registTime = (String)recordMap.get("registTime");
		    
			Element root=DocumentHelper.createElement("result");
			Document document=DocumentHelper.createDocument(root);
			root.addAttribute("code","0");
			Element msg=root.addElement("message");
			msg.setText("success");
			Element idElement = root.addElement("expertId");
			idElement.setText(expertId);
			Element timeElement = root.addElement("registTime");
			timeElement.setText(registTime);
			response.setCharacterEncoding("text/xml;charset=utf-8");
			response.setHeader("cache-control", "no-cache");
			XMLWriter writer=new XMLWriter(response.getOutputStream());
			writer.write(document);
			writer.flush();
			writer.close();
			cleanDir(zipTempDir,tempName);

		}catch(Exception e){
			e.printStackTrace();
			logger.debug("upload error",e);
		}
		
	}
	
	/**
	 * 根据专家ID下载专家信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="downloadDoctor",method=RequestMethod.POST)
	@SpecialLog
	public void downloadDoctorInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(request.getInputStream());
		Element root = doc.getRootElement();
		String apikey = root.attributeValue("apikey");
		Map<String, Object> obj = Maps.newHashMap();
		obj.put("apikey", apikey);
		Map<String, Object> appClient = submitClientService.getDataByQuery(obj);
		if (appClient==null) {
			outputError(response, "未找到apikey");
			return;
		}
		Map<String,Object> params = Maps.newHashMap();
		for (Iterator iter = root.elementIterator("infoNode"); iter.hasNext();) {
			Element infoNode = (Element) iter.next();
			String key = infoNode.attributeValue("name");
			if (StringUtils.isEmpty(key)) {
				continue;
			}
			String value = infoNode.getTextTrim();
			params.put(key, value);
		}
		
		
		List<Map<String,Object>> doctorList= basicInfoService.queryDoctorList(params);
		if (doctorList == null || doctorList.size() == 0) {
			outputError(response, "未找到对应的数据");
			return;
		}
		//临时文件夹
		String zipTempDir=PropertyUtils.getProperty("system.temp.dir");
		//根据用户创建根目录
		String fileName=UUID.randomUUID().toString();
		File dir=new File(zipTempDir,fileName);
		if(!dir.exists()){
			dir.mkdirs();
		}

		for (Map<String,Object> doctor:doctorList) {
			Document document=DocumentHelper.createDocument();
			Element outroot=document.addElement("info");
			Element basicElement = outroot.addElement("basicinfo");
			String signImage = (String)doctor.remove("signImage");
			String expertId = (String)doctor.get("expertId");
			if (StringUtils.isNotEmpty(signImage)) {
				fileService.downFile(new File(dir, expertId+".jpg"), signImage);
			}

			Iterator iter = doctor.entrySet().iterator(); 
			while (iter.hasNext()) { 
				Map.Entry entry = (Map.Entry) iter.next(); 
				Object key = entry.getKey(); 
				Object val = entry.getValue(); 
				if (val == null) {
					val = "";
				}
				if (!(key.equals("_id") || key.equals("id") || key.equals("ctime") || key.equals("utime"))) {
					Element tagName=basicElement.addElement(key.toString());
					tagName.setText(val.toString());
				}
			}
			
            OutputFormat format = OutputFormat.createPrettyPrint();     
            FileOutputStream fos = new FileOutputStream(new File(dir, expertId+".xml"));     
            XMLWriter writer = new XMLWriter(fos, format);     
            writer.write(document);     
            writer.close();     
		}
		
		ZipUtils.zipDirectory(zipTempDir, dir.getAbsolutePath());
        //生成并下载zip文件
        File downFile=new File(zipTempDir,fileName+".zip");
        downStream(downFile,dir,request,response);
        FileUtils.deleteDirectory(dir);
        FileUtils.deleteQuietly(downFile);
	}

	

	private Map<String,Object> parseDoctorInfoXmlFile(File dir, File xmlFile, File zipFile) throws Exception{
		SAXReader saxReader=new SAXReader();
		Map<String,Object> params=Maps.newHashMap();
		String expertId="";
		String registTime = "";
		Document document=saxReader.read(xmlFile);
		Element root=document.getRootElement();
		Element infoElement = root.element("basicinfo");
		for(Iterator iter=infoElement.elementIterator();iter.hasNext();){
			Element element=(Element) iter.next();
			if(element.getName().equals("expertId")){
				expertId=element.getTextTrim();
			} else if(element.getName().equals("registTime")){
				registTime=element.getTextTrim();
			} else {
				params.put(element.getName(),element.getTextTrim());
			}

		}
		
		
		Map<String,Object> expertMap = null;
		if (StringUtils.isNotEmpty(expertId)) {
			expertMap=basicInfoService.getExpertInfoById(expertId);
		} else {
			String name = (String)params.get("name");
			String idno = (String)params.get("idno");
			if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(idno)) {
				expertMap = basicInfoService.getExpertInfoByNameAndNo(name,idno);
				if (expertMap != null) {
					expertId = (String)expertMap.get("expertId");
				}
			}
		}
		if (expertMap == null) {
			expertId = UUID.randomUUID().toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			registTime = sdf.format(new Date());
			params.put("registTime", registTime);
		}
		
		Element imageFileElement = root.element("signimginfo");
		String fileName = null;
		if (imageFileElement != null) {
			Element imageElement = imageFileElement.element("image");
			
			if (imageElement != null) {
				fileName = imageElement.getTextTrim();
			}
		}
		File file = new File(dir, fileName);
		if (StringUtils.isNotEmpty(fileName) && file.exists()) {
			String fsId = fileService.uploadFile(file, getUploadFileId(file.getName()));
//			String fsId = filesManageService.uploadFileToFs(file,expertId,"sign");
			params.put("signImage", fsId);
		}

		if(expertMap==null){
			params.put("expertId", expertId);
			eyeRecordService.saveDataByModule("expertInfo",params);
			return params;
		} else {
			expertMap.putAll(params);
			eyeRecordService.saveDataByModule("expertInfo",expertMap);
			return expertMap;
		}
	}


	private Map<String,Object> parseBasicInfoXmlFile(File dir, File xmlFile, File zipFile, Map<String,Object> infoMap) throws Exception{
		SAXReader saxReader=new SAXReader();
		Map<String,Object> params=Maps.newHashMap();
		String userId="";
		String visitId = "";
		String customerId = "";
//		String cardId = "";
		String checkPlace = "";
		Document document = null;
		try {
			document=saxReader.read(xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (document == null) {
			return null;
		}
		Element root=document.getRootElement();
		Element infoElement = root.element("basicinfo");
		for(Iterator iter=infoElement.elementIterator();iter.hasNext();){
			Element element=(Element) iter.next();
			if(element.getName().equals("userId")){
				userId=element.getTextTrim();
			}
			if (element.getName().equals("visitTime")) {
				visitId = element.getTextTrim();
			}
			if (element.getName().equals("idno")) {
				customerId = element.getTextTrim();
			}
			if (element.getName().equals("checkPlace")) {
				checkPlace = element.getTextTrim();
			}
			params.put(element.getName(),element.getTextTrim());
		}
		
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId1 = (String) ContextUtils.getSession().getAttribute("userId");
		Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId1);
		String uniqueId = null;
		if (secretMap != null) {
			uniqueId = (String)secretMap.get("uniqueId");
		}
		String idno = (String)params.remove("idno");
		if (StringUtils.isNotEmpty(idno)) {
			idno = EncryptUtil.encrypt(idno);
		}
		String name = (String)params.remove("name");
		if (StringUtils.isNotEmpty(name)) {
			name = EncryptUtil.encrypt(name);
		}
		String mobile = (String)params.remove("mobile");
		if (StringUtils.isNotEmpty(mobile)) {
			mobile = EncryptUtil.encrypt(mobile);
		}
		
		if (StringUtils.isEmpty(uniqueId)) {
			logger.debug("uniqueId not found, customerId="+customerId);
			params.put("idno", idno);
			params.put("name", name);
			params.put("mobile", mobile);
		} else {
			params.put("uniqueId", uniqueId);
		}
		
		Map<String,Object> ehrInfoMap=basicInfoService.getInfoByUserId(userId);
		if(ehrInfoMap==null){
			params.put("uploadTime", new Date());
			String infoId = eyeRecordService.saveDataByModule("basicInfo",params);
			infoMap.putAll(params);
			infoMap.put("id", infoId);
		} else {
			ehrInfoMap.putAll(params);
			ehrInfoMap.put("uploadTime", new Date());
			String infoId = eyeRecordService.saveDataByModule("basicInfo",ehrInfoMap);
			infoMap.putAll(ehrInfoMap);
			infoMap.put("id", infoId);
		}
		
		Element imagesElement = root.element("imageinfo");
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap.put("userId", userId);
		queryMap.put("visitId", visitId);
		Map<String,Object> uploadRecord=eyeRecordService.getDataByQuery(queryMap);
		if (uploadRecord == null) {
			uploadRecord = Maps.newHashMap();
			uploadRecord.put("userId", userId);
			uploadRecord.put("visitId", visitId);
			Date visitTime = ParamUtils.getDate(visitId);
			uploadRecord.put("visitTime", visitTime);
		} else {
			eyeFileService.deleteFile(uploadRecord);
		}
		if (StringUtils.isNotEmpty(uniqueId)) {
			uploadRecord.put("uniqueId", uniqueId);
		} else {
			uploadRecord.put("idno", idno);
			uploadRecord.put("name", name);
		}
//		if (StringUtils.isNotEmpty(cardId)) {
//			uploadRecord.put("cardId", cardId);
//		}
		if (StringUtils.isNotEmpty(checkPlace)) {
			uploadRecord.put("checkPlace", checkPlace);
		}
//		uploadRecord.put("name", infoMap.get("name"));
		
		if (imagesElement != null) {
			Element leftElement = imagesElement.element("leftImages");
			if (leftElement != null) {
				List<Element> imageList = leftElement.elements("image");
				for (Element imageElement:imageList) { 
					String pos = "";
					Element pElement = imageElement.element("position");
					if (pElement!=null) {
						pos = "l"+pElement.getTextTrim().toLowerCase();
					}
					List<Element> imageFileList = imageElement.elements("imageId");
					List<Map<String,Object>> fileList = Lists.newArrayList();
					for (Element imageFileElement:imageFileList) {
						String fileName = imageFileElement.getTextTrim();
						File file = new File(dir, fileName);
						if (file.exists()) {
							Map<String,Object> fileMap = Maps.newHashMap();
							fileMap.put("fileName", fileName);
							String fileId = fileService.uploadFile(file, getUploadFileId(file.getName()));
							fileMap.put("fileId", fileId);
							fileList.add(fileMap);
						}
					}
					uploadRecord.put(pos, fileList);
				}
			}
			Element rightElement = imagesElement.element("rightImages");
			if (rightElement != null) {
				List<Element> imageList = rightElement.elements("image");
				for (Element imageElement:imageList) { 
					String pos = "";
					Element pElement = imageElement.element("position");
					if (pElement!=null) {
						pos = "r"+pElement.getTextTrim().toLowerCase();
					}
					List<Element> imageFileList = imageElement.elements("imageId");
					List<Map<String,Object>> fileList = Lists.newArrayList();
					for (Element imageFileElement:imageFileList) {
						String fileName = imageFileElement.getTextTrim();
						File file = new File(dir, fileName);
						if (file.exists()) {
							Map<String,Object> fileMap = Maps.newHashMap();
							fileMap.put("fileName", fileName);
							String fileId = fileService.uploadFile(file, getUploadFileId(file.getName()));
							fileMap.put("fileId", fileId);

							fileList.add(fileMap);
						}
					}
					uploadRecord.put(pos, fileList);
				}
			}
		}
		Element reportFileElement = root.element("resultinfo");
		String reportFileName = "report.xml";
		if (reportFileElement != null) {
			Element resultIdElement = reportFileElement.element("resultId");
			reportFileName = resultIdElement.getTextTrim();
		}
		Element surveyFileElement = root.element("surveyinfo");
		String surveyFileName = "survey.xml";
		if (surveyFileElement != null) {
			Element surveyIdElement = surveyFileElement.element("surveyId");
			surveyFileName = surveyIdElement.getTextTrim();
		}
		uploadRecord.put("reportFileName", reportFileName);
		uploadRecord.put("surveyFileName", surveyFileName);
		//save pdf file
		File pdfFile = new File(dir, "report-r.pdf");
		if (pdfFile.exists()) {
			String fileId = fileService.uploadFile(pdfFile, getUploadFileId(pdfFile.getName()));
			uploadRecord.put("pdfFile", fileId);
		}
		File parsePdfFile = new File(dir, "report.pdf");
		if (parsePdfFile.exists()) {
			String fileId = fileService.uploadFile(parsePdfFile, getUploadFileId(parsePdfFile.getName()));
			uploadRecord.put("parsePdfFile", fileId);
		}
		
		// save zip file
		String fileId = fileService.uploadFile(zipFile, getUploadFileId(zipFile.getName()));
		uploadRecord.put("zipFile", fileId);
		
		//process parse eye image
		Map<String,Object> parsedMap = getParsedFileMap(dir, queryMap);
		if (parsedMap != null) {
			uploadRecord.put("parsedMap", parsedMap);
		}
		
		String id = eyeRecordService.saveData(uploadRecord);
		logger.info("save data success "+userId);
		uploadRecord.put("id", id);
		
		return uploadRecord;
	}
	
	private Map<String,Object> getParsedFileMap(File dir, Map<String,Object> queryMap) throws Exception{
		String[] positions = new String[]{"LUp","LDown","LLeft","LRight","RUp","RDown","RLeft","RRight"};
		String[] types = new String[]{"Vessle","Dot", "Plaque", "WuMan", "YueYun"};
		Map<String,Object> parsedMap = Maps.newHashMap();
		for (int i = 0; i < positions.length; i++) {
			for (int j = 0; j < types.length; j++) {
				String fileName = positions[i]+"_"+types[j];
				File file = new File(dir, fileName+".jpg");
				if (file.exists()) {
					String fileId = fileService.uploadFile(file, getUploadFileId(file.getName()));
					parsedMap.put("fileName", fileId);
				}
			}
		}
		if (parsedMap.isEmpty()) {
			return null;
		}
		return parsedMap;
	}
	
	private String getUploadFileId(String fileName) {
			String uuid = UUID.randomUUID().toString();
			StringBuffer key = new StringBuffer();
			key.append("eye/").append(uuid).append("_").append(fileName);
			String fileId = key.toString();
			return fileId;
	}
	private Map<String,String> getFileMap(Map<String,Object> eyeRecord) {
		Map<String,String> map = Maps.newHashMap();
		String[] cols = new String[]{"lf","ll","lr","lu","ld","rf","rl","rr","ru","rd"};
		for (int i = 0; i < cols.length; i++) {
			String col = cols[i];
			List<Map<String,Object>> fileList = (List<Map<String, Object>>) eyeRecord.get(col);
			for (Map<String,Object> fileMap:fileList) {
				String fileId = (String)fileMap.get("fileId");
				String fileName = (String)fileMap.get("fileName");
				map.put(fileName, fileId);
			}
		}
		return map;
	}
	
	
	private void parseSurveyXmlFile(File surveyFile, Map<String,Object> eyeRecord) throws Exception {
    	SAXReader saxReader=new SAXReader();
    	Document document=saxReader.read(surveyFile);
    	Element root=document.getRootElement();
    	String userId = (String)eyeRecord.get("userId");
    	String visitId = (String)eyeRecord.get("visitId");
    	for (Iterator iter = root.elementIterator();iter.hasNext();) {
    		Element cne = (Element)iter.next();
    		String tagName = cne.getName();
    		if (!tagName.startsWith("cn_")) {
    			continue;
    		}
    		String para = cne.attributeValue("para");
    		Map<String,Object> survey = Maps.newHashMap();
    		survey.put("code", tagName);
    		survey.put("name", para);
    		survey.put("userId", userId);
    		survey.put("visitId", visitId);
    		List<Map<String,Object>> itemList = Lists.newArrayList();
    		for (Iterator itemIter = cne.elementIterator(); itemIter.hasNext();) {
    			Element ie = (Element)itemIter.next();
    			String itemTagName = ie.getName();
    			String value = ie.attributeValue("value");
    			String itemName = ie.attributeValue("name");
    			Map<String,Object> item = Maps.newHashMap();
    			item.put("itemCode", itemTagName);
    			item.put("itemValue", value);
    			item.put("itemName", itemName);
    			itemList.add(item);
    		}
    		survey.put("itemList", itemList);
    		eyeRecordService.saveData("survey", survey);
    	}
	}
	
	private boolean parseHealthResultXmlFile(Element reportInfoElement, Map<String,Object> eyeRecord) throws Exception {
    	boolean hasResult = false;
    	Element eyeViewImageElement = reportInfoElement.element("EyeViewImage");
    	Map<String, Object> swMap = new HashMap<String,Object>();
    	Map<String,String> fileIdMap = getFileMap(eyeRecord);
    	if (eyeViewImageElement != null) {
        	Map<String,Object> eyeImageMap = Maps.newHashMap();
        	for (Iterator iter=eyeViewImageElement.elementIterator(); iter.hasNext();) {
        		Element e = (Element)iter.next();
        		String fileName = e.attributeValue("href");
        		String fileId = fileIdMap.get(fileName);
        		eyeImageMap.put(e.getName(), fileId);
        	}
			hasResult = true;
        	eyeRecord.put("eyeImage", eyeImageMap);
        	swMap.put("eyeImage", eyeViewImageElement.attributeValue("SW"));
    	}

    	Element featureResultElement = reportInfoElement.element("FeatureResult");
    	if (featureResultElement != null) {
    		List<Element> featureElementList = featureResultElement.elements("Feature");
    		List<Map<String,Object>> featureList = Lists.newArrayList();
    		for (Element featureElement:featureElementList) {
    			
    			Element idElement = featureElement.element("ID");
    			if (idElement == null || idElement.getTextTrim().length() == 0) {
    				continue;
    			}
    			Map<String,Object> featureMap = Maps.newHashMap();
    			featureMap.put("featureId", idElement.getTextTrim());
    			Element nameElement = featureElement.element("Name");
    			if (nameElement != null) {
    				featureMap.put("featureName", nameElement.getTextTrim());
    			}
    			Element noteElement = featureElement.element("Note");
    			if (noteElement != null) {
    				featureMap.put("featureNote", noteElement.getTextTrim());
    			}
    			Element countElement = featureElement.element("Count");
    			if (countElement != null) {
    				featureMap.put("featureCount", countElement.getTextTrim());
    			}
    			Element posElement = featureElement.element("Pos");
    			if (posElement != null) {
    				featureMap.put("featurePos", posElement.getTextTrim());
    			}
    			featureList.add(featureMap);
    		}
    		if (featureList.size() > 0) {
    			eyeRecord.put("featureList", featureList);
    			hasResult = true;
    			swMap.put("featureList", featureResultElement.attributeValue("SW"));
    		}
    	}
    	
    	
    	
    	Element analysisResultElement = reportInfoElement.element("EyeAnalysisResult");
    	if (analysisResultElement != null) {
    		Map<String,Object> analysisResultMap = Maps.newHashMap();
			Element corporeityElement = analysisResultElement.element("Corporeity");
			if (corporeityElement != null) {
				analysisResultMap.put("corporeity", corporeityElement.getTextTrim());
				swMap.put("corporeity", corporeityElement.attributeValue("SW"));
			}
			Element syndromeElement = analysisResultElement.element("Syndrome");
			if (syndromeElement != null) {
				analysisResultMap.put("syndrome", syndromeElement.getTextTrim());
				swMap.put("syndrome", syndromeElement.attributeValue("SW"));
			}
			Element diseaseElement = analysisResultElement.element("Disease");
			if (diseaseElement != null) {
				analysisResultMap.put("disease", diseaseElement.getTextTrim());
				swMap.put("disease", diseaseElement.attributeValue("SW"));
			}
			Element analysisElement = analysisResultElement.element("Analysis");
			if (analysisElement != null) {
				analysisResultMap.put("analysis", analysisElement.getTextTrim());
				swMap.put("analysis", analysisElement.attributeValue("SW"));
			}
			hasResult = true;
        	eyeRecord.put("analysisResult", analysisResultMap);
			swMap.put("analysisResult", analysisResultElement.attributeValue("SW"));
    	}
    	
    	Element healthControlResultElement = reportInfoElement.element("HealthControlResult");
    	if (healthControlResultElement != null) {
        	Map<String,Object> healthControlMap = Maps.newHashMap();
        	for (Iterator iter=healthControlResultElement.elementIterator(); iter.hasNext();) {
        		Element e = (Element)iter.next();
        		healthControlMap.put(e.getName(), e.getTextTrim());
    			swMap.put(e.getName(), e.attributeValue("SW"));
        	}
			hasResult = true;
        	eyeRecord.put("healthControl", healthControlMap);
			swMap.put("healthControl", healthControlResultElement.attributeValue("SW"));
    	}

    	eyeRecord.put("sw", swMap);
		return hasResult;

	}
	
	private boolean parseOriginalXml(Element reportInfoElement, Map<String,Object> eyeRecord) {
    	Element analysisElement = reportInfoElement.element("EyeAnalysisResult");
    	boolean hasResult = false;
    	if (analysisElement != null) {
        	List<Element> resultElementList = analysisElement.elements("Result");
        	Map<String,String> fileIdMap = getFileMap(eyeRecord);
        	List<Map<String,Object>> resultList = Lists.newArrayList();
        	for (Element resultElement:resultElementList) {
        		Map<String,Object> resultMap = Maps.newHashMap();
        		for (Iterator iter = resultElement.elementIterator(); iter.hasNext();) {
        			Element element = (Element)iter.next();
        			String elementName = element.getName();
        			if (elementName.equals("Path1")) {
        				Map<String,Object> fileMap = Maps.newHashMap();
        				String href=element.attributeValue("href");
        				String name=element.attributeValue("name");
        				String imageName = element.getTextTrim();
        				if (!imageName.equals("EmptyEye.jpg")) {
        					hasResult = true;
        				}
        				if (StringUtils.isNotEmpty(href)) {
            				String fileId = fileIdMap.get(href);
            				fileMap.put("fileId", fileId);
            				
        				}
        				fileMap.put("fileName", href);
        				fileMap.put("name", name);
        	    		resultMap.put("Path1", fileMap);
        			} else if (elementName.equals("Path2")) {
        				Map<String,Object> fileMap = Maps.newHashMap();
        				String href=element.attributeValue("href");
        				String name=element.attributeValue("name");
        				String imageName = element.getTextTrim();
        				if (!imageName.equals("EmptyEye.jpg")) {
        					hasResult = true;
        				}
        				if (StringUtils.isNotEmpty(href)) {
            				String fileId = fileIdMap.get(href);
            				fileMap.put("fileId", fileId);
        				}
        				fileMap.put("fileName", href);
        				fileMap.put("name", name);
        	    		resultMap.put("Path2", fileMap);
        			} else if (!elementName.equals("ID")) {
        				resultMap.put(elementName, element.getTextTrim());
        			}
        		}
        		if (!resultMap.isEmpty()) {
        			resultList.add(resultMap);
        		}
        	}
        	if (resultList.size() > 0) {
        		eyeRecord.put("resultList", resultList);
        	}
    		
    	}
    	List<String> compreList = Lists.newArrayList();
    	Element compreElement = reportInfoElement.element("ComprehensiveResult");
    	if (compreElement != null) {
        	List<Element> itemElementList = compreElement.elements("Item");
        	for (Element itemElement:itemElementList) {
        		Element contentElement = itemElement.element("Content");
        		Element contentDMElement = itemElement.element("ContentDM");
        		String text = null;
        		String textDM = null;
        		if (contentElement != null) {
        			text = contentElement.getTextTrim();
        		}
        		if (contentDMElement != null) {
        			textDM = contentDMElement.getTextTrim();
        		}
        		if (StringUtils.isNotEmpty(text) || StringUtils.isNotEmpty(textDM)) {
        			hasResult = true;
        		}
        		compreList.add(text);
        	}
    	}
    	if (compreList.size() > 0) {
    		eyeRecord.put("comprehensiveList", compreList);
    	}
    	return hasResult;
	}
	private boolean parseResultXmlFile(File resultFile, Map<String,Object> eyeRecord, Map<String,Object> messageObj) throws Exception{
    	SAXReader saxReader=new SAXReader();
    	Document document=saxReader.read(resultFile);
    	Element root=document.getRootElement();
    	Element reportInfoElement = root.element("ReportInfo");
    	if (reportInfoElement == null) {
    		return false;
    	}
    	Element checkInfoElement = reportInfoElement.element("CheckInfo");
    	if (checkInfoElement != null) {
        	Map<String,Object> checkInfoMap = Maps.newHashMap();
        	for (Iterator iter=checkInfoElement.elementIterator(); iter.hasNext();) {
        		Element e = (Element)iter.next();
        		checkInfoMap.put(e.getName(), e.getTextTrim());
        	}
        	eyeRecord.put("checkInfo", checkInfoMap);
    	}
    	Element healthControlResultElement = reportInfoElement.element("HealthControlResult");
    	boolean hasResult = false;
    	if (healthControlResultElement != null) {
    		hasResult = parseHealthResultXmlFile(reportInfoElement, eyeRecord);
    	} else {
    		hasResult = parseOriginalXml(reportInfoElement, eyeRecord);
    	}
		String recordId = eyeRecordService.saveData(eyeRecord);
		eyeRecord.put("id", recordId);
		messageObj.put("recordId", recordId);
		return hasResult;
	}
	
	private void cleanDir(String rootDir,String dateDir){
		File file=new File(rootDir,dateDir);
		if (!file.exists()) {
			return;
		}
		File sfile[]=file.listFiles();
		for(File delfile:sfile){
			if(delfile.isDirectory()){
				File images[]=delfile.listFiles();
				String filePath=delfile.getAbsolutePath();
				for (File img : images) {
					if(img.isDirectory()){
						File imgInner[]=img.listFiles();
						String fp=img.getAbsolutePath();
						for(File inner:imgInner){
							inner.delete();
						}
						File innerFilepath=new File(fp);
						innerFilepath.delete();
					}else{
						img.delete();
					}
				}
				java.io.File myFilePath = new java.io.File(filePath);
				//删除空文件夹
			    myFilePath.delete(); 
			}else{
				delfile.delete();
			}
		}
		
		File dirFile=new File(rootDir);
		File dirFileList[]=dirFile.listFiles();
		if(dirFileList.length>0){
			for (File file2 : dirFileList) {
				if(file2.getName().equals(dateDir)){
					file2.delete();
				}
				if(file2.getName().equals(dateDir+".zip")){
					file2.delete();
				}
			}
		}
	}
	/*
	 * 输出结果
	 */
	private void outPutResult(HttpServletResponse response) throws IOException{
		Element root=DocumentHelper.createElement("result");
		Document document=DocumentHelper.createDocument(root);
		root.addAttribute("code","0");
		Element msg=root.addElement("message");
		msg.setText("success");
		response.setCharacterEncoding("text/xml;charset=utf-8");
		response.setHeader("cache-control", "no-cache");
		XMLWriter writer=new XMLWriter(response.getOutputStream());
		writer.write(document);
		writer.flush();
		writer.close();
	}
	
    //下载的流
	private void downStream(File file, File dir,HttpServletRequest request,HttpServletResponse response){
  		try {
  			response.setContentType("application/octet-stream");
  			String name = file.getName();
  			String fileName = new String(name.getBytes("utf-8"), "ISO8859-1");
  			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
  			 OutputStream out=response.getOutputStream(); 
             FileInputStream ins=new FileInputStream(file);  
             byte bytes[]=new byte[1024]; 
             int len=0;  
             while((len=ins.read(bytes))!=-1){     
                 out.write(bytes,0,len);   
             }  
             out.flush();
             out.close();  
             ins.close();
  		} catch (IOException e) {
  			logger.error("down file error",e);
  		}finally{
  		}
  	}

	/**
	 * 根据加密后的身份证号获得PDF文件
	 * @param request
	 * @param response
	 */
	@RequestMapping("downPdf")
	@SpecialLog
	public void downPdf(HttpServletRequest request,HttpServletResponse response) {
		String uniqueId = request.getParameter("uniqueId");
		String visitTime = request.getParameter("visitTime");
		String userId = request.getParameter("userId");
		
		logger.info("downPdf uniqueId:"+uniqueId + ", visitTime:"+visitTime+"userId"+userId);
		if (visitTime == null) {
			visitTime = "";
		}
		if (userId == null) {
			userId = "";
		}
		String type = request.getParameter("type");
		String token = request.getParameter("token");
		long sysTime = ParamUtils.getLong(request.getParameter("sysTime"));
		String idno = EncryptUtil.decrypt(uniqueId);
		
		String md5 = MD5Util.MD5Encode(uniqueId+userId+visitTime+type+sysTime+Constant.SECRET_KEY);
		String fileId = null;
		logger.info("downPdf type:"+type + ", token:"+token+"userId");
		if (md5.equals(token)) {
			if (idno != null) {
				Map<String,Object> queryMap = Maps.newHashMap();
				queryMap.put("uniqueId", idno);
				if (visitTime.length() > 0) {
					queryMap.put("visitId", visitTime);
				}
				if (userId.length() > 0) {
					String decryptUserId = EncryptUtil.decrypt(userId);
					queryMap.put("userId", decryptUserId);
				}
				
				List<Map<String,Object>> recordList = eyeRecordService.queryListBySort(queryMap);
				if (recordList != null) {
					for (Map<String,Object> record:recordList) {
						if ("simple".equals(type)) {
							fileId = (String)record.get("pdfFile");
						} else {
							fileId = (String)record.get("parsePdfFile");
						}
						if (fileId != null) {
							break;
						}
					}
				}
			}
		}
		if (fileId != null) {
			try {
				response.setContentType("application/octet-stream");
				String name = userId+".pdf";
				String fileName = new String(name.getBytes("utf-8"), "ISO8859-1");
				response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				OutputStream sos = response.getOutputStream();
				fileService.downStream(sos, fileId);
				sos.flush();
				sos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

//			boolean oss = OSSUtil.oss();
//			if (oss) {
//				InputStream stream = ossFileService.loadOssFile(fileId);
//				logger.info("downPdf stream:"+stream);
//				if (stream != null) {
//					String name = fileId.substring(fileId.lastIndexOf("_") + 1);
//					OutputStream sos = null;
//					try {
//						sos = response.getOutputStream();
//						response.setContentType("application/octet-stream");
//						String fileName = new String(name.getBytes("utf-8"), "ISO8859-1");
//						response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//						int len = 0;
//						byte[] b = new byte[1024];
//						while( (len = stream.read(b)) != -1 ){  
//							sos.write(b,0,len);  
//				        }
//					} catch (IOException e) {
//						e.printStackTrace();
//						logger.error("down pdf error "+ e.getMessage());
//					} finally {
//						try {
//							if (sos != null) {
//								sos.flush();
//								sos.close();
//							}
//							stream.close();								
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			} else {
//				GridFSDBFile gridFSDBFile=null;
//				Map<String,Object> query=Maps.newHashMap();
//				query.put("_id", new ObjectId(fileId));
//				gridFSDBFile=filesManageService.getFileByFs(query,"fs");
//				if(gridFSDBFile!=null){
//					downStream(gridFSDBFile, request, response);
//				}
//			}
		}
	}

	//下载的流
//	public void downStream(GridFSDBFile gridFSDBFile,HttpServletRequest request,HttpServletResponse response){
//		try {
//			OutputStream sos = response.getOutputStream();
//			response.setContentType("application/octet-stream");
//			String name = (String) gridFSDBFile.get("filename");
//			String fileName = new String(name.getBytes("utf-8"), "ISO8859-1");
//			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//			gridFSDBFile.writeTo(sos);
//			sos.flush();
//			sos.close();
//		} catch (IOException e) {
//			logger.error("down file error",e);
//		}
//	}

}
