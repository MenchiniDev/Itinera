import json
from geopy.geocoders import Nominatim
from time import sleep

def generate_addresses(museums):
    geolocator = Nominatim(user_agent="museum_locator")
    updated_museums = []

    for museum in museums:
        try:
            # Genera la query per cercare il museo nella città specificata
            query = f"{museum['name']}, {museum['city']}"
            location = geolocator.geocode(query, timeout=10)
            sleep(1)  # Evita di sovraccaricare il servizio

            if location:
                museum["address"] = location.address
            else:
                museum["address"] = "Address not found"

            updated_museums.append(museum)
        except Exception as e:
            print(f"Errore durante la ricerca per il museo: {museum['name']} - {e}")
            museum["address"] = "Error retrieving address"
            updated_museums.append(museum)

    return updated_museums

# Dati di input
museums = [
    {"name": "Pergamon Museum", "city": "Berlin"},
    {"name": "Museo del Prado", "city": "Madrid"},
    {"name": "Kunsthistorisches Museum", "city": "Vienna"},
    {"name": "National Museum of Art of Romania", "city": "Bucharest"},
    {"name": "Hungarian National Gallery", "city": "Budapest"},
    {"name": "Pinacoteca di Brera", "city": "Milan"},
    {"name": "National Museum (Národní muzeum)", "city": "Prague"},
    {"name": "Royal Museums of Fine Arts of Belgium", "city": "Brussels"},
    {"name": "Birmingham Museum and Art Gallery", "city": "Birmingham"},
    {"name": "Egyptian Museum", "city": "Turin"},
    {"name": "British Museum", "city": "London"}
]


# Genera gli indirizzi
museums_with_addresses = generate_addresses(museums)

# Salva in un file JSON
output_file = "museums_addresses.json"
with open(output_file, 'w', encoding='utf-8') as json_file:
    json.dump(museums_with_addresses, json_file, indent=4, ensure_ascii=False)

print(f"Dati salvati in '{output_file}'.")

