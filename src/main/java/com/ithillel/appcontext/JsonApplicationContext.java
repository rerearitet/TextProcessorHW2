package com.ithillel.appcontext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.ithillel.service.InMemoryTextProcessor;
import com.ithillel.service.Storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonApplicationContext implements ApplicationContext {

    private static final Map<String, Object> beans = new HashMap<>();
    private static final JSONParser jsonParser = new JSONParser();

    public static void main(String[] args) {
        JsonApplicationContext jap = new JsonApplicationContext();
        ((InMemoryTextProcessor) jap.getBean("textProcessor")).save("1", "2");
        System.out.println(((InMemoryTextProcessor) jap.getBean("textProcessor")).getByKey("1"));
    }

    public JsonApplicationContext() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("application.json"))) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(inputStreamReader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("beans");

            for (Object obj : jsonArray) {
                JSONObject jsonObj = (JSONObject) obj;
                String name = (String) jsonObj.get("name");

                if (name.equals("storage")) {
                    beans.put(name, Class.forName((String) jsonObj.get("type")).getDeclaredConstructor().newInstance());
                } else {
                    String s = (String) ((JSONArray) jsonObj.get("constructorArgs")).get(0);
                    beans.put(name, Class.forName((String) jsonObj.get("type")).getDeclaredConstructor(Storage.class).newInstance(getBean(s))
                    );
                }
            }
        } catch (IOException | ParseException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String name) {
        return beans.get(name);
    }
}