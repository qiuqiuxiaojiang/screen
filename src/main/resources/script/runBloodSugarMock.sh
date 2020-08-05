echo 测试一体机上传数据
# java -jar runmock.jar host function idCode measureTime height weight temperature waistline hipline oxygen bmp highPressure lowPressure
# 参数示例：host: http://localhost:8080/screen/
#         function: bodyData
#         idCode，身份证号: 110110111111111111
#         measureTime，检测时间："2019-05-23 01:01:01"
#         height，身高:183
#         weight，体重:65
#         temperature，体温:38
#         waistline，腰围:80
#         hipline，臀围:100
#         oxygen，血氧: 95
#         bmp，脉搏: 69
#         highPressure，收缩压：120
#         lowPressure,舒张压：80 
java -jar runmock.jar http://localhost:8080/screen/ bodyData 120223197806260165 "2019-05-23 14:01:01" null 53 37 80 100 95 60 120 80
