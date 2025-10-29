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
package com.google.play.billing.samples.managedcatalogue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.android.billingclient.api.BillingClient.ProductType;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.play.billing.samples.managedcatalogue.billing.BillingServiceClient;
import com.google.play.billing.samples.managedcatalogue.billing.BillingServiceClientListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** This is the main activity class */
public class MainActivity extends AppCompatActivity implements BillingServiceClientListener {

  private static final String TRENDING_MOVIE_PRODUCT_ID = "trending_movie_1";
  private static final String TAG = "BillingServiceClient";

  private BillingServiceClient billingServiceClient;
  private NestedScrollView landingPage;

  private TextView movieTitle, movieDesc, moviePrice;
  private MaterialCardView trendingMovieCard;
  private AtomicBoolean isProductFound = new AtomicBoolean(false);
  private String productName;
  private String productDescription;
  private MaterialButton licenseButton, githubButton, codelabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        landingPage = findViewById(R.id.landing_page);
        trendingMovieCard = findViewById(R.id.trending_movie_card);

        movieTitle = findViewById(R.id.trending_movie_title);
        movieDesc = findViewById(R.id.trending_movie_desc);
        moviePrice = findViewById(R.id.trending_movie_price);

        billingServiceClient = new BillingServiceClient(this, this);
        trendingMovieCard.setOnClickListener(v -> {
            if(isProductFound.get()) {
            Intent intent = new Intent(MainActivity.this, TrendingMovieActivity.class);
            intent.putExtra("productId", TRENDING_MOVIE_PRODUCT_ID);
            intent.putExtra("productName", productName);
            intent.putExtra("productDescription", productDescription);
            startActivity(intent);
            }
        });

        licenseButton = findViewById(R.id.license_button);
        licenseButton.setOnClickListener(
            v -> {
              startActivity(new Intent(this, OssLicensesMenuActivity.class));
            });
        githubButton = findViewById(R.id.github_button);
        githubButton.setOnClickListener(
            v -> {
              Intent intent = new Intent(Intent.ACTION_VIEW);
              intent.setData(Uri.parse(getString(R.string.github_url)));
              startActivity(intent);
            });
        codelabButton = findViewById(R.id.codelabs_button);
        codelabButton.setOnClickListener(
            v -> {
              Intent intent = new Intent(Intent.ACTION_VIEW);
              intent.setData(Uri.parse(getString(R.string.codelab_url)));
              startActivity(intent);
            });

        queryProducts();
  }

  private void queryProducts() {
    List<Product> productList = List.of(
                Product.newBuilder()
                        .setProductId(TRENDING_MOVIE_PRODUCT_ID)
                        .setProductType(ProductType.INAPP)
                        .build()
        );
    billingServiceClient.startBillingConnection(productList);
  }

  @Override
  public void onProductDetailsResponse(List<ProductDetails> productDetailsList) {
    runOnUiThread(
        () -> {
          for (ProductDetails productDetails : productDetailsList) {
            if (TRENDING_MOVIE_PRODUCT_ID.equals(productDetails.getProductId())) {
              isProductFound.set(true);
              productName = productDetails.getName();
              productDescription = productDetails.getDescription();
              productDescription = productDescription.replace("\n", "");
              movieTitle.setText(productName);
              movieDesc.setText(R.string.default_movie_desc);

              List<ProductDetails.OneTimePurchaseOfferDetails> offerDetailsList =
                  productDetails.getOneTimePurchaseOfferDetailsList();
              String priceToDisplay = null;
              for (ProductDetails.OneTimePurchaseOfferDetails offerDetails : offerDetailsList) {
                if (offerDetails.getRentalDetails() != null) {
                  priceToDisplay = offerDetails.getFormattedPrice();
                  break;
                } else if (priceToDisplay == null) {
                  priceToDisplay = offerDetails.getFormattedPrice();
                }
              }
              if (priceToDisplay != null) {
                moviePrice.setText(getString(R.string.default_movie_price, priceToDisplay));
              } else {
                moviePrice.setText(R.string.price_unavailable);
              }
            }
          }
          if (!isProductFound.get()) {
            movieDesc.setText(R.string.movie_unavailable_text);
          }
        });
  }

  @Override
    public void onBillingSetupFailed(BillingResult billingResult) {
    runOnUiThread(
        () -> {
          Toast.makeText(
                  this, "Billing Error: " + billingResult.getDebugMessage(), Toast.LENGTH_LONG)
              .show();
        });
    }

  @Override
  public void onBillingError(String errorMsg) {
    runOnUiThread(
        () -> {
          Toast.makeText(this, "Billing Error: " + errorMsg, Toast.LENGTH_LONG).show();
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (billingServiceClient != null) {
      billingServiceClient.endBillingConnection();
    }
  }
}
