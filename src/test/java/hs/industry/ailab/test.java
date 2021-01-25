package hs.industry.ailab;

import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/10 10:13
 */
public class test {
    public static void main(String[] args) {

        JSONObject data=new JSONObject();
        JSONObject user=new JSONObject();
        user.put("id",1);
        user.put("name","zzx");
        user.put("value",3.2);
        data.put("user",user);
        System.out.println(data.toJSONString());
        Pattern pvenablepattern = Pattern.compile("(^filter(\\w+)$)");

        Matcher matcher =pvenablepattern.matcher("filtermvav");
        if(matcher.find()){
            System.out.println(matcher.group(2));
        }

    }
}
