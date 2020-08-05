<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<style>
.row .form-group label{
	text-align: right!important;
	    height: 40px!important;
    line-height: 40px!important;
}
.row .row label{
	text-align: left!important;
}
	.qlist{
	 	padding-left: 30px !important;
	}
	.row{
		padding-left:15px;
		margin-bottom:15px;
	}
</style>
<title>录入问卷信息</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<script src="${ctx_jquery }/jquery.validate.js"></script>
<script src="${ctx_jquery }/card.js"></script>
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
<script type="text/javascript" src="${ctx_static}/js/region.js"></script>
<script language="javascript" type="text/javascript" src="${ctx_static}/My97DatePicker/WdatePicker.js"></script>
    
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
           <div >
             <div class="panel panel-warning">
                <div class="panel-body form-body">
	               	 <form id="questionForm" role="form" method="post">
						<input type="hidden" id=dataId name="dataId" value="${editEntity.id}" />
						<input type="hidden" name="uniqueId" id="uniqueId" value="${editEntity.uniqueId }">
						<input type="hidden" id="takingDrugsIds" name="takingDrugsIds" />
						<input type="hidden" id="beIllIds" name="beIllIds" />
						<input type="hidden" id="relativesBeIllIds" name="relativesBeIllIds" />
					       	<h4 class="block-title"><span>基本信息</span></h4>
					       	
							<div class="form-group">
								<label for="customerId" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>身份证号</label>
								<div class="col-sm-9 controls checkbox-list">
									<input type="text" id="customerId" readonly class="form-control" name="customerId" value="${editEntity.customerId}"
										placeholder="请输入身份证号" style="width: 200px; padding: 7px 10px; display:inline-block;border: 1px solid #ccc; margin-right: 10px;">
									<input type="button" id="inputIdcardButton" class="btn btn-primary " <c:if test="${editEntity != null}">style="display:none"</c:if> value="手工录入" onclick="javascript:inputId()">
									<input type="button" id="inputQueryButton" class="btn btn-primary " style="display:none" value="查询" 
										onclick="javascript:queryByCustomerId()">
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>姓名</label>
								<div class="col-sm-9 controls checkbox-list">
									<input type="text" class="form-control" id="customerName" name="name" value="${editEntity.name}"
										readonly placeholder="请输入用户名">
								</div>
							</div>
							<div class="form-group">
								<label for="gender" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>性别</label>
								<div class="col-sm-9 controls checkbox-list">
									<input type="text" class="form-control" id="gender" name="gender" value="${editEntity.gender}" readonly>
								</div>
							</div>

							<div class="form-group">
								<label for="age" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>年龄</label>
								<div class="col-sm-9 controls checkbox-list">
									<input type="text" class="form-control" id="age" readonly="readonly" name="age" value="${editEntity.age}"
										onblur="bloodSugarConditionCharge();">
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>是否患有疾病</label>
								<div class="col-sm-9 controls checkbox-list">
									<c:if test="${editEntity.haveDisease=='是' }">
									<c:set var="diseaseShow" value="${editEntity.disease }"/>
									</c:if>
									<c:if test="${editEntity.haveDisease=='否' }">
									<c:set var="diseaseShow" value="否"/>
									</c:if>
									
									<input type="text"  class="form-control" readonly id="diseaseShow" value="${diseaseShow }"/>
									<input type="hidden" id="disease" name="disease" value="${editEntity.disease }"/>
									<input type="hidden" id="haveDisease" name="haveDisease" value="${editEntity.haveDisease }"/>
								</div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>是否有疾病家族史</label>
								<div class="col-sm-9 controls checkbox-list">
									<c:if test="${editEntity.familyHistory=='是' }">
									<c:set var="familyHistoryShow" value="${editEntity.familyDisease }"/>
									</c:if>
									<c:if test="${editEntity.familyHistory=='否' }">
									<c:set var="familyHistoryShow" value="否"/>
									</c:if>
									<input type="text"  class="form-control" readonly id="familyHistoryShow" value="${familyHistoryShow }"/>
									<input type="hidden" id="familyHistory" name="familyHistory" value="${editEntity.familyHistory }"/>
									<input type="hidden" id="familyDisease" name="familyDisease" value="${editEntity.familyDisease }"/>
								</div>
							</div>
							<br/>
							<div class="form-group">
								<label for="mobile" class="col-sm-3 control-lavel">联系电话1</label>
								<div class="col-sm-9 controls checkbox-list">
									<input type="text" class="form-control" id="mobile" name="mobile" value="${editEntity.mobile}" onblur="bloodSugarConditionCharge();" <c:if test="${editEntity != null}">readonly</c:if>>
								</div>
							</div>
							<div class="form-group">
								<label for="mobile" class="col-sm-3 control-lavel">联系电话2</label>
								<div class="col-sm-9 controls checkbox-list">
									<input type="text" class="form-control" id="mobile2" name="mobile2" value="${editEntity.mobile2}" >
								</div>
							</div>
							
							<div class="form-group">
								<label for="checkDate" class="col-sm-3 control-lavel"><span style='color:red; <c:if test="${editEntity != null}">display:none;</c:if>'>*</span>问卷调查日期</label>
								<div class="col-sm-9 controls checkbox-list">
									<input class="Wdate" id="checkDate" name="checkDate" readonly type="text" value="${editEntity != null ? editEntity.checkDate : checkDate }"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
								</div>
							</div>
							<br />
							<div class="form-group">
								<label for="nation" class="col-sm-3 control-lavel">民族</label>
								<div class="col-sm-9 controls checkbox-list" style="display:inline">
									<label><input type="radio" name="nation" value="汉族" onchange="changeNationOption(this)" <c:if test="${fn:contains(editEntity.nation,'汉族')}">checked="checked"</c:if>>汉族</label>
									<label><input type="radio" name="nation" value="其他" onchange="changeNationOption(this)" <c:if test="${fn:contains(editEntity.nation,'其他')}">checked="checked"</c:if>>其他</label>
									&nbsp;&nbsp;&nbsp;<input type="text" <c:if test="${!fn:contains(editEntity.nation,'其他')}">style="display:none"</c:if> name="nationName" id="nationOtherDiv" value="${editEntity.nationName }">
								</div>
							</div>
							<div class="form-group">
								<label for="placeRresidence" class="col-sm-3 control-lavel">居住地</label>
								<div class="col-sm-9 controls checkbox-list">
									<label><input type="radio"  name="placeRresidence" value="农村"  <c:if test="${fn:contains(editEntity.placeRresidence,'农村')}">checked="checked"</c:if>>农村</label>
									<label><input type="radio" name="placeRresidence" value="城镇" <c:if test="${fn:contains(editEntity.placeRresidence,'城镇')}">checked="checked"</c:if>>城镇</label>
								</div>
							</div>
							<div class="form-group">
								<label for="birthPlace" class="col-sm-3 control-lavel">出生地</label>
								<div class="col-sm-9 controls checkbox-list">
									<select id="province" name="province" ></select>省&nbsp;&nbsp;
									<select id="city" name="city"></select>市&nbsp;&nbsp;
									<select id="area" name="area"></select>区/县&nbsp;&nbsp;</label>
								</div>
							</div>
							<br />
							<div class="form-group">
								<label for="degreeEducation" class="col-sm-3 control-lavel">文化程度</label>
								<div class="col-sm-9 controls checkbox-list">
									<label><input type="radio" id="degreeEducation" name="degreeEducation" value="小学/以下"  <c:if test="${fn:contains(editEntity.degreeEducation,'小学/以下')}">checked="checked"</c:if>>小学/以下</label>
									<label><input type="radio" id="degreeEducation" name="degreeEducation" value="初中"  <c:if test="${fn:contains(editEntity.degreeEducation,'初中')}">checked="checked"</c:if>>初中</label>
									<label><input type="radio" id="degreeEducation" name="degreeEducation" value="高中/中专"  <c:if test="${fn:contains(editEntity.degreeEducation,'高中/中专')}">checked="checked"</c:if>>高中/中专</label>
									<label><input type="radio" id="degreeEducation" name="degreeEducation" value="大专/大学"  <c:if test="${fn:contains(editEntity.degreeEducation,'大专/大学')}">checked="checked"</c:if>>大专/大学</label>
									<label><input type="radio" id="degreeEducation" name="degreeEducation" value="硕士/博士"  <c:if test="${fn:contains(editEntity.degreeEducation,'硕士/博士')}">checked="checked"</c:if>>硕士/博士</label>
								</div>
							</div>
							<br />
							<div class="form-group">
								<label for="currentAddress" class="col-sm-3 control-lavel" style="margin-right: 15px;">现住址</label>
								<div style="width: 130%;">
									<select id="provinceNow" name="provinceNow"></select>省&nbsp;&nbsp;
									<select id="cityNow" name="cityNow"></select>市&nbsp;&nbsp;
									<select id="areaNow" name="areaNow"></select>区/县&nbsp;&nbsp;&nbsp;
									<input type="text" style="width: 120px;height: 40px;" name="streetName" value="${editEntity.streetName }">街道/路/村
								</div>
							</div>
							<br/>
				          	<h4 class="block-title"><span>问卷信息</span></h4>
							<div class="row">
								<label for="income" class="col-sm-12 control-lavel">去年您全家（一年有6个月以上住在一起的成员）一年的总收入</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="income" value="不足3万" <c:if test="${fn:contains(editEntity.income,'不足3万')}">checked="checked"</c:if>>不足3万</label>
									<label><input type="radio" name="income" value="3-5万" <c:if test="${fn:contains(editEntity.income,'3-5万')}">checked="checked"</c:if>>3-5万</label>
									<label><input type="radio" name="income" value="5-10万" <c:if test="${fn:contains(editEntity.income,'5-10万')}">checked="checked"</c:if>>5-10万</label>
									<label><input type="radio" name="income" value="10-20万" <c:if test="${fn:contains(editEntity.income,'10-20万')}">checked="checked"</c:if>>10-20万</label>
									<label><input type="radio" name="income" value="20万以上" <c:if test="${fn:contains(editEntity.income,'20万以上')}">checked="checked"</c:if>>20万以上</label>
								</div>
							</div>
							
							<div class="row">
								<label for="takingDrugs" class="col-sm-12 control-lavel">1.您目前在服用以下药物吗？</label>
								<div class="col-sm-12 checkbox-list" style="padding-left:20px;">
									<label><input type="checkbox" name="takingDrugs" value="降压药" <c:if test="${fn:contains(editEntity.takingDrugsIds,'降压药')}">checked="checked"</c:if>>降压药</label>
									<label><input type="checkbox" name="takingDrugs" value="降脂药" <c:if test="${fn:contains(editEntity.takingDrugsIds,'降脂药')}">checked="checked"</c:if>>降脂药</label>
									<label><input type="checkbox" name="takingDrugs" value="降血糖药" <c:if test="${fn:contains(editEntity.takingDrugsIds,'降血糖药')}">checked="checked"</c:if>>降血糖药</label>
									<label><input type="checkbox" name="takingDrugs" value="止痛药" <c:if test="${fn:contains(editEntity.takingDrugsIds,'止痛药')}">checked="checked"</c:if>>止痛药</label>
									<label><input type="checkbox" name="takingDrugs" value="抗生素" <c:if test="${fn:contains(editEntity.takingDrugsIds,'抗生素')}">checked="checked"</c:if>>抗生素</label>
									<label><input type="checkbox" name="takingDrugs" id="takingDrugs" value="无" <c:if test="${fn:contains(editEntity.takingDrugsIds,'无')}">checked="checked"</c:if>>无</label>
								</div>
							</div>
							
							<div class="row">
								<label for="beIll" class="col-sm-12 control-lavel">2.您本人是否确诊患以下病症？（在二级及以上医院确诊）</label>
								<div class="col-sm-12 checkbox-list" style="padding-left:20px;">
									<label><input type="checkbox" name="beIll"  value="高血压" <c:if test="${fn:contains(editEntity.beIllIds,'高血压')}">checked="checked"</c:if>>高血压</label>
									<label><input type="checkbox" name="beIll" value="高血脂" <c:if test="${fn:contains(editEntity.beIllIds,'高血脂')}">checked="checked"</c:if>>高血脂</label>
									<label><input type="checkbox" name="beIll" value="冠心病" <c:if test="${fn:contains(editEntity.beIllIds,'冠心病')}">checked="checked"</c:if>>冠心病</label>
									<label><input type="checkbox" name="beIll" value="糖尿病" <c:if test="${fn:contains(editEntity.beIllIds,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
									<label><input type="checkbox" name="beIll" value="心肌梗塞" <c:if test="${fn:contains(editEntity.beIllIds,'心肌梗塞')}">checked="checked"</c:if>>心肌梗塞</label>
									<label><input type="checkbox" name="beIll" value="中风" <c:if test="${fn:contains(editEntity.beIllIds,'中风')}">checked="checked"</c:if>>中风</label>
									<label><input type="checkbox" name="beIll" value="肺气肿" <c:if test="${fn:contains(editEntity.beIllIds,'肺气肿')}">checked="checked"</c:if>>肺气肿</label>
									<label><input type="checkbox" name="beIll" value="肺结核" <c:if test="${fn:contains(editEntity.beIllIds,'肺结核')}">checked="checked"</c:if>>肺结核</label>
									<label><input type="checkbox" name="beIll" value="哮喘" <c:if test="${fn:contains(editEntity.beIllIds,'哮喘')}">checked="checked"</c:if>>哮喘</label>
									<label><input type="checkbox" name="beIll" value="胆结石" <c:if test="${fn:contains(editEntity.beIllIds,'胆结石')}">checked="checked"</c:if>>胆结石</label>
									<label><input type="checkbox" name="beIll" value="慢性肝炎" <c:if test="${fn:contains(editEntity.beIllIds,'慢性肝炎')}">checked="checked"</c:if>>慢性肝炎</label>
									<label><input type="checkbox" name="beIll" value="肾炎" <c:if test="${fn:contains(editEntity.beIllIds,'肾炎')}">checked="checked"</c:if>>肾炎</label>
									<label><input type="checkbox" name="beIll" value="甲亢/甲低" <c:if test="${fn:contains(editEntity.beIllIds,'甲亢/甲低')}">checked="checked"</c:if>>甲亢/甲低</label>
									<label><input type="checkbox" name="beIll" value="肿瘤" <c:if test="${fn:contains(editEntity.beIllIds,'肿瘤')}">checked="checked"</c:if>>肿瘤</label>
									<label><input type="checkbox" name="beIll" value="无"  id="beIll" <c:if test="${fn:contains(editEntity.beIllIds,'无')}">checked="checked"</c:if>>无</label>
								</div>
							</div>
							
							<div class="row">
								<label for="relativesBeIll" class="col-sm-12 control-lavel">3.您的父母或兄弟姐妹是否确诊患以下病症？（在二级及以上医院确诊）</label>
								<div class="col-sm-12 checkbox-list" style="padding-left:20px;">
									<label><input type="checkbox" name="relativesBeIll" value="高血压" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'高血压')}">checked="checked"</c:if>>高血压</label>
									<label><input type="checkbox" name="relativesBeIll" value="高血脂" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'高血脂')}">checked="checked"</c:if>>高血脂</label>
									<label><input type="checkbox" name="relativesBeIll" value="冠心病" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'冠心病')}">checked="checked"</c:if>>冠心病</label>
									<label><input type="checkbox" name="relativesBeIll" value="糖尿病" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
									<label><input type="checkbox" name="relativesBeIll" value="心肌梗塞" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'心肌梗塞')}">checked="checked"</c:if>>心肌梗塞</label>
									<label><input type="checkbox" name="relativesBeIll" value="中风" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'中风')}">checked="checked"</c:if>>中风</label>
									<label><input type="checkbox" name="relativesBeIll" value="肺气肿" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'肺气肿')}">checked="checked"</c:if>>肺气肿</label>
									<label><input type="checkbox" name="relativesBeIll" value="肺结核" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'肺结核')}">checked="checked"</c:if>>肺结核</label>
									<label><input type="checkbox" name="relativesBeIll" value="哮喘" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'哮喘')}">checked="checked"</c:if>>哮喘</label>
									<label><input type="checkbox" name="relativesBeIll" value="胆结石" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'胆结石')}">checked="checked"</c:if>>胆结石</label>
									<label><input type="checkbox" name="relativesBeIll" value="慢性肝炎" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'慢性肝炎')}">checked="checked"</c:if>>慢性肝炎</label>
									<label><input type="checkbox" name="relativesBeIll" value="肾炎" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'肾炎')}">checked="checked"</c:if>>肾炎</label>
									<label><input type="checkbox" name="relativesBeIll" value="甲亢/甲低" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'甲亢/甲低')}">checked="checked"</c:if>>甲亢/甲低</label>
									<label><input type="checkbox" name="relativesBeIll" value="肿瘤" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'肿瘤')}">checked="checked"</c:if>>肿瘤</label>
									<label><input type="checkbox" name="relativesBeIll" id="relativesBeIll" value="无" <c:if test="${fn:contains(editEntity.relativesBeIllIds,'无')}">checked="checked"</c:if>>无</label>
								</div>
							</div>
								<div class="row" style="padding-left:15px;margin-bottom:15px;">
								<label for="smoke" class="col-sm-12 control-lavel">4.您目前是否吸烟？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="smoke" value="吸烟（每天>5支）" <c:if test="${fn:contains(editEntity.smoke,'吸烟（每天>5支）')}">checked="checked"</c:if>>吸烟（每天>5支）</label>
									<label><input type="radio" name="smoke" value="偶尔吸（每天1-5支）" <c:if test="${fn:contains(editEntity.smoke,'偶尔吸（每天1-5支）')}">checked="checked"</c:if>>偶尔吸（每天1-5支）</label>
									<label><input type="radio" name="smoke" value="基本不吸烟（一天<1支）" <c:if test="${fn:contains(editEntity.smoke,'基本不吸烟（一天<1支）')}">checked="checked"</c:if>>基本不吸烟（一天<1支）</label>
								</div>
							</div>
							
							<div class="row" >
								<label for="quitSmoke" class="col-sm-12 control-lavel qlist">如已戒烟，戒烟前的情况</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="quitSmoke" value="每天>5支" <c:if test="${fn:contains(editEntity.quitSmoke,'每天>5支')}">checked="checked"</c:if>>每天>5支</label>
									<label><input type="radio" name="quitSmoke" value="每天1-5支" <c:if test="${fn:contains(editEntity.quitSmoke,'每天1-5支')}">checked="checked"</c:if>>每天1-5支</label>
								</div>
							</div>
							<div class="row">
								<label for="pastDisease" class="col-sm-12 control-lavel qlist">您有没有吸入别人吸烟产生的烟雾（被动吸烟）？</label>
								<label for="passiveSmokeWeek" class="col-sm-3 control-lavel qlist">平均每周被动吸烟：</label>
								<div class="col-sm-9 checkbox-list">
									<label><input type="radio" name="passiveSmokeWeek" value="<1天" <c:if test="${fn:contains(editEntity.passiveSmokeWeek,'<1天')}">checked="checked"</c:if>><1天</label>
									<label><input type="radio" name="passiveSmokeWeek" value="1-2天" <c:if test="${fn:contains(editEntity.passiveSmokeWeek,'1-2天')}">checked="checked"</c:if>>1-2天</label>
									<label><input type="radio" name="passiveSmokeWeek" value="3-5天" <c:if test="${fn:contains(editEntity.passiveSmokeWeek,'3-5天')}">checked="checked"</c:if>>3-5天</label>
									<label><input type="radio" name="passiveSmokeWeek" value="≥6天" <c:if test="${fn:contains(editEntity.passiveSmokeWeek,'≥6天')}">checked="checked"</c:if>>≥6天</label>
								</div>
								<div style="clear:both;"></div>
								<label for="passiveSmokeDay" class="col-sm-3 control-lavel qlist">平均每天被动吸烟时长：</label>
								<div class="col-sm-9 checkbox-list">
									<label><input type="radio" name="passiveSmokeDay" value="<30分钟" <c:if test="${fn:contains(editEntity.passiveSmokeDay,'<30分钟')}">checked="checked"</c:if>><30分钟</label>
									<label><input type="radio" name="passiveSmokeDay" value="30-60分钟" <c:if test="${fn:contains(editEntity.passiveSmokeDay,'30-60分钟')}">checked="checked"</c:if>>30-60分钟</label>
									<label><input type="radio" name="passiveSmokeDay" value=">60分钟" <c:if test="${fn:contains(editEntity.passiveSmokeDay,'>60分钟')}">checked="checked"</c:if>>>60分钟</label>
								</div>
							</div>
							<div class="row">
								<label for="smokeLength" class="col-sm-3 control-lavel qlist">您的烟龄（包括被动吸烟）为：</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="smokeLength" value=">10年" <c:if test="${fn:contains(editEntity.smokeLength,'>10年')}">checked="checked"</c:if>>>10年</label>
									<label><input type="radio" name="smokeLength" value="5-10年" <c:if test="${fn:contains(editEntity.smokeLength,'5-10年')}">checked="checked"</c:if>>5-10年</label>
									<label><input type="radio" name="smokeLength" value="<5年" <c:if test="${fn:contains(editEntity.smokeLength,'<5年')}">checked="checked"</c:if>><5年</label>
								</div>
							</div>
							
							<div class="row">
							<label for="drink" class="col-sm-12 control-lavel">5.您目前是否喝酒？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="drink" value="喝酒（每周7次以上）" <c:if test="${fn:contains(editEntity.drink,'喝酒（每周7次以上）')}">checked="checked"</c:if>>喝酒（每周7次以上）</label>
									<label><input type="radio" name="drink" value="偶尔喝（每周2-7次）" <c:if test="${fn:contains(editEntity.drink,'偶尔喝（每周2-7次）')}">checked="checked"</c:if>>偶尔喝（每周2-7次）</label>
									<label><input type="radio" name="drink" value="基本不喝酒（每周<=1次）" <c:if test="${fn:contains(editEntity.drink,'基本不喝酒（每周<=1次）')}">checked="checked"</c:if>>基本不喝酒（每周<=1次）</label>
								</div>
							</div>
							
							<div class="row">
								<label for="quitDrink" class="col-sm-3 control-lavel qlist">如已戒酒，戒酒前的情况:</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="quitDrink" value="每周>7次" <c:if test="${fn:contains(editEntity.quitDrink,'每周>7次')}">checked="checked"</c:if>>每周>7次</label>
									<label><input type="radio" name="quitDrink" value="每周2-7次" <c:if test="${fn:contains(editEntity.quitDrink,'每周2-7次')}">checked="checked"</c:if>>每周2-7次</label>
								</div>
							</div>
							
							<div class="row ">
								<label for="drinkLength" class="col-sm-3 control-lavel qlist">您的酒龄为:</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="drinkLength" value=">10年" <c:if test="${fn:contains(editEntity.drinkLength,'>10年')}">checked="checked"</c:if>>10年</label>
									<label><input type="radio" name="drinkLength" value="5-10年" <c:if test="${fn:contains(editEntity.drinkLength,'5-10年')}">checked="checked"</c:if>>5-10年</label>
									<label><input type="radio" name="drinkLength" value="<5年" <c:if test="${fn:contains(editEntity.drinkLength,'<5年')}">checked="checked"</c:if>><5年</label>
								</div>
							</div>
							
							<div class="row">
								<label for="haveMealsFamily" class="col-sm-12 control-lavel">6.最近一年，您在家用餐的次数？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="haveMealsFamily" value="几乎每天" <c:if test="${fn:contains(editEntity.haveMealsFamily,'几乎每天')}">checked="checked"</c:if>>几乎每天</label>
									<label><input type="radio" name="haveMealsFamily" value="每周1-2次" <c:if test="${fn:contains(editEntity.haveMealsFamily,'每周1-2次')}">checked="checked"</c:if>>每周1-2次</label>
									<label><input type="radio" name="haveMealsFamily" value="每月1-2次" <c:if test="${fn:contains(editEntity.haveMealsFamily,'每月1-2次')}">checked="checked"</c:if>>每月1-2次</label>
									<label><input type="radio" name="haveMealsFamily" value="极少" <c:if test="${fn:contains(editEntity.haveMealsFamily,'极少')}">checked="checked"</c:if>>极少</label>
								</div>
							</div>
							
							<div class="row">
								<label for="insomnia" class="col-sm-12 control-lavel">7.您经常失眠吗？每天夜间睡眠时间如何？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="insomnia" value="经常失眠<5小时" <c:if test="${fn:contains(editEntity.insomnia,'经常失眠<5小时')}">checked="checked"</c:if>>经常失眠<5小时</label>
									<label><input type="radio" name="insomnia" value="偶尔失眠5-8小时" <c:if test="${fn:contains(editEntity.insomnia,'偶尔失眠5-8小时')}">checked="checked"</c:if>>偶尔失眠5-8小时</label>
									<label><input type="radio" name="insomnia" value="不失眠>8小时" <c:if test="${fn:contains(editEntity.insomnia,'不失眠>8小时')}">checked="checked"</c:if>>不失眠>8小时</label>
								</div>
							</div>
							
							<div class="row">
								<label for="physicalExercise" class="col-sm-12 control-lavel">8.最近一年，您是否经常锻炼身体？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="physicalExercise" value="≥5次/周" <c:if test="${fn:contains(editEntity.physicalExercise,'≥5次/周')}">checked="checked"</c:if>>≥5次/周</label>
									<label><input type="radio" name="physicalExercise" value="2-4次/周" <c:if test="${fn:contains(editEntity.physicalExercise,'2-4次/周')}">checked="checked"</c:if>>2-4次/周</label>
									<label><input type="radio" name="physicalExercise" value="1次/周" <c:if test="${fn:contains(editEntity.physicalExercise,'1次/周')}">checked="checked"</c:if>>1次/周</label>
									<label><input type="radio" name="physicalExercise" value="无（平均每月少于3次）" <c:if test="${fn:contains(editEntity.physicalExercise,'无（平均每月少于3次）')}">checked="checked"</c:if>>无（平均每月少于3次）</label>
								</div>
							</div>
							
							<div class="row">
								<label for="livingEnvironment" class="col-sm-12 control-lavel">9.您对自己现在的居住环境是否满意？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="livingEnvironment" value="满意" <c:if test="${fn:contains(editEntity.livingEnvironment,'满意')}">checked="checked"</c:if>>满意</label>
									<label><input type="radio" name="livingEnvironment" value="基本满意" <c:if test="${fn:contains(editEntity.livingEnvironment,'基本满意')}">checked="checked"</c:if>>基本满意</label>
									<label><input type="radio" name="livingEnvironment" value="不满意" <c:if test="${fn:contains(editEntity.livingEnvironment ,'不满意')}">checked="checked"</c:if>>不满意</label>
								</div>
							</div>
							
							<div class="row">
								<label for="nightShift" class="col-sm-12 control-lavel">10.您最近3个月内经常倒班或夜班吗？（一周2次以上）</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="nightShift" value="是" <c:if test="${fn:contains(editEntity.nightShift,'是')}">checked="checked"</c:if>>是</label>
									<label><input type="radio" name="nightShift" value="否" <c:if test="${fn:contains(editEntity.nightShift,'否')}">checked="checked"</c:if>>否</label>
								</div>
							</div>
							
							<div class="row">
								<label for="livingEnvironment" class="col-sm-12 control-lavel">11.您喝可乐、雪碧、芬达等碳酸饮料吗？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="drinkRefresh" value="不喝或很少喝" <c:if test="${fn:contains(editEntity.drinkRefresh,'不喝或很少喝')}">checked="checked"</c:if>>不喝或很少喝</label>
									<label><input type="radio" name="drinkRefresh" value="1-2次/周" <c:if test="${fn:contains(editEntity.drinkRefresh,'1-2次/周')}">checked="checked"</c:if>>1-2次/周</label>
									<label><input type="radio" name="drinkRefresh" value="3-6次/周" <c:if test="${fn:contains(editEntity.drinkRefresh,'3-6次/周')}">checked="checked"</c:if>>3-6次/周</label>
									<label><input type="radio" name="drinkRefresh" value="≥7次/周" <c:if test="${fn:contains(editEntity.drinkRefresh,'≥7次/周')}">checked="checked"</c:if>>≥7次/周</label>
								</div>
							</div>
							
							<div class="row">
								<label for="livingEnvironment" class="col-sm-12 control-lavel">12.您吃蔬菜水果的频率？</label>
								<div class="col-sm-12 checkbox-list qlist">
									<label><input type="radio" name="eatVegetables" value="每天吃" <c:if test="${fn:contains(editEntity.eatVegetables,'每天吃')}">checked="checked"</c:if>>每天吃</label>
									<label><input type="radio" name="eatVegetables" value="非每天吃" <c:if test="${fn:contains(editEntity.eatVegetables,'非每天吃')}">checked="checked"</c:if>>非每天吃</label>
								</div>
							</div>
					</div>
					<div class="panel-footer">
						<div class="form-group button-center-block">
							<input class="btn btn-primary btn-lg" type="submit" id="saveBtn" value="保存数据">
							<input class="btn btn-primary btn-lg" type="submit" id="saveGenePdfBtn" value="保存并打印报告">
						</div>
					</div>
					</form>
                             </div>
                         </div>
                     </div>
               
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>
</body>

<script type="text/javascript">
	
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
        	window.clearInterval(t2);
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
	
	function queryInfo(idCard) {
		$.ajax({
			type : "post",
			url : "${ctx}/manage/hc/queryInfo.json",
			data : {
				customerId:idCard
			},
			success:function(data){
				if(data.code==0){
					console.log(data);
					var uniqueId = data.dataMap.uniqueId;
					$('#uniqueId').val(uniqueId);
					var mobile = data.dataMap.mobile;
					if (mobile!='') {
						$('#mobile').val(mobile);
			        	$('#mobile').attr("readonly","readonly");
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
					} else if(haveDisease == "是") {
						if (disease == null) {
							disease = '';
						}
						$('#diseaseShow').val(disease);
					}

					var familyDisease = data.dataMap.familyDisease;
					var familyHistory = data.dataMap.familyHistory;
					$('#familyHistory').val(familyHistory);
					$('#familyDisease').val(familyDisease);
					if (familyHistory == "否") {
						$('#familyHistoryShow').val(familyHistory);
					} else if(familyHistory == "是") {
						if (familyDisease == null) {
							familyDisease = '';
						}
						$('#familyHistoryShow').val(familyDisease);
					}
				} else {
					alert("此用户尚未建档，请建档后再进行筛查");
				}
			}
		});

	}
    
    
	 $(document).ready(function(){
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
           
		$("#saveBtn").click(function(){
			saveFormData();
		});
		
		$("#saveGenePdfBtn").click(function(){
			generatePdf();
		});
		
		 jQuery.validator.addMethod("isIdCardNo", function(value, element) { 
		     return this.optional(element) || idCardNoUtil.checkIdCardNo(value);     
		   }, "请正确输入您的身份证号码");
		 
		$("#questionForm").validate({
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
				familyHistory:{
					required:true
				},
				checkDate:{
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
                familyHistory:{
					required:"请选择糖尿病家族史"
				},
				checkDate:{
					required:"请输入问卷调查日期"
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
		
		  $('#banklist').click(function(){
        	  window.location.href="${ctx}/otherInfo/list.htm";
          })
          
      	var province = '${editEntity.province}';
       	var city = '${editEntity.city}';
       	var area = '${editEntity.area}';
       	
       	var provinceNow = '${editEntity.provinceNow}';
       	var cityNow = '${editEntity.cityNow}';
       	var areaNow = '${editEntity.areaNow}';
		addressInit('province', 'city', 'area', province, city, area);
	  	addressInit('provinceNow', 'cityNow', 'areaNow', provinceNow, cityNow, areaNow);
	  	
	  	
	  	checkBoxOpt("takingDrugs");
	  	checkBoxOpt("beIll");
	  	checkBoxOpt("relativesBeIll");
	  	
	  	var takingDrugs = '${editEntity.takingDrugsIds}';
		reloadOpt(takingDrugs, 'takingDrugs');
		
		var beIll = '${editEntity.beIllIds}';
		reloadOpt(beIll, 'beIll');
		
		var relativesBeIll = '${editEntity.relativesBeIllIds}';
		reloadOpt(relativesBeIll, 'relativesBeIll');
	}); 
	
	 function checkboxData() {
		 var uniqueId=$('#uniqueId').val();
	    	console.log(uniqueId);
	    	if (uniqueId==null || uniqueId=='') {
	    		alert("此用户尚未建档，请建档后再填写问卷！");
	    		return false;
	    	}
	    	var checkDate = $("#checkDate").val();
			if (checkDate == null || checkDate.length <= 0) {
				alert("请输入问卷调查日期！");
				return false;
			}
			var takingDrugsIds = '';
			$("input[type=checkbox][name='takingDrugs']").each(function(){
			    if(this.checked){
			    	takingDrugsIds+=$(this).val()+",";
			    }
			});
			var beIllIds = '';
			$("input[type=checkbox][name='beIll']").each(function(){
			    if(this.checked){
			    	beIllIds+=$(this).val()+",";
			    }
			});
			var relativesBeIllIds = '';
			$("input[type=checkbox][name='relativesBeIll']").each(function(){
			    if(this.checked){
			    	relativesBeIllIds+=$(this).val()+",";
			    }
			});
			
			$("#takingDrugsIds").val(returnData(takingDrugsIds));
		    $("#beIllIds").val(returnData(beIllIds));
		    $("#relativesBeIllIds").val(returnData(relativesBeIllIds));
		    
		    return true;
	 }
	 
	 function saveFormData() {
		var isTrue = checkboxData();
		if (!isTrue) {
			return ;
		}
		$.ajax({
	        type: "POST",
	        cache: false,
	        url: "${ctx}/question/editQuestionSave.json",
	        dataType: "html",
			data : $('#questionForm').serialize(),
	        success: function (res) {
	        	alert("保存成功!");
	        	window.location.href="${ctx}/question/list.htm";
	        },
	        error: function (res) {
	        	if(res.message="success"){
	        		 alert("保存成功!");
			         window.location.href="${ctx}/question/list.htm";
				}else{
					alert(o.message);
				}
	           
	        }
	    });
	 }
	 
	 function generatePdf() {
			var isTrue = checkboxData();
			if (isTrue != null && !isTrue) {
				return ;
			}
			
			var options = {
                url:"${ctx}/question/generateQuestionPdf.json",
                success:function(o){
                    var dataResult = o.dataMap;
                    if(dataResult.message =="success"){
                        previewPdf(dataResult.fileName);
                    }else{
                        alert("生成pdf失败！");
                    }
                }
            }
			$("#questionForm").ajaxForm(options);
		 }
	 
	 function previewPdf(fileName){
         $('#printIframe').attr('src',fileName);
         $('#myModal').modal('show');
     }
	 function returnData(data) {
		 console.log(data);
		 if (data == null || data.length <= 0) {
			 return '';
		 }
		 
		 var str = data.substr(0, data.length -1);
		 console.log(str);
		 return str;
	 }
	 
	 var addressInit = function(_cmbProvince, _cmbCity, _cmbArea, defaultProvince, defaultCity, defaultArea)
	 {
		 var cmbProvince = document.getElementById(_cmbProvince);
		 var cmbCity = document.getElementById(_cmbCity);
		 var cmbArea = document.getElementById(_cmbArea);
	
		 function cmbSelect(cmb, str) {
			 for(var i=0; i<cmb.options.length; i++) {
				 if(cmb.options[i].value == str)  {
					 cmb.selectedIndex = i;
					 return;
				 }
			 }
		 }
		 function cmbAddOption(cmb, str, obj) {
			 var option = document.createElement("OPTION");
			 cmb.options.add(option);
			 option.innerText = str;
			 option.value = str;
			 option.obj = obj;
		 }
	
		 function changeCity() {
			 cmbArea.options.length = 0;
			 if(cmbCity.selectedIndex == -1)return;
			 var item = cmbCity.options[cmbCity.selectedIndex].obj;
			 for(var i=0; i<item.areaList.length; i++) {
			 	cmbAddOption(cmbArea, item.areaList[i], null);
			 }
			 cmbSelect(cmbArea, defaultArea);
		 }
		 function changeProvince()
		 {
			 cmbCity.options.length = 0;
			 cmbCity.onchange = null;
			 if(cmbProvince.selectedIndex == -1)return;
			 var item = cmbProvince.options[cmbProvince.selectedIndex].obj;
			 for(var i=0; i<item.cityList.length; i++) {
			 	cmbAddOption(cmbCity, item.cityList[i].name, item.cityList[i]);
			 }
			 cmbSelect(cmbCity, defaultCity);
			 changeCity();
			 cmbCity.onchange = changeCity;
		 }
	
		 for(var i=0; i<provinceList.length; i++)
		 {
		 	cmbAddOption(cmbProvince, provinceList[i].name, provinceList[i]);
		 }
		 cmbSelect(cmbProvince, defaultProvince);
		 changeProvince();
		 cmbProvince.onchange = changeProvince;
	 }
	 
	 function changeNationOption(obj) {
			var name = $(obj).attr("name");
			var radio = $('input[name="' + name + '"]:checked').val();
			if(radio.indexOf('其他')<0) {
				$("#nationOtherDiv").hide();
				$("#nationName").val("");
			} else {
				$("#nationOtherDiv").show()
			}
		}
	 
	 function checkBoxOpt(ch) {
		 $("#"+ch).on("click", function(){
	  			if(this.checked) {
	  				$("[name='"+ch+"']").attr("disabled","disabled");
	  				$("[name='"+ch+"']").removeAttr("checked");
	  				$("#"+ch).removeAttr("disabled");
	  				$("#"+ch).prop("checked","checked");
	  			} else {
	  				$("[name='"+ch+"']").removeAttr("disabled");
	  			}
		  });
	 }
	 
	 function reloadOpt(value, ch) {
		  if (value != null && value.indexOf("无") >= 0) {
			  $("[name='"+ch+"']").attr("disabled","disabled");
			  $("[name='"+ch+"']").removeAttr("checked");
			  $("#"+ch).removeAttr("disabled");
			  $("#"+ch).prop("checked","checked");
		  }
	 }
	</script>
</html>