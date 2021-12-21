var editAttList = [];
var formFileList = [];
var defectAttIds = [];
var dateTable=""
var userType="";
$(function(){
	uploadFileList();
	//executeResult();
	upFileList();
    
    $("#new_assignUserName").click(function(){
    	userType="defect";
    	select_rows = new Array();
    	userInit();
    });
    //搜索展开和收起
    /*$("#downBtn").click(function () { 
        if( $(this).children("span").hasClass( "fa-caret-up" ) ){
            $(this).children("span").removeClass("fa-caret-up");
            $(this).children("span").addClass("fa-sort-desc");
            $("#search_div_test").slideUp(200);
        }else {
            $(this).children("span").removeClass("fa-sort-desc");
            $(this).children("span").addClass("fa-caret-up");
            $("#search_div_test").slideDown(200);
        }
    })*/
    
// 搜索框 收藏按钮 js 部分
    /*$(".collection").click(function () {
        if( $(this).children("span").hasClass("fa-heart-o") ){
            $(this).children("span").addClass("fa-heart");
            $(this).children("span").removeClass("fa-heart-o");
        }else {
            $(this).children("span").addClass("fa-heart-o");
            $(this).children("span").removeClass("fa-heart");
        }
    })
    //所有的Input标签，在输入值后出现清空的按钮
    $('.color1 input[type="text"]').parent().css("position","relative");
    $('.color1 input[type="text"]').parent().append("<span onclick='clearContent(this)' class='btn_clear'></span>");
    $('.color1 input[type="text"]').bind("input propertychange",function(){
        if( $(this).val()!="" ){
            $(this).parent().children(".btn_clear").css("display","block");
        }else {
            $(this).parent().children(".btn_clear").css("display","none");
        }
    })*/
    formValidator();
    refactorFormValidator();
});


/*//表格数据加载
function pageInit(){
	//$("#loading").css('display','block');
	$("#testExecutionTable").jqGrid('clearGridData');
    $("#testExecutionTable").jqGrid({
        url:'/testManage/testExecution/getTestExecute',
        datatype: 'json', 
        contentType: "application/json; charset=utf-8",
        mtype:"post",
        height: 'auto',
        width: $(".content-table").width()*0.999,
        colNames:['id','testCase','测试集编号','轮次','案例编号', '案例名称','所属任务编码','执行状态','操作'],
        colModel:[
            {name:'id',index:'id',hidden:true},
            {name:'setCaseID',index:'setCaseID',hidden:true},
            {name:'TEST_SET_NUMBER',index:'TEST_SET_NUMBER', searchoptions:{sopt:['cn']}},
            {name:'EXCUTE_ROUND',index:'EXCUTE_ROUND', searchoptions:{sopt:['cn']}},
            {name:'CASE_NUMBER',index:'CASE_NUMBER', searchoptions:{sopt:['cn']}},
            {name:'CASE_NAME',index:'CASE_NAME',searchoptions:{sopt:['cn']}},
            {name:'TEST_TASK_CODE',index:'TEST_TASK_CODE',searchoptions:{sopt:['cn']}},
            {name:'CASE_EXECUTE_RESULT',index:'CASE_EXECUTE_RESULT',searchoptions:{sopt:['cn']},
            	formatter : function(value,grid,rows,redis){
            		return '<span class="doing'+value+'">'+rows.redisValue+'</span>';
            	}
            	},
            {
                name:'操作',
                index:'操作',
                align:"center",
                fixed:true,
                sortable:false,
                resize:false,
                search: false,
                width : 260,
                formatter : function(value, grid, rows, state) {
                	var row = JSON.stringify(rows).replace(/"/g, '&quot;');
	            	var opt_status = [];
	            	opt_status.push('<a class="a_style" href="#" onclick="executeResult(\'' + row +'\')">执行结果</a>');
	            	opt_status.push('<a class="a_style" href="#" onclick="startExrcution(' + row + ')">开始执行</a>');
	            	opt_status.push('<a class="a_style" href="#" onclick="addDefect(\'' + rows.id +'\')">提交缺陷</a>');
	            	opt_status.push('<a class="a_style" href="#" onclick="edit(\'' + rows.id +'\')">修改案例</a>');
	            	return  opt_status.join('<span> | </span>');
                }
            }
        ],
        rowNum:10,
        rowTotal: 200,
        rowList : [10,20,30],
        
        rownumWidth: 40,
        pager: '#testExecutionPage',
        sortable:true,   //是否可排序
        sortorder: 'asc',
        sortname: 'id',
        loadtext:"数据加载中......",
        viewrecords: true, //是否要显示总记录数
        loadComplete :function(){
        	$("#loading").css('display','none');
        }
    }).trigger("reloadGrid");
    $("#testExecutionTable").jqGrid('filterToolbar',{searchOperators : true});
}*/
function tableClearSreach(){
	$(".ui-search-toolbar .ui-search-input input[type^=text]").val('');
	$(".ui-search-toolbar .ui-search-input select").val('0');
	$("#testExecutionTable").jqGrid('setGridParam',{ 
		postData : {  
			"Execute" : JSON.stringify({
				 caseName : "",
				 taskCode : "",
				 testSetNumber : "",
				 excuteRound : "",
			}), 
            "filters" : ""
        },  
        page:1,
        loadComplete :function(){
            $("#loading").css('display','none');
        }
    }).trigger("reloadGrid"); //重新载入
}
/**执行案例**/
/*function startExrcution( row ){ 
	modalType="new";
	clearTestCaseStep();
	$("#startExrcution").modal("show");
	$("#pecondition").html(row.CASE_PRECONDITION);
	$("#caseNumber").text(row.CASE_NUMBER);
	$("#textCaseName").text(row.CASE_NAME);
	dateTable=row;
	TestSetStepShow(row.setCaseID);
}*/

/* function getTestCaseStep(rows,type){
	 
	 var getSelectRows = JSON.stringify($("#CaseSteps").bootstrapTable('getData'));
	 var files=$("#files").val();
	 var rows=JSON.stringify(rows)
	 $.ajax({
		 url:"/testManage/testExecution/insetSetStepExecute",
		 method:"post", 
		 data:{
			 "rows":rows,
			 "enforcementResults":getSelectRows,
			 "type":type,
			 "files":files
		 }, 
		 success:function(data){  
			 
			 pageInit(); 
		 }, 
	 }); 
 }*/
 function clearTestCaseStep(){
	 $("#files").val("");
	 $("#addFileTable").empty();
 }
 
//清空搜索内容
function clearSearch() {
    $('#caseName').val("");
    $('#taskCode').val("");
    $("#testSetNumber").val("");
    $("#excuteRound").val("");
}
 	
/*//搜索
function searchInfo(){
	$("#loading").css('display','block');
		 var Execute = {};
		 Execute.caseName =$.trim($("#caseName").val());
		 Execute.taskCode = $.trim($("#taskCode").val());
		 Execute.testSetNumber = $.trim($("#testSetNumber").val());
		 Execute.excuteRound = $.trim($("#excuteRound").val());
		 Execute = JSON.stringify(Execute); 
	  //重新载入
		 
	 $("#testExecutionTable").jqGrid('setGridParam',{ 
		 url:'/testManage/testExecution/getTestExecute',
	        postData:{
	         "Execute":Execute
	        }, 
	        page:1
	    }).trigger("reloadGrid");  
	}*/



function newDefect_reset(){
	defectAttIds = [];
	$(".selectpicker").selectpicker('refresh');
    /*新建的字段*/
	
    $("#testTaskId").val('');
    $("#testTaskName").val('');
    $("#systemId").val('');
    $("#system_Name").val('');
    $("#testCaseId").val('');
    $("#testCaseName").val('');
    $("#new_repairRound").val('');
    $("#defectOverview").val('');
    $("#new_defectSummary").val('');

    $("#new_requirementCode").val('');
    $("#new_commissioningWindowId").val('');
    $("#testSetCaseExecuteId").val('');
    $("#testcaseNumber").val('');

    $("#new_defectType").selectpicker('val', '');
    $("#new_defectSource").selectpicker('val', '');
    $("#new_defectSource").attr("disabled",false);
    $("#new_severityLevel").selectpicker('val', '');
    $("#new_emergencyLevel").selectpicker('val', '');
    $("#new_assignUserId").val('');
    $("#new_assignUserName").val('');
    $("#newFileTable tbody").html("");

    // 清空定义数据
    formFileList = [];
    editAttList = [];
}
//新建缺陷
function addDefect(){
    defectAttIds = [];
    newDefect_reset();
    $(".selectpicker").selectpicker('refresh');
}

function down(This){
    if( $(This).hasClass("fa-angle-double-down") ){
        $(This).removeClass("fa-angle-double-down");
        $(This).addClass("fa-angle-double-up");
        $(This).parents('.allInfo').children(".def_content").slideDown(100);
    }else {
        $(This).addClass("fa-angle-double-down");
        $(This).removeClass("fa-angle-double-up");
        $(This).parents('.allInfo').children(".def_content").slideUp(100);
    }
}
//查看执行结果
/*function executeResult(){
	$.ajax({
		url:"/testManage/testExecution/getExecuteCaseDetails",
		method:"post", 
		data:{
			"testSetCaseExecuteId":1,
			"testSetCaseId":1
		}, 
		success:function(data){  
			
		}, 
	}); 
	
}*/
//日期转换
function datFmt(date,fmt) { // author: meizz
    var o = {
        "M+": date.getMonth() + 1, // 月份
        "d+": date.getDate(), // 日
        "h+": date.getHours(), // 小时
        "m+": date.getMinutes(), // 分
        "s+": date.getSeconds(), // 秒
        "q+": Math.floor((date.getMonth() + 3) / 3), // 季度
        "S": date.getMilliseconds() // 毫秒
    };  
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));  
    for (var k in o)  
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));  
    return fmt;  	
}; 




