package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_server_info")
public class TblServerInfo extends BaseEntity {

	private static final long serialVersionUID = 6875852489685419330L;

    private Integer systemId;

	private String hostName;

    private String ip;
    
    private Long sshPort;

    private String sshUserAccount;

    private String sshUserPassword;

    @TableField(exist = false)
    private String systemName;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName == null ? null : hostName.trim();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }
    
    public Long getSshPort() {
		return sshPort;
	}

	public void setSshPort(Long sshPort) {
		this.sshPort = sshPort;
	}

	public String getSshUserAccount() {
        return sshUserAccount;
    }

    public void setSshUserAccount(String sshUserAccount) {
        this.sshUserAccount = sshUserAccount == null ? null : sshUserAccount.trim();
    }

    public String getSshUserPassword() {
        return sshUserPassword;
    }

    public void setSshUserPassword(String sshUserPassword) {
        this.sshUserPassword = sshUserPassword == null ? null : sshUserPassword.trim();
    }

}