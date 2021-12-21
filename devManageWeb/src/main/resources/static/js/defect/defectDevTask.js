/**
 * Description:  确认缺陷并创建一个工作任务
 * Author:liushan
 * Date: 2019/1/10 下午 5:10
 */
var _icon_word ="../static/images/devtask/word.png";
var _icon_excel ="../static/images/devtask/excel.png";
var _icon_text = "../static/images/devtask/text.png";
var _icon_pdf = "../static/images/devtask/pdf.png";
var _addworkFile = [];
var workModalType="";
$(function(){
	/*getDevDept();
	getDevCompany();*/
	
	addWork_uploadList();
	  //时间控件 配置参数信息
    laydate.render({
        elem: '#startWork'
    });
    laydate.render({
        elem: '#endWork'
    });

   /* $("#startWork").datetimepicker({
        minView:"month",
        format: "yyyy-mm-dd",
        autoclose: true,
        todayBtn: true,
        language: 'zh-CN',
        pickerPosition: "bottom-left"
    });

    $("#endWork").datetimepicker({
	    minView:"month",
	    format: "yyyy-mm-dd",
	    autoclose: true,
	    todayBtn: true,
	    language: 'zh-CN',
	    pickerPosition: "bottom-left"
	});*/

    $("#new_taskUser").click(function(){
        tShowWithinManPopup();
    });

    $("#checkuploadFile").change(function(){
        var files = this.files;
        var formFile = new FormData();
        outer:for(var i=0,len=files.length;i<len;i++){
            var file = files[i];
            if(file.size<=0){
                layer.alert(file.name+"文件为空！", {icon: 0});
                continue;
            }

            var fileList = [];
            var oldFileList = [];
            if(workModalType == 'new'){
                fileList=_addworkFile;
            }
            for(var j=0;j<fileList.length;j++){
                if(fileList[j].fileNameOld ==file.name){
                    layer.alert(file.name +" 文件已存在", {
                        icon: 2,
                        title: "提示信息"
                    });
                    continue outer;
                }
            }
            formFile.append("files", file);
            //读取文件
            if (window.FileReader) {
                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onerror = function(e) {
                    layer.alert("文件" + file.name +" 读取出现错误", {
                        icon: 2,
                        title: "提示信息"
                    });
                    return false;
                };
                reader.onload = function (e) {
                    if(e.target.result) {
                        console.log("文件 "+file.name+" 读取成功！");
                    }
                };
            }

            //列表展示
            var _tr = '';
            var file_name = file.name.split("\.")[0];
            var file_type = file.name.split("\.")[1];
            var _td_icon;
            var _td_name = '<span >'+file.name+'</span><i class="file-url"></i><i class = "file-bucket"></i><i class = "file-s3Key"></i></div></td>';
            var _td_opt = '<td><a href="javascript:void(0);" class="del-file-button" onclick="delworkFile(this)">删除</a></td>';
            switch(file_type){
                case "doc":
                case "docx":_td_icon = '<img src="'+_icon_word+'" />';break;
                case "xls":
                case "xlsx":_td_icon = '<img src="'+_icon_excel+'" />';break;
                case "txt":_td_icon = '<img src="'+_icon_text+'" />';break;
                case "pdf":_td_icon = '<img src="'+_icon_pdf+'" />';break;
                default:_td_icon = '<img src="'+_icon_word+'" />';break;
            }
            var _table=$(this).parent(".file-upload-select").next(".file-upload-list").children("table");
            _tr+='<tr><td><div class="fileTb">'+_td_icon+'  '+_td_name+_td_opt+'</tr>';
            _table.append(_tr);

        }
        //上传文件
        $.ajax({
            type:'post',
            url:'/zuul/devManage/worktask/uploadFile',
            contentType: false,
            processData: false,
            dataType: "json",
            data:formFile,

            success:function(data){
                for(var k=0,len=data.length;k<len;k++){
                    if(modalType == 'check'){
                        _checkfiles.push(data[k]);
                    }
                    $(".file-upload-tb span").each(function(o){
                        if($(this).text() == data[k].fileNameOld){
                            //$(this).parent().children(".file-url").html(data[k].filePath);
                            $(this).parent().children(".file-bucket").html(data[k].fileS3Bucket);
                            $(this).parent().children(".file-s3Key").html(data[k].fileS3Key);
                        }
                    });
                }
                if(modalType == 'check'){
                    $("#checkfiles").val(JSON.stringify(_checkfiles));
                    clearUploadFile('checkuploadFile');
                }
            },
            error:function(){
                $("#loading").css('display','none');
                layer.alert(errorDefect, {
                    icon: 2,
                    title: "提示信息"
                });
            }
        });
    });
	
});

function tShowWithinManPopup(){
	
	var id =$("#new_taskUserId").val();
	withinUserShow(id);
	cleanUser();
	$("#withinUserModal").modal("show");
}

function addUserID(){
    var rowData = $("#withinUserTable").bootstrapTable('getSelections')[0];
    if(typeof(rowData) == 'undefined') {
        layer.alert("请选择一条数据",{
            icon:2,
            title:"提示信息"
        })
    }else {
		$("#new_taskUserId").val(rowData.USER_ID).change();
		$("#new_taskUser").val(rowData.USER_NAME).change();
		$("#withinUserModal").modal("hide");
    }
}

//清除人员搜索信息
function cleanUser(){
    $("#withinUserName").val("");
    $("#withinDeptName").selectpicker('val', '');
    $("#withinCompanyName").selectpicker('val', '');

}

//添加
function addWorkTask(){ 
	var files=$("#addfiles").val();
	// 所需的缺陷ID
	var defectIdWithDevTask  = $("#dev_checkDefectID").val();
	var obj={};
	obj.requirementFeatureId = $("#Attribute").attr("featureCode");  //关联开发任务
	obj.defectID =defectIdWithDevTask;
	obj.devTaskName=$("#ataskName").val();//任务名称
	obj.devTaskPriority=$("#devTaskPriority").val();//优先级
	obj.devTaskOverview=$("#taskOverview").val();//任务描述
	obj.planStartDate=$("#startWork").val();//预计开始时间
	obj.planEndDate=$("#endWork").val();//预计结束时间
	obj.planWorkload=$("#workLoad").val();//预计工作量
	obj.devUserId=$("#new_taskUserId").val();//任务分配
	obj.commissioningWindowId= $("#Attribute").attr("commissioningWindowId");
	obj.requirementFeatureStatus=$("#Attribute").attr("requirementFeatureStatus");
	var objStr=JSON.stringify(obj); 
	$('#new_WorkTaskForm').data('bootstrapValidator').validate();
	if(!$('#new_WorkTaskForm').data('bootstrapValidator').isValid()){
		return;
	}
	$("#loading").css('display','block');
 $.ajax({
	 type:"post",
 	 url:'/devManage/worktask/addDevTask',
 	//contentType: "application/json; charset=utf-8",
 	data:{objStr,
 		"attachFiles":files
 		},
 	success : function(data) {
        $("#loading").css('display','none');
 		$("#new_WorkTaskModal").modal("hide");
        if (data.status == 2){
            layer.alert(data.errorMessage, {
                icon: 2,
                title: "提示信息"
            });
        } else if(data.status == 1){
            //affirm(defectIdWithDevTask);
            layer.alert('操作成功！', {
                icon: 1,
                title: "提示信息"
            });
            initTable();
            $("#rejectDiv").modal("hide");
            $("#new_WorkTaskModal").modal("hide");
        }
	},
     error:function(){
         $("#loading").css('display','none');
         layer.alert(errorDefect, {
             icon: 2,
             title: "提示信息"
         });
     }
 });
	
}

//显示开发任务弹窗
function showAdPopup(){
	clearstatus();
	DevPopup();
	$("#DevPopup").modal("show");
}

//清空搜索内容
function clearstatus() {
    $('#RelationCode').val("");
    $('#RelationName').val("");
    $("#PoStatus").selectpicker('val', '');
    $(".color1 .btn_clear").css("display","none");
}

function DevPopup(){
	var featureStatusList=$("#PoStatus").find("option");
	$("#loading").css('display','block');
    $("#devPopupTable").bootstrapTable('destroy');
    $("#devPopupTable").bootstrapTable({  
    	url:"/devManage/worktask/getAllFeature",
    	method:"post",
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 5, 10, 15],
        singleSelect : true,//单选
        queryParams : function(params) {
             var param={ 
            	featureName:$.trim($("#RelationName").val()),
            	featureCode:$.trim($("#RelationCode").val()),
            	FeatureStatus:$.trim($("#PoStatus").val()),
                pageNumber:params.pageNumber,
                pageSize:params.pageSize, 
             }
            return param;
        },
        columns : [{
            checkbox : true,
            width : "30px"
        },{
            field : "id",
            title : "id",
            visible : false,
            align : 'center'
        },{
            field : "FEATURE_CODE",
            title : "任务编码",
            align : 'center'
        },{
            field : "COMMISSIONING_WINDOW_ID",
            visible : false,
            align : 'center'
        },{
            field : "FEATURE_NAME",
            title : "任务名称",
            align : 'center'
        },{
            field : "PLAN_END_DATE",
            visible : false,
            align : 'center'
        },{
            field : "PLAN_START_DATE",
            visible : false,
            align : 'center'
        },{
            field : "ESTIMATE_WORKLOAD",
            visible : false,
            align : 'center'
        },{
        	field : "REQUIREMENT_FEATURE_STATUS",
        	title : "任务状态",
        	align : 'center',
        	formatter : function(value,rows, index) {
        		for (var i = 0,len = featureStatusList.length;i < len;i++) {
        			if(rows.REQUIREMENT_FEATURE_STATUS == featureStatusList[i].value){
        				var _status = "<input type='hidden' id='list_featureStatusList' value='"+featureStatusList[i].innerHTML+"'>";
                        return featureStatusList[i].innerHTML+_status
                    }
                }
            }
        }],
        onLoadSuccess:function(){
        	   $("#loading").css('display','none');
        },
        onLoadError : function() {
            $("#loading").css('display','none');
            layer.alert(errorDefect, {
                icon: 2,
                title: "提示信息"
            });
        }
    });
}
//赋值开发任务
function addPopup(){
	var rowData = $("#devPopupTable").bootstrapTable('getSelections')[0];
    if(typeof(rowData) == 'undefined') {
        layer.alert("请选择一条数据",{
            icon:2,
            title:"提示信息"
        })
    } else {

        $("#featureCode").val(rowData.FEATURE_NAME).change();
        $("#Attribute").attr("featureCode",rowData.ID);
        $("#Attribute").attr("commissioningWindowId",rowData.COMMISSIONING_WINDOW_ID); 
        $("#Attribute").attr("requirementFeatureStatus",rowData.REQUIREMENT_FEATURE_STATUS);
        if(rowData.PLAN_START_DATE!=undefined){
      	  $("#startWork").val(rowData.PLAN_START_DATE);
        }else{
        	$("#startWork").val("");
        }
        if(rowData.ESTIMATE_WORKLOAD!=undefined){
        	$("#workLoad").val(rowData.ESTIMATE_WORKLOAD).change();
        }else{
        	$("#workLoad").val("");
        }
      if(rowData.PLAN_END_DATE!=undefined){
    	  $("#endWork").val(rowData.PLAN_END_DATE);
      }else{
      	 $("#endWork").val("");
      }
        $("#DevPopup").modal("hide");
    }
	
}

//移除上传文件
function delworkFile(that,id){
	var fileS3Key = $(that).parent().prev().children().children(".file-s3Key").text(); 
	$.ajax({
        type:'post',
        url:'/devManage/worktask/delFile',
        data:{
        		id:id
        	 },
        success:function(data){
        	if(data.status=="success"){
        		layer.alert('删除成功！', {
                    icon: 1,
                    title: "提示信息"
                });
        	}else if(data.fail == "fail"){
        		layer.alert('删除失败！', {
                    icon: 1,
                    title: "提示信息"
                });
        	}
        },
        error:function(){
            $("#loading").css('display','none');
            layer.alert(errorDefect, {
                icon: 2,
                title: "提示信息"
            });
        }
    });
	$(that).parent().parent().remove();
	removeFile(fileS3Key);
}
//移除暂存数组中的指定文件
function removeFile(fileS3Key){
	if(workModalType == "new"){
		var _file = $("#addfiles").val();
		if(_file != ""){
			var files = JSON.parse(_file);
			for(var i=0,len=files.length;i<len;i++){
				if(files[i].fileS3Key == fileS3Key){
					Array.prototype.splice.call(files,i,1);
					break;
				}
			}
			_files = files;
			_addworkFile=[];
			$("#addfiles").val(JSON.stringify(files));
		}
		
	}
}

function addWork_uploadList(){
	$("#AddworkUploadFile").change(function(){
        var files = this.files;
        var formFile = new FormData();
        /*if(!fileAcceptBrowser()){
            for(var i=0,len=files.length;i<len;i++){
                var file_type = files[i].name.split(".")[1];
                if(file_type!="doc"&&file_type!="docx"&&file_type!="xls"&&file_type!="xlsx"&&file_type!="txt"&&file_type!="pdf"){
                    layer.alert('上传文件格式错误! 请上传后缀名为.doc，.docx，.xls，.xlsx，.txt，.pdf的文件', {icon:0});
                    return false;
                }
            }
        }*/

        outer:for(var i=0,len=files.length;i<len;i++){
            var file = files[i];
            if(file.size<=0){
                layer.alert(file.name+"文件为空！", {icon: 0});
                continue;
            }

            var fileList = [];
            var oldFileList = [];
            if(workModalType == 'new'){
                fileList=_addworkFile;
            }
            for(var j=0;j<fileList.length;j++){
                if(fileList[j].fileNameOld ==file.name){
                    layer.alert(file.name +" 文件已存在", {
                        icon: 2,
                        title: "提示信息"
                    });
                    continue outer;
                }
            }
            formFile.append("files", file);
            //读取文件
            if (window.FileReader) {
                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onerror = function(e) {
                    layer.alert("文件" + file.name +" 读取出现错误", {
                        icon: 2,
                        title: "提示信息"
                    });
                    return false;
                };
                reader.onload = function (e) {
                    if(e.target.result) {
                        console.log("文件 "+file.name+" 读取成功！");
                    }
                };
            }

            //列表展示
            var _tr = '';
            var file_name = file.name.split("\.")[0];
            var file_type = file.name.split("\.")[1];
            var _td_icon;
            var _td_name = '<span >'+file.name+'</span><i class="file-url"></i><i class = "file-bucket"></i><i class = "file-s3Key"></i></div></td>';
            var _td_opt = '<td><a href="javascript:void(0);" class="del-file-button" onclick="delworkFile(this)">删除</a></td>';
            switch(file_type){
                case "doc":
                case "docx":_td_icon = '<img src="'+_icon_word+'" />';break;
                case "xls":
                case "xlsx":_td_icon = '<img src="'+_icon_excel+'" />';break;
                case "txt":_td_icon = '<img src="'+_icon_text+'" />';break;
                case "pdf":_td_icon = '<img src="'+_icon_pdf+'" />';break;
                default:_td_icon = '<img src="'+_icon_word+'" />';break;
            }
            var _table=$(this).parent(".file-upload-select").next(".file-upload-list").children("table");
            _tr+='<tr><td><div class="fileTb">'+_td_icon+'  '+_td_name+_td_opt+'</tr>';
            _table.append(_tr);

        }
        //上传文件
        $.ajax({
            type:'post',
            url:'/zuul/devManage/worktask/uploadFile',
            contentType: false,
            processData: false,
            dataType: "json",
            data:formFile,

            success:function(data){
                for(var k=0,len=data.length;k<len;k++){
                    if(workModalType == 'new'){
                        _addworkFile.push(data[k]);
                    }
                    $(".file-upload-tb span").each(function(o){
                        if($(this).text() == data[k].fileNameOld){
                            //$(this).parent().children(".file-url").html(data[k].filePath);
                            $(this).parent().children(".file-bucket").html(data[k].fileS3Bucket);
                            $(this).parent().children(".file-s3Key").html(data[k].fileS3Key);
                        }
                    });
                }
                if(workModalType == 'new'){
                    $("#addfiles").val(JSON.stringify(_addworkFile));
                    clearUploadFile('AddworkUploadFile');
                }
            },
            error:function(){
                $("#loading").css('display','none');
                layer.alert(errorDefect, {
                    icon: 2,
                    title: "提示信息"
                });
            }
        });
    });
}

//清空上传文件
function clearUploadFile(idName){
	$('#'+idName+'').val('');
}

//内人员弹窗
function withinUserShow(notWithUserID){
    $("#loading").css('display','block');
    $("#withinUserTable").bootstrapTable('destroy');
    $("#withinUserTable").bootstrapTable({
        url:"/system/user/selectById",
        method:"post",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 10, 25, 50, 100 ],
        singleSelect : true,//单选
        queryParams : function(params) {
        	   var param = {
   	                pageNumber:params.pageNumber,
   	                pageSize:params.pageSize,
   	                userName:$.trim($("#withinUserName").val()),
   	                deptId:$.trim($("#withinDeptName").find("option:selected").val()),
   	                companyId:$.trim($("#withinCompanyName").find("option:selected").val()),
   	                notWithUserID:notWithUserID
   	            };
            return param;
        },
        columns : [{
            checkbox : true,
            width : "30px"
        },{
            field : "id",
            title : "id",
            visible : false,
            align : 'center'
        },{
            field : "USER_NAME",
            title : "姓名",
            align : 'center'
        },{
            field : "COMPANY_NAME",
            title : "所属公司",
            align : 'center'
        },{
            field : "DEPT_NAME",
            title : "所属部门",
            align : 'center'
        }],
        onLoadSuccess:function(){
            $("#loading").css('display','none');
        },
        onLoadError : function() {
            $("#loading").css('display','none');
            layer.alert(errorDefect, {
                icon: 2,
                title: "提示信息"
            });
        }
    });

}

function getDevDept() {
    $("#withinDeptName").append("<option value=''>请选择</option>");
    $.ajax({
        type: "post",
        url: "/system/user/getDept",
        dataType: "json",
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var id = data[i].id;
                var name = data[i].deptName;
                var opt = "<option value='" + id + "'>" + name + "</option>";
                $("#withinDeptName").append(opt);
            }
            $('.selectpicker').selectpicker('refresh');
        },
        error:function(){
            $("#loading").css('display','none');
            layer.alert(errorDefect, {
                icon: 2,
                title: "提示信息"
            });
        }
    });
}

function getDevCompany() {
    $("#withinCompanyName").append("<option value=''>请选择</option>");
    $.ajax({
        type: "post",
        url: "/system/user/getCompany",
        dataType: "json",
        success: function(data) {
            for(var i = 0; i < data.length; i++) {
                var id = data[i].id;
                var name = data[i].companyName;
                var opt = "<option value='" + id + "'>" + name + "</option>";
                $("#withinCompanyName").append(opt);
            }
            $('.selectpicker').selectpicker('refresh');
        },
        error:function(){
            $("#loading").css('display','none');
            layer.alert(errorDefect, {
                icon: 2,
                title: "提示信息"
            });
        }
    });
}
