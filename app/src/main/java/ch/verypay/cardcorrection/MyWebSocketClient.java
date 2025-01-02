package ch.verypay.cardcorrection;

import android.nfc.tech.IsoDep;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

public class MyWebSocketClient  {
    long startTime = System.currentTimeMillis();
   static Map<String, String> httpHeaders = new HashMap<String, String>() ;

    static  {
        httpHeaders.put("X-ClientId", "Airtel");
    }
    IsoDep isoDep;
    private WebSocket webSocket;
    private OkHttpClient client;

    public void send(String text) {
        webSocket.send(text);
    }
    private Consumer<String> consumer;
    public MyWebSocketClient(IsoDep isoDep, Consumer<String> consumer) {
        this.isoDep = isoDep;
     this.consumer= consumer;
        // Set up logging interceptor for debugging (optional)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Request request = new Request.Builder()
                .header("X-ClientId","Airtel")
                .url("wss://api-uat.verypay.ch/card-service/card-correction") // Replace with your WebSocket server URL
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String data) {
                System.out.println("Received message: " + data);


                consumer.accept("Received message: " + data);

                if(data.toString().startsWith("9000")
                        || data.toString().startsWith("9001")
                        ||data.toString().startsWith("9002")
                        || "OK".equalsIgnoreCase(data.toString())) {
                    long endTime = System.currentTimeMillis();
                    consumer.accept("EndTime elapsed:" + (endTime - startTime) + " ms");

                    System.out.println("EndTime elapsed:" + (endTime - startTime) );

                } else {
                    try {
                        byte[] response = isoDep.transceive(ByteUtils.fromHexString(data));
                        String cardResponse =  ByteUtils.bytesToHexStringNoSpace(response);
                        consumer.accept("\n Card response: " +  cardResponse);
                        System.out.printf(">>>>" + cardResponse);
                        webSocket.send(ByteUtils.bytesToHexStringNoSpace(response));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                super.onOpen(webSocket, response);
            }
        });

    }
}