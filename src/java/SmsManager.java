import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Илья on 16.01.2017.
 */
public class SmsManager {

    static String[] messages = null;

    public static void main(String[] args){
        File f = new File(Search.main_file);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        }catch(FileNotFoundException fnfe){
            System.out.println("файл пользователей не найден: "+fnfe.toString());
            return;
        }
        File f2 = new File("messages_text.txt");
        FileInputStream fis2 = null;
        try {
            fis2 = new FileInputStream(f2);
        }catch(FileNotFoundException fnfe){
            System.out.println("напишите сообщения в файле message_text.txt");
            return;
        }

        Scanner s = new Scanner(System.in);
        Scanner fs = new Scanner(fis,"UTF-8");
        Search.users.clear();
        int errors = 0;
        while (fs.hasNextLine()){
            String ss = "NULL";
            try {
                ss = fs.nextLine();
                char[] c = ss.toCharArray();
                String[] sss = new String[16];
                StringBuilder sb = new StringBuilder();
                boolean wait_for_new_string = true;
                int last_sss_i = 0;
                for (int i = 0;i<c.length;i++){
                    if (c[i]==' '){
                        if (!wait_for_new_string) {
                            sss[last_sss_i++] = sb.toString();
                            sb.delete(0, sb.length());
                            wait_for_new_string = true;
                        }
                    }else{
                        sb.append(c[i]);
                        wait_for_new_string = false;
                    }
                }
                String id = sss[1];
                User u = new User(Integer.parseInt(id), new String[]{sss[2], sss[3], ""}, sss[6], sss[5], sss[4], 0);
                if (!Search.users.containsKey(u.id))
                    Search.users.put(u.id,u);
            }catch(Throwable t){
                System.err.println("ошибка в разборе строки: \n   "+ss+"причина:\n   "+t.toString());
            }
        }
        System.out.println("список пользователей загружен, ошибок:" + errors + ", пользователей к обработке:" + Search.users.size());
        String message = getFile(fis2,"UTF-8");
        if (message.contains("|"))
            messages = message.split("|");
        else
            messages = new String[]{message};
        System.out.println("текст сообщений загружен: " + messages.length + " шаблонов");
        prepare_messages();

        dump_users();
        dump_messages(messages);

        send_messages();
    }


    public static void prepare_messages(){
        for (String s : messages){
            if (s.endsWith("\n")) s = s.substring(0,s.length()-2);
            s = s.trim();
        }
    }

    public static void send_messages(){
        for (User u : Search.users.values()){
            try{

            }catch(Throwable t){}
        }
    }




    public void send_message(String number,String text){

    }














    public static String getFile(FileInputStream fis,String charset_name){
        try {
            ArrayList<byte[]> arr = new ArrayList<byte[]>();
            while (true) {
                arr.add(new byte[512]);
                byte[] b = arr.get(arr.size() - 1);
                int a = -2;
                try {
                    a = fis.read(b, 0, 1024);
                } catch (Throwable t) {
                }
                if (a < 0) {
                    arr.remove(arr.size() - 1);
                    break;
                }
            }
            byte[][] arr_b = new byte[arr.size()][];
            arr_b = arr.toArray(arr_b);
            byte[] arr_ch = implode(arr_b);
            return new String(arr_ch, Charset.forName(charset_name));
        }catch(Throwable t){
            System.err.println("in function getFile() : "+t.toString());
        }
        return null;
    }

    public static byte[] implode(byte[][] bytes){
        int length = 0;
        for (byte[] b : bytes)
            length += b.length;
        byte[] a = new byte[length];
        int last_i = 0;
        for (byte[] b : bytes){
            for (int i = 0; i<b.length ; i++)
                a[last_i++] = b[i];
        }
        return a;
    }

    public static void dump_users(){
        for (User u : Search.users.values()){
            System.out.print(u.toString());
        }
    }

    public static void dump_messages(String[] messages){
        for (String s : messages ){
            System.out.println("'"+s+"'");
        }
    }

}
