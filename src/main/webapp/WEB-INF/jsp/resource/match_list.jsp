<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="../main.jsp" %>
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
    	$('#optionTip').tooltip({html : true });
    	var encodedKeywords = urlencode("${keywords}");
    	var encodedDisease = urlencode("${disease}");
    	$('.pagination').twbsPagination({
            totalPages: ${page.totalPages},
            visiblePages: 7,
            last:'Last(${page.totalPages})',
            href: '?keywords='+encodedKeywords+'&disease='+encodedDisease+'&queryOption=${queryOption}&page={{number}}'
        });
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
						眼象特征：
                			<input type="text" name="keywords" placeholder="眼象特征关键字" value="${keywords }"/>
						病证：
                			<input type="text" name="disease" placeholder="病证关键字" value="${disease }"/>
                			  <button type="submit" title="Search"><i class="glyphicon glyphicon-search"></i></button>
                			<span>匹配选项：</span>
                			<label ><input type="radio" name="queryOption" value="in" <c:if test="${queryOption=='in' }">checked</c:if>> 模糊匹配</label>
                			<label><input type="radio" name="queryOption" value="all" <c:if test="${queryOption=='all' }">checked</c:if>>精确匹配</label>
                			<label><input type="radio" name="queryOption" value="eq" <c:if test="${queryOption=='eq' }">checked</c:if>>完全匹配</label>
                			<a href="#" id="optionTip" data-placement="right"  data-toggle="tooltip" title="模糊匹配：病证特征中包括关键字中的一项。<br>精确匹配：病证特征包括关键字中的全部特征。<br>完全匹配：病证特征与关键字的特征完全相同。">匹配选项说明</a>
                		</form>
	                		</div>
                	</div>
                	<div class="row">
                		<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12 main'>
                			<ul class="pagination"></ul>
                			<table class="table table-bordered">
                				<thead>
                					<tr>
                						<th>眼象特征</th>
                						<th>病证</th>
                						<th>部位</th>
                						<th>脏腑</th>
                						<th>原句</th>
                 						<th>图片</th>
                 						<th nowrap>详细信息</th>
                					</tr>
                				</thead>
	                		<c:forEach var="i" items="${contents }">
	                		<tr>
	                			<td nowrap>${i.match }</td>
	                			<td >${i.disease }</td>
	                			<td nowrap>${i.featurePart }</td>
	                			<td nowrap>${i.viscera }</td>
	                			<td>${i.matchSentence }</td>
	                			<td nowrap>${i.imageName }</td>
	                			<td>
	                			<button type="button" class="btn btn-default" title="详细信息"  
      							data-container="body" data-toggle="popover" data-placement="left" 
      							data-content="<b>原文：</b>${i.wholeSentence }<br><b>卷：</b>${i.volume }<br><b>篇：</b>${i.sheet }<br><b>章：</b>${i.chapter }<br><b>节：</b>${i.section }<br><b>部分：</b>${i.part }<br><b>页码：</b>${i.page }">
      							详细信息
   								</button>
	                			
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
