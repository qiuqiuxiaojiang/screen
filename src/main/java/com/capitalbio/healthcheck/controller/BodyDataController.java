package com.capitalbio.healthcheck.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.log.SpecialLog;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.healthcheck.service.BodyDataService;

@Controller
@RequestMapping("/bodyData")
public class BodyDataController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired BodyDataService bodyDataService;
	
	@RequestMapping(value="uploadData")
	@ResponseBody
	@SpecialLog
	public void uploadData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		// 获取request传输过来的字符流
		BufferedReader bf = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		// 循环读取字符流所有字符
		while ((line = bf.readLine()) != null) {
			sb.append(line);
		}
		
		String jsonString = sb.toString();
		if (StringUtils.isEmpty(jsonString)) {
			logger.info("没有接收到数据"+ jsonString);
			outPutResult(response, "data is null");
			return;
		}
		logger.info("接收数据："+ jsonString);
		
//		String uniqueId = 
		String message = bodyDataService.analysisData(jsonString);
		outPutResult(response, message);
//		if (uniqueId == null) {
//			outPutResult(response, "此身份证号未在筛查端建档");
//		} else {
//			outPutResult(response, "");
//		}
	}
	
	/*
	 * 输出结果
	 */
	private void outPutResult(HttpServletResponse response, String error) throws IOException{
		PrintWriter out = null;
		try {
			Map<String, Object> mapJson = new HashMap<String, Object>();
			if (StringUtils.isEmpty(error)) {
				mapJson.put("success", "true");
				mapJson.put("message", "上传成功");
			} else {
				mapJson.put("success", "false");
				mapJson.put("message", error);
			}
			
			String msg = JsonUtil.mapToJson(mapJson);
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=utf-8");
			response.setHeader("cache-control", "no-cache");
			out = response.getWriter();
			out.print(msg);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
