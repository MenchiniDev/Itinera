import pandas as pd

df = pd.read_csv("Hotel_Reviews.csv")
df.head()

countries = [
    'Italy', 'Spain', 'Portugal', 'Greece', 'Croatia', 'Albania', 'Andorra', 
    'Bosnia and Herzegovina', 'Gibraltar', 'United Kingdom', 'North Macedonia', 
    'Malta', 'Montenegro', 'San Marino', 'Serbia', 'Slovenia', 'Turkey'
]

df_combined = pd.DataFrame()

for country in countries:
    df_country = df[df['Hotel_Address'].str.contains(country, case=False, na=False)]
    df_combined = pd.concat([df_combined, df_country])

df_combined.to_csv('Hotel_Reviews_Choosen.csv', mode='w', index=False)
