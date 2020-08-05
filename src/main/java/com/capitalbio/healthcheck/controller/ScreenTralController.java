package com.capitalbio.healthcheck.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.healthcheck.service.CustomerService;
import com.capitalbio.healthcheck.service.HealthCheckService;
import com.google.common.collect.Maps;


@Controller
@RequestMapping("/screenTral")
public class ScreenTralController {
	@Autowired
	private HealthCheckService healthCheckService;
	
	@Autowired
	private CustomerService customerService;
	@Autowired AuthService authService;
	
	@RequestMapping(value="/screenTralList",method = RequestMethod.GET)
	@ControllerLog
	public String screenTralList(HttpServletRequest request) throws Exception {
		String uniqueId = request.getParameter("uniqueId");
		
		if (StringUtils.isEmpty(uniqueId)) {
			return "healthCheck/screen_tral";
		}
		
		String userId = (String) request.getSession().getAttribute("userId");
		String token = (String) request.getSession().getAttribute("token");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uniqueId", uniqueId);
		
		Map<String, Object> customer = customerService.getDataByQuery(params);
		Map<String,Object> secretMap = authService.requestInfoByUniqueId(uniqueId, token, userId);
		if (secretMap != null) {
			secretMap.remove("id");
			customer.putAll(secretMap);
		}
		
		Map<String, Object> sortMap = Maps.newHashMap();
		sortMap.put("checkDate", -1);
		List<Map<String, Object>> healthchecks = healthCheckService.queryList("healthcheck", params, sortMap);
		
		List<HealthCheck> listhc = new LinkedList<HealthCheck>();
		if (healthchecks != null && healthchecks.size() > 0) {
			for (Map<String, Object> healthcheck : healthchecks) {
				HealthCheck hc = new HealthCheck();
				hc.setId(String.valueOf(healthcheck.get("id")));
				hc.setStr("初筛信息");
				hc.setCreateDate(String.valueOf(healthcheck.get("checkDate")));
				hc.setClassifyResult(String.valueOf(healthcheck.get("classifyResult")));
				hc.setTitle("1");
				listhc.add(hc);
			}
		}
		
		Map<String, Object> sortMap1 = Maps.newHashMap();
		sortMap1.put("ctime", -1);
		List<Map<String, Object>> healthcheckDetails = healthCheckService.queryList("healthcheckDetail", params, sortMap1);
		if (healthcheckDetails != null) {
			for (Map<String, Object> healthcheckDetail : healthcheckDetails) {
				Date ctime = (Date)healthcheckDetail.get("ctime");
				healthcheckDetail.put("ctime", DateUtil.dateToString(ctime));
				HealthCheck hc = new HealthCheck();
				hc.setId(String.valueOf(healthcheckDetail.get("id")));
				hc.setStr("精筛信息");
				hc.setCreateDate(DateUtil.dateToString(ctime));
				hc.setClassifyResult(String.valueOf(healthcheckDetail.get("classifyResultJs")));
				hc.setTitle("2");
				listhc.add(hc);
			}
		}
		
		//加载基因筛查数据
		this.handleGeneData(listhc, params);
		
		request.setAttribute("customer", customer);
		
		Collections.sort(listhc, new HealthCheck());
		request.setAttribute("healthchecks", listhc);
		return "healthCheck/screen_tral";
	}
	
	/**
	 * 加载基因数据
	 *
	 * @param list
	 * @param params
	 */
	private void handleGeneData(List<HealthCheck> list, Map<String, Object> params) {
		List<Map<String, Object>> genes = healthCheckService.queryList("gene", params, null);
		Map<String, HealthCheck> mapGenes = Maps.newHashMap();
		if (genes != null && genes.size() > 0) {
			for (Map<String, Object> gene : genes) {
				List<Map<String, Object>> gxzs = (List<Map<String, Object>>)gene.get("gxz");
				List<Map<String, Object>> tnbs = (List<Map<String, Object>>)gene.get("tnb");
				List<Map<String, Object>> gxys = (List<Map<String, Object>>)gene.get("gxy");
				String uniqueId = (String)gene.get("uniqueId");
				this.convertGene(mapGenes, gxzs, "gxzTime", "gxzNo", "gxz", uniqueId);
				this.convertGene(mapGenes, tnbs, "tnbTime", "tnbNo", "tnb", uniqueId);
				this.convertGene(mapGenes, gxys, "gxyTime", "gxyNo", "gxy", uniqueId);
			}
		}
		
		list.addAll(mapGenes.values());
	}
	
	private void convertGene(Map<String, HealthCheck> mapGenes, List<Map<String, Object>> datas, String timeKey, String noKey, String geneStr, String uniqueId) {
		if (datas != null && datas.size() > 0) {
			for (Map<String, Object> data : datas) {
				String time = (String)data.get(timeKey);
				String no = (String)data.get(noKey);
				
				HealthCheck hc = mapGenes.get(time);
				String str = gene(geneStr) +no;
				if (hc != null) {
					String cr = hc.getClassifyResult();
					hc.setClassifyResult(cr + "<br/>" + str);
				} else {
					hc = new HealthCheck();
					hc.setTitle("3");
					hc.setId(uniqueId);
					hc.setCreateDate(time);
					hc.setStr("基因检测");
					hc.setClassifyResult(str);
					mapGenes.put(time, hc);
				}
			}
		}
		
	}
	private String gene(String gene) {
		String item = PropertyUtils.getProperty("item");
		if ("fuxin".equals(item)) {
			if (gene.equals("tnb")) {
				return "糖尿病用药套餐：";
			}
		} 
		else if ("kunming".equals(item)) {
			if (gene.equals("tnb")) {
				return "糖尿病用药套餐或糖尿病基因检测4项：";
			}
		}
		
		if (gene.equals("gxy")) {
			return "高血压用药套餐：";
		}
		if (gene.equals("gxz")) {
			return "他汀类降脂药套餐：";
		}
		
		return "";
	}
	
	public class HealthCheck implements Comparator{
		private String id;
		private String str;
		private String createDate;
		private String classifyResult;
		private String title;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		
		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}
		
		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		
		public String getClassifyResult() {
			return classifyResult;
		}

		public void setClassifyResult(String classifyResult) {
			this.classifyResult = classifyResult;
		}
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			HealthCheck h1 = (HealthCheck)o1;
			HealthCheck h2 = (HealthCheck)o2;
			if ("1".equals(h1.getTitle()) && "2".equals(h2.getTitle()) 
					&& DateUtil.stringToDate(h2.getCreateDate()).getTime() == DateUtil.stringToDate(h1.getCreateDate()).getTime()) {
				return 1;
			}
			return DateUtil.stringToDate(h2.getCreateDate()).compareTo(DateUtil.stringToDate(h1.getCreateDate()));
		}
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtil.stringToDate("2019-02-22").compareTo(DateUtil.stringToDate("2019-02-21")));
	}
}
