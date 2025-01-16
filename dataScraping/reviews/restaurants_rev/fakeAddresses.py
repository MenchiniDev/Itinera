import random
import faker
import json

# Lista di città europee
cities = [
    "London", "Berlin", "Madrid", "Rome", "Paris", "Vienna", "Bucharest", "Hamburg", "Budapest", "Warsaw",
    "Barcelona", "Munich", "Milan", "Prague", "Sofia", "Brussels", "Birmingham", "Cologne", "Napoli", "Stockholm",
    "Turin", "Amsterdam", "Marseille", "Zagreb", "Valencia", "Krakow", "Frankfurt", "Seville", "Oslo", "Copenhagen",
    "Dublin", "Lisbon", "Helsinki", "Riga", "Tallinn", "Vilnius", "Luxembourg", "Ljubljana", "Bratislava", "Sarajevo",
    "Skopje", "Tirana", "Podgorica", "Reykjavik", "Valletta", "Travel", "Monaco", "San Marino", "Vaduz", "Pristina"
]

# Numero di indirizzi da generare per città
address_count = {city: 25 if index < 21 else 24 for index, city in enumerate(cities)}

# Generatore di dati fittizi
fake = faker.Faker()

# Genera indirizzi fittizi senza duplicati
addresses = []
for city, count in address_count.items():
    generated_for_city = set()
    while len(generated_for_city) < count:
        new_address = f"{fake.building_number()} {fake.street_name()}"
        if new_address not in generated_for_city:
            generated_for_city.add(new_address)
            addresses.append({
                "address": new_address,
                "city": city
            })

# Salva gli indirizzi in un file JSON
output_file = "addresses.json"
with open(output_file, "w", encoding="utf-8") as f:
    json.dump(addresses, f, indent=4)

print(f"Generati {len(addresses)} indirizzi univoci e salvati in '{output_file}'")
