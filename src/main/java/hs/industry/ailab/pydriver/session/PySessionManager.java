package hs.industry.ailab.pydriver.session;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/12/29 23:04
 */
@Component
public class PySessionManager {
    private Logger logger = LoggerFactory.getLogger(PySessionManager.class);

    //key nodeid
    private Map<Integer, PySession> modulepoolbynodeid =new ConcurrentHashMap<>();
    //ctx
    private Map<ChannelHandlerContext, PySession> modulepoolbyctx =new ConcurrentHashMap<>();
    public void addSessionModule(int nodeid, ChannelHandlerContext ctx){
        if(!modulepoolbynodeid.containsKey(nodeid)){
            PySession session=new PySession();
            session.setCtx(ctx);
            session.setModleid(nodeid);
            modulepoolbynodeid.put(nodeid,session);
            modulepoolbyctx.put(ctx,session);
        }

    }


    public PySession removeSessionModule(Integer nodeid, ChannelHandlerContext ctx){
        PySession session=null;
        if(ctx!=null){

            session= modulepoolbyctx.remove(ctx);
            if(session!=null){
                modulepoolbynodeid.remove(session.getModleid());
            }else {
                logger.warn("session is null");
            }
        }else if(nodeid!=null){
             session=modulepoolbynodeid.remove(nodeid);
            if(session!=null){
                modulepoolbyctx.remove(session.getModleid());
            }else {
                logger.warn("session is null");
            }

        }
        return session;
    }

    public Map<Integer, PySession> getModulepoolbynodeid() {
        return modulepoolbynodeid;
    }

    public Map<ChannelHandlerContext, PySession> getModulepoolbyctx() {
        return modulepoolbyctx;
    }
}
