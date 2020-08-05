<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>修改用户</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<script src="${ctx_jquery }/jquery.validate.js"></script>
    <script src="${ctx_js }/validate.expand.js"></script>
    <style type="text/css">
	    label.error{
	    	color:Red; 
			font-size:13px; 
	    }
    </style>
</head>
<body>
<div class="container-fluid">
                 <!-- 以下这是页面需要编辑的区域 -->
                   <div class="row">
                       <div >
                           <div class="panel panel-warning">
                                <div class="panel-heading">修改密码</div>
                                 <form id="updatepass" role="form" method="post">
                                <div class="panel-body">
	                               	<div class="form-group">
										<label for="before_password" class="col-sm-2 control-lavel">原密码：</label>
										<div class="col-sm-10">
										<input type="password" class="form-control" id="before_password" name="before_password">
										</div>
									</div>
									
									<div class="form-group">
										<label for="password" class="col-sm-2 control-lavel">新密码</label>
										<div class="col-sm-10">
										<input type="password" class="form-control" name="password" id="password">
										</div>
									</div>
									
									<div class="form-group">
										<label for="confirm_password" class="col-sm-2 control-lavel">确认密码</label>
										<div class="col-sm-10">
										<input type="password" class="form-control" name="confirm_password" id="confirm_password">
										</div>
									</div>
									<div>
										<input class="btn btn-default" type="submit" id="saveBtn" value="提交">
									</div>
									
                               </div>
                               </form>
                           </div>
                       </div>
                   </div>
                 <!-- 以上这是页面需要编辑的区域 -->
   </div>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#saveBtn").click(function(){
				var options = {
				    url:"${ctx}/user/savePass.json",
					success:function(o){
						if(o.message="success"){
							alert("保存成功!");
							window.location.href="${ctx}/login/toLogin.htm";
						}else{
							alert("保存失败！");
						}
					}
				};
				$("#updatepass").ajaxForm(options);
			})
			
			
			$("#updatepass").validate({
				rules:{
					before_password:{
						required:true/* ,
						remote:"${ctx}/user/checkPwd.htm" */
					},
					password:{
						required: true,  
	    	  			minlength: 8,
	    	  			alnum:true
					},
					confirm_password:{
						required:true,
						equalTo:"#password"
					}
				},
				messages:{
					before_password:{
						required:"请输入原密码"/* ,
						remote:"原密码不正确，请重新输入" */
					},
					password:{
						required:"请输入密码",
						minlength:"密码不能小于8个字符"
					},
					confirm_password:{
						required:"请输入确认密码",
						equalTo:"两次输入密码不一致，请重新输入"
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
		
		
		</script>
</body>
</html>