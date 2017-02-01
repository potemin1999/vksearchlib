import org.json.JSONObject;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by Ilya on 04.01.2017.
 */

public class User {

    public User(int id,String[] name,String phone,String bdate,String city,int sex){
        super();
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.bdate = bdate;
        this.city = city;
        this.sex = sex;
    }

    public boolean isFriendsReceived = false;
    int id;
    int sex;
    String bdate;
    String city;
    String[] name = new String[3];
    String phone;

    public static User parse(JSONObject o){
        User u = new User(o.getInt("uid"),
                new String[]{o.getString("first_name"),o.getString("last_name"),""},
                (o.has("mobile_phone") ? o.getString("mobile_phone") : "unknown"),
                (o.has("bdate") ? o.getString("bdate") : "unknown"),
                (o.has("city") ? o.getInt("city")+"" : "unknown"),
                (o.has("sex") ? o.getInt("sex") : -1)
        );
        return u;
    }

    public String toString(){
        return new StringBuilder().append("id='").append(id).append("'    '").append(name[0]).append("' '").append(name[1]).append("' ").append(name[2]).append("    '")
                .append(bdate).append("'      '").append(city).append("'      '").append(phone).append("' \n").toString();
    }

    public void writeInStream(OutputStream os){
        String id = this.id+"";
        String n1 = name[0];
        String n2 = name[1];
        String n3 = name[2];
        String bdate = this.bdate;
        String city = this.city;
        String phone = this.phone;
        char[] buffer = new char[108];
        for (int i = 0 ; i<buffer.length;i++){
            buffer[i] = ' ';
        }
        writeStringOnPosition(id,0,buffer);
        writeStringOnPosition(n1,14,buffer);
        writeStringOnPosition(n2,32,buffer);
        writeStringOnPosition(bdate,56,buffer);
        writeStringOnPosition(city,70,buffer);
        writeStringOnPosition(phone,80,buffer);
        try {
            os.write(new String(buffer).getBytes(Charset.forName("UTF-8")));
        }catch(Throwable t){}
       // os.write_char_array(buffer,0,buffer.length);
    }

    public void writeStringOnPosition(String s,int position,char[] buffer){
        final char[] write = s.toCharArray();
        if (position+write.length>buffer.length-1) return;
        for (int i = 0; i<write.length;i++){
            buffer[i+position] = write[i];
        }
    }

}
