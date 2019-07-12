package socket.server;

/**
 * 启动socketServer.
 *
 * @author <a href="mailto:junfeng_pan96@qq.com">junfeng</a>
 * @version 1.0.0.0
 * @since 1.8
 */


public class SocketCenter {
    public static void main(String[] args) {
        if(args.length ==0) {
            new GetSocket().start();
        } else if(args.length == 1) {
            int port = Integer.parseInt(args[0]);
            new GetSocket(port).start();
        } else {
            throw new IllegalArgumentException("Require port number.");
        }

    }
}
