package io.chengguo.api.debugger.lang.run;

import com.intellij.configurationStore.XmlSerializer;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.xmlb.annotations.Attribute;
import io.chengguo.api.debugger.ApiDebuggerBundle;
import io.chengguo.api.debugger.lang.ApiPsiFile;
import io.chengguo.api.debugger.lang.ApiPsiUtils;
import io.chengguo.api.debugger.lang.ApiVariableReplacer;
import io.chengguo.api.debugger.lang.environment.ApiEnvironment;
import io.chengguo.api.debugger.lang.psi.ApiApiBlock;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiDebuggerDefaultRunConfiguration extends LocatableConfigurationBase {

    private Settings mSettings;

    protected ApiDebuggerDefaultRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
        mSettings = createSettings();
    }

    protected ApiDebuggerDefaultRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
        this(project, factory, null);
    }

    @NotNull
    private Settings createSettings() {
        return new Settings();
    }

    public void setSettings(Settings settings) {
        mSettings = settings;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ApiDebuggerDefaultRunConfigurationSettingsEditor(getProject());
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        XmlSerializer.deserializeInto(element, (mSettings = createSettings()));
        if (mSettings != null) {
            String filePath = mSettings.getFilePath();
            if (StringUtil.isNotEmpty(filePath)) {
                mSettings.setFilePath(FileUtil.toSystemDependentName(filePath));
            }
        }
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        if (mSettings != null) {
            Settings settings = mSettings.clone();
            String filePath = settings.getFilePath();
            if (StringUtil.isNotEmpty(filePath)) {
                settings.setFilePath(FileUtil.toSystemIndependentName(filePath));
            }
            XmlSerializer.serializeObjectInto(mSettings, element);
        }
        super.writeExternal(element);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        createConfig();
    }

    private ApiDebuggerExecutionConfig createConfig() throws RuntimeConfigurationException {
        Project project = getProject();
        String filePath = mSettings.getFilePath();
        if (StringUtil.isEmpty(filePath)) {
            throw new RuntimeConfigurationException(ApiDebuggerBundle.message("api.debugger.run.configuration.file_is_not_configured"));
        }
        PsiFile file = ApiPsiUtils.findFileByPath(project, filePath);
        if (file == null) {
            throw new RuntimeConfigurationException(ApiDebuggerBundle.message("api.debugger.run.configuration.file_doesnt_exists"));
        }
        if (file instanceof ApiPsiFile && mSettings.getRunFileType() == RunFileType.ALL_IN_FILE) {
            return new ApiDebuggerFileExecutionConfig(mSettings.getEnvName(), mSettings.getFilePath());
        }
        ApiApiBlock[] apiBlocks = ApiPsiUtils.findApiBlocks(file);
        int index = mSettings.getIndexInFile();
        int length = apiBlocks.length;
        if (index >= length || index < 0) {
            throw new RuntimeConfigurationException(ApiDebuggerBundle.message("api.debugger.run.configuration.api_request_doesnt_exists"));
        }
        return new ApiDebuggerSingleRequestExecutionConfig(mSettings.getEnvName(), mSettings.getFilePath(), mSettings.getIndexInFile());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        try {
            Project project = getProject();
            ApiDebuggerExecutionConfig config = createConfig();
            ApiVariableReplacer variableReplacer = ApiVariableReplacer.create(ApiEnvironment.create(project, config.getEnvironment()));
//            SMTRunnerConsoleProperties properties = new SMTRunnerConsoleProperties(project , this, "", executor);
            return new ApiHttpRequestRunProfileState(project, variableReplacer, config);
        } catch (RuntimeConfigurationException e) {
            throw new ExecutionException(e.getMessage());
        }
    }

    public Settings getSettings() {
        return mSettings;
    }

    @Override
    public RunConfiguration clone() {
        ApiDebuggerDefaultRunConfiguration clone = (ApiDebuggerDefaultRunConfiguration) super.clone();
        clone.setSettings(mSettings.clone());
        return clone;
    }

    public static class Settings {
        private RunFileType runFileType;
        private String envName;
        private String filePath;
        private int indexInFile;

        public Settings() {
            this.runFileType = RunFileType.SINGLE_REQUEST;
            envName = ApiEnvironment.empty().getName();
        }

        @Attribute("runFileType")
        public RunFileType getRunFileType() {
            return runFileType;
        }

        public void setRunFileType(@Nullable RunFileType runFileType) {
            if (runFileType == null) {
                runFileType = RunFileType.SINGLE_REQUEST;
            }
            this.runFileType = runFileType;
        }

        @Attribute("environment")
        public String getEnvName() {
            return envName;
        }

        public void setEnvName(@Nullable String envName) {
            this.envName = envName;
        }

        @Attribute("path")
        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        @Attribute("index")
        public int getIndexInFile() {
            return indexInFile;
        }

        public void setIndexInFile(int indexInFile) {
            this.indexInFile = indexInFile;
        }

        @Override
        public Settings clone() {
            Settings settings = new Settings();
            settings.setRunFileType(getRunFileType());
            settings.setEnvName(getEnvName());
            settings.setFilePath(getFilePath());
            settings.setIndexInFile(getIndexInFile());
            return settings;
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "runFileType=" + runFileType +
                    ", envName='" + envName + '\'' +
                    ", filePath='" + filePath + '\'' +
                    ", indexInFile=" + indexInFile +
                    '}';
        }
    }
}
