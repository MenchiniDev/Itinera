import pandas as pd
import kaggle

# Autenticazione automatica (usa il file kaggle.json)
kaggle.api.authenticate()

# Scarica il dataset
kaggle.api.dataset_download_files(
    'jiashenliu/515k-hotel-reviews-data-in-europe', 
    path='./Kaggle', 
    unzip=True
)

df = pd.read_csv("Hotel_Reviews.csv")

# Lista di paesi
countries = [
    'Italy', 'Spain', 'Portugal', 'Greece', 'Croatia', 'Albania', 'Andorra', 
    'Bosnia and Herzegovina', 'Gibraltar', 'United Kingdom', 'North Macedonia', 
    'Malta', 'Montenegro', 'San Marino', 'Serbia', 'Slovenia', 'Turkey'
]

#filter data
df_combined = pd.DataFrame()
for country in countries:
    df_country = df[df['Hotel_Address'].str.contains(country, case=False, na=False)]
    df_combined = pd.concat([df_combined, df_country])

#saving data
df_combined.to_csv('Hotel_Reviews_Choosen.csv', mode='w', index=False)
print("File 'Hotel_Reviews_Choosen.csv' salvato con successo!")
