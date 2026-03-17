import Foundation
import SmileID
import SwiftUI

struct SmileIDSmartSelfieCaptureView: View {
    @ObservedObject var product: SmileIDProductModel
    @State private var viewModel: SelfieViewModel?
    @State private var acknowledgedInstructions = false
    var smileIDUIViewDelegate: SmileIDUIViewDelegate

    var body: some View {
        NavigationView {
            selfieCaptureScreen
        }.navigationViewStyle(StackNavigationViewStyle())
            .padding()
            .onReceive(product.$allowAgentMode.combineLatest(product.$forceAgentMode)) { _ in
                createViewModelIfNeeded()
            }
    }

    private func createViewModelIfNeeded() {
        guard viewModel == nil else { return }
        let allowAgentMode = product.allowAgentMode
        let forceAgentMode = product.forceAgentMode
        viewModel = SelfieViewModel(
            isEnroll: false,
            userId: product.userId ?? generateUserId(),
            jobId: product.jobId ?? generateJobId(),
            allowNewEnroll: false,
            allowAgentMode: allowAgentMode,
            forceAgentMode: forceAgentMode,
            skipApiSubmission: true,
            extraPartnerParams: [:]
        )
    }

    private var selfieCaptureScreen: some View {
        Group {
            if product.useStrictMode {
                SmileID.smartSelfieEnrollmentScreenEnhanced(
                    userId: product.userId ?? generateUserId(),
                    showAttribution: product.showAttribution,
                    showInstructions: product.showInstructions,
                    skipApiSubmission: true,
                    extraPartnerParams: product.extraPartnerParams,
                    delegate: self
                )
            } else if let viewModel = viewModel {
                legacySelfieCaptureScreen(viewModel: viewModel)
            }
        }
    }

    private func legacySelfieCaptureScreen(viewModel: SelfieViewModel) -> some View {
        ZStack {
            if product.showInstructions, !acknowledgedInstructions {
                SmartSelfieInstructionsScreen(
                    showAttribution: product.showAttribution) {
                        acknowledgedInstructions = true
                    }
            } else if viewModel.processingState != nil {
                Color.clear.onAppear {
                    viewModel.onFinished(callback: self)
                }
            } else if let selfieToConfirm = viewModel.selfieToConfirm {
                if product.showConfirmation {
                    ImageCaptureConfirmationDialog(
                        title: SmileIDResourcesHelper.localizedString(for: "Confirmation.GoodSelfie"),
                        subtitle: SmileIDResourcesHelper.localizedString(for: "Confirmation.FaceClear"),
                        image: UIImage(data: selfieToConfirm)!,
                        confirmationButtonText: SmileIDResourcesHelper.localizedString(for: "Confirmation.YesUse"),
                        onConfirm: viewModel.submitJob,
                        retakeButtonText: SmileIDResourcesHelper.localizedString(for: "Confirmation.Retake"),
                        onRetake: viewModel.onSelfieRejected,
                        scaleFactor: 1.25
                    ).preferredColorScheme(.light)
                } else {
                    Color.clear.onAppear {
                        viewModel.submitJob()
                    }
                }
            } else {
                SelfieCaptureScreen(
                    viewModel: viewModel,
                    allowAgentMode: product.allowAgentMode,
                    forceAgentMode: product.forceAgentMode,
                    smileSensitivity: product.smileSensitivity
                ).preferredColorScheme(.light)
            }
        }
    }
}

extension SmileIDSmartSelfieCaptureView: SmartSelfieResultDelegate {
    func didSucceed(selfieImage: URL, livenessImages: [URL], apiResponse _: SmartSelfieResponse?) {
        let params: [String: Any] = [
            "selfieFile": selfieImage.absoluteString,
            "livenessFiles": livenessImages.map {
                $0.absoluteString
            },
        ]

        guard let jsonData = try? JSONSerialization.data(withJSONObject: params.toJSONCompatibleDictionary(), options: .prettyPrinted) else {
            smileIDUIViewDelegate.onError(error: SmileIDError.unknown("SmileIDSmartSelfieCaptureView encoding error"))
            return
        }
        smileIDUIViewDelegate.onResult(smileResult: String(data: jsonData, encoding: .utf8)!)
    }

    func didError(error: Error) {
        smileIDUIViewDelegate.onError(error: error)
    }
}
