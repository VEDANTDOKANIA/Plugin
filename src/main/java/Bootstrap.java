import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.vertx.core.json.JsonObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;


public class Bootstrap {

    public static HashMap<String,Object> SimpleJson(String FileValue){
        JSONObject jsonObject = new JSONObject(FileValue.substring(1));// FIle se json object read ho raha hain
        HashMap<String,Object> configmap = (HashMap<String, Object>) jsonObject.toMap();
        return configmap;

         /*JSONObject jsonObject = new JSONObject(FileValue.substring(1));// FIle se json object read ho raha hain
        HashMap<String,Object> configmap = (HashMap<String, Object>) jsonObject.toMap();*/

    }

    public static HashMap<String,Object> Vertex(String FileValue){
        HashMap<String,Object> ConfigMap = new HashMap<>();
        JsonObject jsonObjectvertex = new JsonObject(FileValue.substring(1,FileValue.length()-1));

        for (Map.Entry<String, Object> entry : jsonObjectvertex.getMap().entrySet()) {
            ConfigMap.put(entry.getKey(), entry.getValue() != null ? entry.getValue() : null);
        }
        return ConfigMap;

        //JsonObject jsonObjectvertex = new JsonObject(FileValue.substring(1,FileValue.length()-1));

        /*for (Map.Entry<String, Object> entry : jsonObjectvertex.getMap().entrySet()) {
            ConfigMap.put(entry.getKey(), entry.getValue() != null ? entry.getValue() : null);
        }*/

    }

    public static HashMap<String,Object> Jackson(String FileValue){
        HashMap<String,Object> ConfigMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ConfigMap = (HashMap<String, Object>) objectMapper.readValue(FileValue.substring(1),Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ConfigMap;

    }



    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        Path path = Paths.get("src/main/java/config.json");
        String FileValue = String.valueOf(Files.readAllLines(path));
        HashMap<String,Object> ConfigMap;
        ConfigMap = new HashMap<>();

        //Using Simple.json
        ConfigMap = SimpleJson(FileValue);

        //Using Vertex
        //ConfigMap = Vertex(FileValue);

        //Using Jackson
        //ConfigMap = Jackson(FileValue);


        //Using Gson

       /* Gson gson = new Gson();
        ConfigMap = (HashMap<String, Object>) gson.fromJson(FileValue.substring(1,FileValue.length()-1),HashMap.class);
        System.out.println(ConfigMap);
        ArrayList<LinkedTreeMap<String, Object>> arrayList = new ArrayList<>();
        arrayList = (ArrayList<LinkedTreeMap<String, Object>>) ConfigMap.get("1");
        ArrayList<HashMap> temparraylist = new ArrayList<>();
        for(int i =0 ;i<arrayList.size();i++)
        {
            LinkedTreeMap<String,Object> tempmap = arrayList.get(i);
            HashMap<String,Object> temphashmap = new HashMap<>();
            String[] keyset = tempmap.keySet().toArray(new String[0]);
            for(int j=0;j< keyset.length;j++)
            {
                temphashmap.put(keyset[j],tempmap.get(keyset[j]));
            }
             temparraylist.add(temphashmap);

        }
        ArrayList<HashMap<String,Object>> arrayListnew = new ArrayList<>();
        for(int i=0;i< temparraylist.size();i++){
            arrayListnew.add(temparraylist.get(i));
        }*/








        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<>((ArrayList<HashMap<String, Object>>) ConfigMap.get("1"));
        ArrayList<HashMap> temparraylist = new ArrayList<>();
        for(int i =0 ;i< arrayList.size();i++)
        {
            temparraylist.add(i, (HashMap) arrayList.get(i).clone());
        }

        int i = 0;
        while (i >= 0) {

           Thread.sleep(10000);

            for (int j = 0; j < temparraylist.size(); j++) {
                int time = (int) temparraylist.get(j).get("Scheduled_Time");
                //int time = (int) Math.round((Double) temparraylist.get(j).get("Scheduled_Time")); //for gsononly
                int timeleft = time-1000;

                if(timeleft==0){
                    //Callfunction

                    JSONObject MapJsonObject = new JSONObject();
                    HashMap<String,Object> temporary = (HashMap<String, Object>) arrayList.get(j).clone();
                    //HashMap<String,Object> temporary = (HashMap<String, Object>) arrayListnew.get(j).clone();//For gson only
                    temporary.remove("Scheduled_Time");
                    List<HashMap> listofmaps = new ArrayList<>();
                    listofmaps.add(temporary);
                    MapJsonObject.put("1",listofmaps);
                    System.out.println(MapJsonObject);
                    String base64encoder = (Base64.getEncoder().encodeToString((MapJsonObject).toString().getBytes(StandardCharsets.UTF_8)));
                    Pluginfunction(base64encoder);
                    //initial value return kardena hain

                    temparraylist.add(j,arrayList.get(j));
                    temparraylist.remove(j+1);
                }
                else{
                    //time minus karna hain original time se
                    HashMap<String,Object> tempmap = new HashMap<>();
                    tempmap=temparraylist.get(j);
                    tempmap.put("Scheduled_Time",timeleft);
                    temparraylist.add(j,tempmap);
                    temparraylist.remove(j+1);
                }
            }

        }
    }
    public static void Pluginfunction(String encodedstring)  {
        try{
            Process pb = new ProcessBuilder("src/main/java/Plugin.exe",encodedstring).start();
            InputStream processInputStream = pb.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
            String line ;

            while ((line = reader.readLine()) != null) {
                JsonObject jsonObject = new JsonObject(line);
                System.out.println(jsonObject);
                System.out.println(LocalTime.now());

            }


        }

        catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }
    }
}
