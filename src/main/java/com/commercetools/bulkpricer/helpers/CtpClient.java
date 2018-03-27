package com.commercetools.bulkpricer.helpers;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClientFactory;

import java.util.concurrent.TimeUnit;

public class CtpClient {
  private static BlockingSphereClient client;

  public static BlockingSphereClient getClient(){
    if(client == null){
      client = BlockingSphereClient.of(SphereClientFactory.of().createClient(
        // TODO manage secrets outside code.
        "nk-playground-1",
        "NecJXvDX_cZaNtC59_ZspfH2",
        "8BO4K8WVtHO_P_WYSgvOCYqmBDUQgm_U"), 20, TimeUnit.SECONDS);
      return client;
    }else{
      return client;
    }
  }
}
