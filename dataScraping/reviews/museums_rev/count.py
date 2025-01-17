import json

def count_json_objects(file_path):
    # Legge il file JSON
    with open(file_path, 'r', encoding='utf-8') as file:
        data = json.load(file)

    # Verifica se è un array
    if isinstance(data, list):
        count = len(data)
        print(f"Il file JSON contiene {count} oggetti.")
    else:
        print("Il file JSON non contiene un array di oggetti.")

# Specifica il percorso del file JSON
file_path = "Data_Review_British_Museum.json"
count_json_objects(file_path)
