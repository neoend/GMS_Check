package com.neoend.gms.check.version;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neoend.gms.check.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jeonghun.ye on 2016-06-16.
 */
public class AppBaseAdapter extends BaseAdapter {

    public static final String TAG = AppBaseAdapter.class.getSimpleName();
    public static final String PKG_COM_ANDROID = "com.android";
    public static final String PKG_COM_GOOGLE = "com.google";

    private LayoutInflater mInflater = null;
    private ArrayList<AppInfo> mAllApps = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> mGmsApps = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> mOtherApps = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> mApps = new ArrayList<AppInfo>();
    private ArrayList<AppInfo> mAppsSelected = new ArrayList<AppInfo>();
    Comparator<AppInfo> mAppsComparator;
    String mKeywoard;
    private Context mContext = null;
    private int mSelectedPosition = -1;
    private boolean mIsAllApps;
    private int mAppCategory = R.id.radioBtnGoogle;

    public AppBaseAdapter(Context c) {
        this.mContext = c;
        this.mInflater = LayoutInflater.from(c);
        mAppsComparator = new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        };
        getDataWithAppInfo();
        makeApps();
        mAppsSelected.addAll(mApps);
    }

    @Override
    public int getCount() {
        return mAppsSelected.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return mAppsSelected.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = mInflater.inflate(R.layout.app_info, null);
        }

        TextView title = (TextView)v.findViewById(R.id.title);
        TextView uid = (TextView)v.findViewById(R.id.uid);
        TextView version = (TextView)v.findViewById(R.id.version);
        TextView pkg = (TextView)v.findViewById(R.id.pkg);
        ImageView icon = (ImageView)v.findViewById(R.id.icon);

        title.setText(getItem(position).name);
        uid.setText(getItem(position).uid);
        version.setText(getItem(position).versionCode);
        pkg.setText(getItem(position).pkg);
        if (getItem(position).icon != null) {
            icon.setImageDrawable(getItem(position).icon);
        }

        if (mSelectedPosition == position) {
            v.setBackgroundResource(R.color.colorListSelected);
        } else {
            v.setBackgroundResource(android.R.color.transparent);
        }

        return v;
    }

    public void filter(String text) {
        makeApps();
        mAppsSelected.clear();
        mKeywoard = text;

        if (TextUtils.isEmpty(text)) {
            mAppsSelected.addAll(mApps);
        } else {
            for (AppInfo appInfo : mApps) {
                if (appInfo.name.toLowerCase().contains(text) ||
                    appInfo.pkg.toLowerCase().contains(text) ||
                    appInfo.versionCode.toLowerCase().contains(text) ||
                    appInfo.uid.toLowerCase().contains(text)) {
                    mAppsSelected.add(appInfo);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void makeApps() {
//        mApps.clear();
//
//        if (mIsAllApps) {
//            mApps.addAll(mAllApps);
//        } else {
//            mApps.addAll(mGmsApps);
//        }
        mApps = null;

//        if (mIsAllApps) {
//            mApps = mAllApps;
//        } else {
//            mApps = mGmsApps;
//        }
        switch (mAppCategory) {
            case R.id.radioBtnGoogle:
                mApps = mGmsApps;
                break;
            case R.id.radioBtnOthers:
                mApps = mOtherApps;
                break;
            case R.id.radioBtnAll:
                mApps = mAllApps;
                break;
            default:
                mApps = mGmsApps;
                break;
        }
    }

    public void getDataWithAppInfo() {
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        mAllApps.clear();
        mGmsApps.clear();
        mOtherApps.clear();

        PackageInfo pi = null;
        Log.d(TAG, "ApplicationInfoSize: " + applicationInfos.size());

        for (ApplicationInfo ai : applicationInfos) {
            String versionName = "";

            try {
                pi = pm.getPackageInfo(ai.packageName, 0);
            } catch (Exception ex) {
                Log.e(TAG, "Package not found: " + ai.packageName);
            }
            if (pi != null) {
                versionName = pi.versionName;
            }

            AppInfo appInfo = new AppInfo(ai.loadLabel(pm).toString(), versionName, ai.packageName, ai.loadIcon(pm), ai.uid);
            mAllApps.add(appInfo);

            if (ai.packageName.startsWith(PKG_COM_ANDROID) || ai.packageName.startsWith(PKG_COM_GOOGLE)) {
                mGmsApps.add(appInfo);
            } else {
                mOtherApps.add(appInfo);
            }
        }
        Log.d(TAG, "applicationInfo GMS size: " + mAllApps.size());

        Collections.sort(mAllApps, mAppsComparator);
        Collections.sort(mGmsApps, mAppsComparator);
        Collections.sort(mOtherApps, mAppsComparator);
    }

    public void setSelectedItem(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public void setIsAllApps(boolean isAllApps) {
        mIsAllApps = isAllApps;
        makeApps();
        mAppsSelected.clear();
        mAppsSelected.addAll(mApps);

        if (!TextUtils.isEmpty(mKeywoard)) {
            filter(mKeywoard);
        } else {
            notifyDataSetChanged();
        }
    }

    public void setAppCategory(int category) {
        mAppCategory = category;
        makeApps();
        mAppsSelected.clear();
        mAppsSelected.addAll(mApps);

        if (!TextUtils.isEmpty(mKeywoard)) {
            filter(mKeywoard);
        } else {
            notifyDataSetChanged();
        }
    }
}
