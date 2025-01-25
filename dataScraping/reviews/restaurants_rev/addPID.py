import json

# Carica i dati dei file JSON
with open('restaurants.json', 'r',encoding='utf-8') as places_file:
    places_data = json.load(places_file)

with open('reviews_restaurants.json', 'r',encoding='utf-8') as reviews_file:
    reviews_data = json.load(reviews_file)

# Crea un dizionario per una ricerca più rapida degli ID dei posti
places_dict = {place['name']: place['_id'] for place in places_data}

# Aggiungi place_id a ogni recensione corrispondente
for review in reviews_data:
    place_name = review.get('place_name')
    if place_name in places_dict:
        review['place_id'] = places_dict[place_name]

# Rimuovi il campo place_name dopo averlo utilizzato
for review in reviews_data:
    if 'place_name' in review:
        del review['place_name']

# Salva i dati aggiornati delle recensioni su un nuovo file JSON
with open('reviews_restaurants.json', 'w',encoding='utf-8') as updated_reviews_file:
    json.dump(reviews_data, updated_reviews_file, indent=4)

print("Le recensioni sono state aggiornate con il campo 'place_id'.")
