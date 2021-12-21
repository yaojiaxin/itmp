/**************
 * 新建类项目详情
 * niexingquan
 * 2019-11-12
 */
var announcementObj = {
  arr: [],
  timer: {},
  title: '项目公告'
};
$(() => {
  Main_request();
  zTree();
})

function nav_href(result) {
  $(".nav_bar>div").hover(function () {
    $("#home").removeClass("myActive");
    $(this).find('.nav_tit').addClass('titActive');
    $(this).find('.nav_item').addClass('myActive');
    $(this).siblings().find('.nav_tit').removeClass('titActive');
    $(this).siblings().find('.nav_item').removeClass('myActive');
  }, function () {
    $('.nav_tit').removeClass('titActive');
    $('.nav_item').removeClass('myActive');
    $("#home").addClass("myActive");
    $("#home").siblings().addClass("titActive");
  })
  let content_arr = {
    'risk': {
      menuButtonId: '9902',
      menuButtonName: '新建类风险管理',
      url: '/projectManageui/newProject/toRiskManage'
    },
    'problem': {
      menuButtonId: '9903',
      menuButtonName: '新建类问题管理',
      url: '../projectManageui/newProject/toQuestionManage'
    },
    'change': {
      menuButtonId: '9904',
      menuButtonName: '新建类变更管理',
      url: '../projectManageui/newProject/toUpdateManage'
    },
    'plan': {
      menuButtonId: '9905',
      menuButtonName: '新建类项目计划管理',
      url: '../projectManageui/newProject/toPlanChart'
    },
    'doc': {
      menuButtonId: '9906',
      menuButtonName: '新建类项目资产库',//../projectManageui/assetLibrary/systemPerspective/toEdit
      url: '../projectManageui/documentLibrary/toEdit',
    },
    'sprint': {
      menuButtonId: '9907',
      menuButtonName: '冲刺管理',
      url: '../devManageui/sprintManageui/toSprint'
    },
    'board': {
      menuButtonId: '9908',
      menuButtonName: '看板',
      url: '../devManageui/displayboard/toDisPlayBoard'
    },
    'job': {
      menuButtonId: '9909',
      menuButtonName: '开发任务管理',
      url: '../devManageui/devtask/toDevTask'
    },
    'team': {
      menuButtonId: '9910',
      menuButtonName: '运维项目管理',
      url: '../projectManageui/oamproject/toEditProject'
    },
    'notice': {
      menuButtonId: '9911',
      menuButtonName: '公告管理',
      url: '../devManageui/notice/toNoticeManage'
    },
  }
  for (let key in content_arr) {
    $("#" + key).bind("click", function () {
      let param = "?id=" + result.id;
      if (key == "notice") {
        param = "?projectIds=" + result.id+"&type=1";
      }
      if (key == "doc") {
        param += ",name=" + encodeURIComponent(result.projectHome.projectName);
      }
      if (key == "risk" || key == "problem" || key == "change") {
        param += "&userId=" + result.projectHome.userId + "&userName=" + result.projectHome.userName + "&name=" + result.projectHome.projectName + "&type=1";
      }
      if (key == "plan") {
        param += "&name=" + result.projectHome.projectName + "&requestUserId=0";
      }
      if (key == "team") {
        param = "?id=" + result.id + "&type=1";
      }
      if (key == "sprint" || key == 'job') {
        let sy_id = '',sy_Name = '',sy_Code = '';
        if(result.interactedSystem.length){
          sy_id = result.interactedSystem.map(function(val){
            return val.systemId
          })
          sy_id.join(',');
          sy_Name = result.interactedSystem.map(function(val){
            return val.interactedSystem
          })
          sy_Name.join(',');
          sy_Code = result.interactedSystem[0].systemCode;
        }
        if(key == "sprint"){
          param = "?projectId=" + result.id + "&systemId=" + sy_id + "&systemName=" + sy_Name;
        }
        if(key == "job"){
          param = "?planId=" + result.id + "&planName=" + result.projectHome.projectName + 
            "&systemId=" + sy_id + "&systemName=" + sy_Name + "&systemCode=" + sy_Code;
        }
      }
      window.parent.toPageAndValue(content_arr[key].menuButtonName, content_arr[key].menuButtonId, content_arr[key].url + param);
    })
  }
}

function Main_request() {
  $.ajax({
    url: "/projectManage/projectHome/home",
    type: "post",
    dataType: "json",
    data: {
      "id": $("#detail_id").val(),
      "type": $("#detail_type").val(),
    },
    beforeSend: function () {
      $("#loading").css('display', 'block');
    },
    success: function (data) {
      summarize(data.projectHome, data.peripheralSystemList, data.systemUserNameList, data.interactedSystem);
      nav_href(data);
      if (data.notice) {
        data.notice.length && showAnnouncement(data.notice);
      }
      update_work(data.riskInfo);
      if ($("#detail_type").val() == 2) { //瀑布
        all_work(data.taskForce, false);
        milestone_give(data.projectPlan);
      } else if ($("#detail_type").val() == 1) { //敏捷
        $('#dynamic_body').addClass('min_max_hei');
        $('#sprint_tit').text('冲刺情况').after(`
	              <ul class="flex sprint_body bor_b">
	                <li class="sprint_item sprint_active">当前冲刺</li>
	                <li class="sprint_item">下一冲刺</li>
	              </ul>
	              <div class="work_body flex justify_c_c"></div>
	            `);
        all_work(data.taskForceAgility, data.springNextWork);
        $('#milestone_body').hide();
        $('#milestone_tit').text('燃尽图').after(`
	          <div class="p_10" id="exponential_chart" style="width: 450px;height: 300px;"></div>
	        `);
        chart_map(data.countWorkload, data.period - 1, data.projectPeriod);
      }
      if(data.messageInfo.length){
        project_dynamic(data.messageInfo);
      }else{
        $('#dynamic_body').append(`<h6 class="dynamic_item">暂无动态...</h6>`);
      }
      $("#loading").css('display', 'none');
      $('.sprint_item').bind('click', function () {
        $(this).addClass('sprint_active').siblings().removeClass('sprint_active');
        var _idx = $(this).index();
        $('.work_body').eq(_idx).show().siblings('.work_body').hide();
      })
    }
  })
}

//项目概述
function summarize(overview, system, users, interactedSystem) {
  if (overview) {
    $('.projectName').text(isValueNull(overview.projectName));
    $('#projectCode').text(isValueNull(overview.projectCode));
    var status_str = '';
    if (overview.projectStatus == 1 || overview.projectStatus == 4) {
      status_str += `<span class="status status0">${select_dic('#PROJECT_STATUS', overview.projectStatus)}</span>`;
    } else {
      status_str += `<span class="status status1">${select_dic('#PROJECT_STATUS', overview.projectStatus)}</span>`;
    }
    if (overview.status){
      if (overview.status == 2) {
        status_str += `<span class="status status2">${select_dic('#_STATUS', overview.status)}</span>`;
      } else if(overview.status == 1){
        status_str += `<span class="status status1">${select_dic('#_STATUS', overview.status)}</span>`;
      }
    }else{
      status_str += '';
    }
    if (overview.developmentMode) {
      status_str += `<span class="status status3">${select_dic('#DEVELOPMENT_MODE', overview.developmentMode)}</span>`;
    }
    
    $('#all_status').html(`${status_str}`);
    interactedSystem.length && interactedSystem.map(function (val) {
      $('#interactedSystem').append(`
        <li class="">
          <img class="img img_15" id="u669_img" src="../../../projectManageui/static/images/detail/u669.png">
          <span>${val.interactedSystem == null ? '' : val.interactedSystem}</span>
        </li>
      `);
    })
    $('#projectType').text(select_dic('#PROJECT_TYPE', overview.projectType));
    var planDate = isValueNull(overview.planStartDate) + '~' + isValueNull(overview.planEndDate);
    $('#projectWeek').text(planDate == '~' ? '' : planDate);
    $('#budgetCode').text(isValueNull(overview.budgetNumber));
    $('#projectManager').text(isValueNull(overview.userName));
  }
  system.length && system.map(function (val) {
    $('#rimSystem').append(`
      <li class="">
        <img class="img img_15" src="../../../projectManageui/static/images/detail/u671.png">
        <span>${val.systemName == null ? '' : val.systemName}</span>
      </li>
    `);
  })
  users.length && users.map(function (val, idx) {
    if (idx <= 9) {
      $('#projectUsers').append(`
        <li class="def_col_18">
          <i class="fa fa-user-o" aria-hidden="true"></i>
          <span>${val.userName == null ? '' : val.userName}</span>
        </li>
      `);
    }
  })
}

//总任务赋值
function all_work(taskForce, springNextWork) {
  $('.work_body').eq(0).html(`
    <div class="record">
      <p class="">总任务数</p>
      <h4 class="big_blue">${taskForce.taskCount || 0}</h4>
    </div>
    <div class="record">
      <p class="">待实施</p>
      <h4 class="big_blue">${taskForce.toImplement || 0}</h4>
    </div>
    <div class="record">
      <p class="">实施中</p>
      <h4 class="big_blue">${taskForce.inImplementation || 0}</h4>
    </div>
    <div class="record">
      <p class="">已完成</p>
      <h4 class="big_blue">${taskForce.offTheStocks || 0}</h4>
    </div>
    <div class="record">
      <p class="">已延期</p>
      <h4 class="big_red">${taskForce.postponed || 0}</h4>
    </div>
    <div class="record">
      <p class="">已取消</p>
      <h4 class="big_blue">${taskForce.canceled || 0}</h4>
    </div>
  `);
  if (springNextWork) {
    $('.work_body').eq(1).html(`
      <div class="record">
        <p class="">总任务数</p>
        <h4 class="big_blue">${springNextWork.taskCount || 0}</h4>
      </div>
      <div class="record">
        <p class="">待实施</p>
        <h4 class="big_blue">${springNextWork.toImplement || 0}</h4>
      </div>
      <div class="record">
        <p class="">实施中</p>
        <h4 class="big_blue">${springNextWork.inImplementation || 0}</h4>
      </div>
      <div class="record">
        <p class="">已完成</p>
        <h4 class="big_blue">${springNextWork.offTheStocks || 0}</h4>
      </div>
      <div class="record">
        <p class="">已延期</p>
        <h4 class="big_red">${springNextWork.postponed || 0}</h4>
      </div>
      <div class="record">
        <p class="">已取消</p>
        <h4 class="big_blue">${springNextWork.canceled || 0}</h4>
      </div>
    `).hide();
  }
}

//变更---------风险
function update_work(work) {
  let wid = (+$('#falls').width() - 10) / 2;
  $('#update_work').html(`
    <div class="def_col_7">
      <img class="img" id="u645_img" src="../../../projectManageui/static/images/detail/u645.png">
      <p class="">变更</hp>
    </div>
    <div class="def_col_9 ">
      <p class="">本周新增</p>
      <h4 class="big_blue">${work.changeWeekAdd || 0}</h4>
    </div>
    <div class="def_col_9 ">
      <p class="">变更总数</p>
      <h4 class="big_blue">${work.changeInfoCount || 0}</h4>
    </div>
    <div class="def_col_9 ">
      <p class="">待确认数</p>
      <h4 class="big_blue">${work.confirmationNumber || 0}</h4>
    </div>
  `).css({width:wid+'px'});
  $('#risk_work').html(`
    <div class="def_col_7">
      <img class="img" id="u626_img" src="../../../projectManageui/static/images/detail/u626.png">
      <p class="">风险</p>
    </div>
    <div class="def_col_9 ">
      <p class="">本周新增</p>
      <h4 class="big_blue">${work.riskWeekAdd || 0}</h4>
    </div>
    <div class="def_col_9 ">
      <p class="">风险总数</p>
      <h4 class="big_blue">${work.riskInfoCount || 0}</h4>
    </div>
    <div class="def_col_9 ">
      <p class="">未解决数</p>
      <h4 class="big_blue">${work.outStandingNumber || 0}</h4>
    </div>
  `).css({width:wid+'px'});
}

//里程碑
function milestone_give(milestones) {
  milestones.length && milestones.map(function (val) {
    var schedule_color = '';
    if (val.progress == 100) {
      schedule_color = 'bg_cyan';
    } else if (val.progress > 50) {
      schedule_color = 'bg_blue';
    } else {
      schedule_color = 'bg_red';
    }
    $('#milestone_body').append(`
      <p class="milestone rowdiv m_t_10">
        <span class="def_col_12 overHidden" title="${val.planName}">${val.planName}</span>
        <span class="def_col_11">${val.planStartDate}~${val.planEndDate}</span>
        <span class="def_col_12 schedule"><span class="${schedule_color}" style="width:${val.currentProgress}%;"></span></span>
        <span class="def_col_3">${val.currentProgress}%</span>
      </p>
    `);
  })
}

//燃尽图
function chart_map(countWorkload, period, projectPeriod) {
  var weed = (countWorkload / period).toFixed(1); //
  var estimateRemainWorkload = [];   //实际
  var estimateWorkload = [];   //预计
  var date = [];

  if (projectPeriod != null && projectPeriod.length>0){
      for (var i = 0; i < projectPeriod.length; i++) {
          estimateRemainWorkload[i] = projectPeriod[i].estimateRemainWorkload == null ? 0 : projectPeriod[i].estimateRemainWorkload;
          if (i == projectPeriod.length - 1) {
              estimateWorkload[i] = 0;
          } else {
              estimateWorkload[i] = (countWorkload - (weed * i)).toFixed(1);

          }
          date[i] = projectPeriod[i].date;
      }
  }


  var myChart = echarts.init(document.getElementById('exponential_chart'));
  var options = {
    title: {},
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      right: 40,
      data: ['实际', '预计']
    },
    xAxis: {
      type: 'category',
      name: '日期',
      boundaryGap: false,
      splitLine: {
        show: false
      },
      data: date
    },
    yAxis: {
      name: '工时'
    },
    series: [
      {
        name: '实际',
        type: 'line',
        // stack: '剩余工作量',
        data: estimateRemainWorkload,
        itemStyle: {
          normal: {
            // 折点颜色样式
            color: 'green',
            lineStyle: {
              // 折线颜色样式
              color: 'green'
            }
          }
        },
        // data: this.opinionData,
      },
      {
        name: '预计',
        type: 'line',
        data: estimateWorkload,
        itemStyle: {
          normal: {

            // 折点颜色样式
            color: 'orange',
            lineStyle: {
              // 折线颜色样式
              color: 'orange'
            }
          }
        },
      },
    ]
  }
  myChart.setOption(options);
}

// 项目动态
function project_dynamic(messageInfo) {
  let _class = '';
  if($("#detail_type").val() == 1){
    _class = 'text_ell3';
  }
  messageInfo.map(function (val) {
    $('#dynamic_body').append(`
      <li class="dynamic_item">
        <p class="rowdiv">
          <span class="def_col_20 overHidden bold p_l_0" title="${val.messageTitle}">${val.messageTitle}</span>
          <span class="def_col_16">${val.createDate}</span>
        </p>
        <p class="p_t_0 text_ell ${_class}" title="${val.messageContent || ''}">${val.messageContent || ''}</p>
      </li>
    `);
  })
}

//项目文档
function zTree() {
  var setting = {
    view: {
      showLine: false
    },
    data: {
      key: {
        name: "dirName",
        // isParent: "isParent"
      },
      simpleData: {
        enable: true,
        idKey: "id",
        pIdKey: "pId",
        rootPId: null
      },
      simpleData: {
        enable: true
      },
      callback: {
        // onAsyncSuccess: onAsyncSuccess,
        // onAsyncError: onAsyncError,
        onClick: onClickInitTable
      }
    }
  };
  $.ajax({
   // url: "/projectManage/systemPerspective/getSystemDirectoryList/forProjectVision",
    url:"/projectManage/documentLibrary/getDirectoryTree",
    type: "post",
    data: {
      projectId: $("#detail_id").val(),
    },
    success: function (data) {
      // $.fn.zTree.init($("#menuTree"), treeSetting, data);
      var zTreeObj = $.fn.zTree.init($("#menuTree"), setting, data);
      var nodes = zTreeObj.getNodes()[0];
      if (zTreeObj != null) zTreeObj.expandNode(nodes, true);
      onClickInitTable(event, "0", nodes);
    },
    error: function (data) {
    }
  })
  
  
}

function onClickInitTable(e, type, nodes) {

}

//字典查数据
function select_dic(ele, value) {
  var _str = '';
  $(ele).find('option').each(function (idx, val) {
    if (value == val.value) {
      _str = val.innerText
    }
  })
  return _str
}
