/**
 * Created by 朱颜辞镜花辞树 on 2018/9/18.
 */
const privateDev = 1
const publicDev = 2
const privateXice = 3
const publicXice = 4
const privateUat = 5
const publicUat = 6
const privateBance = 7
const publicBance = 8
const prdIn = 9;
const prdOut = 10;
var line = 0;
var notes = "";

var sql_files = [];
var oper_files = [];
var config_files = [];

var sql_files_flag = [];
var oper_files_flag = [];
var config_files_flag = [];

var _icon_word = "../static/images/devtask/word.png";
var _icon_excel = "../static/images/devtask/excel.png";
var _icon_text = "../static/images/devtask/text.png";
var _icon_pdf = "../static/images/devtask/pdf.png";
//分页所需参数 
var searchArr = {};
var timer = null;
var count = 0;
var reqFeatureqIdsArtifact = "";
//总页数
searchArr.total = 0;
//当前页
searchArr.pageNum = 1;
//页面条目数
searchArr.row = [10, 20, 30];
//当前页面条目数
searchArr.select = searchArr.row[0];
//查询条件
var environmentTypeData;
var environmentTypeDataInsystemId;

var systemIdScm = "";
var systemIdManual = "";

var deployStatusData;
//自动部署部署方式
searchArr.postData = {
	"pageNum": searchArr.pageNum,
	"pageSize": searchArr.select,
	"systemInfo": {},
	"scmBuildStatus": $("#scmBuildStatus").val()
};
//查询参数
searchArr.postData.systemInfo.systemName = $("#seacrhSystemName").val();
searchArr.postData.systemInfo.systemCode = $("#seacrhSystemNum").val();
searchArr.postData.systemInfo.projectIds = $("#seacrhProject").val();
$("#loading").css('display', 'block');
var allData = [];
var checkBoxDataArr = [];
var sonarData;
var end = "";

var environmentList = '';
$(function () {
	// environmentList = $("#defectStatus").find("option");
	// $(document).bind("keyup", function (e) {
	// 	if(e.keyCode == 13){
	// 		console.log(e.keyCode)
	// 		return false;
	// 		console.log(e.keyCode)
	// 	}
	// })
	//$("#deploymentConfig").modal('show');
	uploadFileList();
	//	uploadConfigFileList();
	//	uploadOperFileList();


	//initDept();
	getEnvironmentType();
	pageInit();
	dateComponent();
	addseacrhProjectOption();
	//setTimeout("searchInfo()",1000*60);
	var t1 = window.setInterval(autoSearchInfo, 1000 * 60);


	$("#recoveryTime").datetimepicker({
		minView: "month",
		format: "yyyy-mm-dd hh:ii:ss",
		autoclose: true,
		todayBtn: true,
		language: 'zh-CN',
		pickerPosition: "bottom-left"
	});

	$("#recoveryTimeArtifact").datetimepicker({
		minView: "month",
		format: "yyyy-mm-dd hh:ii:ss",
		autoclose: true,
		todayBtn: true,
		language: 'zh-CN',
		pickerPosition: "bottom-left"
	});

	// 搜索框 收藏按钮 js 部分
	$("#downBtn").click(function () {
		if ($(this).children("span").hasClass("fa-caret-up")) {
			$(this).children("span").removeClass("fa-caret-up");
			$(this).children("span").addClass("fa-sort-desc");
			$("#search_div").slideUp(200);
		} else {
			$(this).children("span").removeClass("fa-sort-desc");
			$(this).children("span").addClass("fa-caret-up");
			$("#search_div").slideDown(200);
		}
	})
	// $(".collection").click(function () {
	// 	if ($(this).children("span").hasClass("fa-heart-o")) {
	// 		$(this).children("span").addClass("fa-heart");
	// 		$(this).children("span").removeClass("fa-heart-o");
	// 	} else {
	// 		$(this).children("span").addClass("fa-heart-o");
	// 		$(this).children("span").removeClass("fa-heart");
	// 	}
	// })
	//所有的Input标签，在输入值后出现清空的按钮
	$('.color1 input').parent().css("position", "relative");
	$('.color1 input').parent().append("<span onclick='clearContent(this)' class='btn_clear'></span>");
	$(".color1 input").bind("input propertychange", function () {
		if ($(this).val() != "") {
			$(this).parent().children(".btn_clear").css("display", "block");
		} else {
			$(this).parent().children(".btn_clear").css("display", "none");
		}
	})
	$("#sonarTime").bind("input propertychange", function () {

	})
	$("#selEnvType").change(function () {
		if ($("#selEnvType").val() == '') {
			$("#manualReqFeatureDiv").hide();
		} else {
			initManualReqFeatureTable(systemIdManual);
			$("#manualReqFeatureDiv").show();
		}

	})
	$('#logModal').on('hide.bs.modal', function () {
		end="true";
		clearInterval(timer);//结束循环
	})
	$('#access_configuration_Modal').on('hide.bs.modal', function () {
		$('#configuration_systemId').val('');
		$("#access_configuration_list").jqGrid("clearGridData");
		configuration_flag = true;
	})
});

function addseacrhProjectOption() {

	$.ajax({
		url: "/devManage/structure/getAllproject",
		method: "post",
		success: function (data) {
			if (!data.list) return
			if (data.list.length) {
				for (var i = 0; i < data.list.length; i++) {
					$("#seacrhProject").append("<option value=" + data.list[i].id + ">" + data.list[i].projectName + "</option>");
				}
				$('.selectpicker').selectpicker('refresh');
			}
		},
		error: function () {
		}
	});
}
//表格数据加载
function pageInit() {
	checkBoxDataArr = [];
	allData = [];
	$("#list2").jqGrid("clearGridData");
	$("#list2").jqGrid({
		url: '/devManage/deploy/getAllManualSystemInfo',
		datatype: "json",
		height: 'auto',
		mtype: "POST",
		cellEdit: true,
		postData: {
			"pageNum": searchArr.pageNum,
			"pageSize": searchArr.row[0],
		},
		width: $(".content-table").width() * 0.999,
		colNames: ['', 'id', 'key_id', '系统编号', '系统名称', '所属项目', '微服务类型', '上次部署时间', '操作'],
		colModel: [{
			name: 'checkBox',
			index: 'checkBox',
			width: 20,
			formatter: function (value, grid, rows, state) {
				var row = JSON.stringify(rows).replace(/"/g, '&quot;');
				if (rows.createType == 2 || rows.deployType == 2) {//制品部署或手动构建不可选择
					return '<input type="checkbox"  disabled="true"  />';
				} else if (rows.createType == 1) {
					return '<input type="checkbox" id="check' + rows.key_id + '" onclick="isChecked( ' + row + ',this )" />';
				}
			}
		},
		{ name: 'id', index: 'id', hidden: true },
		{ name: 'key_id', index: 'key_id', hidden: true, key: true },
		{ name: 'systemCode', index: 'systemCode' },
		{
			name: 'systemName', index: 'systemName', formatter: function (value, grid, rows, state) {
				var str = rows.systemName;
				if (rows.architectureType == 1) {
					str += " <sup class='micSup'>多模块系统</sup>"
				}
				return str;
			}
		},

		{ name: 'projectName', index: 'projectName',width:110},
		{ name: 'architectureType', index: 'architectureType', hidden: true },
		{
			name: 'lastBuildTime', index: 'lastBuildTime', formatter: function (value, grid, rows, state) {
				if (rows.level == 1) {
					if (rows.createType == 2) {
						return '';
					}
				}
				var row = JSON.stringify(rows).replace(/"/g, '&quot;');
				if (rows.nowStatus == 'true') {
					if (rows.createType == 1) {
						var str = '';
						var arr = rows.nowEnvironmentType.split(",");
						for (var i = 0; i < arr.length; i++) {
							var envNameflag = arr[i];
							if (envNameflag.indexOf("测试") !=-1) {
								//显示部署日志不是实时的
								envNameflag = envNameflag.replace("正在自动化测试", "");
								str += "<a  href='#' onclick=getTestLog(" + rows.deployType + "," + rows.systemId + "," + rows.createType + ",\"" + envNameflag + "\",\"" + manualJobName + "\")>" + arr[i] + "</a><br>";
								//            				str+=arr[i]+



							} else {

								envNameflag = envNameflag.replace("正在部署", "");

								//envNameflag = envNameflag.replace("正在自动化测试", "");
								var manualJobName = "";
								str += "<a  href='#' onclick=getLogNew(" + rows.deployType + "," + rows.systemId + "," + rows.createType + ",\"" + envNameflag + "\",\"" + manualJobName + "\")>" + arr[i] + "</a><br>";
								//            				str+=arr[i]+"<a  onclick=getLogNew("+rows.deployType+","+rows.systemId+","+rows.createType+",\""+envNameflag+"\",\""+manualJobName+"\")>实时日志</a><br>";
							}
						}
						return str;
					} else if (rows.createType == 2) {
						var str = '';
						var arr = rows.nowJobName.split(",");

						for (var i = 0; i < arr.length; i++) {
							var envNameflag = "";
							var deployType = "";
							var manualJobName = arr[i];
							manualJobName = manualJobName.replace("正在部署", "");
							//            				str+=arr[i]+"<a onclick=getLogNew(\""+deployType+"\","+rows.systemId+","+rows.createType+",\""+envNameflag+"\",\""+manualJobName+"\")>实时日志</a><br>";
							str += "<a  href='#'  onclick=getLogNew(\"" + deployType + "\"," + rows.systemId + "," + rows.createType + ",\"" + envNameflag + "\",\"" + manualJobName + "\")>" + arr[i] + "</a><br>";

						}
						return str;
					} else {
						return "";
					}
				} else {
					if (rows.buildStatus == '4') {
						// 没有上次部署时间
						return '';
					} else {
						var type = '';
						for (var k in environmentTypeData) {
							if (k == rows.environmentType) {
								type = environmentTypeData[k];
							}
						}
						if (rows.createType == 1) {
							if (rows.buildStatus == 2) {

								return rows.lastBuildTime + " " + type + "  <span style='color:green;'>成功</span>";
							} else if (rows.buildStatus == 3) {
								return rows.lastBuildTime + " " + type + "  <span style='color:red;'>失败</span>";
							}

							else if (rows.buildStatus == 5) {
								return rows.lastBuildTime + " " + type + "  <span style='color:red;'>自动化测试失败</span>";
							}

						} else {
							if (rows.buildStatus == 2) {

								return rows.lastBuildTime + " " + rows.lastJobName + "  <span style='color:green;'>成功</span>";
							} else if (rows.buildStatus == 3) {
								return rows.lastBuildTime + " " + rows.lastJobName + "  <span style='color:red;'>失败</span>";
							}

						}
					}
				}
			}
		},
		{
			name: '操作',
			index: '操作',
			align: "left",
			fixed: true,
			sortable: false,
			resize: false,
			classes: 'optClass',
			search: false,
			width: 322,
			formatter: function (value, grid, rows, state) {
				var type = '';
				for (var k in environmentTypeData) {
					if (k == rows.nowEnvironmentType) {
						type = environmentTypeData[k];
					}
				}
				var row = JSON.stringify(rows).replace(/"/g, '&quot;');
				if (rows.level == 1) {

				} else if (rows.level == 0) {
					var str = '<li role="presentation" class="dropdown">';
					if (rows.createType == 1) {
						var envids = "";
						if (rows.choiceEnvids == "" && rows.deployType == 1) {

						} else {
							if (rows.deployType == 1) {//源码部署
								var envs = rows.choiceEnvids.split(",");
								for (var i = 0; i < envs.length; i++) {
									var id = envs[i].split(":")[0];
									var value = envs[i].split(":")[1];

									envids += '<li><a name=' + id + ' onclick="startBuilding(' + row + ',this)">' + value + '</a></li>';

								}
							} else {
								var envs = rows.choiceAtrEnvids.split(",");
								for (var i = 0; i < envs.length; i++) {
									var id = envs[i].split(":")[0];
									var value = envs[i].split(":")[1];
									envids += '<li><a name=' + id + ' onclick="startBuildingArtifact(' + row + ',this)">' + value + '</a></li>';
								}

							}
						}


						if (deployFlag == true) {

							str += '<a class="dropdown-toggle a_style" data-toggle="dropdown" role="button"><span class="fa fa-cog"></span> 部署 <span class="fa fa-angle-down"></span></a>' +
								'<ul class="dropdown-menu" >' + envids +
								'</ul></li>';

						}
					} else if (rows.createType == 2) {
						if (deployFlag == true) {
							str += '<a class="dropdown-toggle a_style" data-toggle="dropdown" onclick="getAllJobName( this,' + row + ' )" role="button"><span class="fa fa-cog"></span> 部署 <span class="fa fa-angle-down"></span></a>' +
								'<ul class="dropdown-menu" ></ul></li>';
						}
					}
					str += '<li role="presentation" class="dropdown">' +
						'<a class="dropdown-toggle a_style" data-toggle="dropdown" role="button">' +
						'</span> 查看历史 <span class="fa fa-angle-down"></span></a>' +
						'<ul class="dropdown-menu" >';
					if (deployHistoryFlag == true) {
						str += '<li><a class="a_style" onclick="buildHistory(' + row + ')">部署历史</a></li>';
					}
					//                		    if( rows.createType==1 && rows.deployType==1){
					//                		    	 if (sonarHistoryFlag == true ){
					//                		    	str+= '<li><a class="a_style" onclick="sonarHistory('+ row + ')">扫描历史</a></li>';
					//                		    	 }
					//                		    }
					str += '</ul></li>';
					if (scheduledFlag == true) {
						if (rows.deployType == 1 && rows.createType == 1) {//包件部署和手动部署没有定时任务

							str += ' | <a class="a_style"  onclick="scheduledTask(' + row + ')"   >定时任务</a> ';
						}
					}
					if (breakDeploying == true) {
						//                		    	if(rows.deployType==1){
						str += ' | <a class="a_style" onclick="getBreakName(' + row + ')">强制结束</a>  ';
						//}
					}

					if (deployPermissionFlag == true) {
						str += ' | <a class="a_style" data-id="' + rows.id + '" onclick="access_configuration(this)">权限配置</a>';
					}
					return str;


				}
				return '';
			}
		}
		],
		treeGrid: true,
		treeGridModel: "adjacency",
		ExpandColumn: "systemCode",
		treeIcons: { plus: 'fa fa-caret-right', minus: 'fa fa-caret-down', leaf: '' },
		sortname: "key_id",
		sortable: true,
		ExpandColClick: true,
		jsonReader: {
			repeatitems: false,
			root: "rows",
		},
		loadui: "disable",
		pager: '#pager',
		loadComplete: function (xhr) {
			if (xhr === undefined) {
			} else {
				allData = xhr.rows;
				$("#loading").css('display', 'none');

				if (xhr.total == 0) {
					searchArr.total = 1;
				} else {
					searchArr.total = xhr.total;
				}
				addPage(searchArr);
			}
		}
	}).trigger('reloadGrid');
}
/*****
 * 权限配置
 * ****/
var configuration_flag = true;//控制每次添加一条

//权限配置跳转
function access_configuration(This){
	var Id = $(This).data('id');
	$('#configuration_systemId').val(Id);
	$("#access_configuration_list").jqGrid("clearGridData");
	$("#access_configuration_list").jqGrid('setGridParam', {postData:{systemId:Id}});
	$("#access_configuration_list").jqGrid({
		url: '/devManage/deploy/getDeployUsers',
		datatype: "json",
		height: 'auto',
		mtype: "POST",
		width: $(".content-table").width() * 0.97,
		postData:{systemId:Id},
		colNames: ['id','systemId', '环境', '部署人员', '操作'],
		colModel: [
		{ name: 'id', index: 'id', hidden: true },
		{ name: 'systemId', index: 'systemId', hidden: true },
		{ name: 'environmentTypeName', 
			index: 'environmentTypeName',
			align: 'center',
			search: false,
			formatter: function (value, grid, row, index) {
				return  `<div class="font_left" style="width:200px;margin: 0 auto;">${value}<div>`;
			}
	 	},
		{ name: 'tblUserInfos', 
			index: 'tblUserInfos',
			align: 'center',
			search: false,
			title:false,
			classes: 'user_ipt',
			formatter: function (value, grid, row, index) {
				return  `
					<div class="environmentType_user_body">
						<input id="environmentType_user_id_${row.environmentType}" type="hidden"  class="environmentType_user_id"/>
						<input id='environmentType_user_${row.environmentType}' data='${JSON.stringify(row.tblUserInfos) || ''}' name='environmentType_user_${row.environmentType}' class='form-control def_hideDiv fakeInput environmentType_user' type='text' placeholder='请输入'/>
					<div>
					<small class="help-block _hide red">部署人员不能为空</small>
				`;
			}
		},
		{
			name: '操作',
			index: '操作',
			align: "center",
			// fixed: true,
			sortable: false,
			resize: false,
			classes: 'optClass',
			search: false,
			width: 50,
			formatter: function (value, grid, rows, state) {
					var row = JSON.stringify(rows).replace(/"/g, '&quot;');
					var str = '';
					str += '<a class="a_style" onclick="_delete_configuration(' + rows.id + ')">删除</a>  ';
					str += ' | <a class="a_style" onclick="_save_configuration(' + rows.id + ',this)">保存</a>';
					return str;
			}
		}
		],
		
		sortable: false,
		// pager: '#access_configuration_list_pager',
		loadComplete: function (xhr) {
			$('.environmentType_user').each(function(idx,val){
				var _id = $(val).attr('id');
				var user_id = $(val).siblings().attr('id');
				var _data = JSON.parse($(val).attr('data'));
				var add_env_user = {
					'ele': _id,
					'url': '/projectManage/personnelManagement/selectUserInfoVague',
					'params': {
						'userAndAccount': '',
						'ids': '',
					},
					'name': 'userName',
					'id': 'id',
					'top': '60px',
					'userId': $(`#${user_id}`),
				};
				fuzzy_search_checkbox(add_env_user);
				search_checkbox_give(_id, $(`#${user_id}`), _data, 'userName', 'id');
			})
			$('#loading').hide();
			$('#access_configuration_Modal').modal('show');
			$(".environmentType_user_id").bind("change blur", function () {
				if($(this).val()){
					$(this).parent().removeClass('bor_red');
					$(this).parent().find('small').hide();
				}else{
					$(this).parent().addClass('bor_red');
					$(this).parent().find('small').show();
				}
			})
			$('.optClass').prev().removeAttr('title');
		},
		beforeRequest:function(){
			$('#loading').show();
		},
	}).trigger('reloadGrid');
}

//权限 删除
function _delete_configuration(Id){
	layer.confirm('你确定要删除吗？', {
		btn: ['确定','取消'],
		'title':'提示信息',
		'icon':2,
	}, function(){
		layer.closeAll(); 
		$('#loading').show();
		$.ajax({
			type:"POST",
			url:"/devManage/deploy/deleteDeployUser",
			dataType:"json",
			async:false,
			data:{
				id:Id,
			},
			success:function(result){
					if(result.status == 1){
						layer.alert('删除成功!',{
							'title':'提示信息',
							'icon':1,
						})
						$("#access_configuration_list").trigger('reloadGrid');
					}else{
						layer.alert(result.message,{
							'title':'提示信息',
							'icon':2,
						})
					}
					$('#loading').hide();
			}
		});
	})
}

//权限 保存
function _save_configuration(Id,This){
	var userIds = $(This).parent().prev().find('.environmentType_user_id').val();
	if(!userIds){
		$(This).parent().prev().find('.environmentType_user_body').addClass('bor_red');
		$(This).parent().prev().find('small').show();
		return
	}else{
		$(This).parent().parent().find('.environmentType_user_body').removeClass('bor_red');
		$(This).parent().parent().find('small').hide();
	}
	$('#loading').show();
	$.ajax({
		type:"POST",
		url:"/devManage/deploy/updateDeployUser",
		dataType:"json",
		data:{
				"id":Id,
				"userIds":userIds,
		},
		async:false,
		success:function(result){
			if(result.status == 1){
				layer.alert('提交成功!',{
					'title':'提示信息',
					'icon':1,
				})
				$("#access_configuration_list").trigger('reloadGrid');
			}else{
				layer.alert(result.message,{
					'title':'提示信息',
					'icon':2,
				})
			}
			$('#loading').hide();
		}
	});
}

//权限 添加按钮
function _add_configuration(){
	
	if(!configuration_flag) {
		layer.alert('请先提交添加信息!',{
			'title':'提示信息',
			'icon':0,
		})
		return;
	}
	$('#loading').show();
	$.ajax({
		type:"POST",
		url:"/devManage/deploy/getEnvsBySystemId",
		dataType:"json",
		async:false,
		data:{
			'systemId':$('#configuration_systemId').val(),
		},
		success:function(result){
			$('#add_env_option').empty();
			search_checkbox_clear('add_env_user', $('#add_env_user_id'));
			if(result.list.length){
				// let option = `<option value="">请选择</option>`;
				configuration_flag = false;
				let option = ``;
				result.list.map(function(val,idx){
					option += `<option value="${val.envVaule}">${val.envName}</option>`
				})
				$('#access_configuration_list').find('.jqgfirstrow').after(`
					<tr role="row" id="197" tabindex="0" class="jqgrow ui-row-ltr table-success" aria-selected="true">
						<td role="gridcell" style="display:none;" >197</td>
						<td role="gridcell" style="text-align:center;padding: 0 !important;" class="" >
							<select class="add_environmentType_val" id="add_env_option">${option}</select>
						</td>
						<td role="gridcell" style="text-align:center;" class="" >
							<div class="environmentType_user_body">
								<input id="add_env_user_id" type="hidden"  class="add_environmentType_user_id"/>
								<input id="add_env_user" name="UserInfo" class="form-control def_hideDiv fakeInput" type="text" placeholder="请输入"/>
								<small class="help-block red _hide">部署人员不能为空</small>
							</div>
						</td>
						<td role="gridcell" style="text-align:center;" class="optClass" >
							<a class="a_style" onclick="add_submit(this)">保存</a>
						</td>
					</tr>
				`);
				var add_env_user = {
					'ele': 'add_env_user',
					'url': '/projectManage/personnelManagement/selectUserInfoVague',
					'params': {
						'userAndAccount': '',
						'ids': '',
					},
					'name': 'userName',
					'id': 'id',
					'top': '85px',
					'userId': $('#add_env_user_id'),
				};
				fuzzy_search_checkbox(add_env_user);
				$(".add_environmentType_user_id").bind("change blur", function () {
					if($(this).val()){
						$(this).parent().removeClass('bor_red');
						$(this).siblings('small').hide();
					}else{
						$(this).parent().addClass('bor_red');
						$(this).siblings('small').show();
					}
				})
			}else{
				layer.alert('暂无环境添加!',{
					'title':'提示信息',
					'icon':0,
				})
			}
			$('#loading').hide();
		}
	});
	
}

//权限 添加提交
function add_submit(This){
	var _environmentType = $(This).parent().parent().find('.add_environmentType_val').val();
	var _userIds = $(This).parent().parent().find('.add_environmentType_user_id').val();
	if(!_userIds){
		$(This).parent().parent().find('.environmentType_user_body').addClass('bor_red');
		$(This).parent().parent().find('small').show();
		return
	}else{
		$(This).parent().parent().find('.environmentType_user_body').removeClass('bor_red');
		$(This).parent().parent().find('small').hide();
	}
	$('#loading').show();
	$.ajax({
		type:"POST",
		url:"/devManage/deploy/addDeployUser",
		dataType:"json",
		async:false,
		data:{
			systemId:$('#configuration_systemId').val(),
			environmentType:_environmentType,
			userIds:_userIds
		},
		success:function(result){
				if(result.status == 1){
					configuration_flag = true;
					layer.alert('提交成功!',{
						'title':'提示信息',
						'icon':1,
					})
					$("#access_configuration_list").trigger('reloadGrid');
				}else{
					layer.alert(result.message,{
						'title':'提示信息',
						'icon':2,
					})
				}
				$('#loading').hide();
		}
	});
}
/*****
 * 权限配置end
 * ****/

//添加翻页栏
function addPage(searchArr) {

	$("#pager_center").empty();
	var aClass = '';
	// pageNum total row
	var str = '<table class="ui-pg-table ui-common-table ui-paging-pager"><tbody><tr>'

	if (searchArr.pageNum == 1) {
		aClass = 'ui-disabled';
	} else {
		aClass = '';
	}
	str += '<td onclick="changePage(this)" id="first_pager" class="ui-pg-button ' + aClass + '" title=""><span class="fa fa-step-backward"></span></td><td onclick="changePage(this)"  id="prev_pager" class="ui-pg-button ' + aClass + '" title=""><span class="fa fa-backward"></span></td><td class="ui-pg-button ui-disabled"><span class="ui-separator"></span></td>';

	str += '<td id="input_pager" dir="ltr" style="position: relative;"> <input  class="pageIn ui-pg-input form-control" type="text" size="2" maxlength="7" value=' + searchArr.pageNum + ' role="textbox"> 共 <span id="sp_1_pager">' + Math.ceil(searchArr.total / searchArr.select) + '</span> 页</td>';

	if (searchArr.pageNum == (Math.ceil(searchArr.total / searchArr.select)) || Math.ceil(searchArr.total / searchArr.select) == 0) {
		aClass = 'ui-disabled';
	} else {
		aClass = '';
	}
	str += '<td class="ui-pg-button ui-disabled" style="cursor: default;"><span class="ui-separator"></span></td><td id="next_pager" onclick="changePage(this)"  class="ui-pg-button ' + aClass + '" title=""><span class="fa fa-forward"></span></td><td onclick="changePage(this)" id="last_pager" class="ui-pg-button ' + aClass + '" title=""><span class="fa fa-step-forward"></span></td><td dir="ltr"><select class="ui-pg-selbox form-control" onchange="changeSelect(this)" size="1" role="listbox" title="">';
	for (var i = 0; i < searchArr.row.length; i++) {
		if (searchArr.row[i] == 0) {
			alert(" 泰拳警告 !");
			return;
		} else if (searchArr.row[i] == searchArr.select) {
			str += '<option role="option" value=' + searchArr.row[i] + ' selected >' + searchArr.row[i] + '</option>';
		} else {
			str += '<option role="option" value=' + searchArr.row[i] + ' >' + searchArr.row[i] + '</option>';
		}
	}
	str += '</select></td></tr></tbody></table>';
	$("#pager_center").append(str);
	$(".pageIn").keydown(function (event) {
		var e = event || window.event;
		if (e.keyCode == 13) {
			var reg = /^[1-9]\d*$/;
			if (reg.test($(this).val())) {
				if ($(this).val() <= Math.ceil(searchArr.total / searchArr.select)) {
					searchArr.postData.pageNum = $(this).val();
					searchArr.pageNum = searchArr.postData.pageNum;
					searchInfo();
					return;
				}
			}
			$(this).val(searchArr.pageNum);
		}
	});
}
function changePage(This) {
	if ($(This).hasClass('ui-disabled')) {
		return;
	} else {
		if ($(This).attr('id') == 'first_pager') {
			searchArr.pageNum = 1;
		} else if ($(This).attr('id') == 'prev_pager') {
			searchArr.pageNum--;
		} else if ($(This).attr('id') == 'next_pager') {
			searchArr.pageNum++;
		} else if ($(This).attr('id') == 'last_pager') {
			searchArr.pageNum = (Math.ceil(searchArr.total / searchArr.select));
		}
		searchArr.postData.pageNum = searchArr.pageNum;
		addPage(searchArr);
		searchInfo();
	}
}
function searchInfo() {
	checkBoxDataArr = [];
	allData = [];
	//重新载入
	$("#loading").css('display', 'block');
	$("#list2").jqGrid('setGridParam', {
		url: "/devManage/deploy/getAllManualSystemInfo",
		postData: searchArr.postData,
		loadComplete: function (xhr) {
			allData = xhr.rows;
			searchArr.total = xhr.total;
			$("#loading").css('display', 'none');
			addPage(searchArr);
		},
	}).trigger("reloadGrid");
}

function autoSearchInfo() {



	//	checkBoxDataArr=[];
	//	allData=[];
	//	 //重新载入
	$("#list2").jqGrid('setGridParam', {
		url: "/devManage/deploy/getAllManualSystemInfo",
		postData: searchArr.postData,
		loadComplete: function (xhr) {
			allData = xhr.rows;
			searchArr.total = xhr.total;
			addPage(searchArr);
		},
	}).trigger("reloadGrid");
}

function changeSelect(This) {
	searchArr.select = $(This).val();
	searchArr.pageNum = 1;
	searchArr.postData = {
		"pageNum": searchArr.pageNum,
		"pageSize": searchArr.select,
	}
	searchInfo();
}


function changeTask() {
	var windowId = $("#commissWindow").val();
	initprdReq(windowId, tempSytemId);
}


//获取最新窗口
function changeTaskArtifact() {

	var windowId = $('#commissWindowArtifact option:selected').val();

	var tempSytemId = $("#system_Id").val();
	initprdReqArtifact(windowId, tempSytemId);


}
// function getReq(){
//
// 	var doms = $("[name=bootstrapTable]");
// 	var idStr = "";
// 	if(doms != null && doms != undefined){
// 		$.each(doms,function(index,dom){
// 			var name = $(dom).attr("id");
// 			var selectContent = $("#"+name+"Table").bootstrapTable('getSelections')[0];
// 			if (selectContent != undefined && !isNaN(selectContent.id)) {
// 				idStr += selectContent.id + ",";
// 			}
// 		})
// 	}
// 	if(idStr == ""){
//
// 		$("#reqFeatureTable4").bootstrapTable('destroy');
// 		return false;
// 	}
// 	var artifactids=idStr.substr(0,idStr.length-1);
//
// 	initprdReqArtifact(artifactids);
//
//
// }


function getReqTest() {

	var doms = $("[name=bootstrapTable]");
	var idStr = "";
	if (doms != null && doms != undefined) {
		$.each(doms, function (index, dom) {
			var name = $(dom).attr("id");
			var selectContent = $("#" + name + "Table").bootstrapTable('getSelections')[0];
			if (selectContent != undefined && !isNaN(selectContent.id)) {
				idStr += selectContent.id + ",";
			}
		})
	}
	if (idStr == "") {

		$("#reqFeatureTable").bootstrapTable('destroy');
		return false;
	}
	var artifactids = idStr.substr(0, idStr.length - 1);

	initprdReqArtifactTest(artifactids);


}


function searchBoxInfo() {
	searchArr.pageNum = 1;
	searchArr.postData.systemInfo = {
		"systemName": $("#seacrhSystemName").val(),
		"systemCode": $("#seacrhSystemNum").val(),
		"projectIds": $("#seacrhProject").val(),
	}
	searchArr.postData = {
		"pageNum": searchArr.pageNum,
		"pageSize": searchArr.select,
		"systemInfo": JSON.stringify(searchArr.postData.systemInfo),
		"scmBuildStatus": $("#scmBuildStatus").val()
	}
	searchInfo();
	addPage(searchArr);
}
var tempSytemId = 0;


function startBuilding(row, This) {
	//获取systemid下的冲刺 flag是下就显示默认选择
	systemIdScm = row.id;
	var env = $(This).attr("name");
	// getSprintBySystemId(row.id);
	// getWindowsBySystemId(row.id,env);
	getDeployStatus();
	var rows = JSON.stringify(row).replace(/"/g, '&quot;');
	$("#promptBox .modal-footer2").empty();
	$("#promptBox .modal-footer2").append('<button type="button" class="btn btn-primary" onclick="commitInfo(' + rows + ',' + env + ')">确定</button><button type="button" class="btn btn-default" data-dismiss="modal">取消</button>')
	if (env == prdIn || env == prdOut) {
		$("#plantformDiv .modal-footer").empty();
		$("#plantformDiv .modal-footer").append('<button type="button" class="btn btn-primary" onclick="commitInfo(' + rows + ',' + env + ')">开始部署</button><button type="button" class="btn btn-default" data-dismiss="modal">取消</button>')
	} else {
		$("#reqFeatureDiv .modal-footer").empty();
		$("#reqFeatureDiv .modal-footer").append('<button type="button" class="btn btn-primary" onclick="commitInfo(' + rows + ',' + env + ')">开始部署</button><button type="button" class="btn btn-default" data-dismiss="modal">取消</button>')

	}



	$("#normalService").modal("hide");
	if (env != prdIn && env != prdOut) {
		//测试环境
		getSprintBySystemId(row.id);
		getWindowsBySystemId(row.id);
		setTimeout(function () {
			getreqFeatureTable2(row.id, $("#windowScm").val(), $("#sprintScm").val());
		}, 1000);


	} else {
		//生产环境
		$("#recoveryTime").val("");
		$("#new_taskUser").val("");
		$("#new_taskUserId").val("");
		//	$("#deptName").val("");
		$("#configFile").val("");
		$("#sqlFile").val("");
		$("#operFile").val("");
		$("#sqlType").val("");
		$("#new_taskDeptId").val("");
		$("#new_taskDept").val("");


		tempSytemId = row.id;
		initWindow(row.id);
		setTimeout(function () {
			initprdReq($("#commissWindow").val(), row.id);
		}, 1000)
		//initprdReq($("#commissWindow").val(),row.id);
		//initWindow(row.id);
		clearFileinfo();


	}



}

function getreqFeatureTable2(systemId, windowId, sprintId) {

	$("#reqFeatureTable2").bootstrapTable('destroy');
	$("#reqFeatureTable2").bootstrapTable(
		{
			url: "/devManage/devtask/deplayReqFestureByParam",
			method: "post",
			queryParamsType: "",
			pagination: false,
			sidePagination: "server",
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			// singleSelect : true,//单选
			queryParams: function (params) {
				var param = {
					systemId: systemId,
					pageNumber: params.pageNumber,
					pageSize: params.pageSize,
					commissioningWindowId: windowId,
					sprintId: sprintId
				}
				return param;
			},
			responseHandler: function (res) {
				return res.rows;
			},
			columns: [{
				checkbox: true,
				width: "30px"
			}, {
				field: "id",
				title: "id",
				visible: false,
				align: 'center'
			}, {
				field: "featureCode",
				title: "任务编码",
				align: 'center'
			}, {
				field: "featureName",
				title: "任务名称",
				align: 'center'
			}, {
				field: "statusName",
				title: "任务状态",
				align: 'center'
			}, {
				field: "windowName",
				title: "投产窗口",
				align: 'center'
			}, {
				field: "systemVersionName",
				title: "系统版本",
				align: "center"
			}, {
				field: "",
				title: "部署状态",
				align: "center",
				formatter: function (value, row, index) {
                    var deployName = getDeployByReqFeatureId(row.id);
                    return deployName;
					/*var valueName = '';
					if (row.deployStatus != undefined && row.deployStatus != null) {
						var statusids = row.deployStatus.split(",");
						for (var j = 0; j < statusids.length; j++) {
							for (var i = 0; i < deployStatusData.length; i++) {
								if (deployStatusData[i].valueCode == statusids[j]) {
									valueName += deployStatusData[i].valueName + "，";
								}
							}
						}

					}

					return valueName.substring(0, valueName.length - 1);*/
				}

			}],
			onLoadSuccess: function () {

			},
			onLoadError: function () {

			}
		});
	$("#reqFeatureDiv").modal("show");
}

function getDeployByReqFeatureId(id){
    var deployName;
    $.ajax({
        type:"POST",
        url:"/devManage/devtask/findDeployByReqFeatureId",
        dataType:"json",
        data:{
            "featureId":id
        },
        async:false,
        success:function(data1){
            deployName = data1["deployName"] || '';
        }
    });
    return deployName;
}

function getSprintBySystemId(systemId) {

	$.ajax({
		url: "/devManage/deploy/getSprintBySystemId",
		type: "post",
		dataType: "json",
		data: {
			systemId: systemId
		},
		success: function (data) {
			$("#sprintScm").empty();
			$("#sprintScm").append("<option value=''>请选择</option>");
			if (data.sprintInfos.length > 0) {

				for (var i = 0; i < data.sprintInfos.length; i++) {
					if (data.sprintInfos[i].default == "true") {
						$("#sprintScm").append('<option selected value="' + data.sprintInfos[i].id + '">' + data.sprintInfos[i].sprintName + '</option>')
					} else {
						$("#sprintScm").append('<option value="' + data.sprintInfos[i].id + '">' + data.sprintInfos[i].sprintName + '</option>')
					}
				}
			}
			$('.selectpicker').selectpicker('refresh');
		}
	})


}

function getSprintBySystemIdManual(systemId) {

	$.ajax({
		url: "/devManage/deploy/getSprintBySystemId",
		type: "post",
		dataType: "json",
		data: {
			systemId: systemId
		},
		success: function (data) {
			$("#sprintMaual").empty();
			$("#sprintMaual").append("<option value=''>请选择</option>");
			if (data.sprintInfos.length > 0) {

				for (var i = 0; i < data.sprintInfos.length; i++) {
					if (data.sprintInfos[i].default == "true") {
						$("#sprintMaual").append('<option selected value="' + data.sprintInfos[i].id + '">' + data.sprintInfos[i].sprintName + '</option>')
					} else {
						$("#sprintMaual").append('<option value="' + data.sprintInfos[i].id + '">' + data.sprintInfos[i].sprintName + '</option>')
					}
				}
			}
			$('.selectpicker').selectpicker('refresh');
		}
	})


}




//
// function getWindowsBySystemId(systemId,env){
//
// 	$.ajax({
// 		url:"/devManage/deploy/getWindowsBySystemId",
// 		type:"post",
// 		dataType:"json",
// 		data:{
// 			systemId:systemId,
// 			env:env
// 		},
// 		success:function(data){
// 			$("#windowScm").empty();
// 			$("#windowScm").append("<option value=''>请选择</option>");
// 			if(data.resultList.length>0){
// 				$("#newsprintDiv").show();
// 				for(var i = 0;i<data.resultList.length;i++){
// 					if(data.resultList[i].default=="true"){
//
// 						$("#windowScm").append('<option selected value="'+data.resultList[i].id+'">'+data.resultList[i].windowName+'</option>')
// 					}else {
// 						$("#windowScm").append('<option value="' + data.resultList[i].id + '">' + data.resultList[i].windowName + '</option>');
// 					}
// 				}
// 			}
// 			$('.selectpicker').selectpicker('refresh');
// 		}
// 	})
//
//
// }


function getWindowsBySystemId(systemId) {

	$.ajax({
		url: "/devManage/deploy/getWindowsLimit",
		type: "post",
		dataType: "json",
		data: {
			systemId: systemId,

		},
		success: function (data) {
			$("#windowScm").empty();
			$("#windowScm").append("<option value=''>请选择</option>");
			if (data.windows.length > 0) {

				for (var i = 0; i < data.windows.length; i++) {
					if (data.windows[i].featureStatus == "defaultSelect") {

						$("#windowScm").append('<option selected value="' + data.windows[i].id + '">' + data.windows[i].windowName + '</option>')
					} else {
						$("#windowScm").append('<option value="' + data.windows[i].id + '">' + data.windows[i].windowName + '</option>');
					}
				}
			}
			$('.selectpicker').selectpicker('refresh');
		}
	})


}

function getWindowsBySystemIdManual(systemId) {

	$.ajax({
		url: "/devManage/deploy/getWindowsLimit",
		type: "post",
		dataType: "json",
		data: {
			systemId: systemId
		},
		success: function (data) {
			$("#windowMaual").empty();
			$("#windowMaual").append("<option value=''>请选择</option>");
			if (data.windows.length > 0) {

				for (var i = 0; i < data.windows.length; i++) {
					if (data.windows[i].featureStatus == "defaultSelect") {
						$("#windowMaual").append('<option selected value="' + data.windows[i].id + '">' + data.windows[i].windowName + '</option>')
					} else {
						$("#windowMaual").append('<option value="' + data.windows[i].id + '">' + data.windows[i].windowName + '</option>');
					}
				}
			}
			$('.selectpicker').selectpicker('refresh');
		}
	})


}
function clearFileinfo() {
	sql_files_flag = [];
	config_files_flag = [];
	oper_files_flag = [];
	sql_files = [];
	config_files = [];
	oper_files = [];
	$(".file-upload-tb").empty();
}
/*function initprdReq(windowId,systemId){


	$("#reqFeatureTable3").bootstrapTable('destroy');
	$("#reqFeatureTable3").bootstrapTable({
		url:"/devManage/devtask/deplayPrdReqFesture",
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
				systemId:systemId,
				pageNumber:params.pageNumber,
				pageSize:params.pageSize,
				commissioningWindowId:windowId
			}
			return param;
		},
		columns : [{
			field : "id",
			title : "id",
			visible : false,
			align : 'center'
		},{
			field : "featureCode",
			title : "任务编码",
			align : 'center'
		},{
			field : "featureName",
			title : "任务名称",
			align : 'center'
		},{
			field : "statusName",
			title : "任务状态",
			align : 'center'
		},{
			field : "windowName",
			title : "投产窗口",
			align : 'center'
		},{
			field :"systemVersionName",
			title  : "系统版本",
			align :"center"
		},{
			field :"",
			title  : "部署状态",
			align :"center",
			formatter:function(value,row,index){
				var valueName = '';
				if(row.deployStatus!=undefined && row.deployStatus!=null){
					var statusids = row.deployStatus.split(",");
					for(var j =0;j<statusids.length;j++){
						for(var i=0; i<deployStatusData.length;i++){
							if(deployStatusData[i].valueCode ==statusids[j]){
								valueName+=	deployStatusData[i].valueName+"，";
							}
						}
					}

				}

				return valueName.substring(0,valueName.length-1);
			}

		}],
		onLoadSuccess:function(){

		},
		onLoadError : function() {

		}
	});

	$("#plantformDiv").modal("show");

}*/


function initprdReq(windowId, systemId) {
	$("#reqFeatureTable3").bootstrapTable('destroy');
	$("#reqFeatureTable3").bootstrapTable({
		url: "/devManage/devtask/deplayPrdReqFestureNoPage",
		method: "post",
		queryParamsType: "",
		pagination: false,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		responseHandler: function (res) {
			return res.rows;
		},
		// singleSelect : true,//单选
		queryParams: function (params) {
			var param = {
				systemId: systemId,
				pageNumber: params.pageNumber,
				pageSize: params.pageSize,
				commissioningWindowId: windowId,
				windowId: windowId

			}
			return param;
		},
		columns: [{
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "featureCode",
			title: "任务编码",
			align: 'center'
		}, {
			field: "featureName",
			title: "任务名称",
			align: 'center'
		}, {
			field: "statusName",
			title: "任务状态",
			align: 'center'
		}, {
			field: "windowName",
			title: "投产窗口",
			align: 'center'
		}, {
			field: "systemVersionName",
			title: "系统版本",
			align: "center"
		}, {
			field: "",
			title: "部署状态",
			align: "center",
			formatter: function (value, row, index) {
				var valueName = '';
				if (row.deployStatus != undefined && row.deployStatus != null) {
					var statusids = row.deployStatus.split(",");
					for (var j = 0; j < statusids.length; j++) {
						for (var i = 0; i < deployStatusData.length; i++) {
							if (deployStatusData[i].valueCode == statusids[j]) {
								valueName += deployStatusData[i].valueName + "，";
							}
						}
					}

				}

				return valueName.substring(0, valueName.length - 1);
			}

		}],
		onLoadSuccess: function () {

		},
		onLoadError: function () {

		}
	});

	$("#plantformDiv").modal("show");

}


function startBuildingArtifact(row, This) {
	$("#reqFeatureTable4").bootstrapTable('destroy');
	getDeployStatus();
	var env = $(This).attr("name");
	var envName = $(This).text();
	clearDefListMenu();
	$("#deploymentConfigTit").html(row.systemName);
	$.ajax({
		url: "/devManage/deploy/getModuleInfo",
		method: "post",
		data: {
			"systemId": row.id,
			"env": env,
			"architectureType": row.architectureType
		},
		success: function (data) {
			var moduleStr = "";
			var idStr = "";
			$("#configFileArtifact").val("");
			$("#sqlFileArtifact").val("");
			$("#sqlTypeArtifact").val("");
			$("#operFileArtifact").val("");
			$("#recoveryTimeArtifact").val("");
			$("#new_taskUserArtifact").val("");
			$("#new_taskUserIdArtifact").val("");
			//			  $("#deptNameArtifact").val("");
			//
			//			  $("#deptNameArtifact").selectpicker('refresh');
			$("#new_taskDeptIdArtifact").val("");
			$("#new_taskDeptArtifact").val("");


			clearFileinfo();

			$("#commissWindowArtifact").empty();
			$("#commissWindowArtifact").selectpicker('refresh');
			if (env == prdIn || env == prdOut) {
				$("#artPrdflag").show();
				$("#artReqFeature").hide();
			} else {
				$("#artReqFeatureTable").bootstrapTable('destroy');
				$("#artPrdflag").hide();
				$("#artReqFeature").show();
			}
			//		   }else{
			//			   $("#artPrdflag").hide();
			//			   $("#artReqFeature").hide();
			//		   }
			idStr = 'serverDiv';
			$("#system_Id").val(row.id);
			$("#deploymentConfig").modal('show');

			if (data.modules == undefined || data.modules.length == 0) {
				$("#serverDiv .def_colList_menu").css('display', 'none');
				var tableStr = '<div id="artifact" name="bootstrapTable"><table id="artifactTable"></table></div>';
				$("#serverDiv .def_rowList_cont").append(tableStr);
				loadingArtifactTable("artifactTable", data.artifactList);
			} else {
				$("#serverDiv .def_colList_menu").css('display', 'block');
				for (var i = 0; i < data.modules.length; i++) {
					if (i == 0) {
						var liStr = '<li class="menuSelected_col" _Id="' + data.modules[i].id + '" isclick="true" onclick="colClick( \'' + idStr + '\',this )">' + data.modules[i].moduleName + '</li>';
					} else {
						var liStr = '<li _Id="' + data.modules[i].id + '" isclick="false" onclick="colClick( \'' + idStr + '\',this )">' + data.modules[i].moduleName + '</li>';
					}
					var tableStr = '<div id="' + data.modules[i].moduleName + '" name="bootstrapTable"><table id="' + data.modules[i].moduleName + 'Table"></table></div>';
					$("#serverDiv .def_rowList_cont").append(tableStr);
					$("#serverDiv .def_colList_menu>ul").append(liStr);
				}
				loadingArtifactTable(data.modules[0].moduleName + "Table", data.artifactList);
			}
			var liStr = '<li class="menuSelected_row" id="env" _Id="' + env + '">' + envName + '</li>';
			$("#serverDiv .def_rowList_menu>ul").append(liStr);

		}

	});
}
/*加载jar包*/
function loadingArtifactTable(id, data) {
	var systemId = $("#system_Id").val();
	$("#" + id + "").bootstrapTable('destroy');
	$("#" + id + "").bootstrapTable({
		method: "post",
		singleSelect: true,
		queryParamsType: "",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		data: data,
		onCheck: function (row) {
			var envCheck = $("#env").attr("_Id");
			if (envCheck == prdIn || envCheck == prdOut) {

				getWindows();
			} else {
				getReqTest();
			}

		},

		onCheckAll: function (rows) {
			var envCheck = $("#env").attr("_Id");
			if (envCheck == prdIn || envCheck == prdOut) {
				getWindows();
			} else {
				if (rows.length > 0) {
					//getReqFeature(systemId);
					getReqTest();
				}
			}
		},
		onUncheckAll: function (rows) {
			var envCheck = $("#env").attr("_Id");
			if (envCheck == prdIn || envCheck == prdOut) {
				getWindows();
			} else {
				$("#artReqFeatureTable").bootstrapTable('destroy');
			}
		},

		onUncheck: function (row) {
			var envCheck = $("#env").attr("_Id");
			if (envCheck == prdIn || envCheck == prdOut) {
				getWindows();
			} else {
				$("#artReqFeatureTable").bootstrapTable('destroy');
				getReqTest();
			}
		},
		columns: [{
			checkbox: true,
			width: "30px"
		}, {
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "repository",
			title: "仓库名",
			align: 'center'
		}, {
			field: "artifactId",
			title: "artifactId",
			align: 'center'
		}, {
			field: "version",
			title: "版本",
			align: 'center'
		},{
			field: "tags",
			title: "标记",
			align: 'center'
		}],
		onLoadSuccess: function () {
		},
		onLoadError: function () {

		}
	});
}
function getReqFeature(systemId) {
	$("sprintPack").empty();

	//制品部署开发任务
	$("#artReqFeatureTable").bootstrapTable('destroy');
	$("#artReqFeatureTable").bootstrapTable({
		url: "/devManage/devtask/deplayReqFestureByParam",
		method: "post",
		queryParamsType: "",
		pagination: false,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		responseHandler: function (res) {
			return res.rows;
		},
		// singleSelect : TRUE,//单选
		queryParams: function (params) {
			var param = {
				systemId: systemId,
				pageNumber: params.pageNumber,
				pageSize: params.pageSize,
			}
			return param;
		},
		columns: [/*{
	            checkbox : true,
	            width : "30px"
	        },*/{
				field: "id",
				title: "id",
				visible: false,
				align: 'center'
			}, {
				field: "featureCode",
				title: "任务编码",
				align: 'center'
			}, {
				field: "featureName",
				title: "任务名称",
				align: 'center'
			}, {
				field: "statusName",
				title: "任务状态",
				align: 'center'
			}, {
				field: "windowName",
				title: "投产窗口",
				align: 'center'
			}, {
				field: "systemVersionName",
				title: "系统版本",
				align: "center"
			}, {
				field: "deployStatus",
				title: "部署状态",
				align: "center",
				formatter: function (value, row, index) {
					var valueName = '';
					if (row.deployStatus != undefined && row.deployStatus != null) {
						var statusids = row.deployStatus.split(",");
						for (var j = 0; j < statusids.length; j++) {
							for (var i = 0; i < deployStatusData.length; i++) {
								if (deployStatusData[i].valueCode == statusids[j]) {
									valueName += deployStatusData[i].valueName + "，";
								}
							}
						}

					}
					return valueName.substring(0, valueName.length - 1);
				}
			}
		],
		onLoadSuccess: function () {

		},
		onLoadError: function () {

		}
	});
}

function clearDefListMenu() {
	$("#deploymentConfig .def_colList li").remove();
	$("#deploymentConfig .def_rowList li").remove();
	$("[name=bootstrapTable]").remove();
	$("#deploymentConfig #serverGroup").empty();
}

function colClick(id, This) {

	$('#' + id + ' .def_colList_menu>ul>li').removeClass("menuSelected_col");
	var moduleId = $(This).attr("_Id");
	var moduleName = $(This).text();
	$(This).addClass("menuSelected_col");
	var isclick = $(This).attr("isclick");
	$("[name=bootstrapTable]").hide();
	$("#" + moduleName + "").show();
	if (isclick == "false") {
		changeModule(moduleId, moduleName);
	}
}



function changeModule(id, name) {
	var env = $("#env").attr("_Id");
	var systemId = $("#system_Id").val();
	$.ajax({
		url: "/devManage/deploy/getArtifactInfo",
		method: "post",
		data: {
			"systemId": systemId,
			"env": env,
			"moduleId": id
		},
		success: function (data) {
			if (data.status == 2) {
				layer.alert("切换失败", { icon: 2 });
			} else {
				loadingArtifactTable(name + "Table", data.list);
				$(".def_colList_menu li[_Id=" + id + "]").attr("isclick", "true");
			}
		}
	});
}
/*部署*/
function updateDeploy() {
	var fromfile = new FormData();

	var doms = $("[name=bootstrapTable]");
	var env = $("#env").attr("_Id");
	var systemId = $("#system_Id").val();
	var idStr = "";
	if (doms != null && doms != undefined) {
		$.each(doms, function (index, dom) {
			var name = $(dom).attr("id");
			var selectContent = $("#" + name + "Table").bootstrapTable('getSelections')[0];
			if (selectContent != undefined && !isNaN(selectContent.id)) {
				idStr += selectContent.id + ",";
			}
		})
	}
	if (idStr == "") {
		layer.alert("请选择一行记录", { icon: 0 });
		return false;
	}

	fromfile.append("systemId", systemId);
	fromfile.append("env", env);
	fromfile.append("artifactids", idStr.substr(0, idStr.length - 1));


	if (env == prdOut || env == prdIn) {
		var reqFeatureqIds = "";
		var allContent = $("#reqFeatureTable4").bootstrapTable('getData');
		for (var i = 0; i < allContent.length; i++) {
			reqFeatureqIds += allContent[i].id + ",";
		}
		if (reqFeatureqIds == "" || reqFeatureqIds == "reqFeatureTable4,") {
			layer.alert("开发任务不能为空!", { icon: 2 });
			return false;
		}


		var recoveryTime = $("#recoveryTimeArtifact").val();
		var userId = $("#new_taskUserIdArtifact").val();
		//var deptId=$("#deptNameArtifact").val();

		var deptId = $("#new_taskDeptIdArtifact").val();

		//var configFileinfo=$("#configFileArtifact")[0].files;
		//var sqlFileinfo=$("#sqlFileArtifact")[0].files;
		//var operFileinfo=$("#operFileArtifact")[0].files;
		var sqlFileinfo = sql_files;
		var configFileinfo = config_files;
		var operFileinfo = oper_files;






		for (var i = 0; i < configFileinfo.length; i++) {
			fromfile.append('configFile', configFileinfo[i]);

		}
		for (var i = 0; i < sqlFileinfo.length; i++) {
			fromfile.append('sqlFile', sqlFileinfo[i]);

		}

		if (sqlFileinfo.length > 0) {
			var sqlType = $("#sqlTypeArtifact").val();
			if (sqlType == "") {
				layer.alert("请选择数据库脚本类型", { icon: 2 });
				return false;
			} else {
				fromfile.append("sqlType", sqlType);
			}
		}

		for (var i = 0; i < operFileinfo.length; i++) {
			fromfile.append('operFile', operFileinfo[i]);
		}


		if (recoveryTime == "") {
			layer.alert("请选择计划恢复时间", { icon: 2 });
			return false;
		}
		if (userId == "") {
			layer.alert("请选择版本负责人", { icon: 2 });
			return false;
		}
		if (deptId == "") {
			layer.alert("请选择所属处室", { icon: 2 });
			return false;
		}

		fromfile.append("recoveryTime", recoveryTime);
		fromfile.append("userId", userId);
		fromfile.append("deptId", deptId);
		fromfile.append("reqFeatureqIds", reqFeatureqIds);



	} else {
		var reqFeatureqIds = "";
		var allContent = $("#artReqFeatureTable").bootstrapTable('getData');
		for (var i = 0; i < allContent.length; i++) {
			reqFeatureqIds += allContent[i].id + ",";
		}
		fromfile.append("reqFeatureqIds", reqFeatureqIds);
	}

	$("#loading").css('display', 'block');
	$.ajax({
		url: "/zuul/devManage/deploy/buildPackageDeploy",
		method: "post",
		processData: false,
		contentType: false,
		data: fromfile,
		success: function (data) {
			if (data.status == 2) {
				layer.alert(data.message, { icon: 2 });

			} else {
				$("#deploymentConfig").modal('hide');
				detaillog(data);
			}
			pageInit();

			clearFileinfo();

			$("#loading").css('display', 'none');

		}
	});
}

function commitInfo(row, env) {
	var fromfile = new FormData();
	var modules = '';
	if (row.architectureType == 1) {
		for (var key in checkBoxDataArr) {
			if (checkBoxDataArr[key].parent == row.key_id) {
				modules += checkBoxDataArr[key].id + ",";
			}
		}
		if (modules == '') {

			layer.alert('未选择子服务，请选择子服务后尝试部署', {
				icon: 2,
				title: "提示信息"
			});
			return;
		}
	}
	var reqFeatureqIds = "";
	fromfile.append("sysId", row.id);
	fromfile.append("systemName", row.systemName);
	fromfile.append("serverType", row.architectureType);
	fromfile.append("modules", modules);
	fromfile.append("env", env);

	if (env != prdOut && env != prdIn) {
		var selectContent = $("#reqFeatureTable2").bootstrapTable('getSelections');
		for (var i = 0; i < selectContent.length; i++) {
			reqFeatureqIds += selectContent[i].id + ",";
		}



	}

	if (env == prdOut || env == prdIn) {

		var allContent = $("#reqFeatureTable3").bootstrapTable('getData');
		for (var i = 0; i < allContent.length; i++) {
			reqFeatureqIds += allContent[i].id + ",";
		}
		if (reqFeatureqIds == "" || reqFeatureqIds == "reqFeatureTable3,") {
			layer.alert("开发任务不能为空!", { icon: 2 });
			return false;
		}

		var recoveryTime = $("#recoveryTime").val();
		var userId = $("#new_taskUserId").val();
		//	var deptId=$("#deptName").val();
		var deptId = $("#new_taskDeptId").val();

		//var configFileinfo=$("#configFile")[0].files;
		var configFileinfo = config_files_flag;
		for (var i = 0; i < configFileinfo.length; i++) {
			fromfile.append('configFile', configFileinfo[i]);
		}

		//var sqlFileinfo=$("#sqlFile")[0].files;
		var sqlFileinfo = sql_files_flag;
		for (var i = 0; i < sqlFileinfo.length; i++) {
			fromfile.append('sqlFile', sqlFileinfo[i]);
		}

		if (sqlFileinfo.length > 0) {
			var sqlType = $("#sqlType").val();
			if (sqlType == "") {
				layer.alert("请选择数据库脚本类型", { icon: 2 });
				return false;
			} else {
				fromfile.append("sqlType", sqlType);
			}
		}


		//var operFileinfo=$("#operFile")[0].files;
		var operFileinfo = oper_files_flag;

		for (var i = 0; i < operFileinfo.length; i++) {
			fromfile.append('operFile', operFileinfo[i]);
		}
		if (recoveryTime == "") {
			layer.alert("请选择计划恢复时间", { icon: 2 });
			return false;
		}
		if (userId == "") {
			layer.alert("请选版本负责人", { icon: 2 });
			return false;
		}
		if (deptId == "") {
			layer.alert("请选择所属处室", { icon: 2 });
			return false;
		}

		fromfile.append("recoveryTime", recoveryTime);
		fromfile.append("userId", userId);
		fromfile.append("deptId", deptId);
		//fromfile.append("configFile", configFileinfo[0]);
		//		fromfile.append("operFile", operFileinfo);
		//		fromfile.append("sqlFile", sqlFileinfo);

	}
	fromfile.append("reqFeatureqIds", reqFeatureqIds);


	$("#loading").css('display', 'block');





	$.ajax({
		type: 'post',
		url: "/zuul/devManage/deploy/buildJobAutoDeploy",
		method: "post",

		processData: false,
		contentType: false,
		data: fromfile,
		success: function (data) {
			if (data.status == 2) {
				layer.alert(data.message, {
					icon: 2,
					title: "提示信息"
				});
				$("#promptBox").modal("hide");
				$("#reqFeatureDiv").modal("hide");
				$("#plantformDiv").modal("hide");
				$("#loading").css('display', 'none');
				return;
			}
			pageInit();
			$("#promptBox").modal("hide");
			$("#reqFeatureDiv").modal("hide");
			$("#loading").css('display', 'none');

			clearFileinfo();

			$("#plantformDiv").modal("hide");

			//新增日志处理
			detaillog(data);
		},
		error: function () {
		}
	});
}

function buildHistory(row) {
	var deployType = row.deployType;
	var jobType = 2;
	if (deployType == 2) {
		jobType = 2;
	}

	$("#loading").css('display', 'block');
	$("#buildHistoryTable").bootstrapTable('destroy');
	$("#buildHistoryTable").bootstrapTable({
		url: "/devManage/deploy/getAllDeployMessage",
		method: "post",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		pagination: true,
		queryParamsType: "",
		sidePagination: "server",
		showHeader: false,
		queryParams: function (params) {
			var param = {
				pageSize: params.pageSize,
				pageNumber: params.pageNumber,
				systemId: row.id,
				createType: row.createType,
				jobType: jobType
			};
			return param;
		},
		columns: [{
			field: "id",
			title: "id",
			align: 'left',
			visible: false
		}, {
			field: "envType",
			title: "envType",
			align: 'left',
			formatter: function (value, row, index) {
				var rows = JSON.stringify(row).replace(/"/g, '&quot;');
				var classA = '';
				var spanFont = '';
				var type = ''
				if (row.buildStatus == 3) {
					classA = "fa-close iconFail2";
					spanFont = '部署失败';
				} else if (row.buildStatus == 2) {
					classA = "fa-check iconSuccess2";
					spanFont = '部署成功';
				}  else if (row.buildStatus == 5) {
					classA = "fa-close iconFail2";
					spanFont = '测试失败';
				}


				else {
					return '';
				}

				if (row.createType == 1) {
					for (var k in environmentTypeData) {
						if (k == row.envType) {
							type = environmentTypeData[k];
						}
					}
				} else if (row.createType == 2) {
					type = row.jobName;
				}
				if (row.createType == 1){
					return '<div class="allInfo"><div class="rowdiv buildHistoryInfo"><div class="form-group def_col_9"><div class="successInfo2"><span class="fa '+classA+'"></span> '+spanFont+" "+type+'</div></div><div class="form-group def_col_8 _show_ellipsis" title='+row.moduleNames+'>'+row.moduleNames+'</div><div class="form-group def_col_4">'+row.userName+'</div><div class="form-group def_col_10">'+row.startDate+' | '+row.endDate+'</div><div class="form-group def_col_5"><button type="button" onclick="detailInfo('+rows+','+index+')" class="btn infoBtm">查看信息</button></div></div></div>';

				}else if(row.createType == 2){
					return '<div class="allInfo"><div class="rowdiv buildHistoryInfo"><div class="form-group def_col_9"><div class="successInfo2"><span class="fa '+classA+'"></span> '+spanFont+" "+type+'</div></div><div class="form-group def_col_8 _show_ellipsis" title='+row.buildParameter+'>'+row.buildParameter+'</div><div class="form-group def_col_4">'+row.userName+'</div><div class="form-group def_col_10">'+row.startDate+' | '+row.endDate+'</div><div class="form-group def_col_5"><button type="button" onclick="detailInfo('+rows+','+index+')" class="btn infoBtm">查看信息</button></div></div></div>';

				}

					//return '<div class="allInfo"><div class="rowdiv buildHistoryInfo"><div class="form-group def_col_9"><div class="successInfo2"><span class="fa '+classA+'"></span> '+spanFont+" "+type+'</div></div><div class="form-group def_col_8 _show_ellipsis" title='+row.moduleNames+'>'+row.moduleNames+'</div><div class="form-group def_col_4">'+row.userName+'</div><div class="form-group def_col_10">'+row.startDate+' | '+row.endDate+'</div><div class="form-group def_col_5"><button type="button" onclick="detailInfo('+rows+','+index+')" class="btn infoBtm">查看信息</button></div></div></div>';
			}
		}],
		onLoadSuccess: function () {
			$("#buildHistoryDIV").modal("show");
			$("#loading").css('display', 'none');
		},
		onLoadError: function () {

		}
	});

}
/*判断是否为null*/
function isValueNull(value) {
	if (value==0 || value=='0') {
		return '0'
	}
	if (!value) {
		return ''
	}
	return value
}
function detailInfo(row, index) {
	if (row.buildStatus == 2) {
		$("#buildingFail").css("display", "none");
		$("#testFail").css("display", "none");
		$("#buildingSuccess").css("display", "block");
	} else if (row.buildStatus == 3) {
		$("#buildingFail").css("display", "block");
		$("#testFail").css("display", "none");
		$("#buildingSuccess").css("display", "none");
	}
	else if (row.buildStatus == 5) {
		$("#testFail").css("display", "block");
		$("#buildingFail").css("display", "none");
		$("#buildingSuccess").css("display", "none");
	}
	$("#buildLogsInfoPre").text();
	$("#loading").css('display', 'block');
	$('#_testResult').empty();
	$.ajax({
		url: "/devManage/deploy/getDeployMessageById",
		method: "post",
		data: {
			jobRunId: row.id,
			createType: row.createType
		},
		success: function (data) {
			$("#SonarResult").empty();
			$("#detailInfoSuccessStartTime").text(data.startDate);
			$("#detailInfoSuccessEndDate").text(data.endDate);
			$("#scanningTime").text(data.endDate);
			$("#buildLogsInfoPre").text(data.buildLogs);
			$('#ui_testResult').empty();
			$('#_testResult').empty();
			$('#ui_testResult_modal').hide();
			$('#_testResult_modal').hide();
			if (data.testResultList) {

				if (data.testResultList.length) {
					$('#_testResult_modal').show();

					for (let i = 0; i < data.testResultList.length; i++) {
						let key = data.testResultList[i];
						let _Result_code, __Name_, __Name_val;
						if (key.moduleName) {
							__Name_ = `模块名`; __Name_val = key.moduleName;
						} else {
							__Name_ = `系统名`; __Name_val = key.systemName;
						}
						if (key.testResult == '失败') {
							_Result_code = `<span style="color:red;">${key.testResult}</span>`
						} else {
							_Result_code = `<span style="color:green;">${key.testResult}</span>`
						}
						var _successNumber = isValueNull(key.successNumber)
						var _failedNumber = isValueNull(key.failedNumber)
						var _testResultDetailUrl = isValueNull(key.testResultDetailUrl)
						var _testScene = isValueNull(key.testScene)

						$('#_testResult').append(`
				 			<div class="rowdiv">
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">测试场景：</label>
				 					<label class="col-sm-6 control-label font_left _moduleName _show_ellipsis">${_testScene}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-5 control-label fontWeihgt">${__Name_}：</label>
				 					<label class="col-sm-7 control-label font_left _moduleName _show_ellipsis">${__Name_val}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">结果：</label>
				 					<label class="col-sm-6 control-label font_left _testResult">${_Result_code}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">成功数：</label>
				 					<label class="col-sm-6 control-label font_left _successNumber">${_successNumber}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">失败数：</label>
				 					<label class="col-sm-6 control-label font_left _failedNumber">${_failedNumber}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-2 control-label fontWeihgt"></label>
				 					<label class="col-sm-10 control-label font_left _testResultDetailUrl _show_ellipsis">
				 						<a target="_blank"  href="${_testResultDetailUrl}">查看详情</a>
				 					</label>
				 				</div>
				 			</div>
				 		`);
					}
				}
			} else {
				$('#_testResult_modal').hide();
			}




			if (data.uiTestResultList) {
				if (data.uiTestResultList.length) {
					$('#ui_testResult_modal').show();

					for (let i = 0; i < data.uiTestResultList.length; i++) {
						let key = data.uiTestResultList[i];
						let _Result_code, __Name_, __Name_val;
						if (key.moduleName) {
							__Name_ = `模块名`; __Name_val = key.moduleName;
						} else {
							__Name_ = `系统名`; __Name_val = key.systemName;
						}
						if (key.testResult == '失败') {
							_Result_code = `<span style="color:red;">${key.testResult}</span>`
						} else {
							_Result_code = `<span style="color:green;">${key.testResult}</span>`
						}

						var _successNumber = isValueNull(key.successNumber)
						var _failedNumber = isValueNull(key.failedNumber)
						var _testResultDetailUrl = isValueNull(key.testResultDetailUrl)
						var _testScene = isValueNull(key.testScene)

						$('#ui_testResult').append(`
				 			<div class="rowdiv">
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">测试场景：</label>
				 					<label class="col-sm-6 control-label font_left _moduleName _show_ellipsis">${_testScene}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-5 control-label fontWeihgt">${__Name_}：</label>
				 					<label class="col-sm-7 control-label font_left _moduleName _show_ellipsis">${__Name_val}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">结果：</label>
				 					<label class="col-sm-6 control-label font_left _testResult">${_Result_code}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">成功数：</label>
				 					<label class="col-sm-6 control-label font_left _successNumber">${_successNumber}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-6 control-label fontWeihgt">失败数：</label>
				 					<label class="col-sm-6 control-label font_left _failedNumber">${_failedNumber}</label>
				 				</div>
				 				<div class="form-group col-md-2">
				 					<label class="col-sm-2 control-label fontWeihgt"></label>
				 					<label class="col-sm-10 control-label font_left _testResultDetailUrl _show_ellipsis">
				 						<a target="_blank"  href="${_testResultDetailUrl}">查看详情</a>
				 					</label>
				 				</div>
				 			</div>
				 		`);
					}
				}
			} else {
				$('#ui_testResult_modal').hide();
			}






			$("#detailInfoDIV").modal("show");
			$('#detailInfoDIV').on('shown.bs.modal', function (e) {
				$("#loading").css('display', 'none');
			})
		},
		error: function () {
		}
	});
}
function creatAllJob(This) {
	if (checkBoxDataArr.length == 0) {

		layer.alert('请选择服务', {
			icon: 2,
			title: "提示信息"
		});
		return;
	} else {
		var data = [];
		for (var key in checkBoxDataArr) {
			if (checkBoxDataArr[key].level == 0) {
				var obj = {};
				obj.sysId = checkBoxDataArr[key].id;
				obj.systemName = checkBoxDataArr[key].systemName;
				obj.serverType = checkBoxDataArr[key].architectureType;
				obj.modules = '';
				data.push(obj);
			} else if (checkBoxDataArr[key].level == 1) {
				var flag = true;
				for (var i in data) {
					if (checkBoxDataArr[key].parent == data[i].key_id) {
						data[i].modules += checkBoxDataArr[key].id + ",";
						flag = false;
					}
				}
				if (flag == true) {
					for (var i in allData) {
						if (allData[i].key_id == checkBoxDataArr[key].parent) {
							var obj = {};
							obj.sysId = allData[i].id;
							obj.systemName = allData[i].systemName;
							obj.serverType = allData[i].architectureType;
							obj.key_id = allData[i].key_id;
							obj.modules = checkBoxDataArr[key].id + ",";
							data.push(obj);
						}
					}
				}
			}
		}
		var i = 0;
		for (var j = 0; j < data.length; j++) {
			if (data[i].architectureType == 1) {
				i++;
			}
		}
		if (i > 10) {

			layer.alert('普通服务的数量不得超过10个', {
				icon: 2,
				title: "提示信息"
			});
			return;
		} else {
			$.ajax({
				url: "/devManage/deploy/creatJenkinsDeployJobBatch",
				method: "post",
				data: {
					data: JSON.stringify(data),
					env: $(This).attr("name"),
				},
				success: function (data) {
					if (data.status == 2) {

						layer.alert(data.message, {
							icon: 2,
							title: "提示信息"
						});
						return;
					}
					setTimeout("searchBoxInfo()", 1000)
				},
				error: function () {
				}
			});
		}
	}
}

function timeCycle(This) {
	$(".leftMenu_ul").empty();
	$("#sonarDate a").removeClass('chooseItem');
	$(This).addClass('chooseItem');
	var id = $("#sonarRowId").val();
	var type = $("#sonarRowType").val();
	var createType = $("#sonarRowCreateType").val();
	var startDate;
	var endDate;
	var arr = [];
	if ($(This).attr("idValue") == 0) {
		startDate = getTodayDate();
		endDate = getTodayDate();
	} else if ($(This).attr("idValue") == 1) {
		startDate = getWeekStartDate();
		endDate = getWeekEndDate();
	} else if ($(This).attr("idValue") == 2) {
		startDate = getMonthStartDate();
		endDate = getMonthEndDate();
	} else if (This.element.attr("idValue") == 3) {
		arr = $("#sonarTime").val().split(' - ');
		startDate = arr[0];
		endDate = arr[1];
	}
	$("#sonarTime").val(startDate + " - " + endDate);
	requestSonarHistory(id, type, startDate, endDate, createType);
}

function sonarHistory(row) {

	$(".leftMenu_ul").empty();
	$("#sonarDate a").removeClass("chooseItem");
	$("#sonarDate a[idValue='1']").addClass("chooseItem");
	$("#sonarHistoryTitle").text(row.systemCode + " " + row.systemName + " 扫描历史");
	$("#sonarRowId").val(row.id);
	$("#sonarRowType").val(row.architectureType);
	$("#sonarRowCreateType").val(row.createType);
	var startDate = getWeekStartDate();
	var endDate = getWeekEndDate();
	$("#sonarTime").val(startDate + " - " + endDate);
	requestSonarHistory(row.id, row.architectureType, startDate, endDate, row.createType);
}
function requestSonarHistory(id, type, startDate, endDate, createType) {
	if (createType == 1) {
		if (type == 1) {
			$.ajax({
				url: "/devManage/deploy/getSonarMessageMincro",
				method: "post",
				data: {
					systemId: id,
					startDate: startDate,
					endDate: endDate,
				},
				success: function (data) {
					showMincroEchartsContent(data);
				},
			});
		} else if (type == 2) {
			$.ajax({
				url: "/devManage/deploy/getSonarMessage",
				method: "post",
				data: {
					systemId: id,
					startDate: startDate,
					endDate: endDate,
				},
				success: function (data) {
					showEchartsContent(data);
				},
			});
		}
	} else if (createType == 2) {
		$.ajax({
			url: "/devManage/deploy/getSonarMessageManual",
			method: "post",
			data: {
				systemId: id,
				startDate: startDate,
				endDate: endDate,
			},
			success: function (data) {
				showMincroEchartsContent(data);
			},
		});

	}
	$("#sonarHistory").modal("show");
}
function down(This) {
	if ($(This).hasClass("fa-angle-double-down")) {
		$(This).removeClass("fa-angle-double-down");
		$(This).addClass("fa-angle-double-up");
		$(This).parents('.allInfo').children(".def_content").slideDown(100);
	} else {
		$(This).addClass("fa-angle-double-down");
		$(This).removeClass("fa-angle-double-up");
		$(This).parents('.allInfo').children(".def_content").slideUp(100);
	}
}
//清空表格内容
function clearContent(that) {
	$(that).parent().children('input').val("");
	$(that).parent().children(".btn_clear").css("display", "none");
}

function clearSearch() {
	$('#seacrhSystemName').val("");
	$('#seacrhSystemNum').val("");
	$("#seacrhProject").selectpicker('val', '');
}

function isChecked(row, That) {
	if ($(That).is(':checked') == true) {
		if (row.level == 0) {
			if (row.architectureType == 1) {
				//选中微服务下所有子服务
				//判断 是否有 子节点 已经 被选中 如果有 先清空，然后重新 循环找出所有子节点
				for (var i in checkBoxDataArr) {
					if (checkBoxDataArr[i].parent == row.key_id) {
						checkBoxDataArr.splice($.inArray(checkBoxDataArr[i], checkBoxDataArr), 1);
					}
				}
				for (var i in allData) {
					if (allData[i].parent == row.key_id) {
						checkBoxDataArr.push(allData[i]);
						$("#check" + allData[i].key_id).prop("checked", true);
					}
				}
			} else if (row.architectureType == 2) {
				checkBoxDataArr.push(row);
			}
		} else if (row.level == 1) {
			checkBoxDataArr.push(row);
		}
	} else {
		//先取消 子节点 在删除其 选中节点
		for (var i in allData) {
			if (row.key_id == allData[i].parent) {
				$("#check" + allData[i].key_id).prop("checked", false);
				checkBoxDataArr.splice($.inArray(allData[i], checkBoxDataArr), 1);
			}
		}
		for (var i in checkBoxDataArr) {
			if (row.key_id == checkBoxDataArr[i].key_id) {
				checkBoxDataArr.splice($.inArray(checkBoxDataArr[i], checkBoxDataArr), 1);
			}
		}
	}
}

function stringToArr(data) {
	var len = data.length;//str为要处理的字符串
	result = data.substring(1, len - 1);//result为你需要的字符串
	return result.split(',');
}
function showEchartsContent(data) {
	$("#sonarHistory .leftMenu").css("display", "none");
	$(".sonarTable").css("width", "1008");
	var tableNum = 1;
	if (data.status == 2) {
		$(".sonarTable").css("display", "none");
		$(".promptInformation").css("display", "block");
		return;
	}
	$(".sonarTable").css("display", "block");
	$(".promptInformation").css("display", "none");
	for (var key in data) {
		if (key == "Bugs" || key == "Code Smells" || key == "Coverage" || key == "Duplications" || key == "Vulnerabilities") {
			echarts.init(document.getElementById('sonarTable' + tableNum)).dispose();
			echarts.init(document.getElementById('sonarTable' + tableNum)).setOption(bulidEcharts(key, data));
			tableNum++;
		}
	}
}
function showMincroEchartsContent(data) {
	if (data.list.length == 0) {
		$("#sonarHistory .leftMenu").css("display", "none");
		$(".sonarTable").css("width", "1008");
		$(".sonarTable").css("display", "none");
		$(".promptInformation").css("display", "block");
		return;
	}
	sonarData = data.list;
	$("#sonarHistory .leftMenu").css("display", "block");
	$(".sonarTable").css("width", "898");
	$(".sonarTable").css("display", "block");
	$(".promptInformation").css("display", "none");
	var tableNum = 1;
	if (data.createType == 1) {
		for (var i = 0; i < data.list.length; i++) {
			if (i == 0) {
				$(".leftMenu_ul").append("<li class='chooseItem2' onclick='changeMincroEcharts(this," + i + ")'>" + data.list[i].moduleName + "</li>")
				for (var key in data.list[i]) {
					if (key == "Bugs" || key == "Code Smells" || key == "Coverage" || key == "Duplications" || key == "Vulnerabilities") {
						echarts.init(document.getElementById('sonarTable' + tableNum)).dispose();
						echarts.init(document.getElementById('sonarTable' + tableNum)).setOption(bulidEcharts(key, data.list[i]));
						tableNum++;
					}
				}
			} else {
				$(".leftMenu_ul").append("<li onclick='changeMincroEcharts(this," + i + ")'>" + data.list[i].moduleName + "</li>")
			}
		}
	} else if (data.createType == 2) {
		for (var i = 0; i < data.list.length; i++) {
			if (i == 0) {
				$(".leftMenu_ul").append("<li class='chooseItem2' onclick='changeMincroEcharts(this," + i + ")'>" + data.list[i].jobName + "</li>")
				for (var key in data.list[i]) {
					if (key == "Bugs" || key == "Code Smells" || key == "Coverage" || key == "Duplications" || key == "Vulnerabilities") {
						echarts.init(document.getElementById('sonarTable' + tableNum)).dispose();
						echarts.init(document.getElementById('sonarTable' + tableNum)).setOption(bulidEcharts(key, data.list[i]));
						tableNum++;
					}
				}
			} else {
				$(".leftMenu_ul").append("<li onclick='changeMincroEcharts(this," + i + ")'>" + data.list[i].jobName + "</li>")
			}
		}
	}


}
function changeMincroEcharts(This, i) {
	$(".leftMenu_ul li").removeClass('chooseItem2');
	$(This).addClass('chooseItem2');
	var tableNum = 1;
	for (var key in sonarData[i]) {
		if (key == "Bugs" || key == "Code Smells" || key == "Coverage" || key == "Duplications" || key == "Vulnerabilities") {
			echarts.init(document.getElementById('sonarTable' + tableNum)).dispose();
			echarts.init(document.getElementById('sonarTable' + tableNum)).setOption(bulidEcharts(key, sonarData[i]));
			tableNum++;
		}
	}
}
function bulidEcharts(key, data) {
	var option = {
		title: {
			text: key,
			top: 6,
			left: 8,
			textStyle: {
				fontSize: 12,
				color: '#666666',
			}
		},
		tooltip: {
			trigger: 'none',
			axisPointer: {
				type: 'cross'
			}
		},
		backgroundColor: '#F4F4FB',
		xAxis: {
			type: 'category',
			boundaryGap: false,
			data: stringToArr(data.xValue),
			axisLabel: {
				show: false,
				interval: 0,
				rotate: -20
			},
			axisPointer: {
				label: {
					formatter: function (params) {
						return params.value + (params.seriesData.length ? '：' + params.seriesData[0].data : '');
					}
				}
			},
		},
		yAxis: {
			splitLine: { show: false },
			type: 'value',
		},
		grid: {
			left: '15',
			right: '15',
			bottom: '20',
			top: '40',
			borderWidth: 0,
			containLabel: true
		},
		/*dataZoom:{
            realtime:true, //拖动滚动条时是否动态的更新图表数据
            height:15,//滚动条高度
            start:0,//滚动条开始位置（共100等份）
            end:20//结束位置（共100等份）
          }, */
		series: [{
			data: stringToArr(data[key]),
			type: 'line',
			smooth: true,
			areaStyle: {
				color: ['#D2E5F8']
			},
			itemStyle: {
				normal: {
					lineStyle: {
						color: '#49A9EE'
					}
				}
			},
		}]
	};
	return option;
}

function showDetails(This) {

	var value = '';
	value = "." + $(This).attr("value");
	if ($(This).parent().parent().find(".questionList").find(value).length > 0) {
		$(This).parent().parent().find(".questionList .listTitle .fa-angle-double-up").addClass("fa-angle-double-down");
		$(This).parent().parent().find(".questionList .listTitle .fa").removeClass("fa-angle-double-up");
		$(This).parent().parent().find(".questionList .questionContent").css("display", "none");
		$(This).parent().parent().find(".questionList " + value + " .fa-angle-double-down").addClass("fa-angle-double-up");
		$(This).parent().parent().find(".questionList " + value + " .fa-angle-double-up").removeClass("fa-angle-double-down");
		$(This).parent().parent().find(".questionList " + value + " .questionContent").css("display", "block");
	} else {
		if ($(This).parent().parent().children(".di_projectKey").val() == 'null') {

			//alert("手动部署，没有配置sonar扫描!");

			layer.alert('部署，没有配置sonar扫描!', {
				icon: 2,
				title: "提示信息"
			});
		} else {
			$("#loading").css('display', 'block');
			$(This).parent().parent().find(".questionList .listTitle .fa-angle-double-up").addClass("fa-angle-double-down");
			$(This).parent().parent().find(".questionList .listTitle .fa").removeClass("fa-angle-double-up");
			$(This).parent().parent().find(".questionList .questionContent").css("display", "none");
			loadingPage(This);
		}
	}
}
function loadingPage(This) {
	var page = 1;
	$.ajax({
		url: "/devManage/structure/getSonarIssule",
		method: "post",
		data: {
			toolId: $(This).parent().parent().children(".di_toolId").val(),
			dateTime: $(This).parent().parent().children(".di_projectDateTime").val(),
			projectKey: $(This).parent().parent().children(".di_projectKey").val(),
			type: $(This).attr("value"),
			p: page,
		},
		success: function (data) {
			$(This).parent().parent().children(".questionList").children(".sonarList").children(".questionContent").children(".questionFooter").remove();
			var data = JSON.parse(data);
			var str = '<div class="sonarList ' + $(This).attr("value") + '" ><div class="listTitle">';
			if ($(This).attr("value") == 'BUG') {
				str += '<span class="fa fa-bug fontIconSize"></span><span>Bugs 问题清单</span>';
			} else if ($(This).attr("value") == 'CODE_SMELL') {
				str += '<span class="fa fa-support fontIconSize"></span><span>Code Smells 问题清单</span>';
			} else if ($(This).attr("value") == 'VULNERABILITY') {
				str += '<span class="fa fa-unlock-alt fontIconSize"></span><span>Vulnerabilities 问题清单</span>';
			}
			str += '<span class="fa fa-remove smallIcon" onclick="romoveSonarList(this)"></span> <span class="fa fa-angle-double-up smallIcon" onclick="down2(this)"></span></div><div class="questionContent"></div></div>';
			$(This).parent().parent().children(".questionList").append(str);
			var lastPath = '';
			for (var i = 0; i < data.issues.length; i++) {
				var onesDiv = '';
				if (lastPath != data.issues[i].component) {
					onesDiv += '<div class="questionPath"> ' + data.issues[i].component + ' </div>';
				}
				onesDiv += '<div class="questionBody"><div class="rowdiv"><div class="erorrCont fontWeihgt def_col_31">' + data.issues[i].message + '</div><div class="lineCont def_col_5 font_right">Line:' + data.issues[i].line + '</div></div>' +
					'<div class="rowdiv"><span>' + data.issues[i].author + '</span>&nbsp;&nbsp;&nbsp;<span>' + data.issues[i].creationDate.replace(/T/, ' ') + '</span></div></div>';

				lastPath = data.issues[i].component;
				$(This).parent().parent().children(".questionList").children(".sonarList").children(".questionContent").append(onesDiv);
			}
			if (Math.ceil(data.total / data.ps) == data.p || data.total <= data.ps) {
				var footer = '<div class="questionFooter">显示 <span>' + data.total + '</span> / <span>' + data.total + '</span></div>';
			} else {
				var footer = '<div class="questionFooter">显示 <span>' + data.p * data.ps + '</span> / <span>' + data.total + '</span> <span class="a_style" value=' + page + ' onclick="moreLoadingPage(this)">更多...</span></div>';
			}
			$(This).parent().parent().children(".questionList").children(".sonarList").children(".questionContent").append(footer);
			$("#loading").css('display', 'none');
		},
	});

}
function moreLoadingPage(This) {
	$("#loading").css('display', 'block');
	$.ajax({
		url: "/devManage/deploy/getSonarIssule",
		method: "post",
		data: {
			toolId: $(This).parent().parent(".questionContent").parent(".sonarList").parent(".questionList").parent(".oneResult").children(".di_toolId").val(),
			dateTime: $(This).parent().parent(".questionContent").parent(".sonarList").parent(".questionList").parent(".oneResult").children(".di_projectDateTime").val(),
			projectKey: $(This).parent().parent(".questionContent").parent(".sonarList").parent(".questionList").parent(".oneResult").children(".di_projectKey").val(),
			type: $(This).parent().parent(".questionContent").parent(".sonarList").attr("id"),
			p: (Number($(This).attr("value")) + 1),
		},
		success: function (data) {
			var node = $(This).parent(".questionFooter").parent(".questionContent");
			var data = JSON.parse(data);
			var lastPath = '';
			for (var i = 0; i < data.issues.length; i++) {
				var onesDiv = '';
				if (lastPath == '') {
				} else {
					if (lastPath != data.issues[i].component) {
						onesDiv += '<div class="questionPath"> ' + data.issues[i].component + ' </div>';
					}
					onesDiv += '<div class="questionBody"><div class="rowdiv"><div class="erorrCont fontWeihgt def_col_31">' + data.issues[i].message + '</div><div class="lineCont def_col_5 font_right">Line:' + data.issues[i].line + '</div></div>' +
						'<div class="rowdiv"><span>' + data.issues[i].author + '</span>&nbsp;&nbsp;&nbsp;<span>' + data.issues[i].creationDate.replace(/T/, ' ') + '</span></div></div>';

				}
				lastPath = data.issues[i].component;
				node.append(onesDiv);
			}
			$(This).parent(".questionFooter").remove();
			if (Math.ceil(data.total / data.ps) == data.p) {
				var footer = '<div class="questionFooter">显示 <span>' + data.total + '</span> / <span>' + data.total + '</span></div>';
			} else {
				var footer = '<div class="questionFooter">显示 <span>' + data.p * data.ps + '</span> / <span>' + data.total + '</span> <span class="a_style" value=' + data.p + ' onclick="moreLoadingPage(this)">更多...</span></div>';
			}
			node.append(footer);
			$("#loading").css('display', 'none');
		}
	});
}
function romoveSonarList(This) {
	$(This).parent().parent(".sonarList").remove();
}
function down2(This) {
	if ($(This).hasClass("fa-angle-double-down")) {
		$(This).removeClass("fa-angle-double-down");
		$(This).addClass("fa-angle-double-up");
		$(This).parent().parent(".sonarList").children(".questionContent").slideDown(100);
	} else {
		$(This).addClass("fa-angle-double-down");
		$(This).removeClass("fa-angle-double-up");
		$(This).parent().parent(".sonarList").children(".questionContent").slideUp(100);
	}
}
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
	$("#sonarTime").daterangepicker({ 'locale': locale });
}
function getAllJobName(This, rows) {
	var row = JSON.stringify(rows).replace(/"/g, '&quot;');
	$.ajax({
		url: "/devManage/deploy/getDeployJobName",
		method: "post",
		data: {
			systemId: rows.id,
		},
		success: function (data) {
			$(This).next(".dropdown-menu").empty();
			var str = '<div class="mySearchbox"><input type="text" class="form-control" oninput="searchBuildFun(this)" /></div>';
			for (var i = 0; i < data.list.length; i++) {
				var uid = data.list[i].rootPom; 
				str += '<li value=' + data.list[i].jobName + '><a class="a_style"   onclick="manualBuilding(\'' + uid + '\',\'' + data.list[i].jobName + '\',' + row + ')">' + isValueNull(data.list[i].environmentTypeName) + ':' + data.list[i].jobName + '</a></li>';
			}	
			$(This).next(".dropdown-menu").append(str);
		},
	});
}

function searchBuildFun(self){  
	var  str = $( self ).val().toString(); 
	if( str == "" ){
		$(self).parent().parent().children( "li" ).css( "display","list-item" );
	}else{
		for( var i = 0 ; i < $(self).parent().parent().children( "li" ).length ; i++  ){
			var nameStr = $(self).parent().parent().children( "li" ).eq( i ).attr("value").toString();
			if( nameStr.indexOf( str ) != -1 ){
				$(self).parent().parent().children( "li" ).eq( i ).css( "display","list-item" );
			}else{
				$(self).parent().parent().children( "li" ).eq( i ).css( "display","none" );
			} 
		}		
	}  
}

function manualBuilding(id, jobName, rows) {
	getDeployStatus();
	var systemId = rows.id;

	getEnvironmentTypeBySystemId(systemId);
	$("#manualReqFeatureDiv").hide();
	$("#selEnvType").empty();
	$("#selEnvType").append("<option value=''>请选择</option>")
	$("#manualBuildingTable tbody").empty();
	$.ajax({
		url: "/devManage/deploy/getJobNameParam",
		method: "post",
		data: {
			systemJenkisId: id,
			systemId: systemId
		},
		success: function (data) {
			$("#manualJobName").text(jobName);
			$("#thisManualJobName").val(data.id)
			//$("#manualBuildingDIVTitle").html( rows.systemCode+" "+rows.systemName+" <span class='fontWeihgt'>手动部署参数化</span>");
			$("#manualBuildingDIVTitle").html(rows.systemCode + " " + rows.systemName + " <span class='fontWeihgt'>部署参数化</span>");

			for (var j in environmentTypeDataInsystemId) {
				$("#selEnvType").append("<option value='" + j + "'>" + environmentTypeDataInsystemId[j] + "</option>")
			}



			systemIdManual = rows.systemId;


			//获取sprintId和windowid





			getWindowsBySystemIdManual(systemId);
			getSprintBySystemIdManual(systemId);
			// initManualReqFeatureTable(systemId);


			//手动部署开发任务
			// $("#reqFeatureTable").bootstrapTable('destroy');
			// $("#reqFeatureTable").bootstrapTable({
			// 	url:"/devManage/devtask/deplayReqFesture",
			// 	method:"post",
			//     queryParamsType:"",
			//     pagination : true,
			//     sidePagination: "server",
			//     contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			//     pageNumber : 1,
			//     pageSize : 10,
			//     pageList : [ 5, 10, 15],
			//    // singleSelect : true,//单选
			//     queryParams : function(params) {
			//          var param={
			//         	 systemId:systemId,
			//              pageNumber:params.pageNumber,
			//              pageSize:params.pageSize,
			// 		 commissioningWindowId:$("#windowMaual").val(),
			// 		 sprintId:$("#sprintMaual").val(),
			//          }
			//         return param;
			//     },
			//     columns : [{
			//         checkbox : true,
			//         width : "30px"
			//     },{
			//         field : "id",
			//         title : "id",
			//         visible : false,
			//         align : 'center'
			//     },{
			//         field : "featureCode",
			//         title : "任务编码",
			//         align : 'center'
			//     },{
			//         field : "featureName",
			//         title : "任务名称",
			//         align : 'center'
			//     },{
			//     	field : "statusName",
			//     	title : "任务状态",
			//     	align : 'center'
			//     },{
			//         field : "windowName",
			//         title : "投产窗口",
			//         align : 'center'
			//     },{
			//     	field :"systemVersionName",
			//     	title  : "系统版本",
			//     	align :"center"
			//     },{
			//     	field :"deployStatus",
			//     	title  : "部署状态",
			//     	align :"center",
			//     	formatter:function(value,row,index){
			//     		var valueName = '';
			//     		if(row.deployStatus!=undefined && row.deployStatus!=null){
			//     			var statusids = row.deployStatus.split(",");
			//     			for(var j =0;j<statusids.length;j++){
			//     				for(var i=0; i<deployStatusData.length;i++){
			// 	        			if(deployStatusData[i].valueCode ==statusids[j]){
			// 	        			valueName+=	deployStatusData[i].valueName+"，";
			// 	        			}
			// 	        		}
			//     			}
			//
			//     		}
			//
			//     		return valueName.substring(0,valueName.length-1);
			//     	}
			//     }
			//     ],
			//     onLoadSuccess:function(){
			//
			//     },
			//     onLoadError : function() {
			//
			//     }
			// });

			$("#manualBuildingDIV").modal("show");
			var dataList = $.parseJSON(data.paramJson);
			var str = "";

			for (var i = 0; i < dataList.length; i++) {
				var tdStr = "";
				tdStr += '<tr><td class="font_right">';

				if (dataList[i].name) {
					tdStr += '<span class="paramName">' + dataList[i].name + '</span></td><td>'
				} else {
					tdStr += '</td><td>'
				}
				switch (dataList[i].type) {
					case 'String':
						tdStr += '<input type="text" class="form-control" name="my' + dataList[i].type + '" value="' + dataList[i].defaultValue + '"  />';
						break;
					case 'Boolean':
						tdStr += '<select class="selectpicker" name="my' + dataList[i].type + '"><option value="">请选择</option>';
						if (dataList[i].defaultValue == true) {
							tdStr += '<option value="true" checked>true</option><option value="false">false</option></select>';
						} else {
							tdStr += '<option value="true">true</option><option value="false" selected = "selected" >false</option></select>';
						}
						break;
					case 'Choice':
						tdStr += '<select class="selectpicker" name="my' + dataList[i].type + '"><option value="">请选择</option>';
						for (var key = 0; key < dataList[i].choices.length; key++) {
							if (key == 0) {
								tdStr += '<option value=' + dataList[i].choices[key] + ' selected = "selected" >' + dataList[i].choices[key] + '</option>';
							} else {
								tdStr += '<option value=' + dataList[i].choices[key] + '>' + dataList[i].choices[key] + '</option>';
							}
						}
						tdStr += '</select>';
						break;
					case 'undefined':
						tdStr += '';
						break;
					default:
						tdStr += '';
				}
				if (dataList[i].description) {
					tdStr += '</td><td><span class="description">' + dataList[i].description + '</span></td>';
				} else {
					tdStr += '</td><td></td>';
				}

				tdStr += '</tr>';
				str += tdStr;
			}

			$("#manualBuildingTable tbody").append(str);
			$('.selectpicker').selectpicker('refresh');
		},
	});
}

function initManualReqFeatureTable(systemId) {

	$("#reqFeatureTable").bootstrapTable('destroy');
	$("#reqFeatureTable").bootstrapTable({
		url: "/devManage/devtask/deplayReqFestureByParam",
		method: "post",
		queryParamsType: "",
		pagination: false,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		responseHandler: function (res) {
			return res.rows;
		},
		// singleSelect : true,//单选
		queryParams: function (params) {
			var param = {
				systemId: systemId,
				pageNumber: params.pageNumber,
				pageSize: params.pageSize,
				commissioningWindowId: $("#windowMaual").val(),
				sprintId: $("#sprintMaual").val(),
			}
			return param;
		},
		columns: [{
			checkbox: true,
			width: "30px"
		}, {
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "featureCode",
			title: "任务编码",
			align: 'center'
		}, {
			field: "featureName",
			title: "任务名称",
			align: 'center'
		}, {
			field: "statusName",
			title: "任务状态",
			align: 'center'
		}, {
			field: "windowName",
			title: "投产窗口",
			align: 'center'
		}, {
			field: "systemVersionName",
			title: "系统版本",
			align: "center"
		}, {
			field: "deployStatus",
			title: "部署状态",
			align: "center",
			formatter: function (value, row, index) {
				var valueName = '';
				if (row.deployStatus != undefined && row.deployStatus != null) {
					var statusids = row.deployStatus.split(",");
					for (var j = 0; j < statusids.length; j++) {
						for (var i = 0; i < deployStatusData.length; i++) {
							if (deployStatusData[i].valueCode == statusids[j]) {
								valueName += deployStatusData[i].valueName + "，";
							}
						}
					}

				}

				return valueName.substring(0, valueName.length - 1);
			}
		}
		],
		onLoadSuccess: function () {

		},
		onLoadError: function () {

		}
	});




}







function startManualBuilding() {
	$("#loading").css('display', 'block');
	var obj = "";
	var id = $("#thisManualJobName").val().toString();
	$("#manualBuildingTable tbody tr").each(function (i) {
		var name = $(this).find("td:first span").text();
		var value = "";
		if ($(this).find("input[name*='myString']").size() != 0) {
			value = $(this).find("input[name*='myString']").val();
		} else if ($(this).find("select[name*='myBoolean']").size() != 0) {
			value = $(this).find("select[name*='myBoolean']").val();
		} else if ($(this).find("select[name*='myChoice']").size() != 0) {
			value = $(this).find("select[name*='myChoice']").val();
		}
		obj += '"' + name + '":"' + value + '",';
	});
	obj = obj.substr(0, obj.length - 1);
	obj = "{" + obj + "}";
	var taskEnvType = $("#selEnvType").val();
	var reqFeatureqIds = '';
	if (taskEnvType != '') {
		var selectContent = $("#reqFeatureTable").bootstrapTable('getSelections');
		for (var i = 0; i < selectContent.length; i++) {
			reqFeatureqIds += selectContent[i].id + ",";
		}
	}
	$.ajax({
		url: "/devManage/deploy/buildJobManualDeploy",
		method: "post",
		data: {
			taskEnvType: taskEnvType,
			reqFeatureqIds: reqFeatureqIds,
			systemJenkisId: id,
			jsonParam: obj,
		},
		success: function (data) {
			pageInit();
			$("#manualBuildingDIV").modal("hide");
			$("#loading").css('display', 'none');
			if (data.status == "2") {
				layer.alert(data.message, {
					icon: 2,
					title: "提示信息"
				});
			} else {

				//		  		    //新增日志处理
				detaillog(data);
			}
		},
	});
	//	}else{
	//
	//		layer.alert("数据出错！", {
	//            icon: 2,
	//            title: "提示信息"
	//        });
	//		$("#manualBuildingDIV").modal("hide");
	//	}

}

function getEnvironmentType() {
	$.ajax({
		url: "/devManage/structure/getEnv",
		method: "post",
		contentType: "application/json;charset=utf-8",
		async: false,
		success: function (data) {
			//console.log( data );
			environmentTypeData = data;
		},
	});
}



function getEnvironmentTypeBySystemId(systemId) {

	$.ajax({
		url: "/devManage/structure/getEnvBySystemId",
		method: "post",
		data: {
			systemId: systemId

		},

		success: function (data) {
			//console.log( data );
			environmentTypeDataInsystemId = data;
		},
	});
}

function getDeployStatus() {
	$.ajax({
		url: "/devManage/devtask/getDeployStatus",
		method: "post",
		dataType: "json",
		success: function (data) {
			//console.log(data.deployStatus);
			deployStatusData = data.deployStatus;
		},
	})
}

function refresh() {
	var ids = "";
	var obj = $("#list2").jqGrid("getRowData");
	for (i = 0; i < obj.length; i++) {
		var name = obj[i].lastBuildTime;
		var architectureType = obj[i].architectureType;
		if (architectureType == "1") {
			if (name.indexOf("正在部署") > -1) {
				ids = ids + obj[i].id + ",";
			}
		}
	}
	if (ids != "") {
		ids = ids.substr(0, ids.length - 1);
	}

}
function scheduledTask(row) {
	var flag = row.createType;
	var systemId = row.id;
	if (flag == "2") {
		//手动构建
		$("#list4").bootstrapTable('destroy');
		$("#list4").bootstrapTable({
			url: '/devManage/deploy/getCorn',
			method: "post",
			queryParamsType: "",
			pagination: true,
			sidePagination: "server",
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			queryParams: function (params) {
				var param = {
					"systemId": row.id,
					"type": row.createType,
				};
				return param;
			},
			columns: [{
				field: "stringId",
				title: "stringId",
				visible: false,
				align: 'center'
			}, {
				field: "jobName",
				title: "手动任务名称",
				align: 'center',
			}, {
				field: "jobCron",
				title: "定时表达式",
				align: 'center',
				formatter: function (value, row, index) {
					var rows = JSON.stringify(row).replace(/"/g, '&quot;');
					if (row.jobCron == 'null' || row.jobCron == null) {
						return '<div class="def_tableDiv2"><input class="jobCronClass form-control def_tableInput " type="text"  value=""  /></div>';
					} else {
						return '<div class="def_tableDiv2"><input class="jobCronClass form-control def_tableInput" type="text" value="' + row.jobCron + '" /></div>';
					}
				}
			}, {
				field: "操作",
				title: "操作",
				align: 'center',
				formatter: function (value, row, index) {
					var rows = JSON.stringify(row).replace(/"/g, '&quot;');
					return '<a class="a_style" style="cursor:pointer" onclick="setCron(' + rows + ',' + flag + ',' + systemId + ',this)">生效</a> '
				}
			}],
			onLoadSuccess: function () {
				$("#loading").css('display', 'none');
				$("#scheduledModal").modal('hide');
				$("#scheduledManualModal").modal('show');
			},
			onLoadError: function () {
			}
		});
	} else {
		var systemIds = row.id;
		$("#list3").bootstrapTable('destroy');
		$("#list3").bootstrapTable({
			url: '/devManage/deploy/getCorn',
			method: "post",
			queryParamsType: "",
			pagination: true,
			sidePagination: "server",
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			pageNumber: 1,
			pageSize: 10,
			pageList: [5, 10, 15],
			queryParams: function (params) {
				var param = {
					"systemId": row.id,
					"type": row.createType,
				};
				return param;
			},
			columns: [{
				field: 'stringId',
				class: 'stringId hideBlock',
				title: 'stringId',
				align: 'center'
			}, {
				field: 'environmentType',
				title: 'environmentType',
				visible: false,
				align: 'center'
			}, {
				field: 'environmentTypeName',
				title: '环境名称',
				align: 'center',
				formatter: function (value, row, index) {
					var str = '';
					str += '<span class="fontContent">' + row.environmentTypeName + '</span>' +
						'<div class="def_tableDiv">' +
						'<select class="selectpicker" name="envSelect">' +
						'<option value="">请选择</option>';
					for (var k in environmentTypeData) {
						str += '<option value=' + k + '>' + environmentTypeData[k] + '</option>';
					}
					str += '</select></div>';
					return str;
				}
			}, {
				field: 'jobCron',
				title: '定时表达式',
				align: 'center',
				formatter: function (value, row, index) {
					var rows = JSON.stringify(row).replace(/"/g, '&quot;');
					if (row.jobCron == 'null' || row.jobCron == null) {
						return '<div class="def_tableDiv2"><input class="jobCronClass form-control def_tableInput " type="text"  value=""  /></div>';
					} else {
						return '<div class="def_tableDiv2"><input class="jobCronClass form-control def_tableInput" type="text" value="' + row.jobCron + '" /></div>';
					}
				}
			}, {
				field: "操作",
				title: "操作",
				align: 'center',
				formatter: function (value, rows, index) {
					var row = JSON.stringify(rows).replace(/"/g, '&quot;');
					return '<a class="a_style" style="cursor:pointer" onclick="setCron(' + row + ',' + flag + ',' + systemId + ',this)">生效</a> ' +
						'<a class="a_style cancelBtnDel" style="cursor:pointer" onclick="deleteCron(' + row + ',' + flag + ',' + systemId + ',this)">删除</a> ' +
					 // ' <a class="a_style" style="cursor:pointer" onclick="deleteCron(' + rows + ',' + flag + ',' + systemId + ',this)">删除</a>' +
						'  <a class="a_style cancelBtn" style="cursor:pointer" onclick="cancel( this)">取消</a> ';
				}
			}],
			onLoadSuccess: function () {
				$("#loading").css('display', 'none');
				$("#scheduledManualModal").modal('hide');
				$("#scheduledModal").modal('show');
			},
			onLoadError: function () {
			}
		});
	}
}
function setCron(row, type, systemId, This) {
	$("#loading").css('display', 'block');
	var url = ""
	var data = "";
	var jobCron = $(This).parent().parent().find('.jobCronClass').val();
	if (row.stringId == "add_Id") {

		if(jobCron==""){
			layer.alert('请填写定时表达式', {
				icon: 2,
				title: "提示信息"
			});
			$("#loading").css('display', 'none');
			return false;
		}

		var envName = $(This).parent().parent().find("select[name='envSelect'] option:selected").text();
		var env = $(This).parent().parent().find("select[name='envSelect'] option:selected").val();
		url = "/devManage/deploy/insertCorn";
		data = {
			"environmentType": env,
			"environmentTypeName": envName,
			"jobCron": jobCron,
			"systemId": systemId

		};
	} else {
		url = "/devManage/deploy/setCornOne";
		data = {
			"type": type,
			"jobCron": jobCron,
			"systemJenkinsId": row.stringId
		};
	}
	$.ajax({
		url: url,
		method: "post",
		data: data,
		success: function (data) {
			$("#loading").css('display', 'none');
			if (type == "1") {
				$("#list3").bootstrapTable('refresh', { silent: true });
			} else {
				$("#list4").bootstrapTable('refresh', { silent: true });
			}
			$("#loading").css('display', 'none');
			if (data.status == 2) {

				layer.alert(data.message, {
					icon: 2,
					title: "提示信息"
				});
			} else {
				layer.alert('成功', {
					icon: 1,
					title: "提示信息"
				});
			}
		}
	});
}





function deleteCron(row, type, systemId, This) {
	$("#loading").css('display', 'block');
	var url = ""
	var data = "";
	url = "/devManage/deploy/setCornOne";
		data = {
			"type": type,
			"jobCron": "",
			"systemJenkinsId": row.stringId
		};

	$.ajax({
		url: url,
		method: "post",
		data: data,
		success: function (data) {
			$("#loading").css('display', 'none');
			if (type == "1") {
				$("#list3").bootstrapTable('refresh', { silent: true });
			} else {
				$("#list4").bootstrapTable('refresh', { silent: true });
			}
			$("#loading").css('display', 'none');
			if (data.status == 2) {

				layer.alert(data.message, {
					icon: 2,
					title: "提示信息"
				});
			} else {
				layer.alert('删除成功', {
					icon: 1,
					title: "提示信息"
				});
			}
		}
	});
}

function insertVersion() {
	for (var i = 0; i < $(".stringId").length; i++) {
		if ($(".stringId").eq(i).text() == "add_Id") {
			layer.alert('表格中以存在一条新增定时任务，请确定是否生效！', {
				icon: 0,
				title: "提示信息"
			});
			return;
		}
	}
	var datas = $("#list3").bootstrapTable('getData');
	dataRow = {
		"stringId": 'add_Id',
		"environmentType": "",
		"environmentTypeName": "",
		"jobCron": ''
	};
	$("#list3").bootstrapTable('prepend', dataRow);
	$('.selectpicker').selectpicker('refresh');

	for (var i = 0; i < $(".stringId").length; i++) {
		if ($(".stringId").eq(i).text() == "add_Id") {
			$(".stringId").eq(i).parent().find(".cancelBtn").css("display", "inline-block");
			$(".stringId").eq(i).parent().find(".cancelBtnDel").css("display", "none");
			$(".stringId").eq(i).parent().find(".def_tableDiv").css("display", "block");
		}
	}
}

function cancel(This) {
	$("#list3").bootstrapTable('refresh', { silent: true });
}


function getBreakName(row) {
	var flag = row.createType;
	var systemId = row.id;
	var jobType = 2;
	var deployType = row.deployType;
	if (deployType == 2) {
		jobType = 2;//制品部署
	}


	//手动部署
	$("#list5").bootstrapTable('destroy');
	$("#list5").bootstrapTable({
		url: '/devManage/structure/getBreakName',
		method: "post",
		queryParamsType: "",
		pagination: true,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		queryParams: function (params) {
			var param = {
				"systemId": systemId,
				"createType": flag,
				"jobType": jobType //自动构建部署
			};
			return param;
		},
		columns: [{
			field: "toolId",
			title: "toolId",
			visible: false,
			align: 'center'
		}, {
			field: "jobName",
			title: "任务名称",
			align: 'center',
		}, {
			field: "envName",
			title: "构建环境",
			align: 'center',
		}, {
			field: "操作",
			title: "操作",
			align: 'center',
			formatter: function (value, row, index) {
				var rows = JSON.stringify(row).replace(/"/g, '&quot;');
				return '<a class="a_style" style="cursor:pointer" onclick="breakDeployings(' + rows + ')">强制结束</a>  '
			}
		}],
		onLoadSuccess: function () {
			$("#loading").css('display', 'none');
			$("#breakModal").modal('show');
		},
		onLoadError: function () {
		}
	});

}


function breakDeployings(row) {
	$("#loading").css('display', 'block');
	var url = ""
	var data = "";
	url = "/devManage/structure/breakJob";
	data = {
		"toolId": row.toolId,
		"jobName": row.jobName,
		"jenkinsId": row.jenkinsId,
		"createType": row.createType

	};

	$.ajax({
		url: url,
		method: "post",
		data: data,
		success: function (data) {

			$("#list5").bootstrapTable('refresh', { silent: true });

			$("#loading").css('display', 'none');
			if (data.status == 2) {
				layer.alert(data.message, {
					icon: 2,
					title: "提示信息"
				});
			} else {
				autoSearchInfo();
				layer.alert('已打断成功，请稍后！', {
					icon: 1,
					title: "提示信息"
				});
			}
		}
	});
}

function pause(row) { 
	timer = setInterval(function () {
		getLogloop(row);
	}, 4000);
}
function getLog(row) {   
	
	$("#logId").empty();
	$("#logId").text("正在初始化请等待.....");  
	$("#loadingData").css( "display","block" );
	$("#JenkinsJobsTable").css( "display","none" ); 
	$('#scrollTop a[href="#StageView"]').tab('show');
	
	$("#logModal").modal('show');
	line = 0;
	setTimeout(function () {
		pause(row);
	}, 1000);

}
var lastbuilding = "";
function getLogloop(row) { 
	if( $("#scrollTop>.nav-tabs>.active>a").attr( "aria-controls" ) == "profile" ){
		createNotes( row );
	}else if( $("#scrollTop>.nav-tabs>.active>a").attr( "aria-controls" ) == "StageView" ){
		createStageView( row );
	}  
} 

var num = "";
function appendTo() {
	var a = $("#logId");

	var h = "";
	if (line == 0) {
		a.empty();
		h = '<p>' + notes + '</p>';
	} else {
		h = h = '<p>' + num + '</p>';
	}

	a.append(h);
	var b = $("#scrollTop");
	b.scrollTop(a[0].scrollHeight);

}






function appendToTest(notes) {
	var a = $("#testlogId");
	a.empty();
	var h = "";

	h = '<p>' + notes + '</p>';


	a.append(h);
	var b = $("#testscrollTop");
	b.scrollTop(a[0].scrollHeight);

}



function detaillog(data) {
	var row = {};
	row.toolId = data.toolId;
	row.jobName = data.jobName;
	row.jenkinsId = data.jenkinsId;
	closelog();
	getLog(row);
}
function closelog() {
	end = "";
	notes = "";
	line = 0;
	num = "";
	lastbuilding = "";
	clearInterval(timer);
	lastbuilding = "";
	count = 0;
}



function withinUserShow() {
	$("#loading").css('display', 'block');
	$("#withinUserTable").bootstrapTable('destroy');
	//	     var obj = {};
	//		 obj.userName = $.trim($("#withinUserName").val());
	//		 obj.companyId = $.trim($("#withinCompanyName").find("option:selected").val());
	//		 obj.deptId =$.trim($("#withinDeptName").find("option:selected").val());
	//		 obj = JSON.stringify(obj);


	$("#withinUserTable").bootstrapTable({
		url: "/system/user/getAllUserModal2",
		method: "post",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		queryParamsType: "",
		pagination: true,
		sidePagination: "server",
		pageNumber: 1,
		pageSize: 10,
		pageList: [10, 25, 50, 100],
		singleSelect: true,//单选
		queryParams: function (params) {
			var param = {
				pageNumber: params.pageNumber,
				pageSize: params.pageSize,
				userName: $.trim($("#withinUserName").val()),
				//	                deptId:$.trim($("#withinDeptName").find("option:selected").val()),
				deptName: $.trim($("#withinDeptName").val()),
				//	                companyId:$.trim($("#withinCompanyName").find("option:selected").val())
				companyName: $.trim($("#withinCompanyName").val())

			};
			//
			//	            var param = {
			//	            		rows:params.pageSize,
			//	            		page:params.pageNumber,
			//	            		userInfo:obj
			//
			//		            };
			return param;
		},
		columns: [{
			checkbox: true,
			width: "30px"
		}, {
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "userName",
			title: "姓名",
			align: 'center'
		}, {
			field: "companyName",
			title: "所属公司",
			align: 'center'
		}, {
			field: "deptName",
			title: "所属部门",
			align: 'center'
		}],
		onLoadSuccess: function () {
			$("#loading").css('display', 'none');
		},
		onLoadError: function () {

		}
	});

}

function devManPopup() {
	$("#withinUserModal").modal("show");
	//getWithinDept();
	//	getwithinCompany();
	cleanUser();
	withinUserShow();
}

function getwithinCompany() {
	$("#withinCompanyName").empty();
	$("#withinCompanyName").append("<option value=''>请选择</option>");
	$.ajax({
		type: "post",
		url: "/system/user/getCompany",
		dataType: "json",
		success: function (data) {
			for (var i = 0; i < data.length; i++) {
				var id = data[i].id;
				var name = data[i].companyName;
				var opt = "<option value='" + id + "'>" + name + "</option>";
				$("#withinCompanyName").append(opt);
			}
			$('.selectpicker').selectpicker('refresh');
		}
	});
}
function getWithinDept() {
	$("#withinDeptName").empty();
	$("#withinDeptName").append("<option value=''>请选择</option>");
	$.ajax({
		type: "post",
		url: "/system/user/getDept",
		dataType: "json",
		success: function (data) {
			for (var i = 0; i < data.length; i++) {
				var id = data[i].id;
				var name = data[i].deptName;
				var opt = "<option value='" + id + "'>" + name + "</option>";
				$("#withinDeptName").append(opt);
			}
			$('.selectpicker').selectpicker('refresh');
		}
	});
}
//清除人员搜索信息
function cleanUser() {
	$("#withinUserName").val("");
	$("#withinDeptName").val("");
	$("#withinCompanyName").val("");
	//	 $("#withinCompanyName").selectpicker('val', '');

}

function addUserID() {
	//	var type=$("#userPopupType").val();
	var rowData = $("#withinUserTable").bootstrapTable('getSelections')[0];
	if (typeof (rowData) == 'undefined') {
		layer.alert("请选择一条数据", {
			icon: 2,
			title: "提示信息"
		})

	} else {
		var name = rowData.userName;
		$("#new_taskUser").val(name);
		$("#new_taskUserId").val(rowData.id);

		$("#new_taskUserArtifact").val(name);
		$("#new_taskUserIdArtifact").val(rowData.id);

		$("#withinUserModal").modal("hide");

	}
}
function initDept() {
	$("#deptName").empty();
	$("#deptName").append("<option value=''>请选择</option>");

	$("#deptNameArtifact").empty();
	$("#deptNameArtifact").append("<option value=''>请选择</option>");
	$.ajax({
		type: "post",
		url: "/system/user/getDept",
		dataType: "json",
		success: function (data) {
			for (var i = 0; i < data.length; i++) {
				var id = data[i].id;
				var name = data[i].deptName;
				var opt = "<option value='" + id + "'>" + name + "</option>";
				$("#deptName").append(opt);
				$("#deptNameArtifact").append(opt);
			}
			$('.selectpicker').selectpicker('refresh');
		}
	});
}


function initWindow(systemId) {
	$("#commissWindow").empty();
	//$("#commissWindow").append("<option value=''>请选择</option>");
	$.ajax({
		type: "post",
		// url: "/devManage/devtask/getSearchWindow",
		url: "/devManage/deploy/getWindowsLimit",
		dataType: "json",
		data: {
			systemId: systemId
		},
		success: function (data) {

			for (var i = 0; i < data.windows.length; i++) {
				var windowId = data.windows[i].id;
				var windowName = data.windows[i].windowName;
				if (data.windows[i].featureStatus == "defaultSelect") {
					opt = "<option selected = 'selected' value='" + windowId + "'>" + windowName + "</option>";


					setTimeout(function () {
						changeTask();
					}, 1000);
				} else {
					opt = "<option value='" + windowId + "'>" + windowName + "</option>";
				}
				$("#commissWindow").append(opt);
			}

			$('.selectpicker').selectpicker('refresh');
		}
	});
}






// function initprdReqArtifact(artifactids){
//
// 	$("#reqFeatureTable4").bootstrapTable('destroy');
//     $("#reqFeatureTable4").bootstrapTable({
//     	url:"/devManage/deploy/getReqFeaByartId",
//     	method:"post",
//         queryParamsType:"",
//         pagination : true,
//         sidePagination: "server",
//         contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
//         pageNumber : 1,
//         pageSize : 10,
//         pageList : [ 5, 10, 15],
//        // singleSelect : true,//单选
//         queryParams : function(params) {
//              var param={
// 				 artifactids:artifactids,
//
//              }
//             return param;
//         },
//         columns : [{
//             field : "id",
//             title : "id",
//             visible : false,
//             align : 'center'
//         },{
//             field : "featureCode",
//             title : "任务编码",
//             align : 'center'
//         },{
//             field : "featureName",
//             title : "任务名称",
//             align : 'center'
//         },{
//         	field : "statusName",
//         	title : "任务状态",
//         	align : 'center'
//         },{
//             field : "windowName",
//             title : "投产窗口",
//             align : 'center'
//         },{
// 	        	field :"systemVersionName",
// 	        	title  : "系统版本",
// 	        	align :"center"
// 	        },{
// 	        	field :"",
// 	        	title  : "部署状态",
// 	        	align :"center",
// 	        	formatter:function(value,row,index){
// 	        		var valueName = '';
// 	        		if(row.deployStatus!=undefined && row.deployStatus!=null){
// 	        			var statusids = row.deployStatus.split(",");
// 	        			for(var j =0;j<statusids.length;j++){
// 	        				for(var i=0; i<deployStatusData.length;i++){
//   	  		        			if(deployStatusData[i].valueCode ==statusids[j]){
//   	  		        			valueName+=	deployStatusData[i].valueName+"，";
//   	  		        			}
//   	  		        		}
// 	        			}
//
// 	        		}
//
// 	        		return valueName.substring(0,valueName.length-1);
// 	        	}
//
// 	        }],
//         onLoadSuccess:function(){
//
//         },
//         onLoadError : function() {
//
//         }
//     });
//
// 	//$("#plantformDiv").modal("show");
//
// }
//



function initprdReqArtifact(artifactids) {


	$("#reqFeatureTable4").bootstrapTable('destroy');
	$("#reqFeatureTable4").bootstrapTable({
		url: "/devManage/devtask/deplayPrdReqFestureNoPage",
		method: "post",
		queryParamsType: "",
		pagination: false,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		responseHandler: function (res) {
			return res.rows;
		},
		// singleSelect : true,//单选
		queryParams: function (params) {
			var param = {
				artifactids: artifactids,

			}
			return param;
		},
		columns: [{
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "featureCode",
			title: "任务编码",
			align: 'center'
		}, {
			field: "featureName",
			title: "任务名称",
			align: 'center'
		}, {
			field: "statusName",
			title: "任务状态",
			align: 'center'
		}, {
			field: "windowName",
			title: "投产窗口",
			align: 'center'
		}, {
			field: "systemVersionName",
			title: "系统版本",
			align: "center"
		}, {
			field: "",
			title: "部署状态",
			align: "center",
			formatter: function (value, row, index) {
				var valueName = '';
				if (row.deployStatus != undefined && row.deployStatus != null) {
					var statusids = row.deployStatus.split(",");
					for (var j = 0; j < statusids.length; j++) {
						for (var i = 0; i < deployStatusData.length; i++) {
							if (deployStatusData[i].valueCode == statusids[j]) {
								valueName += deployStatusData[i].valueName + "，";
							}
						}
					}

				}

				return valueName.substring(0, valueName.length - 1);
			}

		}],
		onLoadSuccess: function () {

		},
		onLoadError: function () {

		}
	});

	//$("#plantformDiv").modal("show");

}







function initprdReqArtifactTest(artifactids) {

	$("#artReqFeatureTable").bootstrapTable('destroy');
	$("#artReqFeatureTable").bootstrapTable({
		url: "/devManage/deploy/getReqFeaByartId",
		method: "post",
		queryParamsType: "",
		pagination: false,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		responseHandler: function (res) {
			return res.rows;
		},
		// singleSelect : true,//单选
		queryParams: function (params) {
			var param = {
				artifactids: artifactids,

			}
			return param;
		},
		columns: [{
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "featureCode",
			title: "任务编码",
			align: 'center'
		}, {
			field: "featureName",
			title: "任务名称",
			align: 'center'
		}, {
			field: "statusName",
			title: "任务状态",
			align: 'center'
		}, {
			field: "windowName",
			title: "投产窗口",
			align: 'center'
		}, {
			field: "systemVersionName",
			title: "系统版本",
			align: "center"
		}, {
			field: "",
			title: "部署状态",
			align: "center",
			formatter: function (value, row, index) {
				var valueName = '';
				if (row.deployStatus != undefined && row.deployStatus != null) {
					var statusids = row.deployStatus.split(",");
					for (var j = 0; j < statusids.length; j++) {
						for (var i = 0; i < deployStatusData.length; i++) {
							if (deployStatusData[i].valueCode == statusids[j]) {
								valueName += deployStatusData[i].valueName + "，";
							}
						}
					}

				}

				return valueName.substring(0, valueName.length - 1);
			}

		}],
		onLoadSuccess: function () {

		},
		onLoadError: function () {

		}
	});

	//$("#plantformDiv").modal("show");

}






//文件上传，并列表展示
function uploadFileList() {
	//列表展示
	$("#sqlFileArtifact").change(function () {
		var files = this.files;
		var obj = this;
		createFileList("sqlFileArtifact", files, sql_files, obj);
	});
	$("#configFileArtifact").change(function () {
		var files = this.files;
		var obj = this;
		createFileList("configFileArtifact", files, config_files, obj);

	});
	$("#operFileArtifact").change(function () {
		var files = this.files;
		var obj = this;
		createFileList("operFileArtifact", files, oper_files, obj);

	});


	$("#sqlFile").change(function () {
		var files = this.files;
		var obj = this;
		createFileList("sqlFile", files, sql_files_flag, obj);
	});

	$("#configFile").change(function () {
		var files = this.files;
		var obj = this;
		createFileList("configFile", files, config_files_flag, obj);

	});
	$("#operFile").change(function () {
		var files = this.files;
		var obj = this;
		createFileList("operFile", files, oper_files_flag, obj);

	});

}


////文件上传，并列表展示
//function uploadConfigFileList(){
//
//	$("#configFileArtifact").change(function(){
//		var files = this.files;
//		var obj=this;
//		createFileList("configFileArtifact",files,config_files,obj);
//
//	});
//
//
//
//}
////文件上传，并列表展示
//function uploadOperFileList(){
//
//	$("#operFileArtifact").change(function(){
//		var files = this.files;
//		var obj=this;
//		createFileList("operFileArtifact",files,oper_files,obj);
//
//	});
//
//}

function clearUploadFile(idName) {
	/*$(idName).wrap('<form></form>');
	$(idName).unwrap();*/
	$('#' + idName + '').val('');
}

//移除上传文件
function delFile(that, fileId, fileName) {

	var _file;
	if (fileId == "configFileArtifact") {
		_file = config_files;
	} else if (fileId == "sqlFileArtifact") {
		_file = sql_files;
	} else if (fileId == "operFileArtifact") {
		_file = oper_files;
	} else if (fileId == "configFile") {
		_file = config_files_flag;
	} else if (fileId == "sqlFile") {
		_file = sql_files_flag;
	} else if (fileId == "operFile") {
		_file = oper_files_flag;
	}
	//var fileS3Key = $(that).parent().prev().children().children(".file-s3Key").text();

	$(that).parent().parent().remove();
	if (_file != "") {
		var files = _file;
		for (var i = 0, len = files.length; i < len; i++) {
			if (files[i].name == fileName) {
				Array.prototype.splice.call(files, i, 1);
				break;
			}
		}
		_file = files;

	}



}

function createFileList(fileId, files, _files, obj) {
	var id = fileId;
	var len = files.length;

	outer: for (var i = 0; i < len; i++) {
		var file = files[i];

		if (file.size <= 0) {
			layer.alert(file.name + "文件为空！", { icon: 0 });
			continue;
		}
		var fileList = [];
		fileList = _files;

		for (var j = 0; j < fileList.length; j++) {
			if (fileList[j].name == file.name) {
				layer.alert(file.name + " 文件已存在", {
					icon: 2,
					title: "提示信息"
				});
				continue outer;
			}
		}
		//列表展示
		var _tr = '';
		var file_name = file.name.split("\.")[0];
		var file_type = file.name.split("\.")[1];
		var _td_icon;
		var _td_name = '<span >' + file.name + '</span><i class="file-url"></i><i class = "file-bucket"></i><i class = "file-s3Key"></i></div></td>';
		var _td_opt = '<td><a href="javascript:void(0);" class="del-file-button"  onclick="delFile(this,\'' + fileId + '\',\'' + file.name + '\')">删除</a></td>';
		switch (file_type) {
			case "doc":
			case "docx": _td_icon = '<img src="' + _icon_word + '" />'; break;
			case "xls":
			case "xlsx": _td_icon = '<img src="' + _icon_excel + '" />'; break;
			case "txt": _td_icon = '<img src="' + _icon_text + '" />'; break;
			case "pdf": _td_icon = '<img src="' + _icon_pdf + '" />'; break;
			default: _td_icon = '<img src="' + _icon_word + '" />'; break;
		}
		var _table = $(obj).parent(".file-upload-select").next(".file-upload-list").children("table");
		_tr += '<tr><td><div class="fileTb">' + _td_icon + '  ' + _td_name + _td_opt + '</tr>';
		_table.append(_tr);
		_files.push(file);
	}
	clearUploadFile(fileId);

}




function devManPopupDept() {
	$("#withinDeptModal").modal("show");

	//getwithinCompany();
	cleanDept();
	withinDeptShow();
}

//清除人员搜索信息
function cleanDept() {

	$("#withinDeptNameNew").val("");
	$("#withinDeptNumber").val("");


}





function withinDeptShow() {



	$("#loading").css('display', 'block');
	$("#withinDeptTable").bootstrapTable('destroy');

	$("#withinDeptTable").bootstrapTable({
		url: "/system/user/getAllDept",
		method: "post",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		queryParamsType: "",
		pagination: true,
		sidePagination: "server",
		pageNumber: 1,
		pageSize: 10,
		pageList: [10, 25, 50, 100],
		singleSelect: true,//单选
		queryParams: function (params) {
			var param = {
				pageNumber: params.pageNumber,
				pageSize: params.pageSize,
				deptName: $.trim($("#withinDeptNameNew").val()),
				deptNumber: $.trim($("#withinDeptNumber").val()),
			};

			return param;
		},
		columns: [{
			checkbox: true,
			width: "30px"
		}, {
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "deptName",
			title: "部门名称",
			align: 'center'
		}, {
			field: "deptNumber",
			title: "部门编号",
			align: 'center'
		}],
		onLoadSuccess: function () {
			$("#loading").css('display', 'none');
		},
		onLoadError: function () {

		}
	});

}


function addDeptID() {
	//	var type=$("#userPopupType").val();
	var rowData = $("#withinDeptTable").bootstrapTable('getSelections')[0];
	if (typeof (rowData) == 'undefined') {
		layer.alert("请选择一条数据", {
			icon: 2,
			title: "提示信息"
		})

	} else {
		var name = rowData.deptName;
		$("#new_taskDept").val(name);
		$("#new_taskDeptId").val(rowData.id);

		$("#new_taskDeptArtifact").val(name);
		$("#new_taskDeptIdArtifact").val(rowData.id);

		$("#withinDeptModal").modal("hide");

	}
}





function getLogNew(deployType, systemId, createType, envName, ManualjobName) {
	closelog();
	var flag;
	if (deployType == 2) {
		flag = 0;//制品部署
	} else {
		flag = 1;
	}
	$.ajax({
		url: '/devManage/structure/getLogParam',
		method: "post",
		data: {
			"systemId": systemId,
			"createType": createType,
			"jobType": 2,
			"envName": envName,
			"ManualjobName": ManualjobName,
			"flag": flag
		},
		success: function (data) {
			var row = {};
			row.toolId = data.toolId;
			row.jobName = data.jobName;
			row.jenkinsId = data.jenkinsId;
			if (row.toolId == "" || row.jenkinsId == "") {
				layer.alert("无法查看实时日志", {
					icon: 2,
					title: "提示信息"
				});
			} else { 
				$("#logId").empty();
				$("#logId").text("正在初始化请等待.....");  
				$("#loadingData").css( "display","block" );
				$("#JenkinsJobsTable").css( "display","none" ); 
				$('#scrollTop a[href="#StageView"]').tab('show');
				
				$("#logModal").modal('show');
				
				setTimeout(function () {
					pause(row);
				}, 1000);

			}
		}

	});
}



function getTestLog(deployType, systemId, createType, envName, ManualjobName) {

	var flag;
	if (deployType == 2) {
		flag = 0;//制品部署
	} else {
		flag = 1;
	}
	$.ajax({
		url: '/devManage/structure/getTestLog',
		method: "post",
		data: {
			"systemId": systemId,
			"createType": createType,
			"jobType": 2,
			"envName": envName,
			"ManualjobName": ManualjobName,
			"flag": flag
		},
		success: function (data) {

			appendToTest(data.log);
			$("#testlogModal").modal('show');

		}


	});
}



function searchTask() {

	var windowId = $("#windowScm").val();
	var sprintId = $("#sprintScm").val();
	getreqFeatureTable2(systemIdScm, windowId, sprintId);

}

function searchTaskMaual() {

	initManualReqFeatureTable(systemIdManual);


}



function getWindows() {

	var doms = $("[name=bootstrapTable]");
	var idStr = "";
	if (doms != null && doms != undefined) {
		$.each(doms, function (index, dom) {
			var name = $(dom).attr("id");
			var selectContent = $("#" + name + "Table").bootstrapTable('getSelections')[0];
			if (selectContent != undefined && !isNaN(selectContent.id)) {
				idStr += selectContent.id + ",";
			}
		})
	}
	if (idStr == "") {
		//layer.alert("请选择一行记录",{icon:0});
		$("#commissWindowArtifact").empty();
		$("#commissWindowArtifact").selectpicker('refresh');

		$("#reqFeatureTable4").bootstrapTable('destroy');
		return false;
	}
	var artifactids = idStr.substr(0, idStr.length - 1);
	$("#commissWindowArtifact").empty();
	$("#commissWindowArtifact").selectpicker('refresh');
	$("#commissWindow").append("<option value=''>请选择</option>");
	$.ajax({
		type: "post",
		url: "/devManage/deploy/getWindowsByartId",
		dataType: "json",
		data: {
			artifactids: artifactids
		},
		success: function (data) {
			if (data.windows.length == 0) {
				$("#commissWindowArtifact").empty();
				$("#commissWindowArtifact").selectpicker('refresh');

				$("#reqFeatureTable4").bootstrapTable('destroy');

			}

			for (var i = 0; i < data.windows.length; i++) {
				var windowId = data.windows[i].id;
				var windowName = data.windows[i].windowName;
				//var windowVersion = data.windows[i].windowVersion;
				var windowDate = data.windows[i].windowDate;
				var opt = "";
				if (i == (data.windows.length - 1)) {
					opt = "<option selected = 'selected'  value='" + windowId + "'>" + windowName + "</option>";
					setTimeout(function () {
						changeTaskArtifact();
					}, 1000);

				} else {
					opt = "<option value='" + windowId + "'>" + windowName + "</option>";
				}
				$("#commissWindowArtifact").append(opt);
			}
			$('.selectpicker').selectpicker('refresh');

		}
	});
}
function initprdReqArtifact(windowId, systemId) {

	$("#reqFeatureTable4").bootstrapTable('destroy');
	$("#reqFeatureTable4").bootstrapTable({
		url: "/devManage/devtask/deplayPrdReqFestureNoPage",
		method: "post",
		queryParamsType: "",
		pagination: false,
		sidePagination: "server",
		contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
		responseHandler: function (res) {
			return res.rows;
		},
		// singleSelect : true,//单选
		queryParams: function (params) {
			var param = {
				systemId: systemId,
				pageNumber: params.pageNumber,
				pageSize: params.pageSize,
				commissioningWindowId: windowId,
				windowId: windowId
			}
			return param;
		},
		columns: [{
			field: "id",
			title: "id",
			visible: false,
			align: 'center'
		}, {
			field: "featureCode",
			title: "任务编码",
			align: 'center'
		}, {
			field: "featureName",
			title: "任务名称",
			align: 'center'
		}, {
			field: "statusName",
			title: "任务状态",
			align: 'center'
		}, {
			field: "windowName",
			title: "投产窗口",
			align: 'center'
		}, {
			field: "systemVersionName",
			title: "系统版本",
			align: "center"
		}, {
			field: "",
			title: "部署状态",
			align: "center",
			formatter: function (value, row, index) {
				var valueName = '';
				if (row.deployStatus != undefined && row.deployStatus != null) {
					var statusids = row.deployStatus.split(",");
					for (var j = 0; j < statusids.length; j++) {
						for (var i = 0; i < deployStatusData.length; i++) {
							if (deployStatusData[i].valueCode == statusids[j]) {
								valueName += deployStatusData[i].valueName + "，";
							}
						}
					}

				}

				return valueName.substring(0, valueName.length - 1);
			}

		}],
		onLoadSuccess: function () {

		},
		onLoadError: function () {

		}
	});

	//$("#plantformDiv").modal("show");

}
 function goContinueLog(url,toolId){ 
    $.ajax({
    	type: "post",
    	url: "/devManage/deploy/goContinueLog",
    	dataType: "json",
    	data: {
    		url: url,
    		toolId:toolId
    	},
    	success: function (data) { 
			layer.alert(data.message, {
				icon: 1,
				title: "提示信息"
			}); 
    	}
    }); 
}