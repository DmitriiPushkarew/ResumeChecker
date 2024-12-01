package org.example.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ResumeNavigatorService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${resume.file-path}")
    private String filePath;

    @Value("${browser.profile-path}")
    private String browserProfilePath; // Путь к профилю браузера

    private WebDriver driver;

    @Value("${selenium.geckodriver-path}")
    private String geckoDriverPath;

    @Autowired
    private ResumeValidationService validationService;


    @Value("${resume.output-file-path}")
    private String outputFilePath; // Путь к файлу для сохранения

    public void navigateThroughResumes() {
        initializeDriver();

        try {
            Resource resource = resourceLoader.getResource(filePath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String resumeUrl;
                while ((resumeUrl = reader.readLine()) != null) {
                    if (!resumeUrl.isBlank()) {
                        System.out.println("Переход на резюме: " + resumeUrl);
                        driver.get(resumeUrl);
                        Thread.sleep(3000); // Задержка 3 секунды

                        if (validationService.hasRecentViews(driver)
                                || !validationService.validateExperience(driver)) {
                            System.out.println("Не соответствует");
                        } else {
                            System.out.println("соответствует.");
                            ensureOutputDirectoryExists(outputFilePath);
                            saveToFile(resumeUrl, outputFilePath); // Сохранение URL в файл
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка при обработке резюме: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDriver();
        }
    }



    private void initializeDriver() {
        System.setProperty("webdriver.gecko.driver", geckoDriverPath);
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--profile", browserProfilePath);

        try {
            driver = new FirefoxDriver(options);
            System.out.println("Браузер запущен.");
        } catch (Exception e) {
            System.err.println("Не удалось запустить браузер: " + e.getMessage());
            throw e; // Пробрасываем исключение, чтобы предотвратить продолжение выполнения
        }
    }

    private void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("Браузер закрыт.");
            } catch (Exception e) {
                System.err.println("Ошибка при закрытии браузера: " + e.getMessage());
            }
        }
    }

    private void saveToFile(String content, String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private void ensureOutputDirectoryExists(String outputPath) {
        File file = new File(outputPath).getParentFile();
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
    }
}
