import re

files = [
    ('Auth', 'app/src/main/java/com/example/myapplication/feature/authentication/AuthScreens.kt'),
    ('CommunityScreen', 'app/src/main/java/com/example/myapplication/feature/community/CommunityScreen.kt'),
    ('EventCommunityScreen', 'app/src/main/java/com/example/myapplication/feature/community/EventCommunityScreen.kt'),
    ('CommunityComposer', 'app/src/main/java/com/example/myapplication/feature/community/CommunityComposer.kt'),
    ('ProfileScreen', 'app/src/main/java/com/example/myapplication/feature/profile/ProfileScreen.kt'),
    ('PromotionalComponents', 'app/src/main/java/com/example/myapplication/core/designsystem/component/PromotionalComponents.kt')
]

with open('extracted_strings.txt', 'w', encoding='utf-8') as out:
    for name, path in files:
        out.write(f'=== {name} ({path}) ===\n')
        try:
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                content = f.read()
            matches = re.finditer(r'"([^"\\]*(?:\\.[^"\\]*)*)"', content)
            for m in matches:
                val = m.group(0)
                # Filter out empty or simple strings that aren't Vietnamese or text
                if len(val) > 2 and any(c.isalpha() for c in val):
                    start_line = content.count('\n', 0, m.start()) + 1
                    out.write(f'Line {start_line}: {val}\n')
        except Exception as e:
            out.write(f'Error: {e}\n')
        out.write('\n')
