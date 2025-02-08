import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class WeatherApp extends JFrame {
    private JTextField cityField;
    private JLabel temperatureLabel, descriptionLabel, humidityLabel;
    private JButton searchButton;
    private static final String API_KEY = "YOUR_API_KEY_HERE";

    public WeatherApp() {
        setTitle("Weather App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        cityField = new JTextField(20);
        searchButton = new JButton("Search");
        inputPanel.add(new JLabel("Enter city: "));
        inputPanel.add(cityField);
        inputPanel.add(searchButton);

        JPanel outputPanel = new JPanel(new GridLayout(3, 1));
        temperatureLabel = new JLabel("Temperature: ");
        descriptionLabel = new JLabel("Description: ");
        humidityLabel = new JLabel("Humidity: ");
        outputPanel.add(temperatureLabel);
        outputPanel.add(descriptionLabel);
        outputPanel.add(humidityLabel);

        add(inputPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String city = cityField.getText();
                fetchWeatherData(city);
            }
        });
    }

    private void fetchWeatherData(String city) {
        try {
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
            String description = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
            int humidity = jsonResponse.getJSONObject("main").getInt("humidity");

            temperatureLabel.setText("Temperature: " + temperature + "°C");
            descriptionLabel.setText("Description: " + description);
            humidityLabel.setText("Humidity: " + humidity + "%");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching weather data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WeatherApp().setVisible(true);
        });
    }
}
