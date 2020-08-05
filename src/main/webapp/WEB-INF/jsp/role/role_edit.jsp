<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>修改用户</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
    
	<script type="text/javascript">
	 $(document).ready(function(){
		$("#saveBtn").click(function(){
			var options = {
			    url:"${ctx}/role/save.json",
				success:function(o){
				
					if(o.message="success"){
						alert("保存成功!");
						$("#roleid").val(o.data);
						window.location.href="${ctx}/role/rolelist.htm";
					}else{
						alert("保存失败！");
					}
				}
			};
			$("#roleInfo").ajaxForm(options);
		})
		
		  $('#banklist').click(function(){
        	  window.location.href="${ctx }/role/rolelist.htm";
          })
          
	}); 
	
	
	</script>
</head>
<body>
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-heading">
                                  <button id="banklist" class="btn btn-default">返回</button>
                                 </div>
                                 <div class="panel-body">
                                 	 <form id="roleInfo" role="form" method="post">
										<input type="hidden" name="roleid" value="${role.id}" id="roleid">
										<div class="form-group">
											<label for="rolename" class="col-sm-2 control-lavel">角色名</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="rolename" value="${role.rolename }">
											</div>
										</div>
										
										<div class="form-group">
											<label for="rolekey" class="col-sm-2 control-lavel">角色KEY</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="rolekey" value="${role.rolekey }">
											</div>
										</div>
										<div>
											<input class="btn btn-primary" type="submit" id="saveBtn" value="提交">
										</div>
									</form>
                                 </div>
                             </div>
                            
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
   </div>
</body>
</html>