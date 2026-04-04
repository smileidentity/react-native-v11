import Foundation
import SmileID
import SwiftUI

struct SmileIDDocumentVerificationView: View {
    @ObservedObject var product: SmileIDProductModel
    var smileIDUIViewDelegate: SmileIDUIViewDelegate
    var body: some View {
        AnyView(NavigationView {
            if let countryCode = product.countryCode {
                SmileID.documentVerificationScreen(
                    userId: product.userId ?? generateUserId(),
                    jobId: product.jobId ?? generateJobId(),
                    allowNewEnroll: product.allowNewEnroll,
                    countryCode: countryCode, // already validated in SmileIDDocumentVerificationViewManager
                    documentType: product.documentType,
                    idAspectRatio: product.idAspectRatio,
                    bypassSelfieCaptureWithFile: product.bypassSelfieCaptureWithFilePath,
                    autoCaptureTimeout: TimeInterval(product.autoCaptureTimeout),
                    autoCapture: product.autoCapture,
                    captureBothSides: product.captureBothSides,
                    allowAgentMode: product.allowAgentMode,
                    forceAgentMode: product.forceAgentMode,
                    allowGalleryUpload: product.allowGalleryUpload,
                    showInstructions: product.showInstructions,
                    showAttribution: product.showAttribution,
                    smileSensitivity: product.smileSensitivity,
                    skipApiSubmission: product.skipApiSubmission,
                    useStrictMode: product.useStrictMode,
                    extraPartnerParams: product.extraPartnerParams as [String: String],
                    delegate: self
                )
            } else {
                // This exists for debugging purposes and will show in extreme cases
                // when the params were not set NB: setParams in the viewmanager will always
                // return an error if the required data is missing
                Text("An error has occured")
            }
        }.navigationViewStyle(StackNavigationViewStyle()))
    }
}

extension SmileIDDocumentVerificationView: DocumentVerificationResultDelegate {
    func didSucceed(selfie: URL, documentFrontImage: URL, documentBackImage: URL?, didSubmitDocumentVerificationJob: Bool) {
        var params: [String: Any] = [
            "selfieFile": selfie.absoluteString,
            "documentFrontFile": documentFrontImage.absoluteString,
            "didSubmitDocumentVerificationJob": didSubmitDocumentVerificationJob,
        ]

        if let documentBackImage = documentBackImage {
            params["documentBackFile"] = documentBackImage.absoluteString
        }

        guard let jsonData = try? JSONSerialization.data(withJSONObject: params.toJSONCompatibleDictionary(), options: .prettyPrinted) else {
            smileIDUIViewDelegate.onError(error: SmileIDError.unknown("SmileIDDocumentVerificationView encoding error"))
            return
        }
        smileIDUIViewDelegate.onResult(smileResult: String(data: jsonData, encoding: .utf8)!)
    }

    func didError(error: Error) {
        smileIDUIViewDelegate.onError(error: error)
    }
}
