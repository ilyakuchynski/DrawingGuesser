package com.guessdraw.app.handlers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class QueryGPT {
    private static final String API_KEY;

    static {
        try {
//            API_KEY = Files.readString(Path.of("src/main/resources/com/guessdraw/app/gpt_api_key"));
            API_KEY = Files.readString(Path.of("gpt_api_key"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String url = "https://api.openai.com/v1/chat/completions";
    private static final String model = "gpt-4o";
    public static String queryGPT(String topic) {
        try{
            System.out.println("Querying GPT-4");
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + API_KEY);
            con.setRequestProperty("Content-Type", "application/json");

            con.setDoOutput(true);

            byte[] drawing = Files.readAllBytes(Path.of("copy.png"));
            String base64_image = Base64.getEncoder().encodeToString(drawing);

            String prompt = "\"" +
                            "This is a doodle drawing. What topic do you think it was drawn on? " +
                            "IF YOU THINK THAT THE POSSIBLE TOPIC OF THIS DRAWING IS " + topic +
                            "AND AN AVERAGE HUMAN WITH PROBABILITY 95% WOULD SAY THAT THIS DRAWING REPRESENTS " + topic + ", " +
                            "THEN JUST RESPOND WITH THIS TOPIC. " +
                            "Otherwise, if this drawing doesn't look like " + topic + ", tell me what you think it is." +
                            "YOUR RESPONSE MUST BE A SINGLE WORD. EITHER THIS EXACT TOPIC IF YOU THINK THE DRAWING IS CORRECT, OR A SINGLE WORD OF THE TOPIC YOU THINK THE DRAWING IS." +
                            "IF THE DRAWING IS CORRECT THIS SINGLE WORD MUST BE " + topic + ", KEEPING ALL LOWERCASE LETTERS IN LOWERCASE, AND ALL UPPERCASE LETTERS IN UPPERCASE WITH NO SPACES." +
                            "ALSO VERY IMPORTANT: IF THERE'S ONLY A LITTLE HAS BEEN DRAWN, AND HUMAN COULD NOT RECOGNIZE TOPIC " + topic + " IN THIS DRAWING, THEN GIVE AS RESPONSE TOPIC DIFFERENT FROM " + topic + ". " +
                            "\"";


            String payload = "{"
                    + "\"model\": \"" + model + "\","
                    + "\"messages\": ["
                    + "{"
                    + "\"role\": \"user\","
                    + "\"content\": ["
                    + "{"
                    + "\"type\": \"text\","
                    + "\"text\": " + prompt
                    + "},"
                    + "{"
                    + "\"type\": \"image_url\","
                    + "\"image_url\": {"
                    + "\"url\": \"data:image/jpeg;base64," + base64_image+"\""
                    + "}"
                    + "}"
                    + "]"
                    + "}"
                    + "],"
                    + "\"max_tokens\": 300"
                    + "}";

//            System.out.println(payload);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(payload);
            writer.flush();
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
//
//            // calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());
        }catch(Exception e){
            System.err.println("Failed to query GPT-4");
        }
        return "NOTHING";
    }
    public static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content")+ 11;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);

    }
}
