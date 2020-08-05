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
    <style>

        @media screen and (max-width: 1500px) {
            .start {
                right: 35.49%!important;
            }
            #divDate{
                right: 36.19%!important;
            }
        }
        @media screen and (max-width: 1200px) {
            .start {
                right: 38.49%!important;
            }
            #divDate{
                right: 39.4%!important;
            }
        }
        @media screen and (max-width: 1050px) {
            .start {
                right: 43.49%!important;
            }
            #divDate{
                right: 44.4%!important;
            }
        }

        .list {
            min-height: 2em;
            width: 80%;
            margin: 10% 10% 0 13%
        }
        .list a{
        margin-right:5%
        }

        .list p {
            float: left;
            margin-left: 5%;
        }

        .btn {
            display: block;
            background: #86e273;
            width: 5%;
            height: 30px;
            position: absolute;
            text-align: center;
            line-height: 30px;
            text-decoration: none;
            top: 7.8%;
            right: 5%;
            border-radius: 5px;
            color: #ffffff;
            padding:0;

        }

        .start {
            position: absolute;
            top: 7.8%;
            right: 31.49%;
        }

        .end {
            position: absolute;
            top: 7.8%;
            right: 12%;
        }

    </style>
</head>
<body>
	<label class="start">开始日期：
	    <input id="txtBeginDate" name="startTime" style="width:170px;padding:7px 10px;border:1px solid #ccc;margin-right:10px;"/>
	</label>
	<label class="end">结束日期：
	   <input id="txtEndDate" name="endTime" style="width:170px;padding:7px 10px;border:1px solid #ccc;"/>
	</label>
	<a class="btn" href="javascript:void(0);" onclick="loadData('search')">查询</a>
	
	<div class="list">
	<div style="margin-left:5%;margin-bottom:2%">
	    <a href="javascript:void(0);" id="currentMonth" onclick="loadData('currentMonth')">近一个月</a>&nbsp;&nbsp;&nbsp;
	
	    <a href="javascript:void(0);" id="cQuarter" onclick="loadData('cQuarter')">近三个月</a>&nbsp;&nbsp;&nbsp;
	
	    <a href="javascript:void(0);" id="cYear" onclick="loadData('cYear')">近一年</a>&nbsp;&nbsp;&nbsp;
	</div>
	    <p>一体机 <span id="hcCount"></span>个</p>
	
	    <p>居家检 <span id="bsCount"></span>个</p>
	
	    <p>目诊 <span id="recordCount"></span>个</p>
	</div>
	
	
	<div id="container" style="width: 80%;margin:5% 13%;height:400px"></div>
	
	
	<script src="${ctx_static}/calendar/lyz.calendar.min.js" type="text/javascript"></script>
	<script src="${ctx_static}/highcharts/highcharts.js" type="text/javascript"></script>
	<script>
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
	        $("#txtEndDate").calendar();
	    });
	
	    
	    loadData('');
	    function loadData(type) {
	    	//alert(type);
	    	var startTime = $("#txtBeginDate").val();
	    	var endTime = $("#txtEndDate").val();
	    	//alert(startTime + "," + endTime);
	    	$.ajax({
	    		url : "${ctx}/manage/statement/getData.json",
	    		dataType : "json",
	    		data : {"type" : type, "startTime" : startTime, "endTime" : endTime},
	    		success : function(o) {
	    			var days = o.dataMap.days;
	    			var hcCount = o.dataMap.hcCount;
	    			var bsCount = o.dataMap.bsCount;
	    			var recordCount = o.dataMap.recordCount;
	    			$("#hcCount").text(hcCount);
	    			$("#bsCount").text(bsCount);
	    			$("#recordCount").text(recordCount);
	    			
	    			$('#container').highcharts({
	    		        chart: {
	    		            type: 'column'
	    		        },
	    		        title: {
	    		            text: '检测人数'
	    		        },
	    		        xAxis: {
	    		            categories: days,
	    		            crosshair: true
	    		        },
	    		        credits: {//去掉版权信息
	    		            enabled: false
	    		        },
	    		        yAxis: {
	    		            min: 0,
	    		            allowDecimals:false,//y轴只能为整数
	    		            title: {
	    		                text: '检测人数（个）'
	    		            }
	    		        },
	    		        tooltip: {
	    		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
	    		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
	    		                    '<td style="padding:0"><b>{point.y:.1f} 人</b></td></tr>',
	    		            footerFormat: '</table>',
	    		            shared: true,
	    		            useHTML: true
	    		        },
	    		        plotOptions: {
	    		            column: {
	    		                pointPadding: 0.2,
	    		                borderWidth: 0
	    		            }
	    		        },
	    		        series: [
	    		            {
	    		                name: '一体机',
	    		                data: o.dataMap.hcs,
	    		                color:'#7cb5ec'
	    		                
	    		            },
	    		            {
	    		                name: '居家检',
	    		                data: o.dataMap.bss,
	    		                color:'#f7a35c'
	    		            },
	    		            {
	    		                name: '目诊',
	    		                data: o.dataMap.records,
	    		                color:'#90ed7d'
	    		            }
	    		        ]
	    		    });
	    		}
	    	});
	    }
	</script>
</body>
</html>