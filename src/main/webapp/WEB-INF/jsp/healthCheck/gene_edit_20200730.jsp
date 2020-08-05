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
						<input type="hidden" id="dataId" name="id" value="${editEntity.id}" />
						<input type="hidden" name="uniqueId" id="uniqueId" value="${editEntity.uniqueId }">
					<!-- <div class="panel-heading">添加初筛数据</div> -->
						<div class="panel-body">
							<h4 class="block-title"><span>基础信息</span></h4>
							
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
									<input type="text" class="form-control" readonly id="customerName" name="name" value="${editEntity.name}"
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
								<label for="customerId" class="col-sm-4 control-lavel"><span style="color:red;">*</span>出生日期</label>
								<div class="col-sm-8 controls">
									<input type="text" id="birthday" class="form-control" readonly="readonly" name="birthday" value="${editEntity.birthday}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="age" class="col-sm-4 control-lavel"><span style="color:red;">*</span>年龄</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="age" readonly="readonly" name="age" value="${editEntity.age}"
										>
								</div>
							</div>
							<div class="form-group">
								<label for="cardId" class="col-sm-4 control-lavel">手机号码</label>
								<div class="col-sm-8 controls">
									<input type="text" class="form-control" id="mobile" name="mobile" readonly value="${editEntity.mobile}">
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
							
							<%-- <div class="form-group">
								<label for="highPressure" class="col-md-4 control-lavel">样本编号</label>
								<div class="col-md-8 controls">
									<input type="text" class="form-control" name="sampleId" value="${editEntity.sampleId}">
								</div>
							</div> --%>

							<h4 class="block-title"><span>基因检测</span></h4>
							
							<div class="form-group">
								<label for="highPressure" class="col-md-4 control-lavel">糖尿病用药套餐</label>
								<div class="col-md-8 controls">
									<input type="text" class="form-control" name="tangniaobing" value="${editEntity.tangniaobing}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="highPressure" class="col-md-4 control-lavel">高血压用药套餐</label>
								<div class="col-md-8 controls">
									<input type="text" class="form-control" name="gaoxueya" value="${editEntity.gaoxueya}">
								</div>
							</div>
							
							<div class="form-group">
								<label for="highPressure" class="col-md-4 control-lavel">他汀类降脂药套餐</label>
								<div class="col-md-8 controls">
									<input type="text" class="form-control" name="gaoxuezhi" value="${editEntity.gaoxuezhi}">
								</div>
							</div>
							<input type="hidden" name="geneId" id="geneId" value="${geneId}">
					</div>
					<div class="panel-footer">
						<div class="form-group button-center-block">
								<input class="btn btn-primary btn-lg" type="submit" id="saveBtn" value="保存数据"> 
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
	
	function queryInfo(idCard) {
		$.ajax({
			type : "post",
			url : "${ctx}/manage/hc/queryInfo.json",
			data : {
				customerId:idCard
			},
			success:function(data){
				if(data.code==0){
					$('#customerId').attr("readonly", "readonly");
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
					
					$('#inputQueryButton').hide();
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
					familyHistory:{
						required:true
					},
					mobile:{
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
					mobile:{
						required:"请输入手机号"
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
	   

	   
	 $(document).ready(function(){
			$("#saveBtn").click(function(){
				var uniqueId=$('#uniqueId').val();
		    	if (uniqueId==null||uniqueId=='') {
		    		alert("此用户尚未建档，请建档后再筛查！");
		    		return false;
		    	} else {
		    		var options = submitFormsAjax();
		    		
		    		$("#healthcheckInfo").ajaxForm(options);
		    		return true;
		    	}
			});
	}); 
	
	
    
    /**设置延时**/
    function sleep(d){
          for(var t = Date.now();Date.now() - t <= d;);
    }
   
   
    /**保存表单信息  ajax**/
    function submitFormsAjax() {
        var options = {
                url:"${ctx}/gene/saveGene.json",
                success:function(o){
                	if (o.code == 0) {
                        if(o.message =="success"){
                        	alert("数据保存成功！")
                        	window.location.href="${ctx}/gene/geneUI.htm";
                        	$("#urlresourceid").val(o.data);
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
  
    /**页面跳转**/
     function jump() {
    	 window.location.href="${ctx}/gene/geneUI.htm";
     }
    
  

</script>


</html>