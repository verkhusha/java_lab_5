import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class IOStreamsTasks {

   
    public static String getLineWithMaxWords(String filePath) throws IOException {
        String maxWordLine = "";
        int maxWords = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.trim().split("\\s+");
                int wordCount = words.length;
                if (wordCount > maxWords) {
                    maxWords = wordCount;
                    maxWordLine = line;
                }
            }
        }
        return maxWordLine;
    }



    static class CaesarCipherWriter extends FilterWriter {
        private final int key;

        public CaesarCipherWriter(Writer out, char keyChar) {
            super(out);
            this.key = keyChar;
        }

        @Override
        public void write(int c) throws IOException {
            super.write((c + key) & 0xFFFF); 
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            for (int i = off; i < off + len; i++) {
                cbuf[i] = (char) ((cbuf[i] + key) & 0xFFFF);
            }
            super.write(cbuf, off, len);
        }
    }

    static class CaesarCipherReader extends FilterReader {
        private final int key;

        public CaesarCipherReader(Reader in, char keyChar) {
            super(in);
            this.key = keyChar;
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            return (c == -1) ? -1 : ((c - key) & 0xFFFF);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int read = super.read(cbuf, off, len);
            if (read == -1) return -1;
            for (int i = off; i < off + read; i++) {
                cbuf[i] = (char) ((cbuf[i] - key) & 0xFFFF);
            }
            return read;
        }
    }

    
    public static void encryptFile(String inputFile, String outputFile, char key) throws IOException {
        try (Reader fr = new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8);
             Writer fw = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);
             CaesarCipherWriter cipherWriter = new CaesarCipherWriter(fw, key)) {

            char[] buffer = new char[1024];
            int read;
            while ((read = fr.read(buffer)) != -1) {
                cipherWriter.write(buffer, 0, read);
            }
        }
    }

    
    public static void decryptFile(String inputFile, String outputFile, char key) throws IOException {
        try (Reader fr = new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8);
             Writer fw = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8);
             CaesarCipherReader cipherReader = new CaesarCipherReader(fr, key)) {

            char[] buffer = new char[1024];
            int read;
            while ((read = cipherReader.read(buffer)) != -1) {
                fw.write(buffer, 0, read);
            }
        }
    }

   
    public static void countHtmlTags(String urlString) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(urlString).openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        Map<String, Integer> tagCount = new HashMap<>();
        String html = content.toString().toLowerCase();

        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("<\\s*([a-z0-9]+)[^>]*>").matcher(html);
        while (matcher.find()) {
            String tag = matcher.group(1);
            tagCount.put(tag, tagCount.getOrDefault(tag, 0) + 1);
        }

        System.out.println("\n--- Теги в лексикографічному порядку ---");
        tagCount.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("%s: %d%n", e.getKey(), e.getValue()));

        System.out.println("\n--- Теги за частотою (зростання) ---");
        tagCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.printf("%s: %d%n", e.getKey(), e.getValue()));
    }


    public static void main(String[] args) {
        try {
          
            System.out.println("=== Завдання 1: Рядок з максимальною кількістю слів ===");
            java.nio.file.Files.writeString(java.nio.file.Paths.get("input.txt"),
                    "Перший рядок\n" +
                            "Другий рядок з кількома словами в ньому\n" +
                            "Один\n" +
                            "Цей рядок має найбільше слів тут є п'ять слів",
                    StandardCharsets.UTF_8);

            String maxLine = getLineWithMaxWords("input.txt");
            System.out.println("Рядок з макс. слів: " + maxLine);

            
            System.out.println("\n=== Завдання 2: Шифрування/дешифрування (Цезар) ===");
            char key = 'K';
            encryptFile("input.txt", "encrypted.txt", key);
            decryptFile("encrypted.txt", "decrypted.txt", key);

            String original = java.nio.file.Files.readString(java.nio.file.Paths.get("input.txt"), StandardCharsets.UTF_8);
            String encrypted = java.nio.file.Files.readString(java.nio.file.Paths.get("encrypted.txt"), StandardCharsets.UTF_8);
            String decrypted = java.nio.file.Files.readString(java.nio.file.Paths.get("decrypted.txt"), StandardCharsets.UTF_8);

            System.out.println("Оригінал:\n" + original);
            System.out.println("Зашифровано:\n" + encrypted);
            System.out.println("Розшифровано:\n" + decrypted);

            
            System.out.println("\n=== Завдання 3: Підрахунок HTML-тегів ===");
            countHtmlTags("https://httpbin.org/html");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}