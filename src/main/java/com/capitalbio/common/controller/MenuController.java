package com.capitalbio.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.MenuService;
import com.capitalbio.common.service.RoleService;
import com.capitalbio.common.util.MapComparator;
import com.capitalbio.common.util.XssCleanUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("menu")
public class MenuController {

	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired MenuService menuService;
	@Autowired RoleService roleService;
	
	@RequestMapping(value="/menulist",method = RequestMethod.GET)
	@ControllerLog
	public String menulist(HttpServletRequest request) throws Exception{
		Map<String, Object> map = Maps.newHashMap();
		map.put("isfristmenu", "0");
		List<Map<String, Object>> menus = menuService.queryList(map);
		List<Map<String, Object>> menuList = new ArrayList<>();
		for(Map<String, Object> menu:menus){
			Map<String, Object> childmap = Maps.newHashMap();
			childmap.put("pid", menu.get("_id"));
			List<Map<String, Object>> childMenus = menuService.queryList(childmap);
			menu.put("childMenus", childMenus);
			
			for (Map<String, Object> childMenu : childMenus) {
				Map<String, Object> queryMap = Maps.newHashMap();
				queryMap.put("pid", childMenu.get("_id"));
				List<Map<String, Object>> threeMenus = menuService.queryList(queryMap);
				childMenu.put("threeMenus", threeMenus);
			}
			
			menuList.add(menu);
		}
		
		Collections.sort(menuList, new MapComparator());
		request.setAttribute("menus", menuList);
		System.out.println("menuList:" + menuList);
		return "menu/menu_list";
	}
	
	@RequestMapping(value="/addMenu",method = RequestMethod.GET)
	@ControllerLog
	public String addMenu(HttpServletRequest request,Page pageData) throws Exception{
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("isfristmenu", "0");
		List<Map<String, Object>> menus = menuService.queryList(map);
		request.setAttribute("menus", menus);
		request.setAttribute("roles", roles);
		return "menu/menu_edit";
	}
	
	@RequestMapping(value="/saveMenu",method = RequestMethod.GET)
	@ResponseBody
	@ControllerLog
	public Message saveMenu(HttpServletRequest request) throws Exception{
		List<Map<String, Object>> objList = new ArrayList<Map<String,Object>>();
		Map<String, Object> params = parseRequest(request);
		
		ObjectId pid = null;
		if(params.get("menuSecond") != null && !"".equals(params.get("menuSecond"))){
			String menuSecond = (String) params.remove("menuSecond");
			pid = new ObjectId(menuSecond);
			params.put("pid", pid);
		} else if(params.get("pid") != null && !"".equals(params.get("pid"))){
			pid = new ObjectId(params.get("pid").toString());
			params.put("pid", pid);
		}
		
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
				obj = menuService.getData(id);
			}else{
				obj = Maps.newHashMap();
			}
			obj.putAll(params);
			id = menuService.saveData(obj);
			return Message.data(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("save menu error", e);
			return Message.error("save menu erro");
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/updateMenu/{id}", method = RequestMethod.GET)
	@ControllerLog
	public String updateMenu(@PathVariable String id, HttpServletRequest request, Page pageData) throws Exception{
		Map<String, Object> menu = menuService.getData(id);//三级
		
		String fristId = "";
		String secondId = "";
		List<Map<String, Object>> thridMenuList = Lists.newArrayList();
		
		if (menu.get("pid") != null && !"".equals(menu.get("pid"))) {
			//获取上一级
			Map<String, Object> menu1 = menuService.getData(menu.get("pid").toString());//二级
			if (menu1.get("pid") != null && !"".equals(menu1.get("pid"))) {
				fristId = menu1.get("pid").toString();//一级id
				secondId = menu.get("pid").toString();
				
				Map<String, Object> queryMap = Maps.newHashMap();
				queryMap.put("pid", new ObjectId(fristId));
				thridMenuList = menuService.queryList(queryMap);
			} else {
				fristId = menu.get("pid").toString();//一级id
				secondId = id;
			}
		}
		menu.put("fristId", fristId);
		menu.put("secondId", secondId);
		
		List<Map<String, Object>> urlroles = (List<Map<String, Object>>) menu.get("roles");
		String roleids = ",";
		for(Map<String, Object> role:urlroles){
			if (role.get("id") != "" && role.get("id") != null) {
				roleids = roleids + role.get("id").toString()+",";
			}
			
		}
		menu.put("roles", roleids);
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("isfristmenu", "0");
		List<Map<String, Object>> menus = menuService.queryList(map);
		request.setAttribute("roles", roles);
		request.setAttribute("menu", menu);
		request.setAttribute("menus", menus);
		request.setAttribute("thrid", thridMenuList);
		return "menu/menu_edit";
	}
	
	@RequestMapping(value = "/delMenu/{id}", method = RequestMethod.POST)
	@ResponseBody
	@ControllerLog
	public Message delMenu(@PathVariable String id,HttpServletRequest request){
		String isFristMenu = request.getParameter("isFristMenu");
		if(StringUtils.isNotEmpty(id)){
			if(isFristMenu.equals("0")){//0：删除一级菜单
				Map<String, Object> map = Maps.newHashMap();
				map.put("pid", new ObjectId(id));
				menuService.deleteDataByParams(map);
			}
			menuService.deleteData(id);
			return Message.success();
		}
		return Message.error("delete menu error");
	}
	
	//查看相应子菜单
	@RequestMapping(value="/getSubmodule/{id}",method = RequestMethod.GET)
	public String getSubmodule(@PathVariable String id,HttpServletRequest request) throws Exception{
		Map<String, Object> url = menuService.getData(id);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("pid", url.get("_id"));
		List<Map<String, Object>> menus = menuService.queryList(map);
		request.setAttribute("islook", 1);
		request.setAttribute("menus", menus);
		return "menu/menu_list";
	}
	
	private Map<String, Object> parseRequest(HttpServletRequest request){
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", request.getParameter("menuid"));
//		params.put("name", request.getParameter("name"));
//		params.put("url", request.getParameter("url"));
//		params.put("roles", request.getParameter("roles"));
//		params.put("pid", request.getParameter("pid"));
//		params.put("seq", request.getParameter("seq"));
//		params.put("isfristmenu", request.getParameter("isfristmenu"));
		
		String name = XssCleanUtil.xssClean(request.getParameter("name"));
		params.put("name", name);
		
		String url = XssCleanUtil.xssClean(request.getParameter("url"));
		params.put("url", url);
		
		String roles = XssCleanUtil.xssClean(request.getParameter("roles"));
		params.put("roles", roles);
		
		String pid = XssCleanUtil.xssClean(request.getParameter("pid"));
		params.put("pid", pid);
		
		String seq = XssCleanUtil.xssClean(request.getParameter("seq"));
		params.put("seq", seq);
		/*if (StringUtils.isNotEmpty(seq)) {
			params.put("seq", Integer.parseInt(seq));
		}*/
		
		String icon = XssCleanUtil.xssClean(request.getParameter("icon"));
		params.put("icon", icon);
		
		String menuSecond = XssCleanUtil.xssClean(request.getParameter("menuSecond"));
		params.put("menuSecond", menuSecond);
		
		String isfristmenu = XssCleanUtil.xssClean(request.getParameter("isfristmenu"));
		params.put("isfristmenu", isfristmenu);
		return params;
	}
	
	@RequestMapping("getSecondMenu")
	@ResponseBody
	@ControllerLog
	public Message getSecondMenu(HttpServletRequest request) {
		String pid = request.getParameter("pid");
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("pid", new ObjectId(pid));
		List<Map<String, Object>> list = menuService.queryList(queryMap);
		return Message.dataList(list);
	}
}
