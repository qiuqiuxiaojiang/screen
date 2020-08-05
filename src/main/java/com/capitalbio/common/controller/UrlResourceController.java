package com.capitalbio.common.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.RoleService;
import com.capitalbio.common.service.UrlResourceService;
import com.capitalbio.common.util.XssCleanUtil;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("urlresource")
public class UrlResourceController {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private UrlResourceService urlResourceService;
	@Autowired private RoleService roleService;
	
	@RequestMapping(value="/resourcelist",method = RequestMethod.GET)
	public String resourcelist(HttpServletRequest request,Page pageData) throws Exception{
		Map<String, Object> map = Maps.newHashMap();
		List<Map<String, Object>> urlresources = urlResourceService.queryList(map);
		request.setAttribute("urlresources", urlresources);
		return "urlresource/urlresource_list";
	}
	
	@RequestMapping(value="/addUrlResource",method = RequestMethod.GET)
	public String addUrlResource(HttpServletRequest request,Page pageData) throws Exception{
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		
	/*	Map<String, Object> map = Maps.newHashMap();
		map.put("isfristmenu", "0");
		List<Map<String, Object>> urlresources = urlResourceService.queryList(map);
		request.setAttribute("urlresources", urlresources);*/
		request.setAttribute("roles", roles);
		return "urlresource/urlresource_edit";
	}
	
	@RequestMapping(value="/saveUrlResource",method = RequestMethod.GET)
	@ResponseBody
	public Message saveUrlResource(HttpServletRequest request) throws Exception{
		List<Map<String, Object>> objList = new ArrayList<Map<String,Object>>();
		Map<String, Object> params = parseRequest(request);
		String roles = params.get("roles").toString();
		if(roles != null && !"".equals(roles)){
			if(roles.contains(",")){
				String[] roleids = roles.split(",");
				for(String roleid:roleids) {
					Map<String, Object> objrole = roleService.getData(roleid);
					objList.add(objrole);
				}
			}else{
				Map<String, Object> objrole = roleService.getData(roles);
				objList.add(objrole);
			}
		}
		
		params.put("roles", objList);
		
		String id = (String) params.get("id");
		
		Map<String, Object> obj = null;
		
		try {
			if(StringUtils.isNotEmpty(id)){
				System.out.println("111111111");
				obj = urlResourceService.getData(id);
			}else{
				System.out.println("2222");
				obj = Maps.newHashMap();
			}
			obj.putAll(params);
			System.out.println("obj:"+obj);
			id = urlResourceService.saveData(obj);
			return Message.data(id);
		} catch (Exception e) {
			logger.error("save urlresource error", e);
			return Message.error("save urlresource erro");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateUrlResource/{id}", method = RequestMethod.GET)
	public String updateUrlResource(@PathVariable String id, HttpServletRequest request, Page pageData) throws Exception{
		Map<String, Object> urlResource = urlResourceService.getData(id);
		List<Map<String, Object>> urlroles = (List<Map<String, Object>>) urlResource.get("roles");
		String roleids = ",";
		for(Map<String, Object> role:urlroles){
			roleids = roleids + role.get("_id").toString()+",";
		}
		urlResource.put("roles", roleids);
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("isfristmenu", "0");
		List<Map<String, Object>> urlresources = urlResourceService.queryList(map);
		request.setAttribute("roles", roles);
		request.setAttribute("urlResource", urlResource);
		request.setAttribute("urlresources", urlresources);
		return "urlresource/urlresource_edit";
	}
	
	@RequestMapping(value = "/delUrlResource/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Message delUrlResource(@PathVariable String id){
		if(StringUtils.isNotEmpty(id)){
			urlResourceService.deleteData(id);
			return Message.success();
		}
		return Message.error("delete urlresource error");
	}

	
	
	private Map<String, Object> parseRequest(HttpServletRequest request){
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", request.getParameter("urlresourceid"));
//		params.put("name", request.getParameter("name"));
//		params.put("url", request.getParameter("url"));
//		params.put("roles", request.getParameter("roles"));
		
		String name = XssCleanUtil.xssClean(request.getParameter("name"));
		params.put("name", name);
		
		String url = XssCleanUtil.xssClean(request.getParameter("url"));
		params.put("url", url);
		
		String roles = XssCleanUtil.xssClean(request.getParameter("roles"));
		params.put("roles", roles);
		
//		params.put("pid", request.getParameter("pid"));
//		params.put("seq", request.getParameter("seq"));
//		params.put("isfristmenu", request.getParameter("isfristmenu"));
		return params;
	}
	

}
