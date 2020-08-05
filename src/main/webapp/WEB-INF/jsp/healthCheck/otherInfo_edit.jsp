<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>录入其他信息</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
<script src="${ctx_jquery }/card.js"></script>
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
    
	<script type="text/javascript">
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
			
	    	var uniqueId=$('#uniqueId').val();
	    	console.log(uniqueId);
	    	if (uniqueId==null || uniqueId=='') {
	    		alert("此用户尚未建档，请建档后再筛查！");
	    		return;
	    	}
			var drugAllergyIds = '';
			$("input[type=checkbox][name='drugAllergy']").each(function(){
			    if(this.checked){
			    	drugAllergyIds+=$(this).val()+",";
			    }
			});
			var pastDiseaseIds = '';
			$("input[type=checkbox][name='pastDisease']").each(function(){
			    if(this.checked){
			    	pastDiseaseIds+=$(this).val()+",";
			    }
			});
			var heredityFatherIds = '';
			$("input[type=checkbox][name='heredityFather']").each(function(){
			    if(this.checked){
			    	heredityFatherIds+=$(this).val()+",";
			    }
			});
			
			var heredityMotherIds = '';
			$("input[type=checkbox][name='heredityMother']").each(function(){
			    if(this.checked){
			    	heredityMotherIds+=$(this).val()+",";
			    }
			});
			var heredityBsIds = '';
			$("input[type=checkbox][name='heredityBs']").each(function(){
			    if(this.checked){
			    	heredityBsIds+=$(this).val()+",";
			    }
			});
			var heredityChildrenIds = '';
			$("input[type=checkbox][name='heredityChildren']").each(function(){
			    if(this.checked){
			    	heredityChildrenIds+=$(this).val()+",";
			    }
			});
			var frequencyExerciseIds = '';
			$("input[type=checkbox][name='frequencyExercise']").each(function(){
			    if(this.checked){
			    	frequencyExerciseIds+=$(this).val()+",";
			    }
			});
			var eatingHabitsIds = '';
			$("input[type=radio][name='eatingHabits']").each(function(){
			    if(this.checked){
			    	eatingHabitsIds+=$(this).val()+",";
			    }
			});
			$("input[type=checkbox][name='eatingHabits']").each(function(){
			    if(this.checked){
			    	eatingHabitsIds+=$(this).val()+",";
			    }
			});
			var drinkingTypeIds = '';
			$("input[type=checkbox][name='drinkingType']").each(function(){
			    if(this.checked){
			    	drinkingTypeIds+=$(this).val()+",";
			    }
			});
			var diseaseSingleIds = '';
			$("input[type=checkbox][name='diseaseSingle']").each(function(){
			    if(this.checked){
			    	diseaseSingleIds+=$(this).val()+",";
			    }
			});
			
			$("#drugAllergyIds").val(returnData(drugAllergyIds));
		    $("#pastDiseaseIds").val(returnData(pastDiseaseIds));
		    $("#heredityFatherIds").val(returnData(heredityFatherIds));
		    $("#heredityMotherIds").val(returnData(heredityMotherIds));
		    $("#heredityBsIds").val(returnData(heredityBsIds));
		    $("#heredityBsIds").val(returnData(heredityBsIds));
		    $("#heredityChildrenIds").val(returnData(heredityChildrenIds));
		    $("#eatingHabitsIds").val(returnData(eatingHabitsIds));
		    $("#drinkingTypeIds").val(returnData(drinkingTypeIds));
		    $("#diseaseSingleIds").val(returnData(diseaseSingleIds));
		    
			$.ajax({
		        type: "POST",
		        cache: false,
		        url: "${ctx}/otherInfo/saveOtherInfo.htm",
		        dataType: "html",
				data : $('#otherInfoForm').serialize(),
		        success: function (res) {
		        	alert("保存成功!");
		        	window.location.href="${ctx}/otherInfo/list.htm";
		        },
		        error: function (res) {
		        	if(res.message="success"){
		        		 alert("保存成功!");
				         window.location.href="${ctx}/otherInfo/list.htm";
					}else{
						alert(o.message);
					}
		           
		        }
		    });
		});
		
		  $('#banklist').click(function(){
        	  window.location.href="${ctx}/otherInfo/list.htm";
          })
          
          $("input[type=radio][name=frequencyExercise]").change(function() {
  			var value = this.value;
  			var readOny = false;
  			if (value == '不锻炼') {
  				readOny = true;
  			}
  			
  			$("input[name=exerciseTime]").val("");
			$("input[name=stickExerciseTime]").val("");
			$("input[name=exerciseMode]").val("");
			
  			$("input[name=exerciseTime]").attr("readonly",readOny);
			$("input[name=stickExerciseTime]").attr("readonly",readOny);
			$("input[name=exerciseMode]").attr("readonly",readOny);
  		  });
		  
		  $("input[type=radio][name=smokingStatus]").change(function() {
  			var value = this.value;
  			var readOny = false;
  			var cessation = false;
  			if (value == '从不吸烟') {
  				readOny = true;
  				cessation = true;
  			}
  			else if (value == '吸烟') {
  				cessation = true;
  			}
  			
  			$("input[name=dailySmoking]").val("");
			$("input[name=smokingAge]").val("");
			$("input[name=smokingCessation]").val("");
			
  			$("input[name=dailySmoking]").attr("readonly",readOny);
			$("input[name=smokingAge]").attr("readonly",readOny);
			$("input[name=smokingCessation]").attr("readonly",cessation);
  		  });
		  
		  $("input[type=radio][name=drinkingFrequency]").change(function() {
	  			var value = this.value;
	  			var readOny = false;
	  			if (value == '从不') {
	  				readOny = true;
	  				
	  				$("#alcohollOtherDiv").hide();
	  			}
	  			
	  			$("input[name=drinkVolume]").val("");
				$("input[name=whetherAlcohol]").removeAttr("checked");
				$("input[name=drinkingAge]").val("");
				$("input[name=drunkPastyear]").removeAttr("checked");
				$("input[name=drinkingType]").removeAttr("checked");
				
	  			$("input[name=drinkVolume]").attr("readonly",readOny);
				$("input[name=whetherAlcohol]").attr("disabled",readOny);
				$("input[name=drinkingAge]").attr("readonly",readOny);
				$("input[name=drunkPastyear]").attr("disabled",readOny);
				$("input[name=drinkingType]").attr("disabled",readOny);
	  		  });
		  
		  //设置复选框选中事件
		  checkBoxOpt("heredityMother");
		  checkBoxOpt("heredityFather");
		  checkBoxOpt("drugAllergy");
		  checkBoxOpt("heredityChildren");
		  checkBoxOpt("pastDisease");
		  checkBoxOpt("heredityBs");
		  
		  var heredityMother = '${editEntity.heredityMother}';
		  reloadOpt(heredityMother, 'heredityMother');
		  
		  var heredityFather = '${editEntity.heredityFather}';
		  reloadOpt(heredityFather, 'heredityFather');
		  
		  var drugAllergy = '${editEntity.drugAllergy}';
		  reloadOpt(drugAllergy, 'drugAllergy');
		  
		  var heredityChildren = '${editEntity.heredityChildren}';
		  reloadOpt(heredityChildren, 'heredityChildren');
		  
		  var pastDisease = '${editEntity.pastDisease}';
		  reloadOpt(pastDisease, 'pastDisease');
		  
		  var heredityBs = '${editEntity.heredityBs}';
		  reloadOpt(heredityBs, 'heredityBs');
	}); 
	
	 function returnData(data) {
		 console.log(data);
		 if (data == null || data.length <= 0) {
			 return '';
		 }
		 
		 var str = data.substr(0, data.length -1);
		 console.log(str);
		 return str;
	 }
	 function changeDiseaseOption(obj) {
		var name = $(obj).attr("name");
		var radio = $('input[name="' + name + '"]:checked').val();
		if(radio.indexOf('有')<0) {
			$("#heredityOtherDiv").hide();
			$("#diseaseName").val("");
		} else {
			$("#heredityOtherDiv").show()
		}
	}
	 
	 function changeAcOption(obj) {
			var name = $(obj).attr("name");
			var radio = $('input[name="' + name + '"]:checked').val();
			if(radio.indexOf('已戒酒')<0) {
				$("#alcohollOtherDiv").hide();
				$("#alcoholAge").val("");
			} else {
				$("#alcohollOtherDiv").show();
				$("#alcoholAge").val("");
				$("#alcoholAge").removeAttr("readonly");
			}
		}
	
	 function reloadOpt(value, ch) {
		  if (value != null && value.indexOf("无") >= 0) {
			  $("[name='"+ch+"']").attr("disabled","disabled");
			  $("[name='"+ch+"']").removeAttr("checked");
			  $("#"+ch).removeAttr("disabled");
			  $("#"+ch).prop("checked","checked");
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
	</script>
</head>
<body>
<div style="display:none">
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
</div>
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-body form-body">
                                 	 <form id="otherInfoForm" role="form" method="post">
										<input type="hidden" id="dataId" name="dataId" value="${editEntity.id}" />
										<input type="hidden" name="uniqueId" id="uniqueId" value="${editEntity.uniqueId }">
										<input type="hidden" id="drugAllergyIds" name="drugAllergyIds" />
										<input type="hidden" id="pastDiseaseIds" name="pastDiseaseIds" />
										<input type="hidden" id="heredityFatherIds" name="heredityFatherIds" />
										<input type="hidden" id="heredityMotherIds" name="heredityMotherIds" />
										<input type="hidden" id="heredityBsIds" name="heredityBsIds" />
										<input type="hidden" id="heredityChildrenIds" name="heredityChildrenIds" />
										<input type="hidden" id="eatingHabitsIds" name="eatingHabitsIds" />
										<input type="hidden" id="drinkingTypeIds" name="drinkingTypeIds" />
										<input type="hidden" id="diseaseSingleIds" name="diseaseSingleIds" />
							          	<h4 class="block-title"><span>基本信息</span></h4>
							          	
                              
							<div class="form-group">
								<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>身份证号</label>
								<div class="col-sm-8 controls">
									<input type="text" id="customerId" readonly class="form-control" name="customerId" value="${editEntity.customerId}"
										placeholder="请输入身份证号" style="width: 200px; padding: 7px 10px; display:inline-block;border: 1px solid #ccc; margin-right: 10px;">
									<input type="button" id="inputIdcardButton" class="btn btn-primary " value="手工录入" onclick="javascript:inputId()">
									<input type="button" id="inputQueryButton" class="btn btn-primary " style="display:none" value="查询" 
										onclick="javascript:queryByCustomerId()">
								</div>
							</div>
							<div class="form-group">
								<label for="name" class="col-sm-4 control-lavel"><span style="color:red;">*</span>姓名</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="customerName" name="name" value="${editEntity.name}"
										readonly placeholder="请输入用户名">
								</div>
							</div>
							<div class="form-group">
								<label for="gender" class="col-sm-4 control-lavel"><span style="color:red;">*</span>性别</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="gender" name="gender" value="${editEntity.gender}" readonly>

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
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否患有疾病</label>
								<div class="col-sm-8 controls">
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
								<label for="url" class="col-sm-4 control-lavel"><span style="color:red;">*</span>是否有疾病家族史</label>
								<div class="col-sm-8 controls">
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
							
							          	<h4 class="block-title"><span>既往史</span></h4>
							          	
										<div class="form-group">
											<label for="drugAllergy" class="col-sm-4 control-lavel">药物过敏史</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="drugAllergy" id="drugAllergy" value="无" <c:if test="${fn:contains(editEntity.drugAllergy,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="checkbox" name="drugAllergy" value="青霉素" <c:if test="${fn:contains(editEntity.drugAllergy,'青霉素')}">checked="checked"</c:if>>青霉素</label>
												<label><input type="checkbox" name="drugAllergy" value="磺胺" <c:if test="${fn:contains(editEntity.drugAllergy,'磺胺')}">checked="checked"</c:if>>磺胺</label>
												<label><input type="checkbox" name="drugAllergy" value="链霉素" <c:if test="${fn:contains(editEntity.drugAllergy,'链霉素')}">checked="checked"</c:if>>链霉素</label>
												<label><input type="checkbox" name="drugAllergy" value="其他" <c:if test="${fn:contains(editEntity.drugAllergy,'其他')}">checked="checked"</c:if>>其他</label>
											</div>
										</div>
										
										<div class="form-group">
											<label for="pastDisease" class="col-sm-4 control-lavel">既往疾病史</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="pastDisease" id="pastDisease" value="无" <c:if test="${fn:contains(editEntity.pastDisease,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="checkbox" name="pastDisease" value="高血压" <c:if test="${fn:contains(editEntity.pastDisease,'高血压')}">checked="checked"</c:if>>高血压</label>
												<label><input type="checkbox" name="pastDisease" value="糖尿病" <c:if test="${fn:contains(editEntity.pastDisease,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
												<label><input type="checkbox" name="pastDisease" value="高血脂" <c:if test="${fn:contains(editEntity.pastDisease,'高血脂')}">checked="checked"</c:if>>高血脂</label>
												<label><input type="checkbox" name="pastDisease" value="冠心病" <c:if test="${fn:contains(editEntity.pastDisease,'冠心病')}">checked="checked"</c:if>>冠心病</label>
												<label><input type="checkbox" name="pastDisease" value="其他" <c:if test="${fn:contains(editEntity.pastDisease,'其他')}">checked="checked"</c:if>>其他</label>
											</div>
										</div>
										
										<div class="form-group">
											<label for="heredity" class="col-sm-4 control-lavel">遗传病史</label>
											<div class="col-sm-8 checkbox-list" style="display:inline">
												<label><input type="radio" name="heredity" value="无" onchange="changeDiseaseOption(this)" <c:if test="${fn:contains(editEntity.heredity,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="radio" name="heredity" value="有" onchange="changeDiseaseOption(this)" <c:if test="${fn:contains(editEntity.heredity,'有')}">checked="checked"</c:if>>有</label>
												
												<div id="heredityOtherDiv" <c:if test="${!fn:contains(editEntity.heredity,'有')}">style="display:none"</c:if>>
												 	<label>疾病名称：</label>
												 	<input type="text" name="diseaseName" id="diseaseName" value="${editEntity.diseaseName }">
									          	</div>
											</div>
											
										</div>
										
										<div class="form-group">
											<label for="heredityFather" class="col-sm-4 control-lavel">家族病史（父亲）</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="heredityFather" id="heredityFather" value="无" <c:if test="${fn:contains(editEntity.heredityFather,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="checkbox" name="heredityFather"  value="高血压" <c:if test="${fn:contains(editEntity.heredityFather,'高血压')}">checked="checked"</c:if>>高血压</label>
												<label><input type="checkbox" name="heredityFather" value="糖尿病" <c:if test="${fn:contains(editEntity.heredityFather,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
												<label><input type="checkbox" name="heredityFather" value="高血脂" <c:if test="${fn:contains(editEntity.heredityFather,'高血脂')}">checked="checked"</c:if>>高血脂</label>
												<label><input type="checkbox" name="heredityFather" value="冠心病" <c:if test="${fn:contains(editEntity.heredityFather,'冠心病')}">checked="checked"</c:if>>冠心病</label>
												<label><input type="checkbox" name="heredityFather" value="其他" <c:if test="${fn:contains(editEntity.heredityFather,'其他')}">checked="checked"</c:if>>其他</label>
											</div>
										</div>
										<div class="form-group">
											<label for="heredityMother" class="col-sm-4 control-lavel">家族病史（母亲）</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="heredityMother" id="heredityMother" value="无" <c:if test="${fn:contains(editEntity.heredityMother,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="checkbox" name="heredityMother" value="高血压" <c:if test="${fn:contains(editEntity.heredityMother,'高血压')}">checked="checked"</c:if>>高血压</label>
												<label><input type="checkbox" name="heredityMother" value="糖尿病" <c:if test="${fn:contains(editEntity.heredityMother,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
												<label><input type="checkbox" name="heredityMother" value="高血脂" <c:if test="${fn:contains(editEntity.heredityMother,'高血脂')}">checked="checked"</c:if>>高血脂</label>
												<label><input type="checkbox" name="heredityMother" value="冠心病" <c:if test="${fn:contains(editEntity.heredityMother,'冠心病')}">checked="checked"</c:if>>冠心病</label>
												<label><input type="checkbox" name="heredityMother" value="其他" <c:if test="${fn:contains(editEntity.heredityMother,'其他')}">checked="checked"</c:if>>其他</label>
											</div>
										</div>
										<div class="form-group">
											<label for="heredityBs" class="col-sm-4 control-lavel">家族病史（兄弟姐妹）</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="heredityBs" id="heredityBs" value="无" <c:if test="${fn:contains(editEntity.heredityBs,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="checkbox" name="heredityBs" value="高血压" <c:if test="${fn:contains(editEntity.heredityBs,'高血压')}">checked="checked"</c:if>>高血压</label>
												<label><input type="checkbox" name="heredityBs" value="糖尿病" <c:if test="${fn:contains(editEntity.heredityBs,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
												<label><input type="checkbox" name="heredityBs" value="高血脂" <c:if test="${fn:contains(editEntity.heredityBs,'高血脂')}">checked="checked"</c:if>>高血脂</label>
												<label><input type="checkbox" name="heredityBs" value="冠心病" <c:if test="${fn:contains(editEntity.heredityBs,'冠心病')}">checked="checked"</c:if>>冠心病</label>
												<label><input type="checkbox" name="heredityBs" value="其他" <c:if test="${fn:contains(editEntity.heredityBs,'其他')}">checked="checked"</c:if>>其他</label>
											</div>
										</div>
										<div class="form-group">
											<label for="heredityChildren" class="col-sm-4 control-lavel">家族病史（子女）</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="heredityChildren" id="heredityChildren" value="无" <c:if test="${fn:contains(editEntity.heredityChildren,'无')}">checked="checked"</c:if>>无</label>
												<label><input type="checkbox" name="heredityChildren" value="高血压" <c:if test="${fn:contains(editEntity.heredityChildren,'高血压')}">checked="checked"</c:if>>高血压</label>
												<label><input type="checkbox" name="heredityChildren" value="糖尿病" <c:if test="${fn:contains(editEntity.heredityChildren,'糖尿病')}">checked="checked"</c:if>>糖尿病</label>
												<label><input type="checkbox" name="heredityChildren" value="高血脂" <c:if test="${fn:contains(editEntity.heredityChildren,'高血脂')}">checked="checked"</c:if>>高血脂</label>
												<label><input type="checkbox" name="heredityChildren" value="冠心病" <c:if test="${fn:contains(editEntity.heredityChildren,'冠心病')}">checked="checked"</c:if>>冠心病</label>
												<label><input type="checkbox" name="heredityChildren" value="其他" <c:if test="${fn:contains(editEntity.heredityChildren,'其他')}">checked="checked"</c:if>>其他</label>
											</div>
										</div>
							          	<h4 class="block-title"><span>体育锻炼</span></h4>
							          	
										<div class="form-group">
											<label for="frequencyExercise" class="col-sm-4 control-lavel">锻炼频率</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="radio" name="frequencyExercise" value="每天" <c:if test="${fn:contains(editEntity.frequencyExercise,'每天')}">checked="checked"</c:if>>每天</label>
												<label><input type="radio" name="frequencyExercise" value="每周一次以上" <c:if test="${fn:contains(editEntity.frequencyExercise,'每周一次以上')}">checked="checked"</c:if>>每周一次以上</label>
												<label><input type="radio" name="frequencyExercise" value="偶尔" <c:if test="${fn:contains(editEntity.frequencyExercise,'偶尔')}">checked="checked"</c:if>>偶尔</label>
												<label><input type="radio" name="frequencyExercise" value="不锻炼" <c:if test="${fn:contains(editEntity.frequencyExercise,'不锻炼')}">checked="checked"</c:if>>不锻炼</label>
											</div>
										</div>
										<div class="form-group">
											<label for="exerciseTime" class="col-sm-4 control-lavel">每次锻炼时间（分钟）</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="exerciseTime" value="${editEntity.exerciseTime}" <c:if test="${fn:contains(editEntity.frequencyExercise,'不锻炼')}">readOnly="true"</c:if>>
											</div>
										</div>
										<div class="form-group">
											<label for="stickExerciseTime" class="col-sm-4 control-lavel">坚持锻炼时间（年）</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="stickExerciseTime" value="${editEntity.stickExerciseTime}" <c:if test="${fn:contains(editEntity.frequencyExercise,'不锻炼')}">readOnly="true"</c:if>>
											</div>
										</div>
										<div class="form-group">
											<label for="exerciseMode" class="col-sm-4 control-lavel">锻炼方式</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="exerciseMode" value="${editEntity.exerciseMode}" <c:if test="${fn:contains(editEntity.frequencyExercise,'不锻炼')}">readOnly="true"</c:if>>
											</div>
										</div>
							          	<h4 class="block-title"><span>饮食习惯</span></h4>
							          	
										<div class="form-group">
											<label for="eatingHabits" class="col-sm-4 control-lavel">饮食习惯</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="radio" name="eatingHabits" value="荤素均衡" <c:if test="${fn:contains(editEntity.eatingHabits,'荤素均衡')}">checked="checked"</c:if>>荤素均衡</label>
												<label><input type="radio" name="eatingHabits" value="荤食为主" <c:if test="${fn:contains(editEntity.eatingHabits,'荤食为主')}">checked="checked"</c:if>>荤食为主</label>
												<label><input type="radio" name="eatingHabits" value="素食为主" <c:if test="${fn:contains(editEntity.eatingHabits,'素食为主')}">checked="checked"</c:if>>素食为主</label>
												<label><input type="checkbox" name="eatingHabits" value="嗜盐" <c:if test="${fn:contains(editEntity.eatingHabits,'嗜盐')}">checked="checked"</c:if>>嗜盐</label>
												<label><input type="checkbox" name="eatingHabits" value="嗜油" <c:if test="${fn:contains(editEntity.eatingHabits,'嗜油')}">checked="checked"</c:if>>嗜油</label>
												<label><input type="checkbox" name="eatingHabits" value="嗜糖" <c:if test="${fn:contains(editEntity.eatingHabits,'嗜糖')}">checked="checked"</c:if>>嗜糖</label>
											</div>
										</div>
										<h4 class="block-title"><span>吸烟情况</span></h4>
										
										<div class="form-group">
											<label for="smokingStatus" class="col-sm-4 control-lavel">吸烟状况</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="radio" name="smokingStatus" value="从不吸烟" <c:if test="${fn:contains(editEntity.smokingStatus,'从不吸烟')}">checked="checked"</c:if>>从不吸烟</label>
												<label><input type="radio" name="smokingStatus" value="已戒烟" <c:if test="${fn:contains(editEntity.smokingStatus,'已戒烟')}">checked="checked"</c:if>>已戒烟</label>
												<label><input type="radio" name="smokingStatus" value="吸烟" <c:if test="${editEntity.smokingStatus == '吸烟'}">checked="checked"</c:if>>吸烟</label>
											</div>
										</div>
										<div class="form-group">
											<label for="dailySmoking" class="col-sm-4 control-lavel">日吸烟量（支)</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="dailySmoking" value="${editEntity.dailySmoking}" <c:if test="${fn:contains(editEntity.smokingStatus,'从不吸烟')}">readOnly="true"</c:if>>
											</div>
										</div>
										<div class="form-group">
											<label for="smokingAge" class="col-sm-4 control-lavel">开始吸烟年龄（岁）</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="smokingAge" value="${editEntity.smokingAge}" <c:if test="${fn:contains(editEntity.smokingStatus,'从不吸烟')}">readOnly="true"</c:if>>
											</div>
										</div>
										<div class="form-group">
											<label for="smokingCessation" class="col-sm-4 control-lavel">戒烟年龄（岁）</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="smokingCessation" value="${editEntity.smokingCessation}" <c:if test="${fn:contains(editEntity.smokingStatus,'从不吸烟') || fn:contains(editEntity.smokingStatus,'吸烟')}">readOnly="true"</c:if>>
											</div>
										</div>
										<h4 class="block-title"><span>饮酒情况</span></h4> 
										
										<div class="form-group">
											<label for="drinkingFrequency" class="col-sm-4 control-lavel">饮酒频率</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="radio" name="drinkingFrequency" value="从不"  <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">checked="checked"</c:if>>从不</label>
												<label><input type="radio" name="drinkingFrequency" value="偶尔" <c:if test="${fn:contains(editEntity.drinkingFrequency,'偶尔')}">checked="checked"</c:if>>偶尔</label>
												<label><input type="radio" name="drinkingFrequency" value="经常" <c:if test="${fn:contains(editEntity.drinkingFrequency,'经常')}">checked="checked"</c:if>>经常</label>
												<label><input type="radio" name="drinkingFrequency" value="每天" <c:if test="${fn:contains(editEntity.drinkingFrequency,'每天')}">checked="checked"</c:if>>每天</label>
											</div>
										</div>
										<div class="form-group">
											<label for="drinkVolume" class="col-sm-4 control-lavel">日饮酒量（两）</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="drinkVolume" value="${editEntity.drinkVolume}" <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">readOnly="true"</c:if>>
											</div>
										</div>
										<div class="form-group">
											<label for="whetherAlcohol" class="col-sm-4 control-lavel">是否戒酒</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="radio" name="whetherAlcohol" onchange="changeAcOption(this)" value="未戒酒" <c:if test="${fn:contains(editEntity.whetherAlcohol,'未戒酒')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>未戒酒</label>
												<label><input type="radio" name="whetherAlcohol" onchange="changeAcOption(this)" value="已戒酒" <c:if test="${fn:contains(editEntity.whetherAlcohol,'已戒酒')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>已戒酒</label>
												<div id="alcohollOtherDiv" <c:if test="${!fn:contains(editEntity.whetherAlcohol,'已戒酒')}">style="display:none"</c:if>>
												 	<label>戒酒年龄：</label>
												 	<input type="text" name="alcoholAge" id="alcoholAge" value="${editEntity.alcoholAge }" <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">readOnly</c:if>>
												</div>
								          	</div>
										</div>
										
										<div class="form-group">
											<label for="drinkingAge" class="col-sm-4 control-lavel">开始饮酒年龄（岁）</label>
											<div class="col-sm-8">
												<input type="text" class="form-control" name="drinkingAge" value="${editEntity.drinkingAge}" <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">readOnly="true"</c:if>>
											</div>
										</div>
										<div class="form-group">
											<label for="drunkPastyear" class="col-sm-4 control-lavel">近一年内是否曾醉酒</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="radio" name="drunkPastyear" value="是" <c:if test="${fn:contains(editEntity.drunkPastyear,'是')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>是</label>
												<label><input type="radio" name="drunkPastyear" value="否" <c:if test="${fn:contains(editEntity.drunkPastyear,'否')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>否</label>
											</div>
										</div>
										<div class="form-group">
											<label for="drinkingType" class="col-sm-4 control-lavel">饮酒种类</label>
											<div class="col-sm-8 checkbox-list">
												<label><input type="checkbox" name="drinkingType" value="白酒" <c:if test="${fn:contains(editEntity.drinkingType,'白酒')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>白酒</label>
												<label><input type="checkbox" name="drinkingType" value="啤酒" <c:if test="${fn:contains(editEntity.drinkingType,'啤酒')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>啤酒</label>
												<label><input type="checkbox" name="drinkingType" value="红酒" <c:if test="${fn:contains(editEntity.drinkingType,'红酒')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>红酒</label>
												<label><input type="checkbox" name="drinkingType" value="黄酒" <c:if test="${fn:contains(editEntity.drinkingType,'黄酒')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>黄酒</label>
												<label><input type="checkbox" name="drinkingType" value="其他" <c:if test="${fn:contains(editEntity.drinkingType,'其他')}">checked="checked"</c:if> <c:if test="${fn:contains(editEntity.drinkingFrequency,'从不')}">disabled="true"</c:if>>其他</label>
											</div>
										</div>
									</form>
                                 </div>
					<div class="panel-footer">
							<div class="form-group button-center-block">
							<input class="btn btn-primary btn-lg" type="submit"
								id="saveBtn"
								value="保存数据">
							</div>
					</div>
                             </div>
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>
</body>
</html>