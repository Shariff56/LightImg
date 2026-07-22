# Feature & Functionality Suggestions

Based on the project overview, constraints (offline-first, zero data collection, privacy-focused), and current MVP scope, here are several suggestions for new functionalities and improvements that would enhance the user experience and align with the app's core mission:

## 1. Privacy & Security Enhancements

Since privacy is a primary selling point for this app, doubling down on offline security features would make it highly appealing:

* **EXIF/Metadata Stripping (Promote to MVP):** Currently slated for v2, stripping EXIF data (GPS coordinates, device model, timestamp) should ideally be an MVP feature. It aligns perfectly with the "your data is safe" messaging. Add a simple "Remove Location & Metadata" toggle on the compression/saving screen.
* **Secure Delete of Originals:** After compressing or converting, offer an option to securely delete the original image. This is useful for users dealing with sensitive documents who only want to keep the processed version.
* **PDF Password Protection:** When converting images to PDF (often used for ID proofs or financial documents), allow users to set a password to encrypt the generated PDF locally before saving or sharing it.

## 2. Advanced Document & Image Processing

* **Document Enhancement Mode (B&W / Contrast Boost):** For the "Image to PDF" feature, users often take photos of receipts or documents. Adding a quick "Document Mode" filter that boosts contrast and turns the image into stark black-and-white (like a scanner) would be a killer feature.
* **Local Watermarking:** Allow users to add a simple text or image watermark to their photos before saving or exporting to PDF. This is frequently requested by users submitting IDs or watermarking their creative work.
* **On-Device Background Removal:** With modern Android APIs (like ML Kit's Subject Segmentation, which runs fully on-device), you could offer a background removal tool without sending data to a server.

## 3. Workflow & Batch Processing Improvements

* **Smart Selection / Filtering:** When selecting images for batch compression, allow users to filter their gallery by file size or type (e.g., "Show me all images larger than 5MB" or "Show only PNGs"). This makes finding the heavy images much easier.
* **Target Size Presets for Common Platforms:** Instead of just a manual exact-size input, provide presets for common requirements, such as "WhatsApp Profile Picture (<5MB)", "Government Form (<1MB)", or "Email Attachment (<25MB)".
* **Auto-Compress Watched Folder (v2 Priority):** Allow users to select a specific folder (like "Screenshots") and have the app automatically compress any new image that appears in that folder in the background using `WorkManager`.

## 4. UI / UX Utility Features

* **Before/After Storage Savings:** On the success screen after a batch job, show a compelling metric: "You saved 124 MB of space." This provides instant positive reinforcement for using the app.
* **Quick Share Actions:** After processing, alongside the default share sheet, offer quick-actions for the most common targets (e.g., "Share to WhatsApp", "Share to Gmail") to save a tap.
