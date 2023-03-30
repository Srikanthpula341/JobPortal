//package com.jobs.handler;
//
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.jsoup.nodes.Element;
//import org.springframework.stereotype.Component;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import javax.imageio.ImageIO;
//
//
//@Component
//public class CompanyHandler {
//    String apiKey = "AIzaSyB0ESNj3tBHgUbbwJ43qz8NXwNDjmqvJO0";
//    String searchEngineId = "221463deb58fc4ef4";
//
//    public String getCompanyLogoUrl(String companyName) {
//        String url = "https://www.googleapis.com/customsearch/v1";
//        String imageUrl = null;
//
//        try {
//            HttpClient httpClient = HttpClient.newBuilder()
//                    .version(HttpClient.Version.HTTP_2)
//                    .build();
//
//            String query = URLEncoder.encode(companyName + " logo", StandardCharsets.UTF_8);
//            URI uri = URI.create(url + "?key=" + apiKey + "&cx=" + searchEngineId +
//                    "&q=" + query + "&searchType=image&imgSize=large&imgType=clipart&fileType=png&alt=json");
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .GET()
//                    .uri(uri)
//                    .build();
//
//            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                JSONObject json = new JSONObject(response.body());
//                JSONArray items = json.getJSONArray("items");
//                if (items.length() > 0) {
//                    imageUrl = items.getJSONObject(0).getJSONObject("image").getString("thumbnailLink");
//
//                    // Use OCR to extract text from the image
//                    BufferedImage image = ImageIO.read(new URL(imageUrl));
//                    Tesseract tesseract = new Tesseract();
//                    tesseract.setDatapath("src/main/resources"); // Set the path to your Tesseract data folder
//                    String extractedText = tesseract.doOCR(image);
//
//                    // Check if the extracted text matches the company name
//                    if (extractedText.toLowerCase().contains(companyName.toLowerCase())) {
//                        return imageUrl;
//                    } else {
//                        // Try matching the first letters of the words in the company name
//                        StringBuilder initials = new StringBuilder();
//                        String[] words = companyName.split("\\s+");
//                        for (String word : words) {
//                            initials.append(word.charAt(0));
//                        }
//                        String initialsString = initials.toString();
//                        if (extractedText.toLowerCase().contains(initialsString.toLowerCase())) {
//                            return imageUrl;
//                        }
//                    }
//                }
//            } else {
//                // Handle errors
//                System.err.println("Error: " + response.statusCode() + " " + response.body());
//            }
//        } catch (IOException | InterruptedException | JSONException | TesseractException e) {
//            e.printStackTrace();
//        }
//
//        return null; // Return null if no matching logo image was found
//    }
//
//
//
//
//
//
//
//
//}
