package com.capitalbio.healthcheck.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.healthcheck.service.ReportService;

@Controller
@RequestMapping("/report")
public class ReportController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired ReportService reportService;

	
	/**
	 * 重新生成报表数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("regenerate")
	@ResponseBody
	public Message regenerate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("regenerate report data");
		reportService.regenerate();
		return Message.success();
	}

}
