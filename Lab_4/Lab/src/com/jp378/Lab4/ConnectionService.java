package com.jp378.Lab4;

import java.util.Date;
import java.util.UUID;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.util.SparseArray;


public class ConnectionService extends IntentService implements BluetoothAdapter.LeScanCallback{
//variables declaration
	
	private static final String TAG = "BluetoothGattActivity";

    private static final String DEVICE_NAME = "SensorTag";

    /* Humidity Service */
    private static final UUID HUMIDITY_SERVICE = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_DATA_CHAR = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
    private static final UUID HUMIDITY_CONFIG_CHAR = UUID.fromString("f000aa22-0451-4000-b000-000000000000");
    /* Barometric Pressure Service */
    private static final UUID PRESSURE_SERVICE = UUID.fromString("f000aa40-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_DATA_CHAR = UUID.fromString("f000aa41-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_CONFIG_CHAR = UUID.fromString("f000aa42-0451-4000-b000-000000000000");
    private static final UUID PRESSURE_CAL_CHAR = UUID.fromString("f000aa43-0451-4000-b000-000000000000");
    /* Acceleromter configuration servcie */
    private static final UUID ACCELEROMETER_SERVICE = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_DATA_CHAR = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_CONFIG_CHAR = UUID.fromString("f000aa12-0451-4000-b000-000000000000");
    private static final UUID ACCELEROMETER_PERIOD_CHAR = UUID.fromString("f000aa13-0451-4000-b000-000000000000");

    /* Gyroscope Configuration service */
    private static final UUID GYROSCOPE_SERVICE = UUID.fromString("f000aa50-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_DATA_CHAR = UUID.fromString("f000aa51-0451-4000-b000-000000000000");
    private static final UUID GYROSCOPE_CONFIG_CHAR = UUID.fromString("f000aa52-0451-4000-b000-000000000000");
    /* Client Configuration Descriptor */
    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private BluetoothAdapter mBluetoothAdapter;
    private SparseArray<BluetoothDevice> mDevices;

    private BluetoothGatt mConnectedGatt;
   

	
// variable declaration end
	
	public ConnectionService() {
		super("ConnectionService");
	}

	@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
			
			BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
	        mBluetoothAdapter = manager.getAdapter();
	        mDevices = new SparseArray<BluetoothDevice>();
	        /*
	         * A progress dialog will be needed while the connection process is
	         * taking place
	         */
	      /* 
	        Thread timer = new Thread(){
				public void run(){
					try{
						sleep(2000);
						
					}catch(InterruptedException e){
						e.printStackTrace();
					}finally{
						stopSelf();
					}
				}
				
	        };
	        timer.start();*/
	        
		}
	
	
	
		
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("ok", "hanldeIntent");
		startScan();
		
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
		// TODO Auto-generated method stub
		Log.i("scan", "in onlescan method");
		Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
        /*
         * We are looking for SensorTag devices only, so validate the name
         * that each device reports before adding it to our collection
         */
        if (DEVICE_NAME.equals(device.getName())) {
        
            mDevices.put(device.hashCode(), device);
            mConnectedGatt = device.connectGatt(this, false, mGattCallback);
           // mBluetoothAdapter.stopLeScan(this);
            //Update the overflow menu
            //invalidateOptionsMenu();
        }
	}

    private void startScan() {
    	Log.i("start", "scan");
        if (mBluetoothAdapter.startLeScan(this)) {
			Log.i("scan", "started");
		}
        else{
        	Log.i("scan", "not started");
        }
        

        //mHandler.postDelayed(mStopRunnable, 2500);
    }
	/*
     * In this callback, we've created a bit of a state machine to enforce that only
     * one characteristic be read or written at a time until all of our sensors
     * are enabled and we are registered to get notifications.
     */
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /* State Machine Tracking */
        private int mState = 0;

        private void reset() { mState = 0; }

        private void advance() { mState++; }

        /*
         * Send an enable command to each sensor by writing a configuration
         * characteristic.  This is specific to the SensorTag to keep power
         * low by disabling sensors you aren't using.
         */
        private void enableNextSensor(BluetoothGatt gatt) {
        	Log.i("sensor", "enable");
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                /*case 0:
                    Log.d(TAG, "Enabling pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x02});
                    break;*/
                /*case 0:
                    Log.d(TAG, "Enabling pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;*/
                
                case 0:
                    Log.d(TAG,"Enabling accelerometer");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;
                case 1:
                    Log.d(TAG,"Enabling accelerometer");
                    characteristic= gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                    characteristic.setValue(new byte[]{(byte)10});
                    break;
                case 2:
                    Log.d(TAG, "Enabling humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_CONFIG_CHAR);
                    characteristic.setValue(new byte[] {0x01});
                    break;
                case 3:
                    Log.d(TAG,"Enabling config gyroscope");
                    characteristic= gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x07});
                    break;
               /* case 3:
                    Log.d(TAG,"Enabling config gyroscope");
                    characteristic= gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;*/
              /*  case 3:
                    Log.d(TAG,"Enabling gyroscope");
                    characteristic= gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_DATA_CHAR);
                    characteristic.setValue(new byte[]{0x01});
                    break;*/
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 1");
                    return;
            }

            gatt.writeCharacteristic(characteristic);
        }
        
        private void readNextSensor(BluetoothGatt gatt) {
            BluetoothGattCharacteristic characteristic;
            switch (mState) {
                /*case 0:
                    Log.d(TAG, "Reading pressure cal");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_CAL_CHAR);
                    break;*/
                /*case 0:
                    Log.d(TAG, "Reading pressure");
                    characteristic = gatt.getService(PRESSURE_SERVICE)
                            .getCharacteristic(PRESSURE_DATA_CHAR);
                    break;*/
                case 0:
                    Log.d(TAG,"Reading accelerometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_DATA_CHAR);
                    break;
                    
                case 1:
                    Log.d(TAG, "Reading humidity");
                    characteristic = gatt.getService(HUMIDITY_SERVICE)
                            .getCharacteristic(HUMIDITY_DATA_CHAR);
                    break;
                    
                case 2:
                    Log.d(TAG,"Reading Gyroscope");
                    characteristic = gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_DATA_CHAR);
                    break;
               /* case 1:
                    Log.d(TAG,"Reading accelrometer");
                    characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                            .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                    break;*/
                /*case 2:
                    Log.d(TAG,"Reading Gyroscope");
                    characteristic = gatt.getService(GYROSCOPE_SERVICE)
                            .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                    break;*/
                default:
                    //mHandler.sendEmptyMessage(MSG_DISMISS);
                    Log.i(TAG, "All Sensors Enabled 2");
                    return;
            }

            gatt.readCharacteristic(characteristic);
        }
       /* * Enable notification of changes on the data characteristic for each sensor
        * by writing the ENABLE_NOTIFICATION_VALUE flag to that characteristic's
        * configuration descriptor.
        */
       private void setNotifyNextSensor(BluetoothGatt gatt) {
           BluetoothGattCharacteristic characteristic;
           switch (mState) {
               /*case 0:
                   Log.d(TAG, "Set notify pressure cal");
                   characteristic = gatt.getService(PRESSURE_SERVICE)
                           .getCharacteristic(PRESSURE_CAL_CHAR);
                   break;*/
               /*case 0:
                   Log.d(TAG, "Set notify pressure");
                   characteristic = gatt.getService(PRESSURE_SERVICE)
                           .getCharacteristic(PRESSURE_DATA_CHAR);
                   break;*/
               case 0:
                   Log.d(TAG, "Set notify humidity");
                   characteristic = gatt.getService(HUMIDITY_SERVICE)
                           .getCharacteristic(HUMIDITY_DATA_CHAR);
                   break;
               case 1:
                   Log.d(TAG,"Set notify accelerometer");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                           .getCharacteristic(ACCELEROMETER_DATA_CHAR);
                   break;
               /*case 1:
                   Log.d(TAG,"Set config accelerometer");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                           .getCharacteristic(ACCELEROMETER_CONFIG_CHAR);
                   break;
               case 2:
                   Log.d(TAG,"Set period accelerometer");
                   characteristic = gatt.getService(ACCELEROMETER_SERVICE)
                           .getCharacteristic(ACCELEROMETER_PERIOD_CHAR);
                   break;*/
              /* case 3:
                   Log.d(TAG,"Config gyroscope cal");
                   characteristic = gatt.getService(GYROSCOPE_SERVICE)
                           .getCharacteristic(GYROSCOPE_CONFIG_CHAR);
                   break;*/
               case 2:
                   Log.d(TAG,"Config gyroscope data");
                   characteristic = gatt.getService(GYROSCOPE_SERVICE)
                           .getCharacteristic(GYROSCOPE_DATA_CHAR);
                   break;

               default:
                  // mHandler.sendEmptyMessage(MSG_DISMISS);
                   Log.i(TAG, "All Sensors Enabled 3");
                   return;
           }

           //Enable local notifications
           gatt.setCharacteristicNotification(characteristic, true);
           //Enabled remote notifications
           BluetoothGattDescriptor desc = characteristic.getDescriptor(CONFIG_DESCRIPTOR);
           desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
           gatt.writeDescriptor(desc);
       }
       @Override
       public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
           Log.d(TAG, "Connection State Change: "+status+" -> "+connectionState(newState));
           if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
               /*
                * Once successfully connected, we must next discover all the services on the
                * device before we can read and write their characteristics.
                */
               gatt.discoverServices();
               //mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Discovering Services..."));
           } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
               /*
                * If at any point we disconnect, send a message to clear the weather values
                * out of the UI
                */
              // mHandler.sendEmptyMessage(MSG_CLEAR);
           } else if (status != BluetoothGatt.GATT_SUCCESS) {
               /*
                * If there is a failure at any stage, simply disconnect
                */
               gatt.disconnect();
           }
       }

       @Override
       public void onServicesDiscovered(BluetoothGatt gatt, int status) {
           Log.d(TAG, "Services Discovered: "+status);
          // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Enabling Sensors..."));
           /*
            * With services discovered, we are going to reset our state machine and start
            * working through the sensors we need to enable
            */
           reset();
           enableNextSensor(gatt);
       }

       @Override
       public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
           //For each read, pass the data up to the UI thread to update the display
           if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
        	   Log.i("sensor", "accelerometer");
               //mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
           }
           if (GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_GYROSCOPE, characteristic));
        	   Log.i("sensor", "gyroscope");
           }
           if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
        	   Log.i("sensor", "humidity");
           }
           //if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
              // mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
        	//   Log.i("sensor", "Pressure");
          // }
          /* if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
           }
*/

           //After reading the initial value, next we enable notifications
           setNotifyNextSensor(gatt);
       }

       @Override
       public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
           //After writing the enable flag, next we read the initial value
           readNextSensor(gatt);
       }

       @Override
       public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
           /*
            * After notifications are enabled, all updates from the device on characteristic
            * value changes will be posted here.  Similar to read, we hand these up to the
            * UI thread to update the display.
            */
           if (HUMIDITY_DATA_CHAR.equals(characteristic.getUuid())) {
              // mHandler.sendMessage(Message.obtain(null, MSG_HUMIDITY, characteristic));
        	   updateHumidityCals(characteristic);

           }
          /* if (PRESSURE_DATA_CHAR.equals(characteristic.getUuid())) {
              // mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE, characteristic));
        	   updatePressureCals(characteristic);

           }*/
           /*if (PRESSURE_CAL_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_PRESSURE_CAL, characteristic));
           }*/
    	   if (ACCELEROMETER_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_ACCELEROMETER, characteristic));
        	   //Log.i("sensor", "acceleromter changed");
        	   updateAccelerometerCals(characteristic);
           }
           if (GYROSCOPE_DATA_CHAR.equals(characteristic.getUuid())) {
               //mHandler.sendMessage(Message.obtain(null, MSG_GYROSCOPE, characteristic));
        	   Log.i("sensor", "gyroscope changed");
        	  updateGyroValues(characteristic);
          }
          /* if (GYROSCOPE_CONFIG_CHAR.equals(characteristic.getUuid())) {
               mHandler.sendMessage(Message.obtain(null, MSG_GYROSCOPE_CAL, characteristic));
           }*/
       }

       @Override
       public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
           //Once notifications are enabled, we move to the next sensor and start over with enable
           advance();
           enableNextSensor(gatt);
       }

       @Override
       public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
           Log.d(TAG, "Remote RSSI: "+rssi);
       }

       private String connectionState(int status) {
           switch (status) {
               case BluetoothProfile.STATE_CONNECTED:
                   return "Connected";
               case BluetoothProfile.STATE_DISCONNECTED:
                   return "Disconnected";
               case BluetoothProfile.STATE_CONNECTING:
                   return "Connecting";
               case BluetoothProfile.STATE_DISCONNECTING:
                   return "Disconnecting";
               default:
                   return String.valueOf(status);
           }
       }
   };
   String gyroData;
   private void updateGyroValues(BluetoothGattCharacteristic characteristic){
       String gyroData = SensorTagData.extractGyroscopeReading(characteristic,0);
       
       
       
		
		Log.i("gyroscope",gyroData);
   }
   String Humdata;
   String humiditytemp;
   private void updateHumidityCals(BluetoothGattCharacteristic characteristic) {
       
	   
	   double HumidityData=SensorTagData.extractHumidity(characteristic);
	   double temperature=SensorTagData.extractHumAmbientTemperature(characteristic);
	   Humdata=String.valueOf(HumidityData);
	   humiditytemp=String.valueOf(temperature);
       Log.i("humidity", Humdata);
	   
   }
   /*String prs;
     private void updatePressureCals(BluetoothGattCharacteristic characteristic) {
    	 int[] c;
    	 c= SensorTagData.extractCalibrationCoefficients(characteristic);
    	 
       double PressureData=SensorTagData.extractBarometer(characteristic, c)(characteristic,c);
	   //tring prs = PressureData.toString();
        prs=String.valueOf(PressureData);
       Log.i("Pressure",prs);
	   
   }*/
   private void updateAccelerometerCals(BluetoothGattCharacteristic characteristic) {
       //if (mPressureCals == null) return;
       //double pressure = SensorTagData.extractBarometer(characteristic, mPressureCals);
       Float[] values = SensorTagData.extractAccelerometerReading(characteristic, 0);
          
       Log.i("values", "x :"+values[0].toString() + "y:"+values[1].toString()+"z:"+values[2].toString());
       
       
       LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       Criteria criteria = new Criteria();
       String provider = locationManager.getBestProvider(criteria, true);
       Location location = locationManager.getLastKnownLocation(provider);
       double latitude = location.getLatitude();
       double longitude = location.getLongitude();
     
       
       Log.i("location: ","Latitude: " + latitude + 
               " Longitude: " + longitude);
       
       
         
       new Date(); 
       
 
    /*   SaveData(string);
       Thread timer = new Thread(){
			public void run(){
				try{
					sleep(2000);
					
				}catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					stopSelf();
				}
			}
			
       };
       timer.start();
       
      */ 
   }
}




