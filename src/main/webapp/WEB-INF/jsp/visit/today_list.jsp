<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>提醒列表</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx}/static/pagenation/pagenation.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<script src="${ctx}/static/pagenation/pagenation.js"></script>
	
	<style type="text/css">
		table td{
			text-align:center;
		}
		.pull-right {
		    margin-top: 2%;
		}
		.btn-primary {
		    color: #fff;
		    background-color: #42a0d2;
		    border: none!important;
		}
	</style>
</head>
<body>
	<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div class="panel panel-warning">
				<jsp:useBean id="now" class="java.util.Date" scope="page"/>
				<div class="panel-heading"><fmt:formatDate value="${now }"/></div>
				<div class="panel-body"  role="navigation">
					<ul class="nav nav-tabs">
						<li role="presentation" <c:if test="${remindType=='new' }">class="active"</c:if>><a data-toggle="tab" href="#newRemindTable">今日新增的随访计划</a></li>
						<li role="presentation" <c:if test="${remindType=='expire' }">class="active"</c:if>><a data-toggle="tab" href="#expireRemindTable">即将过期的随访计划</a></li>
						
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="newRemindTable">
						<table class="table table-bordered">
							<thead><tr><th>姓名</th><th>手机号</th><th>联系人电话</th><th>筛查地点</th><th>计划过期时间</th></tr></thead>
							<tbody>
							<c:forEach var="visitPlan" items="${newPlanList }" varStatus="index">
								<tr>
									<td>${visitPlan.customerName }</td>
									<td>${visitPlan.mobile }</td>
									<td>${visitPlan.contactPhone }</td>
									<td>${visitPlan.checkPlace }</td>
									<td><fmt:formatDate value="${visitPlan.expireDate }"/></td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
						</div>
						<div class="tab-pane" id="expireRemindTable">
						<table class="table table-striped">
							<thead><tr><th>姓名</th><th>手机号</th><th>联系人电话</th><th>筛查地点</th><th>计划过期时间</th></tr></thead>
							<tbody>
							<c:forEach var="visitPlan" items="${expirePlanList }" varStatus="index">
								<tr>
									<td>${visitPlan.customerName }</td>
									<td>${visitPlan.mobile }</td>
									<td>${visitPlan.contactPhone }</td>
									<td>${visitPlan.checkPlace }</td>
									<td><fmt:formatDate value="${visitPlan.expireDate }"/></td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
