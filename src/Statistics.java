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
    private HashSet<String> allEndpoints;
    private HashMap<String, Integer> operatingSystemStat;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        allEndpoints = new HashSet<>();
        operatingSystemStat = new HashMap<>();
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
            allEndpoints.add(logEntryPath);
        }

        String logEntryOsType = logEntry.getAgent().getTypeOS();
        if (operatingSystemStat.containsKey(logEntryOsType)) {
            int counter = operatingSystemStat.get(logEntryOsType);
            operatingSystemStat.replace(logEntryOsType, ++counter);
        } else {
            operatingSystemStat.put(logEntryOsType, 1);
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
        return new ArrayList<>(allEndpoints);
    }

    public HashMap<String, Double> getOsTypeProportionStat() {
        HashMap<String, Double> typeOsProportionMap = new HashMap<>();
        int totalOsCounter = totalOsTypesCalculator();

        for (Map.Entry<String, Integer> set : operatingSystemStat.entrySet()) {
            typeOsProportionMap.put(set.getKey(), ((double) set.getValue() / (double) totalOsCounter) );
        }
        return typeOsProportionMap;
    }

    private int totalOsTypesCalculator() {
        int typeOsCounter = 0;
        for (Map.Entry<String, Integer> set : operatingSystemStat.entrySet()) {
            typeOsCounter += set.getValue();
        }
        return typeOsCounter;
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
