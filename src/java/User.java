import org.json.JSONObject;

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
        return new StringBuilder().append(id).append("    ").append(name[0]).append(" ").append(name[1]).append(" ").append(name[2]).append("    ")
                .append(bdate).append("      ").append(city).append("      ").append(phone).append("\n").toString();
    }

}
