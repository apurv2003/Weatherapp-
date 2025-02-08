import requests
import tkinter as tk
from tkinter import messagebox

API_KEY = "YOUR_API_KEY_HERE"

def fetch_weather_data(city):
    try:
        url = f"http://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}&units=metric"
        response = requests.get(url)
        data = response.json()
        
        if response.status_code == 200:
            temperature = data['main']['temp']
            description = data['weather'][0]['description']
            humidity = data['main']['humidity']
            return temperature, description, humidity
        else:
            return None
    except Exception as e:
        print(f"Error fetching weather data: {e}")
        return None

class WeatherApp:
    def __init__(self, master):
        self.master = master
        master.title("Weather App")
        master.geometry("400x300")

        self.label = tk.Label(master, text="Enter city name:")
        self.label.pack()

        self.city_entry = tk.Entry(master)
        self.city_entry.pack()

        self.search_button = tk.Button(master, text="Search", command=self.search_weather)
        self.search_button.pack()

        self.result_label = tk.Label(master, text="")
        self.result_label.pack()

    def search_weather(self):
        city = self.city_entry.get()
        result = fetch_weather_data(city)
        
        if result:
            temperature, description, humidity = result
            self.result_label.config(text=f"Temperature: {temperature}°C\nDescription: {description}\nHumidity: {humidity}%")
        else:
            messagebox.showerror("Error", "Unable to fetch weather data")

if __name__ == "__main__":
    root = tk.Tk()
    app = WeatherApp(root)
    root.mainloop()
