import Foundation
import SmileID
import SwiftUI

struct SmileIDBiometricKYCView: View {
    @ObservedObject var product: SmileIDProductModel
    var smileIDUIViewDelegate: SmileIDUIViewDelegate
    var body: some View {
        NavigationView {
            if let idInfo = product.idInfo {
                SmileID.biometricKycScreen(
                    idInfo: idInfo,
                    userId: product.userId ?? generateUserId(),
                    jobId: product.jobId ?? generateJobId(),
                    allowNewEnroll: product.allowNewEnroll,
                    allowAgentMode: product.allowAgentMode,
                    forceAgentMode: product.forceAgentMode,
                    showAttribution: product.showAttribution,
                    showInstructions: product.showInstructions,
                    smileSensitivity: product.smileSensitivity,
                    useStrictMode: product.useStrictMode,
                    skipApiSubmission: product.skipApiSubmission,
                    extraPartnerParams: product.extraPartnerParams as [String: String],
                    consentInformation: product.consentInformation,
                    delegate: self
                )
            } else {
                // This exists for debugging purposes and will show in extreme cases
                // when the params were not set NB: setParams in the viewmanager will always
                // return an error if the required data is missing
                Text("An error has occured")
            }
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

extension SmileIDBiometricKYCView: BiometricKycResultDelegate {
    func didSucceed(selfieImage: URL, livenessImages: [URL], didSubmitBiometricJob: Bool) {
        let params: [String: Any] = [
            "selfieFile":  selfieImage.absoluteString,
            "livenessFiles": livenessImages.map {
                 $0.absoluteString
            },
            "didSubmitBiometricKycJob": didSubmitBiometricJob,
        ]

        guard let jsonData = try? JSONSerialization.data(withJSONObject: params.toJSONCompatibleDictionary(), options: .prettyPrinted) else {
            smileIDUIViewDelegate.onError(error: SmileIDError.unknown("SmileIDBiometricKYCView encoding error"))
            return
        }
        smileIDUIViewDelegate.onResult(smileResult: String(data: jsonData, encoding: .utf8)!)
    }

    func didSucceed(
        selfieImage _: URL,
        livenessImages _: [URL],
        jobStatusResponse: BiometricKycJobStatusResponse
    ) {
        let encoder = JSONEncoder()
        let jsonData = try! encoder.encode(jobStatusResponse)
        smileIDUIViewDelegate.onResult(smileResult: String(data: jsonData, encoding: .utf8)!)
    }

    func didError(error: Error) {
        smileIDUIViewDelegate.onError(error: error)
    }
}
