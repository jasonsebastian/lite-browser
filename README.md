## Lite Browser

A dummy Android browser app for learning purposes.

<img width="300" alt="image" src="https://github.com/user-attachments/assets/90e80519-fd9e-45ca-af6c-447702d8a2f0" />

## Learnings

To create this Android browser app, I have decided to use `WebView` as the library.
- Custom Tabs ([ref](https://developer.android.com/develop/ui/views/layout/webapps/overview-of-android-custom-tabs)) is not suited for learning purposes because it provides minimal control on the browser (the typical use case for it is for privacy policy page, OAuth screen, etc).

Inside `WebView`, I have enabled `javaScriptEnabled`.
- 99% of modern websites need JavaScript to load properly.
    - Single Page Applications (SPAs) built with React, Angular, or Vue
    - Social media feeds
    - Login pages (including Google, Apple, and most banks)
    - Video players (like YouTube)
- To minimize the risk of XSS:
    - Safe Browsing is enabled (Google Play Services' built-in malware and phishing detector for WebViews, enabled by default starting from Android 8.0 / API level 26).
    - No `addJavascriptInterface()` is used.

### Phase 1: Single Tab
The following needs to be implemented:
- Accept and sanitize URL from user input.
- Update URL whenever user navigates to another page.
- Override back handler when the current history has previous entries.
- Add a progress bar and sync with `WebChromeClient.onProgressChanged()`.
- Handle download and upload logic.

File uploads are handled via `WebChromeClient.onShowFileChooser()`, backed by the system Storage Access Framework picker.

Downloads are routed through the native Android `DownloadManager` via `WebView.setDownloadListener()`.
- Cookies from the current session are forwarded as a `cookie` header on the download request, so authenticated downloads (e.g. banking PDFs) work correctly. This behavior is only enabled for `http`/`https` URL scheme.
