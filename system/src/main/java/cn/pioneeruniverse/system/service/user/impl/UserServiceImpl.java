package cn.pioneeruniverse.system.service.user.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.dto.ResultDataDTO;
import cn.pioneeruniverse.common.dto.TblUserInfoDTO;
import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.system.dao.mybatis.dept.DeptDao;
import cn.pioneeruniverse.system.dao.mybatis.role.RoleDao;
import cn.pioneeruniverse.system.dao.mybatis.role.UserRoleDao;
import cn.pioneeruniverse.system.dao.mybatis.user.UserDao;
import cn.pioneeruniverse.system.entity.TblDeptInfo;
import cn.pioneeruniverse.system.entity.TblMenuButtonInfo;
import cn.pioneeruniverse.system.entity.TblRoleInfo;
import cn.pioneeruniverse.system.entity.TblUserInfo;
import cn.pioneeruniverse.system.entity.TblUserRole;
import cn.pioneeruniverse.system.feignInterface.SystemToDevManageInterface;
import cn.pioneeruniverse.system.service.menu.IMenuService;
import cn.pioneeruniverse.system.service.role.IRoleService;
import cn.pioneeruniverse.system.service.role.IUserRoleService;
import cn.pioneeruniverse.system.service.user.IUserService;

@Service("iUserService")
public class UserServiceImpl extends ServiceImpl<UserDao, TblUserInfo> implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    private UserDao userDao;

    @Autowired
    private DeptDao deptDao;
    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IMenuService iMenuService;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private SystemToDevManageInterface systemToDevManageInterface;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Value("${svn.default.password}")
    private String svnDefaultPassword;


    private static Pattern urlPattern = Pattern.compile("[/[\\w]{1,}]{1,}(\\?.+?)*");

    @Override
    public Map getAllUser(TblUserInfo user, Integer pageIndex, Integer pageSize) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (pageIndex != null && pageSize != null) {

            PageHelper.startPage(pageIndex, pageSize);
            List<TblUserInfo> list = userDao.getAllUser(user);
            PageInfo<TblUserInfo> pageInfo = new PageInfo<TblUserInfo>(list);
            result.put("rows", list);
            result.put("records", pageInfo.getTotal());
            result.put("total", pageInfo.getPages());
            result.put("page", pageIndex < pageInfo.getPages() ? pageIndex : pageInfo.getPages());
            return result;

        } else {
            result.put("rows", userDao.getAllUser(user));
        }

        return result;
    }


    @Override
    public List<TblUserInfo> getExcelAllUser(TblUserInfo user) {
        List<TblUserInfo> list = userDao.getAllUser(user);
        return list;
    }


    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateUser(TblUserInfo user, List<TblUserRole> list) {
        userDao.updateUser(user);
        iUserRoleService.delUserRoleByUserId(user.getId());
        if (list != null && !list.isEmpty()) {
            for (TblUserRole userRole : list) {
                userRole.setUserId(user.getId());
            }
            iUserRoleService.insertUserRole(list);
        }
        SpringContextHolder.getBean(IUserService.class).updateTmpUser(user, list);
    }

    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateTmpUser(TblUserInfo user, List<TblUserRole> list) {
        // TODO Auto-generated method stub
        userDao.updateUser(user);
        iUserRoleService.delUserRoleByUserId(user.getId());
        if (list != null && !list.isEmpty()) {
            for (TblUserRole userRole : list) {
                userRole.setUserId(user.getId());
            }
            iUserRoleService.insertUserRole(list);
        }
    }

    @Override
    public TblUserInfo getUserAcc(String userAccount) {
        return userDao.getUserAcc(userAccount);
    }

    @Override
    public TblUserInfo findUserById(Long id) {
        TblUserInfo user = userDao.findUserById(id);
        List<Long> roleIds = userRoleDao.findRoleIdByUserId(id);
        user.setRoleIds(roleIds);
        return user;
    }

    @Override
    public TblUserInfo getUserRoleByAcc(String userAccount) {
        return userDao.getUserRoleByAcc(userAccount);
    }

    @Override
    @Transactional(readOnly = false)
    public void updatePassword(TblUserInfo user) {
        userDao.updatePassword(user);
    }

    @Override
    @Transactional(readOnly = false)
    public void resetPassword(TblUserInfo user) {
        userDao.resetPassword(user);
    }

    @Override
    public Long getPreAccUser(String userAccount) {
        return userDao.getPreAccUser(userAccount);
    }


    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertUser(TblUserInfo user, List<TblUserRole> list) {
//        String maxEmpNo = this.selectMaxEmpNo();
//        String empNo = Constants.ITMP_EMP_PREFIX + "00001";
//        if (StringUtils.isNotBlank(maxEmpNo)) {
//            Integer no = Integer.valueOf(maxEmpNo.substring(Constants.ITMP_EMP_PREFIX.length())) + 1;
//            empNo = Constants.ITMP_EMP_PREFIX + String.format("%05d", no);
//        }
//        user.setEmployeeNumber(empNo);
        userDao.insertUser(user);
        if (list != null && !list.isEmpty()) {
            for (TblUserRole userRole : list) {
                userRole.setUserId(user.getId());
            }
            iUserRoleService.insertUserRole(list);
        }
        SpringContextHolder.getBean(IUserService.class).insertTmpUser(user, list);
    }

    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void insertTmpUser(TblUserInfo user, List<TblUserRole> list) {
        // TODO Auto-generated method stub
        userDao.insertUser(user);
        if (list != null && !list.isEmpty()) {
            for (TblUserRole userRole : list) {
                userRole.setUserId(user.getId());
            }
            iUserRoleService.insertUserRole(list);
        }
    }


    @Override
    @Transactional(readOnly = false)
    public void delUser(List<Long> userIds, Long lastUpdateBy) {
        userDao.delUserByIds(userIds, lastUpdateBy);
        iUserRoleService.delUserRoleByUserIds(userIds, lastUpdateBy);
    }

    @Override
    public String selectMaxEmpNo() {
        return userDao.selectMaxEmpNo();
    }

    @Override
    public List<TblUserInfo> getUser() {
        return userDao.getUser();
    }

    @Override
    @Transactional(readOnly = true)
    public TblUserInfoDTO getCodeBaseUserDetailByUserScmAccount(String userScmAccount) {
        return userDao.getCodeBaseUserDetailByUserScmAccount(userScmAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public BootStrapTablePage<TblUserInfoDTO> getUserPageForCodeBase(BootStrapTablePage<TblUserInfoDTO> bootStrapTablePage, TblUserInfoDTO tblUserInfoDTO) {
        PageHelper.startPage(bootStrapTablePage.getPageNumber(), bootStrapTablePage.getPageSize());
        List<TblUserInfoDTO> userInfoDTOList = userDao.getUserListForCodeBase(tblUserInfoDTO);
        PageInfo<TblUserInfoDTO> pageInfo = new PageInfo<TblUserInfoDTO>(userInfoDTOList);
        bootStrapTablePage.setTotal((int) pageInfo.getTotal());
        bootStrapTablePage.setRows(pageInfo.getList());
        return bootStrapTablePage;
    }

    @Override
    @Transactional(readOnly = true)
    public BootStrapTablePage<TblUserInfoDTO> getDevUserPageForCodeReview(BootStrapTablePage<TblUserInfoDTO> bootStrapTablePage, TblUserInfoDTO tblUserInfoDTO, Long currentUserId) {
        PageHelper.startPage(bootStrapTablePage.getPageNumber(), bootStrapTablePage.getPageSize());
        List<TblUserInfoDTO> userInfoDTOList = userDao.getUserListForCodeReview(tblUserInfoDTO, currentUserId);
        PageInfo<TblUserInfoDTO> pageInfo = new PageInfo<TblUserInfoDTO>(userInfoDTOList);
        bootStrapTablePage.setTotal((int) pageInfo.getTotal());
        bootStrapTablePage.setRows(pageInfo.getList());
        return bootStrapTablePage;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getDevUserNameAndReviewersNameForCodeReivew(Long devUserId, String codeReviewUserIds) {
        Map<String, String> result = new HashMap<>();
        if (devUserId != null) {
            result.put("devUserName", userDao.getUserNameById(devUserId));
        }
        if (StringUtils.isNotEmpty(codeReviewUserIds)) {
            result.put("codeReviewUserNames", userDao.getUserNamesByUserIds(codeReviewUserIds));
        }
        return result;
    }

    /**
     * 查询所有的用户
     *
     * @param userInfo
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public BootStrapTablePage<TblUserInfo> getAllUserModal(BootStrapTablePage<TblUserInfo> bootStrapTablePage, TblUserInfo userInfo, Long systemId, Long notWithUserID, String projectIds, String userPost) {
        Map<String, Object> map = new HashMap<>();
        map.put("notWithUserID", notWithUserID);
        map.put("user", userInfo);
        map.put("systemId", systemId);
        map.put("projectIds", projectIds);
        map.put("userPost", userPost);
        PageHelper.startPage(bootStrapTablePage.getPageNumber(), bootStrapTablePage.getPageSize());
        List<TblUserInfo> userInfoDTOList = userDao.getAllUserModal(map);
        PageInfo<TblUserInfo> pageInfo = new PageInfo<TblUserInfo>(userInfoDTOList);
        bootStrapTablePage.setTotal((int) pageInfo.getTotal());
        bootStrapTablePage.setRows(pageInfo.getList());
        return bootStrapTablePage;
    }


    @Override
    @Transactional(readOnly = true)
    public TblUserInfo checkLogin(String userAccount, String password) throws AccountException, CredentialException {
        TblUserInfo user = userDao.getUserAcc(userAccount);
        if (user == null) {
            throw new AccountException("用户名不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            throw new CredentialException("密码错误");
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Object afterLoginCheck(TblUserInfo user) {
        //生成当前已验证登录成功用户token
        String token = JWTTokenUtils.createJavaWebToken(new HashMap<String, Object>() {{
            put("userAccount", user.getUserAccount());
            put("userName", user.getUserName());
            put("id", user.getId());
            put("uuId", UUID.randomUUID().toString().replaceAll("-", ""));
        }});
        //绑定菜单,角色权限
        //获取用户所有菜单权限
        user.addStringPermissionUrls(Constants.System.INDEX_URL);
        List<TblMenuButtonInfo> menuList = iMenuService.getUserAllMenuButton(user.getId());
        if (CollectionUtil.isNotEmpty(menuList)) {
            for (TblMenuButtonInfo menuButtonInfo : menuList) {
                if (StringUtils.isNotBlank(menuButtonInfo.getMenuButtonCode())) {
                    for (String permission : StringUtils.split(menuButtonInfo.getMenuButtonCode(), ",")) {
                        user.addStringPermissions(permission);
                    }
                }

                if (StringUtils.isNotBlank(menuButtonInfo.getUrl())) {
                    for (String url : StringUtils.split(menuButtonInfo.getUrl(), ",")) {
                        if (StringUtils.isNotBlank(url) && urlPattern.matcher(url.substring(2)).matches()) {
                            user.addStringPermissionUrls(url.substring(2));
                        }
                    }
                }
            }
        }
        user.addRoles("user");
        //获取用户所有角色
        List<TblRoleInfo> roleList = iRoleService.getUserAllRole(user.getId());
        if (CollectionUtil.isNotEmpty(roleList)) {
            for (TblRoleInfo roleInfo : roleList) {
                if (StringUtils.isNotBlank(roleInfo.getRoleCode())) {
                    user.addRoles(roleInfo.getRoleCode());
                }
            }
        }
        redisUtils.set(token, user, Constants.ITMP_TOKEN_TIMEOUT);
        return token;
    }

    @Override
    @Transactional(readOnly = true)
    public Object afterCasLogin(String userCode, String userName) throws AccountException {
        TblUserInfo user = userDao.getUserAcc(userCode);//大地人员工号即平台登录账号
        if (user == null) {
            throw new AccountException("无法找到账号(" + userCode + "),用户名(" + userName + ")对应的用户");
        }
        String token = JWTTokenUtils.createJavaWebToken(new HashMap<String, Object>() {{
            put("userAccount", userCode);
            put("userName", userName);
            put("id", user.getId());
            put("uuId", UUID.randomUUID().toString().replaceAll("-", ""));
        }});

        //绑定菜单,角色权限
        //获取用户所有菜单权限
        user.addStringPermissionUrls(Constants.System.INDEX_URL);
        List<TblMenuButtonInfo> menuList = iMenuService.getUserAllMenuButton(user.getId());
        if (CollectionUtil.isNotEmpty(menuList)) {
            for (TblMenuButtonInfo menuButtonInfo : menuList) {
                if (StringUtils.isNotBlank(menuButtonInfo.getMenuButtonCode())) {
                    for (String permission : StringUtils.split(menuButtonInfo.getMenuButtonCode(), ",")) {
                        user.addStringPermissions(permission);
                    }
                }
                if (StringUtils.isNotBlank(menuButtonInfo.getUrl())) {
                    for (String url : StringUtils.split(menuButtonInfo.getUrl(), ",")) {
                        if (StringUtils.isNotBlank(url) && urlPattern.matcher(url.substring(2)).matches()) {
                            user.addStringPermissionUrls(url.substring(2));
                        }
                    }
                }
            }
        }
        user.addRoles("user");
        //获取用户所有角色
        List<TblRoleInfo> roleList = iRoleService.getUserAllRole(user.getId());
        if (CollectionUtil.isNotEmpty(roleList)) {
            for (TblRoleInfo roleInfo : roleList) {
                if (StringUtils.isNotBlank(roleInfo.getRoleCode())) {
                    user.addRoles(roleInfo.getRoleCode());
                }
            }
        }
        redisUtils.set(token, user, Constants.ITMP_TOKEN_TIMEOUT);
        return token;
    }


    @Override
    public List<Map<String, Object>> getAllDevUser(String deptName, String companyName, String userName, Integer notWithUserID, Integer devID, Integer pageNumber, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        map.put("deptName", deptName);
        map.put("companyName", companyName);
        map.put("userName", userName);
        map.put("notWithUserID", notWithUserID);
        map.put("devID", devID);
        return userDao.getAllDevUser(map);
    }

    /**
     * 处理内部数据用户信息
     *
     * @param userData json字符串
     * @param password 默认密码123456
     */
    @Override
    @Transactional(readOnly = false)
    public List<Map<String, Object>> itmpInnerUserData(String userData, String password) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        Map<String, Object> result1 = new HashMap<>();

        Map<String, Object> result = jsonToMap(userData);
        /*String requestBody = result.get("requestBody").toString();
        List<Map<String,Object>> listObjectSec = JSONArray.parseObject(requestBody,List.class);
    	for(Map<String,Object> map:listObjectSec) {*/
        TblUserInfo userInfo = new TblUserInfo();
        String userName = result.get("USERNAME").toString();
        String userCode = result.get("USERCODE").toString();
        String deptCode = result.get("COMCODE").toString();
        String status = result.get("VALIDSTATUS").toString();
        // 部门：根据部门编码找到id
        TblDeptInfo deptInfo = deptDao.getDeptByCode(deptCode);
        //根据编码找到id
        TblUserInfo user = userDao.getAllUserRoleByAcc(userCode);

        userInfo.setUserAccount(userCode);
        userInfo.setUserName(userName);
        userInfo.setEmployeeNumber(userCode);
        userInfo.setDeptId(deptInfo == null ? null : deptInfo.getId());
        userInfo.setUserType(1);
        userInfo.setUserStatus(status.equals("0") ? 2 : 1);
        userInfo.setLastUpdateBy(1L);
        userInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));

        if (user == null) {
            userInfo.setCreateBy(1L);
            userInfo.setUserPassword(password);
            userInfo.setCreateDate(new Timestamp(new Date().getTime()));
            userDao.insertUser(userInfo);
        } else {
            userInfo.setId(user.getId());
            userDao.updateById(userInfo);
        }
        result1.put("userInfo", JSONObject.toJSONString(userInfo));
        listMap.add(result1);
        /*}*/
        return listMap;
    }


    /**
     * 处理外部用户信息
     *
     * @param userData json字符串
     * @param password 默认密码123456
     */
    @Override
    @Transactional(readOnly = false)
    public List<Map<String, Object>> itmpExtUserData(String userData, String password) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        Map<String, Object> result1 = new HashMap<>();
        Map<String, Object> result = jsonToMap(userData);

        TblUserInfo userInfo = new TblUserInfo();
        String userName = result.get("USERNAME").toString();
        String userCode = result.get("USERCODE").toString();
        String deptCode = result.get("COMCODE").toString();
        String status = result.get("VALIDSTATUS").toString();
        if (userCode != null && (userCode.substring(0, 4).equals("itop") || userCode.substring(0, 4).equals("oper") || userCode.substring(0, 4).equals("sqxm"))) {
            // 部门：根据部门编码找到id
            TblDeptInfo deptInfo = deptDao.getDeptByCode(deptCode);
            //根据编码找到id
            TblUserInfo user = userDao.getAllUserRoleByAcc(userCode);

            userInfo.setUserAccount(userCode);
            userInfo.setUserName(userName);
            userInfo.setEmployeeNumber(userCode);
            userInfo.setDeptId(deptInfo == null ? null : deptInfo.getId());
            userInfo.setUserType(2);
            userInfo.setUserStatus(status.equals("0") ? 2 : 1);
            userInfo.setLastUpdateBy(1L);
            userInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));

            if (user == null) {
                userInfo.setCreateBy(1L);
                userInfo.setUserPassword(password);
                userInfo.setCreateDate(new Timestamp(new Date().getTime()));
                userDao.insertUser(userInfo);
            } else {
                userInfo.setId(user.getId());
                userDao.updateById(userInfo);
            }
            result1.put("userInfo", JSONObject.toJSONString(userInfo));
            listMap.add(result1);
        }
        return listMap;
    }

    //json转map
    public static Map<String, Object> jsonToMap(String str) {
        Map<String, Object> mapTypes = JSON.parseObject(str);
        return mapTypes;
    }

    @Override
    public List<Map<String, Object>> getAllTestUser(TblUserInfo tblUserInfo, Integer notWithUserID, Integer devID,
                                                    Integer pageNumber, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        map.put("tblUserInfo", tblUserInfo);
        map.put("notWithUserID", notWithUserID);
        map.put("devID", devID);
        return userDao.getAllTestUser(map);
    }

    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly = false)
    public int tmpUserData(TblUserInfo tblUserInfo) {
        int status = 0;
        TblUserInfo user = userDao.findUserById(tblUserInfo.getId());
        if (user == null) {
            status = userDao.insert(tblUserInfo);
        } else {
            status = userDao.updateById(tblUserInfo);
        }
        return status;
    }

    @Override
    public List<Map<String, Object>> selectById(String deptName, String companyName, String userName, Long userId, Long notWithUserID, Integer pageNumber, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        map.put("deptName", deptName);
        map.put("companyName", companyName);
        map.put("userName", userName);
        map.put("id", userId);
        map.put("notWithUserID", notWithUserID);
        return userDao.selectById(map);
    }

    @Override
    public List<Map<String, Object>> selectByProjectId(TblUserInfo tblUserInfo, Long notWithUserID, Integer pageNumber, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        map.put("tblUserInfo", tblUserInfo);
        map.put("notWithUserID", notWithUserID);
        return userDao.selectByProjectId(map);
    }

    @Override
    public List<TblUserInfo> getAllUserModal2(TblUserInfo userInfo, String projectIds, Integer pageNumber,
                                              Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        if (pageNumber != null && pageSize != null) {
            map.put("start", (pageNumber - 1) * pageSize);
            map.put("pageSize", pageSize);
        } else {
            map.put("start", null);
            map.put("pageSize", null);
        }
        map.put("projectIds", projectIds);
        map.put("user", userInfo);
        return userDao.getAllUserModal2(map);
    }

    @Override
    public Integer getAllUserModalCount2(TblUserInfo userInfo, String projectIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectIds", projectIds);
        map.put("user", userInfo);
        return userDao.getAllUserModalCount2(map);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkMyOldSvnPassword(String oldPassword, Long currentUserId) throws IOException, NoSuchAlgorithmException {
        return PasswordUtil.validateForAES(oldPassword, userDao.getMySvnPassword(currentUserId));
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateMySvnPassword(String newPassword, HttpServletRequest request) throws NoSuchAlgorithmException, AccountException {
        String entryptPassword = PasswordUtil.encryptForAES(newPassword);
        Map currentUser = CommonUtil.getCurrentUser(request);
        Long currentUserId = Long.valueOf(currentUser.get("id").toString());
        String userScmAccount = currentUser.get("userScmAccount").toString();
        if (StringUtils.isEmpty(userScmAccount)) {
            throw new AccountException("您还未生成svn账号，无法更新密码！");
        }
        if (userDao.updateMySvnPassword(entryptPassword, currentUserId) == 1) {
            //接口请求devManage,更改svn配置文件
            ResultDataDTO resultDataDTO = systemToDevManageInterface.modifySvnPassword(currentUserId, userScmAccount, newPassword, entryptPassword);
            if (StringUtils.equals(resultDataDTO.getResCode(), ResultDataDTO.DEFAULT_ABNORMAL_CODE)) {
                throw new RuntimeException(resultDataDTO.getResDesc());
            }
        }
    }

    @Override
    public List<TblDeptInfo> getAllDept(TblDeptInfo tblDeptInfo, Integer pageNumber, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        if (pageNumber != null && pageSize != null) {
            map.put("start", (pageNumber - 1) * pageSize);
            map.put("pageSize", pageSize);
        } else {
            map.put("start", null);
            map.put("pageSize", null);
        }

        map.put("dept", tblDeptInfo);
        return deptDao.getAllDeptByPage(map);
    }


    /**
     * @param userId
     * @return java.lang.String
     * @Description 生成svn账号
     * @MethodName createSvnAccount
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/4/9 15:50
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map<String, String> createSvnAccountPassword(Long userId) {
        Map<String, String> result = new HashMap<>();
        try {
            TblUserInfo user = userDao.getUserDetailByUserIdForSvnAccountPasswordCreate(userId);
            if (user != null) {
                //创建svn账户
                if (StringUtils.isEmpty(user.getUserScmAccount())) {
                    //创建svn账号(用户姓名全拼-公司简称)
                    StringBuilder svnAccountBuilder = new StringBuilder(PinYinUtil.getFullSpell(user.getUserName()));
                    if (StringUtils.isNotEmpty(user.getCompanyShortName())) {
                        svnAccountBuilder.append("-").append(PinYinUtil.getFullSpell(user.getCompanyShortName()));
                    }
                    //将svn账号在库中查重校验
                    if (userDao.getSvnAccountCount(svnAccountBuilder.toString()) > 0) {
                        //重复则加上唯一Id
                        svnAccountBuilder.append(user.getId());
                    }
                    if (userDao.createSvnAccountByUserId(svnAccountBuilder.toString(), userId) == 1) {
                        result.put("username", svnAccountBuilder.toString());
                    }
                } else {
                    result.put("username", user.getUserScmAccount());
                }
                //创建svn密码(默认密码)
                if (StringUtils.isEmpty(user.getUserScmPassword())) {
                    String entryptPassword = PasswordUtil.encryptForAES(svnDefaultPassword);
                    if (userDao.createSvnPasswordByUserId(entryptPassword, userId) == 1) {
                        result.put("password", entryptPassword);
                    }
                } else {
                    result.put("password", user.getUserScmPassword());
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("创建SVN账号异常，异常原因:" + e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblRoleInfo> getRoles() {
        // TODO Auto-generated method stub
        return roleDao.getRoles();
    }

    @Override
    public Long getIdByNum(TblUserInfo user) {
        // TODO Auto-generated method stub
        return userDao.getIdByNum(user);
    }

    @Override
    public Long getIdByUserAccount(TblUserInfo user) {
        // TODO Auto-generated method stub
        return userDao.getIdByUserAccount(user);
    }

    @Override
    public List<Map<String, Object>> getAllproject() {
        // TODO Auto-generated method stub
        return userDao.getAllproject();
    }


    @Override
    @Transactional(readOnly = true)
    public BootStrapTablePage<TblUserInfo> getAllUserModal2(BootStrapTablePage<TblUserInfo> bootStrapTablePage, TblUserInfo userInfo, String projectIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectIds", projectIds);
        map.put("user", userInfo);
        PageHelper.startPage(bootStrapTablePage.getPageNumber(), bootStrapTablePage.getPageSize());
        List<TblUserInfo> userInfoDTOList = userDao.getAllUserModal2(map);
        PageInfo<TblUserInfo> pageInfo = new PageInfo<TblUserInfo>(userInfoDTOList);
        bootStrapTablePage.setTotal((int) pageInfo.getTotal());
        bootStrapTablePage.setRows(pageInfo.getList());
        return bootStrapTablePage;

    }

    @Override
    @Transactional(readOnly = true)
    public List<TblUserInfoDTO> getBatchUserInfoByIds(List<Long> ids) {
        return userDao.getBatchUserDetailByIds(ids);
    }


}