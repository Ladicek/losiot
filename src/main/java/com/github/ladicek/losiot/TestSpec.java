package com.github.ladicek.losiot;

import java.util.Objects;

public final class TestSpec {
    public final Target target;
    public final DeploymentType deploymentType;
    public final Mission mission;
    public final Runtime runtime;
    public final MavenCoordinates mavenCoordinates;

    public static TestSpec hardcoded() {
        return new TestSpec(
                Target.STAGE,
                DeploymentType.ZIP,
                Mission.M100_HTTP_API,
                Runtime.WILDFLY_SWARM,
                MavenCoordinates.forTest()
        );
    }

    public static TestSpec fromSystemProperties() {
        String target = Objects.requireNonNull(System.getProperty("losiot.target"));
        String deploymentType = Objects.requireNonNull(System.getProperty("losiot.deployment.type"));
        String mission = Objects.requireNonNull(System.getProperty("losiot.mission"));
        String runtime = Objects.requireNonNull(System.getProperty("losiot.runtime"));

        return new TestSpec(
                Target.valueOf(target),
                DeploymentType.valueOf(deploymentType),
                Mission.valueOf(mission),
                Runtime.valueOf(runtime),
                MavenCoordinates.forTest()
        );
    }

    private TestSpec(Target target, DeploymentType deploymentType, Mission mission, Runtime runtime, MavenCoordinates mavenCoordinates) {
        this.target = target;
        this.deploymentType = deploymentType;
        this.mission = mission;
        this.runtime = runtime;
        this.mavenCoordinates = mavenCoordinates;
    }
}
