var templefactory = {
    temple: function (data) {

        let waper=$('<div id="" class="window"></div>');
        waper.attr("id",data.moduleid);
        waper.css({"top": data.top + "px", "left": data.left + "px"});
        waper.attr("data-projectid",data.projectid);
        waper.attr("data-id",data.moduleid);
        waper.on('contextmenu', (ev) => {
            ev.preventDefault();
            // console.log("nodeev",ev);
            // console.log("$(\"#side-buttons\").css(\"width\")=",$("#side-buttons").css("width"))
            // console.log($(ev['currentTarget']).find("li")[0].text())
            contextMenu.show({
                left: ev.pageX-$("#side-buttons").width(),
                top: ev.pageY
            },ev['currentTarget']['dataset']["id"],ev['currentTarget']['dataset']["projectid"]);
        });
        let header=$('<div style="width: 150px"></div>');
        let modulenode=undefined;
        switch (data.type) {
            case 'input':
            {
                modulenode=$('<i class="layui-icon" style="font-size: 15px; color: #1E9FFF;">&#xe66e;'+data.name+'</i> ')
                header.append(modulenode);
                break;
            }

            case 'output':{
                modulenode=$('<i class="layui-icon" style="font-size: 15px; color: #1E9FFF;">&#xe691;'+data.name+'</i> ')
                header.append(modulenode);
                break;
            }

            case 'customize':
            {
                modulenode=$('<i class="layui-icon" style="font-size: 15px; color: #1E9FFF;">&#xe655;'+data.name+'</i> ')
                header.append(modulenode);
                break;
            }

            case 'filter':{
                modulenode=$('<i class="layui-icon" style="font-size: 15px; color: #1E9FFF;">&#xe663;'+data.name+'</i> ')
                header.append(modulenode);
                break;
            }


            case 'mpc':
            {
                modulenode=$('<i class="layui-icon" style="font-size: 15px; color: #1E9FFF;">&#xe638;'+data.name+'</i> ')
                header.append(modulenode);
                break;
            }

            case 'pid':{
                modulenode=$('<i class="layui-icon" style="font-size: 15px; color: #1E9FFF;">&#xe638;'+data.name+'</i> ')
                header.append(modulenode);
                break;
            }

        }
        // let deletebt=$('<span class="delete-node pull-right" data-type="deleteNode" data-id="">X</span>');
        // deletebt.attr('data-id',data.moduleid);
        // deletebt.attr('data-projectid',data.projectid);
        // modulenode.append(deletebt);


        waper.append(header);

        let collapseTotal = $('<div class="layui-collapse" lay-filter="" style="height: 70%"> </div>');
        collapseTotal.attr("lay-filter",data.moduleid);
        waper.append(collapseTotal);


        let inuputcontext_1=$('<div class="layui-colla-item"></div>' );


        let inuputcontext_2=$('<h2 class="layui-colla-title" style="height:25px;line-height:20px;font-size:10px">Ins</h2>');


        let inuputcontext_3=$('<div class="layui-colla-content" id="">incontext</div>');
        inuputcontext_3.attr("id",data.moduleid+'incontext');

        let context_='';
        for(let index=0;index<data.inputproperty.length;index++){
            context_=context_+(data.inputproperty[index].name.substring(0,7))+ (data.inputproperty[index].pin!=undefined?data.inputproperty[index].pin.substring(0,7):"")  + '\n' + data.inputproperty[index].value+'\n';
        }
        // console.log("ins",inuputcontext_3.text());
        // inuputcontext_3.html(context_);
        inuputcontext_3[0].innerText=context_;

        inuputcontext_1.append(inuputcontext_2);

        inuputcontext_1.append(inuputcontext_3);

        collapseTotal.append(inuputcontext_1);

        let outputcontext_1=$('<div class="layui-colla-item"></div>' );

        let outputcontext_2=$('<h2 class="layui-colla-title" style="height:25px;line-height:20px;font-size:10px">Outs</h2>');


        let outputcontext_3=$('<div class="layui-colla-content" style="width: 150px;font-size: 12px" id="">outcontext</div>');
        outputcontext_3.attr("id",data.moduleid+'outcontext');

        context_='';
        for(let index=0;index<data.outputproperty.length;index++){
            context_=context_+(data.outputproperty[index].name.substring(0,7))+ (data.outputproperty[index].pin!=undefined?data.outputproperty[index].pin.substring(0,7):"")  + '\n' + data.outputproperty[index].value+'\n';
        }
        // console.log(outputcontext_3.text());
        // console.log("context_",context_)
        outputcontext_3[0].innerText=context_;

        outputcontext_1.append(outputcontext_2);

        outputcontext_1.append(outputcontext_3);

        collapseTotal.append(outputcontext_1);

        return waper;
    }


}