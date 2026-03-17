import Foundation
import React
import SmileID
import SwiftUI

@objc(SmileIDEnhancedDocumentVerificationViewManager)
class SmileIDEnhancedDocumentVerificationViewManager: SmileIDBaseViewManager {
  override func getView() -> UIView {
    BaseSmileIDView(frame: .zero, contentView: AnyView(SmileIDEnhancedDocumentVerificationView(product: product, smileIDUIViewDelegate: self)), product: product)
  }

  @objc func setParams(_ node: NSNumber, commandId _: NSNumber, params: NSDictionary) {
    /*  UI Updates on the Main Thread:async ensures that the UI update is scheduled to run on the next cycle of the run loop, preventing any potential blocking of the UI if the update were to take a noticeable amount of time
     */
    DispatchQueue.main.async {
      if let component = self.bridge.uiManager.view(forReactTag: node) as? BaseSmileIDView {
        let onResult = params["onResult"] as? RCTDirectEventBlock
        guard let countryCode = params["countryCode"] as? String else {
          onResult?(["error": SmileIDError.unknown("countryCode is required to run Enhanced Document Verification")])
          return
        }

        var bypassSelfieCaptureWithFilePath: URL?
        if let filePath = params["bypassSelfieCaptureWithFile"] as? String {
          if !filePath.isValidUrl() {
            onResult?(["error": SmileIDError.unknown("bypassSelfieCaptureWithFile must be a valid file url")])
            return
          }
          bypassSelfieCaptureWithFilePath = URL(string: filePath)
        }

        if let consentInformation = params["consentInformation"] as? NSDictionary{
          self.product.consentInformation = consentInformation.toConsentInfo()
        } else  {
          self.product.consentInformation = nil
        }
        
        var autoCapture: String?
        if let autoCaptureValue = params["autoCapture"] as? String {
          autoCapture = autoCaptureValue
        }

        self.product.extraPartnerParams = params["extraPartnerParams"] as? [String: String] ?? [:]
        self.product.userId = params["userId"] as? String
        self.product.jobId = params["jobId"] as? String
        self.product.countryCode = countryCode
        self.product.autoCaptureTimeout = params["autoCaptureTimeout"] as? Int ?? 10
        self.product.autoCapture = autoCapture?.toAutoCapture() ?? .autoCapture
        self.product.allowAgentMode = params["allowAgentMode"] as? Bool ?? false
        self.product.forceAgentMode = params["forceAgentMode"] as? Bool ?? false
        self.product.allowNewEnroll = params["allowNewEnroll"] as? Bool ?? false
        self.product.showAttribution = params["showAttribution"] as? Bool ?? true
        self.product.showInstructions = params["showInstructions"] as? Bool ?? true
        self.product.documentType = params["documentType"] as? String
        self.product.idAspectRatio = params["idAspectRatio"] as? Double
        self.product.bypassSelfieCaptureWithFilePath = bypassSelfieCaptureWithFilePath
        self.product.captureBothSides = params["captureBothSides"] as? Bool ?? true
        self.product.allowGalleryUpload = params["allowGalleryUpload"] as? Bool ?? false
        self.product.skipApiSubmission = params["skipApiSubmission"] as? Bool ?? false
        self.product.useStrictMode = params["useStrictMode"] as? Bool ?? false
        self.product.onResult = params["onResult"] as? RCTDirectEventBlock
      }
    }
  }
}
