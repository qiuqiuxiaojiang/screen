<?xml version="1.0" encoding="UTF-8"?>   
<document margin="45,45,40,20">
<#assign colorWords = "0,0,0,1">
<#assign colorNoWords = "0,0,0,0">
<#assign colorBoldWords = "0.95,0.95,0.95,1">
<#assign color1 = "0.01,0.41,0.96,0">
<#assign color2 = "0.01,0.91,0.99,0">
<#assign colorArrowRed = "212,22,22">
<#assign colorArrowBlue = "37,73,215">
<chapter name="健康筛查问卷及知情同意书" id="projectScreening" fontName="simsun" fontSize="1" color="255,255,255" spacingAfter="1" >
	<text indent= "0" align="center" fontName="simsun" fontSize="15" spacing="1" style="bold">
		健康筛查问卷及知情同意书
	</text>
	<table  cols="105,200,105,200" borderColor="${colorWords}" spacing="-5">
		<row>
			<cell padding="4" align="left"  fontName="simsun" fontSize="10" color="${colorWords}">姓名</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" >${name}</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">性别</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">${gender}</cell>
		</row>
		<row>
			<cell padding="4" align="left"  fontName="simsun" fontSize="10" color="${colorWords}">民族</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" >
			${nation}
			
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if nationName?? && nationName?length != 0>
						£   
					<#else>
						R
					</#if>
					</text>
					<text fontSize="10" fontName="simsun" color="${colorWords}" spacing="10">汉族  </text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontSize="10" fontName="simsun" color="${colorWords}">其他
					<#if nationName?? && nationName?length != 0>
						${nationName}
					<#else>
						____
					</#if></text>
				</complex>
			</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">身份证号</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">${customerId}</cell>
		</row>
		<row>
			<cell padding="4" align="left"  fontName="simsun" fontSize="10" color="${colorWords}">居住地</cell>
			<cell align="left" fontSize="10" color="${colorWords}" >
				<complex>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if placeRresidence?? && placeRresidence?contains('农村')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontSize="10" fontName="simsun" color="${colorWords}">农村  </text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text padding="4" fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if placeRresidence?? && placeRresidence?contains('城镇')>
					R
				<#else>
					£
				</#if></text>
				<text fontSize="10" fontName="simsun" color="${colorWords}">城镇</text>  
				</complex>
			</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">出生地</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">
				<complex>
				<text fontName="simsun" fontSize="10" color="${colorWords}">
				<#if province?? && province?length != 0>
					${province}
				<#else>
					_______
				</#if>省
				<#if city?? && city?length != 0>
					${city}
				<#else>
					_______
				</#if>市
				<#if area?? && area?length != 0>
					${area}
				<#else>
					_______
				</#if>区/县
				</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell padding="4" align="left"  fontName="simsun" fontSize="10" color="${colorWords}">文化程度</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" colspan="3">
			<complex>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if degreeEducation?? && degreeEducation?contains('小学/以下')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">小学/以下</text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if degreeEducation?? && degreeEducation?contains('初中')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">初中</text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if degreeEducation?? && degreeEducation?contains('高中、中专')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">高中、中专</text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if degreeEducation?? && degreeEducation?contains('大专/大学')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">大专/大学</text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if degreeEducation?? && degreeEducation?contains('硕士/博士')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">硕士/博士</text>
			</complex>
			</cell>
		</row>
		<row>
			<cell padding="4" align="left"  fontName="simsun" fontSize="10" color="${colorWords}">现住址</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" colspan="3">
			<complex>
				<text fontName="simsun" fontSize="10" color="${colorWords}">
				<#if provinceNow?? && provinceNow?length != 0>
					${provinceNow}
				<#else>
					_______
				</#if>省
				<#if cityNow?? && cityNow?length != 0>
					${cityNow}
				<#else>
					_______
				</#if>市
				<#if areaNow?? && areaNow?length != 0>
					${areaNow}
				<#else>
					_______
				</#if>区/县
				<#if streetName?? && streetName?length != 0>
					${streetName}
				<#else>
					_______
				</#if>街道/路/村
				</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell padding="4" align="left"  fontName="simsun" fontSize="10" color="${colorWords}">联系电话1</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" >${mobile}</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">联系电话2</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">${mobile2}</cell>
		</row>
		<row>
			<cell border="13" align="left" fontName="simsun" fontSize="10" color="${colorWords}" colspan="4" >
			<text fontName="simsun" fontSize="10" color="${colorWords}">去年您全家（一年有6个月以上住在一起的成员）一年的总收入：</text>
			</cell>
		</row>
		<row>
			<cell border="14" padding="4" align="left" fontName="simsun" fontSize="10" color="${colorWords}" colspan="4">
				<complex>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if income?? && income?contains('不足3万')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">不足3万</text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">  
				<#if income?? && income?contains('3-5万')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">3-5万 </text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if income?? && income?contains('5-10万')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">5-10万</text><text wordSpace="5" color="${colorNoWords}">1</text> 
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if income?? && income?contains('10-20万')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">10-20万</text><text wordSpace="5" color="${colorNoWords}">1</text>
				<text fontName="wingdng2" fontSize="10" color="${colorWords}">
				<#if income?? && income?contains('20万以上')>
					R
				<#else>
					£
				</#if>
				</text>
				<text fontName="simsun" fontSize="10" color="${colorWords}">20万以上</text>
			</complex>
			</cell>
		</row>
	</table>
	<text wordSpace="5" color="${colorNoWords}">1</text>
	<table cols="20,175,260" borderColor="0,1,1,1" spacing="15" padding="50">
		<header>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}"></cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}"></cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}"></cell>
		</header>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">1</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" >您目前在服用以下药物吗？（可多选）</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if takingDrugsIds?? && takingDrugsIds?contains('降压药')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">降压药</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if takingDrugsIds?? && takingDrugsIds?contains('降脂药')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">降脂药</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if takingDrugsIds?? && takingDrugsIds?contains('降血糖药')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">降血糖药</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if takingDrugsIds?? && takingDrugsIds?contains('止痛药')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">止痛药</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if takingDrugsIds?? && takingDrugsIds?contains('抗生素')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">抗生素</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if takingDrugsIds?? && takingDrugsIds?contains('无')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">无</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">2</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您本人是否确诊患以下病症？（在二级及以上医院确诊，可多选）</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" isNew="yes" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}" leading="20">
					<#if beIllIds?? && beIllIds?contains('高血压')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">高血压</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('高血脂')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">高血脂</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('冠心病')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">冠心病</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('糖尿病')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">糖尿病</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('心肌梗塞')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">心肌梗塞</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('中风')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">中风</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('肺气肿')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肺气肿</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('肺结核')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肺结核</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('哮喘')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">哮喘</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('胆结石')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">胆结石</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('慢性肝炎')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">慢性肝炎</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('肾炎')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肾炎</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('甲亢/甲低')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">甲亢/甲低</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('肿瘤')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肿瘤 </text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if beIllIds?? && beIllIds?contains('无')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">无</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">3</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您的父母或兄弟姐妹是否确诊患以下病症？（在二级及以上医院确诊，可多选）</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
			<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('高血压')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">高血压</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('高血脂')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">高血脂</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('冠心病')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">冠心病</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('糖尿病')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">糖尿病</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('心肌梗塞')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">心肌梗塞</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('中风')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">中风</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('肺气肿')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肺气肿</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('肺结核')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肺结核</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('哮喘')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">哮喘</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('胆结石')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">胆结石</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('慢性肝炎')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">慢性肝炎</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('肾炎')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肾炎</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('甲亢/甲低')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">甲亢/甲低</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('肿瘤')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">肿瘤</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if relativesBeIllIds?? && relativesBeIllIds?contains('无')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">无</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="6">4</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="2">您目前是否吸烟？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4" border="13">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if smoke?? && smoke?contains('吸烟（每天>5支）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">吸烟（每天＞5支）</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if smoke?? && smoke?contains('偶尔吸（每天1-5支）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">偶尔吸（每天1-5支）</text><text wordSpace="5" color="${colorNoWords}">1</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4"  border="14">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if smoke?? && smoke?contains('基本不吸烟（一天<1支）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">基本不吸烟（一天＜1支）</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">如已戒烟，戒烟前的情况</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if quitSmoke?? && quitSmoke?contains('每天>5支')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每天＞5支</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if quitSmoke?? && quitSmoke?contains('每天1-5支')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每天1-5支</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="2" >您有没有吸入别人吸烟产生的烟雾（被动吸烟）？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" border="13" padding="4">
				<complex>
					<text fontName="simsun" fontSize="10" color="${colorWords}">平均每周被动吸烟：</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeWeek?? && passiveSmokeWeek?contains('<1天')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＜1天</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeWeek?? && passiveSmokeWeek?contains('1-2天')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">1-2天</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeWeek?? && passiveSmokeWeek?contains('3-5天')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">3-5天</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeWeek?? && passiveSmokeWeek?contains('≥6天')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">≥6天</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" border="14" padding="4">
			<complex>
					<text fontName="simsun" fontSize="10" color="${colorWords}">平均每天被动吸烟时长：</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeDay?? && passiveSmokeDay?contains('<30分钟')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＜30分钟</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeDay?? && passiveSmokeDay?contains('30-60分钟')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">30-60分钟</text><text wordSpace="4" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if passiveSmokeDay?? && passiveSmokeDay?contains('>60分钟')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＞60分钟</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您的烟龄（包括被动吸烟）为</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if smokeLength?? && smokeLength?contains('>10年')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＞10年</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if smokeLength?? && smokeLength?contains('5-10年')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">5-10年</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if smokeLength?? && smokeLength?contains('<5年')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＜5年</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="4">5</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="2">您目前是否喝酒？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4" border="13">
			<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drink?? && drink?contains('喝酒（每周7次以上）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">喝酒（每周7次以上）</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drink?? && drink?contains('偶尔喝（每周2-7次）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">偶尔喝（每周2-7次）</text><text wordSpace="5" color="${colorNoWords}">1</text>
				</complex>
			</cell>
		</row>
		
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4" border="14">
			<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drink?? && drink?contains('基本不喝酒（每周<=1次）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">基本不喝酒（每周＜=1次）</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">如已戒酒，戒酒前的情况</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if quitDrink?? && quitDrink?contains('每周>7次')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每周＞7次</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if quitDrink?? && quitDrink?contains('每周2-7次')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每周2-7次</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您的酒龄为</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkLength?? && drinkLength?contains('>10年')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＞10年</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkLength?? && drinkLength?contains('5-10年')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">5-10年</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkLength?? && drinkLength?contains('<5年')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">＜5年</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">6</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">最近一年，您在家用餐的次数？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if haveMealsFamily?? && haveMealsFamily?contains('几乎每天')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">几乎每天</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if haveMealsFamily?? && haveMealsFamily?contains('每周1-2次')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每周1-2次</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if haveMealsFamily?? && haveMealsFamily?contains('每月1-2次 ')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每月1-2次 </text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if haveMealsFamily?? && haveMealsFamily?contains('极少')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">极少 </text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">7</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您经常失眠吗？每天夜间睡眠时间如何？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if insomnia?? && insomnia?contains('经常失眠<5小时')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">经常失眠＜5小时</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if insomnia?? && insomnia?contains('偶尔失眠5-8小时')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">偶尔失眠5-8小时</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if insomnia?? && insomnia?contains('不失眠>8小时 ')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">不失眠＞8小时 </text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="2">8</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" rowspan="2">最近一年，您是否经常锻炼身体？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4" border="13">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if physicalExercise?? && physicalExercise?contains('≥5次/周')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">≥5次/周</text><text wordSpace="10" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if physicalExercise?? && physicalExercise?contains('2-4次/周')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">2-4次/周</text><text wordSpace="10" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if physicalExercise?? && physicalExercise?contains('1次/周 ')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">1次/周 </text><text wordSpace="10" color="${colorNoWords}">1</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4"  border="14">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if physicalExercise?? && physicalExercise?contains('无（平均每月少于3次）')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">无（平均每月少于3次）</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">9</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您对自己现在的居住环境是否满意？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if livingEnvironment?? && !livingEnvironment?contains('基本满意') && !livingEnvironment?contains('不满意')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">满意</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if livingEnvironment?? && livingEnvironment?contains('基本满意')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">基本满意</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if livingEnvironment?? && livingEnvironment?contains('不满意 ')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">不满意 </text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">10</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您最近3个月内经常倒班或夜班吗？（一周2次以上）</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if nightShift?? && nightShift?contains('是')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">是</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if nightShift?? && nightShift?contains('否')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">否</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">11</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您喝可乐、雪碧、芬达等碳酸饮料吗？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkRefresh?? && drinkRefresh?contains('不喝或很少喝')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">不喝或很少喝</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkRefresh?? && drinkRefresh?contains('1-2次/周')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">1-2次/周</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkRefresh?? && drinkRefresh?contains('3-6次/周')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">3-6次/周</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if drinkRefresh?? && drinkRefresh?contains('≥7次/周')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">≥7次/周</text>
				</complex>
			</cell>
		</row>
		<row>
			<cell align="center" fontName="simsun" fontSize="10" color="${colorWords}">12</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}">您吃蔬菜水果的频率？</cell>
			<cell align="left" fontName="simsun" fontSize="10" color="${colorWords}" padding="4">
				<complex leading="20">
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if eatVegetables?? && !eatVegetables?contains('非每天吃')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">每天吃</text><text wordSpace="5" color="${colorNoWords}">1</text>
					<text fontName="wingdng2" fontSize="10" color="${colorWords}">
					<#if eatVegetables?? && eatVegetables?contains('非每天吃')>
						R
					<#else>
						£
					</#if>
					</text>
					<text fontName="simsun" fontSize="10" color="${colorWords}">非每天吃</text>
				</complex>
			</cell>
		</row>
	</table>
	<text fontName="simsun" indent="0" fontSize="11" color="${colorBoldWords}" style="bold">
	知情同意：
	</text>
	<text fontName="simsun" fontSize="11" color="${colorBoldWords}" style="bold">
	本人已了解此次活动的意义，知晓活动的具体内容和要求，项目所开展的检测内容可用于科研，但不可用于疾病诊断，本人自愿参加本项目开展的体检。
	</text>
	
	<text align="right" fontName="simsun" fontSize="12" color="${colorBoldWords}" leading="20">
	签   字：____________________
	</text>
</chapter>
	<header >
	    <headeritem align="left" x="56" y="796" ><image width="98" height="40"></image></headeritem>
	</header>
</document>