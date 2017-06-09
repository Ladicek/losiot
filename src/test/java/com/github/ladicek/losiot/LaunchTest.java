package com.github.ladicek.losiot;

import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LaunchTest {
    private WebDriver driver = Selenium.firefox();

    @Rule
    public SeleniumTestWatcher watcher = new SeleniumTestWatcher(driver);

    @Test
    public void downloadAndVerifyZip() throws IOException {
        TestSpec spec = TestSpec.fromSystemProperties();

        TestDriver test = new TestDriver(driver, spec.target);

        test.loadInitialPage();
        test.startWizard();
        test.selectDeploymentType(spec.deploymentType);
        test.selectMission(spec.mission);
        test.selectRuntime(spec.runtime);
        test.setProjectInfo(spec.mavenCoordinates);
        test.checkSummary(spec.mission, spec.runtime, spec.mavenCoordinates);
        test.downloadZip();

        try (DownloadedZip zip = DownloadedZip.find()) {
            String pomPath = spec.mavenCoordinates.artifactId + "/pom.xml";
            Optional<String> pomText = zip.readFileAsString(pomPath);
            assertThat(pomText)
                    .as("POM file %s must be present", pomPath)
                    .isPresent();
            pomText.ifPresent(pom -> {
                assertThat(pom)
                        .as("POM must contain groupId %s", spec.mavenCoordinates.groupId)
                        .contains("<groupId>" + spec.mavenCoordinates.groupId + "</groupId>");
                assertThat(pom)
                        .as("POM must contain artifactId %s", spec.mavenCoordinates.artifactId)
                        .contains("<artifactId>" + spec.mavenCoordinates.artifactId + "</artifactId>");
                assertThat(pom)
                        .as("POM must contain version %s", spec.mavenCoordinates.version)
                        .contains("<version>" + spec.mavenCoordinates.version + "</version>");
            });

            String licensePath = spec.mavenCoordinates.artifactId + "/LICENSE";
            Optional<String> licenseText = zip.readFileAsString(licensePath);
            assertThat(licenseText)
                    .as("License file %s must be present")
                    .isPresent();
            licenseText.ifPresent(license -> {
                assertThat(license)
                        .as("Apache 2 license must be present")
                        .contains("Apache License")
                        .contains("Version 2.0, January 2004");
            });

            // ideally, we should unpack the .zip, run a Maven build, maybe execute tests etc. in here,
            // but this is already covered by booster testing, so it isn't high priority
        }
    }
}
