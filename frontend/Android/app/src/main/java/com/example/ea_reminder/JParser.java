package com.example.ea_reminder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class JParser {
    /*
    public void boardinfoParse(JSONObject js, ArrayList<String> name, ArrayList<String> time) throws ParseException{
        String jstr = js.toString();
        JSONParser jpar = new JSONParser();
        JSONObject list = (JSONObject)jpar.parse(jstr);
        JSONArray aList = (JSONArray)list.get("list");
        for(int i=0;i<aList.size();i++){
            JSONObject temp = (JSONObject)aList.get(i);
            String title = (String)temp.get("title");
            String day = (String)temp.get("day");
            String stime = (String)temp.get("start_time");
            name.add(title);
            time.add(day+" "+stime);
        }
    }*/
    public void boardinfoParse(String jstr, ArrayList<String> name, ArrayList<String> time) throws ParseException, RuntimeException {
        JSONParser jpar = new JSONParser();
        JSONObject list = (JSONObject)jpar.parse(jstr);
        JSONArray aList = (JSONArray)list.get("list");
        if(aList!=null){
            for(int i=0;i<aList.size();i++){
                JSONObject temp = (JSONObject)aList.get(i);
                String title = (String)temp.get("title");
                String day = (String)temp.get("day");
                String stime = (String)temp.get("start_time");
                name.add(title);
                time.add(day+" "+stime);
            }
        }
        else{
            System.out.println("JSON Parsing failed using 'list'");
            throw new RuntimeException();
        }
    }
}
