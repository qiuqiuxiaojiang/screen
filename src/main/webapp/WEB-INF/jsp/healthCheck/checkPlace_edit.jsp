<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<%@include file="../main.jsp" %>
<head>
<title>修改筛查地点</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <script src="${ctx_jquery }/jquery.form.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
    
	<script type="text/javascript">
	 $(document).ready(function(){
		$("#saveBtn").click(function(){
			var options = {
			    url:"${ctx}/checkPlace/saveCheckPlace.json",
				success:function(o){
				
					if(o.message="success"){
						alert("保存成功!");
						$("#checkplaceid").val(o.data);
						window.location.href="${ctx}/checkPlace/list.htm";
					}else{
						alert("保存失败！");
					}
				}
			};
			$("#checkPlaceInfo").ajaxForm(options);
		})
		
		  $('#banklist').click(function(){
			  window.location.href="${ctx}/checkPlace/list.htm";
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
                       <div class="panel-heading">编辑筛查地点</div>
                       <div class="panel-body">
                       	 <form id="checkPlaceInfo" role="form" method="post">
                       	 	<input type="hidden" name="checkplaceid" value="${checkplace.id }" id="checkplaceid">
							<div class="form-group">
								<label class="col-sm-2 control-lavel">区/县:</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" name="district" value="${checkplace.district}">
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