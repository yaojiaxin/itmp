var curDate = (new Date()).getTime();
var myChart1 = "";
var myChart2 = "";
var myChart3 = "";
var myChart4 = "";
var myChart5 = "";
var myChart6 = "";
var option1 = {};
var option2 = {};
var option3 = {};
$(function(){
	//所有的Input标签，在输入值后出现清空的按钮
	$('input[type="text"]').parent().css("position", "relative");
	$('input[type="text"]').parent().append("<span onclick='clearContent(this)' class='btn_clear'></span>");
	$('input[type="text"]').bind("input propertychange", function () {
		if ($(this).val() != "") {
			$(this).parent().children(".btn_clear").css("display", "block");
		} else {
			$(this).parent().children(".btn_clear").css("display", "none");
		}
	});

	showSystems();
	
	laydate.render({ 
		  elem: '#startDate'
		});
	
	laydate.render({ 
		  elem: '#endDate'
		  ,max:  curDate
		});
	
	myChart1 = echarts.init(document.getElementById('main'));
	myChart2 = echarts.init(document.getElementById('main2'));
	myChart3 = echarts.init(document.getElementById('main3'));
	myChart4 = echarts.init(document.getElementById('main4'));
	myChart5 = echarts.init(document.getElementById('main5'));
	myChart6 = echarts.init(document.getElementById('main6'));

    // 指定图表的配置项和数据
	option1 = {
			animation:false,
		    title: {
		        left: 'center',
		        text: '缺陷率整体趋势图(按月)',
		    },
		    xAxis: {
		        type: 'category',
		        data: [],
		        axisLabel:{
		        	rotate:40
		        }
		    },
		    yAxis: {
		    	name: '缺陷率(%)',
		        type: 'value'
		    },
		    series: [{
		        label: {
		                normal: {
		                    show: true,
		                    position: 'top'
		                }
		            },
		        data: [],
		        type: 'line'
		    }]
		};
	
	option2 = {
			animation:false,
		    title: {
		        left: 'center',
		        text: '本月缺陷率统计(按项目)',
		    },
		    grid:{
		    	bottom:10,
		    	containLabel:true
		    },
		    xAxis: {
		        type: 'category',
		        data: [],
		        axisLabel:{
		        	rotate:40,
		        	interval: 0,
		        	fontSize:11
		        }
		    },
		    yAxis: {
		    	name: '缺陷率(%)',
		        type: 'value'
		    },
		    series: [{
		        data: [],
		        type: 'bar'
		    }]
		};
	
	option3 = {
			animation:false,
		    title : {
		        text: '缺陷等级分布情况',
		        x:'center'
		    },
		    legend: {
		        type: 'scroll',
		        orient: 'vertical',
		        right: 10,
		        top: 20,
		        bottom: 20,
		        data: ["a","b","c"],

		    },
		    series : [
		        {
		            name: '姓名',
		            type: 'pie',
		            radius : '55%',
		            center: ['40%', '50%'],
		            data: [],
		            itemStyle: {
		                emphasis: {
		                    shadowBlur: 10,
		                    shadowOffsetX: 0,
		                    shadowColor: 'rgba(0, 0, 0, 0.5)'
		                }
		            }
		        }
		    ]
		};

});
function clearSearch(){
	$("#startDate").val('');
	$("#endDate").val('');
	$("#systemId").selectpicker('val','');
}

function showSystems() {
	$.ajax({
		url: "/testManage/testCase/getAllSystem",
		method: "post",
		dataType: "json",
		success: function (data) {
			var list = data.data;
			system_arr = data.data;
			for (var i = 0; i < list.length; i++) {
				//先创建好select里面的option元素       
				var option = document.createElement("option");
				//转换DOM对象为JQ对象,好用JQ里面提供的方法 给option的value赋值         
				$(option).val(list[i].id);
				//给option的text赋值,这就是你点开下拉框能够看到的东西          
				$(option).text(list[i].systemName);
				//获取select 下拉框对象,并将option添加进select            
				$('#systemId').append(option);
			}
			$('.selectpicker').selectpicker('refresh');
		}
	});
}

function exportExcel(){
	var startDate = $("#startDate").val()
	var endDate = $("#endDate").val();
	var systemId = $("#systemId").selectpicker('val');
	if(startDate == ""){
		layer.alert("开始时间为空",{icon:0});
		return;
	}else if(endDate == ""){
		layer.alert("结束时间为空",{icon:0});
		return;
	}else if(new Date(startDate) > new Date(endDate)){
		layer.alert("开始时间大于结束时间",{icon:0});
		return;
	}else if(startDate.substr(0,7) != endDate.substr(0,7)){
		layer.alert("开始时间和结束时间必须为同一年同一月",{icon:0});
		return;
	}
	$("#loading").css('display', 'block');
	$.ajax({
		url: "/testManage/testReport/getChartData",
		method: "post",
		data:{
			startDate : startDate,
			endDate : endDate,
			systemIdStr : JSON.stringify(systemId)
		},
		success: function (data) {
			$("#loading").css('display', 'none');
			option1.xAxis.data = data.defectPro.time;
			option1.series[0].data = data.defectPro.defectPro;
			// 使用刚指定的配置项和数据显示图表。
		    myChart1.setOption(option1);
		    var defectProBase64 = myChart1.getDataURL({type:"jpeg",pixelRatio: 2,
		        backgroundColor: '#fff'});
		    option2.xAxis.data = data.defectProMonth.systemName;
			option2.series[0].data = data.defectProMonth.defectPro;
			// 使用刚指定的配置项和数据显示图表。
		    myChart2.setOption(option2);
		    var defectProMonthBase64 = myChart2.getDataURL({type:"jpeg",pixelRatio: 2,
		        backgroundColor: '#fff'});
		    
		    option2.xAxis.data = data.defectProYear.systemName;
			option2.series[0].data = data.defectProYear.defectPro;
			// 使用刚指定的配置项和数据显示图表。
		    myChart3.setOption(option2);
		    var defectProYearBase64 = myChart3.getDataURL({type:"jpeg",pixelRatio: 2,
		        backgroundColor: '#fff'});
		    
		    option2.xAxis.data = data.defectProProjectMonth.projectName;
			option2.series[0].data = data.defectProProjectMonth.defectPro;
			// 使用刚指定的配置项和数据显示图表。
		    myChart4.setOption(option2);
		    var defectProProjectMonthBase64 = myChart4.getDataURL({type:"jpeg",pixelRatio: 2,
		        backgroundColor: '#fff'});
		    
		    option2.xAxis.data = data.defectProProjectYear.projectName;
			option2.series[0].data = data.defectProProjectYear.defectPro;
			// 使用刚指定的配置项和数据显示图表。
		    myChart5.setOption(option2);
		    var defectProProjectYearBase64 = myChart5.getDataURL({type:"jpeg",pixelRatio: 2,
		        backgroundColor: '#fff'});
		    
		    option3.legend.data = data.defectLevel.level;
			option3.series[0].data = data.defectLevel.defectCount;
			// 使用刚指定的配置项和数据显示图表。
		    myChart6.setOption(option3);
		    var defectLevelBase64 = myChart6.getDataURL({type:"jpeg",pixelRatio: 2,
		        backgroundColor: '#fff'});
		    var params = {};
		    params.defectProImg = defectProBase64;
		    params.defectProMonthImg = defectProMonthBase64;
		    params.defectProYearImg = defectProYearBase64;
		    params.defectProProjectMonthImg = defectProProjectMonthBase64;
		    params.defectProProjectYearImg = defectProProjectYearBase64;
		    params.defectLevelImg = defectLevelBase64;
		    params.startDate = startDate;
		    params.endDate = endDate;
		    post("/testManage/testReport/exportReport",params);
		}
	});
	
//	window.location.href = "/testManage/testReport/exportReport?startDate="+startDate+"&endDate="+endDate+"&systemIdStr="+systemId;
}

function post(url, params) { 
    // 创建form元素
    var temp_form = document.createElement("form");
    // 设置form属性
    temp_form .action = url;      
    temp_form .target = "_self";
    temp_form .method = "post";      
    temp_form .style.display = "none";
    // 处理需要传递的参数 
    for (var x in params) { 
        var opt = document.createElement("textarea");      
        opt.name = x;      
        opt.value = params[x];      
        temp_form .appendChild(opt);      
    }      
    document.body.appendChild(temp_form);
    // 提交表单      
    temp_form .submit();     
} 