import faker
import random
from datetime import datetime, timedelta
import json
import os

def generate_fake_data(input_file, output_file, posts_folder):
    fake = faker.Faker()

    user_data = []

    current_id = 0

    # Leggi il file di input con gli usernames
    with open(input_file, 'r') as infile:
        usernames = [line.strip() for line in infile if line.strip()]  # Rimuove righe vuote

    # Genera dati per ogni username
    for username in usernames:
        email = f"{username.lower()}@{fake.free_email_domain()}"  # Genera email
        password = fake.password(length=12)  # Genera password fittizia

        # Genera una data casuale nel 2017 con ora
        start_date = datetime(2017, 1, 1)
        end_date = datetime(2017, 12, 31)
        random_days = random.randint(0, (end_date - start_date).days)
        random_date = start_date + timedelta(days=random_days)
        random_hour = random.randint(0, 23)
        random_minute = random.randint(0, 59)
        final_date = random_date.replace(hour=random_hour, minute=random_minute)
        formatted_date = final_date.strftime("%Y-%m-%d %H:%M")  # Formatta la data con ora

        latest_post = None
        latest_timestamp = None

        # Scorri tutti i file JSON nella cartella
        for filename in os.listdir(posts_folder):
            if filename.endswith(".json"):
                file_path = os.path.join(posts_folder, filename)
                with open(file_path, 'r', encoding='utf-8') as post_file:
                    post_data = json.load(post_file)

                    if post_data.get("Username") == username:
                        # Confronta i timestamp per trovare il più recente
                        post_timestamp = datetime.strptime(post_data["Timestamp"], "%Y-%m-%d %H:%M:%S")
                        if latest_timestamp is None or post_timestamp > latest_timestamp:
                            latest_timestamp = post_timestamp
                            latest_post = {
                                "Post_body": post_data["Post_body"],
                                "Timestamp": post_data["Timestamp"]
                            }

        # Crea l'oggetto utente
        user = {
            "Id": current_id,
            "Username": username,
            "Email": email,
            "Password": password,
            "Created": formatted_date,
            "Active": False,
            "Reported": False,
            "Role": "User"
        }

        # Aggiungi il campo Last_post solo se trovato
        if latest_post is not None:
            user["Last_post"] = latest_post

        user_data.append(user)
        print(f"[INFO] Utente {current_id} completato.")
        current_id += 1

    # Salva i dati generati nel file JSON
    with open(output_file, 'w') as outfile:
        json.dump(user_data, outfile, indent=4)

    print(f"Dati generati con successo e salvati in '{output_file}'")

input_file = "usernames.txt"  
output_file = "users.json"  
posts_folder = "../Post_doc"  

# Esegui la funzione
generate_fake_data(input_file, output_file, posts_folder)
