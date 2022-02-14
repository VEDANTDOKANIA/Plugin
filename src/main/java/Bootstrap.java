import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;


public class Bootstrap {
    public long time=40000 ;

    public static void main(String[] args) throws IOException, InterruptedException {
        final Bootstrap test = new Bootstrap();

        Path path = Paths.get("src/main/java/configtemp.json");
       // byte[] data = Files.readAllBytes(path);
        String value = String.valueOf(Files.readAllLines(path));
        JSONObject jsonObject = new JSONObject(value.substring(1));// FIle se json object read ho raha hain
        //System.out.println(jsonText);

        HashMap<String, Object> configmap = new HashMap<>();
        configmap = (HashMap<String, Object>) jsonObject.toMap();


        ArrayList<HashMap> arrayList = new ArrayList<>((ArrayList<HashMap<String, Object>>) configmap.get("1"));
        ArrayList<HashMap> temparraylist =  new ArrayList<HashMap>();
        for(int i =0 ;i< arrayList.size();i++)
        {
            temparraylist.add(i, (HashMap) arrayList.get(i).clone());
        }
        //ArrayList<HashMap> originallist = new ArrayList<>(arrayList);

       // String base64encoder = (Base64.getEncoder().encodeToString(data));
        //System.out.println(base64encoder);
        int i = 0;
        while (i >= 0) {
            Timer timer = new Timer();
           Thread.sleep(10000);

            for (int j = 0; j < temparraylist.size(); j++) {
                int time = (int) temparraylist.get(j).get("Scheduled_Time");
                int timeleft = time-1000;

                if(timeleft==0){
                    //Callfunction
                    //String jsonText = JSONObject.valueToString(jsonObject.get("1"));
                    JSONObject jsonObject1 = new JSONObject();
                    HashMap<String,Object> temporary = (HashMap<String, Object>) arrayList.get(j).clone();
                    temporary.remove("Scheduled_Time");
                    List<HashMap> listofmaps = new ArrayList<>();
                    listofmaps.add(temporary);
                    jsonObject1.put("1",listofmaps);
                    System.out.println(jsonObject1);
                    String base64encoder = (Base64.getEncoder().encodeToString((jsonObject1).toString().getBytes(StandardCharsets.UTF_8)));
                    callfunction(base64encoder);

                    //System.out.println("FUnctioned Called");
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


        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    callfunction(base64encoder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,test.time);
    }*/




    }
    public static void callfunction(String encodedstring) throws IOException {
        try{
            Process pb = new ProcessBuilder("src/main/java/Pluginnew.exe",encodedstring).start();
            //pb.wait();
            InputStream processInputStream = pb.getInputStream();
            //pb.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
            String line ;
            String[] outputarray = new String[9];
            int i =0;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                System.out.println(LocalTime.now());

            }

            //JSONObject jsonObject = new JSONObject(outputarray[1].toString().substring(1));
            //System.out.println(outputarray[0]);
        }

        catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }
    }
}
