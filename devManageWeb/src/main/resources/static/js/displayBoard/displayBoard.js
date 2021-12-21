var devtaskStatusList = [];

$(document).ready(function () {
	initPage();
	uploadFileList();
	$("#cheakWorkTaskBtn").attr("workStatus", "1");
	$("#cheakWorkTaskBtn").bind("click", function () {
		if ($("#cheakWorkTaskBtn").attr("workStatus") == "1") {
			$("#cheakWorkTaskBtn").attr("workStatus", "2");
			changeWorkTask();
		} else {
			$("#cheakWorkTaskBtn").attr("workStatus", "1");
			$('#cheakSprint').trigger('change');
		}
	});
	$("#refreshBtn").bind("click", function () {
		if ($("#cheakWorkTaskBtn").attr("workStatus") == "1") {
			$('#cheakSprint').trigger('change');
		} else if ($("#cheakWorkTaskBtn").attr("workStatus") == "2") {
			changeWorkTask();
		}
	});
	var L = $('.bottom').scrollLeft();
	$('#fixed_nav').css({left:-L});
	//泳道头部固定
	$('.bottom').scroll(function(){
		var L = $('.bottom').scrollLeft();
		$('#fixed_nav').css({left:-L});
		var scrollTop = $('.bottom').scrollTop();
		if(scrollTop > 0){
			$('.flow_top').css({top:scrollTop + 140 + 'px'})
			$('#fixed_nav').css('display','block');	
		}else{
			$('#fixed_nav').css('display','none');
			$('.flow_top').css({top:140+'px'})
		}
	})
});
//展示或隐藏泳道
function show(This,index) {
	if ($(This).parent().parent().hasClass("shrink")) {
		var item = $('#statusDiv').children().eq(index);
		item.removeClass('shrink');
		item.find('.fa').addClass("fa-angle-double-left");
		item.find('.fa').removeClass("fa-angle-double-right");
		item.find('.taskBody').css({'width':'274px'})
		var item2 = $('.nav_flex').children().eq(index);
		item2.removeClass('shrink');
		item2.find('.fa').addClass("fa-angle-double-left");
		item2.find('.fa').removeClass("fa-angle-double-right");
		item2.find('.taskBody').css({'width':'274px'})
	} else {
		var item = $('#statusDiv').children().eq(index);
		item.addClass('shrink');
		item.find('.fa').removeClass("fa-angle-double-left");
		item.find('.fa').addClass("fa-angle-double-right");
		item.find('.taskBody').css({'width':'38px'})
		var item2 = $('.nav_flex').children().eq(index);
		item2.addClass('shrink');
		item2.find('.fa').removeClass("fa-angle-double-left");
		item2.find('.fa').addClass("fa-angle-double-right");
		item2.find('.taskBody').css({'width':'38px'})
	}

}
function showChildTask(This) {
	if ($(This).next(".taskContDiv").hasClass("hideBlock")) {
		$(This).next(".taskContDiv").removeClass("hideBlock");
	} else {
		$(This).next(".taskContDiv").addClass("hideBlock");
	}
}
function initPage() {
	getAllProject();
}
//获取一级——所有项目
function getAllProject() {
	$.ajax({
		url: "/devManage/displayboard/getAllProject",
		dataType: "json",
		type: "post",
		success: function (data) {
			let give_val = false;
			$("#cheakProject").empty();
			$(".cheakSystem").css("display", "none");
			$(".cheakSprint").css("display", "none");
			$(".cheakSprintBtn").css("display", "none");
			$("#cheakProject").append("<option value >请选择项目</option>");
			for (var i = 0; i < data.projects.length; i++) {
				$("#cheakProject").append("<option value='" + data.projects[i].id + "'>" + data.projects[i].projectName + "</option>");
				if($("#project_id").val() && $("#project_id").val() == data.projects[i].id){
					give_val = true;
				}
			}
			$('.selectpicker').selectpicker('refresh');
			if(give_val){
				$("#cheakProject").selectpicker('val',$("#project_id").val()).change();
			}
		}
	})
}
//获取二级——所有项目下的系统
function getSystem(This) {
	if ($(This).val() != '') {
		$.ajax({
			url: "/devManage/displayboard/getSystemByPId",
			dataType: "json",
			type: "post",
			data: {
				"projectId": $(This).val()
			},
			success: function (data) {
				$("#cheakSystem").empty();
				$(".cheakSystem").css("display", "block");
				$("#cheakProjectGroup").css("display", "block");
				$(".cheakSprint").css("display", "none");
				$(".cheakSprintBtn").css("display", "none");
				$("#cheakSystem").append("<option value >请选择系统</option>");
				for (var i = 0; i < data.systemInfos.length; i++) {
					$("#cheakSystem").append("<option value='" + data.systemInfos[i].id + "'>" + data.systemInfos[i].systemName + "</option>");
				}
				$('.selectpicker').selectpicker('refresh');
				//查询项目组
				getProjectGroup();
				// if($("#system_id").val()){
				// 	// $("#cheakSystem").selectpicker('val',1).change();
				// 	$("#cheakSystem").selectpicker('val',$("#system_id").val()).change();
				// }
			}
		})
	} else {
		$(".cheakSystem").css("display", "none");
		$(".cheakProjectGroup").css("display", "none");
		$(".cheakSprint").css("display", "none");
	}
}
//获取三级——所有的系统下面的冲刺任务
function getSprint(This) {
	if ($(This).val() != '') {
		$.ajax({
			url: "/devManage/displayboard/getSprintBySystemId",
			dataType: "json",
			type: "post",
			data: {
				"systemId": $(This).val()
			},
			success: function (data) {
				$("#cheakSprint").empty();
				$(".cheakSprint").css("display", "block");
				$("#cheakSprint").append("<option value >请选择冲刺</option>");
				for (var i = 0; i < data.sprintInfos.length; i++) {
					$("#cheakSprint").append("<option value='" + data.sprintInfos[i].id + "'>" + data.sprintInfos[i].sprintName + "</option>");
				}
				$('.selectpicker').selectpicker('refresh');
			}
		})
	} else {
		$(".cheakSprint").css("display", "none");
	}
}


//获取该系统下项目小组
function getProjectGroup() {
	var projectId = $("#cheakProject").val();

	if (projectId == null || projectId == "") {
		return;
	}

	$.ajax({
		url: "/devManage/displayboard/getProjectGroupByProjectId",
		dataType: "json",
		type: "post",
		data: {
			"projectId": projectId
		},
		success: function (data) {
			var znodes = data.zNodes;
			var znodesend = JSON.parse(znodes);
			$.fn.zTree.init($("#projectOwn"), setting, znodesend);
			var zTree = $.fn.zTree.getZTreeObj("projectOwn");
			//全选
			var node = zTree.getNodes();
			var name = "";
			var nodes = zTree.transformToArray(node);
			for (var i = 0; i < nodes.length; i++) {
				//zTree.checkNode(nodes[i], true, true);
				zTree.checkNode(nodes[i], true, true);
				//zTree.selectNode(nodes[i],true);
				zTree.updateNode(nodes[i]);
				nodes[i].nocheck = false;
				name += nodes[i].name + ",";
			}
			if (name.length > 0) name = name.substring(0, name.length - 1);
			//showMenu();
			// zTree.expandAll(true);
			$("#cheakProjectGroup").val(name);
			//var nodes = zTree.getCheckedNodes();
			//获取光标的值
			// var nodes = zTree.getSelectedNodes();

			//zTree.expandAll(true);

		}
	})

}
//排序取最大高度
function Sort_(){
	var arr_ = []
	for(var i = 0; i < $('#statusDiv .taskCont').length; i++){
		arr_.push($('.taskCont').eq(i).height())
	}
	return arr_.sort(function(a,b){
		return a - b;
	})[arr_.length-1] + 'px'
}
//获取列表
function getDevTask(This) {
	if ($(This).val() != '') {
		$.ajax({
			url: "/devManage/displayboard/getDevTaskBySprintId",
			dataType: "json",
			type: "post",
			data: {
				"sprintId": $(This).val()
			},
			success: function (data) {
				$("#cheakWorkTaskBtn").attr("workStatus", "1");
				$(".cheakSprintBtn").css("display", "block");
				$("#statusDiv").empty();
				$(".nav_flex").empty();
				var allStatus = {};
				for (var i = 0; i < data.workTaskstatus.length; i++) {
					allStatus[data.workTaskstatus[i].valueCode] = {};
					allStatus[data.workTaskstatus[i].valueCode].valueName = data.workTaskstatus[i].valueName;
					allStatus[data.workTaskstatus[i].valueCode].num = 0;
				}
				$("#statusDiv").width(data.status.length * 280);
				for (var i = 0; i < data.status.length; i++) {
					devtaskStatusList = data.status;
					var str = '';
					var taskBodyBg = '';
					if (i % 2 == 0) {
						taskBodyBg = 'taskBodyBg1';
					} else {
						taskBodyBg = 'taskBodyBg2';
					}
					str += '<div class="sortable"><div class="head"><div class="headFont">' + data.status[i].valueName + '<span class="workNumbers"></span></div><div class="rightBtn" onclick="show( this,' + i + ' )"><span class="fa fa-angle-double-left"></span></div></div>' +
						'<div class="taskBody ' + taskBodyBg + '"><div class="taskCont" id="sortable' + data.status[i].valueCode + '" val="' + data.status[i].valueCode + '"></div>' +
						'<div class="overHeadFont ' + taskBodyBg + '"><div class="flow_top">' + data.status[i].valueName + '<span class="workNumbers"></span></div>' +
						'</div></div></div>';

					$("#statusDiv").append(str);
					$('.nav_flex').append(str);
				}

				//获取选中的项目组
				var ids = getCheckedZtree();

				for (var i = 0; i < data.reqFeatures.length; i++) {


					var executeProjectGroupId = data.reqFeatures[i].executeProjectGroupId;
					if (executeProjectGroupId == null || executeProjectGroupId == "" || executeProjectGroupId == "null") {
						if (ids.indexOf("-1") == -1) {//所选不包括无项目组
							continue;
						}
					} else {
						//有值
						if (ids.indexOf(executeProjectGroupId) == -1) {
							continue;
						}

					}
					var taskStr = '';
					var status = '';
					var childStr = '';
					var childStatusArr = $.extend(true, {}, allStatus);
					var taskColor = '';

					for (var j = 0; j < data.reqFeatures[i].workStatusCount.length; j++) {
						childStatusArr[data.reqFeatures[i].workStatusCount[j].devtatskStatus].num = data.reqFeatures[i].workStatusCount[j].statusCount;
					}
					for (var key in childStatusArr) {
						status += '<span class="def_col_12">' + childStatusArr[key].valueName + ":" + childStatusArr[key].num + '</span> ';
					}
					for (var j = 0; j < data.reqFeatures[i].devTasks.length; j++) {

						var fontColor = colorChance(data.reqFeatures[i].devTasks[j].devTaskPriority);

						childStr += '<div class="childTask">' +
							'<div class="rowdiv taskTitDiv"><div class="def_col_36 taskCode childTaskBg">' +
							'<span class="fa fa-check-square ' + fontColor + '"></span> <a class="a_style" title="' + data.reqFeatures[i].devTasks[j].devTaskCode + '（' + data.reqFeatures[i].devTasks[j].statusName + '）" onclick="getSee(' + data.reqFeatures[i].devTasks[j].id + ')">' + data.reqFeatures[i].devTasks[j].devTaskCode + '（' + data.reqFeatures[i].devTasks[j].statusName + '）</a></div>' +
							'<div class="def_col_36 taskDescribe" title="' + toStr(data.reqFeatures[i].devTasks[j].devTaskName) + '">' + toStr(data.reqFeatures[i].devTasks[j].devTaskName) + '</div>' +
							'<div class="def_col_13 taskPreson" title="' + toStr(data.reqFeatures[i].devTasks[j].userName) + '">' + toStr(data.reqFeatures[i].devTasks[j].userName) + '</div>' +
							'<div class="def_col_6 taskTime" title="' + toStr(data.reqFeatures[i].devTasks[j].planWorkload) + '">' + toStr(data.reqFeatures[i].devTasks[j].planWorkload) + '</div>' +
							'</div></div>';
					}
					var classColor = choiceClass(data.reqFeatures[i].requirementFeaturePriority);
					var numColor = 'numColor';

					if (toStr(data.reqFeatures[i].devTasks.length) == "0") {
						numColor = '';
					}
					if (data.reqFeatures[i].requirementFeatureStatus == '06' || data.reqFeatures[i].requirementFeatureStatus == '03' || data.reqFeatures[i].requirementFeatureStatus == '00') {
						taskColor = 'bg_247';
					}
					taskStr += '<div class="taskDiv ' + taskColor + '" val="' + data.reqFeatures[i].id + '"><div class=' + classColor + ' ondblclick="showChildTask( this )">' +
						'<div class="rowdiv taskTitDiv"><div class="def_col_36 taskCode taskCodeBg">' +
						'<a class="a_style" title="' + data.reqFeatures[i].featureCode + '" onclick="showReqFeature(' + data.reqFeatures[i].id + ')">' + data.reqFeatures[i].featureCode + '</a></div>' +
						'<div class="def_col_36 taskDescribe" title="' + toStr(data.reqFeatures[i].featureName) + '">' + toStr(data.reqFeatures[i].featureName) + '</div>' +
						'<div class="def_col_13 taskPreson" title="' + toStr(data.reqFeatures[i].executeUserName) + '">' + toStr(data.reqFeatures[i].executeUserName) + '</div>' +
						'<div class="def_col_6 taskNum ' + numColor + '" title="' + toStr(data.reqFeatures[i].devTasks.length) + '">' + toStr(data.reqFeatures[i].devTasks.length) + '</div>' +
						'<div class="def_col_6 taskTime" title="' + toStr(data.reqFeatures[i].estimateWorkload) + '">' + toStr(data.reqFeatures[i].estimateWorkload) + '</div>' +
						'<div class="def_col_3"></div>' +
						'<div class="def_col_4"><a href="javascript:;" class="editTask_a" onclick="workEdit(\'' + toStr(data.reqFeatures[i].featureCode) + '\',\'' + toStr(data.reqFeatures[i].featureName) + '\',' + data.reqFeatures[i].id + ' )"></a></div>' +
						'<div class="def_col_4"><a href="javascript:;" class="checkTask_a"  onclick="workSplit(\'' + toStr(data.reqFeatures[i].featureCode) + '\',\'' + toStr(data.reqFeatures[i].featureName) + '\',' + data.reqFeatures[i].id + ',' + data.reqFeatures[i].systemId + ')"></a></div>' +
						'</div></div>' +
						'<div class="taskContDiv hideBlock"><div class="taskContBody">' + childStr + '</div>' +
						'</div></div></div>';
					$("#sortable" + data.reqFeatures[i].requirementFeatureStatus).append(taskStr);
				}
				$('#statusDiv .taskBody').css({height:Sort_()})
				reloadSortable();
			}
		})
	} else {
		$(".cheakSprintBtn").css("display", "none");
	}
}
function choiceClass(type) {
	var myclass = "";
	switch (type) {
		case 0:
			myclass = "status-cancel";
			break;
		case 1:
			myclass = "status-toBeImplemented";
			break;
		case 2:
			myclass = "status-inImplemented";
			break;
		case 3:
			myclass = "status-complete";
			break;
		case 4:
			myclass = "status-review";
			break;
		default:
			myclass = "status-defind";
			break;
	}
	return myclass;
}

//拖拽后请求
function reloadSortable() {
	countWorkNum();
	$(".taskCont").sortable({
		connectWith: ".taskCont",
	}).disableSelection();
	$(".taskCont").sortable({
		stop: function (event, ui) {
			if ($(ui.item).parent().attr('val') != $(this).attr('val')) {
				if ($("#cheakWorkTaskBtn").attr("workStatus") == "1") {
					$.ajax({
						url: "/devManage/displayboard/updateDevTaskStatus",
						dataType: "json",
						type: "post",
						data: {
							reqFeatureId: $(ui.item).attr('val'),
							status: $(ui.item).parent().attr('val')
						},
						success: function (data) {
							if (data.status == 1) {
								countWorkNum();
								$('#cheakSprint').trigger('change');
								layer.alert('保存成功！', {
									icon: 1,
									title: "提示信息"
								});
							} else {
								layer.alert('保存失败！', {
									icon: 0,
									title: "提示信息"
								});
							}
						}
					})
				} else if ($("#cheakWorkTaskBtn").attr("workStatus") == "2") {
					$.ajax({
						url: "/devManage/displayboard/updateWorkTaskStatus",
						dataType: "json",
						type: "post",
						data: {
							devTaskId: $(ui.item).attr('val'),
							status: $(ui.item).parent().attr('val')
						},
						success: function (data) {
							if (data.status == 1) {
								countWorkNum();
								changeWorkTask();
								layer.alert('保存成功！', {
									icon: 1,
									title: "提示信息"
								});
							} else {
								layer.alert('保存失败！', {
									icon: 0,
									title: "提示信息"
								});
							}
						}
					})
				}
			}
		}
	});
}

//切换
function changeWorkTask() {
	if ($("#cheakSprint").val() == '') {
		layer.alert('请选择任务！', {
			icon: 0,
			title: "提示信息"
		});
	} else {
		$.ajax({
			url: "/devManage/displayboard/getWorkTaskBySprintId",
			dataType: "json",
			type: "post",
			data: {
				sprintId: $("#cheakSprint").val()
			},
			success: function (data) {
				$("#statusDiv").empty();
				$(".nav_flex").empty();
				$("#statusDiv").width(data.status.length * 280);
				for (var i = 0; i < data.status.length; i++) {
					devtaskStatusList = data.status;
					var str = '';
					var taskBodyBg = '';
					if (i % 2 == 0) {
						taskBodyBg = 'taskBodyBg1';
					} else {
						taskBodyBg = 'taskBodyBg2';
					}
					str += '<div class="sortable"><div class="head"><div class="headFont">' + data.status[i].valueName + '<span class="workNumbers"></span></div><div class="rightBtn" onclick="show( this,' + i + ')"><span class="fa fa-angle-double-left"></span></div></div>' +
						'<div class="taskBody ' + taskBodyBg + '"><div class="taskCont" id="sortable' + data.status[i].valueCode + '" val="' + data.status[i].valueCode + '"></div>' +
						'<div class="overHeadFont ' + taskBodyBg + '"><div class="flow_top">' + data.status[i].valueName + '<span class="workNumbers"></span></div>' +
						'</div></div></div>';
					$("#statusDiv").append(str);
					$(".nav_flex").append(str);
				}
				var ids = getCheckedZtree();
				for (var i = 0; i < data.devTasks.length; i++) {

					var executeProjectGroupId = data.devTasks[i].executeProjectGroupId;
					if (executeProjectGroupId == null || executeProjectGroupId == "" || executeProjectGroupId == "null") {
						if (ids.indexOf("-1") == -1) {//所选不包括无项目组
							continue;
						}
					} else {
						//有值
						if (ids.indexOf(executeProjectGroupId) == -1) {
							continue;
						}

					}

					var taskStr = '';
					var status = '';
					var fontColor = colorChance(data.devTasks[i].devTaskPriority);
					taskStr += '<div class="taskDiv" val="' + data.devTasks[i].id + '"><div class="childTask">' +
						'<div class="rowdiv taskTitDiv"><div class="def_col_36 taskCode childTaskBg">' +
						'<span class="fa fa-check-square ' + fontColor + '"></span> <a class="a_style" title="' + data.devTasks[i].devTaskCode + '" onclick="getSee(' + data.devTasks[i].id + ')">' + data.devTasks[i].devTaskCode + '</a></div>' +
						'<div class="def_col_36 taskDescribe">' + toStr(data.devTasks[i].devTaskName) + '</div>' +
						'<div class="def_col_13 taskPreson">' + toStr(data.devTasks[i].userName) + '</div>' +
						'<div class="def_col_6 taskTime">' + toStr(data.devTasks[i].planWorkload) + '</div>' +
						'</div></div></div>';
					$("#sortable" + data.devTasks[i].devTaskStatus).append(taskStr);
				}
				$("#statusDiv .sortable").eq(3).find(".taskDiv").addClass('bg_247');
				$('#statusDiv .taskBody').css({height:Sort_()})
				reloadSortable();
			}
		})

	}
}

function workEdit(featureCode, featureName, id) {
	layer.open({
		type: 2,
		title: '编辑：' + featureCode + ' | ' + featureName,
		shadeClose: true,
		shade: false,
		area: ['94%', '90%'],
		id: "1",
		offset: "4%",
		shade: 0.3,
		tipsMore: true,
		anim: 2,
		content: '/devManageui/devtask/toEdit?rowId=' + id,
		btn: ['确定', '取消'],
		btnAlign: 'c', //按钮居中
		yes: function (index, layero) {
			var body = layer.getChildFrame('body', index);
			var iframeWin = window[layero.find('iframe')[0]['name']];//得到iframe页的窗口对象，执行iframe页的方法：
			iframeWin.editDevTask();
		},
		btn2: function () {
			layer.closeAll();
		}
	});
}

function workSplit(featureCode, featureName, id, systemId) {
	layer.open({
		type: 2,
		title: '拆分：' + featureCode + ' | ' + featureName,
		shadeClose: true,
		shade: false,
		area: ['100%', '100%'],
		id: "1",
		tipsMore: true,
		anim: 2,
		content: '/devManageui/devtask/toSplit?rowId=' + id + ',systemId=' + systemId,
		btn: ['确定', '取消'],
		btnAlign: 'c', //按钮居中
		yes: function (index, layero) {
			var body = layer.getChildFrame('body', index);
			var iframeWin = window[layero.find('iframe')[0]['name']];//得到iframe页的窗口对象，执行iframe页的方法：
			iframeWin.splitCommit();
		},
		btn2: function () {
			layer.closeAll();
		}
	});
}

function pageInit() {
	$('#cheakSprint').trigger('change');
}

//计算工作num
function countWorkNum() {
	for (var i = 0; i < $("#statusDiv>.sortable").length; i++) {
		$("#statusDiv>.sortable").eq(i).find(".workNumbers").text("（" + $("#statusDiv>.sortable").eq(i).children(".taskBody").children(".taskCont").children(".taskDiv").length + "）")
		$(".nav_flex>.sortable").eq(i).find(".workNumbers").text("（" + $("#statusDiv>.sortable").eq(i).children(".taskBody").children(".taskCont").children(".taskDiv").length + "）")
	}
}

function colorChance(type) {
	var myclass = "";
	switch (type) {
		case 0:
			myclass = "devTaskPriority0";
			break;
		case 1:
			myclass = "devTaskPriority1";
			break;
		case 2:
			myclass = "devTaskPriority2";
			break;
		case 3:
			myclass = "devTaskPriority3";
			break;
		case 4:
			myclass = "devTaskPriority4";
			break;
		default:
			myclass = "devTaskPriorityUndef";
			break;
	}
	return myclass;
}

function getCheckedZtree() {
	var zTree = $.fn.zTree.getZTreeObj("projectOwn");
	var nodes = zTree.getCheckedNodes();
	//获取光标的值
	// var nodes = zTree.getSelectedNodes();
	var ids = [];
	for (var i = 0, l = nodes.length; i < l; i++) {

		ids.push(nodes[i].id);   //获取选中节点的id值

	}

	return ids;
}
