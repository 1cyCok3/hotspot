package com.mapreduce.hotspot.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class HBaseConnectionManager {
    private static Configuration conf = null;
    static {
        conf = HBaseConfiguration.create();
        conf.set(HConstants.ZOOKEEPER_QUORUM, "master001,slave002,slave003");
    }
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null || !conn.isClosed()) {
            try {
                conn = ConnectionFactory.createConnection(conf);
            } catch (IOException e) {
                try {
                    conn = ConnectionFactory.createConnection(conf);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return conn;
    }

}
