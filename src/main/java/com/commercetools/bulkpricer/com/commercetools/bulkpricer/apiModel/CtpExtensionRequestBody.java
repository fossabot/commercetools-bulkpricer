package com.commercetools.bulkpricer.com.commercetools.bulkpricer.apiModel;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.models.Reference;

public class CtpExtensionRequestBody {
  private String action;
  private Reference<Cart> resource;

  public String getAction() {
    return action;
  }

  public Reference<Cart> getResource() {
    return resource;
  }

  public CtpExtensionRequestBody(String actionParam, Reference<Cart> cartReference){
    action = actionParam;
    resource = cartReference;
  }
}
