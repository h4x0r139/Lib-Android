/**
 * description :
 * Created by csq E-mail:csqwyyx@163.com
 * github:https://github.com/John-Chen
 * 15-1-11
 * Created with IntelliJ IDEA
 */

package cn.yinxm.lib.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.yinxm.lib.utils.log.LogUtil;

/**
 * Created by yinxm on 2016/3/24.
 * 存储设备列表详细信息
 */
public class StorageVolumeUtil {

    /**
     * 获取存储设备及容量信息
     */
    public static List<MyStorageVolume> getVolumeList(Context context){
        List<MyStorageVolume> svList = new ArrayList<MyStorageVolume>(3);
        StorageManager mStorageManager = (StorageManager)context
                .getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumeList");
            Object[] list = (Object[]) mMethodGetPaths.invoke(mStorageManager);
            for(Object item : list){
                System.out.println("item="+item);
                svList.add(new MyStorageVolume(context, item));
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
        return svList;
    }


    /**
     * 获取已经挂载的存储设备及容量信息
     */
    public static List<MyStorageVolume> getMountedVolumeList(Context context){
        List<MyStorageVolume> result = new ArrayList<MyStorageVolume>(3);
        List<MyStorageVolume> list = getVolumeList(context);
        if(!list.isEmpty()){
            for(MyStorageVolume item : list){
                if(item.isMounted(context)){
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * 根据存储id获得存储卡信息
     */
    public static MyStorageVolume getStorageVolume(Context context, int storageId){
        MyStorageVolume sv = null;
        List<MyStorageVolume> ls = getVolumeList(context);
        if(!ls.isEmpty()){
            for(MyStorageVolume s : ls){
                if(s.storageId == storageId){
                    sv = s;
                    break;
                }
            }
        }
        return sv;
    }




    /**
     * 获取Volume挂载状态, 例如Environment.MEDIA_MOUNTED
     */
    public static String getVolumeState(Context context, String path){
        //mountPoint是挂载点名Storage'paths[1]:/mnt/extSdCard不是/mnt/extSdCard/
        //不同手机外接存储卡名字不一样。/mnt/sdcard
        StorageManager mStorageManager = (StorageManager)context
                .getSystemService(Activity.STORAGE_SERVICE);
        String status = null;
        try {
            Method mMethodGetPathsState = mStorageManager.getClass().
                    getMethod("getVolumeState", String.class);
            return (String)mMethodGetPathsState.invoke(mStorageManager, path);

        } catch (Exception e) {
            LogUtil.e(e);
        }
        return status;
    }

    public static boolean isStorageVolumeMounted(Context context, String path){
        return Environment.MEDIA_MOUNTED.equals(getVolumeState(context, path));
    }

    /**
     * 获取目录总存储空间
     */
    public static long getTotalSize(String path){
        try {
            StatFs sf = new StatFs(path);
            long blockSize = sf.getBlockSize();
            long totalCount = sf.getBlockCount();
            return totalCount * blockSize;
        } catch (Exception e){
            LogUtil.e(e);
        }
        return 0;
    }

    /**
     * 获取目录可用空间大小
     */
    public static long getAvailableSize(String path){
        try {
            StatFs sf = new StatFs(path);
            long blockSize = sf.getBlockSize();
            long availableCount = sf.getAvailableBlocks();
            return availableCount * blockSize;
        } catch (Exception e){
            LogUtil.e(e);
        }
        return 0;
    }


    /**
     * 存储设备及容量信息
     */
    public static class MyStorageVolume{
        public int storageId; //储设备的唯一标识
        public String path;
        public String description;
        public boolean primary;
        public boolean removable;
        public boolean emulated;
        public int mtpReserveSpace;
        public boolean allowMassStorage;
        public long maxFileSize;  //最大文件大小。(0表示无限制)
        public String state;      //返回null

        public long totalSize;//总容量
        public String totalSizeFormat;//总容量 B、KB、MB、GB...
        public long availableSize;//可用容量
        public String availableSizeFormat;//可用容量 B、KB、MB、GB...

        public MyStorageVolume(Context context, Object reflectItem){
            try {
                Method fmStorageId = reflectItem.getClass().getDeclaredMethod("getStorageId");
                fmStorageId.setAccessible(true);
                storageId = (Integer) fmStorageId.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fmPath = reflectItem.getClass().getDeclaredMethod("getPath");
                fmPath.setAccessible(true);
                path = (String) fmPath.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fmDescriptionId = reflectItem.getClass().getDeclaredMethod("getDescription");
                fmDescriptionId.setAccessible(true);
                description = (String) fmDescriptionId.invoke(reflectItem);
            } catch (Exception e) {
            }
            System.out.println("description1="+description);
            if(description == null || TextUtils.isEmpty(description)){
                try {
                    Method fmDescriptionId = reflectItem.getClass().getDeclaredMethod("getDescription");
                    fmDescriptionId.setAccessible(true);
                    description = (String) fmDescriptionId.invoke(reflectItem, context);
                } catch (Exception e) {
                }
            }
            System.out.println("description2="+description);

            if(description == null || TextUtils.isEmpty(description)){
                try {
                    Method fmDescriptionId = reflectItem.getClass().getDeclaredMethod("getDescriptionId");
                    fmDescriptionId.setAccessible(true);
                    int mDescriptionId = (Integer) fmDescriptionId.invoke(reflectItem);
                    if(mDescriptionId != 0){
                        description = context.getResources().getString(mDescriptionId);
                    }
                } catch (Exception e) {
                }
            }
            System.out.println("description3="+description);


            try {
                Method fmPrimary = reflectItem.getClass().getDeclaredMethod("isPrimary");
                fmPrimary.setAccessible(true);
                primary = (Boolean) fmPrimary.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fisRemovable = reflectItem.getClass().getDeclaredMethod("isRemovable");
                fisRemovable.setAccessible(true);
                removable = (Boolean) fisRemovable.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fisEmulated = reflectItem.getClass().getDeclaredMethod("isEmulated");
                fisEmulated.setAccessible(true);
                emulated = (Boolean) fisEmulated.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fmMtpReserveSpace = reflectItem.getClass().getDeclaredMethod("getMtpReserveSpace");
                fmMtpReserveSpace.setAccessible(true);
                mtpReserveSpace = (Integer) fmMtpReserveSpace.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fAllowMassStorage = reflectItem.getClass().getDeclaredMethod("allowMassStorage");
                fAllowMassStorage.setAccessible(true);
                allowMassStorage = (Boolean) fAllowMassStorage.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fMaxFileSize = reflectItem.getClass().getDeclaredMethod("getMaxFileSize");
                fMaxFileSize.setAccessible(true);
                maxFileSize = (Long) fMaxFileSize.invoke(reflectItem);
            } catch (Exception e) {
            }

            try {
                Method fState = reflectItem.getClass().getDeclaredMethod("getState");
                fState.setAccessible(true);
                state = (String) fState.invoke(reflectItem);
            } catch (Exception e) {
            }

            if (path != null) {
                totalSize = getTotalSize(path);
                availableSize = getAvailableSize(path);

                totalSizeFormat = Formatter.formatFileSize(context, totalSize);
                availableSizeFormat =  Formatter.formatFileSize(context, availableSize);
            }

        }

        /**
         * 获取Volume挂载状态, 例如Environment.MEDIA_MOUNTED
         */
        public String getVolumeState(Context context){
            return StorageVolumeUtil.getVolumeState(context, path);
        }

        public boolean isMounted(Context context){
            return getVolumeState(context).equals(Environment.MEDIA_MOUNTED);
        }


        /*public boolean isUsbStorage(){
            return mDescriptionId == android.R.string.storage_usb;
        }*/

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MyStorageVolume{");
            sb.append("storageId=").append(storageId);
            sb.append(", path='").append(path).append('\'');
            sb.append(", description='").append(description).append('\'');
            sb.append(", primary=").append(primary);
            sb.append(", removable=").append(removable);
            sb.append(", emulated=").append(emulated);
            sb.append(", mtpReserveSpace=").append(mtpReserveSpace);
            sb.append(", allowMassStorage=").append(allowMassStorage);
            sb.append(", maxFileSize=").append(maxFileSize);
            sb.append(", state='").append(state).append('\'');
            sb.append(", totalSize=").append(totalSize);
            sb.append(", totalSizeFormat='").append(totalSizeFormat).append('\'');
            sb.append(", availableSize=").append(availableSize);
            sb.append(", availableSizeFormat='").append(availableSizeFormat).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}