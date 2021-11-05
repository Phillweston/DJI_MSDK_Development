package com.ew.autofly.module.ars100.request;

import java.util.List;



public interface IRequest {

  
    int setPackageId();

  
    List<byte[]> createHeader();

  
    List<byte[]> createBody();

  
    byte[] getData();

}
