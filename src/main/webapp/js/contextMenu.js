var contextMenu = {

    //用于控制右键菜单
    currentshowmenumodleid: undefined,
    currentshowmenuprojectid: undefined,
    //右键位置
    clicktop: undefined,
    clickleft: undefined,
    //菜单弹出的jq对象选项
    menu_modle_li_edit: undefined,
    menu_modle_li_delete: undefined,
    menu_block_li_newmodleulli_input: undefined,
    menu_block_li_newmodleulli_output: undefined,
    menu_block_li_newmodleulli_filter: undefined,
    menu_block_li_newmodleulli_customize: undefined,
    menu_block_li_newmodleulli_mpc: undefined,
    menu_block_li_newmodleulli_pid: undefined,
    menu_block_li_projectparamsedit:undefined,
    menu_block_li_projectrun:undefined,
    menu_block_li_projectstop:undefined,
    menu_modle_li_detail:undefined,
    init2: function (container) {
        /**初始化modle选项*/
        let menu_modle = $('<div class="imenu"></div>');
        menu_modle.attr("id", 'modle-right-menu-id');
        menu_modle.on('contextmenu', (ev) => {
            ev.preventDefault();
            return false;
        });
        menu_modle.css("display", "none");
        container.append(menu_modle);


        let meny_modle_ul = $('<ul class="imenu-list"></ul>');
        menu_modle.append(meny_modle_ul);

        this.menu_modle_li_edit = $('<li class="imenu-item"><button class="imenu-button">编辑</button></li>');
        meny_modle_ul.append(this.menu_modle_li_edit);
        let me=this;
        this.menu_modle_li_edit.bind("click", function () {
            layermanager.vieweditmolde('/projectedit/vieweditmodle',me.menu_modle_li_edit.attr("modleid"),parent.layer);
            // alert("编辑" + this.currentshowmenumodleid + "--" + this.currentshowmenuprojectid);
        });

        this.menu_modle_li_delete = $('<li class="imenu-item"><button class="imenu-button">删除</button></li>');
        meny_modle_ul.append(this.menu_modle_li_delete);
        //let me = this;
        this.menu_modle_li_delete.bind("click", function (e) {
            let parent=[];
            let selectmodle=jsplumbinstance.getConnections({'target':me.menu_modle_li_delete.attr("modleid")+''});
            for(let index=0;index<selectmodle.length;index++) {
                parent.push(selectmodle[index].sourceId);
            }
            console.log('parent',parent);
            let deletemodleinfo = api.deletemodle(me.menu_modle_li_delete.attr("modleid"), me.menu_modle_li_delete.attr("projectid"),parent);
            if (deletemodleinfo) {
                jsplumbinstance.remove(me.currentshowmenumodleid);
                me.currentshowmenumodleid = undefined;
            }
        });


        this.menu_modle_li_detail = $('<li class="imenu-item"><button class="imenu-button">运行详情</button></li>');
        meny_modle_ul.append(this.menu_modle_li_detail);
        //let me = this;
        this.menu_modle_li_detail.bind("click", function (e) {
            layermanager.viewmodledetail("/projectedit/viewempcmodlerundetail",me.menu_modle_li_detail.attr("modleid"),parent.layer);
        });



        /**初始化空白页选项*/

        let menu_block = $('<div class="imenu"></div>');
        menu_block.attr("id", 'block-right-menu-id');
        menu_block.css("display", "none");
        menu_block.on('contextmenu', (ev) => {
            ev.preventDefault();
            return false;
        });
        container.append(menu_block);

        let menu_block_ul = $('<ul class="imenu-list">  </ul>');
        menu_block.append(menu_block_ul);

        let menu_block_li_newmodle = $('<li class="imenu-item"></li>');
        menu_block_ul.append(menu_block_li_newmodle);

        let menu_block_li_newmodlebutton = $('<button class="imenu-button">新建</button>  ');
        menu_block_li_newmodle.append(menu_block_li_newmodlebutton);

        let menu_block_li_newmodleul = $('<ul class="imenu-sub-list">  </ul>');
        menu_block_li_newmodle.append(menu_block_li_newmodleul);

        this.menu_block_li_newmodleulli_input = $('<li class="imenu-item"><button class="imenu-button imenu-button--orange"><i data-feather="square"></i>输入模块</button></li>');
        menu_block_li_newmodleul.append(this.menu_block_li_newmodleulli_input);
        this.menu_block_li_newmodleulli_input.bind("click", function (e) {
            //console.log(e);
            let result = api.createmodle({top:me.clicktop,left:me.clickleft}, 'input', me.menu_block_li_newmodleulli_input.attr("projectid"));
            console.log("****", result)
            let data = {
                'projectid': me.menu_block_li_newmodleulli_input.attr("projectid") + "",
                'modules': [
                    {
                        'id': result['modleid'],
                        'type': 'input',
                        'name': result['modlename'],
                        'status': '1',
                        'child': [],
                        'inputproperty': [],
                        'outputproperty': [],
                        'top': me.clicktop,
                        'left':me.clickleft
                    }
                ]
            }
            DataDraw.draw(data);
            element.render();
        });

        this.menu_block_li_newmodleulli_output = $('<li class="imenu-item"><button class="imenu-button imenu-button--orange"><i data-feather="square"></i>输出模块</button></li>');
        menu_block_li_newmodleul.append(this.menu_block_li_newmodleulli_output);
        this.menu_block_li_newmodleulli_output.bind("click",function (e) {
            let result = api.createmodle({top:me.clicktop,left:me.clickleft}, 'output', me.menu_block_li_newmodleulli_input.attr("projectid"));
            console.log("****", result)
            let data = {
                'projectid': me.menu_block_li_newmodleulli_input.attr("projectid")  + "",
                'modules': [
                    {
                        'id': result['modleid'],
                        'type': 'output',
                        'name': result['modlename'],
                        'status': '1',
                        'child': [],
                        'inputproperty': [],
                        'outputproperty': [],
                        'top':me.clicktop,
                        'left': me.clickleft
                    }
                ]
            }
            DataDraw.draw(data);
            element.render();

        });

        this.menu_block_li_newmodleulli_filter = $('<li class="imenu-item"><button class="imenu-button imenu-button--orange"><i data-feather="square"></i>滤波模块</button></li>');
        menu_block_li_newmodleul.append(this.menu_block_li_newmodleulli_filter);
        this.menu_block_li_newmodleulli_filter.bind("click",function (e) {
            let result = api.createmodle({top:me.clicktop,left:me.clickleft}, 'filter', me.menu_block_li_newmodleulli_input.attr("projectid"));
            console.log("****", result)
            let data = {
                'projectid': me.menu_block_li_newmodleulli_input.attr("projectid")  + "",
                'modules': [
                    {
                        'id': result['modleid'],
                        'type': 'filter',
                        'name': result['modlename'],
                        'status': '1',
                        'child': [],
                        'inputproperty': [],
                        'outputproperty': [],
                        'top': me.clicktop,
                        'left': me.clickleft
                    }
                ]
            }
            DataDraw.draw(data);
            element.render();

        });

        this.menu_block_li_newmodleulli_customize = $('<li class="imenu-item"><button class="imenu-button imenu-button--orange"><i data-feather="square"></i>自定义模块</button></li>');
        menu_block_li_newmodleul.append(this.menu_block_li_newmodleulli_customize);
        this.menu_block_li_newmodleulli_customize.bind("click",function (e) {
            let result = api.createmodle({top:me.clicktop,left:me.clickleft}, 'customize', me.menu_block_li_newmodleulli_input.attr("projectid"));
            console.log("****", result)
            let data = {
                'projectid': me.menu_block_li_newmodleulli_input.attr("projectid")  + "",
                'modules': [
                    {
                        'id': result['modleid'],
                        'type': 'customize',
                        'name': result['modlename'],
                        'status': '1',
                        'child': [],
                        'inputproperty': [],
                        'outputproperty': [],
                        'top': me.clicktop,
                        'left': me.clickleft
                    }
                ]
            }
            DataDraw.draw(data);
            element.render();

        });

        this.menu_block_li_newmodleulli_mpc = $('<li class="imenu-item"><button class="imenu-button imenu-button--orange"><i data-feather="square"></i>MPC</button></li>');
        menu_block_li_newmodleul.append(this.menu_block_li_newmodleulli_mpc);
        this.menu_block_li_newmodleulli_mpc.bind("click",function (e) {
            let result = api.createmodle({top:me.clicktop,left:me.clickleft}, 'mpc', me.menu_block_li_newmodleulli_input.attr("projectid"));
            console.log("****", result)
            let data = {
                'projectid': me.menu_block_li_newmodleulli_input.attr("projectid")  + "",
                'modules': [
                    {
                        'id': result['modleid'],
                        'type': 'mpc',
                        'name': result['modlename'],
                        'status': '1',
                        'child': [],
                        'inputproperty': [],
                        'outputproperty': [],
                        'top': me.clicktop,
                        'left': me.clickleft
                    }
                ]
            }
            DataDraw.draw(data);
            element.render();

        });

        this.menu_block_li_newmodleulli_pid = $('<li class="imenu-item"><button class="imenu-button imenu-button--orange"><i data-feather="square"></i>PID</button></li>');
        menu_block_li_newmodleul.append(this.menu_block_li_newmodleulli_pid);
        this.menu_block_li_newmodleulli_pid.bind("click",function (e) {
            let result = api.createmodle({top:me.clicktop,left:me.clickleft}, 'pid', me.menu_block_li_newmodleulli_input.attr("projectid"));
            console.log("****", result)
            let data = {
                'projectid': me.menu_block_li_newmodleulli_input.attr("projectid")  + "",
                'modules': [
                    {
                        'id': result['modleid'],
                        'type': 'pid',
                        'name': result['modlename'],
                        'status': '1',
                        'child': [],
                        'inputproperty': [],
                        'outputproperty': [],
                        'top': me.clicktop,
                        'left': me.clickleft
                    }
                ]
            }

            DataDraw.draw(data);
            element.render();

        });

        this.menu_block_li_projectparamsedit= $('<li class="imenu-item"><button class="imenu-button"><i data-feather="link"></i>工程设置</button></li>');
        menu_block_ul.append(this.menu_block_li_projectparamsedit);
        this.menu_block_li_projectparamsedit.bind("click",function (e) {
            layermanager.vieweditproject('/projectedit/vieweditprojectparam',me.menu_block_li_projectparamsedit.attr("projectid"),parent.layer);

        });

        this.menu_block_li_projectrun= $('<li class="imenu-item"><button class="imenu-button"><i data-feather="link"></i>运行</button></li>');
        menu_block_ul.append(this.menu_block_li_projectrun);
        this.menu_block_li_projectrun.bind("click",function (e) {
            let result = api;
        });

        this.menu_block_li_projectstop= $('<li class="imenu-item"><button class="imenu-button"><i data-feather="link"></i>停止</button></li>');
        menu_block_ul.append(this.menu_block_li_projectstop);
        this.menu_block_li_projectstop.bind("click",function (e) {
            let result = api;

        });


        // menu_block.css("display", "none");


    },
    init: function () {
        let rightmenu = $(' <div class="right-menu"> ');
        rightmenu.attr("id", 'right-menu-id');

        rightmenu.css("display", "none");
        // rightmenu.css({"top": "200px", "left":"400px"});
        let opt = $('<ul class="right-menu-con"></ul>');
        rightmenu.append(opt);
        let optedit = $('<li>编辑</li>');
        opt.append(optedit);
        optedit.bind("click", function () {
            alert("这个段落被点击了。");
        });
        return rightmenu;
    },
    show: function (position, componentid, projectid,componettype) {
        // this.hide();

        if ((componentid == 'mainContainer')) {

            if((this.currentshowmenumodleid == undefined || this.currentshowmenumodleid == 'mainContainer')){
                console.log("mainContainer show", position, componentid, projectid);
                this.hide();
                this.currentshowmenumodleid = componentid;
                this.currentshowmenuprojectid = projectid;
                let blockrightmenu = $('#block-right-menu-id');
                blockrightmenu.css("left", position.left + 'px');
                blockrightmenu.css('top', position.top + 'px');
                blockrightmenu.css("display", 'block');
                this.menu_block_li_newmodleulli_input.attr("dmodleid", componentid);
                this.menu_block_li_newmodleulli_output.attr("modleid", componentid);
                this.menu_block_li_newmodleulli_filter.attr("modleid", componentid);
                this.menu_block_li_newmodleulli_customize.attr("modleid", componentid);
                this.menu_block_li_newmodleulli_mpc.attr("modleid", componentid);
                this.menu_block_li_newmodleulli_pid.attr("modleid", componentid);

                this.menu_block_li_newmodleulli_input.attr("projectid", projectid);
                this.menu_block_li_newmodleulli_output.attr("projectid", projectid);
                this.menu_block_li_newmodleulli_filter.attr("projectid", projectid);
                this.menu_block_li_newmodleulli_customize.attr("projectid", projectid);
                this.menu_block_li_newmodleulli_mpc.attr("projectid", projectid);
                this.menu_block_li_newmodleulli_pid.attr("projectid", projectid);
                this.menu_block_li_projectparamsedit.attr("projectid", projectid);
                this.menu_block_li_projectrun.attr("projectid", projectid);
                this.menu_block_li_projectstop.attr("projectid", projectid);
                // console.log(this.menu_block_li_newmodleulli_input);
            }else{
                return;
            }

        }  else {
            console.log("modle show", position, componentid, projectid);
            this.hide();
            // console.log("2show",position,componentid);
            this.currentshowmenumodleid = componentid;
            this.currentshowmenuprojectid = projectid;
            let modlerightmenu = $('#modle-right-menu-id');
            modlerightmenu.css("left", position.left + 'px');
            modlerightmenu.css('top', position.top + 'px');
            modlerightmenu.css("display", 'block');

            this.menu_modle_li_edit.attr("modleid", componentid);
            this.menu_modle_li_edit.attr("projectid", projectid);

            this.menu_modle_li_delete.attr("modleid", componentid);
            this.menu_modle_li_delete.attr("projectid", projectid);

            this.menu_modle_li_detail.attr("modleid", componentid);
            this.menu_modle_li_detail.attr("projectid", projectid);

            // console.log("show modleid", this.menu_modle_li_delete.attr("modleid"));
            // console.log("show projectid", this.menu_modle_li_delete.attr("projectid"));
            if(componettype!='mpc'){
                this.menu_modle_li_detail.css({'visibility': 'hidden','width': '0px','height':'0px','z-index':'-99'});
            }else {
                this.menu_modle_li_detail.removeAttr("style");
            }


        }
        this.clickleft = position.left;
        this.clicktop = position.top;
        // let rightmenu = $('#right-menu-id');
        // // console.log('rightmenu',rightmenu);
        // // console.log('position',position);
        //
        //
        // rightmenu.css("left", position.left + 'px');
        // rightmenu.css('top', position.top + 'px');
        // rightmenu.css("display", 'block');
        element.render();
        // console.log("编辑" + this.currentshowmenumodleid + "--" + this.currentshowmenuprojectid);

        // console.log('component',component)
        // console.log('component', rightmenu.css('left'),rightmenu.css('top'));

    },
    hide: function () {
        // let rightmenu = $('#right-menu-id');
        // rightmenu.css("display", "none");
        this.currentshowmenumodleid = undefined;
        this.currentshowmenuprojectid = undefined;
        let modlerightmenu = $('#modle-right-menu-id');
        modlerightmenu.css("display", "none");

        let blockrightmenu = $('#block-right-menu-id');
        blockrightmenu.css("display", "none");


    }


}