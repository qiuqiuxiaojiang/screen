<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>
<html>
<head>
<title>筛查地点列表</title>
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
                                 <div class="panel-heading">筛查地点列表</div>
                                  <div class="panel-body">
			                		<table  class="table table-bordered">
						      			<thead>
						      				<tr>
						      					  <th><input name="checkall" type="checkbox" />全选</th>
						      					 <!--  <th>县</th> -->
									              <th>区/县</th>
									              <th>添加日期</th>
									              <th>修改日期</th>
									              <th>操作</th>
						      				</tr>
						      			</thead>
						      			<tbody>
						      				<c:forEach var="checkplace" items="${checkplaces }" varStatus="index">
										          <tr>
										          <td><input name="checkbox" type="checkbox" value="${checkplace.id}"></td>
										          <%-- <td>${checkplace.county }</td> --%>
										          <td>${checkplace.district }</td>
										          <td><fmt:formatDate value="${checkplace.ctime }" pattern="yyyy-MM--dd HH:mm:ss"/></td>
										          <td><fmt:formatDate value="${checkplace.utime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
										          <td>
										           <a href="${ctx }/checkPlace/editCheckPlace.htm?id=${checkplace.id}">编辑</a> 
										            <a href="javascript:delCheckPlace('${checkplace.id }');">删除</a>
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
                	<div class="panel-footer"style="text-align: center;background:none;border:none;"><button class="btn btn-primary" data-toggle="modal" data-target="#roleModal">添加筛查地点</button></div>
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
		            	添加筛查地点
		            </h4>
		         </div>
		         <div class="modal-body">
		            <form id="checkPlaceInfo" role="form" method="post">
						<input type="hidden" name="checkplaceid" value="${checkplace.id }" id="checkplaceid">
						<div class="modal-body row">
							<label for="rolekey" class="col-sm-2 control-lavel">区/县：</label>
							<div class="col-sm-10">
								<input type="text" class="form-control" name="district" value="${checkplace.district }">
							</div>
						</div>
						
						 <div class="modal-footer">
				             <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				             <input class="btn btn-primary" type="submit" id="saveCheckplace" value="保存">
				         </div>
					</form>
		        </div>
		        
		      </div><!-- /.modal-content -->
		</div><!-- /.modal -->
		</div>
   
   
      <script type="text/javascript">
      	  
	      $(document).ready(function(){
				$("#saveCheckplace").click(function(){
					 var options = {
						url:"${ctx}/checkPlace/saveCheckPlace.json",
						success:function(o){
							if(o.message="success"){
								alert("保存成功");
								window.location.href="${ctx}/checkPlace/list.htm";
							}else{
								alert("保存失败！");
							}
						}
					};
					$("#checkPlaceInfo").ajaxForm(options); 
				});
				
			})
      
	      function delCheckPlace(id){
				var c = confirm("确定要删除吗？");
				if(c){
					$.ajax({
						type:'post',
						url:'${ctx}/checkPlace/delCheckPlace/'+id+'.json',
						success:function(o){
							if(o.message='success'){
								alert("删除成功!");
								window.location.href="${ctx}/checkPlace/list.htm";
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