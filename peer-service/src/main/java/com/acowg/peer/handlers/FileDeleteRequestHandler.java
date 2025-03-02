package com.acowg.peer.handlers;

import com.acowg.peer.repositories.IMediaRepository;
import com.acowg.proto.peer_edge.PeerEdge;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileDeleteRequestHandler implements EdgeRequestHandler<PeerEdge.FileDeleteRequest, PeerEdge.FileDeleteResponse> {

    private final IMediaRepository mediaRepository;

    @Override
    public void handleRequest(String requestId, PeerEdge.FileDeleteRequest request,
                              StreamObserver<PeerEdge.PeerMessage> responseObserver) {
        List<String> catalogIds = request.getCatalogUuidsList();

        log.info("Processing file delete request for {} catalog IDs", catalogIds.size());

        for (String catalogId : catalogIds) {
            try {
                boolean success = deleteFile(catalogId);

                PeerEdge.FileDeleteResponse response = PeerEdge.FileDeleteResponse.newBuilder()
                        .setCatalogUuid(catalogId)
                        .setSuccess(success)
                        .setErrorMessage(success ? "" : "File not found or could not be deleted")
                        .build();

                PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                        .setRequestId(requestId)
                        .setFileDeleteResponse(response)
                        .build();

                responseObserver.onNext(message);

                log.info("Processed delete request for catalog ID {}: success={}", catalogId, success);

            } catch (Exception e) {
                log.error("Error processing file delete request for {}: {}", catalogId, e.getMessage(), e);

                PeerEdge.FileDeleteResponse response = PeerEdge.FileDeleteResponse.newBuilder()
                        .setCatalogUuid(catalogId)
                        .setSuccess(false)
                        .setErrorMessage("Error processing delete: " + e.getMessage())
                        .build();

                PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                        .setRequestId(requestId)
                        .setFileDeleteResponse(response)
                        .build();

                responseObserver.onNext(message);
            }
        }
    }

    private boolean deleteFile(String catalogId) {
        try {
            mediaRepository.deleteByCatalogId(catalogId);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete file with catalog ID {}: {}", catalogId, e.getMessage(), e);
            return false;
        }
    }
}