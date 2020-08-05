package com.capitalbio.common.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MenuDAO;
import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.util.MapComparator;
import com.google.common.collect.Maps;

@Service
public class MenuService extends BaseService{

	@Autowired MenuDAO menuDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return menuDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "menu";
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMenu(HttpServletRequest request, Map<String, Object> user){
		if(user.get("roles") != null){
			List<Map<String, Object>> roles = (List<Map<String, Object>>) user.get("roles");
			//获取用户权限信息
			List<Map<String, Object>> objList = new ArrayList<Map<String, Object>>();
			for(Map<String, Object> role:roles){
				
				//首先获取一级菜单
				Map<String, Object> map1 = Maps.newHashMap();
				map1.put("isfristmenu", "0");  //0：一级菜单   1:非一级菜单
				List<Map<String, Object>> menus = menuDao.getMenu(map1);
				
				//System.out.println("urlresources1:"+menus);
				for(Map<String, Object> menu:menus){
					
					/********************************获取二级菜单  START********************************/
					List<Map<String, Object>> objList2 = new ArrayList<Map<String, Object>>();
					Map<String, Object> map2 = Maps.newHashMap();
					map2.put("pid", menu.get("_id"));
					List<Map<String, Object>> menus2 =  menuDao.getMenu(map2);
					for(Map<String, Object> menu2:menus2){
						/********************************获取三级菜单  START********************************/
						List<Map<String, Object>> objList3 = new ArrayList<Map<String, Object>>();
						Map<String, Object> map3 = Maps.newHashMap();
						map3.put("pid", menu2.get("_id"));
						List<Map<String, Object>> menus3 =  menuDao.getMenu(map3);
						for(Map<String, Object> menu3:menus3){
							if(menu3.get("roles") != null){
								List<Map<String, Object>> urlroles3 = (List<Map<String, Object>>) menu3.get("roles");
								for(Map<String, Object> urlrole3:urlroles3){
									if(urlrole3.get("rolekey").equals(role.get("rolekey"))){
										Map<String, Object> objMap3 = Maps.newHashMap();
										objMap3.put("url", menu3.get("url"));
										objMap3.put("name", menu3.get("name"));
										objMap3.put("id", menu3.get("id"));
										objMap3.put("icon", menu3.get("icon"));
										objMap3.put("seq", menu3.get("seq"));
										if(!(objList3.contains(objMap3))){
											objList3.add(objMap3);
										}
										break;
									}
								}
							}
						}
						
						/********************************获取三级菜单  ENd********************************/
						
						if(menu2.get("roles") != null){
							List<Map<String, Object>> urlroles2 = (List<Map<String, Object>>) menu2.get("roles");
							for(Map<String, Object> urlrole2:urlroles2){
								if(urlrole2.get("rolekey").equals(role.get("rolekey"))){
									Map<String, Object> objMap2 = Maps.newHashMap();
									objMap2.put("url", menu2.get("url"));
									objMap2.put("name", menu2.get("name"));
									objMap2.put("id", menu2.get("id"));
									objMap2.put("icon", menu2.get("icon"));
									objMap2.put("seq", menu2.get("seq"));
									objMap2.put("nodes", objList3);
									if(!(objList2.contains(objMap2))){
										objList2.add(objMap2);
									}
									break;
								}
							}
						}
					}
					/********************************获取二级菜单 END********************************/
					
					if(menu.get("roles") != null){
						List<Map<String, Object>> urlroles = (List<Map<String, Object>>) menu.get("roles");
						for(Map<String, Object> urlrole:urlroles){
							if(urlrole.get("rolekey").equals(role.get("rolekey"))){
								Map<String, Object> objMap = Maps.newHashMap();
								objMap.put("url", menu.get("url"));
								objMap.put("name", menu.get("name"));
								objMap.put("id", menu.get("id"));
								objMap.put("icon", menu.get("icon"));
								objMap.put("seq", menu.get("seq"));
								objMap.put("nodes", objList2);
								if(!(objList.contains(objMap))){
									objList.add(objMap);
								}
								break;
							}
						}
					}
				}
				
				
			}
			Collections.sort(objList, new MapComparator());
			return objList;
		}
		return null;
	}

}
