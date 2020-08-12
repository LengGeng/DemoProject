# VueElementAdmin使用指南

[vue-element-admin](http://panjiachen.github.io/vue-element-admin) 是一个后台前端解决方案，它基于 [vue](https://github.com/vuejs/vue) 和 [element-ui](https://github.com/ElemeFE/element)实现。它使用了最新的前端技术栈，内置了 i18 国际化解决方案，动态路由，权限验证，提炼了典型的业务模型，提供了丰富的功能组件，它可以帮助你快速搭建企业级中后台产品原型。相信不管你的需求是什么，本项目都能帮助到你。

> ## 建议
>
> 本项目的定位是后台集成方案，不太适合当基础模板来进行二次开发。因为本项目集成了很多你可能用不到的功能，会造成不少的代码冗余。如果你的项目不关注这方面的问题，也可以直接基于它进行二次开发。
>
> - 集成方案: [vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)
> - 基础模板: [vue-admin-template](https://github.com/PanJiaChen/vue-admin-template)
> - 桌面终端: [electron-vue-admin](https://github.com/PanJiaChen/electron-vue-admin)
> - Typescript 版: [vue-typescript-admin-template](https://github.com/Armour/vue-typescript-admin-template) (鸣谢: [@Armour](https://github.com/Armour))
> - Others: [awesome-project](https://github.com/PanJiaChen/vue-element-admin/issues/2312)

## 安装

```bash
# 克隆项目
git clone https://github.com/PanJiaChen/vue-element-admin.git

# 进入项目目录
cd vue-element-admin

# 安装依赖
npm install

# 建议不要用 cnpm 安装 会有各种诡异的bug 可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 本地开发 启动项目
npm run dev
```

# vue-element-admin登录流程

> 本教程使用基础模板vue-admin-template进行演示

## 1. 下载项目

```bash
# 克隆项目
git clone https://github.com/PanJiaChen/vue-admin-template

# 进入项目目录
cd vue-admin-template

# 安装依赖
npm install

# 建议不要用 cnpm 安装 会有各种诡异的bug 可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 本地开发 启动项目
npm run dev
```

## 2. 项目概览

![image-20200626173053303](images/VueElementAdmin使用指南/项目概览.png)

## 3. 安装依赖并运行

> 若下载项目时安装过依赖可以省略

~~~bash
# 安装依赖
npm install
# 运行项目
npm run dev
~~~

## 4. 运行界面

![image-20200626173610762](images/VueElementAdmin使用指南/运行界面.png)

## 5. 后端接口代码

> 后端登录接口代码示例（这里以java为列）,下面会用到

`CrossFilter.java`  解决跨域问题

~~~java
package cn.usaz.web.filter;

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
~~~

`BaseServlet.java`  Servlet基类,实现反射调用函数

~~~java
package cn.usaz.web.servlet;

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
~~~

`AdminServlet.java`  实现登录,会话保持,退出接口

~~~java
package cn.usaz.web.servlet;

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
~~~

## 6. 分析登录页面

首先打开 `src/views/login/index.vue`

![image-20200626183751809](images/VueElementAdmin使用指南/登录页面.png)

分析代码,出现this.$store,去store目录下面找找看

![image-20200626184151686](images/VueElementAdmin使用指南/store-user-1.png)

可以看到login方法中又调用了另一个login方法,接着往上找可以看到其来自@[^1]api/user,前往src/api目录下寻找user

![image-20200626184518413](images/VueElementAdmin使用指南/store-user-2.png)

可以看到登录相关接口该文件中均以实现

![image-20200626185055615](images/VueElementAdmin使用指南/api-user-1.png)

查看发现方法中都使用了return request({...})形式,继续前往@/utils/request查看

![image-20200626190118096](images/VueElementAdmin使用指南/api-user-2.png)

可以看到,该方法对axios进行了封装

![image-20200626190505664](images/VueElementAdmin使用指南/request-1.png)

另外可以看到`url = base url + request url`, 拿login来说我们实际请求的地址就是base url加上我们定义的`/vue-admin-template/user/login`, 那么这个`baseURL: process.env.VUE_APP_BASE_API`又是啥呢?

打开根目录下的`.env.development`文件可以看到

![image-20200626191014432](images/VueElementAdmin使用指南/env.development-1.png)

将路径进行拼接后与我们在浏览器中实际的请求地址一样,可以肯定就是它了

![image-20200626191225887](images/VueElementAdmin使用指南/请求-1.png)

将其修改为我们后台服务器接口的实际路径,并重启Node服务器

![image-20200626191417094](images/VueElementAdmin使用指南/env.development-2.png)

重新请求发现请求地址已经发生变化

![image-20200626191830127](images/VueElementAdmin使用指南/请求-2.png)

下面打开@api/user将实际的请求地址进行更改

![image-20200626192040508](images/VueElementAdmin使用指南/api-user-3.png)

后台代码以在上方给出,若出现以下情况则为跨域问题,请添加`CrossFilter.java`的代码解决跨域问题

![image-20200626192303736](images/VueElementAdmin使用指南/跨域问题.png)

接下来再次请求发现后台无法获取请求的参数,查看发现请求的方式为`Request Payload`而不是我们常见的`FROM DATA`,这是Axios框架的问题,可以在请求中通过添加以下代码进行解决

~~~javascript
// 引入框架
import Qs from 'qs'
// 在请求前执行方法将数据进行替换
transformRequest: [function(data) {
    return Qs.stringify(data)
}],
~~~

因为代码中都通过request请求后台接口,因此我们对其进行修改

~~~javascript
// 修改前
import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// 修改后
import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import Qs from 'qs'

// create an axios instance
const service = axios.create({
  transformRequest: [function(data) {
    return Qs.stringify(data)
  }],
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})
~~~

![image-20200626192454135](images/VueElementAdmin使用指南/请求-3.png)

另外,在request代码中还应该进行一处修改,将`if (res.code !== 20000) {`改为`if (res.code !== 0) {`此处是因为后台代码返回为0,纯属个人习惯,以实际项目接口返回而定

![image-20200626193752437](images/VueElementAdmin使用指南/response.png)

以上修改完成后再次进行登录

![image-20200626194211550](images/VueElementAdmin使用指南/成功登录.png)

OK!  圆满完成!

[^1]:@为别名,指src目录