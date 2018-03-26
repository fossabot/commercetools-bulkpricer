package com.commercetools.bulkpricer.apimodel;

import java.util.ArrayList;

/*
Implements this model:
https://docs.commercetools.com/http-api-projects-api-extensions.html#validation-failed
 */
public class CtpExtensionValidationFailedResponseBody {
  private ArrayList<CtpExtensionError> errors;

  public ArrayList<CtpExtensionError> getErrors() {
    return errors;
  }
  public void appendError(CtpExtensionError error){
    errors.add(error);
  }
}
