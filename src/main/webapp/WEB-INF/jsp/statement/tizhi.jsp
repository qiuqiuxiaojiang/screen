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
	<div id="tz" style="height:400px;width:90%;float: left;margin-left:2%"></div>
	<div>
		<table class="table table-striped">
			<tr>
				<td colSpan="10" class="tdTitle">九种体质人群分布</td>
			</tr>
			<tr>
				<th>体质</th>
				<th>A平和质</th>
				<th>B气虚质</th>
				<th>C阳虚质</th>
				<th>D阴虚质</th>
				<th>E痰湿质</th>
				<th>F湿热质</th>
				<th>G血瘀质</th>
				<th>H气郁质</th>
				<th>I特禀质</th>
			</tr>
			<tr>
				<td>人数</td>
				<c:forEach var="data" items="${totalByTz.datas}">
					<td>${data }</td>
				</c:forEach>
				
			</tr>
			<tr>
				<td>比例</td>
				<c:forEach var="p" items="${totalByTz.tzPerc}">
					<td>
						<c:choose>
							<c:when test="${p == null || p == '' || p == 0 }">
							0%
							</c:when>
							<c:otherwise>
							${p }%
							</c:otherwise>
						</c:choose>
					</td>
				</c:forEach>
			</tr>
		</table>
	</div>
	
	<div id="tzByDisease" style="height:400px;width: 90%;float: left;margin-left:2%"></div>
	<div>
			<table class="table table-striped">
				<tr>
					<td colSpan="10" class="tdTitle">不同疾病在不同体质中的发病率</td>
				</tr>
				<tr>
					<th>体质</th>
					<th>A平和质</th>
					<th>B气虚质</th>
					<th>C阳虚质</th>
					<th>D阴虚质</th>
					<th>E痰湿质</th>
					<th>F湿热质</th>
					<th>G血瘀质</th>
					<th>H气郁质</th>
					<th>I特禀质</th>
				</tr>
				<tr>
					<td>糖尿病</td>
					<td>
						<c:if test="${tnbs.平和质!= null}">${tnbs.平和质}%</c:if>
						<c:if test="${tnbs.平和质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.气虚质!= null}">${tnbs.气虚质}%</c:if>
						<c:if test="${tnbs.气虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.阳虚质!= null}">${tnbs.阳虚质}%</c:if>
						<c:if test="${tnbs.阳虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.阴虚质!= null}">${tnbs.阴虚质}%</c:if>
						<c:if test="${tnbs.阴虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.痰湿质!= null}">${tnbs.痰湿质}%</c:if>
						<c:if test="${tnbs.痰湿质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.湿热质!= null}">${tnbs.湿热质}%</c:if>
						<c:if test="${tnbs.湿热质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.血瘀质!= null}">${tnbs.血瘀质}%</c:if>
						<c:if test="${tnbs.血瘀质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.气郁质!= null}">${tnbs.气郁质}%</c:if>
						<c:if test="${tnbs.气郁质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tnbs.特禀质!= null}">${tnbs.特禀质}%</c:if>
						<c:if test="${tnbs.特禀质== null}">0%</c:if>
					</td>
				</tr>
				<tr>
					<td>糖前</td>
					<td>
						<c:if test="${tqs.平和质!= null}">${tqs.平和质}%</c:if>
						<c:if test="${tqs.平和质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.气虚质!= null}">${tqs.气虚质}%</c:if>
						<c:if test="${tqs.气虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.阳虚质!= null}">${tqs.阳虚质}%</c:if>
						<c:if test="${tqs.阳虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.阴虚质!= null}">${tqs.阴虚质}%</c:if>
						<c:if test="${tqs.阴虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.痰湿质!= null}">${tqs.痰湿质}%</c:if>
						<c:if test="${tqs.痰湿质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.湿热质!= null}">${tqs.湿热质}%</c:if>
						<c:if test="${tqs.湿热质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.血瘀质!= null}">${tqs.血瘀质}%</c:if>
						<c:if test="${tqs.血瘀质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.气郁质!= null}">${tqs.气郁质}%</c:if>
						<c:if test="${tqs.气郁质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${tqs.特禀质!= null}">${tqs.特禀质}%</c:if>
						<c:if test="${tqs.特禀质== null}">0%</c:if>
					</td>
				</tr>
				<tr>
					<td>高血压</td>
					<td>
						<c:if test="${gxys.平和质!= null}">${gxys.平和质}%</c:if>
						<c:if test="${gxys.平和质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.气虚质!= null}">${gxys.气虚质}%</c:if>
						<c:if test="${gxys.气虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.阳虚质!= null}">${gxys.阳虚质}%</c:if>
						<c:if test="${gxys.阳虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.阴虚质!= null}">${gxys.阴虚质}%</c:if>
						<c:if test="${gxys.阴虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.痰湿质!= null}">${gxys.痰湿质}%</c:if>
						<c:if test="${gxys.痰湿质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.湿热质!= null}">${gxys.湿热质}%</c:if>
						<c:if test="${gxys.湿热质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.血瘀质!= null}">${gxys.血瘀质}%</c:if>
						<c:if test="${gxys.血瘀质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.气郁质!= null}">${gxys.气郁质}%</c:if>
						<c:if test="${gxys.气郁质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${gxys.特禀质!= null}">${gxys.特禀质}%</c:if>
						<c:if test="${gxys.特禀质== null}">0%</c:if>
					</td>
				</tr>
				
				<tr>
					<td>肥胖</td>
					<td>
						<c:if test="${fps.平和质!= null}">${fps.平和质}%</c:if>
						<c:if test="${fps.平和质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.气虚质!= null}">${fps.气虚质}%</c:if>
						<c:if test="${fps.气虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.阳虚质!= null}">${fps.阳虚质}%</c:if>
						<c:if test="${fps.阳虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.阴虚质!= null}">${fps.阴虚质}%</c:if>
						<c:if test="${fps.阴虚质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.痰湿质!= null}">${fps.痰湿质}%</c:if>
						<c:if test="${fps.痰湿质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.湿热质!= null}">${fps.湿热质}%</c:if>
						<c:if test="${fps.湿热质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.血瘀质!= null}">${fps.血瘀质}%</c:if>
						<c:if test="${fps.血瘀质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.气郁质!= null}">${fps.气郁质}%</c:if>
						<c:if test="${fps.气郁质== null}">0%</c:if>
					</td>
					<td>
						<c:if test="${fps.特禀质!= null}">${fps.特禀质}%</c:if>
						<c:if test="${fps.特禀质== null}">0%</c:if>
					</td>
				</tr>
	
				<tr>
					<td>总人数</td>
					<td>
						<c:if test="${totalMap.平和质  == null}">0</c:if>
						<c:if test="${totalMap.平和质 != null}">${totalMap.平和质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.气虚质  == null}">0</c:if>
						<c:if test="${totalMap.气虚质 != null}">${totalMap.气虚质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.阳虚质  == null}">0</c:if>
						<c:if test="${totalMap.阳虚质 != null}">${totalMap.阳虚质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.阴虚质  == null}">0</c:if>
						<c:if test="${totalMap.阴虚质 != null}">${totalMap.阴虚质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.痰湿质  == null}">0</c:if>
						<c:if test="${totalMap.痰湿质 != null}">${totalMap.痰湿质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.湿热质  == null}">0</c:if>
						<c:if test="${totalMap.湿热质 != null}">${totalMap.湿热质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.血瘀质  == null}">0</c:if>
						<c:if test="${totalMap.血瘀质 != null}">${totalMap.血瘀质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.气郁质  == null}">0</c:if>
						<c:if test="${totalMap.气郁质  != null}">${totalMap.气郁质}</c:if>
					</td>
					<td>
						<c:if test="${totalMap.特禀质  == null}">0</c:if>
						<c:if test="${totalMap.特禀质  != null}">${totalMap.特禀质 }</c:if>
					</td>
				</tr>
			</table>
			
			
			
		</div>
	<div class="container-fluid">
		<div id="tnb" style="height:400px;width: 50%;float: left;"></div>
		<div id="tq" style="height:400px;width: 48%;float: left;margin-left:2%"></div>
		<div id="gxy" style="height:400px;width: 50%;float: left;"></div>
		<div id="fp" style="height:400px;width: 48%;float: left;margin-left:2%"></div>
   	</div>
</div>
</body>

<script type="text/javascript">
$(function () {
	var chart = null;
	$.ajax({
		url : "${ctx}/tz/tz.json",
		dataType : "json",
		success : function(o) {
		 	$('#tz').highcharts({
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
		            text: '体质对比统计'
		        },
		        xAxis: {
		            categories: o.dataMap.names,
		            crosshair: true,
		            gridLineColor: null
		        },
		        yAxis: {
		            min: 0,
		            minRange: 1,
		            title: {
		                text: ''
		            },
		            labels: {
		                format: '{value}'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		            '<td style="padding:0"><b>{point.y:.0f}</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        legend: {
		            enabled: false
		        },
		        plotOptions: {
		            column: {
		                depth: 25,
		                dataLabels:{
		                	enabled:true,
		                    format: '{y}'
		                }
		            }
		        },
		        series: [{
		            name: '累计人数',
		            data: o.dataMap.datas
		        }]
		        
		      
		    });
		 	
		 	
		 	
		 	
		 	
			 
			 <!--九种体重不同疾病的患病人数-->
			 $('#tzByDisease').highcharts({
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
			            text: '九种体质中不同疾病的患病人数'
			        },
			        xAxis: {
			            categories: o.dataMap.tzs,
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
			            name: '糖尿病',
			            data: o.dataMap.tnb,
			            color:'rgb(124, 181, 236)'
			        },{
			            name: '糖前',
			            data: o.dataMap.tq,
			            color:'rgb(144, 237, 125)'
			        },{
			            name: '高血压',
			            data: o.dataMap.gxy,
			            color:'#9767d4'
			        },{
			            name: '肥胖',
			            data: o.dataMap.fp,
			            color:'rgb(241, 92, 128)'
			        }]
			    });
			 <!--九种体重不同疾病的患病人数-->
		}
	}); 
	
	$.ajax({
		url : "${ctx}/tz/tzByDisease.json",
		dataType : "json",
		success : function(o) {
			$('#tnb').highcharts({
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
		            text: '糖尿病'
		        },
		        xAxis: {
		            categories: o.dataMap.tnbs.diseases,
		            crosshair: true,
		            gridLineColor: null
		        },
		        yAxis: {
		            min: 0,
		            minRange: 1,
		            title: {
		                text: ''
		            },
		            labels: {
		                format: '{value} %'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		            '<td style="padding:0"><b>{point.y:.1f} %</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        legend: {
		            enabled: false
		        },
		        plotOptions: {
		            column: {
		                depth: 25,
		                dataLabels:{
		                	enabled:true,
		                    format: '{y} %'
		                }
		            }
		        },
		        series: [{
		            name: '糖尿病',
		            data: o.dataMap.tnbs.disPerc,
		            color:'rgb(124, 181, 236)'
		        }]
		    });
			
			$('#tq').highcharts({
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
		            text: '糖前'
		        },
		        xAxis: {
		            categories: o.dataMap.tqs.diseases,
		            crosshair: true,
		            gridLineColor: null
		        },
		        yAxis: {
		            min: 0,
		            minRange: 1,
		            title: {
		                text: ''
		            },
		            labels: {
		                format: '{value} %'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		            '<td style="padding:0"><b>{point.y:.1f} %</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        legend: {
		            enabled: false
		        },
		        plotOptions: {
		            column: {
		                depth: 25,
		                dataLabels:{
		                	enabled:true,
		                    format: '{y} %'
		                }
		            }
		        },
		        series: [{
		            name: '糖前',
		            data: o.dataMap.tqs.disPerc,
		            color:'rgb(144, 237, 125)'
		        }]
		        
		      
		    });
			
			$('#gxy').highcharts({
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
		            text: '高血压'
		        },
		        xAxis: {
		            categories: o.dataMap.gxys.diseases,
		            crosshair: true,
		            gridLineColor: null
		        },
		        yAxis: {
		            min: 0,
		            minRange: 1,
		            title: {
		                text: ''
		            },
		            labels: {
		                format: '{value} %'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		            '<td style="padding:0"><b>{point.y:.1f} %</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        legend: {
		            enabled: false
		        },
		        plotOptions: {
		            column: {
		                depth: 25,
		                dataLabels:{
		                	enabled:true,
		                    format: '{y} %'
		                }
		            }
		        },
		        series: [{
		            name: '高血压',
		            data: o.dataMap.gxys.disPerc,
		            color:'#9767d4'
		        }]
		    });
			
			$('#fp').highcharts({
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
		            text: '肥胖'
		        },
		        legend: {
		            enabled: false
		        },
		        xAxis: {
		            categories: o.dataMap.fps.diseases,
		            crosshair: true,
		            gridLineColor: null
		        },
		        yAxis: {
		            min: 0,
		            minRange: 1,
		            title: {
		                text: ''
		            },
		            labels: {
		                format: '{value} %'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		            '<td style="padding:0"><b>{point.y:.1f} %</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        credits: {//去掉版权信息
		            enabled: false
		        },
		        
		        plotOptions: {
		            column: {
		                depth: 25,
		                dataLabels:{
		                	enabled:true,
		                    format: '{y} %'
		                }
		            }
		        },
		        series: [{
		            name: '肥胖',
		            data: o.dataMap.fps.disPerc,
		            color:'rgb(241, 92, 128)'
		        }]
		    });
		}
	}); 
	
});
</script>
</html>