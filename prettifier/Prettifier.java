import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Prettifier {

    public static final String RESET  = "\u001b[0m";
    public static final String BOLD   = "\u001b[1m";
    public static final String CYAN   = "\u001b[36m";
    public static final String BLUE   = "\u001b[94m";
    public static final String YELLOW = "\u001b[33m";
    public static final String GREEN  = "\u001b[32m";

    private static final Map<String, Integer> changeSummary = new LinkedHashMap<>();

    public static void main(String[] args) {

        // Interactive walkthrough mode
        if (args.length == 1 && args[0].equals("-i")) {
            runInteractive();
            return;
        }

        if (!isValidArgs(args)) {
            printUsage();
            return;
        }
        String inputPath  = args[0];
        String outputPath = args[1];
        String lookupPath = args[2];

        if (!fileExists(inputPath))  { System.out.println("Input not found");          return; }
        if (!fileExists(lookupPath)) { System.out.println("Airport lookup not found"); return; }

        Map<String, String> lookup = loadAirportLookup(lookupPath);
        if (lookup == null) { System.out.println("Airport lookup malformed"); return; }

        String text = readFile(inputPath);
        if (text == null) return;

        text = normalizeWhitespaces(text);
        text = replaceAirportCodes(text, lookup);
        text = replaceDateTimes(text);
        text = normalizeWhitespaces(text);

        writeFile(outputPath, text);
        printChangeSummary();
    }

    private static void runInteractive() {
        String inputPath  = "./input.txt";
        String outputPath = "./out.txt";
        String lookupPath = "./airport-lookup.csv";

        System.out.println();
        System.out.println("Hello! Welcome to the itinerary prettifier.");
        System.out.println();
        System.out.println("This program is designed for information processing and tidying for increased readability.");
        System.out.println();
        System.out.println("Files that are used in this program:");
        System.out.println("  input:  " + inputPath);
        System.out.println("  output: " + outputPath);
        System.out.println("  lookup: " + lookupPath);
        System.out.println();

        if (!fileExists(inputPath)) {
            System.out.println(inputPath + " doesn't exist in this folder. Drop it here and try again.");
            System.out.println("Exiting program.");
            return;
        }
        if (!fileExists(lookupPath)) {
            System.out.println(lookupPath + " doesn't exist in this folder. Drop it here and try again.");
            System.out.println("Exiting program.");
            return;
        }

        System.out.print("Ready to process data? [Y/n] ");
        Scanner in = new Scanner(System.in);
        String response = in.nextLine().trim().toLowerCase();
        if (response.equals("n") || response.equals("no")) {
            System.out.println("Exiting program.");
            return;
        }

        Map<String, String> lookup = loadAirportLookup(lookupPath);
        if (lookup == null) {
            System.out.println("Airport lookup malformed");
            return;
        }
        System.out.println("\u2713 Loaded " + (lookup.size() / 4) + " airports");

        String text = readFile(inputPath);
        if (text == null) return;
        int lineCount = text.split("\n", -1).length;
        System.out.println("\u2713 Read " + lineCount + " lines from input");

        text = normalizeWhitespaces(text);
        text = replaceAirportCodes(text, lookup);
        text = replaceDateTimes(text);
        text = normalizeWhitespaces(text);

        int totalReplacements = 0;
        for (int count : changeSummary.values()) {
            totalReplacements += count;
        }
        System.out.println("\u2713 Made " + totalReplacements + " replacements");

        writeFile(outputPath, text);
        System.out.println("\u2713 Saved to " + outputPath);

        System.out.println();
        System.out.println("Done.");
    }

    private static boolean isValidArgs(String[] args) {
        if (args.length == 1 && args[0].equals("-h")) return false;
        if (args.length != 3) return false;
        return true;
    }

    private static boolean fileExists(String path) {
        return Files.exists(Path.of(path));
    }

    private static String readFile(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            return null;
        }
    }

    private static String normalizeWhitespaces(String text) {
        text = text.replace("\r\n", "\n");
        text = text.replace('\r', '\n');
        text = text.replace('\f', '\n');
        text = text.replace('\u000B', '\n');
        text = text.replaceAll("\\n{3,}", "\n\n");
        return text;
    }

    private static String replaceAirportCodes(String text, Map<String, String> lookup) {
        Pattern pattern = Pattern.compile("(\\*?)(##[A-Z]{4}|#[A-Z]{3})");
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String prefix = matcher.group(1);
            String code   = matcher.group(2);
            String key    = prefix + code;
            String replacement = lookup.get(key);

            if (replacement != null) {
                String color = prefix.startsWith("*") ? GREEN : YELLOW;
                String summaryLine = color + key + RESET + " -> " + BOLD + replacement + RESET;
                changeSummary.put(summaryLine, changeSummary.getOrDefault(summaryLine, 0) + 1);
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String replaceDateTimes(String text) {
        Pattern pattern = Pattern.compile("(D|T12|T24)\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String type  = matcher.group(1);
            String value = matcher.group(2);

            try {
                OffsetDateTime dt = OffsetDateTime.parse(value);
                String offset = dt.getOffset().getId();
                if (offset.equals("Z")) offset = "+00:00";
                String offsetStr = "(" + offset + ")";

                String formatted;
                if (type.equals("D")) {
                    formatted = dt.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US));
                } else if (type.equals("T12")) {
                    formatted = dt.format(DateTimeFormatter.ofPattern("hh:mma", Locale.US)) + " " + offsetStr;
                } else {
                    formatted = dt.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US)) + " " + offsetStr;
                }

                String color = type.equals("D") ? CYAN : BLUE;
                String summaryLine = color + type + "(" + value + ")" + RESET + " -> " + BOLD + formatted + RESET;
                changeSummary.put(summaryLine, changeSummary.getOrDefault(summaryLine, 0) + 1);
                matcher.appendReplacement(result, Matcher.quoteReplacement(formatted));
            } catch (Exception e) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static void writeFile(String path, String content) {
        try {
            Files.writeString(Path.of(path), content);
        } catch (IOException e) {
            // silent failure
        }
    }

    private static void printChangeSummary() {
        if (changeSummary.isEmpty()) return;
        System.out.println();
        System.out.println(BOLD + "Summary of changes:" + RESET);
        for (Map.Entry<String, Integer> entry : changeSummary.entrySet()) {
            String line  = entry.getKey();
            int count    = entry.getValue();
            String suffix = count > 1 ? " (x" + count + ")" : "";
            System.out.println("  " + line + suffix);
        }
    }

    private static Map<String, String> loadAirportLookup(String path) {
        Map<String, String> lookup = new HashMap<>();

        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            return null;
        }
        if (lines.isEmpty()) return null;

        String[] headerCols = lines.get(0).split(",");
        Map<String, Integer> colIndex = new HashMap<>();
        for (int i = 0; i < headerCols.length; i++) {
            colIndex.put(headerCols[i].trim(), i);
        }

        String[] requiredCols = {"name", "iso_country", "municipality", "icao_code", "iata_code", "coordinates"};
        for (String col : requiredCols) {
            if (!colIndex.containsKey(col)) return null;
        }

        int nameIdx = colIndex.get("name");
        int munIdx  = colIndex.get("municipality");
        int icaoIdx = colIndex.get("icao_code");
        int iataIdx = colIndex.get("iata_code");

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] cells = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            if (cells.length != headerCols.length) return null;

            for (String cell : cells) {
                if (cell.trim().isEmpty()) return null;
            }

            String iata = cells[iataIdx].replace("\"", "").trim();
            String icao = cells[icaoIdx].replace("\"", "").trim();
            String name = cells[nameIdx].replace("\"", "").trim();
            String city = cells[munIdx].replace("\"", "").trim();

            lookup.put("#" + iata, name);
            lookup.put("##" + icao, name);
            lookup.put("*#" + iata, city);
            lookup.put("*##" + icao, city);
        }

        return lookup;
    }

    private static void printUsage() {
        System.out.println("itinerary usage:");
        System.out.println("$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv");
    }
}