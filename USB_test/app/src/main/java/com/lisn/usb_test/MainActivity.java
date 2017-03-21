package com.lisn.usb_test;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";   //记录标识
    private Button btsend;      //发送按钮
    private UsbManager manager;   //USB管理器
    private UsbDevice mUsbDevice;  //找到的USB设备
    private ListView lsv1;         //显示USB信息的
    private UsbInterface mInterface;
    private UsbDeviceConnection mDeviceConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btsend = (Button) findViewById(R.id.btsend);

        btsend.setOnClickListener(btsendListener);

        lsv1 = (ListView) findViewById(R.id.lsv1);
        // 获取USB设备
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            return;
        } else {
            Log.i(TAG, "usb设备：" + String.valueOf(manager.toString()));
        }
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Log.i(TAG, "usb设备：" + String.valueOf(deviceList.size()));
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        ArrayList<String> USBDeviceList = new ArrayList<String>(); // 存放USB设备的数量
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            USBDeviceList.add(String.valueOf(device.getVendorId()));
            USBDeviceList.add(String.valueOf(device.getProductId()));

            // 在这里添加处理设备的代码
//            if (device.getVendorId() == 1155 && device.getProductId() == 22352) {
            if (device.getVendorId() == 4817 && device.getProductId() == 4178) {
                mUsbDevice = device;
                Log.i(TAG, "找到设备");
            }
        }
        // 创建一个ArrayAdapter
        lsv1.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, USBDeviceList));
        findIntfAndEpt();

    }

    private byte[] Sendbytes;    //发送信息字节
    private byte[] Receiveytes;  //接收信息字节
    private OnClickListener btsendListener = new OnClickListener() {
        int ret = -100;
        @Override
        public void onClick(View v) {
            /*
             * 请注意，本模块通信的内容使用的协议是HID读卡器协议，不会和大家手上的设备一样
             * 请大家在测试时参考自己手上的设备资料，严格按照HID标准执行发送和接收数据
             * 我的范例使用的设备是广州微云电子的WY-M1RW-01非接触式读卡器，outputreport是64，因此我发送的字节长度是64
             * 我发送的字节内容是要求读卡器蜂鸣器响两短一长
             */
            String testString = "90000CB20301F401F401F401F407D447FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

            Sendbytes = clsPublic.hexStringToBytes(testString);

            // 1,发送准备命令
            ret = mDeviceConnection.bulkTransfer(epOut, Sendbytes, Sendbytes.length, 5000);
            Log.i(TAG,"已经发送!");

            // 2,接收发送成功信息
            Receiveytes=new byte[64];     //这里的64是设备定义的，不是我随便乱写，大家要根据设备而定
            ret = mDeviceConnection.bulkTransfer(epIn, Receiveytes, Receiveytes.length, 10000);
            Log.i(TAG,"接收返回值:" + String.valueOf(ret));
            if(ret != 64) {
                DisplayToast("接收返回值"+String.valueOf(ret));
                return;
            }
            else {
                //查看返回值
                DisplayToast(clsPublic.bytesToHexString(Receiveytes));
                Log.i(TAG,clsPublic.bytesToHexString(Receiveytes));
            }
        }
    };

    // 显示提示的函数，这样可以省事，哈哈
    public void DisplayToast(CharSequence str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        // 设置Toast显示的位置
        toast.setGravity(Gravity.TOP, 0, 200);
        // 显示Toast
        toast.show();
    }

    // 寻找接口和分配结点
    private void findIntfAndEpt() {
        if (mUsbDevice == null) {
            Log.i(TAG,"没有找到设备");
            return;
        }
        for (int i = 0; i < mUsbDevice.getInterfaceCount();) {
            // 获取设备接口，一般都是一个接口，你可以打印getInterfaceCount()方法查看接
            // 口的个数，在这个接口上有两个端点，OUT 和 IN 
            UsbInterface intf = mUsbDevice.getInterface(i);
            Log.d(TAG, i + " " + intf);
            mInterface = intf;
            break;
        }

        if (mInterface != null) {
            UsbDeviceConnection connection = null;
            // 判断是否有权限
            if(manager.hasPermission(mUsbDevice)) {
                // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
                connection = manager.openDevice(mUsbDevice);
                if (connection == null) {
                    return;
                }
                if (connection.claimInterface(mInterface, true)) {
                    Log.i(TAG,"找到接口");
                    mDeviceConnection = connection;
                    //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
                    getEndpoint(mDeviceConnection,mInterface);
                } else {
                    connection.close();
                }
            } else {
                Log.i(TAG,"没有权限");
            }
        }
        else {
            Log.i(TAG,"没有找到接口");
        }
    }


    private UsbEndpoint epOut;
    private UsbEndpoint epIn;
    //用UsbDeviceConnection 与 UsbInterface 进行端点设置和通讯
    private void getEndpoint(UsbDeviceConnection connection, UsbInterface intf) {
        if (intf.getEndpoint(1) != null) {
            epOut = intf.getEndpoint(1);
        }
        if (intf.getEndpoint(0) != null) {
            epIn = intf.getEndpoint(0);
        }
    }


}