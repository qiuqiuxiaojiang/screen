//保存模块方法
function submitFormMobile(that){
	var recordId=$('#recordId').val();
	var subRecordId=$('#subRecordId').val();
	var templateCode=$('#templateCode').val();
	var moduleCode=$(that).attr('collName');
	var moduleHeight=0;
	var options={
		url:'/ehr/healthrecord/saveModuleData.json',
		type:'POST',
		async:false,
		data:{
			'subRecordId':subRecordId,
			'recordId':recordId,
			'templateCode':templateCode
		},
		success:function(data){
			if(data.message=="success"){
				$("#hidDataId").val(data.data);
				alert("保存成功");
			}
		}
	}
	$('#form-'+moduleCode).ajaxForm(options);
}



//保存模块方法
function submitForm(that){
	var recordId=$('#recordId').val();
	var subRecordId=$('#subRecordId').val();
	var templateCode=$('#templateCode').val();
	var moduleCode=$(that).attr('collName');
	var moduleHeight=0;
	var options={
		url:'/ehr/healthrecord/saveModuleData.json',
		type:'POST',
		async:false,
		data:{
			'subRecordId':subRecordId,
			'recordId':recordId,
			'templateCode':templateCode
		},
		success:function(data){
			if(data.message=="success"){
				$("#hidDataId").val(data.data);
				var module=getModule(templateCode,moduleCode);
				if(module.isMultiple){
					loadMultipelModule(module);
				}else{
					loadModule(module);
				}
				alert("保存成功");
			}
		}
	}
	$('#form-'+moduleCode).ajaxForm(options);
}

//获取模块结构	
function getModule(templateCode,collection){
	var module="";
	$.ajax({
		url:'/ehr/healthrecord/queryModuleByCode.json',
		type:'POST',
		async:false,
		data:{
			'templateCode':templateCode,
			'code':collection
		},
		success:function(data){
			module=data.dataMap;
		}
	});
	return module;
}

//获取数据
function getData(collection,id){
	var moduleData="";
	$.ajax({
		url:'/ehr/healthrecord/getModuleDataById/'+collection+'.json',
		type:'POST',
		async:false,
		data:{
			'moduleDataId':id
		},
		success:function(data){
			moduleData=data.dataMap;
		}
	});
	return moduleData;
}

//多值模块中删除操作
function del(mCode,id){
	var templateCode=$('#templateCode').val();
	var dialog=confirm("您确定要删除该条数据吗？");
	if(dialog){
		$.ajax({
			url:'/ehr/healthrecord/delTemplateMultipleListData.json',
			type:'POST',
			aysnc:false,
			data:{
				'collection':mCode,
				'templateCode':templateCode,
				'id':id
			},
			success:function(data){
				if(data.message="success"){
					var module=getModule(templateCode,mCode);
					loadMultipelModule(module);
					alert('删除成功！');
				}
			}
		});
	}
}

//关闭弹出框
function closeDialog(){
	$('#module-dialog').modal('hide');
}



