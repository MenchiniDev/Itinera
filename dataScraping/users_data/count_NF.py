import json

def count_objects_with_and_without_last_post(input_file):
    # Carica i dati dal file JSON
    with open(input_file, 'r', encoding='utf-8') as file:
        data = json.load(file)

    # Inizializza i contatori
    with_last_post = 0
    without_last_post = 0

    # Conta gli oggetti
    for obj in data:
        if 'last_post' in obj:
            with_last_post += 1
        else:
            without_last_post += 1

    # Stampa i risultati
    print(f"Oggetti con 'Last_post': {with_last_post}")
    print(f"Oggetti senza 'Last_post': {without_last_post}")

# Percorso del file JSON di input
input_file = "users.json"

# Esegui la funzione
count_objects_with_and_without_last_post(input_file)
