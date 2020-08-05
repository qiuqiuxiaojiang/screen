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
            <a href="${ctx }/manage/hv/fpIndex.htm?district=${districtHc}"><li class="activeLi">肥胖筛查情况</li></a>
            <a href="${ctx }/manage/hv/tzIndex.htm?district=${districtHc}"><li>体质筛查情况</li></a>
            <a href="${ctx }/manage/hv/mzIndex.htm?district=${districtHc}"><li>目诊筛查情况</li></a>
        </ul>
    </div>
	<input type="hidden" id="district" value="${districtHc }">
	<div class="chartDiv">
        <h1>肥胖情况人群分布</h1>
        <table class="table" id="table1">
        </table>
        <div id="fp_groups"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="fpGroupData"/>
        </div>
    </div>
    <div class="chartDiv">
        <h1>肥胖指数年龄分布</h1>
        <table class="table" id="tableAge">
        </table>
        <div id="fp_age"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="fpAgeData" style="margin-top:35px;"/>
        </div>
    </div>
     <div class="chartDiv">
        <h1>肥胖情况性别分布</h1>
        <table class="table" id="tableGender">
        </table>
        <div id="fp_sex"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="fpGenderData" style="margin-top:35px;"/>
        </div>
    </div>
    <div class="chartDiv">
        <h1>肥胖情况体质分布</h1>
        <table class="table" id="tableTizhi">
        </table>
        <div id="fp_physique"></div>
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="fpTizhiData" style="margin-top:35px;"/>
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
		/*体质指数人群分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findFatPeopleDistribution.json",
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
				html += "<td>肥胖人群</td>";
				html += "<td>超重人群</td>";
				html += "<td>BMI正常人群</td>";
				html += "<td>偏瘦人群</td>";
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
				
			    //var sum = 500;
			    var ds = new DataSet();
			    var dv = ds.createView().source(data);
			    /* dv.transform({
			        type: 'map',
			        callback: function callback(row) {
			            row.value = parseInt(sum * row.percent);
			            return row;
			        }
			    }); */
			    var xt_groups = new G2.Chart({
			        container: 'fp_groups',
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
			    xt_groups.intervalStack().position('percent').color('item', ['#FF7439','#FED563',  '#42D2A1', '#42A0D3']).opacity(1).label('percent', {
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
			   /*  xt_groups.on('interval:mouseenter', function(ev) {
			        var data = ev.data._origin;
			        $(".g2-guide-html").css('opacity', 1);
			        $(".g2-guide-html .title").text(data.item);
			        $(".g2-guide-html .value").text(data.value);
			    });

			    xt_groups.on('interval:mouseleave', function() {
			        $(".g2-guide-html .title").text('项目总计');
			        $(".g2-guide-html .value").text(500);
			    }); */
			    xt_groups.render();
			}
		});
		
		/*肥胖情况年龄分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findFatPeopleDistributionByAgeHistogram.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var data1 = o.dataMap.obesity;
				var data2 = o.dataMap.overweight;
				var data3 = o.dataMap.normal;
				var data4 = o.dataMap.thin;
				var ageList = o.dataMap.ageList;
				
				var dataCount1 = 0;
				var dataCount2 = 0;
				var dataCount3 = 0;
				var dataCount4 = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				for (var i = 0; i < ageList.length; i ++) {
			        html += "<td>" + ageList[i] + "</td>"; 
				}
				html += "<td>合计</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>肥胖人群（人）</td>";
				for (var i = 0; i < data1.length; i ++) {
			        html += "<td>" + data1[i] + "</td>"; 
			        dataCount1 += data1[i];
				}
				html += "<td>" + dataCount1 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>超重人群（人）</td>";
				for (var i = 0; i < data2.length; i ++) {
			        html += "<td>" + data2[i] + "</td>"; 
			        dataCount2 += data2[i];
				}
				html += "<td>" + dataCount2 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>BMI正常人群（人）</td>";
				for (var i = 0; i < data3.length; i ++) {
			        html += "<td>" + data3[i] + "</td>"; 
			        dataCount3 += data3[i];
				}
				html += "<td>" + dataCount3 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>偏瘦人群（人）</td>";
				for (var i = 0; i < data4.length; i ++) {
			        html += "<td>" + data4[i] + "</td>"; 
			        dataCount4 += data4[i];
				}
				html += "<td>" + dataCount4 + "</td>"; 
				html += "</tr>";
				
				$("#tableAge").html(html);
				
				 var fp_age = new G2.Chart({
				        container: 'fp_age',
				        forceFit: true,
				        height: 400,
				        padding: 'auto'
				    });
				 fp_age.source(data);
				 fp_age.scale('count', {
				        alias: '人数（人）',
				        //max: 75,
				        min: 0,
				        tickCount: 4
				    });
				 fp_age.axis('type', {
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

				 fp_age.axis('count', {
				        label: {
				            textStyle: {
				                fill: '#aaaaaa'
				            }
				        },
				        title: {
				            offset: 50
				        }
				    });
				 fp_age.legend({
				        position: 'bottom-center'
				    });
				 fp_age.interval().position('type*count').color('disease', ['#FF7439','#FED563',  '#42D2A1', '#42A0D3']).opacity(1).adjust([{
				        type: 'dodge',
				        marginRatio: 1 / 32,
				        useHtml: true,
				        htmlTemplate: function htmlTemplate(text, item) {
				            var a = item.point;
				            return '<span class="g2-label-item"><p class="g2-label-item-value">' + a.count + '</p></div>';
				            }
				    }]);
				 fp_age.render();
			}
		});
		
		
		/*肥胖情况性别分布*/
		$.ajax({
			url : "${ctx}/manage/hv/findFatPeopleDistributionByGender.json",
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
				html += "<td>肥胖人群</td>";
				html += "<td>超重人群</td>";
				html += "<td>BMI正常人群</td>";
				html += "<td>偏瘦人群</td>";
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
				
				var fp_sex = new G2.Chart({
			        container: 'fp_sex',
			        forceFit: true,
			        height: 400,
			        padding: 'auto'
			    });
				fp_sex.source(data);
			    fp_sex.scale('count', {
			        alias: '人数（人）',
			        //max: 75,
			        min: 0,
			        tickCount: 4
			    });
			    fp_sex.axis('type', {
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

			    fp_sex.axis('count', {
			        label: {
			            textStyle: {
			                fill: '#aaaaaa'
			            }
			        },
			        title: {
			            offset: 50
			        }
			    });
			    fp_sex.legend({
			        position: 'bottom-center'
			    });
			    fp_sex.interval().position('type*count').color('gender', ['#FF7439','#FED563']).opacity(1).adjust([{
			        type: 'dodge',
			        marginRatio: 1 / 32
			    }]);
			    fp_sex.render();
			}
		}); 
		
		/*肥胖情况体质分布 */
		$.ajax({
			url : "${ctx}/manage/hv/findFatPeopleDistributionByTizhi.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var fps = o.dataMap.fps;
				var czs = o.dataMap.czs;
				var zcs = o.dataMap.zcs;
				var pss = o.dataMap.pss;
				
				var fpCount = 0;
				var czCount = 0;
				var zcCount = 0;
				var psCount = 0;
				
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
				html += "<td>肥胖</td>";
				for (var i = 0; i < fps.length; i ++) {
			        html += "<td>" + fps[i] + "</td>"; 
			        fpCount += fps[i];
				}
				html += "<td>" + fpCount + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>超重人群</td>";
				for (var i = 0; i < czs.length; i ++) {
			        html += "<td>" + czs[i] + "</td>"; 
			        czCount += czs[i];
				}
				html += "<td>" + czCount + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>BMI正常人群</td>";
				for (var i = 0; i < zcs.length; i ++) {
			        html += "<td>" + zcs[i] + "</td>"; 
			        zcCount += zcs[i];
				}
				html += "<td>" + zcCount + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>偏瘦人群</td>";
				for (var i = 0; i < pss.length; i ++) {
			        html += "<td>" + pss[i] + "</td>"; 
			        psCount += pss[i];
				}
				html += "<td>" + psCount + "</td>";
				html += "</tr>";
				
				$("#tableTizhi").html(html);
				
				var fp_physique = new G2.Chart({
			        container: 'fp_physique',
			        forceFit: true,
			        height: 400,
			        padding: 'auto'
			    });
				fp_physique.source(data);
				fp_physique.scale('count', {
			        alias: '人数（人）',
			        //max: 75,
			        min: 0,
			        tickCount: 4
			    });
				fp_physique.axis('tizhi', {
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

				fp_physique.axis('count', {
			        label: {
			            textStyle: {
			                fill: '#aaaaaa'
			            }
			        },
			        title: {
			            offset: 50
			        }
			    });
				fp_physique.legend({
			        position: 'bottom-center'
			    });
				fp_physique.interval().position('tizhi*count').color('disease', ['#FF7439','#FED563','#42D2A1']).opacity(1).adjust([{
			        type: 'dodge',
			        marginRatio: 1 / 32
			    }]);
				fp_physique.render();
			}
		});
		
		/* $("#fpGroupData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportFatPeopleDistribution.json?district='+district;
		});
		
		$("#fpAgeData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportFatPeopleDistributionByAge2.json?district='+district;
		});
		
		$("#fpGenderData").click(function(){
		    window.location.href = '${ctx}/hv/download/exportFatPeopleDistributionByGender.json?district='+district;
		});
		
		$("#fpTizhiData").click(function(){
		    window.location.href = '${ctx}/hv/download/findFatPeopleDistributionByTizhi.json?district='+district;
		}); */
		
		$("#fpGroupData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportFatPeopleDistribution.json",
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
		
		$("#fpAgeData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportFatPeopleDistributionByAge2.json",
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
		
		$("#fpGenderData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportFatPeopleDistributionByGender.json",
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
		
		$("#fpTizhiData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/findFatPeopleDistributionByTizhi.json",
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
