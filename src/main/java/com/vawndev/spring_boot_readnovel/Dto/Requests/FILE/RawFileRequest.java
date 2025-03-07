package com.vawndev.spring_boot_readnovel.Dto.Requests.FILE;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;


public class RawFileRequest extends FileRequest {
    @Override
    public RESOURCE_TYPE Type() {
        return RESOURCE_TYPE.RAW;
    }
}
