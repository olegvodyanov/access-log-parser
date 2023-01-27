package org.ovodyanov.model;

public class UserAgent {
    private final String typeOS;
    private final String browser;
    private final boolean isBot;

    public UserAgent(String userAgent) {
        this.typeOS = getOsType(userAgent);
        this.browser = getBrowser(userAgent);
        this.isBot = isBot(userAgent);
    }

    public String getTypeOS() {
        return typeOS;
    }

    public String getBrowser() {
        return browser;
    }
    public boolean isBot() {
        return isBot;
    }


    private String getOsType(String userAgent) {
        if (userAgent.contains("Windows") && !userAgent.contains("Windows Phone")) {
            return "Windows";
        } else if (userAgent.contains("Macintosh")) {
            return "Macintosh";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else {
            return "Other";
        }
    }

    private String getBrowser(String userAgent) {
        if (userAgent.contains("Gecko/") && userAgent.contains("Firefox/")) {
            return "Firefox";
        } else if (userAgent.contains("KHTML, like Gecko") && userAgent.contains("Safari/")) {
            return "Chrome";
        } else if (userAgent.contains("OPR/")) {
            return "Opera";
        } else if (userAgent.contains("Edg/") && userAgent.contains("Edge/")) {
            return "Edge";
        } else {
            return "Other";
        }
    }
    private boolean isBot(String userAgent) {
        return userAgent.toLowerCase().contains("bot");
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "typeOS='" + typeOS + '\'' +
                ", browser='" + browser + '\'' +
                '}';
    }
}
