package com.ew.autofly.module.ars100.response.result.push;

import com.ew.autofly.module.ars100.response.result.BaseResult;

import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt8;
import static com.ew.autofly.module.ars100.ARS100Command.subBytes;



public class AuthorityResult extends BaseResult{

    private boolean hasAuthority;

    public AuthorityResult(byte[] data) {
        super(data);
        int connectResult= bytesToUInt8(subBytes(data, 0, 1));
        hasAuthority=connectResult==1;
    }

    public boolean isHasAuthority() {
        return hasAuthority;
    }
}
