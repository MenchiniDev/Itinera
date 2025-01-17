import json

def count_and_list_unique_monuments(input_file, output_file):
    try:
        # Legge i dati dal file JSON
        with open(input_file, 'r', encoding='utf-8') as file:
            data = json.load(file)

        # Estrae i valori unici del campo "Monument"
        unique_monuments = set()
        for entry in data:
            monument = entry.get("name", None)
            if monument:
                unique_monuments.add(monument)

        # Scrive i monumenti unici in un file di testo
        with open(output_file, 'w', encoding='utf-8') as file:
            for monument in sorted(unique_monuments):
                file.write(monument + "\n")

        # Restituisce il numero di monumenti unici trovati
        print(f"Numero di monumenti unici trovati: {len(unique_monuments)}")
        print(f"Elenco salvato in: {output_file}")

    except FileNotFoundError:
        print("File non trovato. Controlla il percorso e riprova.")
    except json.JSONDecodeError:
        print("Errore nel decodificare il file JSON. Assicurati che il file sia in formato JSON valido.")

if __name__ == "__main__":
    input_file = "Reviews_Cleaned_and_reduced.json"
    output_file = "monum.txt"
    count_and_list_unique_monuments(input_file, output_file)
