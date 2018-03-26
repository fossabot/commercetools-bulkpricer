package com.commercetools.bulkpricer.apimodel;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.commands.UpdateAction;

import java.util.ArrayList;

/*
Implements this model:
https://docs.commercetools.com/http-api-projects-api-extensions.html#updates-requested
*/
public class CtpExtensionUpdateRequestedResponse {
  private ArrayList<UpdateAction<Cart>> actions = new ArrayList<>();

  public ArrayList<UpdateAction<Cart>> getActions(){
    return actions;
  }

  public void appendUpdateAction(UpdateAction<Cart> action){
    actions.add(action);
  }

}
