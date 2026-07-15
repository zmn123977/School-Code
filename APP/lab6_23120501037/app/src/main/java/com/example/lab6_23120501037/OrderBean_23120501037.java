package com.example.lab6_23120501037;

public class OrderBean_23120501037 {
    private String userName;
    private String userPhone;
    private String address;
    private String orderTime;
    private String serviceName;

    private String workerName;
    private String workerPhone;

    public OrderBean_23120501037(String userName, String userPhone, String address, String orderTime, String serviceName, String workerName, String workerPhone) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.address = address;
        this.orderTime = orderTime;
        this.serviceName = serviceName;
        this.workerName = workerName;
        this.workerPhone = workerPhone;
    }

    public String getUserName() { return userName; }
    public String getUserPhone() { return userPhone; }
    public String getAddress() { return address; }
    public String getOrderTime() { return orderTime; }
    public String getServiceName() { return serviceName; }
    public String getWorkerName() { return workerName; }
    public String getWorkerPhone() { return workerPhone; }
}