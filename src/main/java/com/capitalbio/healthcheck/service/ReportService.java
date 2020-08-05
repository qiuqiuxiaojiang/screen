package com.capitalbio.healthcheck.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.healthcheck.dao.ReportDAO;

@Service
public class ReportService extends BaseService{
	@Autowired private ReportDAO reportDAO;

	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return reportDAO;
	}

	@Override
	public String getCollName() {
		return "report";
	}

	public void regenerate() {
		reportDAO.regenerate();
	}
	
	public void processReport(Map<String,Object> data) {
		reportDAO.processReport(data);
	}
	
	public void processEyeReport(Map<String,Object> eyeRecord) {
		reportDAO.processEyeReport(eyeRecord);
	}
}
