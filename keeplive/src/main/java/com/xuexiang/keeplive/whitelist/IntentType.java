/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.keeplive.whitelist;

/**
 * 开启
 * @author xuexiang
 * @since 2019-09-02 14:49
 */
public interface IntentType {
    /**
     * Android 7.0+ Doze 模式
     */
    int DOZE = 98;
    /**
     * 华为 自启管理
     */
    int HUAWEI = 99;
    /**
     * 华为 锁屏清理
     */
    int HUAWEI_GOD = 100;
    /**
     * 小米 自启动管理
     */
    int XIAOMI = 101;
    /**
     * 小米 神隐模式
     */
    int XIAOMI_GOD = 102;
    /**
     * 三星 5.0/5.1 自启动应用程序管理
     */
    int SAMSUNG_L = 103;
    /**
     * 魅族 自启动管理
     */
    int MEIZU = 104;
    /**
     * 魅族 待机耗电管理
     */
    int MEIZU_GOD = 105;
    /**
     * OPPO 自启动管理
     */
    int OPPO = 106;
    /**
     * 三星 6.0+ 未监视的应用程序管理
     */
    int SAMSUNG_M = 107;
    /**
     * Oppo 自启动管理(旧版本系统)
     */
    int OPPO_OLD = 108;
    /**
     * Vivo 后台高耗电
     */
    int VIVO_GOD = 109;
    /**
     * 金立 应用自启
     */
    int GIONEE = 110;
    /**
     * 乐视 自启动管理
     */
    int LETV = 111;
    /**
     * 乐视 应用保护
     */
    int LETV_GOD = 112;
    /**
     * 酷派 自启动管理
     */
    int COOLPAD = 113;
    /**
     * 联想 后台管理
     */
    int LENOVO = 114;
    /**
     * 联想 后台耗电优化
     */
    int LENOVO_GOD = 115;
    /**
     * 中兴 自启管理
     */
    int ZTE = 116;
    /**
     * 中兴 锁屏加速受保护应用
     */
    int ZTE_GOD = 117;
}
