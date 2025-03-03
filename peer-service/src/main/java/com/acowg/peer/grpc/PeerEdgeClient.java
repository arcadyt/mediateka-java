package com.acowg.peer.grpc;

import com.acowg.peer.config.PeerConfig;
import com.acowg.peer.handlers.FileDeleteRequestHandler;
import com.acowg.peer.handlers.FileHashRequestHandler;
import com.acowg.peer.handlers.ScreenshotCaptureRequestHandler;
import com.acowg.proto.peer_edge.PeerEdge;
import com.acowg.proto.peer_edge.PeerEdgeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeerEdgeClient {

    private final PeerConfig peerConfig;
    private final FileDeleteRequestHandler fileDeleteHandler;
    private final FileHashRequestHandler fileHashHandler;
    private final ScreenshotCaptureRequestHandler screenshotHandler;

    private ManagedChannel channel;
    private PeerEdgeServiceGrpc.PeerEdgeServiceStub peerEdgeServiceStub;
    private StreamObserver<PeerEdge.PeerMessage> clientStream;

    @PostConstruct
    public void initializeConnection() {
        String edgeAddress = peerConfig.getEdgeServiceAddress();

        // Create a channel to the edge service
        channel = ManagedChannelBuilder.forTarget(edgeAddress)
                .usePlaintext() // For testing only; use TLS in production
                .build();

        // Create an async stub
        peerEdgeServiceStub = PeerEdgeServiceGrpc.newStub(channel);

        // Initialize the bidirectional stream
        clientStream = peerEdgeServiceStub.message(new StreamObserver<>() {
            @Override
            public void onNext(PeerEdge.EdgeMessage message) {
                String requestId = message.getRequestId();

                try {
                    if (message.hasRegistrationResponse()) {
                        PeerEdge.PeerRegistrationResponse response = message.getRegistrationResponse();
                        log.info("Received registration response: {}", response);

                    } else if (message.hasFileDeleteRequest()) {
                        fileDeleteHandler.handleRequest(requestId, message.getFileDeleteRequest(), clientStream);

                    } else if (message.hasFileHashRequest()) {
                        fileHashHandler.handleRequest(requestId, message.getFileHashRequest(), clientStream);

                    } else if (message.hasScreenshotCaptureRequest()) {
                        screenshotHandler.handleRequest(requestId, message.getScreenshotCaptureRequest(), clientStream);

                    } else if (message.hasFileRemapRequest()) {
                        log.warn("File remap request received but handler not implemented");

                    } else if (message.hasBatchFileOfferResponse()) {
                        log.info("Received batch file offer response: {}", message.getBatchFileOfferResponse());
                    }
                } catch (Exception e) {
                    log.error("Error handling edge message: {}", e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Server stream error: {}", t.getMessage());
                scheduleReconnect();
            }

            @Override
            public void onCompleted() {
                log.info("Server stream completed");
                scheduleReconnect();
            }
        });

        sendRegistrationRequest();
    }

    private void sendRegistrationRequest() {
        String requestId = UUID.randomUUID().toString();
        PeerEdge.PeerMessage request = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setRegistrationRequest(PeerEdge.PeerRegistrationRequest.newBuilder()
                        .setPeerName(peerConfig.getPeerName())
                        .build())
                .build();
        clientStream.onNext(request);
        log.info("Sent registration request with ID: {}", requestId);
    }

    private void scheduleReconnect() {
        //todo - add logic here!
        log.warn("Connection to edge service lost. Manual reconnection needed.");
    }

    public void sendBatchFileOffer(Set<PeerEdge.FileOfferItem> fileOfferItems) {
        String requestId = UUID.randomUUID().toString();
        PeerEdge.BatchFileOfferRequest batchRequest = PeerEdge.BatchFileOfferRequest.newBuilder()
                .addAllFiles(fileOfferItems)
                .build();

        PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setBatchFileOfferRequest(batchRequest)
                .build();

        clientStream.onNext(message);
        log.info("Sent batch file offer with {} items", fileOfferItems.size());
    }

    /**
     * Sends a notification to the edge service about deleted files.
     *
     * @param deletedCatalogIds Catalog IDs that have been deleted from the peer
     * @param requestId         Optional request ID for reactive mode, null for proactive mode
     */
    public void sendDeletedFilesNotification(Set<String> deletedCatalogIds, String requestId) {
        if (deletedCatalogIds.isEmpty()) {
            return;
        }

        String messageRequestId = Optional.ofNullable(requestId).orElseGet(() -> UUID.randomUUID().toString());

        deletedCatalogIds.forEach(catalogId -> {
            var response = PeerEdge.FileDeleteResponse.newBuilder()
                    .setCatalogUuid(catalogId)
                    .setSuccess(true)
                    .build();

            var message = PeerEdge.PeerMessage.newBuilder()
                    .setRequestId(messageRequestId)
                    .setFileDeleteResponse(response)
                    .build();

            clientStream.onNext(message);
        });

        log.info("Sent {} deletion notification for {} files",
                requestId == null ? "proactive" : "reactive",
                deletedCatalogIds.size());
    }

    /**
     * Sends a proactive notification about deleted files.
     */
    public void sendDeletedFilesNotification(Set<String> deletedCatalogIds) {
        sendDeletedFilesNotification(deletedCatalogIds, null);
    }

    // Example: Send a file hash response to the edge directly
    public void sendFileHashResponse(String catalogUuid, Map<String, String> hashes) {
        String requestId = UUID.randomUUID().toString();
        PeerEdge.PeerMessage request = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setFileHashResponse(PeerEdge.FileHashResponse.newBuilder()
                        .setCatalogUuid(catalogUuid)
                        .putAllHashes(hashes)
                        .build())
                .build();
        clientStream.onNext(request);
        log.info("Sent file hash response for catalog UUID: {}", catalogUuid);
    }

    @PreDestroy
    public void shutdown() {
        if (clientStream != null) {
            try {
                clientStream.onCompleted();
            } catch (Exception e) {
                log.warn("Error completing client stream: {}", e.getMessage());
            }
        }

        if (channel != null) {
            channel.shutdown();
        }

        log.info("PeerEdgeClient shut down");
    }
}