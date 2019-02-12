package com.osc.skyz.zkviewer.web;

import com.osc.skyz.zkviewer.service.ZkDataService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/2/11 6:09 PM.
 * <p/>
 */
public class ConnectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String host = req.getParameter("host");
        String port = req.getParameter("port");

        String rlt = "ok";
        try {
            ZkDataService.connect(host, port);
        } catch (Exception e) {
            rlt = "fail";
            e.printStackTrace();
        }
        resp.getOutputStream().write(rlt.getBytes());
    }
}
