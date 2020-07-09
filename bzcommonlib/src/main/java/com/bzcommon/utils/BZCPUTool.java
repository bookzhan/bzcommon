package com.bzcommon.utils;

import android.os.Build;

import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * Created by zhandalin on 2017-04-20 18:43.
 * 说明:
 */
public class BZCPUTool {
    private static int maxCpuFreq = 0;
    private final static String TAG = "bz_BZCPUTool";

    public static int getNumberOfCPUCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static long getMaxCpuFreq() {
        if (maxCpuFreq > 0) {
            return maxCpuFreq;
        }
        int cpuCount = getNumberOfCPUCores();
        long cpuMaxFreq = -1;
        for (int i = 0; i < cpuCount; i++) {
            long cpuMaxFreqTemp = getCpuMaxFreq(i);
            if (cpuMaxFreq < 0 || cpuMaxFreqTemp > cpuMaxFreq)
                cpuMaxFreq = cpuMaxFreqTemp;
        }
        return cpuMaxFreq;
    }

    private static long getCpuMaxFreq(int coreNumber) {
        String maxPath = "/sys/devices/system/cpu/cpu" + coreNumber + "/cpufreq/cpuinfo_max_freq";
        try {
            RandomAccessFile reader = new RandomAccessFile(maxPath, "r");
            long maxMhz = Long.parseLong(reader.readLine()) / 1000;
            reader.close();
            return maxMhz;
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
        return 0;
    }

    public static boolean supported64BitAbi() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String[] supported64BitAbis = Build.SUPPORTED_64_BIT_ABIS;
            BZLogUtil.d(TAG, "supported64BitAbis=" + Arrays.toString(supported64BitAbis));
            return null != supported64BitAbis && supported64BitAbis.length > 0;
        } else {
            String cpuInfo = android.os.Build.CPU_ABI;
            BZLogUtil.d(TAG, "cpuInfo=" + cpuInfo);
            return null != cpuInfo && cpuInfo.contains("arm64");
        }
    }

    public static boolean isArmCpuArchitecture() {
        boolean isArm = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] supportedAbis = Build.SUPPORTED_ABIS;
            if (null != supportedAbis && supportedAbis.length > 0) {
                for (String supportedAbi : supportedAbis) {
                    if (null != supportedAbi) {
                        if (supportedAbi.toLowerCase().contains("arm") || supportedAbi.toLowerCase().contains("neon")) {
                            isArm = true;
                            break;
                        }
                    }
                }
            }
        } else {
            String cpu_abi = Build.CPU_ABI;
            if (null != cpu_abi) {
                if (cpu_abi.toLowerCase().contains("arm") || cpu_abi.toLowerCase().contains("neon")) {
                    isArm = true;
                }
            }
            cpu_abi = Build.CPU_ABI2;
            if (null != cpu_abi) {
                if (cpu_abi.toLowerCase().contains("arm") || cpu_abi.toLowerCase().contains("neon")) {
                    isArm = true;
                }
            }
        }
        if (!isArm) {
            try {
                String strContent = BZFileUtils.readFile("/proc/cpuinfo");
                String lowerCaseContent = strContent.toLowerCase();
                if (lowerCaseContent.contains("arm") || lowerCaseContent.contains("neon")) {
                    isArm = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isArm;
    }
}
