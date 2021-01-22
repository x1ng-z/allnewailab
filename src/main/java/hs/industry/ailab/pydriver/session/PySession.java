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
}
