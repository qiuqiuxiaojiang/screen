package com.capitalbio.common.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.UrlResourceDAO;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.MD5Util;

/**
 * 一个自定义的service用来和数据库进行操作，即以后我们要通过数据库保存权限，则需要我们继承UserDetailsService
 * 
 *
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	protected static Logger logger = Logger.getLogger(CustomUserDetailsService.class.getName());
	
	@Autowired UserDAO userDao;
	@Autowired UrlResourceDAO urlResourceDao;
	
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		UserDetails user = null;
		try {
			// 搜索数据库以匹配用户登录名。
			Map<String, Object> dbUser = userDao.getUserByName(username);
			if(dbUser != null){
				user = new User(dbUser.get("username").toString(), MD5Util.MD5Encode(username).toLowerCase(), 
						true, true, true, true, getAuthorities(dbUser));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in retrieving user.");
			throw new UsernameNotFoundException("Error in retrieving user.");
		}
		return user;
	}
	
	/**
	 * 加盐值
	 */
	/*public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		SaltedUser user = null;
		try {
			// 搜索数据库以匹配用户登录名。
			Map<String, Object> dbUser = userDao.getUserByName(username);
			if(dbUser != null){
				user = new SaltedUser(dbUser.get("username").toString(), dbUser.get("password").toString().toLowerCase(), 
						true, true, true, true, getAuthorities(dbUser), dbUser.get("salt").toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in retrieving user.");
			throw new UsernameNotFoundException("Error in retrieving user.");
		}
		return user;
	}*/
	/**
	 * 判断用户的权限
	 * @param access 
	 * @return
	 */
	public Collection<GrantedAuthority> getAuthorities(Map<String, Object> dbUser) {
		
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
		if (dbUser.get("roles") != null) {
			List<Map<String, Object>> roles = JsonUtil.jsonToList(dbUser.get("roles").toString());
			if(roles != null && roles.size() > 0){
			
				for(Map<String, Object> role:roles){
					authList.add(new GrantedAuthorityImpl(role.get("rolekey").toString()));
				}
			}
		}
		return authList;
	}
}
