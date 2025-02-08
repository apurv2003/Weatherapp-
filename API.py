import requests
from dataclasses import dataclass

API_KEY = "YOUR_API_KEY_HERE"
BASE_URL = "http://api.openweathermap.org/data/2.5/weather"

@dataclass
class WeatherData:
    temperature: float
    description: str
    humidity: int

def get_weather_data(city: str) -> WeatherData:
    try:
        params = {
            "q": city,
            "appid": API_KEY,
            "units": "metric"
        }
        response = requests.get(BASE_URL, params=params)
        response.raise_for_status()  # Raise an exception for bad responses
        
        data = response.json()
        
        temperature = data['main']['temp']
        description = data['weather'][0]['description']
        humidity = data['main']['humidity']
        
        return WeatherData(temperature, description, humidity)
    except requests.RequestException as e:
        print(f"Error fetching weather data: {e}")
        return None
