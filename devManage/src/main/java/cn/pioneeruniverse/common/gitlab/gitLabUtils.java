package cn.pioneeruniverse.common.gitlab;

/*import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;*/

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 16:34 2019/7/15
 * @Modified By:
 */
public class gitLabUtils {

    /*public static String getFileContentByCommitID(String commitID, String filePath) throws GitAPIException, IOException {
        ByteArrayOutputStream out = null;
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider("root", "12345678");
        Git git = Git.cloneRepository().setURI("http://192.168.1.155:82/root/ITMP.git")
                .setBranch("master")
                .setDirectory(new File("E:\\jgitTest\\ITMP"))
                .setCredentialsProvider(provider)
                .call();
        Repository repository = git.getRepository();
        RevWalk walk = new RevWalk(repository);
        ObjectId objId = repository.resolve(commitID);
        RevCommit revCommit = walk.parseCommit(objId);
        RevTree revTree = revCommit.getTree();
        TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, revTree);
        ObjectId blobId = treeWalk.getObjectId(0);
        ObjectLoader loader = repository.open(blobId);
        loader.copyTo(out);
        return new String(out.toByteArray(), Charset.forName("UTF-8"));
    }

    public static void main(String[] args) {
        try {
            String test = getFileContentByCommitID("602529cbe57505f4a811a6eb62fc5445d30b6066", "/README.md");
            String a = "";
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
