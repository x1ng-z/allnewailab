package hs.industry.ailab.pydriver.session;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
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
    //key tcp port
    private Map<Integer, PySession> modulepoolbytcpport =new ConcurrentHashMap<>();
    public void addSessionModule(int nodeid, ChannelHandlerContext ctx){
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        Integer port = ipSocket.getPort();
        if(!modulepoolbynodeid.containsKey(nodeid)){
            PySession session=new PySession();
            session.setCtx(ctx);
            session.setTcpPort(port);
            modulepoolbynodeid.put(nodeid,session);
            modulepoolbytcpport.put(port,session);
        }

    }


    public void removeSessionModule(Integer nodeid, ChannelHandlerContext ctx){

        if(ctx!=null){
            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String clientIp = ipSocket.getAddress().getHostAddress();
            Integer port = ipSocket.getPort();
            PySession session=modulepoolbytcpport.remove(port);
            if(session!=null){
                modulepoolbynodeid.remove(session.getNodeid());
            }else {
                logger.warn("session is null");
            }
        }else if(nodeid!=null){
            PySession session=modulepoolbynodeid.remove(nodeid);
            if(session!=null){
                modulepoolbytcpport.remove(session.getTcpPort());
                session.getCtx().close();
            }else {
                logger.warn("session is null");
            }

        }

    }

    public Map<Integer, PySession> getModulepoolbynodeid() {
        return modulepoolbynodeid;
    }

    public Map<Integer, PySession> getModulepoolbytcpport() {
        return modulepoolbytcpport;
    }
}
