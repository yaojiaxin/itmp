
/**
 * 表单验证
 */
 
function formValidator(){
	 $('#editForm').bootstrapValidator({  
         message: 'This value is not 1valid', 
         feedbackIcons : {
             valid : 'glyphicon glyphicon-ok',
             invalid : 'glyphicon glyphicon-remove',
             validating : 'glyphicon glyphicon-refresh'
         }, 
         fields : {
        	 editTestTaskName : {
                 validators : {
                     notEmpty:{
                         message: "关联任务不能为空!"
                     }  
                 }
             },
             editSystemName : {
                validators : {
                    notEmpty: {
                        message: "涉及系统不能为空!"
                    }  
                }
            },
            editCaseName : {
                validators : {
                    notEmpty:{
                        message: "案例名称不能为空!"
                    }   
                }, 
            },
             /*edit_expectResult: {
                 validators: {
                     notEmpty: {
                         message: '预期结果不能为空'
                     } ,
                     stringLength: {
                         max:300,
                         message: '预期结果长度必须小于500字符'
                     }
                 }
             },
             edit_inputData: {
                 validators: {
                     notEmpty: {
                         message: '输入数据不能为空'
                     } ,
                     stringLength: {
                         max:300,
                         message: '输入数据长度必须小于500字符'
                     }
                 }
             },
             edit_testPoint: {
                 validators: {
                     notEmpty: {
                         message: '测试项不能为空'
                     } ,
                     stringLength: {
                         max:300,
                         message: '测试项长度必须小于500字符'
                     }
                 }
             },
             edit_moduleName: {
                 validators: {
                     notEmpty: {
                         message: '模块不能为空'
                     } ,
                     stringLength: {
                         max:300,
                         message: '模块长度必须小于500字符'
                     }
                 }
             },
             edit_businessType: {
                 validators: {
                     notEmpty: {
                         message: '业务类型不能为空'
                     } ,
                     stringLength: {
                         max:300,
                         message: '业务类型长度必须小于500字符'
                     }
                 }
             },*/
            editCasePrecondition : {
                validators : {
                    notEmpty:{
                        message: "前置条件不能为空!"
                    } 
                }
            },  
         } 
     });

    $('#newDefectFrom').bootstrapValidator({
        excluded : [ ':disabled' ],
        feedbackIcons : {
            valid : 'glyphicon glyphicon-ok',
            invalid : 'glyphicon glyphicon-remove',
            validating : 'glyphicon glyphicon-refresh'
        },
        live : 'enabled',
        fields : {
            system_Name : {
                trigger:"change",
                validators : {
                    notEmpty:{
                        message: '所属系统不能为空！'
                    }
                }
            },
            defectSummary : {
                validators : {
                    notEmpty:{
                        message: '缺陷摘要不能为空！'
                    }
                }
            },
            repairRound : {
                validators : {
                    notEmpty:{
                        message: '请输入大于0的正数！'
                    },
                    regexp: {
                        regexp: /^\d+(\.\d{0,2})?$/,
                        message: '请输入大于0的正数！'
                    },
                    greaterThan:{
                        value:1,
                        message: '请输入大于0的正数！'
                    },
                    integer:{
                        message: '请不要输入奇怪的数字！'
                    },
                    lessThan:{
                        message: '请输入大于0的正数！'
                    }
                }
            },
            defectType : {
                validators : {
                    notEmpty : {
                        message: '缺陷类型不能为空！'
                    },
                    callback : {
                        message:function(){
                            return '';
                        },
                        callback : function(value, validator) {
                            if (value == '') {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            },
            defectSource : {
                validators : {
                    notEmpty : {
                        message: '缺陷来源不能为空！'
                    },
                    callback : {
                        message:function(){
                            return '';
                        },
                        callback : function(value, validator) {
                            if (value == '') {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            },
            severityLevel : {
                validators : {
                    notEmpty : {
                        message: '严重级别不能为空！'
                    },
                    callback : {
                        message:function(){
                            return '';
                        },
                        callback : function(value, validator) {
                            if (value == '') {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            },
            emergencyLevel : {
                validators : {
                    notEmpty : {
                        message: '紧急程度不能为空！'
                    },
                    callback : {
                        message:function(){
                            return '';
                        },
                        callback : function(value, validator) {
                            if (value == '') {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        }
    });
}

/**
 * 重构表单验证
 */
function refactorFormValidator(){
    $('#commitBug').on('hidden.bs.modal', function() {
        $("#newDefectFrom").data('bootstrapValidator').destroy();
        $('#newDefectFrom').data('bootstrapValidator', null);
        formValidator();
    });

}