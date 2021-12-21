var casePageSize=10;
var scrollflag=true;
var oddFileObj={
    "execute":[],
    "check":[],
    "commitBugUpfileTable":[],
}; 
var caseRows={};
var executeResult = '';

$(document).ready(function () {  
    getAllExecuteCase();
    banEnterSearch();
    $(".def_choice").on("click",function(){
    	if( $( this ).hasClass( "def_Selection" ) ){
    		$( this ).removeClass( "def_Selection" );
    		 getAllExecuteCase();
    	}else{
    		$( this ).addClass( "def_Selection" );
    		getAllExecuteCase();
    	}
    })
    /*$("#uploadFile").change(function(){
        getFile( 'upfileTable','execute',this);
    });
    $("#checkUploadFile").change(function(){
        getFile( 'checkUpfileTable','check',this);
    });
    $("#commitBugUploadFile").change(function(){
        getFile( 'commitBugUpfileTable','commitBugUpfileTable',this);
    }); */
}); 

/*function getFile( id, oddfileName ,This){
    var files = This.files;
    for(var i=0,len=files.length;i<len;i++) {
        var file = files[i];
        var flag=true;
        if (files[i].size <= 0) {
            layer.alert(file.name + "文件为空！", {icon: 0});
            continue;
        }
        for(var j=0;j<oddFileObj[oddfileName].length;j++){
            if(oddFileObj[oddfileName][j].name==file.name){
                flag=false;
                layer.alert(file.name +"文件名重复！",{icon:0});
                break;
            }
        }
        //文件读取
        if( flag ){
            (function (i) {
                var file = files[i];
                oddFileObj[oddfileName].push( files[i] );
                if ( window.FileReader ) {
                    var reader = new FileReader();
                    reader.readAsDataURL( file );
                    reader.onerror = function(e) {
                        e = e || window.event;
                        layer.alert("文件" + file.name +" 读取出现错误", {icon: 0});
                        return false;
                    };
                    reader.onload = function (e) {
                        e = e || window.event;
                        if( e.target.result ) {
                            console.log("文件 "+file.name+" 读取成功！");
                        }
                    };
                }
                var _tr = '';
                var file_type = file.name.split("\.")[1];
                var _td_icon;
                var _td_name = '<span >'+file.name+'</span><i class="file-url"></i><i class = "file-bucket"></i><i class = "file-s3Key"></i></div></td>';
                var _td_opt = '<td><a href="javascript:void(0);" class="del-fileBtn" onclick="delFile(this)">删除</a></td>';
                switch(file_type){
                    case "doc":
                    case "docx":_td_icon = '<img src="../static/images/file/word.png" />';break;
                    case "xls":
                    case "xlsx":_td_icon = '<img src="../static/images/file/excel.png" />';break;
                    case "txt":_td_icon = '<img src="../static/images/file/text.png" />';break;
                    case "pdf":_td_icon = '<img src="../static/images/file/pdf.png" />';break;
                    case "png":
                    case "jpeg":
                    case "jpg":_td_icon = '<img src="../static/images/file/img.png"/>';break;
                    default:_td_icon = '<img src="../static/images/file/other.png" />';break;
                }
                _tr+='<tr><td><div class="fileTb">'+_td_icon+'  '+_td_name+_td_opt+'</tr>';
                $("#"+id).append(_tr);
            })(i);
        }
    }
}*/
function commitBug(data,testCaseExecuteId){
	newDefect_reset();
	 defectAttIds = [];
	 $("#testTaskId").val(data.taskId);
	 
	 $("#new_defectSource").val(data.testStage);
	 $("#new_defectSource").attr("disabled","disabled");
	 $("#new_requirementCode").val(data.requirementCode);
	 $("#new_commissioningWindowId").val(data.commissioningWindowId);
	 $("#systemId").val(data.systemId);
	 $("#system_Name").val(data.systemName);
	 $("#testSetCaseExecuteId").val(testCaseExecuteId);
	 
	 $("#testcaseNumber").val(data.caseNumber);
	 
	 $("#testCaseName").val(data.caseName);
	 $("#testTaskName").val(data.taskName);
	$(".selectpicker").selectpicker('refresh');
    $("#commitBug").modal( "show" );
} 

 

function getAllExecuteCase(){
	 $("#loading").css('display','block');
	var num='';
	if( !$("#onlyDoing").hasClass("def_Selection") ){
		num=2;
	} 
	
	var own = 2;
	if( !$("#onlyDoing2").hasClass("def_Selection") ){
		own = 1;
	}
	
	$.ajax({
		url : "/testManage/testSet/getTaskTree",
	    method:"post",
		data:{ 
//			userId : $("#userId option:selected").attr("val"),  
			own : own,
			taskName : $("#checkTaskName").val() ,
			testSetName : $("#checkTestSetName").val(),
			requirementFeatureStatus: num, 
		},
	    success:function(data){   
	    	$("#taskTree").empty();
	    	 if(data.rows.length == 0){
				   $("#taskTree").html( '<div class="haveNoFont"><span>暂无测试任务...</span></div>' );
			   }else{
				   $(".haveNoFont").remove();
			   }
	    	 
	    	 for( var k=0;k<data.rows.length;k++  ){
	    		 var str='';
	    		 var testSetList1 = JSON.stringify( data.rows[k].testSetList ).replace(/"/g, '&quot;');
	    		 str+='<div class="testTask">'+
	    		 '<div class="taskTop" onclick="getAllTestSetByFeatureId(this ,'+testSetList1+')" val='+ data.rows[k].id +'><img src="../static/images/testExe/task.png"><span class="taskName">'+data.rows[k].featureCode+' '+data.rows[k].featureName+'</span></div>'+
	    		 '<div class="taskBottom" status="false" ><div class="taskContent"></div></div></div>';
	    		 $("#taskTree").append( str ); 
	    	 }
	    	 $("#loading").css('display','none');
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	})
}
function getAllTestSetByFeatureId( This, rows ){ 
	 $("#loading").css('display','block');
	if( $(This).parent().children(".taskBottom").css("display")==="none" ){
		if( $(This).parent().children(".taskBottom").attr("status")==="false" ){
			$(This).parent().children(".taskBottom").attr("status","true");
			for( var i=0;i< rows.length;i++ ){
	    		 var str='';
	    		 var noBorder='';
	    		 if( i == rows.length-1 ){
	    			 noBorder='noBorder';
	    		 }
	    		 var setCaseExprot=""; 
	    		 if(testCaseExprot==true){
	    			var Round=[];
	    			Round=rows[i].executeRound.split(",");
	    			 setCaseExprot='<div class="btn-group place_right"><span type="button" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">…</span>'+
	    		    '<ul class="dropdown-menu"><li></li><li><a onclick="getExcelAllTestSet( '+ rows[i].testSetId +',&apos;' + Round + '&apos;)">导出</a></li></ul></div>';
	    		 }
	    		 str+='<div class="testJob" val='+ rows[i].testSetId +'><div class="testName"><div class="testNameFont" onclick="oponCuteRound(this)" >'+ rows[i].testSetName+'</div>'+ setCaseExprot +'</div>'+
	    		 '<div class="testContent hideAll '+noBorder+'">';
	    		 var executeRound=rows[i].executeRound.split(",");
	    		// console.log( executeRound );
	    		 for( var j=0;j<executeRound.length;j++){ 
	    			 if(executeRound[j]<=rows[i].totalRound){
	    			 var noBorder2="";
	    			 if( j== executeRound.length-1 ){
	    				 noBorder2="noBorder";
	    			 }  
	    			 var valueData=JSON.stringify(  rows[i]  ).replace(/"/g, '&quot;');
	    			 var batchButton="";//批量执行
	    			 var roundExpory="";//轮次导出
	    			 
	    			 if(batchExecution==true){
	    				  batchButton='<li><a onclick="batchExecutivePage('+ valueData + ',' +executeRound[j]+ ')">批量执行</a></li>';
	    			 }
	    			 if(testCaseRoundExprot==true){
	    				 roundExpory='<li><a href="#" onclick="getExcelByExcuteRound( '+ rows[i].testSetId +',' +executeRound[j]+ ' )">导出</a></li>'; 
	    			 }
	    			 var buttonShow='<ul class="dropdown-menu">'+batchButton+roundExpory+'</ul>';
	    			 if(batchExecution==false&&testCaseRoundExprot==false){
	    				 buttonShow='';
	    			 }
	    			 str+='<div><div class="testContent_border"><div class="testName"><div class="testNameFont2"  onclick="oponCuteRound2(this)" val='+executeRound[j]+' >第'+executeRound[j]+'轮执行</div>'+
                         '<div class="btn-group place_right"><span type="button" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">…</span>'+
                         ''+buttonShow+''+
                         '</div></div></div><div class="testContent2 hideAll '+ noBorder2 +'" page="1" total="" onscroll="moreCase(this)" val="false" ></div></div>';
	    		 }
			} 
	    		 str+='</div></div>';
	    		 $(This).parent().children(".taskBottom").children(".taskContent").append( str );
	    	 } 
		} 
		$(This).parent().children(".taskBottom").css("display","block");
	}else{
		$(This).parent().children(".taskBottom").css("display","none");
	} 
	$("#loading").css('display','none');
}
function oponCuteRound( This ){
	 $("#loading").css('display','block');
	if( $(This).parent().parent().children(".testContent").hasClass("hideAll") ){
		$(This).css({"background-image":"url('../static/images/testSet/hide.png')","backgroundPosition":"1px 1px"});
		$(This).parent().parent().children(".testContent").removeClass("hideAll");
		 
	}else{
		$(This).css({"background-image":"url('../static/images/testSet/show.png')","backgroundPosition":"0 1px"});
		$(This).parent().parent().children(".testContent").addClass("hideAll");
	} 
	$("#loading").css('display','none');
}


function oponCuteRound2(This){
	 $("#loading").css('display','block');
	 $( This ).parent().parent().parent().children(".testContent2").empty();  
	if( $(This).parent().parent().parent().children(".testContent2").hasClass("hideAll") ){
		/*if( $(This).parent().parent().parent().children(".testContent2").attr("val")==='false' ){
			$(This).parent().parent().parent().children(".testContent2").attr("val","true"); */
			$.ajax({
				url : " /testManage/testSet/getTestSetCaseByTestSetId",
			    method:"post",
				data:{ 
					page: 1,
					pageSize: casePageSize,
					testSetId: $(This).parent().parent().parent().parent().parent().attr("val"),/*$(This).parent().parent().parent().parent().parent().attr("val")*/ 
					executeRound:  $(This).attr("val"),
					executeResult : $("#executeResult").val(),
				},
			    success:function(data){   
			    	 $(This).parent().parent().parent().children(".testContent2").attr( "total", data.total );
			    	 for( var i=0;i<data.rows.length;i++ ){ 
			    		 var obj = JSON.stringify( data.rows[i] ).replace(/"/g, '&quot;'); 
			    		 var str='';
			    		 var casebg='';
			    		 if( data.rows[i].caseExecuteResult==2 || data.rows[i].caseExecuteResult==3 ){
			    			 casebg='casebg2';
			    		 }else if( data.rows[i].caseExecuteResult==4 ){
			    			 casebg='casebg4';
			    		 }else if( data.rows[i].caseExecuteResult==5 ){
			    			 casebg='casebg5';
			    		 }else{
			    			 casebg='casebg1';
			    		 } 
			    		 var edit="";
			    		 if(testCaseStepEdit==true){
	  		    		 edit='<div class="btn-group place_right placeBottom"><span type="button" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">…</span>'+
  		    			  '<ul class="dropdown-menu"><li><a onclick="editCaseBtn('+ data.rows[i].caseId +')">编辑</a></li></ul>'+
  		    			  '</div>'
	  		    			}
			    		
			    		 str+='<div class="oneCase">'+
  		    			  '<div class="caseTitle '+ casebg +'">'+
  		    			  '<div class="caseTitleLeft" onclick="getAllExecuteCases(this,'+obj+')">'+  data.rows[i].caseNumber+' '+data.rows[i].caseName+'</div>'+edit+''+
  		    			  '</div><div class="caseBody"></div></div>';
			    		 $(This).parent().parent().parent().children(".testContent2").append( str );
			    	 }
			    	 
			    },
			    error:function(XMLHttpRequest, textStatus, errorThrown){ 
					layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
				}
			}) 
		/*} */ 
		$(This).css({"background-image":"url('../static/images/testSet/hide.png')","backgroundPosition":"1px 1px"});
		$(This).parent().parent().parent().children(".testContent2").removeClass("hideAll");
		
	}else{
		$(This).css({"background-image":"url('../static/images/testSet/show.png')","backgroundPosition":"0 1px"});
		$(This).parent().parent().parent().children(".testContent2").addClass("hideAll");
	}
	$("#loading").css('display','none');
}

function moreCase( This ){ 
	viewH =$(This).height(),//可见高度
    contentH =$(This).get(0).scrollHeight,//内容高度
    scrollTop =$(This).scrollTop();//滚动高度
    if(contentH - viewH - scrollTop <= 12) { //到达底部5px时,加载新内容 
    	if( scrollflag ){   
    		scrollflag=false; 
    		if( $( This ).attr("page")*casePageSize < $( This ).attr("total") ){ 
    			$( This ).attr("page" , Number( $( This ).attr("page") )+1 ); 
            	$.ajax({
        			url : " /testManage/testSet/getTestSetCaseByTestSetId",
        		    method:"post",
        			data:{ 
        				page: $( This ).attr("page"),
        				pageSize: casePageSize,
        				testSetId: $(This).parent().parent().parent().attr("val") ,
        				executeRound: $(This).parent().children(".testContent_border").children(".testName").children(".testNameFont2").attr("val")
        			},
        		    success:function(data){   
        		    	 $(This).attr( "total", data.total );
        		    	 for( var i=0;i<data.rows.length;i++ ){
        		    		 var obj = JSON.stringify( data.rows[i] ).replace(/"/g, '&quot;');
        		    		 var str='';
        		    		 var casebg='';
        		    		 if( data.rows[i].caseExecuteResult==2 || data.rows[i].caseExecuteResult==3 ){
        		    			 casebg='casebg2';
        		    		 }else if( data.rows[i].caseExecuteResult==4 ){
        		    			 casebg='casebg4';
        		    		 }else if( data.rows[i].caseExecuteResult==5 ){
        		    			 casebg='casebg5';
        		    		 }else{
        		    			 casebg='casebg1';
        		    		 }
        		    		 
        		    		 str+='<div class="oneCase">'+
	   		    			  '<div class="caseTitle '+ casebg +'">'+
	   		    			  '<div class="caseTitleLeft" onclick="getAllExecuteCases(this,'+obj+')">'+  data.rows[i].caseNumber+' '+data.rows[i].caseName+'</div>'+
	   		    			  '<div class="btn-group place_right placeBottom"><span type="button" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">…</span><ul class="dropdown-menu"><li><a onclick="editCaseBtn('+ data.rows[i].caseId +')">编辑</a></li></ul></div>'+
	   		    			  '</div><div class="caseBody"></div></div>'; 
        		    		 $(This).append( str );
        		    		 scrollflag=true;
        		    	 }
        		    },
        		    error:function(XMLHttpRequest, textStatus, errorThrown){ 
        				layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
        			}
        		}) 
    		} else{
    			 scrollflag=true;
    		}
    	}  
    }   
}
/*function cleanImplement(){
	$("#excuteRemark").val("");
	var obj=$("#objData").val();
	CaseStepsTable( obj ); //修改表格数据
}*/

function getAllExecuteCases( This , obj ){
	 $("#loading").css('display','block');
	 var thiss= $(This).parent().parent().parent().parent().children(".testContent_border").find(".testNameFont2");
	 $(".right_blank").css("display","none");
	 $(".right").css("display","block");
	 
	 $("#newUpfileTable").empty();
	 $("#excuteRemark").val("");
	 $("#files").val("");
	 $("#testSetCaseId").val("");
	 $("#testSetCaseId").val(obj.caseId);
	 _files = [];
	//$("#objData").val(JSON.stringify(obj));
	var myData={  
		testSetId : $( This ).parent().parent().parent().parent().parent().parent().attr( "val" ) ,
		caseNumber : obj.caseNumber,
		excuteRound :  $( This ).parent().parent().parent().parent().children(".testContent_border").find('.testNameFont2').attr( "val" ),
	};  
	caseRows={};
	caseRows.obj=obj;
	caseRows.excuteRound=myData.excuteRound;
	caseRows.testSetId=myData.testSetId;
	modalType ='new';
	myData=JSON.stringify(myData);
	$.ajax({
		url : "/testManage/testExecution/getAllExecuteCase",
	    method:"post",
		data:{
			"myData":myData 
		},
	    success:function(data){ 
	    	//console.log( data );
	    	 $( This ).parent().parent( ".oneCase" ).children(".caseBody").empty();
	    	 for( var i=0;i<data.length;i++){
	    		 var str='';
	    		 var status='';
	    		 var font=''
	    		 if( data[i].caseExecuteResult==2 ){
	    			 status='caseSuccess';
	    			 font='执行成功';
	    		 }else if( data[i].caseExecuteResult==3 ){
	    			 status='caseFail';
	    			 font='执行失败';
	    		 }else if( data[i].caseExecuteResult==4 ){
	    			 continue;
	    		 }else if( data[i].caseExecuteResult==5 ){
	    			 continue;
	    		 }else{
	    			 status='caseFail'; 
	    		 } 
	    		 var testCaseSeeButton='';
	    		 if(testCaseSee==true){
	    			 testCaseSeeButton='getExecuteCaseDetails( '+data[i].id+','+obj.caseId+' )'; 
	    		 }
	    		 str+='<div class="caseStatus '+ status +'" onclick="'+testCaseSeeButton+'">'+
	    		 font + ' ' + data[i].resultName + ' | '+ data[i].createDate + 
		         '</div>'; 
	    		 $( This ).parent().parent( ".oneCase" ).children(".caseBody").append( str );
	    	 } 
	 	    $("#loading").css('display','none');
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}

	})
	
	testSetId = $( This ).parent().parent().parent().parent().parent().parent().attr( "val" ) ;
	caseNumber = obj.caseNumber;
	//查询案例执行结果
	getCaseExecuteResult(testSetId,caseNumber);
	
	//查询案例(挂起和撤销)执行记录
	getCaseExecute(testSetId,caseNumber);
	
	//清空
	$( ".rightCaseDivNew .right_div_titFont" ).html( '' )   //清空右边案例的标题
	$( "#newCasePrecondition" ).html( '' )   				//清空右边案例的前置条件
    $( "#expectResult").html( '' );
    $( "#inputData").html( '' );
    $( "#testPoint").html( '' );
    $( "#moduleName").html( '' );
    $( "#businessType").html( '' );
	closeSee();
	 if(testCaseExecution==true){
		 $( ".rightCaseDivNew" ).css( "display","block" ); //显示右边的案例div
	 }
	
	/*$( ".rightCaseDivEdit" ).css( "display","none" ); //关闭编辑
	$( ".rightCaseDivCheck" ).css( "display","none" );//关闭查看
*/	$( ".rightCaseDivNew .right_div_titFont" ).html( obj.caseNumber + " " + obj.caseName + "  " + executeResult)   //修改右边案例的标题
	$( "#newCasePrecondition" ).html( obj.casePrecondition )  //修改右边案例的前置条件
    $( "#expectResult").html(obj.expectResult);
    $( "#inputData").html(obj.inputData);
    $( "#testPoint").html(obj.testPoint);
    $( "#moduleName").html(obj.moduleName);
    $( "#businessType").html(obj.businessType);
	CaseStepsTable( obj.caseId ); //修改表格数据
	
}

function getCaseExecuteResult(testSetId,caseNumber){
	$.ajax({
		url : "/testManage/testExecution/getCaseExecuteResult",
	    method:"post",
	    async:false,
		data:{
			'testSetId':testSetId,
			'caseNumber':caseNumber
		},
	    success:function(data){
	    	executeResult = data.executeResult;
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	})
}

function getCaseExecute(testSetId,caseNumber){
	$.ajax({
		url : "/testManage/testExecution/getCaseExecute",
	    method:"post",
	    async:false,
		data:{
			'testSetId':testSetId,
			'caseNumber':caseNumber
		},
	    success:function(data){
	    	$("#checkExcuteRemark2").empty();
	    	if (data.status == 2){
                layer.alert(data.errorMessage, {
                    icon: 2,
                    title: "提示信息"
                });
            } else if(data.status == 1){
            	if(data.data != null){
            		for( var i=0;i<data.data.length;i++ ){
            			str = "<div class='oneHistory'><span class='fontWeihgt'>执行人:</span>"+data.data[i].executeUserName+"  "+data.data[i].createDate+"<br>"
            			+"<span class='fontWeihgt'>执行结果:</span>"+data.data[i].executeResult+"<br>"
            			+"<span class='fontWeihgt'>备注:</span>"+data.data[i].excuteRemark+"<br></div>" 
            			$("#checkExcuteRemark2").append(str);
            		}
            	}
            }
	    	
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	})
}


function CaseStepsTable( caseId ){ 
	$("#CaseSteps").bootstrapTable('destroy');
    $("#CaseSteps").bootstrapTable({
    	url:"/testManage/testExecution/getTestCaseRun",
		method:"post",
		contentType : 'application/x-www-form-urlencoded; charset=UTF-8',  
        queryParamsType : "",  
        sidePagination: "server",   
        queryParams : function(params) { 
            var param = { 
            	id : caseId,  
            }; 
            return param;
        },
        columns : [{
            field : "id",
            title : "id",
            visible :  false,
            align : 'center'
        },{
            field : "stepOrder",
            title : "步骤",
            align : 'center',
            width : 50,
        },{
            field : "stepDescription",
            title : "步骤描述",
            width : 300,
            align : 'center'
        },{
            field : "stepExpectedResult",
            title : "预期结果",
            align : 'center'
        },{
            field : "stepExecuteResult",
            title : "执行结果",
            align : 'center',
            width : 100,
            class : "stepExecuteResult",
            formatter : function(value, row, index){ 
            	var str='<div class="def_tableDiv"><select class="selectpicker executionSelect" name="envSelect" >';  
            	str+=' </select></div>';
            	return str;
            }
        },{
            field : "stepActualResult",
            title : "执行实际情况",
            align : 'center',
            class : "stepActualResult",
            formatter : function(value, row, index){
            	var str='<div class="def_tableDiv2"><input class="form-control def_tableInput showBlock"  type="text" value=""></div>';
            	return str;
            }
        }],
        onLoadSuccess:function(){ 
        	$( ".def_tableDiv .executionSelect" ).append( $("#executionSelect").find("*").clone(true) );
        	$(".selectpicker").selectpicker('refresh');
        },
        onLoadError : function(){
        }
    });
}
function showEditCase(){ 
	
	editCaseBtn($("#testSetCaseId").val());
}

function getTestCaseStep( type ){
	
	 var tableData=$("#CaseSteps").bootstrapTable('getData'); 
	 var newTableData=[];
	 for( var i=0;i<tableData.length;i++ ){
		 var rows = tableData[i]; 
		 rows.stepExecuteResult=$("#CaseSteps>tbody>tr .stepExecuteResult").eq( i ).find("select").val();
		 rows.stepActualResult=$("#CaseSteps>tbody>tr .stepActualResult").eq( i ).find(".def_tableInput").val();
		 newTableData.push( rows );
	 }
	 var files=$("#files").val();
	 var newTableData=JSON.stringify( newTableData ); 
	 if(type==0){
		 
		 $.ajax({
			 url:"/testManage/testExecution/insetSetStepExecute",
			 method:"post", 
			 data:{
				 "testSetId": caseRows.testSetId,
				 "excuteRound":caseRows.excuteRound,
				 "rows":JSON.stringify(caseRows.obj),
				 "enforcementResults":newTableData,
				 "type":3,
				 "files":files,
				 "excuteRemark": $("#excuteRemark").val(), 
			 }, 
			 success:function(data){  
				 layer.confirm("执行成功,确认要提交缺陷吗？",{
					    btn: ['确定','取消'], //按钮
					    title: "提示信息"
					},function(){
					    layer.closeAll('dialog');

					    commitBug(caseRows.obj,data.testCaseExecuteId);
					},function(){
					    layer.closeAll('dialog');
		                 getAllExecuteCase();
		                 closeClose();
					})
			 }, 
			 error:function(XMLHttpRequest, textStatus, errorThrown){ 
				 layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
			 }
		 }); 
	 }else{
		 if(type==2){
			 for(var j=0;j<tableData.length;j++){
				if( tableData[j].stepExecuteResult==3){
					 layer.alert("案例步骤有失败步骤,不能通过",{icon : 2});
					return false;
				} 
			 }
		 }
		 $.ajax({
			 url:"/testManage/testExecution/insetSetStepExecute",
			 method:"post", 
			 data:{
				 "testSetId": caseRows.testSetId,
				 "excuteRound":caseRows.excuteRound,
				 "rows":JSON.stringify(caseRows.obj),
				 "enforcementResults":newTableData,
				 "type":type,
				 "files":files,
				 "excuteRemark": $("#excuteRemark").val(), 
			 }, 
			 success:function(data){   
				 if( data.status == 1 ){
					 layer.alert("操作成功",{icon : 1});
					 getAllExecuteCase();
				 }else{
					 layer.alert("操作失败",{icon : 2}); 
				 }
				 closeClose();
				// $( ".rightCaseDivNew" ).css( "display","none" );
				// cleanImplement();
			 },
			 error:function(XMLHttpRequest, textStatus, errorThrown){ 
				 layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
			 }
		 });
	 }
	
	  
}
//跳转编辑页面方法
function editCaseBtn( caseId ){
	$("#loading").css('display','block');
	 $(".right_blank").css("display","none");
	 $(".right").css("display","block");
	 
	$.ajax({
		url : "/testManage/testExecution/getUpdateCase",
	    method:"post",
		data:{
			"testSetCaseId" : caseId
		},
	    success:function(data){   
	    	//根据返回参数打印到页面上 
			$("#editForm").data('bootstrapValidator').destroy(); 
		    formValidator();
		    
	    	$("#editHideId").val( caseId );//关联任务
	    	
	    	$("#caseNumber").val( data.CASE_NUMBER );//案例编号
	    	
	    	$("#editTestTaskName").attr( 'idValue',data.TEST_TASK_ID );//关联任务
	    	$("#editTestTaskName").val( data.TEST_TASK_NAME );
	    	 
	    	$("#editSystemName").attr( 'idValue',data.SYSTEM_ID );//涉及系统
	    	$("#editSystemName").val( data.SYSTEM_NAME );
	    	
	    	$("#editCaseName").val( data.CASE_NAME );//案例名称 
	    	$("#editCasePrecondition").val( data.CASE_PRECONDITION ); //前置条件 

            $("#edit_expectResult").val(data.expectResult);
            $("#edit_inputData").val(data.inputData);
            $("#edit_testPoint").val(data.testPoint);
            $("#edit_moduleName").val(data.moduleName);
            $("#edit_businessType").val(data.businessType);
	    	//形成表格数据
	    	createEditTable( data.caseStep ); 
	    	closeSee();
	    	//$( ".right_div" ).css( "display","none" ); //显示右边的案例div
	    	$( ".rightCaseDivEdit" ).css( "display","block" ); //显示右边的案例div 
	    	$("#loading").css('display','none');
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	}); 
}

//关闭页面
function closeSee(){
	$( ".rightCaseDivCheck" ).css( "display","none" ); 
	$( ".right_div" ).css( "display","none" );
	$( ".rightCaseDivEdit" ).css( "display","none" ); 
	$( ".rightCaseDivNew" ).css( "display","none" ); 
}
function closeClose(){
	$( ".right" ).css( "display","none" ); 
	$( ".rightCaseDivCheck" ).css( "display","none" ); 
	$( ".right_div" ).css( "display","none" );
	$( ".rightCaseDivEdit" ).css( "display","none" ); 
	$( ".rightCaseDivNew" ).css( "display","none" ); 
	$(".right_blank").css("display","block");
	$( ".right_alert" ).css( "display","block" );
}

function createEditTable( data ){ 
	$("#editCaseSteps").bootstrapTable('destroy');
    $("#editCaseSteps").bootstrapTable({
        queryParamsType:"",
        pagination : false, 
        data:data,
        columns : [{
        	field : "id",
            title : "id",
            class :  "hideCaseID",
            align : 'center'
        },{
            field : "stepOrder",
            title : "步骤",
            align : 'center',
            class : "stepOrder",
            width : '50px',
        },{
        	field : "stepDescription",
            title : "步骤描述",
            align : 'center',
            formatter : function (value, row, index) { 
                return '<div class="def_tableDiv2"><textarea  style="resize:none;" class="form-control def_tableInput showBlock">'+row.stepDescription+'</textarea></div>';
            }
        },{
            field : "stepExpectedResult",
            title : "预期结果",
            align : 'center',
            formatter : function (value, row, index) { 
                return '<div class="def_tableDiv2"><textarea  style="resize:none;" class="form-control def_tableInput showBlock">'+row.stepExpectedResult+'</textarea></div>';
            }
        },{
            field : "操作",
            title : "操作",
			align : 'center',
			class : "handleBtn",
            width : '210px',
            formatter : function (value, row, index) { 
                var str='<a class="a_style" style="cursor:pointer" onclick="addEditTableRow( this )">增加下一步骤 </a> | '+
                        '<a class="a_style defColor" style="cursor:pointer" onclick="delEditTableRow(this)">删除</a> | '+
                        '<a class="a_style" style="cursor:pointer" onclick="upTableRow(this)">上移</a> | '+
                        '<a class="a_style" style="cursor:pointer" onclick="downTableRow(this)">下移</a>';
                return str;
            }
        }],
        onLoadSuccess:function(){
        },
        onLoadError : function(){
        }
    });  
    if( data.length==0 ){
    	$("#editCaseSteps>tbody").empty();
    }
}


//表格四种操作
function addSteps(){
	var i= $("#editCaseSteps>tbody>tr").length + 1;
	var str='<tr><td class="hideCaseID" style="text-align: center;"></td><td class="stepOrder" style="text-align: center; width: 50px;line-height:84px;">'+ i +'</td> <td style="text-align: center; "><div class="def_tableDiv2"><textarea  style="resize:none;" class="form-control def_tableInput showBlock" value=""></textarea></div></td> <td style="text-align: center; "><div class="def_tableDiv2"><textarea  style="resize:none;" class="form-control def_tableInput showBlock" value=""></textarea></div></td> <td style="text-align: center; width: 210px;line-height:84px;"><a class="a_style" style="cursor:pointer" onclick="addEditTableRow( this )">增加下一步骤 </a> | <a class="a_style defColor" style="cursor:pointer" onclick="delEditTableRow(this)">删除</a> | <a class="a_style" style="cursor:pointer" onclick="upTableRow(this)">上移</a> | <a class="a_style" style="cursor:pointer" onclick="downTableRow(this)">下移</a></td> </tr>';
	$("#editCaseSteps>tbody").append( str );
	changeStepOrder();
}
function addEditTableRow( This ){ 
	var i= $("#editCaseSteps>tbody>tr").length + 1;
	var str='<tr><td class="hideCaseID" style="text-align: center;"></td><td class="stepOrder" style="text-align: center; width: 50px;line-height:84px;">'+ i +'</td> <td style="text-align: center; "><div class="def_tableDiv2"><textarea  style="resize:none;" class="form-control def_tableInput showBlock" value=""></textarea></div></td> <td style="text-align: center; "><div class="def_tableDiv2"><textarea  style="resize:none;" class="form-control def_tableInput showBlock" value=""></textarea></div></td> <td style="text-align: center; width: 210px;line-height:84px;"><a class="a_style" style="cursor:pointer" onclick="addEditTableRow( this )">增加下一步骤 </a> | <a class="a_style defColor" style="cursor:pointer" onclick="delEditTableRow(this)">删除</a> | <a class="a_style" style="cursor:pointer" onclick="upTableRow(this)">上移</a> | <a class="a_style" style="cursor:pointer" onclick="downTableRow(this)">下移</a></td> </tr>';
	$( This ).parent().parent().after( str ); 
	changeStepOrder()
}
function delEditTableRow( This ) { 
    $( This ).parent().parent().remove();
    changeStepOrder();
}
function upTableRow( This ){
    var $tr = $(This).parents("tr");
    if ($tr.index() == 0){
        alert("首行数据不可上移");
    }else{
    	$tr.fadeOut(200).fadeIn(100).prev().fadeOut(100).fadeIn(100); 
        $tr.prev().before($tr);
        changeStepOrder();
    }
} 
function downTableRow( This ){
    var $tr = $(This).parents("tr");
    if ( $tr.index() == $( "#editCaseSteps>tbody>tr" ).length - 1) {
        alert("最后一条数据不可下移");
    }else {
        $tr.fadeOut(200).fadeIn(100).next().fadeOut(100).fadeIn(100); 
        $tr.next().after($tr);
        changeStepOrder();
    }
}
//动态改变表格步骤值
function changeStepOrder(){ 
	for( var i=0;i<$("#editCaseSteps>tbody>tr").length ;i++){ 
		$( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".stepOrder" ).text( ( i + 1) );   
	}
}



//保存编辑页面信息
function UpdateCaseStep(){
	$("#loading").css('display','block');
	$('#editForm').data('bootstrapValidator').validate();  
	if( !$('#editForm').data("bootstrapValidator").isValid() ){
		return;
	}  
	var testCaseStep = []
	for( var i=0;i<$( "#editCaseSteps>tbody>tr" ).length;i++ ){
		var obj={}
		obj.id=$( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".hideCaseID" ).text();
		obj.stepOrder=$( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".stepOrder" ).text();
		if( $( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".def_tableInput" ).eq(0).val()=='' ){
			layer.alert("表格数据不能为空，请检查",{icon : 0});
			return;
		}else{
			obj.stepDescription=$( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".def_tableInput" ).eq(0).val();
		}
		if( $( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".def_tableInput" ).eq(1).val()=='' ){
			layer.alert("表格数据不能为空，请检查",{icon : 0}); 
			return;
		}else{
			obj.stepExpectedResult=$( "#editCaseSteps>tbody>tr" ).eq( i ).find( ".def_tableInput" ).eq(1).val(); 
		} 
		testCaseStep.push( obj ); 
	} 
	
	testCaseStep=JSON.stringify(testCaseStep);
	var data=JSON.stringify({ 
		'id': $("#editHideId").val(),
		'testTaskId': $( "#editTestTaskName" ).attr( 'idvalue' ),
		'systemId': $( "#editSystemName" ).attr( 'idvalue' ),
		'caseName': $( "#editCaseName" ).val(),
		'caseNumber': $( "#caseNumber" ).val(),
		'casePrecondition': $( "#editCasePrecondition" ).val(),
        'expectResult':$('#edit_expectResult').val(),
        'inputData':$('#edit_inputData').val(),
        'testPoint':$('#edit_testPoint').val(),
        'moduleName':$('#edit_moduleName').val(),
        'businessType':$('#edit_businessType').val(),
	}) ;
	
	$.ajax({
		url : "/testManage/testExecution/UpdateCaseStep",
	    method:"post",
		data:{
			"testSetCase":data,
			'testCaseStep':testCaseStep
		},
	    success:function(data){   
	    	if( data.status == 1 ){
				 layer.alert("保存成功",{icon : 1});
				 getAllExecuteCase();
			 }else{
				 layer.alert("保存失败",{icon : 2}); 
			 }
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	});  
	closeSee();
	$("#loading").css('display','none');
}
function getExecuteCaseDetails(  id,setId ){
	 $(".right_blank").css("display","none");
	 $(".right").css("display","block");
	$("#checkUpfileTable").empty();
	$("#loading").css('display','block');
	$.ajax({
		url : "/testManage/testExecution/getExecuteCaseDetails",
	    method:"post",
		data:{
			"testSetCaseExecuteId":id,
			'testSetCaseId':setId
		},
	    success:function(data){
	    	modalType ='seeFile';
	    	if( data.listCase.caseExecuteResult==2 ){
	    		$(".successBg").css( "display","inhert" );
	    		$(".failBg").css( "display","none" );
	    	}else if( data.listCase.caseExecuteResult==3 ){
	    		$(".successBg").css( "display","none" );
	    		$(".failBg").css( "display","inhert" );	
	    	}
	    	$( "#checkTitle" ).html(  data.listCase.caseNumber +" "+ data.listCase.caseName  );  
	    	$( "#checkNameAndTime" ).html( data.userName +" | "+ data.listCase.createDate );
	    	$( "#checkCasePrecondition" ).html( data.listCase.casePrecondition );
	    	$( "#checkExcuteRemark" ).html( data.listCase.excuteRemark );

            $( "#check_expectResult" ).html( data.listCase.expectResult );
            $( "#check_inputData" ).html( data.listCase.inputData );
            $( "#check_testPoint" ).html( data.listCase.testPoint );
            $( "#check_moduleName" ).html( data.listCase.moduleName );
            $( "#check_businessType" ).html( data.listCase.businessType );
	    	
	    	$( "#checkRelatedDefects" ).empty();
	    	for( var i=0;i<data.listDefect.length;i++ ){
	    		var str='';
	    		str+='<div class=rowdiv><div class="def_col_4">'; 
	    		
	    		for( var j=0;j<$( "#relatedDefectsType option" ).length;j++ ){ 
	    			if( data.listDefect[i].defectType==$( "#relatedDefectsType option" ).eq( j ).val() ){
	    				str+='<span class="classColor'+data.listDefect[i].defectType+'">'+ $( "#relatedDefectsType option" ).eq( j ).text() +'</span>'+
	    				'<span class="classColor'+data.listDefect[i].defectType+'">P'+ $( "#relatedDefectsType option" ).eq( j ).val() +'</span>';
	    			}
	    		}
	    		str+=' </div><div class="def_col_6 defectCode" title="' + data.listDefect[i].defectCode +'">' + data.listDefect[i].defectCode + 
	    		'</div><div class="def_col_26 fontWrop">' + data.listDefect[i].defectSummary + '</div></div>';
	    		$( "#checkRelatedDefects" ).append( str );
	    	}    
	    	
	    	$("#checkCaseSteps").bootstrapTable('destroy');
	        $("#checkCaseSteps").bootstrapTable({
	            queryParamsType:"",
	            pagination : false,
	            data:data.listStep,
	            sidePagination: "server",
	            contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
	            columns : [{
	                field : "id",
	                title : "id",
	                visible : false,
	                align : 'center'
	            },{
	                field : "stepOrder",
	                title : "步骤",
	                width:50,
	                align : 'center'
	            },{
	                field : "stepDescription",
	                title : "步骤描述",
	                width:300,
	                align : 'center'
	            },{
	                field : "stepExpectedResult",
	                title : "预期结果",
	                align : 'center'
	            },{
	                field : "stepExecuteResult",
	                title : "执行结果",
	                width:100,
	                align : 'center',
	                	 formatter : function (value, row, index) {
	     	            	for( var j=0;j<$( "#executionSelect option" ).length;j++ ){ 
	     	            		if( row.stepExecuteResult==$( "#executionSelect option" ).eq( j ).val() ){
	     	            			return  '<span class="spanClass'+row.stepExecuteResult+'">'+$( "#executionSelect option" ).eq( j ).text()+'</span>';
	     	            		}
	     	            	}
	     	            }   	
	            },{
	                field : "stepActualResult",
	                title : "执行情况",
	                align : 'center'
	            }],
	            onLoadSuccess:function(){
	            },
	            onLoadError : function(){
	            }
	        }); 
	        if((data.listFile).length>0){
	    		var _table=$("#checkUpfileTable");
	    		var attMap=data.listFile;
	    		//var _table=$(this).parent(".file-upload-select").next(".file-upload-list").children("table");
	    		for(var i=0 ;i<attMap.length;i++){
	    			var _tr = '';
	    			var file_name = attMap[i].fileNameOld;
	    			var file_type = attMap[i].fileType;
	    			var file_id =  attMap[i].id;
	                var attache = JSON.stringify(attMap[i]).replace(/"/g, '&quot;');
	    			var _td_icon;
	    			//<i class="file-url">'+data.attachements[i].filePath+'</i>
	    			var _td_name = '<span>'+file_name+'</span><i class = "file-bucket">'+attMap[i].fileS3Bucket+'</i><i class = "file-s3Key">'+attMap[i].fileS3Key+'</i></td>';
	    			//var _td_opt = '<td><a href="javascript:void(0);" class="del-file-button" onclick="deleteAttachements(this,'+attache+')" style="color:red">删除</a></td>';
	    			switch(file_type){
	    				case "doc":
	    				case "docx":_td_icon = '<img src="'+_icon_word+'" />';break;
	    				case "xls":
	    				case "xlsx":_td_icon = '<img src='+_icon_excel+' />';break;
	    				case "txt":_td_icon = '<img src="'+_icon_text+'" />';break;
	    				case "pdf":_td_icon = '<img src="'+_icon_pdf+'" />';break;
	    				default:_td_icon = '<img src="'+_icon_word+'" />';break;
	    			}
	    			var row =  JSON.stringify( attMap[i]).replace(/"/g, '&quot;');
	    			_tr+='<tr><td><div class="fileTb" style="cursor:pointer" onclick ="download('+row+')">'+_td_icon+" "+_td_name+'</tr>'; 
	    			_table.append(_tr); 
	    			_seefiles.push(data.listFile[i]);
	    			$("#seeFiles").val(JSON.stringify(_seefiles));
	    		}
	    	}
	    	$( ".right_div" ).css( "display","none" ); //显示右边的案例div
	    	$( ".rightCaseDivCheck" ).css( "display","block" ); //显示右边的案例div  
	    	$("#loading").css('display','none');  
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	});
	
	
}
function batchExecutivePage( obj , excuteRound ){  
	 $("#exeExecuteState").selectpicker('val', '');
	 $(".right_blank").css("display","none");
	 $(".right").css("display","block");
	 
	 $( "#executeHiddenID" ).val("");
	 $( "#exeExcuteRound" ).val( "" );
	 $( "#exeTitleName" ).html( "" );
	$( "#executeHiddenID" ).val( obj.testSetId );
	$( "#exeExcuteRound" ).val( excuteRound );
	 
	$( "#exeTitleName" ).html( obj.testSetName );
	
	$( "#excuteRound" ).val( "" );
	$( "#excuteRound" ).val(excuteRound );
	initExecutionTable();
	
	$( ".right_div" ).css( "display","none" ); //显示右边的案例div
	$(".batchExecutivePage").css("display","block");  
}  

function initExecutionTable(){
	$("#executionTable").bootstrapTable('destroy');
    $("#executionTable").bootstrapTable({
    	url:'/testManage/testExecution/getTestCaseExecute', 
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
        		 testSetId : $( "#executeHiddenID" ).val(),
        		 excuteRound:$( "#excuteRound" ).val(),
        		 caseExecuteResultList :JSON.stringify(  $("#exeExecuteState").val() ), 
	    		 pageNumber:params.pageNumber,
	    		 pageSize:params.pageSize, 
         	 }
         	 return param;
	    },
        columns : [{ 
	            checkbox : true, 
	        },{
	        	field : "id",
	            title : "id", 
	            visible :  false,
	            align : 'center'
	        },{
	            field : "excuteRound",
	            title : "测试轮次",
	            align : 'center',
	            width : "100px",
	            formatter : function (value, row, index) {
	            	var  str="第"+ $("#exeExcuteRound").val() +"轮执行";
	                return  str;
	            }
	        },{
	        	field : "caseNumber",
	            title : "案例编号",
	            align : 'center', 
	        },{
	            field : "caseName",
	            title : "案例标题",
	            align : 'center' 
	        },{
	            field : "caseExecuteResult",
	            title : "执行状态",
	            align : 'center', 
	            width : "100px",
	            formatter : function (value, row, index) {
	            	if( row.caseExecuteResult==undefined ){
	            		return '<span class="spanClass1">未执行</span>'
	            	}else{
	            		for( var j=0;j<$( "#exeExecuteState option" ).length;j++ ){ 
			    			if( row.caseExecuteResult==$( "#exeExecuteState option" ).eq( j ).val() ){
			    				return  '<span class="spanClass'+row.caseExecuteResult+'">'+$( "#exeExecuteState option" ).eq( j ).text()+'</span>';
			    			}
			    		}
	            		return '<span class="spanClass1">未执行</span>'
	            	}
	                
	            }
	        },{
	            field : "userName",
	            title : "执行人",
	            align : 'center', 
	            formatter : function (value, row, index) {
	            	var  str="第"+ $("#exeExcuteRound").val() +"轮执行";
	            	if( row.userName == undefined || row.lastUpdateDate == undefined ){
	            		return '';
	            	}else{
	            		return  row.userName + " | " +datFmt(new Date(row.lastUpdateDate),"yyyy-MM-dd hh:mm:ss") ;
	            	} 
	            }
	        }],
        onLoadSuccess:function(){
        },
        onLoadError : function(){
        }
    }); 
}

function batchPass(){
	var data=$("#executionTable").bootstrapTable('getSelections'); 
	if(data.length==0){
		layer.alert("请选择执行案例",{icon : 6}); 
		return;
	}
	for( var i=0;i<data.length;i++ ){
		data[i].excuteRound=$( "#exeExcuteRound" ).val();
	}
	
	$.ajax({
		url : "/testManage/testExecution/updateTestCaseExecute",
	    method:"post",
		data:{
			"allExecuteDate": JSON.stringify( data ) , 
		},
	    success:function(data){   
	    	if( data.status == 1 ){
				 layer.alert("执行成功",{icon : 1});
				 $("#exeExecuteState").selectpicker('val', '');
				 initExecutionTable();
				 getAllExecuteCase();
			 }else{
				 layer.alert("执行失败",{icon : 2}); 
			 }
	    },
	    error:function(XMLHttpRequest, textStatus, errorThrown){ 
			layer.alert("系统内部错误，请联系管理员 ！！！", {icon: 0});
		}
	}); 
}








