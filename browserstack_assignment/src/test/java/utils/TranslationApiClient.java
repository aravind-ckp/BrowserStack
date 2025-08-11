package utils;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.json.JSONObject;

public class TranslationApiClient {
    private final String rapidApiKey;

    // The key is always sourced from the env variable now
    public TranslationApiClient() {
        this.rapidApiKey = System.getenv("RAPIDAPI_KEY");
        if (rapidApiKey == null || rapidApiKey.isBlank()) {
            throw new RuntimeException("RAPIDAPI_KEY environment variable not set!");
        }
    }

    public String translateToEnglish(String textToTranslate) {
        try (AsyncHttpClient client = Dsl.asyncHttpClient()) {
            String safeText = textToTranslate.replace("\"", "\\\"");
            String requestBody = String.format(
                    "{\"from\":\"es\",\"to\":\"en\",\"json\":{\"text\":\"%s\"}}", safeText
            );

            return client.prepare("POST", "https://google-translate113.p.rapidapi.com/api/v1/translator/json")
                    .setHeader("X-RapidAPI-Key", rapidApiKey)
                    .setHeader("X-RapidAPI-Host", "google-translate113.p.rapidapi.com")
                    .setHeader("Content-Type", "application/json")
                    .setBody(requestBody)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(res -> {
                        String body = res.getResponseBody();
                        System.out.println("DEBUG Translation API raw response: " + body);

                        try {
                            JSONObject json = new JSONObject(body);
                            // Handle possible output keys
                            if (json.has("trans")) {
                                return json.getJSONObject("trans").getString("text");
                            } else if (json.has("translated_text")) {
                                return json.getString("translated_text");
                            } else if (json.has("error")) {
                                return "[API Error] " + json.getString("error");
                            } else if (json.has("message")) {
                                return "[API Message] " + json.getString("message");
                            } else {
                                return "[API returned no translation]";
                            }
                        } catch (Exception e) {
                            return "[Translation parsing error] " + e.getMessage();
                        }
                    })
                    .join();
        } catch (Exception e) {
            return "[Error] " + e.getMessage();
        }
    }
}
