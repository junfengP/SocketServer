package socket.server;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import mylogger.LoggerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * 处理单个socket连接.
 *
 * @author <a href="mailto:junfeng_pan96@qq.com">junfeng</a>
 * @version 1.0.0.0
 * @since 1.8
 */


public class ChatSocket extends Thread {
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_TO = "to";
    private static final String FIELD_MSG = "message";
    private static final String FIELD_ACTION = "action";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_INFO = "info";
    private static final String CMD_REGISTER = "register";
    private static final String CMD_CHECK = "check";
    private static final String RESULT_SUCCESS = "success";
    private static final String RESULT_FAILED = "failed";
    private static final String INFO_ONLINE = "online";
    private static final String INFO_OFFLINE = "offline";
    private Socket socket;
    private String selfType;

    public ChatSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * 向客户端发送消息
     *
     * @param str 消息字符串
     */
    public void send(String str) {
        try {
            // 消息末端增加 \n，以表示 一条消息结束
            str += "\n";
            socket.getOutputStream().write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务该socket连接
     * 1. 注册类型：向ChatManager 注册该socket客户端设备类型
     * 2. 转发消息：向ChatManager 告知转发消息给谁
     * 3. 忽略其他字符串
     */
    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                String line;
                line = br.readLine();
                if (null == line) {
                    // 连接断开，则无法接收到消息，从登记列表中删除该连接
                    ChatManager.GetChatManager().deregisterType(selfType);
                    break;
                }
                LoggerUtil.server.debug(line);
                // 尝试json解析字符串，若异常，则跳过
                JSONObject jo = (JSONObject) JSONObject.parse(line);

                if (jo == null) {
                    throw new MsgStructureException("Not JSON String");
                }
                // 解析 是否 为 注册类型 命令
                if (jo.containsKey(FIELD_TYPE)) {
                    selfType = jo.getString(FIELD_TYPE);
                    ChatManager.GetChatManager().registerType(selfType, this);
                }

                // 解析 是否 为 转发消息 命令
                if (jo.containsKey(FIELD_TO) && jo.containsKey(FIELD_MSG)) {
                    String dest = jo.getString(FIELD_TO);
                    String msg = jo.getString(FIELD_MSG);
                    ChatManager.GetChatManager().Send(dest, msg);
                }

                // 解析是否 为 新命令框架
                if (jo.containsKey(FIELD_ACTION) && jo.containsKey(FIELD_VALUE)) {
                    String action = jo.getString(FIELD_ACTION);
                    String value = jo.getString(FIELD_VALUE);
                    if (CMD_REGISTER.equals(action)) {
                        // 注册命令
                        selfType = value;
                        ChatManager.GetChatManager().registerType(selfType, this);
                        replyResult(RESULT_SUCCESS, "");
                    } else if (CMD_CHECK.equals(action)) {
                        // 检查在线命令
                        boolean online = ChatManager.GetChatManager().checkOnline(value);
                        replyResult(RESULT_SUCCESS, online ? INFO_ONLINE : INFO_OFFLINE);
                    } else {
                        replyResult(RESULT_FAILED, "CMD unknown.");
                    }
                }
            } catch (SocketException e) {
                ChatManager.GetChatManager().deregisterType(selfType);
                break;
            } catch (IOException | JSONException | MsgStructureException | ClassCastException e) {
                LoggerUtil.server.error(e.getMessage());
            }
        }
    }

    /**
     * 回复消息
     *
     * @param result 命令解析是否成功
     * @param info   额外信息
     */
    private void replyResult(String result, String info) {
        JSONObject reply = new JSONObject();
        reply.put(FIELD_RESULT, result);
        reply.put(FIELD_INFO, info);
        this.send(reply.toJSONString());
    }
}

class MsgStructureException extends Exception {
    MsgStructureException(String message) {
        super(message);
    }
}
