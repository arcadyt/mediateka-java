package com.acowg.shared.models.events.peer;

import com.acowg.shared.models.basics.HasFileId;
import com.acowg.shared.models.basics.HasPeerId;
import com.acowg.shared.models.events.AEvent;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Bob")
@EqualsAndHashCode
public class FramesResponseEvent extends AEvent implements HasFileId, HasPeerId {
    @NotBlank
    private final String peerId;
    @NotBlank
    private final String fileId;
}
