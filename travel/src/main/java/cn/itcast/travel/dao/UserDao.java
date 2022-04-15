package cn.itcast.travel.dao;

import cn.itcast.travel.domain.User;

public interface UserDao {
    //根据用户名查找用户
    User findByUserName(String username);
    //注册添加用户
    void save(User user);
    //根据激活码查找用户
    User findByCode(String code);
    //激活后更新激活状态码
    void updateStatus(User user);

    //根据用户名和密码查找用户,登录
    User findByUsernameAndPassword(String username, String password);

}
