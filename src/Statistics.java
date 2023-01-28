import org.ovodyanov.model.LogEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private long totalTraffic;
    private int uniqueSecondCounter;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> allExistingEndpoints;
    private HashSet<String> allInvalidEndpoints;
    private HashSet<String> uniqueSetOfIpAddresses;
    private HashSet<String> uniqueTimeStamps;
    private HashMap<String, Integer> operatingSystemStat;
    private HashMap<String, Integer> usersBrowserStat;
    private HashMap<String, Integer> numberOfVisitsFoEachRealUserMap;
    private TreeMap<String, Integer> amountOfVisitsEachSecond;
    private HashSet<String> domainAddresses;
    private int noBotsEntriesCounterInLogFile;
    private static final int GOOD_RESPONSE_CODE = 200;
    private static final int BAD_RESPONSE_CODE_4XX = 4;
    private static final int BAD_RESPONSE_CODE_5XX = 5;
    private static final double TIME_CONSTANT = 60.0;

    public Statistics() {
        totalTraffic = 0;
        minTime = null;
        maxTime = null;
        noBotsEntriesCounterInLogFile = 0;
        uniqueSecondCounter = 0;
        allExistingEndpoints = new HashSet<>();
        allInvalidEndpoints = new HashSet<>();
        uniqueSetOfIpAddresses = new HashSet<>();
        uniqueTimeStamps = new HashSet<>();
        operatingSystemStat = new HashMap<>();
        usersBrowserStat = new HashMap<>();
        numberOfVisitsFoEachRealUserMap = new HashMap<>();
        amountOfVisitsEachSecond = new TreeMap<>();
        domainAddresses = new HashSet<>();
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if (minTime == null || logEntry.getTime().isBefore(minTime)) {
            minTime = logEntry.getTime();
        } else if (maxTime == null || logEntry.getTime().isAfter(maxTime)) {
            maxTime = logEntry.getTime();
        }

        String logEntryPath = logEntry.getPath();
        /*В одном из заданий курсовой работы нам надо отбирать все ошибочные запросы с кодами 4хх и 5хх
        для этого я привожу числовое значение кода ответа из файла к строке, чтобы потом проверить является ли первый символ
        кода пятёркой или четвёркой*/
        String stringWrapperForResponseCode = "" + logEntry.getResponseCode();

        if (logEntry.getResponseCode() == GOOD_RESPONSE_CODE) {
            allExistingEndpoints.add(logEntryPath);
        } else if (stringWrapperForResponseCode.startsWith(String.valueOf(BAD_RESPONSE_CODE_4XX))
                || stringWrapperForResponseCode.startsWith(String.valueOf(BAD_RESPONSE_CODE_5XX))) {
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

       /* Проверяем есть ли в user agent упоминание слова bot. Для этого в UserAgent class добавил
        добавил логическое поле isBot и метод проверки который строковым методом contains проверяет наличие слова bot*/
        boolean isBotEntry = logEntry.getAgent().isBot();

        if (!isBotEntry) {
            //Если запись не относится к боту то добавляем IP адрес в набор
            String logEntryIpAddr = logEntry.getIpAddr();
            uniqueSetOfIpAddresses.add(logEntryIpAddr);
            //Если запись не относится к боту то добавляем единицу к счетчику реальных запросов (сделанных людьми)
            noBotsEntriesCounterInLogFile += 1;
            //Если запись не относится к боту то добавляем время в TreeMap (для сортировки) Далее в методе меняем ключи со строк на числа
            String logEntryTime = logEntry.getTime().toString();
            if (amountOfVisitsEachSecond.containsKey(logEntryTime)) {
                amountOfVisitsEachSecond.replace(logEntryTime, amountOfVisitsEachSecond.get(logEntryTime) + 1);
            } else {
                amountOfVisitsEachSecond.put(logEntryTime, 1);
            }

            if (numberOfVisitsFoEachRealUserMap.containsKey(logEntryIpAddr)) {
                numberOfVisitsFoEachRealUserMap.replace(logEntryIpAddr, numberOfVisitsFoEachRealUserMap.get(logEntryIpAddr) + 1);
            } else {
                numberOfVisitsFoEachRealUserMap.put(logEntryIpAddr, 1);
            }
        }

        String logEntryDomain = logEntry.getDomain();
        if (logEntryDomain != null && !logEntryDomain.equals("-")) {
            domainAddresses.add(logEntryDomain);
        }

    }

    public long getTrafficRate() {
        double durationInHours = getTimeInHoursForAllEntriesInLogFile();
        return BigDecimal.valueOf(totalTraffic).divide(BigDecimal.valueOf(durationInHours), RoundingMode.HALF_UP).longValue();
    }

    //Вынес в отдельный метод подсчет времени, так как он используется в разных методах класса Statistics
    private double getTimeInHoursForAllEntriesInLogFile() {
        Duration durationBetween = Duration.between(minTime, maxTime);
        double hours = durationBetween.toHoursPart();
        double minutesToHours = (double) durationBetween.toMinutesPart() / TIME_CONSTANT;
        double secondsToHours = (double) durationBetween.toSecondsPart() / TIME_CONSTANT / TIME_CONSTANT;
        return hours + minutesToHours + secondsToHours;
    }

    //отдаю список страниц у которых код ответа 200
    public List<String> getAllUniquePagesExistingInLogFile() {
        return new ArrayList<>(allExistingEndpoints);
    }

    //Отдаём список страниц у которых коды ответов 4хх или 5хх
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

    public double getAvgNumberOfVisitsByRealUsersPerHour() {
        double durationInHours = getTimeInHoursForAllEntriesInLogFile();
        return durationInHours / noBotsEntriesCounterInLogFile;
    }

    public double getAvgNumberOfInvalidVisitsByRealUsersPerHour() {
        double durationInHours = getTimeInHoursForAllEntriesInLogFile();
        int numberOfAllInvalidEndpoints = getNumberOfInvalidRequests();
        return durationInHours / (double) numberOfAllInvalidEndpoints;
    }

    public double getAvgVisitsOfOneUniqueUser() {
        return (double) noBotsEntriesCounterInLogFile / (double) uniqueSetOfIpAddresses.size();
    }

    public int getMaxVisitsByOneUniqueUser() {
        Optional<Map.Entry<String, Integer>> maxEntry = numberOfVisitsFoEachRealUserMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        return maxEntry.get()
                .getValue();
    }

    /*Вынес в отдельный метод util метод подсчета значений Values из входящих объектов ХешМап
    Используется в методах получения общего числа замеченных браузеров и операционных систем в лог файле*/
    private static int totalCalculator(HashMap<String, Integer> inputSet) {
        int finalResult = 0;
        for (Map.Entry<String, Integer> set : inputSet.entrySet()) {
            finalResult += set.getValue();
        }
        return finalResult;
    }

    private int getNumberOfInvalidRequests() {
        return allInvalidEndpoints.size();
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

    public HashSet<String> getDomainAddresses() {
        return domainAddresses;
    }

    public int getNumberOfVisitsForParticularSecond(Integer particularSecond) {
        if (particularSecond < 0 || particularSecond > amountOfVisitsEachSecond.size())
            throw new IllegalArgumentException("Вы ввели несуществующее количество секунд");

        AtomicInteger i = new AtomicInteger();
        HashMap<Integer, Integer> map = new HashMap<>();
        if (amountOfVisitsEachSecond != null || amountOfVisitsEachSecond.size() > 0) {
            amountOfVisitsEachSecond.forEach((s, integer) -> map.put(i.getAndIncrement(), integer));
        }


        return map.get(particularSecond);
    }
}
