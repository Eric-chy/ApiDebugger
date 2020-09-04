package io.chengguo.api.debugger.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import io.chengguo.api.debugger.lang.ApiVariableReplacer;
import io.chengguo.api.debugger.lang.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiPsiImplUtils {

    public static PsiElement getIdentifier(PsiElement element) {
        ASTNode node = element.getNode().findChildByType(ApiTypes.Api_IDENTIFIER);
        return node == null ? null : node.getPsi();
    }

    /**
     * 获取
     * @param element
     * @return
     */
    public static String getElementTextNotNull(PsiElement element) {
        return element == null ? "" : element.getText();
    }

    /**
     * 获取HTTP scheme，默认http
     *
     * @param element
     * @return
     */
    public static String getScheme(ApiScheme element) {
        return element == null ? ApiTypes.Api_HTTP.toString() : element.getText();
    }

    /**
     * 获取URL，不包含参数部分
     *
     * @param element
     * @param replacer
     * @return
     */
    public static String getUrl(ApiRequestTarget element, ApiVariableReplacer replacer) {
        StringBuilder sb = new StringBuilder();
        sb.append(getScheme(element.getScheme()));
        sb.append(ApiTypes.Api_SCHEME_SEPARATOR).append(replacer.getValue(element.getHost()));
        if (element.getPort() != null) {
            sb.append(ApiTypes.Api_COLON).append(replacer.getValue(element.getPort()));
        }
        for (ApiSegmentBlock segment : element.getSegmentBlockList()) {
            sb.append(segment.getText());
        }
        return sb.toString();
    }

    /**
     * 获取参数列表
     *
     * @param element
     * @param replacer
     * @return
     */
    @NotNull
    public static List<Pair<String, String>> getParameters(@Nullable ApiQuery element, ApiVariableReplacer replacer) {
        if (element != null) {
            List<Pair<String, String>> result = new ArrayList<>();
            for (ApiQueryParameter parameter : element.getQueryParameterList()) {
                String key = replacer.getValue(parameter.getQueryParameterKey());
                String value = replacer.getValue(parameter.getQueryParameterValue());
                result.add(Pair.createNonNull(key, value));
            }
            return result;
        }
        return Collections.emptyList();
    }
}