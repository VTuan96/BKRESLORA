package com.bkset.vutuan.bkreslora.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bkset.vutuan.bkreslora.R;
import com.bkset.vutuan.bkreslora.adapter.CustomPagerPagerGiamSat;
import com.bkset.vutuan.bkreslora.fragment.BieuDoRealTimeFragment;
import com.bkset.vutuan.bkreslora.fragment.ThongSoRealTimeFragment;
import com.bkset.vutuan.bkreslora.model.*;
import com.bkset.vutuan.bkreslora.task.DownloadJSON;
import com.bkset.vutuan.bkreslora.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Customer customer;
    private final int REQUEST_SETTING_CONFIG = 111;

    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();

    //Widget
    TextView txt_Nav_UserName, txt_Nav_Email;

    //View pager
    private ViewPager pagerGiamSatHeThong;
    private CustomPagerPagerGiamSat adapterPager;
    public static NavigationView navigationView;

    public DownloadJSON downloadJSON;
    public static String selectedImeiDevice = "";
    public static String selectedLake = "";
    public static String selectedDevice = "";
    public String tempSelectedDevice = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        txt_Nav_UserName = (TextView) hView.findViewById(R.id.txt_nav_username);
        txt_Nav_Email= (TextView) hView.findViewById(R.id.txt_nav_email);

        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObject");

        downloadJSON = new DownloadJSON(this);
        getLakeAndDevice();

        String userName = customer.getUsername();
        String email = customer.getEmail();
        txt_Nav_UserName.setText(userName);
        txt_Nav_Email.setText(email);

        pagerGiamSatHeThong= (ViewPager) findViewById(R.id.pagerGiamSatHeThong);
        adapterPager=new CustomPagerPagerGiamSat(getSupportFragmentManager(),getListFragments());
        pagerGiamSatHeThong.setAdapter(adapterPager);

    }

    //Ham xu li su kien khi giu phim Back de thoat ung dung
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Giữ phím Back để thoát chương trình",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        item.setChecked(true);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            onResume();
        } else
        if (id == R.id.nav_chart) {
            Intent t = new Intent(HomeActivity.this, BieuDoActivity.class);
            t.putExtra("customerObj", customer);
            t.putExtra("listLake", listLake);
            t.putExtra("listDevice", listDevice);
            startActivity(t);

        }  else if (id == R.id.nav_setting) {
            Intent t = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivityForResult(t, REQUEST_SETTING_CONFIG);

        } else if (id == R.id.nav_share) {
            Intent t = new Intent(HomeActivity.this, GopYActivity.class);
            startActivity(t);

        } else if (id == R.id.nav_send) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Thông tin ứng dụng");
            View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.thongtin_ungdung_layout,null);
//            alert.setView(R.layout.thongtin_ungdung_layout);
            alert.setView(view);
            alert.setCancelable(false);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();

        } else if (id==R.id.nav_info){
            Intent intentInfo=new Intent(HomeActivity.this,InfoActivity.class);
            startActivity(intentInfo);
        }
        else if (id == R.id.nav_exit) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Bạn có muốn thoát chương trình!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public List<Fragment> getListFragments(){
        List<Fragment> list=new ArrayList<>();

//        Bundle bundle = new Bundle();
//        bundle.putString(Constant.SELECTED_DEVICE, selectedDevice);
//        bundle.putString(Constant.SELECTED_LAKE, selectedLake);
//        bundle.putString(Constant.SELECTED_IMEI_DEVICE, selectedImeiDevice);
//        bundle.putParcelableArrayList(Constant.LIST_LAKE, listLake);
//        bundle.putParcelableArrayList(Constant.LIST_DEVICE, listDevice);

        System.out.println(Constant.SELECTED_IMEI_DEVICE + " " + selectedImeiDevice);

        ThongSoRealTimeFragment thongSoRealTimeFragment=new ThongSoRealTimeFragment();
//        thongSoRealTimeFragment.setArguments(bundle);
        list.add(thongSoRealTimeFragment);

//        BieuDoRealTimeFragment bieuDoRealTimeFragment=new BieuDoRealTimeFragment();
////        bieuDoRealTimeFragment.setArguments(bundle);
//        list.add(bieuDoRealTimeFragment);

        return list;
    }

    public void getLakeAndDevice(){
        final Uri builder = Uri.parse(Constant.URL + Constant.API_GET_LAKE_BY_HO_DAN + customer.getHoDanId())
                .buildUpon()
                .build();

        System.out.println(builder.toString());

        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);
                if(msgData.length()>1){
                    try {
                        JSONArray jsonArray = new JSONArray(msgData);
                        for(int i=0 ; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            int Id = objTmp.getInt("Id");
                            String HoDanName = objTmp.getString("HoDanName");
                            String Name = objTmp.getString("Name");
                            String MapUrl = objTmp.getString("MapUrl");
                            String CreatedDate = objTmp.getString("CreatedDate");
                            Lake lakeObj = new Lake(Id, HoDanName, Name,MapUrl, CreatedDate);

                            lakeObj.toString();

                            listLake.add(lakeObj);

                            String urlGetDevice = Constant.URL + Constant.API_GET_DEVICE_BY_LAKE + Id;
                            System.out.println(urlGetDevice);

                            Uri builderDevice = Uri.parse(urlGetDevice)
                                    .buildUpon()
                                    .build();
                            downloadJSON.GetJSONArray(builderDevice, new DownloadJSON.DownloadJSONCallBack() {
                                @Override
                                public void onSuccess(String msgData) {
                                    try {
                                        JSONArray jsonDeviceArray = new JSONArray(msgData);
                                        Log.i("Get device by lake", msgData);
                                        for(int j=0; j<jsonDeviceArray.length(); j++){
                                            JSONObject jsonDeviceObj = jsonDeviceArray.getJSONObject(j);
                                            int IdDevice = jsonDeviceObj.getInt("Id");
                                            System.out.println("Device id "+IdDevice);
                                            String NameDevice = jsonDeviceObj.getString("Name");
                                            String ImeiDevice = jsonDeviceObj.getString("Imei");
                                            String CreatedDateDevice = jsonDeviceObj.getString("CreatedDate");
                                            String WarningNumberPhone = jsonDeviceObj.getString("WarningPhoneNumber");
                                            String WarningMail = jsonDeviceObj.getString("WarningMail");
                                            int LakeIdDevice = jsonDeviceObj.getInt("LakeId");
                                            Device deviceObj = new Device(IdDevice, NameDevice, ImeiDevice, CreatedDateDevice, WarningNumberPhone, WarningMail, LakeIdDevice);

                                            deviceObj.toString();
                                            listDevice.add(deviceObj);
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFail(String msgError) {

                                }
                            });

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }

            @Override
            public void onFail(String msgError) {
                Log.i("Error", msgError);
            }
        });
    }

    public void selectDeviceDialog(String title) {
        tempSelectedDevice = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        ViewGroup viewGroup = (ViewGroup) pagerGiamSatHeThong.getParent();
        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_select_device,viewGroup,false);

        builder.setView(view);
        builder.setTitle(title);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 600); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Lake = (Spinner)alertDialog.findViewById(R.id.spinner_lake);
        final Spinner spinner_Device = (Spinner)alertDialog.findViewById(R.id.spinner_device);

        if(listLake.size() == 0){
            Toast.makeText(getBaseContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listLake.size()];
        String arr_device[] = new String[listDevice.size()];

        for(int i=0; i<listLake.size(); i++)
            arr_lake[i] = listLake.get(i).getName();
        for(int j=0; j<listDevice.size(); j++)
            arr_device[j] = listDevice.get(j).getName();

        final ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (getBaseContext(), android.R.layout.simple_spinner_item,arr_lake);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Lake.setAdapter(adapter);
        //spinner_Lake.setOnItemSelectedListener(new MyOnItemSelectedListener());

        spinner_Lake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int LakeId = listLake.get(i).getId();
                selectedLake = spinner_Lake.getSelectedItem().toString();
                int count = 0;
                for(int k=0 ; k<listDevice.size(); k++){
                    if(listDevice.get(k).getLakeId() == LakeId){
                        count++;
                    }
                }

                String arr[] = new String[count];
                count = -1;
                for(int k=0 ; k<listDevice.size(); k++){
                    if(listDevice.get(k).getLakeId() == LakeId){
                        count++;
                        arr[count] = listDevice.get(k).getName();
                    }
                }

                ArrayAdapter<String> adapter2 =new ArrayAdapter<String>
                        (getBaseContext(), android.R.layout.simple_spinner_item, arr);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_Device.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_Device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempSelectedDevice = spinner_Device.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btn_Ok = (Button) alertDialog.findViewById(R.id.btn_Ok);
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
                //Toast.makeText(HomeActivity.this, selectedDevice, Toast.LENGTH_SHORT).show();
                selectedDevice = tempSelectedDevice;
                for(int k=0; k<listDevice.size(); k++){
                    if(listDevice.get(k).getName().compareTo(selectedDevice) == 0){
                        selectedImeiDevice = listDevice.get(k).getImei();

                        listDevice = new ArrayList<>();
                        adapterPager=new CustomPagerPagerGiamSat(getSupportFragmentManager(),getListFragments());
                        pagerGiamSatHeThong.setAdapter(adapterPager);
                        adapter.notifyDataSetChanged();
                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                    }

                }
                //getDatapackageByDeviceName();
            }
        });

        Button btn_Huy = (Button) alertDialog.findViewById(R.id.btn_Huy);
        btn_Huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


}
