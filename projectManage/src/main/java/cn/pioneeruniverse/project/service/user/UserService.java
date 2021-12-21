package cn.pioneeruniverse.project.service.user;

public interface UserService {

	Long findIdByUserAccount(String userAccount);

	Long findIdByUserName(String userName);
}
