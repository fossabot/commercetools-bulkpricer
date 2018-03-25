package com.commercetools.bulkpricer;

import io.vertx.core.buffer.Buffer;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Random;

public class ExampleData {

  public static String getPriceLoadRequestAsString(String fileURL){
    return "{\n" +
      "  \"groupKey\": \"test-key\",\n" +
      "  \"currency\": \"EUR\",\n" +
      "  \"fileURL\": \"" + fileURL + "\"\n" +
      "  }";
  }


  public static String getCtpExtensionRequestBodyAsString(){
    return "{\n" +
      "  \"action\": \"Update\",\n" +
      "  \"resource\": " + getTestCartReferenceAsString() +
      "  }";
  }

  public static String getTestCartReferenceAsString(){
    return "{\n" +
      "  \"typeId\": \"cart\",\n" +
      "  \"id\": \"d3eb6d7c-e7a1-4143-b2e0-ef84db2b507e\",\n" +
      "  \"obj\": " + getTestCartAsJsonString() +
      "  }";
  }

  public static String getTestCartAsJsonString(){
    return "{\n" +
      "  \"type\": \"Cart\",\n" +
      "  \"id\": \"d3eb6d7c-e7a1-4143-b2e0-ef84db2b507e\",\n" +
      "  \"version\": 6,\n" +
      "  \"createdAt\": \"2016-08-30T12:22:52.143Z\",\n" +
      "  \"lastModifiedAt\": \"2016-08-30T12:22:52.451Z\",\n" +
      "  \"lineItems\": [\n" +
      "    {\n" +
      "      \"id\": \"5dac682a-257a-4ada-8062-cdcd756a294a\",\n" +
      "      \"productId\": \"640d248b-870b-44d0-bd3d-40583e5172d8\",\n" +
      "      \"name\": {\n" +
      "        \"en\": \"Sweater Brunello Cucinelli cream\",\n" +
      "        \"de\": \"Pullover Brunello Cucinelli creme\"\n" +
      "      },\n" +
      "      \"productType\": {\n" +
      "        \"typeId\": \"product-type\",\n" +
      "        \"id\": \"d59b3983-cb99-4b80-a4db-090521592bfa\",\n" +
      "        \"version\": 1\n" +
      "      },\n" +
      "      \"productSlug\": {\n" +
      "        \"en\": \"brunello-cucinelli-sweater-m8z878102-cream\",\n" +
      "        \"de\": \"brunello-cucinelli-pullover-m8z878102-creme\"\n" +
      "      },\n" +
      "      \"variant\": {\n" +
      "        \"id\": 1,\n" +
      "        \"sku\": \"M0E20000000DQTH\",\n" +
      "        \"prices\": [\n" +
      "          {\n" +
      "            \"value\": {\n" +
      "              \"type\": \"centPrecision\",\n" +
      "              \"currencyCode\": \"EUR\",\n" +
      "              \"centAmount\": 94375,\n" +
      "              \"fractionDigits\": 2\n" +
      "            },\n" +
      "            \"id\": \"555c18ed-9bde-4302-a829-f0ab9abd0045\"\n" +
      "          }\n" +
      "        ],\n" +
      "        \"images\": [\n" +
      "          {\n" +
      "            \"url\": \"https://s3-eu-west-1.amazonaws.com/commercetools-maximilian/products/073475_1_large.jpg\",\n" +
      "            \"dimensions\": {\n" +
      "              \"w\": 0,\n" +
      "              \"h\": 0\n" +
      "            }\n" +
      "          }\n" +
      "        ],\n" +
      "        \"attributes\": [],\n" +
      "        \"assets\": [],\n" +
      "        \"availability\": {\n" +
      "          \"channels\": {}\n" +
      "        }\n" +
      "      },\n" +
      "      \"price\": {\n" +
      "        \"value\": {\n" +
      "          \"type\": \"centPrecision\",\n" +
      "          \"currencyCode\": \"EUR\",\n" +
      "          \"centAmount\": 83050,\n" +
      "          \"fractionDigits\": 2\n" +
      "        },\n" +
      "        \"id\": \"6454df32-f4a5-4bef-9ca6-dfc8ec167d38\",\n" +
      "        \"country\": \"DE\",\n" +
      "        \"channel\": {\n" +
      "          \"typeId\": \"channel\",\n" +
      "          \"id\": \"eeff7b26-f179-4739-b88d-6b8814da4e5a\"\n" +
      "        }\n" +
      "      },\n" +
      "      \"quantity\": 1,\n" +
      "      \"discountedPricePerQuantity\": [],\n" +
      "      \"supplyChannel\": {\n" +
      "        \"typeId\": \"channel\",\n" +
      "        \"id\": \"eeff7b26-f179-4739-b88d-6b8814da4e5a\"\n" +
      "      },\n" +
      "      \"distributionChannel\": {\n" +
      "        \"typeId\": \"channel\",\n" +
      "        \"id\": \"eeff7b26-f179-4739-b88d-6b8814da4e5a\"\n" +
      "      },\n" +
      "      \"taxRate\": {\n" +
      "        \"name\": \"standard\",\n" +
      "        \"amount\": 0.19,\n" +
      "        \"includedInPrice\": true,\n" +
      "        \"country\": \"DE\",\n" +
      "        \"id\": \"gpHy-nob\",\n" +
      "        \"subRates\": []\n" +
      "      },\n" +
      "      \"state\": [\n" +
      "        {\n" +
      "          \"quantity\": 1,\n" +
      "          \"state\": {\n" +
      "            \"typeId\": \"state\",\n" +
      "            \"id\": \"db2b1109-27bd-4d67-a18a-526eb5e0062d\"\n" +
      "          }\n" +
      "        }\n" +
      "      ],\n" +
      "      \"priceMode\": \"Platform\",\n" +
      "      \"totalPrice\": {\n" +
      "        \"type\": \"centPrecision\",\n" +
      "        \"currencyCode\": \"EUR\",\n" +
      "        \"centAmount\": 83050,\n" +
      "        \"fractionDigits\": 2\n" +
      "      },\n" +
      "      \"taxedPrice\": {\n" +
      "        \"totalNet\": {\n" +
      "          \"type\": \"centPrecision\",\n" +
      "          \"currencyCode\": \"EUR\",\n" +
      "          \"centAmount\": 69790,\n" +
      "          \"fractionDigits\": 2\n" +
      "        },\n" +
      "        \"totalGross\": {\n" +
      "          \"type\": \"centPrecision\",\n" +
      "          \"currencyCode\": \"EUR\",\n" +
      "          \"centAmount\": 83050,\n" +
      "          \"fractionDigits\": 2\n" +
      "        }\n" +
      "      },\n" +
      "      \"lineItemMode\": \"Standard\"\n" +
      "    }\n" +
      "  ],\n" +
      "  \"cartState\": \"Ordered\",\n" +
      "  \"totalPrice\": {\n" +
      "    \"type\": \"centPrecision\",\n" +
      "    \"currencyCode\": \"EUR\",\n" +
      "    \"centAmount\": 83050,\n" +
      "    \"fractionDigits\": 2\n" +
      "  },\n" +
      "  \"taxedPrice\": {\n" +
      "    \"totalNet\": {\n" +
      "      \"type\": \"centPrecision\",\n" +
      "      \"currencyCode\": \"EUR\",\n" +
      "      \"centAmount\": 69790,\n" +
      "      \"fractionDigits\": 2\n" +
      "    },\n" +
      "    \"totalGross\": {\n" +
      "      \"type\": \"centPrecision\",\n" +
      "      \"currencyCode\": \"EUR\",\n" +
      "      \"centAmount\": 83050,\n" +
      "      \"fractionDigits\": 2\n" +
      "    },\n" +
      "    \"taxPortions\": [\n" +
      "      {\n" +
      "        \"rate\": 0.19,\n" +
      "        \"amount\": {\n" +
      "          \"type\": \"centPrecision\",\n" +
      "          \"currencyCode\": \"EUR\",\n" +
      "          \"centAmount\": 13260,\n" +
      "          \"fractionDigits\": 2\n" +
      "        },\n" +
      "        \"name\": \"standard\"\n" +
      "      }\n" +
      "    ]\n" +
      "  },\n" +
      "  \"country\": \"DE\",\n" +
      "  \"customLineItems\": [],\n" +
      "  \"discountCodes\": [],\n" +
      "  \"custom\": {\n" +
      "    \"type\": {\n" +
      "      \"typeId\": \"type\",\n" +
      "      \"id\": \"8add813a-fcb7-4460-8647-1658a8012678\"\n" +
      "    },\n" +
      "    \"fields\": {\n" +
      "      \"isReservation\": true\n" +
      "    }\n" +
      "  },\n" +
      "  \"inventoryMode\": \"None\",\n" +
      "  \"taxMode\": \"Platform\",\n" +
      "  \"taxRoundingMode\": \"HalfEven\",\n" +
      "  \"taxCalculationMode\": \"LineItemLevel\",\n" +
      "  \"refusedGifts\": [],\n" +
      "  \"origin\": \"Customer\",\n" +
      "  \"shippingAddress\": {\n" +
      "    \"streetName\": \"Dachauer Str.\",\n" +
      "    \"streetNumber\": \"128\",\n" +
      "    \"postalCode\": \"80637\",\n" +
      "    \"city\": \"Munich\",\n" +
      "    \"country\": \"DE\"\n" +
      "  },\n" +
      "  \"itemShippingAddresses\": []\n" +
      "}\n";
  }

  public static Buffer getRandomPriceLines(String amount){
    try {
      int a = NumberFormat.getIntegerInstance().parse(amount).intValue();
      return getRandomPriceLines(a);
    } catch (ParseException e) {
      return getRandomPriceLines(100);
    }
  }

  public static Buffer getRandomPriceLines(int amount){
    Random rnd = new Random();
    Buffer buf = Buffer.buffer(amount * 20);
    for (int i = 0; i < amount; i++){
      buf.appendString(new Integer(rnd.nextInt()).toString() + "," + new Float(rnd.nextInt() / 100.0).toString() + "\n");
    }
    return buf;
  }
}
