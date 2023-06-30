import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitcoinRateProgram {
    public static void main(String[] args) {
        try {
            // Construct the URI
            URI uri = new URI("https", "api.coindesk.com", "/v1/bpi/currentprice.json", null);

            // Make a GET request to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Extract the rate from the JSON response using regex
            String rate = extractRate(response.toString());

            // Convert the rate to words
            String rateInWords = convertToWords(rate);

            // Print the rate in words
            System.out.println(rateInWords);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // Extracts the rate from the JSON response using regex
    private static String extractRate(String json) {
        Pattern pattern = Pattern.compile("\"rate\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // Converts the rate to words
    private static String convertToWords(String rate) {
        if (rate == null || rate.isEmpty()) {
            return "";
        }

        String[] parts = rate.split("\\.");
        String wholePart = parts[0].replace(",", "");
        String decimalPart = "0";
        if (parts.length > 1) {
            decimalPart = parts[1];
        }

        String wholePartInWords = convertToWords(Integer.parseInt(wholePart));
        String decimalPartInWords = convertToWords(Integer.parseInt(decimalPart));

        StringBuilder words = new StringBuilder();
        if (!wholePartInWords.isEmpty()) {
            words.append(wholePartInWords).append(" ");
        }
        if (!decimalPartInWords.isEmpty()) {
            words.append(decimalPartInWords);
        }

        return words.toString().trim();
    }

    // Converts a numeric value to words
    private static String convertToWords(int number) {
        if (number == 0) {
            return "Zero";
        }

        String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        };

        String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        };

        StringBuilder words = new StringBuilder();

        if (number >= 1000) {
            int thousands = number / 1000;
            words.append(convertToWords(thousands)).append(" Thousand ");
            number %= 1000;
        }

        if (number >= 100) {
            int hundreds = number / 100;
            words.append(units[hundreds]).append(" Hundred ");
            number %= 100;
        }

        if (number >= 20) {
            int tensDigit = number / 10;
            words.append(tens[tensDigit]).append(" ");
            number %= 10;
        }

        if (number > 0) {
            words.append(units[number]).append(" ");
        }

        return words.toString().trim();
    }
}
