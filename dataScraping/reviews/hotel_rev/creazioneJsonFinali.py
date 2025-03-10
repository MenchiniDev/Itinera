import json
import random
from collections import defaultdict
from datetime import datetime, timedelta

def format_iso8601(date_obj):
    return date_obj.isoformat(timespec="microseconds")

def create_json_files(input_file, usernames_file, reviews_output_file, hotels_output_file):
    # Carica il file di input
    with open(input_file, 'r', encoding='utf-8') as f:
        reviews = json.load(f)

    # Carica gli usernames dal file
    with open(usernames_file, 'r', encoding='utf-8') as f:
        usernames = [line.strip() for line in f if line.strip()]
    if not usernames:
        raise ValueError("Il file usernames.txt è vuoto!")

    # variabili per generare gli id
    review_id = 65000
    hotel_id = 1250

    reviews_output = []
    hotels_output = []

    # dizionario per aggregare le recensioni per hotel
    hotel_reviews = defaultdict(list)


    username_index = 0
    for review in reviews:
        # Genera una data randomica tra il 2018 e il 2024
        start_date = datetime(2018, 1, 1)
        end_date = datetime(2024, 12, 31)
        random_date = start_date + timedelta(days=random.randint(0, (end_date - start_date).days))
        # Aggiungi un'ora randomica
        random_time = timedelta(
            hours=random.randint(0, 23),
            minutes=random.randint(0, 59)
        )
        formatted_date =random_date + random_time
        
        
        # Creazione del JSON per le recensioni
        review_entry = {
            "place_name": review.get("name", ""),
            "text": review.get("text", ""),
            "timestamp": format_iso8601(formatted_date), #porto la data nel formato iso 8601
            "_id": str(review_id),
            "user": usernames[username_index],
            "stars": review.get("stars", 0),
            "reported": False
        }
        reviews_output.append(review_entry)
        review_id += 1
        username_index = (username_index + 1) % len(usernames)  # Ciclo per assegnare usernames ciclicamente

        # Raggruppa le recensioni per hotel
        hotel_key = (review.get("name", ""), review.get("address", ""), review.get("city", ""))
        hotel_reviews[hotel_key].append(review.get("stars", 0))

    # Elaborazione degli hotel
    for (name, address, city), stars in hotel_reviews.items():
        review_info = {
            "overall_rating": round(sum(stars) / len(stars), 2),
            "tot_rev_number": len(stars)
        }
        hotel_entry = {
            "_id": str(hotel_id),
            "name": name,
            "address": address,
            "city": city,
            "category": "Hotel",
            "reviews_info": review_info
        }
        hotels_output.append(hotel_entry)
        hotel_id += 1

    # Scrittura dei file
    with open(reviews_output_file, 'w', encoding='utf-8') as f:
        json.dump(reviews_output, f, ensure_ascii=False, indent=4)

    with open(hotels_output_file, 'w', encoding='utf-8') as f:
        json.dump(hotels_output, f, ensure_ascii=False, indent=4)

    print(f"File JSON delle recensioni creato: {reviews_output_file}")
    print(f"File JSON degli hotel creato: {hotels_output_file}")

input_file = "filePerDocumenti.json"  
usernames_file = "../../users_data/usernames.txt"  
reviews_output_file = "ReviewsHotels.json"  
hotels_output_file = "Hotels.json" 

# Esegui la funzione
create_json_files(input_file, usernames_file, reviews_output_file, hotels_output_file)
