package com.capitalbio.healthcheck.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.service.GeneService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.FileManageService;
import com.capitalbio.common.util.ContextUtils;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.ParamUtils;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.eye.service.EyeRecordService;
import com.capitalbio.healthcheck.service.GeneratePdfService;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RequestMapping("/gene")
@Controller
public class GeneController {
	
	@Autowired
	GeneService geneService;
	
	@Autowired
	HealthCheckService healthCheckService;
	
	@Autowired
	GeneratePdfService generatePdfService;
	
	@Autowired 
	AuthService authService;
	
	@Autowired 
	EyeRecordService eyeRecordService;
	
	@Autowired 
	FileManageService fileManageService;
	
	@RequestMapping(value = "geneUI")
	public String geneUI(HttpServletRequest request) {
		String sampleTime = DateUtil.dateToString(new Date());
		request.setAttribute("sampleTime", sampleTime);
		return "healthCheck/gene_edit";
	}
	
	
	@RequestMapping(value = "saveGene")
	@ResponseBody
	@ControllerLog
	public Message saveGene(HttpServletRequest request) {
		String birthday = request.getParameter("birthday");
		String haveDisease = request.getParameter("haveDisease");
		String disease = request.getParameter("disease");
		String familyDisease = request.getParameter("familyDisease");
		String familyHistory = request.getParameter("familyHistory");
		String mobile = request.getParameter("mobile");
		String uniqueId = request.getParameter("uniqueId");
		String geneId = request.getParameter("geneId");
		String age = request.getParameter("age");
		String gender = request.getParameter("gender");
		
		String[] tnbTime = request.getParameterValues("tnbTime");
		String[] tnbNo = request.getParameterValues("tnbNo");
		String[] gxyTime = request.getParameterValues("gxyTime");
		String[] gxyNo = request.getParameterValues("gxyNo");
		String[] gxzTime = request.getParameterValues("gxzTime");
		String[] gxzNo = request.getParameterValues("gxzNo");
		
		List<Map<String, Object>> tnbList = Lists.newArrayList();
		if (tnbTime.length != 0 && tnbNo.length != 0) {
			for (int i = 0; i < tnbTime.length; i ++) {
				Map<String, Object> map = Maps.newHashMap();
				if (StringUtils.isEmpty(tnbTime[i]) && StringUtils.isEmpty(tnbNo[i])) {
					continue;
				}
				map.put("tnbTime", tnbTime[i]);
				map.put("tnbNo", tnbNo[i]);
				tnbList.add(map);
				
			}
		}
		
		List<Map<String, Object>> gxyList = Lists.newArrayList();
		if (gxyTime.length != 0 && gxyNo.length != 0) {
			for (int i = 0; i < gxyTime.length; i ++) {
				Map<String, Object> map = Maps.newHashMap();
				if (StringUtils.isEmpty(gxyTime[i]) && StringUtils.isEmpty(gxyNo[i])) {
					continue;
				}
				map.put("gxyTime", gxyTime[i]);
				map.put("gxyNo", gxyNo[i]);
				gxyList.add(map);
				
			}
		}
		
		List<Map<String, Object>> gxzList = Lists.newArrayList();
		if (gxzTime.length != 0 && gxzNo.length != 0) {
			for (int i = 0; i < gxzTime.length; i ++) {
				Map<String, Object> map = Maps.newHashMap();
				if (StringUtils.isEmpty(gxzTime[i]) && StringUtils.isEmpty(gxzNo[i])) {
					continue;
				}
				map.put("gxzTime", gxzTime[i]);
				map.put("gxzNo", gxzNo[i]);
				gxzList.add(map);
				
			}
		}
		
		Map<String, Object> geneMap = null;
		if (StringUtils.isNotEmpty(geneId)) {
			geneMap = healthCheckService.getData("gene", geneId);
		} else {
			geneMap = Maps.newHashMap();
		}
		
		geneMap.put("birthday", birthday);
		geneMap.put("gender", gender);
		geneMap.put("age", age);
		geneMap.put("haveDisease", haveDisease);
		geneMap.put("disease", disease);
		geneMap.put("familyDisease", familyDisease);
		geneMap.put("familyHistory", familyHistory);
		geneMap.put("uniqueId", uniqueId);
		geneMap.put("tnb", tnbList);
		geneMap.put("gxy", gxyList);
		geneMap.put("gxz", gxzList);
		
		//保存基因检测历史记录
		healthCheckService.saveData("geneHistory", geneMap);
		
		if (StringUtils.isNotEmpty(geneId)) {
			geneMap.put("id", geneId);
		}
		String id = healthCheckService.saveData("gene", geneMap);
		
		geneMap.remove("birthday");
		geneMap.remove("gender");
		geneMap.remove("age");
		geneMap.remove("haveDisease");
		geneMap.remove("disease");
		geneMap.remove("familyDisease");
		geneMap.remove("familyHistory");
		healthCheckService.saveCustomer(geneMap, null);
		return Message.data(id);	
	}
	
	@RequestMapping(value = "geneList")
	public String geneList(HttpServletRequest request) throws Exception {
		return "healthCheck/gene_list";
	}
	
	/**
	 * 查询gene列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "geneListPage")
	@ResponseBody
	@ControllerLog
	public Message geneListPage(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		String uniqueId = null;
		if (StringUtils.isNotEmpty(customerId)) {
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
			if (secretMap == null) {
				return Message.success();
			}
			uniqueId = (String)secretMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				return Message.success();
			}

		}
		Map<String,Object> queryMap = Maps.newHashMap();
		String customerName = request.getParameter("customerName");
		if (StringUtils.isNotEmpty(customerName)) {
			List<String> idList = authService.queryInfoByName(customerName);
			if (idList == null || idList.size() == 0) {
				return Message.success();
			}
			Map<String,Object> inMap = Maps.newHashMap();
			inMap.put("$in", idList);
			queryMap.put("uniqueId", inMap);
		}
		//Map<String,Object> queryMap = Maps.newHashMap();
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		if (StringUtils.isNotEmpty(uniqueId)) {
			queryMap.put("uniqueId", uniqueId);
		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.findPageBySort(pageData, queryMap, sortMap,"gene");
		long count = healthCheckService.count("gene",queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	@RequestMapping(value = "editGene")
	@ControllerLog
	public String editGene(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		
		String uniqueId = request.getParameter("uniqueId");
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		Map<String, Object> editEntity = healthCheckService.getDataByQuery("gene", query);
		if (editEntity == null) {
			return "healthCheck/gene_edit";
		}
		
		editEntity.put("geneId", editEntity.remove("id"));
		
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		if (secretMap != null) {
			editEntity.putAll(secretMap);
		}
		
		request.setAttribute("editEntity", editEntity);
		
		String sampleTime = DateUtil.dateToString(new Date());
		request.setAttribute("sampleTime", sampleTime);
		
		return "healthCheck/gene_edit";
	}

	@RequestMapping(value="/getGeneInfo")
	@ResponseBody
	@ControllerLog
	public Message getGeneInfo(HttpServletRequest request) throws Exception{
		// 套餐唯一id
		String serviceCode = request.getParameter("serviceCode");
		//条码编号
		String aspId = request.getParameter("aspId");
		String name = request.getParameter("name");
		
		//判断条形编号是否被其他筛查人员使用
		
		
		/**
		 *  调用基因检测平台获取样本信息
		 *  
		 *  code：100  msg：参数错误
		 *	code：101  msg：条码编号不存在
		 *	code：102  msg：样本信息中不存在此套餐id
		 *	code：200  msg：查询成功
		 */
//		{
//		    "data":{
//		        "reportTypeName":"类经堂遗传性肿瘤基因检测（女34项）",
//		        "fileReportPath":"http://file.medlab.cn/ehfs/upload/otherfile/sip/11110000/report_11110000_Two.pdf",
//		        "name":"吴宁安",
//		        "state":3
//		    },
//		    "code":"200",
//		    "msg":""
//		}
		
		Map<String, Object> geneInfo = geneService.getGeneInfo(serviceCode, aspId);
		String code = (String) geneInfo.get("code");
		if (code.equals("200")) {
			//判断姓名是否一致，若不一致给予友情提示
			Map<String, Object> data = (Map<String, Object>)geneInfo.get("data");
			if (data.get("name") != null && data.get("name") != "") {
				if (!name.equals(data.get("name"))) {
					
				}
			}
		}
		
		return Message.dataMap(geneInfo);
	}
	
	@RequestMapping(value = "showGene")
	@ControllerLog
	public String showGene(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		
		String uniqueId = request.getParameter("uniqueId");
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		Map<String, Object> editEntity = healthCheckService.getDataByQuery("gene", query);
		if (editEntity == null) {
			return "healthCheck/gene_show";
		}
		
		editEntity.put("geneId", editEntity.remove("id"));
		
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		if (secretMap != null) {
			editEntity.putAll(secretMap);
		}
		
		String item = PropertyUtils.getProperty("item");
		if ("fuxin".equals(item)) {
			editEntity.put("tnbStr", "糖尿病用药套餐：");
		} 
		else if ("kunming".equals(item)) {
			editEntity.put("tnbStr", "糖尿病用药套餐或糖尿病基因检测4项：");
		}
		
		editEntity.put("gxyStr", "高血压用药套餐：");
		editEntity.put("gxzStr", "他汀类降脂药套餐：");
		
		request.setAttribute("editEntity", editEntity);
		return "healthCheck/gene_show";
	}
	/*@RequestMapping(value = "geneUI")
	public String geneUI() {
		return "healthCheck/gene_edit";
	}
	
	
	@RequestMapping(value = "saveGene")
	@ResponseBody
	@ControllerLog
	public Message saveGene(HttpServletRequest request) {
		Map<String, Object> params = parseRequest(request);
		String uniqueId = (String)params.get("uniqueId");
		if (StringUtils.isEmpty(uniqueId)) {
			return Message.error("此人尚未建档，请建档后再进行筛查");
		}
		
		params.remove("customerId");
		params.remove("name");
		params.remove("mobile");
		params.remove("haveDisease");
		params.remove("disease");
		params.remove("familyDisease");
		params.remove("familyHistory");
		params.remove("id");
		
		String geneId = (String)params.remove("geneId");;
		Map<String, Object> map = null;
		if (StringUtils.isNotEmpty(geneId)) {
			map = healthCheckService.getData("gene", geneId);
		} else {
			map = Maps.newHashMap();
		}
		map.putAll(params);
		String id = healthCheckService.saveData("gene", map);
		return Message.data(id);	
	}
	
	@RequestMapping(value = "geneList")
	public String geneList(HttpServletRequest request) throws Exception {
		return "healthCheck/gene_list";
	}
	
	*//**
	 * 查询gene列表
	 * @param request
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value = "geneListPage")
	@ResponseBody
	@ControllerLog
	public Message geneListPage(HttpServletRequest request) throws Exception {
		String token = (String) ContextUtils.getSession().getAttribute("token");
		String userId = (String) ContextUtils.getSession().getAttribute("userId");
		
		String customerId = request.getParameter("customerId");
		String uniqueId = null;
		if (StringUtils.isNotEmpty(customerId)) {
			Map<String,Object> secretMap = authService.requestInfo(customerId, token, userId);
			if (secretMap == null) {
				return Message.success();
			}
			uniqueId = (String)secretMap.get("uniqueId");
			if (StringUtils.isEmpty(uniqueId)) {
				return Message.success();
			}

		}
		Map<String,Object> queryMap = Maps.newHashMap();
		String customerName = request.getParameter("customerName");
		if (StringUtils.isNotEmpty(customerName)) {
			List<String> idList = authService.queryInfoByName(customerName);
			if (idList == null || idList.size() == 0) {
				return Message.success();
			}
			Map<String,Object> inMap = Maps.newHashMap();
			inMap.put("$in", idList);
			queryMap.put("uniqueId", inMap);
		}
		//Map<String,Object> queryMap = Maps.newHashMap();
		Page pageData = new Page();
		Integer page = ParamUtils.getIntValue(request.getParameter("current"));
		Integer pageCount = ParamUtils.getIntValue(request.getParameter("showRows"));
		if (page != null) {
			pageData.setPageNum(page.intValue());
		}
		if (pageCount != null) {
			pageData.setNumPerPage(pageCount);
		}
		if (StringUtils.isNotEmpty(uniqueId)) {
			queryMap.put("uniqueId", uniqueId);
		}
		String sortFileld = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("ctime", -1);
		if (StringUtils.isNotEmpty(sortFileld) && StringUtils.isNotEmpty(sortOrder)) {
			sortMap.put(sortFileld, Integer.parseInt(sortOrder));
		}
		Map<String, Object> returnMap = Maps.newHashMap();
		List<Map<String, Object>> list = healthCheckService.findPageBySort(pageData, queryMap, sortMap,"gene");
		long count = healthCheckService.count("gene",queryMap);
		returnMap.put("list", list);
		returnMap.put("totalCount", count);
		return Message.dataMap(returnMap);
	}
	
	@RequestMapping(value = "editGene")
	@ControllerLog
	public String editGene(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		
		String id = request.getParameter("id");
		Map<String, Object> editEntity = healthCheckService.getData("gene", id);
		if (editEntity == null) {
			return "healthCheck/gene_edit";
		}
		
		System.out.println("editEntity:" + editEntity);
		String uniqueId = (String)editEntity.get("uniqueId");
		System.out.println("/editHealthCheck--uniqueId=="+uniqueId);
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		if (secretMap != null) {
			editEntity.putAll(secretMap);
		}
		
		Map<String, Object> query = Maps.newHashMap();
		query.put("uniqueId", uniqueId);
		Map<String, Object> customer = healthCheckService.getDataByQuery("customer", query);
		editEntity.put("haveDisease", customer.get("haveDisease"));
		editEntity.put("disease", customer.get("disease"));
		editEntity.put("familyHistory", customer.get("familyHistory"));
		editEntity.put("familyDisease", customer.get("familyDisease"));
		
		request.setAttribute("editEntity", editEntity);
		request.setAttribute("geneId", id);
		return "healthCheck/gene_edit";
	}
	
	@SuppressWarnings("rawtypes")
	private Map<String,Object> parseRequest(HttpServletRequest request) {
		Map<String,Object> maps = Maps.newHashMap();
		Enumeration keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = (String)request.getParameter(key);
			maps.put(key, value);
		}
		return maps;
	}*/
	
}
