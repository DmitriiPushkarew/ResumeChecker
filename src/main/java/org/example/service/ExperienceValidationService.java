package org.example.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class  ExperienceValidationService {

    private static final int REQUIRED_EXPERIENCE_MONTHS = 30; // Порог: 2.5 года в месяцах

    private Set<String> processedDescriptions = new HashSet<>(); // Хранилище обработанных текстов для текущего резюме

    public void resetProcessedDescriptions() {
        processedDescriptions.clear(); // Сбрасываем обработанные тексты
    }


    public boolean hasRequiredNecessaryExperience(WebDriver driver) {
        try {
            // Найти все блоки опыта работы
            List<WebElement> experienceBlocks = driver.findElements(By.cssSelector("div.bloko-columns-row"));

            int totalMonths = 0;

            for (WebElement block : experienceBlocks) {
                if (experienceDescriptionContainsJava(block)) { // Проверяем наличие "Java" в описании
                    int months = extractExperienceFromBlock(block); // Извлекаем стаж
                    if (months > 0) {
                        totalMonths += months; // Прибавляем стаж, если он найден
                    } else {
                        // Логируем отсутствие стажа
                        System.err.println("Ошибка: стаж не добавлен для блока с Java: " + block.getText());
                    }
                }
            }

            // Про

            // Проверить, превышает ли общий стаж порог
            return totalMonths >= REQUIRED_EXPERIENCE_MONTHS;

        } catch (Exception e) {
            System.err.println("Ошибка при проверке общего опыта: " + e.getMessage());
            return false;
        }
    }

    private boolean experienceDescriptionContainsJava(WebElement block) {
        try {
            // Найти элемент с описанием опыта
            WebElement descriptionElement = block.findElement(By.cssSelector("div[data-qa='resume-block-experience-description']"));
            String descriptionText = descriptionElement.getText().toLowerCase();

            // Проверить, содержит ли описание слово "Java"
            if (descriptionText.contains("java") && descriptionText.contains("spring")) {
                // Если текст уже обработан, вернуть false
                if (processedDescriptions.contains(descriptionText)) {
                    return false;
                }

                // Сохранить текст как обработанный
                processedDescriptions.add(descriptionText);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Ошибка при проверке описания блока: " + e.getMessage());
        }
        return false;
    }


    private int extractExperienceFromBlock(WebElement block) {
        try {
            // Попытка найти элементы с продолжительностью работы
            List<WebElement> durationElements = block.findElements(By.cssSelector("div.bloko-text.bloko-text_tertiary"));

            for (WebElement durationElement : durationElements) {
                String durationText = durationElement.getText();

                // Проверяем, является ли текст информацией о стаже
                if (isExperienceText(durationText)) {
                    // Извлекаем годы и месяцы из текста
                    int years = extractYears(durationText);
                    int months = extractMonths(durationText);

                    // Логируем успешное извлечение
                    System.out.println("Найден стаж: " + years + " лет, " + months + " месяцев.");
                    return (years * 12) + months; // Возвращаем количество месяцев
                }
            }

            // Если элементы найдены, но подходящий текст не найден
            System.err.println("Ошибка: подходящий текст стажа не найден в блоке: " + block.getText());
            return 0;

        } catch (Exception e) {
            // Логируем ошибку при извлечении стажа
            System.err.println("Ошибка при извлечении стажа из блока: " + e.getMessage());
            return 0;
        }
    }


    private int extractYears(String text) {
        try {
            if (text.contains("год") || text.contains("года") || text.contains("лет")) {
                String[] parts = text.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].matches("(год|года|лет)")) {
                        return Integer.parseInt(parts[i - 1]);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при извлечении лет: " + e.getMessage());
        }
        return 0;
    }

    private int extractMonths(String text) {
        try {
            if (text.contains("месяц") || text.contains("месяца") || text.contains("месяцев")) {
                String[] parts = text.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].matches("(месяц|месяца|месяцев)")) {
                        return Integer.parseInt(parts[i - 1]);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при извлечении месяцев: " + e.getMessage());
        }
        return 0;
    }

    private boolean isExperienceText(String text) {
        // Проверить, содержит ли текст ключевые слова, характерные для стажа
        return text.matches(".*(год|года|лет|месяц|месяца|месяцев).*");
    }

}