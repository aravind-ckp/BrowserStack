package steps;

import hooks.Hooks;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebElement;
import pages.OpinionSectionPage;
import utils.TextProcessor;
import utils.TranslationApiClient;

import java.util.ArrayList;
import java.util.List;

public class ElPaisSteps {

    OpinionSectionPage opinionSectionPage;
    TranslationApiClient translationApiClient;
    List<String> translatedTitles = new ArrayList<>();


    public ElPaisSteps() {
        opinionSectionPage = new OpinionSectionPage(Hooks.getDriver());
        translationApiClient = new TranslationApiClient();
    }

    @Given("I navigate to the El Pais Opinion section")
    public void iNavigateToTheElPaisOpinionSection() {
        opinionSectionPage.openSite();
    }

    @Given("Ensure that whole text is in Spanish")
    public void ensureWholeTextIsInSpanish() {
        opinionSectionPage.verifyPageLanguageAttributeIsSpanish();
    }

    @When("I navigate to the Opinion section")
    public void i_navigate_to_the_opinion_section() {
        opinionSectionPage.navigateToOpinionSection();
    }

    @When("I fetch the first five articles")
    public void iFetchTheFirstFiveArticles() {
        opinionSectionPage.getFirstFiveArticles();
    }

    @Then("I print titles and contents in Spanish")
    public void printStoredArticlesInSpanish() {
        opinionSectionPage.printStoredArticlesInSpanish();
    }

    @And("I download cover images if available")
    public void downloadCoverImagesIfAvailable() {
        opinionSectionPage.downloadCoverImagesForFetchedArticles();
    }

    @Then("I translate and print the titles of the first five articles in english")
    public void translateTitlesToEnglish() {
        translatedTitles.clear();
        List<WebElement> articles = opinionSectionPage.getFirstFiveArticles();
        for (WebElement article : articles) {
            String originalTitle = article.getText().trim();
            String translated = translationApiClient.translateToEnglish(originalTitle);
            System.out.println("Original Title in Spanish: " + originalTitle);
            System.out.println("Translated Title in English: " + translated);
            System.out.println("-------------------------");
            translatedTitles.add(translated);
        }
    }

    @And("I analyze translated headers for repeated words and print them")
    public void iAnalyzeTranslatedHeadersForRepeatedWordsAndPrintThem() {
        if (translatedTitles.isEmpty()) {
            throw new IllegalStateException("No translated titles available. Did you run the translation step?");
        }
        TextProcessor.analyzeRepeatedWords(translatedTitles);
    }

}
