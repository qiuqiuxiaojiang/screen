<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>

<title>社区健康筛查数据采集与管理系统</title>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/lightbox/lightbox.css"></link>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/zyUpload/zyUpload.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/pagenation/pagenation.css"></link>
	

<script type="text/javascript">
	$(function (){
		$("#mytable").createTable({
	        title:"记录列表",
	        url:"${ctx}/eyeRecord/infoList.json",
	        multiChecked:false,
	        hasRowNumber:false,
	        width:"auto",
	        params:{},
	        columns:[
	            {field:"uniqueId", title:"用户标识",width:100},
	            {field:"userId",title:"记录编号",width:100,formatter:function(val,row){
	            	var subval=val;
	            	if (val.length>18) {
	            		subval=val.substring(0,18);
	            	}
	            	var str='<a href="${ctx}/eyeRecord/listRecord/'+val+'.htm" title="查看目诊记录">'+subval+'</a>';
					return str;
	            }},
	            {field:"visitTime",title:"目诊日期",width:100},
	            {field:"sex",title:"性别",width:100},
	            {field:"age",title:"年龄",width:100},
	            {field:"checkPlace",title:"筛查地点",width:100},
	            {field:"id",title:"操作",width:150,formatter:function(val,row){
	                var str='<a href="${ctx}/eyeRecord/download/'+val+'.htm" title="下载"><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span></a>&nbsp; '; 
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
						url:'${ctx}/eyeRecord/deleteMulti.json',
						type:'post',
						async:false,
						data:{
							'id':ids
						},
						success:function(data){
							if(data.message=="success"){
								alert("删除成功！");
								window.location.href="${ctx}/eyeRecord/list.htm";
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
		
	})
	
	//单个删除
	function singledel(that){
		var id=$(that).attr('id');
		var name=$(that).attr('name');
		var dialog=confirm("您确定要删除这条记录吗？");
		if(dialog){
			$.ajax({
				url:'${ctx}/eyeRecord/delete/'+id+'.json',
				type:'post',
				async:false,
				data:{
				},
				success:function(data){
					if(data.message=="success"){
						alert("删除成功！");
						window.location.href="${ctx}/eyeRecord/list.htm";
					}else{
						alert("删除失败！");
					}
				}
			})
		} 
	}
	
	function search(){
		var customerId=$('#customerId').val();
		var customerName=$('#customerName').val();
		$("#mytable").createTable({
			params:{
				customerId:customerId,
				customerName:customerName
			}
		});
	}
</script>
</head>
<body>
<div style="display:none">
<object id="MyObject" classid="clsid:AD6DB163-16C6-425F-97DA-CC28F30F249A" codebase="HXIDCard.CAB#version=1,0,0,1" width=0 height=0></object>
</div>


<div class="container-fluid">
		<!-- 以下这是页面需要编辑的区域 -->
		<div class="row">
			<div class='col-lg-12 col-md-12 col-sm-12 col-xs-12 main'>
				<div class="table-assist" for="mytable"  style="text-align: right">
				         <div class="search-panel">
				            <label>身份证：</label><input type="text" name="customerId" id="customerId" />
				            <input type="button" class="btn btn-primary" value="读取身份证" onclick="javascript:readCard()">
				            <label>姓名：</label><input type="text" name="customerName" id="customerName" />
				            <input type="button" class="btn btn-danger btn-sm" value="查询" onclick="javascript:search();" />
				        </div> 
			    </div>
				<table id="mytable" class="table table-bordered"></table>
			</div>
			<!-- 以上这是页面需要编辑的区域 -->
		</div>
	</div>

  
</body>
<script type="text/javascript" src="${ctx_static}/pagenation/pagenation.js"></script>
<script type="text/javascript" src="${ctx_static}/device/HXIDCard.js"></script>
<script type="text/javascript">
function readCard() {
    var result = HXIDCard.readIDCardContent(MyObject);
    if ("1" == result.code) {
    	var idCard = result.content.cardNo;
    	$('#customerId').val(idCard);
    }
}
</script>

</html>
