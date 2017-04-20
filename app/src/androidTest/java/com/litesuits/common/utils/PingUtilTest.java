package com.litesuits.common.utils;

import android.test.AndroidTestCase;

import com.litesuits.android.log.Log;

import java.net.InetAddress;
import java.util.HashMap;


/**
 * Created by yanxq on 17/4/20.
 */
public class PingUtilTest extends AndroidTestCase{

    private static final String TAG = "PingUtil";

    public void testPingCommand() throws Exception {
        String command = PingUtil.createSimplePingCommand("14.215.177.37",2,10);
        Log.i(TAG,"command: " + command);
        String response = PingUtil.ping(command);
        Log.i(TAG,response);

        PingUtil.PingBean pingBean = PingUtil.parsePingInputString(response);
        assertNotNull(pingBean);
        Log.i(TAG,"ping bean: " + pingBean);
    }

    public void testCustomPropertiesCommand() throws Exception {
        String command = new PingUtil
                .PingCommandBuilder("14.215.177.37")
                .count(3)
                .packageSize(12)
                .timeout(1)
                .build();
        Log.i(TAG,"command: " + command);

        String response = PingUtil.ping(command);
        PingUtil.PingBean pingBean = PingUtil.parsePingInputString(response);
        assertNotNull(pingBean);

        Log.i(TAG,"ping bean: " + pingBean);
    }

}