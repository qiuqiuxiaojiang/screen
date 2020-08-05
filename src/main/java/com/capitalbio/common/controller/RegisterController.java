package com.capitalbio.common.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.capitalbio.auth.service.AuthService;
import com.capitalbio.common.service.LoginService;
import com.capitalbio.common.service.RegistService;
import com.capitalbio.common.service.UserService;
import com.capitalbio.common.util.PropertyUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("register")
public class RegisterController {
	@Autowired private RegistService registService;
	@Autowired private LoginService loginService;
	@Autowired private UserService userService;
	@Autowired private AuthService authService;
	
	@RequestMapping(value="toRegist",method = RequestMethod.GET)
	public String toRegist(){
		return "register";
	}
	
	@RequestMapping(value="regist",method = RequestMethod.POST)
	public String regist(HttpServletRequest request,RedirectAttributes redirectAttributes){
		String id = registService.regist(request);
		if(null != id && !id.equals("")){
			return "registSuccess";
		}
		return "register";
	}
	
	@RequestMapping(value="checkUser",method = RequestMethod.GET)
	@ResponseBody
	public void checkUser(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.reset();
		response.setContentType("text/html;charset=UTF-8");
		String username = request.getParameter("username");
//		JSONObject jsonObject = get(PropertyUtils.getProperty("auth.url") + "/dm/isValidUsername", username);
		JSONObject jsonObject = authService.isValidUsername(username);
		String code = jsonObject.getString("code");
		if (code.equals("0")) {
			String data = jsonObject.getString("data");
			JSONObject jsonData = JSON.parseObject(data);
			String userExists = jsonData.getString("userExists");
			if (userExists.equals("2")) {
				response.getWriter().print(true); 
			}
		}
		response.getWriter().print(false);
	}
	
	
	public JSONObject get(String url, String username){
		List<NameValuePair> params = Lists.newArrayList();
		params.add(new BasicNameValuePair("username", username));
		String str = "";
		try {
			//转换为键值对
			str = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
			System.out.println(str);
			//创建Get请求
			HttpGet httpGet = new HttpGet(url+"?"+str);
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity());
				return JSONObject.parseObject(result);
			}
		} catch (Exception e) {
		} 
		return null;
	}
}
