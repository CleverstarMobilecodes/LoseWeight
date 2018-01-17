package com.surfcityapps.am.loseweight.purchase;


import com.surfcityapps.am.loseweight.Constants;

/**
 * 
 * MySku enum contains all In App Purchase products definition that the sample
 * app will use. The product definition includes two properties: "SKU" and
 * "Available Marketplace".
 * 
 */
public enum MySku {

    // The only entitlement product used in this sample app
    UPDATEAPP(Constants.IAP_IDENTIFIER, "US");

    private final String sku;
    private final String availableMarkpetplace;

    /**
     * Returns the MySku object from the specified Sku and marketplace value.
     * 
     * @param sku
     * @param marketplace
     * @return
     */
    public static MySku fromSku(final String sku, final String marketplace) {
        if (UPDATEAPP.getSku().equals(sku) && (marketplace == null || UPDATEAPP.getAvailableMarketplace().equalsIgnoreCase(marketplace))) {
            return UPDATEAPP;
        }
        return null;
    }

    /**
     * Returns the Sku string of the MySku object
     * 
     * @return
     */
    public String getSku() {
        return this.sku;
    }

    /**
     * Returns the Available Marketplace of the MySku object
     * 
     * @return
     */
    public String getAvailableMarketplace() {
        return this.availableMarkpetplace;
    }

    private MySku(final String sku, final String availableMarkpetplace) {
        this.sku = sku;
        this.availableMarkpetplace = availableMarkpetplace;
    }

}
