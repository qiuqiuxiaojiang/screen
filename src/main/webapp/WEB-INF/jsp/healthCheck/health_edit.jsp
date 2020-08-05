<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp"%>
<head>
<title>添加初筛数据</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<script language="javascript" type="text/javascript" src="${ctx_static}/My97DatePicker/WdatePicker.js"></script>
<script src="${ctx_jquery }/jquery.form.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/static/js/component.js"></script>
<!-- 表单验证 -->
<script src="${ctx_jquery }/jquery.validate.js"></script>
<!-- 身份证、护照等验证 -->
<script src="${ctx_jquery }/card.js"></script>
<style type="text/css">
	    label.error{
	    	color:Red; 
			font-size:13px; 
	    }
	    #visit-error{
		    position: absolute;
		    right: 13%;
		    bottom: 0;
		}
		
		#nice-select input {
		    outline: none;
		    cursor: pointer;
		    width: 100%!important;
		}
		
		#nice-select {
		    position: relative;
		    width: 60%!important;
		    display: inline-block;
		}
		#nice-select ul {
		    display:none;
		    border: 1px solid #d5d5d5;
		    min-width: 100%;
		    width: auto;
		    position: absolute;
		    top: 34px;
		    overflow: hidden;
		    background-color: #fff;
		    max-height: 150px;
		    overflow-y: auto;
		    border-top: 0;
		    z-index: 10001;
		    padding: 0px!important;
		}
		#nice-select ul li {
		    height:30px;
		    line-height:2em;
		    overflow:hidden;
		    padding:0 10px;
		    cursor:pointer;
		}
		#nice-select ul li:hover {
		    background-color:#e0e0e0;
		}
    </style>
</head>
<body>
<div style="display:none">
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
</div>
	<div class="modal fade" id="myModal" style="z-index: 10000;"
		tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
		aria-hidden="true">
		<div class="modal-dialog" style="width: 900px;">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel">报告预览</h4>
				</div>
				<div class="modal-body" style="width: 100%;">
					<iframe id="printIframe"  width="100%" height="700"></iframe> 
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">
						返回</button>
				</div>
			</div>
		</div>
	</div>
	<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div>
				<div class="panel panel-warning">
					<form id="healthcheckInfo" role="form" method="post">
						<input type="hidden" id="dataId" name="id" value="${editEntity.customerId}" />
						<input type="hidden" name="uniqueId" id="uniqueId" value="${editEntity.uniqueId }">
						<input type="hidden" name="operation" id="operation" value="${operation}">
						<input type="hidden" name="primarykeyId" id="primarykeyId" value="${editEntity.id }">
						<input type="hidden" id="item" value="${item }">
						<input type="hidden" id="editflag" value="${editflag}">
					<div class="panel-body">
					
					<h4 class="block-title"><span>基础信息</span></h4>
					
						<div id="userInfo1">
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>身份证号</label>
								<div class="col-sm-8 controls">
									<input type="text" id="customerId" readonly class="form-control" name="customerId" value="${editEntity.customerId}"
										placeholder="请输入身份证号" onblur="computeAll();" style="width: 200px; padding: 7px 10px; display:inline-block;border: 1px solid #ccc; margin-right: 10px;">
									<input type="button" id="inputIdcardButton" class="btn btn-primary " value="手工录入" onclick="javascript:inputId()">
									<input type="button" id="inputQueryButton" class="btn btn-primary " style="display:none" value="查询" 
										onclick="javascript:queryByCustomerId()">
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel"><span style="color:red;">*</span>姓名</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="customerName" name="name" value="${editEntity.name}"
										placeholder="请输入用户名">
								</div>
							</div>
							
							<div class="form-group">
								<label for="gender" class="col-sm-4 control-lavel"><span style="color:red;">*</span>性别</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="gender" name="gender" value="${editEntity.gender}">

								</div>
							</div>
							
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>出生日期</label>
								<div class="col-sm-8 controls">
									<input type="text" id="birthday" class="form-control" name="birthday" value="${editEntity.birthday}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="age" class="col-sm-4 control-lavel"><span style="color:red;">*</span>年龄</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="age" name="age" value="${editEntity.age}"
										onblur="computeAll();">
								</div>
							</div>
							<div class="form-group">
								<label for="cardId" class="col-sm-4 control-lavel">手机号码</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="mobile" name="mobile" value="${editEntity.mobile}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否患有疾病</label>
								<div class="col-sm-8 controls">
									<c:if test="${editEntity.haveDisease=='是' }">
									<c:set var="diseaseShow" value="${editEntity.disease }"/>
									</c:if>
									<c:if test="${editEntity.haveDisease=='否' }">
									<c:set var="diseaseShow"  value="否"/>
									</c:if>
									
									<input type="text"  class="form-control" id="diseaseShow" value="${diseaseShow }"/>
									<input type="hidden" id="disease" name="disease" value="${editEntity.disease }"/>
									<input type="hidden" id="haveDisease" name="haveDisease" value="${editEntity.haveDisease }"/>
								</div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否有疾病家族史</label>
								<div class="col-sm-8 controls">
									<c:if test="${editEntity.familyHistory=='是' }">
									<c:set var="familyHistoryShow" value="${editEntity.familyDisease }"/>
									</c:if>
									<c:if test="${editEntity.familyHistory=='否' }">
									<c:set var="familyHistoryShow" value="否"/>
									</c:if>
									<input type="text"  class="form-control" id="familyHistoryShow" value="${familyHistoryShow }"/>
									<input type="hidden" id="familyHistory" name="familyHistory" value="${editEntity.familyHistory }"/>
									<input type="hidden" id="familyDisease" name="familyDisease" value="${editEntity.familyDisease }"/>
								</div>
							</div>
							
							<div class="form-group">
								<label for="checkDate" class="col-sm-4 control-lavel"><span style="color:red;">*</span>检测时间</label>
								<div class="col-sm-8 controls">
									<input class="Wdate" id="checkDate" name="checkDate" readonly type="text" value="${editEntity.checkDate}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
								</div>
							</div>
						</div>
							
						<div id="userInfo2" style="display: none;">
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel">身份证号</label>
								<div class="col-sm-8 controls" id="customerIdDiv"></div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">姓名</label>
								<div class="col-sm-8 controls" id="nameDiv"></div>
							</div>
							
							<div class="form-group">
								<label for="gender" class="col-sm-4 control-lavel">性别</label>
								<div class="col-sm-8 controls" id="genderDiv"></div>
							</div>
							
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel">出生日期</label>
								<div class="col-sm-8 controls" id="birthdayDiv"></div>
							</div>
							
							<div class="form-group">
								<label for="age" class="col-sm-4 control-lavel">年龄</label>
								<div class="col-sm-8 controls" id="ageDiv"></div>
							</div>
							<div class="form-group">
								<label for="cardId" class="col-sm-4 control-lavel">手机号码</label>
								<div class="col-sm-8 controls" id="mobileDiv"></div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">是否患有疾病</label>
								<div class="col-sm-8 controls" id="diseaseShowDiv"></div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">是否有疾病家族史</label>
								<div class="col-sm-8 controls" id="familyHistoryShowDiv"></div>
							</div>
							
							<div class="form-group">
								<label for="checkDate" class="col-sm-4 control-lavel"><span style="color:red;">*</span>检测时间</label>
								<div class="col-sm-8 controls" id="checkDateDiv"></div>
							</div>
						</div>
						<br>
						<div class="form-group" style="display: none;" id="checkDatesDiv2">
							<label for="gender" class="col-sm-4 control-lavel"><span style="color:red;">*</span>以往检测日期</label>
							<div class="col-sm-8 controls" id="checkDates">
							</div>
						</div>
						
						<div id="csInfo" style="display: none;">
							<h4 class="block-title"><span>生理指标</span></h4>
							
	
							<div class="form-group">
								<label for="height" class="col-sm-4 control-lavel">身高(cm)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="height" name="height" value="${editEntity.height}" onblur="analyseBMI();">
								</div>
							</div>
							<div class="form-group">
								<label for="weight" class="col-sm-4 control-lavel">体重(kg)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="weight" name="weight" value="${editEntity.weight}" onblur="analyseBMI();">
								</div>
							</div>
							
							<div class="form-group">
								<label for="oxygen" class="col-sm-4 control-lavel">体质指数</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="BMI" name="BMI" value="${editEntity.BMI}" readonly="readonly"
										onblur="computeAll();">
								</div>
							</div>
							<div class="form-group">
								<label for="waistline" class="col-sm-4 control-lavel">腰围(cm)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="waistline"
										name="waistline" value="${editEntity.waistline}" onblur="computeAll();">
								</div>
							</div>
							
							<div class="form-group">
								<label for="highPressure" class="col-sm-4 control-lavel">收缩压(mmHg)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="highPressure" value="${editEntity.highPressure}"
										id="highPressure" onblur="computeAll();">
									<span id="highPressureUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="highPressureDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="highPressureState" id="highPressureState" value="${editEntity.highPressureState}">
								</div>
							</div>
	
							<div class="form-group">
								<label for="lowPressure" class="col-sm-4 control-lavel">舒张压(mmHg)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="lowPressure" value="${editEntity.lowPressure}"
										id="lowPressure" onblur="computeAll();">
									<span id="lowPressureUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="lowPressureDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="lowPressureState" id="lowPressureState" value="${editEntity.lowPressureState}">
								</div>
							</div>
							<div class="form-group">
								<label for="pulse" class="col-sm-4 control-lavel">脉率(次/min)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="pulse" name="pulse" value="${editEntity.pulse}">
								</div>
							</div>
							<div class="form-group">
								<label for="temperature" class="col-sm-4 control-lavel">体温(°C)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="temperature" name="temperature" value="${editEntity.temperature}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="oxygen" class="col-sm-4 control-lavel">血氧(%)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="oxygen" name="oxygen" value="${editEntity.oxygen}">
								</div>
							</div>
							<div class="form-group">
								<label for="hipline" class="col-sm-4 control-lavel">臀围(cm)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="hipline"
										id="hipline" value="${editEntity.hipline}" onblur="analyseyaotunbi();">
								</div>
							</div>
	
							<div class="form-group">
								<label for="WHR" class="col-sm-4 control-lavel">腰臀比</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="WHR"
										id="WHR" value="${editEntity.WHR}" readonly="readonly">
								</div>
							</div>
							<div class="form-group">
								<label for="fatContent" class="col-sm-4 control-lavel">体脂率(%)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="fatContent" name="fatContent" value="${editEntity.fatContent}">
								</div>
							</div>
							<h4 class="block-title"><span>生化指标</span></h4>
							
	
							<div class="form-group">
								<label for="bloodGlucose" class="col-sm-4 control-lavel">空腹血糖(mmol/L)</label>
								<div class="col-sm-8 controls" style="position:relative;">
									<input type="text" class="form-control" id="bloodGlucose" value="${editEntity.bloodGlucose}"
										name="bloodGlucose" onblur="computeAll();">
										<span id="bloodGlucoseUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
										<span id="bloodGlucoseDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="bloodGlucoseState" id="bloodGlucoseState" value="${editEntity.bloodGlucoseState}">
								</div>
								
							</div>
	
							<div class="form-group">
								<label for="bloodGlucose2h" class="col-sm-4 control-lavel">餐后两小时血糖(mmol/L)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="bloodGlucose2h" value="${editEntity.bloodGlucose2h}"
										name="bloodGlucose2h" onblur="computeAll();">
									<span id="bloodGlucose2hUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="bloodGlucose2hDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="bloodGlucose2hState" id="bloodGlucose2hState" value="${editEntity.bloodGlucose2hState}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="bloodGlucose" class="col-sm-4 control-lavel">随机血糖(mmol/L)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="bloodGlucoseRandom" value="${editEntity.bloodGlucoseRandom}"
										name="bloodGlucoseRandom" onblur="computeAll();">
									<span id="bloodGlucoseRandomUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="bloodGlucoseRandomDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="bloodGlucoseRandomState" id="bloodGlucoseRandomState" value="${editEntity.bloodGlucoseRandomState}">
								</div>
							</div>
							<div class="form-group">
								<label for="tc" class="col-sm-4 control-lavel">总胆固醇(mmol/L)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="tc" id="tc" value="${editEntity.tc}"
										onblur="computeAll();">
									<span id="tcUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="tcDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="tcState" id="tcState" value="${editEntity.tcState}">
								</div>
							</div>
							<div class="form-group">
								<label for="tg" class="col-sm-4 control-lavel">甘油三脂(mmol/L)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="tg" id="tg" value="${editEntity.tg}"
										onblur="computeAll();">
									<span id="tgUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="tgDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="tgState" id="tgState" value="${editEntity.tgState}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="ldl" class="col-sm-4 control-lavel">低密度脂蛋白胆固醇(mmol/L)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="ldl" id="ldl" value="${editEntity.ldl}" 
									onblur="computeAll();">
									<span id="ldlUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="ldlDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="ldlState" id="ldlState" value="${editEntity.ldlState}">
								</div>
							</div>
							<div class="form-group">
								<label for="hdl" class="col-sm-4 control-lavel">高密度脂蛋白胆固醇(mmol/L)</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="hdl" id="hdl" value="${editEntity.hdl}"
										onblur="computeAll();">
									<span id="hdlUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<span id="hdlDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:15%;top:10px;color:red;display:none;"></span>
									<input type="hidden" name="hdlState" id="hdlState" value="${editEntity.hdlState}">
								</div>
							</div>
	
							<h4 class="block-title"><span>初筛结果</span></h4>
							
							<div class="form-group">
								<label for="tizhi" class="col-sm-4 control-lavel">中医体质分类代码</label>
								<div class="col-sm-8 controls" id="nice-select">
									<input type="text" class="form-control" id="tizhi" name="tizhi" value="${editEntity.tizhi}" oninput="searchList(this.value)">
									 <ul>
									 </ul>
								</div>
								
							</div>
							
							<!-- <div class="form-group">
				                <label>确诊医院/机构</label>
				                <div class="nice-select">
				                    <input class="form-control" id="customerId" type="text" oninput="searchList(this.value)">
				                    <ul>
				                        <li>Java--我的--最爱</li>
				                        <li>PHP--很棒的wo</li>
				                        <li>HTML--简单</li>
				                        <li>jQuery--有点难</li>
				                        <li>Android--安卓</li>
				                        <li>C--不会</li>
				                        <li>C++--更不会</li>
				                        <li>Struts--懂哦</li>
				                        <li>hibernate--已经不怎么懂</li>
				                        <li>spring--懂哦</li>
				                        <li>0123456789--10</li>
				                    </ul>
				                </div>
				            </div> -->
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">眼象信息</label>
								<div class="col-sm-8">
									<label>
										<input type="radio" id="haveCheck" name="eyeCheck" value="已检测" <c:if test="${(editEntity.eyeCheck=='已检测')}">checked="checked"</c:if> />已检测
									</label> 
									<label> 
										<input type="radio" id="noCheck" name="eyeCheck" value="未检测" <c:if test="${(editEntity.eyeCheck=='未检测')}">checked="checked"</c:if> /> 未检测
									</label>
								</div>
							</div>
							
							<div class="form-group">
								<label for="riskScore"
									class="col-sm-4 control-lavel">糖尿病危险因素评估分数</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control"
										id="riskScore" name="riskScore"
										readonly="readonly">
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">血糖情况</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="bloodSugarCondition" id="bloodSugarCondition" value="${editEntity.bloodSugarCondition}">
									<input type="hidden" name="dmRisk" id="dmRisk" value="${editEntity.dmRisk }">
									<input type="hidden" name="dmTag" id="dmTag" value="${editEntity.dmTag }">
									<input type="hidden" name="dmRiskType" id="dmRiskType" value="${editEntity.dmRiskType }">
								</div>
							</div>
							
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">血脂情况</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="bloodLipidCondition" id="bloodLipidCondition" value="${editEntity.bloodLipidCondition}">
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">血压情况</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="bloodPressureCondition" id="bloodPressureCondition" value="${editEntity.bloodPressureCondition}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">人群分类结果</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" name="classifyResult" id="classifyResult" value="${editEntity.classifyResult}">
								</div>
							</div>
							<%-- <div class="form-group">
								<label for="checkDate" class="col-sm-4 control-lavel"><span style="color:red;">*</span>检测时间</label>
								<div class="col-sm-8 controls">
									<input class="Wdate" id="checkDate" name="checkDate" readonly type="text" value="${editEntity.checkDate}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
								</div>
	
							</div> --%>
							<input type="hidden" id="visit" value="${editEntity.visit}">
							<div class="form-group" style="display: none;" id="visitDiv">
								<label class="col-sm-4 control-lavel">是否参加随访</label>
								<div class="col-sm-4">
									<label> 
										<input type="radio" id="visitYes" name="visit" value="是" <c:if test="${(editEntity.visit=='是')}">checked="checked"</c:if> /> 是
									</label>
									<label>
										<input type="radio" id="visitNo"  name="visit" value="否" <c:if test="${(editEntity.visit=='否')}">checked="checked"</c:if> /> 否
									</label>
								</div>
							</div>
							
							<h4 class="block-title"><span>其他</span></h4>
							
	
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">OGTT检测</label>
								<div class="col-sm-8">
									<label> 
										<input type="radio" name="OGTTTest" id="OGTTNeed" value="需要" <c:if test="${(editEntity.OGTTTest=='需要')}">checked="checked"</c:if>>需要
									</label> 
									<label> 
										<input type="radio" name="OGTTTest"	id="OGTTNoneed" value="不需要" <c:if test="${(editEntity.OGTTTest=='不需要')}">checked="checked"</c:if>> 不需要
									</label>
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">血脂检测</label>
								<div class="col-sm-8">
									<label> 
										<input type="radio" name="bloodLipidTest" id="bloodLipidTestNeed" value="需要" <c:if test="${(editEntity.bloodLipidTest=='需要')}">checked="checked"</c:if>> 需要
									</label> 
									<label> 
										<input type="radio" name="bloodLipidTest" id="bloodLipidTestNoneed" value="不需要" <c:if test="${(editEntity.bloodLipidTest=='不需要')}">checked="checked"</c:if>> 不需要
									</label>
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">血压检测</label>
								<div class="col-sm-8">
									<label> 
										<input type="radio" name="bloodPressureTest" id="bloodPressureNeed" value="需要" <c:if test="${(editEntity.bloodPressureTest=='需要')}">checked="checked"</c:if>> 需要
									</label> 
									<label> 
										<input type="radio" name="bloodPressureTest" id="bloodPressureNoneed" value="不需要" <c:if test="${(editEntity.bloodPressureTest=='不需要')}">checked="checked"</c:if>> 不需要
									</label>
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">基因检测</label>
								<div class="col-sm-8">
									<label id="geneTestNedd"> 
										<input type="radio" name="geneTest" value="需要" <c:if test="${(editEntity.geneTest=='需要')}">checked="checked"</c:if>>需要
									</label> 
									<label style="display: none;" id="geneTestNeddJs"> 
										<input type="radio" name="geneTest" value="需要（进入精筛环节）" <c:if test="${(editEntity.geneTest=='需要（进入精筛环节）')}">checked="checked"</c:if>>需要（进入精筛环节）
									</label> 
									<label id="geneTestNoNedd"> 
										<input type="radio" name="geneTest" value="不需要" <c:if test="${(editEntity.geneTest=='不需要')}">checked="checked"</c:if>> 不需要
									</label>
									<label style="display: none;" id="geneTestNoNeddJs"> 
										<input type="radio" name="geneTest" value="不需要（进入精筛环节）" <c:if test="${(editEntity.geneTest=='不需要（进入精筛环节）')}">checked="checked"</c:if>> 不需要（进入精筛环节）
									</label>
								</div>
							</div>
							
							<h4 class="block-title"><span>基因检测</span></h4>
							
							<div class="form-group" style="display:none;" id="fuxinDiv">
								<label for="url" class="col-sm-4 control-lavel"></label>
								<div class="col-sm-8 controls">
									<label>
										<input name="geneReport" type="checkbox" disabled="true" value="糖尿病用药套餐" id="fuxintangniaobing" <c:if test="${fn:contains(editEntity.geneReportCs,'糖尿病用药套餐')}">checked="checked"</c:if>>糖尿病用药套餐
									</label> 
									<label>
										<input name="geneReport" type="checkbox" disabled="true" value="高血压用药套餐" id="fuxingaoxueya" <c:if test="${fn:contains(editEntity.geneReportCs,'高血压用药套餐')}">checked="checked"</c:if>>高血压用药套餐
									</label> 
									<label>
										<input name="geneReport" type="checkbox" disabled="true" value="他汀类降脂药套餐" id="fuxingaoxuezhi" <c:if test="${fn:contains(editEntity.geneReportCs,'他汀类降脂药套餐')}">checked="checked"</c:if>>他汀类降脂药套餐
									</label>
								</div>
							</div>
							
							<div class="form-group" style="display:none;" id="kunmingDiv">
								<label for="url" class="col-sm-4 control-lavel"></label>
								<div class="col-sm-8 controls">
									<div style="width:1000px;">
										<label>
											<input name="geneReport" type="checkbox" disabled="true" value="糖尿病用药套餐或糖尿病基因检测4项" id="kunmingtangniaobing" <c:if test="${fn:contains(editEntity.geneReportCs,'糖尿病用药套餐或糖尿病基因检测4项')}">checked="checked"</c:if>>糖尿病用药套餐或糖尿病基因检测4项
										</label> 
										<label>
											<input name="geneReport" type="checkbox" disabled="true" value="高血压用药套餐" id="kunminggaoxueya" <c:if test="${fn:contains(editEntity.geneReportCs,'高血压用药套餐')}">checked="checked"</c:if>>高血压用药套餐
										</label> 
										<label>
											<input name="geneReport" type="checkbox" disabled="true" value="他汀类降脂药套餐" id="kunminggaoxuezhi" <c:if test="${fn:contains(editEntity.geneReportCs,'他汀类降脂药套餐')}">checked="checked"</c:if>>他汀类降脂药套餐
										</label>
									</div>
								</div>
							</div>
							
							<hr>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">备注</label>
								<div class="col-sm-8">
									<textarea rows="3" cols="150" name="remark" id="remark">${editEntity.remark}</textarea>
								</div>
							</div>
							<hr>
							<input type="hidden" name="district" value="${district}">
						</div>
					</div>
					<div class="panel-footer" id="panelFooter" style="display: none;">
						<div class="form-group button-center-block">
							<div class="col-sm-6 controls" >
								<input class="btn btn-primary btn-lg" type="submit" id="saveBtn" value="保存数据"> 
							</div> 
							<div class="col-sm-6 controls" >
								<input class="btn btn-primary btn-lg" type="submit" id="printBtn" value="保存并打印报告">
							</div>
						</div>
					</div>
					
					<div class="panel-footer" id="nextFooter">
						<div class="form-group button-center-block" style="width:100%;">
								<input class="btn btn-primary btn-lg" type="submit" id="nextBtn" value="下一步"> 
							</div> 
					</div>
					</form>
				</div>
			</div>
		</div>
		<!-- 以上这是页面需要编辑的区域 -->
	</div>

</body>
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
<script language="JavaScript">
var tizhiFlag = true;

$(function(){
	var item = $("#item").val();
	if (item == "fuxin") {
		$("#fuxinDiv").show();
	} else {
		$("#fuxinDiv").hide();
	}
	
	if (item == "kunming") {
		$("#kunmingDiv").show();
	} else {
		$("#kunmingDiv").hide();
	} 
	var operation = $("#operation").val();
	 if ($("#operation").val() == "edit") {
 	        $("#userInfo1").hide();
			$("#userInfo2").show();
			$("#csInfo").show();
			$("#panelFooter").show();
			$("#nextFooter").hide();
			$("#checkDatesDiv2").hide();
			
			$('#customerIdDiv').text($("#customerId").val());
			$('#mobileDiv').text($("#mobile").val());
			$('#nameDiv').text($("#customerName").val());
			$('#birthdayDiv').text($("#birthday").val());
			$('#ageDiv').text($("#age").val());
			$('#genderDiv').text($("#gender").val());
			$('#checkDateDiv').text($("#checkDate").val());
			$('#familyHistoryShowDiv').text($("#familyHistoryShow").val());
			$('#diseaseShowDiv').text($("#diseaseShow").val());
			
			var visit = $("#visit").val();
			if (visit == "是") {
				$("#visitDiv").hide();
			} else {
				$("#visitDiv").show();
			}
			
			//debugger;
			//展示历史记录
			historyData();
			
			//管理员权限开发体质模糊查询
			$.ajax({
				type : "post",
				url : "${ctx}/manage/hc/getTizhi.json",
				success:function(data){
					if(data.code==0){
						console.log(data.dataMap.tizhiResults);
						tizhiSearch(data.dataMap.tizhiResults);
					}
				}
			});
			
			if ('${editflag}' == "false") {
				$('#tizhi').attr("readonly","readonly");
			}
    }
})

	var t2;
    function OnBtnReadIDCardContent() {
        var result = HXIDCard.readIDCardContent(MyObject);
        if ("1" == result.code) {
        	var idCard = result.content.cardNo;
        	$('#customerId').val(idCard);
        	$('#customerName').val(result.content.name);
        	$('#gender').val(result.content.sexMc);
        	var age = idCardNoUtil.getAge(idCard);
        	$("#age").val(age);
        	var birthday = idCardNoUtil.getIdCardInfo(idCard).birthday;
        	$('#birthday').val(birthday);
        	queryInfo(idCard);
        	window.clearInterval(t2);

        }
    }
    
    
	var submitSign = "generatePdf";
	function addDateModule(inp){
		var id = $(inp).attr('id');
		if($("#"+id+"Calendar").length == 0){
			var $Top = $("#"+id).offset().top+ $("#"+id).height() + 16;
			$Left = $("#"+id).offset().left;
			$("#"+id).calendar();
			$("#"+id+"Calendar").css({"left":$Left,"top":$Top,"width": "190px","z-index":"999999",
		    "height":"180px"});
			//addDateModule(inp);
			inp.click();
		}	
	}
	
	function inputId() {
		$('#customerId').removeAttr("readonly");
		$('#inputQueryButton').show();
		$('#inputIdcardButton').hide();
	
		window.clearInterval(t2);
	}
	
	function queryByCustomerId() {
		var idCardNo = $('#customerId').val();
		if (idCardNo == null || idCardNo=='') {
			alert('请输入身份证号码！');
			return;
		}
		if (!idCardNoUtil.checkIdCardNo(idCardNo)) {
			alert('身份证号码格式错误，请重新输入！');
			return;
		}
		var idCardInfo = idCardNoUtil.getIdCardInfo(idCardNo);
		if (idCardInfo == null) {
			alert('身份证号码格式错误，请重新输入！');
			return;
		}
		
    	$('#gender').val(idCardInfo.gender);
    	$('#birthday').val(idCardInfo.birthday);
		queryInfo(idCardNo);
	}
	
	function queryInfo(idCard, checkDate) {
		var TempArr = [];//存储option
		
		var uniqueId1 = $("#uniqueId").val();
		if (uniqueId1 != "" && uniqueId1 != null) {
			$("#userInfo1").hide();
			$("#userInfo2").show();
			$("#csInfo").show();
			$("#panelFooter").show();
			$("#nextFooter").hide();
			$("#checkDatesDiv2").hide();
			//debugger;
			//展示历史记录
			historyData();
		} 
		
		$.ajax({
			type : "post",
			url : "${ctx}/manage/hc/queryInfoHealthCheck.json",
			data : {
				customerId:idCard,
				checkDate:checkDate
			},
			success:function(data){
				if(data.code==0){
					if ($('#userInfo1').is(':visible')) {
						$('#customerId').attr("readonly", "readonly");
						$('#customerName').attr("readonly","readonly");
						$('#birthday').attr("readonly", "readonly");
			        	$('#age').attr("readonly", "readonly");
			        	$('#gender').attr("readonly", "readonly");
			        	$('#mobile').attr("readonly","readonly");
			        	$('#diseaseShow').attr("readonly", "readonly");
			        	$('#familyHistoryShow').attr("readonly", "readonly");
			        	
			        	var dataResult = data.dataMap;
                    	var checkDates = dataResult.checkDates;
                    	var checkDate = dataResult.checkDate;
                    	$("#checkDatesDiv2").show();
                    	
                    	var idCard = $('#customerId').val(); 
                    	$.each(checkDates, function(key, value){
                    		$("#checkDates").append('<span style="color: #337ab7" onclick="javascript:queryInfo(\''+idCard+'\',\''+ value +'\')">'+ value +'</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
                    	});
					}
					
					$("#primarykeyId").val(data.dataMap.id);
					
					var uniqueId = data.dataMap.uniqueId;
					$('#uniqueId').val(uniqueId);
					
					var mobile = data.dataMap.mobile;
					if (mobile!='') {
						$('#mobile').val(mobile);
					}
					
					var customerName = data.dataMap.name;
					if (customerName != null && customerName != '') {
						$('#customerName').val(customerName);
					}
					
					var disease = data.dataMap.disease;
					var haveDisease = data.dataMap.haveDisease;
					$('#disease').val(disease);
					$('#haveDisease').val(haveDisease);
					if (haveDisease == "否") {
						$('#diseaseShow').val(haveDisease);
						$('#diseaseShowDiv').text(haveDisease);
					} else if(haveDisease == "是") {
						if (disease == null) {
							disease = '';
						}
						$('#diseaseShow').val(disease);
						$('#diseaseShowDiv').text(disease);
					}
					
					$('#customerIdDiv').text(idCard);
					if (data.dataMap.mobile != null) {
						$('#mobileDiv').text(data.dataMap.mobile);
					} 
					
					$('#nameDiv').text(data.dataMap.name);
					$('#birthdayDiv').text(data.dataMap.birthday);
					$('#ageDiv').text(data.dataMap.age);
					$('#age').val(data.dataMap.age);
					$('#genderDiv').text(data.dataMap.gender);
					$('#checkDateDiv').text(data.dataMap.checkDate);
					
					if (disease != null && disease != "") {
						if (disease.indexOf("糖尿病")>=0) {
							$("#bloodSugarCondition").val("糖尿病患者");
							$("#OGTTNoneed").prop("checked","checked");
						}
						if (disease.indexOf("高血压")>=0) {
							$("#bloodPressureCondition").val("高血压患者");
							$("#bloodPressureNoneed").prop("checked","checked");
						}
						if (disease.indexOf("高血脂")>=0) {
							$("#bloodLipidCondition").val("血脂异常患者");
							$("#bloodLipidTestNoneed").prop("checked","checked");
						}
					}
					
					var familyDisease = data.dataMap.familyDisease;
					var familyHistory = data.dataMap.familyHistory;
					$('#familyHistory').val(familyHistory);
					$('#familyDisease').val(familyDisease);
					if (familyHistory == "否") {
						$('#familyHistoryShow').val(familyHistory);
						$('#familyHistoryShowDiv').text(familyHistory);
					} else if(familyHistory == "是") {
						if (familyDisease == null) {
							familyDisease = '';
						}
						$('#familyHistoryShow').val(familyDisease);
						$('#familyHistoryShowDiv').text(familyDisease);
					}
					
					var eyeCheck = data.dataMap.eyeCheck;
					if(eyeCheck == "已检测") {
						$("#haveCheck").prop("checked","checked");
					} else {
						$("#noCheck").prop("checked","checked");
					}
					$("#checkDate").val(data.dataMap.checkDate);
					//WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d',minDate:'${editEntity.recordDate}'})
					//<input class="Wdate" id="checkDate" name="checkDate" readonly type="text" value="${editEntity.checkDate}"
					//						onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d',minDate:'${editEntity.recordDate}'})">
					var recordDate = data.dataMap.recordDate;
					if (recordDate != null && recordDate != "") {
						$("#checkDate").removeAttr("onclick");
						$("#checkDate").attr("onclick", "WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d',minDate:'" + recordDate + "'})");
					}
					
					$("#height").val(data.dataMap.height);
					$("#weight").val(data.dataMap.weight);
					$("#BMI").val(data.dataMap.BMI);
					$("#waistline").val(data.dataMap.waistline);
					$("#highPressure").val(data.dataMap.highPressure);
					$("#lowPressure").val(data.dataMap.lowPressure);
					$("#pulse").val(data.dataMap.pulse);
					$("#temperature").val(data.dataMap.temperature);
					$("#oxygen").val(data.dataMap.oxygen);
					$("#hipline").val(data.dataMap.hipline);
					$("#WHR").val(data.dataMap.WHR);
					$("#fatContent").val(data.dataMap.fatContent);
					$("#bloodGlucose").val(data.dataMap.bloodGlucose);
					$("#bloodGlucose2h").val(data.dataMap.bloodGlucose2h);
					$("#bloodGlucoseRandom").val(data.dataMap.bloodGlucoseRandom);
					$("#tc").val(data.dataMap.tc);
					$("#tg").val(data.dataMap.tg);
					$("#ldl").val(data.dataMap.ldl);
					$("#hdl").val(data.dataMap.hdl);
					$("#riskScore").val(data.dataMap.riskScore);
					$("#OGTTTest").val(data.dataMap.OGTTTest);
					$("#bloodLipidTest").val(data.dataMap.bloodLipidTest);
					$("#bloodPressureTest").val(data.dataMap.bloodPressureTest);
					$("#geneTest").val(data.dataMap.geneTest);
					$("#geneReportCs").val(data.dataMap.geneReportCs);
					$("#remark").val(data.dataMap.remark);
					
					var tizhi = data.dataMap.tizhi;
					$('#tizhi').val(tizhi);
					
					var editflag = $('#editflag').val();
					if (editflag == "false") {
						$('#tizhi').attr("readonly","readonly");
					} else {
						var tizhiResults = data.dataMap.tizhiResults;
						console.log(tizhiResults);
						tizhiSearch(tizhiResults);
					}
					
					classifyResultFunc();
					
					$('#inputQueryButton').hide();
					//alert(data.dataMap.visit);
					var visit = data.dataMap.visit;
					$("#visit").val(visit);
					if (visit == '是') {
						$("#visitDiv").hide();
					} else if (visit == '否'){
						$("#visitNo").prop("checked",true);
					}
					
					computeAll();
				} else {
					alert("此用户尚未建档，请建档后再进行筛查");
				}
			}
		});
	}
	
   $(function () {
	   var dataId = $('#dataId').val();
	   var customerId = $('#customerId').val();
	   if (dataId == '') {
	       t2 = window.setInterval("OnBtnReadIDCardContent()", 2500); 
	   } else if (customerId == '') {
		   alert('没有找到此对象的建档信息');
	   }
          if (window.history && window.history.pushState) {
              $(window).on('popstate', function () {
                  window.history.pushState('forward', null, ''); 
                  window.history.forward(1);
              });
          }
          window.history.pushState('forward', null, '');  //在IE中必须得有这两行
          window.history.forward(1);
          
	   // 身份证号码验证 
	   jQuery.validator.addMethod("isIdCardNo", function(value, element) { 
	     return this.optional(element) || idCardNoUtil.checkIdCardNo(value);     
	   }, "请正确输入您的身份证号码"); 
	   
		jQuery.validator.addMethod("indicator", function(value, element) {
			var check = /^((<|>)?\d+)(\.\d+)?$/.test(value);
			if (check) {
				var dvalue=parseFloat(value);
				if (dvalue==0) {
					check = false;
				}
			}
			return this.optional(element) || check;     
		}, "请正确输入数值"); 
		   
		   
	        $("#healthcheckInfo").validate({
				rules:{
					name:{
						required: true
					},
					customerId:{
						required: true,
						isIdCardNo:true
					},
					age:{
						required: true,
						digits:true
					},
					gender:{
						required: true
					},
					checkDate:{
						required:true
					},
					familyHistory:{
						required:true
					},
					mobile:{
						required:true
					},
					height:{
						number:true,
						min:1
					},
					weight:{
						number:true,
						min:1
					},
					waistline:{
						number:true,
						min:1
					},
					hipline:{
						number:true,
						min:1
					},
					highPressure:{
						digits:true,
						min:1
					},
					lowPressure:{
						digits:true,
						min:1
					},
					pulse:{
						digits:true,
						min:1
					},
					temperature:{
						number:true,
						min:1
					},
					oxygen:{
						number:true,
						min:1
					},
					fatContent:{
						number:true,
						min:1
					},
					bloodGlucose:{
						indicator:true
					},
					bloodGlucose2h:{
						indicator:true
					},
					bloodGlucoseRandom:{
						indicator:true
					},
					tc:{
						indicator:true
					},
					tg:{
						indicator:true
					},
					hdl:{
						indicator:true
					},
					ldl:{
						indicator:true
					},
					visit:{
						required:true
					}
				},  
    	        messages: {  
    	        	name: {
    	        		required:"请输入用户名",
    	        	},
    	        	customerId:{
						required: "请输入身份证号",
						isIdCardNo:"请输入正确的身份证号"
					},
					age:{
						required: "请输入年龄",
						digits:"请输入合法数字"
					},
					gender:{
						required: "请选择性别"
					},
					checkDate:{
						required:"请输入检测时间"
                    },
                    familyHistory:{
						required:"请选择糖尿病家族史"
					},
					mobile:{
						required:"请输入手机号"
					},
					visit:{
						required:"请选择是否参加随访"
					}
    	  		},
    	  		highlight : function(element) {  
		             	$(element).closest('.controls').addClass('has-error');  
			    },  
	            success : function(label) {  
	                label.closest('.controls').removeClass('has-error');  
	                label.remove();  
	            }	        	
	        });
	        
	    });
	   
	function computeAll() {
		 //血糖分数
		var riskScore = 0; 
		var tangniaobingAgeScore = 0;
		var tangniaobingSexScore = 0;
		//体质指数
		var tangniaobingBMIScore = 0;
		//腰围
		var tangniaobingWaistlineScore = 0;
		//收缩压
		var tangniaobingHighPressureScore = 0;
		//糖尿病家族史
		var tangniaobingFamilyHistoryScore = 0;
		//人群分类结果
		var classifyResult = "";
		var xtResult = "";
		var xzResult = "";
		var xyResult = "";
		//是否存在糖尿病风险分数
		//gender
		var editEntitySex = $("#gender").val();
		if(editEntitySex == '男'){
			 tangniaobingSexScore = 2;
		}
		if(editEntitySex == '女'){
			tangniaobingSexScore = 0;
		}
		//血糖分数
		if($("#highPressure").val()!=""){
			var highPressureNumber = parseInt($("#highPressure").val());
			if(highPressureNumber<110){
				tangniaobingHighPressureScore = 0;
			}
			if(highPressureNumber >= 110 && highPressureNumber<120){
				tangniaobingHighPressureScore = 1;
			}
			if(highPressureNumber >= 120 && highPressureNumber<130){
				tangniaobingHighPressureScore = 3;
			}
			if(highPressureNumber >= 130 && highPressureNumber<140){
				tangniaobingHighPressureScore = 6;
			}
			if(highPressureNumber >= 140 && highPressureNumber<150){
				tangniaobingHighPressureScore = 7;
			}
			if(highPressureNumber >= 150 && highPressureNumber<160	){
				tangniaobingHighPressureScore = 8;
			}
			if(highPressureNumber >= 160){
				tangniaobingHighPressureScore = 10;
			}
		}
		//tangniaobingAgeScore
		if($("#age").val()!=""){
			var ageNumber = parseInt($("#age").val());
			if(ageNumber>=18 && ageNumber<25){
				tangniaobingAgeScore = 0;
			}
			if(ageNumber>=25 && ageNumber<35){
				tangniaobingAgeScore = 4;
			}
			if(ageNumber>=35 && ageNumber<40){
				tangniaobingAgeScore = 8;
			}
			if(ageNumber>=40 && ageNumber<45){
				tangniaobingAgeScore = 11;
			}
			if(ageNumber>=45 && ageNumber<50){
				tangniaobingAgeScore = 12;
			}
			if(ageNumber>=50 && ageNumber<55){
				tangniaobingAgeScore = 13;
			}
			if(ageNumber>=55 && ageNumber<60){
				tangniaobingAgeScore = 15;
			}
			if(ageNumber>=60 && ageNumber<65){
				tangniaobingAgeScore = 16;
			}
			if(ageNumber>=65){
				tangniaobingAgeScore = 18;
			}
		}
		//tangniaobingBMIScore
		if($("#BMI").val()!=""){
			var bmiNumber = parseFloat($("#BMI").val());
			if(bmiNumber < 22){
				tangniaobingBMIScore = 0;
			}
			if(bmiNumber >= 22 && bmiNumber < 24){
				tangniaobingBMIScore = 1;
			}
			if(bmiNumber >=24 && bmiNumber <30){
				tangniaobingBMIScore = 3;
			}
			if(bmiNumber >=30){
				tangniaobingBMIScore = 5;
			}
		}
		//tangniaobingWaistlineScore
		if($("#waistline").val()!=""){
			
			analyseyaotunbi();
			
			var gender = $("#gender").val();
			var waistlineNumber = parseFloat($("#waistline").val());
			if(gender == '男'){
				if(waistlineNumber < 75){
					tangniaobingWaistlineScore = 0;
				}
				if(waistlineNumber >= 75 && waistlineNumber<80){
					tangniaobingWaistlineScore = 3;
				}
				if(waistlineNumber >= 80 && waistlineNumber<85){
					tangniaobingWaistlineScore = 5;
				}
				if(waistlineNumber >= 85 && waistlineNumber<90){
					tangniaobingWaistlineScore = 7;
				}
				if(waistlineNumber >= 90 && waistlineNumber<95){
					tangniaobingWaistlineScore = 8;
				}
				if(waistlineNumber >= 95){
					tangniaobingWaistlineScore = 10;
				}
			}
			if(gender == '女'){
				if(waistlineNumber < 70){
					tangniaobingWaistlineScore = 0;
				}
				if(waistlineNumber >= 70 && waistlineNumber<75){
					tangniaobingWaistlineScore = 3;
				}
				if(waistlineNumber >= 75 && waistlineNumber<80){
					tangniaobingWaistlineScore = 5;
				}
				if(waistlineNumber >= 80 && waistlineNumber<85){
					tangniaobingWaistlineScore = 7;
				}
				if(waistlineNumber >= 85 && waistlineNumber<90){
					tangniaobingWaistlineScore = 8;
				}
				if(waistlineNumber >= 90){
					tangniaobingWaistlineScore = 10;
				}
			}
		}
		//tangniaobingFamilyHistoryScore
		var editEntityFamilyHistory = $('#familyHistory').val();
		var editEntityFamilyDisease = $('#familyDisease').val();
		if(editEntityFamilyHistory=='是' && editEntityFamilyDisease.indexOf('糖尿病')>=0){
			 tangniaobingFamilyHistoryScore = 6;
		 }
		riskScore = tangniaobingAgeScore + tangniaobingSexScore + tangniaobingBMIScore + tangniaobingWaistlineScore + tangniaobingHighPressureScore + tangniaobingFamilyHistoryScore;
		$("#riskScore").val(riskScore);
		
		var disease = $('#disease').val();
		if (disease == null) {
			disease = '';
		}
		
		/* if (disease.indexOf("糖尿病")>=0) {
			$("#bloodSugarCondition").val("糖尿病患者");
			$("#OGTTNoneed").prop("checked","checked");
			 $("#dmRisk").val("高");
			 $('#dmRiskType').val("2");
		} else {
			bloodGlucoseCon(disease, riskScore);
		} */
		bloodGlucoseCon(disease, riskScore);
		
		if (disease.indexOf("高血压")>=0) {
			$("#bloodPressureCondition").val("高血压患者");
			$("#bloodPressureNoneed").prop("checked","checked");
		} else {
			if($("#highPressure").val()!="" || $("#lowPressure").val()!="" ){
				bloodPressureCon();
			} else {
				$("#bloodPressureCondition").val("");
				$("#bloodPressureNoneed").prop("checked",false);
				$("#bloodPressureNeed").prop("checked",false);
			}
		}
		if (disease.indexOf("高血脂")>=0) {
			$("#bloodLipidCondition").val("血脂异常患者");
			$("#bloodLipidTestNoneed").prop("checked","checked");
		} else {
			bloodLipidCon();
		}
			
		classifyResultFunc();
		
		var bgVal = $("#bloodGlucose").val();
		if (bgVal != '' && parseFloat(bgVal) >= 6.1) {
			$("#bloodGlucoseUp").show();
			$("#bloodGlucoseDown").hide();
			$("#bloodGlucoseState").val("up");
		} else if(bgVal != '' && parseFloat(bgVal) < 3.9){
			$("#bloodGlucoseUp").hide();
			$("#bloodGlucoseDown").show();
			$("#bloodGlucoseState").val("down");
		} else {
			$("#bloodGlucoseUp").hide();
			$("#bloodGlucoseDown").hide();
			$("#bloodGlucoseState").val("");
		}
		
		var bg2hVal = $("#bloodGlucose2h").val();
		if (bg2hVal != '' && parseFloat(bg2hVal) >= 7.8) {
			$("#bloodGlucose2hUp").show();
			$("#bloodGlucose2hDown").hide();
			$("#bloodGlucose2hState").val("up");
		} else if(bg2hVal != '' && parseFloat(bg2hVal) < 3.9){
			$("#bloodGlucose2hUp").hide();
			$("#bloodGlucose2hDown").show();
			$("#bloodGlucose2hState").val("down");
		} else {
			$("#bloodGlucose2hUp").hide();
			$("#bloodGlucose2hDown").hide();
			$("#bloodGlucose2hState").val("");
		}
		
		var bgrVal = $("#bloodGlucoseRandom").val();
		if (bgrVal != '' && parseFloat(bgrVal) >= 11.1) {
			$("#bloodGlucoseRandomUp").show();
			$("#bloodGlucoseRandomDown").hide();
			$("#bloodGlucoseRandomState").val("up");
		} else if(bgrVal != '' && parseFloat(bgrVal) < 3.9){
			$("#bloodGlucoseRandomUp").hide();
			$("#bloodGlucoseRandomDown").show();
			$("#bloodGlucoseRandomState").val("down");
		} else {
			$("#bloodGlucoseRandomUp").hide();
			$("#bloodGlucoseRandomDown").hide();
			$("#bloodGlucoseRandomState").val("");
		}
		
		var hpVal = $("#highPressure").val();
		if (hpVal != '' && parseFloat(hpVal) >= 130) {
			$("#highPressureUp").show();
			$("#highPressureDown").hide();
			$("#highPressureState").val("up");
		} else if(hpVal != '' && parseFloat(hpVal) < 90){
			$("#highPressureUp").hide();
			$("#highPressureDown").show();
			$("#highPressureState").val("down");
		} else {
			$("#highPressureUp").hide();
			$("#highPressureDown").hide();
			$("#highPressureState").val("");
		}
		
		var lpVal = $("#lowPressure").val();
		if (lpVal != '' && parseFloat(lpVal) >= 85) {
			$("#lowPressureUp").show();
			$("#lowPressureDown").hide();
			$("#lowPressureState").val("up");
		} else if(lpVal != '' && parseFloat(lpVal) < 60){
			$("#lowPressureUp").hide();
			$("#lowPressureDown").show();
			$("#lowPressureState").val("down");
		} else {
			$("#lowPressureUp").hide();
			$("#lowPressureDown").hide();
			$("#lowPressureState").val("");
		}
		
		var tcVal = $("#tc").val();
		if (tcVal != '' && parseFloat(tcVal) >= 5.2) {
			$("#tcUp").show();
			$("#tcDown").hide();
			$("#tcState").val("up");
	/* 	} else if(tcVal != '' && parseFloat(tcVal) < 2.8){
			$("#tcUp").hide();
			$("#tcDown").show();
			$("#tcState").val("down");
	 */	} else {
			$("#tcUp").hide();
			$("#tcDown").hide();
			$("#tcState").val("");
		}
		
		var tgVal = $("#tg").val();
		if (tgVal != '' && parseFloat(tgVal) >= 1.7) {
			$("#tgUp").show();
			$("#tgDown").hide();
			$("#tgState").val("up");
	/* 	} else if(tgVal != '' && parseFloat(tgVal) < 0.56){
			$("#tgUp").hide();
			$("#tgDown").show();
			$("#tgState").val("down");
	 */	} else {
			$("#tgUp").hide();
			$("#tgDown").hide();
			$("#tgState").val("");
		}
		
		var hdlVal = $("#hdl").val();
	/* 	if (hdlVal != '' && parseFloat(hdlVal) >= 2) {
			$("#hdlUp").show();
			$("#hdlDown").hide();
			$("#hdlState").val("up");
		} else 
	 */	if(hdlVal != '' && parseFloat(hdlVal) < 1){
			$("#hdlUp").hide();
			$("#hdlDown").show();
			$("#hdlState").val("down");
		} else {
			$("#hdlUp").hide();
			$("#hdlDown").hide();
			$("#hdlState").val("");
		}
		var ldlVal = $('#ldl').val();
		if (ldlVal != '' && parseFloat(ldlVal) >= 3.4) {
			$("#ldlUp").show();
			$("#ldlDown").hide();
			$("#ldlState").val("up");
		} else {
			$("#ldlUp").hide();
			$("#ldlDown").hide();
			$("#ldlState").val("");
		}
		
	}
	   
	$(document).ready(function(){
		 	computeAll();
			$("#printBtn").click(function(){
				return submitForm("generatePdf");
			});
			$("#saveBtn").click(function(){
				return submitForm("submit");
			});
	}); 
	
	$("#nextBtn").click(function(){
		var checkDate = $("#checkDate").val();
		
		var uniqueId = $('#uniqueId').val();
		var customerId = $('#customerId').val();
    	
		if ((uniqueId==null||uniqueId=='') && (customerId==null||customerId=='')) {
    		alert("请输入身份证号！");
    		return false;
    	} else if ((uniqueId==null||uniqueId=='') && customerId!=null) {
    		alert("请点击【查询】获取用户信息！");
    		return false;
    	} else/*  if($('#checkDatesDiv2').is(':visible')) */{
			var customerId = $("#customerId").val();
			queryInfo(customerId, checkDate);
			return false;
    	}
		
	});
	
	//计算腰臀比
	function analyseyaotunbi(){
		 if($("#waistline").val()!="" && $("#hipline").val()!="" ){
			 var waistlineNum = parseFloat($("#waistline").val());
			 var hiplineNum = parseFloat($("#hipline").val());
			  $("#WHR").val(toDecimal2(waistlineNum/hiplineNum));
		 }else{
			 $("#WHR").val("");
		 }
	}
	//计算BMI:体重（千克）/（身高（米）*身高（米）
	function analyseBMI(){
		if($("#height").val()!="" && $("#weight").val()!="" ){
			 //cm
			 var heightNum = parseFloat($("#height").val());
			 //kg
			 var weightNum = parseFloat($("#weight").val());
			 var bmi = parseFloat(weightNum/(heightNum * heightNum/10000));
			 $("#BMI").val(toDecimal2(bmi));
		 }else{
			 $("#BMI").val("");
		 }
		computeAll();
		
	}
	
	function toDecimal2(x) {
		var f = parseFloat(x);      
		if (isNaN(f)) {   
		 return false;     
		}          
		var f = Math.round(x*100)/100;  
		var s = f.toString();       
		var rs = s.indexOf('.');      
		if (rs < 0) {   
		 rs = s.length;      
		 s += '.';   
		            }       
		while (s.length <= rs + 2) {   
		 s += '0';       
		}            
		return s;   
		}  
	
    
    /**设置延时**/
    function sleep(d){
          for(var t = Date.now();Date.now() - t <= d;);
    }
    /**生成pdf ajax**/
    function generatePdfAjax() {
    	var geneReportCs="";
		$("input[type=checkbox][name='geneReport']").each(function(){
		    if(this.checked){
		    	geneReportCs+=$(this).val()+",";
		    }
		});
		if(geneReportCs.length>0){
			geneReportCs = geneReportCs.substring(0, geneReportCs.length-1);
		} 
		var remark = $("#remark").val();
    	var options = {
                url:"${ctx}/manage/hc/generateHealthCheckPdf.json",
                data:{
                	geneReportCs:geneReportCs,
                	remark:remark
                },
                success:function(o){
                    var dataResult = o.dataMap;
                    if(dataResult.message =="success"){
                        previewPdf(dataResult.fileName);
                    }else{
                        alert("生成pdf失败！");
                    }
                }
            };
    	return options;
    }
    function submitForm(submitSign) {
    	var uniqueId=$('#uniqueId').val();
    	if (uniqueId==null||uniqueId=='') {
    		alert("此用户尚未建档，请建档后再筛查！");
    		return false;
    	} else if(!tizhiFlag){
    		alert("请填写正确的中医体质分类信息");
    		return false;
    	} else {
    		var options;
    		if (submitSign == "generatePdf") {
    			options = generatePdfAjax();
    		} else if (submitSign == "submit") {
    			options = submitFormsAjax();
    		}
    		$("#healthcheckInfo").ajaxForm(options);
    		return true;
    	}
    }
    /**保存表单信息  ajax**/
    function submitFormsAjax() {
    	var geneReportCs="";
		$("input[type=checkbox][name='geneReport']").each(function(){
		    if(this.checked){
		    	geneReportCs+=$(this).val()+",";
		    }
		});
		if(geneReportCs.length>0){
			geneReportCs = geneReportCs.substring(0, geneReportCs.length-1);
		} 
    	
    	var remark = $("#remark").val();
        var options = {
                url:"${ctx}/manage/hc/saveHealthCheck.json",
                data:{
                	geneReportCs:geneReportCs,
                	remark:remark
                },
                success:function(o){
                	if (o.code == 0) {
                        var dataResult = o.dataMap;
                        if(dataResult.message =="success"){
                        	alert("数据保存成功！")
                        	window.location.href="${ctx}/manage/hc/addHealthCheckUI.htm";
                        	$("#urlresourceid").val(dataResult.id);
                        	jump();
                        }else{
                            alert("数据保存失败，请重试！");
                        }
                	} else {
                		alert(o.message);
                	}
                }
            };
        return options;
    }
    /**页面跳转**/
    function submit() {
        submitSign = "submit";
        $("#saveBtn").click();
        
    }
    /**预览pdf**/
    function previewPdf(fileName){
         $('#printIframe').attr('src',fileName);
         $('#myModal').modal('show');
     }
    /**页面跳转**/
     function jump() {
    	 window.location.href="${ctx}/manage/hc/addHealthCheckUI.htm";
     }
    
    //血糖情况判断
    function bloodGlucoseCon(disease, riskScore) {
    	 var bloodGlucose = $("#bloodGlucose").val();
		 var bloodGlucose2h = $("#bloodGlucose2h").val();
		 var bloodGlucoseRandom = $("#bloodGlucoseRandom").val();
		 
		 var bloodGlucoseNumber = 0;
		 if (bloodGlucose.indexOf(">") != -1) {
			 bloodGlucoseNumber = 33.3;
		 } else if(bloodGlucose.indexOf("<") != -1){
			bloodGlucoseNumber = 1.1;
		 } else{
			bloodGlucoseNumber = parseFloat(bloodGlucose);
		 }
		 
		 var bloodGlucose2hNumber = 0;
		 if(bloodGlucose2h.indexOf(">") != -1){
			 bloodGlucose2hNumber = 33.3;
		 }else if(bloodGlucose2h.indexOf("<") != -1){
			 bloodGlucose2hNumber = 1.1;
		 }else{
			 bloodGlucose2hNumber = parseFloat(bloodGlucose2h)
		 }
		 
		 var bloodGlucoseRandomNumber = 0;
		 if(bloodGlucoseRandom.indexOf(">") != -1){
		 	bloodGlucoseRandomNumber = 33.3;
		 }else if(bloodGlucoseRandom.indexOf("<") != -1){
		 	bloodGlucoseRandomNumber = 1.1;
		 }else{
		 	bloodGlucoseRandomNumber = parseFloat(bloodGlucoseRandom);
		 }
		 
		 if (bloodGlucose == null || bloodGlucose == ''){
			 bloodGlucoseNumber = 0;
		 }
		 
		 if (bloodGlucose2h == null || bloodGlucose2h == ''){
			 bloodGlucose2hNumber = 0;
		 }
		 
		 if (bloodGlucoseRandom == null || bloodGlucoseRandom == ''){
			 bloodGlucoseRandomNumber = 0;
		 }
		 
		 if (disease.indexOf("糖尿病")>=0) {
			 if ((bloodGlucoseNumber>0 && bloodGlucoseNumber<=3.9) || bloodGlucoseNumber>=16.7) {
				 $("#bloodSugarCondition").val("糖尿病患者");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("立即转诊");
				 $('#dmRiskType').val("3");
			 } else {
				 $("#bloodSugarCondition").val("糖尿病患者");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("高");
				 $('#dmRiskType').val("2");
			 }
			 return;
		 }
		 
		 if (disease.indexOf("糖尿病") == -1) {
			 if (bloodGlucoseNumber >= 16.7) {
				 $("#bloodSugarCondition").val("血糖异常人群");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("提醒就医");
				 $("#dmTag").val("疑似糖尿病");
				 $('#dmRiskType').val("10");
				return $("#bloodSugarCondition").val();
			 }
		 }
		 
		 if (riskScore<25) {
			if ((bloodGlucoseNumber>=3.9 && bloodGlucoseNumber<6.1) 
					 || (bloodGlucose2hNumber >=3.9 && bloodGlucose2hNumber<7.8) 
					 || (bloodGlucoseRandomNumber>=3.9 && bloodGlucoseRandomNumber < 11.1)) {
						 $("#bloodSugarCondition").val("正常");
						 $("#dmRisk").val("低");
					 	 $("#OGTTNoneed").prop("checked","checked");
						 $("#dmTag").val("");
						 $('#dmRiskType').val("1");
			} else if ((bloodGlucoseNumber>=6.1 && bloodGlucoseNumber<7.0)
					|| (bloodGlucose2hNumber >=7.8 && bloodGlucose2hNumber<11.1)) {
				 $("#bloodSugarCondition").val("血糖异常人群");
				 $("#OGTTNeed").prop("checked","checked");
				 $("#dmRisk").val("高");
				 $("#dmTag").val("疑似糖尿病前期");
				 $('#dmRiskType').val("6");
			} else if ((bloodGlucoseNumber>=7.0 && bloodGlucoseNumber<16.7 )|| bloodGlucose2hNumber >=11.1 || bloodGlucoseRandomNumber>=11.1) {
				 $("#bloodSugarCondition").val("血糖异常人群");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("提醒就医");
				 $("#dmTag").val("疑似糖尿病");
				 $('#dmRiskType').val("8");
			} else if ((bloodGlucoseNumber>0 && bloodGlucoseNumber<3.9) 
					|| (bloodGlucose2hNumber>0 && bloodGlucose2hNumber <3.9) 
					|| (bloodGlucoseRandomNumber>0 && bloodGlucoseRandomNumber<3.9)) {
				 $("#bloodSugarCondition").val("血糖异常人群");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("立即转诊");
				 $("#dmTag").val("低血糖");
				 $('#dmRiskType').val("11");
			} else {
				 $("#bloodSugarCondition").val("");
				 $("#OGTTNoneed").removeAttr("checked");
				 $("#OGTTNeed").removeAttr("checked");
				 $("#dmRisk").val("无法判断");
				 $("#dmTag").val("");
				 $('#dmRiskType').val("0");

			}
		 } else {
			if ((bloodGlucoseNumber>=3.9 && bloodGlucoseNumber<6.1) 
					 || (bloodGlucose2hNumber >=3.9 && bloodGlucose2hNumber<7.8) 
					 || (bloodGlucoseRandomNumber>=3.9 && bloodGlucoseRandomNumber < 11.1)) {
						 $("#bloodSugarCondition").val("糖尿病高风险人群");
						 $("#dmRisk").val("高");
						 $("#OGTTNeed").prop("checked","checked");
						 $("#dmTag").val("");
						 $('#dmRiskType').val("4");
			} else if ((bloodGlucoseNumber>=6.1 && bloodGlucoseNumber<7.0)
					|| (bloodGlucose2hNumber >=7.8 && bloodGlucose2hNumber<11.1)) {
				 $("#bloodSugarCondition").val("血糖异常人群");
				 $("#OGTTNeed").prop("checked","checked");
				 $("#dmRisk").val("高");
				 $("#dmTag").val("疑似糖尿病前期");
				 $('#dmRiskType').val("7");
			} else if ((bloodGlucoseNumber>=7.0 && bloodGlucoseNumber<16.7 ) || bloodGlucose2hNumber >=11.1 || bloodGlucoseRandomNumber>=11.1) {
				 $("#bloodSugarCondition").val("血糖异常人群");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("提醒就医");
				 $("#dmTag").val("疑似糖尿病");
				 $('#dmRiskType').val("9");
			} else if ((bloodGlucoseNumber>0 && bloodGlucoseNumber<3.9) 
					|| (bloodGlucose2hNumber>0&&bloodGlucose2hNumber <3.9) 
					|| (bloodGlucoseRandomNumber>0&&bloodGlucoseRandomNumber<3.9)) {
				 $("#bloodSugarCondition").val("糖尿病高风险人群");
				 $("#OGTTNoneed").prop("checked","checked");
				 $("#dmRisk").val("立即转诊");
				 $("#dmTag").val("低血糖");
				 $('#dmRiskType').val("5");
			} else {
				 $("#bloodSugarCondition").val("");
				 $("#OGTTNoneed").removeAttr("checked");
				 $("#OGTTNeed").removeAttr("checked");
				 $("#dmRisk").val("无法判断");
				 $("#dmTag").val("");
				 $('#dmRiskType').val("0");
			} 
		 }

		 return $("#bloodSugarCondition").val();
    }

    //血压情况判断
   	function bloodPressureCon() {
    	var highPressure = $("#highPressure").val();
    	var lowPressure = $("#lowPressure").val();
    	var highPressureNumber = parseInt(highPressure);
    	var lowPressureNumber = parseInt(lowPressure);
    	if (highPressureNumber == NaN) {
    		highPressureNumber = 0;
    	}
    	if (lowPressureNumber == NaN) {
    		lowPressureNumber = 0;
    	}
	 
    	if ((highPressureNumber>0 && highPressureNumber<130)&& (lowPressureNumber>0&&lowPressureNumber<85)) {
			$("#bloodPressureCondition").val("正常");
			$("#bloodPressureNoneed").prop("checked","checked");
    	} else if (highPressureNumber>=130 || lowPressureNumber>=85) {
			$("#bloodPressureCondition").val("血压异常人群");
			$("#bloodPressureNeed").prop("checked","checked");
    	} else {
   			$("#bloodPressureCondition").val("");
			 $("#bloodPressureNeed").removeProp("checked");
			 $("#bloodPressureNoneed").removeProp("checked");
    	}
    	
    }
    
    //血脂情况判断
    function bloodLipidCon() {
    	var tcStr = $("#tc").val();
    	var tc = 0;
		if (tcStr!="") {
			if(tcStr.indexOf(">") != -1){
				tc = 10.36;
			}else if(tcStr.indexOf("<") != -1){
				tc = 2.59;
			}else{
				tc = parseFloat(tcStr);
			}
		}
		if (tc == NaN) {
			tc = 0;
		}
		var tgStr = $("#tg").val();
		var tgNumber = 0;
		if (tgStr!="") {
			if(tgStr.indexOf(">") != -1){
				tgNumber = 5.65;
			}else if(tgStr.indexOf("<") != -1){
				tgNumber = 0.57;
			}else{
				tgNumber = parseFloat(tgStr);
			}
		}
		if (tgNumber == NaN) {
			tgNumber = 0;
		}
		
		var hdl = 0;
		var hdlStr = $("#hdl").val();
		if (hdlStr!="") {
			if(hdlStr.indexOf(">") != -1){
				hdl = 2.59;
			}else if(hdlStr.indexOf("<") != -1){
				hdl = 0.39;
			}else{
				hdl = parseFloat(hdlStr);
			}
		}
		if (hdl ==NaN) {
			hdl = 0;
		}
		if(tc>=5.2 || tgNumber>=1.7 || (hdl> 0&& hdl<1)){
			$("#bloodLipidCondition").val("血脂异常高风险人群");
			$("#bloodLipidTestNeed").prop("checked","checked");
		} else if((tc>0&&tc<5.2) && (tgNumber>0&&tgNumber<1.7) && (hdl>=1)){
			$("#bloodLipidCondition").val("正常");
			$("#bloodLipidTestNoneed").prop("checked","checked");
		} else {
			$("#bloodLipidCondition").val("");
			$("#bloodLipidTestNoneed").removeProp("checked");
			$("#bloodLipidTestNeed").removeProp("checked");
		}
    	
    }
    
    function classifyResultFunc() {
		var rqflResult = "";
		var bloodSugarCondition = $("#bloodSugarCondition").val();
		var bloodLipidCondition = $("#bloodLipidCondition").val();
		var bloodPressureCondition = $("#bloodPressureCondition").val();
		
		var fatCondition = "";
		var BMI = $("#BMI").val();
		if (BMI != null) {
			BMI = parseFloat(BMI);
			if (BMI >= 24 && BMI < 28) {
				fatCondition = "超重人群";
			}else if (BMI >= 28) {
				fatCondition = "肥胖人群";
			}else if (BMI > 0 && BMI < 24) {
				fatCondition = "正常";
			}
		}
		
		if (bloodSugarCondition != "" && bloodSugarCondition != "正常") {
			rqflResult = rqflResult + bloodSugarCondition + ",";
		}
		
		if (bloodLipidCondition != "" && bloodLipidCondition != "正常") {
			rqflResult = rqflResult + bloodLipidCondition + ",";
		}
		
		if (bloodPressureCondition != "" && bloodPressureCondition != "正常") {
			rqflResult = rqflResult + bloodPressureCondition + ",";
		}
		
		if (fatCondition != "" && fatCondition != "正常") {
			rqflResult = rqflResult + fatCondition + ",";
		}
		if(bloodSugarCondition == "" && bloodLipidCondition == "" && bloodPressureCondition == "" && fatCondition == "") {
			rqflResult = "检测信息缺失，无法判断";
		} else if(bloodSugarCondition == "正常" && bloodLipidCondition == "正常" && bloodPressureCondition == "正常" && fatCondition == "正常") {
			rqflResult = "初筛检测指标无异常";
		}else if (rqflResult != "") {
			rqflResult = rqflResult.substring(0,rqflResult.length-1);
		}
		$("#classifyResult").val(rqflResult);
		
		var customerId = $('#customerId').val();
		if (customerId != "") {
			/* if(bloodSugarCondition != "糖尿病患者" && bloodLipidCondition != "血脂异常患者" && bloodPressureCondition != "高血压患者") {
				$("#tangniaobing").prop("checked",false); 
				$("#xueshuan").prop("checked",false); 
				$("#getihua").prop("checked",false); 
			} else if (bloodSugarCondition == "糖尿病患者" && bloodLipidCondition != "血脂异常患者" && bloodPressureCondition != "高血压患者") {
				$("#tangniaobing").prop("checked",true); 
				$("#xueshuan").prop("checked",false); 
				$("#getihua").prop("checked",true); 
			} else if (bloodSugarCondition != "糖尿病患者" && (bloodLipidCondition == "血脂异常患者" || bloodPressureCondition == "高血压患者")) {
				$("#tangniaobing").prop("checked",false); 
				$("#xueshuan").prop("checked",true); 
				$("#getihua").prop("checked",true); 
			} else if(bloodSugarCondition == "糖尿病患者" && (bloodLipidCondition == "血脂异常患者" || bloodPressureCondition == "高血压患者")) {
				$("#tangniaobing").prop("checked",true); 
				$("#xueshuan").prop("checked",true); 
				$("#getihua").prop("checked",true);  
			} */
			
			var dmTag = $("#dmTag").val();
			var item = $("#item").val();
			
			if (rqflResult.indexOf("糖尿病患者")!=-1) {
				$("#"+item+"tangniaobing").prop("checked",true); 
			} else if (rqflResult.indexOf("血糖异常人群")!=-1){
				if (dmTag == "疑似糖尿病") {
					$("#"+item+"tangniaobing").prop("checked",true);
				} else {
					$("#"+item+"tangniaobing").prop("checked",false);
				}
			} else {
				$("#"+item+"tangniaobing").prop("checked",false); 
			}
			
			if (rqflResult.indexOf("高血压患者")!=-1) {
				$("#"+item+"gaoxueya").prop("checked",true); 
			} else {
				$("#"+item+"gaoxueya").prop("checked",false); 
			}
			
			if (rqflResult.indexOf("血脂异常患者")!=-1) {
				$("#"+item+"gaoxuezhi").prop("checked",true); 
			} else {
				$("#"+item+"gaoxuezhi").prop("checked",false); 
			}
			
			if (rqflResult == "肥胖人群" || rqflResult == "超重人群") {
				if (bloodSugarCondition == "正常" && bloodLipidCondition == "正常" && bloodPressureCondition == "正常") {
					rqflResult = "初筛检测指标无异常";
				} else {
					rqflResult = "";
				}
				
			}else if (rqflResult.indexOf("肥胖人群")!=-1) {
				rqflResult = rqflResult.replace("肥胖人群", "");
			}else if (rqflResult.indexOf("超重人群")!=-1) {
				rqflResult = rqflResult.replace("超重人群", "");
			}
			
			var visit = $("#visit").val();
			//alert("visit:" + visit);
			if(rqflResult == "" ) {
				$("#geneTestNoNeddJs").hide();
				$("#geneTestNoNedd").show();
				$("#geneTestNoNedd").children().prop("checked", false);
				$("#geneTestNoNeddJs").children().prop("checked", false);
				
				$("#visitDiv").hide();
				
			} else if (rqflResult == "初筛检测指标无异常") {
				$("#geneTestNedd").show();
				$("#geneTestNoNedd").show();
				$("#geneTestNoNeddJs").hide();
				$("#geneTestNeddJs").hide();
				
				$("#geneTestNoNedd").children().prop("checked", true);
				$("#geneTestNedd").children().prop("checked", false);
				
				//if (visit != "是") {
					$("#visitDiv").hide();
					//$("#visitYes").prop("checked", false);
					//$("#visitNo").prop("checked", false);
				//}
			} else if (rqflResult.indexOf("患者")!=-1 && rqflResult.indexOf("人群")==-1) {
				$("#geneTestNeddJs").hide();
				$("#geneTestNedd").show();
				$("#geneTestNedd").children().prop("checked", true);
				
				if (visit != "是") {
					$("#visitDiv").show();
					//$("#visitYes").prop("checked", true);
				} else {
					$("#visitDiv").hide();
				}
			} else if (rqflResult.indexOf("人群")!=-1 && rqflResult.indexOf("患者")==-1 ) {
				if (dmTag == "疑似糖尿病"){
					if(rqflResult.indexOf("血压异常人群")!=-1 || rqflResult.indexOf("血脂异常高风险人群")!=-1) {
						$("#geneTestNeddJs").show();
						$("#geneTestNedd").hide();
						$("#geneTestNeddJs").children().prop("checked", true);
					} else {
						$("#geneTestNeddJs").hide();
						$("#geneTestNedd").show();
						$("#geneTestNedd").children().prop("checked", true);
					}
				} else {
					$("#geneTestNoNeddJs").show();
					$("#geneTestNoNedd").hide();
					$("#geneTestNoNeddJs").children().prop("checked", true);
				}
				
				//if (visit != "是") {
					$("#visitDiv").hide();
					//$("#visitYes").prop("checked", false);
					//$("#visitNo").prop("checked", false);
				//}
			} else if (rqflResult.indexOf("患者")!=-1 && rqflResult.indexOf("人群")!=-1) {
				if (dmTag == "疑似糖尿病" || rqflResult.indexOf("糖尿病患者")!=-1) {
					if (rqflResult.indexOf("血压异常人群")!=-1 || rqflResult.indexOf("血脂异常高风险人群")!=-1) {
						$("#geneTestNeddJs").show();
						$("#geneTestNedd").hide();
						$("#geneTestNeddJs").children().prop("checked", true);
					} else {
						$("#geneTestNeddJs").hide();
						$("#geneTestNedd").show();
						$("#geneTestNedd").children().prop("checked", true);
					}
				} else {
					$("#geneTestNeddJs").show();
					$("#geneTestNedd").hide();
					$("#geneTestNeddJs").children().prop("checked", true);
				}
				
				//if (visit != "是") {
					$("#visitDiv").hide();
					//$("#visitYes").prop("checked", false);
					//$("#visitNo").prop("checked", false);
				//}
			} else if (rqflResult == '检测信息缺失，无法判断') {
				$("#geneTestNedd").show();
				$("#geneTestNoNedd").show();
				$("#geneTestNoNeddJs").hide();
				$("#geneTestNeddJs").hide();
				
				$("#geneTestNoNedd").children().prop("checked", false);
				$("#geneTestNedd").children().prop("checked", false);
				
				//if (visit != "是") {
					$("#visitDiv").hide();
					//$("#visitYes").prop("checked", false);
					//$("#visitNo").prop("checked", false);
				//}
			} else {
				$("#geneTestNedd").show();
				$("#geneTestNoNedd").show();
				$("#geneTestNoNeddJs").hide();
				$("#geneTestNeddJs").hide();
				
				$("#geneTestNoNedd").children().prop("checked", true);
				$("#geneTestNedd").children().prop("checked", false);
				
				//if (visit != "是") {
					$("#visitDiv").hide();
					//$("#visitYes").prop("checked", false);
					//$("#visitNo").prop("checked", false);
				//}
			}
		}
    }
    
    function historyData() {
    	var children = $("#csInfo").find("input");
		children.each(function(index, element) {
			if (element.type =='text' && (element.id!="bloodSugarCondition" && element.id!="bloodLipidCondition" 
					&& element.id!="bloodPressureCondition" && element.id!="classifyResult")){
				var id = element.id;
				var name = "";
				if (id == "highPressure" || id == "lowPressure") {
					name = "bloodPressure";
				} else if (id == "bloodGlucose" || id == "bloodGlucose2h" || id == "bloodGlucoseRandom") {
					name = "bloodSugger";
				} else if (id == "tc" || id == "tg" || id == "ldl" || id == "hdl") {
					name = "bloodLipid";
				} else {
					name = id;
				}
				
				//var ids = "'" + id + "'";
				//console.log(id);
				$("#" + id).parent().append('<a href="#" onclick="javascript:showHistory(\''+name+'\')"><img src="${ctx_static}/img/historydata1.png" style="width:20px;height:19px;"></a>');
			}
		});
    }
 
	//展示历史数据
	function showHistory(type) {
		var uniqueId = $("#uniqueId").val();
		var id = $("#primarykeyId").val();
		var checkDate = $("#checkDate").val();
		//console.log(uniqueId + "----" + type + "-----" + id);
		window.location.href = "${ctx}/manage/hc/getHealthHistoryData.htm?type=" + type + "&uniqueId=" + uniqueId + "&dataId=" + id + "&checkDate=" + checkDate;
	}

    function changeF(this_) {
        $(this_).prev("input").val($(this_).find("option:selected").text());
        $("#typenum").css({"display":"none"});
    }
    function setfocus(this_){
        $("#typenum").css({"display":""});
        var select = $("#typenum");
        for(i=0;i<TempArr.length;i++){
            var option = $("<option></option>").text(TempArr[i]);
            select.append(option);
        }
    }

    function setinput(this_) {
        var select = $("#typenum");
        select.html("");
        for (i = 0; i < TempArr.length; i++) {
            //若找到以txt的内容开头的，添option
            if (TempArr[i].substring(0, this_.value.length).indexOf(this_.value) == 0) {
                var option = $("<option></option>").text(TempArr[i]);
                select.append(option);
            }
        }
    }

    function searchList(strValue) {
        var count = 0;
        if (strValue != "") {
            $("#nice-select ul li").each(function(i) {
                var contentValue = $(this).text();
                console.log(this);
                if (contentValue.toLowerCase().indexOf(strValue.toLowerCase()) < 0) {
                    $(this).hide();
                    count++;
                } else {
                    $(this).show();
                }
                debugger;
                if (count == (i + 1)) {
                	tizhiFlag = false;
                    $("#nice-select").find("ul").hide();
                } else {
                	tizhiFlag = true;
                    $("#nice-select").find("ul").show();
                } 
            });
        } else {
            $("#nice-select ul li").each(function(i) {
                $(this).show();
                $("#nice-select").find("ul").show();
            });
            tizhiFlag = true;
        }

    }
    
    $(document).on("click",'#nice-select ul li', function(){ 
    	var str = $(this).html();
        $("#tizhi").val(str);
        $("#nice-select").find("ul").hide();
    });
    
    function tizhiSearch(tizhiResults) {
    	
    	$("#nice-select ul li").remove();
		$.each(tizhiResults, function(key, value){
    		$("#nice-select ul").append('<li>' + value + '</li>');
    	});
		
		/*先将数据存入数组*/
		$("#typenum option").each(function(index, el) {
            TempArr[index] = $(this).text();
        });
		$(document).bind(
			'click',
			function(e) {
				var e = e || window.event; //浏览器兼容性
				var elem = e.target || e.srcElement;
				while (elem) { //循环判断至跟节点，防止点击的是div子元素
					if (elem.id
							&& (elem.id == 'typenum' || elem.id == "makeupCo")) {
						return;
					}
					elem = elem.parentNode;
				}
				$('#typenum').css('display', 'none'); //点击的不是div或其子元素
		 });
    }
    
</script>


</html>