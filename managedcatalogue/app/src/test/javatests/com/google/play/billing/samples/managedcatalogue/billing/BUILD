load("//tools/build_defs/android:rules.bzl", "android_local_test")

package(
    default_applicable_licenses = ["//third_party/googlesamples/play_billing_samples:license"],
    default_visibility = ["//visibility:private"],
)

android_local_test(
    name = "BillingServiceClientTest",
    srcs = ["BillingServiceClientTest.java"],
    manifest_values = {
        "minSdkVersion": "24",
        "targetSdkVersion": "36",
        "theme": "@style/Theme.AppCompat",
    },
    deps = [
        "//java/com/google/android/libraries/play/billing/public/billing",
        "//third_party/android/androidx_test/ext/junit",
        "//third_party/googlesamples/play_billing_samples/managedcatalogue:managedcatalogue_lib",
        "//third_party/googlesamples/play_billing_samples/managedcatalogue:manifest",
        "//third_party/java/android_libs/guava_jdk5:collect",
        "//third_party/java/androidx/appcompat",
        "//third_party/java/mockito:mockito-android",
    ],
)
