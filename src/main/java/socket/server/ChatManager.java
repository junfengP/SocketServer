package socket.server;

import mylogger.LoggerUtil;

import java.util.Hashtable;
import java.util.Map;


/**
 * Socket管理.
 *
 * @author <a href="mailto:junfeng_pan96@qq.com">junfeng</a>
 * @version 1.0.0.0
 * @since 1.8
 */


public class ChatManager {
    private static final ChatManager cm = new ChatManager();
    private Map<String, ChatSocket> address = new Hashtable<>();

    /**
     * 单例化
     * 因为一个聊天系统只有一个聊天管理，所以需进行单例化private
     */
    private ChatManager() {
    }

    static ChatManager GetChatManager() {
        return cm;
    }

    /**
     * 注册设备类型
     *
     * @param type 设备类型
     * @param cs   通信socket实例
     */
    void registerType(String type, ChatSocket cs) {
        address.put(type, cs);
    }

    /**
     * 移除已注册类型
     *
     * @param type 设备类型
     */
    void deregisterType(String type) {
        if (null != type) {
            LoggerUtil.server.info(type + " is offline.");
            address.remove(type);
        }
    }


    /**
     * 转发消息
     *
     * @param dest 消息目的地
     * @param msg  消息本体
     */
    void Send(String dest, String msg) {
        ChatSocket cs = address.get(dest);
        if (cs != null) {
            cs.send(msg);
        }
    }

    /**
     * 检查对应类型socket是否在线
     *
     * @param type 待检查类型
     * @return 在线true, 否则false
     */
    boolean checkOnline(String type) {
        ChatSocket cs = address.get(type);
        return cs != null;
    }
}
