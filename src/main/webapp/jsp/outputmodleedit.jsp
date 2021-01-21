<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/29
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <link rel="shortcut icon"
          href="/img/favicon.ico" type="image/x-icon"/>
    <meta charset="utf-8">
    <title>editmodle</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/js/layui/css/layui.css" media="all">
    <script src="/js/layui/layui.js" ></script>
    <script src="/js/jquery-3.0.0.js" ></script>
    <script src="/js/api.js" ></script>
    <script src="/js/layoutmanager.js"></script>
</head>

<body>

<div id="bt_flush_pvtab" onclick="flush_pvtab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>


<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>基础参数设置</legend>
    </fieldset>

    <div class="layui-form-item">
        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="text" name="modleId" value="${outputmodle.modleId}"  style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"/>
            </div>
        </div>
        <div class="layui-inline">
            <input type="text" name="modletype" value="${outputmodle.modletype}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">模型名称</label>
            <div class="layui-input-inline">
                <input type="text" name="modleName" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${outputmodle.modleName}">
            </div>
        </div>
    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="motifymodlesubmit" id="motifymodlesubmit" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">保存</button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>测点添加</legend>
</fieldset>

<div style="width: 700px">
    <table class="layui-hide" id="pvtable" lay-filter="pvtable"></table>
</div>


<script type="text/html" id="toolbarpv">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addpvpinbt" lay-event="addpvpin"
                lay-href="/projectedit/createmodleproperties" >
            添加测点
        </button>
    </div>
</script>


<script type="text/html" id="barpv">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>



<script>
    var table;
    var element;
    var form;
    var layer;

    $(document).ready(function () {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;

            element = layui.element;
            form = layui.form;
            console.log("input get parent layer object",parent.layer === undefined);
            layer = parent.layer === undefined ? layui.layer : parent.layer;
            form.render(); //更新全部
            // form.render('select'); //刷新select选择框渲染
            form.on('submit(motifymodlesubmit)', function (data) {

                let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
                let url="/projectedit/updatemodle";

                if(api.updatemodle(url,data.field,layer)){
                    layer.close(layer.getFrameIndex(window.name));
                }
                layer.close(index);
                return false;
            });

            pvtabrender(table);
        });
    });



    function flush_pvtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('pvtable', {
                elem: '#pvtable',
                // ,data:
                url: '/projectedit/getmodleproperties',
                method: 'post',
                request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                },
                where: {
                    modleid: '${outputmodle.modleId}',
                    pindir:'input'
                },
            });

        });
    };


    function pvtabrender(table) {

        table.render({
            elem: '#pvtable'
            // ,data:
            , url: '/projectedit/getmodleproperties'
            , method: 'post'
            , where: {
                modleid: '${outputmodle.modleId}',
                pindir:'input'
            }
            , toolbar: '#toolbarpv' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: []
            , title: '模型属性'
            , limit: 1E10
            , loading: true
            , minWidth: 150
            , cols: [[
                 {field: 'modlepinsId', title: 'modlepinsId', width: 150, hide: true}
                , {field: 'refmodleId', title: 'refmodleId', width: 150, hide: true}
                , {field: 'modlePinName', title: '引脚名称', width: 150}
                , {field: 'modleOpcTag', title: '数据源位号', width: 150}
                , {field: 'opcTagName', title: '中文描述', width: 150}
                ,{field: 'outpotopcTagName', title: '输出映射中文描述', width: 150}
                , {fixed: 'right', title: '操作', toolbar: '#barpv', width: 300}
            ]],
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(pvtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addpvpin':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    let url='${pageContext.request.contextPath}/projectedit/viewaddmodleproperty';
                    let modletype='${outputmodle.modletype}';
                    let modleId='${outputmodle.modleId}';
                    let pindir='output';
                    let addinputpropertylayer=layermanager.viewaddinputproperty(url,modletype,modleId,pindir,layer,document);
                    //layer.close(addinputpropertylayer);
                    //newmodlepinwindow(layer, $("#addpvpinbt").attr('lay-href'), 'pv', document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(pvtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if(api.deletmodleproperties('${pageContext.request.contextPath}/projectedit/deletemodleproperties',data['modlepinsId'],layer)){
                        $("#bt_flush_pvtab", document).trigger('click');
                    }
                }
            } else if (obj.event === 'edit') {
                let url='${pageContext.request.contextPath}/projectedit/viewupdatemodleproperty';
                let modletype='${outputmodle.modletype}';
                let modlepinsId=data['modlepinsId'];
               layermanager.viewupdateinputproperty(url,modletype,modlepinsId,layer,document);
            }
        });

    };
</script>

</body>
</html>
