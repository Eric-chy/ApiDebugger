/*
 * Copyright 2010-present ApiDebugger
 */
package io.chengguo.api.debugger.lang.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class ApiVisitor extends PsiElementVisitor {

  public void visitApiBlock(@NotNull ApiApiBlock o) {
    visitBlockElement(o);
  }

  public void visitDescription(@NotNull ApiDescription o) {
    visitKeyValueElement(o);
  }

  public void visitFilePath(@NotNull ApiFilePath o) {
    visitElement(o);
  }

  public void visitHeaderField(@NotNull ApiHeaderField o) {
    visitKeyValueElement(o);
    // visitHeaderFieldElement(o);
  }

  public void visitHeaderFieldValueItem(@NotNull ApiHeaderFieldValueItem o) {
    visitElement(o);
  }

  public void visitHeaderKey(@NotNull ApiHeaderKey o) {
    visitElement(o);
  }

  public void visitHeaderValue(@NotNull ApiHeaderValue o) {
    visitElement(o);
  }

  public void visitHost(@NotNull ApiHost o) {
    visitElement(o);
  }

  public void visitInputFile(@NotNull ApiInputFile o) {
    visitRequestMessageElement(o);
  }

  public void visitMessageBody(@NotNull ApiMessageBody o) {
    visitRequestMessageElement(o);
  }

  public void visitMethod(@NotNull ApiMethod o) {
    visitElement(o);
  }

  public void visitMultipartField(@NotNull ApiMultipartField o) {
    visitMultipartFieldElement(o);
  }

  public void visitMultipartMessage(@NotNull ApiMultipartMessage o) {
    visitBodyMessageElement(o);
  }

  public void visitPort(@NotNull ApiPort o) {
    visitElement(o);
  }

  public void visitQuery(@NotNull ApiQuery o) {
    visitElement(o);
  }

  public void visitQueryParameter(@NotNull ApiQueryParameter o) {
    visitElement(o);
  }

  public void visitQueryParameterKey(@NotNull ApiQueryParameterKey o) {
    visitElement(o);
  }

  public void visitQueryParameterValue(@NotNull ApiQueryParameterValue o) {
    visitElement(o);
  }

  public void visitRequest(@NotNull ApiRequest o) {
    visitRequestElement(o);
  }

  public void visitRequestLine(@NotNull ApiRequestLine o) {
    visitElement(o);
  }

  public void visitRequestMessageGroup(@NotNull ApiRequestMessageGroup o) {
    visitRequestMessageGroupElement(o);
    // visitBodyMessageElement(o);
  }

  public void visitRequestTarget(@NotNull ApiRequestTarget o) {
    visitRequestTargetElement(o);
  }

  public void visitScheme(@NotNull ApiScheme o) {
    visitElement(o);
  }

  public void visitSegmentBlock(@NotNull ApiSegmentBlock o) {
    visitElement(o);
  }

  public void visitVariable(@NotNull ApiVariable o) {
    visitNamedElement(o);
  }

  public void visitBlockElement(@NotNull ApiBlockElement o) {
    visitElement(o);
  }

  public void visitBodyMessageElement(@NotNull ApiBodyMessageElement o) {
    visitElement(o);
  }

  public void visitKeyValueElement(@NotNull ApiKeyValueElement o) {
    visitElement(o);
  }

  public void visitMultipartFieldElement(@NotNull ApiMultipartFieldElement o) {
    visitElement(o);
  }

  public void visitNamedElement(@NotNull ApiNamedElement o) {
    visitElement(o);
  }

  public void visitRequestElement(@NotNull ApiRequestElement o) {
    visitElement(o);
  }

  public void visitRequestMessageElement(@NotNull ApiRequestMessageElement o) {
    visitElement(o);
  }

  public void visitRequestMessageGroupElement(@NotNull ApiRequestMessageGroupElement o) {
    visitElement(o);
  }

  public void visitRequestTargetElement(@NotNull ApiRequestTargetElement o) {
    visitElement(o);
  }

  public void visitElement(@NotNull ApiElement o) {
    super.visitElement(o);
  }

}