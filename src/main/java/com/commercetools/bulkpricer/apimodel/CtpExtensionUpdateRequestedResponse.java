package com.commercetools.bulkpricer.apimodel;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.commands.UpdateAction;

import java.util.ArrayList;

public class CtpExtensionUpdateRequestedResponse {
  private ArrayList<UpdateAction<Cart>> actions;

  public ArrayList<UpdateAction<Cart>> getActions(){
    return actions;
  }

  public void appendUpdateAction(UpdateAction<Cart> action){
    actions.add(action);
  }

}
