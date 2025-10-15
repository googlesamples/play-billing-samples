# Play Billing Samples
------------

## Introduction
------------

This sample demonstrates how to integrate the Google Play Billing Library into an Android app. It provides a simple example of how to set up and use the library to handle in-app purchases. This sample aims to provide clear and well-documented code to assist app developers in seamlessly and efficiently integrating the Play Billing Library into their existing applications.

The documentation for the Play Billing Library is available on the [Android Developer website](https://developer.android.com/google/play/billing).

## Prerequisites
------------

Before you begin, you must complete the following steps:

1.  **Set up your Google Play Console:** You need to have a Google Play Console account and have set up your app's one time products. For more information, see the [Play Console documentation](https://developer.android.com/distribute/console).
2.  **Install Android Studio:** This project uses the Gradle build system and is intended to be opened in Android Studio (make sure you are using the latest stable version [here](https://developer.android.com/studio)).

## Getting started
---------------
1.  **Clone the repository:**
    ```
    git clone https://github.com/your-repository/your-project.git
    ```
2.  **Open the project in Android Studio.**
3.  **Update the package name:** In your `app/build.gradle` and your `AndroidManifest.xml` file, change the `applicationId` and `package` attribute respectively to your app's package name as configured in your Console account.
4.  **Update the product IDs:** In your code, find the list of product IDs and replace them with the product IDs you created in the Google Play Console.

## Running the App
----------------

Once you've completed the "Getting Started" steps, you can run the app on an Android device or emulator. The app will demonstrate the basic functionality of the Play Billing Library, including querying for products and launching the billing flow.

## For Java developers
-------------------

The sample app code resides in the `purchases/` and `managedcatalogue/` directories. Key Google Play Billing integration logic is located in the `billing/` directory, specifically within the `BillingServiceClient.java` class.

## Libraries used
--------------

* [Play Billing Library][0] - A library that enables you to sell digital products and content in your Android app,
whether you want to monetize through one-time purchases or offer subscriptions to your services.

[0]: https://developer.android.com/distribute/play-billing

## Support
-------

Please report issues with this sample in this project's issues page:
https://github.com/googlesamples/play-billing-samples/issues


## License
-------

```
Copyright 2025 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
```
