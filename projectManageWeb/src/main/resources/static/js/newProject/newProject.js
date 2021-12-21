/**************
 * 新建类项目管理
 * niexingquan
 * 2019-11-8
 */
var selectUserIds = [];
$(() => {
  // show_detail(173,1);
  if($('#project_name').val()){
    $('#projectName').val($('#project_name').val()).change();
  }
  pageInit();
  showSystems();
  downOrUpButton();
  buttonClear();
  _Config();//时间配置-验证规则-弹窗配置
  formValidator();
  refactorFormValidator();
})

//表格数据加载
function pageInit() {
	$("#loading").css('display', 'block');
  $("#project_list").jqGrid({
    url:'/projectManage/newProject/getAllNewProject',
    datatype: 'json', 
    mtype: "POST",
    height: 'auto',
    width: $(".content-table").width() * 0.999,
    colNames: ['id', '项目编号', '项目名称', '项目类型', '计划起止日期', '项目状态', '所属处室', '操作'],
    postData: {
      "projectName" : $('#project_name').val()
    },
    colModel: [
      { name: 'id', index:'id', hidden: true },
      { name: 'projectCode', index:'projectCode'},
      { name: 'projectName', index:'projectName' },
      { name: 'projectClassName', index:'projectClassName' },
      { name: 'startEndDate', index:'startEndDate',
        formatter : function(value, grid, rows) {
       	  return isValueNull(rows.planStartDate) +" - "+ isValueNull(rows.planEndDate);
        } 
      },
      { name: 'projectStatusName', index:'projectStatusName' },
      { name: 'deptName', index:'deptName' },
      {
        name: '操作', align: "center", fixed: true, sortable: false, resize: false, search: false, width: 140,
        formatter: function (value, grid, rows, state) {
          var span_ = "<span>&nbsp;|&nbsp;</span>";
          var a = '<a class="a_style" onclick="addProject(2,' + rows.id + ')">编辑</a>';
          var b = '<a class="a_style" onclick="show_detail(' + rows.id + ',' + rows.developmentMode + ')">管理</a>';
          var opt_status = [];
          if(newProjectEdit == true){
            opt_status.push(a);
          }
          if(newProjectConfig == true)
          {
            opt_status.push(b);
          }
          return opt_status.join(span_);
        }
      },
    ],
    rowNum: 10,
    rowTotal: 200,
    rowList: [10, 20, 30],
    rownumWidth: 40,
    pager: '#pager2',
    sortable: false,
    loadtext: "数据加载中......",
    viewrecords: true, //是否要显示总记录数 
    jsonReader: {
      repeatitems: false,
      root: "data",
    },
    gridComplete: function () {
//      $("[data-toggle='tooltip']").tooltip();
    },
    loadComplete: function () {
      $("#loading").css('display', 'none');
    },
    beforeRequest: function(){
      $("#loading").css('display','block');
    }
  }).trigger("reloadGrid");
}

//跳转主页
function show_detail(Id,type) {
  var data = {"menuButtonId":"9901","url":"../projectManageui/newProject/toNewProjectDetail","menuButtonName":"新建类项目主页"};
  var _type = type ? type : 1;
  window.parent.toPageAndValue( data.menuButtonName , data.menuButtonId , data.url +"?id=" + Id + "&type=" + _type );
}

//项目群  新增项目
function add_project_group(){
    $("#group_list_Table").bootstrapTable('destroy');
    $("#group_list_Table").bootstrapTable({
        url:"/projectManage/newProject/getNewProjectByPage",
        method:"post",
        queryParamsType:"",
        pagination : true,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        pageNumber : 1,
        pageSize : 10,
        pageList : [ 5, 10, 15],
        singleSelect : false,
        queryParams : function(params) {
            var param={
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
            field : "projectCode",
            title : "项目编号",
            align : 'center'
        },{
            field : "projectName",
            title : "项目名称",
            align : 'center'
        },{
            field : "planStartDate",
            title : "计划起止日期",
            align : 'center',
            formatter:function(value,row,index){
                return isValueNull(row.planStartDate)+" - "+isValueNull(row.planEndDate);
            }
        },{
            field : "projectStatusName",
            title : "项目状态",
            align : 'center'
        },
    {
    	field : "deptName",
    	title : "牵头部门",
    	align : 'center'
    },],
        onLoadSuccess:function(){
          $('#group_list_Modal').modal('show');
        },
        onLoadError : function() {

        },
    });
}

function push_group(){
  var selectContent = $("#group_list_Table").bootstrapTable('getSelections')[0];
  if(typeof(selectContent) == 'undefined') {
    layer.alert('请选择一列数据！', {icon: 0});
      return false;
  }else {
  
    var ids = '';
    var names = '';
    var _flag = true;
    var selectContent = $("#group_list_Table").bootstrapTable('getSelections');
    $("#project_group_tbody tr").each(function(index,value){
	    selectContent.map(function(val,idx){
	      if($(value).data('id') == val.id){
	    	  layer.alert('编号'+val.projectCode+'已存在,不能重复添加',{icon:0})
	    	  _flag = false;
	    	  return
	      }
	    })
    })
    if(!_flag) return
	    selectContent.map(function(val,idx){
		      $("#project_group_tbody").append(`
		        <tr data-id="${val.id}">
		          <td>${val.projectCode}</td>
		          <td>${val.projectName}</td>
		          <td>${val.planStartDate} - ${val.planEndDate}</td>
		          <td>${val.projectStatusName}</td>
		          <td>${val.deptName}</td>
		          <td><a href="#" class="a_style" onclick="removeThis(this)">移除</a></td>
		        </tr>
		      `);
	    })
    $("#group_list_Modal").modal("hide");
  }
}

//清空搜索内容
function clearSearch() {
	$("#startEndDate").val("");
//	startEndDate = [];
//	startEndDate[1] = '';
   _clear_val('#search_div');
  $('.selectpicker').selectpicker('refresh');
//  $("#project_list").trigger("reloadGrid");
}

//查询信息
function searchInfo() {
//	var startEndDate = $("#startEndDate").val().split(" - ");
	var time = '';
	var st = '';
	var et = '';
	time=$("#startEndDate").val();
	if(time != ''){
		var timeArr=time.split(' - ');
		st=timeArr[0];
		et=timeArr[1];
	} 
  $("#project_list").jqGrid('setGridParam', {
    postData: {
    	"projectCode":$.trim($("#projectCode").val()),
    	"projectName":$.trim($("#projectName").val()),
    	"projectClass":$("#projectType").val(),
    	"projectStatus":$("#projectStatus").val(),
    	"planStartDate":st,
    	"planEndDate":et,
    	"managerUserId":$("#manageUserId").val(),
    	"developmentMode":$("#developmentMode").val(),
    	"systemId":$("#system").val(),
//    	"projectOverview":$.trim($("#projectIntro").val()),
//    	"projectUserIds":$("#projectUserIds").val(),
    },
    page: 1
  }).trigger("reloadGrid"); //重新载入
}

//清空value
function _clear_val(parent) {
  $(parent).find('input').each(function (idx, val) {
    $(val).val('');
  })
  $(parent).find('select').each(function (idx, val) {
    $(val).selectpicker('val', '');
  })
  $(".selectpicker").selectpicker('refresh');
  $(parent).find('textarea').each(function (idx, val) {
    $(val).val('');
  })
}

//新建,编辑
function addProject(status,Id) {
  if (!status) {           //新增项目
    _clear_val('#add_form');
    $("#add_project_modal").modal('show');
  } else if(status == 1) { //新增项目群
    _clear_val('#add_group_form');
    $("#project_group_table tbody").html("");
    $("#add_group_modal").modal('show');
  } else if(status == 2) { //编辑
    _clear_val('#edit_form');
    $.ajax({
      type:"post", 
      url:"/projectManage/newProject/getNewProjectById",
      dataType:"json",
//      contentType: "application/json; charset=utf-8",
      data:{
        'id':Id
      },
      success : function(data) {
         var result = data.data;
         $('#edit_id').val(result.id);
         $('input[name="edit_projectName"]').val(result.projectName);
         $('select[name="edit_projectType"]').val(result.projectClass);
         $('#edit_projectRoom').val(result.deptNumber);
         $('#edit_projectManagerId').val(result.managerUserId);
         $('#edit_projectManager').val(result.managerUserName);
         $('select[name="edit_projectModel"]').val(result.developmentMode);
         $('#edit_budgetNumber').val(result.budgetNumber);
         var developSystemIds = result.developSystemIds.split(',');
         $('#edit_projectSystem').selectpicker('val',developSystemIds);
         var peripheralSystemIds = result.peripheralSystemIds.split(',');
         $('#edit_projectRimSystem').selectpicker('val',peripheralSystemIds);
         
         $('select[name="edit_projectDepartment"]').val(result.businessDeptNumber);
         $('textarea[name="edit_projectScope"]').val(result.projectScope);
         $('textarea[name="edit_projectBackground"]').val(result.projectBackground);
         $('textarea[name="edit_projectRemark"]').val(result.budgetRemark);
         $('#edit_startDate').val(result.planStartDate);
         $('#edit_deliveryDate').val(result.planEndDate);
         $('#edit_endDate').val(result.closeDate);
         $('.selectpicker').selectpicker('refresh');
//         console.log($('#edit_projectSystem').val(),result.developSystemIds.split(','))
        $("#edit_project_modal").modal('show');
      }
    })
  }
}

//新增提交
function add_submit() {
  $('#add_form').data('bootstrapValidator').validate();  
  if( !$('#add_form').data("bootstrapValidator").isValid() ){
    return;
  } 
  var submit_data = {
    projectName : $.trim($('input[name="projectName"]').val()),
    projectClass : $.trim($('select[name="projectType"]').val()),
    deptNumber : $.trim($('#projectRoom').val()),
    managerUserId : $.trim($('#projectManagerId').val()),
    developmentMode : $.trim($('select[name="projectModel"]').val()),
    budgetNumber : $.trim($('#budgetNumber').val()),
    developSystemIds : $.trim($('#projectSystem').val()),
    peripheralSystemIds : $.trim($('#projectRimSystem').val()),
    projectScope : $.trim($('textarea[name="projectScope"]').val()),
    businessDeptNumber : $.trim($('select[name="projectDepartment"]').val()),
    projectBackground : $.trim($('textarea[name="projectBackground"]').val()),
    budgetRemark : $.trim($('textarea[name="projectRemark"]').val()),
    planStartDate : $.trim($('#startDate').val()),
    planEndDate : $.trim($('#deliveryDate').val()),
    closeDate : $.trim($('#endDate').val()),
  };
  console.log(submit_data)
  // $("#loading").css('display', 'block');
  $.ajax({
  	type:"post", 
  	url:"/projectManage/newProject/insertNewProject",
  	dataType:"json",
  	contentType: "application/json; charset=utf-8",
  	data:JSON.stringify(submit_data),
  	success : function(data) {
  		if( data.status == 1 ){
  			 layer.alert("新增成功",{icon : 1});
         $("#add_project_modal").modal("hide");
         $("#project_list").trigger("reloadGrid");
  		 }if(data.status == 2){
  			 layer.alert("新增失败",{icon : 2});
  			//  $("#add_project_modal").modal("hide");
       }
       $("#loading").css('display', 'none');
  	}
  });  
}

//编辑提交
function edit_submit() {
  $('#edit_form').data('bootstrapValidator').validate();  
  if( !$('#edit_form').data("bootstrapValidator").isValid()){
    return;
  } 
  if($('#edit_budgetNumber').val()){
	  $('#edit_budgetNumber').siblings('i').removeClass('glyphicon-remove').addClass('glyphicon-ok').parent().parent().removeClass('has-error').addClass('has-success');
	  $('#edit_budgetNumber').parent().parent().find('small').hide();
  }else{
	  $('#edit_budgetNumber').siblings('i').removeClass('glyphicon-ok').addClass('glyphicon-remove').parent().parent().removeClass('has-success').addClass('has-error');
	  $('#edit_budgetNumber').parent().parent().find('small').show();
	  return;
  }
  var submit_data = {
	id : $('#edit_id').val(),
    projectName : $.trim($('input[name="edit_projectName"]').val()),
    projectClass : $.trim($('select[name="edit_projectType"]').val()),
    deptNumber : $.trim($('#edit_projectRoom').val()),
    managerUserId : $.trim($('#edit_projectManagerId').val()),
    developmentMode : $.trim($('select[name="edit_projectModel"]').val()),
    budgetNumber : $.trim($('#edit_budgetNumber').val()),
    developSystemIds : $.trim($('#edit_projectSystem').val()),
    peripheralSystemIds : $.trim($('#edit_projectRimSystem').val()),
    projectScope : $.trim($('textarea[name="edit_projectScope"]').val()),
    businessDeptNumber : $.trim($('select[name="edit_projectDepartment"]').val()),
    projectBackground : $.trim($('textarea[name="edit_projectBackground"]').val()),
    budgetRemark : $.trim($('textarea[name="edit_projectRemark"]').val()),
    planStartDate : $.trim($('#edit_startDate').val()),
    planEndDate : $.trim($('#edit_deliveryDate').val()),
    closeDate : $.trim($('#edit_endDate').val()),
  };
  console.log(submit_data)
  // $("#loading").css('display', 'block');
  $.ajax({
  	type:"post", 
  	url:"/projectManage/newProject/updateNewProject",
  	dataType:"json",
  	contentType: "application/json; charset=utf-8",
  	data:JSON.stringify(submit_data),
  	success : function(data) {
  		if( data.status == 1 ){
  			 layer.alert("编辑成功",{icon : 1});
         $("#edit_project_modal").modal("hide");
         $("#project_list").trigger("reloadGrid");
  		 }if(data.status == 2){
  			 layer.alert("编辑失败",{icon : 2});
  			//  $("#edit_project_modal").modal("hide");
       }
       $("#loading").css('display', 'none');
  	}
  });  
}

function removeThis(This){
	$(This).parent().parent().remove();
}

function add_group_submit(){
  $('#add_group_form').data('bootstrapValidator').validate();  
  if( !$('#add_group_form').data("bootstrapValidator").isValid() ){
    return;
  } 
  var projectIds = '';
  $("#project_group_tbody tr").each(function(idx,val){
	  projectIds += $(val).data('id') + ',';
  })
  console.log(projectIds)
  if(projectIds == ''){
	  layer.alert("请选择项目",{icon : 0});
	  return
  }
  var submit_data = {
	programName : $.trim($('#projectGroupName').val()),
	manageUserId : $.trim($('#programManagerId').val()),
	projectIds : projectIds,
	programIntro : $.trim($('#projectGroupInfo').val()),
	programBackground : $.trim($('#projectGroupBackground').val()),
	remark : $.trim($('#projectGroupRemark').val()),
  };
  console.log(submit_data)
  // $("#loading").css('display', 'block');
  $.ajax({
  	type:"post", 
  	url:"/projectManage/newProject/insertProgram",
  	dataType:"json",
  	contentType: "application/json; charset=utf-8",
  	data:JSON.stringify(submit_data),
  	success : function(data) {
  		if( data.status == 1 ){
  			 layer.alert("新增成功",{icon : 1});
         $("#add_group_modal").modal("hide");
         $("#project_list").trigger("reloadGrid");
  		 }if(data.status == 2){
  			 layer.alert("新增失败",{icon : 2});
//  			 $("#add_project_modal").modal("hide");
       }
       $("#loading").css('display', 'none');
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
        singleSelect : false,
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

function commitUser(){
    if($("#userbutton").attr("data-user")== 'devManageUser'){
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
                $("#projectUserIds").val(ids);
                $("#projectUser").val(names);
            }
           
            $("#userModal").modal("hide");
        }
    }else{
        var selectContent = $("#userTable").bootstrapTable('getSelections')[0];
        if(typeof(selectContent) == 'undefined') {
    	    layer.alert('请选择一列数据！', {icon: 0});
            return false;
        }else {
        	if ($("#userbutton").attr("data-user") == 'projectManageUser') {
                $("#projectManagerId").val(selectContent.id);
                $("#projectManager").val(selectContent.userName).change();
            }
        	if ($("#userbutton").attr("data-user") == 'programManageUser') {
        		$("#programManagerId").val(selectContent.id);
        		$("#programManager").val(selectContent.userName).change();
        	}
        	if ($("#userbutton").attr("data-user") == 'edit_projectManager') {
        		$("#edit_projectManagerId").val(selectContent.id);
        		$("#edit_projectManager").val(selectContent.userName).change();
        	}
        	if ($("#userbutton").attr("data-user") == 'edit_projectManager') {
        		$("#edit_projectManagerId").val(selectContent.id);
        		$("#edit_projectManager").val(selectContent.userName).change();
        	}
        	if ($("#userbutton").attr("data-user") == 'manageUser') {
        		$("#manageUserId").val(selectContent.id);
        		$("#manageUser").val(selectContent.userName).change();
        	}
            $("#userModal").modal("hide");
        }
    }
}

function showSystems() {
	$.ajax({
		url: "/testManage/testCase/getAllSystem",
		method: "post",
		dataType: "json",
		success: function (data) {
			var list = data.data;
			for (var i = 0; i < list.length; i++) {
		        $('#projectSystem').append(`<option value="${list[i].id}">${list[i].systemName}</option>`);
		        $('#projectRimSystem').append(`<option value="${list[i].id}">${list[i].systemName}</option>`);
		        $('#edit_projectSystem').append(`<option value="${list[i].id}">${list[i].systemName}</option>`);
		        $('#edit_projectRimSystem').append(`<option value="${list[i].id}">${list[i].systemName}</option>`);
		        $('#system').append(`<option value="${list[i].id}">${list[i].systemName}</option>`);
			}
			$('.selectpicker').selectpicker('refresh');
		}
	});
}

//表单校验
function formValidator() {
  $('#add_form').bootstrapValidator({
    message: '不能为空',//通用的验证失败消息
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields:{
      projectName:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      projectType:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      projectRoom:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      projectManager:{
    	trigger:"change",
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
    }
  })

  $('#edit_form').bootstrapValidator({
    message: '不能为空',//通用的验证失败消息
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields:{
      edit_projectName:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_projectType:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_projectRoom:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_projectManager:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_projectModel:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_budgetNumber:{
        validators: {
          notEmpty: {
            message: '必须是数字字母组合,0~13位'
          }
        }
      },
      edit_projectSystem:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_projectScope:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      edit_projectBackground:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
    }
  })

  $('#add_group_form').bootstrapValidator({
    message: '不能为空',//通用的验证失败消息
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields:{
      projectGroupName:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      programManager:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      /*projectGroupInfo:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      projectGroupBackground:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },
      projectGroupRemark:{
        validators: {
          notEmpty: {
            message: '此项不能为空'
          }
        }
      },*/
    }
  })
}

//重构表单验证
function refactorFormValidator() {
  $('#add_project_modal').on('hidden.bs.modal', function () {
    $("#add_form").data('bootstrapValidator').destroy();
    $('#add_form').data('bootstrapValidator', null);
    formValidator();
  })
  $('#edit_project_modal').on('hidden.bs.modal', function () {
    $("#edit_form").data('bootstrapValidator').destroy();
    $('#edit_form').data('bootstrapValidator', null);
    formValidator();
  })
  $('#add_group_modal').on('hidden.bs.modal', function () {
    $("#add_group_form").data('bootstrapValidator').destroy();
    $('#add_group_form').data('bootstrapValidator', null);
    formValidator();
  })
}

//时间配置-表单配置-弹窗配置
function _Config() {
  var locale = {
    "format": 'yyyy-mm-dd',
    "separator": " -222 ",
    "applyLabel": "确定",
    "cancelLabel": "取消",
    "fromLabel": "起始时间",
    "toLabel": "结束时间",
    "customRangeLabel": "自定义",
    "weekLabel": "W",
    "daysOfWeek": ["日", "一", "二", "三", "四", "五", "六"],
    "monthNames": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
    "firstDay": 1
  };
  $("#startEndDate").daterangepicker({ 'locale': locale 
  }).on('cancel.daterangepicker', function(ev, picker) {
    // $("#startEndDate").val("");
  }).on('apply.daterangepicker', function(ev, picker) {
    $("#startEndDate").change();
  });
  //单个时间插件
  TimeConfig('#startDate');
  TimeConfig('#deliveryDate');
  TimeConfig('#endDate');
  TimeConfig('#edit_startDate');
  TimeConfig('#edit_deliveryDate');
  TimeConfig('#edit_endDate');
//  $("#projectUser").click(function(){
//    selectUserIds=[];
//  	$("#userbutton").attr("data-user", "devManageUser");
//  	$("#userModal").modal("show");
//  	clearSearchUser();
//    userTableShowAll();
//  });
  $("#projectManager").click(function(){
	  selectUserIds=[];
	  $("#userbutton").attr("data-user", "projectManageUser");
	  $("#userModal").modal("show");
	  clearSearchUser();
	  userTableShow2();
  });
  $("#edit_projectManager").click(function(){
	  selectUserIds=[];
	  $("#userbutton").attr("data-user", "edit_projectManager");
	  $("#userModal").modal("show");
	  clearSearchUser();
	  userTableShow2();
  });
  $("#programManager").click(function(){
	  selectUserIds=[];
	  $("#userbutton").attr("data-user", "programManageUser");
	  $("#userModal").modal("show");
	  clearSearchUser();
	  userTableShow2();
  });
  $("#manageUser").click(function(){
	  selectUserIds=[];
	  $("#userbutton").attr("data-user", "manageUser");
	  $("#userModal").modal("show");
	  clearSearchUser();
	  userTableShow2();
  });
  $('#projectRoom').bind('change', function () {
    if($('select[name="projectType"]').val() && $('select[name="projectNumber"]').val()){
      var _type = $('select[name="projectType"]').val();
      var _val = $(this).val();
      var _yer = new Date().getFullYear();
    }
  })
  $('#budgetNumber').bind('change', function () {
    var Exp = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,13}$/;
    var _val = $('#budgetNumber').val();
    if (Exp.test(_val)) {
      $('#budgetNumber').val(_val).change();
    } else {
      $('#budgetNumber').val('').change();
    }
  })
  $('#edit_budgetNumber').bind('change', function () {
    var Exp = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,13}$/;
    var _val = $('#edit_budgetNumber').val();
    if (Exp.test(_val)) {
      $('#edit_budgetNumber').val(_val);
      $('#edit_budgetNumber').siblings('i').removeClass('glyphicon-remove').addClass('glyphicon-ok').parent().parent().removeClass('has-error').addClass('has-success');
      $('#edit_budgetNumber').parent().parent().find('small').hide();
    } else {
      $('#edit_budgetNumber').val('');
      $('#edit_budgetNumber').siblings('i').removeClass('glyphicon-ok').addClass('glyphicon-remove').parent().parent().removeClass('has-success').addClass('has-error');
      $('#edit_budgetNumber').parent().parent().find('small').show();
    }
  })
}

function clearSearchUser(){
  $("#userName").val('');
  $("#deptName").val('');
  $("#companyName").val('');
}

function TimeConfig(ele){
  $(ele).datetimepicker({
    minView:"month",
    format: "yyyy-mm-dd",
    autoclose: true,
    todayBtn: true,
    language: 'zh-CN',
    pickerPosition: "top-left"
  });
}