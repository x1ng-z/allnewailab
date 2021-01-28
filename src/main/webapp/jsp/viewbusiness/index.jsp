<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon"
          href="/img/favicon.ico" type="image/x-icon"/>
    <meta charset="UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="ie=edge,chrome=1">
    <title>${companyName}过程控制系统</title>
    <link rel="stylesheet" href="/js/layui/css/layui.css"/>
<%--    <link rel="stylesheet" href="../css/index.css"/>--%>
    <script src="${pageContext.request.contextPath}/js/layui/layui.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script src="/js/api.js" ></script>
    <script src="/js/layoutmanager.js"></script>
<%--    <script src="${pageContext.request.contextPath}/js/aimodle/newaimodle.js"></script>--%>
<%--    <script src="${pageContext.request.contextPath}/js/contrlmodle/contrlmodle.js"></script>--%>
    <style>
        .layui-layout-admin .layui-side-menu {
            z-index: 1001;
        }

        .layui-layout-admin .layui-logo {
            left: 0px;
            top: 0px;
            width: 220px;
            height: 49px;
            overflow: hidden;
            padding-top: 0px;
            padding-right: 15px;
            padding-bottom: 0px;
            padding-left: 15px;
            font-weight: 300;
            position: fixed;
            z-index: 1002;
            box-sizing: border-box;
            background-repeat: no-repeat;
            background-position-x: center;
            background-position-y: center;
        }

        .layui-admin-pagetabs .layui-tab {
            margin-top: 0px;
        }

        .layui-admin-pagetabs .layui-tab-title li {
            border-right: 1px solid #f6f6f6;
        }

        .layui-admin-pagetabs .layui-tab-title .layui-this {
            background-color: rgb(246, 246, 246);
        }

        .layui-admin-iframe {
            left: 0px;
            top: 0px;
            right: 0px;
            bottom: 0px;
            width: 100%;
            border: 0px;
        }
        .layui-bg-white{
            background-color: white;
        }



        .layui-body {
            overflow: hidden;
        }

        .layui-tab-content {
            height: 100%;
            padding: 0;
        }

        .layui-tab-item {
            position: absolute;
            top: 41px;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 0;
            margin: 0;
            -webkit-overflow-scrolling: touch;
            overflow: auto;
        }

        /*.layui-tab-title .layui-this{ background-color:#1AA094; color:#fff; }*/
        .layui-tab-title .layui-this:after {
            border: none;
        }

        .layui-tab-title li cite {
            font-style: normal;
            padding-left: 5px;
        }

        .clildFrame.layui-tab-content {
            padding-right: 0;
        }

        .clildFrame.layui-tab-content iframe {
            width: 100%;
            height: 100%;
            border: none;
            min-width: 320px;
            position: absolute;
        }

    </style>
    <script>
        //iframe高度自适应
        function setIframeHeight(iframe) {
            if (iframe) {
                var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
                if (iframeWin.document.body) {
                    // iframe.height =$(window).height()-144;
                    // iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
                }
                // console.log(iframe.contentWindow.document.documentElement.scrollHeight )
                // console.log(iframe.contentWindow.document.body.scrollHeight )
                // console.log(iframe.contentDocument.parentWindow.document.body.scrollHeight )
            }
        };
    </script>
</head>
<body class="layui-layout-body" style="position:absolute;top:0;left:0;height:100%;width:100%" onload="loadbody() ">
<%--<button id="bt_flush_nav" onclick="reflush()" style="visibility: hidden;z-index: -99">;</button>--%>
<%--<button id="bt_flush_nav" onclick="reflush()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">;</button>--%>
<div class="layui-layout layui-layout-admin">
    <!--头部-->
    <div class="layui-header">
        <!--头部左侧导航-->
        <div id="bt_flush_nav" onclick="reflush()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
        <div id="bt_close_tab" onclick="closetab(this)" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;"></div>
        <ul class="layui-nav layui-layout-left" lay-filter="leftmenu">
            <li class="layui-nav-item">
                <a href="javascript:;" class="hidetab" title="隐藏左侧菜单"><i class="layui-icon layui-icon-shrink-right"></i></a>
            </li>
            <li class="layui-nav-item">
                <a href="./index.do" title="主页"><i class="layui-icon layui-icon-home"></i></a>
            </li>
            <li class="layui-nav-item">
                <a href="javascript:window.location.reload();" title="刷新"><i
                        class="layui-icon layui-icon-refresh-3"></i></a>
            </li>
            <li class="layui-nav-item layui-hide-xs">
                <input class="layui-input layui-input-search" type="text" placeholder="搜索" autocomplete="off"/>
            </li>
        </ul>
        <!--头部右侧导航-->
        <ul class="layui-nav layui-layout-right" lay-filter="rightmenu">
            <li class="layui-nav-item">
                <a href="javascript:;" lay-href="${pageContext.request.contextPath}/login/message.do"><i
                        class="layui-icon layui-icon-speaker"></i>消息中心<span class="layui-badge">9</span></a>
            </li>
            <li class="layui-nav-item">
                <a href="javascript:;">
                    <i class="layui-icon layui-icon-user"></i>
                    admin
                </a>
                <dl class="layui-nav-child">
                    <dd><a href="javascript:;" lay-href="${pageContext.request.contextPath}/login/userinfo.do"><i
                            class="layui-icon layui-icon-form"></i>基本资料</a></dd>
                    <dd><a href="javascript:;" lay-href="${pageContext.request.contextPath}/login/changepassword.do"><i
                            class="layui-icon layui-icon-password"></i>修改密码</a></dd>
                </dl>
            </li>
            <li class="layui-nav-item">
                <a href="javascript:;">锁屏<i class="layui-icon layui-icon-password"></i></a>
            </li>
            <li class="layui-nav-item"><a href="${pageContext.request.contextPath}/login/login.do">退出</a></li>
        </ul>
    </div>
    <!--左侧-->
    <div class="layui-side layui-side-menu">
        <!--带滚动条垂直导航-->
        <div class="layui-side-scroll layui-bg-black">
            <div class="layui-logo">${companyName}过程控制系统</div>
            <ul class="layui-nav layui-nav-tree" lay-filter="navtree">
                <!--默认展开-->
                <li class="layui-nav-item layui-nav-itemed">
                    <a href="javascript:;" title="控制回路"><i class="layui-icon layui-icon-app"></i><cite>控制回路</cite></a>
                    <dl class="layui-nav-child">
                        <c:forEach var="md" items="${contrlmodles}" varStatus="Count">
                            <dd><a href="javascript:;"
                                   lay-href="/projectedit/vieweditproject?projectid=${md.projectid}" projectid="contrlmodle${md.projectid}">${md.name}</a>
                            </dd>
                        </c:forEach>
                    </dl>
                </li>

                <li class="layui-nav-item">
                    <a href="javascript:;" title="视频及大数据分析"><i class="layui-icon layui-icon-template-1"></i><cite>视频及大数据分析</cite></a>
                    <dl class="layui-nav-child">
                        <c:forEach var="ai" items="${aimodle}" varStatus="Count">
                            <dd><a href="javascript:;"
                                   lay-href="${pageContext.request.contextPath}/aimodle/aimodlestatus/${ai.modleid}.do">${ai.algorithmName}</a>
                            </dd>
                        </c:forEach>
                    </dl>
                </li>

                <li class="layui-nav-item">
                    <a href="javascript:;" title="用户"><i class="layui-icon layui-icon-user"></i><cite>用户</cite></a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:;" lay-href="${pageContext.request.contextPath}/user.do">角色管理</a>
                        </dd>
                    </dl>
                </li>
                <li class="layui-nav-item">
                    <a href="javascript:;" title="设置"><i class="layui-icon layui-icon-set"></i><cite>设置</cite></a>
                    <dl class="layui-nav-child">
<%--                        <dd><a href="javascript:;"--%>
<%--                               lay-href="${pageContext.request.contextPath}/modle/newmodle.do">模型新建</a></dd>--%>
                        <dd><a href="javascript:;"
                               id="newproject" lay-href="${pageContext.request.contextPath}/projectedit/newproject">控制工程新建</a></dd>
                        <dd><a href="javascript:;" lay-href="${pageContext.request.contextPath}/aimodle/newaimodle.do"
                               id="newaimodle">ai模型新建</a></dd>
                        <dd><a href="javascript:;" lay-href="${pageContext.request.contextPath}/opc/opcserveinfo.do"
                               id="opcserveinfo">opc配置</a></dd>
                        <dd><a href="javascript:;" lay-href="${pageContext.request.contextPath}/login/set.do">系统设置</a>
                        </dd>
                    </dl>
                </li>
            </ul>
        </div>
    </div>
    <!--正文-->
    <div class="layui-body layui-bg-gray">
        <!--选项卡-->
        <div class="layui-admin-pagetabs">
            <div class="layui-tab layui-tab-brief marg0" lay-allowClose="true" lay-filter="pagetabs" scrolling="no">
                <ul class="layui-tab-title layui-bg-white">
                    <li class="layui-this" lay-id="${pageContext.request.contextPath}/home"><i
                            class="layui-icon layui-icon-home"></i></li>
                </ul>
                <div class="layui-tab-content clildFrame">
                    <div class="layui-tab-item layui-show">
                        <iframe src="${pageContext.request.contextPath}/home" class="layui-admin-iframe"
                                scrolling="auto" frameborder="0" onload="setIframeHeight(this);"></iframe>
                    </div>
                </div>
            </div>
        </div>

    </div>
    <!--底部-->
    <div class="layui-footer">© 2020 智能制造研究所</div>
</div>

<script>
    // var myelement;
    var indexdocument = document;

    $(document).ready(function () {
        indexdocument = document;
    });


    layui.use(['element', 'layer'], function () {

        var myelement = layui.element
            , layer = layui.layer
            , $ = layui.jquery;
        //隐藏tab主页关闭标签
        console.log("ssss" + $(window).height())

        $(".layui-tab-title li:first-child i:last-child").css("display", "none");
        //tab点击监控
        myelement.on('tab(pagetabs)', function (data) {
            //tab切换时，左侧菜单对应选中
            var url = $(this).attr("lay-id");
            $(".layui-nav-tree li dd").each(function (i, e) {
                if ($(this).find("a").attr("lay-href") === url) {
                    $(this).attr("class", "layui-this");
                } else {
                    $(this).attr("class", "");
                }
            })
            //
            //console.log("%%%%%%%%%%%%%")
            //console.log($(this));
            //console.log("%%%%%%%%%%%%%")
        });
        //顶部左侧菜单监控
        myelement.on('nav(leftmenu)', function (elem) {
            //隐藏显示侧边菜单
            if (elem[0].className == "hidetab") {//隐藏
                //侧边菜单伸缩
                $(".layui-side-menu").animate({width: $(".layui-side-menu").width() - 144 + "px"});
                //正文伸缩
                $(".layui-body").animate({left: $(".layui-body").position().left - 144 + "px"});
                //底部伸缩
                $(".layui-footer").animate({left: $(".layui-footer").position().left - 144 + "px"});
                $(this).attr("class", "showtab");
                $(this).find("i").attr("class", "layui-icon layui-icon-spread-left");
                //侧边菜单只显示图标
                $(".layui-nav-tree").find("li").each(function (em, ind) {
                    $(this).find("cite").css("display", "none");
                    $(this).find("dl").css("display", "none");
                });
            } else if (elem[0].className == "showtab") {//显示
                $(".layui-side-menu").animate({width: $(".layui-side-menu").width() + 144 + "px"});
                $(".layui-body").animate({left: $(".layui-body").position().left + 144 + "px"});
                $(".layui-footer").animate({left: $(".layui-footer").position().left + 144 + "px"});
                $(this).attr("class", "hidetab");
                $(this).find("i").attr("class", "layui-icon layui-icon-shrink-right");
                $(".layui-nav-tree").find("li").each(function (em, ind) {
                    $(this).find("cite").css("display", "");
                    $(this).find("dl").css("display", "");
                });
            } else {

            }
        });
        //顶部右侧菜单监控
        myelement.on('nav(rightmenu)', function (elem) {
            var url = $(this).attr("lay-href");
            console.log(elem[0].innerText);
            console.log(elem[0].innerText);
            if ((url !== undefined) && (elem[0].innerText.indexOf("消息中心") === -1)) {
                layer.open({
                    title: elem[0].innerText,
                    type: 2,
                    content: url,
                    area: ['600px', '500px']
                });
            }
            if ((elem[0].innerText.indexOf("消息中心") !== -1)) {
                myelement.tabAdd('pagetabs', {
                    title: '<i class="layui-icon layui-icon-refresh" onclick="document.getElementById('+url+').contentWindow.location.reload()"></i>&nbsp'+elem[0].innerText
                    ,
                    content: '<iframe id="'+url+'" src="' + url + '" class="layui-admin-iframe" scrolling="auto" frameborder="0" "></iframe>'
                    ,
                    id: url
                });
                //切换选项卡
                myelement.tabChange('pagetabs', url);
            }
            if (elem[0].innerText === "锁屏") {
                layer.open({
                    title: "已锁屏"
                    ,
                    content: '<input name="pass" class="layui-input" type="text" placeholder="请输入密码,默认123123" autocomplete="off"/>'
                    ,
                    btnAlign: 'c'
                    ,
                    anim: 1
                    ,
                    btn: ['解锁']
                    ,
                    yes: function (index, layero) {
                        var pass = layero.find('.layui-layer-content input').val();
                        if (pass === "123123") {
                            layer.close(index);
                        } else {
                            layer.title("密码不正确！", index);
                        }
                    }
                    ,
                    cancel: function () {
                        return false //开启该代码可禁止点击该按钮关闭
                    }
                });
            }
        })
        //左侧垂直菜单监控
        myelement.on('nav(navtree)', function (elem) {
            if ($(".layui-side-menu").width() < 144) {//200
                $(".layui-side-menu").animate({width: $(".layui-side-menu").width() + 144 + "px"});
                $(".layui-body").animate({left: $(".layui-body").position().left + 144 + "px"});
                $(".layui-footer").animate({left: $(".layui-footer").position().left + 144 + "px"});
                $(".layui-layout-left li:first-child").find("a").attr("class", "hidetab");
                $(".layui-layout-left li:first-child").find("i").attr("class", "layui-icon layui-icon-shrink-right");
                $(".layui-nav-tree").find("li").each(function (em, ind) {
                    $(this).find("cite").css("display", "");
                    $(this).find("dl").css("display", "");
                });
            } else {
                if ($(this).attr("lay-href") !== undefined) {


                    if (($(this).attr("id") != undefined) && ($(this).attr("id") == 'newproject')) {

                        let indexlayer =layermanager.viewnewproject($(this).attr("lay-href"),layer);

                    } else if (($(this).attr("id") != undefined) && ($(this).attr("id") == 'newcontrlmodle')){

                        let indexlayer=newcontrlmodlewindows(layer,this);
                    }else {
                        let flag = true;
                        //url
                        let url = $(this).attr("lay-href");
                        //判断选项卡中是否存在已打开的页面，如果有直接切换到打开页面
                        $(".layui-tab-title li").each(function (i, e) {
                            if ($(this).attr("lay-id") === url) {
                                //切换选项卡
                                myelement.tabChange('pagetabs', url);
                                flag = false;
                            }
                        })
                        if (flag) {
                            //新增选项卡
                            let timestamp=new Date();

                            myelement.tabAdd('pagetabs', {
                                title:   '<i class="layui-icon layui-icon-refresh" onclick="document.getElementById('+timestamp.getMilliseconds()+').contentWindow.location.reload()"></i>&nbsp;'+elem[0].innerText
                                ,
                                content: '<iframe id="'+ timestamp.getMilliseconds() + '" src="' + url + '" class="layui-admin-iframe" scrolling="auto" frameborder="0" "></iframe>'
                                ,
                                id: url
                            });
                            //切换选项卡
                            myelement.tabChange('pagetabs', url);
                            myelement.on("tab()");
                        }
                    }


                }
            }
        });
    });


    function reflush() {
        console.log("flush flush")
        layui.use(['element', 'layer'], function () {
            var myelement = layui.element
                , layer = layui.layer
                , $ = layui.jquery;
            myelement.render();
        });

    }

    function closetab(dom) {
        // console.log("closetab flush")
        // console.log($(dom).attr("lay-id"))
        layui.use(['element', 'layer'], function () {
            var myelement = layui.element
                , layer = layui.layer
                , $ = layui.jquery;
            myelement.tabDelete("pagetabs", $(dom).attr("lay-id"));
            myelement.render();
        });

    }




    function loadbody() {
        console.log("loadbody" + $(window).height() + "px" + $(document).height() + "px" + $(document.body).height() + "px")
        // $(".layui-layout-body").css("height", $(window).height()+"px")
    }
</script>

<%--<button id="bt_flush_nav" onclick="reflush()" style="visibility: hidden;width: 0px;height: 0px;z-index: -99;">;</button>--%>
</body>
</html>
