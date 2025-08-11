package pages;

import hooks.Hooks;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpinionSectionPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By opinionMenuLink = By.xpath("(//a[text()='Opinión'])[1]");
    private final By articleLinks = By.cssSelector("h2 a, h3 a");

    private List<WebElement> firstFiveArticles;

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    public OpinionSectionPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openSite() {
        driver.get("https://elpais.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Try Spanish 'Aceptar' button
            WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("didomi-notice-agree-button")));
            acceptBtn.click();
            logger.info("'Aceptar' button clicked successfully.");
        } catch (TimeoutException e1) {
            logger.info("'Aceptar' button did not appear. Trying 'Learn More'...");

            try {
                // Try English 'Learn More' (close or proceed afterwards)
                WebElement learnMoreBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("didomi-notice-learn-more-button")));
                learnMoreBtn.click();
                logger.info("'Learn More' button clicked to close consent banner.");
            } catch (TimeoutException e2) {
                logger.info("No Didomi consent banner detected.");
            }
        }
    }


    public void navigateToOpinionSection() {
        // Try to dismiss the consent/paywall overlay if it exists
        try {
            By consentButton = By.cssSelector(".pmConsentWall-button");
            WebElement consentBtn = wait.until(ExpectedConditions.elementToBeClickable(consentButton));
            consentBtn.click();
            logger.info("Consent wall accepted.");
            // Wait for consent overlay to disappear before continuing
            wait.until(ExpectedConditions.invisibilityOfElementLocated(consentButton));
        } catch (TimeoutException e) {
            logger.info("No consent wall present or already accepted.");
        }

        // Now click the "Opinión" menu link safely
        WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(opinionMenuLink));
        opinionLink.click();
        logger.info("Navigated to Opinion section.");
    }

    public List<WebElement> getFirstFiveArticles() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(articleLinks));
        List<WebElement> links = driver.findElements(articleLinks);
        return links.subList(0, Math.min(5, links.size()));
    }

    public void verifyPageLanguageAttributeIsSpanish() {
        WebElement htmlElement = driver.findElement(By.tagName("html"));
        String langAttribute = htmlElement.getAttribute("lang");

        if (langAttribute == null || !langAttribute.equalsIgnoreCase("es-ES")) {
            throw new AssertionError("Page 'lang' attribute is not 'es-ES'. Found: " + langAttribute);
        }
    }

    public void printStoredArticlesInSpanish() {
        List<WebElement> firstFiveArticles = getFirstFiveArticles();
        if (firstFiveArticles == null || firstFiveArticles.isEmpty()) {
            throw new IllegalStateException("No articles have been fetched yet.");
        }

        for (WebElement link : firstFiveArticles) {
            String titulo = link.getText().trim();
            String contenido;
            try {
                WebElement container = link.findElement(By.xpath("./ancestor::*[self::article or self::div]"));
                contenido = container.findElement(By.cssSelector("p")).getText().trim();
            } catch (NoSuchElementException e) {
                contenido = "[No se encontró resumen]";
            }
            System.out.println("Título: " + titulo);
            System.out.println("Contenido: " + contenido);
            System.out.println("-------------------------");
        }
    }

    public void downloadCoverImagesForFetchedArticles() {
        List<WebElement> firstFiveArticles = getFirstFiveArticles();
        if (firstFiveArticles == null || firstFiveArticles.isEmpty()) {
            throw new IllegalStateException("No articles have been fetched yet.");
        }

        for (int i = 0; i < firstFiveArticles.size(); i++) {
            WebElement article = firstFiveArticles.get(i);
            try {
                WebElement h2Element = article.findElement(By.xpath("./ancestor::h2"));
                WebElement imgElement = h2Element.findElement(By.xpath("../../figure/a/img"));

                String srcset = imgElement.getAttribute("srcset");
                if (srcset != null && !srcset.isEmpty()) {
                    String imageUrl = getLargestImageFromSrcset(srcset);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        saveImageFromUrl(imageUrl, "article_" + (i + 1) + ".jpg");
                        logger.info("Image saved: article_" + (i + 1) + ".jpg");
                    } else {
                        logger.info("No valid image URL found in srcset for article " + (i + 1));
                    }
                } else {
                    logger.info("No srcset attribute found for article " + (i + 1));
                }
            } catch (Exception e) {
                logger.info("No image found for article " + (i + 1));
            }
        }
    }

    private String getLargestImageFromSrcset(String srcset) {
        String[] parts = srcset.split(",");
        String largestUrl = null;
        int maxWidth = 0;

        for (String part : parts) {
            part = part.trim();
            String[] urlAndWidth = part.split(" ");
            if (urlAndWidth.length == 2) {
                String url = urlAndWidth[0];
                String widthStr = urlAndWidth[1].replace("w", "").trim();
                try {
                    int width = Integer.parseInt(widthStr);
                    if (width > maxWidth) {
                        maxWidth = width;
                        largestUrl = url;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return largestUrl;
    }

    private void saveImageFromUrl(String imageUrl, String fileName) {
        String folderPath = "downloaded_images";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        File outputFile = new File(folder, fileName);
        try (InputStream in = new URL(imageUrl).openStream();
             FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            System.out.println("Failed to save image: " + fileName + " due to " + e.getMessage());
        }
    }
}
