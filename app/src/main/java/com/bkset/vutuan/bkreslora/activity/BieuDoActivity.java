package com.bkset.vutuan.bkreslora.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.bkset.vutuan.bkreslora.R;
import com.bkset.vutuan.bkreslora.adapter.CustomPagerAdapter;
import com.bkset.vutuan.bkreslora.fragment.DeviceFragment;
import com.bkset.vutuan.bkreslora.model.*;
import com.bkset.vutuan.bkreslora.task.DownloadJSON;
import com.bkset.vutuan.bkreslora.utils.Constant;
import com.bkset.vutuan.bkreslora.utils.XuLyThoiGian;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/*
Param to get data from server:
paramId: 1 = PH
        2 = Salt
        3 = Oxy
        4 = Temp
        5 = H2S
        6 = NH3
        7 = NH4Min
        8 = NH4Max
        9 = NO2Min
        10 = NO2Max
        11 = SulfideMin
        12 = SulfideMax
 */

public class BieuDoActivity extends AppCompatActivity {

    TextView txt_NgayThangNam, txt_BieuDo;

    DownloadJSON downloadJSON;
    Customer customer;
    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();

    String arr_thongso[] = {"PH", "Muối", "Oxy", "Nhiệt độ", "H2S", "NH3", "NH4 Min", "NH4 Max", "N02 Min", "NO2 Max", "Sulfide Min", "Sulfide Max"};
    public static String tmpSelectedDeviceId = "";
    public static String tmpNgayThangNam = "";
    public static String selectedDateTime = "";

    private Calendar calendar;
    private int year, month, day;


    //My code
    ArrayList<Graph> listGraph=new ArrayList<>();

    private ViewPager pagerDevice;
    private CustomPagerAdapter adapterDevice;
    private List<DeviceFragment> listFragments=new ArrayList<>();
    public static int positionFragment=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bieu_do);
        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();
        selectedDateTime = XuLyThoiGian.getCurrentTime();
        System.out.println("Time date: "+ tmpNgayThangNam);
        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObj");
        listLake = (ArrayList<Lake>) i.getSerializableExtra("listLake");
        listDevice = (ArrayList<Device>) i.getSerializableExtra("listDevice");
//        tmpSelectedDeviceId=String.valueOf(listDevice.get(0).getId());

        initWidget();
        showBackArrow();

        initViewPager();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    public void showBackArrow(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bieudo);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initWidget() {
//        txtContent= (TextView) findViewById(R.id.txtContent);
        txt_BieuDo = (TextView) findViewById(R.id.txt_TenBieuDo);
        pagerDevice= (ViewPager) findViewById(R.id.pagerDevice);
    }

    private void initViewPager(){
        listFragments=new ArrayList<>();
        listFragments=getListFragments();
        adapterDevice=new CustomPagerAdapter(getSupportFragmentManager(),listFragments);
        pagerDevice.setAdapter(adapterDevice);

    }

    private List<DeviceFragment> getListFragments(){
        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObj");
        listLake = (ArrayList<Lake>) i.getSerializableExtra("listLake");
        listDevice = (ArrayList<Device>) i.getSerializableExtra("listDevice");
        List<DeviceFragment> mList=new ArrayList<>();
        for(Device d:listDevice){
            mList.add(DeviceFragment.newInstance(d.getName(),d.getId()));
            System.out.println("device id in getListFragments: "+d.getId());
        }

        return mList;
    }


    public void getDataThongKe(final int tempSelectThongSo, final ArrayList<Graph> listGraph) {

        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_THONGKE)
                .buildUpon()
                .appendQueryParameter("strCode", "QN290394")
                .appendQueryParameter("time", tmpNgayThangNam)
                .appendQueryParameter("paramId", String.valueOf(tempSelectThongSo))
                .appendQueryParameter("deviceId", tmpSelectedDeviceId).build();

        downloadJSON = new DownloadJSON(this);


        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                try{
                    //JSONObject jsonObj = new JSONObject(msgData);
                    JSONArray jsonArray = new JSONArray(msgData);
                    if(jsonArray.length()==0){
//                        txtContent.setVisibility(View.VISIBLE);
                    }
                    else{
                        String nameGraph=arr_thongso[tempSelectThongSo-1];
                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList labels = new ArrayList<String>();

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            double value = objTmp.getDouble("value");
                            String time = objTmp.getString("time");
                            String[] words = time.split("\\s");
                            Log.d("time",words[0]+words[1]);
                            entries.add(new Entry((float)value, i));
                            labels.add(words[1]);
                        }
                        Graph graph=new Graph(nameGraph,entries,labels);
                        listGraph.add(graph);
//                        txtContent.setVisibility(View.GONE);
//
//                        graphAdapter=new GraphAdapter(listGraph);
//                        graphAdapter.notifyDataSetChanged();
//                        rvBieuDo.setAdapter(graphAdapter);

                        Log.d("size graph",listGraph.size()+"");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
//                progress.dismiss();
                Log.i("Error", msgError);
            }
        });

    }

    public void getDataThongKe(final int tempSelectThongSo) {

        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_THONGKE)
                .buildUpon()
                .appendQueryParameter("strCode", "QN290394")
                .appendQueryParameter("time", tmpNgayThangNam)
                .appendQueryParameter("paramId", String.valueOf(tempSelectThongSo))
                .appendQueryParameter("deviceId", tmpSelectedDeviceId).build();

        downloadJSON = new DownloadJSON(this);


        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                try{
                    //JSONObject jsonObj = new JSONObject(msgData);
                    JSONArray jsonArray = new JSONArray(msgData);
                    if(jsonArray.length()==0){
//                        txtContent.setVisibility(View.VISIBLE);
                    }
                    else{
                        String nameGraph=arr_thongso[tempSelectThongSo-1];
                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList labels = new ArrayList<String>();

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            double value = objTmp.getDouble("value");
                            String time = objTmp.getString("time");
                            String[] words = time.split("\\s");
                            Log.d("time",words[0]+words[1]);
                            entries.add(new Entry((float)value, i));
                            labels.add(words[1]);
                        }
                        Graph graph=new Graph(nameGraph,entries,labels);
                        listGraph.add(graph);

                        Log.i("size graph",listGraph.size()+"");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
//                progress.dismiss();
                Log.i("Error", msgError);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getBaseContext(),"Resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeActivity.navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            DatePickerDialog datePickerDialog=new DatePickerDialog(this,myDateListener,year,month,day);

            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    if ( arg2 < 9 ){
                        tmpNgayThangNam =  "0"+ (arg2+1) +"/" + arg3 +"/" + arg1;

                        if (arg3<9){
                            selectedDateTime = "0" + arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                        } else
                        selectedDateTime = arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                    } else{
                        tmpNgayThangNam =  (arg2+1)+ "/" + arg3 +"/" +  arg1;

                        if (arg3<9){
                            selectedDateTime = "0" + arg3 + "/" +  (arg2+1) +"/" + arg1;
                        } else
                            selectedDateTime = arg3 + "/" + "0"+ (arg2+1) +"/" + arg1;
                    }


                    initViewPager();
                    adapterDevice.notifyDataSetChanged();

                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu= menu.addSubMenu(0,9999,0,"Chọn ao").setIcon(R.drawable.ic_lake_menu);

        for (int i=0;i<listLake.size();i++){
            SubMenu subMenu1= subMenu.addSubMenu(0,888,i,listLake.get(i).getName());
            for (int j=0;j<listDevice.size();j++){
                if (listLake.get(i).getId()==listDevice.get(j).getLakeId())
                    subMenu1.add(j,j,j,listDevice.get(j).getName());
            }
        }

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_graph,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        for (int i=0;i<listDevice.size();i++) {
            if (id==i) {
                Log.d("menu ",listDevice.get(i).getName());

                int deviceID = listDevice.get(i).getId();
                tmpSelectedDeviceId= String.valueOf(deviceID);

                return true;
            }
        }


        if (id== R.id.mnuTime) {
            showDialog(999);

            return true;
        }

        return super.onOptionsItemSelected(item);

    }


}

