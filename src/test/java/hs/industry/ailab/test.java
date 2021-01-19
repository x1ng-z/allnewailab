package hs.industry.ailab;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/10 10:13
 */
public class test {
    public static void main(String[] args) {
        Pattern pvenablepattern = Pattern.compile("(^filter(\\w+)$)");

        Matcher matcher =pvenablepattern.matcher("filtermvav");
        if(matcher.find()){
            System.out.println(matcher.group(2));
        }

    }
}
