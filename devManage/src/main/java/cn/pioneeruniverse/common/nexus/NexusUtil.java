package cn.pioneeruniverse.common.nexus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.StringUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import cn.pioneeruniverse.common.utils.HttpUtil;
import cn.pioneeruniverse.dev.entity.TblToolInfo;

/**
 * Nexus Rest API 基础公共类
 * @author zhoudu
 *
 */
public class NexusUtil {
	private static final Logger logger = LoggerFactory.getLogger(NexusUtil.class);
	
	private String baseUrl;
	private String username;
	private String password;
	
	private static final String TOKEN_REGEX = "^.+\"continuationToken\".+?(?=\"(.+?)\"|null|NULL).+$";
	
	public NexusUtil(String baseUrl, String username, String password) {
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}
	
	public NexusUtil(TblToolInfo bean) {
		String baseUrl = "http://" + bean.getIp() + ":" + bean.getPort() + bean.getContext();// http://localhost:8081/nexus/
		this.baseUrl = baseUrl;
		this.username = bean.getUserName();
		this.password = bean.getPassword();
		
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 发送GET请求,返回查询LIST的Json字符串
	 * @param componentList
	 * @param url
	 * @param clazz
	 * @return
	 */
	private String sendGetListUrl(List list, String url, Class clazz) throws Exception {
		String continuationToken;
		String resultJson = HttpUtil.httpRequest(url, "GET", null);
		continuationToken = resultJson.replaceAll(NexusUtil.TOKEN_REGEX, "$1");
		if (StringUtil.isNotEmpty(resultJson)) {
			resultJson = resultJson.substring(resultJson.indexOf("["), resultJson.lastIndexOf("]") + 1);
			list.addAll(JSON.parseArray(resultJson, clazz));
		}
		return continuationToken;
	}

	/**
	 * 将参数对象转换成URL参数连接串
	 * @param searchVO
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getParameterWithVO(NexusSearchVO searchVO) throws Exception {
		if (searchVO != null) {
			searchVO.setAllFieldEmptyToNull();
		}
		String paraStr = "";
		if (searchVO != null) {
			Map paraMap = JSON.parseObject(JSON.toJSONString(searchVO), Map.class);
			if (paraMap.size() > 0) {
				Collection collection = Collections2.transform(paraMap.entrySet(), new Function<Map.Entry<String, String>, String>(){
					@Override
					public String apply(Entry<String, String> entry) {
						Escaper escaper = UrlEscapers.urlFormParameterEscaper();
						//&maven.groupId=111&maven.artifactId=222&maven.baseVersion=333&maven.extension=444&maven.classifier=555
						String key = entry.getKey();
						if ("groupId,artifactId,baseVersion,extension,classifier".indexOf(key) != -1) {
							key = "maven." + key;
						}
						return escaper.escape(key) + "=" + escaper.escape(entry.getValue());
					}
				});
				paraStr = StringUtils.join(collection, "&");
				if (StringUtil.isNotEmpty(paraStr)) {
					paraStr = "?" + paraStr;
				}
			}
		}
		return paraStr;
	}
	
	/**
	 * 获取仓库列表
	 * http://localhost:8081/nexus/service/rest/v1/repositories
	 * @return
	 */
	public List<NexusRepositoryBO> getRepositoryList() throws Exception {
		List<NexusRepositoryBO> repositoryList = new ArrayList<NexusRepositoryBO>();
		String url = this.baseUrl + "service/rest/v1/repositories";
		String assetsJson = HttpUtil.httpRequest(url, "GET", null);
		if (StringUtil.isNotEmpty(assetsJson)) {
			repositoryList = JSON.parseArray(assetsJson, NexusRepositoryBO.class);
		}
		return repositoryList;
	}

	/**
	 * 获取仓库Component列表
	 * http://localhost:8081/nexus/service/rest/v1/assets?repository=maven-releases&continuationToken=5818005de2eb2bd1081fb5e1ddaaf23c
	 * @return
	 */
	public List<NexusComponentBO> getComponentList(String repositoryName) throws Exception {
		List<NexusComponentBO> componentList = new ArrayList<NexusComponentBO>();
		String continuationToken = null;
		String url = null;
		if (StringUtil.isNotEmpty(repositoryName)) {
			do {
				if (StringUtil.isEmpty(continuationToken)) {
					url = this.baseUrl + "service/rest/v1/components?repository=" + repositoryName;
				} else {
					url = this.baseUrl + "service/rest/v1/components?repository=" + repositoryName + "&continuationToken=" + continuationToken;
				}
				continuationToken = sendGetListUrl(componentList, url, NexusComponentBO.class);
			} while (StringUtil.isNotEmpty(continuationToken));
		}
		return componentList;
	}

	/**
	 * 根据componentId获取Component
	 * @param assetId
	 * @return
	 */
	public NexusComponentBO getComponent(String componentId) throws Exception {
		NexusComponentBO component = null;
		String url = null;
		if (StringUtil.isNotEmpty(componentId)) {
			url = this.baseUrl + "service/rest/v1/components/" + componentId;
			String componentJson = HttpUtil.httpRequest(url, "GET", null);
			if (StringUtil.isNotEmpty(componentJson)) {
				component = JSON.parseObject(componentJson, NexusComponentBO.class);
			}
		}
		return component;
	}
	
	/**
	 * 获取仓库assets列表
	 * http://localhost:8081/nexus/service/rest/v1/assets?repository=maven-releases&continuationToken=5818005de2eb2bd1081fb5e1ddaaf23c
	 * @return
	 */
	public List<NexusAssetBO> getAssetsList(String repositoryName) throws Exception {
		List<NexusAssetBO> assetList = new ArrayList<NexusAssetBO>();
		String continuationToken = null;
		String url = null;
		if (StringUtil.isNotEmpty(repositoryName)) {
			do {
				if (StringUtil.isEmpty(continuationToken)) {
					url = this.baseUrl + "service/rest/v1/assets?repository=" + repositoryName;
				} else {
					url = this.baseUrl + "service/rest/v1/assets?repository=" + repositoryName + "&continuationToken=" + continuationToken;
				}
				continuationToken = sendGetListUrl(assetList, url, NexusAssetBO.class);
			} while (StringUtil.isNotEmpty(continuationToken));
		}
		return assetList;
	}
	
	/**
	 * 根据assetId获取assets
	 * @param assetId
	 * @return
	 */
	public NexusAssetBO getAssets(String assetId) throws Exception {
		NexusAssetBO asset = null;
		String url = null;
		if (StringUtil.isNotEmpty(assetId)) {
			url = this.baseUrl + "service/rest/v1/assets/" + assetId;
			String assetsJson = HttpUtil.httpRequest(url, "GET", null);
			if (StringUtil.isNotEmpty(assetsJson)) {
				asset = JSON.parseObject(assetsJson, NexusAssetBO.class);
			}
		}
		return asset;
	}
	
	/**
	 * 搜索仓库Component列表
	 * http://localhost:8081/nexus/service/rest/v1/search?group=com.zd&name=test
	 * @param paraMap
	 * @return
	 */
	public List<NexusComponentBO> searchComponentList(NexusSearchVO searchVO) throws Exception {
		List<NexusComponentBO> componentList = new ArrayList<NexusComponentBO>();
		String continuationToken = null;
		String url = null;
		String paraStr = getParameterWithVO(searchVO);
		do {
			if (StringUtil.isEmpty(continuationToken)) {
				url = this.baseUrl + "service/rest/v1/search" + paraStr;
			} else {
				url = this.baseUrl + "service/rest/v1/search" + paraStr + "&continuationToken=" + continuationToken;
			}
			continuationToken = sendGetListUrl(componentList, url, NexusComponentBO.class);
		} while (StringUtil.isNotEmpty(continuationToken));
		return componentList;
	}
	
	/**
	 * 搜索仓库Asset列表
	 * http://localhost:8081/nexus/service/rest/v1/search/assets?group=com.zd&name=test
	 * @param paraMap
	 * @return
	 */
	public List<NexusAssetBO> searchAssetList(NexusSearchVO searchVO) throws Exception {
		if (searchVO == null) {
			searchVO = new NexusSearchVO();
		}
		List<NexusAssetBO> assetList = new ArrayList<NexusAssetBO>();
		String continuationToken = null;
		String url = null;
		searchVO.setExtension("jar");
		String paraStr = getParameterWithVO(searchVO);
		do {
			if (StringUtil.isEmpty(continuationToken)) {
				url = this.baseUrl + "service/rest/v1/search/assets" + paraStr;
			} else {
				url = this.baseUrl + "service/rest/v1/search/assets" + paraStr + "&continuationToken=" + continuationToken;
			}
			continuationToken = sendGetListUrl(assetList, url, NexusAssetBO.class);
		} while (StringUtil.isNotEmpty(continuationToken));
		logger.info("Nexus包件URL：" + url);
		continuationToken = null;
		searchVO.setExtension("war");
		paraStr = getParameterWithVO(searchVO);
		do {
			if (StringUtil.isEmpty(continuationToken)) {
				url = this.baseUrl + "service/rest/v1/search/assets" + paraStr;
			} else {
				url = this.baseUrl + "service/rest/v1/search/assets" + paraStr + "&continuationToken=" + continuationToken;
			}
			continuationToken = sendGetListUrl(assetList, url, NexusAssetBO.class);
		} while (StringUtil.isNotEmpty(continuationToken));
		logger.info("Nexus包件URL：" + url);
		continuationToken = null;
		searchVO.setExtension("zip");
		paraStr = getParameterWithVO(searchVO);
		do {
			if (StringUtil.isEmpty(continuationToken)) {
				url = this.baseUrl + "service/rest/v1/search/assets" + paraStr;
			} else {
				url = this.baseUrl + "service/rest/v1/search/assets" + paraStr + "&continuationToken=" + continuationToken;
			}
			continuationToken = sendGetListUrl(assetList, url, NexusAssetBO.class);
		} while (StringUtil.isNotEmpty(continuationToken));
		logger.info("Nexus包件URL：" + url);
		assetList = this.filterInvalidNexusAsset(assetList);
		return assetList;
	}
	
	/**
	 * 获取的Nexus数据有很多无效数据，去除无效的。
	 * @param componentList
	 * @return 
	 */
	private List<NexusAssetBO> filterInvalidNexusAsset(List<NexusAssetBO> assetList) throws Exception {
		List<NexusAssetBO> resultList = new ArrayList<NexusAssetBO>();
		String[] pathArr = null;
		String[] packageArr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));//Nexus时区相差8小时
		String path = null;
		String prePath = null;
		int lastSplit = 0;
		int packageLastSplit = 0;
		String artifactId = null;
		String group = null;
		String dateStr = null;
		String version = null;
		for (NexusAssetBO asset : assetList) {
			//实例：com/zd2/test2/0.0.1-SNAPSHOT/test2-0.0.1-20181228.035125-9.war
			//cn/pioneerservice/system/0.0.1/system-0.0.1.jar
			//com/wms/wms/1.0.0/wms-1.0.0.zip
			path = asset.getPath();
			if (path.endsWith("jar") || path.endsWith("war") || path.endsWith("zip")) {
				pathArr = path.split("/");
				lastSplit = pathArr.length - 1;
				packageArr = pathArr[lastSplit].split("-");//test2-0.0.1-20181228.035125-9.war
				packageLastSplit = packageArr.length - 1;
				artifactId = pathArr[lastSplit - 2];
				prePath = path.substring(0, path.lastIndexOf("/"));
				group = prePath.substring(0, prePath.lastIndexOf(artifactId) - 1);
				group = group.replaceAll("/", "\\.");
				if (asset.getRepository().toLowerCase().indexOf("snapshot") != -1 || path.indexOf("SNAPSHOT") != -1) {//快照
					dateStr = packageArr[packageLastSplit - 1];
					version = packageArr[packageLastSplit - 2] + "-" + packageArr[packageLastSplit - 1] + "-" + packageArr[packageLastSplit].split("\\.")[0];
					asset.setCreateTime(sdf.parse(dateStr));
				} else {//releases
					version = packageArr[packageLastSplit].substring(0, packageArr[packageLastSplit].lastIndexOf("."));
				}
				asset.setGroup(group);
				asset.setArtifactId(artifactId);
				asset.setVersion(version);
				resultList.add(asset);
			}
		}
		return resultList;
	}
	
	/**
	 * 从Nexus下载包件
	 * @param tblToolInfo
	 * @param repositoryName
	 * @param path
	 */
	public String downloadPackage(HttpServletRequest request,HttpServletResponse response, TblToolInfo bean, String repositoryName, String path) throws Exception {
		String downloadUrl = "http://" + bean.getIp() + ":" + bean.getPort() + "/nexus/repository/" + repositoryName + "/" + path;
		// 浏览器下载后的文件名称showValue,从url中截取到源文件名称以及，以及文件类型，如board.docx;
		String[] pathArr = path.split("/");
		String fileName = pathArr[pathArr.length - 1];//test2-0.0.1-20181228.035125-9.war
		return HttpUtil.downloadPackage(request, response, fileName, downloadUrl);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
//	public static void main(String[] args) throws Exception {
//		NexusUtil nexus = new NexusUtil("http://10.1.12.38:8081/nexus", "admin", "admin123");
//		NexusUtil nexus = new NexusUtil("http://10.1.15.52:40091/nexus", "admin", "Asdfghjkl15a!");
//		NexusSearchVO searchVO = new NexusSearchVO();
//		searchVO.setRepository("Snapshots-Dev1");//maven-snapshots maven-releases
//		searchVO.setQ("");
//		searchVO.setGroup("");
//		searchVO.setGroupId("com.ccic");
//		List<NexusAssetBO> list = nexus.searchAssetList(searchVO);
	
//		NexusAssetBO asset = nexus.getAssets("bWF2ZW4tc25hcHNob3RzOjNmNWNhZTAxNzYwMjMzYjYzMDg5OGJmNmZlMWQ5MTY0");
//		List<NexusComponentBO> list = nexus.getComponentList("maven-releases");
//		NexusSearchVO vo = new NexusSearchVO();
//		vo.setGroup("com.zd");
//		vo.setName("test");
//		List<NexusComponentBO> list = nexus.searchComponentList(vo);
//		
//		System.out.println("ok");
//
//	}
	
}
