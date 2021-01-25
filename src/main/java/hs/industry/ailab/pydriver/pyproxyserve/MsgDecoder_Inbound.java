package hs.industry.ailab.pydriver.pyproxyserve;


import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.pydriver.command.CommandImp;
import hs.industry.ailab.pydriver.session.PySession;
import hs.industry.ailab.pydriver.session.PySessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Arrays;

@ChannelHandler.Sharable
@Component
public class MsgDecoder_Inbound extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(MsgDecoder_Inbound.class);

    @Autowired
    private PySessionManager sessionManager;


    public MsgDecoder_Inbound() {
        super();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();

        String clientIp = ipSocket.getAddress().getHostAddress();
        Integer port = ipSocket.getPort();
        logger.info("come in " + clientIp + ":" + port);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        Integer port = ipSocket.getPort();
        PySession pySession = sessionManager.removeSessionModule( ctx);
        logger.info("come out" + clientIp + ":" + port + " modleid=" + pySession.getModleid()+" scriptName="+pySession.getScriptName());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String clientIp = ipSocket.getAddress().getHostAddress();
            Integer port = ipSocket.getPort();
            String ipAndPort = clientIp + ":" + port;
            logger.info(ipAndPort);
            ByteBuf wait_for_read = (ByteBuf) msg;
            if (wait_for_read.isReadable()) {
                int datacontextlength = wait_for_read.readableBytes();
                logger.info("read dada size=" + wait_for_read.readableBytes());
                byte[] bytes = new byte[wait_for_read.readableBytes()];
                wait_for_read.readBytes(bytes);
                //提取命令
                byte[] opcserveidarray = Arrays.copyOfRange(bytes, 3, 7);//header
                int modleid = byteToInt(opcserveidarray);
                byte[] command = Arrays.copyOfRange(bytes, 2, 3);//cmd
                JSONObject paramjson = null;
                switch (command[0]) {
                    case 0x01: {
                        if (CommandImp.RESULT.valid(bytes)) {
                            logger.info(CommandImp.RESULT.analye(bytes).toJSONString());
                        }
                        break;
                    }

                    case 0x03: {
                        if (CommandImp.HEART.valid(bytes)) {
                            JSONObject heartmsg = CommandImp.HEART.analye(bytes);
                            logger.info(heartmsg.toJSONString());
                            sessionManager.addSessionModule(modleid, heartmsg.getString("scriptName"),ctx);
                        }
                        break;
                    }

                    case 0x04: {
                        if (CommandImp.ACK.valid(bytes)) {
                            logger.info(CommandImp.ACK.analye(bytes).toJSONString());
                        }
                        break;
                    }

                    case 0x05: {
                        if (CommandImp.STOP.valid(bytes)) {
                            logger.info(CommandImp.STOP.analye(bytes).toJSONString());
                        }
                        break;
                    }
                    default:
                        logger.warn("no match any command");
                        break;
                }
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error(cause.getMessage(), cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;

            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            String clientIp = ipSocket.getAddress().getHostAddress();
            IdleStateEvent stateEvent = (IdleStateEvent) evt;

            switch (stateEvent.state()) {
                case READER_IDLE:
                    logger.info(clientIp + "Read Idle");
                    break;
                case WRITER_IDLE:
                    logger.info(clientIp + "Read Idle");
                    break;
                case ALL_IDLE:
                    logger.info(clientIp + "Read Idle");
                    break;
                default:
                    break;
            }
        }
    }

    private int byteToInt(byte[] data) {

        int reult = ((data[0] << 24) & 0xff000000) |
                ((data[1] << 16) & 0xff0000) |
                ((data[2] << 8) & 0xff00) |
                ((data[3] << 0) & 0xff);

        return reult;
    }

}
