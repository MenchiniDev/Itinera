import paramiko
import pandas as pd

# Dettagli del server
server_ip = "93.46.108.152"
server_port = 3000
username = "pi"
password = "pigrecoeasy"
remote_path = "/home/pi/shared/Hotel_Reviews.csv"  # Percorso remoto sul server
local_path = "./../data/Hotel_Reviews.csv"  # Percorso locale per salvare il file

# Connessione SSH e trasferimento del file
try:
    # Creazione del client SSH
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    ssh.connect(server_ip, port=server_port, username=username, password=password)

    # Uso di SCP per trasferire il file
    sftp = ssh.open_sftp()
    sftp.get(remote_path, local_path)
    sftp.close()
    ssh.close()

    print(f"File scaricato con successo da {server_ip} e salvato in {local_path}!")

except Exception as e:
    print(f"Errore durante il trasferimento del file: {e}")

# Lettura del file scaricato
df = pd.read_csv(local_path)

# Lista di paesi
countries = [
    'Italy', 'Spain', 'Portugal', 'Greece', 'Croatia', 'Albania', 'Andorra', 
    'Bosnia and Herzegovina', 'Gibraltar', 'United Kingdom', 'North Macedonia', 
    'Malta', 'Montenegro', 'San Marino', 'Serbia', 'Slovenia', 'Turkey'
]

# Filtro dei dati
df_combined = pd.DataFrame()
for country in countries:
    df_country = df[df['Hotel_Address'].str.contains(country, case=False, na=False)]
    df_combined = pd.concat([df_combined, df_country])

# Salvataggio dei dati filtrati
df_combined.to_csv('./../data/Hotel_Reviews_Choosen.csv', mode='w', index=False)
print("File 'Hotel_Reviews_Choosen.csv' salvato con successo!")
