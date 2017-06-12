package com.github.ladicek.losiot;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public final class ProdTestDriver implements TestDriver {
    private static final String HIDDEN_BUTTON_SELECTOR = "img[src='https://upload.wikimedia.org/wikipedia/commons/c/ce/Transparent.gif']";

    private final WebDriver driver;
    private final Wait<WebDriver> wait;
    private final ElementSelectors by;

    private final Target target;

    ProdTestDriver(WebDriver driver, Target target) {
        assert target == Target.PROD;

        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        this.by = new ElementSelectors();
        this.target = target;
    }

    @Override
    public void loadInitialPage() {
        driver.get(target.url);

        wait.until(ExpectedConditions.elementToBeClickable(by.linkText("Request early access")));
        wait.until(ExpectedConditions.presenceOfElementLocated(by.cssSelector(HIDDEN_BUTTON_SELECTOR)));
    }

    @Override
    public void startWizard() {
        driver.findElement(by.cssSelector(HIDDEN_BUTTON_SELECTOR)).click();

        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("I will build and run locally")));
        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("Use OpenShift Online to build and deploy")));
    }

    @Override
    public void selectDeploymentType(DeploymentType deploymentType) {
        if (deploymentType != DeploymentType.ZIP) {
            // TODO
            throw new UnsupportedOperationException("Deployment type " + deploymentType + " not yet implemented");
        }

        driver.findElement(by.buttonText("I will build and run locally")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("wizard"), "Missions are preconfigured, functioning applications"));
    }

    @Override
    public void selectMission(Mission mission) {
        driver.findElements(by.cssSelector("label"))
                .stream()
                .filter(el -> el.getText().contains(mission.text))
                .forEach(el -> el.findElement(by.tagName("input")).click());

        next();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("wizard"), "We offer a choice of runtime frameworks"));
    }

    @Override
    public void selectRuntime(Runtime runtime) {
        driver.findElements(by.cssSelector("h2"))
                .stream()
                .filter(el -> el.getText().contains(runtime.text))
                .forEach(WebElement::click);

        next();

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("form")));
    }

    @Override
    public void setProjectInfo(MavenCoordinates coords) {
        {
            wait.until(ExpectedConditions.elementToBeClickable(by.cssSelector("input#groupId")));
            WebElement input = driver.findElement(by.cssSelector("input#groupId"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, coords.groupId, Keys.TAB);
        }

        {
            wait.until(ExpectedConditions.elementToBeClickable(by.cssSelector("input#artifactId")));
            WebElement input = driver.findElement(by.cssSelector("input#artifactId"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, coords.artifactId, Keys.TAB);
        }

        {
            wait.until(ExpectedConditions.elementToBeClickable(by.cssSelector("input#version")));
            WebElement input = driver.findElement(by.cssSelector("input#version"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, coords.version, Keys.TAB);
        }

        next();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("wizard"), "Your project is ready"));
    }

    @Override
    public void checkSummary(Mission mission, Runtime runtime, MavenCoordinates coords) {
        String summary = driver.findElement(by.tagName("wizard")).getText();

        assertThat(summary)
                .as("Mission %s must be shown in summary", mission)
                .contains(mission.text);
        assertThat(summary)
                .as("Runtime %s must be shown in summary", runtime)
                .contains(runtime.text);

        assertThat(summary)
                .as("Maven group ID %s must be shown in summary", coords.groupId)
                .contains(coords.groupId);

        assertThat(summary)
                .as("Maven artifact ID %s must be shown in summary", coords.artifactId)
                .contains(coords.artifactId);

        assertThat(summary)
                .as("Maven version %s must be shown in summary", coords.version)
                .contains(coords.version);
    }

    @Override
    public void downloadZip() {
        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("Download as ZIP File")));
        driver.findElement(by.buttonText("Download as ZIP File")).click();
    }

    private void next() {
        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("Next")));
        driver.findElement(by.buttonText("Next")).click();
    }
}
