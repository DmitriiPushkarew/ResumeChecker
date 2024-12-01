package org.example.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ResumeValidationService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private final ExperienceValidationService experienceValidationService;

    public ResumeValidationService(ExperienceValidationService experienceValidationService) {
        this.experienceValidationService = experienceValidationService;
    }


    public boolean hasRecentViews(WebDriver driver) {
        try {
            // Найти секцию "Последние просмотры за 3 месяца"
            WebElement section = driver.findElement(By.cssSelector("div.resume-sidebar-section_noprint"));

            // Предполагаем, что последняя дата всегда первая или последняя в списке
            WebElement lastDateElement = section.findElement(By.cssSelector("div.resume-sidebar-item span:last-of-type"));

            String dateText = lastDateElement.getText();
            LocalDate currentDate = LocalDate.now();

            try {
                LocalDate lastViewDate = LocalDate.parse(dateText, DATE_TIME_FORMATTER);

                boolean result =!lastViewDate.isBefore(currentDate.minusDays(10));;
                if(result) {
                    System.out.println("Смотрели недавно");
                    return true;
                }else{
                    return false;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Неверный формат даты: " + dateText);
            }

        } catch (NoSuchElementException e) {
            System.err.println("Секция 'Последние просмотры' или дата не найдена: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


    public boolean validateExperience(WebDriver driver) {
        boolean hasRequiredExperience = experienceValidationService.hasRequiredNecessaryExperience(driver);
        if (hasRequiredExperience) {
            System.out.println("Кандидат имеет достаточный опыт с Java и Spring.");
        } else {
            System.out.println("Кандидат не имеет достаточного опыта с Java и Spring.");
        }

        return hasRequiredExperience;
    }}