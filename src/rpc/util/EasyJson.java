package rpc.util;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EasyJson {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
    
    static public Object String2Json(String jsonstr) throws JSONException {
        if(jsonstr==null || jsonstr.isEmpty()){
            return null;
        }
    
        if (jsonstr.startsWith("{")) {
            return new JSONObject(jsonstr);
        }
        if (jsonstr.startsWith("[")) {
            return new JSONArray(jsonstr);
        }
        
        // cannot determine if is json array or json object. try one by one
        try {
            return new JSONObject(jsonstr);
        } catch (JSONException e) {
            return new JSONArray(jsonstr);
        }
    }
    
    
    /**
     * mix json_a value base on json_b
     * @param json_a
     * @param json_b
     * @return the mixed object
     * @throws JSONException 
     */
    static public Object mixJson(Object json_a, Object json_b) throws JSONException {
        if (json_a == null) {   return json_a; }
        if (json_b == null) {   return json_a; }
        
        if (json_a instanceof JSONArray && json_b instanceof JSONArray) {
            return mixJsonArray((JSONArray)json_a, (JSONArray)json_b);
        }
        if (json_a instanceof JSONObject && json_b instanceof JSONObject) {
            return mixJsonObject((JSONObject)json_a, (JSONObject)json_b);
        }
        
        // 不同类型的json对象 或者两者都是基本类型，直接覆盖
        return json_a;
    }
    
    /**
     * 对数组逐项元素混合
     * @param json_a
     * @param json_b
     * @return
     * @throws JSONException 
     */
    static public JSONArray mixJsonArray(JSONArray json_a, JSONArray json_b) throws JSONException {
        //System.out.println("mix json array" + json_a.toString());
        if (json_a.length() < json_b.length()) {
            JSONArray json_mix = json_b;
            for (int i = 0; i<json_a.length(); ++i) {
                json_mix.put(i, mixJson(json_a.get(i), json_b.get(i)));
            }
            return json_mix;
        } else {
            JSONArray json_mix = json_a;
            for (int i = 0; i<json_b.length(); ++i) {
                    json_mix.put(i, mixJson(json_a.get(i), json_b.get(i)));
            }
            return json_mix;
        }
        
    }
    
    /**
     * 对json object，按成员逐项混合。
     * @param json_a
     * @param json_b
     * @return
     * @throws JSONException
     */
    static public JSONObject mixJsonObject(JSONObject json_a, JSONObject json_b) throws JSONException {
        //System.out.println("mix json object" + json_a.toString());
        JSONObject json_mix = json_b;
        String key = null;
        for (Iterator<String> iter = json_a.keys();iter.hasNext();) {
            key = iter.next();
            json_mix.put(key, mixJson(json_a.get(key), json_b.opt(key)));
        }
        return json_mix;
    }

}
