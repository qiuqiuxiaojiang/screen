<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>
<html>
<head>
<title>用户列表</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
	<style type="text/css">
		#roleModal{
			margin-top: 5%
		}
	</style>
	
</head> 
<body>
	<div class="container-fluid">
                <!-- 以下这是页面需要编辑的区域 -->
                	 <div class="row" >
                          <div >
                             <div class="panel panel-primary">
                                 <div class="panel-heading">角色列表</div>
                                  <div class="panel-body">
			                		<table  class="table table-bordered">
						      			<thead>
						      				<tr>
						      					  <th><input name="checkall" type="checkbox" />全选</th>
						      					  <th>角色名</th>
									              <th>角色</th>
									              <th>添加日期</th>
									              <th>修改日期</th>
									              <th>操作</th>
						      				</tr>
						      			</thead>
						      			<tbody>
						      				<c:forEach var="role" items="${roles }" varStatus="index">
										          <tr>
										          <td><input name="checkbox" type="checkbox" value="${role.id}"></td>
										          <td>${role.rolename }</td>
										          <td>${role.rolekey }</td>
										          <td><fmt:formatDate value="${role.ctime }" pattern="yyyy-MM--dd HH:mm:ss"/></td>
										          <td><fmt:formatDate value="${role.utime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
										          <td>
										           <a href="${ctx }/role/updateRole/${role.id}.htm">编辑</a> 
										            <a href="javascript:delRole('${role.id }');">删除</a>
										          </td>
										          </tr>
									         </c:forEach>
									         <!-- <tr>
							      				 <td colspan="4"></td>
							      				 <td><a class="deldicpl" id="deleteRoles">批量删除</a></td>
							      			 </tr> -->
						      			</tbody>
				      				</table>
				      			</div>
				      		</div>
	      				</div>
                	</div>
                	<div class="panel-footer"style="text-align: center;background:none;border:none;"><button class="btn btn-primary" data-toggle="modal" data-target="#roleModal">添加角色</button></div>
                <!-- 以下这是页面需要编辑的区域 -->
   </div>

		<!-- 模态框（Modal） -->
		<div class="modal fade" id="roleModal" tabindex="-1" role="dialog" 
		   aria-labelledby="myModalLabel" aria-hidden="true">
		   <div class="modal-dialog">
		      <div class="modal-content">
		         <div class="modal-header">
		            <button type="button" class="close" 
		               data-dismiss="modal" aria-hidden="true">
		                  &times;
		            </button>
		            <h4 class="modal-title" id="myModalLabel">
		            	添加角色
		            </h4>
		         </div>
		         <div class="modal-body">
		            <form id="roleInfo" role="form" method="post">
						<div class="modal-body row">
							<label for="rolename" class="col-sm-2 control-lavel">角色名</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" name="rolename" value="${role.rolename }">
							</div>
						</div>
						
						<div class="modal-body row">
							<label for="rolekey" class="col-sm-2 control-lavel">角色KEY</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" name="rolekey" value="${role.rolekey }">
							</div>
						</div>
						
						 <div class="modal-footer">
				             <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				             <input class="btn btn-primary" type="submit" id="saveRole" value="保存">
				         </div>
				</form>
		         </div>
		        
		      </div><!-- /.modal-content -->
		</div><!-- /.modal -->
		</div>
   
   
      <script type="text/javascript">
      	  
	      $(document).ready(function(){
			/* 	$("#addrole").click(function(){
					window.location.href="${ctx}/role/addrole.htm";
				}) */
				
				/* $("#deleteRoles").click(function(){
					var ids = "";
					var count = 0;
					$(":checkbox[checked]").each(function(){
						alert(ids)
						ids = ids +$(this).val()+",";
						count++;
					})
					if(count == 0){
						alert("请选择您要删除的对象");
						return;
					}
					$.ajax({
						type:'post',
						data:{
							ids:ids
						},
						async:false,
						url:'${ctx}/role/deleteRole.htm?ids='+ids,
						success:function(o){
							if(o.message='success'){
								alert("删除成功!");
	                    	location.reload();
							}else{
								alert("删除失败！");
							}
						}
					});
				}); */
				
				$("#saveRole").click(function(){
					 var options = {
						url:"${ctx}/role/save.json",
						success:function(o){
							if(o.message="success"){
								alert("保存成功");
								window.location.href="${ctx}/role/rolelist.htm";
							}else{
								alert("保存失败！");
							}
						}
					};
					$("#roleInfo").ajaxForm(options); 
				});
				
			})
      
	      function delRole(roleid){
				var c = confirm("确定要删除吗？");
				if(c){
					$.ajax({
						type:'post',
						url:'${ctx}/role/deleteRole/'+roleid+'.json',
						success:function(o){
							if(o.message='success'){
								alert("删除成功!");
								window.location.href="${ctx}/role/rolelist.htm";
	                      	location.reload();
							}else{
								alert("删除失败！");
							}
						}
					});
				}
			}
			
      </script>
      
      
</body>
</html>