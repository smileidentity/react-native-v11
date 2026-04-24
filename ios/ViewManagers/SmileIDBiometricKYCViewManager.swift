import Foundation
import React
import SmileID
import SwiftUI

@objc(SmileIDBiometricKYCViewManager)
class SmileIDBiometricKYCViewManager: SmileIDBaseViewManager {
  override func getView() -> UIView {
    BaseSmileIDView(frame: .zero, contentView: AnyView(SmileIDBiometricKYCView(product: product, smileIDUIViewDelegate: self)), product: product)
  }
  
  @objc func setParams(_ node: NSNumber, commandId _: NSNumber, params: NSDictionary) {
    /*  UI Updates on the Main Thread:async ensures that the UI update is scheduled to run on the next cycle of the run loop, preventing any potential blocking of the UI if the update were to take a noticeable amount of time
     */
    DispatchQueue.main.async {
      if let component = self.bridge.uiManager.view(forReactTag: node) as? BaseSmileIDView {
        let onResult = params["onResult"] as? RCTDirectEventBlock
        guard let idInfo = params["idInfo"] as? NSDictionary else {
          onResult?(["error": SmileIDError.unknown("idInfo is required to run Biometric KYC")])
          return
        }
        if let consentInformation = params["consentInformation"] as? NSDictionary{
          self.product.consentInformation = consentInformation.toConsentInfo()
        } else  {
          self.product.consentInformation = nil
        }
        var smileSensitivity: String?
        if let smileSensitivityValue = params["smileSensitivity"] as? String {
          smileSensitivity = smileSensitivityValue
        }
        self.product.extraPartnerParams = params["extraPartnerParams"] as? [String: String] ?? [:]
        self.product.userId = params["userId"] as? String
        self.product.jobId = params["jobId"] as? String
        self.product.allowNewEnroll = params["allowNewEnroll"] as? Bool ?? false
        self.product.allowAgentMode = params["allowAgentMode"] as? Bool ?? false
        self.product.forceAgentMode = params["forceAgentMode"] as? Bool ?? false
        self.product.showAttribution = params["showAttribution"] as? Bool ?? true
        self.product.showInstructions = params["showInstructions"] as? Bool ?? true
        self.product.smileSensitivity = smileSensitivity?.toSmileSensitivity() ?? .normal
        self.product.useStrictMode = params["useStrictMode"] as? Bool ?? false
        self.product.skipApiSubmission = params["skipApiSubmission"] as? Bool ?? false
        self.product.idInfo = idInfo.toIdInfo()
        self.product.onResult = onResult
      }
    }
  }
}
