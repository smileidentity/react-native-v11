import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type {
  AuthenticationRequest,
  AuthenticationResponse,
  BiometricKycJobStatusResponse,
  DocumentVerificationJobStatusResponse,
  EnhancedDocumentVerificationJobStatusResponse,
  EnhancedKycAsyncResponse,
  EnhancedKycRequest,
  EnhancedKycResponse,
  JobStatusRequest,
  PrepUploadRequest,
  PrepUploadResponse,
  ProductsConfigRequest,
  ProductsConfigResponse,
  ServicesResponse,
  SmartSelfieJobStatusResponse,
  UploadRequest,
  ValidDocumentsResponse,
} from './index';
import { Config } from './types';

export interface Spec extends TurboModule {
  /**
   * Initialize SmileID SDK with configuration
   * @param useSandBox - Configuration object for the SDK
   * @param apiKey - api key specific to the partner and also environment
   * @param config - Configuration object for the SDK
   * @param enableCrashReporting - Whether to enable crash reporting
   */
  initialize(
    useSandBox: boolean,
    enableCrashReporting: boolean,
    config?: Config,
    apiKey?: string
  ): Promise<void>;

  /**
   * The callback mechanism allows for asynchronous job requests and responses.
   * While the job_status API can be polled to get a result, a better method is to set up a
   * callback url and let the system POST a JSON response.
   */
  setCallbackUrl: (callbackUrl: string) => Promise<void>;

  /**
   * Sets allow offline mode which enables
   * the ability to capture jobs offline and submit later
   */
  setAllowOfflineMode: (allowOfflineMode: boolean) => Promise<void>;

  /**
   * Submits an already captured job id
   */
  submitJob: (jobId: string) => Promise<void>;

  /**
   * Returns all job ids from the submitted directory
   */
  getSubmittedJobs: () => Promise<[string]>;

  /**
   * Returns all job ids from the unsubmitted directory
   */
  getUnsubmittedJobs: () => Promise<[string]>;

  /**
   * Cleans up a job id from the submitted or unsubmitted
   * directory
   */
  cleanup: (jobId: string) => Promise<void>;
  /**
   * NB: Only available on Android
   * Disable crash reporting
   */
  disableCrashReporting: () => Promise<void>;

  authenticate(request: AuthenticationRequest): Promise<AuthenticationResponse>;

  prepUpload(request: PrepUploadRequest): Promise<PrepUploadResponse>;

  upload(url: string, request: UploadRequest): Promise<void>;

  doEnhancedKyc(request: EnhancedKycRequest): Promise<EnhancedKycResponse>;

  doEnhancedKycAsync(
    request: EnhancedKycRequest
  ): Promise<EnhancedKycAsyncResponse>;

  getSmartSelfieJobStatus(
    request: JobStatusRequest
  ): Promise<SmartSelfieJobStatusResponse>;

  getDocumentVerificationJobStatus(
    request: JobStatusRequest
  ): Promise<DocumentVerificationJobStatusResponse>;

  getBiometricKycJobStatus(
    request: JobStatusRequest
  ): Promise<BiometricKycJobStatusResponse>;

  getEnhancedDocumentVerificationJobStatus(
    request: JobStatusRequest
  ): Promise<EnhancedDocumentVerificationJobStatusResponse>;

  getProductsConfig(
    request: ProductsConfigRequest
  ): Promise<ProductsConfigResponse>;

  getValidDocuments(
    request: ProductsConfigRequest
  ): Promise<ValidDocumentsResponse>;

  getServices(): Promise<ServicesResponse>;

  pollSmartSelfieJobStatus(
    request: JobStatusRequest
  ): Promise<SmartSelfieJobStatusResponse>;

  pollDocumentVerificationJobStatus(
    request: JobStatusRequest
  ): Promise<DocumentVerificationJobStatusResponse>;

  pollBiometricKycJobStatus(
    request: JobStatusRequest
  ): Promise<BiometricKycJobStatusResponse>;

  pollEnhancedDocumentVerificationJobStatus(
    request: JobStatusRequest
  ): Promise<EnhancedDocumentVerificationJobStatusResponse>;

  /**
   * Apply localization strings from the host app's bundle.
   * On iOS, this calls SmileID.apply(SmileIDLocalizableStrings(bundle: Bundle.main, tablename: "Localizable"))
   * so the SDK uses the app's Localizable.strings instead of the default English strings.
   * On Android, this is a no-op since Android handles localization automatically via string resources.
   */
  applyLocalization(): Promise<void>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RNSmileID');
