import * as React from 'react';
import { useEffect, useRef, useState } from 'react';

import { FlatList, Platform, StyleSheet, Text } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import {
  type BiometricKYCRequest,
  ConsentInformation,
  type ConsentRequest,
  type DocumentVerificationRequest,
  type EnhancedDocumentVerificationRequest,
  JobType,
  type SmartSelfieAuthenticationEnhancedRequest,
  type SmartSelfieAuthenticationRequest,
  type SmartSelfieEnrollmentEnhancedRequest,
  type SmartSelfieEnrollmentRequest,
  SmileID,
} from '@smile_identity/react-native';
import type { Product } from './types/Product';
import { SmileButton } from './SmileButton';
import { SmileIDComponent } from './SmileIDComponent';
import { AutoCapture, SmileSensitivity } from '../../src/types';

export const HomeScreen = ({ navigation }: { navigation: any }) => {
  const generateUuid = (prefix: 'job_' | 'user_'): string => {
    return (
      prefix +
      'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = (Math.random() * 16) | 0;
        const v = c === 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      })
    );
  };
  const USE_CURRENT_COMPONENT = true;
  const [isCapturing, setIsCapturing] = useState<boolean>(false);
  const [currentProduct, setCurrentProduct] = useState<Product | null>(null);
  const defaultProductRef = useRef({
    userId: '',
    jobId: '',
    allowAgentMode: true,
    showInstructions: true,
    showAttribution: true,
    showConfirmation: true,
    isDocumentFrontSide: true,
    allowGalleryUpload: true,
    useStrictMode: true, // set to false for biometric KYC,doc V and enhanced doc V to use old SmartSelfie™ capture
  });

  const defaultConsentInfo = useRef<ConsentInformation>(
    new ConsentInformation(new Date().toISOString(), true, true, true)
  );

  const [userId, setUserId] = useState(generateUuid('user_'));
  const [jobId, setJobId] = useState(generateUuid('job_'));
  const [smartSelfieEnrollment, setSmartSelfieEnrollment] =
    useState<SmartSelfieEnrollmentRequest>({
      ...defaultProductRef.current,
      extraPartnerParams: {
        optionalThingKey: 'optionalThingValue',
      },
    });
  const [smartSelfieCapture, setSmartSelfieCapture] =
    useState<SmartSelfieEnrollmentRequest>({
      ...defaultProductRef.current,
    });
  const [documentCapture, setDocumentCapture] =
    useState<DocumentVerificationRequest>({
      ...defaultProductRef.current,
      countryCode: 'ZW',
      documentType: 'PASSPORT',
      isDocumentFrontSide: false,
      captureBothSides: false,
      allowGalleryUpload: false,
    });
  const [smartSelfieAuthentication, setSmartSelfieAuthentication] =
    useState<SmartSelfieAuthenticationRequest>({
      ...defaultProductRef.current,
      userId: 'user_random_user_id',
    });
  const [smartSelfieEnrollmentEnhanced, setSmartSelfieEnrollmentEnhanced] =
    useState<SmartSelfieEnrollmentEnhancedRequest>({
      ...defaultProductRef.current,
      extraPartnerParams: {
        optionalThingKey: 'optionalThingValue',
      },
    });
  const [
    smartSelfieAuthenticationEnhanced,
    setSmartSelfieAuthenticationEnhanced,
  ] = useState<SmartSelfieAuthenticationEnhancedRequest>({
    ...defaultProductRef.current,
    userId: 'user_random_user_id',
  });
  const [documentVerification, setDocumentVerification] =
    useState<DocumentVerificationRequest>({
      ...defaultProductRef.current,
      countryCode: 'ZW',
      documentType: 'PASSPORT',
      captureBothSides: true,
      allowGalleryUpload: false,
    });

  const [enhancedDocV, setEnhancedDocV] =
    useState<EnhancedDocumentVerificationRequest>({
      ...defaultProductRef.current,
      ...defaultConsentInfo.current,
      countryCode: 'ZW',
      documentType: 'PASSPORT',
      captureBothSides: true,
      allowGalleryUpload: false,
    });

  const [biometricKYC, setBiometricKYC] = useState<BiometricKYCRequest>({
    ...defaultProductRef.current,
    ...defaultConsentInfo.current,
    idInfo: {
      country: 'NG',
      idType: 'NIN_V2',
      idNumber: '00000000000',
      entered: true,
    },
  });

  const [consentScreen, setConsentScreen] = useState<ConsentRequest>({
    partnerIcon: Platform.OS === 'android' ? 'si_logo_with_text' : 'smile_logo',
    partnerName: 'Smile React',
    productName: 'BVN',
    partnerPrivacyPolicy: 'https://docs.usesmileid.com',
    showAttribution: true,
  });
  const [smileProducts, setSmileProducts] = useState<Array<Product>>([]);

  useEffect(() => {
    /*
    SmileID initialisation can be done in multiple ways
    see https://docs.usesmileid.com/integration-options/mobile/getting-started for more details
    */
    //SmileID.initialize(false);
    let partnerId = 'YOUR_PARTNER_ID';
    let authTokenProd = 'YOUR_AUTH';

    let prodBaseUrl = 'https://api.smileidentity.com/v1/';
    let sandboxBaseUrl = 'https://testapi.smileidentity.com/v1/';

    SmileID.initialize(false, false, {
      partnerId: partnerId,
      authToken: authTokenProd,
      prodLambdaUrl: prodBaseUrl,
      testLambdaUrl: sandboxBaseUrl,
    }).catch((e) => {
      console.log('Error initialize', e);
    });

    SmileID.setAllowOfflineMode(false).catch((e) => {
      console.log('Error setting offline mode', e);
    });

    // Call if you want to apply localization
    // SmileID.applyLocalization().catch((e) => {
    //   console.log('Error applying localisation', e);
    // });

    //call back url if needed
    // SmileID.setCallbackUrl(
    //   'https://your.site/url'
    // ).catch((e) => {
    //   console.log('Error setting setCallbackUrl', e);
    // });
    SmileID.disableCrashReporting();
    setUserId(generateUuid('user_'));
    setJobId(generateUuid('job_'));
    setCurrentProduct(null);
  }, []);

  useEffect(() => {
    navigation.addListener('focus', () => {
      setUserId(generateUuid('user_'));
      setJobId(generateUuid('job_'));
      setCurrentProduct(null);
    });
  }, [navigation]);

  useEffect(() => {
    defaultProductRef.current = {
      ...defaultProductRef.current,
      userId,
      jobId,
    };

    setSmartSelfieCapture({
      ...defaultProductRef.current,
      smileSensitivity: SmileSensitivity.Normal,
    });

    setDocumentCapture({
      ...defaultProductRef.current,
      countryCode: 'ZW',
      documentType: 'PASSPORT',
      isDocumentFrontSide: false,
      captureBothSides: false,
      allowGalleryUpload: false,
      autoCaptureTimeout: 10, // seconds,
      autoCapture: AutoCapture.AutoCapture,
    });

    setSmartSelfieEnrollment({
      ...defaultProductRef.current,
      smileSensitivity: SmileSensitivity.Normal,
      extraPartnerParams: {
        optionalThingKey: 'optionalThingValue',
        job_id: 'thisismytestjobwithxyzandroid',
      },
    });

    setSmartSelfieAuthentication({
      ...defaultProductRef.current,
      smileSensitivity: SmileSensitivity.Normal,
      userId: 'YOUR ENROLLED USER ID',
      extraPartnerParams: {
        optionalThingKey: 'optionalThingValue',
        job_id: 'thisismytestjobwithxyzandroid22',
      },
    });

    setSmartSelfieEnrollmentEnhanced({
      ...defaultProductRef.current,
      showAttribution: true,
    });

    setSmartSelfieAuthenticationEnhanced({
      ...defaultProductRef.current,
      showInstructions: true,
      showAttribution: true,
    });

    setDocumentVerification({
      ...defaultProductRef.current,
      countryCode: 'ZW',
      documentType: 'PASSPORT',
      captureBothSides: true,
      allowGalleryUpload: false,
      autoCaptureTimeout: 10, // seconds,
      autoCapture: AutoCapture.AutoCapture,
      smileSensitivity: SmileSensitivity.Normal,
    });

    setEnhancedDocV({
      ...defaultProductRef.current,
      ...defaultConsentInfo.current,
      countryCode: 'ZW',
      documentType: 'PASSPORT',
      captureBothSides: true,
      allowGalleryUpload: false,
      autoCaptureTimeout: 10, // seconds,
      autoCapture: AutoCapture.AutoCapture,
    });

    setBiometricKYC({
      ...defaultProductRef.current,
      ...defaultConsentInfo.current,
      smileSensitivity: SmileSensitivity.Normal,
      idInfo: {
        country: 'NG',
        idType: 'NIN_V2',
        idNumber: '00000000000',
        entered: true,
      },
    });

    setConsentScreen({
      partnerIcon:
        Platform.OS === 'android' ? 'si_logo_with_text' : 'smile_logo',
      partnerName: 'Smile React',
      productName: 'BVN',
      partnerPrivacyPolicy: 'https://docs.usesmileid.com',
      showAttribution: true,
    });
  }, [userId, jobId]);

  useEffect(() => {
    setSmileProducts([
      {
        title: 'SmartSelfie Capture',
        product: smartSelfieCapture,
        isAsync: false,
      },
      {
        title: 'Document Capture',
        product: documentCapture,
        isAsync: false,
      },
      {
        title: 'SmartSelfie Enrollment',
        product: smartSelfieEnrollment,
        isAsync: false,
        jobType: JobType.SmartSelfieEnrollment,
      },
      {
        title: 'SmartSelfie Authentication',
        product: smartSelfieAuthentication,
        isAsync: false,
        jobType: JobType.SmartSelfieAuthentication,
      },
      {
        title: 'SmartSelfie Enrollment (Enhanced)',
        product: smartSelfieEnrollment,
        isAsync: false,
        jobType: JobType.SmartSelfieAuthentication,
      },
      {
        title: 'SmartSelfie Authentication (Enhanced)',
        product: smartSelfieAuthenticationEnhanced,
        isAsync: false,
        jobType: JobType.SmartSelfieEnrollment,
      },
      {
        title: 'Document Verification',
        product: documentVerification,
        isAsync: true,
        jobType: JobType.DocumentVerification,
        pollMethod: 'pollDocumentVerificationJobStatus',
      },
      {
        title: 'Enhanced Document Verification',
        product: enhancedDocV,
        isAsync: true,
        jobType: JobType.EnhancedDocumentVerification,
        pollMethod: 'pollEnhancedDocumentVerificationJobStatus',
      },
      {
        title: 'Biometric KYC',
        product: biometricKYC,
        isAsync: true,
        jobType: JobType.BiometricKyc,
        pollMethod: 'pollBiometricKycJobStatus',
      },
      {
        title: 'Consent Screen',
        product: consentScreen,
        isAsync: false,
      },
    ]);
  }, [
    smartSelfieCapture,
    documentCapture,
    smartSelfieEnrollment,
    smartSelfieAuthentication,
    smartSelfieEnrollmentEnhanced,
    smartSelfieAuthenticationEnhanced,
    documentVerification,
    biometricKYC,
    enhancedDocV,
    consentScreen,
  ]);

  useEffect(() => {
    setIsCapturing(!!currentProduct);
  }, [currentProduct]);

  return (
    <SafeAreaView style={styles.container} edges={['bottom']}>
      {!isCapturing && (
        <>
          <Text style={styles.title}>Test Our Products</Text>
          <FlatList
            numColumns={2}
            data={smileProducts}
            renderItem={({ item }) => (
              <SmileButton
                onPress={
                  USE_CURRENT_COMPONENT
                    ? () => {
                        setCurrentProduct(item);
                      }
                    : null
                }
                navigation={navigation}
                smileProduct={item}
              />
            )}
            keyExtractor={(item) => item.title}
          />
        </>
      )}
      {USE_CURRENT_COMPONENT && isCapturing && currentProduct && (
        <SmileIDComponent
          componentProduct={currentProduct}
          style={styles.smileView}
          onResult={(event) => {
            setUserId(generateUuid('user_'));
            setJobId(generateUuid('job_'));
            setCurrentProduct(null);
            console.log('Got response from SmileIDComponent', event);
            // If you want to run another job after this one
            // update the current product's job/user IDs
            // setCurrentProduct((prev) => ({
            //   ...prev,
            //   product: {
            //     ...prev.product,
            //     userId: generateUuid('user_'),
            //     jobId: generateUuid('job_')
            //   }
            // }));
          }}
        />
      )}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
    alignItems: 'center',
    width: '100%',
    height: '100%',
  },
  title: {
    marginTop: 20,
    fontSize: 20,
    color: 'black',
  },
  smileView: {
    width: '100%',
    height: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
