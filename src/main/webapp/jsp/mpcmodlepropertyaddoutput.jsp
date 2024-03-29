<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/29
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html lang="en" >
<head>
    <link rel="shortcut icon"
          href="/img/favicon.ico"  type="image/x-icon"/>
    <meta charset="utf-8">
    <title>add fillermodle input property</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <script src="/js/layui/layui.js" ></script>
    <script src="/js/jquery-3.0.0.js" ></script>
    <script src="/js/api.js"></script>
    <script src="/js/layoutmanager.js"></script>
    <link rel="stylesheet" href="/js/layui/css/layui.css"  media="all">
</head>
<body>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>输出引脚添加</legend>
</fieldset>


<form class="layui-form" action="" method="post">

    <div class="layui-form-item layui-collapse" lay-accordion>

        <div class="layui-form-item">
            <div class="layui-inline">
                <div class="layui-input-inline">
                    <input type="text" name="refmodleId" value="${modleId}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;" />
                </div>
            </div>
            <div class="layui-inline">
                <div class="layui-input-inline">
                    <input type="text" name="modletype" value="${modletype}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;" />
                </div>
            </div>
            <div class="layui-inline">
                <div class="layui-input-inline">
                    <input type="text" name="pindir" value="${pindir}"
                           style="visibility: hidden;width: 0px;height: 0px;z-index: -99;" />
                </div>
            </div>
        </div>

        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">引脚名称</label>
                <div class="layui-input-inline">
                    <input type="text" name="modlePinName" lay-verify="required" autocomplete="off" class="layui-input"
                           value="" placeholder="规则dpvimvj 其中i,j为引脚序列">
                </div>
            </div>

            <div class="layui-inline">
                <label class="layui-form-label">引脚中文描述</label>
                <div class="layui-input-inline">
                    <input type="text" name="opcTagName" lay-verify="required" autocomplete="off" class="layui-input"
                           value="">
                </div>
            </div>
        </div>

    </div>


    <div class="layui-input-block">
        <button type="submit" class="layui-btn" lay-submit="" id="newcontrlmodlepinsubmitbt"
                lay-filter="newcontrlmodlepinsubmitbt"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">立即提交
        </button>
        <button type="reset" class="layui-btn layui-btn-primary"
                style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">重置
        </button>
    </div>
</form>

</body>



<script>


    $(document).ready(function () {

        var layer;
        layui.use(['element', 'form', 'layer'], function () {
            var element = layui.element;
            var form = layui.form;
            // console.log("inputmodlepropertyadd get parent layer object",parent.layer === undefined);
            layer =parent.layer === undefined ? layui.layer : parent.layer;
            form.render(); //更新全部
            form.render('select'); //刷新select选择框渲染
            //监听提交
            form.on('submit(newcontrlmodlepinsubmitbt)', function (data) {
                console.log("in newcontrlmodlepinsubmitbt");
                let url ="${pageContext.request.contextPath}/projectedit/createmodleproperties";
                let partcontex=data.field;
                if(api.addmodleproperties(url,partcontex,layer)){
                    let thiswindon = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(thiswindon); //再执行关闭
                }
                return false;
            });


        });

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
