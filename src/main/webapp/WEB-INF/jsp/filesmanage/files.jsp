<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>

<title>个人健康档案</title>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/lightbox/lightbox.css"></link>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/zyUpload/zyUpload.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/pagenation/pagenation.css"></link>
	
<style type="text/css">

	#ss a{
		cursor:pointer;
	}
	.demo{
		height:auto;
	}
	#info{
		display:none;
	}
	.lb-outerContainer {
		width:100%;
	}
	#myModal{
		width:100%;
		border: 0px;
		margin-top:100px;
	}
	.modal{
		margin-left:0px;
		background-color:transparent;
		
	}
 	.modal-dialog{
 		width:800px;
 	}
 	#mytable td{
 		text-align:center;
 	}
	
</style>

<script type="text/javascript">
	$(function (){
		$("#mytable").createTable({
	        title:"文件列表",
	        url:"${ctx}/files/getUserFilesList.json",
	        multiChecked:true,
	        hasRowNumber:true,
	        width:"auto",
	        params:{},
	        columns:[
	            {field:"filename",title:"文件名称",width:300},
	            {field:'contentType',title:'缩略图',width:150,formatter:function(val,row){
	            	var str="";
					if(val==".pdf"){
						str='<img src="${ctx_static}/img/pdf.png" class="img-rounded" style="height: 40px;" />';
					}else if(val==".txt"){
						str='<img src="${ctx_static}/img/txt.png" class="img-rounded" style="height: 40px;" />';
					}else if(val==".rar"){
						str='<img src="${ctx_static}/img/rar.png" class="img-rounded" style="height: 40px;" />';
					}else if(val==".zip"){
						str='<img src="${ctx_static}/img/zip.png" class="img-rounded" style="height: 40px;" />';
					}else if(val==".doc"){
						str='<img src="${ctx_static}/img/doc.png" class="img-rounded" style="height: 40px;" />';
					}else if(val==".ppt"){
						str='<img src="${ctx_static}/img/ppt.jpg" class="img-rounded" style="height: 40px;" />';
					}else if(val==".xls"){
						str='<img src="${ctx_static}/img/xls.png" class="img-rounded" style="height: 40px;" />';
					}else if(val==".jpg" ||val ==".gif"||val==".bmp"||val==".png"){
						str='<a href="${ctx}/files/downFile/'+row.id+'.htm" data-lightbox="example-1">';
						str+='<img src="${ctx}/files/downThumbFile/'+row.id+'.htm" class="img-rounded" style="height: 40px;" /></a>';
					}else{
						str='<img src="${ctx_static}/img/add_img.png" class="img-rounded" style="height: 40px;" />';
					}
	            	return str;
	            }},
	            {field:"contentType",title:"类型",width:130,sorting:true},
	            {field:"length",title:"大小",width:150,sorting:true,formatter:function(val){
	            	var str="";
	            	if(val>=1024*1024){
	            		str=Math.round(val/(1024*1024)*10)/10+"MB";
	            	}else{
	            		str=Math.round(val/1024*10)/10+"KB";
	            	}
	            	return str;
	            }},
	            {field:"uploadDate",title:"上传时间",width:250,sorting:true,formatter:function(val){
	            	var date=new Date(val);
	            	var y = date.getFullYear();
	            	var m = date.getMonth() + 1;
	            	var d = date.getDate();
	            	var h = date.getHours();
	            	var i = date.getMinutes();
	            	var s = date.getSeconds();
	            	var str=y+"-"+m+"-"+d+" "+h+":"+i+":"+s;
	            	return str;
	            }},
	            {field:"id",title:"操作",width:150,formatter:function(val,row){
	                var str='<a href="${ctx}/files/downFile/'+val+'.htm" title="下载"><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span></a>&nbsp; '; 
					str+='<a href="javascript:;" onclick="singledel(this)" id="'+val+'" name="'+row.filename+'" title="删除"><span class="glyphicon glyphicon-remove" aria-hidden="true"></a>'; 
	                return str;
	            }}
	        ],
	        pagenation:true
	    });
		
		//批量删除
		$('#delFile').bind('click',function(){
			var ids="";
			$("input[name='ck_box']").each(function(){
			    if(this.checked){
			    	ids+=$(this).val()+",";
			    }
			});
			if(ids!=""){
				var dialog=confirm("您确定要删除这些文件吗？");
				if(dialog){
					$.ajax({
						url:'${ctx}/files/delFile.json',
						type:'post',
						async:false,
						data:{
							'id':ids
						},
						success:function(data){
							if(data.message=="success"){
								alert("删除成功！");
								window.location.href="${ctx}/files/filesManage.htm";
							}else{
								alert("删除失败！");
							}
						}
					})
				}
			}else{
				alert("请选择要删除的文件！");
			}
		})
		
		//头闭模态窗口时跳转到列表
		$('#myModal').on('hide.bs.modal', function () {
			window.location.href="${ctx}/files/filesManage.htm";
		})
		
		$('#uploadBnt').click(function(){
			$('#uploadInner').html("<div id='upload-demo' class='demo'></div>");
			//重新初始化数据
	        ZYFILE.init();
	        //绘制上传层
	        drawDemoMadel();
		})
		
	})
	
	//单个删除
	function singledel(that){
		var id=$(that).attr('id');
		var name=$(that).attr('name');
		var dialog=confirm("您确定要删除“ "+name+" ”这个文件吗？");
		if(dialog){
			$.ajax({
				url:'${ctx}/files/delFile.json',
				type:'post',
				async:false,
				data:{
					'id':id
				},
				success:function(data){
					if(data.message=="success"){
						alert("删除成功！");
						window.location.href="${ctx}/files/filesManage.htm";
					}else{
						alert("删除失败！");
					}
				}
			})
		} 
	}
	
	function search(){
		var type=$('#selectType').val();
		var filename=$('#searchName').val();
		if(type==0){
			type=null;
		}
		$("#mytable").createTable({
			params:{fileName:filename,type:type}
		})
	}
</script>
</head>
<body>


<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12 main'>.
				<div class="table-assist" for="mytable"  style="text-align: right">
			        <div class="search-panel">
			            <label>文件名称：</label><input type="text" name="name" id="searchName" />
			            <label>选择类型：</label><select name="type" id="selectType">
			            						<option value="0">-- 请选择 --</option>
			                                    <option value="1">图片</option>
			                                    <option value="2">附件</option>
			                                </select>
			            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
			        </div>
			        <div class="btn-panel">
			            <input id="uploadBnt" type="button" class="btn btn-sm btn-success" data-toggle="modal" data-target="#myModal" value="上传新文件" />
			            <input id="delFile" type="button" class="btn btn-sm btn-success" value="批量删除" />
			        </div>
			    </div>
				<table id="mytable" class="table table-bordered"></table>
			</div>
			<!-- 以上这是页面需要编辑的区域 -->
		</div>
	</div>

   <!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" data-backdrop="static"  tabindex="-1"  role="dialog" aria-labelledby="myModalLabel"  aria-hidden="true">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
	                  &times;
	            </button>
	            <h4 class="modal-title" id="myModalLabel">
	             	  <b>文件上传</b>
	            </h4>
	         </div>
	         <div class="modal-body" id="uploadInner">
				<div id="upload-demo" class="demo"></div>
	         </div>
	         <div class="modal-footer">
	            <button type="button" class="btn btn-primary" data-dismiss="modal">
	            	关闭
	            </button>
	         </div>
	      </div>
		</div>
   	</div>
  
</body>
<script type="text/javascript" src="${ctx_jquery}/jquery.form.js"></script>
<script type="text/javascript" src="${ctx_static}/lightbox/lightbox-2.6.min.js"></script>
<script type="text/javascript" src="${ctx_static}/zyUpload/uploadinfo.js"></script>
<script type="text/javascript" src="${ctx_static}/zyUpload/zyFile.js"></script>
<script type="text/javascript" src="${ctx_static}/zyUpload/zyUpload.js"></script>
<script type="text/javascript" src="${ctx_static}/js/component.js"></script>
<script type="text/javascript" src="${ctx_static}/js/zyUpload-assist.js"></script>
<script type="text/javascript" src="${ctx_static}/pagenation/pagenation.js"></script>
</html>
