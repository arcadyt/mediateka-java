package com.acowg.peer.services.scraping;

import com.acowg.peer.services.locks.RequiresDriveLock;
import com.acowg.shared.models.enums.CategoryType;

import java.io.File;

public interface IMediaScraperService {
    void scrapeNow();

    @RequiresDriveLock(pathParamName = "categoryRoot")
    void scanCategoryDirectory(CategoryType categoryType, File categoryRoot);
}
