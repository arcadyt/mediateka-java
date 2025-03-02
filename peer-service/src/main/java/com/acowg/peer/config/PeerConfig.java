package com.acowg.peer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PeerConfig {

    @Value("${peer.name}") // Inject peer name from configuration
    private String peerName;

    @Value("${peer.edge.service.address}") // Inject edge service address from configuration
    private String edgeServiceAddress;
}