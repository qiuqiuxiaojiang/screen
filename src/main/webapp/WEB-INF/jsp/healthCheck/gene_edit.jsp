<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp"%>
<head>
<title>录入基因检测信息</title>
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
.supF{
    background: #b3afaf;
    color: #fff;
    border-radius: 50%;
    width: 13px;
    height: 13px;
    font-size: 15px;
    display: inline-block;
    line-height: 13px;
    text-align: center;
    left: 4px;
    cursor: pointer;
}
.diseaseLabel{
	text-align:right;
	font-weight:normal;
}
label{
	min-height: 10px!important;
    height: auto!important;
    line-height: 15px!important;
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
						<input type="hidden" name="tnb" id="tnb" value='${editEntity.tnb}' >
						<input type="hidden" name="gxy" id="gxy" value='${editEntity.gxy}'>
						<input type="hidden" name="gxz" id="gxz" value='${editEntity.gxz}'>
						<input type="hidden" name="sampleTime" id="sampleTime" value='${sampleTime}'>
						<input type="hidden" name="tnblen" id="tnblen">
						<input type="hidden" name="gxylen" id="gxylen">
						<input type="hidden" name="gxzlen" id="gxzlen">
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
							
							<h4 class="block-title"><span>基因检测</span></h4>
							
							<div id="tnbs">
								<div class="row" style="padding:15px;" id="tnb0">
									<label class="col-md-1 control-lavel" id="tnbLabel">糖尿病用药套餐或糖尿病基因检测4项:</label>
									<div class="col-md-11">
										<div id="tnbDiv"></div>
										<!-- <div class="col-md-12 controls">
											<label class="col-md-1 control-lavel diseaseLabel">取样时间:</label><span>2020-07-28</span> &nbsp;&nbsp;&nbsp;&nbsp; 条形编码：<span>123456</span><sup class="supF">×</sup>
										</div> -->
										
										<div class="col-md-12 controls">
											<label class="col-md-1 control-lavel" style="text-align:right;">取样时间:</label>
											<div class="col-md-2 controls">
												<input class="Wdate" name="tnbTime" id="tnbTime" readonly type="text" value="${sampleTime}"
														onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
											</div>
											<label class="col-md-1 control-lavel" style="text-align:right;">条码编号</label>
											<div class="col-md-3 controls">
												<input type="text" class="form-control" name="tnbNo" id="tnbNo">
												<!-- <input type="text" class="form-control" name="tnbNo" aspId="11110000"  onblur="getState(this);"> -->
											</div>
											<input type="button" class="btn btn-primary " value="添加" onclick="addGene('tnb');">
										</div>
									</div>
									
								</div>
							</div>
							
							<div id="gxys">
								<div class="row" style="padding:15px;" id="gxy0">
									<label class="col-md-1 control-lavel">高血压用药套餐:</label>
									<div class="col-md-11">
										<div id="gxyDiv"></div>
										<div class="col-md-12 controls">
										<label class="col-md-1 control-lavel" style="text-align:right;">取样时间:</label>
										<div class="col-md-2 controls">
											<input class="Wdate" name="gxyTime" readonly type="text" value="${sampleTime}"  id="gxyTime"
													onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
										</div>
										<label class="col-md-1 control-lavel" style="text-align:right;">条码编号</label>
										<div class="col-md-3 controls">
											<input type="text" class="form-control" name="gxyNo" id="gxyNo">
										</div>
										<input type="button" class="btn btn-primary " value="添加" onclick="addGene('gxy');">
										</div>
									</div>
									
								</div>
							</div>
							
							<div id="gxzs">
								<div class="row" style="padding:15px;" id="gxz0">
									<label class="col-md-1 control-lavel">他汀类降脂药套餐:</label>
									<div class="col-md-11">
										<div id="gxzDiv"></div>
										<div class="col-md-12 controls">
										<label class="col-md-1 control-lavel" style="text-align:right;">取样时间:</label>
										<div class="col-md-2 controls">
											<input class="Wdate" name="gxzTime" readonly type="text" value="${sampleTime}" id="gxzTime"
													onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-%d'})">
										</div>
										<label class="col-md-1 control-lavel" style="text-align:right;">条码编号</label>
										<div class="col-md-3 controls">
											<input type="text" class="form-control" name="gxzNo" id="gxzNo">
										</div>
										<input type="button" class="btn btn-primary " value="添加" onclick="addGene('gxz');">
										</div>
									</div>
									
								</div>
							</div>
							
							<input type="hidden" name="geneId" id="geneId" value="${editEntity.geneId}">
					</div>
					<div class="panel-footer">
						<div class="form-group button-center-block">
							<div class="col-sm-6 controls" >
								<input class="btn btn-primary btn-lg" type="submit" id="saveBtn" value="保存数据"> 
							</div> 
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
	/* var tnbNo = 1;
	var gxyNo = 1;
	var gxzNo = 1; */
	
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
			url : "${ctx}/manage/hc/queryInfoGene.json",
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
					
					var tnb = data.dataMap.tnb;
					var gxy = data.dataMap.gxy;
					var gxz = data.dataMap.gxz;
					showGene(tnb, gxy, gxz);
					
					if (tnb != "" && tnb != null) {
						$("#tnblen").val(tnb.length);
					}
					
					if (gxy != "" && gxy != null) {
						$("#gxylen").val(gxy.length);
					}
					
					if (gxz != "" && gxz != null) {
						$("#gxzlen").val(gxz.length);
					}
					
					$("#geneId").val(data.dataMap.geneId);
					
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
    	var flag = false;
    	var tnbTime = $("#tnbTime").val();
    	var tnbNo = $("#tnbNo").val();
    	if (tnbTime == "" && tnbNo != "") {
			alert("采样时间不能为空");
			return false;
		} else if((tnbTime == '${sampleTime}') && tnbNo == ""){
			$("#tnbTime").val("");
			flag = true;
		}  else if(tnbTime != "" && tnbNo == ""){
			alert("条码编号不能为空");
			return false;
		} else {
			flag = true;
		}
    	
    	var gxyTime = $("#gxyTime").val();
    	var gxyNo = $("#gxyNo").val();
    	if (gxyTime == "" && gxyNo != "") {
			alert("采样时间不能为空");
			return false;
		} else if((gxyTime == '${sampleTime}') && gxyNo == ""){
			$("#gxyTime").val("");
			flag = true;
		} else if(gxyTime != "" && gxyNo == ""){
			alert("条码编号不能为空");
			return false;
		} else {
			flag = true;
		}
    	
    	var gxzTime = $("#gxzTime").val();
    	var gxzNo = $("#gxzNo").val();
    	if (gxzTime == "" && gxzNo != "") {
			alert("采样时间不能为空");
			return false;
		} else if((gxzTime == '${sampleTime}') && gxzNo == ""){
			$("#gxzTime").val("");
			flag = true;
		} else if(gxzTime != "" && gxzNo == ""){
			alert("条码编号不能为空");
			return false;
		} else {
			flag = true;
		}
    	
    	//$("input[name='tnbTime']").val();
    	var tnbNos =[];
        $("input[name='tnbNo']").each(function(){
        	if ($(this).val() != "" && $(this).val() != null) {
        		tnbNos.push($(this).val());
        	}
        });
        
        var gxyNos =[];
        $("input[name='gxyNo']").each(function(){
        	if ($(this).val() != "" && $(this).val() != null) {
        		gxyNos.push($(this).val());
        	}
        });
        
        var gxzNos =[];
        $("input[name='gxzNo']").each(function(){
        	if ($(this).val() != "" && $(this).val() != null) {
        		gxzNos.push($(this).val());
        	}
        });
        console.log("tnbNos:" + tnbNos);
        console.log("gxyNos:" + gxyNos);
        console.log("gxzNos:" + gxzNos);
        
       /*  var tnbRepeat = isRepeat(tnbNos);
        if (tnbRepeat) {
        	alert("重复");
        } */
        var tnbArry = tnbNos.sort();
        for(var i = 0; i < tnbArry.length - 1; i++)
        {
           if (tnbArry[i] == tnbArry[i+1])
            {
               alert("条形编码为【 " + tnbArry[i] + " 】的糖尿病用药套餐已存在，请重新输入");
               return false;
            }
        }
        
        var gxyArry = gxyNos.sort();
        for(var i = 0; i < gxyArry.length - 1; i++)
        {
           if (gxyArry[i] == gxyArry[i+1])
            {
               alert("条形编码为【 " + gxyArry[i] + " 】的高血压用药套餐已存在，请重新输入");
               return false;
            }
        }
        
        var gxzArry = gxzNos.sort();
        for(var i = 0; i < gxzArry.length - 1; i++)
        {
           if (gxzArry[i] == gxzArry[i+1])
            {
               alert("条形编码为【 " + gxzArry[i] + " 】的他汀类降脂药套餐已存在，请重新输入");
               return false;
            }
        }
        
    	
    	/* var tnblen = $("#tnblen").val();
		var gxylen = $("#gxylen").val();
		var gxzlen = $("#gxzlen").val();
		
		var tnbNo = parseInt(tnblen);
		var gxyNo = parseInt(gxylen);
		var gxzNo = parseInt(gxzlen); */
		
		var tnbNo = tnbNos.length;
		var gxyNo = gxyNos.length;
		var gxzNo = gxzNos.length;
		
    	if (tnbNo == 0 && gxyNo == 0 && gxzNo == 0) {
    		alert("请填写基因检测相关信息");
    		return false;
    	}
    	
    	if (flag) {
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
    }
    
    function isRepeat(arr){
 	   var hash = {};
 	   for(var i in arr) {
 	      if(hash[arr[i]])
 	        return true;
 	      hash[arr[i]] = true;
 	  }
 	  return false;
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
    
   	 /* function addGene(type) {
    	var tnblen = $("#tnblen").val();
		var gxylen = $("#gxylen").val();
		var gxzlen = $("#gxzlen").val();

		var tnbNo = parseInt(tnblen);
		var gxyNo = parseInt(gxylen);
		var gxzNo = parseInt(gxzlen);
		
    	var sampleTime = $("#sampleTime").val();
    	if (type == "tnb") {
    		genePackage("tnb", "tnb"+tnbNo, sampleTime, "");
    		tnbNo++
    		$("#tnblen").val(tnbNo);
    	} else if(type == "gxy") {
		    genePackage("gxy", "gxy"+gxyNo, sampleTime, "");
		    gxyNo++
		    $("#gxylen").val(gxyNo);
    	} else if(type == "gxz") {
    		genePackage("gxz", "gxz"+gxzNo, sampleTime, "");
    		gxzNo++
    		$("#gxzlen").val(gxzNo);
    	}
    }
    
    // type tnb：糖尿病;  gxy：高血压; gxz：高血脂
    // id 外层div的id
    // time 取样时间的值
    // num 条码编号
    function genePackage(type,id, time, num) {
    	$("#"+type+"s").append('<div class="row" style="padding:15px;" id="'+ id +'">'
	    		+'<label class="col-md-1 control-lavel"></label>'
	    		+'<label class="col-md-1 control-lavel" style="text-align:right;">取样时间</label>'
	    		+'<div class="col-md-2 controls">'
	    		+'<input class="Wdate" value="'+ time +'" name="'+type+'Time" readonly type="text" onClick="WdatePicker({el:this,dateFmt:\''+'yyyy-MM-dd'+'\',maxDate:\''+'%y-%M-%d'+'\'})">'
	    		+'</div>'
	    		+'<label class="col-md-1 control-lavel" style="text-align:right;">条码编号</label>'
	    		+'<div class="col-md-3 controls">'
	    		+'<input type="text" class="form-control" value="' + num + '"  name="'+type+'No">'
	    		+'</div>'
	    		+'</div>');
    } */
    
    function addGene(type) {
    	var tnblen = $("#tnblen").val();
		var gxylen = $("#gxylen").val();
		var gxzlen = $("#gxzlen").val();

		var tnbNo = parseInt(tnblen);
		var gxyNo = parseInt(gxylen);
		var gxzNo = parseInt(gxzlen);
		
		var timeVal = $("#" + type + "Time").val();//取样时间
		var numVal = $("#" + type + "No").val();//条码编号
		if (timeVal == null || timeVal == "" || numVal == null || numVal == "") {
			alert("请填写取样时间和条码编号");
			return;
		}
		
    	var sampleTime = $("#sampleTime").val();
    	if (type == "tnb") {
    		genePackage("tnb", "tnb"+tnbNo, timeVal, numVal);
    		tnbNo++
    		$("#tnblen").val(tnbNo);
    	} else if(type == "gxy") {
		    genePackage("gxy", "gxy"+gxyNo, timeVal, numVal);
		    gxyNo++
		    $("#gxylen").val(gxyNo);
    	} else if(type == "gxz") {
    		genePackage("gxz", "gxz"+gxzNo, timeVal, numVal);
    		gxzNo++
    		$("#gxzlen").val(gxzNo);
    	}
    	
    	$("#" + type + "Time").val("${sampleTime}");//取样时间
		$("#" + type + "No").val("");
    }
    
    function genePackage(type,id, time, num) {
    	$("#"+type+"Div").append('<div class="col-md-12 controls">'+
				'<label class="col-md-1 control-lavel diseaseLabel">取样时间:</label><span name="'+type+'Time">'+ time +'</span> &nbsp;&nbsp;&nbsp;&nbsp;'+ 
				'条形编码：<span name="' + type + 'No">' + num + '</span><sup class="supF" onclick="deleteGene(this);">×</sup>'+
				'<input class="Wdate" value="'+ time +'" name="'+type+'Time" readonly type="hidden" onClick="WdatePicker({el:this,dateFmt:\''+'yyyy-MM-dd'+'\',maxDate:\''+'%y-%M-%d'+'\'})">'	+
				'<input type="hidden" class="form-control" value="' + num + '"  name="'+type+'No">'+
				'</div>'
    	);
    } 
    
    function showGene(tnb, gxy, gxz) {
		$(tnb).each(function(i,v){
			console.log(tnb.length);
			if (i == tnb.length-1) {
				$("#tnbTime").val(v.tnbTime);
				$("#tnbNo").val(v.tnbNo);
			} else {
				var tnbTime = v.tnbTime;
				var tnbNo = v.tnbNo;
				genePackage("tnb", "tnb"+i, tnbTime, tnbNo);
			}
		}); 
		
		$(gxy).each(function(i,v){
			if (i == gxy.length-1) {
				$("#gxyTime").val(v.gxyTime);
				$("#gxyNo").val(v.gxyNo);
			} else {
				var gxyTime = v.gxyTime;
				var gxyNo = v.gxyNo;
				genePackage("gxy", "gxy"+i, gxyTime, gxyNo);
			}
		}) 
		
		$(gxz).each(function(i,v){
			if (i == gxz.length-1) {
				$("#gxzTime").val(v.gxzTime);
				$("#gxzNo").val(v.gxzNo);
			} else {
				var gxzTime = v.gxzTime;
				var gxzNo = v.gxzNo;
				genePackage("gxz", "gxz"+i, gxzTime, gxzNo);
			}
		}); 
    }
    
    $(function() {
    	var tnb = $("#tnb").val();
	    var gxy = $("#gxy").val();
	    var gxz = $("#gxz").val();
	    if (tnb != "") {
	    	tnb = eval(tnb);
	    	$("#tnblen").val(tnb.length);
	    }
	    
	    if (gxy != "") {
	    	gxy = eval(gxy);
	    	$("#gxylen").val(gxy.length);
	    }
	    
	    if (gxz != "") {
	    	gxz = eval(gxz);
	    	$("#gxzlen").val(gxz.length);
	    }
	    
		showGene(tnb, gxy, gxz);
		
    });
    
    function getState(obj){
    	var serviceCode = $(obj).val();
    	var aspId = $(obj).attr("aspId");
    	var name = $("#customerName").val();
    	$.ajax({
			type : "post",
			url : "${ctx}/gene/getGeneInfo.json",
			data : {
				aspId : aspId,
				serviceCode : serviceCode,
				name : name
			},
			success:function(data){
				if(data.code==0){
					var dataMap = data.dataMap;
					console.log("dataMap:" + dataMap);
					
					var code = dataMap.code;
					if (code == "200") {
						var state = dataMap.data.state;
						$(obj).append('<label class="col-md-1 control-lavel" style="text-align:right;">'+state+'</label>');
					}
				}
			}
		});
    }
    
    function deleteGene(obj) {
    	$(obj).parent().remove();
    }
    
</script>
</html>