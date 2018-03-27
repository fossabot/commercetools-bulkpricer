package com.commercetools.bulkpricer.helpers;

import com.commercetools.bulkpricer.ShareablePriceList;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.CustomObjectDraft;
import io.sphere.sdk.customobjects.commands.CustomObjectDeleteCommand;
import io.sphere.sdk.customobjects.commands.CustomObjectUpsertCommand;
import io.sphere.sdk.customobjects.queries.CustomObjectByKeyGet;
import io.sphere.sdk.customobjects.queries.CustomObjectQuery;
import io.sphere.sdk.queries.QueryExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CtpMetadataStorage {

  private static String containerName = "bulkpricer-pricegroup-status";

  public static CustomObject<ShareablePriceList> storePriceListMetadata(ShareablePriceList newPriceList) {
    CustomObjectUpsertCommand<ShareablePriceList> createCommand = CustomObjectUpsertCommand.of(
      CustomObjectDraft.ofUnversionedUpsert(
        containerName, newPriceList.getGroupKey(), newPriceList, ShareablePriceList.class
      )
    );
    return CtpClient.getClient().executeBlocking(createCommand);
  }

  public static CustomObject<ShareablePriceList> deletePriceListMetadata(ShareablePriceList priceList) {
    CustomObject<ShareablePriceList> currentList = CtpClient.getClient().executeBlocking(
      CustomObjectByKeyGet.of(containerName, priceList.getGroupKey(), ShareablePriceList.class)
    );
    return CtpClient.getClient().executeBlocking(
      CustomObjectDeleteCommand.of(currentList, ShareablePriceList.class)
    );
  }

  public static CustomObject<ShareablePriceList> deletePriceListMetadata(String groupKey) {
    CustomObject<ShareablePriceList> currentList = CtpClient.getClient().executeBlocking(
      CustomObjectByKeyGet.of(containerName, groupKey, ShareablePriceList.class)
    );
    return CtpClient.getClient().executeBlocking(
      CustomObjectDeleteCommand.of(currentList, ShareablePriceList.class)
    );
  }

  public static ShareablePriceList getStoredPriceListMetadata(String groupKey) {
    CustomObject<ShareablePriceList> currentList = CtpClient.getClient().executeBlocking(
      CustomObjectByKeyGet.of(containerName, groupKey, ShareablePriceList.class)
    );
    return currentList.getValue();
  }

  public static List<CustomObject<ShareablePriceList>> getAllStoredListMetadata(){
    try {
      return QueryExecutionUtils.queryAll(CtpClient.getClient(),
        CustomObjectQuery.of(ShareablePriceList.class).byContainer(containerName)).toCompletableFuture().get();
    } catch (InterruptedException e) {
      return new ArrayList<>();
    } catch (ExecutionException e) {
      return new ArrayList<>();
    }
  }

}
