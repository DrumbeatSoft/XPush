package com.xuexiang.xpush.oppo.net;

public interface OppoPushAPI {
    String baseUrl = "http://192.168.70.99:20002/push";
    String auth = baseUrl + "/oppoPushController/getOppoProjectAuthToken";
    String updateDeviceTagAlias = baseUrl + "/oppoPushController/updateDeviceTagAlias";
    String getDeviceTagAlias = baseUrl + "/oppoPushController/getDeviceTagAlias";

}
