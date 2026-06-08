# Community post Firestore schema

Community now uses only Firestore collection:

```text
posts
posts/{postId}/comments
posts/{postId}/shares
```

The old collection `communityPosts` is deprecated and should be deleted after migration/reset.

## Standard document fields

Every document in `posts/{postId}` should contain these fields. If a value is not available, keep the field and set it to `null`, except arrays which should be empty arrays.

```text
type: "post" | "share"
authorId: string
author: string
authorAvatarUrl: string | null
authorFollowerCount: number
authorFollowingCount: number
isAuthorFollowed: boolean
role: string
topic: string
content: string
likes: number
likedBy: string[]
comments: number
shareCount: number
imageUrl: string | null
mediaUrl: string | null
mediaType: string | null
mediaItems: { url: string, type: string }[]
eventId: string | null
eventTitle: string | null
originalPostId: string | null
resharedFromPostId: string | null
sharedPost: {
  postId: string,
  authorId: string,
  author: string,
  authorAvatarUrl: string | null,
  content: string,
  mediaItems: { url: string, type: string }[],
  eventId: string | null,
  eventTitle: string | null
} | null
createdAt: timestamp
updatedAt: timestamp
```

`comments` is only a counter. Comment data lives in `posts/{postId}/comments/{commentId}`:

```text
commentId: string
postId: string
authorId: string
authorName: string
authorAvatarUrl: string | null
text: string
mediaUrl: string | null
mediaType: string | null
mediaItems: { url: string, type: string }[]
likes: number
likedBy: string[]
createdAt: timestamp
updatedAt: timestamp
```

Each share creates a feed post with `type = "share"` and always points back to the root original post via `originalPostId`. The clicked shared post can be tracked with `resharedFromPostId`, but the rendered preview is still the original post.

## Required for UI rendering

These fields must be non-null for a post to render correctly:

```text
authorId
author
role
topic
content
likes
likedBy
comments
shareCount
createdAt
updatedAt
```

## Optional fields

These fields may be `null`:

```text
imageUrl
mediaUrl
mediaType
eventId
eventTitle
sharedPost
```

`mediaItems` should be an empty array when there is no media.

## Identity link

Profile navigation uses:

```text
posts/{postId}.authorId -> users/{uid}
```

So `authorId` must match the Firebase Authentication UID and Firestore user document ID.

## Reset/seed script

Use:

```powershell
python scripts/reset-firestore-posts.py --confirm-delete
```

The script deletes:

```text
communityPosts
posts
```

Then it seeds `posts` from:

```text
scripts/post_seed.json
```
