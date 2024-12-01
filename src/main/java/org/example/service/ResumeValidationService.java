package org.example.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ResumeValidationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public boolean hasRecentViews(WebDriver driver) {
        try {
            // Найти секцию "Последние просмотры за 3 месяца"
            WebElement section = driver.findElement(By.cssSelector("div.resume-sidebar-section_noprint"));
            List<WebElement> dates = section.findElements(By.cssSelector("div.resume-sidebar-item span"));

            LocalDate currentDate = LocalDate.now();

            for (WebElement dateElement : dates) {
                String dateText = dateElement.getText();
                LocalDateTime viewDateTime = LocalDateTime.parse(dateText, DATE_TIME_FORMATTER);

                // Преобразуем LocalDateTime в LocalDate, чтобы сравнивать только по дате
                LocalDate viewDate = viewDateTime.toLocalDate();

                // Если дата просмотра позже, чем 10 дней назад, возвращаем true
                if (!viewDate.isBefore(currentDate.minusDays(10))) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при проверке даты последних просмотров: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}