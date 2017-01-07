import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Search {

    Calendar c;
    File f;
   // BufferedWriter f_output;
    FileOutputStream f_output;
    int last_user;
    int current_year;
    int start_year = 0;
    int finish_year = 0;
    int sex = 0;
    int depth = 2;
    boolean no_access_token_mode = false;
    final String[] args;
    final String access_token;
    final static String[] access_tokens = {
            "c8a89a78d018027bf56cd096294618170f6464136d1e1d6e552ef9046ca30ad190cb8356770570b746e01"
    };
    HashMap<Integer,User> users;
    static Object[][] cities = {
            {1,"Санкт-Петербург"},
            {2,"Москва"},
            {60,"Казань"},
            {109,"Пенза"},
            {123,"Самара"}
    };

    public Search(String access_token,String[] args){
        super();
        try {
            f = new File("users.txt");
            f.setReadable(true);
            f.setWritable(true);
            f_output = new FileOutputStream(f);
          //  f_output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
        }catch(Throwable t){
            System.err.println(t.toString());
        }
        this.args = args;
        this.access_token = access_token;
        this.users = new HashMap<Integer,User>();
        c = Calendar.getInstance();
        current_year = c.get(Calendar.YEAR);
    }

    public void writeInFile(User u){
        String s = u.toString();
        try {
            f_output.write(Integer.toString(++last_user).getBytes());
            f_output.write("   ".getBytes());
            f_output.write(s.getBytes(Charset.forName("UTF-8")));
            f_output.write(System.getProperty("line.separator").getBytes());
            f_output.flush();
            //System.out.println("write in file "+s);
        }catch(Throwable t){
            //System.out.println("write in file "+s+" failed");
        }
    }

    public int addUser(User u){
        users.put(u.id,u);
        if (u.bdate.contains(".")) {
            String[] t = u.bdate.split(".");
            if (t.length == 3) {
                int year = Integer.parseInt(t[2]);
                if (year<start_year || year>finish_year) return 1;
            }else{
                return 1;
            }
        }
        if (sex!=0)
            if (u.sex!=sex) return 1;
        if (u.city.equalsIgnoreCase(args[1])){
            if (u.phone.startsWith("+")){
                writeInFile(u);
            }else{
                try{
                    Long.parseLong(u.phone);
                    writeInFile(u);
                }catch(Throwable t){}
            }
        }
        return 0;
    }

    public int getSex() {
        if (args.length>4)
            return Integer.parseInt(args[4]);
        return 0;
    }

    public int getDepth(){
        if (args.length>5)
            return Integer.parseInt(args[5]);
        return 2;
    }

    public int getStartYear(String[] args){
        int i = Integer.parseInt(args[3]);
        return current_year-i-1;
    }

    public int getFinishYear(String[] args){
        int i = Integer.parseInt(args[2]);
        return current_year-i-1;
    }

    int last_token;

    public String nextToken(){
        String s = access_tokens[last_token];
        last_token++;
        if (last_token>=access_tokens.length)
            last_token=0;
        return s;
    }

    public void buildTree(int id,final int level,final int max_level){
        try {
            String s = null;
            if (no_access_token_mode)
                s = request("https://api.vk.com/method/friends.get?user_id="+id+ "&fields=contacts,bdate,sex,city");
            else
                s = request("https://api.vk.com/method/friends.get?user_id="+id+ "&access_token="+nextToken()+   "&fields=contacts,bdate,sex,city");
            //System.out.println("response : "+s);
            JSONObject root = new JSONObject(s);
            if (root.has("response")){
                JSONArray response = root.getJSONArray("response");
                System.out.println("["+level+"]: received "+response.length()+" friends of user "+id);
                for (int i = 0; i<response.length(); i++){
                    User u = User.parse(response.getJSONObject(i));
                    if (!users.containsKey(u.id)) {
                        //if (((!u.bdate.equalsIgnoreCase("bdate unknown")) && (!u.phone.equalsIgnoreCase("phone unkown")))
                              //  && ((!u.bdate.equalsIgnoreCase("bdate unknown")) && (!u.phone.equalsIgnoreCase("phone unkown")))) {
                            addUser(u);
                            if (level<max_level) {
                                if (!no_access_token_mode)
                                    Thread.sleep(360);
                                buildTree(u.id, level + 1, max_level);
                            }
                       // }
                    }
                }
            }else{
                System.out.println("NO RESPONSE : "+s);
            }
        }catch(Throwable t){
            System.err.println("ERROR IN TREE BUILDING FOR USER "+id+" ON LEVEL "+level+" : " + t.toString());
        }
    }


    public void start(){
        System.out.println("SEARCH STARTED");

        start_year = getStartYear(args);
        finish_year = getFinishYear(args);
        sex = getSex();
        depth = getDepth();
        String town = args[1];
        System.out.println("START YEAR:"+start_year+"  FINISH YEAR:"+finish_year);

        buildTree(Integer.parseInt(args[0]),1,depth);
       /* float year_weight = 1f/(finish_year - start_year + 1);
        float year_progress = 0;
        float month_progress = 0;
        System.out.println("year_weight = "+year_weight);
        for (int i = start_year;i<=finish_year;i++){
            year_progress = (i-start_year)*year_weight;
            for (int j = 0;j<12;j++){
                month_progress = ((float)j)/12;
                search("a",town,i,j,1000,0);
                System.out.println(year_progress+"   "+ month_progress+"    percent = "+ ( year_progress+month_progress*year_weight)*100  );
                try {
                    Thread.sleep(350);
                }catch(Throwable t){}
            }
        }
        //search(args[0],town,start_year,1,1000,0);*/
        System.out.println("\nSEARCH FINISHED");
        //dump_users(System.out);
    }

    public void dump_users(OutputStream out){
        users.size();
        final Collection<User> c = users.values();
        try {
            out.write(("DUMP USERS : "+c.size()).getBytes());
            out.flush();
        }catch(Throwable t){}
        for ( User u : c){
            String s = new StringBuilder()
                    .append("id=").append(u.id).append("  ")
                    .append("name=").append(u.name[0]).append(" ").append(u.name[1]).append(" ").append(u.name[2]).append("  ")
                    .append("birthday=").append(u.bdate).append("  ")
                    .append("city=").append(u.city).append("  ")
                    .append("phone=").append(u.phone).toString();
            try {
                out.write(s.getBytes(Charset.forName("UTF-8")));
                out.flush();
            }catch(Throwable t){}
        }
        try {
            out.write(("DUMP USERS FINISHED : "+c.size()).getBytes());
            out.flush();
        }catch(Throwable t){}
    }

    public int search(String q,String town,int year,int month,int count,int offset){
        return search_request(new StringBuilder()
                .append("q=").append(q)
                //.append("&hometown=").append(town)
                //.append("&birth_year=").append(year)
                //.append("&birth_month=").append(month)
                .append("&count=").append(count)
                //.append("&offset=").append(offset)
                .toString());
    }


    public int search_request(String args){
        try {
            String request_url = "https://api.vk.com/method/users.search?access_token=" + access_token + "&" + args;
            String s = request(request_url);
            System.out.println("request : "+request_url);
            System.out.println("response: "+s);
            JSONObject root = new JSONObject(s);
            JSONArray response = root.getJSONArray("response");
            int length = response.length();
            for (int i = 1;i<length;i++){
                User u = User.parse(response.getJSONObject(i));
                if (!users.containsKey((Integer)u.id)){
                    users.put((Integer)u.id,u);
                }else{
                    System.out.println("hash map already contains user with id "+u.id);
                }
            }
            //System.out.println("RESPONSE LENGTH:"+response.length());
        }catch(Throwable t){
            System.err.println(t.toString());
        }
        return 0;
    }


    public static String request(String url) throws Throwable{
        InputStream is = requestIS(url);
        //System.out.println("request started");
        Scanner s = new Scanner(is,"UTF-8");
        StringBuilder sb = new StringBuilder();
        while (s.hasNextLine())
            sb.append(s.nextLine());
        is.close();
        final String st = sb.toString();
        //System.out.println("request finished: "+st);
        return st;
    }


    public static InputStream requestIS(String url){
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            return connection.getInputStream();
        }catch (Throwable t){
            System.err.println(t.toString());
            return null;
        }
    }


    public static String getToken(){
        return "c8a89a78d018027bf56cd096294618170f6464136d1e1d6e552ef9046ca30ad190cb8356770570b746e01";
    }

    public static void main_android(String[] args){
        new Search(getToken(),args).start();
    }

    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        System.out.println();
        print_cities();
        System.out.println("Введите номер города : ");
        String city = s.nextLine();
        System.out.println("Введите минимальный возраст : ");
        String min_age = s.nextLine();
        System.out.println("Введите максимальный возраст : ");
        String max_age = s.nextLine();
        System.out.println("Введите пол ( 1=женщина; 2=мужчина; 0=любой ) : ");
        int sex = s.nextInt();
        System.out.println("Введите глубину поиска ( от 1 до 5 ) : ");
        int depth = s.nextInt();
        System.out.println("Введите ID стартового пользователя : ");
        int id_start = s.nextInt();
        new Search(getToken(),new String[]{id_start+"", city, min_age, max_age,sex+"",depth+""}).start();
    }

    public static void print_cities(){
        System.out.println("  Номер города  |   Название города   ");
        for (int i =0;i<cities.length;i++){
            System.out.println("      "+((int)cities[i][0]) +"             "+((String)cities[i][1]));
        }
        System.out.println();
    }

}
