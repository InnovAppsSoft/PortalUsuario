package com.marlon.portalusuario.blueChat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.marlon.portalusuario.R

class ActivityChat : AppCompatActivity(), DevicesRecyclerViewAdapter.ItemClickListener,
        ChatFragment.CommunicationListener {

    private val REQUEST_ENABLE_BT = 123
    private val TAG = javaClass.simpleName
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewPaired: RecyclerView
    private val mDeviceList = arrayListOf<DeviceData>()
    private lateinit var devicesAdapter: DevicesRecyclerViewAdapter
    private var mBtAdapter: BluetoothAdapter? = null
    private val PERMISSION_REQUEST_LOCATION = 123
    private val PERMISSION_REQUEST_LOCATION_KEY = "PERMISSION_REQUEST_LOCATION"
    private var alreadyAskedForPermission = false
    private lateinit var headerLabel: TextView
    private lateinit var headerLabelPaired: TextView
    private lateinit var headerLabelContainer: LinearLayout
    private lateinit var status: TextView
    private lateinit var connectionDot: ImageView
    private lateinit var  mConnectedDeviceName: String
    private var connected: Boolean = false

    private var mChatService: BluetoothChatService? = null
    private lateinit var chatFragment: ChatFragment


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbarTitle = findViewById<TextView>(R.id.toolbarTitle)

        val typeFace = Typeface.createFromAsset(assets, "fonts/product_sans.ttf")
        toolbarTitle.typeface = typeFace

        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewPaired = findViewById(R.id.recyclerViewPaired)
        headerLabel = findViewById(R.id.headerLabel)
        headerLabelPaired = findViewById(R.id.headerLabelPaired)
        headerLabelContainer = findViewById(R.id.headerLabelContainer)
        status = findViewById(R.id.status)
        connectionDot = findViewById(R.id.connectionDot)

        status.text = getString(R.string.bluetooth_not_enabled)

        headerLabelContainer.visibility = View.INVISIBLE

        if (savedInstanceState != null)
            alreadyAskedForPermission = savedInstanceState.getBoolean(PERMISSION_REQUEST_LOCATION_KEY, false)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewPaired.layoutManager = LinearLayoutManager(this)

        recyclerView.isNestedScrollingEnabled = false
        recyclerViewPaired.isNestedScrollingEnabled = false

        findViewById<Button>(R.id.search_devices).setOnClickListener {
            findDevices()
        }

        findViewById<Button>(R.id.make_visible).setOnClickListener {
            makeVisible()
        }

        devicesAdapter = DevicesRecyclerViewAdapter(context = this, mDeviceList = mDeviceList)
        recyclerView.adapter = devicesAdapter
        devicesAdapter.setItemClickListener(this)

        // Register for broadcasts when a device is discovered.
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)

        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mReceiver, filter)

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = BluetoothChatService(this, mHandler)

        if (mBtAdapter == null)
            showAlertAndExit()
        else {

            if (mBtAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                status.text = getString(R.string.not_connected)
            }

            // Get a set of currently paired devices
            val pairedDevices = mBtAdapter?.bondedDevices
            val mPairedDeviceList = arrayListOf<DeviceData>()

            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices?.size ?: 0 > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (device in pairedDevices!!) {
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mPairedDeviceList.add(DeviceData(deviceName,deviceHardwareAddress))
                }

                val devicesAdapter = DevicesRecyclerViewAdapter(context = this, mDeviceList = mPairedDeviceList)
                recyclerViewPaired.adapter = devicesAdapter
                devicesAdapter.setItemClickListener(this)
                headerLabelPaired.visibility = View.VISIBLE

            }
        }

        //showChatFragment()

    }


    @SuppressLint("MissingPermission")
    private fun makeVisible() {

        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        startActivity(discoverableIntent)

    }

    private fun checkPermissions() {

        if (alreadyAskedForPermission) {
            // don't check again because the dialog is still open
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.need_loc_access))
                builder.setMessage(getString(R.string.please_grant_loc_access))
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    // the dialog will be opened so we have to save that
                    alreadyAskedForPermission = true
                    requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_REQUEST_LOCATION)
                }
                builder.show()

            } else {

                startDiscovery()
            }
        } else {
            startDiscovery()
            alreadyAskedForPermission = true
        }

    }

    private fun showAlertAndExit() {

        AlertDialog.Builder(this)
                .setTitle(getString(R.string.not_compatible))
                .setMessage(getString(R.string.no_support))
                .setPositiveButton("Exit", { _, _ -> System.exit(0) })
                .show()
    }

    private fun findDevices() {

        checkPermissions()
    }


    @SuppressLint("MissingPermission")
    private fun startDiscovery() {

        headerLabelContainer.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        headerLabel.text = getString(R.string.searching)
        mDeviceList.clear()

        if (mBtAdapter?.isDiscovering ?: false)
            mBtAdapter?.cancelDiscovery()

        // Request discover from BluetoothAdapter
        mBtAdapter?.startDiscovery()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val mReceiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device!!.name
                val deviceHardwareAddress = device.address // MAC address

                val deviceData = DeviceData(deviceName, deviceHardwareAddress)
                mDeviceList.add(deviceData)

                val setList = HashSet<DeviceData>(mDeviceList)
                mDeviceList.clear()
                mDeviceList.addAll(setList)

                devicesAdapter.notifyDataSetChanged()
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                progressBar.visibility = View.INVISIBLE
                headerLabel.text = getString(R.string.found)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressBar.visibility = View.INVISIBLE

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //Bluetooth is now connected.
            status.text = getString(R.string.not_connected)

            // Get a set of currently paired devices
            val pairedDevices = mBtAdapter?.bondedDevices
            val mPairedDeviceList = arrayListOf<DeviceData>()

            mPairedDeviceList.clear()

            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices?.size ?: 0 > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (device in pairedDevices!!) {
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mPairedDeviceList.add(DeviceData(deviceName,deviceHardwareAddress))
                }

                val devicesAdapter = DevicesRecyclerViewAdapter(context = this, mDeviceList = mPairedDeviceList)
                recyclerViewPaired.adapter = devicesAdapter
                devicesAdapter.setItemClickListener(this)
                headerLabelPaired.visibility = View.VISIBLE

            }

        }
        //label.setText("Bluetooth is now enabled.")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(PERMISSION_REQUEST_LOCATION_KEY, alreadyAskedForPermission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            PERMISSION_REQUEST_LOCATION -> {
                // the request returned a result so the dialog is closed
                alreadyAskedForPermission = false
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Log.d(TAG, "Coarse and fine location permissions granted")
                    startDiscovery()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.fun_limted))
                        builder.setMessage(getString(R.string.since_perm_not_granted))
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.show()
                    }
                }
            }
        }
    }

    override fun itemClicked(deviceData: DeviceData) {
        connectDevice(deviceData)
    }


    private fun connectDevice(deviceData: DeviceData) {

        // Cancel discovery because it's costly and we're about to connect
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mBtAdapter?.cancelDiscovery()
        val deviceAddress = deviceData.deviceHardwareAddress

        val device = mBtAdapter?.getRemoteDevice(deviceAddress)

        status.text = getString(R.string.connecting)
        connectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connecting))

        // Attempt to connect to the device
        mChatService?.connect(device, true)

    }

    override fun onResume() {
        super.onResume()
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService?.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService?.start()
            }
        }

        if(connected)
            showChatFragment()

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {

                Constants.MESSAGE_STATE_CHANGE -> {

                    when (msg.arg1) {

                        BluetoothChatService.STATE_CONNECTED -> {

                            status.text = getString(R.string.connected_to) + " "+ mConnectedDeviceName
                            connectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
                            Snackbar.make(findViewById(R.id.mainScreen),"Connected to " + mConnectedDeviceName,Snackbar.LENGTH_SHORT).show()
                            //mConversationArrayAdapter.clear()
                            connected = true
                        }

                        BluetoothChatService.STATE_CONNECTING -> {
                            status.text = getString(R.string.connecting)
                            connectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connecting))
                            connected = false
                        }

                        BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                            status.text = getString(R.string.not_connected)
                            connectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_red))
                            Snackbar.make(findViewById(R.id.mainScreen),getString(R.string.not_connected),Snackbar.LENGTH_SHORT).show()
                            connected = false
                        }
                    }
                }

                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)
                    //Toast.makeText(this@MainActivity,"Me: $writeMessage",Toast.LENGTH_SHORT).show()
                    //mConversationArrayAdapter.add("Me:  " + writeMessage)
                    val milliSecondsTime = System.currentTimeMillis()
                    chatFragment.communicate(Message(writeMessage,milliSecondsTime,Constants.MESSAGE_TYPE_SENT))

                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)
                    val milliSecondsTime = System.currentTimeMillis()
                    //Toast.makeText(this@MainActivity,"$mConnectedDeviceName : $readMessage",Toast.LENGTH_SHORT).show()
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage)
                    chatFragment.communicate(Message(readMessage,milliSecondsTime,Constants.MESSAGE_TYPE_RECEIVED))
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME).toString()
                    status.text = getString(R.string.connected_to) + " " +mConnectedDeviceName
                    connectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_connected))
                    Snackbar.make(findViewById(R.id.mainScreen),"Connected to " + mConnectedDeviceName,Snackbar.LENGTH_SHORT).show()
                    connected = true
                    showChatFragment()
                }
                Constants.MESSAGE_TOAST -> {
                    status.text = getString(R.string.not_connected)
                    connectionDot.setImageDrawable(getDrawable(R.drawable.ic_circle_red))
                    msg.data.getString(Constants.TOAST)
                        ?.let { Snackbar.make(findViewById(R.id.mainScreen), it,Snackbar.LENGTH_SHORT).show() }
                    connected = false
                  }
            }
        }
    }


    private fun sendMessage(message: String) {

        // Check that we're actually connected before trying anything
        if (mChatService?.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return
        }

        // Check that there's actually something to send
        if (message.isNotEmpty()) {
            // Get the message bytes and tell the BluetoothChatService to write
            val send = message.toByteArray()
            mChatService?.write(send)

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0)
            //mOutEditText.setText(mOutStringBuffer)
        }
    }

    private fun showChatFragment() {

        if(!isFinishing) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            chatFragment = ChatFragment.newInstance()
            chatFragment.setCommunicationListener(this)
            fragmentTransaction.replace(R.id.mainScreen, chatFragment, "ChatFragment")
            fragmentTransaction.addToBackStack("ChatFragment")
            fragmentTransaction.commit()
        }
    }

    override fun onCommunication(message: String) {
           sendMessage(message)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0)
            super.onBackPressed()
        else
            supportFragmentManager.popBackStack()
    }

}
