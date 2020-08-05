package com.capitalbio.eye.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.EncryptUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.ZipUtils;
import com.capitalbio.eye.dao.BasicInfoDAO;
import com.capitalbio.eye.dao.EyeRecordDao;
import com.google.common.collect.Maps;

@Service
public class EyeRecordService extends BaseService {
	private Logger logger=LoggerFactory.getLogger(getClass());
//	@Autowired FilesManageDao filesManageDao;
	@Autowired EyeRecordDao recordDao;
	@Autowired BasicInfoDAO basicInfoDAO;
	@Autowired FileManageService fileService;
	
//	@Autowired OSSFileService ossFileService;

	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return recordDao;
	}

	@Override
	public String getCollName() {
		return "eyeRecord";
	}
	
	/**
	 * 保存数据
	 * @param colName
	 * @param map
	 * @return
	 */
	public String saveDataByModule(String colName,Map<String,Object> map){
		return getMongoBaseDAO().saveData(colName, map);
	}
	public Map<String,Object> getUserInfo(String username){
		Map<String,Object> query=Maps.newHashMap();
		query.put("username", username);
		return recordDao.getDataByQuery("basicInfo", query);
	}
	
	/**
	 * 查询某一时间段内上传过数据的用户ID
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> queryUserByVisitTime(String start, String end) {
		Date startTime = null;
		if (StringUtils.isNotEmpty(start)) {
			start = start + " 00:00:00";
			startTime = ParamUtils.getDateTime(start);
		}
		Date endTime = null;
		if (StringUtils.isNotEmpty(end)) {
			end = end + " 23:59:59";
			endTime = ParamUtils.getDateTime(end);
		}
		
		return recordDao.queryUserByVisitTime(startTime, endTime);
	}

	/**
	 * 查询某一时间段内所有报告不为空的用户ID
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> queryReportUserByVisitTime(String start, String end) {
		Date startTime = null;
		if (StringUtils.isNotEmpty(start)) {
			start = start + " 00:00:00";
			startTime = ParamUtils.getDateTime(start);
		}
		Date endTime = null;
		if (StringUtils.isNotEmpty(end)) {
			end = end + " 23:59:59";
			endTime = ParamUtils.getDateTime(end);
		}
		
		return recordDao.queryReportUserByVisitTime(startTime, endTime);
	}

	/**
	 * 根据其他信息查询用户
	 * @param queryMap
	 * @return
	 */
	public List<String> queryUserByInfo(Map<String,Object> queryMap) {
		return recordDao.queryUserByInfo(queryMap);
	}
	
	/**
	 * 查询某一个用户一段时间内的目诊记录
	 * @param userId
	 * @param start
	 * @param end
	 * @return
	 */
	public List<Map<String,Object>> queryRecord(String userId, String start, String end) {
		Date startTime = null;
		if (StringUtils.isNotEmpty(start)) {
			start = start + " 00:00:00";
			startTime = ParamUtils.getDateTime(start);
		}
		Date endTime = null;
		if (StringUtils.isNotEmpty(end)) {
			end = end + " 23:59:59";
			endTime = ParamUtils.getDateTime(end);
		}
		
		return recordDao.queryRecordByVisitTime(userId,startTime, endTime);
		
	}
	
	public Map<String,Object> getLatestRecordByUniqueId(String uniqueId) {
		return recordDao.getLatestRecordByUniqueId(uniqueId);
	}

	public Map<String,Object> getLatestRecord(String userId) {
		return recordDao.getLatestRecord(userId);
	}

	public Map<String,Object> getRecordByCardId(String cardId) {
		return recordDao.getRecordByCardId(cardId);
	}
	public List<Map<String,Object>> getRecordListByCardId(String cardId) {
		return recordDao.getRecordListByCardId(cardId);
	}

	/**
	 * 下载目诊记录
	 * @param userId
	 * @param basicMap
	 * @param request
	 * @param response
	 * @param start
	 * @param end
	 * @throws Exception
	 */
	public void downFileByUser(String userId,Map<String,Object> basicMap, HttpServletRequest request,HttpServletResponse response,String start, String end) throws Exception{
		//获取用户信息
		
		//临时文件夹
		String zipTempDir=PropertyUtils.getProperty("system.temp.dir");
		//根据用户创建根目录
		String fileName=UUID.randomUUID().toString();
		File dirByUser=new File(zipTempDir,fileName);
		if(!dirByUser.exists()){
			dirByUser.mkdirs();
		}
		
		List<Map<String,Object>> recordList = queryRecord(userId,start, end);
		
		if (recordList.size() == 0) {
			createBasicInfoXmlFile(dirByUser, basicMap);
		} else {
			for (Map<String,Object> eyeRecord:recordList) {
				String visitId = (String)eyeRecord.get("visitId");
				String zipFileName = userId+"_"+visitId+".zip";
				
				File file = new File(dirByUser, zipFileName);
				String fileId = (String)eyeRecord.get("zipFile");
				fileService.downFile(file, fileId);
			}
		}
		
		//生成压缩文件
		File[] files = dirByUser.listFiles();
		if (files.length == 1 && files[0].getName().endsWith(".zip")) {
	        downStream(files[0],dirByUser,request,response);
		} else {
			ZipUtils.zipDirectory(zipTempDir, dirByUser.getAbsolutePath());
	        //生成并下载zip文件
	        File downFile=new File(zipTempDir,fileName+".zip");
	        downStream(downFile,dirByUser,request,response);
		}
	}
	
	@SuppressWarnings({ "rawtypes"})
	private void createBasicInfoXmlFile(File dir,Map<String,Object> basicMap) throws Exception{
		Document document=DocumentHelper.createDocument();
		Element root=document.addElement("info");
		Element basicElement = root.addElement("basicinfo");
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
		try {
            OutputFormat format = OutputFormat.createPrettyPrint();     
            FileOutputStream fos = new FileOutputStream(new File(dir, "/info.xml"));     
            XMLWriter writer = new XMLWriter(fos, format);     
            writer.write(document);     
            writer.close();     
        } catch (IOException e) {     
			logger.debug("create basicinfo.xml error", e);
            e.printStackTrace();     
        }   
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
  		} catch (Exception e) {
  			e.printStackTrace();
  			logger.error("down file error",e);
  		}finally{
  			cleanDir(file, dir);
  		}
  	}
	
	private void cleanDir(File file,File dir){
		file.delete();
		dir.delete();
	}
	
	public Map<String, Object> getEyeRecordData(String startTime, String endTime) throws Exception {
		Date start = DateUtil.stringToDate(startTime);
		Date end = DateUtil.stringToDate(endTime);
		return recordDao.getEyeRecordData(start, end);
	}
	
	public double getErDataOfMonth(String startTime, String endTime) throws Exception {
		return recordDao.getErDataOfMonth(DateUtil.stringToDate(startTime), DateUtil.stringToDate(endTime));
	}
	

	public double getEyeRecordCount(String startTime, String endTime) throws Exception {
		return recordDao.getErCount(DateUtil.stringToDate(startTime), DateUtil.stringToDate(endTime));
	}
	
	/**
	 * 根据查询条件获得数据
	 * @param queryMap
	 * @return
	 */
	public  List<Map<String,Object>> queryListBySort(Map<String,Object> queryMap) {
		return recordDao.queryListBySort(queryMap);
//		return getMongoBaseDAO().queryList(getCollName(), queryMap);
	}
	
	public boolean isEyeRecordTest(String uniqueId, String customerId) {
		Map<String, Object> queryMap = Maps.newHashMap();
		//昆明不以日期判断是否有目诊记录
//		queryMap.put("visitId", DateUtil.getNowDate(new Date()));
		queryMap.put("uniqueId", uniqueId);
		List<Map<String, Object>> list = recordDao.queryList(getCollName(), queryMap);
		for (Map<String, Object> obj : list) {
			if (obj.get("featureList") != null) {
				List<Map<String, Object>> featureList = (List<Map<String, Object>>) obj.get("featureList");
				if (featureList != null && featureList.size() > 0) {
					return true;
				}
			}
		}
		boolean findResult = false;
		if (StringUtils.isNotEmpty(customerId)) {
			String encIdNo = EncryptUtil.encrypt(customerId);
			queryMap.clear();
			queryMap.put("idno", encIdNo);
			list = recordDao.queryList(getCollName(), queryMap);
			for (Map<String, Object> obj : list) {
				//设置标识ID
				obj.put("uniqueId", uniqueId);
				obj.remove("idno");
				obj.remove("name");
				obj.remove("mobile");
				saveData(getCollName(), obj);
				if (obj.get("featureList") != null) {
					List<Map<String, Object>> featureList = (List<Map<String, Object>>) obj.get("featureList");
					if (featureList != null && featureList.size() > 0) {
						findResult = true;
					}
				}
			}
		}
		
		
		return findResult;
	}

	/************************* CHMS系统访问接口   ****************************/
	public JSONObject getRecordList(String uniqueId,int pageNo,int pageSize) throws Exception {
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		
		
		Page pageData = new Page();
		Integer page = pageNo;
		Integer pageCount = pageSize;
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("visitId", -1);
		
		List<Map<String, Object>> list = recordDao.queryPage("eyeRecord", pageData, query, sortMap);
		
		JSONObject json = new JSONObject();
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> m = null;
		if(list!=null&&list.size()>0){
			/*recordId	记录id
			time	眼项采集时间，格式为精确到日的时间戳
			syndrome	中医症候
			disease	易发疾病
			corporeity	体质*/
			for(Map<String, Object> mm : list){
				m = Maps.newHashMap();
				if(mm.get("visitId")!=null&&StringUtils.isBlank(mm.get("visitId").toString())){
					continue;
				}
				Map<String, String> eyeImages = (Map<String, String>) mm.get("eyeImage");
				if(eyeImages!=null&&eyeImages.size()>0){
					if(eyeImages.get("LLeft")==null&&eyeImages.get("RDown")==null&&eyeImages.get("LDown")==null&&
							eyeImages.get("LN")==null&&eyeImages.get("LRight")==null&&eyeImages.get("LUp")==null&&
							eyeImages.get("RRight")==null&&eyeImages.get("RN")==null&&eyeImages.get("RUp")==null&&
							eyeImages.get("RLeft")==null){
						continue;
					}
					if(mm.containsKey("analysisResult")&&mm.get("analysisResult")!=null){
						Map<String, Object> analysisResult = (Map<String, Object>) mm.get("analysisResult");
						m.put("syndrome", analysisResult.containsKey("syndrome")&&analysisResult.get("syndrome")!=null?
								analysisResult.get("syndrome"):"");
						m.put("disease", analysisResult.containsKey("disease")&&analysisResult.get("disease")!=null?
								delCharacter(analysisResult.get("disease").toString()):"");
					}else{
						m.put("syndrome", "");
						m.put("disease", "");
					}
					m.put("recordId", mm.get("_id").toString());
					m.put("time", DateUtil.stringToDate(mm.get("visitId").toString()).getTime());
					result.add(m);
				}
			}
		}
		json.put("r", result);
		return json;
	}

	
	public JSONObject getRecordDetail(String recordId) throws BaseException {
		Map<String, Object> query = Maps.newHashMap();
		query.put("_id", new ObjectId(recordId));
		Map<String, Object> m = recordDao.getDataByQuery(getCollName(), query);
		
		JSONObject json = new JSONObject();
		if(m!=null){
			/*featureList	featureName	特征名称
							featurePos	特征位置
							featureNote	健康提示
			*/
			
			List<Map<String, Object>> featureList = new ArrayList<Map<String,Object>>();
			if(m.containsKey("featureList")&&m.get("featureList")!=null){
				Map<String, Object> featureMap = null;
				List<Map<String, Object>> features = (List<Map<String, Object>>) m.get("featureList");
				if(features!=null&&features.size()>0){
					for(Map<String, Object> mm : features){
						featureMap = Maps.newHashMap();
						featureMap.put("featureName", mm.get("featureName"));
						featureMap.put("featurePos", mm.get("featurePos"));
						featureMap.put("featureNote", mm.get("featureNote"));
						featureList.add(featureMap);
					}
				}
			}
			json.put("featureList", featureList);
			
			Map<String, String>	healthControl = (Map<String, String>) m.get("healthControl");	
			List<Map<String, String>> healthControlList = new ArrayList<Map<String,String>>();
			Map<String, String> diet = Maps.newHashMap();
			Map<String, String> dailylife = Maps.newHashMap();
			Map<String, String> sport = Maps.newHashMap();
			Map<String, String> emotion = Maps.newHashMap();
			Map<String, String> channelandpoint = Maps.newHashMap();
			Map<String, String> medicatedbath = Maps.newHashMap();
			Map<String, String> drug = Maps.newHashMap();
			Map<String, String> physicalexamination = Maps.newHashMap();
			
			diet.put("name", "饮食调理方案");
			dailylife.put("name", "起居调理");
			sport.put("name", "运动调理");
			emotion.put("name", "情志调理");
			channelandpoint.put("name", "经穴调理");
			medicatedbath.put("name", "药浴调理");
			drug.put("name", "中药调理");
			physicalexamination.put("name", "体检方案");
			if(healthControl!=null&&healthControl.size()>0){
				logger.info("healthControl=="+healthControl.toString());
				//饮食调理方案、起居调理、运动调理、情志调理、经穴调理、药浴调理、中药调理、体检方案
				if(healthControl.containsKey("diet")&&healthControl.get("diet")!=null){
					diet.put("content", delCharacter(healthControl.get("diet")));
				}else{
					diet.put("content", "");
				}
				
				if(healthControl.containsKey("dailylife")&&healthControl.get("dailylife")!=null){
					dailylife.put("content", delCharacter(healthControl.get("dailylife")));
				}else{
					dailylife.put("content", "");
				}
				
				if(healthControl.containsKey("sport")&&healthControl.get("sport")!=null){
					sport.put("content", delCharacter(healthControl.get("sport")));
				}else{
					sport.put("content", "");
				}
				
				if(healthControl.containsKey("emotion")&&healthControl.get("emotion")!=null){
					emotion.put("content", delCharacter(healthControl.get("emotion")));
				}else{
					emotion.put("content", "");
				}
				
				if(healthControl.containsKey("channelandpoint")&&healthControl.get("channelandpoint")!=null){
					channelandpoint.put("content", delCharacter(healthControl.get("channelandpoint")));
				}else{
					channelandpoint.put("content", "");
				}
				
				if(healthControl.containsKey("medicatedbath")&&healthControl.get("medicatedbath")!=null){
					medicatedbath.put("content", delCharacter(healthControl.get("medicatedbath")));
				}else{
					medicatedbath.put("content", "");
				}
				
				if(healthControl.containsKey("drug")&&healthControl.get("drug")!=null){
					drug.put("content", delCharacter(healthControl.get("drug")));
				}else{
					drug.put("content", "");
				}
				
				if(healthControl.containsKey("physicalexamination")&&healthControl.get("physicalexamination")!=null){
					physicalexamination.put("content", delCharacter(healthControl.get("physicalexamination")));
				}else{
					physicalexamination.put("content", "");
				}
			}else{
				diet.put("content", "");
				dailylife.put("content", "");
				sport.put("content", "");
				emotion.put("content", "");
				channelandpoint.put("content", "");
				medicatedbath.put("content", "");
				drug.put("content", "");
				physicalexamination.put("content", "");
			}
			/*healthControl	name	调理名称
							content	调理内容*/
			healthControlList.add(diet);
			healthControlList.add(dailylife);
			healthControlList.add(sport);
			healthControlList.add(emotion);
			healthControlList.add(channelandpoint);
			healthControlList.add(medicatedbath);
			healthControlList.add(drug);
			healthControlList.add(physicalexamination);
			json.put("healthControl", healthControlList);
			/*rightEyeImages	name	眼项图片名称
							url	云存储完整地址
			leftEyeImages	name	眼项图片名称
						url	云存储完整地址
			*/
			Map<String, String> eyeImage = (Map<String, String>) m.get("eyeImage");
			List<Map<String, String>> rightEyeImagesList = new ArrayList<Map<String,String>>();
			List<Map<String, String>> leftEyeImagesList = new ArrayList<Map<String,String>>();
			try {
				if (eyeImage != null && eyeImage.size() > 0) {
					Map<String, String> eyeMap = null;
					//右眼正视,右眼左视,右眼右视,右眼上视,右眼下视
					if (eyeImage.containsKey("RN")&& eyeImage.get("RN") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "右眼正视");
						eyeMap.put("url", fileService.getFileUrl(eyeImage.get("RN").toString()));
						rightEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("RLeft")&& eyeImage.get("RLeft") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "右眼左视");
						eyeMap.put("url", fileService.getFileUrl(eyeImage.get("RLeft").toString()));
						rightEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("RRight")&& eyeImage.get("RRight") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "右眼右视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("RRight").toString()));
						rightEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("RUp")&& eyeImage.get("RUp") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "右眼上视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("RUp").toString()));
						rightEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("RDown")&& eyeImage.get("RDown") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "右眼下视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("RDown").toString()));
						rightEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("LN")&& eyeImage.get("LN") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "左眼正视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("LN").toString()));
						leftEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("LLeft")&& eyeImage.get("LLeft") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "左眼左视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("LLeft").toString()));
						leftEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("LRight")&& eyeImage.get("LRight") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "左眼右视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("LRight").toString()));
						leftEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("LUp")&& eyeImage.get("LUp") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "左眼上视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("LUp").toString()));
						leftEyeImagesList.add(eyeMap);
					}
					if (eyeImage.containsKey("LDown")&& eyeImage.get("LDown") != null) {
						eyeMap = Maps.newHashMap();
						eyeMap.put("name", "左眼下视");
						eyeMap.put("url",fileService.getFileUrl(eyeImage.get("LDown").toString()));
						leftEyeImagesList.add(eyeMap);
					}
				}
			} catch (Exception e) {
				logger.error("获取目诊眼项图片异常",e);
			}
			json.put("rightEyeImages", rightEyeImagesList);
			json.put("leftEyeImages", leftEyeImagesList);
			/*analysisResult	name	分析模块名称
								content	分析详细内容*/
			Map<String, String> analysisResult = (Map<String, String>) m.get("analysisResult");
			List<Map<String, String>> analysisResultList = new ArrayList<Map<String,String>>();
			if(analysisResult!=null&&analysisResult.size()>0){
				//中医症候、易发疾病、中医分析
				Map<String, String> analyMap = null;
				if(analysisResult.containsKey("syndrome")&&analysisResult.get("syndrome")!=null){
					analyMap = Maps.newHashMap();
					analyMap.put("name", "中医症候");
					analyMap.put("content", delCharacter(analysisResult.get("syndrome")));
					analysisResultList.add(analyMap);
				}
				if(analysisResult.containsKey("disease")&&analysisResult.get("disease")!=null){
					analyMap = Maps.newHashMap();
					analyMap.put("name", "易发疾病");
					analyMap.put("content", delCharacter(analysisResult.get("disease")));
					analysisResultList.add(analyMap);
				}
				if(analysisResult.containsKey("analysis")&&analysisResult.get("analysis")!=null){
					analyMap = Maps.newHashMap();
					analyMap.put("name", "中医分析");
					analyMap.put("content", delCharacter(analysisResult.get("analysis")));
					analysisResultList.add(analyMap);
				}
			}
			json.put("analysisResult", analysisResultList);
		}
		
		return json;
	}
	
	
	public String delCharacter(String content){
		content = content.replace("&#160;;", "&#160;").
				replace("&#160;,", "&#160;").
				replace("<br/>&#160;&#160;&#160;&#160;", "\n").
				replace("\n\n", "\n");
		content = content.replaceAll("(\n|\r\n)\\s+", content);
		return content;
	}
	
	public void updateUniqueId(String uniqueId, String idno) {
		try {
			Map<String,Object> queryMap = new HashMap<>();
			String encIdno = EncryptUtil.encrypt(idno);
			queryMap.put("idno", encIdno);
			List<Map<String,Object>> infoList = basicInfoDAO.queryList("basicInfo", queryMap);
			for (Map<String, Object> basicInfo:infoList) {
				basicInfo.remove("idno");
				basicInfo.remove("name");
				basicInfo.remove("mobile");
				basicInfoDAO.saveData("basicInfo", basicInfo);
			}
			
			List<Map<String,Object>> recordList = recordDao.queryList("eyeRecord", queryMap);
			for (Map<String, Object> eyeRecord: recordList) {
				eyeRecord.remove("idno");
				eyeRecord.remove("name");
				eyeRecord.remove("mobile");
				recordDao.saveData("eyeRecord", eyeRecord);
			}
		} catch (Exception e) {
			logger.debug("推送uniqueId错误", e);
		}
		
	}
}

