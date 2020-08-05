package com.capitalbio.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalbio.auth.util.JwtUtil;
import com.capitalbio.cbos.FileManager;
import com.capitalbio.common.service.FileManageService;
import com.toolkit.redisClient.template.JedisTemplate;
import com.toolkit.redisClient.util.RedisUtils;

/**
 * 华为云文件处理Controller
 * @author wdong
 *
 */
@RestController
@RequestMapping("/obsfile")
public class ObsFileController {
	private static final Logger log = LoggerFactory.getLogger(ObsFileController.class);
	
	JedisTemplate template = RedisUtils.getTemplate();
	
	@Autowired
	FileManageService fileService;
	
	/**
	 * 通用下载请求
	 * 
	 * @param fileName 文件名称
	 * @param delete   是否删除
	 */
	@RequestMapping("image/{fileId}")
	public void imageDownload(@PathVariable String fileId,  HttpServletResponse response,
			HttpServletRequest request) {
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("image/jpeg");
			
			String signature = request.getParameter("Signature");
			String jwtSubject = JwtUtil.getJwtSubject(signature);
			if (template.exists("webSignature" + signature) &&  jwtSubject.equals("healthcheck")) {
				fileService.downStream(response.getOutputStream(), fileId);
			}
			
		} catch (Exception e) {
			log.error("下载文件失败", e);
		}
	}

}
