import csv
import json
#convertitore di file csv in file json
def convert_csv_to_json(input_csv, output_json):
    # Leggi il file CSV
    try:
        with open(input_csv, mode='r', encoding='utf-8') as csv_file:
            csv_reader = csv.DictReader(csv_file)

            # Converti ogni riga in un dizionario e aggiungila a una lista
            data = [row for row in csv_reader]

        # Scrivi i dati nel file JSON
        with open(output_json, mode='w', encoding='utf-8') as json_file:
            json.dump(data, json_file, indent=4, ensure_ascii=False)

        print(f"Conversione completata con successo! File JSON salvato in: {output_json}")
    except Exception as e:
        print(f"Errore durante la conversione: {e}")

# Specifica i percorsi dei file
input_csv = "Data_Review_British_Museum.csv"  # Percorso del file CSV di input
output_json = "Data_Review_British_Museum.json"  # Percorso del file JSON di output

# Esegui la conversione
convert_csv_to_json(input_csv, output_json)
