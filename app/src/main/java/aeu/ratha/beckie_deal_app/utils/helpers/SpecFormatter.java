package aeu.ratha.beckie_deal_app.utils.helpers;

/**
 * Utility for formatting product attributes, specifications, tech terms,
 * HTML rich-text descriptions, and variant details into clean, user-friendly,
 * and catchy e-commerce representations.
 */
public class SpecFormatter {

    /**
     * Formats raw HTML/rich-text product descriptions into clean, beautifully styled CharSequence.
     * Handles inline HTML, CSS tags, stray characters (like '>-'), entities (&amp;, &nbsp;),
     * and bullet points.
     */
    public static CharSequence formatHtmlDescription(String rawText) {
        if (rawText == null || rawText.trim().isEmpty() || "null".equalsIgnoreCase(rawText.trim())) {
            return "No description available.";
        }

        String cleaned = rawText.trim();

        // 1. Check if the string contains HTML tags
        boolean hasHtml = cleaned.contains("<") && cleaned.contains(">");

        if (hasHtml) {
            // Fix common API rich-text stray characters like '>-', '> -', '>•'
            cleaned = cleaned.replaceAll(">\\s*-\\s*", ">• ")
                             .replaceAll(">\\s*•\\s*", ">• ")
                             .replaceAll("^\\s*-\\s*", "• ")
                             .replaceAll("<br\\s*/?>\\s*-\\s*", "<br/>• ")
                             .replaceAll("</p>\\s*<p>\\s*-\\s*", "</p><p>• ");

            // Parse HTML using Android's Html parser
            CharSequence spanned;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                spanned = android.text.Html.fromHtml(cleaned, android.text.Html.FROM_HTML_MODE_COMPACT);
            } else {
                @SuppressWarnings("deprecation")
                CharSequence legacy = android.text.Html.fromHtml(cleaned);
                spanned = legacy;
            }

            return trimCharSequence(spanned);
        } else {
            // Non-HTML plain text: clean up stray '>-' or format lines with bullets
            String[] lines = cleaned.split("\r?\n");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                String l = line.trim();
                if (l.isEmpty() || "null".equalsIgnoreCase(l)) continue;
                if (l.startsWith(">-") || l.startsWith("> -")) {
                    l = "• " + l.substring(l.indexOf('-') + 1).trim();
                } else if (l.startsWith("- ")) {
                    l = "• " + l.substring(2).trim();
                }
                if (sb.length() > 0) sb.append("\n");
                sb.append(l);
            }
            return sb.toString();
        }
    }

    /**
     * Strips all HTML tags and unescapes HTML entities to return plain text.
     */
    public static String stripHtml(String input) {
        if (input == null || input.trim().isEmpty() || "null".equalsIgnoreCase(input.trim())) return "";
        String cleaned = input.trim();
        if (cleaned.contains("<") && cleaned.contains(">")) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                return android.text.Html.fromHtml(cleaned, android.text.Html.FROM_HTML_MODE_COMPACT).toString().trim();
            } else {
                @SuppressWarnings("deprecation")
                String result = android.text.Html.fromHtml(cleaned).toString().trim();
                return result;
            }
        }
        return cleaned;
    }

    private static CharSequence trimCharSequence(CharSequence source) {
        if (source == null) return "";
        int start = 0;
        int end = source.length();
        while (start < end && Character.isWhitespace(source.charAt(start))) {
            start++;
        }
        while (end > start && Character.isWhitespace(source.charAt(end - 1))) {
            end--;
        }
        return source.subSequence(start, end);
    }

    /**
     * Formats a raw attribute key into a user-friendly label.
     */
    public static String formatAttributeKey(String key) {
        if (key == null || key.trim().isEmpty() || "null".equalsIgnoreCase(key.trim())) return "";
        String trimmed = key.trim().toLowerCase();
        switch (trimmed) {
            case "ram":
                return "RAM";
            case "rom":
            case "storage":
            case "disk":
            case "capacity":
                return "Storage";
            case "ssd":
                return "SSD Storage";
            case "hdd":
                return "HDD Storage";
            case "color":
            case "colour":
                return "Color";
            case "cpu":
            case "processor":
                return "Processor (CPU)";
            case "gpu":
            case "graphics":
                return "Graphics (GPU)";
            case "os":
            case "operating_system":
                return "Operating System";
            case "display":
            case "screen":
            case "screen_size":
                return "Display Size";
            case "sku":
                return "SKU";
            case "size":
                return "Size";
            case "weight":
                return "Weight";
            case "warranty":
                return "Warranty";
            default:
                return toTitleCase(trimmed.replace("_", " ").replace("-", " "));
        }
    }

    /**
     * Formats an attribute value depending on its key context.
     */
    public static String formatAttributeValue(String key, String val) {
        if (val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val.trim())) return "";
        String raw = val.trim();
        String lowerKey = key != null ? key.trim().toLowerCase() : "";

        switch (lowerKey) {
            case "ram":
                return formatMemoryOrStorage(raw, "RAM");
            case "storage":
            case "rom":
            case "ssd":
            case "hdd":
            case "capacity":
            case "disk":
                return formatMemoryOrStorage(raw, "Storage");
            case "color":
            case "colour":
                return toTitleCase(raw.replace("_", " ").replace("-", " "));
            case "size":
                return formatSize(raw);
            default:
                return formatGenericValue(raw);
        }
    }

    /**
     * Formats multi-spec variant names or summaries like "12gb Gddr6x - Oc · 301mm" -> "12GB GDDR6X · OC · 301mm"
     * Filters out null or "null" entries completely.
     */
    public static String formatVariantString(String text) {
        if (text == null || text.trim().isEmpty() || "null".equalsIgnoreCase(text.trim())) return "";
        String trimmed = text.trim();
        if (trimmed.contains("/") || trimmed.contains("|") || trimmed.contains("·") || trimmed.contains(" - ")) {
            String[] parts = trimmed.split("(?<=^|[^a-zA-Z0-9])[\\/|·]|\\s+-\\s+");
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                String cleanedPart = part.trim();
                if (cleanedPart.isEmpty() || "null".equalsIgnoreCase(cleanedPart)) continue;
                String formatted = formatGenericValue(cleanedPart);
                if (!formatted.isEmpty() && !"null".equalsIgnoreCase(formatted.trim())) {
                    if (sb.length() > 0) sb.append(" · ");
                    sb.append(formatted);
                }
            }
            return sb.toString();
        }
        return formatGenericValue(trimmed);
    }

    /**
     * Formats spec tags like "sku: as-tuf-4070s" -> "SKU: AS-TUF-4070S"
     */
    public static String formatTag(String tag) {
        if (tag == null || tag.trim().isEmpty() || "null".equalsIgnoreCase(tag.trim())) return "";
        String trimmed = tag.trim();
        if (trimmed.toLowerCase().startsWith("sku:")) {
            String skuVal = trimmed.substring(4).trim().toUpperCase();
            return "SKU: " + skuVal;
        }
        return formatGenericValue(trimmed);
    }

    /**
     * Memory / Storage specific formatting.
     */
    private static String formatMemoryOrStorage(String val, String type) {
        if (val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val.trim())) return "";
        String cleaned = val.trim();
        String lower = cleaned.toLowerCase();

        if (cleaned.matches("(?i)^\\d+\\s*(GB|TB|MB)$")) {
            return cleaned.replaceAll("(?i)gb", "GB")
                          .replaceAll("(?i)tb", "TB")
                          .replaceAll("(?i)mb", "MB");
        }

        if (lower.matches("^(\\d+)\\s*(t|tb)$")) {
            String num = lower.replaceAll("[^0-9]", "");
            return num + "TB";
        }
        if (lower.matches("^(\\d+)\\s*(g|gb)$")) {
            String num = lower.replaceAll("[^0-9]", "");
            return num + "GB";
        }
        if (lower.matches("^(\\d+)\\s*(m|mb)$")) {
            String num = lower.replaceAll("[^0-9]", "");
            return num + "MB";
        }

        if (lower.matches("^\\d+$")) {
            try {
                int num = Integer.parseInt(lower);
                if (num == 1 || num == 2 || num == 4) {
                    return "RAM".equals(type) ? num + "GB" : num + "TB";
                }
                return num + "GB";
            } catch (Exception ignored) {}
        }

        return formatGenericValue(cleaned);
    }

    /**
     * Formats generic values (handles tech acronyms, units, GPU/RAM specs, colors, etc.)
     */
    public static String formatGenericValue(String val) {
        if (val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val.trim())) return "";
        String cleaned = val.trim();

        // 12gb -> 12GB, 16gb -> 16GB, 1tb -> 1TB, 301mm -> 301mm, 6000mhz -> 6000MHz
        if (cleaned.matches("(?i)^(\\d+)\\s*(gb|tb|mb|g|t|m)$")) {
            String lower = cleaned.toLowerCase();
            String num = lower.replaceAll("[^0-9]", "");
            if (lower.endsWith("t") || lower.endsWith("tb")) return num + "TB";
            if (lower.endsWith("m") || lower.endsWith("mb")) return num + "MB";
            return num + "GB";
        }

        // Pure numbers
        if (cleaned.matches("^\\d+$")) {
            try {
                int num = Integer.parseInt(cleaned);
                if (num == 512 || num == 256 || num == 128 || num == 64 || num == 32 || num == 16 || num == 8 || num == 4 || num == 2) {
                    return num + "GB";
                } else if (num == 1) {
                    return "1TB";
                }
            } catch (Exception ignored) {}
        }

        // Apparel sizes: xl -> XL, xxl -> XXL
        if (cleaned.matches("(?i)^(xs|s|m|l|xl|xxl|xxxl)$")) {
            return cleaned.toUpperCase();
        }

        // Words with underscores or hyphens
        String text = cleaned;
        if (text.contains("_")) {
            text = text.replace("_", " ");
        }

        return toTitleCase(text);
    }

    private static String formatSize(String raw) {
        String lower = raw.trim().toLowerCase();
        if (lower.matches("^(xs|s|m|l|xl|xxl|xxxl)$")) {
            return lower.toUpperCase();
        }
        return toTitleCase(raw);
    }

    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty() || "null".equalsIgnoreCase(input.trim())) return "";
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty() || "null".equalsIgnoreCase(word)) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(formatWord(word));
        }
        return sb.toString();
    }

    /**
     * Formats an individual word according to tech e-commerce standards.
     * e.g. "gddr6x" -> "GDDR6X", "oc" -> "OC", "tuf" -> "TUF", "rtx" -> "RTX", "pcie" -> "PCIe", "nvme" -> "NVMe"
     */
    private static String formatWord(String word) {
        if (word == null || word.isEmpty() || "null".equalsIgnoreCase(word.trim())) return "";

        // Check if hyphenated (e.g. AS-TUF-4070S or PCIe-4.0)
        if (word.contains("-") && !word.startsWith("-")) {
            String[] parts = word.split("-");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append("-");
                sb.append(formatWord(parts[i]));
            }
            return sb.toString();
        }

        String lower = word.toLowerCase();

        // Specific hardware acronyms
        switch (lower) {
            case "gddr6x": return "GDDR6X";
            case "gddr6": return "GDDR6";
            case "gddr5": return "GDDR5";
            case "ddr5": return "DDR5";
            case "ddr4": return "DDR4";
            case "ddr3": return "DDR3";
            case "oc": return "OC";
            case "sc": return "SC";
            case "ti": return "Ti";
            case "super": return "SUPER";
            case "rtx": return "RTX";
            case "gtx": return "GTX";
            case "rx": return "RX";
            case "arc": return "ARC";
            case "pcie": return "PCIe";
            case "nvme": return "NVMe";
            case "ssd": return "SSD";
            case "hdd": return "HDD";
            case "ram": return "RAM";
            case "rom": return "ROM";
            case "rgb": return "RGB";
            case "led": return "LED";
            case "fhd": return "FHD";
            case "qhd": return "QHD";
            case "uhd": return "UHD";
            case "oled": return "OLED";
            case "amoled": return "AMOLED";
            case "ips": return "IPS";
            case "hdr": return "HDR";
            case "lcd": return "LCD";
            case "cpu": return "CPU";
            case "gpu": return "GPU";
            case "tuf": return "TUF";
            case "rog": return "ROG";
            case "asus": return "ASUS";
            case "msi": return "MSI";
            case "evga": return "EVGA";
            case "amd": return "AMD";
            case "nvidia": return "NVIDIA";
            case "intel": return "Intel";
            case "sku": return "SKU";
        }

        // Units attached to numbers e.g. 12gb -> 12GB, 301mm -> 301mm, 6000mhz -> 6000MHz
        if (lower.matches("^\\d+(gb|tb|mb|g|t|m)$")) {
            String num = lower.replaceAll("[^0-9]", "");
            if (lower.endsWith("t") || lower.endsWith("tb")) return num + "TB";
            if (lower.endsWith("m") || lower.endsWith("mb")) return num + "MB";
            return num + "GB";
        }
        if (lower.matches("^\\d+(mhz|ghz|hz)$")) {
            String num = lower.replaceAll("[^0-9]", "");
            if (lower.endsWith("ghz")) return num + "GHz";
            if (lower.endsWith("hz")) return num + "Hz";
            return num + "MHz";
        }
        if (lower.matches("^\\d+mm$")) {
            String num = lower.replaceAll("[^0-9]", "");
            return num + "mm";
        }
        if (lower.matches("^\\d+cm$")) {
            String num = lower.replaceAll("[^0-9]", "");
            return num + "cm";
        }
        if (lower.matches("^\\d+kg$")) {
            String num = lower.replaceAll("[^0-9]", "");
            return num + "kg";
        }

        // Numbers + letters e.g. 4070S -> 4070S
        if (word.matches("^\\d+[A-Z]+$") || word.matches("^[A-Z]+\\d+[A-Z]*$")) {
            return word.toUpperCase();
        }

        // Default Title Case
        if (word.length() == 1) return word.toUpperCase();
        return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
    }

    /**
     * Maps product variant text/attributes to a real color HEX integer or null if no color detected.
     */
    public static Integer getColorForName(String text) {
        if (text == null || text.trim().isEmpty() || "null".equalsIgnoreCase(text.trim())) return null;
        String lower = text.toLowerCase().trim();

        if (lower.contains("rose gold")) return android.graphics.Color.parseColor("#E8A7A1");
        if (lower.contains("starlight")) return android.graphics.Color.parseColor("#ECE3CE");
        if (lower.contains("gold")) return android.graphics.Color.parseColor("#E5C158");
        if (lower.contains("silver")) return android.graphics.Color.parseColor("#D1D5DB");
        if (lower.contains("space black")) return android.graphics.Color.parseColor("#18181B");
        if (lower.contains("space gray") || lower.contains("space grey")) return android.graphics.Color.parseColor("#52525B");
        if (lower.contains("eclipse gray") || lower.contains("dark grey") || lower.contains("dark gray")) return android.graphics.Color.parseColor("#4B5563");
        if (lower.contains("midnight blue")) return android.graphics.Color.parseColor("#1E3A8A");
        if (lower.contains("midnight")) return android.graphics.Color.parseColor("#1E293B");
        if (lower.contains("black")) return android.graphics.Color.parseColor("#18181B");
        if (lower.contains("white")) return android.graphics.Color.parseColor("#F9FAFB");
        if (lower.contains("pacific blue") || lower.contains("sky blue") || lower.contains("blue")) return android.graphics.Color.parseColor("#2563EB");
        if (lower.contains("alpine green") || lower.contains("olive") || lower.contains("green")) return android.graphics.Color.parseColor("#16A34A");
        if (lower.contains("deep purple") || lower.contains("purple")) return android.graphics.Color.parseColor("#7C3AED");
        if (lower.contains("red")) return android.graphics.Color.parseColor("#DC2626");
        if (lower.contains("yellow")) return android.graphics.Color.parseColor("#EAB308");
        if (lower.contains("pink")) return android.graphics.Color.parseColor("#EC4899");
        if (lower.contains("orange")) return android.graphics.Color.parseColor("#F97316");
        if (lower.contains("bronze")) return android.graphics.Color.parseColor("#CD7F32");
        if (lower.contains("gray") || lower.contains("grey")) return android.graphics.Color.parseColor("#6B7280");
        if (lower.contains("citrus")) return android.graphics.Color.parseColor("#EAB308");
        if (lower.contains("blush")) return android.graphics.Color.parseColor("#FB7185");
        if (lower.contains("indigo")) return android.graphics.Color.parseColor("#4F46E5");
        if (lower.contains("teal")) return android.graphics.Color.parseColor("#0D9488");
        if (lower.contains("cyan")) return android.graphics.Color.parseColor("#06B6D4");
        if (lower.contains("lavender")) return android.graphics.Color.parseColor("#C084FC");
        if (lower.contains("coral")) return android.graphics.Color.parseColor("#FB923C");
        if (lower.contains("mint")) return android.graphics.Color.parseColor("#34D399");
        if (lower.contains("cream")) return android.graphics.Color.parseColor("#FEF08A");
        if (lower.contains("beige")) return android.graphics.Color.parseColor("#E5E7EB");
        if (lower.contains("champagne")) return android.graphics.Color.parseColor("#FDE68A");
        if (lower.contains("graphite")) return android.graphics.Color.parseColor("#4B5563");
        if (lower.contains("charcoal")) return android.graphics.Color.parseColor("#1F2937");
        if (lower.contains("titanium")) return android.graphics.Color.parseColor("#71717A");

        return null;
    }

    /**
     * Creates a circular swatch drawable with a subtle border for real color indicators.
     */
    public static android.graphics.drawable.Drawable createColorSwatchDrawable(int colorInt, int sizePx) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        gd.setColor(colorInt);
        gd.setStroke(1, android.graphics.Color.parseColor("#9CA3AF"));
        gd.setSize(sizePx, sizePx);
        gd.setBounds(0, 0, sizePx, sizePx);
        return gd;
    }
}
