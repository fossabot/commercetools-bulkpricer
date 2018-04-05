package com.commercetools.bulkpricer.messages;

/*
 * A generic bus message that carries HTTP status code semantics and custom status message text
 */
public class HttpLikeStatusMessage extends AbstractBusMessage {
  public Integer statusCode;
  public String statusMessage;

  public HttpLikeStatusMessage(Integer statusCode, String statusMessage) {
    this.statusCode = statusCode;
    this.statusMessage = statusMessage;
  }
}
