<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="shortcut icon"
          href="../img/favicon.ico" type="image/x-icon"/>
    <meta charset="utf-8">
    <title>modifymodle</title>
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

<div id="bt_flush_pvtab" onclick="flushtab()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>


<form class="layui-form" action="" method="post">
    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>基础参数设置</legend>
    </fieldset>

    <div class="layui-form-item">
        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="text" name="modleId" value="${mpcmodle.modleId}"
                       style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"/>
            </div>
        </div>

        <div class="layui-inline">
            <input type="text" name="modletype" value="${mpcmodle.modletype}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>

    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">模型名称</label>
            <div class="layui-input-inline">
                <input type="text" name="modleName" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${mpcmodle.modleName}">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">序列数量</label>
            <div class="layui-input-inline">
                <input type="number" name="timeserise_N" lay-verify="required|number" autocomplete="off"
                       placeholder="响应序列的数目"
                       class="layui-input" value="${mpcmodle.timeserise_N}" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">预测步数</label>
            <div class="layui-input-inline">
                <input type="number" name="predicttime_P" lay-verify="required|number" autocomplete="off"
                       class="layui-input"
                       value="${mpcmodle.predicttime_P}" onmousewheel='scrollFunc()'>
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">输出步数</label>
            <div class="layui-input-inline">
                <input type="number" name="controltime_M" lay-verify="required|number" autocomplete="off"
                       placeholder="计算后续多少步的输出"
                       class="layui-input" value="${mpcmodle.controltime_M}" onmousewheel='scrollFunc()'>
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">PV对Dmv作用分配方式</label>
            <div class="layui-input-inline">
                <select name="runstyle" lay-verify="required">
                    <option value="">请选择分配方式</option>

                    <c:choose>
                        <c:when test="${mpcmodle.runstyle==0}">
                            <option value="0" selected>最小误差</option>
                        </c:when>
                        <c:otherwise>
                            <option value="0">最小误差</option>
                        </c:otherwise>
                    </c:choose>

                    <c:choose>
                        <c:when test="${mpcmodle.runstyle==1}">
                            <option value="1" selected>手动分配</option>
                        </c:when>
                        <c:otherwise>
                            <option value="1">手动分配</option>
                        </c:otherwise>
                    </c:choose>

                </select>
            </div>
        </div>


    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" lay-filter="motifymodlesubmit"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;" id="motifymodlesubmit">更新模型
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>PV设置</legend>
</fieldset>

<script type="text/html" id="toolbarpv">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addpvpinbt" lay-event="addpvpin">
            添加pv&sp
        </button>
    </div>
</script>

<script type="text/html" id="toolbarmv">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addmvpinbt" lay-event="addmvpin"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newmodelmvpin.do?pintype=mv&modleid=${modle.modleId}">
            添加mv
        </button>
    </div>
</script>

<script type="text/html" id="toolbarff">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addffpinbt" lay-event="addffpin"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newmodelffpin.do?pintype=ff&modleid=${modle.modleId}">
            添加ff
        </button>
    </div>
</script>


<script type="text/html" id="toolbarrespon">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addresponbt" lay-event="addrespon"
                lay-href="${pageContext.request.contextPath}/contrlmodle/newrespon.do?modleid=${modle.modleId}">
            添加响应
        </button>
    </div>
</script>

<script type="text/html" id="barpv">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>

<script type="text/html" id="barmv">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>


<script type="text/html" id="barff">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>

<script type="text/html" id="barrespon">
    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>
    <a class="layui-btn layui-btn-xs" lay-event="edit">编辑</a>
</script>


<table class="layui-hide" id="pvtable" lay-filter="pvtable"></table>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>MV设置</legend>
</fieldset>
<table class="layui-hide" id="mvtable" lay-filter="mvtable"></table>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>FF(前馈)设置</legend>
</fieldset>

<table class="layui-hide" id="fftable" lay-filter="fftable"></table>


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>响应设置</legend>
</fieldset>

<table class="layui-hide" id="respontable" lay-filter="respontable"></table>
<script>


    function pvtabrender(table) {

        table.render({
            elem: '#pvtable'
            // ,data:
            , url: '/projectedit/getmpcmodleproperties'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${mpcmodle.modleId}',
                pintype: 'pv'
            }
            , toolbar: '#toolbarpv' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: []
            , title: '模型属性'
            , limit: 1E1000
            , loading: true
            , minWidth: 150
            , cols: [[
                // {type: 'checkbox', fixed: 'left',}
                {field: 'refmodleId', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlepinsId', title: 'pinid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlePinName', title: '引脚名', width: 150}
                , {field: 'modleOpcTag', title: 'opc位号', width: 100}
                , {field: 'opcTagName', title: '中文描述', width: 100}
                , {field: 'Q', title: 'Q值', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barpv', width: 300}
            ]]
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(pvtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addpvpin':
                    let url = "/projectedit/viewaddmpcmodleproperty";
                    let modleId = ${mpcmodle.modleId};
                    let pintype = 'pv';
                    let addinputpropertylayer = layermanager.viewaddmpcmodleproperty(url, pintype, modleId, layer, document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(pvtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if (api.deletmodleproperties('${pageContext.request.contextPath}/projectedit/deletemodleproperties', data['modlepinsId'], layer)) {
                        $("#bt_flush_pvtab", document).trigger('click');
                    }
                }
            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatempcmodleproperty';
                let pintype = 'pv';
                let modlepinsId = data['modlepinsId'];
                layermanager.viewupdatempcmodleproperty(url, pintype, modlepinsId, layer, document);
            }
        });

    }


    function mvtabrender(table) {
        table.render({
            elem: '#mvtable'
            // ,data:
            , url: '/projectedit/getmpcmodleproperties'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${mpcmodle.modleId}',
                pintype: 'mv'
            }
            , toolbar: '#toolbarmv' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: []
            , title: '模型属性'
            , limit: 1E1000
            , loading: true
            , minWidth: 150
            , cols: [[
                {field: 'refmodleId', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlepinsId', title: 'pinid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlePinName', title: '引脚名', width: 150}
                , {field: 'modleOpcTag', title: 'opc位号', width: 100}
                , {field: 'opcTagName', title: '中文描述', width: 100}
                , {field: 'R', title: 'R值', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barmv', width: 300}
            ]]
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(mvtable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addmvpin':
                    let url = "/projectedit/viewaddmpcmodleproperty";
                    let modleId = ${mpcmodle.modleId};
                    let pintype = 'mv';
                    let addinputpropertylayer = layermanager.viewaddmpcmodleproperty(url, pintype, modleId, layer, document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(mvtable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if (api.deletmodleproperties('${pageContext.request.contextPath}/projectedit/deletemodleproperties', data['modlepinsId'], layer)) {
                        $("#bt_flush_pvtab", document).trigger('click');
                    }
                }
            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatempcmodleproperty';
                let pintype = 'mv';
                let modlepinsId = data['modlepinsId'];
                layermanager.viewupdatempcmodleproperty(url, pintype, modlepinsId, layer, document);
            }
        });

    }


    function fftabrender(table) {
        table.render({
            elem: '#fftable'
            // ,data:
            , url: '/projectedit/getmpcmodleproperties'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleid: '${mpcmodle.modleId}',
                pintype: 'ff'
            }
            , toolbar: '#toolbarff' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: []
            , title: '模型属性'
            , limit: 1E10000
            , loading: true
            , minWidth: 150
            , cols: [[
                {field: 'refmodleId', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlepinsId', title: 'pinid', width: 150, fixed: 'left', hide: true}
                , {field: 'modlePinName', title: '引脚名', width: 150}
                , {field: 'modleOpcTag', title: 'opc位号', width: 100}
                , {field: 'opcTagName', title: '中文描述', width: 100}
                // , {field: 'Q', title: 'Q值', width: 100}
                , {fixed: 'right', title: '操作', toolbar: '#barff', width: 300}
            ]]
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(fftable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addffpin':
                    let url = "/projectedit/viewaddmpcmodleproperty";
                    let modleId = ${mpcmodle.modleId};
                    let pintype = 'ff';
                    let addinputpropertylayer = layermanager.viewaddmpcmodleproperty(url, pintype, modleId, layer, document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(fftable)', function (obj) {

            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if (api.deletmodleproperties('${pageContext.request.contextPath}/projectedit/deletemodleproperties', data['modlepinsId'], layer)) {
                        $("#bt_flush_pvtab", document).trigger('click');
                    }
                }
            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatempcmodleproperty';
                let pintype = 'ff';
                let modlepinsId = data['modlepinsId'];
                layermanager.viewupdatempcmodleproperty(url, pintype, modlepinsId, layer, document);
            }
        });


    }


    function respontabtabrender(table) {
        table.render({
            elem: '#respontable'
            // ,data:
            , url: '/projectedit/getmpcmodlerespon'
            , method: 'post'
            , request: {
                pageName: 'page' //页码的参数名称，默认：page
                , limitName: 'pagesize' //每页数据量的参数名，默认：limit
            }
            , where: {
                modleId: '${mpcmodle.modleId}'
            }
            , toolbar: '#toolbarrespon' //开启头部工具栏，并为其绑定左侧模板
            , defaultToolbar: []
            , title: '模型属性'
            , limit: 1E1000
            , loading: true
            , minWidth: 150
            , cols: [[
                // {type: 'checkbox', fixed: 'left',}
                {field: 'refrencemodleId', title: 'modleid', width: 150, fixed: 'left', hide: true}
                , {field: 'modleresponId', title: 'responid', width: 150, fixed: 'left', hide: true}
                , {field: 'input', title: 'input', width: 150}
                , {field: 'output', title: 'output', width: 150}
                , {field: 'K', title: 'K', width: 150}
                , {field: 'T', title: 'T', width: 150}
                , {field: 'Tau', title: 'Tau', width: 150}
                , {field: 'effectRatio', title: '作用比例', width: 150}
                , {fixed: 'right', title: '操作', toolbar: '#barrespon', width: 300}
            ]]
            // , page: true
        });

        //头工具栏事件
        table.on('toolbar(respontable)', function (obj) {
            // var checkStatus = table.checkStatus(obj.config.id);
            switch (obj.event) {
                case 'addrespon':
                    let url = "/projectedit/viewaddmpcmodlerespon";
                    let modleId = ${mpcmodle.modleId};
                    // let pintype = 'ff';
                    let addinputpropertylayer = layermanager.viewaddmpcmodlerespon(url, modleId, layer, document);
                    break;
            }
        });

        //监听行工具事件
        table.on('tool(respontable)', function (obj) {
            var data = obj.data;
            // console.log(obj)
            if (obj.event === 'del') {
                if (confirm('确定删除吗？')) {
                    if (api.deletmodlerespon('${pageContext.request.contextPath}/projectedit/deletempcmodlerespon', data['modleresponId'], layer)) {
                        $("#bt_flush_pvtab", document).trigger('click');
                    }
                }
            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatempcmodlerespon';
                let modleresponId = data['modleresponId'];
                layermanager.viewupdatempcmodlerespon(url, modleresponId, layer, document);
            }
        });


    }


    function verifyrespon(param) {
        if (param == "") {
            return true;
        } else {
            //alert("function in " + param);
            let pattern = new RegExp("^{k:[\\d|.|-]+,t:[\\d|.]+,tao:[\\d|.]+}$");
            return pattern.test(param);
        }
    }

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

    function flush_pvtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('pvtable', {
                elem: '#pvtable'
                // ,data:
                , url: '${pageContext.request.contextPath}/projectedit/getmpcmodleproperties'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${mpcmodle.modleId}',
                    pintype: 'pv'
                }
            });

        });
    }

    function flush_sptab() {

    }

    function flush_mvtab() {

        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('mvtable', {
                elem: '#mvtable'
                // ,data:
                , url: '${pageContext.request.contextPath}/projectedit/getmpcmodleproperties'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${mpcmodle.modleId}',
                    pintype: 'mv'
                }
            });

        });

    }


    function flush_fftab() {

        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('fftable', {
                elem: '#fftable'
                // ,data:
                , url: '${pageContext.request.contextPath}/projectedit/getmpcmodleproperties'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleid: '${mpcmodle.modleId}',
                    pintype: 'ff'
                }
            });

        });

    }

    function flush_resppvmvtab() {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;
            table.reload('respontable', {
                elem: '#respontable'
                // ,data:
                , url: '${pageContext.request.contextPath}/projectedit/getmpcmodlerespon'
                , method: 'post'
                , request: {
                    pageName: 'page' //页码的参数名称，默认：page
                    , limitName: 'pagesize' //每页数据量的参数名，默认：limit
                }
                , where: {
                    modleId: '${mpcmodle.modleId}'
                }
            });

        });


    }

    function flush_resppvfftab() {

    }

    function flushtab() {
        flush_pvtab();
        flush_sptab();
        flush_mvtab();
        flush_fftab();
        flush_fftab();
        flush_resppvmvtab();
        flush_resppvfftab();
    }
</script>

<script>
    var table;
    var element;
    var form;
    var layer;
    var opcvertaglayerindex;


    $(document).ready(function () {
        layui.use(['table', 'element', 'form', 'layer'], function () {
            table = layui.table;

            element = layui.element;
            form = layui.form;
            layer = parent.layer === undefined ? layui.layer : parent.layer;
            form.render(); //更新全部
            form.render('select'); //刷新select选择框渲染
            form.on('submit(motifymodlesubmit)', function (data) {
                console.log(' data.field', data.field);
                let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
                let url = "/projectedit/updatemodle";
                if (api.updatemodle(url, data.field, layer)) {
                    layer.close(layer.getFrameIndex(window.name));
                }
                layer.close(index);

                return false;
            });

            pvtabrender(table);

            mvtabrender(table);

            fftabrender(table);
            respontabtabrender(table);


        });
    });


</script>


</body>
</html>
