package com.capitalbio.healthcheck.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.capitalbio.common.model.Message;
import com.capitalbio.healthcheck.service.HealthCheckImportService;


@Controller
@RequestMapping("/importData")
public class HealthCheckImportController {
	@Autowired HealthCheckImportService healthCheckImportService;

	@RequestMapping(value="healthCheckDetail")
	@ResponseBody
	public Message importHealthCheckDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!(request instanceof MultipartHttpServletRequest)) {
			return Message.error("请求错误");
		}
		MultipartHttpServletRequest mreq = (MultipartHttpServletRequest) request;
		MultipartFile uploadFile = mreq.getFile("file");
		if (uploadFile != null) {
			Workbook wb = healthCheckImportService.readFile(uploadFile.getInputStream());
		    if (wb == null) {
		    	return Message.error("上传文件读取格式错误，请按照模板上传Excel文件");
		    }
		    List<Map<String,Object>> msgList = healthCheckImportService.importData(wb);
		    if (msgList != null && msgList.size() > 0) {
		    	Map<String,Object> dataMap = new HashMap<String,Object>();
		    	dataMap.put("msgList", msgList);
		    	
		    	return Message.dataMap(dataMap);
		    }
		    return Message.success();
		}
	   return Message.error("请上传文件");
	   
	}

}
