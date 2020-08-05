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
	<script src="${ctx_static}/highcharts/highcharts-3d.js" type="text/javascript"></script>
	<%-- <script src="${ctx_static}/highcharts/exporting.js" type="text/javascript"></script> --%>
	<script src="${ctx_static}/highcharts/highcharts-zh_CN.js" type="text/javascript"></script>
	<style>
		.table{
			    width: 80%;
			    margin-left:5%;
			    margin-top:3%
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
			line-height:1.5em;
			font-size:1.1em;
		}
		.tdTitle{
			background-color: rgba(124, 181, 236, 0.42); 
    		font-weight: bold;
		}
		.right-side >.content{
			    background: #fff;
		}
	</style>
</head>
<body>
<div class="container-fluid">
	<div class="container-fluid">
		<div id="diseasePie" style="height:400px;width: 90%;float: left;margin-left:2%"></div>
		<div id="diseaseByAge" style="height:400px;width: 90%;float: left;"></div>
   	</div>
</div>
</body>

<script type="text/javascript">
$(function () {
	$.ajax({
		url : "${ctx}/diseaseAge/diseaseScale.json",
		dataType : "json",
		success : function(o) {
			$('#diseasePie').highcharts({
		        chart: {
		            plotBackgroundColor: null,
		            plotBorderWidth: null,
		            plotShadow: false
		        },
		        title: {
		            text: '糖尿病筛查情况'
		        },
		        tooltip: {
		            headerFormat: '{series.name}<br>',
		            pointFormat: '{point.name}: <b>{point.percentage:.1f}%</b>'
		        },
		        plotOptions: {
		            pie: {
		                allowPointSelect: true,
		                cursor: 'pointer',
		               	dataLabels: {
		                    enabled: true
		                },
		                showInLegend: true,
		                shadow: false
		            }
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        series: [{
		            type: 'pie',
		            name: '糖尿病筛查情况占比',
		            data: o.dataList,
		            dataLabels: {
		                formatter: function () {
		                    // 大于1则显示
		                    return this.y > 1 ? '<b>' + this.point.name + ':</b> ' + this.y + '%'  : null;
		                }
		            }
		        }]
		    });
		}
	}); 
	
	
	$.ajax({
		url : "${ctx}/diseaseAge/diseaseByAge.json",
		dataType : "json",
		success : function(o) {
			$('#diseaseByAge').highcharts({
		        chart: {
		            type: 'column',
		            margin: 75,
		            options3d: {
		                enabled: true,
		                alpha: 10,
		                beta: 0,
		                depth: 70
		            }
		        },
		        title: {
		            text: '血糖情况年龄分布'
		        },
		        xAxis: {
		            categories: o.dataMap.ageList,
		            crosshair: true,
		            gridLineColor: null
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: '(单位：人)'
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
		                depth: 25
		            }
		        },
		        /* plotOptions: {
		            column: {
		                pointPadding: 0.3,
		                borderWidth: 0
		            }
		        }, */
		        series: [{
	            	 name: '糖前',
		             data: o.dataMap.preDiabeteList,
		             color:'rgb(144, 237, 125)'
		        },{
		        	name: '糖尿病',
		            data: o.dataMap.diabeteList,
		            color:'rgb(124, 181, 236)'
		        },{
		            name: '正常人群',
		            data: o.dataMap.normalList,
		            color:'#9767d4'
		        }]
		    });
		}
	}); 
	
});
</script>
</html>