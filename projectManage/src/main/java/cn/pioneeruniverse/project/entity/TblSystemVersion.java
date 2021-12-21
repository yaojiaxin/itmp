package cn.pioneeruniverse.project.entity;


import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblSystemVersion extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 706095827303768023L;

	private Long systemId;

	private String groupFlag;
	
    private String version;

    private Long projectId;
    private String ids;
	private String systemIds;
	private String systemNames;


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

	public String getSystemIds() {
		return systemIds;
	}
	public void setSystemIds(String systemIds) {
		this.systemIds = systemIds;
	}

	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getSystemNames() {
		return systemNames;
	}
	public void setSystemNames(String systemNames) {
		this.systemNames = systemNames;
	}

	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}