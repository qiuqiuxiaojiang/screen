package com.capitalbio.common.service;

import java.io.File;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.capitalbio.cbos.FileManager;
import com.capitalbio.common.util.PropertyUtils;

@Service
public class FileManageService {
	private FileManager fileManager;
	public void deleteFile(String fileId) {
		if (fileManager == null) {
			fileManager = FileManager.getInstance(PropertyUtils.getProperties());
		}
		fileManager.deleteFile(fileId);
	}
	
	public void downFile(File file, String fileId) throws Exception{
		if (fileManager == null) {
			fileManager = FileManager.getInstance(PropertyUtils.getProperties());
		}
		fileManager.downFile(file, fileId);
	}
	
	public String uploadFile(File file, String fileId) throws Exception{
		if (fileManager == null) {
			fileManager = FileManager.getInstance(PropertyUtils.getProperties());
		}
		return fileManager.uploadFile(file, fileId);
	}
	
	public void downStream(OutputStream os, String fileId) throws Exception{
		if (fileManager == null) {
			fileManager = FileManager.getInstance(PropertyUtils.getProperties());
		}
		fileManager.downStream(os, fileId);
	}
	public String getFileUrl(String fileId) throws Exception{
		if (fileManager == null) {
			fileManager = FileManager.getInstance(PropertyUtils.getProperties());
		}
		return fileManager.getSignedUrl(fileId);
	}
}
