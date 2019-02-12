package com.osc.skyz.zkviewer.web;

import com.google.gson.Gson;
import com.osc.skyz.zkviewer.entity.NodeAcl;
import com.osc.skyz.zkviewer.entity.NodeDetailInfo;
import com.osc.skyz.zkviewer.entity.NodeMetaData;
import com.osc.skyz.zkviewer.service.ZkDataService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/1/31 1:51 PM.
 * <p/>
 */
public class GetNodeDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String host = req.getParameter("host");
        String port = req.getParameter("port");
        String path = req.getParameter("id");
        if(path == null || "".equals(path)) {
            path = "/";
        }

        NodeDetailInfo detailInfo = new NodeDetailInfo();
        try {
            String data = ZkDataService.getNodeData(host, port, path);
            NodeMetaData metaData = ZkDataService.getNodeMetaData(host, port, path);
            List<NodeAcl> acls = ZkDataService.getNodeAcls(host, port, path);
            detailInfo.setData(data)
                    .setAcls(acls)
                    .setMetaData(metaData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = new Gson().toJson(detailInfo);
        resp.getOutputStream().write(json.getBytes());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
