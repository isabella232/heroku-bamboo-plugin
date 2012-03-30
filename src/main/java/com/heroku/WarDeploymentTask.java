package com.heroku;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.*;
import com.herokuapp.directto.client.DirectToHerokuClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WarDeploymentTask implements TaskType {

    private final StaticSandbox staticSandbox;

    public WarDeploymentTask() {
        this(new StaticSandbox() {
            public TaskResult success(TaskContext taskContext) {
                return TaskResultBuilder.create(taskContext).success().build();
            }
        });
    }

    public WarDeploymentTask(StaticSandbox staticSandbox) {
        this.staticSandbox = staticSandbox;
    }

    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException {
        final BuildLogger buildLogger = taskContext.getBuildLogger();

        final String apiKey = taskContext.getConfigurationMap().get("apiKey");
        final String appName = taskContext.getConfigurationMap().get("appName");
        final String artifactPath = taskContext.getConfigurationMap().get("artifactPath");

        final DirectToHerokuClient directClient = new DirectToHerokuClient(apiKey);

        final Map<String, File> files = new HashMap<String, File>(1);
        files.put("war", new File(taskContext.getWorkingDirectory().getAbsolutePath() + "/" + artifactPath));

        final Map<String, String> deployResults;
        try {
            deployResults = directClient.deploy("war", appName, files);
        } catch (InterruptedException e) {
            throw new TaskException("Deployment was interrupted", e);
        }

        for (Map.Entry<String, String> result : deployResults.entrySet()) {
            buildLogger.addBuildLogEntry(result.getKey() + ":" + result.getValue());
        }
        
        return staticSandbox.success(taskContext);
    }

    static interface StaticSandbox {
        TaskResult success(TaskContext taskContext);
    }
}