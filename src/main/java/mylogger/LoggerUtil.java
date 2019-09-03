package mylogger;

import org.apache.log4j.Logger;


/**
 * Logger工具,
 * 整个项目通用的Logger工具.
 *
 * @author <a href="mailto:junfeng_pan96@qq.com">junfeng</a>
 * @version 1.0.0.0
 * @since 1.8
 */

public class LoggerUtil {
    public static Logger server = Logger.getLogger("socketserver");

//    static {
//        System.out.println(System.getProperty("user.dir"));
//
//        PropertyConfigurator.configure("log4j.properties");
//    }
}