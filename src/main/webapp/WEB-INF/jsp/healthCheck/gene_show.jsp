<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp"%>
<head>
<title>基因检测信息</title>
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
</style>
</head>
<body>
<div style="display:none">
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
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
									<label class="col-md-2 control-lavel" id="tnbLabel">${editEntity.tnbStr }</label>
									<div class="col-md-10">
										<div id="tnbDiv"></div>
									</div>
								</div>
							</div>
							
							<div id="gxys">
								<div class="row" style="padding:15px;" id="gxy0">
									<label class="col-md-2 control-lavel">${editEntity.gxyStr }</label>
									<div class="col-md-10">
										<div id="gxyDiv"></div>
									</div>
								</div>
							</div>
							
							<div id="gxzs">
								<div class="row" style="padding:15px;" id="gxz0">
									<label class="col-md-2 control-lavel">${editEntity.gxzStr }</label>
									<div class="col-md-10">
										<div id="gxzDiv"></div>
									</div>
								</div>
							</div>
							
							<input type="hidden" name="geneId" id="geneId" value="${editEntity.geneId}">
					</div>
					<div class="panel-footer">
						<div class="form-group button-center-block">
						<input class="btn btn-primary btn-lg" type="button"
							onclick="javascript:history.go(-1);"
							value="返回">
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
    function isRepeat(arr){
 	   var hash = {};
 	   for(var i in arr) {
 	      if(hash[arr[i]])
 	        return true;
 	      hash[arr[i]] = true;
 	  }
 	  return false;
 }
    
    function genePackage(type,id, time, num) {
    	$("#"+type+"Div").append('<div class="col-md-12 controls">'+
				'<label class="col-md-1 control-lavel diseaseLabel">取样时间:</label><span name="'+type+'Time">'+ time +'</span> &nbsp;&nbsp;&nbsp;&nbsp;'+ 
				'条形编码：<span name="' + type + 'No">' + num + '</span>'+
				'<input class="Wdate" value="'+ time +'" name="'+type+'Time" readonly type="hidden" onClick="WdatePicker({el:this,dateFmt:\''+'yyyy-MM-dd'+'\',maxDate:\''+'%y-%M-%d'+'\'})">'	+
				'<input type="hidden" class="form-control" value="' + num + '"  name="'+type+'No">'+
				'</div>'
    	);
    } 
    
    function showGene(tnb, gxy, gxz) {
		$(tnb).each(function(i,v){
			console.log(tnb.length);
			var tnbTime = v.tnbTime;
			var tnbNo = v.tnbNo;
			genePackage("tnb", "tnb"+i, tnbTime, tnbNo);
		}); 
		
		$(gxy).each(function(i,v){
			var gxyTime = v.gxyTime;
			var gxyNo = v.gxyNo;
			genePackage("gxy", "gxy"+i, gxyTime, gxyNo);
		}) 
		
		$(gxz).each(function(i,v){
			var gxzTime = v.gxzTime;
			var gxzNo = v.gxzNo;
			genePackage("gxz", "gxz"+i, gxzTime, gxzNo);
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