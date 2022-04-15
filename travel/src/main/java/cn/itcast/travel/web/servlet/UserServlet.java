package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private UserService service = new UserServiceImpl();
    /**
     * 用户注册功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证验证码
        String checkCode = request.getParameter("check");
        HttpSession session = request.getSession();
        String checkCode_session = (String) session.getAttribute("BUYaoBUg");
        session.removeAttribute("BUYaoBUg");//保证验证码只能使用一次
        if (checkCode_session == null || !checkCode_session.equalsIgnoreCase(checkCode)){
            //验证码错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误!");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(json);
            return;
        }
        //获取数据
        Map<String, String[]> map = request.getParameterMap();
        //封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //调用service注册方法
        boolean flag = service.regist(user);
        ResultInfo info = new ResultInfo();
        //响应结果
        if (flag) {
            //注册成功
            info.setFlag(true);
        }else {
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败!");
        }
        //将info序列化为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        //设置contentType
        response.setContentType("application/json;charset=utf-8");
        //将json写会客户端
        response.getWriter().write(json);
    }

    /**
     * 登录功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //判断验证码
        String check = request.getParameter("check");
        HttpSession session = request.getSession();
        String check_session = (String) session.getAttribute("BUYaoBUg");
        session.removeAttribute("BUYaoBUg");
        if (check_session == null || !check_session.equalsIgnoreCase(check)){
            //验证码为空或错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误!");
            String json = writeValueAsString(info, response);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(json);
            return;
        }
        //获取数据
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        User u = service.login(user);
        ResultInfo info = new ResultInfo();
        if (u == null) {
            //用户不存在或用户密码错误,请先注册
            info.setFlag(false);
            info.setErrorMsg("用户不存在或用户密码错误");
        }
        if (u != null && "N".equals(u.getStatus())){
            //用户未激活
            info.setFlag(false);
            info.setErrorMsg("账号未激活,请先激活");
        }
        if (u != null && "Y".equals(u.getStatus())) {
            request.getSession().setAttribute("user",u);//登录成功标记
            //登录成功
            info.setFlag(true);
        }
        writeValue(info,response);
    }

    /**
     * 退出功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //销毁session
        request.getSession().invalidate();
        //跳转到登录界面
        response.sendRedirect(request.getContextPath()+"/index.html");
    }

    /**
     * 查找用户名,并显示欢迎回来功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从session中获取用户登录的信息
        Object user = request.getSession().getAttribute("user");
        //将user写回客户端
        writeValue(user,response);
    }

    /**
     * 激活功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取激活码
        String code = request.getParameter("code");
        if (code != null) {
            //调用service完成激活
            boolean flag = service.active(code);
            //判断标记
            String msg = null;
            if (flag) {
                //激活成功
                msg = "激活成功,请<a href='http://47.103.211.88/travel_liu/login.html'>登录</a>";
            }else {
                //激活失败
                msg = "激活失败,请联系管理员-刘波";
            }
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(msg);
        }

    }
}
