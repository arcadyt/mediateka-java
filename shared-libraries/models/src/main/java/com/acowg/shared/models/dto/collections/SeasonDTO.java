package com.acowg.shared.models.dto.collections;

import com.acowg.shared.models.dto.VideoDTO;
import lombok.Data;

import java.util.List;

@Data
public class SeasonDTO {
    private int seasonNumber;
    private List<VideoDTO> episodes;
}
