package client;

import bizsocket.core.*;
import bizsocket.tcp.Packet;
import bizsocket.tcp.PacketFactory;
import common.WPBCmd;
import common.WPBPacket;
import okio.BufferedSource;
import okio.ByteString;
import java.io.IOException;
import java.util.Map;

/**
 * Created by tong on 16/10/3.
 */
public class WPBClientForMockServer extends AbstractBizSocket implements PacketFactory {
    public WPBClientForMockServer(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected PacketFactory createPacketFactory() {
        return this;
    }

    @Override
    public Packet buildRequestPacket(int command, ByteString requestBody, Map<String, String> attach) {
        return new common.WPBPacket(command,requestBody);
    }

    @Override
    public Packet buildPacket(BufferedSource source) throws IOException {
        return WPBPacket.build(source);
    }

    @Override
    public boolean supportHeartBeat() {
        return false;
    }

    @Override
    public Packet buildHeartBeatPacket() {
        return null;
    }

    public static void main(String[] args) {
        WPBClientForMockServer client = new WPBClientForMockServer(new Configuration.Builder()
                .host("127.0.0.1")
                .port(9103)
                .build());
        client.addSerialSignal(new SerialSignal(OrderListSerialContext.class, WPBCmd.QUERY_ORDER_LIST.getValue(),
                new int[]{WPBCmd.QUERY_ORDER_LIST.getValue(), WPBCmd.QUERY_ORDER_TYPE.getValue()}));

        client.getInterceptorChain().addInterceptor(new Interceptor() {
            @Override
            public boolean postRequestHandle(RequestContext context) throws Exception {
                System.out.println("发现一个请求postRequestHandle: " + context);
                return false;
            }

            @Override
            public boolean postResponseHandle(int command, Packet responsePacket) throws Exception {
                System.out.println("收到一个包postResponseHandle: " + responsePacket);
                return false;
            }
        });
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.subscribe(client, WPBCmd.NOTIFY_PRICE.getValue(), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        String json = "{\"productId\" : \"1\",\"isJuan\" : \"0\",\"type\" : \"2\",\"sl\" : \"1\"}";
        client.request(client, WPBCmd.CREATE_ORDER.getValue(), ByteString.encodeUtf8(json), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " attach: " + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        json = "{\"pageSize\" : \"10000\"}";
        client.request(client, WPBCmd.QUERY_ORDER_LIST.getValue(), ByteString.encodeUtf8(json), new ResponseHandler() {
            @Override
            public void sendSuccessMessage(int command, ByteString requestBody, Packet responsePacket) {
                System.out.println("cmd: " + command + " ,requestBody: " + requestBody + " responsePacket: " + responsePacket);
            }

            @Override
            public void sendFailureMessage(int command, Throwable error) {
                System.out.println(command + " ,err: " + error);
            }
        });

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
