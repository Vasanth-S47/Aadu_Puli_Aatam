package com.game.utils;

import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonRequestBodyReader {
    public static JSONObject getJsonRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder jsonBody = new StringBuilder();
        BufferedReader requestReader = request.getReader();
        String currentLine;

        while ((currentLine = requestReader.readLine()) != null) {
            jsonBody.append(currentLine);
        }

        return new JSONObject(jsonBody.toString());
    }
}
