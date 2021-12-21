/**
 * Created by 朱颜辞进花辞树 on 2018/9/18.
 */
var parameterArr = {};
parameterArr.arr = window.location.href.split("?");
parameterArr.parameterArr = parameterArr.arr[1].split(",");
parameterArr.obj = {};
for (var i = 0; i < parameterArr.parameterArr.length; i++) {
    var obj = parameterArr.parameterArr[i].split("=");
    parameterArr.obj[obj[0]] = obj[1];
}
$(document).ready(function () { 
    getHistory();
})

//获取系统信息 ， 形成表格显示
function getHistory(){
	$.ajax({
        url:"/projectManage/systemPerspective/getDocumentHistory",
        type:"post",
        data:{ 
        	documentId : parameterArr.obj.id
        },
        success:function(data){
        	console.log( data )
        	getTable( data ); 
        },
        error:function(data){
        	 console.log( data );
        }
    }) 
    return ; 
}
function getTable( data ){
	$("#historyTable").bootstrapTable({ 
		data:data.data,
        method:"post",
        queryParamsType:"",
        pagination : false,
        sidePagination: "server",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8', 
        columns : [{
            field : "id",
            title : "id",
            visible : false,
            align : 'center'
        },{
            field : "documentVersion",
            title : "版本号",
            align : 'left'
        },{
            field : "updateUserName",
            title : "修改人",
            align : 'left'
        },{
            field : "lastUpdateDate",
            title : "更新时间",
            align : 'left'
        },{
            field : "relatedRequirement",
            title : "关联需求变更单",
            align : 'left',
            formatter : function(value, row, index) { 
            	console.log( value.requirementCode );
            	return value.requirementCode + " " + value.requirementName;
            }
        },{
            field : "opt",
            title : "操作",
            align : 'center',
            formatter : function(value, row, index) {
            	rows = JSON.stringify(row).replace(/"/g, '&quot;');
            	return '<a class="a_style">查看</a>';
            }
        }],
        onLoadSuccess:function(){

        },
        onLoadError : function() {

        }
    });
} 