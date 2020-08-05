echo 测试一体机上传数据
REM java -jar runmock.jar host function idCode measureTime height weight temperature waistline hipline oxygen bmp highPressure lowPressure
REM 参数示例：host: http://localhost:8080/screen/
REM         function: bodyData
REM         idCode，身份证号: 110110111111111111
REM         measureTime，检测时间："2019-05-23 01:01:01"
REM         height，身高:183
REM         weight，体重:65
REM         temperature，体温:38
REM         waistline，腰围:80
REM         hipline，臀围:100
REM         oxygen，血氧: 95
REM         bmp，脉搏: 69
REM         highPressure，收缩压：120
REM         lowPressure,舒张压：80 
java -jar runmock.jar http://localhost:8080/screen/ bodyData 120223197806260165 "2019-05-23 14:01:01" null 53 37 80 100 95 60 120 80