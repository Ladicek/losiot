package com.github.ladicek.losiot;

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
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private TestSpec(Target target, DeploymentType deploymentType, Mission mission, Runtime runtime, MavenCoordinates mavenCoordinates) {
        this.target = target;
        this.deploymentType = deploymentType;
        this.mission = mission;
        this.runtime = runtime;
        this.mavenCoordinates = mavenCoordinates;
    }
}
