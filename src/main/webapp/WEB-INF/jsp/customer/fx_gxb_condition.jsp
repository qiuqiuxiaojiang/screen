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
            <a href="${ctx }/manage/hv/csIndex.htm"><li>建档情况</li></a>
            <a href="${ctx }/manage/hv/xtIndex.htm"><li>血糖筛查情况</li></a>
            <a href="${ctx }/manage/hv/xyIndex.htm"><li>血压筛查情况</li></a>
            <a href="${ctx }/manage/hv/xzIndex.htm"><li>血脂筛查情况</li></a>
           <%--  <a href="${ctx }/manage/hv/gxbIndex.htm"><li class="activeLi">冠心病筛查情况</li></a> --%>
            <a href="${ctx }/manage/hv/fpIndex.htm"><li>肥胖筛查情况</li></a>
            <a href="${ctx }/manage/hv/tzIndex.htm"><li>体质筛查情况</li></a>
            <a href="${ctx }/manage/hv/mzIndex.htm"><li>目诊筛查情况</li></a>
        </ul>
    </div>

    <div class="chartDiv">
        <h1>各年龄段冠心病分布</h1>
        <table class="table" id="tableAge">
        </table>
        <div id="xt_age"></div>
    </div>
    <div class="chartDiv">
        <h1>各性别冠心病分布</h1>
        <table class="table" id="tableGender">
        </table>
        <div id="xt_sex"></div>
    </div>
    <div class="chartDiv">
        <h1>各体质冠心病分布</h1>
        <table class="table" id="tableTizhi">
        </table>
        <div id="xt_physique"></div>
    </div>
</div>
<script src="${ctx_static }/healthcheck/js/g2.min.js"></script>
<script src="${ctx_static }/healthcheck/js/data-set.min.js"></script>
<script src="${ctx_static }/healthcheck/js/jquery-1.11.2.js"></script>
<script>
	$(function () {
		$.ajaxSetup({ cache: false });
		
		var district = $("#district").val();
		/*各年龄段冠心病分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findCoronaryHeartDiseaseByAge.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var data1 = o.dataMap.cpd;
				var data2 = o.dataMap.noCpd;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>35-39岁</td>";
				html += "<td>40-44岁</td>";
				html += "<td>45-49岁</td>";
				html += "<td>50-54岁</td>";
				html += "<td>55-59岁</td>";
				html += "<td>60-65岁</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>冠心病（人）</td>";
				for (var i = 0; i < data1.length; i ++) {
			        html += "<td>" + data1[i] + "</td>"; 
				}
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>非冠心病（人）</td>";
				for (var i = 0; i < data2.length; i ++) {
			        html += "<td>" + data2[i] + "</td>"; 
				}
				html += "</tr>";
				
				$("#tableAge").html(html);
				
				 var xt_age = new G2.Chart({
				        container: 'xt_age',
				        forceFit: true,
				        height: 400,
				        padding: 'auto'
				    });
				    xt_age.source(data);
				    xt_age.scale('value', {
				        alias: '人数（人）',
				        max: 75,
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

				    xt_age.axis('value', {
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
		
		/*各性别冠心病分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findCoronaryHeartDiseaseByGender.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var maleList = o.dataMap.maleList;
				var femaleList = o.dataMap.femaleList;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>冠心病</td>";
				html += "<td>非冠心病</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>男（人）</td>";
				for (var i = 0; i < maleList.length; i ++) {
			        html += "<td>" + maleList[i] + "</td>"; 
				}
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>女（人）</td>";
				for (var i = 0; i < femaleList.length; i ++) {
			        html += "<td>" + femaleList[i] + "</td>"; 
				}
				html += "</tr>";
				
				$("#tableGender").html(html);
				
				var xt_sex = new G2.Chart({
			        container: 'xt_sex',
			        forceFit: true,
			        height: 400,
			        padding: 'auto'
			    });
			    xt_sex.source(data);
			    xt_sex.scale('value', {
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
			    });
			    xt_sex.legend({
			        position: 'bottom-center'
			    });
			    xt_sex.interval().position('type*count').color('gender', ['#FF7439','#FED563']).opacity(1).adjust([{
			        type: 'dodge',
			        marginRatio: 1 / 32
			    }]);
			    xt_sex.render();
			}
		});
		
		
		/*各体质冠心病分布 */
		$.ajax({
			url : "${ctx}/manage/hv/findCoronaryHeartDiseaseByTizhi.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var data1 = o.dataMap.cpd;
				var data2 = o.dataMap.noCpd;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>平和质</td>";
				html += "<td>气虚质</td>";
				html += "<td>阳虚质</td>";
				html += "<td>阴虚质</td>";
				html += "<td>痰湿质</td>";
				html += "<td>湿热质</td>";
				html += "<td>血瘀质</td>";
				html += "<td>气郁质</td>";
				html += "<td>特禀质</td>";
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>冠心病</td>";
				for (var i = 0; i < data1.length; i ++) {
			        html += "<td>" + data1[i] + "</td>"; 
				}
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>非冠心病</td>";
				for (var i = 0; i < data2.length; i ++) {
			        html += "<td>" + data2[i] + "</td>"; 
				}
				html += "</tr>";
				
				$("#tableTizhi").html(html);
				
				var xt_physique = new G2.Chart({
			        container: 'xt_physique',
			        forceFit: true,
			        height: 400,
			        padding: 'auto'
			    });
			    xt_physique.source(data);
			    xt_physique.scale('value', {
			        alias: '人数（人）',
			        max: 75,
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

			    xt_physique.axis('value', {
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
	});
</script>
</body>
</html>
