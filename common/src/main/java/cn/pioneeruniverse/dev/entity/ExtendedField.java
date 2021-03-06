package cn.pioneeruniverse.dev.entity;

public class ExtendedField {

    private String label;       //字段显示名
    private String fieldName;   //字段名
    private String type;        //字段类型(数据字典）
    private String required;    //允许空值
    private String maxLength;   //最大长度
    private String defaultValue;//默认值
    private String status;      //有效无效
    private String readOnly;    //只读
    private String enums;  //枚举类型
    private String valueName;   //扩展字段对应的值

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getRequired() {
        return required;
    }
    public void setRequired(String required) {
        this.required = required;
    }

    public String getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getReadOnly() {
        return readOnly;
    }
    public void setReadOnly(String readOnly) {
        this.readOnly = readOnly;
    }

    public String getEnums() {
        return enums;
    }
    public void setEnums(String enums) {
        this.enums = enums;
    }

    public String getValueName() {
        return valueName;
    }
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}