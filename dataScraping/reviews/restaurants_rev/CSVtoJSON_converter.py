import csv
import json
import math

def convert_csv_to_json(input_csv, output_json):
    # Leggi il file CSV
    try:
        with open(input_csv, mode='r', encoding='utf-8') as csv_file:
            csv_reader = csv.DictReader(csv_file)

            # Filtro per eliminare righe con valori null o NaN
            data = []
            removed_count = 0

            for row in csv_reader:
                # Controlla se ci sono valori nulli o NaN nella riga
                if any(value is None or value.strip() == "" or (isinstance(value, float) and math.isnan(value)) for value in row.values()):
                    removed_count += 1  # Incrementa il contatore per ogni riga rimossa
                else:
                    data.append(row)  # Aggiungi solo righe valide

        # Scrivi i dati nel file JSON
        with open(output_json, mode='w', encoding='utf-8') as json_file:
            json.dump(data, json_file, indent=4, ensure_ascii=False)

        print(f"Conversione completata con successo! File JSON salvato in: {output_json}")
        print(f"Numero di oggetti eliminati: {removed_count}")

    except Exception as e:
        print(f"Errore durante la conversione: {e}")

# Specifica i percorsi dei file
input_csv = "New_York_reviews.csv"  # Percorso del file CSV di input
output_json = "New_York_reviews_no_NaN.json"  # Percorso del file JSON di output

# Esegui la conversione
convert_csv_to_json(input_csv, output_json)
