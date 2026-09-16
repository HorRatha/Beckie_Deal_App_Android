# Font Setup Instructions

## ⚠️ IMPORTANT - Required for Build

The typography system requires font files to be manually downloaded and placed in the project.

## Required Font Files

You need to download the following font files and place them in `app/src/main/res/font/`:

### From Amulya (https://www.fontshare.com/fonts/amulya)
- `amulya_bold.ttf` - Amulya Bold

### From Synonym (https://www.fontshare.com/fonts/synonym)
- `synonym_regular.ttf` - Synonym Regular
- `synonym_medium.ttf` - Synonym Medium
- `synonym_semibold.ttf` - Synonym Semibold

## Step-by-Step Instructions:

1. **Download Amulya:**
   - Visit: https://www.fontshare.com/fonts/amulya
   - Click "Download family" button
   - Extract the downloaded ZIP file
   - Find the file `Amulya-Bold.ttf` (or similar)
   - Rename it to `amulya_bold.ttf` (all lowercase, underscores)

2. **Download Synonym:**
   - Visit: https://www.fontshare.com/fonts/synonym
   - Click "Download family" button
   - Extract the downloaded ZIP file
   - Find these files:
     - `Synonym-Regular.ttf` → rename to `synonym_regular.ttf`
     - `Synonym-Medium.ttf` → rename to `synonym_medium.ttf`
     - `Synonym-Semibold.ttf` → rename to `synonym_semibold.ttf`

3. **Place Font Files:**
   - Copy all 4 renamed .ttf files to:
     ```
     app/src/main/res/font/
     ```
   - Your font directory should contain:
     ```
     font/
       ├── amulya_bold.ttf
       ├── amulya_bold.xml
       ├── khmer_font.xml
       ├── synonym_regular.ttf
       ├── synonym_medium.ttf
       ├── synonym_semibold.ttf
       └── synonym.xml
     ```

4. **Clean and Rebuild:**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

## Why Manual Download?

Font files cannot be auto-downloaded due to:
- Licensing requirements from Fontshare
- Copyright considerations
- User must accept Fontshare's license terms

## Troubleshooting

**Build fails with "file not found":**
- Check file names are exactly: `amulya_bold.ttf`, `synonym_regular.ttf`, etc.
- File names are case-sensitive on some systems
- Ensure files are .ttf format, not .otf or other formats

**Fonts don't display:**
- Clean and rebuild project
- Invalidate Android Studio caches: File > Invalidate Caches / Restart
- Check logcat for font loading errors

## License

Fonts from Fontshare are free for personal and commercial use. See fontshare.com for full license details.
