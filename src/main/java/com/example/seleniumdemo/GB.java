package com.example.seleniumdemo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class GB {
    public static void crawl() throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.londonstockexchange.com/news-article/DAL/dalata-hotel-group-plc-holding-s-in-company/14875315");
        Thread.sleep(5000);
        String javaScriptCode = "return arguments[0].shadowRoot";
        WebElement hostElement = driver.findElement(By.xpath("//*[@id=\"news-article-content\"]/div[1]/div[6]"));
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        WebElement shadowDom =(WebElement) jse.executeScript(javaScriptCode, hostElement);
        WebElement table = shadowDom.findElement(By.cssSelector("div > table:nth-child(2)"));
        WebElement tr = table.findElement(By.cssSelector("tr"));
        Document parse = Jsoup.parse(table.getAttribute("outerHTML"));
        System.out.println(parse);
        driver.quit();
        driver.close();

    }
}
