echo 测试体质测评相关数据上传
REM java -jar runmock.jar host tizhi idCode answerStr physiqueResult
REM 参数示例：host: http://114.116.74.14:8080/screening
REM         function: tizhi
REM         idCode，身份证号:411122199209068279
REM         answerStr，选中选项（第一个数字为1代表第一道题选中第一个选项，第二的数字为2代表第二题选中第二个选项，共60个数字）: 1,2,1,3,1,3,2,3,3,3,2,2,2,2,2,3,3,3,3,3,3,3,4,4,4,4,3,3,3,3,3,3,2,2,2,2,2,3,3,3,3,3,2,2,2,2,2,2,3,3,3,3,3,3,2,2,2,2,2,3
REM         physiqueResult，体质测评结果：平和质
java -jar runmock.jar http://114.116.74.14:8080/screening tizhi 411122199209068279 1,2,1,3,1,3,2,3,3,3,2,2,2,2,2,3,3,3,3,3,3,3,4,4,4,4,3,3,3,3,3,3,2,2,2,2,2,3,3,3,3,3,2,2,2,2,2,2,3,3,3,3,3,3,2,2,2,2,2,3 平和质