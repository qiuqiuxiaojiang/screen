<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>编辑菜单</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
    <script src="${ctx_jquery }/jquery.validate.js"></script>
    
     <style type="text/css">
	    label.error{
	    	color:Red; 
			font-size:13px; 
	    }
    </style>
    
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
			    url:"${ctx}/menu/saveMenu.json",
			    data:{
			    	roles:ids
			    },
				success:function(o){
				
					if(o.code=1){
						alert("保存成功!");
						$("#urlresourceid").val(o.data);
						window.location.href="${ctx}/menu/menulist.htm";
					}else{
						alert(o.message);
					}
				}
			};
			$("#urlresourceInfo").ajaxForm(options);
		})
		
		  $('#banklist').click(function(){
        	  window.location.href="${ctx }/menu/menulist.htm";
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
                                 	 	<c:if test="${menu.id != null && menu.id != ''}">
											<input type="hidden" name="menuid" value="${menu.id}" id="menuid">
										</c:if>
										
										<div class="form-group">
											<label for="name" class="col-sm-2 control-lavel">功能名称</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="name" value="${menu.name}">
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-2 control-lavel">URL</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="url" value="${menu.url}">
											</div>
										</div>
										
										<input type="hidden" name="menu" id="menu" value="${menu.isfristmenu}" />
										<div class="form-group">
											<label for="isfristmenu" class="col-sm-2 control-lavel">是否一级菜单</label>
											<div class="col-sm-10">
												 <label>
												      <input type="radio" name="isfristmenu" id="isfristmenu0" value="0" checked> 是
												 </label>
												 
												 <label>
												      <input type="radio" name="isfristmenu" id="isfristmenu1" value="1" checked> 否
												 </label>
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-2 control-lavel">所属一级菜单</label>
											<div class="col-sm-10 controls">
												<select class="form-control" name="pid" id="menuFrist">
												<option value="">--请选择--</option>
												<c:forEach var="url" items="${menus}">
												<option <c:if test="${(url.id==menu.fristId)}">selected="selected"</c:if> value="${url.id }">${url.name } </option>
												</c:forEach>
												</select>
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-2 control-lavel">所属二级菜单</label>
											<div class="col-sm-10 controls">
												<select class="form-control" name="menuSecond" id="menuSecond">
												<option value="">--请选择--</option>
												<c:forEach var="subMenu" items="${thrid}">
												<option <c:if test="${(subMenu.id==menu.secondId)}">selected="selected"</c:if> value="${subMenu.id }">${subMenu.name } </option>
												</c:forEach>
												</select>
											</div>
										</div>
										
										<div class="form-group">
											<label for="url" class="col-sm-2 control-lavel">序号</label>
											<div class="col-sm-10">
												<input type="text" class="form-control" name="seq" value="${menu.seq}">
											</div>
										</div>
										<input type="hidden" id="roleids" value="${menu.roles}" />
										<div class="form-group">
											<label for="rolename" class="col-sm-2 control-lavel">角色名称</label>
											<div class="col-sm-10">
												<c:forEach var="role" items="${roles}">
												<label><input type="checkbox" value="${role.id}">${role.rolename}</label>
												</c:forEach>
											</div>
										</div>
										
										<div class="form-group">
											<label for="icon" class="col-sm-2 control-lavel">图标</label>
											<div class="col-sm-10">
												<!-- <label><input type="checkbox" class="glyphicon glyphicon-asterisk"></label>
												<label><input type="checkbox" class="glyphicon glyphicon-plus"></label>
												<span class="glyphicon glyphicon-asterisk"></span>
												<span class="glyphicon glyphicon-plus"></span> -->
												<input type="text" class="form-control" name="icon" value="${menu.icon}">
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
 
   <script type="text/javascript">
	   //alert(roles);
	   
	   
	   $().ready(function() {  
		   var item = $("input[name='isfristmenu']").val();
		   var menu = $("#menu").val();
		   var fristmenus = document.getElementsByName("isfristmenu");
		   for(var i=0;i<fristmenus.length;i++){
		 	  if(fristmenus[i].value==menu){
		 		  fristmenus[i].checked=true;
		 	  }
		   }
		   
		   var roles = $("#rolekey").val();
		   
 		$("#urlresourceInfo").validate({  
 	       	rules: {  
 	       		name:{
 	       			required:true
    	   		},
    	   		seq:{
    	   			required:true,
    	   			digits:true
    	   		}
 	  		},  
 	        messages: {  
 	        	name: {
 	        		required:"*"
 	        	},
 	        	seq: {
 	        		required:"*",
 	        		digits:"请输入整数"
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
 		
 		$("#menuFrist").change(function() {
			 empty();
			 var pid = $(this).val();
			// alert(pid);
			 $.ajax({
	             type:"post",
	             url:'${ctx}/menu/getSecondMenu.json',
	             data:{
	            	 pid:pid
	             },
	             success:function(o){
	            	 var datas = o.dataList;
	            	 console.log(datas);
	                 for (var i=0; i<datas.length; i++) {
	                	 var id = datas[i].id;
	                	 var name = datas[i].name;
	                	 $("#menuSecond").append("<option value='"+id+"'>"+name+"</option>");
	                 } 
	             }
	         });
		}); 
 		
 		
 		function empty(){
 			$("#menuSecond").empty();
 			$("#menuSecond").append("<option value=''>--请选择--</option>");
 		}
 		
 	}); 
	   
   </script>
</body>
</html>