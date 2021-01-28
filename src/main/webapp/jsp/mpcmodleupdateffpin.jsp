<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core_1_1" %>
<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/21
  Time: 9:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="shortcut icon"
          href="../img/favicon.ico" type="image/x-icon"/>
    <meta charset="utf-8">
    <title>new pv pin</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="/js/api.js"></script>
    <script src="/js/layoutmanager.js"></script>
</head>
<body>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>FF(前馈)设置</legend>
</fieldset>

<form class="layui-form" action="" method="post">
    <div class="layui-form-item layui-collapse" lay-accordion>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF属性设置</h2>
            <div class="layui-colla-content  layui-show">
                <div class="layui-form-item">

                    <input type="text" name="refmodleId" value="${ff.refmodleId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <input type="text" name="ffpinid" value="${ff.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <input type="text" name="pintype" autocomplete="off" class="layui-input" value="${pintype}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">FF引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="ffmodlePinName" lay-verify="required">
                                <option value="">请选择引脚名称</option>
                                <c:forEach var="pinindex" items="${unuserpinscope}" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${pinindex==pinorder}">
                                            <option value="${pintype}${pinindex}"
                                                    selected>${pintype}${pinindex}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${pintype}${pinindex}">${pintype}${pinindex}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">FF值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="ffpincontantvalue" autocomplete="off" class="layui-input"
                                   value="${ff.resource.getDouble("value")}" id="ffpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">FF数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="ffmodleOpcTag" lay-search=""
                                    lay-filter="selectffopctag" id="selectffopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==ff.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    ffresourcemodleId="${point.refmodleId}"--%>
<%--                                                    ffresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    ffresourcemodleId="${point.refmodleId}"--%>
<%--                                                    ffresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>


                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==ff.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlePinName}" ffresourcemodleId="${parentpin.refmodleId}"
                                                            ffresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlePinName}" ffresourcemodleId="${parentpin.refmodleId}"
                                                            ffresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </optgroup>

                                </c:forEach>
                            </select>
                        </div>
                    </div>


                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)上限设置</h2>
            <div class="layui-colla-content  layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">FF值的置信区间上限</p>
                <div class="layui-form-item">

                    <input type="text" name="ffuppinid" value="${ffup.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">FFUP值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="ffuppincontantvalue" autocomplete="off" class="layui-input"
                                   value="${ffup.resource.getDouble("value")}" id="ffuppincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">FFUP数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="ffupmodleOpcTag" lay-search=""
                                    lay-filter="selectffupopctag" id="selectffupopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==ffup.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    ffupresourcemodleId="${point.refmodleId}"--%>
<%--                                                    ffupresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    ffupresourcemodleId="${point.refmodleId}"--%>
<%--                                                    ffupresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>

                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==ffup.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlePinName}" ffupresourcemodleId="${parentpin.refmodleId}"
                                                            ffupresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlePinName}" ffupresourcemodleId="${parentpin.refmodleId}"
                                                            ffupresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </optgroup>

                                </c:forEach>

                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">FF(前馈)下限设置</h2>
            <div class="layui-colla-content  layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">FF值的置信区间下限</p>
                <div class="layui-form-item">

                    <input type="text" name="ffdownpinid" value="${ffdown.modlepinsId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <div class="layui-inline">
                        <label class="layui-form-label">FFDOWN值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="ffdownpincontantvalue" autocomplete="off" class="layui-input"
                                   value="${ffdown.resource.getDouble("value")}" id="ffdownpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">FFDOWN数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="ffdownmodleOpcTag" lay-search=""
                                    lay-filter="selectffdownopctag" id="selectffdownopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==ffdown.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    ffdownresourcemodleId="${point.refmodleId}"--%>
<%--                                                    ffdownresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    ffdownresourcemodleId="${point.refmodleId}"--%>
<%--                                                    ffdownresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>

                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==ffdown.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlePinName}" ffdownresourcemodleId="${parentpin.refmodleId}"
                                                            ffdownresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlePinName}" ffdownresourcemodleId="${parentpin.refmodleId}"
                                                            ffdownresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </optgroup>

                                </c:forEach>

                            </select>
                        </div>
                    </div>

                </div>
            </div>
        </div>

    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="newcontrlmodlepinsubmitbt"
                lay-filter="newcontrlmodlepinsubmit"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">立即提交
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>

</body>

<script>
    var element;
    var form;
    var layer;
    layui.use(['element', 'form', 'layer'], function () {
        element = layui.element;
        form = layui.form;
        layer = parent.layer === undefined ? layui.layer : parent.layer;
        ${ff.resource.getString('resource')=='constant'||ff.resource.getString('resource')!='modle'} ? $('#ffpincontantvalueid').removeAttr('disabled') : $('#ffpincontantvalueid').attr('disabled', true);
        ${ffup.resource.getString('resource')=='constant'||ffup.resource.getString('resource')!='modle'} ? $('#ffuppincontantvalueid').removeAttr('disabled') : $('#ffuppincontantvalueid').attr('disabled', true);
        ${ffdown.resource.getString('resource')=='constant'||ffdown.resource.getString('resource')!='modle'} ? $('#ffdownpincontantvalueid').removeAttr('disabled') : $('#ffdownpincontantvalueid').attr('disabled', true);

        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染
        //监听提交
        form.on('submit(newcontrlmodlepinsubmit)', function (data) {

            let url = "/projectedit/updatempcmodleproperties";
            let partcontex = data.field;
            partcontex['ffmodleOpcTagName'] = $('#selectffopctag').find("option:selected").html();
            partcontex['ffresourcemodleId'] = $('#selectffopctag').find("option:selected").attr("ffresourcemodleId");
            partcontex['ffresourcemodlepinsId'] = $('#selectffopctag').find("option:selected").attr("ffresourcemodlepinsId");

            partcontex['ffupmodleOpcTagName'] = $('#selectffupopctag').find("option:selected").html();
            partcontex['ffupresourcemodleId'] = $('#selectffupopctag').find("option:selected").attr("ffupresourcemodleId");
            partcontex['ffupresourcemodlepinsId'] = $('#selectffupopctag').find("option:selected").attr("ffupresourcemodlepinsId");


            partcontex['ffdownmodleOpcTagName'] = $('#selectffdownopctag').find("option:selected").html();
            partcontex['ffdownresourcemodleId'] = $('#selectffdownopctag').find("option:selected").attr("ffdownresourcemodleId");
            partcontex['ffdownresourcemodlepinsId'] = $('#selectffdownopctag').find("option:selected").attr("ffdownresourcemodlepinsId");

            // console.log(partcontex);
            if (api.addmpcmodleproperties(url, partcontex, layer)) {
                let thiswindon = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                parent.layer.close(thiswindon); //再执行关闭
            }
            return false;
        });

        form.on('select(selectffopctag)', function (data) {
            if (data.value == '') {
                $('#ffpincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#ffpincontantvalueid').attr('disabled', true);
                form.render();
                element.render();
            }
        });

        form.on('select(selectffdownopctag)', function (data) {
            if (data.value == '') {
                $('#ffdownpincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#ffdownpincontantvalueid').attr('disabled', true);
                form.render();
                element.render();
            }
        });



        form.on('select(selectffupopctag)', function (data) {
            if (data.value == '') {
                $('#ffuppincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#ffuppincontantvalueid').attr('disabled', true);
                form.render();
                element.render();
            }
        });


    });

</script>


<script>

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
</html>
