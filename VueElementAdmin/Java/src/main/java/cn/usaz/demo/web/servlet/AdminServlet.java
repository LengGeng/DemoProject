package cn.usaz.demo.web.servlet;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 愿与君长安
 * @file ${NAME}.java
 * @desc ${DESCRIPTION}
 * @date 2020/6/26 14:38
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends BaseServlet {
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        response.setContentType("application/json; charset=utf-8");
        JSONObject responseJson = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("token", "admin-token");
        responseJson.put("code", 0);
        responseJson.put("data", data);
        response.getWriter().write(responseJson.toJSONString());
    }

    public void info(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        System.out.println("token = " + token);
        response.setContentType("application/json; charset=utf-8");
        JSONObject responseJson = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("roles", "admin");
        data.put("introduction", "I am a super administrator");
        data.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        data.put("name", "Super Admin");
        responseJson.put("code", 0);
        responseJson.put("data", data);
        response.getWriter().write(responseJson.toJSONString());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        System.out.println("token = " + token);
        response.setContentType("application/json; charset=utf-8");
        JSONObject responseJson = new JSONObject();
        responseJson.put("code", 0);
        responseJson.put("data", "success");
        response.getWriter().write(responseJson.toJSONString());
    }
}