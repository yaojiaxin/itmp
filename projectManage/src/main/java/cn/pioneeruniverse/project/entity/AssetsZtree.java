package cn.pioneeruniverse.project.entity;

public class AssetsZtree {
    private String id;
    private String realId;
    private String name;
    private String code;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    private String systemId;

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    private String pId;
    private String level;
    private String requireId;
    private String docType;




    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    private boolean  isParent;
    private String type;
    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealId() {
        return realId;
    }

    public void setRealId(String realId) {
        this.realId = realId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRequireId() {
        return requireId;
    }

    public void setRequireId(String requireId) {
        this.requireId = requireId;
    }

}
