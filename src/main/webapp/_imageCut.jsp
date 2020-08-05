<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="ctx_static" value="${ctx}/static" />
<c:set var="ctx_jquery" value="${ctx_static}/jquery" />
<c:set var="ctx_bootstrap" value="${ctx_static}/bootstrap-3.3.4-dist" />
<c:set var="ctx_js" value="${ctx_static}/js" />
<c:set var="debug" value="true"/>

<link rel="stylesheet" type="text/css" href="${ctx_bootstrap}/css/jquery.Jcrop.css">

<style type="text/css">
#preview-pane .preview-container {
	width: 100px;
	height: 100px;
	overflow: hidden;
	border: 1px solid #ccc;}
	
<!-- file美化css start -->
.upload-file-tip{margin-bottom: 5px;}
.upload-file-btn-div{border: 1px solid #ddd; height: 52px;}
.upload-file-btn-div .upload-file-btn-ceil{
	position: absolute; 
	text-align: center; 
	width: 204px; 
	height: 40px; 
	line-height: 40px; 
	font-size: 16px; 
	z-index: 100; 
	background-color: #fff; 
	border: 1px solid #ccc; 
	margin: 5px 5px;
	cursor: pointer;}
.upload-file-btn-div .upload-file-btn{
	position: absolute; 
	z-index: 99; 
	left: 20px; 
	height: 40px; 
	width: 200px; 
	margin: 5px 5px;}
.upload-file-btn-div .upload-file-btn-tip{
	padding-left: 230px; 
	color: #999; 
	line-height: 52px;}
	#myModal1{
		margin-top:5%;
	}
<!-- end file美化css -->

</style>

<script type="text/javascript" src="${ctx_jquery }/jquery.Jcrop.js"></script>
<script src="${ctx_jquery}/jquery.form.js"></script>
<script type="text/javascript">
$(function(){
	//定义一个全局api，这样操作起来比较灵活
	var api = null,
		boundx,
        boundy,
        
		$preview = $('#preview-pane'),
		$pcnt = $('#preview-pane .preview-container'),
		$pimg = $('#preview-pane .preview-container img'),
		
		xsize = $pcnt.width(),
		ysize = $pcnt.height();

	$("#fcupload").change(function(){
		
		var _this = this;
		
		var options = {
			    url:"${ctx}/user/getUploadImageHW.json",
				success:function(data){
					if (_this.files && _this.files[0]) {
						var reader = new FileReader();
						reader.readAsDataURL(_this.files[0]);
						
						reader.onload = function (e) {
							
							var n_imgH, n_imgW;
							
							var imgH = data.height;
							var imgW = data.width;
							
							if (imgH/imgW > 0.7) {
								n_imgH = 280;
								n_imgW = n_imgH*imgW/imgH;
							} else if (imgH/imgW < 0.7) {
								n_imgW = 400;
								n_imgH = n_imgW*imgH/imgW;
							} else {
								n_imgW = 280;
								n_imgH = 280;
							}
							
							$("#cutimg").height(n_imgH);
							$("#cutimg").width(n_imgW);
							
							$("#y_w").val(n_imgW);
							$("#y_h").val(n_imgH);
							
							$('#cutimg').attr('src', e.target.result);
							$('#previewImg').attr('src', e.target.result);
							$("#img_div").css({"padding":"0"});
							if (n_imgW > n_imgH) {
								$("#img_div").css({"paddingTop": (280 - n_imgH)/2 + "px"});
							} else if (n_imgW < n_imgH) {
								$("#img_div").css({"paddingLeft": (400 -n_imgW)/2 + "px"});
							} else {
								$("#img_div").css({"paddingLeft": (400 -n_imgW)/2 + "px"});
							}
							
							$('#cutimg').Jcrop({
								onChange: updateCoords,
								onSelect: updateCoords,
								aspectRatio: xsize / ysize, // 图像剪切比例
								//boxWidth:600,
								//minSize:[200,200],   //裁剪框默认大小
								//setSelect : [0,0,200,200]
							},function(){
								var bounds = this.getBounds();
								boundx = bounds[0];
								boundy = bounds[1];
								api = this;
							});
						};
						if (api != undefined) {
							api.destroy();
						}
					}
				}
			};
		$("#userImg").ajaxSubmit(options);
	});
		
	function updateCoords(obj) {
		$("#x").val(obj.x);
		$("#y").val(obj.y);
		$("#w").val(obj.w);
		$("#h").val(obj.h);
		
		if(parseInt(obj.w)>0){
			//计算预览区域图片缩放的比例，通过计算显示区域的宽度（与高度）与剪裁的宽度（与高度）之比得到
			var rx = xsize / obj.w;
			var ry = ysize / obj.h;
			//通过比例值控制图片的样式与显示
			$pimg.css({
				width: Math.round(rx * boundx) + 'px',
				height: Math.round(ry * boundy) + 'px',
				marginLeft: '-' + Math.round(rx * obj.x) + 'px',
				marginTop: '-' + Math.round(ry * obj.y) + 'px'
	        });
		}
	};
	 
	$("#nav_info li").click(function(){
		var index = $(this).index();
		if ($(this).hasClass("active")) {
			$(this).removeClass("active");
		} else {
			$(this).addClass("active").siblings().removeClass("active");
		}
		switch (index) {
			case 0:
				$("#piccut").hide();
				$("#userinfo").show();
				break;
			case 1:
				$("#piccut").show();
				$("#userinfo").hide();
				break;
		}
	});
}); 
</script>

<script type="text/javascript">
	
	$(document).ready(function(){
		//保存用户信息
		$("#saveBtn1").click(function(){
			var options = {
			    url:"${ctx}/user/updateUserInfo.json",
				success:function(o){
					if(o.message="success"){
						alert("保存成功!");
						$("#userid").val(o.data);
						//window.location.href="${ctx}/user/userlist.htm";
					}else{
						alert("保存失败！");
					}
				}
			};
			$("#userInfo").ajaxForm(options);
		})
		
		
		//上传图片
		$("#saveBtnImg").click(function(){
			var options = {
					url:"${ctx}/user/uploadHeadImage.json",
					success:function(o){
						if(o.message="success"){
							alert("保存成功!");
							$("#userid").val(o.data);
							window.location.reload();
						}else{
							alert("保存失败!");
						}
					}
			};
			$("#userImg").ajaxForm(options);
		})
		
		
		$("#selectedImgFile").click(function(){
			$("#fcupload").click();
		});
		
	});
	</script>
<%
		Map<String,Object> user = (Map<String,Object>) request.getSession().getAttribute("user");
		if(user != null){
%>
<!-- start -- 模态框（Modal） -->
<%-- <form name="form" action="${ctx}/user/uploadHeadImage.htm" class="form-horizontal" method="post" enctype="multipart/form-data"> --%>
<div class="modal fade" id="myModal1" tabindex="-1" role="dialog" 
   aria-labelledby="myModalLabel" aria-hidden="true" style="overflow: hidden;">
   <div class="modal-dialog">
      <div class="modal-content">
         <div class="modal-header">
            <button type="button" class="close" 
               data-dismiss="modal" aria-hidden="true">
                  &times;
            </button>
            <h4 class="modal-title" id="myModalLabel">
             	个人资料设置
            </h4>
         </div>
         <ul class="nav nav-tabs" id="nav_info">
         	<li><a href="javascript:void(0);">个人信息</a></li>
         	<li class="active"><a href="javascript:void(0);">头像设置</a></li>
         </ul>
         <!-- 裁剪图片弹窗  -- start -->
         <div id="piccut" >
         <form id="userImg" name="form" class="form-horizontal" method="post" enctype="multipart/form-data">
	         <div class="modal-body">
	         	<!-- 选择上传图片  start -->
	         	<div class="modal-body" style="margin-top: -10px; margin-bottom: -10px;">
					<div class="upload-file-tip">选择本地照片，上传编辑自己的头像</div>
					<div class="upload-file-btn-div">
						<div class="upload-file-btn-ceil" id="selectedImgFile">选 择 文 件</div>
						<input class="upload-file-btn" type="file" name="imgFile" id="fcupload"/>
						<font class="upload-file-btn-tip"><span>支持jpg、jpeg、gif、png、bmp格式</span></font>
					</div>
	         	</div>
	         	<!-- end 选择上传图片 -->
	         	
	         	<!-- 图片裁剪  start -->
	         	<div class="modal-body row">
	         		<!-- 图片裁剪区域 start -->
	         		<div class="col-md-9">
	         			<div style="height: 280px; width: 400px; background-color: #ccc;" id="img_div">
	         				<div>
		         				<img alt="" id="cutimg" />
			         			<input type="hidden" id="x" name="x"/>
				                <input type="hidden" id="y" name="y"/>
				                <input type="hidden" id="w" name="w"/>
				                <input type="hidden" id="h" name="h"/>
								<input type="hidden" id="y_w" name="y_w"/>
				                <input type="hidden" id="y_h" name="y_h"/>
	         				</div>
	         			</div>
	         		</div>
	         		<!-- end 图片裁剪区域 -->
	         		
	         		<!-- 图片预览区域  start  -->
	         		<div class="col-md-3">
	         			<div id="preview-pane" style="margin-left: 10px;">
						   <div>
						   	 <h4>头像预览</h4>
						   </div>
						   <div class="preview-container">
						     <img id="previewImg" class="jcrop-preview" alt="" src="${ctx}/user/imgByByte.htm?userid=<%=user.get("_id")%>" style="height: 100px; width: 100px;" />
						   </div>
						   <div>
							 <span style="font-size: 12px;">大小头像100*100</span>
						   </div>
						</div>
	         		</div>
	         		<!-- end 图片预览区域 -->
	         	</div>
	         	<!-- end 图片裁剪 -->
	         </div>
	        <%--  <input type="hidden" name="userid" value="<%=user.get("_id") %>" id="userid"> --%>
	         <input type="hidden" name="username" value="<%=user.get("username") %>">
	         <input type="hidden" name="telephone" value="<%=user.get("telephone") %>">
	         <input type="hidden" name="birthday" value="<%=user.get("birthday") %>">
	         <input type="hidden" name="idCard" value="<%=user.get("idCard") %>">
	         <input type="hidden" name="address" value="<%=user.get("address") %>">
	          <div class="modal-footer">
	             <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	             <!-- <button id="submit" class="btn btn-primary">保存</button> -->
	             <input class="btn btn-primary" type="submit" id="saveBtnImg" value="保存">
	         </div>
         </form>
         </div>
         <!-- end -- 裁剪图片弹窗 -->
         
         <!-- 个人基本信息Start -->
         <div id="userinfo" style="display:none">
          <div class="modal-body">
	        <form id="userInfo" role="form" method="post">
				<input type="hidden" name="userid" value="<%=user.get("id") %>" id="userid">
				<div class="modal-body row">
					<label for="username" class="col-sm-2 control-lavel">用户名</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" name="username" value="<%=user.get("username") %>" readonly="readonly">
					</div>
				</div>
				
				<div class="modal-body row">
					<label for="telephone" class="col-sm-2 control-lavel">联系方式</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" name="telephone" value="<%=user.get("telephone") %>">
					</div>
				</div>
				
				<div class="modal-body row">
					<label for="birthday" class="col-sm-2 control-lavel">出生年月</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" name="birthday" value="<%=user.get("birthday") %>">
					</div>
				</div>
				
				<div class="modal-body row">
					<label for="idCard" class="col-sm-2 control-lavel">身份证号</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" name="idCard" value="<%=user.get("idCard") %>">
					</div>
				</div>
				
				<div class="modal-body row">
					<label for="address" class="col-sm-2 control-lavel">家庭地址</label>
					<div class="col-sm-10">
						<input type="text" class="form-control" name="address" value="<%=user.get("address") %>">
					</div>
				</div>
				<!-- <input class="btn btn-default" type="submit" id="saveBtn" value="提交"> -->
				
				 <div class="modal-footer">
		             <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		             <input class="btn btn-primary" type="submit" id="saveBtn1" value="保存">
		         </div>
		</form>
	   </div>
	   </div>
	   <!-- 个人基本信息end -->
      </div>
	</div>
</div>
<%
		}
%>
<%-- </form> --%>
<!-- end -- 模态框（Modal）  -->