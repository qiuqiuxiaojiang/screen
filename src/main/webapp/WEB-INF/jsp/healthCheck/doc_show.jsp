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
	/* margin-left:5px; 
		padding-left:5px;  
		background:url("error.png") left no-repeat; */
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
					<form id="healthcheckInfo" role="form" method="post">
						<input type="hidden" name="uniqueId" id="uniqueId">
						<input type="hidden" name="idCardInputTag" id="idCardInputTag" value="auto">
					<div class="panel-body">
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
								<label for="birthday" class="col-sm-4 control-lavel">出生日期</label>
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
								<label for="nation" class="col-sm-4 control-lavel">民族</label>
								<div class="col-sm-8 controls">
									${showEntity.nationality}
								</div>
							</div>
							<div class="form-group">
								<label for="mobile" class="col-sm-4 control-lavel">手机号码</label>
								<div class="col-sm-8 controls">
									${showEntity.mobile}
								</div>
							</div>
							<div class="form-group">
								<label for="address" class="col-sm-4 control-lavel">地址</label>
								<div class="col-sm-8 controls">
									${showEntity.address}
								</div>
							</div>
							
							<div class="form-group">
								<label for="contactName" class="col-sm-4 control-lavel">联系人姓名</label>
								<div class="col-sm-8 controls">
									${showEntity.contactName}
								</div>
							</div>
							<div class="form-group">
								<label for="contactMobile" class="col-sm-4 control-lavel">联系人电话</label>
								<div class="col-sm-8 controls">
									${showEntity.contactMobile}
								</div>
							</div>
							<div class="form-group">
								<label for="contactMobile" class="col-sm-4 control-lavel">公司</label>
								<div class="col-sm-8 controls">
									${showEntity.company}
								</div>
							</div>
							<div class="form-group" style="width:100%;min-height:30px;height:auto;">
								<label for="url" class="col-sm-4 control-lavel">是否患有疾病</label>
								<c:if test="${showEntity.haveDisease=='是' }">
									<div class="col-sm-6" id="diseaseDiv">
										${showEntity.disease }
									</div>
								</c:if>
								<c:if test="${showEntity.haveDisease=='否' }">
									<div class="col-sm-6">
										${showEntity.haveDisease }
									</div>
								</c:if>
							</div>
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">是否有疾病家族史</label>
								<c:if test="${showEntity.familyHistory=='是' }">
									<div class="col-sm-6" id="diseaseDiv">
										${showEntity.familyDisease }
									</div>
								</c:if>
								<c:if test="${showEntity.familyHistory=='否' }">
									<div class="col-sm-6">
										${showEntity.familyHistory }
									</div>
								</c:if>
							</div>
							</div>
							<div class="form-group">
								<label for="checkDate" class="col-sm-4 control-lavel">建档日期</label>
								<div class="col-sm-8 controls">
									${showEntity.recordDate}
								</div>

							</div>
							
							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">筛查地点</label>
								<div class="col-sm-8 controls form-inline" >
											${showEntity.district}&nbsp;${showEntity.checkPlace}
								</div>
							</div>

							<div class="form-group">
								<label for="url" class="col-sm-4 control-lavel">筛查组</label>
								<div class="col-sm-8 controls">
									${showEntity.checkGroup}
								</div>
							</div>
							
							<c:if test="${item=='kunming' }">
								<div class="form-group">
									<label for="url" class="col-sm-4 control-lavel">所属街道及社区</label>
									<div class="col-sm-8 controls">
										${showEntity.street} ${showEntity.community}社区
									</div>
								</div>
							</c:if>
							
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
</html>