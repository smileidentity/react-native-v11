# Release Notes

## Unreleased

### Added
* Added `applyLocalization()` function to enable partner-provided string translations on iOS via
`SmileIDLocalizableStrings` to be applied in the SDK.

## 11.1.7 - January 23, 2026

### Changed
* Bump Android SDK to [v11.1.7](https://github.com/smileidentity/android/releases/tag/v11.1.7)
* Bump iOS SDK to [v11.1.7](https://github.com/smileidentity/ios/releases/tag/v11.1.7)

## 11.1.3 - December 23, 2025

### Changed
* Bump Android SDK to [v11.1.6](https://github.com/smileidentity/android/releases/tag/v11.1.6)
* Bump iOS SDK to [v11.1.5](https://github.com/smileidentity/ios/releases/tag/v11.1.5)

### Added
* Added smile sensitivity parameter for customizable selfie capture thresholds.

## 11.1.2 - November 10, 2025

### Changed
* Bump Android SDK to [v11.1.4](https://github.com/smileidentity/android/releases/tag/v11.1.4)
* Bump iOS SDK to [v11.1.3](https://github.com/smileidentity/ios/releases/tag/v11.1.3)

## 11.1.1 - August 26, 2025

### Changed
* Updated layout handling in the Sample app to prevent screens from rendering behind navigation bars by
  integrating `SafeAreaView`, standardizing white status bar styling across platforms, replacing `View`
  components with `SafeAreaView`, and improving Android window configuration for system UI management.
* Bump iOS to 11.1.1 (https://github.com/smileidentity/ios/releases/tag/v11.1.1)

## 11.1.0 - August 5, 2025

### Added
* Added `autoCaptureTimeout` to allow partners to configure the auto-capture timeout duration.

### Changed
* Updated the `targetSdk` to 36 and updated the AGP version
* Changed `enableAutoCapture` to `AutoCapture` enum to allow partners to change document capture options
* Upgraded Smile ID Android and iOS SDKs to version `v11.1.0`

### Removed
* Removed `AntiFraud` response in `JobStatus` calls
* Removed the default `ConsentInformation`

## 11.0.3 - July 11, 2025

### Changed

* Updated SmileID iOS dependencies to the latest version, which now returns absolute file paths for
  images and documents—bringing consistency with the Android SDK.
* Removed unnecessary methods that manually generated absolute file paths, as this is now handled
  directly by the updated SDK.

## 11.0.2 - July 3, 2025

### Added

* Added option to disable document auto capture in DocV and Enhanced DocV

## 11.0.1 - June 16, 2025

### Changed
* Bump android to 11.0.4 (https://github.com/smileidentity/android/releases/tag/v11.0.4)

## 11.0.0

### Changed
* Metadata collection is now handled internally by native SDKs
* Bump iOS to 11.0.0 (https://github.com/smileidentity/ios/releases/tag/v11.0.0)
* Bump android to 11.0.3 (https://github.com/smileidentity/android/releases/tag/v11.0.3)

## 10.3.3

### Fixed
* Fixed `showAttribution` parameter not being passed to the instruction screen in enhanced selfie capture
* Underlying implementation for `showConfirmationDialog` flag for `SmileIDSmartSelfieCaptureView` on iOS and Android

### Changed
* Restructured consent object that is being sent to the backend API for biometric kyc, enhanced kyc and enhanced document verification
* Bump iOS to 10.5.3 (https://github.com/smileidentity/ios/releases/tag/v10.5.3)
* Bump android to 10.6.3 (https://github.com/smileidentity/android/releases/tag/v10.6.3)

## 10.3.2

### Changed
* Require selfie recapture when retrying failed submission for Enhanced Smart Selfie Capture.
* Bump iOS to 10.5.2 (https://github.com/smileidentity/ios/releases/tag/v10.5.2)

### Fixed
* iOS delegate callback order after submission for Biometric KYC and Document Verification jobs.

## 10.3.1

### Changed
* Bumped iOS to 10.5.1 (https://github.com/smileidentity/ios/releases/tag/v10.5.1)

### Fixed
* Selfie submission error returned in success delegate callback.

## 10.3.0
* Changes the `allow_new_enroll` flag to be a real boolean instead of a string for prepUpload
  requests and multi-part requests. This is a breaking change for stored offline jobs, where the job
  is written using an older sdk version and then submission is attempted using this version.
* Bump android to 10.6.0 (https://github.com/smileidentity/android/releases/tag/v10.6.0)
* Bump iOS to 10.5.0 (https://github.com/smileidentity/ios/releases/tag/v10.5.0)

## 10.2.6
* Added enhanced SmartSelfie™ capture Selfie capture screen component
* Added `skipApiSubmission` to SmartSelfie™ capture which defaults to `false` and will allow Selfie capture without submission to the api
* Make consent information optional on Biometric KYC, Enhanced KYC and Enhanced Document Verification
* Bump android to 10.5.2 (https://github.com/smileidentity/android/releases/tag/v10.5.2)
* Bump iOS to 10.4.2 (https://github.com/smileidentity/ios/releases/tag/v10.4.2)

## 10.2.5
* Added enhanced SmartSelfie™ capture to docV, enhanced docV, and biometric kyc
* Added consent information to BioMetric KYC and Enhanced Document Verification
* Bump android to 10.5.0 (https://github.com/smileidentity/android/releases/tag/v10.5.0)
* Bump iOS to 10.4.0 (https://github.com/smileidentity/ios/releases/tag/v10.4.0)

## 10.2.4
* Fixed issues with rendering capture components and native modals staying after onResult has been called

## 10.2.4-beta.1
* Fixed dialog presentation issues for capture screens by making context a variable instead of accessing it everytime when needed using getContext
* Bump android to 10.4.4-SNAPSHOT to cater for kotling 1.9.x (https://github.com/smileidentity/android/releases/tag/v10.4.4-SNAPSHOT)
* Known bug with Smartselfie Enhanced Enrollment and Authentication not working correctly

## 10.2.3
* Fixed missing `didSubmitBiometricKycJob` flag in BiometricKyc response on android
* Added `apiKey` and `config` missing to allow multiple initialization options see (https://docs.usesmileid.com/integration-options/mobile/getting-started)
* Fix document capture restore optional parameters and showing or hiding instruction and confirmation screen
* Bump android to 10.4.2 (https://github.com/smileidentity/android/releases/tag/v10.4.2)
* Bump iOS to 10.3.4 (https://github.com/smileidentity/ios/releases/tag/v10.3.4)

## 10.2.2
* Fixed setting `kotlinVersion` and `kotlinCompilerExtensionVersion`

## 10.2.1
* Allow skipApiSubmission which will capture Enrollment, Authentication, Doc V and Enhanced DocV without submitting to SmileID and will return captured images file paths
* Bump android to 10.3.7 (https://github.com/smileidentity/android/releases/tag/v10.3.7)
* Bump iOS to 10.2.17 (https://github.com/smileidentity/ios/releases/tag/v10.2.17)

## 10.2.0
* Consistent file paths for all products and capture screens
* Smartselfie enroll removed jobId will now be job_id in the extra partner params
* Smartselfie authentication removed jobId will now be job_id in the extra partner params

## 10.1.12
* Added selfie capture screens
* Added document capture screens
* Bump android to 10.3.1 (https://github.com/smileidentity/android/releases/tag/v10.3.1)
* Bump iOS to 10.2.12 (https://github.com/smileidentity/ios/releases/tag/v10.2.12)

## 10.1.11
* Fix config issues on iOS

## 10.1.10
* Bump ios to 10.2.8 (https://github.com/smileidentity/ios/releases/tag/v10.2.8) In memory zip file handling

## 10.1.9
* Bump ios to 10.2.6 (https://github.com/smileidentity/ios/releases/tag/v10.2.6) All polling methods now return a AsyncThrowingStream<JobStatusResponse<T>, Error> and instead of a timeout, if there is no error it'll return the last valid response and complete the stream.
* Bump android to 10.2.5 (https://github.com/smileidentity/android/releases/tag/v10.2.5)
* Removed `SmileID.setEnvironment()` since the API Keys are no longer shared between environments

## 10.1.7

* Return the correct type exports and class exports
* Sample app updates to include polling
* Fix ios results to make them uniform with android
* Bump android to 10.2.2 (https://github.com/smileidentity/android/releases/tag/v10.2.2)

## 10.1.6

* Bump iOS to 10.2.2 (https://github.com/smileidentity/ios/releases/tag/v10.2.2) which fixes retry crash

## 10.1.5

* Fix setCallbackUrl crash on ios
* Bump android to 10.1.6 (https://github.com/smileidentity/android/releases/tag/v10.1.7)
* Bump iOS to 10.2.1 (https://github.com/smileidentity/ios/releases/tag/v10.2.1)


## 10.1.4

* Support for react native 0.74.x see (https://reactnative.dev/blog/2024/04/22/release-0.74)
* Android minSdkVersion 23 as per react native 0.74.x (https://reactnative.dev/blog/2024/04/22/release-0.74#android-minimum-sdk-bump-android-60)
* Added offline functionality
  * setAllowOfflineMode
  * submitJob
  * getUnsubmittedJobs
  * getSubmittedJobs
  * cleanup
* Added missing setEnvironment and setCallbackUrl interfaces
* Bump android to 10.1.6 (https://github.com/smileidentity/android/releases/tag/v10.1.6)
* Bump iOS to 10.2.0 (https://github.com/smileidentity/ios/releases/tag/v10.2.0)

## 10.1.3

* Fix polling when instantly starting polling after capture result

## 10.1.2

* Return correct types in all networking methods
* Serialize networking methods to json
* Fix authentication request optional params

## 10.1.1

* Exposed JobType Enum which was missing in exports

## 10.1.0

* Introduced polling methods for products
  * SmartSelfie
  * Biometric kyc
  * Document verification
  * Enhanced document verification
* Moved SmartSelfie enrollment and authentication to synchronous endpoints
* Offline responses for the rest of the products
* Updated to react native 0.73.8
* Bump android to 10.1.4 (https://github.com/smileidentity/android/releases/tag/v10.1.4)
* Bump kotlin version to 2.0.0

## 10.0.3
* Bump iOS to 10.0.11 (https://github.com/smileidentity/ios/releases/tag/v10.0.11)

## 10.0.2

* Bump iOS to 10.0.9 (https://github.com/smileidentity/ios/releases/tag/v10.0.9)
* Bump minimum ios version to 13.4 (https://reactnative.dev/blog/2023/12/06/0.73-debugging-improvements-stable-symlinks#other-breaking-changes)
* Update java version to 17 on all instances
* Update react native to 0.73.6
* Update kotlin to version 1.9.23

## 10.0.1

* Bump Android to 10.0.4 (https://github.com/smileidentity/android/releases/tag/v10.0.4)
* Bump iOS to 10.0.8 (https://github.com/smileidentity/ios/releases/tag/v10.0.8)
* Networking for iOS
* Update java version to 17

## 10.0.0

* Remove beta
* iOS: Biometric KYC allow agent mode
* iOS: Document capture captureBothSides fixes
* iOS: Biometric KYC invalid payload fixes
* Android: Document capture crash fixes
* iOS & Android: Consent screen import fixes

## 10.0.0-beta03

* Added networking wrappers
* Allow new enroll flag
* Android DocV and Enhanced DocV crashes
* iOS DocV and enhanced DocV crashes

## 10.0.0-beta02

### Added

* iOS
  * SmartSelfie™  Enrolment
  * SmartSelfie™ Authentication
  * Document Verification
  * Enhanced Document Verification
  * Biometric KYC
  * Consent Screen
* Android
  * Enhanced Document Verification
  * Consent Screen

### Changed

* SmartSelfie™  Enrolment
* SmartSelfie™ Authentication
* Document Verification

## 10.0.0-beta01

* Initial release
* Support for Android only
  * SmartSelfie™  Enrolment
  * SmartSelfie™ Authentication
  * Document Verification
  * Biometric KYC
