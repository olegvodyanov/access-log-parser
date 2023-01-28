import org.ovodyanov.exceptions.StringAttributeException;
import org.ovodyanov.model.LogEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String GOOGLE_BOT = "Googlebot";
    private static final String YANDEX_BOT = "YandexBot";
    private static final int MAX_LINE_LENGTH = 1024;
    private static final String REGEX = "([\\d]{1,3}[\\.][\\d]{1,3}[\\.][\\d]{1,3}[\\.][\\d]{1,3})\\s([\\-|\\s])" +
            "\\s([\\-|\\s])\\s\\[([^]]*)\\]\\s\\\"([^\\\"]*)\\\"\\s([\\d]+)\\s([\\d]+)\\s\\\"([^\\\"]*)\\\"\\s\\\"([^\\\"]*)\\\"";


    public static void main(String[] args) {
        int counterOfCorrectlyProvidedFiles = 0;
        while (true) {
            System.out.println("Пожалуйста, ведите путь к файлу ниже.");

            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists) {
                System.out.println("Файл не существует, попробуйте, пожалуйста, ещё раз.");
                continue;
            } else if (isDirectory) {
                System.out.println("Это директория, попробуйте, пожалуйста, ещё раз.");
                continue;
            } else {
                System.out.println("Путь к файлу указан верно.");
                counterOfCorrectlyProvidedFiles++;
                System.out.println("Количество верно указанных файлов равно: " + counterOfCorrectlyProvidedFiles);

                try {
                    FileReader fileReader = new FileReader(path);
                    BufferedReader reader = new BufferedReader(fileReader);
                    Statistics statistics = new Statistics();

                    String line;
                    int maxLineLength = Integer.MIN_VALUE;
                    int minLineLength = Integer.MAX_VALUE;
                    int lineCounter = 0;
                    int counterGoogleBot = 0;
                    int counterYandexBot = 0;

                    final Pattern pattern = Pattern.compile(REGEX, Pattern.MULTILINE);

                    while ((line = reader.readLine()) != null) {
                        int length = line.length();
                        if (length > maxLineLength) maxLineLength = length;
                        if (length < minLineLength) minLineLength = length;
                        lineCounter++;

                        LogEntry logEntry = new LogEntry(line);
                        statistics.addEntry(logEntry);


                        final Matcher matcher = pattern.matcher(line);

                        while (matcher.find()) {
                            String userAgent = matcher.group(9);
                            if (userAgent.contains(GOOGLE_BOT)) {
                                counterGoogleBot++;
                            } else if (userAgent.contains(YANDEX_BOT)) {
                                counterYandexBot++;
                            }
                        }

                        if (maxLineLength > MAX_LINE_LENGTH)
                            throw new StringAttributeException("Длина строки не может превышать 1024 символа. " +
                                    "Длина найденной строки " + maxLineLength);
                    }

                    double gResult = ((double) counterGoogleBot / (double) lineCounter) * 100;
                    double yResult = ((double) counterYandexBot / (double) lineCounter) * 100;
                    System.out.println("Доля запросов Google " + gResult);
                    System.out.println("Доля запросов Yandex " + yResult);
                    //Для отладки
                    System.out.println();
                    System.out.println("+++++++++++++++++Отладка начало++++++++++++++++");
                    System.out.println("Min time " + statistics.getMinTime());
                    System.out.println("Max time " + statistics.getMaxTime());
                    System.out.println("Total traffic " + statistics.getTotalTraffic());
                    System.out.println("Traffic rate " + statistics.getTrafficRate());
                    System.out.println("Avg number of visits by real users per hour " + statistics.getAvgNumberOfVisitsByRealUsersPerHour());
                    System.out.println("Avg number of invalid visits by real users per hour " + statistics.getAvgNumberOfInvalidVisitsByRealUsersPerHour());
                    System.out.println("Avg visits of one unique user " + statistics.getAvgVisitsOfOneUniqueUser());
                    System.out.println("Browser proportion statistics " + statistics.getBrowserProportionStat());
                    System.out.println("OS type proportion statistics " + statistics.getOsTypeProportionStat());
                    System.out.println("Visits for particular second " + statistics.getNumberOfVisitsForParticularSecond(5));
                    System.out.println("Number of valid pages with code 2xx " + statistics.getAllUniquePagesExistingInLogFile().size());
                    System.out.println("Number of invalid pages with code 4xx or 5xx " + statistics.getAllUniquePagesInvalidInLogFile().size());
                    System.out.println("Max number of visits by one unique users " + statistics.getMaxVisitsByOneUniqueUser());
                    System.out.println("+++++++++++++++++Отладка конец++++++++++++++++");


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}