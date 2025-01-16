import json
import random
from datetime import datetime, timedelta

def generate_random_datetime():
    start_date = datetime(2018, 1, 1)
    end_date = datetime(2024, 12, 31)
    random_date = start_date + timedelta(days=random.randint(0, (end_date - start_date).days))
    random_hour = random.randint(0, 23)
    random_minute = random.randint(0, 59)
    return random_date.replace(hour=random_hour, minute=random_minute)

def extract_numeric_rating(rating):
    """Estrae il numero dal formato 'X stars' e lo converte in un intero."""
    try:
        return int(rating.split()[0])  # Prende il primo elemento prima dello spazio
    except (ValueError, AttributeError, IndexError):
        return None

def filter_and_transform_cities(data, known_cities):
    filtered_data = []

    for entry in data:
        # Modifica del campo Monument se contiene il valore specificato
        if "Colosseum - Forum - Palatine Hill Circuit - Italy Rome" in entry.get("Monument", ""):
            entry["Monument"] = "The Colosseum - Rome, Italy"

        for city in known_cities:
            if city in entry.get("Monument", ""):
                # Generate a random timestamp using the helper function
                random_date = generate_random_datetime()
                timestamp = random_date.strftime("%Y-%m-%d %H:%M")

                # Rimuove la parte del campo Monument dopo il carattere "-"
                monument_name = entry.get("Monument", "").split("-")[0].strip()

                # Estrae il rating numerico
                numeric_rating = extract_numeric_rating(entry.get("Rating", ""))

                # Create the transformed object
                transformed_entry = {
                    "name": monument_name,
                    "username": "not signed yet",
                    "text": entry.get("Cleaned Reviews", ""),
                    "timestamp": timestamp,
                    "city": city,
                    "rating": numeric_rating
                }
                filtered_data.append(transformed_entry)
                break  # Assumiamo che un monumento appartenga a una sola città

    return filtered_data

# Legge il file JSON, trasforma i dati e salva un nuovo file

def main():
    input_file = "Reviews_Cleaned.json"
    output_file = "Reviews_Cleaned_and_reduced.json"

    european_cities = [
        "London", "Berlin", "Madrid", "Rome", "Paris", "Vienna", "Bucharest", "Hamburg", "Budapest", "Warsaw",
        "Barcelona", "Munich", "Milan", "Prague", "Sofia", "Brussels", "Birmingham", "Cologne", "Napoli", "Stockholm",
        "Turin", "Amsterdam", "Marseille", "Zagreb", "Valencia", "Krakow", "Frankfurt", "Seville", "Oslo", "Copenhagen",
        "Dublin", "Lisbon", "Helsinki", "Riga", "Tallinn", "Vilnius", "Luxembourg", "Ljubljana", "Bratislava", "Sarajevo",
        "Skopje", "Tirana", "Podgorica", "Reykjavik", "Valletta", "Florence", "Monaco", "San Marino", "Vaduz", "Pristina"
    ]

    try:
        with open(input_file, 'r', encoding='utf-8') as file:
            json_data = json.load(file)

        transformed_data = filter_and_transform_cities(json_data, european_cities)

        with open(output_file, 'w', encoding='utf-8') as output:
            json.dump(transformed_data, output, ensure_ascii=False, indent=4)

        print(f"File trasformato salvato in: {output_file}")

    except FileNotFoundError:
        print("File non trovato. Controlla il percorso e riprova.")
    except json.JSONDecodeError:
        print("Errore nel decodificare il file JSON. Assicurati che il file sia in formato JSON valido.")

if __name__ == "__main__":
    main()
