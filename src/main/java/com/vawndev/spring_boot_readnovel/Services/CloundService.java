package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CloundService {
    List<String> getUrlChapterAfterUpload(FileRequest file) throws IOException;
    String getUrlCoverAfterUpload(ImageCoverRequest cover) throws IOException;
    Map<String, String> removeUrlOnChapterDelete(List<String> privateIDs);
    Map<String, String> removeUrlOnStory(String publicId);
}
