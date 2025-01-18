import json
from collections import defaultdict

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
        # Creazione del JSON per le recensioni
        review_entry = {
            "Place_name": review.get("name", ""),
            "Text": review.get("text", ""),
            "Timestamp": review.get("timestamp", ""),
            "Rev_id": review_id,
            "User": usernames[username_index],
            "Stars": review.get("stars", 0),
            "Reported": False
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
            "Overall_rating": round(sum(stars) / len(stars), 2),
            "Tot_rev_number": len(stars)
        }
        hotel_entry = {
            "Id": hotel_id,
            "Name": name,
            "Address": address,
            "City": city,
            "Category": "Hotel",
            "Reviews_info": review_info
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
