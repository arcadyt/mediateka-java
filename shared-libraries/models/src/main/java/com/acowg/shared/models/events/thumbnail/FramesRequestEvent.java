package com.acowg.shared.models.events.thumbnail;

import com.acowg.shared.models.basics.HasFileId;
import com.acowg.shared.models.events.AEvent;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Bob")
@EqualsAndHashCode
public class FramesRequestEvent extends AEvent implements HasFileId {
    @NotBlank
    private String fileId;

    /**
     * Creates a new builder initialized with the file ID from the specified HasFileId instance.
     *
     * @param hasFileId The instance providing the file ID.
     * @return A builder preconfigured with the file ID.
     */
    public static Bob builderFrom(HasFileId hasFileId) {
        return new Bob().fileId(hasFileId.getFileId());
    }
}
