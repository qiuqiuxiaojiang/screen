echo 测试社区筛查app上传血糖血脂数据
REM java -jar runmock.jar host bloodSugar userName password customerId checkTime tc tg hdl ldl bloodGlucose bloodGlucose2h bloodGlucoseRandom
REM 参数示例：host: http://114.116.74.14:8080/screening
REM         function: bloodSugar
REM         userName，用户名: shenchuang
REM         password，密码：123456
REM         customerId，身份证号:411122199209068279
REM         checkTime，筛查时间:"2019-05-23 01:01:01"
REM         tc，总胆固醇:4
REM         tg，甘油三酯:5
REM         hdl，高密度脂蛋白:6
REM         ldl，低密度脂蛋白: 7
REM         bloodGlucose，空腹血糖: 8
REM         bloodGlucose2h，餐后2h血糖: 8
REM         bloodGlucoseRandom，随机血糖: 8
java -jar runmock.jar http://114.116.74.14:8080/screening bloodSugar shenchuang 123456 411122199209068279 2019-05-23 01:01:01 4 5 6 7 null null 8