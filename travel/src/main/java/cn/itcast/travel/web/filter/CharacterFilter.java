package cn.itcast.travel.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤所有文件统一设置编码
 */
@WebFilter("/*")
public class CharacterFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

//        //将父接口转化为子接口
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String requestURI = request.getRequestURI();
        if (requestURI.contains(".css")||requestURI.contains(".js")||requestURI.contains(".png")||requestURI.contains(".jpg")) {
            chain.doFilter(request,response);
            return;
        }
        //获取请求方法
        String method = request.getMethod();
        //解决post请求中文数据乱码问题
        if (method.equalsIgnoreCase("post")) {
            request.setCharacterEncoding("utf-8");
        }
        //处理响应乱码问题
        response.setContentType("text/html;charset=utf-8");
        chain.doFilter(request, response);

    }

    public void destroy() {

    }

}
