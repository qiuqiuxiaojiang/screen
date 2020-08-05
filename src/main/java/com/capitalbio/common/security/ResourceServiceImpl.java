package com.capitalbio.common.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.AntUrlPathMatcher;
import org.springframework.security.web.util.UrlMatcher;

import com.capitalbio.common.dao.UrlResourceDAO;
import com.capitalbio.common.util.JsonUtil;
import com.google.common.collect.Maps;

public class ResourceServiceImpl implements ResourceService{

protected static Logger logger = Logger.getLogger(ResourceServiceImpl.class.getName());
	
	@Autowired private UrlResourceDAO urlResourceDao;
	private UrlMatcher urlMatcher = new AntUrlPathMatcher();   
    private static Map<String, Collection<ConfigAttribute>> resourceMap = null;  
      
    //tomcat启动时实例化一次  
    public void init() {  
    	resourceMap = new HashMap<String, Collection<ConfigAttribute>>();
    	
    	Map<String, Object> map = Maps.newHashMap();
    	List<Map<String, Object>> resources = urlResourceDao.queryList("urlresource", map);
    	for(Map<String, Object> resource:resources){
    		resourceMap.put(resource.get("url").toString(), listToCollection(JsonUtil.jsonToList(resource.get("roles").toString())));
    	}
    }
    
    private Collection<ConfigAttribute> listToCollection(List<Map<String, Object>> roles){
    	List<ConfigAttribute> list = new ArrayList<ConfigAttribute>();
    	for(Map<String, Object> role:roles){
    		list.add(new SecurityConfig(role.get("rolekey").toString()));
    	}
    	return list;
    }
      
    //参数是要访问的url，返回这个url对于的所有权限（或角色）  
    //这个方法在url请求时才会调用，服务器启动时不会执行这个方法，前提是需要在<http>标签内设置  <custom-filter>标签
    //getAttributes这个方法会根据你的请求路径去获取这个路径应该是有哪些权限才可以去访问。
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // object是一个url，被用户请求的url  将参数转为url      
        String url = ((FilterInvocation)object).getRequestUrl();
       
        //resourceMap保存了init方法加载进来的数据
        Iterator<String> ite = resourceMap.keySet().iterator();   
        while (ite.hasNext()) { 
        	
        	//取出resourceMap中读取数据库的url地址
            String resURL = ite.next(); 
            
            //如果两个 url地址相同，那么将返回resourceMap中对应的权限集合，然后跳转到MyAccessDecisionManager类里的decide方法，再判断权限
            if (urlMatcher.pathMatchesUrl(resURL, url)) {
            	 init();
            	//返回对应的url地址的权限 ，resourceMap是一个主键为url，值为权限的集合对象
                return resourceMap.get(resURL);           
            }         
        } 
        //如果上面的两个url地址没有匹配，返回return null，不再调用MyAccessDecisionManager类里的decide方法进行权限验证，代表允许访问页面
        return null;      
    }
    
    public boolean supports(Class<?>clazz) {   
            return true;    
    }
    
    /**
     * 获取所有权限配置属性
     */
    public Collection<ConfigAttribute> getAllConfigAttributes() {  
    	Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();
    	for(Map.Entry<String, Collection<ConfigAttribute>> entry : resourceMap.entrySet()){
    		allAttributes.addAll(entry.getValue());
    	}
        return allAttributes;    
    } 
  

}
