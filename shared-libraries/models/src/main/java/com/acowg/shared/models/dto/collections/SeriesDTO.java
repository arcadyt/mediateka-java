package com.acowg.shared.models.dto.collections;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeriesDTO extends MediaCollectionDTO {
    private List<SeasonDTO> seasons;
}
