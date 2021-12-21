package cn.pioneeruniverse.common.nexus;

import java.util.Date;
import java.util.Map;

/**
 * {
      "downloadUrl": "http://localhost:8081/nexus/repository/maven-snapshots/com/zd2/test2/0.0.1-SNAPSHOT/test2-0.0.1-20181218.013358-1.war",
      "path": "com/zd2/test2/0.0.1-SNAPSHOT/test2-0.0.1-20181218.013358-1.war",
      "id": "bWF2ZW4tc25hcHNob3RzOjNmNWNhZTAxNzYwMjMzYjYzMDg5OGJmNmZlMWQ5MTY0",
      "repository": "maven-snapshots",
      "format": "maven2",
      "checksum": {
        "sha1": "21353064de899f5c6b4568a736760f5c85b0dd4b",
        "md5": "8b4508d3ace57f995b47204d1ea6a16e"
      }
    }
 * @author zhoudu
 *
 */
public class NexusAssetBO {

	private String id;
	private String downloadUrl;
	private String path;
	private String repository;
	private String format;
	private Map<String, String> checksum;
	
	
	private String group;
	private String artifactId;
	private String version;
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Map<String, String> getChecksum() {
		return checksum;
	}

	public void setChecksum(Map<String, String> checksum) {
		this.checksum = checksum;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	

}
