import org.ovodyanov.exceptions.StringAttributeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

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

                    while ((line = reader.readLine()) != null) {
                        int length = line.length();
                        if (length > maxLineLength) maxLineLength = length;
                        if (length < minLineLength) minLineLength = length;
                        lineCounter++;

                        if (maxLineLength > 1024)
                            throw new StringAttributeException("Длина строки не может превышать 1024 символа. " +
                                    "Длина найденной строки " + maxLineLength);
                    }


                    System.out.println("maxLineLength " + maxLineLength);
                    System.out.println("minLineLength " + minLineLength);
                    System.out.println("counter " + lineCounter);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}