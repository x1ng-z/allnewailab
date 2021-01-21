var api = {

    loadproject:function(projectid){
        let projectresult;
        $.ajax({
            url: "/projectedit/findproject" + "?" + Math.random(),
            async: false,
            data: {
                "projectid":projectid
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("加载失败！");
                    // return [];
                } else {
                    layer.msg("加载成功！");
                    console.log( json['project'])
                    projectresult= json['project']
                    //layer.msg("加载成功！");
                    // location.href = '${pageContext.request.contextPath}/' + json['go'];
                    //newleft(json['modleName'],json['modleId'])
                    // parent.location.reload();
                }
                //window.location.href("result")
                // var json = JSON.parse(result);
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
            }
        });

        return projectresult;

    },
    createmodle:function (position,modletype,projectid) {
        let createmodleinfo;
        console.log(JSON.stringify({'position':position,'modletype':modletype,'projectid':projectid})+'');
        $.ajax({
            url: "/projectedit/creatmodle" + "?" + Math.random(),
            async: false,
            data: {
                "modleinfo":JSON.stringify({'position':position,'modletype':modletype,'projectid':projectid})+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("加载失败！");
                    // return [];
                } else {
                    layer.msg("加载成功！");
                    // console.log( json['project'])
                    // projectresult= json['project']
                    createmodleinfo=json;
                    //layer.msg("加载成功！");
                    // location.href = '${pageContext.request.contextPath}/' + json['go'];
                    //newleft(json['modleName'],json['modleId'])
                    // parent.location.reload();
                }
                //window.location.href("result")
                // var json = JSON.parse(result);
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
            }
        });
        return createmodleinfo;

    },
    deletemodle:function (modleid,projectid,parent) {
        let deletemodleinfo;
        //console.log(JSON.stringify({'position':position,'modletype':modletype,'projectid':projectid})+'');
        $.ajax({
            url: "/projectedit/deletemodle" + "?" + Math.random(),
            async: false,
            data: {
                "modleinfo":JSON.stringify({'modleid':modleid,'projectid':projectid,'parent':parent})+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("模型删除失败！");
                    deletemodleinfo=false;
                    // return [];
                } else {
                    layer.msg("模型删除成功！");
                    // console.log( json['project'])
                    // projectresult= json['project']
                    deletemodleinfo=true;
                    //layer.msg("加载成功！");
                    // location.href = '${pageContext.request.contextPath}/' + json['go'];
                    //newleft(json['modleName'],json['modleId'])
                    // parent.location.reload();
                }
                //window.location.href("result")
                // var json = JSON.parse(result);
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });
        return deletemodleinfo;

    },
    createmodlechild:function (modleid,childid) {
        let createmodleinfo=false;
        console.log(JSON.stringify({'modleid':modleid,'childid':childid})+'');
        $.ajax({
            url: "/projectedit/createmodlechild" + "?" + Math.random(),
            async: false,
            data: {
                "modlechildinfo":JSON.stringify({'modleid':modleid,'childid':childid})+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("创建失败！");
                    createmodleinfo=false;
                    // return [];
                } else {
                    layer.msg("创建成功！");
                    createmodleinfo=true;
                }
                //window.location.href("result")
                // var json = JSON.parse(result);
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
                createmodleinfo=false;
            }
        });
        return createmodleinfo;

    },
    deletemodlechild:function (modleid,childid) {
        let deletemodleinfo;
        //console.log(JSON.stringify({'position':position,'modletype':modletype,'projectid':projectid})+'');
        $.ajax({
            url: "http://127.0.0.1/projectedit/deletemodlechild" + "?" + Math.random(),
            async: false,
            data: {
                "modlechildinfo":JSON.stringify({'modleid':modleid,'childid':childid})+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("删除失败！");
                    deletemodleinfo=false;
                    // return [];
                } else {
                    layer.msg("删除成功！");
                    // console.log( json['project'])
                    // projectresult= json['project']
                    deletemodleinfo=true;
                    //layer.msg("加载成功！");
                    // location.href = '${pageContext.request.contextPath}/' + json['go'];
                    //newleft(json['modleName'],json['modleId'])
                    // parent.location.reload();
                }
                //window.location.href("result")
                // var json = JSON.parse(result);
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });
        return deletemodleinfo;

    },
    movemodle:function (modleid,poistion) {
        let deletemodleinfo;
        console.log(JSON.stringify({'modleid':modleid,'poistion':poistion})+'');
        $.ajax({
            url: "/projectedit/movemodle" + "?" + Math.random(),
            async: false,
            data: {
                "modleifno":JSON.stringify({'modleid':modleid,'position':poistion})+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    // layer.msg("移动失败！");
                    deletemodleinfo=false;
                } else {
                    // layer.msg("移动成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });
        return deletemodleinfo;

    },


    /****/
    updatemodle:function (url,modleinfo,layer) {

        let deletemodleinfo;
        console.log(JSON.stringify(modleinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modleinfo":JSON.stringify(modleinfo)+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("修改失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("修改成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });
        return deletemodleinfo;

    },
    getmodleproperties:function (url,modleinfo,layer) {

        let deletemodleinfo;
        console.log(JSON.stringify(modleinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modleinfo":JSON.stringify(modleinfo)+''
            },
            type: "POST",
            success: function (result) {
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("修改失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("修改成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });
        return deletemodleinfo;

    },
    addmodleproperties:function (url,modlepropertyinfo,layer) {
        let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        console.log(JSON.stringify(modlepropertyinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modlepropertyinfo":JSON.stringify(modlepropertyinfo)+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("添加失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("添加成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },
    addmodlerespon:function (url,responcontext,layer) {
        let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        // console.log(JSON.stringify(modlepropertyinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "responcontext":JSON.stringify(responcontext)+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("添加失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("添加成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },
    deletmodleproperties:function (url,modlepropertyid,layer) {
        let index = layer.msg('删除，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modlepropertyid":modlepropertyid+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("删除失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("删除成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },
    deletmodlerespon:function (url,modleresponId,layer) {
        let index = layer.msg('删除，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modleresponId":modleresponId+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("删除失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("删除成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },
    updatemodleproperties:function (url,modlepropertyinfo,layer) {
        let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        console.log(JSON.stringify(modlepropertyinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modlepropertyinfo":JSON.stringify(modlepropertyinfo)+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("添加失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("添加成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },
    updatemodlerespon:function (url,modlepropertyinfo,layer) {
        let index = layer.msg('修改中，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        console.log(JSON.stringify(modlepropertyinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modlepropertyinfo":JSON.stringify(modlepropertyinfo)+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("添加失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("添加成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },
    addmpcmodleproperties:function (url,modlepropertyinfo,layer) {
        let index = layer.msg('创建中，请稍候', {icon: 16, time: false, shade: 0.8});
        let deletemodleinfo;
        console.log(JSON.stringify(modlepropertyinfo)+'');
        $.ajax({
            url: url + "?" + Math.random(),
            async: false,
            data: {
                "modlepropertyinfo":JSON.stringify(modlepropertyinfo)+''
            },
            type: "POST",
            success: function (result) {
                layer.close(index);
                console.log(result);
                // layer.close(index);
                let json = JSON.parse(result);
                if (json['msg'] == "error") {
                    layer.msg("添加失败！");
                    deletemodleinfo=false;
                } else {
                    layer.msg("添加成功！");
                    deletemodleinfo=true;
                }
            },
            error:function (result) {
                layer.close(index);
                layer.msg("连接服务器异常！");
                deletemodleinfo=false
            }
        });

        return deletemodleinfo;

    },


}

