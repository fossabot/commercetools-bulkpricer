package com.commercetools.bulkpricer.com.commercetools.bulkpricer.apiModel;

import java.util.ArrayList;

public class CtpExtensionValidationFailedResponseBody {
  private ArrayList<CtpExtensionError> errors;

  public ArrayList<CtpExtensionError> getErrors() {
    return errors;
  }
  public void appendError(CtpExtensionError error){
    errors.add(error);
  }
}
