package com.commercetools.bulkpricer.apimodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.models.Reference;

/*
Implements this request:
https://docs.commercetools.com/http-api-projects-api-extensions.html#input
 */

public class CtpExtensionRequestBody {
  private String action;
  private Reference<Cart> resource;

  public String getAction() {
    return action;
  }

  public Reference<Cart> getResource() {
    return resource;
  }

  @JsonCreator
  CtpExtensionRequestBody(String action, Reference<Cart> resource){
    this.action = action;
    this.resource = resource;
  }
}
