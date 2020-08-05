<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../main.jsp"%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="Cache-Control" content="no-store">
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
            <a href="${ctx }/manage/hv/csIndex.htm?district=${districtHc}"><li class="activeLi">建档情况</li></a>
            <a href="${ctx }/manage/hv/xtIndex.htm?district=${districtHc}"><li>血糖筛查情况</li></a>
            <a href="${ctx }/manage/hv/xyIndex.htm?district=${districtHc}"><li>血压筛查情况</li></a>
            <a href="${ctx }/manage/hv/xzIndex.htm?district=${districtHc}"><li>血脂筛查情况</li></a>
           <%--  <a href="${ctx }/manage/hv/gxbIndex.htm?district=${districtHc}"><li>冠心病筛查情况</li></a> --%>
            <a href="${ctx }/manage/hv/fpIndex.htm?district=${districtHc}"><li>肥胖筛查情况</li></a>
            <a href="${ctx }/manage/hv/tzIndex.htm?district=${districtHc}"><li>体质筛查情况</li></a>
            <a href="${ctx }/manage/hv/mzIndex.htm?district=${districtHc}"><li>目诊筛查情况</li></a>
        </ul>
    </div>
    <input type="hidden" id="district" value="${districtHc }">
	<div class="total" id="jdCount"></div>
    <div class="chartDiv">
        <h1>建档人群年龄分布</h1>
        <table class="table" id="tableAge">
        </table>
        <div id="jd_age"></div>
       <!--  <div id="jdAgeData" style="text-align: center;"><a href="javascript:void(0);">下载数据</a></div> -->
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="jdAgeData"/>
        </div>
    </div>
    <div class="chartDiv">
        <h1>建档人群性别分布</h1>
        <table class="table" id="tableGender">
        </table>
        <div id="jd_sex"></div>
        <!-- <div id="jdSexData" style="text-align: center;"><a href="javascript:void(0);">下载数据</a></div> -->
        <div style="text-align: center;">
        	<input type="button" class="btn btn-success btn-sm" value="下载数据" id="jdSexData"/>
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
		$.ajax({
			url : "${ctx}/manage/hv/findRecordConditionByAge.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.list;
				var countList = o.dataMap.countList;
				var percList = o.dataMap.percList;
				var count = o.dataMap.count;
				var ageList = o.dataMap.ageList;
				
				var count1 = 0;
				var percCount = 0;
				
				$("#jdCount").text("建档人数：" + count + "人");
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				for (var i = 0; i < ageList.length; i ++) {
					if (i == 0 || (i == ageList.length-1)){
						html += "<td>" + ageList[i] + "</td>"; 
					} else {
						html += "<td>" + ageList[i] + "</td>"; 
					}
				}
				html += "<td>合计</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>人数</td>";
				for (var i = 0; i < countList.length; i ++) {
			        html += "<td>" + countList[i] + "</td>"; 
			        count1 += countList[i];
				}
				html += "<td>" + count1 + "</td>"; 
				html += "</tr>";
				
				html += "<tr>";
				html += "<td>比例</td>";
				for (var i = 0; i < percList.length; i ++) {
			        html += "<td>" + percList[i] + "%</td>"; 
			        percCount += percList[i];
				}
				html += "<td>" + percCount.toFixed(0) + "%</td>"; 
				html += "</tr>";
				
				$("#tableAge").html(html); 
				
			    var jd_age = new G2.Chart({
			        container: 'jd_age',
			        forceFit: true,
			        height: 350,
			    });
			    jd_age.source(data);
			    jd_age.scale('count', {
			        alias: '人数 (人) ',
			        //tickInterval: 10,
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
			    jd_age.interval().position('age*count').color('#55BAE4').opacity(1).label('count', {
			        useHtml: true,
			        htmlTemplate: function htmlTemplate(text, item) {
			            var a = item.point;
			            return '<span class="g2-label-item"><p class="g2-label-item-value">' + a.count + '</p></div>';
			        }
			    });
			    jd_age.render();
			}
		});
		
		
		$.ajax({
			url : "${ctx}/manage/hv/findRecordConditionByGender.json",
			dataType : "json",
			data : {
				"district" : district
			},
			success : function(o) {
				var data = o.dataMap.result;
				var countList = o.dataMap.countList;
				var percList = o.dataMap.percList;
				
				var count = 0;
				var percCount = 0;
				
				var html = "";
				html += "<tr>";
				html += "<td></td>";
				html += "<td>男</td>";
				html += "<td>女</td>";
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
				
				$("#tableGender").html(html); 
				
			    var jd_sex = new G2.Chart({
			        container: 'jd_sex',
			        forceFit: true,
			        height: 350,
			    });
			    jd_sex.source(data, {
			        percent: {
			            formatter: function formatter(val) {
			                val = val + '%';
			                return val;
			            }
			        }
			    });
			    jd_sex.coord('theta');
			    jd_sex.tooltip({
			        showTitle: false
			    });
			    jd_sex.intervalStack().position('percent').color('item').label('percent', {
			        offset: -50,
			        textStyle: {
			            textAlign: 'center',
			            shadowBlur: 2,
			            shadowColor: 'rgba(0, 0, 0, .45)'
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
			    jd_sex.render();
			}
		});
		
		/* $("#jdAgeData").click(function(){
		     window.location.href = '${ctx}/hv/download/exportRecordConditionByAge.json?district='+district;
		});
		
		$("#jdSexData").click(function(){
		    window.location.href = '${ctx}/hv/download/exportRecordConditionByGender.json?district='+district;
		}); */
		
		
		$("#jdAgeData").click(function(){
		     $.ajax({
			      	url:"${ctx}/hv/download/exportRecordConditionByAge.json",
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
		
		$("#jdSexData").click(function(){
		     $.ajax({
		      	url:"${ctx}/hv/download/exportRecordConditionByGender.json",
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
