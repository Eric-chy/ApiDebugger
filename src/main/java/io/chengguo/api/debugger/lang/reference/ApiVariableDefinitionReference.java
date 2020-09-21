package io.chengguo.api.debugger.lang.reference;

import com.intellij.json.JsonUtil;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.indexing.FileBasedIndex;
import io.chengguo.api.debugger.lang.ApiPsiUtil;
import io.chengguo.api.debugger.lang.environment.ApiEnvironmentIndex;
import io.chengguo.api.debugger.lang.environment.ApiEnvironmentInputFilter;
import io.chengguo.api.debugger.lang.psi.ApiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 变量定义引用
 *
 * @param <T>
 */
public class ApiVariableDefinitionReference<T extends ApiVariable> extends PsiPolyVariantReferenceBase<T> {

    private TextRange mRangeInElement;
    private String mIdentifier;

    public ApiVariableDefinitionReference(@NotNull T element, TextRange range) {
        super(element, range);
        PsiElement identifier = myElement.getIdentifier();
        if (identifier != null) {
            int startOffset = identifier.getTextRange().getStartOffset() - myElement.getTextRange().getStartOffset();
            mRangeInElement = new TextRange(startOffset, startOffset + identifier.getTextLength());
            mIdentifier = identifier.getText();
        }
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        return mRangeInElement == null ? super.getRangeInElement() : mRangeInElement;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (StringUtil.isEmpty(mIdentifier)) {
            return ResolveResult.EMPTY_ARRAY;
        }
        Project project = myElement.getProject();
        PsiFile containingFile = myElement.getContainingFile();
        GlobalSearchScope scope = ApiEnvironmentIndex.getSearchScope(project, containingFile);
        List<ResolveResult> result = new ArrayList<>();
        String defaultEnv = "default env";// TODO 当前的默认环境优先
        addVariableDefinitions(project, mIdentifier, defaultEnv, result, scope);

        for (String environment : ApiEnvironmentIndex.getAllEnvironments(project, scope)) {
            if (!StringUtil.equals(environment, defaultEnv)) {
                addVariableDefinitions(project, mIdentifier, environment, result, scope);
            }
        }
        return result.isEmpty() ? ResolveResult.EMPTY_ARRAY : result.toArray(ResolveResult.EMPTY_ARRAY);
    }

    private void addVariableDefinitions(Project project, String name, String env, List<ResolveResult> result, GlobalSearchScope scope) {
        FileBasedIndex.getInstance().processValues(ApiEnvironmentIndex.KEY, env, null, (file, value) -> {
            if (value.contains(name)) {
                PsiFile psiFile = ApiPsiUtil.findFileByVF(project, file);
                if (!(psiFile instanceof JsonFile)) {
                    return true;
                }
                JsonValue root = ((JsonFile) psiFile).getTopLevelValue();
                JsonObject environment = (root instanceof JsonObject) ? JsonUtil.getPropertyValueOfType((JsonObject) root, env, JsonObject.class) : null;
                JsonProperty property = (environment != null) ? environment.findProperty(name) : null;
                if (property != null) {
                    result.add(new PsiElementResolveResult(property));
                }
            }
            return true;
        }, scope);
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        return couldBeReferenceTo(element);
    }

    private boolean couldBeReferenceTo(@NotNull PsiElement element) {
        if (element instanceof JsonProperty && StringUtil.equals(((JsonProperty) element).getName(), myElement.getName()) && element.isValid() && element.isPhysical()) {
            PsiFile file = element.getContainingFile();
            return file != null && ApiEnvironmentInputFilter.isApiEnvFile(file.getVirtualFile());
        }
        return false;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return myElement.setName(newElementName);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        // ⌃Space 或 输入时，出现在建议选项中
        return super.getVariants();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}