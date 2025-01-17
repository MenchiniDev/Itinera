import json
import pandas as pd
from collections import OrderedDict

file_path = "New_York_reviews_no_NaN.json"
output_path = "output_results.txt"

# Carica il file JSON
with open(file_path, 'r', encoding='utf-8') as file:
    data = json.load(file)

# Converti i dati in un DataFrame
df = pd.DataFrame(data)

# Mantieni solo i campi specificati
fields_to_keep = ['restaurant_name', 'rating_review', 'review_full', 'date']
df = df[fields_to_keep]

# Conta il numero di recensioni per ciascun ristorante, preservando l'ordine
review_counts = OrderedDict()
for name in df['restaurant_name']:
    if name in review_counts:
        review_counts[name] += 1
    else:
        review_counts[name] = 1

# Filtra i ristoranti con almeno 50 recensioni
popular_restaurants = {name: count for name, count in review_counts.items() if count >= 50}

# Numero di ristoranti con almeno 50 recensioni
num_popular_restaurants = len(popular_restaurants)

# Filtra le recensioni per mantenere solo i ristoranti popolari
filtered_reviews = df[df['restaurant_name'].isin(popular_restaurants.keys())]

# Mantieni solo le prime 50 recensioni per ogni ristorante
limited_reviews = filtered_reviews.groupby('restaurant_name').head(50)

# Salva i risultati sul numero di recensioni dei ristoranti popolari
with open(output_path, 'w', encoding='utf-8') as txt_file:
    for restaurant in popular_restaurants.keys():
        txt_file.write(f"{restaurant}: {popular_restaurants[restaurant]} recensioni totali\n")

# Salva il file JSON con i dati filtrati
output_json_path = "NY_rev_postFirtsClean.json"
limited_reviews.to_json(output_json_path, orient='records', indent=4, force_ascii=False)

# Stampa i risultati
print(f"Numero di ristoranti con almeno 50 recensioni: {num_popular_restaurants}")
print(f"I risultati sono stati salvati in: {output_path}")
print(f"I dati filtrati sono stati salvati in: {output_json_path}")
