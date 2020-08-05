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
            <a href="${ctx }/manage/hv/xzHealthDetailIndex.htm?district=${districtHc}"><li>血脂筛查情况</li></a>
            <a href="${ctx }/manage/hv/tizhiHealthDetailIndex.htm?district=${districtHc}"><li class="activeLi">体质筛查情况</li></a>
        </ul>
    </div>
    
    <input type="hidden" id="district" value="${districtHc }">
    <div class="chartDiv">
        <h1>社区人群体质分布</h1>
        <table class="table" id="table1">
        </table>
        <div id="xt_groups"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="tizhiGroupData"/>
        </div>
    </div>
   
    <div class="chartDiv">
        <h1>体质与代谢性疾病患病率的关系</h1>
        <table class="table" id="tableTizhi">
        </table>
        <div id="xt_physique"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="tizhiDiseaseData" style="margin-top:35px;"/>
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
		/*社区人群体质分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findTizhiPeopleDistribution.json",
			dataType : "json",
			data : {
				"district" : district,
				"type" : "js"
			},
			success : function(o) {
				var data = o.dataMap.list;
				var countList = o.dataMap.countList;
				var percList = o.dataMap.percList;
				console.log("countList:" + countList);
				console.log("percList:" + percList);
				
				var count = 0;
				var percCount = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>A平和质</td>";
				html += "<td>B气虚质</td>";
				html += "<td>C阳虚质</td>";
				html += "<td>D阴虚质</td>";
				html += "<td>E痰湿质</td>";
				html += "<td>F湿热质</td>";
				html += "<td>G血瘀质</td>";
				html += "<td>H气郁质</td>";
				html += "<td>I特禀质</td>";
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
				
				var jd_age = new G2.Chart({
			        container: 'xt_groups',
			        forceFit: true,
			        height: 350
			    });
			    jd_age.source(data);
			    jd_age.scale('count', {
			        alias: '人数 (人) '
			    });
			    jd_age.axis('count', {
			    	label: {
			            textStyle: {
			                fill: '#aaaaaa'
			            }
			        },
			        title: {
			            offset: 50
			        }
			    });
			    jd_age.tooltip({
			        share: true
			    });
			    jd_age.interval().position('tizhi*count').color('#55BAE4').opacity(1).label('count', {
			        useHtml: true,
			        htmlTemplate: function htmlTemplate(text, item) {
			            var a = item.point;
			            return '<span class="g2-label-item"><p class="g2-label-item-value">' + a.count + '</p></div>';
			        }
			    });
			    jd_age.render();
			}
		});
		
		
		/*体质与代谢性疾病患病率的关系 */
		$.ajax({
			url : "${ctx}/manage/hv/findTizhiConditionByDiseaseHealthCheckDetail.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var data1 = o.dataMap.diabetesList;
				var data2 = o.dataMap.hypertensionList;
				var data3 = o.dataMap.hplList;
				var data4 = o.dataMap.fatList;
				
				var dataCount1 = 0;
				var dataCount2 = 0;
				var dataCount3 = 0;
				var dataCount4 = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>A平和质</td>";
				html += "<td>B气虚质</td>";
				html += "<td>C阳虚质</td>";
				html += "<td>D阴虚质</td>";
				html += "<td>E痰湿质</td>";
				html += "<td>F湿热质</td>";
				html += "<td>G血瘀质</td>";
				html += "<td>H气郁质</td>";
				html += "<td>I特禀质</td>";
				html += "<td>合计</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>糖尿病</td>";
				for (var i = 0; i < data1.length; i ++) {
			        html += "<td>" + data1[i] + "</td>"; 
			        dataCount1 += data1[i];
				}
				html += "<td>" + dataCount1 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>高血压</td>";
				for (var i = 0; i < data2.length; i ++) {
			        html += "<td>" + data2[i] + "</td>"; 
			        dataCount2 += data2[i];
				}
				html += "<td>" + dataCount2 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>血脂异常患者</td>";
				for (var i = 0; i < data3.length; i ++) {
			        html += "<td>" + data3[i] + "</td>"; 
			        dataCount3 += data3[i];
				}
				html += "<td>" + dataCount3 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>肥胖</td>";
				for (var i = 0; i < data4.length; i ++) {
			        html += "<td>" + data4[i] + "</td>"; 
			        dataCount4 += data4[i];
				}
				html += "<td>" + dataCount4 + "</td>"; 
				html += "</tr>";
				
				$("#tableTizhi").html(html);
				
				var xt_physique = new G2.Chart({
			        container: 'xt_physique',
			        forceFit: true,
			        height: 400,
			        padding: 'auto'
			    });
			    xt_physique.source(data);
			    xt_physique.scale('count', {
			        alias: '人数（人）',
			       // max: 75,
			        min: 0,
			        tickCount: 4
			    });
			    xt_physique.axis('type', {
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

			    xt_physique.axis('count', {
			        label: {
			            textStyle: {
			                fill: '#aaaaaa'
			            }
			        },
			        title: {
			            offset: 50
			        }
			    });
			    xt_physique.legend({
			        position: 'bottom-center'
			    });
			    xt_physique.interval().position('tizhi*count').color('disease', ['#FF7439','#FED563','#42D2A1']).opacity(1).adjust([{
			        type: 'dodge',
			        marginRatio: 1 / 32
			    }]);
			    xt_physique.render();
			}
		});
		
		/* $("#tizhiGroupData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportizhiPeopleDistribution.json?district='+district+"&type=js";
		});
		
		$("#tizhiDiseaseData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportTizhiConditionByDiseaseHealthCheckDetail.json?district='+district;
		}); */
		
		$("#tizhiGroupData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportizhiPeopleDistribution.json",
		      	type:"POST",
		      	data : {
					"district" : district,
					"type" : "js"
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
		
		$("#tizhiDiseaseData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportTizhiConditionByDiseaseHealthCheckDetail.json",
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
