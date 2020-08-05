<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%   
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;   
%>
<!DOCTYPE html>
<html lang="en">
	<head>
	    <meta charset="UTF-8">
	    <title>社区健康筛查数据采集与管理系统</title>
	</head>
	<c:set var="ctx" value="${pageContext.request.contextPath}" />
	<c:set var="ctx_static" value="${ctx}/static" />
	<style>
	    div,a,p{
	        padding: 0;
	        margin: 0;
	    }
	    h1{
	        font-size: 4.2em;
	        color: #ffffff;
	        font-family: "Microsoft Yahei";
	        letter-spacing: .2em;
	        margin-left: 12%;
	    }
	    .main{
	        top: 0;
	        bottom: 0;
	        left: 0;
	        right: 0;
	        width: 100%;
	        position: absolute;
	        height: 100%;
	        background: url("${ctx_static}/img/bg.png");
	        background-size: 100% 100%;
	    }
	    .content{
	        width: 25%;
	        height: 45%;
	        background: url("${ctx_static}/img/bg1.png");
	        background-size: 100% 100%;
	        position: absolute;
	        right: 10%;
	        top: 30%;
	    }
	    .content p{
	        margin-top: 13%; 
	    }
	    .name,.password{
	        display: inline-block;
	        width: 30%;
	        text-align: right;
	        color: #ffffff;
	        font-size: 1.0em;
	    }
	    .name1,.password1{
	        display: inline-block;
	        width: 55%;
	        margin-right: 10%;
	        height: 2.8em;
	        padding: 0 1%;
	        border: none;
	    }
	    .content p button{
	        text-decoration: none;
	        display: block;
	        width: 48%;
	        margin:0 27%;
	        height: 2.7em;
	        background: #c0e1fe;
	        color: #0a4277;
	        text-align: center;
	        line-height: 2.7em;
	        font-size: 1.1em;
	        border: none;
	    }
	</style>
	<body>
		<div class="main">
		    <h1>社区健康筛查数据采集与管理系统</h1>
			<div class="content">
			 	<form id="loginForm" class="form-horizontal" role="form" action="<c:url value='${basePath }/j_spring_security_check' />" method="post">
				    <input type="hidden" value="pc" name="loginType">
				    <p><label class="name">用&nbsp;户&nbsp;名：</label><input class="name1" type="text" name="username" id="username" value="${sessionScope['SPRING_SECURITY_LAST_USERNAME']}" placeholder="请输入用户名"/></p>
				    <p style="margin-top: 6%"><label class="password">密&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;码：</label><input class="password1" name="password" id="password" type="password" placeholder="请输入密码"/></p>
				    <p style="margin-top: 6%">
					    <label class="name">筛查区域：</label>
					    <select class="name1" name="district">
					    	<option value="">--请选择--</option>
					    	<c:forEach var="district" items="${districts}">
					    		<option value="${district.district}">${district.district}</option>
					    	</c:forEach>
					    </select>
				    </p>
				    
				    <p style="margin-top: 6%">
					    <label class="name">筛查地点：</label>
					    <input class="name1" name="checkPlace" id="checkPlace" type="text" placeholder="请输入筛查地址"/>
				    </p>
				    
				    <div style="margin-left: 30%;margin-top: 2%;color: red;">${SPRING_SECURITY_LAST_EXCEPTION.message}</div>
				    <p style="margin-top:8%;"><button type="submit" id="submitForm">登&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;录</button></p>
				</form>
			</div>
		</div>
	</body>
	
</html>
