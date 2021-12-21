package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.dto.ResultDataDTO;
import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.dto.TblUserInfoDTO;
import cn.pioneeruniverse.common.gitlab.entity.Project;
import cn.pioneeruniverse.common.gitlab.entity.UserDTO;
import cn.pioneeruniverse.common.subversion.SubversionUtils;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.service.systeminfo.ISystemInfoService;
import cn.pioneeruniverse.dev.service.version.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description: 版本管理restController
 * @Date: Created in 13:41 2018/12/13
 * @Modified By:
 */
@RestController
@RequestMapping(value = "version")
public class VersionController {
    private final static Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private VersionService versionService;

    @Autowired
    private ISystemInfoService systemInfoService;

    /**
     * @param systemId
     * @return java.util.List<cn.pioneeruniverse.dev.entity.SvnFileDirectoryStructure>
     * @Description 获取当前用户所属项目组的svn代码库文件目录结构
     * @MethodName getSvnFileTree
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2018/12/13 16:03
     */
    @RequestMapping(value = "getSvnFileTree", method = RequestMethod.POST)
    public List<SvnFileDirectoryStructure> getSvnFileTree(Long systemId) {
        return versionService.getSvnFileTree(systemId);
    }

    /**
     * @param structure
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description svn文件目录树展开获取节点数据
     * @MethodName getSvnSonFileTree
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/1/28 9:48
     */
    @RequestMapping(value = "getSvnSonFileTree", method = RequestMethod.POST)
    public AjaxModel getSvnSonFileTree(SvnFileDirectoryStructure structure) {
        try {
            List<SvnFileDirectoryStructure> sonFileStructure = SubversionUtils.getSvnDirEntry(structure.getRepositoryUrl(), structure.getUsername(), structure.getPassword(), structure.getAccessProtocol(), structure.getIp(), structure.getPort(), structure.getPath(), structure.getRepositoryName(), null).getChildren();
            return AjaxModel.SUCCESS(sonFileStructure);
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }


    /**
     * @param ip
     * @param repositoryName
     * @param path
     * @param request
     * @param response
     * @return cn.pioneeruniverse.common.entity.JqGridPage<cn.pioneeruniverse.common.dto.TblUserInfoDTO>
     * @Description 获取svn上的用户权限
     * @MethodName getSvnUsers
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2018/12/21 14:00
     */
    @RequestMapping(value = "getSvnUsers", method = RequestMethod.POST)
    public JqGridPage<TblUserInfoDTO> getSvnUsers(String ip, String repositoryName, String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return versionService.getSvnUserAuth(new JqGridPage<TblUserInfoDTO>(request, response), ip, repositoryName, path);
    }

    /**
     * @param project
     * @param request
     * @param response
     * @return cn.pioneeruniverse.common.entity.JqGridPage<cn.pioneeruniverse.common.dto.TblUserInfoDTO>
     * @Description 获取gitlab上的用户及权限
     * @MethodName getGitLabUsers
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/7/4 14:51
     */
    @RequestMapping(value = "getGitLabUsers", method = RequestMethod.POST)
    public JqGridPage<TblUserInfoDTO> getGitLabUsers(Project project, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        return versionService.getGitLabUserAuth(new JqGridPage<TblUserInfoDTO>(request, response), project);
    }

    /**
     * @param ip
     * @param repositoryName
     * @param path
     * @param modifyOperates
     * @return java.lang.String
     * @Description 保存对svn的修改
     * @MethodName saveSvnModify
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2018/12/21 14:01
     */
    @RequestMapping(value = "saveSvnModify", method = RequestMethod.POST)
    public AjaxModel saveSvnModify(Integer accessProtocol, String ip, String repositoryName, String path, String modifyOperates) {
        try {
            return AjaxModel.SUCCESS(versionService.saveSvnModify(accessProtocol, ip, repositoryName, path, modifyOperates));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    /**
     * @param svnPreCommitWebHookParam
     * @return java.lang.String
     * @Description svn的pre-commit钩子请求
     * @MethodName svnWebHookRequest
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2018/12/27 10:07
     */
    @RequestMapping(value = "svnPreCommitWebHookRequest", method = RequestMethod.POST)
    public String svnPreCommitWebHookRequest(@RequestBody SvnPreCommitWebHookParam svnPreCommitWebHookParam) {
        return versionService.handleSvnPreCommitWebHookRequest(svnPreCommitWebHookParam);
    }

    @RequestMapping(value = "svnPreCommitWebHookFor726Request", method = RequestMethod.POST)
    public String svnPreCommitWebHookFor726Request(@RequestBody SvnPreCommitWebHookParam svnPreCommitWebHookParam) {
        svnPreCommitWebHookParam.setCheckDBScriptFileFor726(true);
        return versionService.handleSvnPreCommitWebHookRequest(svnPreCommitWebHookParam);
    }

    /**
     * @param svnPostCommitWebHookParam
     * @return java.lang.String
     * @Description svn的post-commit的钩子请求
     * @MethodName svnPostCommitWebHookRequest
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/1/8 10:52
     */
    @RequestMapping(value = "svnPostCommitWebHookRequest", method = RequestMethod.POST)
    public String svnPostCommitWebHookRequest(@RequestBody SvnPostCommitWebHookParam svnPostCommitWebHookParam) {
        return versionService.handleSvnPostCommitWebHookRequest(svnPostCommitWebHookParam);
    }

    @RequestMapping(value = "gitLabPreReceiveHookRequest", method = RequestMethod.POST)
    public String gitLabPreReceiveHookRequest(@RequestBody GitLabPreReceiveHookParam gitLabPreReceiveHookParam) {
        return versionService.handleGitLabPreReceiveHookRequest(gitLabPreReceiveHookParam);
    }

    @RequestMapping(value = "gitLabPostReceiveHookRequest", method = RequestMethod.POST)
    public String gitLabPostReceiveHookRequest(@RequestBody GitLabPostReceiveHookParam gitLabPostReceiveHookParam) {
        return versionService.handleGitLabPostReceiveHookRequest(gitLabPostReceiveHookParam);
    }

    @RequestMapping(value = "modifySvnPassword", method = RequestMethod.POST)
    public ResultDataDTO modifySvnPassword(Long currentUserId, String userScmAccount, String userScmPassword, String entryptUserScmPassword) {
        try {
            versionService.modifySvnPassword(currentUserId, userScmAccount, userScmPassword, entryptUserScmPassword);
            return ResultDataDTO.SUCCESS("修改SVN密码配置文件成功");
        } catch (Exception e) {
            logger.error("修改SVN密码配置文件异常，异常原因：" + e.getMessage(), e);
            return ResultDataDTO.ABNORMAL("修改SVN密码配置文件异常，异常原因：" + e.getMessage());
        }
    }

    /**
     * @param token
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Description 获取当前用户所属项目组对应的系统
     * @MethodName getMyProjectSystems
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/5/27 13:38
     */
    @RequestMapping(value = "getMyProjectSystems", method = RequestMethod.POST)
    public List<Map<String, Object>> getMyProjectSystems(String token) {
        return versionService.getMyProjectSystems(token);
    }

    /**
     * @param systemId
     * @return java.util.List<cn.pioneeruniverse.dev.entity.TblSystemScmSubmit>
     * @Description 获取系统代码提交配置
     * @MethodName getSystemScmSubmitConfigs
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/6/24 16:01
     */
    @RequestMapping(value = "getSystemScmSubmitConfigs", method = RequestMethod.POST)
    public List<TblSystemScmSubmit> getSystemScmSubmitConfigs(Long systemId, Integer scmType) {
        return versionService.getSystemScmSubmitConfigs(systemId, scmType);
    }

    @RequestMapping(value = "getSystemScmSubmitRegexConfigs", method = RequestMethod.POST)
    public List<TblSystemScmSubmitRegex> getSystemScmSubmitRegexConfigs(Long systemId) {
        return versionService.getSystemScmSubmitRegexConfigs(systemId);
    }

    @RequestMapping(value = "getSystemScmSubmitRegexConfigList", method = RequestMethod.POST)
    public AjaxModel getSystemScmSubmitRegexConfigList(Long systemId) {
        try {
            return AjaxModel.SUCCESS(versionService.getSystemScmSubmitRegexConfigs(systemId));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getCodeBaseAddresses", method = RequestMethod.POST)
    public List<Map<String, Object>> getCodeBaseAddresses(Integer codeBaseType) {
        return versionService.getCodeBaseAddresses(codeBaseType);
    }

    @RequestMapping(value = "addNewCodeBase", method = RequestMethod.POST)
    public AjaxModel addNewCodeBase(HttpServletRequest request, TblSystemScmRepository tblSystemScmRepository) {
        try {
            versionService.addNewCodeBase(request, tblSystemScmRepository);
            return AjaxModel.SUCCESS(tblSystemScmRepository);
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getCodeBases", method = RequestMethod.POST)
    public AjaxModel getCodeBases(Long systemId, Integer scmType) {
        try {
            return AjaxModel.SUCCESS(versionService.getCodeBases(systemId, scmType));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getSystemVersions", method = RequestMethod.POST)
    public AjaxModel getSystemVersions(Long systemId) {
        try {
            return AjaxModel.SUCCESS(versionService.getSystemVersions(systemId));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getCommissioningWindows", method = RequestMethod.POST)
    public AjaxModel getCommissioningWindows() {
        try {
            return AjaxModel.SUCCESS(systemInfoService.getAllTblCommissioningWindow());
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "saveCodeSubmitConfig", method = RequestMethod.POST)
    public AjaxModel saveCodeSubmitConfig(HttpServletRequest request, @RequestBody TblSystemScmSubmitVo tblSystemScmSubmitVo) {
        try {
            versionService.saveCodeSubmitConfig(request, tblSystemScmSubmitVo.getTblSystemScmSubmitList());
            versionService.saveCodeSubmitRegexConfig(request, tblSystemScmSubmitVo.getTblSystemScmSubmitRegexList());
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "delCodeSubmitConfig", method = RequestMethod.POST)
    public AjaxModel delCodeSubmitConfig(HttpServletRequest request, TblSystemScmSubmit tblSystemScmSubmit) {
        try {
            versionService.delCodeSubmitConfig(request, tblSystemScmSubmit);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "delCodeSubmitRegexConfig", method = RequestMethod.POST)
    public AjaxModel delCodeSubmitRegexConfig(HttpServletRequest request, TblSystemScmSubmitRegex tblSystemScmSubmitRegex) {
        try {
            versionService.delCodeSubmitRegexConfig(request, tblSystemScmSubmitRegex);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getSystemGitLabProjects", method = RequestMethod.POST)
    public List<Project> getSystemGitLabProjects(Long systemId) {
        try {
            return versionService.getSystemGitLabProjects(systemId);
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return null;
        }
    }

    @RequestMapping(value = "saveGitLabModify", method = RequestMethod.POST)
    public AjaxModel saveGitLabModify(Project project, @RequestParam("addUsers") String addUsers, @RequestParam("delUsers") String delUsers, @RequestParam("modifyUsers") String modifyUsers) {
        try {
            List<UserDTO> addUserList = JsonUtil.fromJson(addUsers, JsonUtil.createCollectionType(ArrayList.class, UserDTO.class));
            List<UserDTO> delUserList = JsonUtil.fromJson(delUsers, JsonUtil.createCollectionType(ArrayList.class, UserDTO.class));
            List<UserDTO> modifyUserList = JsonUtil.fromJson(modifyUsers, JsonUtil.createCollectionType(ArrayList.class, UserDTO.class));
            String msg = versionService.saveGitLabModify(project, addUserList, delUserList, modifyUserList);
            return AjaxModel.SUCCESS(msg);
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "findScmRepositoryBySystemId", method = RequestMethod.POST)
    public AjaxModel findScmRepositoryBySystemId(Long systemId) {
        try {
            return AjaxModel.SUCCESS(versionService.findScmRepositoryBySystemId(systemId));
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
            return AjaxModel.FAIL(e);
        }
    }
    @RequestMapping(value = "getCodeFilesByDevTaskId", method = RequestMethod.POST)
    public Map<String, Object> getCodeFilesByDevTaskId(@RequestParam Long devTaskId) {
        return versionService.getCodeFilesByDevTaskId(devTaskId);
    }

}
