package com.osc.skyz.zkviewer.service;

import com.osc.skyz.zkviewer.entity.NodeAcl;
import com.osc.skyz.zkviewer.entity.NodeMetaData;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述:
 * <p/>
 * 创建人: shoujun.yang
 * <p/>
 * 创建时间: 2019/1/29 5:10 PM.
 * <p/>
 */
public class ZkDataService {

    public static final int DEFAULT_TIME_OUT = 60 * 60 * 1000;

    private static ConcurrentHashMap<String, ZooKeeper> connections = new ConcurrentHashMap<String, ZooKeeper>();

    public static ZooKeeper connect(String host, String port) throws Exception {
        String hostPort = host + ":" + port;
        if(connections.get(hostPort) != null) {
            return connections.get(hostPort);
        }
        ZooKeeper zooKeeper = new ZooKeeper(host + ":" + port, DEFAULT_TIME_OUT, null);
        connections.put(hostPort, zooKeeper);
        return zooKeeper;
    }

    public static List<String> getChildNodes(String host, String port, String parentPath) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if(zooKeeper == null) {
            zooKeeper = connect(host, port);
        }
        List<String> list = zooKeeper.getChildren(parentPath, false);
        return list;
    }

    public static int getChildrenCount(String host, String port, String parentPath) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if(zooKeeper == null) {
            zooKeeper = connect(host, port);
        }
        List<String> list = zooKeeper.getChildren(parentPath, false);
        return list.size();
    }

    public static String getNodeData(String host, String port, String path) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if(zooKeeper == null) {
            zooKeeper = connect(host, port);
        }
        Stat stat = zooKeeper.exists(path, null);
        byte[] data = zooKeeper.getData(path, false ,stat);
        data = data == null ? "".getBytes() : data;
        return new String(data,"utf-8");
    }

    public static void addNode(String host, String port, String path, String nodeName,
                               String nodeValue, int createMode) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if (zooKeeper == null) {
            zooKeeper = connect(host, port);
        }

        Stat stat = zooKeeper.exists(path, null);
        if(stat != null) {
            zooKeeper.create(("/".equals(path) ? "" : path) + "/" + nodeName, nodeValue.getBytes("utf-8"),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.fromFlag(createMode));
        }
    }

    public static void removeNode(String host, String port, String path, int version) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if (zooKeeper == null) {
            zooKeeper = connect(host, port);
        }

        Stat stat = zooKeeper.exists(path, null);
        if(stat != null) {
            zooKeeper.delete(path, version);
        }
    }

    public static void updateNodeData(String host, String port, String path, String data, int version) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if (zooKeeper == null) {
            zooKeeper = connect(host, port);
        }

        Stat stat = zooKeeper.exists(path, null);
        if(stat != null) {
            zooKeeper.setData(path, data.getBytes("utf-8"), version);
        }
    }

    public static NodeMetaData getNodeMetaData(String host, String port, String path) throws Exception {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if(zooKeeper == null) {
            zooKeeper = connect(host, port);
        }

        Stat stat = zooKeeper.exists(path, null);
        NodeMetaData metaData = new NodeMetaData();
        if(stat != null) {
            metaData.setAclVersion(stat.getAversion());
            metaData.setCreateTime(stat.getCtime());
            metaData.setCreationId(stat.getCzxid());
            metaData.setDataLength(stat.getDataLength());
            metaData.setDataVersion(stat.getVersion());
            metaData.setChildrenVersion(stat.getCversion());
            metaData.setEphemeralOwner(stat.getEphemeralOwner());
            metaData.setLastModifyTime(stat.getMtime());
            metaData.setModifyId(stat.getMzxid());
            metaData.setNumOfChildren(stat.getNumChildren());
            metaData.setNodeId(stat.getPzxid());
        }
        return metaData;
    }

    public static List<NodeAcl> getNodeAcls(String host, String port, String path) throws Exception  {
        String hostPort = host + ":" + port;
        ZooKeeper zooKeeper = connections.get(hostPort);
        if(zooKeeper == null) {
            zooKeeper = connect(host, port);
        }

        Stat stat = zooKeeper.exists(path, null);

        List<NodeAcl> acls = new ArrayList<>();
        if(stat != null) {
            List<ACL> list = zooKeeper.getACL(path, stat);
            for(ACL acl : list) {
                NodeAcl nodeAcl = new NodeAcl();
                nodeAcl.setId(acl.getId().getId());
                nodeAcl.setSchema(acl.getId().getScheme());
                int perms = acl.getPerms();
                StringBuilder sb = new StringBuilder();
                if ((perms & ZooDefs.Perms.READ) == ZooDefs.Perms.READ)
                {
                    sb.append("Read");
                    sb.append(", ");
                }
                if ((perms & ZooDefs.Perms.WRITE) == ZooDefs.Perms.WRITE)
                {
                    sb.append("Write");
                    sb.append(", ");
                }
                if ((perms & ZooDefs.Perms.CREATE) == ZooDefs.Perms.CREATE)
                {
                    sb.append("Create");
                    sb.append(", ");
                }
                if ((perms & ZooDefs.Perms.DELETE) == ZooDefs.Perms.DELETE)
                {
                    sb.append("Delete");
                    sb.append(", ");
                }
                if ((perms & ZooDefs.Perms.ADMIN) == ZooDefs.Perms.ADMIN)
                {
                    sb.append("Admin");
                }
                nodeAcl.setPermissions(sb.toString());
                acls.add(nodeAcl);
            }
        }
        return acls;
    }
}
