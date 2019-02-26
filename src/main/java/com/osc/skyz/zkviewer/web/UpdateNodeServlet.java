package com.osc.skyz.zkviewer.web;

import com.google.gson.Gson;
import com.osc.skyz.zkviewer.entity.NodeMetaData;
import com.osc.skyz.zkviewer.service.ZkDataService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/2/23 7:59 PM.
 * <p/>
 */
public class UpdateNodeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getParameter("path");
        String data = req.getParameter("data");
        String host = req.getParameter("host");
        String port = req.getParameter("port");

        Map<String, String> rlt = new HashMap<String, String>();
        try {
            NodeMetaData metaData = ZkDataService.getNodeMetaData(host, port, path);
            int version = metaData.getDataVersion();
            ZkDataService.updateNodeData(host, port, path, data, version);
            rlt.put("result", "ok");
        } catch (Exception e) {
            rlt.put("result", "fail");
            rlt.put("message", e.getMessage());
            e.printStackTrace();
        }
        String json = new Gson().toJson(rlt);
        resp.getOutputStream().write(json.getBytes());
    }
}
