import pandas as pd
import praw
import prawcore
import json
from geopy.geocoders import Nominatim
import googlemaps
import os

api_key = "AIzaSyByUaM5K3h4oVamDjGG9RImuLlcaHLt_EY"

european_cities = [
    "London", "Berlin", "Madrid", "Rome", "Paris", "Vienna", "Bucharest", "Hamburg", "Budapest", "Warsaw",
    "Barcelona", "Munich", "Milan", "Prague", "Sofia", "Brussels", "Birmingham", "Cologne", "Napoli", "Stockholm",
    "Turin", "Amsterdam", "Marseille", "Zagreb", "Valencia", "Krakow", "Frankfurt", "Seville", "Oslo", "Copenhagen",
    "Dublin", "Lisbon", "Helsinki", "Riga", "Tallinn", "Vilnius", "Luxembourg", "Ljubljana", "Bratislava", "Sarajevo",
    "Skopje", "Tirana", "Podgorica", "Reykjavik", "Valletta", "Travel", "Monaco", "San Marino", "Vaduz", "Pristina"
]


def extract_city(address):
    """
    Estrae la città da un indirizzo usando Google Maps API.
    """
    gmaps = googlemaps.Client(key=api_key)
    try:
        geocode_result = gmaps.geocode(address)
        if (geocode_result):
            print(f"Indirizzo: {address}, Risultato: {geocode_result[0]['formatted_address']}")
            # Cerca la componente della località
            for component in geocode_result[0]['address_components']:
                if 'locality' in component['types']:  # Cerca il tipo "locality"
                    return component['long_name']
            # Fallback: Se non trova 'locality', cerca 'administrative_area_level_1' o altri livelli
            for component in geocode_result[0]['address_components']:
                if 'administrative_area_level_1' in component['types']:
                    return component['long_name']
        print(f"Indirizzo non trovato: {address}")
    except Exception as e:
        print(f"Errore durante l'elaborazione dell'indirizzo '{address}': {e}")
    return address  # Ritorna l'indirizzo originale come fallback

def save_addresses_to_csv(addresses, filename):
    """
    Salva una lista di indirizzi in un file CSV.
    """
    df = pd.DataFrame(addresses, columns=["Address"])
    df.to_csv(filename, index=False)


reddit = praw.Reddit(
    client_id="aU5OMoCcld7EW3xopXdovw",
    client_secret="B3gUogmxBcJ_u5gaNXG6WYDDsEai1Q",
    user_agent="script",
)

df_reviews = None

print("Current working directory:", os.getcwd())  #debug

if os.path.exists("itinera/data/normalized_addresses.csv"):
    df_reviews = pd.read_csv("itinera/data/normalized_addresses.csv")["Hotel_Address"]
    print("Il file 'data/normalized_addresses.csv' esiste già. Nessuna elaborazione necessaria.")
else:
    df_reviews = pd.read_csv('itinera/data/original/Hotel_Reviews_Choosen.csv')
    df_reviews = df_reviews["Hotel_Address"].drop_duplicates()
    df_reviews = df_reviews.dropna()
    print("Normalizzazione degli indirizzi in corso...")
    # Estrai la città da ogni indirizzo
    df_reviews = df_reviews.apply(extract_city)
    # Salva i risultati in un file CSV
    df_reviews = df_reviews.drop_duplicates()
    df_reviews.to_csv("itinera/data/normalized_addresses.csv", index=False)



df_european_cities = pd.DataFrame(european_cities, columns=["Hotel_Address"])
df_reviews = pd.concat([df_reviews, df_european_cities["Hotel_Address"]])
df_reviews = df_reviews.dropna()

subreddit_list = []
print(df_reviews)

for city in df_reviews:
    try:
        subreddit = reddit.subreddit(city)
        subreddit.id
        subreddit_list.append(subreddit)
    except prawcore.exceptions.Forbidden:
        print(f"Subreddit '{city}' non trovato.")
        search_results = reddit.subreddits.search(city, limit=5)
        found_subreddit = None
        for result in search_results:
            found_subreddit = result
            break
        if found_subreddit:
            print(f"Trovato subreddit simile: {found_subreddit.display_name}")
            subreddit_list.append(found_subreddit)
        else:
            print(f"Nessun subreddit trovato per '{city}', uso un fallback generico.")
            subreddit_list.append(reddit.subreddit("travel"))
    except prawcore.exceptions.Redirect:
        print(f"Subreddit '{city}' non trovato. Provo una ricerca.")
        search_results = list(reddit.subreddits.search(city))
        if search_results:
            print(f"Trovato subreddit simile: {search_results[0].display_name}")
            subreddit_list.append(search_results[0])
    except prawcore.exceptions.NotFound:
        print(f"Nessun SubReddit '{city}' .")


# Analizza i post nei subreddit
for sub in subreddit_list:
    try:
        top_posts = []  # Lista per accumulare fino a 10 post
        for post in sub.hot(limit=10):  # Prendi fino a 10 post
            top_posts.append(post)
    except prawcore.exceptions.Forbidden:
        print(f"Non posso accedere a '{sub.display_name}'.")
        continue

    # Elabora ogni post individualmente
    for i, top_post in enumerate(top_posts):
        if top_post:
            print(f"Post selezionato: {top_post.title} (Score: {top_post.score})")
            # Carica tutti i commenti del post
            try:
                top_post.comments.replace_more(limit=None)  # Espande tutti i commenti
                comments = []

                for comment in top_post.comments.list():
                    comments.append({
                        "author": str(comment.author),
                        "body": comment.body,
                        "score": comment.score,
                        "created_utc": comment.created_utc
                    })

                # Salva i dati in un file JSON per ogni post
                output = {
                    "post_title": top_post.title,
                    "post_author": str(top_post.author),
                    "post_score": top_post.score,
                    "post_url": top_post.url,
                    "comments": comments
                }

                with open(f"itinera/dataScraping/posts2/top_post_{sub.display_name}_{i+1}.json", "w", encoding="utf-8") as f:
                    json.dump(output, f, ensure_ascii=False, indent=4)

                print(f"Dati salvati in top_post_{sub.display_name}_{i+1}.json")

            except prawcore.exceptions.Forbidden:
                print(f"Non posso accedere ai commenti di '{sub.display_name}'.")

        else:
            print(f"Nessun post trovato per '{sub.display_name}'.")
