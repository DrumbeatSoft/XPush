package com.xuexiang.xpush.oppo.net;

public interface OppoPushAPI {
    String auth = baseUrl + "/oppoPushController/getOppoProjectAuthToken";
    String updateDeviceTagAlias = baseUrl + "/oppoPushController/updateDeviceTagAlias";
    String getDeviceTagAlias = baseUrl + "/oppoPushController/getDeviceTagAlias";

}
