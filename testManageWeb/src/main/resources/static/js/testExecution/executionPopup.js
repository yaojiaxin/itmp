var typeName="";
/*-----------------测试集弹框--------------*/
/*function TestSetShow() {
	$("#loading").css('display', 'block');
	$("#testSetTable").bootstrapTable('destroy');
	$("#testSetTable")
			.bootstrapTable(
					{
						url : "/testManage/testSet/getAllTestSet2",
						method : "post",
						contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
						queryParamsType : "",
						pagination : true,
						sidePagination : "server",
						pageNumber : 1,
						pageSize : 10,
						pageList : [ 10, 25, 50, 100 ],
						singleSelect : true,// 单选
						queryParams : function(params) {
							var param = {
									page : params.pageNumber,
									rows : params.pageSize,
									testSetNumber : $.trim($("#testSetCode").val()),
									testSetName : $.trim($("#testSetName").val()),
								
							};
							return param;
						},
						columns : [
								{
									checkbox : true,
									width : "30px"
								},
								{
									field : "id",
									title : "id",
									visible : false,
									align : 'center'
								},
								{
									field : "testSetNumber",
									title : "测试集编号",
									align : 'center'
								},
								{
									field : "testSetName",
									title : "测试集名称",
									align : 'center'
								},
								{
									field : "excuteRound",
									title : "轮次",
									align : 'center'
								} ],
						onLoadSuccess : function() {
							$("#loading").css('display', 'none');
						},
						onLoadError : function() {

						}
					});
}*/
//案例执行步骤
/*function TestSetStepShow(setCaseID) {
	$("#caseSteps").bootstrapTable('destroy');
	$("#caseSteps").bootstrapTable(
		{
			url : "/testManage/testExecution/getTestCaseRun",
			method : "post",
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType: "json",
		    pagination: false, //分页
		    //sidePagination: "server", //服务端处理分页
			queryParams : function(params) {
				var param = {
						id :setCaseID,
				};
				return param;
			},
			columns : [ 
				{	field : 'checked',
					checkbox : true,
					visible : false,
					formatter :function(value, rows,index){
							    return true; 
					}
				},
				{
				field : "id",
				title : "id",
				visible : false,
				align : 'left'
			}, {
				field : "STEP_ORDER",
				title : "步骤",
				align : 'left',
				
			},{
				field : "STEP_DESCRIPTION",
				title : "步骤描述",
				align : 'left', 
			},{
				field : "STEP_EXPECTED_RESULT",
				title : "预期结果",
				align : 'left', 
			},{
				field : "testExecuteResult",
				title : "执行结果",
				align : 'left', 
				formatter : function(value, row, index) {
					var select="<select fields='testExecuteResult' onchange='executionResult("+index+",this)' class='excutionResult' id='' name='testExecutionStep'>";
					  var reqSourceList = $("#executionSelect").find("option");
					  for (var i = 0,len = reqSourceList.length;i < len;i++) {
						  select+="<option value='"+reqSourceList[i].value+"'>"+reqSourceList[i].innerHTML+"</option>";
		                 
		                }
					
					  select+="</select>"     
						
					return select;
				},
				
			},{
				field : "executionSituation",
				title : "执行情况", 
				width:"200",
				formatter : function(value, row, index) {
					var select="";
					select="<input fields='executionSituation' onchange='executionResult("+index+",this)' type='text' class='form-control'  value=''>";
					return select;
				}
			}],
			onLoadSuccess : function() {
				$("#loading").css('display', 'none');
			},
			onLoadError : function() {

			}
		});
}*/

/*function executionResult(index,This){
	  var value = $(This).val();
	 var field=$(This).attr("fields")
	var rows = {
            index : index,  //更新列所在行的索引
            field : field, //要更新列的field
            value : value //要更新列的数据
        }//更新表格数据        
$('#CaseSteps').bootstrapTable("updateCell",rows);
	 
	$("#caseSteps").bootstrapTable('updateCell',{
		index:index,
		field:'testExecuteResult',
		value:value
	});
}*/


/*-----------------工作任务弹框--------------*/
function EtestTaskShow() {
	$("#loading").css('display', 'block');
	$("#testTaskTable").bootstrapTable('destroy');
	$("#testTaskTable")
			.bootstrapTable(
					{
						url : "/testManage/modal/getAllTestTask",
						method : "post",
						contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
						queryParamsType : "",
						pagination : true,
						sidePagination : "server",
						pageNumber : 1,
						pageSize : 10,
						pageList : [ 10, 25, 50, 100 ],
						singleSelect : true,// 单选
						queryParams : function(params) {
							var param = {
								pageNumber : params.pageNumber,
								pageSize : params.pageSize,
								testTaskCode : $.trim($("#taskCode").val()),
								testTaskName : $.trim($("#taskName").val()),
								testTaskStatus : $.trim($("#taskState").find(
										"option:selected").val())
							};
							return param;
						},
						columns : [
								{
									checkbox : true,
									width : "30px"
								},
								{
									field : "id",
									title : "id",
									visible : false,
									align : 'center'
								},
								{
									field : "testTaskCode",
									title : "任务编号",
									align : 'center'
								},
								{
									field : "testTaskName",
									title : "任务名称",
									align : 'center'
								},
								{
									field : "testTaskStatus",
									title : "状态",
									align : 'center',
									formatter : function(value, row, index) {
										var className = "doing";
										var taskStateList = $("#taskState")
												.find("option");
										for (var i = 0, len = taskStateList.length; i < len; i++) {
											if (row.testTaskStatus == taskStateList[i].value) {
												className += row.testTaskStatus;
												return "<span>"
														+ taskStateList[i].innerHTML
														+ "</span>";
											}
										}
									}
								}, {
									field : "systemId",
									title : "systemId",
									visible : false,
									align : 'center'
								}, {
									field : "systemName",
									title : "systemName",
									visible : false,
									align : 'center'
								} ],
						onLoadSuccess : function() {
							$("#loading").css('display', 'none');
						},
						onLoadError : function() {

						}
					});
}
//测试集确认
/*function TestSetOk() {
		var rowData = $("#testSetTable").bootstrapTable('getSelections')[0];
		if (typeof (rowData) == 'undefined') {
			layer.alert("请选择一条数据", {
				icon : 2,
				title : "提示信息"
			})
		} else {
			$("#testSetNumber").val(rowData.testSetNumber);
			$("#testSetModal").modal("hide");
		}
	
}*/
// 工作确认
function selectTask(typeName) {
	if(typeName=="search"){
		var rowData = $("#testTaskTable").bootstrapTable('getSelections')[0];
		if (typeof (rowData) == 'undefined') {
			layer.alert("请选择一条数据", {
				icon : 2,
				title : "提示信息"
			})
		} else {
			$("#editTestTaskName").val(rowData.testTaskName);
			$("#editTestTaskName").attr( 'idValue',rowData.id );//工作任务ID
			$("#editSystemName").attr( 'idValue',rowData.systemId );//涉及系统
	    	$("#editSystemName").val( rowData.systemName );
			$("#testTaskModal").modal("hide");
		}
	}else if(typeName=="defect"){
		var rowData = $("#testTaskTable").bootstrapTable('getSelections')[0];
		if (typeof (rowData) == 'undefined') {
			layer.alert("请选择一条数据", {
				icon : 2,
				title : "提示信息"
			})
		} else {
			$("#testTaskId").val(rowData.id);
            $("#testTaskName").val(rowData.testTaskName);
            $("#systemId").val(rowData.systemId);
            $("#system_Name").val(rowData.systemName);
            $("#new_requirementCode").val(rowData.requirementCode);
            $("#new_commissioningWindowId").val(rowData.commissioningWindowId);
            $("#testTaskModal").modal("hide");
		}
	}
	 $('.selectpicker').selectpicker('refresh');
}
// 清除工作任务搜索
function cleanTextTask() {
	$("#taskCode").val("");
	$("#taskName").val("");
	$("#taskState").selectpicker('val', '');
}
// 显示工作任务
function shouTestTask(date) {
	if(date=="search"){
		cleanTextTask();
		EtestTaskShow();
		typeName="search"
		$("#testTaskModal").modal("show");	
	}else if(date=="defect"){
		cleanTextTask();
		EtestTaskShow();
		typeName="defect"
		$("#testTaskModal").modal("show");
	}
	

}
//测试集
/*function showTestSet() {
	TestSetShow();
	cleanTestSet();
	$("#testSetModal").modal("show");

}*/
//清除测试集
/*function cleanTestSet(){
	$("#testSetCode").val("");
	$("#testSetName").val("");
}*/

// 清除人员弹窗
function userReset() {
	$("#userName").val('');
	$("#deptName").val("");
	$("#companyName").val("");
	$(".color1 .btn_clear").css("display", "none");

}
// 提交缺陷
function submitDefect() {

	$('#newDefectFrom').data('bootstrapValidator').validate();
	if (!$('#newDefectFrom').data('bootstrapValidator').isValid()) {
		return false;
	}
	var assignUserId = $("#new_assignUserId").val();

	if (assignUserId == '') {
		layer.alert("必须选择指派人！", {
			icon : 2,
			title : "提示信息"
		})
	} else {
		updateDefectStatus(2);
	}

}

// 暂存缺陷
function stagDefect() {
	$('#newDefectFrom').data('bootstrapValidator').validate();
	if (!$('#newDefectFrom').data('bootstrapValidator').isValid()) {
		return false;
	}
	updateDefectStatus(1);
}

// 新增 编辑 缺陷
function updateDefectStatus(defectStatus) {
	var files = new FormData();
    if(formFileList.length > 0 ){
        var filesSize = 0;
        for (var i = 0,len2 = formFileList.length;i < len2;i++){
            filesSize += formFileList[i].size;
            files.append("files",formFileList[i]);
        }

        if(filesSize > 1048576000){
            layer.alert('文件太大,请删选！！！', {
                icon: 2,
                title: "提示信息"
            });
            return false;
        }

    }
	$("#loading").css('display', 'block');
	$("#new_defectSource").removeAttr("disabled");
	 var obj = {};
     obj.assignUserId = $("#new_assignUserId").val();
     obj.testTaskId = $("#testTaskId").val();
     obj.requirementCode = $("#new_requirementCode").val();
     obj.systemId = $("#systemId").val();
     obj.testSetCaseExecuteId = $("#testSetCaseExecuteId").val();
     obj.caseNumber=$("#testcaseNumber").val();
     obj.commissioningWindowId = $("#new_commissioningWindowId").val();
     obj.defectSummary = $.trim($("#new_defectSummary").val());
     obj.repairRound = $.trim($("#new_repairRound").val());
     obj.defectOverview = $.trim($("#defectOverview").val());
     obj.defectType = $.trim($("#new_defectType").find("option:selected").val());
     obj.defectSource = $.trim($("#new_defectSource").find("option:selected").val());
     obj.severityLevel = $.trim($("#new_severityLevel").find("option:selected").val());
     obj.emergencyLevel = $.trim($("#new_emergencyLevel").find("option:selected").val());
     obj.defectStatus = defectStatus;
     files.append("defectInfo",JSON.stringify(obj));
	// 新增缺陷
     $.ajax({
         type: "post",
         url : '/zuul/testManage/defect/insertDefect',
         dataType: "json",
         data:files,
         cache: false,
         processData: false,
         contentType: false,
         success: function(data) {
             $("#loading").css('display','none');
             if (data.status == 2){
                 layer.alert(data.errorMessage, {
                     icon: 2,
                     title: "提示信息"
                 });
             } else if(data.status == 1){
                 layer.alert('操作成功！', {
                     icon: 1,
                     title: "提示信息"
                 });

                 $("#commitBug").modal("hide");
 				$( ".rightCaseDivNew" ).css( "display","none" );
                 //reset_opt();
                 formFileList = [];
                 editAttList = [];
                 getAllExecuteCase();
             }
         },
         error:function(){
             $("#loading").css('display','none');
             layer.alert(errorDefect, {
                 icon: 2,
                 title: "提示信息"
             });
         }
     })

}
//确认用户
function commitUser(){
    var rowData = $("#userTable").bootstrapTable('getSelections')[0];
    if(typeof(rowData) == 'undefined') {
        layer.alert("请选择一条数据",{
            icon:2,
            title:"提示信息"
        })
    }else {
       
            $("#new_assignUserId").val(rowData.id);
            $("#new_assignUserName").val(rowData.userName);
       
        $("#userModal").modal("hide");
    }
}
// 显示用户
function userInit() {
	userReset();
    var notWithUserID = $("#new_assignUserId").val();
	var systemId=$("#systemId").val();
	userTableShow(notWithUserID,systemId,true);
	$("#userModal").modal("show");
}


function getExcelByExcuteRound(testSetId,excuteRound){
	window.location.href="/testManage/testExecution/getExcelByExcuteRound?testSetId="+testSetId+"&excuteRound="+excuteRound;
}
function getExcelAllTestSet(testSetId,excuteRound){
	window.location.href="/testManage/testExecution/getExcelAllTestSet?testSetId="+testSetId+"&excuteRound="+excuteRound;
}


