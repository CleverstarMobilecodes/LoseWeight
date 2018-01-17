package com.surfcityapps.am.loseweight.purchase;


import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;
import com.surfcityapps.am.loseweight.UnlockActivity;

public class SamplePurchasingListener implements PurchasingListener {

    private static final String TAG = "SampleIAPEntitlementsApp";
    private final UnlockActivity mContext;
    private String currentUserId =  null;
    private String currentMarketplace =  null;

    public SamplePurchasingListener(final UnlockActivity context) {
        this.mContext = context;
    }

    @Override
    public void onUserDataResponse(final UserDataResponse response) {
        final UserDataResponse.RequestStatus status = response.getRequestStatus();
        switch (status) {
        case SUCCESSFUL:
            //iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
            currentUserId = response.getUserData().getUserId();
            currentMarketplace = response.getUserData().getMarketplace();
            break;

        case FAILED:
        case NOT_SUPPORTED:
            //iapManager.setAmazonUserId(null, null);
            break;
        }
    }

    @Override
    public void onProductDataResponse(final ProductDataResponse response) {
        final ProductDataResponse.RequestStatus status = response.getRequestStatus();

        switch (status) {
        case SUCCESSFUL:
            final Set<String> unavailableSkus = response.getUnavailableSkus();
            //iapManager.enablePurchaseForSkus(response.getProductData());
            //iapManager.disablePurchaseForSkus(response.getUnavailableSkus());
            //iapManager.refreshLevel2Availability();

            break;
        case FAILED:
        case NOT_SUPPORTED:
            //iapManager.disableAllPurchases();
            break;
        }
    }

    @Override
    public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
        final PurchaseUpdatesResponse.RequestStatus status = response.getRequestStatus();
        switch (status) {
        case SUCCESSFUL:
            //iapManager.setAmazonUserId(response.getUserData().getUserId(), response.getUserData().getMarketplace());
            for (final Receipt receipt : response.getReceipts()) {
                //iapManager.handleReceipt(response.getRequestId().toString(), receipt, response.getUserData());
                if(receipt.getSku().equals(MySku.UPDATEAPP.getSku())){
                    mContext.RestoreAmazonSuccess();
                    return;
                }
            }
            //if (response.hasMore()) {
            //    PurchasingService.getPurchaseUpdates(false);
            //}
            break;
        case FAILED:
        case NOT_SUPPORTED:
            //iapManager.disableAllPurchases();
            break;
        }
    }

    @Override
    public void onPurchaseResponse(final PurchaseResponse response) {
        final String requestId = response.getRequestId().toString();
        final String userId = response.getUserData().getUserId();
        final PurchaseResponse.RequestStatus status = response.getRequestStatus();
        switch (status) {
        case SUCCESSFUL:
            if(response.getReceipt().getSku().equals(MySku.UPDATEAPP.getSku())) {
                final Receipt receipt = response.getReceipt();
                mContext.handleEntitlementPurchase(receipt, response.getUserData());
                return;
            }
            break;
        case ALREADY_PURCHASED:
            break;
        case INVALID_SKU:
            final Set<String> unavailableSkus = new HashSet<String>();
            unavailableSkus.add(response.getReceipt().getSku());
            //iapManager.disablePurchaseForSkus(unavailableSkus);
            break;
        case FAILED:
        case NOT_SUPPORTED:
            //iapManager.purchaseFailed(response.getReceipt().getSku());
            break;
        }
    }


}
