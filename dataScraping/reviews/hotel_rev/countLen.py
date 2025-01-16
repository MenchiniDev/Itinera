import json
from collections import defaultdict
def count_cities_reviews_and_hotels(input_file):
    #Conta il numero di città diverse, il numero di recensioni e il numero di hotel unici per ciascuna città.
    with open(input_file, 'r', encoding='utf-8') as f:
        reviews = json.load(f)

    # Dizionario per contare le recensioni e gli hotel per città
    city_data = defaultdict(lambda: {"review_count": 0, "hotels": set()})

    for review in reviews:
        city = review.get("city", "Unknown")  # Ottieni il campo "city", usa "Unknown" come fallback
        hotel_name = review.get("name", "Unknown")  # Ottieni il nome dell'hotel
        
        # Aggiorna i dati per la città
        city_data[city]["review_count"] += 1
        city_data[city]["hotels"].add(hotel_name)

    # Conta il numero di città diverse
    unique_cities = len(city_data)

    print(f"Numero di città diverse: {unique_cities}")
    for city, data in city_data.items():
        hotel_count = len(data["hotels"])
        print(f"Città: {city}, Numero di recensioni: {data['review_count']}, Numero di hotel unici: {hotel_count}")

    return city_data

#input_file = "Hotel_Reviews_postPrimaPulizia.json"
input_file = "filePerDocumenti.json"

# Esegui la funzione
count_cities_reviews_and_hotels(input_file)


