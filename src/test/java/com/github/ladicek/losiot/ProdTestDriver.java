package com.github.ladicek.losiot;

import com.github.ladicek.losiot.selenium.ElementSelectors;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public final class ProdTestDriver implements TestDriver {
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

        wait.until(ExpectedConditions.elementToBeClickable(by.linkText("Log in")));
        wait.until(ExpectedConditions.elementToBeClickable(by.linkText("Prepare for Takeoff")));
    }

    @Override
    public void login() {
        // TODO
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
        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("mission"), "Mission"));
    }

    @Override
    public void checkIfNotLoggedIn() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by.buttonText("Log in or register")));
    }

    @Override
    public void selectMission(Mission mission) {
        driver.findElements(by.cssSelector("mission label h3"))
                .stream()
                .filter(el -> el.getText().contains(mission.text))
                .forEach(el -> el.findElement(by.tagName("input")).click());

        next();

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("runtime")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("runtime"), "Runtime"));
    }

    @Override
    public void selectRuntime(Runtime runtime) {
        driver.findElements(by.cssSelector("runtime h2"))
                .stream()
                .filter(el -> el.getText().contains(runtime.text))
                .forEach(WebElement::click);

        next();

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("projectinfo")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("projectinfo"), "Project Info"));
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

        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("deploy")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("deploy"), "Review Summary"));
    }

    @Override
    public void checkSummary(DeploymentType deploymentType, Mission mission, Runtime runtime, MavenCoordinates coords) {
        String summary = driver.findElement(by.tagName("deploy")).getText();

        assertThat(summary)
                .as("Deployment type %s must be shown in summary", deploymentType)
                .contains(deploymentType.text);
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

    @Override
    public void checkNextSteps() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by.tagName("nextsteps")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(by.tagName("nextsteps"), "Next Steps"));

        String summary = driver.findElement(by.tagName("nextsteps")).getText();

        assertThat(summary)
                .as("Next steps should refer to README")
                .contains("README.adoc");
    }

    private void next() {
        wait.until(ExpectedConditions.elementToBeClickable(by.buttonText("Next")));
        driver.findElement(by.buttonText("Next")).click();
    }
}
