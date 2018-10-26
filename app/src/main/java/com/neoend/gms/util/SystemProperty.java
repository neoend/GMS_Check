package com.neoend.gms.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SystemProperty {
    private String[] propsArray;
    private ArrayList<String> propsArrayList = new ArrayList<String>();
    private HashMap<String, String> properties = new HashMap<String, String>();

    public SystemProperty() {
        readProps();
        parseProps();
    }

    public String get(String key) {
        String prop = properties.get(key);

        if (prop == null) {
            prop = "";
        }

        return prop;
    }

    private void readProps() {
        String propString = SystemCommandJNI.getInstance().execute("/system/bin/getprop");
        if (propString == null) {
            return;
        }

        propsArray = propString.split("\\r?\\n");
    }

    private void readFile() {
        File file = new File("/sdcard/getprop.txt");

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                propsArrayList.add(line);
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    } // readFile()

    private void parseProps() {
        for (String line : propsArray) {
            line = line.replace("]: [", "@");
            String[] parse = line.split("@");

            switch(parse.length) {
                case 2:
                    parse[0] = parse[0].replace("[", " ");
                    parse[0] = parse[0].replaceAll("\\s+", "");
                    parse[1] = parse[1].replace("]", " ");
                    properties.put(parse[0], parse[1]);
                    Log.i("TAG", parse[0] + " : " + parse[1]);
                    break;
                case 1:
                    parse[0] = parse[0].replace("[", " ");
                    parse[0] = parse[0].replaceAll("\\s+", "");
                    properties.put(parse[0], "");
                    Log.i("TAG", parse[0] + " : ");
                    break;
                case 0:
                    break;
                default:
                    break;
            }
        }
    } // parseProps()
}
