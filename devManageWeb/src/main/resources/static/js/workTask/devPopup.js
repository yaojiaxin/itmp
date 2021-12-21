 var selectSysIds = [];
 var selectUserIds =[];
 var selectTaskIds =[];
 var selectRequirement =[];
//内人员弹窗
function withinUserShow(notWithUserID,devId){
	 $("#loading").css('display','block');
	    $("#withinUserTable").bootstrapTable('destroy');
	    $("#withinUserTable").bootstrapTable({
	        url:"/system/user/getAllDevUser",
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
	                deptName:$.trim($("#withinDeptName").val()),
	                companyName:$.trim($("#withinCompanyName").val()),
	                devID:devId,
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

	        }
	    });
	
}
function userByGroupShow(notWithUserID){
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
	                deptName:$.trim($("#withinDeptName").val()),
	                companyName:$.trim($("#withinCompanyName").val()),
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
	            field : "USER_ACCOUNT",
	            title : "用户名",
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

	        }
	    });
	
}
//列表人员弹窗
function userTableShow(notWithUserID){
	var projectIds = $("#project").val();
	if(projectIds!=null && projectIds!=''){
		projectIds = projectIds.join(",");
	}
	 $("#loading").css('display','block');
	    $("#userTable").bootstrapTable('destroy');
	    $("#userTable").bootstrapTable({
	        url:"/system/user/getAllUserModal2",
	        method:"post",
	        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
	        queryParamsType:"",
	        pagination : true,
	        sidePagination: "server",
	        pageNumber : 1,
	        pageSize : 10,
	        pageList : [ 10, 25, 50, 100 ],
	       // singleSelect : true,//单选
	        queryParams : function(params) {
	            var param = {
	                pageNumber:params.pageNumber,
	                pageSize:params.pageSize,
	                userName:$.trim($("#userName").val()),
	                deptName:$.trim($("#deptName").val()),
	                companyName:$.trim($("#companyName").val()),
	                systemId:"",
	                projectIds:projectIds,
	                id:notWithUserID
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
	            field : "userName",
	            title : "姓名",
	            align : 'center'
	        },{
	            field : "userAccount",
	            title : "用户名",
	            align : 'center'
	        },{
	            field : "companyName",
	            title : "所属公司",
	            align : 'center'
	        },{
	            field : "deptName",
	            title : "所属部门",
	            align : 'center'
	        }],
	        onLoadSuccess:function(){
	            $("#loading").css('display','none');
	        },
	        onLoadError : function() {

	        },
	        onCheckAll:function(rows){//全选
	        	for(var i=0;i<rows.length;i++){
	        		//if(selectUserIds.indexOf(rows[i])<=-1){
	        		selectUserIds.push(rows[i]);
	        		//}
	        	}
	        },
	        onUncheckAll: function (rows) {
	        	for(var i=0;i<rows.length;i++){
	        		if(selectUserIds.indexOf(rows[i])>-1){
	        			selectUserIds.splice(selectUserIds.indexOf(rows[i]),1);
	        		}
	        	}
	        },
	        onCheck:function(row){//选中复选框
	        	//if(selectUserIds.indexOf(row)<=-1){
	        	selectUserIds.push( row );
	        	//}
	        },
	        onUncheck:function(row){//取消复选框
	        	if(selectUserIds.indexOf(row)>-1){
	        		selectUserIds.splice(selectUserIds.indexOf(row),1);
	        	}
	        }
	    });
	
}


function TaskPopup(){
	var featureStatusList=$("#TaskPType").find("option");
	$("#loading").css('display','block');
    $("#TaskTable").bootstrapTable('destroy');
    $("#TaskTable").bootstrapTable({  
    	url:"/devManage/devtask/getAllFeature",
    	method:"post",
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 5, 10, 15],
        //singleSelect : true,//单选
        queryParams : function(params) {
             var param={ 
            	featureName:$.trim($("#TaskPName").val()),
            	featureCode:$.trim($("#TaskPCode").val()),
            	requirementFeatureStatus:$.trim($("#TaskPType").val()),
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
            field : "FEATURE_code",
            title : "任务编码",
            align : 'center'
        },{
            field : "FEATURE_NAME",
            title : "任务名称",
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

        },
        onCheckAll:function(rows){//全选
        	for(var i=0;i<rows.length;i++){
        		//if(selectUserIds.indexOf(rows[i])<=-1){
        		selectTaskIds.push(rows[i]);
        		//}
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectTaskIds.indexOf(rows[i])>-1){
        			selectTaskIds.splice(selectTaskIds.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectUserIds.indexOf(row)<=-1){
        	selectTaskIds.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectTaskIds.indexOf(row)>-1){
        		selectTaskIds.splice(selectTaskIds.indexOf(row),1);
        	}
        }
    });
	
    
}

function reqPopup(){
//	$("#ReqType").selectpicker('val', '');
    $("#loading").css('display','block');
    $("#reqTable").bootstrapTable('destroy');
    $("#reqTable").bootstrapTable({
    	url:"/projectManage/requirement/getAllRequirement2",
    	method:"post",
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 5, 10, 15],
       // singleSelect : true,//单选
        queryParams : function(params) {
             var param={ 
            	 requirementCode : $.trim($("#ReqCode").val()),
            	 requirementName  :$.trim( $("#ReqName").val()),
            	 requirementStatus : $("#reqStatus").val(),
        		 pageNumber:params.pageNumber,
        		 pageSize:params.pageSize, 
             }
            return param;
        },
        columns : [{
            checkbox : true,
            width : "30px",
            formatter:function(value, row, index) {
            	if(reqStatus == 'cancel'){
            		if (row.reqStatusName == "已取消"){
                        return {
                            disabled : true,//设置是否可用
                            checked : false//设置选中
                        	};
                        	return value;
                    }
            	}
                
             }
        },{
            field : "id",
            title : "id",
            visible : false,
            align : 'center'
        },{
            field : "REQUIREMENT_CODE",
            title : "需求编号",
            align : 'center'
        },{
            field : "REQUIREMENT_NAME",
            title : "需求名称",
            align : 'center'
        },{
        	field : "reqStatusName",
        	title : "需求状态",
        	align : 'center'
        },{
            field : "reqSourceName",
            title : "需求来源",
            align : 'center'
        },{

            field : "reqTypeName",
            title : "需求类型",
            align : 'center'
        }],
        onLoadSuccess:function(){
            $("#loading").css('display','none');
        },
        onLoadError : function() {

        },
        onCheckAll:function(rows){//全选
        	for(var i=0;i<rows.length;i++){
        		//if(selectSysIds .indexOf(rows[i])<=-1){
        		selectRequirement.push(rows[i]);
        		//}
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectRequirement.indexOf(rows[i])>-1){
        			selectRequirement.splice(selectRequirement.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectSysIds.indexOf(row)<=-1){
        	selectRequirement.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectRequirement.indexOf(row)>-1){
        		selectRequirement.splice(selectRequirement.indexOf(row),1);
        	}
        }
    });
	
    
}

function SystemPopup(){
	$("#loading").css('display','block');
    $("#systemTable").bootstrapTable('destroy');
    $("#systemTable").bootstrapTable({
        url:"/devManage/systeminfo/selectAllSystemInfo2",
        method:"post",
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 10, 25, 50, 100],
        //singleSelect : true,//单选
        queryParams : function(params) {
            var param={
                systemName:$.trim($("#SystemName").val()),
                systemCode:$.trim($("#SystemCode").val()),
                pageNumber:params.pageNumber,
                pageSize:params.pageSize
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
            field : "systemCode",
            title : "系统编码",
            align : 'center'
        },{
            field : "systemName",
            title : "系统名称",
            align : 'center'
        },{
            field : "systemShortName",
            title : "系统简称",
            align : 'center'
        },{
            field : "projectName",
            title : "所属项目",
            align : 'center'
        }],
        onLoadSuccess:function(){
            $("#loading").css('display','none');
        },
        onLoadError : function() {

        },
        onCheckAll:function(rows){//全选
        	for(var i=0;i<rows.length;i++){
        		//if(selectSysIds .indexOf(rows[i])<=-1){
        		selectSysIds.push(rows[i]);
        		//}
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectSysIds.indexOf(rows[i])>-1){
        			selectSysIds.splice(selectSysIds.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectSysIds.indexOf(row)<=-1){
        	 selectSysIds.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectSysIds.indexOf(row)>-1){
        		selectSysIds.splice(selectSysIds.indexOf(row),1);
        	}
        }
    });
	
    
}
//编辑页面选择开发任务弹框
function EditDevPopup(){
	
	var featureStatusList=$("#PoStatus").find("option");
	$("#loading").css('display','block');
    $("#EditPopupTable").bootstrapTable('destroy');
    $("#EditPopupTable").bootstrapTable({  
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
            	featureName:$.trim($("#edRelationName").val()),
            	featureCode:$.trim($("#edRelationCode").val()),
            	FeatureStatus:$.trim($("#edPoStatus").val()),
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

        }
    });
	
    
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

        }
    });
	
    
}

function edbeforeSelectRow()  
{  
    $("#EditPopupTable").jqGrid('resetSelection');  
    return(true);  
} 
function TaskbeforeSelectRow()  
{  
    $("#TaskTable").jqGrid('resetSelection');  
    return(true);  
} 

function ReqSelectRow()  
{  
    $("#reqTable").jqGrid('resetSelection');  
    return(true);  
} 

function systemSelectRow()  
{  
    $("#systemTable").jqGrid('resetSelection');  
    return(true);  
} 

function beforeSelectRow()  
{  
    $("#devPopupTable").jqGrid('resetSelection');  
    return(true);  
} 
function beforeSelectRow1() {  
    $("#userTable").jqGrid('resetSelection');  
    return(true);  
}

function searchSystem(){
	SystemPopup();
}


//清空任务搜索内容
function clearTask() {
    $('#TaskPCode').val("");
    $('#TaskPName').val("");
    $("#TaskPType").selectpicker('val', '');
    $("#taskP .color1 .btn_clear").css("display","none");
}
//清空需求搜索内容
function clearReq() {
    $('#ReqCode').val("");
    $('#ReqName').val("");
    $("#reqStatus").selectpicker('val', '');
    $("#requirementP .color1 .btn_clear").css("display","none");
}
//清空搜索内容
function clearstatus() {
    $('#RelationCode').val("");
    $('#RelationName').val("");
    $("#PoStatus").selectpicker('val', '');
    $(".color1 .btn_clear").css("display","none");
}



//清空系统搜索内容
function clearSystem() {
    $('#SystemName').val("");
    $('#SystemCode').val("");
    $("#SystemType").selectpicker('val', '');
    $("#SystemP .color1 .btn_clear").css("display","none");
}
function edclearstatus() {
    $('#edRelationCode').val("");
    $('#edRelationName').val("");
    $("#edPoStatus").selectpicker('val', '');
    $("#EditPopup .color1 .btn_clear").css("display","none");

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
       //$("#sel_systemId").val(rowData.id);
    	if(rowData.developmentMode!=null && rowData.developmentMode!=undefined){
         	$("#ndevelopmentMode").val(rowData.developmentMode);
         }else{
         	$("#ndevelopmentMode").val('');
         }
    	 $("#newSprintId").val(rowData.sprintId);
 		 $("#newSprintName").val(rowData.sprintName);
 		 
        $("#featureCode").val(rowData.FEATURE_NAME).change();
        if(rowData.PLAN_START_DATE!=undefined){
        	  $("#startWork").val(rowData.PLAN_START_DATE);
        }else{
        	 $("#startWork").val("");
        }
        if(rowData.ESTIMATE_WORKLOAD!=undefined){
      	  $("#workLoad").val(rowData.ESTIMATE_WORKLOAD).change();
      }else{
      	 $("#workLoad").val("").change();
      }
        if(rowData.PLAN_END_DATE!=undefined){
      	  $("#endWork").val(rowData.PLAN_END_DATE);
        }else{
        	 $("#endWork").val("");
        }
        $("#wSystemId").val(rowData.systemId);
        $("#Attribute").attr("featureCode",rowData.ID);
        $("#Attribute").attr("commissioningWindowId",rowData.COMMISSIONING_WINDOW_ID); 
        $("#Attribute").attr("requirementFeatureStatus",rowData.REQUIREMENT_FEATURE_STATUS);
        $("#DevPopup").modal("hide");
    }
	
}
//系统确认
function selectSystem(){
	// var rowData = $("#systemTable").bootstrapTable('getSelections')[0];
	if(selectSysIds.length<=0){
 		 layer.alert('请选择一列数据！', {icon: 0});
 	       return false;
 	} else {
 			var ids = '';
 			var names = '';
	    	for(var i=0;i<selectSysIds.length;i++){
	 			ids += selectSysIds[i].id+",";
	 			if(i==selectSysIds.length-1){
	 				names += selectSysIds[i].systemName
	 			}else{
	 				names += selectSysIds[i].systemName+',';
	 			}
	 			
	 		}
	    	//$("#sel_systemId").val(rowData.id);
	        $("#involveSystem").val(names);
	        $("#SystemP").modal("hide");
	    }

}
//需求确认
function selectReq(){
	var rowData = $("#reqTable").bootstrapTable('getSelections')[0];

	if(selectRequirement.length<=0){
		 layer.alert('请选择一列数据！', {icon: 0});
	       return false;
	} else {
			var ids = '';
			var names = '';
	    	for(var i=0;i<selectRequirement.length;i++){
	 			ids += selectRequirement[i].id+",";
	 			if(i==selectRequirement.length-1){
	 				names += selectRequirement[i].REQUIREMENT_CODE
	 			}else{
	 				names += selectRequirement[i].REQUIREMENT_CODE+',';
	 			}
	 			
	 		}
	}
	    $("#relationDemand").val(names);
	    $("#requirementP").modal("hide");
	
}

//人员确认
function selectMan(){
	 // var rowData = $("#userTable").bootstrapTable('getSelections')[0];
	  if(selectUserIds.length<=0){
	 		 layer.alert('请选择一列数据！', {icon: 0});
	 	       return false;
	 	}else {
	 		var ids = '';
	 		var names = '';
	 		for(var i=0;i<selectUserIds.length;i++){
	 			if(i==selectUserIds.length-1){
	 				ids += selectUserIds[i].id;
		 			names += selectUserIds[i].userName;
	 			}else{
	 			ids += selectUserIds[i].id+",";
	 			names += selectUserIds[i].userName+',';
	 			}
	 		}
	            $("#developer").val(names);
	            $("#man_devUserId").val(ids);
	        $("#userModal").modal("hide");
	    }
}

//任务确认
function selectTask(){
	if(selectTaskIds.length<=0){
		 layer.alert('请选择一列数据！', {icon: 0});
	       return false;
		}else {
			var ids = '';
	 		var names = '';
	 		for(var i=0;i<selectTaskIds.length;i++){
	 			if(i==selectTaskIds.length-1){
	 				ids += selectTaskIds[i].id;
		 			names += selectTaskIds[i].FEATURE_code;
	 			}else{
	 				ids += selectTaskIds[i].id+",";
		 			names += selectTaskIds[i].FEATURE_code+',';
	 			}
	 			
	 		}
	            $("#parallelTask").val(names);
	       
	        $("#taskP").modal("hide");
	    }/*
	            $("#parallelTask").val(rowData.FEATURE_code);
	       
	        $("#taskP").modal("hide");
	    }*/
}

//选择开发任务弹框提交
function editPopup(){
	var rowData = $("#EditPopupTable").bootstrapTable('getSelections')[0];
    if(typeof(rowData) == 'undefined') {
        layer.alert("请选择一条数据",{
            icon:2,
            title:"提示信息"
        })
    } else {
       //$("#sel_systemId").val(rowData.id);
    	
    	//必须在  $("#edfeatureCode")赋值前加 勿改 ztt
    	if(rowData.developmentMode!=null && rowData.developmentMode!=undefined){
         	$("#edevelopmentMode").val(rowData.developmentMode);
         }else{
         	$("#edevelopmentMode").val('');
         }
    	 $("#editSystemId").val(rowData.systemId);
    	 
    	 $("#editSprintId").val(rowData.sprintId);
 		 $("#editSprintName").val(rowData.sprintName);
    	 
    	 
        $("#edfeatureCode").val(rowData.FEATURE_NAME).change();
        if(rowData.PLAN_START_DATE!=undefined){
      	  $("#edstartWork").val(rowData.PLAN_START_DATE);
        }else{
        	$("#edstartWork").val("");
        }
        if(rowData.ESTIMATE_WORKLOAD!=undefined){
        	$("#edworkLoad").val(rowData.ESTIMATE_WORKLOAD).change();
        }else{
        	$("#edworkLoad").val("").change();
        }
        if(rowData.PLAN_END_DATE!=undefined){
        	$("#edendWork").val(rowData.PLAN_END_DATE);
        }else{
        	$("#edendWork").val("").change();
        }
       
        $("#edAttribute").attr("edfeatureCode",rowData.ID);
        $("#edAttribute").attr("edcommissioningWindowId",rowData.COMMISSIONING_WINDOW_ID); 
        $("#edAttribute").attr("edrequirementFeatureStatus",rowData.REQUIREMENT_FEATURE_STATUS);
        $("#EditPopup").modal("hide");
    }
	
}



function removeEdit(){
	  $("#EditPopup").modal("hide");
}
function clearSearchUser(){
	$("#project").selectpicker('val', '');
	 $("#userName").val('');
	 $("#employeeNumber").val('');
	 $("#deptName").val('');
	 $("#companyName").val('');
	 $("#userModal .color1 .btn_clear").css("display","none");
}

function showManPopup(){
	
	selectUserIds=[];
	clearSearchUser();
	getProject();
	userTableShow();//$("#man_devUserId").val()
	
	$("#deptName").val("");
	$("#companyName").val("");
	$("#userModal").modal("show");
}
function devManPopup(){
	
	//getWithinDept();
	//getwithinCompany();
	cleanUser();
	tShowWithinManPopup();
}
//转派
function tShowWithinManPopup(){
	var type=$("#userPopupType").val();
	if(type=="Transfer"){
		var id=$("#TransferUser").attr("userID");
		var devId=$("#TransferDevID").val();
		userByGroupShow(id);
		
		$("#withinUserModal").modal("show");
	}else if(type=="Assign"){
		var id=$("#assignUser").attr("assignUserID");
		var divID=$("#assigDevID").val();
		userByGroupShow(id);
		$("#withinUserModal").modal("show");
	}else if(type=="insertWorkTask"){
        var id='';
        userByGroupShow(id);
        $("#withinUserModal").modal("show");
    }else if(type=="updatetWorkTask"){
        var id = $("#edit_taskUserId").val();
        var divID = $("#taskID").val();
        userByGroupShow(id);
        
        $("#withinUserModal").modal("show");
    }
	
}

function showEdPopup(){
	edclearstatus();
	EditDevPopup();
	//EdFeatureStatus();
	$("#EditPopup").modal("show");
}
function showAdPopup(){
	clearstatus();
	DevPopup();
	$("#DevPopup").modal("show");
}


function showRequirement(){
	clearReq();
	selectRequirement=[];
	reqPopup();
	$("#requirementP").modal("show");
}

function removeReq(){
	$("#requirementP").modal("hide");
}

function showTask(){
	selectTaskIds=[];
	clearTask();
	TaskPopup();
	$("#taskP").modal("show");
}
function showSystem(){
	clearSystem();
	selectSysIds=[];
	 SystemPopup();
	$("#SystemP").modal("show");
}


function removeSystem(){
	  $("#SystemP").modal("hide");
}

 

//系统状态
/*function ReqSystem(){
	$("#SystemType").empty();
	$.ajax({
		method:"post", 
		url:"/devManage/worktask/ReqSystem",
		success : function(data) {
			for(var i in data){
                  var opt = "<option value='" + i + "'>" + data[i] + "</option>";
                  $("#SystemType").append(opt);
                  }
			$('.selectpicker').selectpicker('refresh'); 
		}
		
	});
}*/
 

/*function searchInfoUser(){
	$("#loading").css('display','block');
	 var obj = {};
	 obj.userName = $.trim($("#userName").val());
	 obj.companyId =  $("#companyName").val();
	 obj.deptId = $("#deptName").val();
	 obj = JSON.stringify(obj); 
	$("#userTable").jqGrid('setGridParam',{ 
		url:'/system/user/getAllUser',
		    datatype:'json',
	       postData:{
	        "FindUser":obj
	       }, 
	       page:1
	   }).trigger("reloadGrid");  
	$("#loading").css('display','none');
}*/
/*function getDept() {
    $("#deptName").append("<option value=''>请选择</option>");
    $.ajax({
        type: "post",
        url: "/system/user/getDept",
        dataType: "json",
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var id = data[i].id;
                var name = data[i].deptName;
                var opt = "<option value='" + id + "'>" + name + "</option>";
                $("#deptName").append(opt);
            }
            $('.selectpicker').selectpicker('refresh');
        }
    });
}*/
//开发状态
/*function AdFeatureStatus(){
	$("#PoStatus").empty();
	$.ajax({
		method:"post", 
		url:"/devManage/worktask/FeatureStatus",
		success : function(data) {
			for(var i in data){
                  var opt = "<option value='" + i + "'>" + data[i] + "</option>";
                  $("#PoStatus").append(opt);
                  }
			$('.selectpicker').selectpicker('refresh'); 
		}
		
	});
}*/
//开发状态
/*function EdFeatureStatus(){
	$("#edPoStatus").empty();
	$.ajax({
		method:"post", 
		url:"/devManage/worktask/FeatureStatus",
		success : function(data) {
			for(var i in data){
                  var opt = "<option value='" + i + "'>" + data[i] + "</option>";
                  $("#edPoStatus").append(opt);
                  }
			$('.selectpicker').selectpicker('refresh'); 
		}
		
	});
}
*/
//提交备注
function addDevRemark(){
	var devTaskRemark=$.trim($("#tyaskRemark").val());
	if(devTaskRemark==""||devTaskRemark==undefined){
		layer.alert('备注信息不能为空！', {
            icon: 2,
            title: "提示信息"
        });
		return;
	}
	var id=$.trim($("#DevTaskID").val());
	var requirementFeatureId=$.trim($("#requirementFeatureId").val());
		 var obj = {};
		 obj.devTaskRemark =$.trim($("#tyaskRemark").val());
		 obj.devTaskId =id;
		var remark=JSON.stringify(obj); 
	$.ajax({
		type:"post",
		url:"/devManage/worktask/addTaskRemark",
		data:{
			remark:remark,
			attachFiles :$("#checkfiles").val()
			},
		success: function(data) {
			layer.alert('保存成功！', {
                icon: 1,
                title: "提示信息"
            });
			_checkfiles = [];
			$("#checkfiles").val(''); 
			getSee(id,requirementFeatureId);
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
		
	});
}
/*function getCompany() {
    $("#companyName").append("<option value=''>请选择</option>");
    $.ajax({
        type: "post",
        url: "/system/user/getCompany",
        dataType: "json",
        success: function(data) {
            for(var i = 0; i < data.length; i++) {
                var id = data[i].id;
                var name = data[i].companyName;
                var opt = "<option value='" + id + "'>" + name + "</option>";
                $("#companyName").append(opt);
            }
            $('.selectpicker').selectpicker('refresh');
        }
    });
}
*/

/*function getwithinCompany() {
	$("#withinCompanyName").val("");
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
        }
    });
}*/
/*function getWithinDept() {
	 $("#withinDeptName").empty();
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
        }
    });
}*/
//清除人员搜索信息
function cleanUser(){
	$("#withinUserName").val("");
	$("#withinDeptName").val('');
	$("#withinCompanyName").val('');
	$("#withinUserModal .color1 .btn_clear").css("display","none");
	 
}
function addUserID(){
	var type=$("#userPopupType").val();
	  var rowData = $("#withinUserTable").bootstrapTable('getSelections')[0];
	    if(typeof(rowData) == 'undefined') {
	        layer.alert("请选择一条数据",{
	            icon:2,
	            title:"提示信息"
	        })
	    }else {
	    	if(type=="Transfer"){
	    		var id=$("#TransferUser").attr("userID");
	    		
	    		 $("#TransferUser").val(rowData.USER_NAME).change();
	    		 $("#TransferUser").attr("userID",rowData.USER_ID);
	 	        $("#withinUserModal").modal("hide");
	    	}else if(type=="Assign"){
	    		 $("#assignUser").val(rowData.USER_NAME).change();
	    		 $("#assignUser").attr("assignUserID",rowData.USER_ID);
	 	        $("#withinUserModal").modal("hide");
	    	}else if(type=="insertWorkTask"){
                $("#new_taskUserId").val(rowData.USER_ID).change();
                $("#new_taskUser").val(rowData.USER_NAME).change();
                $("#withinUserModal").modal("hide");
            }else if(type=="updatetWorkTask"){
                $("#edit_taskUserId").val(rowData.USER_ID).change();
                $("#edtaskUser").val(rowData.USER_NAME).change();
                $("#withinUserModal").modal("hide");
            }
	           
	    }
}
//获取当前登录用户所在项目（结项的项目除外） ztt
function getProject(){
	$("#project").empty();
	//var projectIds = '';
	$.ajax({
		url:"/devManage/displayboard/getAllProject",
		dataType:"json",
		type:"post",
		async:false,
		success:function(data){
			if(data.projects!=undefined && data.projects.length>0){
				for(var i=0;i <data.projects.length;i++){
					//projectIds += data.projects[i].id+",";
					$("#project").append("<option selected value='"+data.projects[i].id+"'>"+data.projects[i].projectName+"</option>")
					
				}
				//$('#project option').attr('selected', true);
			}
			
			 $('.selectpicker').selectpicker('refresh');
			 if(data.status == '2'){
				layer.alert("查询项目失败！！！", { icon: 2});
			}
		},
		error:function(){
			layer.alert("系统内部错误，请联系管理员！！！", { icon: 2});
		}
	})
	//return projectIds;
	
}