package com.flycloud.autofly.base.util.workflow;


public class WorkNode implements Node {


    private int nodeId;


    private Worker worker;

    private WorkCallBack callBack;

    public static WorkNode build(int nodeId, Worker worker) {
        return new WorkNode(nodeId, worker);
    }

    public WorkNode(int nodeId, Worker worker) {
        this.nodeId = nodeId;
        this.worker = worker;
    }

    void doWork(WorkCallBack callBack) {
        this.callBack = callBack;
        worker.doWork(this);
    }

    void removeCallBack() {
        this.callBack = null;
    }

    @Override
    public int getId() {
        return nodeId;
    }

    @Override
    public void onCompleted() {
        if (null != callBack) {
            callBack.onWorkCompleted();
        }
    }

    @Override
    public String toString() {
        return "nodeId : " + getId();
    }

    interface WorkCallBack {


        void onWorkCompleted();

    }
}
