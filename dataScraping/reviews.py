import googlemaps
import requests
import json
import os

api_key = "AIzaSyB3mm8iBZ8qln6WGtL9dZt0vXwvVsBjSRA"


european_cities = [
    "London", "Berlin", "Madrid", "Rome", "Paris", "Vienna", "Bucharest", "Hamburg", "Budapest", "Warsaw",
    "Barcelona", "Munich", "Milan", "Prague", "Sofia", "Brussels", "Birmingham", "Cologne", "napoli", "Stockholm",
    "Turin", "Amsterdam", "Marseille", "Zagreb", "Valencia", "Krakow", "Frankfurt", "Seville", "Oslo", "Copenhagen",
    "Dublin", "Lisbon", "Helsinki", "Riga", "Tallinn", "Vilnius", "Luxembourg", "Ljubljana", "Bratislava", "Sarajevo",
    "Skopje", "Tirana", "Podgorica", "Reykjavik", "Valletta", "Florence", "Monaco", "San Marino", "Vaduz", "Pristina"
]

place_type = ["restaurant", "hotel", "tourist_attraction"]

def get_coordinates(city_name):
    gmaps = googlemaps.Client(key=api_key)
    geocode_result = gmaps.geocode(city_name)
    if geocode_result:
        location = geocode_result[0]['geometry']['location']
        return location['lat'], location['lng']
    return None, None

def search_places(lat, lng, place_type, api_key):
    url = f"https://maps.googleapis.com/maps/api/place/nearbysearch/json"
    for type in place_type:
        params = {
            "location": f"{lat},{lng}",
            "radius": 10000,  # Raggio in metri
            "type": place_type,
            "key": api_key
        }
        response = requests.get(url, params=params)
        if response.status_code == 200:
            return response.json().get("results", [])
    return []


import requests

def get_place_reviews(place_id):
    """
    Ottiene le recensioni di un luogo dato il suo place_id.
    """
    url = "https://maps.googleapis.com/maps/api/place/details/json"
    params = {
        "place_id": place_id,
        "fields": "name,rating,reviews",
        "key": api_key
    }
    response = requests.get(url, params=params)
    if response.status_code == 200:
        result = response.json().get("result", {})
        return {
            "name": result.get("name"),
            "rating": result.get("rating"),
            "reviews": result.get("reviews", [])
        }
    else:
        print(f"Errore: {response.status_code} - {response.text}")
        return None

def get_local_coordinates_and_id(city):
    file_path = f"./dataScraping/reviews/places/{city}_places.json"
    if os.path.exists(file_path):
        with open(file_path, "r", encoding="utf-8") as f:
            places = json.load(f)
            return [(place['geometry']['location']['lat'], place['geometry']['location']['lng'], place['place_id']) for place in places]
    return []

for city in european_cities:
    if os.path.exists(f"./dataScraping/reviews/places/{city}_places.json") == False:
        print(f"Elaborazione di {city} in corso...")
        lat, long = get_coordinates(city)
        places = search_places(lat,long,place_type,api_key)
        if places:
            with open(f"./dataScraping/reviews/places/{city}_places.json", "w", encoding="utf-8") as f:
                json.dump(places, f, ensure_ascii=False, indent=4)
    else:
        places = get_local_coordinates_and_id(city)
        for place in places:
            print(place[2])
            place_id = place[2]
            reviews = get_place_reviews(place_id)
            if reviews:
                with open(f"./dataScraping/reviews/reviews/{city}_{place_id}_reviews.json", "w", encoding="utf-8") as f:
                    json.dump(reviews, f, ensure_ascii=False, indent=4)
                

    
