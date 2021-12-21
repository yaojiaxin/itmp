var parameterArr={};
$(document).ready(function(){
	initPage();
});
function initPage(){  
    parameterArr.arr = window.location.href.split( "?" ); 
    parameterArr.parameterArr = parameterArr.arr[1].split( "," );
    parameterArr.obj={};
    for( var i = 0 ; i < parameterArr.parameterArr.length ; i++ ){
	   	var obj = parameterArr.parameterArr[i].split( "=" );  
	   	if( obj[1] == "undefined" ){
	   		parameterArr.obj[ obj[0] ] = '';
	   	}else{
	   		parameterArr.obj[ obj[0] ] = obj[1];
	   	}
	    
    }   
	var id=parameterArr.obj.id;
	var createType=parameterArr.obj.createType;
	var systemId=parameterArr.obj.systemId;
	var artifactId=parameterArr.obj.artifactId;
	var buildDependency=parameterArr.obj.buildDependency;
	var buildDependencySequence=parameterArr.obj.buildDependencySequence; 
	formValidator(); 
	if(systemId == ""){ 
		//系统配置弹框 
		$(".dataEntry").empty();
		$("#scmTypeName").val('');
		/*$("#architectureId").empty();*/
		$("#deployType").empty();
		$("#productionDeployType").empty();
		$("#codeReviewStatus").empty();
		$(".buildingConfig").empty();
		$(".deployConfig").empty();
		$("#sonarScanStatus").selectpicker('val', '');
		$("#deployType").append('<option value="">请选择</option>');
		$("#productionDeployType").append('<option value="">请选择</option>');
		$("#codeReviewStatus").append('<option value="">请选择</option>');
		$("#microServiceGroupID").val( '' );
		$("#microServiceArtifactID").val( '' );
		$("#systemFlag").val( '' );
		
		$("#deployType").append('<option value="1">源码构建部署 </option><option value="2">制品部署 </option>');
		$("#productionDeployType").append('<option value="1">726部署流程 </option><option value="2">自动化运维部署流程 </option>');
		$("#codeReviewStatus").append('<option value="1">是 </option><option value="2">否 </option>');
					
		$("#buildingWay input[value*='1']").prop("checked", 'true');  
		$(".manualInfo").css("display","none");
			
		$(".parentModal").css('display','block');
		
		if( id == '' || id == 'undefined' || id == null || id == undefined ){
			  
			
		}else{
			$("#loading").css('display','block'); 
			if( createType == null){
				createType = 1 ;
			}
			$.ajax({
		  		url:"/devManage/systeminfo/getOneSystemInfo",
		    	method:"post", 
		    	data:{
		    		"id":id,
		    		"createType": createType
		    	},
		  		success:function(data){  
		  			$("#loading").css('display','none'); 
		  			$("#architectureId").val(data.systemInfo.architectureType); 

	                $("#microServiceTitle").text( data.systemInfo.systemCode+" "+data.systemInfo.systemName ); 
		  			$("#microServiceName").val( data.systemInfo.systemName);
	                $("#microServiceNum").val( data.systemInfo.systemCode );
		  			
		  			$("#editCompileCommand").val( data.systemInfo.compileCommand ); 
		  			$("#microServiceSnapshotRepositoryName").val(data.systemInfo.snapshotRepositoryName);
		  			$("#microServiceReleaseRepositoryName").val(data.systemInfo.releaseRepositoryName);
		  			
		  			$("#microServiceID").val( data.systemInfo.id );
		  			$("#sonarScanStatus").val( data.systemInfo.sonarScanStatus );
		  			$("#editJdkVersion").val(data.systemInfo.jdkVersion);
		  			$("#editBuildToolVersion").val(data.systemInfo.buildToolVersion);
		  			$("#editPackageSuffix").val(data.systemInfo.packageSuffix); 
	                $("#systemFlag").val(data.systemInfo.systemFlag);
	                
	                $("#deployType").selectpicker('val', data.systemInfo.deployType );
	                $("#productionDeployType").selectpicker('val', data.systemInfo.productionDeployType );
	                
	                if(data.systemInfo.codeReviewStatus == 1){ 
		  				$("#codeReviewUserIds").parents('.form-group').removeClass('hidden');
		  			}else if(data.systemInfo.codeReviewStatus == 2){ 
		  				$("#codeReviewUserIds").parents('.form-group').addClass('hidden');
		  			}
	                $("#codeReviewStatus").selectpicker('val', data.systemInfo.codeReviewStatus )
	                
	                /*部署方式*/
		  			/*var depFlag1 = '';
		  			var depFlag2 = '';
		  			if(data.systemInfo.deployType == 1){
		  				depFlag1 = "selected";
		  			}else if(data.systemInfo.deployType == 2){
		  				depFlag2 = "selected";
		  			}
		  			$("#deployType").append('<option '+depFlag1+' value="1">源码构建部署 </option><option '+depFlag2+' value="2">制品部署 </option>');
		  			*/
	                /*生产环境部署方式*/
		  			/*var proFlag1 = '';
		  			var proFlag2 = '';
		  			if(data.systemInfo.productionDeployType == 1){
		  				proFlag1 = "selected";
		  			}else if(data.systemInfo.productionDeployType == 2){
		  				proFlag2 = "selected";
		  			}
		  			$("#productionDeployType").append('<option '+proFlag1+' value="1">726部署流程 </option><option '+proFlag2+' value="2">自动化运维部署流程 </option>');
		  			*/
	                /*是否代码评审*/
		  			/*var codeReviewFlag1 = '';
		  			var codeReviewFlag2 = '';
		  			if(data.systemInfo.codeReviewStatus == 1){
		  				codeReviewFlag1 = "selected";
		  				$("#codeReviewUserIds").parents('.form-group').removeClass('hidden');
		  			}else if(data.systemInfo.codeReviewStatus == 2){
		  				codeReviewFlag2 = "selected";
		  				$("#codeReviewUserIds").parents('.form-group').addClass('hidden');
		  			}
		  			$("#codeReviewStatus").append('<option '+codeReviewFlag1+' value="1">是 </option><option '+codeReviewFlag2+' value="2">否 </option>');
		  			*/
	                /*开发模式*/
		  			$("#developMode").selectpicker('val',data.systemInfo.developmentMode);
		  			
		  			$("#codeReviewUserIds").val(data.systemInfo.codeReviewUserIds);
		  			$("#codeReviewUserName").val(data.systemInfo.codeReviewUserName);
		  			$("#buildingTool input[type*='radio']").prop("checked", false);
		  			$("#buildingWay input[type*='radio']").prop("checked", false); 
		  			$("#microServiceGroupID").val( data.systemInfo.groupId );
		  			$("#microServiceArtifactID").val( data.systemInfo.artifactId );
		  			if( data.systemInfo.buildType===null ){   
		  				$("#buildingTool input[value*='1']").prop("checked", 'true'); 
		  				showArtAndGroupId( 1 )
		  			}else if( data.systemInfo.buildType == '1' ){
		  				$("#buildingTool input[value*='"+data.systemInfo.buildType+"']").prop("checked", 'true'); 
		  				showArtAndGroupId( data.systemInfo.buildType );
			  			$("#editJdkVersion").val( data.systemInfo.jdkVersion );
		  			}else if( data.systemInfo.buildType == '2' ){
		  				$("#buildingTool input[value*='"+data.systemInfo.buildType+"']").prop("checked", 'true'); 
		  				showArtAndGroupId( data.systemInfo.buildType );
		  				$("#editJdkVersion").val( data.systemInfo.jdkVersion );
		  			}else{
		  				$("#buildingTool input[value*='"+data.systemInfo.buildType+"']").prop("checked", 'true'); 
		  				showArtAndGroupId( data.systemInfo.buildType );
		  			} 
		  			/*systemVersionStr = '';
	  				commissioningWindow = '';
	  				if(data.systemVersionList != null){
	  					$.each(data.systemVersionList,function(index,value){
	  						var valueFlag=value.groupFlag;
	  						if(valueFlag==null){
	  							valueFlag="";
	  						}else{
	  							valueFlag=valueFlag+"-"
	  						}
	  						systemVersionStr += '<option value="'+value.id+'">'+valueFlag+value.version+'</option>'
	  					});
	  				} */
	  				/*environmentTypeStr = '';
	  				if(data.dics!=undefined){
	  					for(var i=0;i<data.dics.length;i++){
	  						environmentTypeStr += '<option value="'+data.dics[i].valueCode+'">'+data.dics[i].valueName+'</option>'
	  					}
	  				}*/
		  			$("#buildingWay input[value*='"+data.systemInfo.createType+"']").prop("checked", 'true');
		  			for( var i=0;i<data.jenkinslist.length;i++ ){
	  					var toolName; var envType;
	  					for(var key in data.toolList){  
	  						 if(data.toolList[key].id==data.jenkinslist[i].toolId){
	                             toolName=data.toolList[key].toolName;
	  						 }
	  					}
                        for(var j=0;j< data.dics.length ;j++){
                            if(data.dics[j].valueCode==data.jenkinslist[i].environmentType){
                                envType = data.dics[j].valueName;
                            }
                        }
						envType=envType||"";
	  					var str='<div class="JenkinsJob rowdiv">' +
									'<input type="hidden" class="hideKeyId" value='+data.jenkinslist[i].rootPom+' />' +
									'<div class="form-group def_col_8">' +
										'<div class="JobName font_right fontWeihgt def_col_20">Jenkins工具地址：</div>'+
	  		 							'<div class="JobName def_col_16"><select disabled="disabled" class="selectpicker JenkinsTooladdress" name="JenkinsTooladdress"><option value='+data.jenkinslist[i].toolId+' checked>'+toolName+'</option>'+
										'</select></div>' +
							'		</div>' +
									'<div class="form-group def_col_8">' +
										'<div class="JobName font_right fontWeihgt def_col_22">Jenkins Job Name：</div>'+
										'<div class="JobName def_col_14"><input disabled="disabled" type="text" value='+data.jenkinslist[i].jobName+' class="form-control" placeholder="请输入" name="JobName"/></div>' +
									'</div>'+
									'<div class="form-group def_col_8">' +
										'<div class="JobName font_right fontWeihgt def_col_20">Jenkins Job Path：</div>'+
										'<div class="JobPath def_col_16"><input disabled="disabled" type="text" value="'+toStr(data.jenkinslist[i].jobPath)+'" class="form-control" placeholder="请输入" name="JobPath"/></div>' +
									'</div>'+
									'<div class="form-group def_col_8">' +
										'<div class="JobName font_right fontWeihgt def_col_16">环境类型：</div>'+
										'<div class="JobName def_col_16">' +
											'<select disabled="disabled" class="selectpicker environmentType" name="environmentType">' +
												'<option value='+data.jenkinslist[i].environmentType+' checked>'+envType+'</option>' +
											'</select>'+
										'</div>'+
									'</div>'+
									'<div class="JobNameDel fontWeihgt def_col_3"><a onclick="delJenkinsJob(this)">删除</a></div>' +
								'</div>';

	  					if( data.jenkinslist[i].jobType==1 ){
	  						$(".buildingConfig").append( str );
	  					}else if( data.jenkinslist[i].jobType==2 ){
	  						$(".deployConfig").append( str );
	  					} 
	  				}
		  			addValShow()
		  			if(data.systemInfo.createType==2){ 
		  				$(".manualInfo").css("display","block"); 
	  				} 
		  			
	                $('.selectpicker').selectpicker('refresh');
	                if($("#scmTypeName").val()!=''){
	            		$(".def_btnGroup").css("display","block");
	            		$(".dataEntry").css("display","block");
	            	}else {
	            		$(".def_btnGroup").css("display","none");
	            		$(".dataEntry").css("display","none");
	            	} 
		        },
		  		error:function(){ 
		  		}
		 	});
		}    
	} else{
		$(".childModal").css('display','block');
		$("#loading").css('display','block');
		//微服务配置弹框
		$.ajax({
	  		url:"/devManage/systeminfo/getOneSystemModule",
	    	method:"post", 
	    	data:{
	    		"id":id
	    	},
	  		success:function(data){   
	  			$("#loading").css('display','none');
	  			
	  			$("#normalBuildDependency").empty();
	  			$("#normalBuildDependency").append('<option value="0">请选择</option>');
	  			//checkBoxStats=[]; 
	  			$(".environmentStatus").remove();
	  			
	  			$("#microChildService").text( data.systemCode+" "+data.systemName+": "+data.moduleName );
	  			$("#microChildServiceID").val( data.id );
	  			$("#microChildSystemId").val(data.systemId);
	  			$("#microChildServiceSystemName").text( data.systemName );
	  			$("#microChildServiceSystemNum").text( data.systemCode );
	  			$("#microChildServiceName").val( data.moduleName );
	  			$("#microChildServiceNum").val( data.moduleCode ); 
	  			$("#relativePath").val(data.relativePath);
	  			$("#childReleaseRepositoryName").val( data.releaseRepositoryName )
	  			$("#childSnapshotRepositoryName").val( data.snapshotRepositoryName )
	  			$("#childBuildToolVersion").val( data.buildToolVersion )
	  			$("#childPackageSuffix").val( data.packageSuffix )
	  			$("#childCompileCommand").val( data.compileCommand );
                $("#systemModuleFlag").val( data.systemModuleFlag );
                $("#childGroupID").val( data.groupId );
                $("#childArtifactID").val( data.artifactId );

	  			$("#childBuildingTool input[type*='radio']").prop("checked", false);
	  			if( data.buildType===null ){   
	  				$("#childBuildingTool input[value*='1']").prop("checked", 'true'); 
	  				showArtAndGroupId( 1 )
	  			}else if( data.buildType == '1' ){
	  				$("#childBuildingTool input[value*='"+data.buildType+"']").prop("checked", 'true'); 
	  				showArtAndGroupId( data.buildType );
		  			$("#childJdkVersion").val( data.jdkVersion );
	  			}else if( data.buildType == '2' ){
	  				$("#childBuildingTool input[value*='"+data.buildType+"']").prop("checked", 'true'); 
	  				showArtAndGroupId( data.buildType );
	  				$("#childJdkVersion").val( data.jdkVersion );
	  			}else{
	  				$("#childBuildingTool input[value*='"+data.buildType+"']").prop("checked", 'true'); 
	  				showArtAndGroupId( data.buildType );
	  			} 
	  			
	  			if( data.systemInfo.buildType == '1' || data.systemInfo.buildType === null ){ 
	  				if( artifactId == "undefined" ){
	  					$("#normalArtifactID").val( "" ); 
		  			}else{
		  				$("#normalArtifactID").val( artifactId ); 
		  			} 
	  				$(".artifactIDrowdiv").css( "display","block" );
	  			} else{
	  				$(".artifactIDrowdiv").css( "display","none" );
	  			}
	  			 
	  			var buildflag1 = '';
	  			var buildflag2 = '';
	  			if( data.buildDependency == 1){
	  				buildflag1 = "selected";
	  			}else if(data.buildDependency == 2){
	  				buildflag2 = "selected";
	  			}
	  			$("#normalBuildDependency").append('<option '+buildflag1+' value="1">必须 </option><option '+buildflag2+' value="2">非必须 </option>');	 
	  			
	  			$("#normalBuildDependencySequence").val( data.buildDependencySequence );
	  			
	  			$('.selectpicker').selectpicker('refresh');
	  			if(data.systemScms!="undefined"){
	  				for(var i=0;i<data.systemScms.length;i++){ 
		  				var flag='';
		  				for(var j=0;j<data.types2.length;j++){
		  					if( data.systemScms[i].id == data.types2[j].systemScmId ){
		  						flag="checked"; 
		  					} 
		  				} 
		  				for(var k=0;k<data.types.length;k++){
		  					if(data.systemScms[i].environmentType == data.types[k].valueCode){
		  						$("#environmentDIV").append('<div class="form-group col-md-2 environmentStatus"><input type="hidden" name="systemScmneme" val='+data.systemScms[i].id+'  /><input type="hidden" name="moduleScmNeme"/><label class="col-sm-8 control-label fontWeihgt"> <input type="checkbox" '+flag+'  />'+data.types[k].valueName+'</label></div>');
		  						break;
		  					}
		  				}
		  			}
	  			} 
	  			for(var i=0;i<data.types2.length;i++){
	  				(function (i){ 
	  					$("input[name='moduleScmNeme']").eq(i).val(data.types2[i].id);
	  				})(i); 
	  			}
                $('.selectpicker').selectpicker('refresh'); 
	        },
	  		error:function(){ 
	  		}
	 	}); 
	}  
}
//为空或者未定义或者为NULL或者是字符串去空格
function toStr(value1){
	if(value1==undefined ||value1==null||value1=="null"||value1=="NULL"){
 		return "";
 	}else if(!isNaN(value1)){
 		return value1;
 	}else{
	   return value1.toString().trim();
 	}
}
 
function inputClear(){
    $('.microServiceData input[type="text"]').parent().css("position","relative"); 
    if(  $('.microServiceData input[type="text"]').parent().children( "span[class*='btn_clear']" ),length  == 0  ){
    	$('.microServiceData input[type="text"]').parent().append("<span onclick='clearContent(this)' class='btn_clear'></span>");
    }  
} 
//追加根据buildingTool选择 显示Gid Aid
function buildingTool( This ){
	 showArtAndGroupId( $( This ).val()  );
}
function showArtAndGroupId( val ){
	$( ".showTag" ).css( "display","none" );
	$( ".showNexus" ).css( "display","block" );
	if( val == '1' ){
		$( ".showMaven" ).css( "display","block" );
	} else if( val == '2' ){
		$( ".showAnt" ).css( "display","block" );
	}
}
function getToolId( type,i ){
	
	$.ajax({
  		url:"/devManage/systeminfo/getToolByType",
    	method:"post",
    	async: true, 
  		success:function(data1){ 
//  			scmI = i;  
  			 if( typeof(i) === 'undefined'  ){  
//  				$(type).parent().parent().parent().next().children('.search_input').children('div').children('select').empty();
//  				$(type).parent().parent().parent().next().children('.search_input').children('div').children('select').append('<option value="">请选择</option>');
//  				
//  				for( var j=0;j<data1.toolList.length;j++ ){
//  					if(  data1.toolList[j].toolType == $( type ).val()  ){
//  						var obj='<option value='+data1.toolList[j].id+'>'+data1.toolList[j].toolName+'</option>';
//  	  	  				$(type).parent().parent().parent().next().children('.search_input').children('div').children('select').append(obj);
//  					}
//  				}
  				var str ='';
  				$(".abc").find("select" ).empty();
  				for(var j=0;j<data1.toolList.length;j++){  
  					if(  data1.toolList[j].toolType == type.options[type.options.selectedIndex].value  ){ 
  						 
  						var obj='<option value='+data1.toolList[j].id+'>'+data1.toolList[j].toolName+'</option>';
  	  	  				$(".abc").find("select" ).append(obj);
  						str += obj;
  					} 
  	  			}  
  				toolData=str;  
  				$('.selectpicker').selectpicker('refresh');
  			 } else{ 
//  				var str ='';
//  				$(".abc").find("select" ).empty();
  				
  				for(var j=0;j<data1.toolList.length;j++){  
  					if(  data1.toolList[j].toolType == type.scmType  ){   
  						 if(data1.toolList[j].id == type.toolId){ 
  							var obj='<option value='+data1.toolList[j].id+' selected  >'+data1.toolList[j].toolName+'</option>'; 
//	  						str += obj;
  						 }else{
  							var obj='<option value='+data1.toolList[j].id+'>'+data1.toolList[j].toolName+'</option>'; 
  						 }
  						$(".abc").find("select" ).eq(i).append(obj);
  					} 
  	  			}  
//  				toolData=str;  
  				$('.selectpicker').selectpicker('refresh');
  			  
        }
  		},
  		error:function(){ 
  		}
 	});
	
	if($("#scmTypeName").val()!=''){
		$(".def_btnGroup").css("display","block");
		$(".dataEntry").css("display","block");
	}else {
		$(".def_btnGroup").css("display","none");
		$(".dataEntry").css("display","none");
	}
}  
function getToolId2( type){
	console.log(type)
	$.ajax({
		url:"/devManage/systeminfo/getToolByType",
		method:"post",
		async: true, 
		success:function(data1){
			var str ='';
				for(var j=0;j<data1.toolList.length;j++){  
					if(  data1.toolList[j].toolType == type  ){ 
						 
						var obj='<option value='+data1.toolList[j].id+'>'+data1.toolList[j].toolName+'</option>';
	  	  				$(".abc").find("select" ).append(obj);
						str += obj;
					} 
	  			}  
				toolData=str;  
				$('.selectpicker').selectpicker('refresh');
		},
		error:function(){ 
		}
	}); 
}   
function inputClear(){
    $('.microServiceData input[type="text"]').parent().css("position","relative"); 
    if(  $('.microServiceData input[type="text"]').parent().children( "span[class*='btn_clear']" ),length  == 0  ){
    	$('.microServiceData input[type="text"]').parent().append("<span onclick='clearContent(this)' class='btn_clear'></span>");
    }  
} 

function submit(){
	if( parameterArr.obj.systemId == ""){
		microServiceCommit();
	}else{
		microChildServiceCommit();
	}
}

//系统配置提交
function microServiceCommit(){
	$('#configInfoForm').data('bootstrapValidator').validate();  
	if( !$('#configInfoForm').data("bootstrapValidator").isValid() ){
		return;
	}
	var data={}
	var id=$("#microServiceID").val();
	var buildType; 
	if( $("#buildingTool input[name='tools']:checked").length > 0  ){
		buildType=$("#buildingTool input[name='tools']:checked").val();
	}else{
		layer.alert('请选择构建工具！', {
            icon: 2,
            title: "提示信息"
        }); 
		return ;
	}
	var groupId=$("#microServiceGroupID").val();
	var artifactId=$("#microServiceArtifactID").val();
	var jdkVersion='';
	if( buildType==1 ){
		jdkVersion=$("#editJdkVersion").val();
	}else if( buildType==2 ){
		jdkVersion=$("#editJdkVersion").val();
	}    
	var architectureId = $("#architectureId").val(); 
	var deployType = $("#deployType").val(); 
	var productionDeployType = $("#productionDeployType").val(); 
	var snapshotRepositoryName = $("#microServiceSnapshotRepositoryName").val(); 
	var releaseRepositoryName = $("#microServiceReleaseRepositoryName").val();  
	var createType=$("#buildingWay input[name='build']:checked").val();
	var codeReviewStatus=$("#codeReviewStatus").val(); 
	
	var developMode=$("#developMode").val();
	var codeReviewUserIds=$("#codeReviewUserIds").val();
	var editCompileCommand=$( "#editCompileCommand" ).val();
	var sonarScanStatus=$( "#sonarScanStatus" ).val();
	var buildToolVersion=$("#editBuildToolVersion").val();
	var packageSuffix=$("#editPackageSuffix").val();
    var systemFlag=$("#systemFlag").val();
    
    if( codeReviewStatus == 1 ){
    	if( codeReviewUserIds == "" ){
    		layer.alert('当需要代码评审时，代码评审人不能为空！', {
                icon: 2,
                title: "提示信息"
            }); 
    		return ;
    	} 
	}
	/*var list=[];
	var changeList=[];
	var envType=[];  
	 //判断环境类型是否重复  
	for(var i=0;i<$("#codeConfig .singleData").length-1;i++){
		var obj={};
		var  warningFont='';   
		if($("select[name='environmentTypeName']").eq(i).find("option:selected").attr("value")!=''){
			obj.environmentType=$("select[name='environmentTypeName']").eq(i).find("option:selected").attr("value");  
			envType.push( obj.environmentType );
		} else{
			 warningFont="环境类型";
			 warningShow( warningFont );
			 return ;
		}
		if($("select[name='scmTypeName']").find("option:selected").attr("value")!=''){
			obj.scmType=$("select[name='scmTypeName']").find("option:selected").attr("value");
		}else{
			 warningFont="托管服务";
			 warningShow( warningFont );
			 return ;
		}
		if($("select[name='toolId']").eq(i).find("option:selected").attr("value")!=''){
			obj.toolId=$("select[name='toolId']").eq(i).find("option:selected").attr("value");
		}else{
			 warningFont="工具地址";
			 warningShow( warningFont );
			 return ;
		}   
		if($(".microServiceScmBranch").eq(i).val()!=''){
			obj.scmBranch=$(".microServiceScmBranch").eq(i).val();
		}else{
			 warningFont="分支名称";
			 warningShow( warningFont );
			 return ;
		}
		if($(".microServiceScmUrl").eq(i).val()!=''){
			var reg = new RegExp(/((http|svn)?:)\/\/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]/g); 
			
			if(reg.test( $(".microServiceScmUrl").eq(i).val() ) ){
				obj.scmUrl=$(".microServiceScmUrl").eq(i).val();
			}else{
				layer.alert("源代码地址格式错误，格式为：xxx://xxx/xx",{icon : 2});
				return;
			}
		}else{
			 warningFont="管理地址";
			 warningShow( warningFont );
			 return ;
		}  
		if($(".systemScmNeme").eq(i).val()!=''){
			obj.id=$("input[name='systemScmNeme']").eq(i).val();
		}
		if($(".microServiceScmSubmitSuperUser").eq(i).val()!=''){
			obj.submitSuperUserNames=$("input[name='microServiceScmSubmitSuperUser']").eq(i).val();
		} 
		if($(".microServiceSubmitUserName").eq(i).val()!=''){
			obj.submitUserNames=$("input[name='microServiceSubmitUserName']").eq(i).val();
		} 
		
		
		if( $("input[name='systemScmNeme']").eq(i).attr("value")!='' ){ 
			if($("select[name='environmentTypeName']").eq(i).find("option:selected").attr("value")!= $("select[name='environmentTypeName']").eq(i).attr("typeValue") ){
				changeList.push( obj );
			} else{
				list.push( obj );
			}
		}else{
			list.push( obj );
		}
		 
	}    
	
	if($("select[name='scmTypeName']").find("option:selected").attr("value")!='' && $(".microServiceData .singleData").length < 2){
		layer.alert('请至少添加一条数据！', {
            icon: 2,
            title: "提示信息"
        });  
		return ;
	} 
	
//	console.log($("select[name='scmTypeName']").find("option:selected").attr("value"))
	if($("#scmTypeName").val()==2){
		console.log($("#scmTypeName").val())
		if( isRepeat(envType) ){  
			layer.alert('环境类型重复，请修改！', {
	            icon: 2,
	            title: "提示信息"
	        });  
			return ;
		}  
	}
	list=JSON.stringify(list);
	changeList=JSON.stringify(changeList);*/
	var flagManual="";
	var flagDeploy="";
	if( createType==1 ){
		data={ 
			"id":id,
    		"systemName":$("#microServiceName").val(),
            "systemCode":$("#microServiceNum").val(),
    		"groupId":groupId,
    		"artifactId":artifactId,/*
    		"systemScm":list,
    		"newSystemScm":changeList,*/
    		"snapshotRepositoryName":snapshotRepositoryName,
    		"releaseRepositoryName":releaseRepositoryName,
    		"architectureType":architectureId,   		
    		"deployType":deployType,
    		"productionDeployType":productionDeployType, 		
    		"createType":createType,
    		"createTypes": createType, 
    		"buildType": buildType,
    		"codeReviewStatus":codeReviewStatus,
    		"jdkVersion":jdkVersion,
    		"buildToolVersion":buildToolVersion,
    		"packageSuffix":packageSuffix,
    		"developmentMode":developMode,
    		"compileCommand":editCompileCommand,
    		"codeReviewUserIds":codeReviewUserIds,
    		"sonarScanStatus":sonarScanStatus,
            "systemFlag":systemFlag
    	}; 
	}else if( createType==2 ){ 
		$('#buildingForm').data('bootstrapValidator').validate();  
		if( !$('#buildingForm').data("bootstrapValidator").isValid() ){
			return;
		}  
		var judgeJobName=new Array();
		var list2=[];
		for( var i=0;i<$(".manualInfo .buildingConfig .JenkinsJob").length;i++ ){
			(function (i){ 
				
				var obj={};
				if(  $(".manualInfo .buildingConfig .JenkinsJob").eq(i).children(".hideKeyId").val()!="" ){
					obj.id=$(".manualInfo .buildingConfig .JenkinsJob").eq(i).children(".hideKeyId").val();
				}
				obj.toolId=$(".manualInfo .buildingConfig .JenkinsJob").eq(i).find("select[class*='JenkinsTooladdress']").val();
				obj.jobName=$(".manualInfo .buildingConfig .JenkinsJob").eq(i).find("input[name*='JobName']").val();
				obj.jobPath=$(".manualInfo .buildingConfig .JenkinsJob").eq(i).find("input[name*='JobPath']").val();
				obj.environmentType=$(".manualInfo .buildingConfig .JenkinsJob").eq(i).find("select[class*='environmentType']").val();
				obj.jobType=1;
				if($.inArray(obj.jobName,judgeJobName)>-1){
//					layer.alert(obj.jobName+'已重复请修改', {
//	                    icon: 2,
//	                    title: "提示信息"
//	                });
					flagManual=flagManual+obj.jobName+",";
					
				}else{
					judgeJobName.push(obj.jobName);
				
				}
				list2.push( obj );
			})(i); 
		}
		var judgeJobNames=new Array();
		for( var i=0;i<$(".manualInfo .deployConfig .JenkinsJob").length;i++ ){
			(function (i){  
				var obj={};
				if(  $(".manualInfo .deployConfig .JenkinsJob").eq(i).children(".hideKeyId").val()!="" ){
					obj.id=$(".manualInfo .deployConfig .JenkinsJob").eq(i).children(".hideKeyId").val();
				}
				obj.toolId=$(".manualInfo .deployConfig .JenkinsJob").eq(i).find("select[class*='JenkinsTooladdress']").val();
				obj.jobName=$(".manualInfo .deployConfig .JenkinsJob").eq(i).find("input[name*='JobName']").val();  
				obj.jobPath=$(".manualInfo .deployConfig .JenkinsJob").eq(i).find("input[name*='JobPath']").val();
                obj.environmentType=$(".manualInfo .deployConfig .JenkinsJob").eq(i).find("select[class*='environmentType']").val();
				if($.inArray(obj.jobName,judgeJobNames)>-1){
//					layer.alert(obj.jobName+'已重复请修改', {
//	                    icon: 2,
//	                    title: "提示信息"
//	                });
//					return false ;
					flagDeploy=flagDeploy+obj.jobName+",";
				}else{
				judgeJobNames.push(obj.jobName);
				}
				obj.jobType=2;
				list2.push( obj );
			})(i); 
		} 
		list2=JSON.stringify(list2);
		data={
	    		"id":id,
	    		"systemName":$("#microServiceName").val(),
            	"systemCode":$("#microServiceNum").val(),
	    		"groupId":groupId,
	    		"artifactId":artifactId,
	    		/*"systemScm":list, 
	    		"newSystemScm":changeList,*/
	    		"systemJenkins":list2,
	    		"snapshotRepositoryName":snapshotRepositoryName,
	    		"releaseRepositoryName":releaseRepositoryName,
	    		"architectureType":architectureId,	    		
	    		"deployType":deployType,
	    		"productionDeployType":productionDeployType, 		    		
	    		"createType":createType,
	    		"createTypes": createType,
	    		"buildType": buildType,
	    		"codeReviewStatus":codeReviewStatus,
	    		"jdkVersion":jdkVersion,
	    		"buildToolVersion":buildToolVersion,
	    		"packageSuffix":packageSuffix,
	    		"developmentMode":developMode,
	    		"compileCommand":editCompileCommand,
	    		"codeReviewUserIds":codeReviewUserIds,
	    		"sonarScanStatus":sonarScanStatus,
            	"systemFlag":systemFlag
	    };
	} 
	if(flagManual!="" || flagDeploy!=""){
		layer.alert(flagManual+flagDeploy+'已重复请修改', {
	        icon: 2,
	        title: "提示信息"
		});
		$("#loading").css('display','none');
		return false;
	}
	$("#loading").css('display','block');  
	if( parameterArr.obj.id == '' ){
		var Z_url = "/devManage/systeminfo/addSystem";
	}else{
		var Z_url = "/devManage/systeminfo/updateSystemInfo";
	}
	$.ajax({
  		url:Z_url,
    	method:"post", 
    	data: data,
  		success:function(data){ 
  			$("#loading").css('display','none');
  			if(data.status == "repeat"){ 
  				//alert("源码配置环境类型重复，请修改！！！");
  				layer.alert('源码配置环境类型重复，请修改！！', {
                    icon: 2,
                    title: "提示信息"
                });
  			}else if(data.status == 2){
  				//alert("保存失败！！！");
  				layer.alert('保存失败，原因：'+data.errorMessage, {
                    icon: 2,
                    title: "提示信息"
                });
  			}
  			else if(data.status == "rejobName"){
  				layer.alert(data.message, {
                    icon: 2,
                    title: "提示信息"
                });
  				//alert(data.message);
  			}else if(data.status = 1){
  				//alert("配置成功！！！");
  				parent.functionOpt( data , 1 );
  				layer.alert('配置成功', {
                    icon: 1,
                    title: "提示信息",
                    yes:function(){
	 					parent.layer.closeAll();
	 				 }
                });  
  			}
        },
  		error:function(){ 
  		}
 	});   
} 
//微服务配置提交
function microChildServiceCommit(){
	$('#normalServiceForm').data('bootstrapValidator').validate();  
	if( !$('#normalServiceForm').data("bootstrapValidator").isValid() ){
		return;
	}
	var id=$("#microChildServiceID").val();
	var systemId = $("#microChildSystemId").val();
	var addmoduleName = $("#microChildServiceName").val();
	var addmoduleCode = $("#microChildServiceNum").val();
	var buildDependency = $("#normalBuildDependency").val();
	var buildDependencySequence = $("#normalBuildDependencySequence").val();
    var systemModuleFlag = $("#systemModuleFlag").val();
	var buildType=$("#childBuildingTool input[name='tools']:checked").val();
	var groupId=$("#childGroupID").val();
	var artifactId=$("#childArtifactID").val();
	var jdkVersion=$("#childJdkVersion").val();

	var relativePath=$("#relativePath").val();
	if(relativePath==""){
		layer.alert('请填写编译相对路径', {
			icon: 2,
			title: "提示信息"
		});
		return;
	}

	var releaseRepositoryName = $("#childReleaseRepositoryName").val();
	var snapshotRepositoryName = $("#childSnapshotRepositoryName").val();
	var buildToolVersion = $("#childBuildToolVersion").val();
	var packageSuffix = $("#childPackageSuffix").val();
	var compileCommand = $("#childCompileCommand").val();
	var moduleArr = [];
	var fflag=false;
	for(var i=0;i<$(".environmentStatus").length;i++){
		(function (i){
			if( $(".environmentStatus").eq(i).find("input[type='checkbox']").is(':checked') ){   
				var scmid={};
				scmid.systemScmId =  $( $(".environmentStatus").eq(i).find("input[name='systemScmneme']")).eq(0).attr('val');
				if($( $(".environmentStatus").eq(i).find("input[name='moduleScmNeme']")).eq(0).attr('value')!=''){
					scmid.id = $( $(".environmentStatus").eq(i).find("input[name='moduleScmNeme']")).eq(0).attr('value') ;
				} 
				moduleArr.push(scmid);
			}
		})(i);  
	}
	moduleArr=JSON.stringify(moduleArr);  
	$("#loading").css('display','block');
	$.ajax({
  		url:"/devManage/systeminfo/updateSystemModuleScm",
    	method:"post", 
    	data:{
    		 "systemId":systemId,
    		 "id":id,
    		 "moduleName":addmoduleName,
    		 "moduleCode":addmoduleCode,
    		 "systemModuleScms":moduleArr,
    		 "buildDependency":buildDependency,
    		 "buildDependencySequence":buildDependencySequence,
    		 "buildType":buildType,
 			"releaseRepositoryName": releaseRepositoryName,
 			"snapshotRepositoryName": snapshotRepositoryName,
 			"buildToolVersion": buildToolVersion,
 			"packageSuffix": packageSuffix,
 			"jdkVersion": jdkVersion,
 			"groupId": groupId,
 			"artifactId": artifactId,
 			"compileCommand":compileCommand,
			"relativePath":relativePath,
			"systemModuleFlag":systemModuleFlag
    	},
  		success:function(data){ 
  			$("#loading").css('display','none');
  			if(data.status == "success"){
  				parent.functionOpt( data.systemModule , 2 )
  				layer.alert('配置成功', {
                    icon: 1,
                    title: "提示信息",
                    yes:function(){
	 					parent.layer.closeAll();
	 				 }
                }); 
  			}
  			if(data.status == "fail"){
  				layer.alert('配置失败', {
                    icon: 2,
                    title: "提示信息"
                });
  			}
  			
        },
  		error:function(){ 
  		}
 	});
} 

function addValShow() {
	$('#buildingForm').bootstrapValidator("addField","environmentType",{
		 feedbackIcons: {//根据验证结果显示的各种图标
    　　　　　　　　valid: 'glyphicon glyphicon-ok',
    　　　　　　　　invalid: 'glyphicon glyphicon-remove',
    　　　　　　　　validating: 'glyphicon glyphicon-refresh'
	    　　　},
		 validators: {
	        notEmpty: {
	            message: '环境类型不能为空'
	        } 
		}
	})
	$('#buildingForm').bootstrapValidator("addField","JenkinsTooladdress",{
		feedbackIcons: {//根据验证结果显示的各种图标
    　　　　　　　　valid: 'glyphicon glyphicon-ok',
    　　　　　　　　invalid: 'glyphicon glyphicon-remove',
    　　　　　　　　validating: 'glyphicon glyphicon-refresh'
    　　　},
		validators: {
		        notEmpty: {
		            message: '请选择Jenkins工具地址'
		        },
		        stringLength: {
		        	max:20,
		        	message: 'Jenkins工具地址长度必须小于20字符'
		        }
		}
	})
	$('#buildingForm').bootstrapValidator("addField","JobName",{  
		feedbackIcons: {//根据验证结果显示的各种图标
    　　　　　　　　valid: 'glyphicon glyphicon-ok',
    　　　　　　　　invalid: 'glyphicon glyphicon-remove',
    　　　　　　　　validating: 'glyphicon glyphicon-refresh'
    　　　},
		validators: {
			notEmpty: {
				message: 'Jenkins Job Name不能为空'
	        },
	        stringLength: {
	        	max:50,
	        	message: 'Jenkins Job Name长度必须小于50字符'
	        }, 
	    }
	})	
}
//是否显示代码审计人
function codeReviewShow( This ){
	 var value = $(This).selectpicker('val');
	 if(value == "1"){
		 $("#codeReviewUserIds").parents('.form-group').removeClass('hidden');
	 }else{
		 $("#codeReviewUserIds").val("");
		 $("#codeReviewUserName").val("");
		 $("#codeReviewUserIds").parents('.form-group').addClass('hidden');
	 }
}
function buildingWay(This){
	if( $(This).val() == "1"){ 
		$(".manualInfo").css("display","none");
	}else if( $(This).val() == "2"){ 
		$(".manualInfo").css("display","block");
	}else{ 
		$(".manualInfo").css("display","none");
	}
}
function addJenkinsJob(This){
	$.ajax({
		url:'/devManage/systeminfo/getToolinfoType',
		dataType:'json',
		type:'post',
		data:{
            "id":parameterArr.obj.id,
   		 	"type":4
		},
		success:function(data){
			var str='<div class="JenkinsJob rowdiv"><input type="hidden" class="hideKeyId" value="" />'+
 			'<div class="form-group def_col_8"><div class="JobName font_right fontWeihgt def_col_20">Jenkins工具地址：</div>'+
 			'<div class="JobName def_col_16"><select class="selectpicker JenkinsTooladdress" name="JenkinsTooladdress"><option value="">请选择</option>';
			for(var i=0;i< data.toolList.length ;i++){
				str+='<option value='+data.toolList[i].id+' >'+data.toolList[i].toolName+'</option>';
			}
			str+='</select></div></div><div class="form-group def_col_8"><div class="JobName font_right fontWeihgt def_col_22">Jenkins Job Name：</div>'+
 			'<div class="JobName def_col_14"><input type="text" class="form-control" placeholder="请输入" name="JobName"/></div></div>'+
 			'<div class="form-group def_col_8"><div class="JobName font_right fontWeihgt def_col_20">Jenkins Job Path：</div>'+
 			'<div class="JobPath def_col_16"><input type="text" class="form-control" placeholder="请输入" name="JobPath"/></div></div><div class="form-group def_col_8"><div class="JobName font_right fontWeihgt def_col_16">环境类型：</div>'+
			'<div class="JobName def_col_16"><select class="selectpicker environmentType" name="environmentType"><option value="">请选择</option>';
            for(var i=0;i< data.dics.length ;i++){
                str+='<option value='+data.dics[i].valueCode+' >'+data.dics[i].valueName+'</option>';
            }
            str+='</select></div></div><div class="JobNameDel fontWeihgt def_col_3"><a onclick="delJenkinsJob(this)">删除</a></div></div>';
            $( This ).parent().next().append( str );
			$('.selectpicker').selectpicker('refresh'); 
			addValShow();
		}
	}) 
}
function delJenkinsJob(This){ 
	if( $( This ).parent().parent().children(".hideKeyId").val()=='' ){ 
		$( This ).parent().parent().remove();
	}else{
		var id= $( This ).parent().parent().children(".hideKeyId").val().toString() ;  
		var JobName=$( This ).parent().parent().find("input[name*='JobName']").val();
		$("#promptBox .modal-footer2").empty();
		$("#promptBox .modal-footer2").append('<button type="button" class="btn btn-primary" onclick="confirmDel(\''+id+'\')">确定</button><button type="button" class="btn btn-default" data-dismiss="modal">取消</button>')
		 
		$("#promptBoxSysName").html( JobName );
		$("#promptBox").modal("show");  
	}
	 
}
function confirmDel(  id ) {    
	$.ajax({
		url:'/devManage/systeminfo/deleteSystemJenkinsManual',
		dataType:'json',
		type:'post',
		data:{
   		 "id": id, 
		},
		success:function(data){   
			$(".manualInfo input[value*='"+id+"']").parent().remove();
			$("#promptBox").modal("hide");
		}
	}) 
}
//表单校验
function formValidator(){
	 $('#addSyatemForm').bootstrapValidator({
        message: 'This value is not valid',//通用的验证失败消息
        feedbackIcons: {//根据验证结果显示的各种图标
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            addSystemName: {
                validators: {
                    notEmpty: {
                        message: '系统名称不能为空'
                    } ,
                    stringLength: {
                        max:100,
                        message: '系统名称长度必须小于100字符'
                    }
                }
            },
            addSystemCode: {
                validators: {
                    notEmpty: {
                        message: '系统编号不能为空'
                    } ,
                    stringLength: {
                        max:100,
                        message: '系统编号长度必须小于100字符'
                    },
                    regexp: {
                        regexp: /^[-_/.0-9a-zA-Z]+$/,
                        message: '只能输入数字、英文或-_/.'
                    }
                }
            }
        }
    });
   $('#newform').bootstrapValidator({
　　　  message: 'This value is not valid',//通用的验证失败消息
	    feedbackIcons: {//根据验证结果显示的各种图标
　　　　　　　valid: 'glyphicon glyphicon-ok',
　　　　　　　invalid: 'glyphicon glyphicon-remove',
　　　　　　　validating: 'glyphicon glyphicon-refresh'
　　　  },
       fields: {
			moduleName: {
			    validators: {
			        notEmpty: {
			            message: '模块名称不能为空'
			        } ,
			        stringLength: {
                      max:100,
                      message: '模块名称长度必须小于100字符'
                   } 
			    }
			},
			moduleCode: {
				validators: {
					notEmpty: {
						message: '模块编号不能为空'
			        } ,
			        stringLength: {
                      max:100,
                      message: '模块编号长度必须小于100字符'
                   },
                   regexp: {            
                      regexp: /^[-_/.0-9a-zA-Z]+$/,
                      message: '只能输入数字、英文或-_/.'
                   } 
			    }
			},
			addBuildDependency: {
	    		   validators: {
	    			   notEmpty: {
	    				   message: '请选择构建依赖'
		               } 
		    	   }
		     }
       }
   });
   $('#configInfoForm').bootstrapValidator({
   　　　  message: 'This value is not valid',//通用的验证失败消息
   　		feedbackIcons: {//根据验证结果显示的各种图标
   　　　　　　　　valid: 'glyphicon glyphicon-ok',
   　　　　　　　　invalid: 'glyphicon glyphicon-remove',
   　　　　　　　　validating: 'glyphicon glyphicon-refresh'
   　　　},
      fields: {
   	   microServiceName: {
              validators: {
                  notEmpty: {
                      message: '系统名称不能为空'
                  } ,
                  stringLength: {
                      max:100,
                      message: '系统名称长度必须小于100字符'
                  }
              }
          },
          microServiceNum: {
              validators: {
                  notEmpty: {
                      message: '系统编号不能为空'
                  } ,
                  stringLength: {
                      max:100,
                      message: '系统编号长度必须小于100字符'
                  },
                  regexp: {
                      regexp: /^[-_/.0-9a-zA-Z]+$/,
                      message: '只能输入数字、英文或-_/.'
                  }
              }
          },
//   	   groupID: {
//   		   validators: {
//   			   notEmpty: {
//   				   message: 'GroupID不能为空'
//	               },
//	               stringLength: {
//                      max:50,
//                      message: 'GroupID长度必须小于50字符'
//                  },
//                  regexp: {            
//                      regexp: /^[-_/.0-9a-zA-Z]+$/,
//                      message: '只能输入数字、英文或-_/.'
//                  }, 
//	    	   }
//	       },
//	       artifactID: {
//	    	   validators: {
//	    		   notEmpty: {
//	    			   message: 'ArtifactID不能为空'
//	               },
//	               stringLength: {
//                      max:50,
//                      message: 'ArtifactID长度必须小于50字符'
//                  },
//                  regexp: {            
//                      regexp: /^[-_/.0-9a-zA-Z]+$/,
//                      message: '只能输入数字、英文或-_/.'
//                  }, 
//	           }
//	       },
	       architectureId: {
   		   validators: {
   			   notEmpty: {
   				   message: '请选择系统架构'
	               } 
	    	   }
	       },
	       deployType: {
   		   validators: {
   			   notEmpty: {
   				   message: '请选择部署方式'
	               } 
	    	   }
	       },
	       productionDeployType: {
   		   validators: {
   			   notEmpty: {
   				   message: '请选择生产环境部署方式'
	               } 
	    	   }
	       },
	       developMode: {
   		   validators: {
   			   notEmpty: {
   				   message: '请选择开发模式'
	               } 
	    	   }
	       },
	       snapshotRepositoryName: {
   		   validators: {
   			   stringLength: {
                      max:50,
                      message: '快照仓库名称长度必须小于50字符'
                  }, 
	    	   }
	       },
	       releaseRepositoryName: {
   		   validators: {
   			   stringLength: {
                      max:50,
                      message: '发布仓库名称长度必须小于50字符'
                  }, 
	    	   }
	       },
      }
   }); 
   $("#normalServiceForm").bootstrapValidator({ 
	    feedbackIcons: {//根据验证结果显示的各种图标
　　　　　　　valid: 'glyphicon glyphicon-ok',
　　　　　　　invalid: 'glyphicon glyphicon-remove',
　　　　　　　validating: 'glyphicon glyphicon-refresh'
　　　  },
       fields: {
       	microChildServiceName: {
			    validators: {
			        notEmpty: {
			            message: '模块名称不能为空'
			        } ,
			        stringLength: {
                      max:100,
                      message: '模块名称长度必须小于100字符'
                   } 
			    }
			},
			microChildServiceNum: {
				validators: {
					notEmpty: {
						message: '模块编号不能为空'
			        } ,
			        stringLength: {
                      max:100,
                      message: '模块编号长度必须小于100字符'
                   },
                   regexp: {            
                       regexp: /^[-_/.0-9a-zA-Z]+$/,
                       message: '只能输入数字、英文或-_/.'
                   }
			    }
			},
			normalArtifactID: {
				validators: {
					notEmpty: {
						message: 'ArtifactID不能为空'
			        } ,
			        stringLength: {
                      max:100,
                      message: 'ArtifactID长度必须小于50字符'
                   },
                   regexp: {            
                      regexp: /^[-_/.0-9a-zA-Z]+$/,
                      message: '只能输入数字、英文或-_/.'
                   } 
			    }
			} 
       }
   });
   $("#deployInfoForm").bootstrapValidator({ 
	    feedbackIcons: {//根据验证结果显示的各种图标
	　　　　　　　　valid: 'glyphicon glyphicon-ok',
	　　　　　　　　invalid: 'glyphicon glyphicon-remove',
	　　　　　　　　validating: 'glyphicon glyphicon-refresh'
	　　　　  },
	        fields: {
	        	deployPath: {
				    validators: {
				        notEmpty: {
				            message: '部署路径不能为空'
				        } ,
				        stringLength: {
	                       max:100,
	                       message: '部署路径长度必须小于200字符'
	                    } 
				    }
				},
				timeOut: {
					validators: {
						notEmpty: {
							message: '超时设置不能为空'
				        }, 
	                    regexp: {            
	                       regexp: /^(0|\+?[1-9][0-9]*)$/,
	                       message: '只能输入大于等于0的整数'
	                    } 
				    }
				},
				retryNumber: {
					validators: {
						notEmpty: {
							message: '重试次数不能为空'
				        }, 
	                    regexp: {            
	                       regexp: /^(0|\+?[1-9][0-9]*)$/,
	                       message: '只能输入大于等于0的整数'
	                    },
	                    stringLength : {
                            max : 2,
                            message : '长度2位字符'
                        }
				    }
				},
				deploySequence: {
					validators: {
	                    regexp: {            
	                       regexp: /^(0|\+?[1-9][0-9]*)$/,
	                       message: '只能输入大于等于0的整数'
	                    },
	                    stringLength : {
                            max : 2,
                            message : '长度2位字符'
                        }
				    }
				}
	        }
	    }); 
}