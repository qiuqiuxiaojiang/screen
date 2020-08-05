<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>
<html>
<head>
<title>用户列表</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
    <script type="text/javascript">
    	$(document).ready(function(){
    		$("#addurlresource").click(function(){
    			window.location.href="${ctx}/urlresource/addUrlResource.htm";
    		})
    	})
    
        function delUrlResourcer(id){
        	var c = confirm("确定要删除改用户吗？");
        	if(c){
        		 $.ajax({
                     type:"POST",
                     url:'${ctx}/urlresource/delUrlResource/'+id+'.json',
                     success:function(o){
                         if(o.message='success'){
                        	 alert("删除成功!");
                        	 location.reload();
                         }else{
                        	 alert("删除失败！");
                         }
                    	 
                     }
                 });

        	}
        }
    </script> 
</head>
<body>
	<div class="container-fluid">
	 
                 <!-- 以下这是页面需要编辑的区域 -->
                            <div class="row">
                                <div >
                                    <div class="panel panel-primary">
                                        <div class="panel-heading">资源列表</div>
                                        <div class="panel-body">
                                        	<c:if test="${not empty urlresources}">
	                                        	<table class="table table-striped">
									      			<thead>
									      				<tr>
									      					  <th>功能名称</th>
												              <th>url</th>
												              <th>角色</th>
												              <th>添加日期</th>
												              <th>修改日期</th>
												              <th>操作</th>
									      				</tr>
									      			</thead>
									      			<tbody>
									      				<c:forEach var="urlresource" items="${urlresources }" varStatus="index">
													          <tr>
													          <td>${urlresource.name }</td>
													          <td>${urlresource.url }</td>
													          <td>
													          <c:forEach var="role" items="${urlresource.roles }">
													          ${role.rolename}
													          </c:forEach>
													         
													          </td>
													          <td><fmt:formatDate value="${urlresource.ctime}" pattern ="yyyy-MM-dd HH:mm:ss"/> </td>
													          <td><fmt:formatDate value="${urlresource.utime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													          <td>
													           	<a href="${ctx }/urlresource/updateUrlResource/${urlresource.id}.htm">编辑</a> 
													            <a href="javascript:delUrlResourcer('${urlresource.id }');">删除</a>
													          </td>
													          </tr>
													         <%--  
													          <c:forEach var="submodule" items="${submodules}">
													          <tr style="font-size: 10px;">
													          
													          	  <td>${submodule.name }</td>
														          <td>${submodule.url }</td>
														          <td>
														          <c:forEach var="role" items="${submodule.roles }">
														          ${role.rolekey}
														          </c:forEach>
													         
													          	</td>
													          	</tr>
													          </c:forEach> --%>
												         </c:forEach>
									      				<!-- <tr>
									      					<td colspan="7"></td>
									      					<td><a class="deldicpl">批量删除</a></td>
									      				</tr> -->
									      			</tbody>
									      		</table>
								      		</c:if>
								      		<c:if test="${empty urlresources}">
								      			<h4>暂无资源！</h4>
								      		</c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-footer"style="text-align: center;background:none;border:none;"><button class="btn btn-primary btn-lg" id="addurlresource">添加资源</button></div>
                    <!-- 以上这是页面需要编辑的区域 -->
      
   </div>
      
      
      
</body>
</html>