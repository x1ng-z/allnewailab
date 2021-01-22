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
    <script src="/js/layui/layui.js"></script>
    <script src="/js/jquery-3.0.0.js"></script>
    <script src="/js/api.js"></script>
    <script src="/js/layoutmanager.js"></script>
</head>

<body>


<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>工程设置</legend>
    </fieldset>

    <div class="layui-form-item">
        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="text" name="projectid" value="${project.projectid}"
                       style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"/>
            </div>
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">工程名称</label>
            <div class="layui-input-inline">
                <input type="text" name="name" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${project.name}">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">调度间隔(秒)</label>
            <div class="layui-input-inline">
                <input type="number" name="runperiod" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${project.runperiod}">
            </div>
        </div>


    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="motifymodlesubmit" id="motifymodlesubmit"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">保存
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>




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
            // console.log("input get parent layer object", parent.layer === undefined);
            layer = parent.layer === undefined ? layui.layer : parent.layer;
            form.render(); //更新全部
            // form.render('select'); //刷新select选择框渲染
            form.on('submit(motifymodlesubmit)', function (data) {

                let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
                let url = "/projectedit/updateproject";
                if(api.updateproject(url, data.field, layer)){
                    layer.close(layer.getFrameIndex(window.name));
                }
                layer.close(index);

                return false;
            });

        });

    });


    function flush_intab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('intable', {
                elem: '#intable',
                // ,data:
                url: '/projectedit/getmodleproperties',
                method: 'post',
                request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                },
                where: {
                    modleid: '${filtermodle.modleId}',
                    pindir:'input'
                },
            });

        });
    };
    function flush_outtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('outtable', {
                elem: '#outtable',
                // ,data:
                url: '/projectedit/getmodleproperties',
                method: 'post',
                request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                },
                where: {
                    modleid: '${filtermodle.modleId}',
                    pindir: 'output'
                },
            });

        });
    };
    function flushtab() {
        flush_outtab();
        flush_intab();
    }


    function outtabrender(table) {

        table.render({
            elem: '#outtable'
            // ,data:
            , url: '/projectedit/getmodleproperties'
            , method: 'post'
            , where: {
                modleid: '${filtermodle.modleId}',
                pindir:'output'
            }
            , toolbar: '#toolbarout' //开启头部工具栏，并为其绑定左侧模板
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
                , {fixed: 'right', title: '操作', toolbar: '#barout', width: 300}
            ]],
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(outtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addoutpin':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    let url = '${pageContext.request.contextPath}/projectedit/viewaddmodleproperty';
                    let modletype = '${filtermodle.modletype}';
                    let modleId = '${filtermodle.modleId}';
                    let pindir='output';
                    let addinputpropertylayer = layermanager.viewaddinputproperty(url, modletype, modleId, pindir,layer, document);
                    //layer.close(addinputpropertylayer);
                    //newmodlepinwindow(layer, $("#addpvpinbt").attr('lay-href'), 'pv', document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(outtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if (api.deletmodleproperties('${pageContext.request.contextPath}/projectedit/deletemodleproperties', data['modlepinsId'], layer)) {
                        $("#bt_flush_outtab", document).trigger('click');
                    }
                }

            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatemodleproperty';
                let modletype = '${filtermodle.modletype}';
                let modlepinsId = data['modlepinsId'];
                layermanager.viewupdateinputproperty(url, modletype, modlepinsId, layer, document);
            }
        });

    };



    function intabrender(table) {
        table.render({
            elem: '#intable'
            // ,data:
            , url: '/projectedit/getmodleproperties'
            , method: 'post'
            , where: {
                modleid: '${filtermodle.modleId}',
                pindir:'input'
            }
            , toolbar: '#toolbarin' //开启头部工具栏，并为其绑定左侧模板
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
                , {fixed: 'right', title: '操作', toolbar: '#barin', width: 300}
            ]],
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(intable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addinpin':
                    // var data = checkStatus.data;
                    // alert("sss"+$("#addopcservebt").attr('lay-href'));
                    let url = '${pageContext.request.contextPath}/projectedit/viewaddmodleproperty';
                    let modletype = '${filtermodle.modletype}';
                    let modleId = '${filtermodle.modleId}';
                    let pindir='input';
                    let addinputpropertylayer = layermanager.viewaddinputproperty(url, modletype, modleId, pindir,layer, document);
                    //layer.close(addinputpropertylayer);
                    //newmodlepinwindow(layer, $("#addpvpinbt").attr('lay-href'), 'pv', document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(intable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if (api.deletmodleproperties('${pageContext.request.contextPath}/projectedit/deletemodleproperties', data['modlepinsId'], layer)) {
                        $("#bt_flush_intab", document).trigger('click');
                    }

                }
            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatemodleproperty';
                let modletype = '${filtermodle.modletype}';
                let modlepinsId = data['modlepinsId'];
                layermanager.viewupdateinputproperty(url, modletype, modlepinsId, layer, document);
            }
        });

    };


    function scrollFunc(evt) {
        evt = evt || window.event;
        if (evt.preventDefault) {
            // Firefox
            evt.preventDefault();
            evt.stopPropagation();
        } else {
            // IE
            evt.cancelBubble = true;
            evt.returnValue = false;
        }
        return false;
    }
</script>

</body>
</html>
