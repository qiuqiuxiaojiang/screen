<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>重庆两江项目</title>
    <link rel="stylesheet" type="text/css" href="${ctx_static}/lightbox/lightbox.css"></link>
 	<link rel="stylesheet" type="text/css" href="${ctx_static}/zyUpload/zyUpload.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/css/component.css"></link>
	<link rel="stylesheet" type="text/css" href="${ctx_static}/pagenation/pagenation.css"></link>
    <link href="${ctx_static}/css/lyz.calendar.css" rel="stylesheet" type="text/css"/>
	<script src="${ctx_static}/calendar/lyz.calendar.min.js" type="text/javascript"></script>
	<script src="${ctx_static}/highcharts/highcharts.js" type="text/javascript"></script>
	<style>
		.table{
			    width: 100%;
		}
		.table>thead{
			background:#E5EDFA;
		}
		.table-striped>tbody>tr:nth-of-type(odd) {
		    background-color:rgba(229, 237, 250, 0.63);
		}
		.table>tbody>tr>td, .table>tbody>tr>th, .table>tfoot>tr>td, .table>tfoot>tr>th, .table>thead>tr>td, .table>thead>tr>th{
			text-align:center;
			height:1.5em;
			vertical-align: middle;
			font-size:1.1em;
			border-right: 1px #eee solid;
		}
		.tdTitle{
			background-color: rgba(124, 181, 236, 0.42); 
    		font-weight: bold;
		}
		.right-side >.content{
			    background: #fff;
		}
		.btn1 {
            display: block;
		    background: #86e273;
		    width: 21%;
		    height: 34px;
		    position: absolute;
		    text-align: center;
		    line-height: 34px;
		    text-decoration: none;
		    top: -2%;
    		right: 22%;
		    border-radius: 5px;
		    color: #ffffff;
		    padding: 0;
        }
        #divDate {
		    top: 11%;
    		right: 242px;
   		}
	</style>
</head>
<body>
<div class="container-fluid">
		<div style="width: 400px;margin: 1% 0;float: right;position: relative;">
			<form action="${ctx }/process/index.htm" method="get">
				<input id="txtBeginDate" name="checkDate" style="width:52%;padding:7px 10px;border:1px solid #ccc;margin-right:10px;" value="${checkDate }"/>	
				<input type="submit" class="btn btn1" href="javascript:void(0);" name="查询"/>
			</form>
		</div>
		<div style="clear:both"></div>
	<!-- 以下这是页面需要编辑的区域 -->
		<div class="table-responsive">
			<table class="table table-striped">
				<tr>
					<td colSpan="21" class="tdTitle">筛查进度统计</td>
				</tr>
				<tr>
					<th>统计项目</th>
					<th colspan="2">建档人数</th>
					<th colspan="2">有效建档</th>
					<th colspan="2">血糖检测</th>
					<th colspan="2">体格检测</th>
					<th colspan="2">眼象检测<br/>（已检测）</th>
					<th colspan="2">眼象检测<br/>（已上传报告）</th>
					<th colspan="2">中医体质辨识</th>
					<th colspan="2">转诊需求(需要并发症检查人数)</th>
					<th colspan="2">转诊需求(需要检测OGTT人数)</th>
					<th colspan="2">转诊需求(需要转诊人数，需要基因检测人数)</th>
				</tr>
				<tr>
					<td></td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>日更新</td>
					<td>累计总人数</td>
				</tr>
				<c:forEach items="${results}" var="result" >
					<tr>
						<td>${result.place }</td>
						<td>${result.riDayNum }</td>
						<td>${result.infoNum }</td>
						
						<td>${result.reDayNum }</td>
						<td>${result.effectNum }</td>
						
						<td>${result.rbDayNum }</td>
						<td>${result.bloodGlucoseNum }</td>
						
						<td>${result.rexamDayNum }</td>
						<td>${result.examNum }</td>
						
						<td>${result.rcDayNum }</td>
						<td>${result.eyeCheckNum }</td>
						
						<td>${result.ruDayNum }</td>
						<td>${result.eyeUploadNum }</td>
						
						<td>${result.rtDayNum }</td>
						<td>${result.tizhiNum }</td>
						
						<td>${result.rCompDayNum }</td>
						<td>${result.complicateNum }</td>
						
						<td>${result.roDayNum }</td>
						<td>${result.ogttNum }</td>
						
						<td>${result.rTransDayNum }</td>
						<td>${result.transferNum }</td>
						
					</tr>
				</c:forEach>
				
				<tr>
					<td>合计</td>
					<td>${totalNum.infoDayAll }</td>
					<td>${totalNum.infoAll }</td>
					
					<td>${totalNum.effectDayAll }</td>
					<td>${totalNum.effectAll }</td>
					
					<td>${totalNum.bgDayAll }</td>
					<td>${totalNum.bgAll }</td>
					
					<td>${totalNum.eDayAll }</td>
					<td>${totalNum.eAll }</td>
					
					<td>${totalNum.ecDayAll }</td>
					<td>${totalNum.ecAll }</td>
					
					<td>${totalNum.epDayAll }</td>
					<td>${totalNum.epAll }</td>
					
					<td>${totalNum.rtDayAll }</td>
					<td>${totalNum.rtAll }</td>
					
					<td>${totalNum.rcDayAll }</td>
					<td>${totalNum.rcAll }</td>
					
					<td>${totalNum.roDayAll }</td>
					<td>${totalNum.roAll }</td>
					
					<td>${totalNum.rTranDayAll }</td>
					<td>${totalNum.rTranAll }</td>
					
				</tr>
				
			</table>
		</div>
		<!-- 以下这是页面需要编辑的区域 -->
	
	<div id="container" style="height:400px;width: 70%;float: left;"></div>
</div>
</body>

<script type="text/javascript">
$(function () {
	
	$("#txtBeginDate").calendar({
        controlId: "divDate",                                 // 弹出的日期控件ID，默认: $(this).attr("id") + "Calendar"
        speed: 200,                                           // 三种预定速度之一的字符串("slow", "normal", or "fast")或表示动画时长的毫秒数值(如：1000),默认：200
        complement: true,                                     // 是否显示日期或年空白处的前后月的补充,默认：true
        readonly: true,                                       // 目标对象是否设为只读，默认：true
        upperLimit: new Date(),                               // 日期上限，默认：NaN(不限制)
        lowerLimit: new Date("2011/01/01"),                   // 日期下限，默认：NaN(不限制)
        callback: function () {                               // 点击选择日期后的回调函数
            //alert("您选择的日期是：" + $("#txtBeginDate").val());
        }
    });
	
	$.ajax({
		url : "${ctx}/process/totalColumn.json",
		dataType : "json",
		success : function(o) {
			 $('#container').highcharts({
		        chart: {
		            type: 'column'
		        },
		        title: {
		            text: '筛查进度统计'
		        },
		        xAxis: {
		            categories: ['建档人数','有效建档','血糖检测','体格检测','眼象检测（已检测）','眼象检测（已上传报告）','中医体质辨识','转诊需求(需要并发症检查人数)','转诊需求(需要检测OGTT人数)','转诊需求(需要转诊人数，需要基因检测人数)'],
		            crosshair: true
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '总人数 (人)'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		            '<td style="padding:0"><b>{point.y:.0f} 人</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        plotOptions: {
		            column: {
		                pointPadding: 0.3,
		                borderWidth: 0
		            }
		        },
		        series: [ {
		            name: '日更新人数',
		            data: o.dataMap.dayNum,
		            color:'rgb(241, 92, 128)'
		        },{
		            name: '累计人数',
		            data: o.dataMap.allNum,
		            color:'rgb(124, 181, 236)'
		        }]
		    });
		}
		
	});
	
});
</script>
</html>