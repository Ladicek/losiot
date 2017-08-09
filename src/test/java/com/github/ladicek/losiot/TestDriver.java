package com.github.ladicek.losiot;

import org.openqa.selenium.WebDriver;

public interface TestDriver {
    static TestDriver create(WebDriver driver, Target target) {
        switch (target) {
            case STAGE:
                return new StageTestDriver(driver, target);
            case PROD:
                return new ProdTestDriver(driver, target);
            default:
                throw new UnsupportedOperationException(target + " not implemented");
        }
    }

    void loadInitialPage();

    void login();

    void startWizard();

    void selectDeploymentType(DeploymentType deploymentType);

    void checkIfNotLoggedIn();

    void selectMission(Mission mission);

    void selectRuntime(Runtime runtime);

    void setProjectInfo(MavenCoordinates coords);

    void checkSummary(DeploymentType deploymentType, Mission mission, Runtime runtime, MavenCoordinates coords);

    void downloadZip();

    void checkNextSteps();
}
