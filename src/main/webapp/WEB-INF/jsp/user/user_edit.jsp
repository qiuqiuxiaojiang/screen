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
    
	<script type="text/javascript">
	$(document).ready(function(){
		$("#saveBtns").click(function(){
			var ids="";
			$("input[type=checkbox]").each(function(){
			    if(this.checked){
			    	ids+=$(this).val()+",";
			    }
			});
			if(ids.length>0){
				ids = ids.substring(0, ids.length-1);
			}
			var options = {
				data:{
					roles:ids
				},
			    url:"${ctx}/user/edit.json",
				success:function(o){
					if(o.message="success"){
						alert("保存成功!");
						$("#userid").val(o.data);
						window.location.href="${ctx}/user/userlist.htm";
					}else{
						alert("保存失败！");
					}
				}
			};
			$("#userInfos").ajaxForm(options);
		})
		
		  $('#banklist').click(function(){
        	  window.location.href="${ctx }/user/userlist.htm";
          })
          
	});
	
	$(function(){
		var roles = $("#roleids").val();
		$("input[type='checkbox']").each(function(index,Ele){
			var val = ","+$(this).val() + ",";
			if(roles.indexOf(val) > -1){
				$(this).attr("checked",true);
			}
		});
	});
	
	 $().ready(function() {  
 		$("#userInfos").validate({  
 	       	rules: {  
    	   		telephone:{
    	   			isMobile:true
    	   		},
    	   		idCard:{
    	   			isIdCard:true
    	   		}
 	  		},  
 	        /* messages: {  
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
 	  		}, */
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
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-heading">修改用户</div>
                                 <div class="panel-body">
                                 	 <form id="userInfos" role="form" method="post">
										<input type="hidden" name="userid" value="${user.id}" id="userid">
										<div class="form-group">
											<label for="username" class="col-sm-2 control-lavel">用户名</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="username" value="${user.username}" readonly="readonly">
											</div>
										</div>
										
										<input type="hidden" id="roleids" value="${user.roles}" />
										<div class="form-group">
											<label for="rolename" class="col-sm-2 control-lavel">角色名称</label>
											<div class="col-sm-10">
												<c:forEach var="role" items="${roles}">
												<label><input type="checkbox" value="${role.id}" 
												<c:if test="${fn:contains(user.roles, role.id)}">checked</c:if>
												>${role.rolename}</label>
												</c:forEach>
												
											</div>
										</div>
										<div style="text-align: center;">
											<input class="btn btn-primary btn-lg" type="submit" id="saveBtns" value="提交">
										</div>
									</form>
                                 </div>
                             </div>
                             <button id="banklist" class="btn btn-default">返回列表</button>
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>

</body>
</html>