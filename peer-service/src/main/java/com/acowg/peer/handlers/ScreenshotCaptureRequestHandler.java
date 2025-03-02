package com.acowg.peer.handlers;

import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.services.catalog.ILocalCatalogService;
import com.acowg.peer.services.catalog.MediaTypeResolver;
import com.acowg.peer.services.locks.RequiresDriveLock;
import com.acowg.peer.utils.FFmpegUtils;
import com.acowg.proto.peer_edge.PeerEdge;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScreenshotCaptureRequestHandler implements EdgeRequestHandler<PeerEdge.ScreenshotCaptureRequest, PeerEdge.ScreenshotCaptureResponse> {

    private final ILocalCatalogService localCatalogService;
    private final MediaTypeResolver mediaTypeResolver;
    private final Executor taskExecutor;

    @Override
    public void handleRequest(String requestId, PeerEdge.ScreenshotCaptureRequest request,
                              StreamObserver<PeerEdge.PeerMessage> responseObserver) {
        String catalogId = request.getCatalogUuid();
        int quantity = Math.max(1, request.getQuantity()); // Ensure at least 1 screenshot

        log.info("Processing screenshot capture request for catalog ID: {}, quantity: {}", catalogId, quantity);

        taskExecutor.execute(() -> processScreenshotRequest(requestId, catalogId, quantity, responseObserver));
    }

    private void processScreenshotRequest(String requestId, String catalogId, int quantity,
                                          StreamObserver<PeerEdge.PeerMessage> responseObserver) {
        try {
            Optional<MediaEntity> mediaEntityOpt = localCatalogService.findByCatalogId(catalogId);

            if (mediaEntityOpt.isEmpty()) {
                sendErrorResponse(requestId, catalogId, "File not found for catalog ID: " + catalogId, responseObserver);
                return;
            }

            MediaEntity mediaEntity = mediaEntityOpt.get();

            if (!mediaTypeResolver.isVideo(mediaEntity)) {
                sendErrorResponse(requestId, catalogId, "File is not a video: " + catalogId, responseObserver);
                return;
            }

            Path videoPath = localCatalogService.getFullMediaPath(mediaEntity);
            List<byte[]> screenshotData = captureScreenshots(videoPath, quantity);

            // Build response with multiple screenshots
            PeerEdge.ScreenshotCaptureResponse.Builder responseBuilder = PeerEdge.ScreenshotCaptureResponse.newBuilder()
                    .setCatalogUuid(catalogId);

            for (int i = 0; i < screenshotData.size(); i++) {
                PeerEdge.ScreenshotData screenshotDataProto = PeerEdge.ScreenshotData.newBuilder()
                        .setFrameNumberInVideo(i + 1)  // Adjust frame numbering
                        .setScreenshot(ByteString.copyFrom(screenshotData.get(i)))
                        .build();
                responseBuilder.setScreenshot(screenshotDataProto);
            }

            PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                    .setRequestId(requestId)
                    .setScreenshotCaptureResponse(responseBuilder.build())
                    .build();

            responseObserver.onNext(message);
            log.info("Successfully processed screenshot request for catalog ID: {}", catalogId);

        } catch (Exception e) {
            log.error("Error processing screenshot capture request for {}: {}", catalogId, e.getMessage(), e);
            sendErrorResponse(requestId, catalogId, "Error capturing screenshot: " + e.getMessage(), responseObserver);
        }
    }

    /**
     * Capture screenshots across video duration
     *
     * @param videoPath Path to the video file
     * @param quantity  Number of screenshots to capture
     * @return List of screenshot byte arrays
     */
    public List<byte[]> captureScreenshots(Path videoPath, int quantity) throws IOException {
        // Total intervals: quantity + 2 to create even intervals, skipping first and last
        int totalIntervals = quantity + 2;

        // Use FFmpegUtils to get video duration and total frames
        int totalFrames = FFmpegUtils.getTotalFrames(videoPath);

        // Calculate frame positions for screenshots
        List<Integer> framePositions = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            double intervalPosition = (double) i / (totalIntervals + 1);
            int frameNumber = (int) (intervalPosition * totalFrames);
            framePositions.add(frameNumber);
        }

        // Capture all frames in one go using FFmpeg
        return FFmpegUtils.captureMultipleFrames(videoPath, framePositions);
    }

    private void sendErrorResponse(String requestId, String catalogId, String errorMessage,
                                   StreamObserver<PeerEdge.PeerMessage> responseObserver) {
        PeerEdge.ScreenshotCaptureResponse response = PeerEdge.ScreenshotCaptureResponse.newBuilder()
                .setCatalogUuid(catalogId)
                .setErrorMessage(errorMessage)
                .build();

        PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setScreenshotCaptureResponse(response)
                .build();

        responseObserver.onNext(message);
        log.error("Screenshot capture error for catalog ID {}: {}", catalogId, errorMessage);
    }

    // External method for drive locking to work with AOP
    @RequiresDriveLock(pathParamName = "videoPath")
    public byte[] lockAndCaptureFrame(Path videoPath, int frameNumber) throws IOException {
        return FFmpegUtils.captureFrame(videoPath, frameNumber);
    }
}