<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>
<html>
<head>
<title>菜单列表</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
	
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
    <script type="text/javascript">
    	$(document).ready(function(){
    		$("#addmenu").click(function(){
    			window.location.href="${ctx}/menu/addMenu.htm";
    		})
    	})
    
        function delUrlResourcer(id,isfristmenu){
        	var c = confirm("确定要删除吗？");
        	if(c){
        		 $.ajax({
                     type:"POST",
                     data:{
                    	 isFristMenu: isfristmenu
                     },
                     url:'${ctx}/menu/delMenu/'+id+'.json',
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
    	
    	function tag(i){
    		var targets = $("[name='con_two_"+i+"']");
    		for(var j=0;j<targets.length;j++){
    			var target = targets[j];
    			if(target.style.display == "none"){
        			target.style.display = "";
        		}else{
        			target.style.display = "none";
        		}
    		}
    	}
    	
    	function tagThree(i){
    		var targets = $("[name='con_three_"+i+"']");
    		for(var j=0;j<targets.length;j++){
    			var target = targets[j];
    			if(target.style.display == "none"){
        			target.style.display = "";
        		}else{
        			target.style.display = "none";
        		}
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
                                        <div class="panel-heading">菜单列表</div>
                                        <div class="panel-body">
                                        	<c:if test="${not empty menus}">
	                                        	<table class="table table-bordered">
									      			<thead>
									      				<tr>
									      					  <th>功能名称</th>
												              <th>url</th>
												              <th>角色</th>
												              <th>序号</th>
												              <th>添加日期</th>
												              <th>修改日期</th>
												              <th>操作</th>
									      				</tr>
									      			</thead>
									      			<tbody>
									      			<% int index=1; %>
									      				<c:forEach var="menu" items="${menus }" varStatus="index">
													          <tr>
													          	  <input type="hidden" value="<%=index++ %>">
														          <td>${menu.name }</td>
														          <td>${menu.url }</td>
														          <td>
														          <c:forEach var="role" items="${menu.roles }">
														          ${role.rolename}
														          </c:forEach>
														         
														          </td>
														          <td>${menu.seq }</td>
														          <td><fmt:formatDate value="${menu.ctime}" pattern ="yyyy-MM-dd HH:mm:ss"/> </td>
														          <td><fmt:formatDate value="${menu.utime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
														          <td>
														            <a href="javascript:void(0);" onclick="tag(<%=index%>);">查看子模块</a> 
														           	<a href="${ctx }/menu/updateMenu/${menu.id}.htm">编辑</a> 
														            <a href="javascript:delUrlResourcer('${menu.id }',${menu.isfristmenu });">删除</a>
														          </td>
													          </tr>
													          <c:forEach var="childmenu" items="${menu.childMenus }" varStatus="index">
														          <tr name="con_two_<%=index%>" style="display:none" class="active">
															          <td>${childmenu.name }</td>
															          <td>${childmenu.url }</td>
															          <td>
															          <c:forEach var="childrole" items="${childmenu.roles }">
															          ${childrole.rolename}
															          </c:forEach>
															          </td>
															          <td>${childrole.seq }</td>
															          <td><fmt:formatDate value="${childmenu.ctime}" pattern ="yyyy-MM-dd HH:mm:ss"/></td>
															          <td><fmt:formatDate value="${childmenu.ctime}" pattern ="yyyy-MM-dd HH:mm:ss"/></td>
															          <td>
															          	<a href="javascript:void(0);" onclick="tagThree('${childmenu.id}');">查看子模块</a> 
															          	<a href="${ctx }/menu/updateMenu/${childmenu.id}.htm">编辑</a> 
															            <a href="javascript:delUrlResourcer('${childmenu.id }',${childmenu.isfristmenu });">删除</a>
															          </td>
														          </tr>
														          <c:forEach var="threemenu" items="${childmenu.threeMenus}" varStatus="index">
														           <tr name="con_three_${childmenu.id}" style="display:none;background-color:#dbe5e6;">
															          <td>${threemenu.name }</td>
															          <td>${threemenu.url }</td>
															          <td>
															          <c:forEach var="childrole" items="${childmenu.roles }">
															          ${childrole.rolename}
															          </c:forEach>
															          </td>
															          <td>${childrole.seq }</td>
															          <td><fmt:formatDate value="${threemenu.ctime}" pattern ="yyyy-MM-dd HH:mm:ss"/></td>
															          <td><fmt:formatDate value="${threemenu.ctime}" pattern ="yyyy-MM-dd HH:mm:ss"/></td>
															          <td>
															          	<a href="${ctx }/menu/updateMenu/${threemenu.id}.htm">编辑</a> 
															            <a href="javascript:delUrlResourcer('${threemenu.id }',${threemenu.isfristmenu });">删除</a>
															          </td>
														          </tr>
													          </c:forEach>
													          </c:forEach>
													           
												         </c:forEach>
									      			</tbody>
									      		</table>
								      		</c:if>
								      		<c:if test="${empty menus}">
								      			<h4>暂无菜单！</h4>
								      		</c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="panel-footer"style="text-align: center;background:none;border:none;"><button class="btn btn-primary btn-lg" id="addmenu">添加菜单</button></div>
                    <!-- 以上这是页面需要编辑的区域 -->
      
   </div>
      
      
      
</body>
</html>