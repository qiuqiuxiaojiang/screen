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
            <a href="${ctx }/manage/hv/tzIndex.htm?district=${districtHc}"><li class="activeLi">体质筛查情况</li></a>
            <a href="${ctx }/manage/hv/mzIndex.htm?district=${districtHc}"><li>目诊筛查情况</li></a>
        </ul>
    </div>
	<input type="hidden" id="district" value="${districtHc }">
    <div class="chartDiv">
        <h1>社区人群体质分类</h1>
        <table class="table" id="table1">
        </table>
        <div id="xt_groups"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="tzGroupData"/>
        </div>
    </div>
    <div class="chartDiv">
        <h1>体质情况性别分布</h1>
        <table class="table" id="tableGender">
        </table>
        <div id="xt_sex"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="tzGenderData" style="margin-top:35px;"/>
        </div>
    </div>
    <div class="chartDiv">
        <h1>体质与代谢性疾病患病率的关系</h1>
        <table class="table" id="tableTizhi">
        </table>
        <div id="xt_physique"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="tzDiseaseData" style="margin-top:35px;"/>
        </div>
    </div>
   <!--  <div class="chartDiv">
        <h1>各年龄段体质分布</h1>
        <table class="table" id="tableAge">
        </table>
        <div id="xt_age"></div>
    </div> -->
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
			        title: {
			            offset: 70,
			            textStyle: {
			                fill: '#333'
			            }
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
		
		
		/*各年龄段体质分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findTizhiPeopleDistributionByAge.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var data1 = o.dataMap.ph;
				var data2 = o.dataMap.qx;
				var data3 = o.dataMap.yx;
				var data4 = o.dataMap.yxu;
				var data5 = o.dataMap.ts;
				var data6 = o.dataMap.sy;
				var data7 = o.dataMap.xy;
				var data8 = o.dataMap.qy;
				var data9 = o.dataMap.tb;
				
				var dataCount1 = 0;
				var dataCount2 = 0;
				var dataCount3 = 0;
				var dataCount4 = 0;
				var dataCount5 = 0;
				var dataCount6 = 0;
				var dataCount7 = 0;
				var dataCount8 = 0;
				var dataCount9 = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>35-39岁</td>";
				html += "<td>40-44岁</td>";
				html += "<td>45-49岁</td>";
				html += "<td>50-54岁</td>";
				html += "<td>55-59岁</td>";
				html += "<td>60-65岁</td>";
				html += "<td>合计</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>A平和质（人）</td>";
				for (var i = 0; i < data1.length; i ++) {
			        html += "<td>" + data1[i] + "</td>"; 
			        dataCount1 += data1[i];
				}
				html += "<td>" + dataCount1 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>B气虚质（人）</td>";
				for (var i = 0; i < data2.length; i ++) {
			        html += "<td>" + data2[i] + "</td>"; 
			        dataCount2 += data2[i];
				}
				html += "<td>" + dataCount2 + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>C阳虚质（人）</td>";
				for (var i = 0; i < data3.length; i ++) {
			        html += "<td>" + data3[i] + "</td>"; 
			        dataCount3 += data3[i];
				}
				html += "<td>" + dataCount3 + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>D阴虚质（人）</td>";
				for (var i = 0; i < data4.length; i ++) {
			        html += "<td>" + data4[i] + "</td>"; 
			        dataCount4 += data4[i];
				}
				html += "<td>" + dataCount4 + "</td>";
				html += "</tr>";
				
				
				html += "<tr>";
				html += "<td>E痰湿质（人）</td>";
				for (var i = 0; i < data5.length; i ++) {
			        html += "<td>" + data5[i] + "</td>"; 
			        dataCount5 += data5[i];
				}
				html += "<td>" + dataCount5 + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>F湿热质（人）</td>";
				for (var i = 0; i < data6.length; i ++) {
			        html += "<td>" + data6[i] + "</td>";
			        dataCount6 += data6[i];
				}
				html += "<td>" + dataCount6 + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>G血瘀质（人）</td>";
				for (var i = 0; i < data7.length; i ++) {
			        html += "<td>" + data7[i] + "</td>"; 
			        dataCount7 += data7[i];
				}
				html += "<td>" + dataCount7 + "</td>";
				html += "</tr>";
				
				
				html += "<tr>";
				html += "<td>H气郁质（人）</td>";
				for (var i = 0; i < data8.length; i ++) {
			        html += "<td>" + data8[i] + "</td>"; 
			        dataCount8 += data8[i];
				}
				html += "<td>" + dataCount8 + "</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>I特禀质（人）</td>";
				for (var i = 0; i < data9.length; i ++) {
			        html += "<td>" + data9[i] + "</td>"; 
			        dataCount9 += data9[i];
				}
				html += "<td>" + dataCount9 + "</td>";
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
				        //max: 75,
				        min: 0,
				        tickCount: 4
				    });
				    xt_age.axis('age', {
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
				    xt_age.interval().position('age*count').color('tizhi', ['#FF7439','#FED563',  '#42D2A1', '#42A0D3']).opacity(1).adjust([{
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
		
		/*各性别体质分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findTizhiPeopleDistributionByGender.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var maleList = o.dataMap.maleList;
				var femaleList = o.dataMap.femaleList;
				
				var maleCount = 0;
				var femaleCount = 0;
				
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
				html += "<td>男（人）</td>";
				for (var i = 0; i < maleList.length; i ++) {
			        html += "<td>" + maleList[i] + "</td>"; 
			        maleCount += maleList[i];
				}
				html += "<td>" + maleCount + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>女（人）</td>";
				for (var i = 0; i < femaleList.length; i ++) {
			        html += "<td>" + femaleList[i] + "</td>"; 
			        femaleCount += femaleList[i];
				}
				html += "<td>" + femaleCount + "</td>"; 
				html += "</tr>";
				
				$("#tableGender").html(html);
				
				var xt_sex = new G2.Chart({
			        container: 'xt_sex',
			        forceFit: true,
			        height: 400,
			        padding: 'auto'
			    });
			    xt_sex.source(data);
			    
			    xt_sex.scale('count', {
			        alias: '人数（人）',
			       /*  max: 75,
			        min: 0,
			        tickCount: 4 */
			    });
			  
			    xt_sex.axis('count', {
			        title: {
			            offset: 70,
			            textStyle: {
			                fill: '#333'
			            }
			        }
			    });
			   /*  xt_sex.scale('value', {
			        alias: '人数（人）',
			        max: 75,
			        min: 0,
			        tickCount: 4
			    });
			    xt_sex.axis('type', {
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

			    xt_sex.axis('value', {
			        label: {
			            textStyle: {
			                fill: '#aaaaaa'
			            }
			        },
			        title: {
			            offset: 50
			        }
			    }); */
			    xt_sex.legend({
			        position: 'bottom-center'
			    });
			    xt_sex.interval().position('tizhi*count').color('gender', ['#FF7439','#FED563']).opacity(1).adjust([{
			        type: 'dodge',
			        marginRatio: 1 / 32
			    }]);
			    xt_sex.render();
			}
		});
		
		
		/*体质与代谢性疾病患病率的关系 */
		$.ajax({
			url : "${ctx}/manage/hv/findTizhiPeopleDistributionByDisease.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var data1 = o.dataMap.diabetesList;
				var data2 = o.dataMap.bloodPressureList;
				var data3 = o.dataMap.bloodLipidList;
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
			    
			    xt_physique.scale('perc', {
			        alias: '百分比（%）',
			       // max: 75,
			        min: 0,
			        tickCount: 4 
			    });
			  
			    xt_physique.axis('perc', {
			        title: {
			            offset: 70,
			            textStyle: {
			                fill: '#333'
			            }
			        }
			    });
			    
			    xt_physique.tooltip({
			        //showTitle: false,
			        //showCrosshairs: true,
			        crosshairs: {
			            type: 'xy',
			        },
			        itemTpl: '<li class="g2-tooltip-list-item" data-index={index} style="margin-bottom:4px;">'
			            + '<span style="background-color:{color};" class="g2-tooltip-marker"></span>'
			            + '{value}'
			            + '</li>'
			    });
			  
			    xt_physique.legend({
			        position: 'bottom-center'
			    });
			    xt_physique.interval().position('tizhi*perc').color('disease', ['#FF7439','#FED563','#42D2A1', "#42A0D3"]).opacity(1).adjust([{
			        type: 'dodge',
			        marginRatio: 1 / 32
			    }]).tooltip('disease*perc', function( disease, perc) {
		            return {
		                value: disease + '：' + perc+"%"
		            };
		        });
			    xt_physique.render();
			}
		});
		
		/* $("#tzGroupData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportTizhiPeopleDistribution.json?district='+district;
		});
		
		$("#tzGenderData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportTizhiPeopleDistributionByGender.json?district='+district;
		});
		
		$("#tzDiseaseData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportTizhiPeopleDistributionByDisease.json?district='+district;
		}); */
		
		$("#tzGroupData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportTizhiPeopleDistribution.json",
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
		
		$("#tzGenderData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportTizhiPeopleDistributionByGender.json",
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
		
		$("#tzDiseaseData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportTizhiPeopleDistributionByDisease.json",
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
