/**
 * Copyright 2010 Lennart Koopmann <lennart@socketfeed.com>
 * 
 * This file is part of Graylog2.
 *
 * Graylog2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog2.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.graylog2.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import org.drools.util.codec.Base64;
import org.joda.time.DateTime;

/**
 * Utilty class for various tool/helper functions.
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public final class Tools {

    private Tools() { }

    /**
     * Get the own PID of this process.
     *
     * @return PID of the running process
     */
    public static String getPID() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    }

    /**
     * Converts integer syslog loglevel to human readable string
     *
     * @param level The level to convert
     * @return The human readable level
     */
    public static String syslogLevelToReadable(int level) {
        switch (level) {
            case 0:
                return "Emergency";
            case 1:
                return "Alert";
            case 2:
                return"Critical";
            case 3:
                return "Error";
            case 4:
                return "Warning";
            case 5:
                return "Notice";
            case 6:
                return "Informational";
            case 7:
                return "Debug";
        }

        return "Invalid";
    }

    /**
     * Converts integer syslog facility to human readable string
     *
     * @param facility The facility to convert
     * @return The human readable facility
     */
    public static String syslogFacilityToReadable(int facility) {
        switch (facility) {
            case 0:  return "kernel";
            case 1:  return "user-level";
            case 2:  return "mail";
            case 3:  return "system daemon";
            case 4: case 10: return "security/authorization";
            case 5:  return "syslogd";
            case 6:  return "line printer";
            case 7:  return "network news";
            case 8:  return "UUCP";
            case 9: case 15: return "clock";
            case 11: return "FTP";
            case 12: return "NTP";
            case 13: return "log audit";
            case 14: return "log alert";

            // TODO: Make user definable?
            case 16: return "local0";
            case 17: return "local1";
            case 18: return "local2";
            case 19: return "local3";
            case 20: return "local4";
            case 21: return "local5";
            case 22: return "local6";
            case 23: return "local7";
        }

        return "Unknown";
    }

    /**
     * Get a String containing version information of JRE, OS, ...
     * @return Descriptive string of JRE and OS
     */
    public static String getSystemInformation() {
        String ret = System.getProperty("java.vendor");
        ret += " " + System.getProperty("java.version");
        ret += " on " + System.getProperty("os.name");
        ret += " " + System.getProperty("os.version");
        return ret;
    }


    /**
     * Decompress ZLIB (RFC 1950) compressed data
     *
     * @return A string containing the decompressed data
     */
    public static String decompressZlib(byte[] compressedData) throws IOException {
        byte[] buffer = new byte[compressedData.length];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(compressedData));
        for (int bytesRead = 0; bytesRead != -1; bytesRead = in.read(buffer)) {
            out.write(buffer, 0, bytesRead);
        }
        return new String(out.toByteArray(), "UTF-8");
    }

    /**
     * Decompress GZIP (RFC 1952) compressed data
     * 
     * @return A string containing the decompressed data
     */
    public static String decompressGzip(byte[] compressedData) throws IOException {
        byte[] buffer = new byte[compressedData.length];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(compressedData));
        for (int bytesRead = 0; bytesRead != -1; bytesRead = in.read(buffer)) {
            out.write(buffer, 0, bytesRead);
        }
        return new String(out.toByteArray(), "UTF-8");
    }

    /**
     *
     * @return The current UTC UNIX timestamp.
     */
    public static int getUTCTimestamp() {
       return (int) (System.currentTimeMillis()/1000);
    }

    /**
     * Get the current UNIX epoch with milliseconds of the system
     *
     * @return The current UTC UNIX timestamp with milliseconds.
     */
    public static double getUTCTimestampWithMilliseconds() {
        return getUTCTimestampWithMilliseconds(System.currentTimeMillis());
    }

    /**
     * Get the UNIX epoch with milliseconds of the provided millisecond timestamp
     *
     * @param timestamp a millisecond timestamp (milliseconds since UNIX epoch)
     * @return The current UTC UNIX timestamp with milliseconds.
     */
    public static double getUTCTimestampWithMilliseconds(long timestamp) {
        
        return timestamp / 1000.0;

    }

    public static String getLocalHostname() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            return "Unknown";
        }

        return addr.getHostName();
    }
    
    public static String getLocalCanonicalHostname() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            return "Unknown";
        }

        return addr.getCanonicalHostName();
    }

    public static int getTimestampDaysAgo(int ts, int days) {
        return (ts - (days*86400));
    }

    public static String encodeBase64(String what) {
        return new String(Base64.encodeBase64(what.getBytes()));
    }

    public static String decodeBase64(String what) {
        return new String(Base64.decodeBase64(what));
    }

    public static String rdnsLookup(InetAddress socketAddress) throws UnknownHostException {
        return socketAddress.getCanonicalHostName();
    }
    
    public static String generateServerId() {
        UUID id = UUID.randomUUID();
        
        return getLocalHostname() + "-" + id.toString();
    }
    
    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
    
    // yyyy-MM-dd HH-mm-ss
    // http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Formatter.html#syntax
    public static String buildElasticSearchTimeFormat(double timestamp) {
        return format(System.currentTimeMillis()); // ramtamtam no more. just fast
    }
    

    static private volatile long   lastTime;
    static private volatile String lastTimeString = null;

    /**
         Appends a date in the format "YYYY-mm-dd HH-mm-ss"
         to <code>sbuf</code>. For example: "1999-11-27 15-49-37".

         @param sbuf the <code>StringBuffer</code> to write to
     */
    public static String format(long now ) {

        int millis = (int)(now % 1000);

        if ((now - millis) != lastTime || lastTimeString == null) {
            StringBuffer sbuf = new StringBuffer(20);
            // We reach this point at most once per second
            // across all threads instead of each time format()
            // is called. This saves considerable CPU time.
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(now);

            int year =  calendar.get(Calendar.YEAR);
            sbuf.append(year);

            String month;
            switch(calendar.get(Calendar.MONTH)) {
            case Calendar.JANUARY: month = "-01-"; break;
            case Calendar.FEBRUARY: month = "-02-";  break;
            case Calendar.MARCH: month = "-03-"; break;
            case Calendar.APRIL: month = "-04-";  break;
            case Calendar.MAY: month = "-05-"; break;
            case Calendar.JUNE: month = "-06-";  break;
            case Calendar.JULY: month = "-07-"; break;
            case Calendar.AUGUST: month = "-08-";  break;
            case Calendar.SEPTEMBER: month = "-09-"; break;
            case Calendar.OCTOBER: month = "-10-"; break;
            case Calendar.NOVEMBER: month = "-11-";  break;
            case Calendar.DECEMBER: month = "-12-";  break;
            default: month = "-NA-"; break;
            }
            sbuf.append(month);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if(day < 10)
                sbuf.append('0');
            sbuf.append(day);

            sbuf.append(' ');

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour < 10) {
                sbuf.append('0');
            }
            sbuf.append(hour);
            sbuf.append('-');

            int mins = calendar.get(Calendar.MINUTE);
            if(mins < 10) {
                sbuf.append('0');
            }
            sbuf.append(mins);
            sbuf.append('-');

            int secs = calendar.get(Calendar.SECOND);
            if(secs < 10) {
                sbuf.append('0');
            }
            sbuf.append(secs);

            lastTime = now - millis;
            lastTimeString = sbuf.toString();
        }

        return lastTimeString;
    }

 
}
