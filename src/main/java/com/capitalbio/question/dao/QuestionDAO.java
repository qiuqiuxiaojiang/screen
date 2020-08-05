package com.capitalbio.question.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.capitalbio.common.dao.MongoBaseDAO;

@Repository
public class QuestionDAO extends MongoBaseDAO {
	Logger logger = Logger.getLogger(this.getClass());
}
