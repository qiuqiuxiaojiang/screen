<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
<title>个人健康档案</title>
    <script src="${ctx_jquery }/jquery.form.js"></script>
    <script src="${ctx_jquery }/pagination/jquery.twbsPagination.min.js"></script>
    <script src="${ctx_js }/encode.js"></script>
    
<style type="text/css">  
.indent{text-indent: 2em;}   
.content {  
 width:100%;/*自动适应父布局宽度*/  
 overflow:auto;  
 word-break:break-all;  
}
</style>
    <script  type="text/javascript">
    $(document).ready(function(){
    	var encodedKeywords = urlencode("${keywords}");
    	$('.pagination').twbsPagination({
            totalPages: ${page.totalPages},
            visiblePages: 7,
            last:'Last(${page.totalPages})',
            href: '?keywords='+encodedKeywords+'&page={{number}}'
        });
    	$('#optionTip').tooltip({html : true });
    	$("[data-toggle='popover']").popover({html : true });
    });
    </script>
</head>
<body>
<!-- --------------- -->
<div class="container-fluid">
                    <!-- 以下这是页面需要编辑的区域 -->
                	<div class="row">
                		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 main">
                		<form >
						Search...
                			<input type="text" name="keywords" placeholder="特征关键字" value="${keywords }"/>
                			  <button type="submit" title="Search"><i class="glyphicon glyphicon-search"></i></button>
                		</form>
	                		</div>
                	</div>
                	<div class="row">
                		<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12 main'>
                			<ul class="pagination"></ul>
                			<table class="table table-bordered">
                				<thead>
                					<tr>
                						<th>特征关键字</th>
                						<th>部位</th>
                						<th>颜色</th>
                						<th>特征</th>
                						<th>分类</th>
                 						<th nowrap>详细信息</th>
                					</tr>
                				</thead>
	                		<c:forEach var="i" items="${contents }">
	                		<tr>
	                			<td nowrap>${i.match }</td>
	                			<td nowrap>${i.part }</td>
	                			<td nowrap>${i.color }</td>
	                			<td nowrap>${i.feature }</td>
	                			<td nowrap>${i.category }</td>
	                			<td>
	                			<c:if test="${not empty i.moreDesc }">
	                			<button type="button" class="btn btn-default" title="详细信息"  
      							data-container="body" data-toggle="popover" data-placement="left" 
      							data-content="${i.moreDesc }">
      							详细信息
   								</button>
   								</c:if>
	                			
	                			</td>
	                		</tr>
	                		</c:forEach>
                			</table>
                			<ul class="pagination"></ul>
						</div>
					</div>
                    <!-- 以上这是页面需要编辑的区域 -->
</div>
</body>
</html>
