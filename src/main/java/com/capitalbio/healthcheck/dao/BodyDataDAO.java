package com.capitalbio.healthcheck.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;

@Repository
public class BodyDataDAO extends MongoBaseDAO {
	Logger logger = Logger.getLogger(this.getClass());
}
