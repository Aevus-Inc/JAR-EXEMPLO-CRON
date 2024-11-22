package sptech.school;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

public class Slack {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String URL = "https://hooks.slack.com/services/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";

    public static void sendMessage(JSONObject content) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(String.format("Status: %s", response.statusCode()));
        System.out.println(String.format("Response: %s", response.body()));
    }


    public static void sendMessage(String text) throws IOException, InterruptedException {
        JSONObject json = createMessage(text);
        sendMessage(json);
    }


    public static JSONObject createMessage(String text) {
        JSONObject json = new JSONObject();
        json.put("text", text);
        return json;
    }


    public static JSONObject createFormattedMessage(String level, String message, String timestamp) {
        String formattedMessage = String.format("[%s] [%s] %s", timestamp, level, message);
        return createMessage(formattedMessage);
    }


    public static void sendFormattedMessage(String level, String message) throws IOException, InterruptedException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        JSONObject json = createFormattedMessage(level, message, timestamp);
        sendMessage(json);
    }
}
