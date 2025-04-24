package com.game.bot;

import com.game.constant.GameConstant;
import com.game.model.GameState;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AaduPuliBotGatway {
    private static final String CONFIG_FILE = "ai.properties";
    private static final String PROMPT_FILE_PATH = "prompt.txt";

    private final Gson gson = new Gson();
    private final GameConstant gameConstant=GameConstant.getInstance();
    private final String apiUrl;
    private final String apiKey;

    private static AaduPuliBotGatway instance;

    private AaduPuliBotGatway() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new FileNotFoundException("Configuration file not found: " + CONFIG_FILE);
            }
            props.load(input);
        }
        apiUrl = props.getProperty("AI_API_URL");
        apiKey = props.getProperty("AI_API_KEY");
    }

    public static AaduPuliBotGatway getInstance() throws IOException {
        if (instance == null) {
            synchronized (AaduPuliBotGatway.class) {
                if (instance == null) {
                    instance = new AaduPuliBotGatway();
                }
            }
        }
        return instance;
    }

    public JSONObject getAIPromptResponse(GameState state) throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROMPT_FILE_PATH);
        if (inputStream == null) {
            throw new FileNotFoundException("Prompt file not found: " + PROMPT_FILE_PATH);
        }

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        String basePrompt = result.toString("UTF-8");
        String serializedState = gson.toJson(state);
        String serializedConstant=gson.toJson(gameConstant.VALID_MOVES);
        String serializedJumps=gson.toJson(gameConstant.VALID_JUMPS);
        String fullPrompt = basePrompt + "\n\n" + serializedState+ "\n\n" +"Valid_Moves"+ serializedConstant + "\n\n"+
                "Valid_Jumps"+serializedJumps;

        JSONObject textPart = new JSONObject().put("text", fullPrompt);
        JSONArray partsArray = new JSONArray().put(textPart);
        JSONObject contentObject = new JSONObject().put("parts", partsArray);
        JSONArray contentsArray = new JSONArray().put(contentObject);
        JSONObject payload = new JSONObject().put("contents", contentsArray);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String fullUrl = apiUrl + "?key=" + apiKey;
            HttpPost post = new HttpPost(fullUrl);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(payload.toString(), "UTF-8"));

            try (CloseableHttpResponse response = client.execute(post)) {

                String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject json = new JSONObject(jsonResponse);
                return json;
            }
        }
    }
}
