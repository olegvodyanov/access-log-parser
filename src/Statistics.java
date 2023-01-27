import org.ovodyanov.model.LogEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> allExistingEndpoints;
    private HashSet<String> allInvalidEndpoints;
    private HashMap<String, Integer> operatingSystemStat;
    private HashMap<String, Integer> usersBrowserStat;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        allExistingEndpoints = new HashSet<>();
        allInvalidEndpoints = new HashSet<>();
        operatingSystemStat = new HashMap<>();
        usersBrowserStat = new HashMap<>();
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if (minTime == null || logEntry.getTime().isBefore(minTime)) {
            minTime = logEntry.getTime();
        } else if (maxTime == null || logEntry.getTime().isAfter(maxTime)) {
            maxTime = logEntry.getTime();
        }

        String logEntryPath = logEntry.getPath();
        if (logEntry.getResponseCode() == 200) {
            allExistingEndpoints.add(logEntryPath);
        } else if (logEntry.getResponseCode() == 404) {
            allInvalidEndpoints.add(logEntryPath);
        }

        String logEntryOsType = logEntry.getAgent().getTypeOS();
        if (operatingSystemStat.containsKey(logEntryOsType)) {
            int counter = operatingSystemStat.get(logEntryOsType);
            operatingSystemStat.replace(logEntryOsType, ++counter);
        } else {
            operatingSystemStat.put(logEntryOsType, 1);
        }

        String logEntryBrowser = logEntry.getAgent().getBrowser();
        if (usersBrowserStat.containsKey(logEntryBrowser)) {
            int counter = usersBrowserStat.get(logEntryBrowser);
            usersBrowserStat.replace(logEntryBrowser, ++counter);
        } else {
            usersBrowserStat.put(logEntryBrowser, 1);
        }
    }

    public long getTrafficRate() {
        Duration durationBetween = Duration.between(minTime, maxTime);
        double hours = durationBetween.toHoursPart();
        double minutesToHours = (double) durationBetween.toMinutesPart() / 60.0;
        double secondsToHours = (double) durationBetween.toSecondsPart() / 60.0 / 60.0;
        double durationInHours = hours + minutesToHours + secondsToHours;

        return BigDecimal.valueOf(totalTraffic).divide(BigDecimal.valueOf(durationInHours), RoundingMode.HALF_UP).longValue();
    }

    public List<String> getAllUniquePagesExistingInLogFile() {
        return new ArrayList<>(allExistingEndpoints);
    }

    public List<String> getAllUniquePagesInvalidInLogFile() {
        return new ArrayList<>(allInvalidEndpoints);
    }

    public HashMap<String, Double> getOsTypeProportionStat() {
        HashMap<String, Double> typeOsProportionMap = new HashMap<>();
        int totalCounter = totalCalculator(operatingSystemStat);

        for (Map.Entry<String, Integer> set : operatingSystemStat.entrySet()) {
            typeOsProportionMap.put(set.getKey(), ((double) set.getValue() / (double) totalCounter));
        }
        return typeOsProportionMap;
    }

    public HashMap<String, Double> getBrowserProportionStat() {
        HashMap<String, Double> browserProportionMap = new HashMap<>();
        int totalCounter = totalCalculator(usersBrowserStat);

        for (Map.Entry<String, Integer> set : usersBrowserStat.entrySet()) {
            browserProportionMap.put(set.getKey(), ((double) set.getValue() / (double) totalCounter));
        }
        return browserProportionMap;
    }

    private static int totalCalculator(HashMap<String, Integer> inputSet) {
        int finalResult = 0;
        for (Map.Entry<String, Integer> set : inputSet.entrySet()) {
            finalResult += set.getValue();
        }
        return finalResult;
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }
}
