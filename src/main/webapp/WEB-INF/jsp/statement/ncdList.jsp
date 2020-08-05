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
	    	<form action="${ctx }/ncd/index.htm" method="get">
				<input id="txtBeginDate" name="checkDate" style="width:52%;padding:7px 10px;border:1px solid #ccc;margin-right:10px;" value="${checkDate }"/>	
				<input type="submit" class="btn btn1" href="javascript:void(0);" name="查询"/>
			</form>
		</div>
		<div style="clear:both"></div>
        <div class="table-responsive">
			<table class="table table-striped">
				<tr>
					<td colSpan="21" class="tdTitle">慢病统计</td>
				</tr>
				<tr>
					<th>统计项目</th>
					<th colspan="3">登记糖尿病</th>
					<th colspan="3">疑似糖尿病前期</th>
					<th colspan="3">疑似糖尿病</th>
					<th colspan="3">糖尿病高危</th>
					<th colspan="3">肥胖人群</th>
					<th colspan="3">新发现的高血压</th>
					<th colspan="3">登记的高血压</th>
				</tr>
				<tr>
					<td></td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
					<td>日更新</td>
					<td>累计总人数</td>
					<td>筛查占比</td>
				</tr>
				<c:forEach items="${results}" var="result" >
					<tr>
						<td>${result.place }</td>
						
						<td>${result.rdDayNum }</td>
						<td>${result.rdNum }</td>
						<td>${result.rdPerc }%</td>
						
						<td>${result.rsDayNum }</td>
						<td>${result.rsNum }</td>
						<td>${result.rsPerc }%</td>
						
						<td>${result.rsdDayNum }</td>
						<td>${result.rsdNum }</td>
						<td>${result.rsdPerc }%</td>
						
						<td>${result.rhrDayNum }</td>
						<td>${result.hrNum }</td>
						<td>${result.hrPerc }%</td>
						
						<%-- <td>${result.rhpeDayNum }</td>
						<td>${result.hpNum }</td>
						<td>${result.hpPerc }%</td> --%>
						
						<td>${result.rfDayNum }</td>
						<td>${result.fNum }</td>
						<td>${result.fPerc }%</td>
						
						<td>${result.nhpDayNum }</td>
						<td>${result.nhpNum }</td>
						<td>${result.nhpPerc }%</td>
						
						<td>${result.rhpDayNum }</td>
						<td>${result.rhpNum }</td>
						<td>${result.rhpPerc }%</td>
						
					</tr>
				</c:forEach>
				
				<tr>
					<td>合计</td>
					<td>${totalNum.dDayAll }</td>
					<td>${totalNum.dAll }</td>
					<td>${totalNum.rdPerc }%</td>
					
					<td>${totalNum.spDayAll }</td>
					<td>${totalNum.spAll }</td>
					<td>${totalNum.rsPerc }%</td>
					
					<td>${totalNum.sdgDayAll }</td>
					<td>${totalNum.sdgAll }</td>
					<td>${totalNum.rsdPerc }%</td>
					
					<td>${totalNum.hrDayAll }</td>
					<td>${totalNum.hrAll }</td>
					<td>${totalNum.hrPerc }%</td>
					
					<td>${totalNum.fDayAll }</td>
					<td>${totalNum.fAll }</td>
					<td>${totalNum.fPerc }%</td>
					
					<td>${totalNum.nhpDayAll }</td>
					<td>${totalNum.nhpAll }</td>
					<td>${totalNum.nhpPerc }%</td>
					
					<td>${totalNum.rhpDayAll }</td>
					<td>${totalNum.rhpAll }</td>
					<td>${totalNum.rhpPerc }%</td>
				</tr>
				
			</table>
		</div>
   	</div>
   	
   	<div class="container-fluid">
   		<div id="container" style="min-width:400px;height:400px;width: 60%;float: left;"></div>
   		<div id="weekColumn" style="min-width:400px;height:400px;width: 80%;float: left;"></div>
   	</div>
   <script type="text/javascript">
	$(function(){
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
			url : "${ctx}/ncd/numByDate.json",
			dataType : "json",
			success : function(o) {
				
				$('#container').highcharts({
			        chart: {
			            type: 'column'
			        },
			        title: {
			            text: '慢病统计'
			        },
			        xAxis: {
			            categories: ['登记糖尿病','疑似糖尿病前期','疑似糖尿病','糖尿病高危','肥胖人群','新发现的高血压','登记的高血压'],
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
				
				$('#weekColumn').highcharts({
			        chart: {
			            type: 'column'
			        },
			        title: {
			            text: '慢病统计'
			        },
			        xAxis: {
			            categories: o.dataMap.weekDates,
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
			        series: [{
			            name: '登记糖尿病',
			            data: o.dataMap.dResult
			        },{
			            name: '疑似糖尿病前期',
			            data: o.dataMap.spResult
			        },{
			            name: '疑似糖尿病',
			            data: o.dataMap.sdgResult
			        },{
			            name: '糖尿病高危',
			            data: o.dataMap.hrResult
			        },{
			            name: '肥胖人群',
			            data: o.dataMap.fatResult
			        },{
			            name: '新发现的高血压',
			            data: o.dataMap.nhpResult
			        },{
			            name: '登记的高血压',
			            data: o.dataMap.rhpResult
			        }]
			    });
			}
		});
	});
	
	
	/* $(function () {
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
        $("#txtEndDate").calendar();
    }); */

   </script>
</body>
</html>