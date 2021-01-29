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
    <title>new mv pin</title>
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
    <legend>MV设置</legend>
</fieldset>

<form class="layui-form" action="" method="post">
    <div class="layui-form-item layui-collapse" lay-accordion>
        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="refmodleId" value="${mv.refmodleId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <input type="text" name="mvpinid" autocomplete="off" class="layui-input" value="${mv.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <input type="text" name="pintype" autocomplete="off" class="layui-input" value="${pintype}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">MV引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="mvmodlePinName" lay-verify="required">
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
                        <label class="layui-form-label">MV值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="mvpincontantvalue" autocomplete="off" class="layui-input"
                                   value="${mv.resource.getDouble("value")}" id="mvpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">MV数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="mvmodleOpcTag" lay-search=""
                                    lay-filter="selectmvopctag" id="selectmvopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==mv.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>


                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==mv.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvresourcemodleId="${parentpin.refmodleId}"
                                                            mvresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvresourcemodleId="${parentpin.refmodleId}"
                                                            mvresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
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
            <h2 class="layui-colla-title">R设置</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">影响MV调节幅度，R越大算法则要求每次的deltaMV越小</p>

                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">r</label>
                        <div class="layui-input-inline">
                            <input type="number" name="R" autocomplete="off" class="layui-input" lay-verify="required"
                                   placeholder="mv的R" value="${mv.r}" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv高限</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">mv增量绝对值不能超过该值</p>
                <div class="layui-form-item">

                    <div class="layui-inline">
                        <label class="layui-form-label">dmvHigh</label>
                        <div class="layui-input-inline">
                            <input type="number" name="dmvHigh" autocomplete="off" class="layui-input"
                                   lay-verify="required"
                                   placeholder="dmvHigh" value="${mv.dmvHigh}" onmousewheel='scrollFunc()'>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">dmv低限</h2>
            <div class="layui-colla-content layui-show">
                <p style="font-weight: bolder;font-size: 1.1rem">低于此不进行调节</p>
                <div class="layui-form-item">

                    <div class="layui-inline">
                        <label class="layui-form-label">dmvLow</label>
                        <div class="layui-input-inline">
                            <input type="number" name="dmvLow" autocomplete="off" class="layui-input"
                                   lay-verify="required"
                                   placeholder="dmvLow" value="${mv.dmvLow}" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                </div>

            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">MV上限设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="mvuppinid" autocomplete="off" class="layui-input" value="${mvup.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;" >

                    <div class="layui-inline">
                        <label class="layui-form-label">MV上限值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="mvuppincontantvalue" autocomplete="off" class="layui-input"
                                   value="${mvup.resource.getDouble("value")}" id="mvuppincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">MV上限数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="mvupmodleOpcTag" lay-search=""
                                    lay-filter="selectmvupopctag" id="selectmvupopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==mvup.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvupresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvupresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvupresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvupresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>
                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==mvup.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvupresourcemodleId="${parentpin.refmodleId}"
                                                            mvupresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvupresourcemodleId="${parentpin.refmodleId}"
                                                            mvupresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
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
            <h2 class="layui-colla-title">MV下限设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">

                    <input type="text" name="mvdownpinid" autocomplete="off" class="layui-input"
                           value="${mvdown.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">MV下限值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="mvdownpincontantvalue" autocomplete="off" class="layui-input"
                                   value="${mvdown.resource.getDouble("value")}" id="mvdownpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">MV下限数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="mvdownmodleOpcTag" lay-search=""
                                    lay-filter="selectmvdownopctag" id="selectmvdownopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==mvdown.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvdownresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvdownresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvdownresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvdownresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>



                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==mvdown.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvdownresourcemodleId="${parentpin.refmodleId}"
                                                            mvdownresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvdownresourcemodleId="${parentpin.refmodleId}"
                                                            mvdownresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
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
            <h2 class="layui-colla-title">MV反馈设置</h2>
            <div class="layui-colla-content layui-show">

                <div class="layui-form-item">

                    <input type="text" name="mvfbpinid" autocomplete="off" class="layui-input"
                           value="${mvfb.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">
                    <div class="layui-inline">
                        <label class="layui-form-label">MV反馈值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="mvfbpincontantvalue" autocomplete="off" class="layui-input"
                                   value="${mvfb.resource.getDouble("value")}" id="mvfbpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">MV反馈数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="mvfbmodleOpcTag" lay-search=""
                                    lay-filter="selectmvfbopctag" id="selectmvfbopctag">
                                <option value="">请选择</option>
<%--                                <c:forEach items="${points}" var="point" varStatus="Count">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${point.modlepinsId==mvfb.resource.getInteger('modlepinsId')}">--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvfbresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvfbresourcemodlepinsId="${point.modlepinsId}"--%>
<%--                                                    selected>${point.opcTagName}</option>--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                            <option value="${point.modlePinName}"--%>
<%--                                                    mvfbresourcemodleId="${point.refmodleId}"--%>
<%--                                                    mvfbresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>--%>
<%--                                        </c:otherwise>--%>
<%--                                    </c:choose>--%>
<%--                                </c:forEach>--%>


                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <optgroup label="${point.key}">
                                        <c:forEach items="${point.value}" var="parentpin">
                                            <c:choose>
                                                <c:when test="${parentpin.modlepinsId==mvfb.resource.getInteger('modlepinsId')}">
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvfbresourcemodleId="${parentpin.refmodleId}"
                                                            mvfbresourcemodlepinsId="${parentpin.modlepinsId}"
                                                            selected>${parentpin.modlePinName}(${parentpin.opcTagName})</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${parentpin.modlepinsId}_${parentpin.modlePinName}" mvfbresourcemodleId="${parentpin.refmodleId}"
                                                            mvfbresourcemodlepinsId="${parentpin.modlepinsId}">${parentpin.modlePinName}(${parentpin.opcTagName})</option>
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

    layui.use(['element', 'form', 'layer'], function () {
        var element = layui.element;
        var form = layui.form,
            layer = parent.layer === undefined ? layui.layer : parent.layer;
            ${mv.resource.getString('resource')=='constant'||mv.resource.getString('resource')!='modle'} ? $('#mvpincontantvalueid').removeAttr('disabled') : $('#mvpincontantvalueid').attr('disabled', true);
            ${mvup.resource.getString('resource')=='constant'||mvup.resource.getString('resource')!='modle'}? $('#mvuppincontantvalueid').removeAttr('disabled'):$('#mvuppincontantvalueid').attr('disabled',true) ;
            ${mvdown.resource.getString('resource')=='constant'||mvdown.resource.getString('resource')!='modle'}? $('#mvdownpincontantvalueid').removeAttr('disabled'):$('#mvdownpincontantvalueid').attr('disabled',true) ;
            ${mvfb.resource.getString('resource')=='constant'||mvfb.resource.getString('resource')!='modle'}? $('#mvfbpincontantvalueid').removeAttr('disabled'):$('#mvfbpincontantvalueid').attr('disabled',true) ;

        form.render(); //更新全部
        form.render('select'); //刷新select选择框渲染
        //监听提交
        form.on('submit(newcontrlmodlepinsubmit)', function (data) {
            // let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
            let url = "/projectedit/updatempcmodleproperties";
            let partcontex = data.field;
            partcontex['mvmodleOpcTagName'] = $('#selectmvopctag').find("option:selected").html();
            partcontex['mvresourcemodleId'] = $('#selectmvopctag').find("option:selected").attr("mvresourcemodleId");
            partcontex['mvresourcemodlepinsId'] = $('#selectmvopctag').find("option:selected").attr("mvresourcemodlepinsId");

            partcontex['mvupmodleOpcTagName'] = $('#selectmvupopctag').find("option:selected").html();
            partcontex['mvupresourcemodleId'] = $('#selectmvupopctag').find("option:selected").attr("mvupresourcemodleId");
            partcontex['mvupresourcemodlepinsId'] = $('#selectmvupopctag').find("option:selected").attr("mvupresourcemodlepinsId");


            partcontex['mvdownmodleOpcTagName'] = $('#selectmvdownopctag').find("option:selected").html();
            partcontex['mvdownresourcemodleId'] = $('#selectmvdownopctag').find("option:selected").attr("mvdownresourcemodleId");
            partcontex['mvdownresourcemodlepinsId'] = $('#selectmvdownopctag').find("option:selected").attr("mvdownresourcemodlepinsId");


            partcontex['mvfbmodleOpcTagName'] = $('#selectmvfbopctag').find("option:selected").html();
            partcontex['mvfbresourcemodleId'] = $('#selectmvfbopctag').find("option:selected").attr("mvfbresourcemodleId");
            partcontex['mvfbresourcemodlepinsId'] = $('#selectmvfbopctag').find("option:selected").attr("mvfbresourcemodlepinsId");

            // console.log(partcontex);
            if (api.addmpcmodleproperties(url, partcontex, layer)) {
                let thiswindon = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                parent.layer.close(thiswindon); //再执行关闭
            }
            // layer.close(index);
            return false;


            // console.log(JSON.stringify(data.field))
            // return false;
        });

        form.on('select(selectmvopctag)', function (data) {
            if (data.value == '') {
                $('#mvpincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#mvpincontantvalueid').attr('disabled', true);
                form.render();
                element.render();
            }
        });


        form.on('select(selectmvupopctag)', function (data) {
            if (data.value == '') {
                $('#mvuppincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#mvuppincontantvalueid').attr('disabled', true);
                form.render();
                element.render();
            }
        });


        form.on('select(selectmvdownopctag)', function (data) {
            if (data.value == '') {
                $('#mvdownpincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#mvdownpincontantvalueid').attr('disabled', true);
                form.render();
                element.render();
            }
        });

        form.on('select(selectmvfbopctag)', function (data) {
            if (data.value == '') {
                $('#mvfbpincontantvalueid').removeAttr('disabled');
                // $('#modlePincontantvalueid').attr('value','');
                form.render();
                element.render();
            } else {
                $('#mvfbpincontantvalueid').attr('disabled', true);
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
