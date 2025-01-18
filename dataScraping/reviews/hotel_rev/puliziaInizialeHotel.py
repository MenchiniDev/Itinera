import json
import random
from datetime import datetime, timedelta

def add_city_field_to_reviews(reviews, cities):
    unknown_count = 0

    for review in reviews:
        address = review.get("Hotel_Address", "").lower()
        found_city = None
        for city in cities:
            if city.lower() in address:
                found_city = city
                break
        if found_city:
            review["city"] = found_city
        else:
            review["city"] = "Unknown"
            unknown_count += 1

    return unknown_count

def process_reviews(input_file, cities):
    with open(input_file, 'r', encoding='utf-8') as f:
        reviews = json.load(f)

    processed_reviews = []
    default_score_count = 0

    def generate_random_datetime():
        start_date = datetime(2018, 1, 1)
        end_date = datetime(2024, 12, 31)
        random_date = start_date + timedelta(days=random.randint(0, (end_date - start_date).days))
        random_hour = random.randint(0, 23)
        random_minute = random.randint(0, 59)
        return random_date.replace(hour=random_hour, minute=random_minute)

    unknown_count = add_city_field_to_reviews(reviews, cities)

    for review in reviews:
        hotel_address = review.get("Hotel_Address", "Unknown")
        hotel_name = review.get("Hotel_Name", "Unknown")
        reviewer_score = review.get("Reviewer_Score", "0.0")
        city = review.get("city", "Unknown")

        try:
            reviewer_score = round(float(reviewer_score) / 2)
        except ValueError:
            reviewer_score = 0
            default_score_count += 1

        if reviewer_score <= 2:
            selected_review = review.get("Negative_Review", "No Negative Review")
        else:
            selected_review = review.get("Positive_Review", "No Positive Review")

        random_datetime = generate_random_datetime()
        formatted_date = random_datetime.strftime("%m-%d-%Y %H:%M")

        processed_review = {
            "address": hotel_address,
            "name": hotel_name,
            "city": city,
            "text": selected_review,
            "timestamp": formatted_date,
            "stars": reviewer_score
        }
        processed_reviews.append(processed_review)

    print(f"Oggetti processati: {len(processed_reviews)}.")
    print(f"Campi 'city' impostati su 'Unknown': {unknown_count}.")
    print(f"Valori 'reviewer_score' impostati a 0 per errore di parsing: {default_score_count}.")

    return processed_reviews

def filter_reviews_by_city_and_hotels(processed_reviews, output_file, cities):
    hotels_per_city = {city: set() for city in cities}
    filtered_reviews = []

    for review in processed_reviews:
        city = review.get("city", "Unknown")
        hotel_name = review.get("name", "Unknown")

        if city in cities:
            max_hotels = 4 if city == "Rome" else 25

            if len(hotels_per_city[city]) < max_hotels:
                hotels_per_city[city].add(hotel_name)

            if hotel_name in hotels_per_city[city]:
                filtered_reviews.append(review)

    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(filtered_reviews, f, ensure_ascii=False, indent=4)

    print(f"Recensioni filtrate salvate in '{output_file}'.")
    for city, hotels in hotels_per_city.items():
        print(f"{city}: {len(hotels)} hotel inclusi.")

# Percorsi e configurazione
input_file = "Hotel_Reviews.json"
final_file = "filePerDocumenti.json"

european_cities = [
    "London", "Berlin", "Madrid", "Rome", "Paris", "Vienna", "Bucharest", "Hamburg", "Budapest", "Warsaw",
    "Barcelona", "Munich", "Milan", "Prague", "Sofia", "Brussels", "Birmingham", "Cologne", "Napoli", "Stockholm",
    "Turin", "Amsterdam", "Marseille", "Zagreb", "Valencia", "Krakow", "Frankfurt", "Seville", "Oslo", "Copenhagen",
    "Dublin", "Lisbon", "Helsinki", "Riga", "Tallinn", "Vilnius", "Luxembourg", "Ljubljana", "Bratislava", "Sarajevo",
    "Skopje", "Tirana", "Podgorica", "Reykjavik", "Valletta", "Florence", "Monaco", "San Marino", "Vaduz", "Pristina"
]
# Array scritto dopo aver visto le città presenti nel file tramite il codice in countLen.py
selected_cities = {'Vienna', 'Barcelona', 'Milan', 'Amsterdam', 'London', 'Paris'}

# Esegui le funzioni in sequenza
processed_reviews = process_reviews(input_file, european_cities)
filter_reviews_by_city_and_hotels(processed_reviews, final_file, selected_cities)
