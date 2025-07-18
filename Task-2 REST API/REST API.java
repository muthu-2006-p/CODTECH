import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import org.json.simple.*;
import org.json.simple.parser.*;
public class Main extends JFrame {

    private JTextField cityField;
    private JTextArea outputArea;
    private JButton fetchButton;

    public Main() {
        setTitle("Weather Fetcher - CODTECH Internship");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("🌦 Weather Info via REST API", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        cityField = new JTextField(20);
        fetchButton = new JButton("Fetch Weather");

        inputPanel.add(new JLabel("Enter City:"));
        inputPanel.add(cityField);
        inputPanel.add(fetchButton);

        add(inputPanel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Button Action
        fetchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText().trim();
                if (!city.isEmpty()) {
                    fetchWeather(city);
                } else {
                    outputArea.setText("❗ Please enter a city name.");
                }
            }
        });
    }

    // Fetch Weather Data using REST API
    private void fetchWeather(String city) {
        try {
            String apiUrl = "https://wttr.in/" + URLEncoder.encode(city, "UTF-8") + "?format=j1";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            parseAndDisplayWeather(jsonBuilder.toString(), city);

        } catch (Exception e) {
            outputArea.setText("❌ Error fetching data: " + e.getMessage());
        }
    }

    // Parse JSON & Display Weather
    private void parseAndDisplayWeather(String json, String city) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject root = (JSONObject) parser.parse(json);

            JSONArray current_condition = (JSONArray) root.get("current_condition");
            JSONObject condition = (JSONObject) current_condition.get(0);

            String tempC = (String) condition.get("temp_C");
            String humidity = (String) condition.get("humidity");
            String weatherDesc = (String) ((JSONObject) ((JSONArray) condition.get("weatherDesc")).get(0)).get("value");
            String windSpeed = (String) condition.get("windspeedKmph");

            StringBuilder sb = new StringBuilder();
            sb.append("📍 City: ").append(city).append("\n");
            sb.append("🌤 Weather: ").append(weatherDesc).append("\n");
            sb.append("🌡 Temperature: ").append(tempC).append(" °C\n");
            sb.append("💧 Humidity: ").append(humidity).append(" %\n");
            sb.append("💨 Wind Speed: ").append(windSpeed).append(" km/h\n");

            outputArea.setText(sb.toString());

        } catch (Exception e) {
            outputArea.setText("❌ Error parsing weather data.");
        }
    }

    public static void main(String[] args) {
        // Load GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
