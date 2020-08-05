package com.capitalbio.eye.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.service.FileManageService;

@Service
@SuppressWarnings("unchecked")
public class EyeFileService {
	@Autowired FileManageService fileService;
	/**
	 * 删除OSS上的文件
	 * @param files
	 */
	public void deleteFile(Map<String,Object> eyeRecord) {
		if (eyeRecord == null || eyeRecord.size() <= 0) {
			return;
		}
		
		String pdfFileKey = (String)eyeRecord.get("pdfFile");
		String parsePdfFileKey = (String)eyeRecord.get("parsePdfFile");
		String zipFileKey = (String)eyeRecord.get("zipFile");
		
		if (!StringUtils.isEmpty(pdfFileKey)) {
			fileService.deleteFile(pdfFileKey);
		}
		if (!StringUtils.isEmpty(parsePdfFileKey)) {
			fileService.deleteFile(parsePdfFileKey);
		}
		if (!StringUtils.isEmpty(zipFileKey)) {
			fileService.deleteFile(zipFileKey);
		}
		
		String[] cols = new String[]{"lf","ll","lr","lu","ld","rf","rl","rr","ru","rd"};
		for (int i = 0; i < cols.length; i++) {
			String col = cols[i];
			List<Map<String,Object>> fileList = (List<Map<String, Object>>) eyeRecord.get(col);
			for (Map<String,Object> fileMap:fileList) {
				String fileId = (String)fileMap.get("fileId");
				if (StringUtils.isEmpty(fileId)) continue;
				fileService.deleteFile(fileId);
			}
		}
		
		Map<String,Object> parsedMap = (Map<String,Object>)eyeRecord.get("parsedMap");
		if (parsedMap != null && parsedMap.size() > 0) {
			for (String key : parsedMap.keySet()) {
				if (StringUtils.isEmpty(key) ) continue;
				
				String objectKey = (String)parsedMap.get(key);
				fileService.deleteFile(objectKey);
			}
		}
		
		//eyeImage
		Map<String,Object> eyeImageMap = (Map<String,Object>)eyeRecord.get("eyeImage");
		if (eyeImageMap != null && eyeImageMap.size() > 0) {
			for (String key : eyeImageMap.keySet()) {
				if (StringUtils.isEmpty(key) ) continue;
				
				String objectKey = (String)eyeImageMap.get(key);
				if (!StringUtils.isEmpty(objectKey)) {
					fileService.deleteFile(objectKey);
				}
			}
		}
		
		this.deleteResultList(eyeRecord);
	}
	private void deleteResultList(Map<String,Object> eyeRecord) {
		List<Map<String,Object>> resultList = (List<Map<String,Object>>) eyeRecord.get("resultList");
		if (resultList == null || resultList.size() <= 0) {
			return;
		}
		
		for (Map<String,Object> result : resultList) {
			if (result == null) continue;
			
			Map<String, Object> path1 = (Map<String, Object>)result.get("Path1");
			Map<String, Object> path2 = (Map<String, Object>)result.get("Path2");
			
			if (path1 != null && path1.size() > 0) {
				String fid1 = (String)path1.get("fileId");
				if (!StringUtils.isEmpty(fid1)) {
					fileService.deleteFile(fid1);
				}
			}
			if (path2 != null && path2.size() > 0) {
				String fid2 = (String)path2.get("fileId");
				if (!StringUtils.isEmpty(fid2)) {
					fileService.deleteFile(fid2);
				}
			}
		}
	}

}
