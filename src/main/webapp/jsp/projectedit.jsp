<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: zaixz
  Date: 2020/9/29
  Time: 13:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <link rel="shortcut icon"
          href="/img/favicon.ico" type="image/x-icon" />
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>project edit</title>
    <link href="/css/font-awesome.min.css" rel="stylesheet">
    <link href="/css/bootstrap.min.css"  rel="stylesheet">
    <link href="/css/jquery-ui.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/css/index.css">
    <link rel="stylesheet" href="/css/menu.css" >
    <link rel="stylesheet" href="/js/layui/css/layui.css" media="all" >

    <script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
    <script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
    <script src="/js/jsplumb.js" ></script>
    <script src="/js/graphlib.min.js" ></script>
    <script src="/js/jquery.min.js" ></script>
    <script src="/js/jquery-ui-1.9.2.min.js" ></script>

    <script src="/js/modle.js"></script>
    <script src="/js/layoutmanager.js"></script>
    <script src="/js/config.js"></script>
    <script src="/js/api.js" ></script>
    <script src="/js/index.js" ></script>
    <script src="/js/panzoom.js" ></script>
    <script src="/js/templefactory.js" ></script>
    <script src="/js/contextMenu.js" ></script>
    <script src="/js/contextMenu.js" ></script>
    <script src="/js/layui/layui.js" charset="utf-8"></script>
</head>

<body>
<!--&lt;!&ndash;  <div id="app">&ndash;&gt;container-fluid-->
<div id="app">
    <div class="layui-container" projectid="${project.projectid}">
        <div class="layui-row">
            <div id="side-buttons" class="layui-col-md2"  style="background-color: rgb(217,237,247);height: 3000px;left: 0">
                <div>
                    <h5 style="font-size: 30px">模块列表</h5>
                    <ul>

                        <li>
                            <a class="btn btn-success btn-controler" href="#" data-template="input" role="button">
                                <i class="layui-icon" style="font-size: 20px; color: #fbfff8;">&#xe66e;输入模块</i>
                            </a>
                        </li>

                        <li>
                            <a class="btn btn-success btn-controler" href="#" data-template="output" role="button">
                                <i class="layui-icon" style="font-size: 20px; color: #fbfff8;">&#xe691;输出模块</i>
                            </a>
                        </li>


                        <li>
                            <a class="btn btn-success btn-controler" href="#" data-template="customize" role="button">
                                <i class="layui-icon" style="font-size: 20px; color: #fbfff8;">&#xe655;自定义脚本</i>
                            </a>
                        </li>

                        <li>
                            <a class="btn btn-success btn-controler" href="#" data-template="filter" role="button">
                                <i class="layui-icon" style="font-size: 20px; color: #fbfff8;">&#xe663;滤波模块</i>
                            </a>
                        </li>


                        <li>
                            <a class="btn btn-success btn-controler" href="#" data-template="mpc" role="button">
                                <i class="layui-icon" style="font-size: 20px; color: #fbfff8;">&#xe638;MPC</i>
                            </a>
                        </li>


                        <li>
                            <a class="btn btn-success btn-controler" href="#" data-template="pid" role="button">
                                <i class="layui-icon" style="font-size: 20px; color: #fbfff8;">&#xe638;PID</i>
                            </a>
                        </li>


                    </ul>


                </div>
            </div>
            <div class="layui-col-md10 mainContainerWrap" id="cannv" style="background-color: rgb(223,240,216);">
                <div style="height: 3000px;" id="mainContainer" data-id="mainContainer" data-projectid="${project.projectid}">
                </div>
            </div>
        </div>
    </div>
</div>


<!-- 注意：如果你直接复制所有代码到本地，上述js路径需要改成你本地的 -->

</body>

<script>
    var area = 'mainContainer'//画布
    var areaId = '#' + area//获取id
    var element;
    var layer;
    var jsplumbinstance;
    var projectstatus_flush_t;
        layui.use(['element', 'layer'], function(){
            element = layui.element;
            console.log("projectedit get parent layer object",parent.layer === undefined);

            layer = parent.layer === undefined ? layui.layer : parent.layer;

            //jueryui 拖拉
            $('.btn-controler').draggable({
                // revert: true,
                helper: 'clone',
                scope: 'ss',
            });

            //jueryui 设置放置
            $(areaId).droppable({
                scope: 'ss',
                drop: function (event, ui) {
                    dropNode(ui.draggable[0].dataset.template, ui.position,$('.layui-container').attr('projectid'))
                }
            });

            $('#app').on('click', function (event) {
                event.stopPropagation()
                event.preventDefault()
                eventHandler(event.target.dataset)
            })

            jsPlumb.ready(main)//document.ready
            //监听折叠
            element.on('collapse(test)', function(data){
                // layer.msg('展开状态：'+ data.show);
            });
            projectstatuflush();


        });
        jsPlumb.importDefaults({
            ConnectionsDetachable: false
        });





    function projectstatuflush() {
        clearInterval(projectstatus_flush_t);
        try {
            let projectresult=api.projectstatus('${project.projectid}');
                projectresult['modules'].forEach(function (module) {
                    let inputcontext=$('#'+module.id).find('#'+module.id+'incontext');

                    let context_='';
                    for(let index=0;index<module.inputproperty.length;index++){
                        context_=context_+(module.inputproperty[index].name.substring(0,7))+ (module.inputproperty[index].pin!=undefined?module.inputproperty[index].pin.substring(0,7):"")  + '\n' + module.inputproperty[index].value+'\n';
                    }
                    // console.log("ins",inuputcontext_3.text());
                    // inuputcontext_3.html(context_);
                    inputcontext[0].innerText=context_;


                    let outputcontext=$('#'+module.id).find('#'+module.id+'outcontext');
                    context_='';
                    for(let index=0;index<module.outputproperty.length;index++){
                        context_=context_+(module.outputproperty[index].name.substring(0,7))+ (module.outputproperty[index].pin!=undefined?module.outputproperty[index].pin.substring(0,7):"")  + '\n' + module.outputproperty[index].value+'\n';
                    }
                    // console.log(outputcontext_3.text());
                    // console.log("context_",context_)
                    outputcontext[0].innerText=context_;



                });
            element.render();
            //buildtable();
        } catch (e) {
            console.log(e);
        }

        projectstatus_flush_t = setInterval(projectstatuflush, 3000);
    }

</script>

</html>