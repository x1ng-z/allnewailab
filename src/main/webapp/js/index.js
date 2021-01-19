/* global $, visoConfig, Mustache, uuid, jsPlumb, graphlib */


var root = {}

window.IVR = root

root.emit = function (event) {
    console.log(event)
}


var fixedNodeId = {
    begin: 'begin-node',
    end: 'end-node'
}


// $(document).ready(function(){
//   $("p").click(function(){
//     $(this).hide();
//   });
// });


// 放入拖动节点
function dropNode(template, position, projectid) {

    position.left -= $('#side-buttons').outerWidth();
    //position.id = uuid.v1()
    ///position.generateId = uuid.v1
    let result = api.createmodle(position, template, projectid);
    console.log("****", result)
    // console.log(position)
    //console.log("****")
    let data = {
        'projectid': projectid + "",
        'modules': [
            {
                'id': result['modleid'],
                'type': template,
                'name': result['modlename'],
                'status': '1',
                'child': [],
                'inputproperty': [],
                'outputproperty': [],
                'top': position.top,
                'left': position.left
            }
        ]
    }
    // let modleview = templefactory.temple(data);//renderHtml(template, position)

    DataDraw.draw(data);
    // $(areaId).append(modleview);
    element.render();
    console.log("****start", data);
    //
    // jsplumbinstance.batch(function () {
    //     // DataDraw.draw(modledata.testproject)
    //     // addDraggable(data.id);
    //     DataDraw.addEndpointOfDefault(data);
    //     console.log("****in",data);
    // });
    // console.log("****end",data);


    // DataDraw.addEndpointOfDefault(data);
    // DataDraw.connectEndpointOfDefault(data);

    //initSetNode(data);//设置节点
}

// 初始化节点设置 temple 是模板名称
function initSetNode(node) {
    console.log(node);
    addDraggable(node.id);//设置拖动

    // if (template === 'tpl-audio') {
    //     setEnterPoint(id);//设置输入点
    //     setExitPoint(id);//设置输出点
    // } else if (template === 'tpl-menu') {
    //     setEnterPoint(id + '-heading')
    //     setExitMenuItem(id)
    // }

    if (node.type == 'input') {
        console.log("*******node")
        console.log(node)

        // $( "#1" ).accordion({
        //     collapsible: true
        // });
        setExitPoint(node.id)
    } else if (node.type == 'output') {
        setEnterPoint(node.id);
    } else {
        console.log("*******node")
        console.log(node)
        setExitPoint(node.id);
        setEnterPoint(node.id);
    }

}

// 设置入口点
function setEnterPoint(id) {
    var config = getBaseNodeConfig()

    config.isSource = false
    config.maxConnections = -1

    jsplumbinstance.addEndpoint(id, {
        anchors: 'Top',
        uuid: id + '-in'
    }, config)
}

// 设置出口点
function setExitPoint(id, position) {
    var config = getBaseNodeConfig()

    config.isTarget = false
    config.maxConnections = 1

    jsplumbinstance.addEndpoint(id, {
        anchors: position || 'Bottom',
        uuid: id + '-out'
    }, config)
}

function setExitPoint(id) {
    var config = getBaseNodeConfig()

    config.isTarget = false
    config.maxConnections = -1

    jsplumbinstance.addEndpoint(id, config, {
        anchors: 'Bottom',
        uuid: id + '-out'
    })
}

function setExitMenuItem(id) {
    $('#' + id).find('li').each(function (key, value) {
        setExitPoint(value.id, 'Right')
    })
}

// 删除一个节点以及
function emptyNode(modleid, projectid) {
    let parent = [];
    let selectmodle = jsplumbinstance.getConnections({'target': modleid + ''});
    for (let index = 0; index < selectmodle.length; index++) {
        parent.push(selectmodle[index].sourceId);
    }
    console.log('parent', parent);
    let deletemodleinfo = api.deletemodle(modleid, projectid, parent);
    if (deletemodleinfo) {
        jsplumbinstance.remove(modleid);
    }
}

// 让元素可拖动
function addDraggable(id) {
    jsplumbinstance.draggable(id, {
        containment: 'parent',//限制拖动范围,
        start(params) {
            //
            console.log("start move");
            console.log(params);

        },
        stop(params) {
            // model.changeNodePos(newNode.id, {
            console.log("end move");
            console.log(params);
            console.log($(params['el']).attr("id"), {'left': params['pos'][0], 'top': params['pos'][1]})
            api.movemodle($(params['el']).attr("id"), {'left': params['pos'][0], 'top': params['pos'][1]});
            // console.log(jsplumbinstance.getConnections({'source':id}));
            // left: params.pos[0],
            // top: params.pos[1],
            // });
        }
    })
}

// 渲染html
function renderHtml(type, position) {
    return Mustache.render($('#' + type).html(), position)
}

function eventHandler(data) {
    contextMenu.hide();
    // if (data.type === 'deleteNode') {
    //     emptyNode(data.id,data.projectid)
    // }
}

// 主要入口
var gobalscal = 1;

function main() {
    // var instance = jsPlumb.getInstance({Container:'"drop-bg'});
    // jsPlumb.setContainer('mainContainer');
    jsplumbinstance = window.j = jsPlumb.getInstance({Container: mainContainer, allowLoopback: false,});
    //
    //
    // // let elem = document.getElementById('drop-bg')
    // const mainContainer = jsPlumb.getContainer();
    //
    // console.log(mainContainer.parentNode)
    // let mainContainerWrap = mainContainer.parentNode;//document.getElementById('flowChartWrap');//mainContainer.parentNode;
    // let pan = Panzoom(mainContainer, {
    //   smoothScroll: false,
    //   bounds: true,
    //   // autocenter: true,
    //   zoomDoubleClickSpeed: 1,
    //   minZoom: 0.5,
    //   maxZoom: 2,
    //   // canvas:true,
    //   excludeClass: 'pa'
    // });
    // // instance.mainContainerWrap = mainContainerWrap;
    // // instance.pan = pan;
    // // 缩放时设置jsPlumb的缩放比率
    // console.log(pan);
    // // pan.on('zoom', (e) => {
    // //     const { scale } = e.getTransform();
    // //   jsPlumb.setZoom(scale);
    // // });
    // //
    // // $.fn.test = function(opts,callback){
    // //   if (callback){
    // //     callback();
    // //   }
    // // }
    //
    // mainContainerWrap.parentElement.addEventListener('wheel', function (event) {
    //   console.log("wheel");
    //   console.log(event)
    //   delta = event.wheelDelta/180;
    //   console.log("delta",delta)
    //
    //   // jsPlumb.setZoom(gobalscal,true);
    //   // if (!event.shiftKey) return
    //   // Panzoom will automatically use `deltaX` here instead
    //   // of `deltaY`. On a mac, the shift modifier usually
    //   // translates to horizontal scrolling, but Panzoom assumes
    //   // the desired behavior is zooming.
    //   //const { scale } = event.getTransform();
    //   //instance.setZoom(scale);
    //     pan.zoomWithWheel(event)
    //   jsPlumb.setZoom(pan.getScale());
    //
    //
    // })
    //
    // mainContainerWrap.addEventListener('panzoomchange', (event) => {
    //   console.log("panzoomzoom");
    //   console.log(event.detail) // => { x: 0, y: 0, scale: 1 }
    //   // $("#drop-bg").css({
    //   //   "-webkit-transform":"scale(0.75)",
    //   //   "-moz-transform":"scale(0.75)",
    //   //   "-ms-transform":"scale(0.75)",
    //   //   "-o-transform":"scale(0.75)",
    //   //   "transform":"scale(0.75)"
    //   // });
    //   // jsPlumb.setZoom(scale);
    //   jsPlumb.setZoom(event.detail['scale'],true);
    // })
    //
    // // 平移时设置鼠标样式
    // mainContainerWrap.style.cursor = 'grab';
    // mainContainerWrap.addEventListener('mousedown', function wrapMousedown() {
    //   this.style.cursor = 'grabbing';
    //   mainContainerWrap.addEventListener('mouseout', function wrapMouseout() {
    //     this.style.cursor = 'grab';
    //   });
    // });
    // mainContainerWrap.addEventListener('mouseup', function wrapMouseup() {
    //   this.style.cursor = 'grab';
    // });

    //jueryui 设置可以


    // 双击连接线上删除
    jsplumbinstance.bind('dblclick', function (conn, originalEvent) {
        DataDraw.deleteLine(conn)
    })

    // 当链接建立
    jsplumbinstance.bind('beforeDrop', function (info) {
        console.log('beforeDrop', info);
        return connectionBeforeDropCheck(info)
    });

    //$('.layui-container').append(contextMenu.init());
    contextMenu.init2($('.layui-container'));
    element.render();
    jsplumbinstance.bind('contextmenu', (component, originalEvent) => {
        console.log('jsplumbinstance contextmenu')

        originalEvent.preventDefault();
        originalEvent.stopPropagation();
        // if (isEndpoint(component)) return;
        return;
    });

    $(areaId).contextmenu((ev) => {
        ev.preventDefault();
        console.log("mainev", ev, $("#side-buttons").width(), ev.clientX - $("#side-buttons").width());
        contextMenu.show({
            left: ev.pageX - $("#side-buttons").width(),
            top: ev.pageY,
        }, ev['currentTarget']['dataset']['id'], ev['currentTarget']['dataset']['projectid']);
    });

    // 让退出节点可拖动
    // addDraggable(fixedNodeId.end)
    // initBeginNode()
    // initEndNode()

    // DataProcess.inputData(data.nodeList)
    //根据json进行绘图
    jsplumbinstance.batch(function () {
        // DataDraw.draw(modledata.testproject)
        let projectconect = api.loadproject($('.layui-container').attr('projectid'));
        console.log(projectconect);
        DataDraw.draw(projectconect)
    });
    jsPlumb.fire("jsPlumbDemoLoaded", jsplumbinstance);
}

// 链接建立后的检查
// 当出现自连接的情况后，要将链接断开
function connectionBeforeDropCheck(info) {
    if (info.connection.targetId == info.connection.sourceId) {
        return false;
    }
    return api.createmodlechild(info.connection.sourceId, info.connection.targetId);
}

// 获取基本配置
function getBaseNodeConfig() {

    var connectorPaintStyle = {
            strokeWidth: 2,
            stroke: "#61B7CF",
            joinstyle: "round",
            outlineStroke: "white",
            outlineWidth: 2
        },
        // .. and this is the hover style.
        connectorHoverStyle = {
            strokeWidth: 3,
            stroke: "#216477",
            outlineWidth: 5,
            outlineStroke: "white"
        },
        endpointHoverStyle = {
            fill: "#216477",
            stroke: "#216477"
        },
        sourceEndpoint = {
            endpoint: "Rectangle",
            paintStyle: {
                stroke: "#7AB02C",
                fill: "transparent",
                width: 10,
                height: 10,
                strokeWidth: 1
            },
            isSource: true,
            isTarget: true,
            maxConnections: -1,
            connector: ["Flowchart", {stub: [10, 20], gap: 10, cornerRadius: 5, alwaysRespectStubs: true}],
            connectorStyle: connectorPaintStyle,
            hoverPaintStyle: endpointHoverStyle,
            connectorHoverStyle: connectorHoverStyle,
            dragOptions: {},
            allowLoopback: false,
            // overlays: [
            //   [ "Label", {
            //     location: [0.5, 1.5],
            //     label: "Drag",
            //     cssClass: "endpointSourceLabel",
            //     visible:true
            //   } ]
            // ],
            connectorOverlays: [
                ['Arrow', {
                    width: 10,
                    length: 10,
                    location: 1
                }],
                // ['Arrow', {
                //     width: 10,
                //     length: 10,
                //     location: 0.2
                // }],
                // ['Arrow', {
                //     width: 10,
                //     length: 10,
                //     location: 0.7
                // }],
                ['Label', {
                    label: '',
                    cssClass: '',
                    labelStyle: {
                        color: 'red'
                    },
                    events: {
                        click: function (labelOverlay, originalEvent) {
                            console.log('click on label overlay for :' + labelOverlay.component)
                            console.log(labelOverlay)
                            console.log(originalEvent)
                        }
                    }
                }]
            ]
        }


    return sourceEndpoint;//Object.assign({}, visoConfig.baseStyle)
}

// 初始化开始节点属性
function initBeginNode(id) {
    var config = getBaseNodeConfig()

    config.isTarget = false
    config.maxConnections = 1

    jsPlumb.addEndpoint(id, {
        anchors: 'Bottom',
        uuid: id + '-out'
    }, config)
}

//初始化结束节点属性
function initEndNode(id) {
    var config = getBaseNodeConfig()

    config.isSource = false

    jsPlumb.addEndpoint(id, {
        anchors: 'Top',
        uuid: id + '-in'
    }, config)
}

//数据处理
var DataProcess = {
    inputData: function (nodes) {
        var ids = this.getNodeIds(nodes);//获取所有id
        var g = new graphlib.Graph();

        ids.forEach(function (id) {
            g.setNode(id)//设置图的节点
        });

        var me = this;

        nodes.forEach(function (item) {
            if (me['dealNode' + item.type]) {
                me['dealNode' + item.type](g, item)
            } else if (me['dealNodeDefault']) {
                if (item['child']) {
                    me['dealNodeDefault'](g, item)
                }
                //console.error('have no deal node of ' + item.type)
            } else {
                console.error('have no deal node of ' + item.type)
            }
        });

        console.log(g.nodes())
        var distance = graphlib.alg.dijkstra(g, 1)//(g, 'Start')

        return this.generateDepth(distance)//每个深度上的key(其实就是node的id)
    },
    setNodesPosition: function (nodes) {
        var me = this
        nodes.forEach(function (item) {
            me.getNodePosition(item)
        })
    },
    getNodePosition: function (node) {
        var $node = document.getElementById(node.id)
        node.top = parseInt($node.style.top)
        node.left = parseInt($node.style.left)
    },
    generateDepth: function (deep) {
        var depth = []

        Object.keys(deep).forEach(function (key) {
            var distance = deep[key].distance

            if (!depth[distance]) {
                depth[distance] = []
            }

            depth[distance].push(key)
        })

        return depth
    },
    getNodeIds: function (nodes) {
        return nodes.map(function (item) {
            return item.id
        })
    },
    dealNodeDefault: function (g, node) {
        let $this = this;
        console.log(node.child);
        node.child.forEach(function (item) {
            $this.setEdge(g, node.id, item.id);
        });
    },
    dealNodeRoot: function (g, node) {
        this.setEdge(g, node.id, node.data.nextNode)
    },
    dealNodeAnnounce: function (g, node) {
        this.setEdge(g, node.id, node.data.nextNode)
    },
    dealNodeExit: function (g, node) {

    },
    dealNodeWorkTime: function (g, node) {
        this.setEdge(g, node.id, node.data.onWorkNode)
        this.setEdge(g, node.id, node.data.offWorkNode)
    },
    dealNodeMenu: function (g, node) {
        this.setEdge(g, node.id, node.data.nextNode)
    },
    setEdge: function name(g, from, to) {
        console.log(from + ' ---> ' + to)
        g.setEdge(from, to)
    }
}

//数据图像绘制操作
var DataDraw = {
    deleteLine: function (conn) {
        if (confirm('确定删除所点击的关系吗？')) {
            console.log("conn", conn);
            if (api.deletemodlechild(conn.sourceId, conn.targetId)) {
                jsplumbinstance.deleteConnection(conn);
            }

        }
    },
    draw: function (project) {
        // 将Exit节点排到最后
        console.log(project);
        let modules = project.modules;
        console.log("nodes", modules);
        // nodes.sort(function (a, b) {
        //     if (a.type === 'output') return 1
        //     if (b.type === 'output') return -1
        //     return 0
        // });

        this.computeXY(modules);

        // var template = $('#tpl-demo').html()
        var $container = $(areaId)
        var me = this

        modules.forEach(function (module, key) {
            // console.log(module)
            // console.log(typeof key)

            var data = {
                projectid: project.projectid,
                moduleid: module.id,
                name: module.name,
                type: module.type,
                top: module.top,
                left: module.left,
                inputproperty: module.inputproperty,
                outputproperty: module.outputproperty
                //choices: item.data.choices || []
            };

            // console.log("data",data);
            // var template = me.getTemplate(item)//mf by zzx
            //
            //
            // $container.append(Mustache.render(template, data));//mf by zzx


            $container.append(templefactory.temple(data));
            element.render();
            // if (me['addEndpointOf' + item.type]) {
            //     me['addEndpointOf' + item.type](item)
            // }

            if (me['addEndpointOfDefault']) {
                me['addEndpointOfDefault'](module)
            }

        });

        this.mainConnect(modules);
    },
    connectEndpoint: function (from, to) {
        jsplumbinstance.connect({uuids: [from, to]})
    },
    mainConnect: function (nodes) {
        var me = this
        nodes.forEach(function (item) {
            if (me['connectEndpointOfDefault']) {
                me['connectEndpointOfDefault'](item)
            }
        })
    },
    getTemplate: function (node) {
        return $('#tpl-' + node.type).html() || $('#tpl-demo').html()
    },
    computeXY: function (nodes) {
        var matrix = DataProcess.inputData(nodes)

        var base = {
            topBase: 50,
            topStep: 150,
            leftBase: 150,
            leftStep: 200
        }

        nodes.forEach(function (dest) {
            dest.top = dest.top || base.topBase;//+ i * base.topStep;
            dest.left = dest.left || base.leftBase; //+ j * base.leftStep;
        })

        return;

        //后面不需要计算了
        for (var i = 0; i < matrix.length; i++) {
            for (var j = 0; j < matrix[i].length; j++) {
                var key = matrix[i][j]

                var dest = nodes.find(function (item) {
                    return item.id === key
                })

                dest.top = dest.top || base.topBase + i * base.topStep
                dest.left = dest.left || base.leftBase + j * base.leftStep
            }
        }
    },


    //添加数据点号
    addEndpointOfDefault: function (node) {
        addDraggable(node.id);
        if (node.type == 'input') {
            console.log("*******node")
            console.log(node)

            // $( "#1" ).accordion({
            //     collapsible: true
            // });
            setExitPoint(node.id)
        } else if (node.type == 'output') {
            setEnterPoint(node.id);
        } else {
            console.log("*******node")
            console.log(node)
            setExitPoint(node.id);
            setEnterPoint(node.id);
        }

        layui.use(['element', 'layer'], function () {

            element.on('collapse(' + node.id + ')', function (data) {
                // layer.msg('展开状态：'+ data.show);
                jsplumbinstance.repaintEverything(true);
            });
        });
    },

    addEndpointOfRoot: function (node) {
        addDraggable(node.id)
        initBeginNode(node.id)
    },
    connectEndpointOfRoot: function (node) {
        this.connectEndpoint(node.id + '-out', node.data.nextNode + '-in')
    },
    addEndpointOfExit: function (node) {
        addDraggable(node.id)
        initEndNode(node.id)
    },
    addEndpointOfAnnounce: function (node) {
        addDraggable(node.id)
        setEnterPoint(node.id)
        setExitPoint(node.id)
    },

    connectEndpointOfDefault: function (node) {
        let me = this;

        node.child.forEach(function (e) {
            me.connectEndpoint(node.id + '-out', e.id + '-in')
        });


    }

    ,

    connectEndpointOfAnnounce: function (node) {
        this.connectEndpoint(node.id + '-out', node.data.nextNode + '-in')
    },
    addEndpointOfWorkTime: function (node) {
        addDraggable(node.id)
        setEnterPoint(node.id)

        var ids = ['onWorkTime', 'offWorkTime']

        ids.forEach(function (key) {
            setExitPoint(node.id + '-' + key, 'Right')
        })
    },
    connectEndpointOfWorkTime: function (node) {
        this.connectEndpoint(node.id + '-onWorkTime-out', node.data.onWorkNode + '-in')
        this.connectEndpoint(node.id + '-offWorkTime-out', node.data.offWorkNode + '-in')
    },
    addEndpointOfMenu: function (node) {
        addDraggable(node.id)
        setEnterPoint(node.id)

        var ids = ['noinput', 'nomatch']

        node.data.choices.forEach(function (item) {
            ids.push('key-' + item.key)
        })

        ids.forEach(function (key) {
            setExitPoint(node.id + '-' + key, 'Right')
        })
    },
    connectEndpointOfMenu: function (node) {
        this.connectEndpoint(node.id + '-noinput-out', node.data.noinput.nextNode + '-in')
        this.connectEndpoint(node.id + '-nomatch-out', node.data.nomatch.nextNode + '-in')

        var me = this

        node.data.choices.forEach(function (item) {
            me.connectEndpoint(node.id + '-key-' + item.key + '-out', item.nextNode + '-in')
        })
    }
}

root.DataProcess = DataProcess
root.DataDraw = DataDraw
