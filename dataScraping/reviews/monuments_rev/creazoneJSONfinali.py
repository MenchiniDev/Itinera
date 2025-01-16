import json
# Funzione per generare il primo file JSON
def generate_first_file(input_file, username_file, output_file):
    try:
        # Legge i dati dal file JSON originale
        with open(input_file, 'r', encoding='utf-8') as file:
            data = json.load(file)

        # Legge i nomi utente dal file di testo
        with open(username_file, 'r', encoding='utf-8') as user_file:
            usernames = [line.strip() for line in user_file.readlines()]

        if not usernames:
            raise ValueError("Il file usernames.txt è vuoto!")

        # Genera i dati trasformati
        transformed_data = []
        rev_id = 120000
        username_index = 0

        for entry in data:
            transformed_entry = {
                "place_name": entry.get("name", ""),
                "text": entry.get("text", ""),
                "timestamp": entry.get("timestamp", ""),
                "rev_id": rev_id,  # Genera un ID univoco
                "user": usernames[username_index],  # Assegna username in modo ciclico
                "stars": entry.get("rating", 0),
                "reported": False  # Imposta reported su false
            }
            transformed_data.append(transformed_entry)
            rev_id += 1  # Incrementa l'ID per ogni recensione
            username_index = (username_index + 1) % len(usernames)  # Cicla sugli username

        # Salva i dati trasformati nel file di output
        with open(output_file, 'w', encoding='utf-8') as output:
            json.dump(transformed_data, output, ensure_ascii=False, indent=4)

        print(f"Primo file JSON generato e salvato in: {output_file}")

    except FileNotFoundError as e:
        print(f"Errore: {e}")
    except json.JSONDecodeError:
        print("Errore nel decodificare il file JSON. Assicurati che il file sia in formato JSON valido.")


# Funzione per leggere gli indirizzi associati ai nomi dei monumenti nel file di testo
def load_addresses(address_file):
    try:
        with open(address_file, 'r', encoding='utf-8') as addr_file:
            address_map = {}
            for line in addr_file:
                if "->" in line:
                    name, address = line.split("->")
                    address_map[name.strip()] = address.strip()
            return address_map
    except FileNotFoundError:
        raise FileNotFoundError("Il file addresses.txt non è stato trovato.")
    
    
    
# Funzione per trovare l'indirizzo con corrispondenza esatta da address_map creato con la precedente funzione
def find_address(name, address_map):
    return address_map.get(name, "") 




# Funzione per generare il secondo file JSON 
def generate_second_file(input_file, address_file, output_file):
    try:
        # Legge i dati dal file JSON originale
        with open(input_file, 'r', encoding='utf-8') as file:
            data = json.load(file)

        # Legge gli indirizzi dal file di testo
        address_map = load_addresses(address_file)
        if not address_map:
            raise ValueError("Il file addresses.txt è vuoto o non contiene dati validi")

        # Genera i dati trasformati
        transformed_data = []
        id_counter = 3000 #parto da 3000 per evitare confusione con gli altri places che avranno un id minore

        # Raggruppa recensioni per nome del monumento
        reviews_by_name = {}
        cities_by_name = {}
        for entry in data:
            name = entry.get("name", "")
            city = entry.get("city", "")
            if name not in reviews_by_name:
                reviews_by_name[name] = []
                cities_by_name[name] = city  # salvo la città associata
            reviews_by_name[name].append(entry)

        for name, reviews in reviews_by_name.items():
            # Calcola la media delle recensioni e il conteggio
            total_rating = sum(review.get("rating", 0) for review in reviews)
            review_count = len(reviews)
            average_rating = total_rating / review_count if review_count > 0 else 0

            transformed_entry = {
                "id": id_counter,
                "name": name,  # Usa il nome originale dalla prima recensione
                "address": find_address(name, address_map),  # Cerca l'indirizzo
                "city": cities_by_name.get(name, ""),  # Ottieni la città associata
                "category": "museum" if name == "Musee du Louvre" else "monument", # I monumenti su kaggle contengono anche Louvre, che noi consideriamo come museo
                "reviews_info": {
                    "overall_rating": round(average_rating, 2),
                    "tot_rev_number": review_count
                }
            }
            transformed_data.append(transformed_entry)
            id_counter += 1  # Incrementa l'ID per ogni monumento

        # Salva i dati trasformati nel file di output
        with open(output_file, 'w', encoding='utf-8') as output:
            json.dump(transformed_data, output, ensure_ascii=False, indent=4)

        print(f"Secondo file JSON generato e salvato in: {output_file}")

    except FileNotFoundError as e:
        print(f"Errore: {e}")
    except json.JSONDecodeError:
        print("Errore nel decodificare il file JSON. Assicurati che il file sia in formato JSON valido.")

# Parametri di input e output
input_file = "Reviews_Cleaned_and_reduced.json"  
username_file = "../../users_data/usernames.txt"  
output_file_1 = "reviews_monuments.json"  
address_file = "monuments_addresses.txt"  
output_file_2 = "monuments.json" 

# Generazione del primo file JSON
generate_first_file(input_file, username_file, output_file_1)

# Generazione del secondo file JSON
generate_second_file(input_file, address_file, output_file_2)
