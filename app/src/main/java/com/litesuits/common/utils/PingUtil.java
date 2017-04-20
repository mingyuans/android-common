package com.litesuits.common.utils;


import com.litesuits.android.log.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanxq on 17/4/20.
 */

public class PingUtil {
    private static final String TAG= "PingUtil";

    public static PingBean parsePingInputString(String response) {
        if (response == null || response.length() == 0) {
            return null;
        }
        String[] lines = response.split("\n");

        boolean isStatisticsLineFound = false;
        for (String line : lines) {
            //find statistics
            if (!isStatisticsLineFound && !line.startsWith("---")) {
                continue;
            }
            if (!isStatisticsLineFound) {
                isStatisticsLineFound = true;
                continue;
            }

            try {
                Pattern pattern = Pattern.compile("(\\d)\\s*packets transmitted,\\s*(\\d)\\sreceived,\\s*(.+)% packet loss,\\stime\\s(\\d+)ms");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    PingBean pingBean = new PingBean();
                    pingBean.transmittedPackages = Integer.valueOf(matcher.group(1));
                    pingBean.receivedPackages = Integer.valueOf(matcher.group(2));
                    pingBean.lossPackagesPercent = Float.valueOf(matcher.group(3));
                    pingBean.millionTime = Integer.valueOf(matcher.group(4));
                    return pingBean;
                }
            } catch (Throwable throwable) {
                Log.e(TAG,"getPackageLost: ",throwable);
            }
        }
        return null;

    }

    public static String ping(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader;
            int status = process.waitFor();
            InputStream inputStream;
            if (status == 0) {
                inputStream = process.getInputStream();
            } else {
                inputStream = process.getErrorStream();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            reader.close();
            inputStream.close();
            return builder.toString();
        } catch (Throwable throwable) {
            Log.e(TAG,"Exec ping command with error.",throwable);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return "";
    }

    public static String createSimplePingCommand(String domain,int count, int timeout) {
        return String.format("/system/bin/ping -c %d -W %d %s",count,timeout,domain);
    }

    public static String createPingCommand(String domain, Map<String, String> properties) {
        StringBuilder builder = new StringBuilder("/system/bin/ping");
        if (properties != null && properties.size() > 0) {
            for (Map.Entry<String,String> entry : properties.entrySet()) {
                builder.append(String.format(" -%s %s",entry.getKey(),entry.getValue()));
            }
        }
        builder.append(" " + domain);
        return builder.toString();
    }

    public static class PingCommandBuilder {
        HashMap<String,String> properties = new HashMap<String, String>();
        String domain = "";
        public PingCommandBuilder(String domain) {
            this.domain = domain;
        }

        public PingCommandBuilder count(int count) {
            properties.put("c",String.valueOf(count));
            return this;
        }

        public PingCommandBuilder timeout(int timeout) {
            properties.put("W",String.valueOf(timeout));
            return this;
        }

        public PingCommandBuilder packageSize(int size) {
            properties.put("s",String.valueOf(size));
            return this;
        }

        public PingCommandBuilder ttl(int ttl) {
            properties.put("t",String.valueOf(ttl));
            return this;
        }

        public String build() {
            return PingUtil.createPingCommand(domain,properties);
        }
    }

    public static class PingBean {
        public int transmittedPackages = 0;
        public int receivedPackages = 0;
        public float lossPackagesPercent = 0;
        public int millionTime = 0;

        @Override
        public String toString() {
            return "PingBean{" +
                    "transmittedPackages=" + transmittedPackages +
                    ", receivedPackages=" + receivedPackages +
                    ", lossPackagesPercent=" + lossPackagesPercent +
                    ", millionTime=" + millionTime +
                    '}';
        }
    }


}
