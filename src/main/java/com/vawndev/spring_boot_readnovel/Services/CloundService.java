package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;

import java.io.IOException;
import java.util.List;

public interface CloundService {
    List<String> getUrlChapterAfterUpload(FileRequest file) throws IOException;
    String getUrlCoverAfterUpload(ImageCoverRequest cover) throws IOException;

}
