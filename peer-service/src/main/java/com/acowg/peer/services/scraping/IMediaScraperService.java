package com.acowg.peer.services.scraping;

import com.acowg.shared.models.enums.CategoryType;

import java.io.File;

public interface IMediaScraperService {
    void scrapeNow();

    void scanCategoryDirectory(CategoryType categoryType, File categoryRoot);
}
