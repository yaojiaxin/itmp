package cn.pioneeruniverse.system.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.common.dto.ResultDataDTO;
import cn.pioneeruniverse.common.dto.TblUserInfoDTO;
import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.system.entity.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;
import cn.pioneeruniverse.common.utils.Md5Utils;
import cn.pioneeruniverse.system.service.company.ICompanyService;
import cn.pioneeruniverse.system.service.dept.IDeptService;
import cn.pioneeruniverse.system.service.user.IUserService;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICompanyService iCompanyService;
    @Autowired
    private IDeptService iDeptService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final static String DEFAULT_PASSWORD = "123456";

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);


    /**
     * @param userAccount
     * @param password
     * @return cn.pioneeruniverse.common.dto.ResultDataDTO
     * @Description ???????????????????????????????????????(??????????????????)
     * @MethodName login
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/1/4 16:10
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResultDataDTO login(String userAccount, String password) {
        try {
            TblUserInfo user = iUserService.checkLogin(userAccount, password);
            String token = (String) iUserService.afterLoginCheck(user);
            return ResultDataDTO.SUCCESSWITHDATA("????????????", token);
        } catch (LoginException e) {
            return ResultDataDTO.FAILURE("???????????????" + e.getMessage());
        } catch (Exception e) {
            logger.error("????????????????????????????????????" + e.getMessage(), e);
            return ResultDataDTO.ABNORMAL("????????????????????????????????????" + e.getMessage());
        }
    }


    /**
     * @param userCode
     * @param userName
     * @return cn.pioneeruniverse.common.dto.ResultDataDTO
     * @Description ?????????????????????????????????
     * @MethodName afterCasLogin
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/3/14 14:58
     */
    @RequestMapping(value = "afterCasLogin", method = RequestMethod.POST)
    public ResultDataDTO afterCasLogin(String userCode, String userName) {
        try {
            String token = (String) iUserService.afterCasLogin(userCode, userName);
            return ResultDataDTO.SUCCESSWITHDATA("????????????", token);
        } catch (Exception e) {
            logger.error("?????????????????????????????????????????????????????????????????????" + e.getMessage(), e);
            return ResultDataDTO.ABNORMAL("?????????????????????????????????????????????????????????????????????" + e.getMessage());
        }
    }


    //????????????????????????
    @RequestMapping(value = "getAllUser", method = RequestMethod.POST)
    public Map getAllUser(String FindUser, Integer rows, Integer page) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            TblUserInfo user = new TblUserInfo();
            if (StringUtils.isNotBlank(FindUser))
                user = JSONObject.parseObject(FindUser, TblUserInfo.class);
            result = iUserService.getAllUser(user, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }
        return result;
    }


    @RequestMapping(value = "getCompany", method = RequestMethod.POST)
    public List<Company> getCompany() {
        List<Company> list = null;
        try {
            list = iCompanyService.getCompany();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }
        return list;
    }

    @RequestMapping(value = "getDept", method = RequestMethod.POST)
    public List<TblDeptInfo> getDept() {
        List<TblDeptInfo> list = null;
        try {
            list = iDeptService.getAllDept();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }
        return list;
    }

    //????????????
    @RequestMapping(value = "getRoles", method = RequestMethod.POST)
    public List<TblRoleInfo> getRoles() {
        List<TblRoleInfo> list = null;
        try {
            list = iUserService.getRoles();
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }
        return list;
    }

    @RequestMapping(value = "getUser", method = RequestMethod.POST)
    public Map<String, Object> getUser(String userAccount, String userPassword) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblUserInfo user = iUserService.getUserAcc(userAccount);
            if (user != null) {
                result.put("data", JSONObject.toJSONString(user));
            } else {
                result.put("status", Constants.ITMP_RETURN_FAILURE);
                result.put("errorMessage", "??????/????????????");
                return result;
            }
        } catch (Exception e) {
            return handleException(e, "?????????????????????????????????????????????");
        }

        return result;
    }

    @RequestMapping(value = "getPreUser", method = RequestMethod.POST)
    public Map<String, Object> getPreUser(String userAccount) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            Long count = iUserService.getPreAccUser(userAccount);
            result.put("data", count == null ? 0l : count);
        } catch (Exception e) {
            return handleException(e, "????????????????????????");
        }

        return result;
    }

    @RequestMapping(value = "saveUser", method = RequestMethod.POST)
    public Map<String, Object> saveUser(@RequestBody String newUser, HttpServletRequest request) {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblUserInfo user = JSONObject.parseObject(newUser, TblUserInfo.class);
            if (user.getEmployeeNumber() != null) {
                Long id = iUserService.getIdByNum(user);
                if (id != null) {
                    result.put("status", "repeat");
                    return result;
                }
            }
            if (user.getUserAccount() != null) {
                Long id = iUserService.getIdByUserAccount(user);
                if (id != null) {
                    result.put("status", "repeat2");
                    return result;
                }
            }

//            user.setUserPassword(passwordEncoder.encode(Md5Utils.md5(DEFAULT_PASSWORD)));
            user.setUserPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setStatus(1);
            List<TblUserRole> list = new ArrayList<TblUserRole>();
            List<Long> roleIds = user.getRoleIds();
            if (roleIds != null && roleIds.size() != 0) {
                for (Long roleId : roleIds) {
                    TblUserRole userRole = new TblUserRole();
                    userRole.setCreateBy(CommonUtil.getCurrentUserId(request));
                    userRole.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
                    userRole.setRoleId(roleId);
                    userRole.setStatus(1);
                    list.add(userRole);
                }
            }
            iUserService.insertUser(user, list);
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }

        return result;
    }

    @RequestMapping(value = "updateUser", method = RequestMethod.POST)
    public Map<String, Object> updateUser(@RequestBody String newUser, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblUserInfo user = JSONObject.parseObject(newUser, TblUserInfo.class);
            if (user.getEmployeeNumber() != null) {
                Long id = iUserService.getIdByNum(user);
                if (id != null) {
                    result.put("status", "repeat");
                    return result;
                }
            }
            if (user.getUserAccount() != null) {
                Long id = iUserService.getIdByUserAccount(user);
                if (id != null) {
                    result.put("status", "repeat2");
                    return result;
                }
            }
            List<TblUserRole> list = new ArrayList<TblUserRole>();
            List<Long> roleIds = user.getRoleIds();
            if (roleIds != null && roleIds.size() != 0) {
                for (Long roleId : roleIds) {
                    TblUserRole userRole = new TblUserRole();
                    userRole.setCreateBy(CommonUtil.getCurrentUserId(request));
                    userRole.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
                    userRole.setRoleId(roleId);
                    userRole.setStatus(1);
                    list.add(userRole);
                }
            }
            iUserService.updateUser(user, list);
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }

        return result;
    }

    @RequestMapping(value = "delUser", method = RequestMethod.POST)
    public Map<String, Object> delUser(String userIds, Long lastUpdateBy) {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            if (StringUtils.isNotBlank(userIds)) {
                String[] ids = userIds.split(",");
                List<Long> list = new ArrayList<Long>();
                for (String id : ids) {
                    list.add(Long.valueOf(id));
                }
                iUserService.delUser(list, lastUpdateBy);
            }
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }

        return result;
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public Map<String, Object> updatePassword(String oldPass, String newPass, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            Long id = CommonUtil.getCurrentUserId(request);
            TblUserInfo tui = iUserService.findUserById(id);
            if (tui != null) {
                //String md5Pass=Md5Utils.md5(oldPass);
                //if (!passwordEncoder.matches(md5Pass, tui.getUserPassword())) {
                if (!passwordEncoder.matches(oldPass, tui.getUserPassword())) {
                    result.put("status", Constants.ITMP_RETURN_FAILURE);
                    result.put("errorMessage", "?????????????????????");
                    return result;
                }
                TblUserInfo newUser = new TblUserInfo();
                newUser.setId(id);
                newUser.setLastUpdateBy(id);
                newUser.setUserPassword(passwordEncoder.encode(newPass));
                iUserService.updatePassword(newUser);
            } else {
                result.put("status", Constants.ITMP_RETURN_FAILURE);
                result.put("errorMessage", "?????????????????????");
                return result;
            }
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }

        return result;
    }

    @RequestMapping(value = "resetPassword", method = RequestMethod.POST)
    public Map<String, Object> resetPassword(Long id, HttpServletRequest request) {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblUserInfo user = new TblUserInfo();
            Long lastUpdateId = CommonUtil.getCurrentUserId(request);
            //user.setUserPassword(passwordEncoder.encode(Md5Utils.md5(DEFAULT_PASSWORD)));
            user.setUserPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setId(id);
            user.setLastUpdateBy(lastUpdateId);
            iUserService.resetPassword(user);
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }

        return result;
    }

    @RequestMapping(value = "findUserById", method = RequestMethod.POST)
    public Map<String, Object> findUserById(Long userId) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblUserInfo user = iUserService.findUserById(userId);
            result.put("data", JSONObject.toJSONString(user));
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }
        return result;
    }

    @RequestMapping(value = "findUserById1", method = RequestMethod.POST)
    public Map<String, Object> findUserById1(Long userId) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblUserInfo user = iUserService.findUserById(userId);
            result.put("data", user);
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }
        return result;
    }

    @RequestMapping(value = "getUserWithRoles", method = RequestMethod.POST)
    public Map<String, Object> getUserWithRoles(String userAccount) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            logger.info("server.port=8093");
            TblUserInfo user = iUserService.getUserRoleByAcc(userAccount);
            result.put("data", JSONObject.toJSONString(user));
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }
        return result;
    }

    //add by ztt
    @RequestMapping(value = "getUserNoCon", method = RequestMethod.POST)
    public List<TblUserInfo> getUser() {
        return iUserService.getUser();
    }

    @ResponseBody
    @RequestMapping(value = "getExcelAllUser")
    public void getExcelUser(String excelData, HttpServletResponse response, HttpServletRequest request) {
        List<TblUserInfo> list = new ArrayList<TblUserInfo>();
        TblUserInfo tblUserInfo = new TblUserInfo();
        try {
            Long Userd = CommonUtil.getCurrentUserId(request);
            if (StringUtils.isNotBlank(excelData))
                tblUserInfo = JSONObject.parseObject(excelData, TblUserInfo.class);
            tblUserInfo.setId(Userd);
            list = iUserService.getExcelAllUser(tblUserInfo);

            String[] title = {"??????", "?????????", "????????????", "????????????", "??????", "????????????"};
            //excel?????????
            SimpleDateFormat sformat = new SimpleDateFormat("yyyy???MM???dd???HH???mm???ss???");
            long now = System.currentTimeMillis();
            String day = sformat.format(now);
            String fileName = "?????????" + day + ".xls";
            //sheet???
            String sheetName = "?????????";
            String[][] content = new String[list.size()][];
            SimpleDateFormat simdate = new SimpleDateFormat("yyyy-MM-dd");
            if (list.size() > 0 && list != null) {
                for (int i = 0; i < list.size(); i++) {
                    content[i] = new String[title.length];
                    content[i][0] = list.get(i).getUserName();
                    content[i][1] = list.get(i).getUserAccount();
                    content[i][2] = list.get(i).getDeptName();
                    Integer taskStatus = list.get(i).getUserStatus();
                    content[i][3] = taskStatus == 1 ? "??????" : "??????";
                    List<TblRoleInfo> role = list.get(i).getUserRoles();
                    String roleName = "";
                    for (int j = 0; j < role.size(); j++) {
                        roleName += role.get(j).getRoleName() + ",";
                    }
                    if (roleName.length() > 0) {
                        roleName = roleName.substring(0, roleName.length() - 1);
                    }

                    content[i][4] = roleName;
                    Date date = list.get(i).getEntryDate();
                    if (null != date) {
                        content[i][5] = simdate.format(list.get(i).getEntryDate());
                    }
                }
            }
            //??????HSSFWorkbook
            HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }
    }
    //?????????????????????

    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("gbk"), "iso8859-1"));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }

    @RequestMapping(value = "getCodeBaseUserDetailByUserScmAccount", method = RequestMethod.POST)
    public TblUserInfoDTO getCodeBaseUserDetailByUserScmAccount(String userScmAccount) {
        return iUserService.getCodeBaseUserDetailByUserScmAccount(userScmAccount);
    }

    @RequestMapping(value = "getUserPageForCodeBase", method = RequestMethod.POST)
    public BootStrapTablePage<TblUserInfoDTO> getUserPageForCodeBase(TblUserInfoDTO tblUserInfoDTO, HttpServletRequest request, HttpServletResponse response) {
        BootStrapTablePage<TblUserInfoDTO> bootStrapTablePage = iUserService.getUserPageForCodeBase(new BootStrapTablePage<>(request, response), tblUserInfoDTO);
        return bootStrapTablePage;
    }

    @RequestMapping(value = "getDevUserPageForCodeReview", method = RequestMethod.POST)
    public BootStrapTablePage<TblUserInfoDTO> getDevUserPageForCodeReview(TblUserInfoDTO tblUserInfoDTO, HttpServletRequest request, HttpServletResponse response) {
        BootStrapTablePage<TblUserInfoDTO> bootStrapTablePage = iUserService.getDevUserPageForCodeReview(new BootStrapTablePage<>(request, response), tblUserInfoDTO, CommonUtil.getCurrentUserId(request));
        return bootStrapTablePage;
    }

    @RequestMapping(value = "getDevUserNameAndReviewersNameForCodeReivew", method = RequestMethod.POST)
    public Map<String, String> getDevUserNameAndReviewersNameForCodeReivew(Long devUserId, String codeReviewUserIds) {
        return iUserService.getDevUserNameAndReviewersNameForCodeReivew(devUserId, codeReviewUserIds);
    }

    /**
     * ??????????????????????????????bootstrap??????
     *
     * @param userInfo
     * @return
     */
    @RequestMapping(value = "getAllUserModal", method = RequestMethod.POST)
    public BootStrapTablePage<TblUserInfo> getAllUserModal(TblUserInfo userInfo,
                                                           Long notWithUserID,
                                                           Long systemId,
                                                           String projectIds,
                                                           String userPost,
                                                           HttpServletRequest request, HttpServletResponse response
                                             /*  @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
                                               @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize*/) {
      /*  Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = iUserService.getAllUserModal(userInfo, systemId, notWithUserID, projectIds,userPost, pageNumber, pageSize);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }
        return result;*/
        BootStrapTablePage<TblUserInfo> bootStrapTablePage = iUserService.getAllUserModal(new BootStrapTablePage<>(request, response), userInfo, systemId, notWithUserID, projectIds, userPost);
        return bootStrapTablePage;
    }

    /**
     * ??????????????????????????????bootstrap??????
     * ???????????????????????????
     */
    @RequestMapping(value = "getAllUserModal2", method = RequestMethod.POST)
    public BootStrapTablePage<TblUserInfo> getAllUserModal2(TblUserInfo userInfo,
                                                            String projectIds,
                                              /*  @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,*/
                                                            HttpServletRequest request, HttpServletResponse response) {
       /* Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<TblUserInfo> userList = iUserService.getAllUserModal2(userInfo, projectIds, pageNumber, pageSize);
            Integer count = iUserService.getAllUserModalCount2(userInfo, projectIds);

            result.put("rows", userList);
            result.put("total", count);
        } catch (Exception e) {
            return handleException(e, "??????????????????");
        }
        return result;*/

        BootStrapTablePage<TblUserInfo> bootStrapTablePage = iUserService.getAllUserModal2(new BootStrapTablePage<>(request, response), userInfo, projectIds);
        return bootStrapTablePage;
    }


    @RequestMapping(value = "getAllDevUser")
    public Map<String, Object> getAllDevUser(String deptName, String companyName, String userName, Integer devID, Integer notWithUserID, Integer pageNumber, Integer pageSize, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        Long id = CommonUtil.getCurrentUserId(request);
        //tblDevTask.setId(id);
        try {
            List<Map<String, Object>> list = iUserService.getAllDevUser(deptName, companyName, userName, notWithUserID, devID, pageNumber, pageSize);
            List<Map<String, Object>> list2 = iUserService.getAllDevUser(deptName, companyName, userName, notWithUserID, devID, null, null);
            result.put("rows", list);
            result.put("total", list2.size());
        } catch (Exception e) {

            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
            return handleException(e, "????????????????????????");
        }
        return result;

    }

    @RequestMapping(value = "getAllTextUser")
    public Map<String, Object> getAllTextUser(TblUserInfo tblDevTask, Integer devID, Integer notWithUserID, Integer pageNumber, Integer pageSize, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        Long id = CommonUtil.getCurrentUserId(request);
        tblDevTask.setId(id);
        try {
            List<Map<String, Object>> list = iUserService.getAllTestUser(tblDevTask, notWithUserID, devID, pageNumber, pageSize);
            List<Map<String, Object>> list2 = iUserService.getAllTestUser(tblDevTask, notWithUserID, devID, null, null);
            result.put("rows", list);
            result.put("total", list2.size());
        } catch (Exception e) {

            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
            return handleException(e, "????????????????????????");
        }
        return result;


    }

    @RequestMapping(value = "updateInnerUserData", method = RequestMethod.POST, produces = "application/json;utf-8")
    public void updateInnerUserData(@RequestBody String userData, HttpServletResponse response) {
        Integer status = 0;
        logger.info("??????????????????????????????" + userData);
        try {
            if (StringUtils.isNotBlank(userData)) {
                String password = passwordEncoder.encode(DEFAULT_PASSWORD);
                List<Map<String, Object>> listMap = iUserService.itmpInnerUserData(userData, password);
                for (Map<String, Object> result : listMap) {
                    String user = result.get("userInfo").toString();
                    TblUserInfo userInfo = JSON.parseObject(user, TblUserInfo.class);
                    status = iUserService.tmpUserData(userInfo);
                }
                if (status > 0) {
                    Map<String, Object> head = new HashMap<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("consumerSeqNo", "itmgr");
                    map.put("status", 0);
                    map.put("seqNo", "");
                    map.put("providerSeqNo", "");
                    map.put("esbCode", "");
                    map.put("esbMessage", "");
                    map.put("appCode", "0");
                    map.put("appMessage", "??????????????????????????????");
                    head.put("responseHead", map);

                    PrintWriter writer = response.getWriter();
                    writer.write(new JSONObject(head).toJSONString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("????????????????????????" + ":" + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "updateExtUserData", method = RequestMethod.POST, produces = "application/json;utf-8")
    public void updateExtUserData(@RequestBody String userData, HttpServletResponse response) {
        Integer status = 0;
        logger.info("??????????????????????????????" + userData);
        try {
            if (StringUtils.isNotBlank(userData)) {
                String password = passwordEncoder.encode(DEFAULT_PASSWORD);
                List<Map<String, Object>> listMap = iUserService.itmpExtUserData(userData, password);
                for (Map<String, Object> result : listMap) {
                    String user = result.get("userInfo").toString();
                    TblUserInfo userInfo = JSON.parseObject(user, TblUserInfo.class);
                    status = iUserService.tmpUserData(userInfo);
                }
                if ((listMap.size() > 0 && status > 0) || (listMap.size() == 0 && status == 0)) {
                    Map<String, Object> head = new HashMap<>();
                    Map<String, Object> map = new HashMap<>();
                    map.put("consumerSeqNo", "itmgr");
                    map.put("status", 0);
                    map.put("seqNo", "");
                    map.put("providerSeqNo", "");
                    map.put("esbCode", "");
                    map.put("esbMessage", "");
                    map.put("appCode", "0");
                    map.put("appMessage", "??????????????????????????????");
                    head.put("responseHead", map);

                    PrintWriter writer = response.getWriter();
                    writer.write(new JSONObject(head).toJSONString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("????????????????????????" + ":" + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "selectById")
    public Map<String, Object> selectById(String deptName, String companyName, String userName, Long notWithUserID, Integer pageNumber, Integer pageSize, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        Long userID = CommonUtil.getCurrentUserId(request);
        try {
            List<Map<String, Object>> list = iUserService.selectById(deptName, companyName, userName, userID, notWithUserID, pageNumber, pageSize);
            List<Map<String, Object>> list2 = iUserService.selectById(deptName, companyName, userName, userID, notWithUserID, null, null);
            result.put("rows", list);
            result.put("total", list2.size());
        } catch (Exception e) {

            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
            return handleException(e, "????????????????????????");
        }
        return result;
    }


    /**
     * ??????????????????bootstrap??????
     * ???????????????????????????
     */
    @RequestMapping(value = "getAllDept", method = RequestMethod.POST)
    public Map<String, Object> getAllUserModal2(TblDeptInfo tblDeptInfo,
                                                @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<TblDeptInfo> deptList = iUserService.getAllDept(tblDeptInfo, pageNumber, pageSize);
            List<TblDeptInfo> List = iUserService.getAllDept(tblDeptInfo, null, null);
            result.put("rows", deptList);
            result.put("total", List.size());
        } catch (Exception e) {
            return handleException(e, "????????????????????????");
        }
        return result;
    }

    /**
     * @param userId
     * @return java.lang.String
     * @Description ??????svn????????????
     * @MethodName createSvnAccount
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/4/9 15:47
     */
    @RequestMapping(value = "createSvnAccountPassword", method = RequestMethod.POST)
    public Map<String, String> createSvnAccountPassword(Long userId) {
        return iUserService.createSvnAccountPassword(userId);
    }


    /**
     * @param oldPassword
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description ??????????????????svn????????????????????????
     * @MethodName checkMyOldSvnPassword
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/4/29 13:45
     */
    @RequestMapping(value = "checkMyOldSvnPassword", method = RequestMethod.POST)
    public AjaxModel checkMyOldSvnPassword(@RequestParam("oldPassword") String oldPassword, HttpServletRequest request) {
        try {
            return AjaxModel.SUCCESS(iUserService.checkMyOldSvnPassword(oldPassword, Long.valueOf(CommonUtil.getCurrentUser(request).get("id").toString())));
        } catch (Exception e) {
            logger.error("???????????????SVN???????????????????????????????????????" + e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    /**
     * @param newPassword
     * @param request
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description ??????svn??????
     * @MethodName updateMySvnPassword
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/4/30 11:35
     */
    @RequestMapping(value = "updateMySvnPassword", method = RequestMethod.POST)
    public AjaxModel updateMySvnPassword(@RequestParam("newPassword") String newPassword, HttpServletRequest request) {
        try {
            iUserService.updateMySvnPassword(newPassword, request);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error("????????????SVN??????????????????????????????" + e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "getAllproject", method = RequestMethod.POST)
    public List<Map<String, Object>> getAllproject() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = iUserService.getAllproject();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("mes:" + e.getMessage(), e);
        }
        return list;
    }

    @RequestMapping(value = "getUserInfoByUserIds", method = RequestMethod.POST)
    public List<TblUserInfoDTO> getUserInfoByUserIds(List<Long> userIds) {
        return iUserService.getBatchUserInfoByIds(userIds);
    }

}
