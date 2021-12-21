package cn.pioneeruniverse.dev.feignInterface;

import cn.pioneeruniverse.common.gitlab.entity.*;
import feign.Param;
import feign.RequestLine;

import java.net.URI;
import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 16:19 2019/7/1
 * @Modified By:
 */
public interface DevManageToGitLabWebApiInterface {

    @RequestLine("GET /projects/{projectId}?private_token={privateToken}")
    Project getProjectById(URI baseUri, @Param("projectId") Integer projectId, @Param("privateToken") String privateToken);

    @RequestLine("GET projects/{projectId}/members?private_token={privateToken}")
    List<Member> getProjectMembers(URI baseUri, @Param("projectId") Integer projectId, @Param("privateToken") String privateToken);

    @RequestLine("GET projects/{projectId}/members/all?private_token={privateToken}")
    List<Member> getProjectAllMembers(URI baseUri, @Param("projectId") Integer projectId, @Param("privateToken") String privateToken);

    @RequestLine("DELETE /projects/{projectId}/members/{userId}?private_token={privateToken}")
    void deleteMemberFromProject(URI baseUri, @Param("projectId") Integer projectId, @Param("userId") Integer userId, @Param("privateToken") String privateToken);

    @RequestLine("POST /projects/{projectId}/members?private_token={privateToken}")
    void addMemberToProject(URI baseUri, Member member, @Param("projectId") Integer projectId, @Param("privateToken") String privateToken);

    @RequestLine("PUT /projects/{projectId}/members/{userId}?private_token={privateToken}")
    void editMemberOfProject(URI baseUri, Member member, @Param("projectId") Integer projectId, @Param("userId") Integer userId, @Param("privateToken") String privateToken);

    @RequestLine("POST /projects?private_token={privateToken}")
    Project createProject(URI baseUri, Project project, @Param("privateToken") String privateToken);

    @RequestLine("POST /users?private_token={privateToken}")
    User createUser(URI baseUri, User user, @Param("privateToken") String privateToken);

    @RequestLine("GET /users?username={userName}&private_token={privateToken}")
    List<User> getUserByUserName(URI baseUri, @Param("userName") String userName, @Param("privateToken") String privateToken);

    @RequestLine("GET /users?search={email}&private_token={privateToken}")
    List<User> getUserByEmail(URI baseUri, @Param("email") String email, @Param("privateToken") String privateToken);

    @RequestLine("GET /projects/{projectId}/repository/commits/{commitId}?private_token={privateToken}")
    Commit getSingleCommitByCommitID(URI baseUri, @Param("projectId") Integer projectId, @Param("commitId") String commitId, @Param("privateToken") String privateToken);

    @RequestLine(value = "GET /projects/{projectId}/repository/files/{filePath}?ref={ref}&private_token={privateToken}", decodeSlash = false)
    File getFileFromRepository(URI baseUri, @Param("projectId") Integer projectId, @Param(value = "filePath", encoded = true) String filePath, @Param("ref") String ref, @Param("privateToken") String privateToken);

    @RequestLine(value = "GET /projects/{projectId}/repository/blobs/{blobSHA}/raw?private_token={privateToken}")
    String getFileRawBlobContent(URI baseUri, @Param("projectId") Integer projectId, @Param("blobSHA") String blobSHA, @Param("privateToken") String privateToken);
}
