/* Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.play.billing.samples.onetimepurchases;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.ProductType;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.common.collect.ImmutableList;
import com.google.play.billing.samples.onetimepurchases.billing.BillingServiceClient;
import com.google.play.billing.samples.onetimepurchases.billing.BillingServiceClientListener;
import java.util.Map;
import java.util.Objects;

/** This is the main activity class */
public class MainActivity extends AppCompatActivity implements BillingServiceClientListener {

  private BillingServiceClient billingServiceClient;
  private static final String ONE_TIME_PRODUCT_01 = "one_time_product_01";
  private static final String CONSUMABLE_PRODUCT_01 = "consumable_product_01";
  private static final String CONSUMABLE_PRODUCT_02 = "consumable_product_02";
  private static final String CONSUMABLE_PRODUCT_03 = "consumable_product_03";

  private static final ImmutableList<Product> PRODUCT_LIST =
      ImmutableList.of(
          Product.newBuilder()
              .setProductId(ONE_TIME_PRODUCT_01)
              .setProductType(ProductType.INAPP)
              .build(),
          Product.newBuilder()
              .setProductId(CONSUMABLE_PRODUCT_01)
              .setProductType(ProductType.INAPP)
              .build(),
          Product.newBuilder()
              .setProductId(CONSUMABLE_PRODUCT_02)
              .setProductType(ProductType.INAPP)
              .build(),
          Product.newBuilder()
              .setProductId(CONSUMABLE_PRODUCT_03)
              .setProductType(ProductType.INAPP)
              .build());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final View bannerText = findViewById(R.id.product_list);
    final MotionLayout motionLayout = (MotionLayout) bannerText.getParent();
    ViewCompat.setOnApplyWindowInsetsListener(
            motionLayout,
            (v, windowInsets) -> {
              Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
              int statusBarHeight = insets.top;

              ConstraintSet endSet = motionLayout.getConstraintSet(R.id.end);
              endSet.setMargin(R.id.product_list, ConstraintSet.TOP, statusBarHeight);

              motionLayout.updateState(R.id.end, endSet);
              return WindowInsetsCompat.CONSUMED;
            });

    MaterialButton githubButton = findViewById(R.id.github_button);
    githubButton.setOnClickListener(v -> {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.github_url)));
        startActivity(intent);
    });

    MaterialButton codelabButton = findViewById(R.id.codelab_button);
    codelabButton.setOnClickListener(v -> {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.codelab_url)));
        startActivity(intent);
    });

    MaterialButton licensesButton = findViewById(R.id.licenses_button);
    licensesButton.setOnClickListener(v -> {
        startActivity(new Intent(this, OssLicensesMenuActivity.class));
    });

    // Setup Billing Client
    billingServiceClient = new BillingServiceClient(this, this);
    billingServiceClient.startBillingConnection(PRODUCT_LIST);

  }

  public void showBillingResponseDialog(int responseCode, BillingResult billingResult) {
    final String dialogTitle =
        responseCode == BillingResponseCode.OK ? "Purchase Successful" : "Purchase Failed";
    final String dialogMessage = billingResult.toString();

    // Run on UI thread in case this is called from a background thread
    runOnUiThread(
        () -> {
          MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
          dialogBuilder
              .setTitle(dialogTitle)
              .setMessage(dialogMessage)
              .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
              .show();
        });
    }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    // End the billing connection
    billingServiceClient.endBillingConnection();
  }

    private void updateProductCardUI(ProductDetails productDetails) {

        String productId = productDetails.getProductId();

        String cardIdName = "product_" + productId;
        int cardResId = getResources().getIdentifier(cardIdName, "id", getPackageName());

        // Find views by their resource IDs
        View cardView = findViewById(cardResId);
        TextView titleView = cardView.findViewById(R.id.product_title);
        TextView descView = cardView.findViewById(R.id.product_description);
        MaterialButton buyButton = cardView.findViewById(R.id.buy_button);
        ShapeableImageView productImageView = cardView.findViewById(R.id.product_image);

        // Update views with product details
        titleView.setText(productDetails.getName());
        descView.setText(productDetails.getDescription());

        productImageView.setImageResource(getDrawableProductImageForProductId(productId));

        String formattedPrice =
                Objects.requireNonNull(productDetails.getOneTimePurchaseOfferDetails()).getFormattedPrice();
        buyButton.setText(formattedPrice);
        buyButton.setOnClickListener(v -> billingServiceClient.launchBillingFlow(productId));
    }

  @Override
  public void onBillingResponse(int responseCode, BillingResult billingResult) {
      showBillingResponseDialog(responseCode, billingResult);
  }

  @Override
  public void onProductDetailsFetched(Map<String, ProductDetails> productDetailsMap) {
      runOnUiThread(
              () -> {
                  for (ProductDetails productDetails : productDetailsMap.values()) {
                      updateProductCardUI(productDetails);
                  }
              });
  }

  private int getDrawableProductImageForProductId(String productId) {
      return switch (productId) {
        case ONE_TIME_PRODUCT_01 -> R.drawable.one_time_product_01;
        case CONSUMABLE_PRODUCT_01 -> R.drawable.consumable_product_01;
        case CONSUMABLE_PRODUCT_02 -> R.drawable.consumable_product_02;
        case CONSUMABLE_PRODUCT_03 -> R.drawable.consumable_product_03;
        default -> 0;
      };
  }
}
