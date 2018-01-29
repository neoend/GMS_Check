package com.neoend.gms.check.version;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.neoend.gms.check.R;

public class VersionCheckActivity extends AppCompatActivity {

    public static final String TAG = VersionCheckActivity.class.getSimpleName();

    public static final String INTENT_MANAGE_APP_PERMISSIONS = "android.intent.action.MANAGE_APP_PERMISSIONS";
    public static final String INTENT_APP_DETAIL_SETTING = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String EXTRA_PKG_NAME = "android.intent.extra.PACKAGE_NAME";
    public static final String URI_SCHEME_PKG = "package:";

    private EditText mSearchField;
    private Button mBtnSetting;
    private Button mBtnLaunch;
    private CheckBox mCheckAllApps;
    private RadioGroup mAppCategory;
    private RadioButton mAppGoogle;
    private RadioButton mAppOthers;
    private RadioButton mAppAll;
    private ListView mListView;

    private AppBaseAdapter mAppBaseAdapter;

    private AppInfo mAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_check);

        TextView gmsVersion = (TextView)findViewById(R.id.gms_version);
        TextView mada = (TextView)findViewById(R.id.mada);
        TextView buildDisplayId = (TextView)findViewById(R.id.build_display_desc);
        TextView buildDate = (TextView)findViewById(R.id.build_date);
        TextView factoryVersion = (TextView)findViewById(R.id.factory_version);
        String gmsVersionStr = SystemProperties.get("ro.com.google.gmsversion");
        String madaStr = SystemProperties.get("ro.com.lge.mada");
        String buildDisplayIdStr = SystemProperties.get("ro.build.description");
        String buildDateStr = SystemProperties.get("ro.build.date");
        String factoryVerStr = SystemProperties.get("ro.lge.factoryversion");
        if (TextUtils.isEmpty(gmsVersionStr)) { gmsVersionStr = getString(R.string.gms_version); }
        if (TextUtils.isEmpty(madaStr)) { madaStr = getString(R.string.mada); }
        if (TextUtils.isEmpty(factoryVerStr)) { factoryVerStr = getString(R.string.factory_version_str); }
        gmsVersion.setText(gmsVersionStr);
        mada.setText(madaStr);
        buildDisplayId.setText(buildDisplayIdStr);
        buildDate.setText(buildDateStr);
        factoryVersion.setText(factoryVerStr);

        mSearchField = (EditText)findViewById(R.id.search_field);
        mBtnLaunch = (Button)findViewById(R.id.btn_launch);
        mBtnSetting = (Button)findViewById(R.id.btn_setting);
        //mCheckAllApps = (CheckBox)findViewById(R.id.check_all_apps);
        mAppCategory = (RadioGroup)findViewById(R.id.radioGroupAppCategory);
        mAppGoogle = (RadioButton)findViewById(R.id.radioBtnGoogle);
        mAppOthers = (RadioButton)findViewById(R.id.radioBtnOthers);
        mAppAll = (RadioButton)findViewById(R.id.radioBtnAll);
        mListView = (ListView)findViewById(R.id.listview);

        mAppBaseAdapter = new AppBaseAdapter(this);

//        mCheckAllApps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mAppBaseAdapter.setIsAllApps(isChecked);
//            }
//        });

        mAppCategory.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnGoogle:
                        mAppBaseAdapter.setAppCategory(R.id.radioBtnGoogle);
                        break;
                    case R.id.radioBtnOthers:
                        mAppBaseAdapter.setAppCategory(R.id.radioBtnOthers);
                        break;
                    case R.id.radioBtnAll:
                        mAppBaseAdapter.setAppCategory(R.id.radioBtnAll);
                        break;
                    default:
                        mAppBaseAdapter.setAppCategory(R.id.radioBtnGoogle);
                        break;
                }
            }
        });

        RadioButton.OnClickListener radioBtnClickListener = new RadioButton.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        };

        mAppGoogle.setChecked(true);

        mListView.setAdapter(mAppBaseAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setItemsCanFocus(false);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAppBaseAdapter.setSelectedItem(position);
                mAppInfo = (AppInfo)mListView.getItemAtPosition(position);
            }
        });

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mAppBaseAdapter.setSelectedItem(-1);
                mAppInfo = null;

                String text = mSearchField.getText().toString().toLowerCase();
                mAppBaseAdapter.filter(text);

                if (mAppBaseAdapter.getCount() == 1) {
                    mAppBaseAdapter.setSelectedItem(0);
                    mAppInfo = (AppInfo)mListView.getItemAtPosition(0);
                }
            }
        });

        mBtnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launch();
            }
        });

        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAppInfo != null) {
                    //Toast.makeText(VersionCheckActivity.this, mAppInfo.pkg, Toast.LENGTH_LONG).show();
                    goAppInfo();
//                    goPermissionsSetting();
                }
            }
        });
    }

    public void launch() {
        PackageManager pm = getPackageManager();
        if (mAppInfo != null) {
            try {
                startActivity(pm.getLaunchIntentForPackage(mAppInfo.pkg));
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goAppInfo() {
        if (mAppInfo !=null) {
            Uri packageUri = Uri.parse(URI_SCHEME_PKG + mAppInfo.pkg);
            Intent intent = new Intent(INTENT_APP_DETAIL_SETTING, packageUri);
            try {
                startActivityForResult(intent, 1);
            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goPermissionsSetting() {
        if (mAppInfo !=null) {
            Intent intent = new Intent(INTENT_MANAGE_APP_PERMISSIONS);
            intent.putExtra(EXTRA_PKG_NAME, mAppInfo.pkg);
            startActivity(intent);
        }
    }

/*
    public void getDataWithPkgInfo() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> pkgs = null;
        pkgs = pm.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
        mGmsApps = new ArrayList<AppInfo>();

        String appName = "";
        Log.d(TAG, "packageInfoSize: " + pkgs.size());
        for (PackageInfo pi : pkgs) {
            if (pi.packageName.startsWith(PKG_COM_ANDROID) || pi.packageName.startsWith(PKG_COM_GOOGLE)) {
                try {
                    appName = (String)pm.getApplicationLabel(pm.getApplicationInfo(pi.packageName, PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                AppInfo appInfo = new AppInfo(appName, pi.versionName, pi.packageName);
                mGmsApps.add(appInfo);
            }
        }
        Log.d(TAG, "packageInfo GMS size: " + mGmsApps.size());
    }

    public void getDataWithAppInfo() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        mGmsApps = new ArrayList<AppInfo>();

        PackageInfo pi = null;
        Log.d(TAG, "ApplicationInfoSize: " + applicationInfos.size());
        for (ApplicationInfo ai : applicationInfos) {
            if (ai.packageName.startsWith(PKG_COM_ANDROID) || ai.packageName.startsWith(PKG_COM_GOOGLE)) {
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

                mGmsApps.add(appInfo);
            }
        }
        Log.d(TAG, "applicationInfo GMS size: " + mGmsApps.size());

        Collections.sort(mGmsApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });
    }

    public void showVersion(View view) {
        PackageManager pm = getPackageManager();
        List<PackageInfo> pkgs = null;
        pkgs = pm.getInstalledPackages(PackageManager.PERMISSION_GRANTED);

        String appName = "";
        String msg = "";

        for (PackageInfo pi : pkgs) {
            if (pi.packageName.startsWith(PKG_COM_ANDROID) || pi.packageName.startsWith(PKG_COM_GOOGLE)) {
                try {
                    appName = (String)pm.getApplicationLabel(pm.getApplicationInfo(pi.packageName, PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                msg += appName + " : " + pi.packageName + " : " + pi.versionName + "\n";
            }
        }

        Utils.writeToFile(this, msg, "gms_version.txt");
    }

    public void fileCheck(View view) {
        Toast.makeText(this, ""+Utils.isExternalStorageWritable(), Toast.LENGTH_LONG).show();
    }
*/
}
