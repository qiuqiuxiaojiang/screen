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
            <a href="${ctx }/manage/hv/xtHealthDetailIndex.htm?district=${districtHc}"><li>血糖筛查情况</li></a>
            <a href="${ctx }/manage/hv/xyHealthDetailIndex.htm?district=${districtHc}"><li>血压筛查情况</li></a>
            <a href="${ctx }/manage/hv/xzHealthDetailIndex.htm?district=${districtHc}"><li class="activeLi">血脂筛查情况</li></a>
            <a href="${ctx }/manage/hv/tizhiHealthDetailIndex.htm?district=${districtHc}"><li>体质筛查情况</li></a>
        </ul>
    </div>
    
	<input type="hidden" id="district" value="${districtHc}">
    <div class="chartDiv">
        <h1>血脂情况人群分布</h1>
        <table class="table" id="table1">
        </table>
        <div id="xt_groups"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="xzGroupData"/>
        </div>
    </div>
    <div class="chartDiv">
        <h1>血脂情况年龄分布</h1>
        <table class="table" id="tableAge">
        </table>
        <div id="xt_age"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="xzAgeData" style="margin-top:35px;"/>
        </div>
    </div>
</div>
<script src="${ctx_static }/healthcheck/js/g2.min.js"></script>
<script src="${ctx_static }/healthcheck/js/data-set.min.js"></script>
<script src="${ctx_static }/healthcheck/js/jquery-1.11.2.js"></script>
<script>
	$(function () {
		$.ajaxSetup({ cache: false });
		var district = $("#district").val();
		/*血脂情况人群分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findBloodLipidConditionPeopleDistributionHealthCheck.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var countList = o.dataMap.countList;
				var percList = o.dataMap.percList;
				
				var count = 0;
				var percCount = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				/* html += "<td>已登记血脂异常患者</td>"; */
				html += "<td>新发现血脂异常患者</td>";
				html += "<td>血脂正常人群</td>";
				html += "<td>合计</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>人数</td>";
				for (var i = 0; i < countList.length; i ++) {
			        html += "<td>" + countList[i] + "</td>";
			        count += countList[i];
				}
				html += "<td>" + count + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>比例</td>";
				for (var i = 0; i < percList.length; i ++) {
			        html += "<td>" + percList[i] + "%</td>"; 
			        percCount += percList[i];
				}
				html += "<td>" + percCount.toFixed(0) + "%</td>";
				html += "</tr>";
				
				$("#table1").html(html);
				
				var sum = 500;
			    var ds = new DataSet();
			    var dv = ds.createView().source(data);
			    dv.transform({
			        type: 'map',
			        callback: function callback(row) {
			            row.value = parseInt(sum * row.percent);
			            return row;
			        }
			    });
			    var xt_groups = new G2.Chart({
			        container: 'xt_groups',
			        forceFit: true,
			        height: 350,
			        padding: 'auto'
			    });
			    xt_groups.source(dv);
			    xt_groups.legend({
			        position: 'left-center',
			        offsetX: 50
			    });
			    xt_groups.coord('theta', {
			        radius: 0.75,
			        innerRadius: 0.6
			    });
			    xt_groups.tooltip({
			    	  showTitle: false,
			    	  showMarkers: false,
			    	  itemTpl: '<li class="g2-tooltip-list-item"><span style="background-color:{color};" class="g2-tooltip-marker"></span>{name}: {value}</li>',
			    });
			    xt_groups.intervalStack().position('percent').color('item', ['#FF7439','#FED563','#42D2A1']).opacity(1).label('percent', {
			        offset: -16,
			        textStyle: {
			            fill: 'white',
			            fontSize: 12,
			            shadowBlur: 2,
			            shadowColor: 'rgba(0, 0, 0, .45)'
			        },
			        rotate: 0,
			        autoRotate: false,
			        formatter: function formatter(text, item) {
			            return String(item.point.percent) + '%';
			        }
			    }).tooltip('item*percent', function(item, percent) {
				        percent = percent + '%';
				        return {
				            name: item,
				            value: percent
				        };
				    }).style({
				        lineWidth: 1,
				        stroke: '#fff'
				    });
			    xt_groups.on('interval:mouseenter', function(ev) {
			        var data = ev.data._origin;
			        $(".g2-guide-html").css('opacity', 1);
			        $(".g2-guide-html .title").text(data.item);
			        $(".g2-guide-html .value").text(data.value);
			    });

			    xt_groups.on('interval:mouseleave', function() {
			        $(".g2-guide-html .title").text('项目总计');
			        $(".g2-guide-html .value").text(500);
			    });
			    xt_groups.render();
			}
		});
		
		
		/*血脂情况年龄分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findBloodLipidConditionAgePeopleDistributionHealthCheck.json",
			data : {
				"district" : district
			},
			dataType : "json",
			success : function(o) {
				var data = o.dataMap.list;
				//var data1 = o.dataMap.highLipid;
				var data2 = o.dataMap.newHighLipid;
				var data3 = o.dataMap.normal;
				var ages = o.dataMap.ages;
				
				//var dataCount1 = 0;
				var dataCount2 = 0;
				var dataCount3 = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				for (var i = 0; i < ages.length; i ++) {
			        html += "<td>" + ages[i] + "</td>"; 
				}
				html += "<td>合计</td>";
				html += "</tr>";
				
				/* html += "<tr>";
				html += "<td>已登记血脂异常患者（人）</td>";
				for (var i = 0; i < data1.length; i ++) {
			        html += "<td>" + data1[i] + "</td>"; 
			        dataCount1 += data1[i];
				}
				html += "<td>" + dataCount1 + "</td>";
				html += "</tr>"; */
				
				html += "<tr>";
				html += "<td>新发现血脂异常患者（人）</td>";
				for (var i = 0; i < data2.length; i ++) {
			        html += "<td>" + data2[i] + "</td>"; 
			        dataCount2 += data2[i];
				}
				html += "<td>" + dataCount2 + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>血脂正常人群（人）</td>";
				for (var i = 0; i < data3.length; i ++) {
			        html += "<td>" + data3[i] + "</td>"; 
			        dataCount3 += data3[i];
				}
				html += "<td>" + dataCount3 + "</td>"; 
				html += "</tr>";
				
				$("#tableAge").html(html);
				
				 var xt_age = new G2.Chart({
				        container: 'xt_age',
				        forceFit: true,
				        height: 400,
				        padding: 'auto'
				    });
				    xt_age.source(data);
				    xt_age.scale('count', {
				        alias: '人数（人）',
				       // max: 75,
				        min: 0,
				        tickCount: 4
				    }); 
				    xt_age.axis('type', {
				        label: {
				            textStyle: {
				                fill: '#aaaaaa'
				            }
				        },
				        tickLine: {
				            alignWithLabel: false,
				            length: 0
				        }
				    });

				    xt_age.axis('count', {
				        label: {
				            textStyle: {
				                fill: '#aaaaaa'
				            }
				        },
				        title: {
				            offset: 50
				        }
				    });
				    xt_age.legend({
				        position: 'bottom-center'
				    });
				    xt_age.interval().position('type*count').color('disease', ['#FF7439','#FED563',  '#42D2A1', '#42A0D3']).opacity(1).adjust([{
				        type: 'dodge',
				        marginRatio: 1 / 32,
				        useHtml: true,
				        htmlTemplate: function htmlTemplate(text, item) {
				            var a = item.point;
				            return '<span class="g2-label-item"><p class="g2-label-item-value">' + a.count + '</p></div>';
				            }
				    }]);
				    xt_age.render();
			}
		});
		
		
		/* $("#xzGroupData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportloodLipidConditionPeopleDistributionHealthCheck.json?district='+district;

		});
		
		$("#xzAgeData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportBloodLipidConditionAgePeopleDistributionHealthCheck.json?district='+district;

		}); */
		
		$("#xzGroupData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportloodLipidConditionPeopleDistributionHealthCheck.json",
		      	type:"POST",
		      	data : {
					"district" : district
				},
		      	success:function(msg){
		      		if (msg.data == 'error') {
		      			alert("暂无相应数据");
		      		} else {
		      			window.open(msg.data);
		      		}
		      	}
		    }); 
		});
		
		$("#xzAgeData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportBloodLipidConditionAgePeopleDistributionHealthCheck.json",
		      	type:"POST",
		      	data : {
					"district" : district
				},
		      	success:function(msg){
		      		if (msg.data == 'error') {
		      			alert("暂无相应数据");
		      		} else {
		      			window.open(msg.data);
		      		}
		      	}
		    }); 
		});
	});
</script>
</body>
</html>
