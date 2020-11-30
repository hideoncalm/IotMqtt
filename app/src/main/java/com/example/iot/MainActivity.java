package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.tabs.TabLayout;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private static String URI = "tcp://broker.hivemq.com:1883";
    private static String USER_NAME = "quyenhaha";
    private static String PASSWORD = "quyenhaha";
    private static String TOPIC_PUB = "PubTopic";
    private static String TOPIC_SUB = "SubTopic";
    private static String TAG = "tag";
    private MqttAndroidClient client;


    private ImageView imageViewLamp1;
    private Button btnOnLamp1;
    private Button btnOffLamp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToMqtt();
        intit();

        btnOffLamp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewLamp1.setBackgroundColor(Color.GRAY);
                String payload = "OFF";
                publish(TOPIC_PUB, false, payload);
            }
        });
        btnOnLamp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewLamp1.setBackgroundColor(Color.GREEN);
                String payload = "ON";
                publish(TOPIC_PUB, false, payload);
            }
        });
    }


    private void intit() {

        imageViewLamp1 = findViewById(R.id.ImgLamp1);
        btnOffLamp1 = findViewById(R.id.btnLamp1OFF);
        btnOnLamp1 = findViewById(R.id.btnLamp1ON);
    }


    private void connectToMqtt() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), URI, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USER_NAME);
        options.setPassword(PASSWORD.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    subscribesToMqtt(TOPIC_SUB, 0);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribesToMqtt(String topicSub, int qos) {
        try {
            client.subscribe(topicSub, qos);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, topicSub + ":  " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(String topicPub, boolean retain, String payload) {
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(retain);
            client.publish(topicPub, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

}  // end class