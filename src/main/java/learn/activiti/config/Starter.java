package learn.activiti.config;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Starter {


        private static final Logger logger = LoggerFactory.getLogger(Starter.class);

        public static void startH2Server() throws SQLException {
            try {
                Server h2Server = Server.createTcpServer().start(); // 关键代码
                if (h2Server.isRunning(true)) {
                    logger.info("H2 server was started and is running.");
                } else {
                    throw new RuntimeException("Could not start H2 server.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to start H2 server: ", e);
            }
        }

}
