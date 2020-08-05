<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
<title>个人健康档案</title>
    <script src="${ctx_jquery }/jquery.form.js"></script>
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
    	  $('.updateContent').click(function(){
				var id=$(this).attr("id");
				$.ajax({
					url:'${ctx}/resource/loadContent.json',
					async:false,
					dataType:'json',
					data:{
						'id':id
					},
					success:function(data){
						$('#contentArea').text(data.data);
			    		$('#updateContentDialog').modal('show');
					}
				})
    	  });
    	  $("#saveContent").click(function () {
              var options = {
            	url:"${ctx }/resource/saveContent.json",
                  success: function (o) {
                	  if (o.message="success") {
                          alert("保存成功");
							window.location.href="${ctx}/resource/list.htm";

                	  } else {
                		  alert("保存失败");
                	  }
                  }
              };
              $("#fieldInfo").ajaxForm(options);
          })
      });
    </script>
</head>
<body>

  
  
  
  
  <!-- --------------- -->
   <div class="container-fluid">
                    <!-- 以下这是页面需要编辑的区域 -->
                         <div class="row">
                                <div class='col-lg-12 col-md-12 col-sm-12 col-xs-12 main'>
						          	<c:forEach var="i" items="${parts }">
							          <h2>${i.title }</h2>
							          <c:if test="${i.type=='chapter' }">
								          <p class="lead">${i.volumeName} > ${i.sheetName }</p>
								      </c:if>
							          <c:if test="${i.type=='section' }">
								          <p class="lead">${i.volumeName} > ${i.sheetName } -> ${i.sheetName }</p>
								      </c:if>
							          <c:if test="${i.type=='part' }">
								          <p class="lead">${i.volumeName} > ${i.sheetName } -> ${i.sheetName } -> ${i.chapterName }</p>
								      </c:if>
								      
							          <c:forEach var="content" items="${i.contentList }">
							          <p class="indent">
							          ${content }
							          </p>
							          </c:forEach>
							          <p class="text-right muted"><fmt:formatDate value="${i.ctime}" pattern="yyyy-MM-dd HH:mm:ss"/>
							            <a class="updateContent" id="${i.id}">编辑内容</a>    
							          </p>  
							        </c:forEach>
						      </div>
					    </div>
					    <div class="row">
				    	    <div id="updateContentDialog" class="modal content fade" tabindex="-1" style="height: auto; width: 1000px;display:none;">
							    <form id="content"  role="form" method="post" action="${ctx }/resource/updateContent.htm">
							    <div class="modal-header">
							      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
							      <h4 class="modal-title">编辑内容</h4>
							    </div>
							    <div class="modal-body">
							        <input type="hidden" id="contentId" name="contentId" value="">
							          <div class="form-group">
							            <label for="name">内容</label>
							            <div>
							              <textarea class="content" id="contentArea" class="form-control" cols="10" rows="15" name="content"></textarea>
							            </div>
							          </div>
							    
							    </div>
							    <div class="modal-footer" style="text-align: center;">
							      <button type="button" data-dismiss="modal" class="btn btn-default">关闭</button>
							      <button id="saveContent" type="button" class="btn btn-primary">保存</button>
							    </div>
							    </form>
						  </div>  
					    </div>
                    <!-- 以上这是页面需要编辑的区域 -->
</div>
</body>
</html>
