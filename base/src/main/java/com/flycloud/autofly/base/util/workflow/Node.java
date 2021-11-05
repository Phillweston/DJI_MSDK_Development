package com.flycloud.autofly.base.util.workflow;


public interface Node {

    /**
     * 节点id
     *
     * @return 当前节点id
     */
    int getId();

    
    void onCompleted();

}