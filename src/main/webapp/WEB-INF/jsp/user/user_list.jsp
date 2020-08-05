<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>

<html>
<head>
<title>用户列表</title>
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
        function delUser(id){
        	var c = confirm("确定要删除改用户吗？");
        	if(c){
        		 $.ajax({
                     type:"POST",
                     url:'${ctx}/user/delete/'+id+'.json',
                     success:function(o){
                         if(o.code==0){
                        	 alert("删除成功!");
                        	 location.reload();
                         }else{
                        	 alert("删除失败！");
                         }
                    	 
                     }
                 });
        	}
        }
        
        $().ready(function() {
        	$("#user1").validate({  
    	       	rules: {  
    	       		username: {
    	       			required:true,
    	       			alnum1:true
    	       			/* ,
    	       			remote:"${ctx}/register/checkUser.json" */
    	       		},   
	    	   		password: {  
	    	   			required: true,  
	    	  			minlength: 8,
	    	  			alnum:true
	    	   		},
	    	   		confirmPassword: {  
	    	    		required: true,  
			    	    equalTo: "#password"  
	    	   		}
    	  		},  
    	        messages: {  
    	        	username: {
    	        		required:"请输入用户名"/* ,
    	        		remote: "该用户已存在！" */
    	        	}, 
    	   			password: {  
			    	    required: "请输入密码",  
			    	    minlength: "密码不能小于8个字符",  
    	   			},  
    	   			confirmPassword: {  
			    	    required: "请输入确认密码",  
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
        	
        	$("#addUser").click(function(){
        		//debugger;
				/* var username=$("#username").val();
				if (username=='') {
					alert("请输入用户名");
					return false;
				}
				var password=$("#password").val();
				var confirmPassword=$("#confirmPassword").val();
				if (password!=confirmPassword) {
					alert("两次密码输入不一致！");
					return false;
				} */
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
				    url:"${ctx}/user/save.json",
					success:function(o){
						if(o.code==0){
							alert("保存成功!");
							window.location.href="${ctx}/user/userlist.htm";
						}else{
							alert(o.msg);
						}
					}
				};
				$("#user1").ajaxForm(options); 
			})
        	
    	}); 
    </script> 
    <style type="text/css">
		#adduserModal{
			margin-top: 5%
		}
	</style>
   
</head>
<body>
	<div class="container-fluid">
                 <!-- 以下这是页面需要编辑的区域 -->
       <div class="row">
           <div >
               <div class="panel panel-primary">
                   <div class="panel-heading">用户列表</div>
                   <div class="panel-body">
                   	<c:if test="${not empty users}">
                    	<table class="table table-bordered">
			    			<thead>
			    				<tr>
			    					  <th>用户名</th>
						              <th>角色</th>
						              <th>添加日期</th>
						              <th>修改日期</th>
						              <th>操作</th>
			    				</tr>
			    			</thead>
			    			<tbody >
			    				<c:forEach var="user" items="${users }" varStatus="index">
							          <tr>
								          <td><c:if test="${user.username!=null && user.username!='' }">${user.username}</c:if></td>
								          <td>
								          	<c:forEach var="role" items="${user.roles}" varStatus="state">
								          		${role.rolename}
								          	</c:forEach>
								          </td>
								          <td><fmt:formatDate value="${user.ctime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								          <td><fmt:formatDate value="${user.utime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								          <td>
								          <a href="${ctx}/user/update/${user.id}.htm">编辑</a> 
								          <a href="javascript:delUser('${user.id }');">删除</a>
								            
								          </td>
							          </tr>
						         </c:forEach>
			    			</tbody>
    					</table>
   					</c:if>
			   		<c:if test="${empty users}">
			   			<h4>暂无用户！</h4>
			   		</c:if>
      			 </div>
           </div>
       </div>
   </div>
   <div class="panel-footer"style="text-align: center;background:none;border:none;"><button class="btn btn-primary" data-toggle="modal" data-target="#adduserModal">添加用户</button></div>
   <!-- 以上这是页面需要编辑的区域 -->
   </div>
     
   <!-- 模态框（Modal） -->
		<div class="modal fade" id="adduserModal" tabindex="-1" role="dialog" 
		   aria-labelledby="myModalLabel" aria-hidden="true">
		   <div class="modal-dialog">
		      <div class="modal-content">
		         <div class="modal-header">
		            <button type="button" class="close" 
		               data-dismiss="modal" aria-hidden="true">
		                  &times;
		            </button>
		            <h4 class="modal-title" id="myModalLabel">
		            	添加用户
		            </h4>
		         </div>
		         <div class="modal-body">
		            <form id="user1" role="form" method="post">
						<div class="modal-body row">
							<label class="col-sm-2 control-lavel">用户名</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" id="username" name="username" placeholder="请输入用户名，用户名只能为字母、数字、下划线，且不得少于六位数">
							</div>
						</div>
						
						<div class="modal-body row">
							<label class="col-sm-2 control-lavel">密码</label>
							<div class="col-sm-10">
								<input type="password" class="form-control" id="password" name="password" placeholder="请输入密码，需包含大写字母、小写字母和数字" title="请输入大写字母、小写字母和数字">
							</div>
						</div>

						<div class="modal-body row">
							<label class="col-sm-2 control-lavel">确认密码</label>
							<div class="col-sm-10">
								<input type="password" class="form-control" id="confirmPassword" name="confirmPassword">
							</div>
						</div>
						
						<div class="modal-body row">
							<label class="col-sm-2 control-lavel">角色</label>
							<div class="col-sm-10">
								<c:forEach var="role" items="${roles}">
								<label><input type="checkbox" value="${role.id}">${role.rolename}</label>
								</c:forEach>
							</div>
						</div>
						
						 <div class="modal-footer">
				             <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				             <input class="btn btn-primary" type="submit" id="addUser" value="保存">
				         </div>
				</form>
		         </div>
		        
		      </div><!-- /.modal-content -->
		</div><!-- /.modal -->
		</div>
		
		
</body>
</html>