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
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/contrlmodle/contrlmodle.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/layui/css/layui.css" media="all">
    <script src="/js/api.js"></script>
    <script src="/js/layoutmanager.js"></script>
</head>
<body>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>PV设置</legend>
</fieldset>


<form class="layui-form" action="" method="post">

    <div class="layui-form-item layui-collapse" lay-accordion>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV属性设置</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <input type="text" name="refmodleId" value="${pv.refmodleId}" autocomplete="off" class="layui-input"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <input type="text" name="modlepinsId" autocomplete="off" class="layui-input" value="${pv.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <input type="text" name="pintype" autocomplete="off" class="layui-input" value="${pintype}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">pv引脚选择</label>
                        <div class="layui-input-inline">
                            <select name="pvmodlePinName" lay-verify="required">
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
                        <label class="layui-form-label">PV值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="pvpincontantvalue" autocomplete="off" class="layui-input"
                                   value="${pv.resource.getDouble("value")}" id="pvpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">PV数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="pvmodleOpcTag" lay-search=""
                                    lay-filter="selectpvopctag" id="selectpvopctag">
                                <option value="">请选择</option>
                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${point.modlepinsId==pv.resource.getInteger('modlepinsId')}">
                                            <option value="${point.modlePinName}"
                                                    pvresourcemodleId="${point.refmodleId}"
                                                    pvresourcemodlepinsId="${point.modlepinsId}"
                                                    selected>${point.opcTagName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${point.modlePinName}"
                                                    pvresourcemodleId="${point.refmodleId}"
                                                    pvresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                                        </c:otherwise>
                                    </c:choose>

                                </c:forEach>
                            </select>
                        </div>
                    </div>


                    <div class="layui-inline">
                        <label class="layui-form-label">pv死区</label>
                        <div class="layui-input-inline">
                            <input type="number" name="deadZone" autocomplete="off" lay-verify="required|number"
                                   class="layui-input" placeholder="pv的死区" value="${pv.deadZone}"
                                   onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv漏斗初始值</label>
                        <div class="layui-input-inline">
                            <input type="number" name="funelinitValue" autocomplete="off" lay-verify="required|number"
                                   class="layui-input" placeholder="pv的漏斗值" value="${pv.funelinitValue}"
                                   onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">
                漏斗类型：全漏斗(区间控制)，上漏斗(保留上漏斗线，下漏斗线为负无穷)，下漏斗(保留下漏斗线，上漏斗线为正无穷)漏斗类型：全漏斗(区间控制)，上漏斗(保留上漏斗线，下漏斗线为负无穷)，下漏斗(保留下漏斗线，上漏斗线为正无穷)</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">pv漏斗类型</label>
                        <div class="layui-input-inline">
                            <select name="funneltype" lay-verify="required">
                                <option value="">请选择选择</option>
                                <c:choose>
                                    <c:when test="${pv.funneltype eq 'fullfunnel'}">
                                        <option value="fullfunnel" selected>全漏斗</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="fullfunnel">全漏斗</option>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${pv.funneltype eq 'upfunnel'}">
                                        <option value="upfunnel" selected>上漏斗</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="upfunnel">上漏斗</option>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${pv.funneltype eq 'downfunnel'}">
                                        <option value="downfunnel" selected>下漏斗</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="downfunnel">下漏斗</option>
                                    </c:otherwise>
                                </c:choose>

                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">Q值：影响反馈与目标值的偏差度，Q越大算法则要求PV与设定值相差越小</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">q</label>
                        <div class="layui-input-inline">
                            <input type="number" name="Q" autocomplete="off" class="layui-input"
                                   lay-verify="required|number"
                                   placeholder="Q" value="${pv.q}" onmousewheel='scrollFunc()'>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="layui-colla-item">
            <h2 class="layui-colla-title">柔化系数(0&ltalphe&lt1)：PV值与SP值有差值时，沿着平稳的参考轨迹接近sp还是陡峭的参考轨迹接近sp。柔化系数越大，路线越平稳</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">
                    <div class="layui-inline">
                        <label class="layui-form-label">pv的柔化系数</label>
                        <div class="layui-input-inline">
                            <input type="number" name="referTrajectoryCoef" autocomplete="off" class="layui-input"
                                   lay-verify="required"
                                   placeholder="pv的柔化系数" value="${pv.referTrajectoryCoef}" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">柔化方式</label>
                        <div class="layui-input-inline">
                            <select name="tracoefmethod" lay-verify="required">
                                <option value="">柔化方式</option>
                                <c:choose>
                                    <c:when test="${pv.tracoefmethod eq 'before'}">
                                        <option value="before" selected>前期</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="before">前期</option>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${pv.tracoefmethod eq 'after'}">
                                        <option value="after" selected>后期</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="after">后期</option>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                    </div>

                    <%--                    <div class="layui-inline">--%>
                    <%--                        <label class="layui-form-label" type="button" id="showtracoefmethodimg" src="${pageContext.request.contextPath}/img/tracof.jpg">ltalphe柔化效果</label>--%>
                    <%--                    </div>--%>


                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">PV置信区间</h2>
            <div class="layui-colla-content layui-show">
                <div class="layui-form-item">


                    <input type="text" name="pvuppinid" autocomplete="off" class="layui-input" value="${pvup.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">pv上限值</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvuppincontantvalue" autocomplete="off" class="layui-input"
                                   placeholder="pvup位号/值" value="${pvup.resource.getDouble("value")}"
                                   id="pvuppincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv上限数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="pvupmodleOpcTag" lay-search=""
                                    lay-filter="selectpvupopctag" id="selectpvupopctag">
                                <option value="">请选择</option>
                                <c:forEach items="${points}" var="point" varStatus="Count">

                                    <c:choose>
                                        <c:when test="${point.modlepinsId==pvup.resource.getInteger('modlepinsId')}">
                                            <option value="${point.modlePinName}"
                                                    pvupresourcemodleId="${point.refmodleId}"
                                                    pvupresourcemodlepinsId="${point.modlepinsId}"
                                                    selected>${point.opcTagName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${point.modlePinName}"
                                                    pvupresourcemodleId="${point.refmodleId}"
                                                    pvupresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                                        </c:otherwise>
                                    </c:choose>


                                </c:forEach>
                            </select>
                        </div>
                    </div>


                    <input type="text" name="pvdownpinid" autocomplete="off" class="layui-input" value="${pvdown.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">

                    <div class="layui-inline">
                        <label class="layui-form-label">pv下限值</label>
                        <div class="layui-input-inline">
                            <input type="text" name="pvdownpincontantvalue" autocomplete="off" class="layui-input"
                                   placeholder="pvdown位号/值" value="${pvdown.resource.getDouble("value")}"
                                   id="pvdownpincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">pv下限数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="pvdownmodleOpcTag" lay-search=""
                                    lay-filter="selectpvdownopctag" id="selectpvdownopctag">
                                <option value="">请选择</option>
                                <c:forEach items="${points}" var="point" varStatus="Count">

                                    <c:choose>
                                        <c:when test="${point.modlepinsId==pvdown.resource.getInteger('modlepinsId')}">
                                            <option value="${point.modlePinName}"
                                                    pvdownresourcemodleId="${point.refmodleId}"
                                                    pvdownresourcemodlepinsId="${point.modlepinsId}"
                                                    selected>${point.opcTagName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${point.modlePinName}"
                                                    pvdownresourcemodleId="${point.refmodleId}"
                                                    pvdownresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                                        </c:otherwise>
                                    </c:choose>


                                </c:forEach>
                            </select>
                        </div>
                    </div>


                </div>
            </div>
        </div>


        <div class="layui-colla-item">
            <h2 class="layui-colla-title">SP设置</h2>
            <div class="layui-colla-content  layui-show">
                <div class="layui-form-item">


                    <input type="text" name="sppinid" autocomplete="off" class="layui-input" value="${sp.modlepinsId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">


                    <div class="layui-inline">
                        <label class="layui-form-label">SP值</label>
                        <div class="layui-input-inline">
                            <input type="text" name="sppincontantvalue" autocomplete="off" class="layui-input"
                                   placeholder="sp位号/值" value="${sp.resource.getDouble("value")}"
                                   id="sppincontantvalueid" onmousewheel='scrollFunc()'>
                        </div>
                    </div>

                    <div class="layui-inline">
                        <label class="layui-form-label">SP数据绑定</label>
                        <div class="layui-input-inline">
                            <select name="spmodleOpcTag" lay-search=""
                                    lay-filter="selectspopctag" id="selectspopctag">
                                <option value="">请选择</option>
                                <c:forEach items="${points}" var="point" varStatus="Count">
                                    <c:choose>
                                        <c:when test="${point.modlepinsId==sp.resource.getInteger('modlepinsId')}">
                                            <option value="${point.modlePinName}"
                                                    spresourcemodleId="${point.refmodleId}"
                                                    spresourcemodlepinsId="${point.modlepinsId}"
                                                    selected>${point.opcTagName}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${point.modlePinName}"
                                                    spresourcemodleId="${point.refmodleId}"
                                                    spresourcemodlepinsId="${point.modlepinsId}">${point.opcTagName}</option>
                                        </c:otherwise>
                                    </c:choose>

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


</script>


<script>
    var form;
    var layer;
    var element;
    $(document).ready(function () {

        layui.use(['element', 'form', 'layer'], function () {
            element = layui.element;
            form = layui.form;
            layer = parent.layer === undefined ? layui.layer : parent.layer;

                ${pv.resource.getString('resource')=='constant'||pv.resource.getString('resource')!='modle'}? $('#pvpincontantvalueid').removeAttr('disabled'):$('#pvpincontantvalueid').attr('disabled',true) ;

                ${pvup.resource.getString('resource')=='constant'||pvup.resource.getString('resource')!='modle'}? $('#pvuppincontantvalueid').removeAttr('disabled'):$('#pvuppincontantvalueid').attr('disabled',true) ;

                ${pvdown.resource.getString('resource')=='constant'||pvdown.resource.getString('resource')!='modle'}? $('#pvdownpincontantvalueid').removeAttr('disabled'):$('#pvdownpincontantvalueid').attr('disabled',true) ;

                ${sp.resource.getString('resource')=='constant'||sp.resource.getString('resource')!='modle'}? $('#sppincontantvalueid').removeAttr('disabled'):$('#sppincontantvalueid').attr('disabled',true) ;


            form.render(); //更新全部
            form.render('select'); //刷新select选择框渲染
            //监听提交
            form.on('submit(newcontrlmodlepinsubmit)', function (data) {

                console.log("data.field", data.field);
                let url = "/projectedit/updatempcmodleproperties";
                let partcontex = data.field;

                partcontex['pvmodleOpcTagName'] = $('#selectpvopctag').find("option:selected").html();
                partcontex['pvresourcemodleId'] = $('#selectpvopctag').find("option:selected").attr("pvresourcemodleId");
                partcontex['pvresourcemodlepinsId'] = $('#selectpvopctag').find("option:selected").attr("pvresourcemodlepinsId");

                partcontex['pvupmodleOpcTagName'] = $('#selectpvupopctag').find("option:selected").html();
                partcontex['pvupresourcemodleId'] = $('#selectpvupopctag').find("option:selected").attr("pvupresourcemodleId");
                partcontex['pvupresourcemodlepinsId'] = $('#selectpvupopctag').find("option:selected").attr("pvupresourcemodlepinsId");


                partcontex['pvdownmodleOpcTagName'] = $('#selectpvdownopctag').find("option:selected").html();
                partcontex['pvdownresourcemodleId'] = $('#selectpvdownopctag').find("option:selected").attr("pvdownresourcemodleId");
                partcontex['pvdownresourcemodlepinsId'] = $('#selectpvdownopctag').find("option:selected").attr("pvdownresourcemodlepinsId");


                partcontex['spmodleOpcTagName'] = $('#selectspopctag').find("option:selected").html();
                partcontex['spresourcemodleId'] = $('#selectspopctag').find("option:selected").attr("spresourcemodleId");
                partcontex['spresourcemodlepinsId'] = $('#selectspopctag').find("option:selected").attr("spresourcemodlepinsId");

                console.log(partcontex);
                if (api.addmpcmodleproperties(url, partcontex, layer)) {
                    let thiswindon = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(thiswindon); //再执行关闭
                }
                return false;
            });


            form.on('select(selectpvopctag)', function (data) {
                if (data.value == '') {
                    $('#pvpincontantvalueid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#pvpincontantvalueid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


            form.on('select(selectpvupopctag)', function (data) {
                if (data.value == '') {
                    $('#pvuppincontantvalueid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#pvuppincontantvalueid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });

            form.on('select(selectpvdownopctag)', function (data) {
                if (data.value == '') {
                    $('#pvdownpincontantvalueid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#pvdownpincontantvalueid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


            form.on('select(selectspopctag)', function (data) {
                if (data.value == '') {
                    $('#sppincontantvalueid').removeAttr('disabled');
                    // $('#modlePincontantvalueid').attr('value','');
                    form.render();
                    element.render();
                } else {
                    $('#sppincontantvalueid').attr('disabled', true);
                    form.render();
                    element.render();
                }
            });


        });


        // var tratips;

        // $('#showtracoefmethodimg').on({
        //     mouseenter: function () {
        //         tratips = layer.tips('<img width="320px" height="240px" src="' + $(this).attr('src') + '">', this, {
        //             time: 0,
        //             area: ['320px', '240px']
        //         });
        //     },
        //     mouseleave: function () {
        //         layer.close(tratips);
        //     }
        // });
    });


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
