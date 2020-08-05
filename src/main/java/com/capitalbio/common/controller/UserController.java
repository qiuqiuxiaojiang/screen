package com.capitalbio.common.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.service.AuthService;
import com.capitalbio.auth.util.HttpUtils;
import com.capitalbio.common.log.ControllerLog;
import com.capitalbio.common.model.ImgHW;
import com.capitalbio.common.model.Message;
import com.capitalbio.common.model.Page;
import com.capitalbio.common.service.RoleService;
import com.capitalbio.common.service.UserService;
import com.capitalbio.common.util.DateUtil;
import com.capitalbio.common.util.HttpClient;
import com.capitalbio.common.util.JsonUtil;
import com.capitalbio.common.util.PropertyUtils;
import com.capitalbio.common.util.XssCleanUtil;
import com.google.common.collect.Maps;


@Controller
@RequestMapping("user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(getClass()); 
	@Autowired private UserService userService;
	@Autowired private RoleService roleService;
	@Autowired private AuthService authService;
	
	@Value("${auth.url}")
	private String url;
	
	@RequestMapping(value="/userlist")
	@ControllerLog
	public String listUser(HttpServletRequest request,ModelMap modelMap,Page pageData) throws Throwable{
		
		List<Map<String, Object>> userlist = userService.findAll();
		List<Map<String, Object>> roleList = roleService.findAll();
		modelMap.put("users", userlist);
		modelMap.put("roles", roleList);
		return "user/user_list";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="update/{id}",method = RequestMethod.GET)
	@ControllerLog
	public String updateUser(HttpServletRequest request, ModelMap modelMap,@PathVariable String id, Page pageData) throws Exception{
		Map<String, Object> user = userService.getData(id);
		List<Map<String, Object>> userRoles = (List<Map<String, Object>>) user.get("roles");
		
		String roleids = ",";
		if(null != userRoles && userRoles.size() > 0){
			for(Map<String, Object> role:userRoles){
				if (role.get("_id") != null) {
					roleids = roleids + role.get("_id").toString()+",";
				} else if (role.get("id") != null) {
					roleids = roleids + role.get("id")+",";
				}
			}
		}
		
		user.put("roles", roleids);
		List<Map<String, Object>> roles = roleService.findPage(pageData);
		request.setAttribute("roles", roles);
		request.setAttribute("user", user);
		return "user/user_edit";
	}
	
	@RequestMapping(value="edit")
	@ResponseBody
	@ControllerLog
	public Message editUser(HttpServletRequest request, ModelMap modelMap,Page pageData) throws Exception{
		List<Map<String, Object>> objList = new ArrayList<Map<String,Object>>();
		String roles = request.getParameter("roles");
		String id = request.getParameter("userid");
		
		if(StringUtils.isNotEmpty(roles)){
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
		
		if(StringUtils.isNotEmpty(id)){
			HashMap<String, Object> obj;
			try {
				obj = (HashMap<String, Object>) userService.getData(id);
				if(obj != null){
					obj.put("roles", objList);
					id = userService.saveData(obj);
					return Message.data(id);
				} 
			} catch (Exception e) {
				logger.error("save user error", e);
				return Message.error("save user error");
			}
			
		}
		return Message.error("data not found");
	}
	
	@RequestMapping(value="save")
	@ResponseBody
	@ControllerLog
	public JSONObject saveUser(HttpServletRequest request) throws Exception{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String roles = request.getParameter("roles");
		
		String url = PropertyUtils.getProperty("auth.url");
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("username", username);
		params.put("password", password);
		//JSONObject results = HttpClient.post(url + "/dm/saveStuffInfo", params);
		JSONObject results = authService.saveStuffInfo(params);
		if (results.getString("code").equals("0")) {
			List<Map<String, Object>> objList = new ArrayList<Map<String,Object>>();
			if(StringUtils.isNotEmpty(roles)){
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
			
			Map<String, Object> map = Maps.newHashMap();
			map.put("username", username);
			map.put("roles", objList);
			String id = userService.saveData(map);
		}
		return results;
	}
	
	@RequestMapping(value="delete/{id}",method=RequestMethod.POST)
	@ResponseBody
	@ControllerLog
	public Message deleteUser(HttpServletRequest request, ModelMap modelMap,Page pageData,@PathVariable String id) throws Exception{
		if(StringUtils.isNotEmpty(id)){
//			String username = (String) request.getSession().getAttribute("username");
			Map<String, Object> user = userService.getData(id);
			JSONObject jsonObject = authService.delStuffInfoByUsername(user.get("username").toString());
			if (jsonObject != null) {
				if (jsonObject.getString("code").equals("0")) {
					userService.deleteData(id);
					return Message.success();
				}
			}
		}
		return Message.error("delete user error");
	}
	
	
	//上传头像
	@RequestMapping(value = "/uploadHeadImage")
	@ResponseBody
	public Message uploadHeadImage(
            HttpServletRequest request,
            @RequestParam(value = "x") String x,
            @RequestParam(value = "y") String y,
            @RequestParam(value = "h") String h,
            @RequestParam(value = "w") String w,
            @RequestParam(value = "y_w") String y_w, // 等比压缩后图片宽
            @RequestParam(value = "y_h") String y_h, // 等比压缩后图片高
            @RequestParam(value = "imgFile") MultipartFile imageFile){
		
       // System.out.println("imgFIle:"+imageFile);
		
        try {
        	
        	String id = userService.uploadHeadImage(request, imageFile, x, y, h, w, y_h, y_w);
        	if(null != id && !"".equals(id)){
        		return Message.data(id);
        	}
        	
		} catch (Exception e) {
			logger.error("save image error", e);
			return Message.error("save image error");
		}
        
        return Message.error("data not found");
    }
	
	/**
	 * 上传头像原图获，取宽和高
	 * @param request
	 * @return 头像原图宽和高
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/getUploadImageHW", method = RequestMethod.POST)
	@ResponseBody
	public ImgHW getImgHeightAndWidth (@RequestParam MultipartFile[] imgFile, HttpServletRequest request, 
			HttpServletResponse response) throws JSONException {
		ImgHW img = new ImgHW();
		
		try {
			BufferedImage bi = ImageIO.read(imgFile[0].getInputStream());
			//System.out.println("img-H: " + bi.getHeight() + " img-W: " + bi.getWidth());
			img.setHeight(bi.getHeight());
			img.setWidth(bi.getWidth());
			img.setResult("success");
		} catch (Exception e) {
			img.setResult("error");
			logger.error("get image height and width error", e);
		}
		return img;
	}
	
	//用户修改个人基本信息
	@RequestMapping(value = "/updateUserInfo",method=RequestMethod.POST)
	@ResponseBody
	@ControllerLog
	private Message updateUserInfo(HttpServletRequest request){
		
		try {
			String id = userService.saveUser(request);
			if(null != id || !"".equals(id)){
				HashMap<String, Object> user = (HashMap<String, Object>) userService.getData(id);
				request.getSession().setAttribute("user", user);
				return Message.data(id);
			}
		} catch (Exception e) {
			logger.error("save user error", e);
			return Message.error("save user error");
		}
	  	return Message.error("user not found");
	}
	
	//jsp页面把byte[]转换为图片
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/imgByByte")
	private void imgByByte(HttpServletRequest request,HttpServletResponse response) throws Exception{
		//String userid = request.getParameter("userid");
		Map<String, Object> user = (Map<String, Object>) request.getSession().getAttribute("user");
		if(user != null){
			//Map<String, Object> user = userService.getData(userid);
			if(user != null){
				if(user.get("headpic") != null){
					InputStream buffin = new ByteArrayInputStream((byte[]) user.get("headpic"));
					BufferedImage img = ImageIO.read(buffin);
					// 禁止图像缓存。
					response.setHeader("Pragma", "no-cache");
					response.setHeader("Cache-Control", "no-cache");
					response.setDateHeader("Expires", 0);
					response.setContentType("jpg");
			
					// 将图像输出到Servlet输出流中。
					ServletOutputStream sos;
					sos = response.getOutputStream();
					ImageIO.write(img, "jpg", sos);
					sos.close();
				}
			}
			
		}
		
	}
	
	//修改密码页面
	@RequestMapping(value="/updatePass")
	private String updatePass(HttpServletRequest request){
		return "/user/password_edit";
	}
	
	@RequestMapping(value="savePass")
	@ResponseBody
	@ControllerLog
	private JSONObject savePass(HttpServletRequest request, ModelMap modelMap,Page pageData) throws Exception{
		String token = (String) request.getSession().getAttribute("token");
		String userId = (String) request.getSession().getAttribute("userId");
		String newPassword = request.getParameter("password");
		String oldPassword = request.getParameter("before_password");
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("oldPassword", oldPassword);
		map.put("newPassword", newPassword);
		map.put("token", token);
		map.put("userId", userId);
//		JSONObject jsonObject = HttpClient.put(PropertyUtils.getProperty("auth.url") + "/dm/updatePassword", map, token, userId);
		JSONObject jsonObject = updatePassword(map, token, userId);
		
		request.getSession().removeAttribute("token");
		request.getSession().removeAttribute("userId");
		request.getSession().removeAttribute("user");
		request.getSession().removeAttribute("username");
		
		return jsonObject;
	}
	
	public JSONObject updatePassword(Map<String, Object> params, String token, String userId) {
		String postUrl = PropertyUtils.getProperty("auth.url") +"/dm/updatePassword";
		try {
			HttpPut httpPut = new HttpPut(postUrl);
			String json = JsonUtil.mapToJson(params);
			StringEntity se = new StringEntity(json, "UTF-8");
			httpPut.setEntity(se);
			httpPut.addHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPut.addHeader("token", token);
			httpPut.addHeader("userId", userId);
			se.setContentType("text/json");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			
			HttpResponse response = HttpUtils.getClient().execute(httpPut);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
				System.out.println(result);
				return JSONObject.parseObject(result);
			} else {
				logger.debug("request error, HTTP code:" + code);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//修改密码判断输入的原密码是否正确
	@SuppressWarnings("unchecked")
	@RequestMapping(value="checkPwd",method=RequestMethod.GET)
	@ResponseBody
	private void checkPwd(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.reset();
		response.setContentType("text/html;charset=UTF-8");
		String before_password = request.getParameter("before_password");
		Map<String, Object> user = (Map<String, Object>) request.getSession().getAttribute("user");
		if(user != null){
			String password = (String) user.get("password");
			String pwd = new Md5PasswordEncoder().encodePassword(before_password, user.get("salt"));
			if(password.equals(pwd)){
				response.getWriter().print(true);
			}else{
				response.getWriter().print(false);
			}
		}
	}
	
	private Map<String, Object> parseRequet(HttpServletRequest request){
		Map<String, Object> params = Maps.newHashMap();
		params.put("username", request.getParameter("username"));
		params.put("id", request.getParameter("userid"));
		//params.put("birthday", request.getParameter("birthday"));
		params.put("idCard", request.getParameter("idCard"));
		
		String address = XssCleanUtil.xssClean(request.getParameter("address"));
		params.put("address", address);
		
		String telephone = XssCleanUtil.xssClean(request.getParameter("telephone"));
		params.put("telephone", telephone);
		
		params.put("roles", request.getParameter("roles"));
		return params;
	}
}
