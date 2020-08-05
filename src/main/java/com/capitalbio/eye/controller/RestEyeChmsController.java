package com.capitalbio.eye.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.common.exception.BaseException;
import com.capitalbio.common.util.message.MessageUtil;
import com.capitalbio.common.util.message.Messages;
import com.capitalbio.eye.service.EyeRecordService;

@Controller
@RequestMapping("rest")
@Component
public class RestEyeChmsController {

	@Autowired
	private EyeRecordService eyeRecordService;
	
	/**
	 * 获取目诊报告列表
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(
	value="/eye/getRecordList",
	method=RequestMethod.GET,
	produces="application/json;charset=utf-8")
	@ResponseBody
	public Messages getQuestionnaireResult(HttpServletRequest request) throws Exception{
		/*	mobile	当前登录用户的手机号
			pageNo	第几页
			pageSize	每页大小*/
		String uniqueId = request.getParameter("uniqueId");
		String pageNo = request.getParameter("pageNo");
		String pageSize = request.getParameter("pageSize");
		System.out.println("===================uniqueId==================" + uniqueId);
		if(uniqueId==null || StringUtils.isBlank(uniqueId)
				||pageNo==null || StringUtils.isBlank(pageNo)
				||pageSize==null || StringUtils.isBlank(pageSize)){
			return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		}
		try {
			JSONObject json = eyeRecordService.getRecordList(uniqueId, Integer.parseInt(pageNo), Integer.parseInt(pageSize));
			System.out.println("===================json==================" + json);
			return MessageUtil.getSuccessMessage("", json);
		} catch (NumberFormatException | BaseException e) {
			System.out.println("获取目诊报告列表");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
	
	
	/**
	 * 获取指定报告id的报告详情
	 * @param request
	 * @return
	 */
	@RequestMapping(
	value="/eye/getRecordDetail",
	method=RequestMethod.GET,
	produces="application/json;charset=utf-8")
	@ResponseBody
	public Messages getRecordDetail(HttpServletRequest request){
		/*	recordId	报告记录id*/
		String recordId = request.getParameter("recordId");
		if(recordId==null || StringUtils.isBlank(recordId)){
			return MessageUtil.getErrorDebugInfoMessage("Parameter_Err");
		}
		try {
			JSONObject json = eyeRecordService.getRecordDetail(recordId);
			if(json!=null&&json.size()>0){
				return MessageUtil.getSuccessMessage("", json);
			}else{
				return MessageUtil.getErrorMessage("Get_Failure_Msg");
			}
		} catch (NumberFormatException | BaseException e) {
			System.out.println("获取指定报告id的报告详情");
			return MessageUtil.getErrorMessage("Get_Failure_Msg");
		}
	}
}
