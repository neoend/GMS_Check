package com.neoend.gms.check.version;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.neoend.gms.check.R;
import com.neoend.gms.util.SystemProperty;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VersionCheckFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VersionCheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VersionCheckFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // copied VersionCheckActivity
    public static final String TAG = VersionCheckFragment.class.getSimpleName();

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
    private TextView tvSwversion;

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


    public VersionCheckFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VersionCheckFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VersionCheckFragment newInstance(String param1, String param2) {
        VersionCheckFragment fragment = new VersionCheckFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View content = inflater.inflate(R.layout.version_check, container, false);
        tvGmsVersion = content.findViewById(R.id.gms_version);
        tvMada = content.findViewById(R.id.mada);
        tvBuildFingerprint = content.findViewById(R.id.build_fingerprint);
        tvBoardPlatform = content.findViewById(R.id.board_platform);
        tvProductCpuAbi = content.findViewById(R.id.product_cpu_abi);
        tvFistApiLevel = content.findViewById(R.id.first_api_level);
        tvProductModel = content.findViewById(R.id.product_model);
        tvProductName = content.findViewById(R.id.product_name);
        tvBuildDate = content.findViewById(R.id.build_date);
        tvSwversion = content.findViewById(R.id.swversion);

        progressBar = content.findViewById(R.id.progressBar);

        mSearchField = content.findViewById(R.id.search_field);
        mBtnLaunch = content.findViewById(R.id.btn_launch);
        mBtnSetting = content.findViewById(R.id.btn_setting);
        //mCheckAllApps = findViewById(R.id.check_all_apps);
        mAppCategory = content.findViewById(R.id.radioGroupAppCategory);
        mAppGoogle = content.findViewById(R.id.radioBtnGoogle);
        mAppOthers = content.findViewById(R.id.radioBtnOthers);
        mAppAll = content.findViewById(R.id.radioBtnAll);
        mListView = content.findViewById(R.id.listview);
        return content;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new ProcessingUITask().execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class ProcessingUITask extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            systemProperty = new SystemProperty();

            mAppBaseAdapter = new AppBaseAdapter(getActivity());

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
            String swversion = systemProperty.get("ro.lge.swversion");
            if (TextUtils.isEmpty(swversion)) { swversion = systemProperty.get("ro.vendor.lge.swversion"); }

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
            tvSwversion.setText(swversion);

            mAppCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

    public void launch() {
        PackageManager pm = getActivity().getPackageManager();
        if (mAppInfo != null) {
            try {
                startActivity(pm.getLaunchIntentForPackage(mAppInfo.pkg));
            } catch (Exception ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
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
}
