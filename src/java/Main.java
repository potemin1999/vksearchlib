/**
 * Created by Илья on 07.01.2017.
 */
import org.json.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Throwable{
        while (true) {
            System.out.println("Выберите задачу");
            System.out.println(" 1 - создание базы данных");
            System.out.println(" 2 - отправка СМС");
            System.out.print("номер задачи: ");
            Scanner s = new Scanner(System.in);
            int i;
            while (true) {
                try {
                    i = s.nextInt();
                    break;
                } catch (Throwable t) {
                }
            }
            switch (i) {
                case 1: {
                    Search.main(args);
                    break;
                }
                case 2: {
                    SmsManager.main(args);
                    break;
                }
            }
        }
    }

}
