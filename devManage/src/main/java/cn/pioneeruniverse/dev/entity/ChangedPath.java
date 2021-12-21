package cn.pioneeruniverse.dev.entity;

import java.io.Serializable;

/**
 * Created by admin on 2018/10/30.
 */
public class ChangedPath implements Serializable {
    private static final long serialVersionUID = -2447525021523407884L;

    private String type;
    private String path;
    private String copyPathMsg;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCopyPathMsg() {
        return copyPathMsg;
    }

    public void setCopyPathMsg(String copyPathMsg) {
        this.copyPathMsg = copyPathMsg;
    }
}
