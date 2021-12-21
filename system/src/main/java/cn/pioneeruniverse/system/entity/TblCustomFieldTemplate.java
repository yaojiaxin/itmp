package cn.pioneeruniverse.system.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_custom_field_template")
public class TblCustomFieldTemplate extends BaseEntity {

    private String customForm;
    private String customField;

    @TableField(exist=false)
    private String columnName;
    @TableField(exist=false)
    private String columnComment;

    public String getCustomForm() {
        return customForm;
    }
    public void setCustomForm(String customForm) {
        this.customForm = customForm;
    }

    public String getCustomField() {
        return customField;
    }
    public void setCustomField(String customField) {
        this.customField = customField;
    }

    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnComment() {
        return columnComment;
    }
    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }
}