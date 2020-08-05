<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>

<title>其他信息列表</title>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/pagenation/pagenation.css"></link>
	
	<style type="text/css">
		.delModule,#batchDel{
			cursor:pointer;
		}
		table td{
			text-align:center;
		}
		.pull-right {
		    margin-top: 2%;
		}
		@media screen and (min-width:1340px) and (max-width: 1680px) {
			#mytable{
				width:150%!important;
				max-width: 150%!important;
			}
		}
		
		@media screen and (max-width:1340px) {
			#mytable{
				width:200%!important;
				max-width: 200%!important;
			}
		}
		@media screen and (max-width:2100px) {
			#mytable{
				width:240%!important;
				max-width: 240%!important;
			}
		}
		
		.modal-dialog {
		    width: 600px;
		    margin: 15% auto;
		}
		.modal-body {
		    width:100%;
		    min-height:8em;
		}
		#uploadFile{
			width:20%;
		}
		.modal-footer {
		    text-align: center;
		}
		.btn-primary {
		    color: #fff;
		    background-color: #42a0d2;
		    border: none!important;
		}
	</style>

<script type="text/javascript">
	$(function (){
		$("#mytable").createTable({
	        title:"记录列表",
	        url:"${ctx}/otherInfo/otherlist.json",
	        multiChecked:false,
	        hasRowNumber:false,
	        width:"auto",
	        params:{},
	        columns:[
                {field:"uniqueId",title:"编号",width:100,formatter:function(val,row){
	                var str="<a href='${ctx}/otherInfo/editOtherInfo.htm?id="+row.id+"' title='编号'>"+row.uniqueId+"</a>&nbsp;"; 
	                return str;
	            }},
	            {field:"drugAllergy",title:"药物过敏史",width:100},
	            {field:"pastDisease", title:"既往疾病史",width:100},
	            {field:"heredity", title:"遗传病史",width:100},
	            {field:"heredityFather", title:"家族病史（父亲）",width:100},
	            {field:"heredityMother", title:"家族病史（母亲）",width:100},
	            {field:"heredityBs", title:"家族病史（兄弟姐妹）",width:100},
	            {field:"heredityChildren", title:"家族病史（子女）",width:100},
	            {field:"frequencyExercise",title:"锻炼频率",width:100},
	            {field:"exerciseTime",title:"每次锻炼时间（分钟）",width:100},
	            {field:"stickExerciseTime",title:"坚持锻炼时间（年）",width:100},
	            {field:"exerciseMode",title:"锻炼方式",width:100},
	            {field:"eatingHabits",title:"饮食习惯",width:100},
	            {field:"smokingStatus",title:"吸烟状况",width:100},
	            {field:"dailySmoking",title:"日吸烟量（支)",width:100},
	            {field:"smokingAge",title:"开始吸烟年龄（岁）",width:100},
	            {field:"smokingCessation",title:"戒烟年龄（岁）",width:100},
	            {field:"drinkingFrequency",title:"饮酒频率",width:100},
	            {field:"drinkVolume",title:"日饮酒量（两）",width:100},
	            {field:"whetherAlcohol",title:"是否戒酒",width:100},
	            {field:"drinkingAge",title:"开始饮酒年龄（岁）",width:100},
	            {field:"drunkPastyear",title:"近一年内是否曾醉酒",width:100},
	            {field:"drinkingType",title:"饮酒种类",width:100}
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
				url:'${ctx}/otherInfo/delOtherInfo/'+id+'.json',
				type:'post',
				async:false,
				data:{
				},
				success:function(data){
					if(data.message=="success"){
						alert("删除成功！");
						window.location.href="${ctx}/otherInfo/list.htm";
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
				<table id="mytable" class="table table-bordered">
				</table>
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
