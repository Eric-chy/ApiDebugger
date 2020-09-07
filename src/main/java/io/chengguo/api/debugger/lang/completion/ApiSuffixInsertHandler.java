package io.chengguo.api.debugger.lang.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import io.chengguo.api.debugger.lang.ApiPsiUtils;
import org.jetbrains.annotations.NotNull;

public class ApiSuffixInsertHandler implements InsertHandler<LookupElement> {
    public static final ApiSuffixInsertHandler VARIABLE_OPTION = new ApiSuffixInsertHandler("}}");
    public static final ApiSuffixInsertHandler HEADER_OPTION = new ApiSuffixInsertHandler("=");
    public static final ApiSuffixInsertHandler SCHEME = new ApiSuffixInsertHandler("://");
    public static final ApiSuffixInsertHandler FIELD_SEPARATOR = new ApiSuffixInsertHandler(": ");
    private final String mSuffix;
    private final String mShortSuffix;

    public ApiSuffixInsertHandler(@NotNull String suffix) {
        this.mSuffix = suffix;
        this.mShortSuffix = suffix.trim();
    }

    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Project project = context.getProject();
        if (project != null) {
            Editor editor = context.getEditor();
            Document document = editor.getDocument();
            // 跳过空格
            int offset = ApiPsiUtils.skipWhitespacesForward(editor.getCaretModel().getOffset(), document.getCharsSequence());
            if (document.getTextLength() == offset || !this.isEqualsToSuffix(document, offset)) {
                EditorModificationUtil.insertStringAtCaret(editor, this.mSuffix);
                PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());
            }
            // 移动光标到后缀之后
            editor.getCaretModel().moveToOffset(offset + this.mSuffix.length());
        }
    }

    private boolean isEqualsToSuffix(@NotNull Document document, int offset) {
        int endOffset = offset + mShortSuffix.length() - 1;
        return document.getTextLength() > endOffset && StringUtil.equals(mShortSuffix, document.getCharsSequence().subSequence(offset, endOffset + 1).toString());
    }

}
