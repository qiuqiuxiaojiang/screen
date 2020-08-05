/**
 * Copyright (c) 2005-2010 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: Page.java 1183 2010-08-28 08:05:49Z calvinxiu $
 */
package com.capitalbio.common.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;


/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * 
 * 注意所有序号从1开始.
 * 
 * @author calvin
 */
@SuppressWarnings("rawtypes")
public class Page {
	
//	<input type="hidden" name="pageNum" value="1" />
//	<input type="hidden" name="numPerPage" value="${model.numPerPage}" />
//	<input type="hidden" name="orderField" value="${param.orderField}" />
//	<input type="hidden" name="orderDirection" value="${param.orderDirection}" />
	/**
	 * 分页默认变量
	 */
	public final static String PAGE_DATA = "pageData"; // 默认page名称
	public static int PAGE_COUNT = 20;
	public static String ORDER_BY = "id";
	
	//-- 公共变量 --//
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	//-- 分页参数 --//
	/**
	 * 第几页
	 */
	protected int pageNum = 1;
	/**
	 * 每页显示多少条
	 */
	protected int numPerPage = PAGE_COUNT;
	/**
	 * 排序字段
	 */
	protected String orderField = ORDER_BY; // 默认按id降序排序
	/**
	 * 排序方向
	 */
	protected String orderDirection = DESC;
	/**
	 * 是否自动计算记录总数
	 */
	protected boolean autoCount = true;

	//-- 返回结果 --//
	/**
	 * 记录结果
	 */
	protected List result = new ArrayList();
	/**
	 * 记录总条数
	 */
	protected long totalCount = -1;

	//-- 构造函数 --//
	public Page() {
	}

	public Page(int pageSize) {
		this.numPerPage = pageSize;
	}

	//-- 分页参数访问函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNum(final int pageNum) {
		this.pageNum = pageNum;
		if (pageNum < 1) {
			this.pageNum = 1;
		}
	}

	/**
	 * 返回Page对象自身的setPageNo函数,可用于连续设置。
	 */
	public Page pageNum(final int thePageNum) {
		setPageNum(thePageNum);
		return this;
	}

	/**
	 * 获得每页的记录数量, 默认为-1.
	 */
	public int getNumPerPage() {
		return numPerPage;
	}

	/**
	 * 设置每页的记录数量.
	 */
	public void setNumPerPage(final int numPerPage) {
		this.numPerPage = numPerPage;
	}

	/**
	 * 返回Page对象自身的setPageSize函数,可用于连续设置。
	 */
	public Page numPerPage(final int theNumPerPage) {
		setNumPerPage(theNumPerPage);
		return this;
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从0开始.
	 */
	public int getFirst() {
		return ((pageNum - 1) * numPerPage);
	}

	/**
	 * 获得排序字段,无默认值. 多个排序字段时用','分隔.
	 */
	public String getOrderField() {
		return orderField;
	}

	/**
	 * 设置排序字段,多个排序字段时用','分隔.
	 */
	public void setOrderField(final String orderField) {
		this.orderField = StringUtils.isNotBlank(orderField) ? orderField : ORDER_BY;
	}

	/**
	 * 返回Page对象自身的setOrderBy函数,可用于连续设置。
	 */
	public Page orderField(final String theOrderField) {
		setOrderField(theOrderField);
		return this;
	}

	/**
	 * 获得排序方向, 无默认值.
	 */
	public String getOrderDirection() {
		return orderDirection;
	}

	/**
	 * 设置排序方式向.
	 * 
	 * @param orderDirection 可选值为desc或asc,多个排序字段时用','分隔.
	 */
	public void setOrderDirection(String orderDirection) {
		orderDirection = StringUtils.isNotBlank(orderDirection)? orderDirection : DESC;
		String lowcaseOrder = StringUtils.lowerCase(orderDirection);

		//检查order字符串的合法值
		String[] orders = StringUtils.split(lowcaseOrder, ',');
		for (String orderStr : orders) {
			if (!StringUtils.equals(DESC, orderStr) && !StringUtils.equals(ASC, orderStr)) {
				throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
			}
		}

		this.orderDirection = lowcaseOrder;
	}

	/**
	 * 返回Page对象自身的setOrder函数,可用于连续设置。
	 */
	public Page orderDirection(final String theOrderDirection) {
		setOrderDirection(theOrderDirection);
		return this;
	}

	/**
	 * 是否已设置排序字段,无默认值.
	 */
	public boolean isOrderBySetted() {
		return (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(orderDirection));
	}

	/**
	 * 获得查询对象时是否先自动执行count查询获取总记录数, 默认为false.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}

	/**
	 * 设置查询对象时是否自动先执行count查询获取总记录数.
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}

	/**
	 * 返回Page对象自身的setAutoCount函数,可用于连续设置。
	 */
	public Page autoCount(final boolean theAutoCount) {
		setAutoCount(theAutoCount);
		return this;
	}

	//-- 访问查询结果函数 --//

	/**
	 * 获得页内的记录列表.
	 */
	public List getResult() {
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final List result) {
		this.result = result;
	}
	
	public Page result(final List result) {
		setResult(result);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public Page addResult(final List result) {
		if(null==getResult()) setResult(new ArrayList());
		getResult().addAll(result);
		return this;
	}

	/**
	 * 获得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}
	
	public Page totalCount(final long totalCount) {
		setTotalCount(totalCount);
		return this;
	}
	
	public Page addTotalCount(final long totalCount) {
		setTotalCount(getTotalCount() + totalCount);
		return this;
	}
	
	public Page addResultAndTotalCount(Page page) {
		addResult(page.getResult());
		addTotalCount(page.getTotalCount());
		return this;
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {
		if (totalCount < 0) {
			return -1;
		}

		long count = totalCount / numPerPage;
		if (totalCount % numPerPage > 0) {
			count++;
		}
		return count;
	}

	/**
	 * 是否还有下一页.
	 */
	public boolean isHasNext() {
		return (pageNum + 1 <= getTotalPages());
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPage() {
		if (isHasNext()) {
			return pageNum + 1;
		} else {
			return pageNum;
		}
	}

	/**
	 * 是否还有上一页.
	 */
	public boolean isHasPre() {
		return (pageNum - 1 >= 1);
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrePage() {
		if (isHasPre()) {
			return pageNum - 1;
		} else {
			return pageNum;
		}
	}
	
	/**
	 * @return 当前页
	 */
	public int getCurrentPage(){
		return pageNum;	
	}
	
	/**
	 * 页面底部显示多少可点击页标
	 */
	public int pageNumShown = 10;

	public int getPageNumShown() {
		return pageNumShown;
	}

	public void setPageNumShown(int pageNumShown) {
		this.pageNumShown = pageNumShown;
	}
	
	public List<Integer> getNumPerPageList(){
		return Lists.newArrayList(5,10,20,50,100,200);
	}
	
	/**
	 * targetType: navTab或dialog，用来标记是navTab上的分页还是dialog上的分页
	 
	private String targetType = "navTab";

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	*/
	
}
