import { NativeModules, Platform } from 'react-native';
import SmileIDSmartSelfieEnrollmentView from './SmileIDSmartSelfieEnrollmentView';
import SmileIDSmartSelfieAuthenticationView from './SmileIDSmartSelfieAuthenticationView';
import SmileIDSmartSelfieEnrollmentEnhancedView from './SmileIDSmartSelfieEnrollmentEnhancedView';
import SmileIDSmartSelfieAuthenticationEnhancedView from './SmileIDSmartSelfieAuthenticationEnhancedView';
import SmileIDDocumentVerificationView from './SmileIDDocumentVerificationView';
import SmileIDBiometricKYCView from './SmileIDBiometricKYCView';
import SmileIDEnhancedDocumentVerificationView from './SmileIDEnhancedDocumentVerificationView';
import SmileIDSmartSelfieCaptureView from './SmileIDSmartSelfieCaptureView';
import SmileIDDocumentCaptureView from './SmileIDDocumentCaptureView';
import SmileIDConsentView from './SmileIDConsentView';
import type {
  BiometricKYCRequest,
  ConsentRequest,
  DocumentVerificationRequest,
  EnhancedDocumentVerificationRequest,
  SmartSelfieAuthenticationEnhancedRequest,
  SmartSelfieAuthenticationRequest,
  SmartSelfieEnrollmentEnhancedRequest,
  SmartSelfieEnrollmentRequest,
  SmileIDViewProps,
} from './types';
import {
  AuthenticationRequest,
  AuthenticationResponse,
  BiometricKycJobStatusResponse,
  Config,
  ConsentInformation,
  DocumentVerificationJobStatusResponse,
  EnhancedDocumentVerificationJobStatusResponse,
  EnhancedKycAsyncResponse,
  EnhancedKycRequest,
  EnhancedKycResponse,
  IdInfo,
  JobStatusRequest,
  JobType,
  PrepUploadRequest,
  PrepUploadResponse,
  ProductsConfigRequest,
  ProductsConfigResponse,
  ServicesResponse,
  SmartSelfieJobStatusResponse,
  UploadRequest,
  ValidDocumentsResponse,
} from './types';
import type { Spec } from './NativeSmileId';

const LINKING_ERROR =
  `The package 'react-native-smile-id' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const SmileIdModule = isTurboModuleEnabled
  ? require('./NativeSmileId').default
  : NativeModules.RNSmileID;

const _SmileID: Spec = SmileIdModule
  ? SmileIdModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const SmileID = {
  /**
   * Initialize SmileID SDK with configuration
   * @param useSandBox - Configuration object for the SDK
   * @param config - Configuration object for the SDK
   * @param apiKey - api key specific to the partner and also environment
   * @param enableCrashReporting - Whether to enable crash reporting
   */
  initialize: (
    useSandBox: boolean = false,
    enableCrashReporting: boolean = false,
    config?: Config,
    apiKey?: string
  ) => _SmileID.initialize(useSandBox, enableCrashReporting, config, apiKey),

  /**
   * Sets allow offline mode which enables
   * offline mode functionality to capture jobs offline and submit later
   */
  setAllowOfflineMode: (allowOfflineMode: boolean = false) =>
    _SmileID.setAllowOfflineMode(allowOfflineMode),

  /**
   * Submits an already captured job id
   */
  submitJob: (jobId: string) => _SmileID.submitJob(jobId),

  /**
   * Gets all job ids from the unsubmitted directory
   */
  getUnsubmittedJobs: () => _SmileID.getUnsubmittedJobs(),

  /**
   * Gets all job ids from the unsubmitted directory
   */
  getSubmittedJobs: () => _SmileID.getSubmittedJobs(),

  /**
   * Cleans up a job id from the submitted or unsubmitted directory
   */
  cleanup: (jobId: string) => _SmileID.cleanup(jobId),
  /**
   * NB: Only available on Android
   * Disable crash reporting
   */
  disableCrashReporting: () =>
    Platform.OS === 'android' ? _SmileID.disableCrashReporting() : () => {},

  /**
   * Apply localization strings from the host app's bundle.
   * On iOS, this makes the SDK use the app's Localizable.strings (e.g. sw.lproj/Localizable.strings)
   * instead of the default English strings bundled with the SDK.
   * On Android, this is a no-op since Android handles localization automatically via string resources.
   * Call this after initialize() if your app provides translated strings.
   */
  applyLocalization: () => _SmileID.applyLocalization(),

  authenticate: (request: AuthenticationRequest) =>
    _SmileID.authenticate(request),

  /**
   * Prepare upload process
   */
  prepUpload: (request: PrepUploadRequest) => _SmileID.prepUpload(request),

  /**
   * Perform the upload operation
   */
  upload: (url: string, request: UploadRequest) =>
    _SmileID.upload(url, request),

  /**
   * Perform Enhanced KYC
   */
  doEnhancedKyc: (request: EnhancedKycRequest) =>
    _SmileID.doEnhancedKyc(request),

  /**
   * Perform Enhanced KYC asynchronously
   */
  doEnhancedKycAsync: (request: EnhancedKycRequest) =>
    _SmileID.doEnhancedKycAsync(request),

  /**
   * Get the status of a Smart Selfie job
   */
  getSmartSelfieJobStatus: (request: JobStatusRequest) =>
    _SmileID.getSmartSelfieJobStatus(request),

  /**
   * Get the status of a document verification job
   */
  getDocumentVerificationJobStatus: (request: JobStatusRequest) =>
    _SmileID.getDocumentVerificationJobStatus(request),

  /**
   * Get the status of a biometric KYC job
   */
  getBiometricKycJobStatus: (request: JobStatusRequest) =>
    _SmileID.getBiometricKycJobStatus(request),

  /**
   * Get the status of an enhanced document verification job
   */
  getEnhancedDocumentVerificationJobStatus: (request: JobStatusRequest) =>
    _SmileID.getEnhancedDocumentVerificationJobStatus(request),

  /**
   * Get products configuration
   */
  getProductsConfig: (request: ProductsConfigRequest) =>
    _SmileID.getProductsConfig(request),

  /**
   * Get valid documents based on products configuration
   */
  getValidDocuments: (request: ProductsConfigRequest) =>
    _SmileID.getValidDocuments(request),

  /**
   * Get available services
   */
  getServices: () => _SmileID.getServices(),

  /**
   * Polls the status of a smart selfie job.
   *
   * @param {JobStatusRequest} request - The job status request object.
   * @param {number} interval - The interval duration (in milliseconds) between each polling attempt.
   * @param {number} numAttempts - The number of polling attempts before stopping.
   */
  pollSmartSelfieJobStatus: (
    request: JobStatusRequest,
    interval: number,
    numAttempts: number
  ) => {
    if (!Number.isInteger(interval) || !Number.isInteger(numAttempts)) {
      throw new Error(`interval and numAttempts must be an integer.`);
    }
    request.interval = interval;
    request.numAttempts = numAttempts;
    return _SmileID.pollSmartSelfieJobStatus(request);
  },

  /**
   * Polls the status of a document verification job.
   *
   * @param {JobStatusRequest} request - The job status request object.
   * @param {number} interval - The interval duration (in milliseconds) between each polling attempt.
   * @param {number} numAttempts - The number of polling attempts before stopping.
   */
  pollDocumentVerificationJobStatus: (
    request: JobStatusRequest,
    interval: number,
    numAttempts: number
  ) => {
    if (!Number.isInteger(interval) || !Number.isInteger(numAttempts)) {
      throw new Error(`interval and numAttempts must be an integer.`);
    }
    request.interval = interval;
    request.numAttempts = numAttempts;
    return _SmileID.pollDocumentVerificationJobStatus(request);
  },

  /**
   * Polls the status of a biometric KYC job.
   *
   * @param {JobStatusRequest} request - The job status request object.
   * @param {number} interval - The interval duration (in milliseconds) between each polling attempt.
   * @param {number} numAttempts - The number of polling attempts before stopping.
   */
  pollBiometricKycJobStatus: (
    request: JobStatusRequest,
    interval: number,
    numAttempts: number
  ) => {
    if (!Number.isInteger(interval) || !Number.isInteger(numAttempts)) {
      throw new Error(`interval and numAttempts must be an integer.`);
    }
    request.interval = interval;
    request.numAttempts = numAttempts;
    return _SmileID.pollBiometricKycJobStatus(request);
  },

  /**
   * Polls the status of an enhanced document verification job.
   *
   * @param {JobStatusRequest} request - The job status request object.
   * @param {number} interval - The interval duration (in milliseconds) between each polling attempt.
   * @param {number} numAttempts - The number of polling attempts before stopping.
   */
  pollEnhancedDocumentVerificationJobStatus: (
    request: JobStatusRequest,
    interval: number,
    numAttempts: number
  ) => {
    if (!Number.isInteger(interval) || !Number.isInteger(numAttempts)) {
      throw new Error(`interval and numAttempts must be an integer.`);
    }
    request.interval = interval;
    request.numAttempts = numAttempts;
    return _SmileID.pollEnhancedDocumentVerificationJobStatus(request);
  },

  /**
   * The callback mechanism allows for asynchronous job requests and responses.
   * While the job_status API can be polled to get a result, a better method is to set up a
   * callback url and let the system POST a JSON response.
   */
  setCallbackUrl: (callbackUrl: string) => _SmileID.setCallbackUrl(callbackUrl),
};

export {
  //module
  SmileID,
  //views
  Config,
  SmileIDSmartSelfieEnrollmentView,
  SmileIDSmartSelfieAuthenticationView,
  SmileIDSmartSelfieEnrollmentEnhancedView,
  SmileIDSmartSelfieAuthenticationEnhancedView,
  SmileIDDocumentVerificationView,
  SmileIDBiometricKYCView,
  SmileIDEnhancedDocumentVerificationView,
  SmileIDSmartSelfieCaptureView,
  SmileIDDocumentCaptureView,
  SmileIDConsentView,
  EnhancedKycRequest,
  JobType,
  AuthenticationRequest,
  JobStatusRequest,
  PrepUploadRequest,
  ProductsConfigRequest,
  ConsentInformation,
  UploadRequest,
  IdInfo,
};

export type {
  //types
  SmileIDViewProps,
  AuthenticationResponse,
  BiometricKycJobStatusResponse,
  DocumentVerificationJobStatusResponse,
  EnhancedDocumentVerificationJobStatusResponse,
  EnhancedKycAsyncResponse,
  EnhancedKycResponse,
  PrepUploadResponse,
  ProductsConfigResponse,
  ServicesResponse,
  SmartSelfieJobStatusResponse,
  ValidDocumentsResponse,
  DocumentVerificationRequest,
  EnhancedDocumentVerificationRequest,
  SmartSelfieEnrollmentRequest,
  SmartSelfieAuthenticationRequest,
  BiometricKYCRequest,
  ConsentRequest,
  SmartSelfieEnrollmentEnhancedRequest,
  SmartSelfieAuthenticationEnhancedRequest,
};
