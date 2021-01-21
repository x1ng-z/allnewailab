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

<div id="bt_flush_pvtab" onclick="flush_outtab()"
     style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>

<form class="layui-form" action="" method="post">

    <div class="layui-form-item">
        <div class="layui-inline">
            <div class="layui-input-inline">
                <input type="text" name="modleId" value="${pidmodle.modleId}"
                       style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"/>
            </div>
        </div>
        <div class="layui-inline">
            <input type="text" name="modletype" value="${pidmodle.modletype}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>

        <div class="layui-inline">
            <input type="text" name="kpid" value="${kp.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>


        <div class="layui-inline">
            <input type="text" name="kiid" value="${ki.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>


        <div class="layui-inline">
            <input type="text" name="kdid" value="${kd.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>


        <div class="layui-inline">
            <input type="text" name="pvid" value="${pv.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>

        <div class="layui-inline">
            <input type="text" name="spid" value="${sp.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>

        <div class="layui-inline">
            <input type="text" name="mvid" value="${mv.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>

        <div class="layui-inline">
            <input type="text" name="ffid" value="${ff.modlepinsId}" autocomplete="off"
                   class="layui-input"
                   style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
        </div>

    </div>

    <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
        <legend>基础参数设置</legend>
    </fieldset>
    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">模型名称</label>
            <div class="layui-input-inline">
                <input type="text" name="modleName" lay-verify="required" autocomplete="off" class="layui-input"
                       value="${pidmodle.modleName}">
            </div>
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">参数:P</label>
            <div class="layui-input-inline">
<%--                lay-verify="number"--%>
                <input type="number" name="kp"  autocomplete="off" class="layui-input"
                       value="${kp.resource.getDouble("value")}" placeholder="请输入P常量值" id="kpconstantid">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">参数:P映射</label>
            <div class="layui-input-inline">
                <select name="kpmodleOpcTag"  lay-search="" id="kpselect" lay-filter="kpselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">
                        <c:choose>
                            <c:when test="${point.modlepinsId==kp.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" kpresourcemodleId="${point.refmodleId}"
                                        kpresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" kpresourcemodleId="${point.refmodleId}"
                                        kpresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
        </div>

    </div>


    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">参数:I</label>
            <div class="layui-input-inline">
                <input type="number" name="ki"  autocomplete="off" class="layui-input"
                       value="${ki.resource.getDouble("value")}" placeholder="请输入I常量值" id="kiconstantid">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">参数:I映射</label>
            <div class="layui-input-inline">
                <select name="kimodleOpcTag"  lay-search="" id="kiselect" lay-filter="kiselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">
                        <c:choose>
                            <c:when test="${point.modlepinsId==ki.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" kiresourcemodleId="${point.refmodleId}"
                                        kiresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" kiresourcemodleId="${point.refmodleId}"
                                        kiresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
        </div>

    </div>


    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">参数:D</label>
            <div class="layui-input-inline">
                <input type="number" name="kd"  autocomplete="off" class="layui-input"
                       value="${kd.resource.getDouble("value")}" placeholder="请输入D常量值" id="kdconstantid" lay-filter="kdconstantid">
            </div>
        </div>

        <div class="layui-inline">
            <label class="layui-form-label">参数:D映射</label>
            <div class="layui-input-inline">
                <select name="kdmodleOpcTag"  lay-search="" id="kdselect" lay-filter="kdselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">
                        <c:choose>
                            <c:when test="${point.modlepinsId==kd.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" kdresourcemodleId="${point.refmodleId}"
                                        kdresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" kdresourcemodleId="${point.refmodleId}"
                                        kdresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
        </div>

    </div>


    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">PV</label>
            <div class="layui-input-inline">
                <input type="number" name="sp" autocomplete="off" class="layui-input"
                       value="${pv.resource.getDouble("value")}" placeholder="请输入PV常量值" id="pvconstantid">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">PV映射</label>
            <div class="layui-input-inline">
                <select name="pvmodleOpcTag"  lay-search="" id="pvselect" lay-filter="pvselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">

                        <c:choose>
                            <c:when test="${point.modlepinsId==pv.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" pvresourcemodleId="${point.refmodleId}"
                                        pvresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" pvresourcemodleId="${point.refmodleId}"
                                        pvresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                            </c:otherwise>
                        </c:choose>


                    </c:forEach>
                </select>
            </div>
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">SP</label>
            <div class="layui-input-inline">
                <input type="number" name="sp"  autocomplete="off" class="layui-input"
                       value="${sp.resource.getDouble("value")}" placeholder="请输入SP常量值" id="spconstantid">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">SP映射</label>
            <div class="layui-input-inline">
                <select name="spmodleOpcTag"  lay-search="" id="spselect" lay-filter="spselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">

                        <c:choose>
                            <c:when test="${point.modlepinsId==sp.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" spresourcemodleId="${point.refmodleId}"
                                        spresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" spresourcemodleId="${point.refmodleId}"
                                        spresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
        </div>
    </div>


    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">MV</label>
            <div class="layui-input-inline">
                <input type="number" name="mv" autocomplete="off" class="layui-input"
                       value="" placeholder="请输入MV常量值" id="mvconstantid">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">MV映射</label>
            <div class="layui-input-inline">
                <select name="mvmodleOpcTag"  lay-search="" id="mvselect" lay-filter="mvselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">
                        <c:choose>
                            <c:when test="${point.modlepinsId==mv.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" mvresourcemodleId="${point.refmodleId}"
                                        mvresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>

                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" mvresourcemodleId="${point.refmodleId}"
                                        mvresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>

                            </c:otherwise>
                        </c:choose>


                    </c:forEach>
                </select>
            </div>
        </div>
    </div>

    <div class="layui-form-item">
        <div class="layui-inline">
            <label class="layui-form-label">FF</label>
            <div class="layui-input-inline">
                <input type="number" name="ff"  autocomplete="off" class="layui-input"
                       value="" placeholder="请输入FF常量值" id="ffconstantid">
            </div>
        </div>
        <div class="layui-inline">
            <label class="layui-form-label">FF映射</label>
            <div class="layui-input-inline">
                <select name="ffmodleOpcTag"  lay-search="" id="ffselect" lay-filter="ffselect">
                    <option value="">请选择</option>
                    <c:forEach items="${points}" var="point" varStatus="Count">
                        <c:choose>
                            <c:when test="${point.modlepinsId==ff.resource.getInteger('modlepinsId')}">
                                <option value="${point.modlePinName}" ffresourcemodleId="${point.refmodleId}"
                                        ffresourcemodlepinsId="${point.modlepinsId}"
                                        selected>${point.opcTagName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${point.modlePinName}" ffresourcemodleId="${point.refmodleId}"
                                        ffresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
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


<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>输出设置</legend>
</fieldset>

<div style="width: 700px">
    <table class="layui-hide" id="outtable" lay-filter="outtable"></table>
</div>


<script type="text/html" id="toolbarout">
    <div class="layui-btn-container">
        <button class="layui-btn layui-btn-sm" id="addoutpinbt" lay-event="addoutpin"
                lay-href="/projectedit/createmodleproperties">
            输出设置
        </button>
    </div>
</script>
<script type="text/html" id="barout">
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
            // console.log("input get parent layer object", parent.layer === undefined);
            layer = parent.layer === undefined ? layui.layer : parent.layer;
            ${kp.resource.getString("resource")=="constant"||kp.resource.getString("resource")!="modle"}
                ? $('#kpconstantid').removeAttr("disabled") : $("#kpconstantid").attr("disabled", true);
            ${ki.resource.getString("resource")=="constant"||ki.resource.getString("resource")!="modle"}
                ? $("#kiconstantid").removeAttr("disabled") : $("#kiconstantid").attr("disabled", true);
            ${kd.resource.getString("resource")=="constant"||kd.resource.getString("resource")!="modle"}
                ? $("#kdconstantid").removeAttr("disabled") : $("#kdconstantid").attr("disabled", true);
            ${pv.resource.getString("resource")=="constant"||pv.resource.getString("resource")!="modle"}
                ? $("#pvconstantid").removeAttr("disabled") : $("#pvconstantid").attr("disabled", true);
            ${sp.resource.getString("resource")=="constant"||sp.resource.getString("resource")!="modle"}
                ? $("#spconstantid").removeAttr("disabled") : $("#spconstantid").attr("disabled", true);
            ${mv.resource.getString("resource")=="constant"||mv.resource.getString("resource")!="modle"}
                ? $("#mvconstantid").removeAttr("disabled") : $("#mvconstantid").attr("disabled", true);
            ${ff.resource.getString("resource")=="constant"||ff.resource.getString("resource")!="modle"}
                ? $("#ffconstantid").removeAttr("disabled") : $("#ffconstantid").attr("disabled", true);

            form.render(); //更新全部
            // form.render('select'); //刷新select选择框渲染
            form.on('submit(motifymodlesubmit)', function (data) {

                let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
                let url = "/projectedit/updatemodle";
                let partcontex = data.field;

                partcontex['kpopcTagName'] = $('#kpselect').find("option:selected").html();
                partcontex['kpresourcemodleId'] = $('#kpselect').find("option:selected").attr("kpresourcemodleId");
                partcontex['kpresourcemodlepinsId'] = $('#kpselect').find("option:selected").attr("kpresourcemodlepinsId");


                partcontex['kiopcTagName'] = $('#kiselect').find("option:selected").html();
                partcontex['kiresourcemodleId'] = $('#kiselect').find("option:selected").attr("kiresourcemodleId");
                partcontex['kiresourcemodlepinsId'] = $('#kiselect').find("option:selected").attr("kiresourcemodlepinsId");


                partcontex['kdopcTagName'] = $('#kdselect').find("option:selected").html();
                partcontex['kdresourcemodleId'] = $('#kdselect').find("option:selected").attr("kdresourcemodleId");
                partcontex['kdresourcemodlepinsId'] = $('#kdselect').find("option:selected").attr("kdresourcemodlepinsId");


                partcontex['pvopcTagName'] = $('#pvselect').find("option:selected").html();
                partcontex['pvresourcemodleId'] = $('#pvselect').find("option:selected").attr("pvresourcemodleId");
                partcontex['pvresourcemodlepinsId'] = $('#pvselect').find("option:selected").attr("pvresourcemodlepinsId");


                partcontex['spopcTagName'] = $('#spselect').find("option:selected").html();
                partcontex['spresourcemodleId'] = $('#spselect').find("option:selected").attr("spresourcemodleId");
                partcontex['spresourcemodlepinsId'] = $('#spselect').find("option:selected").attr("spresourcemodlepinsId");


                partcontex['mvopcTagName'] = $('#mvselect').find("option:selected").html();
                partcontex['mvresourcemodleId'] = $('#mvselect').find("option:selected").attr("mvresourcemodleId");
                partcontex['mvresourcemodlepinsId'] = $('#mvselect').find("option:selected").attr("mvresourcemodlepinsId");


                partcontex['ffopcTagName'] = $('#ffselect').find("option:selected").html();
                partcontex['ffresourcemodleId'] = $('#ffselect').find("option:selected").attr("ffresourcemodleId");
                partcontex['ffresourcemodlepinsId'] = $('#ffselect').find("option:selected").attr("ffresourcemodlepinsId");

                if (api.updatemodle(url, partcontex, layer)) {
                    layer.close(layer.getFrameIndex(window.name));
                }
                layer.close(index);

                return false;
            });


            form.on('select(kpselect)', function (data) {
                console.log('data.value',data.value );
                if (data.value == '') {
                    $('#kpconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#kpconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


            form.on('select(kiselect)', function (data) {
                if (data.value == '') {
                    $('#kiconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#kiconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });

            form.on('select(kdselect)', function (data) {
                if (data.value == '') {
                    $('#kdconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#kdconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });

            form.on('select(pvselect)', function (data) {
                if (data.value == '') {
                    $('#pvconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#pvconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


            form.on('select(spselect)', function (data) {
                if (data.value == '') {
                    $('#spconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#spconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


            form.on('select(mvselect)', function (data) {
                if (data.value == '') {
                    $('#mvconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#mvconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });

            form.on('select(ffselect)', function (data) {
                if (data.value == '') {
                    $('#ffconstantid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#ffconstantid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


            outtabrender(table);


        });

    });


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
                    modleid: '${pidmodle.modleId}',
                    pindir: 'output'
                },
            });

        });
    };


    function outtabrender(table) {

        table.render({
            elem: '#outtable'
            // ,data:
            , url: '/projectedit/getmodleproperties'
            , method: 'post'
            , where: {
                modleid: '${pidmodle.modleId}',
                pindir: 'output'
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
                    let modletype = '${pidmodle.modletype}';
                    let modleId = '${pidmodle.modleId}';
                    let pindir = 'output';
                    let addinputpropertylayer = layermanager.viewaddinputproperty(url, modletype, modleId, pindir, layer, document);
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
                        $("#bt_flush_pvtab", document).trigger('click');
                    }
                }

            } else if (obj.event === 'edit') {
                let url = '${pageContext.request.contextPath}/projectedit/viewupdatemodleproperty';
                let modletype = '${pidmodle.modletype}';
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
