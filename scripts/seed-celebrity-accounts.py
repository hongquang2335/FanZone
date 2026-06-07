import os
from datetime import datetime, timezone

import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials
from firebase_admin import firestore


PROJECT_ID = os.environ.get("FIREBASE_PROJECT_ID", "fanzone-app")
DEFAULT_PASSWORD = os.environ.get("CELEBRITY_DEMO_PASSWORD", "FanZone@2026")


CELEBRITIES = [
    {
        "uid": "celeb-son-tung",
        "email": "son.tung.demo@fanzone.local",
        "displayName": "Son Tung M-TP",
        "role": "Nghe si am nhac",
        "topic": "San khau Neon Nights",
        "content": "Gap nhau o Neon Nights nhe. Team nao da san sang hat that lon cung minh?",
        "likes": 245000,
        "comments": 18200,
        "shareCount": 6430,
        "eventId": "neon-night",
        "eventTitle": "Neon Nights Festival 2024",
    },
    {
        "uid": "celeb-den-vau",
        "email": "den.vau.demo@fanzone.local",
        "displayName": "Den Vau",
        "role": "Rapper",
        "topic": "Loi hen voi fan",
        "content": "Co nhung cau rap chi that su song khi duoc nghe cung khan gia.",
        "likes": 189500,
        "comments": 9760,
        "shareCount": 4110,
        "eventId": "neon-night",
        "eventTitle": "Neon Nights Festival 2024",
    },
    {
        "uid": "celeb-my-tam",
        "email": "my.tam.demo@fanzone.local",
        "displayName": "My Tam",
        "role": "Ca si",
        "topic": "Cam on fan",
        "content": "Moi lan gap khan gia la mot lan duoc tiep them nang luong.",
        "likes": 312800,
        "comments": 21430,
        "shareCount": 7850,
        "eventId": None,
        "eventTitle": None,
    },
    {
        "uid": "celeb-hoang-thuy-linh",
        "email": "hoang.thuy.linh.demo@fanzone.local",
        "displayName": "Hoang Thuy Linh",
        "role": "Ca si",
        "topic": "Y tuong san khau",
        "content": "Dang thu nghiem mot ban phoi moi cho san khau sap toi.",
        "likes": 98400,
        "comments": 6210,
        "shareCount": 2340,
        "eventId": None,
        "eventTitle": None,
    },
    {
        "uid": "celeb-toc-tien",
        "email": "toc.tien.demo@fanzone.local",
        "displayName": "Toc Tien",
        "role": "Ca si",
        "topic": "Behind the scenes",
        "content": "Dang chon outfit cho dem dien tiep theo.",
        "likes": 124700,
        "comments": 5900,
        "shareCount": 3020,
        "eventId": None,
        "eventTitle": None,
    },
    {
        "uid": "celeb-suboi",
        "email": "suboi.demo@fanzone.local",
        "displayName": "Suboi",
        "role": "Rapper",
        "topic": "Hip-hop corner",
        "content": "Ai co beat yeu thich thi comment thu.",
        "likes": 76300,
        "comments": 4840,
        "shareCount": 1760,
        "eventId": None,
        "eventTitle": None,
    },
    {
        "uid": "celeb-binz",
        "email": "binz.demo@fanzone.local",
        "displayName": "Binz",
        "role": "Rapper",
        "topic": "Rap show",
        "content": "FanZone co ai muon nghe mot track moi o san khau sap toi khong?",
        "likes": 88900,
        "comments": 4320,
        "shareCount": 1980,
        "eventId": None,
        "eventTitle": None,
    },
    {
        "uid": "celeb-bich-phuong",
        "email": "bich.phuong.demo@fanzone.local",
        "displayName": "Bich Phuong",
        "role": "Ca si",
        "topic": "Fan request",
        "content": "Dang lap playlist cho dem dien moi.",
        "likes": 102600,
        "comments": 6780,
        "shareCount": 2250,
        "eventId": None,
        "eventTitle": None,
    },
]


def initialize_firebase():
    service_account_path = os.environ.get("FIREBASE_SERVICE_ACCOUNT")
    emulator_host = os.environ.get("FIRESTORE_EMULATOR_HOST")
    auth_emulator_host = os.environ.get("FIREBASE_AUTH_EMULATOR_HOST")

    if service_account_path:
        cred = credentials.Certificate(service_account_path)
    elif emulator_host or auth_emulator_host:
        cred = credentials.AnonymousCredentials()
    else:
        cred = credentials.ApplicationDefault()

    firebase_admin.initialize_app(cred, {"projectId": PROJECT_ID})


def upsert_auth_user(account):
    try:
        user = auth.get_user(account["uid"])
        auth.update_user(
            user.uid,
            email=account["email"],
            display_name=account["displayName"],
            email_verified=True,
            password=DEFAULT_PASSWORD,
            disabled=False,
        )
    except auth.UserNotFoundError:
        auth.create_user(
            uid=account["uid"],
            email=account["email"],
            display_name=account["displayName"],
            email_verified=True,
            password=DEFAULT_PASSWORD,
            disabled=False,
        )


def user_document(account):
    now = datetime.now(timezone.utc)
    return {
        "uid": account["uid"],
        "email": account["email"],
        "displayName": account["displayName"],
        "phone": "",
        "birthday": "",
        "gender": "",
        "avatarUri": None,
        "pinSet": False,
        "emailVerified": True,
        "updatedAt": int(now.timestamp() * 1000),
    }


def post_document(account):
    now = firestore.SERVER_TIMESTAMP
    return {
        "authorId": account["uid"],
        "author": account["displayName"],
        "role": account["role"],
        "topic": account["topic"],
        "content": account["content"],
        "likes": account["likes"],
        "likedBy": [],
        "comments": account["comments"],
        "shareCount": account["shareCount"],
        "imageUrl": None,
        "mediaUrl": None,
        "mediaType": None,
        "mediaItems": [],
        "eventId": account["eventId"],
        "eventTitle": account["eventTitle"],
        "sharedPost": None,
        "createdAt": now,
        "updatedAt": now,
    }


def main():
    initialize_firebase()
    db = firestore.client()

    for account in CELEBRITIES:
        upsert_auth_user(account)
        db.collection("users").document(account["uid"]).set(
            user_document(account),
            merge=True,
        )
        db.collection("communityPosts").document(f"post-{account['uid']}").set(
            post_document(account),
            merge=True,
        )
        print(f"Seeded Auth user, users/{account['uid']}, communityPosts/post-{account['uid']}")

    print(f"Done. Demo password for all celebrity accounts: {DEFAULT_PASSWORD}")


if __name__ == "__main__":
    main()
