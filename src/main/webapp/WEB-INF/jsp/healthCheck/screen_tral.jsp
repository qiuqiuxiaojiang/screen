<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<style>
<!--
ul,li{
        list-style:none;
        padding: 0;
        margin: 0;
    }
    .main{
        width: 100%;
        padding:1% 2% 2%;
    }
    .panel{
        width: 60%;
        margin-left: 100px;
    }
    .dateT{
        position: absolute;
        display: inline-block;
        left: -150px;
        margin-top: 38px;
        font-size: 16px;
     }
    /*纵向时间轴*/
    .time-vertical {
        list-style-type: none;
        border-left: 1px solid #dddddd;
        min-height: 20px;
        height: auto;
        float: left;
        width: 85%;
        margin-left: 25%;
        position: relative;

    }
    .time-vertical li {
        min-height: 100px;
        height: auto;
        position: relative;
        margin-top: 40px;
    }


    .time-vertical li>div>span {
        position: absolute;
        top: 35px;
        left: -13px;
        width: 25px;
        height: 25px;
        border: 2px solid #bce8f1;
		border-radius: 50%;
		background:#337ab7;
    }

-->
</style>
<title>筛查轨迹</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/bootstrap.min.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
    
	<script type="text/javascript">
	</script>
</head>
<body>
<div class="main">
    <ul class="time-vertical">
    <%-- <c:if test="${!empty healthchecks}">
    	<c:forEach items="${healthchecks }" var="hc">
       <li>
        <p class="dateT">${hc.checkDate }</p>
        <div>
           <span></span>
           <div class="panel panel-info">
               <div class="panel-heading">
                   <h3 class="panel-title"><a href='${ctx}/manage/hc/showHealthCheckUI.htm?id=${hc.id }' title='随访信息'>随访信息</a></h3>
               </div>
               <div class="panel-body">
                   <div class="row">
                       <div class="col-md-8">
                      	<b>人群分类结果：</b><span>${hc.classifyResult }</span>
                       </div>
                    </div>
               </div>
           </div>
       </div>
    </li>
    </c:forEach>
    </c:if> --%>
   <%--  <c:if test="${healthcheckDetail != null}">
    	<li>
        <p class="dateT">${healthcheckDetail.ctime }</p>
        <div>
           <span></span>
           <div class="panel panel-info">
               <div class="panel-heading">
                   <h3 class="panel-title"><a href='${ctx}/manage/hc/showHealthCheckDetailUI.htm?id=${healthcheckDetail.id }' title='精筛信息'>精筛信息</a></h3>
               </div>
               <div class="panel-body">
               		<div class="row">
                       <div class="col-md-8">
                      <b>精筛结果人群分类：</b><span>${healthcheckDetail.classifyResultJs }</span>
                       </div>
                    </div>
               </div>
           </div>
       </div>
    	</li>
    	</c:if> --%>
    	<c:if test="${!empty healthchecks}">
    	<c:forEach items="${healthchecks }" var="hc">
       <li>
        <p class="dateT">${hc.createDate }</p>
        <div>
           <span></span>
           <div class="panel panel-info">
               <div class="panel-heading">
               	<c:if test="${hc.title == '1'}">
                  <h3 class="panel-title"><a href='${ctx}/manage/hc/showHealthCheckUI.htm?id=${hc.id }' title='${hc.str }'>${hc.str }</a></h3>
                  </c:if>
                  <c:if test="${hc.title == '2'}">
                  <h3 class="panel-title"><a href='${ctx}/manage/hc/showHealthCheckDetailUI.htm?id=${hc.id }' title='${hc.str }'>${hc.str }</a></h3>
                  </c:if>
                  <c:if test="${hc.title == '3'}">
                  <h3 class="panel-title"><a href='${ctx}/gene/showGene.htm?uniqueId=${hc.id }' title='${hc.str }'>${hc.str }</a></h3>
                  </c:if>
               </div>
               <div class="panel-body">
                   <div class="row">
                       <div class="col-md-8">
                       <c:if test="${hc.title == '1' || hc.title == '2'}">
	                  	<b>人群分类结果：</b>
	                  </c:if>
                      	<span><c:if test="${hc.classifyResult != null && hc.classifyResult!='' && hc.classifyResult != 'null'}">${hc.classifyResult }</c:if></span>
                       </div>
                    </div>
               </div>
           </div>
       </div>
    </li>
    </c:forEach>
    </c:if>
        <li>
            <p class="dateT">${customer.recordDate }</p>
            <div>
                <span></span>
                <div class="panel panel-info">
                    <div class="panel-heading">
                        <h3 class="panel-title"><a href='${ctx}/manage/hc/showDocument.htm?uniqueId=${customer.uniqueId }' title='建档信息'>建档信息</a></h3>
                    </div>
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-md-4">
                               <span>${customer.name }，${customer.gender }，${customer.age }岁</span>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                疾病史：<span>${customer.haveDisease == null || customer.haveDisease == "否" ? "否" : customer.disease }</span>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                家族病史：<span>${customer.familyHistory == null || customer.familyHistory == "否" ? "否" : customer.familyDisease }</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </li>
    </ul>
</div>
</body>
</html>