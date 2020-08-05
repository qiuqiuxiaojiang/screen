package com.capitalbio.healthcheck.controller;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.healthcheck.service.BodyCompositionService;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/body")
public class BodyCompositonController {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired BodyCompositionService bodyCompositionService;
	
	@RequestMapping(value = "list")
	public String list() {
		return "bodyComposition/body_comp_list";
	}
	
	@RequestMapping(value = "dataList")
	@ResponseBody
	public Message dataList(HttpServletRequest request) throws Exception {
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		//Map<String,Object> queryMap = parseConditionRequest(request);
		Map<String,Object> queryMap = Maps.newHashMap();
		queryMap = parseQueryRequest(request);
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = bodyCompositionService.findPageBySort(pageData, queryMap, sortMap);
		long count = bodyCompositionService.count(queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}

	private Map<String,Object> parseQueryRequest(HttpServletRequest request) throws Exception{
		Map<String,Object> param=Maps.newHashMap();
		String name=request.getParameter("name");
		if(StringUtils.isNotEmpty(name)){
			param.put("name",name);
		}
		String customerId=request.getParameter("customerId");
		if(StringUtils.isNotEmpty(customerId)){
			param.put("customerId", customerId);
		}
		return param;
	}
	
	/**
	 * 新增体成分仪
	 * @param request
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/addBodyComposition",method = RequestMethod.GET)
	public String addBodyComposition(HttpServletRequest request,Page pageData) throws Exception{
		return "bodyComposition/body_comp_edit";
	}
	/**
	 * 保存体成分仪
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/saveBodyComposition")
	@ResponseBody
	public Message saveBodyComposition(HttpServletRequest request) throws Exception{
		Map<String, Object> params = parseRequest(request);
		params = bodyCompositionService.parseBodyCompostion(params);
		String id = bodyCompositionService.saveData(params);
		return Message.data(id);
		
	}
	
	private Map<String,Object> parseRequest(HttpServletRequest request) {
		Map<String,Object> maps = Maps.newHashMap();
		Enumeration keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = (String)request.getParameter(key);
			maps.put(key, value);
		}
		return maps;
	}

	/**
	 * 上传体脂成分信息
	 * @param dataMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="upload")
	@ResponseBody
	public Message uploadInfo(HttpServletRequest request) throws Exception {
		logger.debug("----body------upload");
//		BufferedReader br = request.getReader();
//		StringBuffer sb = new StringBuffer();
//		String str = "";
//		while((str = br.readLine()) != null){
//			sb.append(str);	
//		}
//		String json = sb.toString();
		Map<String, Object> params = parseRequest(request);
		logger.debug("----body------upload data:" + params);

//		Map<String,Object> map = JsonUtil.jsonToMap(json);
//		if (map == null) {
//			return Message.error(Message.MSG_PARAM_NULL, "json 格式错误");
//		}
		Map<String,Object> map = bodyCompositionService.parseBodyCompostion(params);
		String customerId = (String)map.get("customerId");
		if (StringUtils.isEmpty(customerId)) {
			return Message.error(Message.MSG_PARAM_NULL, "身份证号不能为空");
		}
		
//		Date checkTime = (Date)map.get("checkTime");
//		
//		if (checkTime == null) {
//			return Message.error(Message.MSG_PARAM_NULL, "检查时间不能为空");
//		}
		
		String id = bodyCompositionService.saveData(map);
		logger.debug("----body------data ID:" + id);
		return Message.success();

	}

}
