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
			    url:"${ctx}/urlresource/saveUrlResource.json",
			    data:{
			    	roles:ids
			    },
				success:function(o){
				
					if(o.message="success"){
						alert("保存成功!");
						$("#urlresourceid").val(o.data);
						window.location.href="${ctx}/urlresource/resourcelist.htm";
					}else{
						alert("保存失败！");
					}
				}
			};
			$("#urlresourceInfo").ajaxForm(options);
		})
		
		  $('#banklist').click(function(){
        	  window.location.href="${ctx }/urlresource/resourcelist.htm";
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

	</script>
</head>
<body>
	<div class="container-fluid">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-heading">添加资源</div>
                                 <div class="panel-body">
                                 	 <form id="urlresourceInfo" role="form" method="get">
                                 	 	<c:if test="${urlResource.id != null && urlResource.id != ''}">
											<input type="hidden" name="urlresourceid" value="${urlResource.id}" id="urlresourceid">
										</c:if>
										<div class="form-group">
											<label for="name" class="col-sm-2 control-lavel">功能名称</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="name" value="${urlResource.name}">
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-2 control-lavel">URL</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="url" value="${urlResource.url}">
											</div>
										</div>
										
										
									
										<input type="hidden" id="roleids" value="${urlResource.roles}" />
										<div class="form-group">
											<label for="rolename" class="col-sm-2 control-lavel">角色名称</label>
											<div class="col-sm-10 checkbox">
												<c:forEach var="role" items="${roles}">
												<label><input type="checkbox" value="${role.id}">${role.rolename}</label>
												</c:forEach>
											</div>
										</div>
										
									
										<input class="btn btn-primary btn-lg" type="submit" id="saveBtn" value="提交">
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