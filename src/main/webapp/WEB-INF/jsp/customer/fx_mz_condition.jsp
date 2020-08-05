<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>建档情况</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/component.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/common.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/bootstrap.min.css"></link>
    <link rel="stylesheet" type="text/css" href="${ctx_static }/healthcheck/css/healthcheck.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/static/js/component.js"></script>
<body>
<div class="main">
	<div class="tab">
        <ul>
            <a href="${ctx }/manage/hv/csIndex.htm?district=${districtHc}"><li>建档情况</li></a>
            <a href="${ctx }/manage/hv/xtIndex.htm?district=${districtHc}"><li>血糖筛查情况</li></a>
            <a href="${ctx }/manage/hv/xyIndex.htm?district=${districtHc}"><li>血压筛查情况</li></a>
            <a href="${ctx }/manage/hv/xzIndex.htm?district=${districtHc}"><li>血脂筛查情况</li></a>
           <%--  <a href="${ctx }/manage/hv/gxbIndex.htm?district=${districtHc}"><li>冠心病筛查情况</li></a> --%>
            <a href="${ctx }/manage/hv/fpIndex.htm?district=${districtHc}"><li>肥胖筛查情况</li></a>
            <a href="${ctx }/manage/hv/tzIndex.htm?district=${districtHc}"><li>体质筛查情况</li></a>
            <a href="${ctx }/manage/hv/mzIndex.htm?district=${districtHc}"><li class="activeLi">目诊筛查情况</li></a>
        </ul>
    </div>
	<input type="hidden" id="district" value="${districtHc }">
	<div class="chartDiv">
        <h1>中医证候与代谢性疾病患病率的关系</h1>
        <table class="table" id="table">
        </table>
    </div>
    <div class="page">
    	<p style="position: absolute;" id="pagePId"></p >
	    <div id='page1'></div>
	</div>
	
	<div style="text-align: center;margin-left: 270px;margin-bottom: 50px;">
       	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="mzData"/>
    </div>
</div>
<script src="${ctx_static }/healthcheck/js/g2.min.js"></script>
<script src="${ctx_static }/healthcheck/js/data-set.min.js"></script>
<script src="${ctx_static }/healthcheck/js/jquery-1.11.2.js"></script>
<script src="${ctx_static }/js/initPage.js"></script>
<script>
    function showResponse(o){
    	$.ajaxSetup({ cache: false });
		var code = o["code"];
		if (code == 1 || code == "1") {
			alert("表格初始化失败");
		} else if (code == 0 || code == "0") {
			var listTable = o["dataMap"]["result"];
			var tbodyObj = $("#table");
			tbodyObj.empty();
			var htmlStrThead = "<tr >"+
			"<td>序号</td>"+
            "<td>中医证候</td>"+
            "<td>中医证候人数</td>"+
            "<td>糖尿病人数</td>"+
            "<td>糖尿病比例</td>"+
            "<td>高血压人数</td>"+
            "<td>高血压比例</td>"+
            "<td>血脂异常患者人数</td>"+
            "<td>血脂异常患者比例</td>"+
            "<td>肥胖人数</td>"+
            "<td>肥胖比例</td></tr>";
            tbodyObj.append(htmlStrThead);
            
			for (var i =0;i<$(listTable).size();i++ ) {
				var mapTable = listTable[i];
				var reportPath = "";
				
				var htmlStr = "<tr >"+
                "<td>"+mapTable["count"]+"</td>"+
                "<td>"+mapTable["syndrome"]+"</td>"+
                "<td>"+mapTable["num"]+"</td>"+
                "<td>"+mapTable["tnbCount"]+"</td>"+
                "<td>"+mapTable["tnbPerc"]+"%</td>"+
                "<td>"+mapTable["gxyCount"]+"</td>"+
                "<td>"+mapTable["gxyPerc"]+"%</td>"+
                "<td>"+mapTable["xzCount"]+"</td>"+
                "<td>"+mapTable["xzPerc"]+"%</td>"+
                "<td>"+mapTable["fpCount"]+"</td>"+
                "<td>"+mapTable["fpPerc"]+"%</td></tr>";
				tbodyObj.append(htmlStr);
			}
			initTablePage(o["dataMap"]["total"],o["dataMap"]["currentPage"],o["dataMap"]["pages"]);
			/* $("#customerTbodyId tr").click(function() {
				$(this).css("background","#ff540070").addClass("selected").siblings().css("background","#fff").removeClass("selected");
				var barcode = $(this).find("td:first").html();
				$("#uploadSampleFormBarcodeId").val(barcode);
				$("#uploadSampleChemFormBarcodeId").val(barcode);
	    	}); */
		}
	}
	function initTablePage(total,currentPage,pages){
		/** 设置处于第几页 **/
		var pagePHtml = "当前显示"+(currentPage*10-9)+"到"+ currentPage*10 +"条，共60条记录";
		$("#pagePId").html(pagePHtml);
		/** 设置表格分页插件，只在页面初始化时设置，pageFresh分页插件初始化标识 **/
			
			pageUtil.initPage('page1', {
				totalCount : total,// 总页数，一般从回调函数中获取。如果没有数据则默认为1页
				curPage : currentPage,// 初始化时的默认选中页，默认第一页。如果所填范围溢出或者非数字或者数字字符串，则默认第一页
				showCount : 6,// 分页栏显示的数量
				pageSizeList : [ 5, 10, 15, 20, 25, 30 ],// 自定义分页数，默认[5,10,15,20,50]
				defaultPageSize : 10,// 默认选中的分页数,默认选中第一个。如果未匹配到数组或者默认数组中，则也为第一个
				isJump : false,// 是否包含跳转功能，默认false
				isPageNum : true,// 是否显示分页下拉选择，默认false
				isPN : true,// 是否显示上一页和下一面，默认true
				isFL : false,// 是否显示首页和末页，默认true
				jump : function(curPage, pageSize) {// 跳转功能回调，传递回来2个参数，当前页和每页大小。如果没有设置分页下拉，则第二个参数永远为0。这里的this被指定为一个空对象，如果回调中需用到this请自行使用bind方法
					console.log(curPage, pageSize);
					$("#page1").empty();
					/** 页面点击事件触发表格数据查询操作 **/
					initTable(curPage,rows,{});
				},
			});
	}
    
    
    function initTable(curPageP,rowsP,dataParam){
		/* if (orderP != undefined && orderP!="") {
			order = orderP;
			dataParam["order"] = order;
		}
		if (sortP != undefined && sortP!="") {
			sort=sortP;
			dataParam["sort"] = sort;
		} */
		if (curPageP != undefined && curPageP!="") {
			currentPage = curPageP;
			dataParam["currentPage"] = currentPage;
		}
		if (rowsP != undefined && rowsP!="") {
			rows = rowsP;
			dataParam["rows"] = rows;
		}
		dataParam["district"] = $("#district").val();
		var options = {
				url:"${ctx}/manage/hv/eyeRecordDistribution.json",
				data:dataParam,
                success: showResponse  // 提交后 
            }; 
		$.ajax(options);
	}
    
    $(document).ready(function(){
    	initTable(1,10,{});
    });
    
   /*  $("#mzData").click(function(){
    	 var district = $("#district").val();
	     window.location.href = '${ctx}/hv/download/exportEyeRecordDistribution.json?district='+district;
	}); */
    
    $("#mzData").click(function(){
    	$("#mzData").prop("value", "下载中");
		$("#mzData").prop("disabled", true);
		 var district = $("#district").val();
	     $.ajax({
	      	url:"${ctx}/hv/download/exportEyeRecordDistribution.json",
	      	type:"POST",
	      	data : {
				"district" : district
			},
	      	success:function(msg){
	      		$("#mzData").prop("value", "下载检索数据");
	    		$("#mzData").prop("disabled", false);
	      		if (msg.data == 'error') {
	      			alert("暂无相应数据");
	      		} else {
	      			window.open(msg.data);
	      		}
	      	}
	    }); 
	});
</script>
</body>
</html>
