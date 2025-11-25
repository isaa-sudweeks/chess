package server.handlers;

import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket Connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        //TODO: This is where I need to put all the stuff to handel different sent messages
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket Closed");

    }

    public void registerRoutes(final Javalin app) {
        app.ws("/ws", ws -> {
            ws.onConnect(this);
            ws.onMessage(this);
            ws.onClose(this);
        });
    }
}
