package com.github.ladicek.losiot;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public final class StageTestDriver implements TestDriver {
    private final WebDriver driver;
    private final Wait<WebDriver> wait;
    private final ElementSelectors by;

    private final Target target;

    StageTestDriver(WebDriver driver, Target target) {
        assert target == Target.STAGE;

        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        this.by = new ElementSelectors();
        this.target = target;
    }

    @Override
    public void loadInitialPage() {
        driver.get(target.url);

        wait.until(ExpectedConditions.elementToBeClickable(by.linkText("Prepare for Takeoff")));
    }

    @Override
    public void startWizard() {
        driver.findElement(by.linkText("Prepare for Takeoff")).click();

        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("I will build and run locally")));
        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("Use OpenShift Online")));
    }

    @Override
    public void selectDeploymentType(DeploymentType deploymentType) {
        if (deploymentType != DeploymentType.ZIP) {
            // TODO
            throw new UnsupportedOperationException("Deployment type " + deploymentType + " not yet implemented");
        }

        driver.findElement(by.buttonText("I will build and run locally")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("mission")));
    }

    @Override
    public void selectMission(Mission mission) {
        driver.findElements(by.cssSelector("mission label h3"))
                .stream()
                .filter(el -> el.getText().contains(mission.text))
                .forEach(el -> el.findElement(by.tagName("input")).click());

        next();

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("runtime")));
    }

    @Override
    public void selectRuntime(Runtime runtime) {
        driver.findElements(by.cssSelector("runtime h2"))
                .stream()
                .filter(el -> el.getText().contains(runtime.text))
                .forEach(WebElement::click);

        next();

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("projectinfo")));
    }

    @Override
    public void setProjectInfo(MavenCoordinates coords) {
        {
            WebElement input = driver.findElement(by.cssSelector("input#groupId"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, coords.groupId, Keys.TAB);
        }

        {
            WebElement input = driver.findElement(by.cssSelector("input#artifactId"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, coords.artifactId, Keys.TAB);
        }

        {
            WebElement input = driver.findElement(by.cssSelector("input#version"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, coords.version, Keys.TAB);
        }

        next();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("deploy"), "Your project is ready"));
    }

    @Override
    public void checkSummary(Mission mission, Runtime runtime, MavenCoordinates coords) {
        String summary = driver.findElement(by.tagName("deploy")).getText();

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

        assertThat(summary)
                .as("{guideName} must not be shown in summary")
                .doesNotContain("{guideName}");
    }

    @Override
    public void downloadZip() {
        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("Download as ZIP File")));
        driver.findElement(by.buttonText("Download as ZIP File")).click();
    }

    private void next() {
        driver.findElement(by.buttonText("Next")).click();
    }
}
