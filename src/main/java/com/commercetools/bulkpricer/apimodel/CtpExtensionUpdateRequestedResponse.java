package com.commercetools.bulkpricer.apimodel;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.commands.UpdateAction;

import java.util.ArrayList;

public class CtpExtensionUpdateRequestedResponse {
  private ArrayList<UpdateAction<Cart>> actions;

  public ArrayList<UpdateAction<Cart>> getActions(){
    return actions;
  }

  public boolean appendUpdateAction(UpdateAction<Cart> action){
    if (actions.size() == 50){
      return false;
    }
    actions.add(action);
    return true;
  }

}
