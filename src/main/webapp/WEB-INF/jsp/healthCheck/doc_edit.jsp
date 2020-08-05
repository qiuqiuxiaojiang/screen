<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
<%@include file="../main.jsp"%>
<head>
<title>建档</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/static/css/component.css"></link>
	<script language="javascript" type="text/javascript" src="${ctx_static}/My97DatePicker/WdatePicker.js"></script>
<script src="${ctx_jquery }/jquery.form.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/static/js/component.js"></script>
<script type="text/javascript" src="${ctx_static}/js/common.js"></script>
<!-- 表单验证 -->
<script src="${ctx_jquery }/jquery.validate.js"></script>
<!-- 身份证、护照等验证 -->
<script src="${ctx_jquery }/card.js"></script>
<style type="text/css">
label.error {
	color: Red;
	font-size: 13px;
}

.form-group {
	margin-bottom: 15px;
	min-height: 30px;
	display: inline-block;
	width: 49%;
}
#familyHistory-error{
    position: absolute;
    right: -10%;
    bottom: 0;
}
#familyDiseaseSingle-error{
    position: absolute;
    right: -10%;
    bottom: 0;
    font-weight: 700;
}
#diseaseSingle-error{
    position: absolute;
    right: 0;
    bottom: 0;
    font-weight: 700;
}
#gender-error{
    position: absolute;
    right: 65%;
    bottom: 0;
}
#haveDisease-error{
	position: absolute;
    right: -30%;
    bottom: 0;
}
.col-sm-2{
	position:relative;
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
.form-control{
	display:inline-block;
}

.block-title{
	padding: 0 1%;
	color:0c8ece;
}

</style>
<script type="text/javascript">
	</script>
</head>
<body>
	<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div>
				<div class="panel panel-warning">
		<h4 class="block-title"><span>基础信息</span></h4>
					<form id="healthcheckInfo" role="form" method="post">
						<input type="hidden" name="uniqueId" id="uniqueId">
						<input type="hidden" name="idCardInputTag" id="idCardInputTag" value="auto">
						<input type="hidden" id="editflag" value="${editflag}">
					<div class="panel-body">
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>身份证号</label>
								<div class="col-sm-8 controls">
									<input type="text" id="customerId" class="form-control" name="customerId" value="${editEntity.customerId}" 
										readonly placeholder="请输入身份证号" style="width: 200px; padding: 7px 10px; display:inline-block;border: 1px solid #ccc; margin-right: 10px;">
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
									<input type="text" class="form-control" id="gender" name="gender" value="${editEntity.gender}" readonly>

								</div>
							</div>
							<div class="form-group">
								<label for="birthday" class="col-sm-4 control-lavel"><span style="color:red;">*</span>出生日期</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="birthday" readonly="readonly" name="birthday" value="${editEntity.birthday}">
								</div>
							</div>


							<div class="form-group">
								<label for="age" class="col-sm-4 control-lavel"><span style="color:red;">*</span>年龄</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="age" readonly="readonly" name="age" value="${editEntity.age}"
										onblur="bloodSugarConditionCharge();">
								</div>
							</div>
							<div class="form-group">
								<label for="nation" class="col-sm-4 control-lavel">民族</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="nationality" name="nationality" value="${editEntity.nationality}" readonly>
								</div>
							</div>
							<div class="form-group">
								<label for="mobile" class="col-sm-4 control-lavel">手机号码</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="mobile" name="mobile" value="${editEntity.mobile}">
								</div>
							</div>
<!-- 							<div class="form-group">
								<label for="mobile" class="col-sm-4 control-lavel">座机号码</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="phone" name="phone" value="${editEntity.phone}">
								</div>
							</div>
							 -->
							<div class="form-group">
								<label for="address" class="col-sm-4 control-lavel">地址</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="address" name="address" value="${editEntity.address}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="contactName" class="col-sm-4 control-lavel">联系人姓名</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="contactName" name="contactName" value="${editEntity.contactName}">
								</div>
							</div>
							<div class="form-group">
								<label for="contactMobile" class="col-sm-4 control-lavel">联系人电话</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="contactMobile" name="contactMobile" value="${editEntity.contactMobile}">
								</div>
							</div>
							<div class="form-group">
								<label for="contactMobile" class="col-sm-4 control-lavel">单位</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="company" name="company" value="${editEntity.company}">
								</div>
							</div>
							<div class="form-group">
								<label for="checkDate" class="col-sm-4 control-lavel"><span style="color:red;">*</span>建档日期</label>
								<div class="col-sm-8 controls">
									<input class="Wdate" id="recordDate" name="recordDate" readonly type="text" value="${editEntity.recordDate}"
											onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
								</div>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否患有疾病</label>
								<div class="col-sm-4 checkbox-list">
									<label> 
										<input type="radio" id="haveDisease" name="haveDisease" onchange="diseaseFunc(this);" value="是" <c:if test="${(editEntity.haveDisease=='是')}">checked="checked"</c:if> /> 是
									</label>
									<label>
										<input type="radio" id="noDisease"  name="haveDisease" onchange="diseaseFunc(this);" value="否" <c:if test="${(editEntity.haveDisease=='否')}">checked="checked"</c:if> /> 否
									</label>
							</div>
								<div class="col-sm-6 checkbox-list" style="margin-top: 0px; margin-left:260px; display: none;" id="diseaseDiv">
									<label>
										<input name="diseaseSingle" type="checkbox"	value="糖尿病" id="tangniaobing" <c:if test="${fn:contains(editEntity.disease,'糖尿病')}">checked="checked"</c:if>>糖尿病
									</label> 
									<label>
										<input name="diseaseSingle" type="checkbox" value="冠心病" id="guanxinbing" <c:if test="${fn:contains(editEntity.disease,'冠心病')}">checked="checked"</c:if>>冠心病
									</label> 
									<label>
										<input name="diseaseSingle" type="checkbox" value="高血压" id="gaoxueya" <c:if test="${fn:contains(editEntity.disease,'高血压')}">checked="checked"</c:if>>高血压
									</label>
									<label>
										<input name="diseaseSingle" type="checkbox"	value="高血脂" id="gaoxuezhi" <c:if test="${fn:contains(editEntity.disease,'高血脂')}">checked="checked"</c:if>>高血脂
									</label>
								</div>
								</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否有疾病家族史</label>
								<div class="col-sm-4 checkbox-list">
									<label> 
										<input type="radio" id="haveHistory" name="familyHistory" onchange="familyDiseaseFunc(this);" value="是" <c:if test="${(editEntity.familyHistory=='是')}">checked="checked"</c:if> /> 是
									</label>
									<label>
										<input type="radio" id="noHistory"  name="familyHistory" onchange="familyDiseaseFunc(this);" value="否" <c:if test="${(editEntity.familyHistory=='否')}">checked="checked"</c:if> /> 否
									</label>
								</div>
								<div class="col-sm-6 checkbox-list" style="margin-top: 0px;margin-left:260px; display: none;" id="familyDiseaseDiv">
									<label>
										<input name="familyDiseaseSingle" type="checkbox"	value="糖尿病" id="familytangniaobing" <c:if test="${fn:contains(editEntity.familyDisease,'糖尿病')}">checked="checked"</c:if>>糖尿病
									</label> 
									<label>
										<input name="familyDiseaseSingle" type="checkbox" value="冠心病" id="familyguanxinbing" <c:if test="${fn:contains(editEntity.familyDisease,'冠心病')}">checked="checked"</c:if>>冠心病
									</label> 
									<label>
										<input name="familyDiseaseSingle" type="checkbox" value="高血压" id="familygaoxueya" <c:if test="${fn:contains(editEntity.familyDisease,'高血压')}">checked="checked"</c:if>>高血压
									</label>
									<label>
										<input name="familyDiseaseSingle" type="checkbox"	value="高血脂" id="familygaoxuezhi" <c:if test="${fn:contains(editEntity.familyDisease,'高血脂')}">checked="checked"</c:if>>高血脂
									</label>
									<label>
										<input name="familyDiseaseSingle" type="checkbox"	value="其他" id="familyqita"  <c:if test="${fn:contains(editEntity.familyDisease,'其他')}">checked="checked"</c:if>>其他
									</label>
								</div>
							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>筛查地点</label>
								<div class="col-sm-8 controls form-inline" >
											<select style="display: inline-block;width: 30%;margin-right: 1%;height:38px!important;padding: 0 1%;" name="district" id="district">
										    	<c:forEach var="place" items="${districts}">
										    		<option <c:if test="${(place.district==district)}">selected="selected"</c:if> value="${place.district}">${place.district}</option>
										    	</c:forEach>
											</select>
									
											<input type="text" class="form-control" id="checkPlace" style="width: 68%" name="checkPlace" value="${checkPlace}">
								</div>
							</div>

							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">筛查组</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="checkGroup" name="checkGroup" value="${editEntity.checkGroup}">
								</div>
							</div>
							<c:if test="${item=='kunming' }">
								<div class="form-group">
									<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>所属街道及社区</label>
									<div class="col-sm-8 controls form-inline" >
										<select style="display: inline-block;width: 30%;margin-right: 1%;height:38px!important;padding: 0 1%;" name="street" id="street">
											<option value="">--请选择--</option>
									    	<c:forEach var="street" items="${streets}">
									    		<option <c:if test="${(editEntity.street==street)}">selected="selected"</c:if> value="${street}">${street}</option>
									    	</c:forEach>
										</select>
										
										<select style="display: inline-block;width: 30%;margin-right: 1%;height:38px!important;padding: 0 1%;" name="checkPlaceId" id="community">
											<option value="">--请选择--</option>
										</select>
									</div>
								</div>
							</c:if>
					</div>
					<div class="panel-footer">
							<input class="btn btn-primary btn-lg" type="submit"
								id="saveBtn"
								value="保存数据">
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
	        	$('#nationality').val(result.content.nationMc);
	        	queryInfo(idCard);

	        	window.clearInterval(t2);
	        }
	    }
	    
	function inputId() {
		$('#customerId').removeAttr("readonly");
		$('#nationality').removeAttr("readonly");
		$('#inputQueryButton').show();
		$('#inputIdcardButton').hide();
		$('#idCardInputTag').val('notvalidate');
	
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
		$('#idCardInputTag').val('input');
		queryInfo(idCardNo);
	}
	
	function queryInfo(idCardNo) {
		$.ajax({
			type : "post",
			url : "${ctx}/manage/hc/queryInfo.json",
			data : {
				customerId:idCardNo
			},
			success:function(data){
				if(data.code==0){
					$('#customerId').attr("readonly", "readonly");
					if (data.dataMap.recordTag=='true') {
						alert('用户已建档！');
					}
					var uniqueId = data.dataMap.uniqueId;
					$('#uniqueId').val(uniqueId);
					
					var editflag = $("#editflag").val();
					var mobile = data.dataMap.mobile;
					if (mobile!=null && mobile!='') {
						$('#mobile').val(mobile);
						if (editflag == "false") {
							$('#mobile').attr("readonly","readonly");
						}
					}
					var customerName = data.dataMap.name;
					
					if (customerName != null && customerName != '') {
						$('#customerName').val(customerName);
						if (editflag == "false") {
							$('#customerName').attr("readonly","readonly");
						}
					}
					
					var nationality = data.dataMap.nationality;
					if (nationality != null && nationality != '') {
						$('#nationality').val(nationality);
			        	$('#nationality').attr("readonly","readonly");
					}
					var idCardInputTag = data.dataMap.idCardInputTag;
					if (idCardInputTag != null && idCardInputTag != '') {
						$('#idCardInputTag').val(idCardInputTag);
					} else {
						$('#idCardInputTag').val("auto");
					}

					var address = data.dataMap.address;
					$('#address').val(address);
					
					$('#contactName').val(data.dataMap.contactName);
					$('#contactMobile').val(data.dataMap.contactMobile);
					$('#company').val(data.dataMap.company);
					var haveDisease = data.dataMap.haveDisease;
					if (haveDisease == null)  {
						haveDisease= '';
					}
					if (haveDisease == "是") {
						$('#haveDisease').prop("checked",true);
			    		$("#diseaseDiv").show();
					}
					if (haveDisease == '否') {
						$('#noDisease').prop("checked",true);
			    		$("#diseaseDiv").hide();
					}
					
					var disease = data.dataMap.disease;
					if (disease == null) {
						disease = '';
					}
					if (disease.indexOf("糖尿病")>=0) {
						$('#tangniaobing').attr("checked","checked");
					}
					if (disease.indexOf("冠心病")>=0) {
						$('#guanxinbing').attr("checked","checked");
					}
					if (disease.indexOf("高血压")>=0) {
						$('#gaoxueya').attr("checked","checked");
					}
					if (disease.indexOf("高血脂")>=0) {
						$('#gaoxuezhi').attr("checked","checked");
					}
					var familyHistory = data.dataMap.familyHistory;
					if (familyHistory == null) {
						familyHistory = '';
					}
					if (familyHistory=="是") {
						$('#haveHistory').prop("checked",true);
			    		$("#familyDiseaseDiv").show();
					}
					if (familyHistory=="否") {
						$('#noHistory').prop("checked",true);
			    		$("#familyDiseaseDiv").hide();
					}
					var familyDisease = data.dataMap.familyDisease;
					if (familyDisease== null) {
						familyDisease = '';
					}
					if (familyDisease.indexOf("糖尿病")>=0) {
						$('#familytangniaobing').attr("checked","checked");
					}
					if (familyDisease.indexOf("冠心病")>=0) {
						$('#familyguanxinbing').attr("checked","checked");
					}
					if (familyDisease.indexOf("高血压")>=0) {
						$('#familygaoxueya').attr("checked","checked");
					}
					if (familyDisease.indexOf("高血脂")>=0) {
						$('#familygaoxuezhi').attr("checked","checked");
					}
					if (familyDisease.indexOf("其他")>=0) {
						$('#familyqita').attr("checked","checked");
					}
					$('#recordDate').val(data.dataMap.recordDate);
					var district = data.dataMap.district;
					if (district!=null && district != '') {
						$('#district').val(district);								
					}
					var checkPlace = data.dataMap.checkPlace;
					if (checkPlace != null && checkPlace != '') {
						$('#checkPlace').val(checkPlace);
					}
					
					$('#checkGroup').val(data.dataMap.checkGroup);
					
					var street = data.dataMap.street;
					$('#street').val(street);
					
					var checkPlaceId = data.dataMap.checkPlaceId;
					$('#community').val(checkPlaceId);
					
					var communitys = data.dataMap.communitys;
					if (communitys != null && communitys != '') {
						for (var i=0; i<communitys.length; i++) {
			               	 var checkPlaceId1 = communitys[i].checkPlaceId;
			               	 var community = communitys[i].community;
			               	 if (checkPlaceId == checkPlaceId1) {
			               		 $("#community").append("<option selected value='"+checkPlaceId1+"'>"+community+"</option>");
			               	 } else {
			               		 $("#community").append("<option value='"+checkPlaceId1+"'>"+community+"</option>");
			               	 }
			            } 
					}
					
					
					$('#inputQueryButton').hide();
				} else {
					$('#customerId').attr("readonly", "readonly");
					var idCardInputTag = $('#idCardInputTag').val();
					if (idCardInputTag == 'input') {
						$('#customerName').removeAttr("readonly");
						$('#nationality').removeAttr("readonly");
					}
					$('#inputQueryButton').hide();
				}
			}
		});
		
	}
	 $(document).ready(function(){
	        t2 = window.setInterval("OnBtnReadIDCardContent()", 2500); 
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

			   jQuery.validator.addMethod("mobile", function(value, element) { 
				   var isValid = true;
				   if(!(/^1\d{10}$/.test(value))){ 
				       isValid = false; 
				   }
				     return this.optional(element) || isValid;     
				   }, "请正确输入您的手机号码"); 

			   jQuery.validator.addMethod("phone", function(value, element) { 
				   var isValid = false;
				   if (/^\d{3}-\d{8}$|^\d{4}-\d{7,8}$|^1\d{10}$|^[1-9][0-9]{6,7}$/.test(value) ) {
					   isValid = true;
				   }
				     return this.optional(element) || isValid;     
				   }, "请正确输入电话号码"); 

	        $("#healthcheckInfo").validate({
				rules:{
					name:{
						required: true
					},
					customerId:{
						required: true,
						isIdCardNo:true
					},
					mobile:{
						mobile:true
					},
					age:{
						required: true,
						digits:true
					},
					gender:{
						required: true
					},
					checkPlace:{
						required: true
					},
					familyHistory:{
						required: true
					},
					recordDate:{
						required:true
					},
					haveDisease:{
						required:true
					},
					diseaseSingle:{
						required:true
					},
					familyDiseaseSingle:{
						required:true
					},
					contactMobile:{
						mobile:true
					},
					checkPlaceId:{
						required:true
					} 
				},  
    	        messages: {  
    	        	name: {
    	        		required:"请输入用户名",
    	        	},
    	        	customerId:{
						required: "请输入身份证号"
					},
					mobile:{
						mobile:"请输入合法手机号"
					},
					age:{
						required: "请输入年龄",
						digits:"请输入合法数字"
					},
					gender:{
						required: "请选择性别"
					},
					checkPlace:{
						required: "请输入筛查地点"
					},
					familyHistory:{
						required: "请选择家族史"
					},
					recordDate:{
						required:"请输入检测时间"
                    },
                    haveDisease:{
						required: "请选择是否患有疾病"
					},
					diseaseSingle:{
						required:'请选择具体疾病'
					},
					familyDiseaseSingle:{
						required:'请选择具体家族史'
					},
					contactMobile:{
						mobile:"请输入正确的电话号码"
					},
					checkPlaceId:{
						required:"请选择所在社区及街道"
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
	        
			$("#saveBtn").click(function(){
				submitForm("submit");
			});
	        
	    });
    function submitForm(submitSign) {
    	if ($('#customerId').val()=='') {
    		alert("请刷身份证或手工输入身份证号码!");
    		return false;
    	}
		var idCardInputTag = $('#idCardInputTag').val();
		if (idCardInputTag == 'notvalidate') {
			alert('请先查询此身份证号是否已经建档！');
			return false;
		}
		var diseaseIds="";
		$("input[type=checkbox][name='diseaseSingle']").each(function(){
		    if(this.checked){
		    	diseaseIds+=$(this).val()+",";
		    }
		});
		if(diseaseIds.length>0){
			diseaseIds = diseaseIds.substring(0, diseaseIds.length-1);
		}
		
		var familyDiseaseIds="";
		$("input[type=checkbox][name='familyDiseaseSingle']").each(function(){
		    if(this.checked){
		    	familyDiseaseIds+=$(this).val()+",";
		    }
		});
		if(familyDiseaseIds.length>0){
			familyDiseaseIds = familyDiseaseIds.substring(0, familyDiseaseIds.length-1);
		}
		
		var options = submitFormsAjax(diseaseIds, familyDiseaseIds);
			$("#healthcheckInfo").ajaxForm(options);
    	
    }
    /**保存表单信息  ajax**/
    function submitFormsAjax(diseaseIds, familyDiseaseIds) {
        var options = {
                url:"${ctx}/manage/hc/saveDoc.json",
                data:{
                    disease:diseaseIds,
                    familyDisease:familyDiseaseIds
                },
                success:function(o){
                    var dataResult = o.dataMap;
                    if(o.code ==0){
                    	alert("建档成功");
                    	window.location.href='${ctx}/manage/hc/addDocument.htm'
                    }else{
                        alert(o.message);
                    }
                }
            };
        return options;
    }
    
    function diseaseFunc(obj) {
    	if (obj.value == "是") {
    		$("#diseaseDiv").show();
    	} else {
    		$("#diseaseDiv").hide();
    	}
    }
    
    function familyDiseaseFunc(obj) {
    	if (obj.value == "是") {
    		$("#familyDiseaseDiv").show();
    	} else {
    		$("#familyDiseaseDiv").hide();
    	}
    }
    
    $("#street").change(function() {
		 empty();
		 var street = $(this).val();
		 $.ajax({
            type:"post",
            url:'${ctx}/communitys/getCommunitysByStreet.json',
            data:{
            	street:street
            },
            success:function(o){
           	 var datas = o.dataList;
           	 console.log(datas);
                for (var i=0; i<datas.length; i++) {
               	 var checkPlaceId = datas[i].checkPlaceId;
               	 var community = datas[i].community;
               	 $("#community").append("<option value='"+checkPlaceId+"'>"+community+"</option>");
                } 
            }
        });
	}); 
    
    function empty(){
		$("#community").empty();
		$("#community").append("<option value=''>--请选择--</option>");
	}
    
</script>


</html>