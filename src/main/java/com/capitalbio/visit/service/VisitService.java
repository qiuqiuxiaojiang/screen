package com.capitalbio.visit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.service.BaseService;
import com.capitalbio.visit.dao.VisitDAO;

/**
 * 随访Service
 * 
 * @author wdong
 *
 */
@Service
public class VisitService extends BaseService {
	@Autowired VisitDAO visitDAO;
	@Override
	public MongoBaseDAO getMongoBaseDAO() {
		return visitDAO;
	}

	@Override
	public String getCollName() {
		return "visit";
	}
	

}
