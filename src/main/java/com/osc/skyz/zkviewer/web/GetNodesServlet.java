package com.osc.skyz.zkviewer.web;

import com.google.gson.Gson;
import com.osc.skyz.zkviewer.entity.TreeNode;
import com.osc.skyz.zkviewer.service.ZkDataService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/1/11 3:59 PM.
 * <p/>
 */
public class GetNodesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String host = req.getParameter("host");
        String port = req.getParameter("port");
        String path = req.getParameter("id");
        String hasRoot = req.getParameter("hasRoot");

        if(host == null || port == null || "".equals(host) || "".equals(port)) {
            return;
        }

        if(path == null || "".equals(path)) {
            path = "/";
        }

        try {
            List<String> list = ZkDataService.getChildNodes(host, port, path);
            List<TreeNode> nodes = new ArrayList<TreeNode>();
            for(String zkNode : list) {
                String nodeFullPath = (path.equals("/") ? "/" : path + "/") + zkNode;
                int childrenCount = ZkDataService.getChildrenCount(host, port, nodeFullPath);
                nodes.add(new TreeNode().setId(nodeFullPath).setText(zkNode).setState(childrenCount > 0 ? "closed" : "open"));
            }

            String json = "";
            if("/".equals(path) && !"true".equals(hasRoot)) {
                TreeNode root = new TreeNode().setId("/").setState("closed").setText("root");
                root.setChildren(nodes);
                List<TreeNode> rootList = new ArrayList<TreeNode>();
                rootList.add(root);
                json = new Gson().toJson(rootList);
            } else {
                json = new Gson().toJson(nodes);
            }
            resp.getOutputStream().write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
