package com.capitalbio.common.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.capitalbio.common.service.FileManageService;

@Controller
@RequestMapping("/files")
public class FilesManagerController{

	private Logger logger= LoggerFactory.getLogger(getClass());
	@Autowired FileManageService fileService;
	
	
	@RequestMapping("img/{id}")
	public void showImage(@PathVariable String id,HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.reset();
		//禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		OutputStream sos = response.getOutputStream();
		try {
			fileService.downStream(sos, id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("showImage error"+ e.getMessage());
		} finally {
			sos.flush();
			sos.close();
		}
		
	}

	@RequestMapping("img/eye/{id}")
	public void showEyeImage(@PathVariable String id,HttpServletRequest request,HttpServletResponse response) throws Exception{
		id = "eye/"+id;
		response.reset();
		//禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		OutputStream sos = response.getOutputStream();
		try {
			fileService.downStream(sos, id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("showImage error"+ e.getMessage());
		} finally {
			sos.flush();
			sos.close();
		}
		
	}
  	
}
