package com.neoend.gms.check.version;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.neoend.gms.check.R;
import com.neoend.gms.util.SystemProperty;

public class VersionCheckActivity extends AppCompatActivity {

    public static final String TAG = VersionCheckActivity.class.getSimpleName();

    public static final String INTENT_MANAGE_APP_PERMISSIONS = "android.intent.action.MANAGE_APP_PERMISSIONS";
    public static final String INTENT_APP_DETAIL_SETTING = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String EXTRA_PKG_NAME = "android.intent.extra.PACKAGE_NAME";
    public static final String URI_SCHEME_PKG = "package:";

    private TextView tvGmsVersion;
    private TextView tvMada;
    private TextView tvBuildFingerprint;
    private TextView tvBoardPlatform;
    private TextView tvProductCpuAbi;
    private TextView tvFistApiLevel;
    private TextView tvProductModel;
    private TextView tvProductName;
    private TextView tvBuildDate;

    private ProgressBar progressBar;

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

    private SystemProperty systemProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_check);

        tvGmsVersion = findViewById(R.id.gms_version);
        tvMada = findViewById(R.id.mada);
        tvBuildFingerprint = findViewById(R.id.build_fingerprint);
        tvBoardPlatform = findViewById(R.id.board_platform);
        tvProductCpuAbi = findViewById(R.id.product_cpu_abi);
        tvFistApiLevel = findViewById(R.id.first_api_level);
        tvProductModel = findViewById(R.id.product_model);
        tvProductName = findViewById(R.id.product_name);
        tvBuildDate = findViewById(R.id.build_date);

        progressBar = findViewById(R.id.progressBar);

        mSearchField = findViewById(R.id.search_field);
        mBtnLaunch = findViewById(R.id.btn_launch);
        mBtnSetting = findViewById(R.id.btn_setting);
        //mCheckAllApps = findViewById(R.id.check_all_apps);
        mAppCategory = findViewById(R.id.radioGroupAppCategory);
        mAppGoogle = findViewById(R.id.radioBtnGoogle);
        mAppOthers = findViewById(R.id.radioBtnOthers);
        mAppAll = findViewById(R.id.radioBtnAll);
        mListView = findViewById(R.id.listview);

        new ProcessingUITask().execute();
        /*
        String gmsVersionStr = Utils.getSystemProperty("ro.com.google.gmsversion");
        String madaStr = Utils.getSystemProperty("ro.com.lge.mada");
        String buildDisplayIdStr = Utils.getSystemProperty("ro.build.description");
        String buildDateStr = Utils.getSystemProperty("ro.build.date");
        String factoryVerStr = Utils.getSystemProperty("ro.lge.factoryversion");
        */

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

    class ProcessingUITask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            systemProperty = new SystemProperty();

            mAppBaseAdapter = new AppBaseAdapter(getBaseContext());

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            String gmsVersionStr = systemProperty.get("ro.com.google.gmsversion");
            String madaStr = systemProperty.get("ro.com.lge.mada");
            String buildFingerprintStr = systemProperty.get("ro.build.fingerprint");
            String boardPlatformStr = systemProperty.get("ro.board.platform");
            String productCpuAbiStr = systemProperty.get("ro.product.cpu.abi");
            String firstApiLevelStr = systemProperty.get("ro.product.first_api_level");
            String productModelStr = systemProperty.get("ro.product.model");
            String productNameStr = systemProperty.get("ro.product.name");
            String buildDateStr = systemProperty.get("ro.build.date");

            if (TextUtils.isEmpty(gmsVersionStr)) { gmsVersionStr = getString(R.string.gms_version); }
            if (TextUtils.isEmpty(madaStr)) { madaStr = getString(R.string.mada); }
            if (TextUtils.isEmpty(boardPlatformStr)) { boardPlatformStr = getString(R.string.board_platform_str); }
            tvGmsVersion.setText(gmsVersionStr);
            tvMada.setText(madaStr);
            tvBuildFingerprint.setText(buildFingerprintStr);
            tvBoardPlatform.setText(boardPlatformStr);
            tvProductCpuAbi.setText(productCpuAbiStr);
            tvFistApiLevel.setText(firstApiLevel(firstApiLevelStr));
            tvProductModel.setText(productModelStr);
            tvProductName.setText(productNameStr);
            tvBuildDate.setText(buildDateStr);

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

            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private String firstApiLevel(String level) {
        if (level == null) {
            return "null";
        }
        level = level.trim();

        int code = 0;
        try {
            code = Integer.parseInt(level);
        } catch (Exception e) {
            level = level + " : " + e.getMessage();
        }

        switch (code) {
            case Build.VERSION_CODES.P:
                level = level + " : P";
                break;
            case Build.VERSION_CODES.O_MR1:
                level = level + " : O MR1";
                break;
            case Build.VERSION_CODES.O:
                level = level + " : O";
                break;
            case Build.VERSION_CODES.N_MR1:
                level = level + " : N MR1";
                break;
            case Build.VERSION_CODES.N:
                level = level + " : N";
                break;
            case Build.VERSION_CODES.M:
                level = level + " : M";
                break;
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                level = level + " : L MR1";
                break;
            case Build.VERSION_CODES.LOLLIPOP:
                level = level + " : L";
                break;
            default:
                break;
        }

        return level;
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
