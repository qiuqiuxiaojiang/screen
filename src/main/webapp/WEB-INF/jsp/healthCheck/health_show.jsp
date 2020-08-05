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
label.error {
	color: Red;
	font-size: 13px;
	/* margin-left:5px; 
		padding-left:5px;  
		background:url("error.png") left no-repeat; */
}

.form-group {
	margin-bottom: 15px;
	height: 30px;
	display: inline-block;
	width: 49%;
}
#familyHistory-error{
    position: absolute;
    right: 52%;
    top: 3px;
}
#diseaseSingle-error{
    position: absolute;
    right: 20%;
    top: 1px;
        font-weight: 700;
}
#gender-error{
    position: absolute;
    right: 65%;
    top: 3px;
}
.button-center-block {  
    float: none;  
    display: block;  
    margin-left: auto;  
    margin-right: auto;  
}  

.panel-title{
	padding: 5 1%;
}

.block-title{
	padding: 0 1%;
	color:0c8ece;
}

.form-control{
	display:inline-block;
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
							<input type="hidden" id="dataId" name="id" value="${showEntity.id}" />
						<input type="hidden" name="uniqueId" id="uniqueId" value="${showEntity.uniqueId }">
					<!-- <div class="panel-heading">添加初筛数据</div> -->
					<div class="panel-body">
					<h4 class="block-title"><span>基础信息</span></h4>
					
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel">身份证号</label>
								<div class="col-sm-8 controls">
									${showEntity.customerId}
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">姓名</label>
								<div class="col-sm-8 controls">
									${showEntity.name}
								</div>
							</div>
							
							<div class="form-group">
								<label for="gender" class="col-sm-4 control-lavel">性别</label>
								<div class="col-sm-8 controls">
									${showEntity.gender}
								</div>
							</div>
							
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel">出生日期</label>
								<div class="col-sm-8 controls">
									${showEntity.birthday}
								</div>
							</div>
							
							<div class="form-group">
								<label for="age" class="col-sm-4 control-lavel">年龄</label>
								<div class="col-sm-8 controls">
									${showEntity.age}
								</div>
							</div>
							<div class="form-group">
								<label for="cardId" class="col-sm-4 control-lavel">手机号码</label>
								<div class="col-sm-8 controls">
									${showEntity.mobile}
								</div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">是否患有疾病</label>
								<div class="col-sm-8 controls">
									<c:if test="${showEntity.haveDisease=='是' }">
									${showEntity.disease }
									</c:if>
									<c:if test="${showEntity.haveDisease=='否' }">
									否
									</c:if>
									
								</div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">是否有疾病家族史</label>
								<div class="col-sm-8 controls">
									<c:if test="${showEntity.familyHistory=='是' }">
										${showEntity.familyDisease }
									</c:if>
									<c:if test="${showEntity.familyHistory=='否' }">
									否
									</c:if>
								</div>
							</div>

							<h4 class="block-title"><span>生理指标</span></h4>
							

							<div class="form-group">
								<label for="height" class="col-sm-4 control-lavel">身高(cm)</label>
								<div class="col-sm-8 controls">
									${showEntity.height}
								</div>
							</div>
							<div class="form-group">
								<label for="weight" class="col-sm-4 control-lavel">体重(kg)</label>
								<div class="col-sm-8 controls">
									${showEntity.weight}
								</div>
							</div>
							
							<div class="form-group">
								<label for="oxygen" class="col-sm-4 control-lavel">体质指数</label>
								<div class="col-sm-8 controls">
									${showEntity.BMI}
								</div>
							</div>
							<div class="form-group">
								<label for="waistline" class="col-sm-4 control-lavel">腰围(cm)</label>
								<div class="col-sm-8 controls">
									${showEntity.waistline}
								</div>
							</div>
							
							<div class="form-group">
								<label for="highPressure" class="col-sm-4 control-lavel">收缩压(mmHg)</label>
								<div class="col-sm-8 controls">
									${showEntity.highPressure}
									<span id="highPressureUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="highPressureDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>

							<div class="form-group">
								<label for="lowPressure" class="col-sm-4 control-lavel">舒张压(mmHg)</label>
								<div class="col-sm-8 controls">
									${showEntity.lowPressure}
									<span id="lowPressureUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="lowPressureDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="pulse" class="col-sm-4 control-lavel">脉率(次/min)</label>
								<div class="col-sm-8 controls">
									${showEntity.pulse}
								</div>
							</div>
							<div class="form-group">
								<label for="temperature" class="col-sm-4 control-lavel">体温(°C)</label>
								<div class="col-sm-8 controls">
									${showEntity.temperature}
								</div>
							</div>
							
							<div class="form-group">
								<label for="oxygen" class="col-sm-4 control-lavel">血氧(%)</label>
								<div class="col-sm-8 controls">
									${showEntity.oxygen}
								</div>
							</div>
							<div class="form-group">
								<label for="hipline" class="col-sm-4 control-lavel">臀围(cm)</label>
								<div class="col-sm-8 controls">
									${showEntity.hipline}
								</div>
							</div>

							<div class="form-group">
								<label for="WHR" class="col-sm-4 control-lavel">腰臀比</label>
								<div class="col-sm-8 controls">
									${showEntity.WHR}
								</div>
							</div>
							<div class="form-group">
								<label for="fatContent" class="col-sm-4 control-lavel">体脂率(%)</label>
								<div class="col-sm-8 controls">
									${showEntity.fatContent}
								</div>
							</div>
							<h4 class="block-title"><span>生化指标</span></h4>
							

							<div class="form-group">
								<label for="bloodGlucose" class="col-sm-4 control-lavel">空腹血糖(mmol/L)</label>
								<div class="col-sm-8 controls" style="position:relative;">
									${showEntity.bloodGlucose}
										<span id="bloodGlucoseUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
										<span id="bloodGlucoseDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
								
							</div>

							<div class="form-group">
								<label for="bloodGlucose2h" class="col-sm-4 control-lavel">餐后两小时血糖(mmol/L)</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodGlucose2h}
									<span id="bloodGlucose2hUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="bloodGlucose2hDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>
							
							<div class="form-group">
								<label for="bloodGlucose" class="col-sm-4 control-lavel">随机血糖(mmol/L)</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodGlucoseRandom}
									<span id="bloodGlucoseRandomUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="bloodGlucoseRandomDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="tc" class="col-sm-4 control-lavel">总胆固醇(mmol/L)</label>
								<div class="col-sm-8 controls">
									${showEntity.tc}
									<span id="tcUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="tcDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="tg" class="col-sm-4 control-lavel">甘油三脂(mmol/L)</label>
								<div class="col-sm-8 controls">
									${showEntity.tg}
									<span id="tgUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="tgDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>
							
							<div class="form-group">
								<label for="ldl" class="col-sm-4 control-lavel">低密度脂蛋白胆固醇(mmol/L)</label>
								<div class="col-sm-8 controls">
									${showEntity.ldl}
									<span id="ldlUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="ldlDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="hdl" class="col-sm-4 control-lavel">高密度脂蛋白胆固醇(mmol/L)</label>
								<div class="col-sm-8 controls">
									${showEntity.hdl}
									<span id="hdlUp" class="glyphicon glyphicon-arrow-up" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
									<span id="hdlDown" class="glyphicon glyphicon-arrow-down" aria-hidden="true" style="position:absolute;right:5%;top:10px;color:red;display:none;"></span>
								</div>
							</div>

							<h4 class="block-title"><span>初筛结果</span></h4>
							
							<div class="form-group">
								<label for="tizhi" class="col-sm-4 control-lavel">中医体质分类代码</label>
								<div class="col-sm-8 controls">
									${showEntity.tizhi}
								</div>
								
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">眼象信息</label>
								<div class="col-sm-8 controls">
									${showEntity.eyeCheck }
								</div>
							</div>
							
							<div class="form-group">
								<label for="riskScore"
									class="col-sm-4 control-lavel">糖尿病危险因素评估分数</label>
								<div class="col-sm-8 controls">
									<span id="riskScoreValue"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">血糖情况</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodSugarCondition}
									<input type="hidden" name="dmRisk" id="dmRisk" value="${showEntity.dmRisk }">
									<input type="hidden" name="dmTag" id="dmTag" value="${showEntity.dmTag }">
									<input type="hidden" name="dmRiskType" id="dmRiskType" value="${showEntity.dmRiskType }">
								</div>
							</div>
							
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">血脂情况</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodLipidCondition}
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">血压情况</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodPressureCondition}
								</div>
							</div>
							
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel">人群分类结果</label>
								<div class="col-sm-8 controls">
									${showEntity.classifyResult}
								</div>
							</div>
							<div class="form-group">
								<label for="checkDate" class="col-sm-4 control-lavel">检测时间</label>
								<div class="col-sm-8 controls">
									${showEntity.checkDate}
								</div>

							</div>
							
							<h4 class="block-title"><span>其他</span></h4>
							

							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">OGTT检测</label>
								<div class="col-sm-8 controls">
									${showEntity.OGTTTest }
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">血脂检测</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodLipidTest }
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">血压检测</label>
								<div class="col-sm-8 controls">
									${showEntity.bloodPressureTest }
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">基因检测</label>
								<div class="col-sm-8 controls">
									${showEntity.geneTest }
								</div>
							</div>
							
							<h4 class="block-title"><span>基因检测</span></h4>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel"></label>
								<div class="col-sm-8 controls">
									${showEntity.geneReportCs }
								</div>
							</div>
							
							<hr>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">备注</label>
								<div class="col-sm-8 controls">
									${showEntity.remark}
								</div>
							</div>
							<hr>
					</div>
					</form>
					<div class="panel-footer">
						<div class="form-group button-center-block">
						<input class="btn btn-primary btn-lg" type="button"
							onclick="javascript:history.go(-1);"
							value="返回">
						</div>
					</div>
				</div>
				
			</div>
		</div>
		<!-- 以上这是页面需要编辑的区域 -->
	</div>

</body>
<script language="JavaScript">
$(document).ready(function(){
	var bgVal = '${showEntity.bloodGlucose }';
	if (bgVal != '' && parseFloat(bgVal) >= 6.1) {
		$("#bloodGlucoseUp").show();
		$("#bloodGlucoseDown").hide();
	} else if(bgVal != '' && parseFloat(bgVal) < 3.9){
		$("#bloodGlucoseUp").hide();
		$("#bloodGlucoseDown").show();
	} else {
		$("#bloodGlucoseUp").hide();
		$("#bloodGlucoseDown").hide();
	}
	
	var bg2hVal = '${showEntity.bloodGlucose2h }';
	if (bg2hVal != '' && parseFloat(bg2hVal) >= 7.8) {
		$("#bloodGlucose2hUp").show();
		$("#bloodGlucose2hDown").hide();
	} else if(bg2hVal != '' && parseFloat(bg2hVal) < 3.9){
		$("#bloodGlucose2hUp").hide();
		$("#bloodGlucose2hDown").show();
	} else {
		$("#bloodGlucose2hUp").hide();
		$("#bloodGlucose2hDown").hide();
	}
	
	var bgrVal = '${showEntity.bloodGlucoseRandom }';
	if (bgrVal != '' && parseFloat(bgrVal) >= 11.1) {
		$("#bloodGlucoseRandomUp").show();
		$("#bloodGlucoseRandomDown").hide();
	} else if(bgrVal != '' && parseFloat(bgrVal) < 3.9){
		$("#bloodGlucoseRandomUp").hide();
		$("#bloodGlucoseRandomDown").show();
	} else {
		$("#bloodGlucoseRandomUp").hide();
		$("#bloodGlucoseRandomDown").hide();
	}
	
	var hpVal = '${showEntity.highPressure}';
	if (hpVal != '' && parseFloat(hpVal) >= 130) {
		$("#highPressureUp").show();
		$("#highPressureDown").hide();
	} else if(hpVal != '' && parseFloat(hpVal) < 90){
		$("#highPressureUp").hide();
		$("#highPressureDown").show();
	} else {
		$("#highPressureUp").hide();
		$("#highPressureDown").hide();
	}
	
	var lpVal = '${showEntity.lowPressure }';
	if (lpVal != '' && parseFloat(lpVal) >= 85) {
		$("#lowPressureUp").show();
		$("#lowPressureDown").hide();
	} else if(lpVal != '' && parseFloat(lpVal) < 60){
		$("#lowPressureUp").hide();
		$("#lowPressureDown").show();
	} else {
		$("#lowPressureUp").hide();
		$("#lowPressureDown").hide();
	}
	
	var tcVal = '${showEntity.tc }';
	if (tcVal != '' && parseFloat(tcVal) >= 5.2) {
		$("#tcUp").show();
		$("#tcDown").hide();
	} else {
		$("#tcUp").hide();
		$("#tcDown").hide();
	}
	
	var tgVal = '${showEntity.tg }';
	if (tgVal != '' && parseFloat(tgVal) >= 1.7) {
		$("#tgUp").show();
		$("#tgDown").hide();
	} else {
		$("#tgUp").hide();
		$("#tgDown").hide();
	}
	
	var hdlVal = '${showEntity.hdl }';
	if(hdlVal != '' && parseFloat(hdlVal) < 1){
		$("#hdlUp").hide();
		$("#hdlDown").show();
	} else {
		$("#hdlUp").hide();
		$("#hdlDown").hide();
	}
	var ldlVal = '${showEntity.ldl }';
	if (ldlVal != '' && parseFloat(ldlVal) >= 3.4) {
		$("#ldlUp").show();
		$("#ldlDown").hide();
	} else {
		$("#ldlUp").hide();
		$("#ldlDown").hide();
	}
	 //血糖分数
	var riskScore = 0; 
	var tangniaobingAgeScore = 0;
	var tangniaobingSexScore = 0;
	var ageNumber = '${showEntity.age }';
	if(ageNumber!=""){
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
	var editEntitySex = '${showEntity.gender }';
	
	var tangniaobingSexScore = 0;
	if(editEntitySex == '男'){
		 tangniaobingSexScore = 2;
	 }
	 if(editEntitySex == '女'){
		 tangniaobingSexScore = 0;
	 }
	 
	 var tangniaobingBMIScore = 0;
	 var bmiNumber = '${showEntity.BMI }';
	 var tangniaobingBMIScore = 0;
	 if(bmiNumber!=""){
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
	 var waistline = '${showEntity.waistline }';
	 var tangniaobingWaistlineScore = 0;
	 if(waistline!=""){
			var waistlineNumber = parseFloat(waistline);
			if(editEntitySex == '男'){
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
			if(editEntitySex == '女'){
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
	 
	 var highPressure = '${showEntity.highPressure }';
	 var tangniaobingHighPressureScore = 0;
	 if(highPressure!=""){
			var highPressureNumber = parseInt(highPressure);
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
	 
	 	var editEntityFamilyHistory = '${showEntity.familyHistory }';
		var editEntityFamilyDisease = '${showEntity.familyDisease }';
		var tangniaobingFamilyHistoryScore = 0;
		if(editEntityFamilyHistory=='是' && editEntityFamilyDisease.indexOf('糖尿病')>=0){
			 tangniaobingFamilyHistoryScore = 6;
		 }
	riskScore = tangniaobingAgeScore + tangniaobingSexScore + tangniaobingBMIScore + tangniaobingWaistlineScore + tangniaobingHighPressureScore + tangniaobingFamilyHistoryScore;
	$("#riskScoreValue").html(riskScore);
});
</script>
</html>