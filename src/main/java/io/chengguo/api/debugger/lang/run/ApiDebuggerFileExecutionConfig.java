package io.chengguo.api.debugger.lang.run;

import io.chengguo.api.debugger.ui.ApiDebuggerRequest;

import java.util.List;

public class ApiDebuggerFileExecutionConfig implements ApiDebuggerExecutionConfig {

    private final String mEnvironment;
    private final String mFilePath;

    public ApiDebuggerFileExecutionConfig(String envName, String filePath) {
        mEnvironment = envName;
        mFilePath = filePath;
    }

    @Override
    public List<ApiDebuggerRequest> getRequests() {
        return null;
    }

    @Override
    public String getEnvironment() {
        return mEnvironment;
    }
}
