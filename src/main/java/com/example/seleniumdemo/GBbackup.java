package com.example.seleniumdemo;

import com.google.gson.Gson;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
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
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GBbackup {
    private List<String> keyWords = new ArrayList<String>() {{
        add("share dealing");
        add("shares dealing");
        add("acquisition of shares");
        add("own shares buyback");
        add("purchase of shares");
        add("own securities");
        add("buy back");
        add("buyback");
        add("treasury share");
        add("treasury stock");
        add("buy-back");
        add("share repurchase");
        add("transaction in own securities");
        add("shares buy-back");
        add("own shares");
        add("transaction in own shares");
        add("repurchase");
        add("treasury");
        add("repurchase");
        add("treasury");
        add("own");
        add("shares buyback");
        add("share purchase");
        add("shares purchase");
        add("dealing in company share");
        add("dealing in company shares");
        add("buyback program");
        add("tender offer");
        add("purchase");
        add("ESOT");
        add("tender price");
        add("AGM");
        add("share purchase");
        add("redemption of shares");
        add("it’s own shares");
    }};



    public void startBot() {
        try {
            scrapeUKAnnouncements();
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }

    }

    private void scrapeUKAnnouncements() throws IOException, JSONException {
        int currentPage = 0;
        String srcUrl = "https://api.londonstockexchange.com/api/v1/components/refresh";
        final String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Mobile Safari/537.36";
        int totalPages = 10;
        String refrerer = "https://www.londonstockexchange.com/news?tab=news-explorer&period=lastmonth&headlinetypes=&excludeheadlines=";
        String jsonBody = "{ " +
                "\"path\":\"news\"," +
                "\"parameters\":\"tab%3Dnews-explorer%26period%3Dcustom%26beforedate%3D20190304%26afterdate%3D20190204%26tabId%3D58734a12-d97c-40cb-8047-df76e660f23f\"," +
                "\"components\":" +
                "[ " +
                "{\"componentId\":\"block_content%3A431d02ac-09b8-40c9-aba6-04a72a4f2e49\"," +
                "\"parameters\":\"period=custom&afterdate=20190304&beforedate=20190204&page=21&size=20&sort=datetime,desc\"}" +
                "]" +
                "}";
        for (int i = 0; i <= totalPages; i++) {
            Connection.Response response = Jsoup.connect(srcUrl)
                    .userAgent(USER_AGENT)
                    .header("Content-Type", "application/json")
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .header("Accept", "application/json")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("User-Agent", USER_AGENT)
                    .header("origin", "https://www.londonstockexchange.com")
                    .header("referer", refrerer)
                    .requestBody(jsonBody)
                    .method(Connection.Method.POST)
                    .execute();
            JSONArray announcements = new JSONArray(response.body());
            JSONObject announcementsJSONObject = announcements.getJSONObject(0);
            totalPages = Integer.parseInt(announcementsJSONObject
                    .getJSONArray("content")
                    .getJSONObject(1)
                    .getJSONObject("value").get("totalPages").toString());
            JSONArray result = announcementsJSONObject
                    .getJSONArray("content")
                    .getJSONObject(1)
                    .getJSONObject("value")
                    .getJSONArray("content");
            GBResponseModel[] gbResponseModel = new Gson().fromJson(result.toString(), GBResponseModel[].class);
            for (GBResponseModel gbModel : gbResponseModel) {
                System.out.println("page  " + i + "   " + gbModel.getId() + "       " + gbModel.getCompanyname());
                boolean found = false;
                for (String word : keyWords) {
                    if (gbModel.getTitle().toLowerCase().contains(word.toLowerCase())) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String url = "https://www.londonstockexchange.com/news-article/" + gbModel.getCompanycode() + "/" + gbModel.getTitle() + "/" + gbModel.getId();
                backUpData(url,gbModel.getId());
                System.out.println(gbModel.getDatetime() + " " + gbModel.getTitle());

            }
            currentPage += i;
//      refrerer = "https://www.londonstockexchange.com/news?tab=news-explorer&period=lastmonth&headlinetypes=&excludeheadlines=&page=" + currentPage;
            jsonBody = "{" +
                    "\"path\":\"news\"," +
                    "\"parameters\":\"tab%3Dnews-explorer%26period%3Dcustom%26beforedate%3D20190304%26afterdate%3D20190204%26page%3D" + i + "%26tabId%3D58734a12-d97c-40cb-8047-df76e660f23f\"," +
                    "\"components\":" +
                    "[ " +
                    "{\"componentId\":\"block_content%3A431d02ac-09b8-40c9-aba6-04a72a4f2e49\"," +
                    "\"parameters\":\"period=custom&afterdate=20190304&beforedate=20190204&page=" + i + "&size=20&sort=datetime,desc\"}" +
                    "]" +
                    "}";

        }
        System.out.println("annoucements added");

    }

    private void backUpData(String url,String fileName) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--start-fullscreen");

        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.get(url);
        Screenshot screenshot;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.findElement(By.xpath("//*[@id=\"ccc-notify-accept\"]")).click();
        WebElement element = driver.findElement(By.xpath("//*[@id=\"news-article-content\"]"));
        String content = element.getText();
        try (FileWriter fileWriter = new FileWriter("d://temp//"+fileName+".txt")) {
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
            ImageIO.write(screenshot.getImage(), "jpg", new File("d://temp//"+fileName+".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            takeSnapShot(driver, "d://temp//"+fileName+".jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        driver.close();
        driver.quit();

    }

    private void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {
        TakesScreenshot scrShot = ((TakesScreenshot) webdriver);
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File DestFile = new File(fileWithPath);
        FileUtils.copyFile(SrcFile, DestFile);
    }

}
