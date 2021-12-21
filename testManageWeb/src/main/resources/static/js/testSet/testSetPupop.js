$(function(){
	$("#createBy").click(function(){
		$("#userbutton").attr("data-user",$(this).attr("data-user"));
		userArr = [];
		clearSearchUser();
   	 	userTableShow("/system/user/getAllUserModal2",'');
   	 	$("#searchFlag").val("");
		$("#userModal").modal('show');
	});
	
	$("[name=checkTestTask]").click(function(){
		var dataTask = $(this).attr("data-task");
		taskArr = [];
		var isSingle = true;
		if(dataTask == 'search'){
			isSingle = false;
		}
		$("#taskbutton").attr("data-task",dataTask);
		clearSearchTask();
		taskTableShow(isSingle);
		$("#testTaskModal").modal('show');
	});
	
	$("#relateCase").click(function(){
		caseArr = [];
		clearSearchCase();
		caseTableShow();
		$("#testCaseModal").modal('show');
	})
	
	$("#systemName").click(function(){
		clearSystem();
		systemTableShow();
		$("#systemModal").modal('show');
	})
});
var userArr = [];
var taskArr = [];
var caseArr = [];
//=========================工作任务弹框start=====================
function taskTableShow(isSingle){
	$("#loading").css('display','block');
	 $("#listTask").bootstrapTable('destroy');
	 $("#listTask").bootstrapTable({  
		url:'/testManage/worktask/getAllWorktask2', 
		method:"post",
	    queryParamsType:"",
	    pagination : true,
	    sidePagination: "server",
	    contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
	    pageNumber : 1,
	    pageSize : 10,
	    pageList : [ 5, 10, 15],
	    singleSelect : isSingle,//单选
	    queryParams : function(params) {
         var param={ 
        	testTaskCode : $.trim($("#taskCode").val()),
        	testTaskName : $.trim( $("#taskName").val()),
        	testTaskStatus : $.trim( $("#taskState").val()),
    		page:params.pageNumber,
    		rows:params.pageSize 
         }
        return param;
    },
    columns : [{
        checkbox : true,
        width : "30px",
        formatter : function(value, grid, rows,
				state){
        	var flag = false;
        	$.each(taskArr,function(index,value){
        		if(value.id == grid.id){
        			flag = true;
        		}
        	})
        	return flag;
        }
    },{
        field : "id",
        title : "id",
        visible : false,
        align : 'center'
    },{
        field : "testTaskCode",
        title : "任务编号",
        align : 'center'
    },{
        field : "testTaskName",
        title : "任务名称",
        align : 'center'
    },{
    	field : "workTaskStatus",
    	title : "任务状态",
    	align : 'center'
    }],
    onLoadSuccess:function(){
    	$("#loading").css('display','none');
    },
    onLoadError : function() {

    },
    onCheck:function(row,dom){
    	if(!isSingle){
    		taskArr.push(row);
    	}
    },
    onUncheck:function(row,dom){
    	$.each(taskArr,function(index,value){
    		if(value!=undefined && row.id == value.id){
    			taskArr.splice(index,1);
    		}
    	})
    },
    onCheckAll:function(rows){
    	var length = taskArr.length;
    	$.each( rows,function(index,value){
    		var i = 0;
    		for( ;i < length;i++){
    			if(taskArr[i].id == value.id){
    				break;
    			}
    		}
    		if(i == length){
    			taskArr.push(value);
			}
    	} );
    },
    onUncheckAll:function(rows){
    	for(var i = taskArr.length-1;i >= 0;i--){
    		for(let value2 of rows){
    			if( taskArr[i].id == value2.id){
    				taskArr.splice(i,1);
    				break;
    			}
    		}
    	}
    }
});
}
//清空
function clearSearchTask(){
	$("#taskCode").val('');
	$("#taskName").val('');
	$("#taskState").selectpicker('val','');
}

function commitTask(){
	var selectContents = $("#listTask").bootstrapTable('getSelections');
	var dataTask = $("#taskbutton").attr("data-task") == 'search';
	selectContents = dataTask?taskArr:selectContents;
    if(typeof(selectContents[0]) == 'undefined') {
    	 layer.alert('请选择一列数据！', {icon: 0});
       return false;
    }else{
		 if(dataTask){
			 var ids = [];
			 var name = '';
			 $.each(selectContents,function(index,value){
				 ids.push(value.id);
				 name += value.testTaskName + ',';
			 })
			 $("#workTaskId").val(ids);
				$("#workTaskName").val(name.substr(0,name.length-1));
				$("#workTaskName").next().css('display','block');
		 }else{
			 $("#checkTestTask").attr("idValue",selectContents[0].id);
			 $("#checkTestTask").val(selectContents[0].testTaskName);
		 }
		$("#testTaskModal").modal("hide");
	 }
}
/*新增执行人触发弹窗*/
function addExecuteUserShow(round){
	$("#userbutton").attr("data-user","add");
	$("#userbutton").attr("round",round);
	userArr = [];
	clearSearchUser();
	userTableShow("/testManage/testSet/getUserTable",round);
	$("#searchFlag").val(round);
	$("#userModal").modal('show');
}
/*搜索人员*/
function searchUser(){
	var round = $("#searchFlag").val();
	if(round == ""){
		userTableShow("/system/user/getAllUserModal2",'');
	}else{
		userTableShow("/testManage/testSet/getUserTable",round);
	}
}

//====================人员弹框start=======================
function userTableShow(url,round){
	var testSetId = $("#taskTestId").val();
	$("#loading").css('display','block');
	   $("#userTable").bootstrapTable('destroy');
	    $("#userTable").bootstrapTable({  
	    	url:url,
	    	method:"post",
	        queryParamsType:"",
	        pagination : true,
	        sidePagination: "server",
	        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
	        pageNumber : 1,
	        pageSize : 10,
	        pageList : [ 5, 10, 15],
	        singleSelect : false,//多选
	        queryParams : function(params) {
	             var param={ 
            		 userName: $.trim($("#userName").val()),
            		 companyName :  $.trim($("#companyName").val()),
            		 deptName : $.trim($("#deptName").val()),
	                 pageNumber:params.pageNumber,
	                 pageSize:params.pageSize, 
	                 testSetId:testSetId,
	                 executeRound:round
	             }
	            return param;
	        },
	        columns : [{
	            checkbox : true,
	            width : "30px",
	            formatter : function(value, grid, rows,
						state){
	            	var flag = false;
	            	$.each(userArr,function(index,value){
	            		if(value.id == grid.id){
	            			flag = true;
	            		}
	            	})
	            	return flag;
	            }
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
	        onCheck:function(row,dom){
	        	userArr.push(row);
	        },
	        onUncheck:function(row,dom){
	        	$.each(userArr,function(index,value){
	        		if(value!=undefined && row.id == value.id){
	        			userArr.splice(index,1);
	        		}
	        	})
	        },
	        onCheckAll:function(rows){
	        	var length = userArr.length;
	        	$.each( rows,function(index,value){
	        		var i = 0;
	        		for( ;i < length;i++){
	        			if(userArr[i].id == value.id){
	        				break;
	        			}
	        		}
	        		if(i == length){
        				userArr.push(value);
        			}
	        	} );
	        },
	        onUncheckAll:function(rows){
	        	for(var i = userArr.length-1;i >= 0;i--){
	        		for(let value2 of rows){
	        			if( userArr[i].id == value2.id){
	        				userArr.splice(i,1);
	        				break;
	        			}
	        		}
	        	}
	        }
	    });   
} 

function clearSearchUser(){
	 $("#userName").val('');
	 $("#deptName").val('');
	 $("#companyName").val('');
	 $(".color1 .btn_clear").css("display","none");
}

function commitUser(){
	var selectContents = userArr;
    if(typeof(selectContents[0]) == 'undefined') {
    	 layer.alert('请选择一列数据！', {icon: 0});
       return false;
    }else{
    	var ids = [];
    	var name = '';
		 $.each(selectContents,function(index,value){
			 ids.push(value.id);
			 name += value.userName + ',';
		 })
		 if($("#userbutton").attr("data-user")== 'search'){
			$("#userId").val(JSON.stringify(ids));
			$("#createBy").val(name.substr(0,name.length-1));
			$("#createBy").next().css('display','block');
		 }else{
			 var round = $("#userbutton").attr("round");
			 addExecuteUser(ids,round);
		 }
		$("#userModal").modal("hide");
	 }
}

function addExecuteUser(userIds,round){
	var testSetId = $("#taskTestId").val();
	$("#loading").css('display','block');
	$.ajax({
        type: "post",
        url: "/testManage/testSet/addExcuteUser",
        dataType: "json",
        data: {
        	testSetId : testSetId,
        	executeRound : round,
        	userIdStr : JSON.stringify(userIds)
        },
        success: function(data) {
            if(data.status == 2){
            	layer.alert("新增执行人失败",{icon : 2});
            }else{
            	layer.alert("新增执行人成功",{icon : 1});
            	assignPerson();
            	$("#loading").css('display','none');
            }
        }
    });
}

//====================测试案例弹框start=======================
function caseTableShow(){
	$("#loading").css('display','block');
	   $("#listCase").bootstrapTable('destroy');
	    $("#listCase").bootstrapTable({  
	    	url:"/testManage/testCase/getCaseInfo",
	    	method:"post",
	        queryParamsType:"",
	        pagination : true,
	        sidePagination: "server",
	        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
	        pageNumber : 1,
	        pageSize : 10,
	        pageList : [ 5, 10, 15],
	        queryParams : function(params) {
	             var param={ 
	            	 testSetId: $.trim($("#taskTestId").val()),
	            	 caseCode: $.trim($("#caseCode").val()),
	            	 caseName: $.trim($("#caseName").val()),
	                 page:params.pageNumber,
	                 rows:params.pageSize 
	             }
	            return param;
	        },
	        columns : [{
	            checkbox : true,
	            width : "30px",
	            formatter : function(value, grid, rows,
						state){
	            	var flag = false;
	            	$.each(caseArr,function(index,value){
	            		if(value.id == grid.id){
	            			flag = true;
	            		}
	            	})
	            	return flag;
	            }
	        },{
	            field : "id",
	            title : "id",
	            visible : false,
	            align : 'center'
	        },{
	            field : "caseNumber",
	            title : "案例编号",
	            align : 'center'
	        },{
	            field : "caseName",
	            title : "案例名称",
	            align : 'center'
	        },{
	        	field : "systemName",
	        	title : "涉及系统",
	        	align : 'center'
	        },{
	            field : "userName",
	            title : "创建人",
	            align : 'center'
	        }],
	        onLoadSuccess:function(){
	        	$("#loading").css('display','none');
	        },
	        onLoadError : function() {

	        },
	        onCheck:function(row,dom){
	        	caseArr.push(row);
	        },
	        onUncheck:function(row,dom){
	        	$.each(caseArr,function(index,value){
	        		if(value!=undefined && row.id == value.id){
	        			caseArr.splice(index,1);
	        		}
	        	})
	        },
	        onCheckAll:function(rows){
	        	var length = caseArr.length;
	        	$.each( rows,function(index,value){
	        		var i = 0;
	        		for( ;i < length;i++){
	        			if(caseArr[i].id == value.id){
	        				break;
	        			}
	        		}
	        		if(i == length){
	        			caseArr.push(value);
        			}
	        	} );
	        },
	        onUncheckAll:function(rows){
	        	for(var i = caseArr.length-1;i >= 0;i--){
	        		for(let value2 of rows){
	        			if( caseArr[i].id == value2.id){
	        				caseArr.splice(i,1);
	        				break;
	        			}
	        		}
	        	}
	        }
	    });   
} 
/*清空*/
function clearSearchCase(){
	 $("#caseName").val('');
	 $("#caseCode").val('');
	 $(".color1 .btn_clear").css("display","none");
}

function commitCase(){
	var selectContents = caseArr;
	var testSetId = $("#taskTestId").val();
	var caseNumbers = [];
	$.each(selectContents,function(index,value){
		caseNumbers.push(value.caseNumber);
	})
	$("#loading").css('display','block');
	$.ajax({
        type: "post",
        url: "/testManage/testSet/relateTestSetCase",
        dataType: "json",
        data: {
        	'caseStr' : JSON.stringify(selectContents),
        	'idStr' : JSON.stringify(caseNumbers),
        	'testSetId': testSetId
        },
        success: function(data) {
           if(data.status == 2){
        	   layer.alert("关联失败",{icon : 2});
           }else{
        	   initCaseTable( testSetId );
        	   $("#testCaseModal").modal('hide');
        	   layer.alert("关联成功",{icon : 1});
           }
           $("#loading").css('display','none');
        }
    });
}
//====================涉及系统弹框start==========================
function systemTableShow(){  
	$("#systemTable").bootstrapTable('destroy');
    $("#systemTable").bootstrapTable({  
    	url:"/testManage/modal/selectAllSystemInfo",
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

function clearSystem(){
	$("#SCsystemName").val("");
	$("#SCsystemCode").val("");
}

function commitSys(){
	var selectContent = $("#systemTable").bootstrapTable('getSelections')[0];
    if(typeof(selectContent) == 'undefined') {
    	 layer.alert('请选择一列数据！', {icon: 0});
       return false;
    }else{
    	$("#systemId").val(selectContent.id);
    	$("#systemName").val(selectContent.systemName);
    }
    $("#systemModal").modal('hide');
}