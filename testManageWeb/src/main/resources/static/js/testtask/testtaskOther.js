/**
 * Created by ztt
 */
var reqStatus = '';
var selectReqIds = [];
var selectUserIds = [];
var selectSysIds = [];
var selectWinIds = [];
var windowTypeList = [];
var numbers = [];
$(function(){
	windowTypeList =  $("#win_windowType").find("option");
	getProject(); 
	dateComponent();
    $("#requirementName").click(function(){
    	$("#reqbutton").attr("data-req","list");
    	$("#reqStatus").empty();
    	selectReqIds = [];
    	$("#selReq").modal("show");
    	reqStatus = '';
    	clearSearchReq(); 
    	getReqStatus();
    	reqTableShowAll();
    });
    $("#newrequirementName").click(function(){
    	$("#reqbutton").attr("data-req","new");
    	$("#reqStatus").empty();
    	$("#selReq").modal("show");
    	reqStatus = 'cancel';
    	clearSearchReq(); 
    	getReqStatus();
    	reqTableShow();
    });
    $("#editrequirementName").click(function(){
    	$("#reqbutton").attr("data-req","edit");
    	$("#reqStatus").empty();
    	$("#selReq").modal("show");
    	reqStatus = 'cancel';
    	clearSearchReq(); 
    	getReqStatus();
    	reqTableShow();
    });
    
    $("#systemName").click(function(){
    	$("#sysbutton").attr("data-sys","list");
    	selectSysIds = [];
    	 $("#selSys").modal("show");
    	 clearSearchSys();
    	 systemTableShowAll();
    	 
    });
    
    $("#newsystemName").click(function(){
    	$("#sysbutton").attr("data-sys","new");
		 $("#selSys").modal("show");
		 clearSearchSys();
		 //systemTableShow();
		 systemTableShowAll();
    });
    $("#editsystemName").click(function(){
    	$("#sysbutton").attr("data-sys","edit");
		 $("#selSys").modal("show");
		 clearSearchSys();
		// systemTableShow();
		 systemTableShowAll();
   });
    $("#devManPostName").click(function(){
    	$("#userbutton").attr("data-user", "list");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	selectUserIds = [];
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 
    	 userTableShow();
    });
    $("#execteUserName").click(function(){
    	$("#userbutton").attr("data-user", "list2");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	selectUserIds = [];
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 
    	 userTableShow();
    });
    $("#newtestManageUserName").click(function(){
    	systemId = $("#newsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "newMan");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	 
    	 userTableShow();
    });
    $("#edittestManageUserName").click(function(){
    	systemId = $("#editsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "editMan");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 $("#userModal").modal("show");
    	 clearSearchUser();
    	
    	 userTableShow();
    });
    
    $("#newexecuteUserName").click(function(){
    	systemId = $("#newsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "new");
    	$("#deptName").empty();
    	$("#companyName").empty();
	   	 $("#userModal").modal("show");
		 clearSearchUser();
		
		 userTableShow();
	});
    
    $("#editexecuteUserName").click(function(){
    	systemId = $("#editsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "edit");
    	$("#deptName").empty();
    	$("#companyName").empty();
	   	 $("#userModal").modal("show");
		 clearSearchUser();
		
		 userTableShow();
	});
    $("#transferUserName").click(function(){
    	$("#userbutton").attr("data-user", "transfer");
    	$("#deptName").empty();
    	$("#companyName").empty();
	   	 $("#userModal").modal("show");
	 	//systemId = $("#transferSystemId").val();
		 clearSearchUser();
		
		 userTableShow();
    });
    
    $("#sWorkDividUserName").click(function(){
    	$("#userbutton").attr("data-user", "split");
    	$("#deptName").empty();
    	$("#companyName").empty();
	   	$("#userModal").modal("show");
		//systemId = $("#transferSystemId").val();
	   	excuteUserName = '';
		 clearSearchUser();
		
		 userTableShow();
    });
    $("#sWorkAssignUserName").click(function(){
    	$("#userbutton").attr("data-user", "split2");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	$("#userModal").modal("show");
    	excuteUserName = '';
    	clearSearchUser();
    	
    	userTableShow();
    });
    
    $("#planVersionName").click(function(){
    	$("#windowButton").attr("data-window","list");
    	winReset();
    	selectWinIds=[];
    	$("#comWindowModal").modal("show");
    	commitWindowAll();
    });
    
     $("#newcommitWindowName").click(function(){
    	$("#windowButton").attr("data-window","new");
    	winReset();
    	selectWinIds=[];
    	$("#comWindowModal").modal("show");
    	commitWindow();
    });
     
     $("#editcommitWindowName").click(function(){
    	$("#windowButton").attr("data-window","edit");
    	winReset();
    	selectWinIds=[];
    	$("#comWindowModal").modal("show");
    	commitWindow();
    });
     
    $("#commitWindowSearch").click(function(){
    	if($("#windowButton").attr("data-window") == "list"){
    		commitWindowAll();
    	}else{
    		commitWindow();
    	}
    });
     
    $("#systemSearch").click(function(){
    	if($("#sysbutton").attr("data-sys") == "list"){
    		systemTableShowAll();
    	}else{
    		//systemTableShow();
    		systemTableShowAll();
    	}
    });
    $("#reqSearch").click(function(){
    	if($("#reqbutton").attr("data-req") == "list"){
    		reqTableShowAll();
    	}else{
    		reqTableShow();
    	}
    });
    /*$("#userSearch").click(function(){
    	if($("#userbutton").attr("data-user") == "list" || $("#userbutton").attr("data-user") == "list2"){
    		userTableShowAll();
    	}else{
    		userTableShow();
    	}
    })
    */
    _opt_html = $("#developmentDept").html();
})
function dateComponent() {
	var locale = {
		"format": 'yyyy-mm-dd',
		"separator": " -222 ",
		"applyLabel": "确定",
		"cancelLabel": "取消",
		"fromLabel": "起始时间",
		"toLabel": "结束时间'",
		"customRangeLabel": "自定义",
		"weekLabel": "W",
		"daysOfWeek": ["日", "一", "二", "三", "四", "五", "六"],
		"monthNames": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
		"firstDay": 1
	};
	$("#timeDate").daterangepicker({ 'locale': locale });
}
//========================投产窗口start=====================
function commitWindow(){
	var startDate = '';
	var endDate = '';
	if( $( "#timeDate" ).val() != "" ){
		var arr = $( "#timeDate" ).val().split(" - ");
		startDate = arr[0];
		endDate = arr[1];
	} 
	$("#comWindowTable").bootstrapTable('destroy');
    $("#comWindowTable").bootstrapTable({  
    	url:"/projectManage/commissioningWindow/selectCommissioningWindows2",
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
        		 "windowName" :  $.trim($("#win_windowName").val()),
        		 "startDate" : startDate,
        		 "endDate" : endDate,
                 "windowType" :  $("#win_windowType").val(),
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
            field : "windowName",
            title : "窗口名称",
            align : 'center'
        },{
            field : "windowDate",
            title : "投产日期",
            align : 'center'
        },{
        	field : "windowType",
        	title : "窗口类型",
        	align : 'center',
        	formatter:function(value, row, index) {
	    		for(var i = 0;i<windowTypeList.length;i++){
	    			 if(windowTypeList[i].value == row.windowType){
		    			  return windowTypeList[i].innerHTML;
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
function commitWindowAll(){
	var startDate = '';
	var endDate = '';
	if( $( "#timeDate" ).val() != "" ){
		var arr = $( "#timeDate" ).val().split(" - ");
		startDate = arr[0];
		endDate = arr[1];
	} 
	$("#comWindowTable").bootstrapTable('destroy');
    $("#comWindowTable").bootstrapTable({  
    	url:"/projectManage/commissioningWindow/selectCommissioningWindows2",
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
        		 "windowName" :  $.trim($("#win_windowName").val()),
        		 "startDate" : startDate,
        		 "endDate" : endDate,
                 "windowType" :  $("#win_windowType").val(),
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
            field : "windowName",
            title : "窗口名称",
            align : 'center'
        },{
            field : "windowDate",
            title : "投产日期",
            align : 'center'
        },{
        	field : "windowType",
        	title : "窗口类型",
        	align : 'center',
	    	  formatter:function(value, row, index) {
	    		for(var i = 0;i<windowTypeList.length;i++){
	    			 if(windowTypeList[i].value == value){
		    			  return windowTypeList[i].innerHTML;
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
        		//if(selectWinIds .indexOf(rows[i])<=-1){
        		selectWinIds.push(rows[i]);
        		//}
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectWinIds.indexOf(rows[i])>-1){
        			selectWinIds.splice(selectWinIds.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectWinIds.indexOf(row)<=-1){
        	selectWinIds.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectWinIds.indexOf(row)>-1){
        		selectWinIds.splice(selectWinIds.indexOf(row),1);
        	}
        }
    });
}
function commitWin(){
	if($("#windowButton").attr("data-window")== 'list'){
		if(selectWinIds.length<=0){
	 		 layer.alert('请选择一列数据！', {icon: 0});
	 	       return false;
	 	}else{
	 		var ids = '';
	 		var names = '';
	 		for(var i=0;i<selectWinIds.length;i++){
	 			ids += selectWinIds[i].id+",";
	 			names += selectWinIds[i].windowName+',';
	 		}
			if($("#windowButton").attr("data-window")== 'list'){
			    $("#planVersion").val(ids);
				$("#planVersionName").val(names).change();
			}
	 	}
	}else{
		var selectContent = $("#comWindowTable").bootstrapTable('getSelections')[0];
	    if(typeof(selectContent) == 'undefined') {
	    	 layer.alert('请选择一列数据！', {icon: 0});
	       return false;
	    }else{
	    	 if($("#windowButton").attr("data-window") == 'new'){
				 $("#newcommitWindowId").val(selectContent.id);
				 $("#newcommitWindowName").val(selectContent.windowName).change();
			 }
			 if($("#windowButton").attr("data-window")== 'edit'){
				 $("#editcommitWindowId").val(selectContent.id);
				 $("#editcommitWindowName").val(selectContent.windowName).change();
			 }
			
	    }
	}
	 
	$("#comWindowModal").modal("hide");

}
function winReset(){
	$("#win_windowName").val("");
	$("#timeDate").val("");
	$("#win_windowType").selectpicker('val', '');
	
}
//=========================需求弹框start=====================
function reqTableShow(){
	 $("#listReq").bootstrapTable('destroy');
	 $("#listReq").bootstrapTable({  
		url:'/testManage/testtask/getAllReq', 
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
        	 requirementCode : $.trim($("#reqCode").val()),
        	 requirementName  :$.trim( $("#reqName").val()),
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
    	 
    },
    onLoadError : function() {

    }
});
}
function reqTableShowAll(){
	$("#listReq").bootstrapTable('destroy');
    $("#listReq").bootstrapTable({  
    	url:"/testManage/testtask/getAllReq",
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
            	 requirementCode : $.trim($("#reqCode").val()),
            	 requirementName  :$.trim( $("#reqName").val()),
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
function getReqStatus(){
$.ajax({
	 type:"POST",
     url:"/testManage/testtask/getDataDicList",
     dataType:"json",
     data:{
     	datadictype:'reqStatus'        	
 	},
 	 success:function(data){
 		$("#reqStatus").empty();
 		$("#reqStatus").append("<option value=''>请选择</option>")
 		for(var i=0;i<data.length;i++){
           var opt = "<option value='" + data[i].valueCode + "'>" + data[i].valueName + "</option>";
           $("#reqStatus").append(opt);
           }
		$('.selectpicker').selectpicker('refresh'); 
 	 }
	});
}

function insertInput(){
	if($("#reqbutton").attr("data-req") == "list"){
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
	 		$("#requirementId").val(ids);
			$("#requirementName").val(codes).change();
	 	}
		
	}else{
		var selectContent = $("#listReq").bootstrapTable('getSelections')[0];
	    if(typeof(selectContent) == 'undefined') {
	    	 layer.alert('请选择一列数据！', {icon: 0});
	       return false;
	    }else{
	    	if($("#reqbutton").attr("data-req") == "new"){
				$("#newrequirementId").val(selectContent.id);
				$("#newrequirementName").val(selectContent.REQUIREMENT_CODE).change();
				findDeptNumber(selectContent.id, 1);
			}
			if($("#reqbutton").attr("data-req") == "edit"){
				$("#editrequirementId").val(selectContent.id);
				$("#editrequirementName").val(selectContent.REQUIREMENT_CODE).change();
				findDeptNumber(selectContent.id, 2);
			}
	    }
	}

	$("#selReq").modal("hide");
	 
}
var _opt_html = '';
function findDeptNumber(id, type){
	
	$("#developmentDept option").each(function(){
		numbers.push($(this).val());
	})
	$.ajax({
		url: "/testManage/testtask/findDeptNumber",
		dataType: "json",
		async: false,
		type: "post",
		data: {
			id:id
		},
		success: function (data) {
			if(type == 1){
				$("#developmentDept").empty();
				$("#developmentDept").append(_opt_html);
				if(data.data != null){
					if(!numbers.includes(data.data.deptNumber)){
						$("#developmentDept").append("<option selected value='"+data.data.deptNumber+"'>"+data.data.deptName+"</option>");
						$("#developmentDept").selectpicker('refresh');
					}else{
						$("#developmentDept").selectpicker('val',data.data.deptNumber);
						$("#developmentDept").selectpicker('refresh');
					}
				}
//				if(data.data != null){		
//					$("#developmentDeptNumber2").val(data.data.deptNumber);
//					$("#developmentDept2").val(data.data.deptName);
//				}else{
//					$("#developmentDeptNumber2").val('');
//					$("#developmentDept2").val('');
//				}
//				$("#development2").css("display","block");
//				$("#development").css("display","none");
			}else if(type == 2){
				$("#editDevelopmentDept").empty();
				console.log(111)
				$("#editDevelopmentDept").append(_opt_html);
				if(data.data != null){
					if(!numbers.includes(data.data.deptNumber)){
						$("#editDevelopmentDept").append("<option selected value='"+data.data.deptNumber+"'>"+data.data.deptName+"</option>");
						$("#editDevelopmentDept").selectpicker('refresh');
					}else{
						$("#editDevelopmentDept").selectpicker('val',data.data.deptNumber);
						$("#editDevelopmentDept").selectpicker('refresh');
					}
				}
				/*if(data.data != null){
					$("#editDevelopmentDeptNumber2").val(data.data.deptNumber);
					$("#editDevelopmentDept2").val(data.data.deptName);
				}else{
					$("#editDevelopmentDeptNumber2").val('');
					$("#editDevelopmentDept2").val('');
				}
				$("#editDevelopment").css("display","none");
				$("#editDevelopment2").css("display","block");*/
			}
		},
		error: function () {
			$("#loading").css('display', 'none');
			layer.alert("系统内部错误，请联系管理员！！！", { icon: 2 });
		}
	})
	
}

//重置
function clearSearchReq() {	
    $('#reqCode').val("");
    $('#reqName').val("");
    $("#reqStatus").selectpicker('val', '');
    $("#selReq .color1 .btn_clear").css("display","none");
}
//===========================需求弹框end=================================

//====================涉及系统弹框start==========================
function systemTableShow(){  
	$("#systemTable").bootstrapTable('destroy');
    $("#systemTable").bootstrapTable({  
    	url:"/testManage/testtask/selectAllSystemInfo",
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
    	url:"/testManage/testtask/selectAllSystemInfo2",
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
	if($("#sysbutton").attr("data-sys") == "list"){
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
			$("#systemId").val(ids);
			$("#systemName").val(names).change(); 
	 	}
	}else{
    	 var selectContent = $("#systemTable").bootstrapTable('getSelections')[0];
         if(typeof(selectContent) == 'undefined') {
        	 layer.alert('请选择一列数据！', {icon: 0});
            return false;
         }else{
	    	 if($("#sysbutton").attr("data-sys") == "new"){
	    		$("#newsystemId").val(selectContent.id);
	 	    	$("#newsystemName").val(selectContent.systemName).change(); 
	    	 }
	    	 if($("#sysbutton").attr("data-sys") == "edit"){
	     		$("#editsystemId").val(selectContent.id);
	  	    	$("#editsystemName").val(selectContent.systemName).change(); 
	     	 }
         }
    }
    $("#selSys").modal("hide");
 
}
//重置
function clearSearchSys(){
	$("#SCsystemName").val('');
	$("#SCsystemCode").val('');
	$("#selSys .color1 .btn_clear").css("display","none");
}
//====================涉及系统弹框end==========================

//====================人员弹框start=======================
function userTableShow(){
	getProject();
	var projectIds = $("#project").val();
	if(projectIds!=null && projectIds!=''){
		projectIds = projectIds.join(",");
	}
	 $("#loading").css('display','block');
	if($("#userbutton").attr("data-user")== 'list' || $("#userbutton").attr("data-user")== 'list2'){
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
	     //   singleSelect : true,//单选
	        queryParams : function(params) {
		             var param={
	        			// id:excuteUserName,
	        			//systemId:systemId,
		            	 projectIds:projectIds,
		         		 userName: $.trim($("#userNameReqF").val()),
		         		 companyName :  $("#companyName").val(),
		         		 deptName : $("#deptName").val(),
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
	  
	}else{
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
	            		 id:excuteUserName,
	            		 projectIds:projectIds,
	        			// systemId:systemId, 
	            		 userName: $.trim($("#userNameReqF").val()),
	            		 companyName :  $("#companyName").val(),
	            		 deptName : $("#deptName").val(),
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
		        	 $("#loading").css('display','none');
		        },
		        onLoadError : function() {

		        }
		    });
	}
}
function userTableShowAll(){
	var projectIds = $("#project").val();
	if(projectIds!=null && projectIds!=''){
		projectIds = projectIds.join(",");
	}
	 $("#loading").css('display','block');
	if($("#userbutton").attr("data-user")== 'list' || $("#userbutton").attr("data-user")== 'list2'){
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
	     //   singleSelect : true,//单选
	        queryParams : function(params) {
		             var param={
	        			// id:excuteUserName,
	        			//systemId:systemId,
		            	 projectIds:projectIds,
		         		 userName: $.trim($("#userNameReqF").val()),
		         		 companyName :  $("#companyName").val(),
		         		 deptName : $("#deptName").val(),
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
	}else{
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
	            		 id:excuteUserName,
	            		 projectIds:projectIds,
	        			// systemId:systemId, 
	            		 userName: $.trim($("#userNameReqF").val()),
	            		 companyName :  $("#companyName").val(),
	            		 deptName : $("#deptName").val(),
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
		        	 $("#loading").css('display','none');
		        },
		        onLoadError : function() {

		        }
		    });
	}
	
   
}


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

*/
function commitUser(){
	if($("#userbutton").attr("data-user")== 'list' || $("#userbutton").attr("data-user")== 'list2'){
		if(selectUserIds.length<=0){
	 		 layer.alert('请选择一列数据！', {icon: 0});
	 	       return false;
	 	}else{
	 		var ids = '';
	 		var names = '';
	 		for(var i=0;i<selectUserIds.length;i++){
	 			ids += selectUserIds[i].id+",";
	 			names += selectUserIds[i].userName+',';
	 		}
			if($("#userbutton").attr("data-user")== 'list'){
			 $("#devManPost").val(ids);
			 $("#devManPostName").val(names).change();
			}
			
			 if($("#userbutton").attr("data-user")== 'list2'){
				 $("#execteUserId").val(ids);
				 $("#execteUserName").val(names).change();
			 }
		 }
	 }else{
		 var selectContent = $("#userTable").bootstrapTable('getSelections')[0];
		    if(typeof(selectContent) == 'undefined') {
		    	 layer.alert('请选择一列数据！', {icon: 0});
		       return false;
		    }else{
				 if($("#userbutton").attr("data-user") == 'new'){
					 $("#newexecuteUser").val(selectContent.id);
					 $("#newexecuteUserName").val(selectContent.userName).change();
				 }
				 if($("#userbutton").attr("data-user")== 'edit'){
					 $("#editexecuteUser").val(selectContent.id);
					 $("#editexecuteUserName").val(selectContent.userName).change();
				 }
				 if($("#userbutton").attr("data-user")==  'newMan'){
					 $("#newtestManageUser").val(selectContent.id);
					 $("#newtestManageUserName").val(selectContent.userName).change();
				 }
				 if($("#userbutton").attr("data-user")==  'editMan'){
					 $("#edittestManageUser").val(selectContent.id);
					 $("#edittestManageUserName").val(selectContent.userName).change();
				 }
				 if($("#userbutton").attr("data-user")==  'transfer'){
					 $("#transferId").val(selectContent.id);
					 $("#transferUserName").val(selectContent.userName).change();
				 }
				 if($("#userbutton").attr("data-user")=="split"){
					 $("#sWorkDivid").val(selectContent.id);
					 $("#sWorkDividUserName").val(selectContent.userName).change();
				 }
				 if($("#userbutton").attr("data-user")=="split2"){
					 $("#sWorkAssignUserId").val(selectContent.id);
					 $("#sWorkAssignUserName").val(selectContent.userName).change();
				 }
		    }
	   }
		$("#userModal").modal("hide");
	 
}

function clearSearchUser(){
	 $("#userNameReqF").val('');
	 $("#employeeNumber").val('');
	 $("#deptName").val('');
	 $("#companyName").val('');
	 $("#project").selectpicker('val', '');
	 $("#userModal .color1 .btn_clear").css("display","none");
}
//====================人员弹框end=======================

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
//			layer.alert("系统内部错误，请联系管理员！！！", { icon: 2});
		}
	})
	//return projectIds;
	
}
