<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@ include file="/_header.jsp"%>
<head>
<title>修改用户</title>
	<link rel="stylesheet" href="${ctx_bootstrap }/css/bootstrap.css">
    <script src="${ctx_jquery }/jquery-1.11.2.js"></script>
    <script src="${ctx_jquery }/jquery.form.js"></script>
    <script src="${ctx_bootstrap }/js/bootstrap.js"></script> 
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
	    <!-- 头部导航自动加载 -->
	    <header id="header"></header>
	 <!-- 内容区域 -->
    <section>
        <div class="row">
            <!-- 左侧菜单自动加载 -->
            <div id="leftpanelside"></div>
            <div id="mainpanelside">
                <!-- 面包屑导航 -->
                <div class="trackerbar">Home >> role info</div>
                <div id="maincontent" class="col-lg-10 col-md-10 col-sm-9 col-xs-12">
               	 <!-- 以下这是页面需要编辑的区域 -->
                     <div class="row">
                         <div >
                             <div class="panel panel-warning">
                                 <div class="panel-heading">添加角色</div>
                                 <div class="panel-body">
                                 	 <form id="roleInfo" role="form" method="get">
										<div class="form-group">
											<label for="username" class="col-sm-2 control-lavel">用户名</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="username" value="${user.username }">
											</div>
										</div>
										<div class="form-group">
											<label for="rolename" class="col-sm-2 control-lavel">角色名称</label>
											<div class="col-sm-10">
												<select class="form-control">
												<option>--请选择--</option>
												<c:forEach var="role" items="${roles}">
													<option value="${role.rolekey}">${role.rolename}</option>
												</c:forEach>
											</select>
											</div>
										</div>
										
										
										<input class="btn btn-default" type="submit" id="saveBtn" value="提交">
									</form>
                                 </div>
                             </div>
                             <button id="banklist" class="btn btn-primary btn-lg">返回列表</button>
                         </div>
                     </div>
                   <!-- 以上这是页面需要编辑的区域 -->
                
              
                </div>
            </div>
		</div>
    </section>
   </div>

</body>
</html>