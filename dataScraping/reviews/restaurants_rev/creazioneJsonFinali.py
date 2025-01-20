import json
import random
from datetime import datetime, timedelta

def process_json_and_create_first_file(input_json_file, username_file, output_json_file):
    # Processa un file JSON e aggiunge dettagli come ID, timestamp, username, ecc.
    
    # Leggi i nomi utente dal file di testo
    with open(username_file, 'r', encoding='utf-8') as user_file:
        usernames = [line.strip() for line in user_file if line.strip()]
    if not usernames:
        raise ValueError("Il file username è vuoto o non valido.")
    
    # Carica i dati dal file JSON
    with open(input_json_file, 'r', encoding='utf-8') as json_file:
        data = json.load(json_file)

    # Inizializza una lista per i dati elaborati
    processed_data = []
    id_counter = 0  

    for review in data:
        # Genera una data randomica tra il 2018 e il 2024
        start_date = datetime(2018, 1, 1)
        end_date = datetime(2024, 12, 31)
        random_date = start_date + timedelta(days=random.randint(0, (end_date - start_date).days))
        # Aggiungi un'ora randomica
        random_time = timedelta(
            hours=random.randint(0, 23),
            minutes=random.randint(0, 59)
        )
        formatted_date = (random_date + random_time).strftime("%m-%d-%Y %H:%M")

        # Seleziona il prossimo username ciclicamente
        username = usernames[id_counter % len(usernames)]

        processed_review = {
            "place_name": review.get("restaurant_name"),
            "text": review.get("review_full"),
            "timestamp": formatted_date,
            "rev_id": id_counter,
            "user": username,
            "stars": int(review.get("rating_review")),  # converto a intero 
            "reported": False
        }

        # Aggiungi il nuovo oggetto alla lista
        processed_data.append(processed_review)
        id_counter += 1 

    # Salva i dati elaborati in un nuovo file JSON
    with open(output_json_file, 'w', encoding='utf-8') as output_file:
        json.dump(processed_data, output_file, indent=4, ensure_ascii=False)

    print(f"Dati elaborati salvati in: {output_json_file}")


def create_second_file(input_json_reviews, input_json_addresses, output_json_file):
    # Crea un file JSON contenente informazioni sui ristoranti, inclusi dettagli come address, city e recensioni aggregate.
    
    # Carica gli indirizzi dal file JSON
    with open(input_json_addresses, 'r', encoding='utf-8') as addresses_file:
        addresses_data = json.load(addresses_file)

    # Carica le recensioni dal file JSON
    with open(input_json_reviews, 'r', encoding='utf-8') as reviews_file:
        reviews_data = json.load(reviews_file)

    # Inizializza il file di output
    processed_data = []
    id_counter = 0

    # Ciclo sugli indirizzi
    address_counter = 0

    # Raggruppa le recensioni per ristorante
    grouped_reviews = {}
    for review in reviews_data:
        place_name = review.get("place_name")
        if place_name not in grouped_reviews:
            grouped_reviews[place_name] = []
        grouped_reviews[place_name].append(review)

    print(f"Numero totale di ristoranti processati: {len(grouped_reviews)}")
    for restaurant_name, restaurant_reviews in grouped_reviews.items():

        # Calcola la media delle valutazioni e il totale delle recensioni
        total_reviews = len(restaurant_reviews)
        average_rating = round(sum(float(review["stars"]) for review in restaurant_reviews) / total_reviews, 2)

        # Assegna un indirizzo e una città dal file addresses.json
        if address_counter < len(addresses_data):
             address_data = addresses_data[address_counter]
        else:
            raise ValueError("Il numero di indirizzi nel file è insufficiente per i ristoranti.")
        
        
        address = address_data.get("address", "Unknown Address")
        city = address_data.get("city", "Unknown City")
        address_counter += 1

        # Costruisci l'oggetto embedded reviews_info
        reviews_info = {
            "overall_rating": average_rating,
            "tot_rev_number": total_reviews,
        }

        # Crea l'oggetto del ristorante
        restaurant_entry = {
            "id": id_counter,
            "name": restaurant_name,
            "address": address,
            "city": city,
            "category": "Restaurant", 
            "reviews_info": reviews_info
        }

        processed_data.append(restaurant_entry)
        id_counter += 1

    # Salva i dati elaborati in un nuovo file JSON
    with open(output_json_file, 'w', encoding='utf-8') as output_file:
        json.dump(processed_data, output_file, indent=4, ensure_ascii=False)

    print(f"Dati elaborati salvati in: {output_json_file}")


# File di input e output
input_reviews_json = "NY_rev_postFirtsClean.json"
username_file = "../../users_data/usernames.txt"
input_addresses_json = "addresses.json"
first_output_json = "reviews_restaurants.json"
second_output_json = "restaurants.json"

process_json_and_create_first_file(input_reviews_json, username_file, first_output_json)
create_second_file(first_output_json, input_addresses_json, second_output_json)
