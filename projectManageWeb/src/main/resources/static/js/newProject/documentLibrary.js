/**
 * Created by 朱颜辞进花辞树 on 2018/9/18.
 */
//全局变量
var mydata = { 
	status:"false", 
    treeArr : [],    //用来储存，点击取得树节点及其所有的祖先节点,显示在头部 
    clickArr : [],   //用来储存，点击添加节点 取得树节点及其所有的祖先节点ID 
    thisData:'',
    treeSetting :{   // 树的配置信息 
        data: {
            simpleData: {
                enable: true
            },
			key:{
		     	name:"dirName"
		    },
        }, 
        edit: {
        	enable: true,
			editNameSelectAll: true, 
			showRenameBtn: showRenameBtn,
			showRemoveBtn: showRemoveBtn
		},
        view: {
        	addHoverDom: addHoverDom,
			removeHoverDom: removeHoverDom, 
			selectedMulti: false
        },
        callback: {
            onClick: getDocumentTable,   
			beforeRemove: beforeRemove,
			beforeRename: beforeRename, 
			onRename: onRename
        }
    }
}  

var parameterArr = {};
parameterArr.arr = window.location.href.split("?");
parameterArr.parameterArr = parameterArr.arr[1].split(",");
parameterArr.obj = {};
for (var i = 0; i < parameterArr.parameterArr.length; i++) {
    var obj = parameterArr.parameterArr[i].split("=");
    parameterArr.obj[obj[0]] = obj[1];
} 
$(document).ready(function () {
	$(".headTitle").text( decodeURIComponent(parameterArr.obj.name) +"：资产库" )
    getTree();
    var testEditormdView2;
    testEditormdView2 = editormd.markdownToHTML("test-editormd-view2", {
        htmlDecode      : "style,script,iframe",  // you can filter tags decode
        emoji           : true,
        taskList        : true,
        tex             : true,  // 默认不解析
        flowChart       : true,  // 默认不解析
        sequenceDiagram : true,  // 默认不解析
    }); 
    //返回项目主页
    // $(".headReturn").on("click",function(){
    // 	//当你在iframe页面关闭自身时
    //     var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    //     parent.layer.close(index); //再执行关闭 
    // }); 
    //权限设置（按人员）
    $("#prePower").on("click",function(){ 
    	$(".headRight>.btn").css("display","none");
    	$("#return").css("display","inline-block");
    	$(".headTitle").text("权限配置"); 
    })
    //权限设置（按目录）
    $("#listsPower").on("click",function(){ 
    	$(".headRight>.btn").css("display","none");
    	$("#return").css("display","inline-block");
    	$(".headTitle").text("权限配置"); 
    	
    	var selectedNodes = $.fn.zTree.getZTreeObj("menuTree").getSelectedNodes();
    	
    	console.log( selectedNodes );
    })
     //返回 文档页面
    $("#return").on("click",function(){ 
    	$(".headRight>.btn").css("display","inline-block");
    	$("#return").css("display","none");
    	$(".headTitle").text( parameterArr.obj.name ); 
    })
    //上传文档
    $("#upDoc").on("click",function(){
    	$("#docTypes").selectpicker('value', '');
    	$("#updateForm")[0].reset();  
    	
    	$("#updateDoc").modal("show");
    })
    //选择关联信息
    $("#relInfo").on('click',function () {
        layer.open({
            type: 2,
            title: '选择关联信息',
            shadeClose: true,
            move: false,
            area: ['94%', '90%'],
            offset: "4%",
            shade: 0.3,
            tipsMore: true,
            anim: 2, 
            content:  '/projectManageui/assetRq/toAssociatedDemand',
            btn: ['确定','取消'] ,
            btnAlign: 'c', //按钮居中
            yes: function( index , layero){
                var body = layer.getChildFrame('body', index );
                var iframeWin = window[layero.find('iframe')[0]['name']];  //得到iframe页的窗口对象，执行iframe页的方法：
                var obj = iframeWin.getAssociatedDemandId(); 
                if( obj.status == true ){
                	mydata.search.reqArr = [];
                	var str = '';
                	obj.data.map(function (item){ 
                		mydata.search.reqArr.push(item.id); 
                		str += item.REQUIREMENT_NAME+",";
                	});
                	$("#relInfo").val( str );
                	layer.closeAll();
                } 
            },
            btn2:function(){
                layer.closeAll();
            }
        });
    }) 
    //选择开发任务
    $("#devTask").on('click',function () {
        layer.open({
            type: 2,
            title: '选择开发任务',
            shadeClose: true,
            move: false,
            area: ['94%', '90%'],
            offset: "4%",
            shade: 0.3,
            tipsMore: true,
            anim: 2, 
            content:  '/projectManageui/assetRq/toDevelopmentTask',
            btn: ['确定','取消'] ,
            btnAlign: 'c', //按钮居中
            yes: function( index , layero){
                var body = layer.getChildFrame('body', index );
                var iframeWin = window[layero.find('iframe')[0]['name']];  //得到iframe页的窗口对象，执行iframe页的方法：
                var obj = iframeWin.getDevTaskId(); 
                if( obj.status == true ){
                	mydata.search.taskArr = [];
                	var str = '';
                	obj.data.map(function (item){
                		mydata.search.taskArr.push(item.id); 
                		str += item.FEATURE_NAME+",";
                	});
                	$("#devTask").val( str );
                	layer.closeAll();
                }
            },
            btn2:function(){
                layer.closeAll();
            }
        });
    })
       
});
//菜单栏 
function getTree(){ 
	$.ajax({
        url:"/projectManage/documentLibrary/getDirectoryTree",
        type:"post",
        data:{ 
        	projectId  : parameterArr.obj.id, 
        },
        success:function(data){   
        	$.fn.zTree.init($("#menuTree"),  mydata.treeSetting , data  ); 
            fuzzySearch('menuTree','#treeSearchInput',null,true); //初始化模糊搜索方法
        },
        error:function(data){
        	 console.log( data );
        }
    })  
}
//点击判断 非需求目录 需求目录
function getDocumentTable(event, treeId, treeNode, clickFlag){ 
	mydata.thisData = "";
	if( treeNode.level == 0 ){
		$(".notRequirement>.title").css("display","none");
		$(".noData").css("display","block");
    	$(".myTableDiv").css("display","none"); 
	}else{
		//设置标题 
		mydata.thisData = treeNode;
		getDocumentType( treeNode );
		
		
		$("#title").css("display","block"); 
        mydata.treeArr = [];
        getParentNameArr( treeNode ); 
        $( "#title" ).text( mydata.treeArr.join(" > ") ); 
        
		$.ajax({
	        url:"/projectManage/documentLibrary/getDocumentFile",
	        type:"post",
	        data:{ 
	        	id : treeNode.id, 
	        },
	        success:function(data){ 
	        	if( treeNode.level == "0"  ){
	        		$(".noData").css("display","block");
	    	    	$(".myTableDiv").css("display","none"); 
	        	}else{
	        		if( data.type == "documents" ){
		        		$(".noData").css("display","none");
		    	    	$(".myTableDiv").css("display","block");
		    	    	console.log( data.data  )
		    	    	getTable( data.data )  
		        		  
		        	}else if( data.data.type == "markdowns" ){
		        		//富文本 或 markdown
		        		
		        	}else{
		        		$(".noData").css("display","none");
		    	    	$(".myTableDiv").css("display","block"); 
		    	    	getTable( [] )  
		        	}
	        	}  
	        },
	        error:function(data){
	        	 console.log( data );
	        }
	    }) 
	/*	getTable( documents ); */
		/*$(".notRequirement>.title").css("display","block");
		if( treeNode.docType == null ){       
	    	//非需求目录
	    	$(".noData").css("display","block");
	    	$(".myTableDiv").css("display","none");  
	    }else{ 
	    	$(".noData").css("display","none");
	    	$(".myTableDiv").css("display","block"); 
	    	    
	    }*/	
	} 
    /*
    //生成表格
   	$(".notRequirement").css("display","none");
   	$(".requirement").css("display","flex");
   */
}

// 非需求目录 --> 生成标题
function getParentNameArr( treeNode ){
    mydata.treeArr.unshift( treeNode.dirName )
    if( treeNode.level == 0 ){
        return ;
    }else{
        var parentObj = treeNode.getParentNode();
        getParentNameArr( parentObj );
    }
}
  
// 非需求目录 --> 形成表格
function getTable( data ) {  
    $("#loading").css('display','block');
    $("#documentList").bootstrapTable('destroy');
    $("#documentList").bootstrapTable({
    	data: data,
        method:"post",
        contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
        queryParamsType:"",
        pagination : false,
        sidePagination: "server",  
        columns : [{
            field : "id",
            title : "id",
            visible : false,
            align : 'center'
        },{
            field : "documentName",
            title : "文档名称",
            align : 'left',
            formatter : function(value, row, index) { 
            	var a;
            	var rows = JSON.stringify(row).replace(/"/g, '&quot;');
            	if( row.saveType == 1 ){  
                	var url = "/projectManage/assetsLibraryRq/downObject?documentS3Bucket=" + encodeURIComponent(row.documentS3Bucket) + "&documentS3Key=" + encodeURIComponent(row.documentS3Key) + "&documentName=" + encodeURIComponent(row.documentName); 
                	a = '<a class="a_style" href="'+ url +'" download="'+value+'">'+ value +'</a><span title="附件类文件，点击可以下载" class="downfilesIcon"></span>';
            	}else {
            		a = '<a class="a_style">'+ value +'</a><span title="Markdown文件，点击可以在线浏览编辑" class="marksIcon"></span>';
            	}
            	return  a;
            }
        },{
            field : "createDate",
            title : "创建信息",
            align : 'center',
            formatter : function(value, row, index) { 
            	return isValueNull(row.createUserName) + " "+ isValueNull(row.createDate);
            }
        },{
            field : "lastUpdateDate",
            title : "最后更新时间",
            align : 'center',
            formatter : function(value, row, index) { 
            	return isValueNull(row.updateUserName) + " "+ isValueNull(row.lastUpdateDate);
            }
        },{
            field : "opt",
            title : "操作",
            align : 'center',
            formatter : function(value, row, index) {
            	var rows = JSON.stringify(row).replace(/"/g, '&quot;');
            	return  '<a class="a_style" onclick="getHistory('+ rows +')">历史版本</a>'+' | '+'<a class="a_style" onclick="delDoc('+ rows +')">移除</a>'
            }
        }],
        onLoadSuccess:function(data){
            $("#loading").css('display','none');
        },
        onLoadError : function() {
            $("#loading").css('display','none');
            layer.alert("系统内部错误!", {
                icon: 2,
                title: "提示信息"
            });
        },
        onPageChange:function(){
            $("#loading").css('display','block');
        }
    });
} 
function delDoc( row ){
	
}
function getRelevanceInfo( row ){
	 layer.open({
        type: 2,
        title: '关联信息',
        shadeClose: true,
        move: false,
        area: ['94%', '90%'],
        offset: "4%",
        shade: 0.3,
        tipsMore: true,
        anim: 2,
        content: '/projectManageui/requirementPerspective/relevanceInfo?id=' + row.id,
        btn: ['关闭'] ,
        btnAlign: 'c', //按钮居中 
        btn:function(){
            layer.closeAll();
        }
    });
}
function getHistory( row ){  
	 layer.open({
        type: 2,
        title: '历史版本',
        shadeClose: true,
        move: false,
        area: ['94%', '90%'],
        offset: "4%",
        shade: 0.3,
        tipsMore: true,
        anim: 2,
        content: '/projectManageui/requirementPerspective/history?id=' + row.id,
        btn: ['关闭'] ,
        btnAlign: 'c', //按钮居中 
        btn:function(){
            layer.closeAll();
        }
    });
} 

//ztree树操作  
function beforeRemove(treeId, treeNode) { 
	var zTree = $.fn.zTree.getZTreeObj("menuTree"); 
	//询问框 
	layer.confirm('您是确定要删除？'+treeNode.dirName, {
	  btn: ['确定','取消'] //按钮
	}, function(){   
		var dataObj = {};
		var url = '';
		if( treeNode.level == "0" ){
			dataObj = {
					systemId:treeNode.systemId 
			};
			url = '/projectManage/systemPerspective/delSystemDirectoryBySystemId';
			zTree.removeChildNodes( treeNode );
		}else{
			dataObj = {
					id:treeNode.id 
			};
			url = '/projectManage/systemPerspective/delSystemDirectory';
			  zTree.removeNode( treeNode );
		}
		$.ajax({
	        url:url,
	        type:"post", 
	        data: dataObj,
	        success:function(data){   
	        	layer.alert("删除成功!", {
                    icon: 1,
                    title: "提示信息"
                });
	        },
	        error:function(data){
	        	layer.alert("系统内部错误!", {
                    icon: 2,
                    title: "提示信息"
                });
	        }
	    })  
		layer.closeAll();
	}, function(){
		layer.closeAll();
	});
	return false; 
} 
function beforeRename(treeId, treeNode, newName, isCancel) { 
	if (newName.length == 0) {
		setTimeout(function() {
			var zTree = $.fn.zTree.getZTreeObj("menuTree");
			zTree.cancelEditName(); 
			layer.alert("节点名称不能为空!", {
                icon: 0,
                title: "提示信息"
            });
		}, 0);
		return false;
	}else{
		$.ajax({
	        url:"/projectManage/systemPerspective/updateSystemDirectoryName",
	        type:"post", 
	        data: {
	        	id: treeNode.id,
	        	dirName: newName,
	        },
	        success:function(data){   
	        	layer.alert("重命名成功!", {
                    icon: 1,
                    title: "提示信息"
                });
	        },
	        error:function(data){
	        	layer.alert("系统内部错误!", {
                    icon: 2,
                    title: "提示信息"
                });
	        }
	    })
	}
	return true;
}
function onRename(e, treeId, treeNode, isCancel) { 
	console.log( 5 )
}  
function showRenameBtn(treeId, treeNode) { 
	if( treeNode.level == "0" ){
		return false;
	} else{
		return true;
	}
}
function showRemoveBtn(treeId, treeNode){
	if( treeNode.level == "0" ){
		return false;
	} else{
		if( treeNode.createType == "1" ){
			return false;
		} else{
			return true;
		}
	}
}
function addHoverDom(treeId, treeNode) {  
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
		+ "' title='add node' onfocus='this.blur();'></span>";
	sObj.after(addStr);
	var btn = $("#addBtn_"+treeNode.tId);
	 
	
	if (btn) btn.bind("click", function(){
		console.log( treeNode )
	 
		mydata.clickArr = []; 
		var dataObj = {};  
		if( treeNode.level == 0 ){
			dataObj.parentId = "0";
			dataObj.parentIds = "";
			dataObj.projectType = 2;
			dataObj.systemId = treeNode.systemId;
			dataObj.dirName = "新建文件夹";
		}else{
			getParentNameArrID( treeNode );
			dataObj.parentId = treeNode.id;
			dataObj.parentIds = mydata.clickArr.join(",");
			dataObj.systemId = treeNode.systemId;
			dataObj.projectType = 2;
			dataObj.dirName = "新建文件夹";
		}  
		$.ajax({
	        url:"/projectManage/systemPerspective/addSystemDirectory",
	        type:"post",
	       /* async: false,*/
	        data: dataObj,
	        success:function(data){   
	        	var zTree = $.fn.zTree.getZTreeObj("menuTree");
				zTree.addNodes(treeNode,data.data  ); 
	        },
	        error:function(data){
	        	 console.log( data );
	        }
	    })  
		return false;
	});
};
//获取祖先节点ID 
function getParentNameArrID( treeNode ){
    mydata.clickArr.unshift( treeNode.id )
    if( treeNode.level <= 1 ){
        return ;
    }else{
        var parentObj = treeNode.getParentNode();
        getParentNameArrID( parentObj );
    }
}
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_"+treeNode.tId).unbind().remove();
}; 

//获取文档类型
function getDocumentType( treeNode ){ 
	$.ajax({
        url:"/projectManage/documentLibrary/getDocumentTypes ",
        type:"post", 
        data: {
        	directoryId:treeNode.id,
        },
        success:function(data){  
        	$("#docTypes").empty();
        	$("#docTypes").append( "<option disabled selected hidden>请选择</option>" );
        	if( data.data ){
        		data.data.map(function(item){
           			$("#docTypes").append( "<option value="+ item.valueCode +">"+ item.valueName +"</option>" );
        		}) 
        	} 
        	$('.selectpicker').selectpicker('refresh'); 
        },
        error:function(data){
        	 console.log( data );
        }
    })  
}
/*上传文档*/
function uploadDoc(){
	var formData = new FormData();
	var files = $('#upfile')[0].files;
	console.log( files );
	for( var i=0;i<files.length;i++ ){ 
		formData.append('file', $('#upfile')[0].files[i] );
	}  
	formData.append('systemDirectoryId', mydata.thisData.id);
	formData.append('projectType', 2);
	formData.append('documentVersion', 1);
	formData.append('documentType2', $("#docTypes").val());
	formData.append('saveType',1);
	$.ajax({
        url:"/projectManage/documentLibrary/uploadNewDocument",
        type:"post", 
        contentType: false, 
		processData: false,
        data:formData,
        success:function(data){  
        	 console.log( data );
        },
        error:function(data){
        	 console.log( data );
        }
    })  
}

