/**
 * Created by ztt
 */
  var reqStatus = '';
  var systemId = '';
  var selectDftIds = [];
  var selectReqIds = [];
  var selectUserIds = [];
  var selectSysIds = [];
  var selectWinIds = [];
	var selectSprints = [];
  
$(function(){
	$("#sprintNames").click(function(){
		clearSearchSpr();
		selectSprints = [];
		$("#sprintModal").modal("show");
		sprintAll();
	})
	 $("#win_windowDate").datetimepicker({
    	minView:"month",
    	format: "yyyy-mm-dd",
    	autoclose: true,
    	todayBtn: true,
    	language: 'zh-CN',
    	pickerPosition: "bottom-left"
    });
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
    
    $("#systemNameReq").click(function(){
    	$("#sysbutton").attr("data-sys","list");
    	selectSysIds=[];
    	 $("#selSys").modal("show");
    	 clearSearchSys();
    	 systemTableShowAll();
		});
    
    $("#newsystemName").click(function(){
    	$("#sysbutton").attr("data-sys","new");
		 $("#selSys").modal("show");
		 clearSearchSys();
		 systemTableShow2();
    });
    $("#editsystemName").click(function(){
    	$("#sysbutton").attr("data-sys","edit");
		 $("#selSys").modal("show");
		 clearSearchSys();
		 systemTableShow2();
   });
  
    $("#devManPostName").click(function(){
    	$("#userbutton").attr("data-user", "list");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	selectUserIds =[];
    	 $("#userModalreqF").modal("show");
    	 clearSearchUser();
    	 userTableShow2();
    });
    $("#execteUserName").click(function(){
    	$("#userbutton").attr("data-user", "list2");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	selectUserIds =[];
    	 
    	$("#userModalreqF").modal("show");
    	clearSearchUser();
    	userTableShow2();
     });
    $("#newdevManageUserName").click(function(){
    	systemId = $("#newsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "newMan");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 
    	 $("#userModalreqF").modal("show");
    	 clearSearchUser();
    	 userTableShow2();
    });
    $("#editdevManageUserName").click(function(){
    	systemId = $("#editsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "editMan");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 
    	 $("#userModalreqF").modal("show");
    	 clearSearchUser();
    	 userTableShow2();
    });
    
    $("#newexecuteUserName").click(function(){
    	systemId = $("#newsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "new");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 
	   	 $("#userModalreqF").modal("show");
		 clearSearchUser();
		 userTableShow2();
	});
    
    $("#editexecuteUserName").click(function(){
    	systemId = $("#editsystemId").val();
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "edit");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 
	   	 $("#userModalreqF").modal("show");
		 clearSearchUser();
		 userTableShow2();
	});
    $("#updateexecuteUserName").click(function(){
    	excuteUserName='';
    	$("#userbutton").attr("data-user", "allupdate");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 
	   	 $("#userModalreqF").modal("show");
		 clearSearchUser();
		 userTableShow2();
	});
    $("#newSynDefectCode").click(function(){
        $("#dftbutton").attr("data-dft","newSyn");
        $("#defModal").modal("show");
        $("#dftReqFeatureId").val('');
        selectDftIds = [];
        resetDft();
        dftTableShow();
    })
    $("#editSynDefectCode").click(function(){
        $("#dftbutton").attr("data-dft","editSyn");
        $("#defModal").modal("show");
        selectDftIds = [];
        resetDft();
        dftTableShow();
    })

    $("#newdefectCode").click(function(){
    	$("#dftbutton").attr("data-dft","new");
    	$("#defModal").modal("show");
    	$("#dftReqFeatureId").val('');
    	selectDftIds = [];
        resetDft();
    	dftTableShow();
    })
    $("#editdefectCode").click(function(){
    	$("#dftbutton").attr("data-dft","edit");
    	$("#defModal").modal("show");
    	selectDftIds = [];
        resetDft();
    	dftTableShow();
    })
    $("#transferUserName").click(function(){
    	$("#userbutton").attr("data-user", "transfer");
    	$("#deptName").empty();
    	$("#companyName").empty();
    	 
	   	$("#userModalreqF").modal("show");
	   	systemId = $("#transferSystemId").val();
		clearSearchUser();
		userTableShow2();
    })
    
     
    
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
     
    
    $("#systemSearch").click(function(){
    	if($("#sysbutton").attr("data-sys") == "list"){
    		systemTableShowAll();
    	}else{
    		systemTableShow2();
    	}
    });
    $("#reqSearch").click(function(){
    	if($("#reqbutton").attr("data-req") == "list"){
    		reqTableShowAll();
    	}else{
    		reqTableShow();
    	}
    });

    $("#newProjectPlanName").click(function(){
    	if($("#newsystemId").val()==''){
            layer.alert("请先选择项目！！！", { icon: 2});
		}else{
            $("#planbutton").attr("data-ProjectPlan", "new");
            $("#planCode").empty();
            $("#planName").empty();
            $("#responsibleUserName").empty();
            $("#projectName").empty();

            $("#selProjectPlan").modal("show");
            clearSearchPlan();
            planTableShow();
		}
    });

    $("#editProjectPlanName").click(function(){
        if($("#editsystemId").val()==''){
            layer.alert("请先选择项目！！！", { icon: 2});
        }else{
            $("#planbutton").attr("data-ProjectPlan", "edit");
            $("#planCode").empty();
            $("#planName").empty();
            $("#responsibleUserName").empty();
            $("#projectName").empty();

            $("#selProjectPlan").modal("show");
            clearSearchPlan();
            planTableShow();
		}
    });
   /* $("#userSearch").click(function(){
    	if($("#userbutton").attr("data-user") == "list" || $("#userbutton").attr("data-user") == "list2"){
    		userTableShowAll();
    	}else{
    		userTableShow2();
    	}
    });*/
    
    defectStatusList = $("#defectStatus").find("option");
    
})

//===========================项目========================
//查询关联项目
function getProject_list(){
	$("#Project_Table").bootstrapTable('destroy');
	$("#Project_Table").bootstrapTable({  
		url:"/devManage/systeminfo/getProjectListByProjectName",
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
				projectName:$("#project_Name").val(),
                projectCode:$("#project_Code").val(),
				pageNumber:params.pageNumber,
				pageSize:params.pageSize, 
			}
			return param;
		},
		responseHandler : function(res) {
			return {
					total:res.total,
					rows:res.projectInfo,
			};
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
				field : "projectCode",
				title : "项目编码",
				align : 'center'
		},{
				field : "projectName",
				title : "项目名称",
				align : 'center'
		}],
		onLoadSuccess:function(a,b,c,d){
				console.log(a,b,c,d)
		},
		onLoadError : function() {

		}
	});
	$("#project_modal").modal('show');
}

//关联项目弹窗搜索清空
function clearSearchProject(){
	$('#project_Name').val('');
	$('#project_Code').val('');
}

//选择项目
function select_project(This){
	var selectContent = $("#Project_Table").bootstrapTable('getSelections')[0];
	if(!selectContent) {
		layer.alert('请选择一列数据！', {icon: 0});
		return false;
	}else{
		$("#editsystemId").val('').removeAttr('systemcode');
		$("#editsystemName").val('').change();
		$("#newsystemId").val('').removeAttr('systemcode');
		$("#newsystemName").val('').change();
		$("#projectInput" ).val("");
		$("#modalInput" ).val("");
		$("#projectOwn").empty();
		$("#newsystemVersionBranch").val('').empty();
		$("#repairVersionBranch").val('').empty();

		$("#editsystemVersionBranch").empty();
		$("#editSprintId").empty();
		$("#editdevTaskStatus").empty();
		

		$("#newProject_listId").val(selectContent.id);
		$("#newProject_list").val(selectContent.projectName).change();
		$("#ndevelopmentMode").val(selectContent.developmentMode);

		$("#newsprintDiv").hide();
		$("#newSprintId").empty().selectpicker('refresh');
		$("#newstoryPointDiv").hide();

		$("#sprintDiv").hide();
		$("#editSprintId").empty();
		$("#estoryPointDiv").hide();
		$("#newsystem_div").show();
		$("#project_modal").modal('hide');
	}
}

//===========================冲刺========================
function sprintAll(){
	$("#sprintTable").bootstrapTable('destroy');
    $("#sprintTable").bootstrapTable({  
    	url:"/devManage/sprintManage/getAllsprint",
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
        		 "sprintName" :  $.trim($("#sprintName").val()),
                 "systemName" :  $("#ssystemName").val(),
                 pageNum:params.pageNumber,
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
            field : "sprintName",
            title : "冲刺名称",
            align : 'center'
        },{
            field : "systemName",
            title : "所属系统",
            align : 'center'
        },{
        	field : "sprintStartDate",
        	title : "开始日期",
        	align : 'center'
        },{
        	field : "sprintEndDate",
        	title : "结束日期",
        	align : 'center'
        }],
        onLoadSuccess:function(){
        	 
        },
        onLoadError : function() {

        },
        onCheckAll:function(rows){//全选
        	for(var i=0;i<rows.length;i++){
        		selectSprints.push(rows[i]);
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectSprints.indexOf(rows[i])>-1){
        			selectSprints.splice(selectSprints.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectWinIds.indexOf(row)<=-1){
        	selectSprints.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectSprints.indexOf(row)>-1){
        		selectSprints.splice(selectSprints.indexOf(row),1);
        	}
        }
    });
}
function commitSprint(){
	if(selectSprints.length<=0){
		 layer.alert('请选择一列数据！', {icon: 0});
	       return false;
	}else{
		var ids = '';
		var names = '';
		for(var i=0;i<selectSprints.length;i++){
			ids += selectSprints[i].id+",";
			names += selectSprints[i].sprintName+',';
		}
	    $("#sprintIds").val(ids);
		$("#sprintNames").val(names.substring(0,names.length-1)).change();
		$("#sprintModal").modal("hide");
	}
	
	
}
function clearSearchSpr(){
	$("#sprintName").val("");
	$("#ssystemName").val("");
}
//========================投产窗口start=====================
function commitWindow(){
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
                 "windowDate" :  $("#win_windowDate").val(),
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
        	field : "typeName",
        	title : "窗口类型",
        	align : 'center'
        }],
        onLoadSuccess:function(){
        	 
        },
        onLoadError : function() {

        }
    });
}
function commitWindowAll(){
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
                 "windowDate" :  $("#win_windowDate").val(),
                 "windowType" :  $("#win_windowType").val(),
        		 pageNumber:params.pageNumber,
        		 pageSize:params.pageSize, 
             }
            return param;
        },
        responseHandler : function(res) {
            return {
                total:res.total,
                rows:res.projectInfo,
			};
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
        	formatter:function(value,row,index){
        		var typeList = $("#win_windowType").find("option");
        		for(var i =0;i<typeList.length;i++){
        			if(typeList[i].value == value){
        				return typeList[i].innerHTML;
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
				$("#planVersionName").val(names.substring(0,names.length-1)).change();
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
	$("#win_windowDate").val("");
	$("#win_windowType").selectpicker('val', '');
	
}

//=========================缺陷弹框start=====================
function dftTableShow(){
    var featureSource;
    if($("#dftbutton").attr("data-dft") == "newSyn" || $("#dftbutton").attr("data-dft") == "new"){
	 	featureSource = $("#newrequirementFeatiureSource").val();
    }
    if($("#dftbutton").attr("data-dft") == "editSyn" || $("#dftbutton").attr("data-dft") == "edit"){
        featureSource = $("#editrequirementFeatureSource").val();
    }
    $("#defectList").bootstrapTable("destroy");
    $("#defectList").bootstrapTable({
        url:"/devManage/devtask/listDftNoReqFeature",
        method:"post",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 10, 25, 50, 100 ],
        queryParams : function(params) {
            var param={
                featureSource : featureSource,
            	reqFeatureId : $("#dftReqFeatureId").val(),
                defectCode:$.trim($("#defectCode").val()),
                defectSummary:$.trim($("#defectSummary").val()),
                defectStatus:$.trim($("#defectStatus").find("option:selected").val()),
                pageNumber:params.pageNumber,
                pageSize:params.pageSize
            };
            return param;
        },
        columns : [{
            checkbox : true,
            width : "30px",
            formatter : stateFormatter
        },{
            field : "defectCode",
            title : "缺陷编号",
            align : 'center',
        },{
            field : "defectSummary",
            title : "缺陷摘要",
            align : 'left'
        },{
            field : "defectStatus",
            title : "缺陷状态",
            align : 'center',
            formatter : function(value, row, index) {
                for (var i = 0,len = defectStatusList.length;i < len;i++) {
                    if(row.defectStatus == defectStatusList[i].valueCode){
                       // var _status = "<input type='hidden' id='list_defectStatus' value='"+defectStatusList[i].valueName+"'>";
                        return defectStatusList[i].valueName //+_status
                    }
                }
            }
        },{
            field : "submitUserName",
            title : "提出人",
            align : 'center',
            formatter : function(value, row, index) {
                return toStr(row.submitUserName) + "  |  " + toStr(row.submitDate);
            }
        }
        ],
        onLoadSuccess:function(){
            $("#loading").css('display','none');
        },
        onLoadError : function() {

        },
       
        onCheckAll:function(rows){//全选
        	for(var i=0;i<rows.length;i++){
        		//if(selectDftIds.indexOf(rows[i])<=-1){
        			selectDftIds.push(rows[i]);
        		//}
        	}
        },
        onUncheckAll: function (rows) {
        	for(var i=0;i<rows.length;i++){
        		if(selectDftIds.indexOf(rows[i])>-1){
        			selectDftIds.splice(selectDftIds.indexOf(rows[i]),1);
        		}
        	}
        },
        onCheck:function(row){//选中复选框
        	//if(selectDftIds.indexOf(row)<=-1){
        		selectDftIds.push( row );
        	//}
        },
        onUncheck:function(row){//取消复选框
        	if(selectDftIds.indexOf(row)>-1){
        		selectDftIds.splice(selectDftIds.indexOf(row),1);
        	}
        }
    });
}

function stateFormatter(value, row, index) {
    var defectArr={};
    if($("#dftbutton").attr("data-dft") == "edit") {
        defectArr = $("#editdefectId").val().split(",");
    }
    if($("#dftbutton").attr("data-dft") == "editSyn" ) {
        defectArr = $("#editSynDefectId").val().split(",");
    }
	for (var i = 0; i < defectArr.length; i++) {
		if (defectArr[i] == row.id) {
			var flag = 0;
			for (var key in selectDftIds) {
				if (selectDftIds[key].id == row.id) {
					var flag = 1;
				}
			}
			if (flag != 1) {
				selectDftIds.push(row);
			}
			return {
				//disabled : true,//设置是否可用
				checked: true//设置选中
			};
		}
	}
	return value;
}
	
function commitDft(){
	//var selectContent = $("#defectList").bootstrapTable('getSelections');
	
	if(selectDftIds.length <=0) {
    	 layer.alert('请选择一列数据！', {icon: 0});
       return false;
    }else{
    	var ids = '';
    	var codes = '';
    	for(var i =0;i<selectDftIds.length;i++){
    		ids += selectDftIds[i].id+",";
    		codes += selectDftIds[i].defectCode+",";
    	}
		if($("#dftbutton").attr("data-dft") == "new"){
			$("#newdefectId").val(ids);
			$("#newdefectCode").val(codes).change();
		}
		if($("#dftbutton").attr("data-dft") == "edit"){
			$("#editdefectId").val(ids);
			$("#editdefectCode").val(codes).change();
		}
        if($("#dftbutton").attr("data-dft") == "newSyn"){
            $("#newSynDefectId").val(ids);
            $("#newSynDefectCode").val(codes).change();
        }
        if($("#dftbutton").attr("data-dft") == "editSyn"){
            $("#editSynDefectId").val(ids);
            $("#editSynDefectCode").val(codes).change();
        }
		$("#defModal").modal('hide');
    }
}

function resetDft(){
    $("#defectCode").val('');
    $("#defectSummary").val('');
    $("#defectStatus").selectpicker('val', '');
    $(".color1 .btn_clear").css("display","none");
}
//=========================缺陷弹框end ======================

//=========================需求弹框start=====================
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
      url:"/projectManage/requirement/getDataDicList",
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
			}
			if($("#reqbutton").attr("data-req") == "edit"){
				$("#editrequirementId").val(selectContent.id);
				$("#editrequirementName").val(selectContent.REQUIREMENT_CODE).change();
			}
	    }
	}

	$("#selReq").modal("hide");
	
}

//重置
function clearSearchReq() {	
    $('#reqCode').val("");
    $('#reqName').val("");
    $("#reqStatus").selectpicker('val', '');
    $(".color1 .btn_clear").css("display","none");
}
//===========================需求弹框end=================================

//====================涉及系统弹框start==========================
function systemTableShow2(){
	if($("#z_project_Id").val()){
		$("#SC_project_body").show();
	}   
	$("#systemTable2").bootstrapTable('destroy');
	$("#systemTable2").bootstrapTable({  
		url:"/devManage/systeminfo/selectAllSystemInfo",
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
				let sys_name = $.trim($("#SCsystemName").val());
					var param={ 
						systemName:sys_name,
						systemCode:$.trim($("#SCsystemCode").val()),
						projectId:$("#newProject_listId").val(),
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
	if($("#z_project_Id").val()){
		$("#SC_project_body").hide();
	} 
	$("#systemTable2").bootstrapTable('destroy');
	$("#systemTable2").bootstrapTable({  
		url:"/devManage/systeminfo/selectAllSystemInfo2",
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
	    	$("#systemNameReq").val(names).change();
	 	}
	 }else{
		 var selectContent = $("#systemTable2").bootstrapTable('getSelections')[0];
	     if(typeof(selectContent) == 'undefined') {
	    	 layer.alert('请选择一列数据！', {icon: 0});
	        return false;
	     }else{
	    	 if($("#sysbutton").attr("data-sys") == "new"){
	     		$("#newsystemId").val(selectContent.id); 
	     		$("#newsystemId").attr( "systemCode", selectContent.systemCode);  
	     		if(selectContent.developmentMode!=null && selectContent.developmentMode!=undefined){
	   	    		$("#ndevelopmentMode").val(selectContent.developmentMode);
	   	    	}else{
	   	    		$("#ndevelopmentMode").val('');
	   	    	}
	  	    	$("#newsystemName").val(selectContent.systemName).change();
	     	 }
	     	 if($("#sysbutton").attr("data-sys") == "edit"){
	      		$("#editsystemId").val(selectContent.id);
	      		$("#editsystemId").attr( "systemCode", selectContent.systemCode);
	      		if(selectContent.developmentMode!=null && selectContent.developmentMode!=undefined){
	   	    		$("#edevelopmentMode").val(selectContent.developmentMode);
	   	    	}else{
	   	    		$("#edevelopmentMode").val('');
	   	    	}
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
	 $(".color1 .btn_clear").css("display","none");
}
//====================涉及系统弹框end==========================

//====================人员弹框start=======================
function userTableShow2(){
	 $("#loading").css('display','block');
	var projectIds = $("#project").val();
	if(projectIds!=null && projectIds!=''){
		projectIds = projectIds.join(",");
	}
	if($("#userbutton").attr("data-user")== 'list' || $("#userbutton").attr("data-user")== 'list2'){
		$("#userTableReqF").bootstrapTable('destroy');
		$("#userTableReqF").bootstrapTable({  
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
		$("#userTableReqF").bootstrapTable('destroy');
		$("#userTableReqF").bootstrapTable({  
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
	 $("#loading").css('display','block');
	var projectIds = $("#project").val();
	if(projectIds!=null && projectIds!=''){
		projectIds = projectIds.join(",");
	}
	if($("#userbutton").attr("data-user")== 'list' || $("#userbutton").attr("data-user")== 'list2'){
		$("#userTableReqF").bootstrapTable('destroy');
		$("#userTableReqF").bootstrapTable({  
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
		$("#userTableReqF").bootstrapTable('destroy');
		$("#userTableReqF").bootstrapTable({  
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
}*/

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
}*/
function commitUserReqF(){	 
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
		var selectContent = $("#userTableReqF").bootstrapTable('getSelections')[0]; 
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
				 $("#newdevManageUser").val(selectContent.id);
				 $("#newdevManageUserName").val(selectContent.userName).change();
			 }
			 if($("#userbutton").attr("data-user")==  'editMan'){
				 $("#editdevManageUser").val(selectContent.id);
				 $("#editdevManageUserName").val(selectContent.userName).change();
			 }
			 
			 if($("#userbutton").attr("data-user")==  'transfer'){
				 $("#transferId").val(selectContent.id);
				 $("#transferUserName").val(selectContent.userName).change();
			 }
			 if($("#userbutton").attr("data-user")=="split"){
				 $("#sWorkDivid").val(selectContent.id);
				 $("#sWorkDividUserName").val(selectContent.userName).change();
			 }
			 if($("#userbutton").attr("data-user")=="allupdate"){
				 $("#updateexecuteUserId").val(selectContent.id);
				 $("#updateexecuteUserName").val(selectContent.userName).change();
			 }
			 if($("#userbutton").attr("data-user")=="other"){
				 $( thisInputVal ).prev().val(selectContent.id);
				 $( thisInputVal ).val( selectContent.userName ).change();
			 }
	    }
	}
	 
	$("#userModalreqF").modal("hide");

}

function clearSearchUser(){
	 $("#userNameReqF").val('');
	 $("#employeeNumber").val('');
	/* $("#deptName").selectpicker('val', '');
	 $("#companyName").selectpicker('val', '');*/
	 $("#deptName").val('');
	 $("#companyName").val('');
	 $("#project").selectpicker('val', '');
	 $(".color1 .btn_clear").css("display","none");
}
//====================人员弹框end=======================

//=========================项目计划弹窗start=====================
function planTableShow(){
	var systemId;
    if($("#planbutton").attr("data-ProjectPlan") == "new"){
        systemId = $("#newsystemId").val();
    }else{
        systemId = $("#editsystemId").val();
	}
    $("#planTable2").bootstrapTable('destroy');
    $("#planTable2").bootstrapTable({
        url:"/devManage/devtask/getAllProjectPlan",
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
                planCode : $.trim($("#planCode").val()),
                planName : $.trim($("#planName").val()),
                responsibleUserName : $("#responsibleUserName").val(),
                projectName : $("#projectName").val(),
                systemId : systemId,
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
            field : "PLAN_CODE",
            title : "计划编号",
            align : 'center'
        },{
            field : "PLAN_NAME",
            title : "计划名称",
            align : 'center'
        },{
            field : "projectName",
            title : "所属项目",
            align : 'center'
        },{
            field : "responsibleUserName",
            title : "责任人",
            align : 'center'
        }],
        onLoadSuccess:function(){

        },
        onLoadError : function() {

        }
    });
}

function commitPlan(){
	var selectContent = $("#planTable2").bootstrapTable('getSelections')[0];
	if(typeof(selectContent) == 'undefined') {
		layer.alert('请选择一列数据！', {icon: 0});
		return false;
	}else{
		if($("#planbutton").attr("data-ProjectPlan") == "new"){
			$("#newProjectPlanId").val(selectContent.id);
			$("#newProjectPlanName").val(selectContent.PLAN_NAME).change();
		}
        if($("#planbutton").attr("data-ProjectPlan") == "edit"){
            $("#editProjectPlanId").val(selectContent.id);
            $("#editProjectPlanName").val(selectContent.PLAN_NAME).change();
		}
	}

    $("#selProjectPlan").modal("hide");
}

//重置
function clearSearchPlan() {
    $('#planCode').val("");
    $('#planName').val("");
    $('#responsibleUserName').val("");
    $('#projectName').val("");
    $(".color1 .btn_clear").css("display","none");
}
//===========================项目计划弹窗end=================================


//获取当前登录用户所在项目（结项的项目除外） ztt
function getProject(){
	$.ajax({
		url:"/devManage/displayboard/getAllProject",
		dataType:"json",
		type:"post",
		async:false,
		success:function(data){
			if(data.projects!=undefined && data.projects.length>0){
				for(var i=0;i <data.projects.length;i++){
					$("#project").append("<option selected value='"+data.projects[i].id+"'>"+data.projects[i].projectName+"</option>")
					
				}
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
}
