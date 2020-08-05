<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="/_header.jsp" %>
<html>
<head>
<title>用户列表</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
</head>
<body>
	<div class="container-fluid">
    <!-- 头部导航自动加载 -->
    <header id="header"></header>
	<!-- 内容区域 -->
    <section>
        <div class="row">
            <!-- 左侧菜单自动加载 -->
            <div id="leftpanelside"></div>
            <div id="mainpanelside">
                <div id="maincontent" class="col-lg-10 col-md-10 col-sm-9 col-xs-12">
                <!-- 以下这是页面需要编辑的区域 -->
                	 <div class="row" >
                          	<h1 >404,您访问的页面出错！</h1>
                	</div>
                <!-- 以下这是页面需要编辑的区域 -->
                </div>
            </div>
		</div>
    </section>
   </div>
</body>
</html>