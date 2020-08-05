<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="/_header.jsp"%>
<html lang="zh-CN">
<head>
	<title>注册</title>
    <link rel="stylesheet" href="${ctx_bootstrap }/css/bootstrap.css">
    <link rel="stylesheet" href="${ctx_static }/css/signin.css">
    <script src="${ctx_jquery }/jquery-1.11.2.js"></script>
    <script src="${ctx_bootstrap }/js/bootstrap.js"></script>
    <script src="${ctx_jquery }/jquery.validate.js"></script>
    <script src="${ctx_js }/validate.expand.js"></script>
    <style type="text/css">
    label.error{
    	color:Red; 
		font-size:13px; 
		/* margin-left:5px; 
		padding-left:5px;  
		background:url("error.png") left no-repeat; */

    }
    </style>
     <script type="text/javascript">
	     	/* $.validator.setDefaults({
	    		submitHandler: function() { 
	    			alert("submitted!");
	    			window.location.href="${ctx}/register/regist.htm";
	    			//window.location.href="${ctx}/user/userlist.htm";
	    		}
	    	}); */
	    	
	    	 $().ready(function() {  
	    		$("#signupForm").validate({  
	    	       	rules: {  
	    	       		username: {
	    	       			required:true,
	    	       			remote:"${ctx}/register/checkUser.htm",
	    	       			rangelength:[6,20],
	    	       			alnum:true
	    	       		},  
		    	 		email: {  
		    	  			required: true,  
		    	   			email: true  
		    	   		},  
		    	   		password: {  
		    	   			required: true,  
		    	  			minlength: 5  
		    	   		},  
		    	   		confirm_password: {  
		    	    		required: true,  
				    	    minlength: 5,  
				    	    equalTo: "#password"  
		    	   		},
		    	   		telephone:{
		    	   			isMobile:true
		    	   		}
	    	  		},  
	    	        messages: {  
	    	        	username: {
	    	        		required:"请输入用户名",
	    	        		remote: "该会员名已存在！",
	    	        		rangelength:$.validator.format("请输入{0} - {1} 个字符")
	    	        	},  
			    	    email: {  
				    	    required: "请输入Email地址"
		    	    	},  
	    	   			password: {  
				    	    required: "请输入密码",  
				    	    minlength: "密码不能小于5个字符",  
	    	   			},  
	    	   			confirm_password: {  
				    	    required: "请输入确认密码",  
				    	    minlength: "确认密码不能小于5个字符",  
				    	    equalTo: "两次输入密码不一致"  
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
</head>
<body>
	<div  class="container">
		<form id="signupForm" class="form-signin" action="${ctx}/register/regist.htm" method="post">
			<h2 class="form-signin-heading">用户注册</h2>
			<input type="hidden" name="userid">
			<div class="control-group">
				<label class="control-label" for="username">用户名：</label>
				<div class="controls">
					<input type="text" name="username" id="username" class="form-control" placeholder="请输入用户名" >
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="password">密    码：</label>
				<div class="controls">
					<input type="password" name="password" id="password" class="form-control" placeholder="请输入密码" >
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="confirm_password">确认密码：</label>
				<div class="controls">
					<input type="password" name="confirm_password" id="confirm_password" class="form-control">
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="telephone">联系方式：</label>
				<div class="controls">
					<input type="text" name="telephone" id="telephone" class="form-control">
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="birthday">出生年月：</label>
				<div class="controls">
					<input type="text" name="birthday" id="birthday" class="form-control" >
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="idCard">身份证号：</label>
				<div class="controls">
					<input type="text" name="idCard" id="idCard" class="form-control">
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label" for="address">家庭地址：</label>
				<div class="controls">
					<input type="text" name="address" id="address" class="form-control" >
				</div>
			</div>
			
		
			<input type="submit" class="btn btn-lg btn-primary btn-block" id="registBtn" value="注册"><a href="${ctx }/login/toLogin.htm">登录</a>
		</form>
	</div>
</body>
</html>