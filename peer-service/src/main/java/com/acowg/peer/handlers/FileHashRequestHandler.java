package com.acowg.peer.handlers;

import com.acowg.peer.entities.MediaEntity;
import com.acowg.peer.repositories.IMediaRepository;
import com.acowg.peer.services.catalog.ILocalCatalogService;
import com.acowg.peer.services.locks.RequiresDriveLock;
import com.acowg.proto.peer_edge.PeerEdge;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileHashRequestHandler implements EdgeRequestHandler<PeerEdge.FileHashRequest, PeerEdge.FileHashResponse> {

    private final IMediaRepository mediaRepository;
    private final ILocalCatalogService localCatalogService;

    @Override
    public void handleRequest(String requestId, PeerEdge.FileHashRequest request,
                              StreamObserver<PeerEdge.PeerMessage> responseObserver) {
        String catalogId = request.getCatalogUuid();
        log.info("Processing file hash request for catalog ID: {}", catalogId);

        try {
            Optional<MediaEntity> mediaEntityOpt = mediaRepository.findByCatalogId(catalogId);

            if (mediaEntityOpt.isEmpty()) {
                sendErrorResponse(requestId, catalogId, "File not found for catalog ID: " + catalogId, responseObserver);
                return;
            }

            MediaEntity mediaEntity = mediaEntityOpt.get();
            Path filePath = localCatalogService.getFullMediaPath(mediaEntity);

            Map<String, String> hashes = calculateFileHashes(filePath, request.getHashTypesList());

            PeerEdge.FileHashResponse response = PeerEdge.FileHashResponse.newBuilder()
                    .setCatalogUuid(catalogId)
                    .putAllHashes(hashes)
                    .build();

            PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                    .setRequestId(requestId)
                    .setFileHashResponse(response)
                    .build();

            responseObserver.onNext(message);
            log.info("Successfully processed hash request for catalog ID: {}", catalogId);

        } catch (Exception e) {
            log.error("Error processing file hash request for {}: {}", catalogId, e.getMessage(), e);
            sendErrorResponse(requestId, catalogId, "Error calculating hash: " + e.getMessage(), responseObserver);
        }
    }

    @RequiresDriveLock(pathParamName = "filePath")
    private Map<String, String> calculateFileHashes(Path filePath, List<String> hashTypes) throws IOException {
        Map<String, String> results = new HashMap<>();

        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist: " + filePath);
        }

        for (String hashType : hashTypes) {
            try {
                MessageDigest digest = DigestUtils.getDigest(hashType.toUpperCase());
                byte[] hashBytes = DigestUtils.digest(digest, filePath.toFile());
                results.put(hashType, Hex.encodeHexString(hashBytes));
            } catch (IllegalArgumentException e) {
                log.warn("Unsupported hash type: {}", hashType);
            }
        }

        return results;
    }

    private void sendErrorResponse(String requestId, String catalogId, String errorMessage,
                                   StreamObserver<PeerEdge.PeerMessage> responseObserver) {
        PeerEdge.FileHashResponse response = PeerEdge.FileHashResponse.newBuilder()
                .setCatalogUuid(catalogId)
                .setErrorMessage(errorMessage)
                .build();

        PeerEdge.PeerMessage message = PeerEdge.PeerMessage.newBuilder()
                .setRequestId(requestId)
                .setFileHashResponse(response)
                .build();

        responseObserver.onNext(message);
    }
}