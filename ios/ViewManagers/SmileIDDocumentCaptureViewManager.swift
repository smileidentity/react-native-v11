import Foundation
import React
import SwiftUI

@objc(SmileIDDocumentCaptureViewManager)
class SmileIDDocumentCaptureViewManager: SmileIDBaseViewManager {
    override func getView() -> UIView {
        BaseSmileIDView(frame: .zero, contentView: AnyView(SmileIDDocumentCaptureView(product: product, smileIDUIViewDelegate: self)), product: product)
    }

    @objc func setParams(_ node: NSNumber, commandId _: NSNumber, params: NSDictionary) {
        /*  UI Updates on the Main Thread:async ensures that the UI update is scheduled to run on the next cycle of the run loop, preventing any potential blocking of the UI if the update were to take a noticeable amount of time
         */
        DispatchQueue.main.async {
            if let component = self.bridge.uiManager.view(forReactTag: node) as? BaseSmileIDView {
              
                var autoCapture: String?
                if let autoCaptureValue = params["autoCapture"] as? String {
                  autoCapture = autoCaptureValue
                }
              
                self.product.extraPartnerParams = params["extraPartnerParams"] as? [String: String] ?? [:]
                self.product.userId = params["userId"] as? String
                self.product.jobId = params["jobId"] as? String
                self.product.front = params["isDocumentFrontSide"] as? Bool ?? true
                self.product.showAttribution = params["showAttribution"] as? Bool ?? true
                self.product.showInstructions = params["showInstructions"] as? Bool ?? true
                self.product.showConfirmation = params["showConfirmation"] as? Bool ?? true
                self.product.allowGalleryUpload = params["allowGalleryUpload"] as? Bool ?? false
                self.product.autoCaptureTimeout = params["autoCaptureTimeout"] as? Int ?? 10
                self.product.autoCapture = autoCapture?.toAutoCapture() ?? .autoCapture
                self.product.onResult = params["onResult"] as? RCTBubblingEventBlock
            }
        }
    }
}
