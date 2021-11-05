package com.ew.autofly.module.ars100.response.result.monitor;

import com.ew.autofly.module.ars100.response.result.BaseResult;

import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt32;
import static com.ew.autofly.module.ars100.ARS100Command.bytesToUInt8;
import static com.ew.autofly.module.ars100.ARS100Command.subBytes;



public class WarnResult extends BaseResult{

  
    public final int TYPE_DEBUG=0;
  
    public final int TYPE_NORMAL=1;
  
    public final int TYPE_WARN=2;
  
    public final int TYPE_ERROR=3;
  
    public final int TYPE_DEAD=4;

    private int type;
    private String message;

    public WarnResult(byte[] data) {
        super(data);
        type=bytesToUInt8(subBytes(data, 0, 1));
        message=new String(subBytes(data, 1, data.length-1));
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
