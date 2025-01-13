import faker
import random
from datetime import datetime, timedelta
import json

def generate_fake_data(input_file, output_file):
    # Generatore di dati fittizi
    fake = faker.Faker()

    # Lista per memorizzare i dati generati
    user_data = []

    # Leggi il file di input con gli usernames
    with open(input_file, 'r') as infile:
        usernames = [line.strip() for line in infile if line.strip()]  # Rimuove righe vuote

    # Genera dati per ogni username
    for idx, username in enumerate(usernames, start=1):
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

        # Aggiungi i dati generati alla lista come oggetti JSON
        user_data.append({
            "id": f"{idx:05d}",  # ID a 5 cifre
            "username": username,
            "email": email,
            "password": password,
            "created": formatted_date,
            "active": False,  # Campo booleano
            "banned": False,  # Campo booleano
            "reported": False,  # Campo booleano
            "last_post": [],  # Array vuoto
            "role": "user"  # Campo fisso
        })

    # Salva i dati generati nel file JSON
    with open(output_file, 'w') as outfile:
        json.dump(user_data, outfile, indent=4)

    print(f"Dati generati con successo e salvati in '{output_file}'")

# Specifica i percorsi dei file
input_file = "usernames.txt"  # File con gli usernames
output_file = "user_fakeData_with_time.json"  # File di output in formato JSON

# Esegui la funzione
generate_fake_data(input_file, output_file)
