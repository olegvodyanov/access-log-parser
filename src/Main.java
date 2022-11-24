import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int firstNumber = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int secondNumber = new Scanner(System.in).nextInt();

        int sum = firstNumber + secondNumber;
        int subtraction = firstNumber - secondNumber;
        int multiplication = firstNumber * secondNumber;
        double division = (double) firstNumber / (double) secondNumber;


        System.out.println("Сумма: " + sum);
        System.out.println("Разность: " + subtraction);
        System.out.println("Произведение: " + multiplication);
        System.out.println("Частное: " + division);
    }
}