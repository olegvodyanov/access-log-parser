import org.ovodyanov.exceptions.StringAttributeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
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

                    String line;
                    int maxLineLength = Integer.MIN_VALUE;
                    int minLineLength = Integer.MAX_VALUE;
                    int lineCounter = 0;
                    int counterGoogleBot = 0;
                    int counterYandexBot = 0;

                    final String regex = "([\\d]{1,3}[\\.][\\d]{1,3}[\\.][\\d]{1,3}[\\.][\\d]{1,3})\\s([\\-|\\s])\\s([\\-|\\s])\\s([\\[].+[\\]])\\s(\\\".+\\\")\\s([\\d]+)\\s([\\d]+)\\s(\\\".+\\\")\\s(\\\".+\\\")";
                    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

                    while ((line = reader.readLine()) != null) {
                        int length = line.length();
                        if (length > maxLineLength) maxLineLength = length;
                        if (length < minLineLength) minLineLength = length;
                        lineCounter++;

                        final Matcher matcher = pattern.matcher(line);

                        while (matcher.find()) {
                            String userAgent = matcher.group(9);

                            if (userAgent.contains("Googlebot")) {
                                counterGoogleBot++;
                            } else if (userAgent.contains("YandexBot")) {
                                counterYandexBot++;
                            }
                        }

                        if (maxLineLength > 1024)
                            throw new StringAttributeException("Длина строки не может превышать 1024 символа. " +
                                    "Длина найденной строки " + maxLineLength);
                    }

                    double gResult = ((double) counterGoogleBot / (double) lineCounter) * 100;
                    double yResult = ((double) counterYandexBot / (double) lineCounter) * 100;
                    System.out.println("Доля запросов Google " + gResult);
                    System.out.println("Доля запросов Yandex " + yResult);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}