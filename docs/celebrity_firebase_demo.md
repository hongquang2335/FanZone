# Celebrity Firebase demo note

## Goal

Seed demo celebrity accounts into Firebase Authentication and Firestore while keeping the same base `users/{uid}` shape used by the app for normal accounts.

The app identifies a profile by Firebase Auth UID:

- Firebase Authentication user `uid`
- Firestore document `users/{uid}`
- Community post field `authorId`

These three values must be the same. Example:

```text
Auth UID: celeb-son-tung
Firestore user: users/celeb-son-tung
Post authorId: celeb-son-tung
```

When a post avatar or author name is tapped, the app navigates to `profile/{authorId}` and renders that profile.

## Current user document shape

The app currently creates and reads these user fields:

```text
uid
email
displayName
phone
birthday
gender
avatarUri
pinSet
emailVerified
updatedAt
```

The seed script writes the same base fields for celebrity accounts.

## Seeded demo accounts

All accounts use this default password unless overridden:

```text
FanZone@2026
```

Demo emails:

```text
son.tung.demo@fanzone.local
den.vau.demo@fanzone.local
my.tam.demo@fanzone.local
hoang.thuy.linh.demo@fanzone.local
toc.tien.demo@fanzone.local
suboi.demo@fanzone.local
binz.demo@fanzone.local
bich.phuong.demo@fanzone.local
```

## Run against local emulators

Start Firestore/Auth emulators, then run:

```powershell
$env:FIREBASE_PROJECT_ID="fanzone-app"
$env:FIRESTORE_EMULATOR_HOST="localhost:8080"
$env:FIREBASE_AUTH_EMULATOR_HOST="localhost:9099"
python scripts/seed-celebrity-accounts.py
```

## Run against real Firebase

1. In Firebase Console, enable Authentication > Sign-in method > Email/Password.
2. Create or download a Firebase Admin service account JSON.
3. Run:

```powershell
$env:FIREBASE_PROJECT_ID="your-firebase-project-id"
$env:FIREBASE_SERVICE_ACCOUNT="C:\path\to\service-account.json"
$env:CELEBRITY_DEMO_PASSWORD="FanZone@2026"
python scripts/seed-celebrity-accounts.py
```

## Demo flow

1. Open the app.
2. Go to Community.
3. Tap a celebrity avatar or author name on any seeded post.
4. The public profile page opens.
5. Tap Follow to toggle follow state.
6. To demo login identity, sign in with one of the seeded email accounts above. Firebase Auth returns the same UID used by `users/{uid}`.

## Notes

- The blue verified badge is currently driven by the app's local celebrity profile list using the same UID values.
- If you change a celebrity UID in Firebase, update the same UID in `SocialProfile.kt` and seeded post `authorId`.
- Google login still requires a real `google_web_client_id` in `app/src/main/res/values/strings.xml`.
