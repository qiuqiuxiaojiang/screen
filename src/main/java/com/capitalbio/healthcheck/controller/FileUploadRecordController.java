package com.capitalbio.healthcheck.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/fileUploadRecord")
public class FileUploadRecordController {
	@Autowired HealthCheckService healthCheckService;
	@Autowired FileManageService fileManageService;
	
	@RequestMapping("/list")
	public String list() {
		return "healthCheck/fileUploadRecord_list";
	}
	
	@RequestMapping(value = "fileList")
	@ResponseBody
	@ControllerLog
	public Message fileList(HttpServletRequest request) throws Exception {
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		Map<String,Object> queryMap = Maps.newHashMap();
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.findPageBySort(pageData, queryMap, sortMap, "fileUploadRecord");
		for (Map<String, Object> obj : list) {
			String fileId = obj.get("fileId").toString();
			String fileUrl = fileManageService.getFileUrl(fileId);
			obj.put("fileUrl", fileUrl);
			
			if (obj.get("errFileId") != "" && obj.get("errFileId") != null) {
				String errFileUrl = fileManageService.getFileUrl(obj.get("errFileId").toString());
				obj.put("errFileUrl", errFileUrl);
			}
		}
		
		long count = healthCheckService.count("fileUploadRecord", queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}

}
