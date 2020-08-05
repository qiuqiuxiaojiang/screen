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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.RoleService;
import com.capitalbio.common.service.UrlResourceService;
import com.capitalbio.common.service.UserService;
import com.capitalbio.common.util.XssCleanUtil;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/role")
public class RoleController {
	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired private RoleService roleService;
	@Autowired private UserService userService;
	@Autowired private UrlResourceService urlResourceService;
	
	@RequestMapping(value="rolelist",method = RequestMethod.GET)
	public String listRole(HttpServletRequest request,ModelMap modelMap,Page pageData) throws Exception{
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		request.setAttribute("roles", roles);
		return "role/role_list";
	}
	
	
	
	@RequestMapping(value="/addrole",method = RequestMethod.GET)
	public String addRole(){
		return "role/role_edit";
	}
	
	@RequestMapping(value="save",method = RequestMethod.POST)
	@ResponseBody
	public Message saveRole(HttpServletRequest request){
		Map<String, Object> params = parseRequest(request);
		String id = (String) params.get("id");
		Map<String, Object> obj = null;
		
		try {
			if(StringUtils.isNotEmpty(id)){
				obj = roleService.getData(id);
				if(obj == null){
					return Message.error("data not found");
				}
			}else{
				obj = Maps.newHashMap();
			}
			obj.putAll(params);
			id = roleService.saveData(obj);
			return Message.data(id);
		} catch (Exception e) {
			logger.error("save role error", e);
			return Message.error("save role error");
		}
		
	}
	
	@RequestMapping(value="updateRole/{id}",method = RequestMethod.GET)
	public String updateRole(HttpServletRequest request,@PathVariable String id) throws Exception{
		Map<String, Object> role = roleService.getData(id);
		request.setAttribute("role", role);
		return "role/role_edit";
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="deleteRole/{id}",method = RequestMethod.POST)
	@ResponseBody
	public Message deleteRole(@PathVariable String id){
		if(StringUtils.isNoneEmpty(id)){
			//删除用户表中对应的角色
			Map<String, Object> userMap = Maps.newHashMap();
			userMap.put("roles.id", id);
			List<Map<String, Object>> users = userService.queryList(userMap);
			if(users != null && users.size() > 0){
				for(Map<String, Object> user:users){
					List<Map<String, Object>> roles = (List<Map<String, Object>>) user.get("roles");
					List<Map<String, Object>> objList = new ArrayList<Map<String,Object>>();
					for(Map<String, Object> role:roles){
						if(!role.get("id").equals(id)){
							objList.add(role);
						}
					}
					user.put("roles", objList);
					userService.saveData(user);
				}
			}
			
			//删除资源表中对应的角色
			Map<String, Object> urlresource = Maps.newHashMap();
			urlresource.put("roles.id", id);
			List<Map<String, Object>> resources = urlResourceService.queryList(urlresource);
			if(resources != null && !"".equals(resources)){
				for(Map<String, Object> resource:resources){
					List<Map<String, Object>> resroles = (List<Map<String, Object>>) resource.get("roles");
					List<Map<String, Object>> resList = new ArrayList<Map<String,Object>>();
					for(Map<String, Object> role:resroles){
						if(!role.get("id").equals(id)){
							resList.add(role);
						}
					}
					resource.put("roles", resList);
					urlResourceService.saveData(resource);
				}
			}
			
			
			roleService.deleteData(id);
			return Message.success();
		}
		return Message.error("delete role error");
	}
	
	@RequestMapping(value="getRole",method = RequestMethod.GET)
	public String getRole(HttpServletRequest request,Page pageData,String username) throws Exception{
		/*Enumeration e = request.getSession().getAttributeNames();

		while( e.hasMoreElements()) {
			String sessionName=(String)e.nextElement();
			System.out.println("\nsession item name="+sessionName);
			System.out.println("\nsession item value="+request.getSession().getAttribute(sessionName));
		}*/
		//System.out.println("session:"+request.getSession().getAttribute("SPRING_SECURITY_CONTEXT"));
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		request.setAttribute("roles", roles);
		//request.setAttribute("user", user);
		return "role/getRole";
	}
	
	private Map<String, Object> parseRequest(HttpServletRequest request){
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", request.getParameter("roleid"));
//		params.put("rolename", request.getParameter("rolename"));
//		params.put("rolekey", request.getParameter("rolekey"));
		
		String rolename = XssCleanUtil.xssClean(request.getParameter("rolename"));
		params.put("rolename", rolename);
		
		String rolekey = XssCleanUtil.xssClean(request.getParameter("rolekey"));
		params.put("rolekey", rolekey);
		
		return params;
	}
	
}
