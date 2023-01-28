package org.ovodyanov.model;

import org.ovodyanov.enums.HttpMethods;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private static final String REGEX = "([\\d]{1,3}[\\.][\\d]{1,3}[\\.][\\d]{1,3}[\\.][\\d]{1,3})\\s([\\-|\\s])" +
            "\\s([\\-|\\s])\\s\\[([^]]*)\\]\\s\\\"([^\\\"]*)\\\"\\s([\\d]+)\\s([\\d]+)\\s\\\"([^\\\"]*)\\\"\\s\\\"([^\\\"]*)\\\"";
    private static final String REGEX_DOMAIN = "\\/\\/([\\w|\\-]+\\.\\w{1,5})";
    private static final String TIME_PATTERN = "dd/MMM/yyyy:HH:mm:ss Z";
    private final String ipAddr;
    private final String attributeOne;
    private final String attributeTwo;
    private final LocalDateTime time;
    private final HttpMethods method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent agent;
    private final String domain;

    public LogEntry(String logString) {
        final Pattern pattern = Pattern.compile(REGEX, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(logString);

        matcher.find();

        /*здесь мы разбиваем строку вида GET /recruitment/november-reports/report/may-reports/?n=2 HTTP/1.0
        на три составляющие
        1) GET
        2) /recruitment/november-reports/report/may-reports/?n=2
        3) HTTP/1.0
        */
        String[] methodPathArray = methodPathParser(matcher.group(5));

        this.ipAddr = matcher.group(1);
        this.attributeOne = matcher.group(2);
        this.attributeTwo = matcher.group(3);
        this.time = getDateTime(matcher.group(4));
        this.method = HttpMethods.valueOf(methodPathArray[0]);
        this.path = methodPathArray[1];
        this.responseCode = Integer.parseInt(matcher.group(6));
        this.responseSize = Integer.parseInt(matcher.group(7));
        this.referer = matcher.group(8);
        this.agent = new UserAgent(matcher.group(9));

        /*Выбираем доменное имя путем разделения строки referer по разделителю /
        Таким образом получаем третим элементом доменное имя, пример https://www.focus-news.net/ делится на части:
        https:
        "<пустое место>"
        www.focus-news.net <- берём вот это значение - индекс 2 в массиве
        */

        String[] domains = matcher.group(8).split("/");
        if (domains.length <= 1) {
            this.domain = null;
        } else {
            this.domain = domains[2];
        }
    }

    public String getDomain() {
        return domain;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getAttributeOne() {
        return attributeOne;
    }

    public String getAttributeTwo() {
        return attributeTwo;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethods getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getAgent() {
        return agent;
    }

    private String[] methodPathParser(String methodPathString) {
        return methodPathString.split(" ");
    }

    private LocalDateTime getDateTime(String dateTimeString) {
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(TIME_PATTERN)
                .toFormatter(Locale.ENGLISH);
        return LocalDateTime.parse(dateTimeString, dtf);
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAddr='" + ipAddr + '\'' +
                ", attributeOne='" + attributeOne + '\'' +
                ", attributeTwo='" + attributeTwo + '\'' +
                ", time=" + time +
                ", method=" + method +
                ", path='" + path + '\'' +
                ", responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                ", referer='" + referer + '\'' +
                ", agent='" + agent + '\'' +
                '}';
    }
}
