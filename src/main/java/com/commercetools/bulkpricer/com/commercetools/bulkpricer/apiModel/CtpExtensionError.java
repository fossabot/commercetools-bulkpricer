package com.commercetools.bulkpricer.com.commercetools.bulkpricer.apiModel;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.errors.SphereError;
import io.vertx.core.json.JsonObject;

import java.util.Locale;

public class CtpExtensionError {
  public String code;
  public String message;
  public LocalizedString localizedMessage;
  public JsonObject extensionExtraInfo;

  public CtpExtensionError(SphereError error){
    code = error.getCode();
    message = error.getMessage();
  }

  public CtpExtensionError(SphereError error, Locale locale){
    code = error.getCode();
    message = error.getMessage();
    localizedMessage = LocalizedString.of(locale, error.getMessage());
  }

  public CtpExtensionError(String c, String m, LocalizedString l){
    code = c;
    message = m;
    localizedMessage = l;
  }

  public void setExtensionExtraInfo(JsonObject extensionExtraInfo) {
    this.extensionExtraInfo = extensionExtraInfo;
  }
}
