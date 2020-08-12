package cn.usaz.demo.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 愿与君长安
 * @file CrossFilter.java
 * @desc
 * @date 2020/6/25 16:30
 */
@WebFilter(filterName = "CrossFilter", urlPatterns = {"/admin"})
public class CrossFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //设置跨域请求
        HttpServletResponse response = (HttpServletResponse) resp;
        //此处ip地址为需要访问服务器的ip及端口号
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9528");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type,Token,X-Token,Accept, Connection, User-Agent, Cookie");
        response.setHeader("Access-Control-Max-Age", "3628800");

        // System.out.println("设置跨域请求");
        chain.doFilter(req, response);
        // 这里注释掉这句话，因为前面已经将修改后的请求头发送了，这句话会使请求发送两次
        // chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}