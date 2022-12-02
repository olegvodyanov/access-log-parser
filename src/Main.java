import java.io.File;
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
            }
        }
    }
}