# Offline Password Generator

A tiny installable PWA for generating passwords locally.

## Security properties

- Uses `crypto.getRandomValues()`
- Uses rejection sampling to avoid modulo bias
- Guarantees at least one character from every enabled character group
- Does not save generated passwords
- Does not use localStorage, cookies, analytics, CDNs, or external libraries
- Works offline after the first successful load
- Minimum selectable password length: 6 characters
- Lengths 6–8 are visibly marked as legacy / low-security choices

## Running locally

A PWA service worker needs a secure context. `file://` is not sufficient for installation/service-worker behavior.

From this directory, for example:

```bash
python -m http.server 8000
```

Then open:

```text
http://localhost:8000
```

For Android installation, serve the folder over HTTPS from a location you control, open it in Chrome, and choose "Install app" / "Add to Home screen".

Once installed and cached, it works offline.

## Files

- `index.html` – complete UI and password generation logic
- `manifest.webmanifest` – PWA metadata
- `sw.js` – offline cache
- `icon-192.svg`
- `icon-512.svg`
