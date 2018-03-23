package com.commercetools.bulkpricer.com.commercetools.bulkpricer.apiModel;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.models.Reference;

public class CtpExtensionRequestBody {
  public String action;
  public Reference<Cart> resource;
  public CtpExtensionRequestBody(String act, Reference<Cart> ref){
    action = act;
    resource = ref;
  }
}
