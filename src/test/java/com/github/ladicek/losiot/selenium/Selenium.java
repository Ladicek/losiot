package com.github.ladicek.losiot.selenium;

import com.github.ladicek.losiot.DownloadedZip;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public final class Selenium {
    private Selenium() {} // avoid instantiation

    /** Assumes that Firefox 45 ESR is on PATH. */
    public static WebDriver firefox() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", DownloadedZip.DOWNLOAD_DIRECTORY.toString());
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip");

        return new FirefoxDriver(new FirefoxOptions().setLegacy(true).setProfile(profile));
    }
}
