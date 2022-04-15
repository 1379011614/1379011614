package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();
    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public boolean regist(User user) {
        User u = userDao.findByUserName(user.getUsername());
        if (u != null) {
            //用户名存在,注册失败
            return false;
        }
        //数据库中没有数据,可以注册
        //设置激活码,唯一字符串
        user.setCode(UuidUtil.getUuid());
        //设置激活状态
        user.setStatus("N");
        //保存用户信息
        userDao.save(user);
        //激活邮件发送,正文
        String context = "<a href='http://47.103.211.88/travel_liu/user/active?code="+
                user.getCode()+" '>点击激活[旅游网-liu]</a>";
        MailUtils.sendMail(user.getEmail(),context,"激活邮件");
        return true;
    }

    /**
     * 激活用户
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        //根据激活码查找用户对象
        User user =  userDao.findByCode(code);
        if (user != null) {
            //调用dao的修改激活状态的方法
            userDao.updateStatus(user);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 用户登录
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        //根据用户名和密码来查找用户对象
        return  userDao.findByUsernameAndPassword(user.getUsername(),user.getPassword());

    }
}
