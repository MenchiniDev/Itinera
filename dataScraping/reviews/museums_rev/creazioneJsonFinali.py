import json
import re
import random
from datetime import datetime, timedelta

def format_iso8601(date_obj):
    return date_obj.isoformat(timespec="microseconds")

def process_reviews(reviews, addresses, usernames):
    processed_reviews = []
    id_counter = 150000  # ID iniziale per le recensioni
    user_count = len(usernames)  # Numero totale di utenti

    for index, review in enumerate(reviews):
        # Genera un timestamp fittizio
        start_date = datetime(2018, 1, 1)
        end_date = datetime(2024, 12, 31)
        random_date = start_date + timedelta(days=random.randint(0, (end_date - start_date).days))
        random_time = timedelta(hours=random.randint(0, 23), minutes=random.randint(0, 59))
        t=random_date + random_time
        timestamp = format_iso8601(t)

        # Sostituzioni nel campo comment
        comment = review.get("comment", "")
        comment = re.sub(r"\b(British Museum|[Ll]ondon)\b", lambda match: "this museum" if match.group(0) == "British Museum" else "this city", comment)


        # Estrazione ciclica dei nomi dei musei
        address = addresses[id_counter % len(addresses)]
        place_name = address["name"]

        # Seleziona l'username ciclicamente
        user = usernames[index % user_count]

        # Creazione della recensione processata
        processed_review = {
            "place_name": place_name,
            "text": comment,
            "timestamp": timestamp,
            "_id": str(id_counter),
            "user": user,
            "stars": int(review.get("rating", 0)),
            "reported": False
        }
        processed_reviews.append(processed_review)
        id_counter += 1

    return processed_reviews

def generate_review_info(processed_reviews, addresses): 
    #crea il secondo file a partire dal primo prodotto nella funzione soprastante
    processed_addresses = []
    id_counter = 1600  # ID iniziale per il secondo file

    for address in addresses:
        # Filtra le recensioni per il museo corrente prendendo Place_name dall'array prodotto nella funzione precedente 
        # e name dal file json contenente gli indirizzi dei musei
        museum_reviews = [review for review in processed_reviews if review["place_name"] == address["name"]]

        # Calcola il numero di recensioni e la media dei rating
        total_reviews = len(museum_reviews)
        average_rating = round(sum(review["stars"] for review in museum_reviews) / total_reviews, 2) if total_reviews > 0 else 0

        # Crea l'oggetto embedded reviews_info
        reviews_info = {
            "overall_rating": average_rating,
            "tot_rev_number": total_reviews
        }

        # Crea l'oggetto per il secondo file
        processed_address = {
            "_id": str(id_counter),
            "name": address["name"],
            "address": address["address"],
            "city": address["city"],
            "category": "Museum",
            "reviews_info": reviews_info
        }

        processed_addresses.append(processed_address)
        id_counter += 1

    return processed_addresses

# Percorsi dei file
reviews_file = "Data_Review_British_Museum.json" 
addresses_file = "museums_addresses.json" 
usernames_file = "../../users_data/usernames.txt"  
processed_reviews_file = "museums_reviews.json"  
museums_with_reviews_file = "museums.json" 

# Caricamento dei file di input
with open(reviews_file, 'r', encoding='utf-8') as file:
    reviews = json.load(file)

with open(addresses_file, 'r', encoding='utf-8') as file:
    addresses = json.load(file)

with open(usernames_file, 'r', encoding='utf-8') as file:
    usernames = [line.strip() for line in file if line.strip()]

if not usernames:
    raise ValueError("Il file usernames.txt è vuoto o non valido.")

# Genera le recensioni processate
processed_reviews = process_reviews(reviews, addresses, usernames)

# Salva il primo file JSON
with open(processed_reviews_file, 'w', encoding='utf-8') as file:
    json.dump(processed_reviews, file, indent=4, ensure_ascii=False)

print(f"Le recensioni processate sono state salvate in '{processed_reviews_file}'.")

# Genera il secondo file JSON
processed_addresses = generate_review_info(processed_reviews, addresses)

# Salva il secondo file JSON
with open(museums_with_reviews_file, 'w', encoding='utf-8') as file:
    json.dump(processed_addresses, file, indent=4, ensure_ascii=False)

print(f"I dati dei musei con informazioni sulle recensioni sono stati salvati in '{museums_with_reviews_file}'.")
