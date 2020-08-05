package com.capitalbio.eye.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.service.FilesManageService;
import com.capitalbio.common.util.OSSUtil;
import com.capitalbio.eye.service.BasicInfoService;
import com.capitalbio.eye.service.EyeRecordService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/resteye")
@SuppressWarnings("unchecked")
public class RestQueryEyeController {
//	private Logger logger=LoggerFactory.getLogger(getClass());
//	@Autowired SubmitClientService submitClientService;
	@Autowired BasicInfoService basicInfoService;
	@Autowired EyeRecordService eyeRecordService;
	@Autowired FilesManageService filesManageService;
//	@Autowired OSSFileService ossFileService;

	/**
	 * 根据贵宾卡ID获取目诊记录列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="getRecordListByCard")
	@ResponseBody
	public Message getRecordListByCard(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String[] leftEyeTitles = new String[]{"LN", "LLeft", "LRight", "LUp", "LDown"};
		String[] leftEyeNames = new String[]{"左眼正视", "左眼左视", "左眼右视", "左眼上视", "左眼下视"};
		String[] rightEyeTitles = new String[]{"RN", "RLeft", "RRight", "RUp", "RDown"};
		String[] rightEyeNames = new String[]{"右眼正视", "右眼左视", "右眼右视", "右眼上视", "右眼下视"};
		String[] types = new String[]{"Vessle","Dot", "Plaque", "WuMan", "YueYun"};
		String cardId = request.getParameter("cardId");
		String token = request.getParameter("token");
		String subjectId = JwtUtil.checkRequest(token);
		if (subjectId== null) {
			return Message.error(Message.MSG_AUTH_TOKENERROR,"Token错误");
		}

		List<Map<String,Object>> list = eyeRecordService.getRecordListByCardId(cardId);
		if (list == null || list.size() == 0) {
			return Message.error(Message.MSG_DATA_NOTFOUND,"此用户没有记录");
		}
		List<Map<String,Object>> returnList = Lists.newArrayList();
		for (Map<String,Object> record:list) {
			Map<String,Object> result = Maps.newHashMap();
			result.put("cardId", cardId);
			result.put("userId", record.get("userId"));
			result.put("visitId", record.get("visitId"));
			//TODO
			boolean oss = OSSUtil.oss();
			if (oss) {
				String pdfFileId = (String)record.get("parsePdfFile");
				if (StringUtils.isEmpty(pdfFileId)) {
					pdfFileId = (String)record.get("pdfFile");
				}
				result.put("pdfFile", pdfFileId);
			} else {
				String pdfFileId = (String)record.get("parsePdfFile");
				if (StringUtils.isEmpty(pdfFileId)) {
					pdfFileId = (String)record.get("pdfFile");
				}
				result.put("pdfFile", pdfFileId);
			}
			
			Map<String,Object> eyeImageMap = (Map<String, Object>) record.get("eyeImage");
			Map<String,Object> parsedMap = (Map<String, Object>) record.get("parsedMap");
			
			List<Map<String,Object>> leftEyeImages = Lists.newArrayList();
			List<Map<String,Object>> rightEyeImages = Lists.newArrayList();
			StringBuffer sb = new StringBuffer();
			if (eyeImageMap != null) {
				for (int i = 0; i < leftEyeTitles.length; i++) {
					sb.setLength(0);
					String fileId = (String)eyeImageMap.get(leftEyeTitles[i]);
					if (StringUtils.isNotEmpty(fileId)) {
						sb.append(fileId).append(",");
						if (parsedMap != null) {
							for (int j = 0; j < types.length; j++) {
								String parseFileId = (String)parsedMap.get(leftEyeTitles[i]+"_"+types[j]);
								if (StringUtils.isNotEmpty(parseFileId)) {
									sb.append(parseFileId).append(",");
								}
							}
						}
					}
					if (sb.length() > 0) {
						sb.deleteCharAt(sb.length() -1);
					}
					Map<String,Object> fileMap = Maps.newHashMap();
					fileMap.put("name", leftEyeNames[i]);
					fileMap.put("fileId", sb.toString());
					leftEyeImages.add(fileMap);
				}
				for (int i = 0; i < rightEyeTitles.length; i++) {
					sb.setLength(0);
					String fileId = (String)eyeImageMap.get(rightEyeTitles[i]);
					if (StringUtils.isNotEmpty(fileId)) {
						sb.append(fileId).append(",");
						if (parsedMap != null) {
							for (int j = 0; j < types.length; j++) {
								String parseFileId = (String)parsedMap.get(rightEyeTitles[i]+"_"+types[j]);
								if (StringUtils.isNotEmpty(parseFileId)) {
									sb.append(parseFileId).append(",");
								}
							}
						}
					}
					if (sb.length() > 0) {
						sb.deleteCharAt(sb.length() -1);
					}
					Map<String,Object> fileMap = Maps.newHashMap();
					fileMap.put("name", rightEyeNames[i]);
					fileMap.put("fileId", sb.toString());
					rightEyeImages.add(fileMap);
				}
				result.put("leftEyeImages", leftEyeImages);
				result.put("rightEyeImages", rightEyeImages);
			}
			result.put("featureList", record.get("featureList"));
			result.put("analysisResult", record.get("analysisResult"));
			result.put("healthControl", record.get("healthControl"));
			returnList.add(result);
		}
		
		return Message.dataList(returnList);
	}
	
	


//	@RequestMapping("image/{id}")
//	public void downImageFileById(@PathVariable String id,HttpServletRequest request,HttpServletResponse response) throws Exception{
//		String token = request.getParameter("token");
//		String subjectId = JwtUtil.checkRequest(token);
//		if (subjectId== null) {
//			return;
//		}
//		//TODO
//		boolean oss = OSSUtil.oss();
//		GridFSDBFile map = null;
//		InputStream inputStream = null;
//		if (oss) {
//			inputStream = ossFileService.loadOssFile(id);
//			if (inputStream == null) {
//				return;
//			}
//		} else {
//			map = filesManageService.getFileById(id);
//			if (map == null) {
//				return;
//			}
//			String type=(String)map.get("contentType");
//			response.setContentType(type);
//		}
//		
//		//禁止图像缓存。
//		response.setHeader("Pragma", "no-cache");
//		response.setHeader("Cache-Control", "no-cache");
//		response.setDateHeader("Expires", 0);
//		OutputStream sos = response.getOutputStream();
//		
//		if (oss) {
//			int len = 0;
//			byte[] b = new byte[1024];
//			while( (len = inputStream.read(b)) != -1 ){  
//				sos.write(b,0,len);  
//	        }  
//			
//			inputStream.close();
//		} else {
//			map.writeTo(sos);
//		}
//		
//		sos.flush();
//		sos.close();
//	}
	
	/**
	 * 根据文件Id获得文件
	 */
//	@RequestMapping("file/{id}")
//	@SpecialLog
//	public void downFile(@PathVariable String id,HttpServletRequest request,HttpServletResponse response) {
//		
//		try {
//			String token = request.getParameter("token");
//			String subjectId = JwtUtil.checkRequest(token);
//			if (subjectId== null) {
//				return;
//			}
//			//TODO
//			boolean oss = OSSUtil.oss();
//			GridFSDBFile map = null;
//			InputStream inputStream = null;
//			String name = "";
//			if (oss) {
//				inputStream = ossFileService.loadOssFile(id);
//				if (inputStream == null) {
//					return;
//				}
//				if (id.indexOf(".zip") >= 0) {
//					name = id;
//				} else {
//					name = id.substring(id.indexOf("_") + 1);
//				}
//			} else {
//				map = filesManageService.getFileById(id);
//				if (map == null) {
//					return;
//				}
//				
//				name = (String) map.get("filename");
//			}
//			
//			OutputStream sos = response.getOutputStream();
//			response.setContentType("application/octet-stream");
//			String fileName = new String(name.getBytes("utf-8"), "ISO8859-1");
//			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//			if (oss) {
//				int len = 0;
//				byte[] b = new byte[1024];
//				while( (len = inputStream.read(b)) != -1 ){  
//					sos.write(b,0,len);  
//		        }  
//				inputStream.close();
//			} else {
//				map.writeTo(sos);
//			}
//			
//			sos.flush();
//			sos.close();
//		} catch (IOException e) {
//			logger.error("down file error",e);
//		}
//	}

}
