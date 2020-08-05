package com.capitalbio.healthcheck.dao;

import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;

@Repository
public class BodyCompositionDAO extends MongoBaseDAO{

	protected static Logger logger = Logger.getLogger(BodyCompositionDAO.class.getName());
}
