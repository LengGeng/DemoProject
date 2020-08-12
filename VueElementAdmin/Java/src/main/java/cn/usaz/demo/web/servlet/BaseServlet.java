package cn.usaz.demo.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet(name = "BaseServlet")
public class BaseServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 判断用户行为
        String methodName = request.getParameter("method");
        System.out.println("methodName = " + methodName);
        // 利用反调用
        try {
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            String url = (String) method.invoke(this, request, response);// this.method();
            if (url != null && url.trim().length() != 0) {
                //转发
                if (url.startsWith("redirect:")) {
                    response.sendRedirect(request.getContextPath() + url.split(":")[1]);
                } else if (url.startsWith("forward:")) {
                    request.getRequestDispatcher(url.split(":")[1]).forward(request, response);
                } else {
                    request.getRequestDispatcher(url).forward(request, response);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}