import Foundation
import SmileID
import SwiftUI

struct SmileIDSmartSelfieEnrollmentView: View {
    @ObservedObject var product: SmileIDProductModel
    var smileIDUIViewDelegate: SmileIDUIViewDelegate
    var body: some View {
        NavigationView {
            SmileID.smartSelfieEnrollmentScreen(
                userId: product.userId ?? generateUserId(),
                allowNewEnroll: product.allowNewEnroll,
                allowAgentMode: product.allowAgentMode,
                forceAgentMode: product.forceAgentMode,
                showAttribution: product.showAttribution,
                showInstructions: product.showInstructions,
                smileSensitivity: product.smileSensitivity,
                skipApiSubmission: product.skipApiSubmission,
                extraPartnerParams: product.extraPartnerParams as [String: String],
                delegate: self
            )
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

extension SmileIDSmartSelfieEnrollmentView: SmartSelfieResultDelegate {
    func didSucceed(selfieImage: URL, livenessImages: [URL], apiResponse: SmartSelfieResponse?) {
        var params: [String: Any] = [
            "selfieFile": selfieImage.absoluteString,
            "livenessFiles": livenessImages.map {
              $0.absoluteString
            },
        ]
        if let apiResponse = apiResponse {
            params["apiResponse"] = apiResponse
        }

        guard let jsonData = try? JSONSerialization.data(withJSONObject: params.toJSONCompatibleDictionary(), options: .prettyPrinted) else {
            smileIDUIViewDelegate.onError(error: SmileIDError.unknown("SmileIDSmartSelfieEnrollmentView encoding error"))
            return
        }
        smileIDUIViewDelegate.onResult(smileResult: String(data: jsonData, encoding: .utf8)!)
    }

    func didError(error: Error) {
        smileIDUIViewDelegate.onError(error: error)
    }
}
