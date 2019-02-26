package com.osc.skyz.zkviewer.web;

import com.google.gson.Gson;
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
 * 创建时间: 2019/2/23 5:43 PM.
 * <p/>
 */
public class AddNodeServlet extends HttpServlet {

    private static Map<String, Integer> CREATE_MODEL_MAP = new HashMap<String, Integer>();

    static {
        CREATE_MODEL_MAP.put("PERSISTENT", 0);
        CREATE_MODEL_MAP.put("PERSISTENT_SEQUENTIAL", 2);
        CREATE_MODEL_MAP.put("EPHEMERAL", 1);
        CREATE_MODEL_MAP.put("EPHEMERAL_SEQUENTIAL", 3);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nodeName = req.getParameter("nodeName");
        String nodeValue = req.getParameter("nodeValue");
        String host = req.getParameter("host");
        String port = req.getParameter("port");
        String createMode = req.getParameter("createMode");
        String parentNode = req.getParameter("parentNode");

        int model = CREATE_MODEL_MAP.get(createMode);
        Map<String, String> rlt = new HashMap<String, String>();
        try {
            ZkDataService.addNode(host, port, parentNode, nodeName, nodeValue, model);
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
