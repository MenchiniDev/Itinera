import praw
import json

# Configura PRAW con le credenziali
reddit = praw.Reddit(
    client_id="aU5OMoCcld7EW3xopXdovw",
    client_secret="B3gUogmxBcJ_u5gaNXG6WYDDsEai1Q",
    user_agent="script_per_r_milan",
)

subreddit = reddit.subreddit("rome")

# Trova il post con il punteggio più alto in modalità 'hot'
top_post = None
for post in subreddit.hot(limit=1):  # Prendi il primo post
    top_post = post

if top_post:
    print(f"Post selezionato: {top_post.title} (Score: {top_post.score})")

    # Carica tutti i commenti del post
    top_post.comments.replace_more(limit=None)  # Espande tutti i commenti "MoreComments"
    comments = []
    
    for comment in top_post.comments.list():  # Ottieni una lista piatta di commenti
        comments.append({
            "author": str(comment.author),
            "body": comment.body,
            "score": comment.score,
            "created_utc": comment.created_utc
        })
    
    # Salva i dati in un file JSON
    output = {
        "post_title": top_post.title,
        "post_author": str(top_post.author),
        "post_score": top_post.score,
        "post_url": top_post.url,
        "comments": comments
    }
    
    with open("top_post_comments.json", "w", encoding="utf-8") as f:
        json.dump(output, f, ensure_ascii=False, indent=4)

    print("Dati salvati in 'top_post_comments.json'")
else:
    print("Nessun post trovato.")