param(
    [string]$GoogleServicesPath = "app/google-services.json"
)

$ErrorActionPreference = "Stop"

function U {
    param([string]$Text)
    return [System.Text.RegularExpressions.Regex]::Unescape($Text)
}

$config = Get-Content -Raw -Path $GoogleServicesPath | ConvertFrom-Json
$projectId = $config.project_info.project_id
$client = $config.client |
    Where-Object { $_.client_info.android_client_info.package_name -eq "com.example.myapplication" } |
    Select-Object -First 1

if (-not $client) {
    throw "No Firebase client found for package com.example.myapplication."
}

$apiKey = $client.api_key[0].current_key

function New-FirestoreValue {
    param($Value)

    if ($null -eq $Value) {
        return @{ nullValue = $null }
    }

    if ($Value -is [int] -or $Value -is [long]) {
        return @{ integerValue = "$Value" }
    }

    if ($Value -is [hashtable]) {
        $fields = @{}
        foreach ($key in $Value.Keys) {
            $fields[$key] = New-FirestoreValue $Value[$key]
        }
        return @{ mapValue = @{ fields = $fields } }
    }

    return @{ stringValue = "$Value" }
}

function New-TimestampValue {
    param([string]$Timestamp)
    return @{ timestampValue = $Timestamp }
}

function ConvertTo-FirestoreDocument {
    param([hashtable]$Document)

    $fields = @{}
    foreach ($key in $Document.Keys) {
        if ($key -eq "createdAt" -or $key -eq "updatedAt") {
            $fields[$key] = New-TimestampValue $Document[$key]
        } else {
            $fields[$key] = New-FirestoreValue $Document[$key]
        }
    }
    return @{ fields = $fields }
}

$posts = @(
    @{
        id = "p1"
        data = @{
            authorId = "user-minh-tuan"
            author = U "Minh Tu\u1ea5n"
            role = U "Fan cu\u1ed3ng S\u01a1n T\u00f9ng"
            topic = U "Trao \u0111\u1ed5i c\u00e1 nh\u00e2n"
            content = U "Kh\u00f4ng th\u1ec3 tin \u0111\u01b0\u1ee3c setlist \u0111\u00eam qua. Ai c\u00f3 video \u0111o\u1ea1n highnote cu\u1ed1i c\u00f9ng th\u00ec chia s\u1ebb v\u1edbi m\u00ecnh v\u1edbi."
            likes = 128
            comments = 24
            shareCount = 4
            imageUrl = "android.resource://com.example.myapplication/drawable/event_concert"
            mediaUrl = "android.resource://com.example.myapplication/drawable/event_concert"
            mediaType = "image/png"
            eventId = $null
            eventTitle = $null
            sharedPost = @{
                author = U "H\u1ed3ng Quang"
                caption = "a"
            }
            createdAt = "2025-05-10T00:00:00Z"
            updatedAt = "2025-05-10T00:00:00Z"
        }
    },
    @{
        id = "p2"
        data = @{
            authorId = "fanzone-official"
            author = "FanZone Official"
            role = U "Th\u00f4ng b\u00e1o c\u1ed9ng \u0111\u1ed3ng"
            topic = U "M\u1edf \u0111\u1ee3t pre-sale \u0111\u1ed9c quy\u1ec1n"
            content = U "Tu\u1ea7n t\u1edbi s\u1ebd c\u00f3 \u0111\u1ee3t pre-sale \u0111\u1ed9c quy\u1ec1n cho kh\u00e1ch \u0111\u00e3 c\u1eadp nh\u1eadt h\u1ed3 s\u01a1 th\u00e0nh vi\u00ean. Nh\u1edb ki\u1ec3m tra profile tr\u01b0\u1edbc 20:00 t\u1ed1i th\u1ee9 S\u00e1u."
            likes = 84
            comments = 18
            shareCount = 2
            imageUrl = $null
            mediaUrl = $null
            mediaType = $null
            eventId = "neon-night"
            eventTitle = "Neon Nights Festival 2024"
            sharedPost = $null
            createdAt = "2026-05-09T14:00:00Z"
            updatedAt = "2026-05-09T14:00:00Z"
        }
    },
    @{
        id = "p3"
        data = @{
            authorId = "user-hoang-lam"
            author = U "Ho\u00e0ng L\u00e2m"
            role = U "Th\u00e0nh vi\u00ean \u0111\u00e3 mua v\u00e9"
            topic = U "L\u1eadp nh\u00f3m check-in"
            content = U "M\u00ecnh l\u1eadp nh\u00f3m chat \u0111\u1ec3 c\u00f9ng trao \u0111\u1ed5i v\u00e0 c\u1eadp nh\u1eadt th\u00f4ng tin check-in. B\u1ea1n n\u00e0o \u0111i Neon Nights m\u1ed9t m\u00ecnh th\u00ec v\u00e0o chung cho vui nh\u00e9."
            likes = 37
            comments = 12
            shareCount = 1
            imageUrl = $null
            mediaUrl = $null
            mediaType = $null
            eventId = "neon-night"
            eventTitle = "Neon Nights Festival 2024"
            sharedPost = $null
            createdAt = "2026-05-08T14:00:00Z"
            updatedAt = "2026-05-08T14:00:00Z"
        }
    }
)

foreach ($post in $posts) {
    $document = ConvertTo-FirestoreDocument $post.data
    $body = $document | ConvertTo-Json -Depth 20
    $url = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/communityPosts/$($post.id)?key=$apiKey"
    Invoke-RestMethod -Method Patch -Uri $url -ContentType "application/json; charset=utf-8" -Body $body | Out-Null
    Write-Output "Seeded communityPosts/$($post.id)"
}

Write-Output "Seed complete for Firebase project $projectId."
