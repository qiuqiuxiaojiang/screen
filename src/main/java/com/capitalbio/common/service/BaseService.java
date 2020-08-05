package com.capitalbio.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.capitalbio.common.dao.MongoBaseDAO;
import com.capitalbio.common.model.Page;

@Service
public abstract class BaseService {
	public abstract MongoBaseDAO  getMongoBaseDAO();
	public abstract String getCollName();
	
	/**
	 * 分页查找数据
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findPage(Page page) throws Exception{
		Assert.notNull(page, "page不能为空");
		if (page.isAutoCount()) {
			long totalCount = getMongoBaseDAO().count(getCollName());
			page.setTotalCount(totalCount);
		}
		return getMongoBaseDAO().queryPage(getCollName(), page);
	}
	
	/**
	 * 获取全部
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findAll() throws Exception{
		return getMongoBaseDAO().queryAll(getCollName());
	}
	
	/**
	 * 获取全部
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findAll(String collName) throws Exception{
		return getMongoBaseDAO().queryAll(collName);
	}

	/**
	 * 根据ID获得数据
	 * @param id
	 * @return
	 */
	public Map<String,Object> getData( String id) {
		return getMongoBaseDAO().getData(getCollName(), id);
	}

	/**
	 * 根据ID获得数据
	 * @param collName
	 * @param id
	 * @return
	 */
	public Map<String,Object> getData(String collName, String id) {
		return getMongoBaseDAO().getData(collName, id);
	}

	/**
	 * 获得数据总数
	 * @return
	 * @throws Exception
	 */
	public long count() throws Exception{
		return getMongoBaseDAO().count(getCollName());
	}
	
	/**
	 * 根据查询条件获得数据总数
	 * @param query
	 * @return
	 */
	public long count(Map<String,Object> query) {
		return getMongoBaseDAO().countByQuery(getCollName(), query);
	}
	
	/**
	 * 根据查询条件获得数据总数
	 * @param collName
	 * @param query
	 * @return
	 */
	public long count(String collName, Map<String,Object> query) {
		return getMongoBaseDAO().countByQuery(collName, query);
	}
	
	/**
	 * 保存数据
	 * @param map
	 * @return
	 */
	public String saveData( Map<String,Object> map) {
		return getMongoBaseDAO().saveData(getCollName(), map);
	}

	/**
	 * 保存数据
	 * @param collName
	 * @param map
	 * @return
	 */
	public String saveData(String collName, Map<String,Object> map) {
		return getMongoBaseDAO().saveData(collName, map);
	}
	
	/**
	 * 保存数据
	 * @param collName
	 * @param map
	 * @return
	 */
	public String saveData1(String collName, Map<String,Object> map) {
		return getMongoBaseDAO().saveData1(collName, map);
	}

	/**
	 *  根据查询条件获得第一条数据
	 * @param queryMap
	 * @return
	 */
	public Map<String,Object> getDataByQuery( Map<String,Object> queryMap) {
		return getMongoBaseDAO().getDataByQuery(getCollName(), queryMap);
	}

	/**
	 *  根据查询条件获得第一条数据
	 * @param queryMap
	 * @return
	 */
	public Map<String,Object> getDataByQuery(String collName, Map<String,Object> queryMap) {
		return getMongoBaseDAO().getDataByQuery(collName, queryMap);
	}

	/**
	 * 根据ID删除数据
	 * @param id
	 */
	public void deleteData(String id) {
		getMongoBaseDAO().deleteData(getCollName(), id);
	}
	
	/**
	 * 根据ID删除数据
	 * @param id
	 */
	public void deleteData(String collName, String id) {
		getMongoBaseDAO().deleteData(collName, id);
	}

	/**
	 * 获取一个新ID
	 * @return
	 */
	public String newId() {
		return getMongoBaseDAO().newId();
	}
	
	/**
	 * 根据查询条件获得数据
	 * @param queryMap
	 * @return
	 */
	public  List<Map<String,Object>> queryList(Map<String,Object> queryMap) {
		return getMongoBaseDAO().queryList(getCollName(), queryMap);
	}
	
	/**
	 * 根据查询条件获得数据
	 * @param queryMap
	 * @return
	 */
	public  List<Map<String,Object>> queryList(Map<String,Object> queryMap, String collName) {
		return getMongoBaseDAO().queryList(collName, queryMap);
	}
	
	public List<Map<String,Object>> queryList(String colName, Map<String,Object> queryMap, Map<String, Object> sortMap) {
		return getMongoBaseDAO().queryList(colName, queryMap, sortMap);
	}
	
	/**
	 * 根据查询条件删除数据
	 * 20151118 xiaoyanzhang
	 * @param map  传递参数map
	 */
	public void deleteDataByParams(Map<String, Object> map){
		getMongoBaseDAO().deleteDataByParams(getCollName(), map);
	}

	/**
	 * 根据查询条件和排序条件分页获得数据
	 * @param page
	 * @param query
	 * @param sortMap
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findPageBySort(Page page,Map<String,Object> query,Map<String,Object> sortMap) throws Exception{
		Assert.notNull(page, "page不能为空");
		if (page.isAutoCount()) {
			long totalCount = getMongoBaseDAO().countByQuery(getCollName(), query);
			page.setTotalCount(totalCount);
		}
		return getMongoBaseDAO().findPageBySort(getCollName(), page, query, sortMap);
	}
	
	/**
	 * 根据查询条件和排序条件分页获得数据
	 * @param page
	 * @param query
	 * @param sortMap
	 * @param collName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findPageBySort(Page page,Map<String,Object> query,Map<String,Object> sortMap, String collName) throws Exception{
		Assert.notNull(page, "page不能为空");
		if (page.isAutoCount()) {
			long totalCount = getMongoBaseDAO().countByQuery(collName, query);
			page.setTotalCount(totalCount);
		}
		return getMongoBaseDAO().findPageBySort(collName, page, query, sortMap);
	}

	/**
	 * 根据查询条件分页获得数据
	 * @param page
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> findPage(Page page,Map<String,Object> query) throws Exception{
		Assert.notNull(page, "page不能为空");
		if (page.isAutoCount()) {
			long totalCount = getMongoBaseDAO().count(getCollName());
			page.setTotalCount(totalCount);
		}
		return getMongoBaseDAO().findPageBySort(getCollName(), page, query, null);
	}
	
}
