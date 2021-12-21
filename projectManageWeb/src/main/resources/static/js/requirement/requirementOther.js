/**
 */
var selectSysIds = [];
var selectFeatureIds = [];
var selectUserIds = [];
var systemStatus = '';
var requirementStatus = '';
var selectReqIds = [];
var moduleStatus = '';
var paramList = {
	sysSelf:null,
	modalSelf:null,
}
$(function(){
		
	$("#devManageUserName").click(function(){
        selectUserIds=[];
    	$("#userbutton").attr("data-user", "devManageUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
        userTableShowAll();
    });

	$("#reqManageUserName").click(function(){
        selectUserIds=[];
    	$("#userbutton").attr("data-user", "reqManageUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShowAll();
    });
	
	
	$("#applyUserName").click(function(){
    	$("#userbutton").attr("data-user", "applyUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#developmentManageUserName").click(function(){
    	$("#userbutton").attr("data-user", "developmentManageUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#requirementManageUserName").click(function(){
    	$("#userbutton").attr("data-user", "requirementManageUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#requirementAcceptanceUserName").click(function(){
    	$("#userbutton").attr("data-user", "requirementAcceptanceUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#editApplyUserName").click(function(){
    	$("#userbutton").attr("data-user", "editApplyUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#editDevelopmentManageUserName").click(function(){
    	$("#userbutton").attr("data-user", "editDevelopmentManageUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#editRequirementManageUserName").click(function(){
    	$("#userbutton").attr("data-user", "editRequirementManageUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#editRequirementAcceptanceUserName").click(function(){
    	$("#userbutton").attr("data-user", "editRequirementAcceptanceUser");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 //getDept();
    	 //getCompany();
    	 //userTableShow();
    	 userTableShow2();
    });
	
	$("#systemName").click(function(){
        selectSysIds=[];
        systemStatus = "select";
   	 	$("#selSys").modal("show");
   	 	clearSearchSys();
   	 	//systemTableShow();
        systemTableShowAll();
    });
	
 
//	$("#editSystems").click(function(){
//		selectSysIds=[];
//		systemStatus = "edit";
//		$("#selSys").modal("show");
//		clearSearchSys();
//		systemTableShowAll();
//	});
	
	
	$("#featureName").click(function(){
        selectFeatureIds=[];
   	 	$("#selfeature").modal("show");
   	 	clearSearchFeature();
   	 	//featureTableShow();
        featureTableShowAll();
    });

    $("#userSearch").click(function(){
        if($("#userbutton").attr("data-user") == "devManageUser"
            || $("#userbutton").attr("data-user") == "reqManageUser"){
            userTableShowAll();
        }else{
            userTableShow2();
        }
    });
    
    $("#requirements").click(function(){
    	requirementStatus = "new";
    	selectReqIds = [];
    	$("#selReq").modal("show");
    	clearSearchReq();
    	reqTableShowAll();
    });
    $("#editRequirements").click(function(){
    	requirementStatus = "edit";
    	selectReqIds = [];
    	$("#selReq").modal("show");
    	clearSearchReq();
    	reqTableShowAll();
    });
    $("#parent").click(function(){
    	requirementStatus = "new2";
    	$("#selReq").modal("show");
    	clearSearchReq();
    	reqTableShow();
    });
    $("#editParent").click(function(){
    	requirementStatus = "edit2";
    	$("#selReq").modal("show");
    	clearSearchReq();
    	reqTableShow();
    });
     
    $("#editModule").click(function(){
    	moduleStatus = "edit";
    	$("#moduleTb").modal("show");
    	clearSearchModule();
    	moduleSearch();
    });
}) 
function showSysModal( self ){ 
	paramList.sysSelf = self; 
	systemStatus = "self"; 
	
	$("#selSys").modal("show");
	clearSearchSys();
	systemTableShowAll();
}
function showModal( self ){ 
}
//====================涉及系统弹框start==========================
function systemTableShow(){ 
	var flag = false;
	if( systemStatus = 'self' ){
		flag = true;
	}
	$("#systemTable").bootstrapTable('destroy');
    $("#systemTable").bootstrapTable({  
    	url:"/devManage/systeminfo/selectAllSystemInfo",
    	method:"post",
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 5, 10, 15],
        singleSelect : flag,//单选
        queryParams : function(params) {
             var param={ 
            	 systemName:$.trim($("#SCsystemName").val()),
            	 systemCode:$.trim($("#SCsystemCode").val()),
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
        	 
        },
        onLoadError : function() {

        }
    });
}
function systemTableShowAll(){
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
        pageList : [ 5, 10, 15],
        singleSelect : true,
        queryParams : function(params) {
            var param={
                systemName:$.trim($("#SCsystemName").val()),
                systemCode:$.trim($("#SCsystemCode").val()),
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
function commitSys(){
		if(systemStatus == "self"){
			var rows = $('#systemTable').bootstrapTable('getSelections'); 
			if( rows.length == 0 ){
				 layer.alert('请选择一列数据！', {icon: 0});
		            return false;
			}else{ 
				$(paramList.sysSelf).val( rows[0].systemName );
		    	$(paramList.sysSelf).prev().val( rows[0].id );
		    	$(paramList.sysSelf).attr( "sysCode", rows[0].systemCode  );
		    	
		    	$(paramList.sysSelf).parent().parent().next().find("input").val("");
		    	$(paramList.sysSelf).parent().parent().next().find("input").attr("assetsystemtreeid","");
		    	
		    	$("#selSys").modal("hide");
			} 
	    }else{ 
	    	if(selectSysIds.length<=0){
	            layer.alert('请选择一列数据！', {icon: 0});
	            return false;
	        }else{
	        	var ids = '';
	            var names = '';
	            for(var i=0;i<selectSysIds.length;i++){
	                ids += selectSysIds[i].id+",";
	                names += selectSysIds[i].systemName+',';
	            }
	            if(systemStatus == "select"){
		            $("#systemId").val(ids);
		            $("#systemName").val(names);
		            $("#selSys").modal("hide");
	            }else if(systemStatus == "new"){
	            	$("#systemIds").val(ids);
		            $("#systems").val(names);
		            $("#selSys").modal("hide");
	            } 
	        }
	    }
		
         
}
/*function commitSys(){
	 var selectContent = $("#systemTable").bootstrapTable('getSelections')[0];
     if(typeof(selectContent) == 'undefined') {
    	 layer.alert('请选择一列数据！', {icon: 0});
        return false;
     }else{
    	 
    	$("#systemId").val(selectContent.id);
    	$("#systemName").val(selectContent.systemName);     	 
    	$("#selSys").modal("hide");
     }

}*/
//重置
function clearSearchSys(){
	$("#SCsystemName").val('');
	$("#SCsystemCode").val('');
}
//====================涉及系统弹框end==========================

//====================开发任务弹框start==========================
function featureTableShow(){  
	$("#featureTable").bootstrapTable('destroy');
    $("#featureTable").bootstrapTable({  
    	url:"/devManage/devtask/getAllFeature",
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
            	featureName:$.trim($("#SCfeatureName").val()),
            	featureCode:$.trim($("#SCfeatureCode").val()),
            	requirementFeatureStatus:$.trim($("#SCfeatureStatus").val()),
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
        	 
        },
        onLoadError : function() {

        }
    });
}
function featureTableShowAll(){
    $("#featureTable").bootstrapTable('destroy');
    $("#featureTable").bootstrapTable({
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
                featureName:$.trim($("#SCfeatureName").val()),
                featureCode:$.trim($("#SCfeatureCode").val()),
                requirementFeatureStatus:$.trim($("#SCfeatureStatus").val()),
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

        },
        onLoadError : function() {

        },
        onCheckAll:function(rows){//全选
            for(var i=0;i<rows.length;i++){
                //if(selectSysIds .indexOf(rows[i])<=-1){
                selectFeatureIds.push(rows[i]);
                //}
            }
        },
        onUncheckAll: function (rows) {
            for(var i=0;i<rows.length;i++){
                if(selectFeatureIds.indexOf(rows[i])>-1){
                    selectFeatureIds.splice(selectFeatureIds.indexOf(rows[i]),1);
                }
            }
        },
        onCheck:function(row){//选中复选框
            //if(selectSysIds.indexOf(row)<=-1){
            selectFeatureIds.push( row );
            //}
        },
        onUncheck:function(row){//取消复选框
            if(selectFeatureIds.indexOf(row)>-1){
                selectFeatureIds.splice(selectFeatureIds.indexOf(row),1);
            }
        }
    });
}
/*function commitFeature(){
	 var selectContent = $("#featureTable").bootstrapTable('getSelections')[0];
     if(typeof(selectContent) == 'undefined') {
    	 layer.alert('请选择一列数据！', {icon: 0});
        return false;
     }else{    	 
    	$("#featureId").val(selectContent.id);
    	$("#featureName").val(selectContent.FEATURE_NAME);     	 
    	$("#selfeature").modal("hide");
     }

}*/
function commitFeature(){
    if(selectFeatureIds.length<=0){
        layer.alert('请选择一列数据！', {icon: 0});
        return false;
    }else{
        var ids = '';
        var names = '';
        for(var i=0;i<selectFeatureIds.length;i++){
            ids += selectFeatureIds[i].id+",";
            names += selectFeatureIds[i].FEATURE_NAME+',';
        }
        $("#featureId").val(ids);
        $("#featureName").val(names);
        $("#selfeature").modal("hide");
    }
}
//重置
function clearSearchFeature(){
	$("#SCfeatureName").val('');
	$("#SCfeatureCode").val('');
	$("#SCfeatureStatus").val('');
}

//====================开发任务弹框end==========================

//====================人员弹框start=======================
function userTableShow2(){
	 $("#userTable").bootstrapTable('destroy');
	    $("#userTable").bootstrapTable({  
	    	url:"/system/user/getAllUserModal2",
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
	            	userName: $.trim($("#userName").val()),
	            	companyName :  $("#companyName").val(),
	         		deptName : $("#deptName").val(),
         		 	/*companyId :  $("#companyName").val(),
         		 	deptId : $("#deptName").val(),*/
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
	            title : "所属处室",
	            align : 'center'
	        }],
	        onLoadSuccess:function(){
	        	 
	        },
	        onLoadError : function() {

	        }
	    });
	   
}
function userTableShowAll(){
    $("#userTable").bootstrapTable('destroy');
    $("#userTable").bootstrapTable({
        url:"/system/user/getAllUserModal2",
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
                userName: $.trim($("#userName").val()),
                companyName :  $("#companyName").val(),
                deptName : $("#deptName").val(),
                /*companyId :  $("#companyName").val(),
                deptId : $("#deptName").val(),*/
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
            title : "所属处室",
            align : 'center'
        }],
        onLoadSuccess:function(){

        },
        onLoadError : function() {

        },
        onCheckAll:function(rows){//全选
            for(var i=0;i<rows.length;i++){
                //if(selectSysIds .indexOf(rows[i])<=-1){
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
            //if(selectSysIds.indexOf(row)<=-1){
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
function getDept() {
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
}

function getCompany() {
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
function commitUser(){
    if($("#userbutton").attr("data-user")== 'devManageUser'||$("#userbutton").attr("data-user")== 'reqManageUser'){
        if(selectUserIds.length<=0){
            layer.alert('请选择一列数据！', {icon: 0});
            return false;
        }else {
            var ids = '';
            var names = '';
            for (var i = 0; i < selectUserIds.length; i++) {
                ids += selectUserIds[i].id + ",";
                names += selectUserIds[i].userName + ',';
            }
            if ($("#userbutton").attr("data-user") == 'devManageUser') {
                $("#devManageUserId").val(ids);
                $("#devManageUserName").val(names);
            }
            if ($("#userbutton").attr("data-user") == 'reqManageUser') {
                $("#reqManageUserId").val(ids);
                $("#reqManageUserName").val(names);
            }
            $("#userModal").modal("hide");
        }
    }else{
        var selectContent = $("#userTable").bootstrapTable('getSelections')[0];
        if(typeof(selectContent) == 'undefined') {
    	    layer.alert('请选择一列数据！', {icon: 0});
            return false;
        }else {
            /*if ($("#userbutton").attr("data-user") == 'devManageUser') {
                $("#devManageUserId").val(selectContent.id);
                $("#devManageUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'devManageUser') {
                $("#reqManageUserId").val(selectContent.id);
                $("#reqManageUserName").val(selectContent.userName).change();
            }*/

            if ($("#userbutton").attr("data-user") == 'applyUser') {
                $("#applyUserId").val(selectContent.id);
                $("#applyUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'developmentManageUser') {
                $("#developmentManageUserId").val(selectContent.id);
                $("#developmentManageUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'requirementManageUser') {
                $("#requirementManageUserId").val(selectContent.id);
                $("#requirementManageUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'requirementAcceptanceUser') {
                $("#requirementAcceptanceUserId").val(selectContent.id);
                $("#requirementAcceptanceUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'editApplyUser') {
                $("#editApplyUserId").val(selectContent.id);
                $("#editApplyUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'editDevelopmentManageUser') {
                $("#editDevelopmentManageUserId").val(selectContent.id);
                $("#editDevelopmentManageUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'editRequirementManageUser') {
                $("#editRequirementManageUserId").val(selectContent.id);
                $("#editRequirementManageUserName").val(selectContent.userName).change();
            }
            if ($("#userbutton").attr("data-user") == 'editRequirementAcceptanceUser') {
                $("#editRequirementAcceptanceUserId").val(selectContent.id);
                $("#editRequirementAcceptanceUserName").val(selectContent.userName).change();
            }
            $("#userModal").modal("hide");
        }
    }
}

function clearSearchUser(){	 
	 $("#userName").val('');
	 $("#employeeNumber").val('');
	 /*$("#deptName").selectpicker('val', '');
	 $("#companyName").selectpicker('val', '');*/
	 $("#deptName").val('');
	 $("#companyName").val('');
}
//====================人员弹框end=======================

//====================需求弹窗start=====================
//重置
function clearSearchReq() {	
    $('#reqCode2').val("");
    $('#reqName2').val("");
    $("#reqStatus2").selectpicker('val', '');
    $(".color1 .btn_clear").css("display","none");

}

function reqTableShowAll(){
	$("#listReq").bootstrapTable('destroy');
    $("#listReq").bootstrapTable({  
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
            	 requirementCode : $.trim($("#reqCode2").val()),
            	 requirementName  :$.trim( $("#reqName2").val()),
            	 requirementStatus : $("#reqStatus2").val(),
        		 pageNumber:params.pageNumber,
        		 pageSize:params.pageSize, 
             }
            return param;
        },
        columns : [{
            checkbox : true,
            width : "30px",
            
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
        	 
        },
        onLoadError : function() {

        },
        onCheckAll:function(rows){//全选
        	for(var i=0;i<rows.length;i++){
        		//if(selectReqIds .indexOf(rows[i])<=-1){
        		selectReqIds.push(rows[i]);
        		//}
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectReqIds.indexOf(rows[i])>-1){
        			selectReqIds.splice(selectReqIds.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectReqIds.indexOf(row)<=-1){
        	  selectReqIds.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectReqIds.indexOf(row)>-1){
        		selectReqIds.splice(selectReqIds.indexOf(row),1);
        	}
        }
    });
}


function reqTableShow(){
	$("#listReq").bootstrapTable('destroy');
	$("#listReq").bootstrapTable({  
		url:"/projectManage/requirement/getAllRequirement2",
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
					requirementCode : $.trim($("#reqCode2").val()),
					requirementName  :$.trim( $("#reqName2").val()),
					requirementStatus : $("#reqStatus2").val(),
					pageNumber:params.pageNumber,
					pageSize:params.pageSize, 
			}
			return param;
		},
		columns : [{
			checkbox : true,
			width : "30px",
			
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
			
		},
		onLoadError : function() {
			
		},
		
	});
}

function insertInput(){
	if(requirementStatus == "new" || requirementStatus == "edit"){
		if(selectReqIds.length<=0){
	 		 layer.alert('请选择一列数据！', {icon: 0});
	 	       return false;
	 	}else{
	 		var ids = '';
	 		var codes = '';
	 		for(var i=0;i<selectReqIds.length;i++){
	 			ids += selectReqIds[i].id+",";
	 			codes += selectReqIds[i].REQUIREMENT_CODE+',';
	 		}
	 		if(requirementStatus == "new"){
		 		$("#requirementIds").val(ids);
				$("#requirements").val(codes).change();
	 		}
	 		if(requirementStatus == "edit"){
	 			$("#editRequirementIds").val(ids);
	 			$("#editRequirements").val(codes).change();
	 		}
	 	}
	}else{
		var selectContent = $("#listReq").bootstrapTable('getSelections')[0];
        if(typeof(selectContent) == 'undefined') {
    	    layer.alert('请选择一列数据！', {icon: 0});
            return false;
        }else {
        	if(requirementStatus == "new2"){
        		$("#parentId").val(selectContent.id);
        		$("#parent").val(selectContent.REQUIREMENT_CODE);
        	}
        	if(requirementStatus == "edit2"){
        		$("#editParentId").val(selectContent.id);
        		$("#editParent").val(selectContent.REQUIREMENT_CODE);
        	}
        }
	}
		
	

	$("#selReq").modal("hide");
	
}

//===================需求弹窗end================================


//===================模块弹窗start==============================
function clearSearchModule(){
	$("#moduleName").val('');
	$("#moduleCode").val('');
	$("#moduleStatus").selectpicker("val",'');
}

function moduleSearch(){
	$("#listModule").bootstrapTable('destroy');
    $("#listModule").bootstrapTable({  
    	url:"/projectManage/systemTree/moduleTable",
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
            	 systemTreeName: $.trim($("#moduleName").val()),
            	 systemTreeCode: $.trim($("#moduleCode").val()),
            	 systemTreeStatus: $.trim($("#moduleStatus").val()),
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
            field : "systemTreeName",
            title : "模块名称",
            align : 'center'
        },{
            field : "systemTreeShortName",
            title : "模块简称",
            align : 'center'
        },{
        	field : "systemTreeCode",
        	title : "模块编码",
        	align : 'center'
        },{
            field : "systemTreeStatus",
            title : "状态",
            align : 'center',
            formatter:function(value,row,index){
            	if(value==1){
            		return "有效";
            	}else if(value==2){
            		return "无效";
            	}
            }
        }],
        onLoadSuccess:function(){
        	 
        },
        onLoadError : function() {

        }
    });
}

function moduleSubmit(){
	var selectContent = $("#listModule").bootstrapTable('getSelections')[0];
    if(typeof(selectContent) == 'undefined') {
	    layer.alert('请选择一列数据！', {icon: 0});
        return false;
    }else{
    	if(moduleStatus == 'new'){
    		$("#moduleId").val(selectContent.id);
    		$("#module").val(selectContent.systemTreeName);
    	}
    	if(moduleStatus == 'edit'){
    		$("#editModuleId").val(selectContent.id);
    		$("#editModule").val(selectContent.systemTreeName);
    	}
    	if(moduleStatus == 'self'){
    		$( paramList.sysSelf ).val( selectContent.systemTreeName);
    		$( paramList.sysSelf ).prev("input").val(selectContent.id);
    	}
    }
    $("#moduleTb").modal("hide");
}

//======================模块弹窗end================================
