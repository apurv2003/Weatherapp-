# Weatherapp-
A Java-Based implementation of a Weather App, a user-centric tool for monitoring weather conditions and preserving data.
4.2.1 AppLauncher.java:

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                new WeatherAppGui().setVisible(true);

            }
        });
    }
}


4.2.2 WeatherAppGui.java:

import org.json.simple.JSONObject;
import java.sql.Connection;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){
        // setup our gui and add a title
        super("Weather App");


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);

        // load our gui at the center of the screen
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        try (Connection connection = DatabaseConnector.getConnection("Weather")) {
            DatabaseConnector.wipeWeatherData(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addGuiComponents();
    }

    private void addGuiComponents(){
        // search field
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user
                String userInput = searchTextField.getText();
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);


                // update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }


                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");


                weatherConditionDesc.setText(weatherCondition);


                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");


                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);

        JButton saveButton = new JButton("SAVE");
        saveButton.setBounds(145,60,80,30);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = searchTextField.getText();
                double temperature = Double.parseDouble(temperatureText.getText().replace(" C", ""));

                Connection connection = null;
                try {
                    connection = DatabaseConnector.getConnection("Weather");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    DatabaseConnector.saveWeatherData(connection, location, temperature);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }


        });
        add(saveButton);

        JButton History = new JButton("SHOW");
        History.setBounds(225,60,80,30);
        History.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connection = DatabaseConnector.getConnection("Weather")) {
                    String weatherData = DatabaseConnector.getWeatherData(connection);
                    JOptionPane.showMessageDialog(null, "Saved Weather Data:\n" + weatherData, "Weather Data", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        add(History);
    }


    //Custom image loader {Internet recommendation}
    private ImageIcon loadImage(String resourcePath){
        try{

            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);

        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}


4.2.3 DatabaseConnector.java:
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection getConnection(String dbname) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/" + dbname;
        String username = "root"; //MSQ username
        String password = " "; //MySQL Password

        return DriverManager.getConnection(url, username, password);
    }
    public static void saveWeatherData(Connection connection, String location, double temperature) throws SQLException {
        String insertQuery = "INSERT INTO weather_data (location, temperature) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, location);
            preparedStatement.setDouble(2, temperature);
            preparedStatement.executeUpdate();
        }
    }

    public static String getWeatherData(Connection connection) throws SQLException {
        String selectQuery = "SELECT location, temperature FROM weather_data";
        StringBuilder weatherData = new StringBuilder();
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String location = resultSet.getString("location");
                double temperature = resultSet.getDouble("temperature");
                weatherData.append("Location: ").append(location).append(", Temperature: ").append(temperature).append("\n");
            }
        }
        return weatherData.toString();
    }

    public static void wipeWeatherData(Connection connection) throws SQLException {
        String deleteQuery = "DELETE FROM weather_data";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.executeUpdate();
        }
    }
}



4.3.4 WeatherApp.java:
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){

        JSONArray locationData = getLocationData(locationName);

        // latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m";

        try{

            HttpURLConnection conn = fetchApiResponse(urlString);

            // 200 - means that the connection was a success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }


            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){

                resultJson.append(scanner.nextLine());
            }


            scanner.close();

            // close url connection
            conn.disconnect();

            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));


            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");


            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build the weather json data object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    public static JSONArray getLocationData(String locationName){

        locationName = locationName.replaceAll(" ", "+");


        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{

            HttpURLConnection conn = fetchApiResponse(urlString);


            // 200 means successful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{

                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());


                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close url connection
                conn.disconnect();

                // parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));


                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("GET");

            // connect to The API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        // could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // return the index
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime(){
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2023-09-02T00:00 (this is how is is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // convert the weather code to something more readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            // clear
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
                    || (weathercode >= 80L && weathercode <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}





	5. PYTHON IMPLEMENTATION

import tkinter as tk
import requests
import time

api_key = "a9173790278131156fc3a41c215b85e8"
def getWeather():

    city = textField.get()
    apiUrl = f"https://api.openweathermap.org/data/2.5/weather?q={city}&appid={api_key}"
    
    result = requests.get(apiUrl)
    data = result.json()
    # condition = data['weather'][0]['main']
    temp = int(data['main']['temp'] - 273.15)
    min_temp = int(data['main']['temp_min'] - 273.15)
    max_temp = int(data['main']['temp_max'] - 273.15)
    pressure = data['main']['pressure']
    humidity = data['main']['humidity']
    wind = data['wind']['speed']

    # final_info = condition + "\n" + str(temp) + "°C" 
    final_data = "\n"+ "Min Temp: " + str(min_temp) + "°C" + "\n" + "Max Temp: " + str(max_temp) + "°C" +"\n" + "Pressure: " + str(pressure) + "\n" +"Humidity: " + str(humidity) + "\n" +"Wind Speed: " + str(wind)
    # label1.config(text = final_info)
    label2.config(text = final_data)

canvas = tk.Tk()
canvas.geometry("600x500")
canvas.title("Weather App")
smallText = ("poppins", 15, "bold")
largeText = ("poppins", 35, "bold")

textField = tk.Entry(canvas, justify='center', width = 20, font = largeText)
submitButton = tk.Button(canvas, text="Get Result", command=getWeather)
textField.pack(pady = 20)
submitButton.pack(pady = 15)
textField.focus()

label1 = tk.Label(canvas, font=largeText)
label1.pack()
label2 = tk.Label(canvas, font=smallText)
label2.pack()
canvas.mainloop()
