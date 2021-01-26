var layermanager={
    vieweditproject:function(url,projectid,layer){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '编辑工程'
                // ,title: false //不显示标题栏
                // ,id:'idLAY_layuipro'
                // ,closeBtn: false
                ,maxmin:true
                , area: ['750px', '550px'] //['300px', '260px']
                , content: url+'?projectid='+projectid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();

                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#motifymodlesubmit").trigger('click');
                    // layer.close(index);
                    // $("#bt_flush_pvtab", dom).trigger('click');
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    // layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );

    },


    viewmodledetail:function(url,projectid,modleId,layer){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '运行详情'
                // ,title: false //不显示标题栏
                // ,id:'idLAY_layuipro'
                // ,closeBtn: false
                ,maxmin:true
                , area: ['750px', '550px'] //['300px', '260px']
                , content: url+'?modleId='+modleId+'&projectid='+projectid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();

                    // var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    // body.find("#motifymodlesubmit").trigger('click');
                    layer.close(index);
                    // $("#bt_flush_pvtab", dom).trigger('click');
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    // layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );

    },

    vieweditmolde:function(url,modleId,layer){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '编辑模型'
                // ,title: false //不显示标题栏
                // ,id:'idLAY_layuipro'
                // ,closeBtn: false
                ,maxmin:true
                , area: ['750px', '550px'] //['300px', '260px']
                , content: url+'?modleId='+modleId
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();

                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#motifymodlesubmit").trigger('click');
                    // layer.close(index);
                    // $("#bt_flush_pvtab", dom).trigger('click');
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    // layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );

    },


    viewaddinputproperty:function(url, modletype, modleId, pindir,layer, dom){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '添加输入设置'
                , area: ['600px', '500px'] //['300px', '260px']
                , content: url+'?modletype='+modletype+'&modleId='+modleId+'&pindir='+pindir//http://127.0.0.1//modifyaimodleproperty.do?modleid=' + modleid + '&propertyid=' + propertyid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    var body = layer.getChildFrame('body', index);
                    console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                    $("#bt_flush_pvtab", dom).trigger('click');
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );
    },

    viewaddmpcmodleproperty:function(url, pintype, modleId,layer, dom){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '添加输入设置'
                , area: ['600px', '500px'] //['300px', '260px']
                , content: url+'?pintype='+pintype+'&modleId='+modleId//http://127.0.0.1//modifyaimodleproperty.do?modleid=' + modleid + '&propertyid=' + propertyid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                    $("#bt_flush_pvtab", dom).trigger('click');
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );
    },
    viewaddmpcmodlerespon:function(url, modleId,layer, dom){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '添加响应设置'
                , area: ['600px', '500px'] //['300px', '260px']
                , content: url+'?modleId='+modleId//http://127.0.0.1//modifyaimodleproperty.do?modleid=' + modleid + '&propertyid=' + propertyid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#newresponsubmitbt").trigger('click');
                    $("#bt_flush_pvtab", dom).trigger('click');
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );
    },

    viewupdateinputproperty:function(url, modletype, propertyid, layer, dom){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '更新输出设置'
                , area: ['600px', '500px'] //['300px', '260px']
                , content: url+'?modletype='+modletype+'&modlepinsId='+propertyid+''
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                    $("#bt_flush_pvtab", dom).trigger('click');
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero,index) {
                    console.log('layero',layero);
                    console.log('index',index);
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );

        return indexlayer;
    },

    viewupdatempcmodleproperty:function(url, pintype, modlepinsId,layer, dom){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '修改设置'
                , area: ['600px', '500px'] //['300px', '260px']
                , content: url+'?pintype='+pintype+'&modlepinsId='+modlepinsId//http://127.0.0.1//modifyaimodleproperty.do?modleid=' + modleid + '&propertyid=' + propertyid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#newcontrlmodlepinsubmitbt").trigger('click');
                    $("#bt_flush_pvtab", dom).trigger('click');
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );
    },

    viewupdatempcmodlerespon:function(url, modleresponId,layer, dom){
        let indexlayer = layer.open(
            {
                type: 2
                , title: '修改设置'
                , area: ['600px', '500px'] //['300px', '260px']
                , content: url+'?modleresponId='+modleresponId//http://127.0.0.1//modifyaimodleproperty.do?modleid=' + modleid + '&propertyid=' + propertyid
                //[dom.attr("lay-href"),'no']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                // , id: 'modifymodlewpropindows'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    var body = layer.getChildFrame('body', index);
                    // console.log('newcontrlmodlepinsubmitbt',body.find("#newcontrlmodlepinsubmitbt"));
                    body.find("#newresponsubmitbt").trigger('click');
                    $("#bt_flush_pvtab", dom).trigger('click');
                    // var iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newcontrlmodlepinsubmit").click();
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );
    },



    createmodleview: function (position) {
        let indexlayer = layer.open(
            {
                type: 2
                , title: '创建模块'
                // ,title: false //不显示标题栏
                // ,id:'idLAY_layuipro'
                // ,closeBtn: false
                , area: ['950px', '500px']
                , content: ['', 'yes']//不要滚动条
                , zIndex: layer.zIndex //重点1
                , shade: 0.3
                , id: 'LAY_layuipro'
                , btn: ['确定', '取消']
                , yes: function (index, layero) {
                    //按钮【按钮一】的回调
                    // layer.getChildFrame()
                    let iframeWin = window[layero.find('iframe')[0]['name']];
                    // iframeWin.document.getElementById("newmodlesubmitbt").click();
                    $("#newmodlesubmitbt", iframeWin.document).trigger('click')
                }
                , btn2: function (index, layero) {
                    //按钮【按钮二】的回调 do nothing
                    //return false 开启该代码可禁止点击该按钮关闭
                }
                , btnAlign: 'c'
                , success: function (layero) {
                    layer.setTop(layero); //重点2
                }

                // content: 'http://sentsin.com' //这里content是一个URL，如果你不想让iframe出现滚动条，你还可以content: ['http://sentsin.com', 'no']
            }
        );

    },

}