package cubes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Configs {
        public static HashMap<String, String> options = new HashMap<>();

        static {
                try {
                        StringBuilder builder = new StringBuilder();
                        BufferedReader reader = new BufferedReader(new FileReader("configs.txt"));
                        String read;
                        while ((read = reader.readLine()) != null) {
                                builder.append(read + "\n");
                        }

                        read = builder.toString();

                        String key = new String();
                        String value = new String();

                        String lastSegment = new String();
                        boolean isOptionLine = false;

                        for (int i = 0; i < read.length(); i++){
                                if (read.charAt(i) == '\n') {
                                        value = lastSegment;
                                        lastSegment = new String();
                                        if (isOptionLine) {
                                                options.put(key, value);
                                        }
                                        isOptionLine = false;
                                }
                                if (read.charAt(i) == ' ' && read.charAt(i + 1) == '=' && read.charAt(i + 2) == ' '){
                                        key = lastSegment;
                                        lastSegment = new String();
                                        isOptionLine = true;
                                } else {
                                        if (read.charAt(i) == '\n' || read.charAt(i) == ' ' || read.charAt(i) == '='){
                                                continue;
                                        }
                                        lastSegment = lastSegment + read.charAt(i);
                                }
                        }

                        System.out.println("Custom config file successfully loaded!");

                } catch (Exception e) {
                        System.out.println("Config file not found or corrupted. Configs will be set to default values");
                        e.printStackTrace();
                }
        }
}
