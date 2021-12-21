package cn.pioneeruniverse.dev.entity;


import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblSystemVersion extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 706095827303768023L;

	private Long systemId;
	
	private Long systemModuleId;

	private String groupFlag;
	
    private String version;

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public String getGroupFlag() {
		return groupFlag;
	}

	public void setGroupFlag(String groupFlag) {
		this.groupFlag = groupFlag;
	}

	public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

	public Long getSystemModuleId() {
		return systemModuleId;
	}

	public void setSystemModuleId(Long systemModuleId) {
		this.systemModuleId = systemModuleId;
	}
    
    

}