package com.capitalbio.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.dao.UserDAO;
import com.capitalbio.common.util.IPUtil;
import com.capitalbio.common.util.ImageUtil;
import com.google.common.collect.Maps;

@Service
public class UserService extends BaseService{

	@Autowired private UserDAO userDao;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		// TODO Auto-generated method stub
		return userDao;
	}

	@Override
	public String getCollName() {
		// TODO Auto-generated method stub
		return "user";
	}
	
	public String saveUser(HttpServletRequest request) throws Exception{
		Map<String, Object> params = parseRequet(request);
        String id = (String) params.get("id");
    	
		if(StringUtils.isNotEmpty(id)){
			HashMap<String, Object> obj = (HashMap<String, Object>) getData(id);
			if(obj != null){
				obj.putAll(params);
				id = saveData(obj);
				return id;
			}
		}
		return null;
	}
	
	/**
	 * 用户上传头像
	 * @param request
	 * @param x // 截取图片x坐标
	 * @param y // 截取图片y坐标
	 * @param h // 截取区域高度
	 * @param w // 截取区域宽度
	 * @param imageFile 图片文件 
	 * @param y_h // 图片等比缩放后高度
	 * @param y_w // 图片等比缩放后宽度
	 * @return
	 * @throws Exception
	 */
	public String uploadHeadImage(HttpServletRequest request, MultipartFile imageFile,String x,String y,String h,String w, String y_h, String y_w) throws Exception{
        
		if (imageFile != null) {
			int imageX = Integer.parseInt(x);
	        int imageY = Integer.parseInt(y);
	        int imageH = Integer.parseInt(h);
	        int imageW = Integer.parseInt(w);
	        
	        double tempH = Double.parseDouble(y_h);
	        double tempW = Double.parseDouble(y_w);
	        
	        int _H = (int) tempH;
	        int _W = (int) tempW;
	        
	        //这里开始截取操作
	        byte[] bytecut = ImageUtil.imgCut(imageFile,imageX,imageY,imageW,imageH,_H,_W);
	        //截取操作结束
	        
	        Map<String, Object> params = parseRequet(request);
	        String id = (String) params.get("id");
	        if(StringUtils.isNotEmpty(id)){
				HashMap<String, Object> obj;
				obj = (HashMap<String, Object>) getData(id);
				if(obj != null){
					params.put("headpic", bytecut);
					obj.putAll(params);
					id = saveData(obj);
					HashMap<String, Object> user = (HashMap<String, Object>)getData(id);
					request.getSession().setAttribute("user", user);
					return id;
				}
			}
		}
        return null;
	}

	public Map<String, Object> getUser(Map<String, Object> obj){
		return userDao.getUser(obj);
	}
	
	public Map<String, Object> getUser(String username) {
		return userDao.getUser(username);
	}

	private Map<String, Object> parseRequet(HttpServletRequest request){
		Map<String, Object> params = Maps.newHashMap();
		params.put("username", request.getParameter("username"));
		params.put("id", request.getParameter("userid"));
		params.put("birthday", request.getParameter("birthday"));
		params.put("idCard", request.getParameter("idCard"));
		params.put("address", request.getParameter("address"));
		params.put("telephone", request.getParameter("telephone"));
		return params;
	}
	
	public void saveUserLoginInfo(String username, HttpServletRequest request, String loginState, String time){
		//将当前登录时间和ip地址存入数据库
		String ip = IPUtil.getRemortIP(request);
		
		Map<String, Object> logininfo = Maps.newHashMap();
		logininfo.put("logintime", time);
		logininfo.put("loginip", ip);
		logininfo.put("username", username);
		logininfo.put("sessionId", request.getSession().getId());
		logininfo.put("loginState", loginState);
		userDao.saveUserLoginInfo(logininfo);
	}
	
	public boolean isLogin(String username, String startTime, String endTime) {
		List<Map<String, Object>> logList = userDao.getLoginfo(username, startTime, endTime);
		if (logList.size() >= 5) {
			return false;
		}
		return true;
	}
}
