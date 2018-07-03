package com.bkset.vutuan.bkreslora.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.bkset.vutuan.bkreslora.R;
import com.bkset.vutuan.bkreslora.model.*;
import com.bkset.vutuan.bkreslora.adapter.*;
import com.bkset.vutuan.bkreslora.activity.*;
import com.bkset.vutuan.bkreslora.utils.*;
import com.bkset.vutuan.bkreslora.task.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A simple {@link Fragment} subclass.
 */
public class BieuDoRealTimeFragment extends Fragment {

    Customer customer;
    DownloadJSON downloadJSON;
    ProgressDialog pDialog;

    public double PH_Max, PH_Min, Temp_Max, Temp_Min, Salt_Max, Salt_Min, Oxy_Max, Oxy_Min, H2S_Max, H2S_Min,NO2_Max, NO2_Min,NH4_Max,NH4_Min;


    private final int REQUEST_SETTING_CONFIG = 111;

    //Widget
    TextView txt_Nav_UserName, txt_Nav_Email, txt_Tittle, txt_Time_Update;

    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();
    public static String selectedDevice = "";
    public static String selectedLake = " ";
    String selectedImeiDevice = "";

    private RecyclerView rvBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter=new GraphAdapter(listGraph);
    private int count=0;
    private String time="";

    //All components of all graphs
    private ArrayList<Entry> entriesPH=new ArrayList<>();
    private ArrayList labelsPH = new ArrayList<String>();

    private ArrayList<Entry> entriesSalt=new ArrayList<>();
    private ArrayList labelsSalt = new ArrayList<String>();

    private ArrayList<Entry> entriesOxy=new ArrayList<>();
    private ArrayList labelsOxy = new ArrayList<String>();

    private ArrayList<Entry> entriesTemp=new ArrayList<>();
    private ArrayList labelsTemp = new ArrayList<String>();


    private ArrayList<Entry> entriesH2S=new ArrayList<>();
    private ArrayList labelsH2S = new ArrayList<String>();

    private ArrayList<Entry> entriesNH3=new ArrayList<>();
    private ArrayList labelsNH3 = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4Min=new ArrayList<>();
    private ArrayList labelsNH4Min = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4Max=new ArrayList<>();
    private ArrayList labelsNH4Max = new ArrayList<String>();

    private ArrayList<Entry> entriesNO2Min=new ArrayList<>();
    private ArrayList labelsNO2Min = new ArrayList<String>();

    private ArrayList<Entry> entriesNO2Max=new ArrayList<>();
    private ArrayList labelsNO2Max = new ArrayList<String>();

    private ArrayList<Entry> entriesH2SMax=new ArrayList<>();
    private ArrayList labelsH2SMax = new ArrayList<String>();

    private ArrayList<Entry> entriesH2SMin=new ArrayList<>();
    private ArrayList labelsH2SMin = new ArrayList<String>();

    //All label of graph
    private String[] arrLabels=new String[]{"PH","Salt","Oxy", "Temp", "H2S","NH3","NH4 Max","NH4 Min",
            "NO2 Max","NO2 Min","H2S Max","H2S Min" };

    private Socket mSocket;
    final String TAG = "Socket IO";

    {
        try {
            IO.Options opts = new IO.Options();
            opts.path = "/socket.io 2.03 version new";
            mSocket = IO.socket("http://202.191.56.103:5515");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public BieuDoRealTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_bieu_do_real_time,container,false);

        customer=HomeActivity.customer;
        initWidget(v);
        pDialog = new ProgressDialog(getContext());

        mSocket.on("new message", onDataReceive);

        downloadJSON = new DownloadJSON(getContext());
        getLakeAndDevice();

//        Bundle bundle = getArguments();
//        selectedDevice = bundle.getString(Constant.SELECTED_DEVICE);
//        selectedLake = bundle.getString(Constant.SELECTED_LAKE);
//        selectedImeiDevice = bundle.getString(Constant.SELECTED_IMEI_DEVICE);
//        listLake = bundle.getParcelableArrayList(Constant.LIST_LAKE);
//        listDevice = bundle.getParcelableArrayList(Constant.LIST_DEVICE);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeviceDialog("Chọn thiết bị");
            }
        });




        return v;
    }

    public void initGraph(){
        entriesPH=new ArrayList<>();
        labelsPH = new ArrayList<String>();

        entriesSalt=new ArrayList<>();
        labelsSalt = new ArrayList<String>();

        entriesOxy=new ArrayList<>();
        labelsOxy = new ArrayList<String>();

        entriesTemp=new ArrayList<>();
        labelsTemp = new ArrayList<String>();


        entriesH2S=new ArrayList<>();
        labelsH2S = new ArrayList<String>();

        entriesNH3=new ArrayList<>();
        labelsNH3 = new ArrayList<String>();

        entriesNH4Min=new ArrayList<>();
        labelsNH4Min = new ArrayList<String>();

        entriesNH4Max=new ArrayList<>();
        labelsNH4Max = new ArrayList<String>();

        entriesNO2Min=new ArrayList<>();
        labelsNO2Min = new ArrayList<String>();

        entriesNO2Max=new ArrayList<>();
        labelsNO2Max = new ArrayList<String>();

        entriesH2SMax=new ArrayList<>();
        labelsH2SMax = new ArrayList<String>();

        entriesH2SMin=new ArrayList<>();
        labelsH2SMin = new ArrayList<String>();

        listGraph=new ArrayList<>();
        Graph gPH=new Graph(arrLabels[0],entriesPH,labelsPH);
        Graph gSalt=new Graph(arrLabels[1],entriesSalt,labelsSalt);
        Graph gOxy=new Graph(arrLabels[2],entriesOxy,labelsOxy);
        Graph gTemp=new Graph(arrLabels[3],entriesTemp,labelsTemp);
        Graph gH2S=new Graph(arrLabels[4],entriesH2S,labelsH2S);
        Graph gNH3=new Graph(arrLabels[5],entriesNH3,labelsNH3);
        Graph gNH4Max=new Graph(arrLabels[6],entriesNH4Max,labelsNH4Max);
        Graph gNH4Min=new Graph(arrLabels[7],entriesNH4Min,labelsNH4Min);
        Graph gNO2Max=new Graph(arrLabels[8],entriesNO2Min,labelsNO2Max);
        Graph gNO2Min=new Graph(arrLabels[9],entriesNO2Min,labelsNO2Min);
        Graph gH2SMax=new Graph(arrLabels[10],entriesH2SMax,labelsH2SMax);
        Graph gH2SMin=new Graph(arrLabels[11],entriesH2SMin,labelsH2SMin);
        Graph [] arrGraph=new Graph[]{gPH,gSalt,gTemp, gH2S,gH2SMax,gH2SMin,gNH3,gNH4Max,gNH4Min,gNO2Max,gNO2Min,gOxy};
        for (Graph g:arrGraph){
            listGraph.add(g);
        }

        count = 0;

        adapter = new GraphAdapter(listGraph);
        rvBieuDoThongKe.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initWidget(View v){
        txt_Time_Update = (TextView) v.findViewById(R.id.txt_time_update);
        txt_Tittle = (TextView) v.findViewById(R.id.txt_title);


        //create graph
        rvBieuDoThongKe= (RecyclerView) v.findViewById(R.id.rvBieuDoThongKe);
        rvBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvBieuDoThongKe.setLayoutManager(manager);

        listGraph=new ArrayList<>();
        Graph gPH=new Graph(arrLabels[0],entriesPH,labelsPH);
        Graph gSalt=new Graph(arrLabels[1],entriesSalt,labelsSalt);
        Graph gOxy=new Graph(arrLabels[2],entriesOxy,labelsOxy);
        Graph gTemp=new Graph(arrLabels[3],entriesTemp,labelsTemp);
        Graph gH2S=new Graph(arrLabels[4],entriesH2S,labelsH2S);
        Graph gNH3=new Graph(arrLabels[5],entriesNH3,labelsNH3);
        Graph gNH4Max=new Graph(arrLabels[6],entriesNH4Max,labelsNH4Max);
        Graph gNH4Min=new Graph(arrLabels[7],entriesNH4Min,labelsNH4Min);
        Graph gNO2Max=new Graph(arrLabels[8],entriesNO2Min,labelsNO2Max);
        Graph gNO2Min=new Graph(arrLabels[9],entriesNO2Min,labelsNO2Min);
        Graph gH2SMax=new Graph(arrLabels[10],entriesH2SMax,labelsH2SMax);
        Graph gH2SMin=new Graph(arrLabels[11],entriesH2SMin,labelsH2SMin);
        Graph [] arrGraph=new Graph[]{gPH,gSalt,gTemp, gH2S,gH2SMax,gH2SMin,gNH3,gNH4Max,gNH4Min,gNO2Max,gNO2Min,gOxy};
        for (Graph g:arrGraph){
            listGraph.add(g);
        }
        adapter=new GraphAdapter(listGraph);
        rvBieuDoThongKe.setAdapter(adapter);
    }

    String tempSelectedDevice;

    public void selectDeviceDialog(String title) {
        tempSelectedDevice = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.layout_select_device,null);
//        builder.setView(R.layout.layout_select_device);
        builder.setView(view);
        builder.setTitle(title);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 650); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Lake = (Spinner)alertDialog.findViewById(R.id.spinner_lake);
        final Spinner spinner_Device = (Spinner)alertDialog.findViewById(R.id.spinner_device);

        if(listLake.size() == 0){
            Toast.makeText(getContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listLake.size()];
        String arr_device[] = new String[listDevice.size()];

        for(int i=0; i<listLake.size(); i++)
            arr_lake[i] = listLake.get(i).getName();
        for(int j=0; j<listDevice.size(); j++)
            arr_device[j] = listDevice.get(j).getName();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,arr_lake);
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
                        (getContext(), android.R.layout.simple_spinner_item, arr);
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
                        initGraph();

                        selectedImeiDevice = listDevice.get(k).getImei();
                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                        mSocket.emit("authentication", selectedImeiDevice);
                        mSocket.emit("join", selectedImeiDevice);
                        //mSocket.on("new message", onDataReceive);
                        mSocket.connect();
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

    public void getLakeAndDevice(){

//        if (!selectedImeiDevice.equals("") && !selectedImeiDevice.equals(null)){
//            mSocket.emit("authentication", selectedImeiDevice);
//            mSocket.emit("join", selectedImeiDevice);
//            mSocket.on("new message", onDataReceive);
//            mSocket.connect();
//            Log.i("IMEI DEVICE SELECT2", selectedImeiDevice);
//        }

        final Uri builder = Uri.parse(Constant.URL + Constant.API_GET_LAKE_BY_HO_DAN + customer.getHoDanId())
                .buildUpon()
                .build();

        System.out.println(builder.toString());

        downloadJSON.GetJSONArray(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Get lake by ho dan ", msgData);
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
                            //DownloadJSON downloadJSONDevice = new DownloadJSON(getContext());
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
                                            System.out.println("size of list divice: " + listDevice.size());
                                        }

                                        Log.i("Number of Lake bieu do", "Lake: " + listLake.size() + " - Device:" + listDevice.size());

                                        if( listLake.size()>0){
                                            if (listDevice.size()>0){
                                                selectedLake = listLake.get(0).getName();
                                                selectedDevice = listDevice.get(0).getName();
                                                selectedImeiDevice = listDevice.get(0).getImei();
                                                mSocket.emit("authentication", selectedImeiDevice);
                                                mSocket.emit("join", selectedImeiDevice);
//                                            mSocket.on("new message", onDataReceive);
                                                mSocket.connect();
                                                Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                                                //getDatapackageByDeviceName();
                                            }

                                        } else {
                                            Toast.makeText(getContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
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

    public void getDatapackageByDeviceName(){
        pDialog.setMessage("Đang tải...");
        pDialog.show();



        int deviceId = -1;
        for(int i=0; i<listDevice.size(); i++){
            if(listDevice.get(i).getName().compareTo(selectedDevice) == 0){
                deviceId = listDevice.get(i).getId();
                break;
            }
        }


        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_PACKAGE)
                .buildUpon()
                .appendQueryParameter("DeviceId", deviceId + "").build();
        downloadJSON = new DownloadJSON(getContext());

        downloadJSON.GetJSON2(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);

                try {
                    JSONObject jsonObj = new JSONObject(msgData);
                    int Id = jsonObj.getInt("Id");
                    int DeviceId = jsonObj.getInt("DeviceId");
                    String Time_Package = jsonObj.getString("Time_Package");
                    double PH = jsonObj.getDouble("PH");
                    double Salt = jsonObj.getDouble("Salt");
                    double Oxy = jsonObj.getDouble("Oxy");
                    double Temp = jsonObj.getDouble("Temp");
                    double H2S = jsonObj.getDouble("H2S");
                    double NH3 = jsonObj.getDouble("NH3");
                    double NH4Min = jsonObj.getDouble("NH4Min");
                    double NH4Max = jsonObj.getDouble("NH4Max");
                    double NO2Min = jsonObj.getDouble("NO2Min");
                    double NO2Max = jsonObj.getDouble("NO2Max");
                    double SulfideMin = jsonObj.getDouble("SulfideMin");
                    double SulfideMax = jsonObj.getDouble("SulfideMax");
                    //double Alkalinity = jsonObj.getDouble("Alkalinity");
                    String NgayTao = jsonObj.getString("NgayTao");

                    Datapackage datapackage = new Datapackage(Id, DeviceId, Time_Package, PH, Salt, Temp, Oxy, H2S, NH3, NH4Max, NH4Min, NO2Min, SulfideMin, NO2Max, SulfideMax,  NgayTao);
                    updateView(datapackage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
                Log.i("Error", msgError);
            }
        });

        pDialog.dismiss();
    }

    public void updateView(Datapackage datapackage){
        txt_Tittle.setText("Ao " + selectedLake + " - Thiết bị " + selectedDevice);
        txt_Time_Update.setText("Cập nhật lúc: " + XuLyThoiGian.StringToDatetimeString(datapackage.getTime_Package()));
        adapter.notifyDataSetChanged();
    }

    public void handleExit(){
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onDataReceive);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onDataReceive);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),
                            "Connected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnected");

//                    Toast.makeText(getContext(),
//                            "Disconnect", Toast.LENGTH_SHORT).show();
                    mSocket.connect();

                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, args[0].toString());
                    Toast.makeText(getContext(),
                            "Lỗi cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDataReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    Log.i("Socket IO data", data);

                    try {
                        JSONObject jsonObj = new JSONObject(data);

                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                        String deviceImei = jsonObj.getString("Device_IMEI");
                        Log.i("IMEI DEVICE SERVER", deviceImei);

                        if(deviceImei.compareTo(selectedImeiDevice) == 0){
                            String Time_Package = jsonObj.getString("Datetime_Packet");
                            double PH = jsonObj.getDouble("PH");
                            double Salt = jsonObj.getDouble("Salt");
                            double Oxy = jsonObj.getDouble("Oxy");
                            double Temp = jsonObj.getDouble("NhietDo");
                            double H2S = jsonObj.getDouble("H2S");
                            double NH3 = jsonObj.getDouble("NH3");
                            double NH4Min = jsonObj.getDouble("NH4Min");
                            double NH4Max = jsonObj.getDouble("NH4Max");
                            double NO2Min = jsonObj.getDouble("NO2Min");
                            double NO2Max = jsonObj.getDouble("NO2Max");
                            double SulfideMin = jsonObj.getDouble("SulfideMin");
                            double SulfideMax = jsonObj.getDouble("SulfideMax");
                            //double Alkalinity= jsonObj.getDouble("Alkalinity");
                            String NgayTao = jsonObj.getString("Datetime_Packet");

                            double [] arrValue=new double[]{PH,Salt,Oxy,Temp,H2S,NH3,NH4Max,NH4Min,NO2Max,NO2Min,SulfideMax,SulfideMin};

                            time=XuLyThoiGian.StringToDatetimeString(Time_Package);
                            String[] arrTime=time.split(" ");
                            time=arrTime[1]; //gia tri thoi gian cua du lieu
//                            Toast.makeText(getApplicationContext(),arrTime[1],Toast.LENGTH_LONG).show();

                            //them gia tri thong so, va thoi gian vao bang bieu do
                            addEntryAndLabel(entriesPH,labelsPH,PH,count,time);
                            addEntryAndLabel(entriesSalt,labelsSalt,Salt,count,time);
                            addEntryAndLabel(entriesH2S,labelsH2S,H2S,count,time);
                            addEntryAndLabel(entriesH2SMax,labelsH2SMax,SulfideMax,count,time);
                            addEntryAndLabel(entriesH2SMin,labelsH2SMin,SulfideMin,count,time);
                            addEntryAndLabel(entriesNH3,labelsNH3,NH3,count,time);
                            addEntryAndLabel(entriesNH4Max,labelsNH4Max,NH4Max,count,time);
                            addEntryAndLabel(entriesNH4Min,labelsNH4Min,NH4Min,count,time);
                            addEntryAndLabel(entriesNO2Max,labelsNO2Max,NO2Max,count,time);
                            addEntryAndLabel(entriesNO2Min,labelsNO2Min,NO2Min,count,time);
                            addEntryAndLabel(entriesOxy,labelsOxy,Oxy,count,time);
                            addEntryAndLabel(entriesTemp,labelsTemp,Temp,count,time);


//                            System.out.println("size graph:"+listGraph.size());
                            System.out.println("cout:"+count);

                            adapter=new GraphAdapter(listGraph);
                            adapter.notifyDataSetChanged();
                            rvBieuDoThongKe.setAdapter(adapter);

                            Datapackage datapackage = new Datapackage(-1, -1, Time_Package, PH, Salt, Oxy, Temp, H2S, NH3, NH4Min, NH4Max, NO2Min, NO2Max, SulfideMin, SulfideMax,NgayTao);
                            updateView(datapackage);
                            count++;

                            //get settings of data
                            getPreferences();
                            //check current data and data on settings
                            checkParameter(PH,PH_Min,PH_Max);
                            checkParameter(Temp,Temp_Min,Temp_Max);
                            checkParameter(Oxy,Oxy_Min,Oxy_Max);
                            checkParameter(Salt,Salt_Min,Salt_Max);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //add Entry and label into a graph
    private void addEntryAndLabel(ArrayList<Entry> entries, ArrayList<String> labels, double value, int index, String time){

        //System.out.println("Contained " + time + " : " + labels.contains("time"));
            entries.add(new Entry(index,(float) value));
            labels.add(time);
    }

    //show warining about out of range data
    private void showWarning(){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Cảnh báo")
                .setContentText("Thông số bị vượt ngưỡng lúc "+time+"!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setSound(alarmSound);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(100,mBuilder.build());

    }

    //get data on settings
    private void getPreferences(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        PH_Max =  preferences.getFloat(SettingsActivity.KEY_PH_MAX, Constant.DEFAULT_PH_MAX);
        PH_Min =  preferences.getFloat(SettingsActivity.KEY_PH_MIN, Constant.DEFAULT_PH_MIN);

        Temp_Max =  preferences.getFloat(SettingsActivity.KEY_TEMP_MAX, Constant.DEFAULT_TEMP_MAX);
        Temp_Min =  preferences.getFloat(SettingsActivity.KEY_TEMP_MIN, Constant.DEFAULT_TEMP_MIN);

        Salt_Max =  preferences.getFloat(SettingsActivity.KEY_SALT_MAX, Constant.DEFAULT_SALT_MAX);
        Salt_Min =  preferences.getFloat(SettingsActivity.KEY_SALT_MIN, Constant.DEFAULT_SALT_MIN);

        Oxy_Max =  preferences.getFloat(SettingsActivity.KEY_OXY_MAX, Constant.DEFAULT_OXY_MAX);
        Oxy_Min =  preferences.getFloat(SettingsActivity.KEY_OXY_MIN, Constant.DEFAULT_OXY_MIN);

        H2S_Max =  preferences.getFloat(SettingsActivity.KEY_H2S_MAX, Constant.DEFAULT_H2S_MAX);
        H2S_Min =  preferences.getFloat(SettingsActivity.KEY_H2S_MIN, Constant.DEFAULT_H2S_MIN);
        NH4_Max =  preferences.getFloat(SettingsActivity.KEY_NH4_MAX, Constant.DEFAULT_NH4_MAX);
        NH4_Min =  preferences.getFloat(SettingsActivity.KEY_NH4_MIN, Constant.DEFAULT_NH4_MIN);
        NO2_Max =  preferences.getFloat(SettingsActivity.KEY_NO2_MAX, Constant.DEFAULT_NO2_MAX);
        NO2_Min =  preferences.getFloat(SettingsActivity.KEY_NO2_MIN, Constant.DEFAULT_NO2_MIN);
    }

    //check limitted of parameter. if it out of range => show warning
    private void checkParameter(double parameter, double min, double max){
        if (parameter < min || parameter > max){
            //showWarning();
        }
    }


}
