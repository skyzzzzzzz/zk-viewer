var host = '';
var port = '';
$(function () {
    port = $.cookie('port');
    if(!port) {
        port = 2181
    }
    $('#server').form('load', {host:$.cookie('host'), port:port});
    $('#nodes').tree({
        onBeforeLoad: function(node, param) {
            param.host = host;
            param.port = port;
        },
        onClick: function(node){
            $.post("nodeDetails",{id:node.id, host:host,port:port},function(rlt){
                $('#nodeData').form('load', {data:rlt.data});
                $('#metadata').form('load', rlt.metaData);

                var html = '';
                var acls = rlt.acls;
                for(var i = 0;i < acls.length;i++) {
                    var acl = acls[i];
                    html += '<tr>' +
                        '<td>Schema:</td>' +
                        '<td><input class="easyui-textbox" type="text" value="' + acl.schema + '"></input></td>' +
                        '</tr>';
                    html += '<tr>' +
                        '<td>ID:</td>' +
                        '<td><input class="easyui-textbox" type="text" value="' + acl.id + '"></input></td>' +
                        '</tr>';
                    html += '<tr>' +
                        '<td>Permissions:</td>' +
                        '<td><input class="easyui-textbox" type="text" value="' + acl.permissions + '"></input></td>' +
                        '</tr>';
                    $('#aclsTab').html(html);
                    $.parser.parse($('#aclsTab'));
                }
            },"json");
        },
        onContextMenu: function(e, node){
            e.preventDefault();
            $('#nodes').tree('select', node.target);
            $('#treeMenu').menu('show', {
                left: e.pageX,
                top: e.pageY
            });
        },
        onLoadSuccess:function (node, data) {

        }
    });

    connect();
});

function connect() {
    host = $('#host').val();
    port = $('#ports').val();
    $.cookie('host', host, {expires: 365});
    $.cookie('port', port, {expires: 365});
    $.post("connect",{host:host, port: port},function(rlt) {
        if('ok' == rlt) {
            $('#nodes').tree("reload");
        }
    });
}

function showAddNodeDlg() {
    $('#addNodeDlg').dialog({
        title: 'Add Node',
        width: 400,
        height: 240,
        closed: false,
        cache: false,
        modal: true
    });
    $('#createModel').combo({
        required:false,
        multiple:false
    });
}

function addNode() {
    var nodeName = $('#nodeName').val();
    var nodeValue = $('#nodeValue').val();
    var parentNode = $('#nodes').tree('getSelected').id;
    var createMode = $('#createModel').val();
    var selected = $('#nodes').tree('getSelected');

    var param = {
        nodeName: nodeName,
        nodeValue: nodeValue,
        parentNode: parentNode,
        createMode: createMode,
        host:host,
        port:port
    }
    $.post("addNode",param,function(rlt){
        if(rlt.result == 'ok') {
            if($('#nodes').tree('isLeaf', selected.target)) {
                $('#nodes').tree('append', {
                    parent: selected.target,
                    data: [{
                        id: selected.id+'/'+nodeName,
                        text: nodeName
                    }]
                });
            } else {
                $('#nodes').tree("reload", $('#nodes').tree('getSelected').target);
            }
            $.messager.show({
                title:'Alert',
                msg:'Add node success!',
                timeout:3000,
                showType:'slide'
            });
            $('#addNodeDlg').dialog('close');
        } else {
            $.messager.show({
                title:'Alert',
                msg:'Add node fail: ' + rlt.message,
                timeout:3000,
                showType:'slide'
            });
        }
    }, 'json');
}

function refresh() {
    var selected = $('#nodes').tree('getSelected');
    $('#nodes').tree("reload", selected.target);
}

function remove() {
    var path = $('#nodes').tree('getSelected').id;
    var param = {
        path: path,
        host:host,
        port:port
    }
    $.post("removeNode",param,function(rlt){
        if(rlt.result == 'ok') {
            var selectedNode = $('#nodes').tree('getSelected');
            var parentNode = $('#nodes').tree("getParent", selectedNode.target);
            $('#nodes').tree("reload", parentNode.target);
            $.messager.show({
                title:'Alert',
                msg:'Remove node success!',
                timeout:3000,
                showType:'slide'
            });
        } else {
            $.messager.show({
                title:'Alert',
                msg:'Remove node fail: ' + rlt.message,
                timeout:3000,
                showType:'slide'
            });
        }
    }, 'json');
}

function updateData() {
    var nodeDataValue = $('#nodeDataValue').val();
    var path = $('#nodes').tree('getSelected').id;
    var param = {
        path: path,
        host:host,
        port:port,
        data:nodeDataValue
    }

    $.post("updateNode",param,function(rlt){
        if(rlt.result == 'ok') {
            var selectedNode = $('#nodes').tree('getSelected');
            var parentNode = $('#nodes').tree("getParent", selectedNode.target);
            $('#nodes').tree("reload", parentNode.target);
            $.messager.show({
                title:'Alert',
                msg:'Update node success!',
                timeout:3000,
                showType:'slide'
            });
        } else {
            $.messager.show({
                title:'Alert',
                msg:'Update node fail: ' + rlt.message,
                timeout:3000,
                showType:'slide'
            });
        }
    }, 'json');
}