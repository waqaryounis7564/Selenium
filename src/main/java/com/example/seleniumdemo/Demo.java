package com.example.seleniumdemo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Demo {


    public static void crawl() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--start-fullscreen");

        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get("https://www.londonstockexchange.com/news-article/SLA/Transaction in Own Shares/14850541");
        Screenshot screenshot;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.findElement(By.xpath("//*[@id=\"ccc-notify-accept\"]")).click();
        WebElement element = driver.findElement(By.xpath("//*[@id=\"news-article-content\"]"));
        String content = element.getText();
        try(FileWriter fileWriter=new FileWriter("d://temp//test.txt")){
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            driver.findElement(By.xpath("//*[@id=\"news-disclaimer\"]/div/div/div/div[5]/div[1]/div[1]")).click();
        } catch (NoSuchElementException e) {
            e.getCause();
        }
        screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
        try {
            ImageIO.write(screenshot.getImage(), "jpg", new File("d://temp//test.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            takeSnapShot(driver,"d://test.png") ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.close();
        driver.quit();

    }

    public static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {
        TakesScreenshot scrShot = ((TakesScreenshot) webdriver);
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File DestFile = new File(fileWithPath);
        FileUtils.copyFile(SrcFile, DestFile);
    }

}



