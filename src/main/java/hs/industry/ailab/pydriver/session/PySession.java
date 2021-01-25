package hs.industry.ailab.pydriver.session;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/12/29 23:05
 */
public class PySession {
   // private Module object;//apc module;
    private ChannelHandlerContext ctx;
    private int modleid;
    private String scriptName;//不含.py


    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }


    public int getModleid() {
        return modleid;
    }

    public void setModleid(int modleid) {
        this.modleid = modleid;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}
