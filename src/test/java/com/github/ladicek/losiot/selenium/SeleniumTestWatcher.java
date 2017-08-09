package com.github.ladicek.losiot.selenium;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class SeleniumTestWatcher extends TestWatcher {
    private final WebDriver driver;

    public SeleniumTestWatcher(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(screenshot.toPath(), Paths.get("target", description.getClassName()
                    + "." + description.getMethodName() + "-" + System.currentTimeMillis() + ".png"));
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    protected void finished(Description description) {
        driver.close();
    }
}
