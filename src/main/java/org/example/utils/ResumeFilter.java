package org.example.utils;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ResumeFilter {

    public static void main(String[] args) {
        String inputFile = "D:\\ResumeChecker\\src\\main\\resources\\files\\analyst_candidates_links.txt";
        String filterFile = "D:\\ResumeChecker\\output\\analyst-resumes_to_review.txt";
        String outputFile = "manual_filtered_candidates_links.txt";

        try {
            // Считать ссылки из файлов
            List<String> candidateLinks = readLinesFromFile(inputFile);
            List<String> filterLinks = readLinesFromFile(filterFile);

            // Извлечь ID из фильтрующих ссылок
            Set<String> filterIds = extractIds(filterLinks);

            // Фильтровать ссылки из основного файла
            List<String> filteredLinks = candidateLinks.stream()
                    .filter(link -> !filterIds.contains(extractId(link)))
                    .collect(Collectors.toList());

            // Записать результат в новый файл
            writeLinesToFile(filteredLinks, outputFile);

            System.out.println("Фильтрация завершена. Результат записан в файл: " + outputFile);

        } catch (IOException e) {
            System.err.println("Ошибка при обработке файлов: " + e.getMessage());
        }
    }

    // Метод для чтения строк из файла
    private static List<String> readLinesFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    // Метод для записи строк в файл
    private static void writeLinesToFile(List<String> lines, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    // Метод для извлечения ID из ссылки
    private static String extractId(String link) {
        String[] parts = link.split("/");
        String idPart = parts[parts.length - 1]; // Последний элемент URL
        return idPart.split("\\?")[0]; // Убираем параметры после '?'
    }

    // Метод для извлечения уникальных ID из списка ссылок
    private static Set<String> extractIds(List<String> links) {
        Set<String> ids = new HashSet<>();
        for (String link : links) {
            ids.add(extractId(link));
        }
        return ids;
    }
}
