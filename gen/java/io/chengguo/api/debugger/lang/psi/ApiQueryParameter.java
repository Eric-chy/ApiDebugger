/*
 * Copyright 2010-present ApiDebugger
 */
package io.chengguo.api.debugger.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ApiQueryParameter extends ApiElement {

  @NotNull
  ApiQueryParameterKey getQueryParameterKey();

  @Nullable
  ApiQueryParameterValue getQueryParameterValue();

}