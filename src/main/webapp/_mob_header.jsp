
<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="ctx_static" value="${ctx}/static" />
<c:set var="ctx_jquery" value="${ctx_static}/jquery" />
<c:set var="ctx_bootstrap" value="${ctx_static}/bootstrap-3.3.4-dist" />
<c:set var="ctx_highcharts" value="${ctx_static}/highcharts" />
<c:set var="ctx_js" value="${ctx_static}/js" />
<c:set var="debug" value="true"/>

<link rel="stylesheet" type="text/css" href="${ctx_bootstrap}/css/bootstrap.min.css"/>
<script type="text/javascript" src="${ctx_jquery}/jquery-1.11.2.js" ></script>
<script type="text/javascript" src="${ctx_bootstrap}/js/bootstrap.min.js"></script>


