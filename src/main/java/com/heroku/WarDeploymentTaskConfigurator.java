package com.heroku;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class WarDeploymentTaskConfigurator extends AbstractDeploymentTaskConfigurator {

    @Override
    protected List<String> getRequiredFiles() {
        return ImmutableList.of("war");
    }
}
