import argparse
import json
import os
from datetime import datetime, timezone
from pathlib import Path

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


PROJECT_ID = os.environ.get("FIREBASE_PROJECT_ID", "fanzone-app")
SEED_PATH = Path(__file__).with_name("post_seed.json")
COLLECTIONS_TO_DELETE = ("communityPosts", "posts")
TARGET_COLLECTION = "posts"

STANDARD_POST_FIELDS = {
    "authorId": None,
    "author": None,
    "role": None,
    "topic": None,
    "content": None,
    "likes": 0,
    "likedBy": [],
    "comments": 0,
    "shareCount": 0,
    "imageUrl": None,
    "mediaUrl": None,
    "mediaType": None,
    "mediaItems": [],
    "eventId": None,
    "eventTitle": None,
    "sharedPost": None,
    "createdAt": None,
    "updatedAt": None,
}


def initialize_firebase():
    service_account_path = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
    emulator_host = os.environ.get("FIRESTORE_EMULATOR_HOST")

    if service_account_path:
        cred = credentials.Certificate(service_account_path)
    elif emulator_host:
        cred = credentials.AnonymousCredentials()
    else:
        cred = credentials.ApplicationDefault()

    firebase_admin.initialize_app(cred, {"projectId": PROJECT_ID})


def parse_timestamp(value):
    if value is None:
        return None
    if isinstance(value, datetime):
        return value
    if isinstance(value, str):
        normalized = value.replace("Z", "+00:00")
        return datetime.fromisoformat(normalized)
    return value


def normalize_post(raw_post):
    post_id = raw_post.get("id")
    if not post_id:
        raise ValueError("Each seed post needs an id.")

    data = dict(STANDARD_POST_FIELDS)
    for field in STANDARD_POST_FIELDS:
        if field in raw_post:
            data[field] = raw_post[field]

    data["likedBy"] = data["likedBy"] or []
    data["mediaItems"] = data["mediaItems"] or []
    data["createdAt"] = parse_timestamp(data["createdAt"]) or datetime.now(timezone.utc)
    data["updatedAt"] = parse_timestamp(data["updatedAt"]) or datetime.now(timezone.utc)
    return post_id, data


def delete_collection(db, collection_name, batch_size=100):
    collection_ref = db.collection(collection_name)
    deleted = 0

    while True:
        docs = list(collection_ref.limit(batch_size).stream())
        if not docs:
            break

        batch = db.batch()
        for doc in docs:
            batch.delete(doc.reference)
        batch.commit()
        deleted += len(docs)

    print(f"Deleted {deleted} documents from {collection_name}.")


def seed_posts(db):
    raw_posts = json.loads(SEED_PATH.read_text(encoding="utf-8"))
    for raw_post in raw_posts:
        post_id, data = normalize_post(raw_post)
        db.collection(TARGET_COLLECTION).document(post_id).set(data)
        print(f"Seeded {TARGET_COLLECTION}/{post_id}")


def main():
    parser = argparse.ArgumentParser(
        description="Delete old community post collections and seed the standard posts collection."
    )
    parser.add_argument(
        "--confirm-delete",
        action="store_true",
        help="Required. Deletes communityPosts and posts before seeding posts.",
    )
    args = parser.parse_args()

    if not args.confirm_delete:
        raise SystemExit("Refusing to delete data. Re-run with --confirm-delete.")

    initialize_firebase()
    db = firestore.client()

    for collection_name in COLLECTIONS_TO_DELETE:
        delete_collection(db, collection_name)
    seed_posts(db)
    print("Done resetting Firestore posts.")


if __name__ == "__main__":
    main()
