package com.example.day8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnAddress, btnSpeech, btnPhone;
    TextView txtResult;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddress = findViewById(R.id.btnAddress);
        btnSpeech = findViewById(R.id.btnSpeech);
        btnPhone = findViewById(R.id.btnPhone);
        txtResult = findViewById(R.id.txtResult);
        imageView = findViewById(R.id.imageView);

        btnAddress.setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK).setData(ContactsContract.Contacts.CONTENT_URI), 1001);
        });

        imageView.setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 1002); // * : 이미지는 모두 다 찾아오겠다는 뜻
        });

        btnSpeech.setOnClickListener(v -> {
            startActivityForResult(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    .putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech Recognition"), 1003);
        });

        btnPhone.setOnClickListener(v -> callPhone());
    }

    private void callPhone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)   // 권환 확인
        {
            // 허가가 안 되었을 때 (Permission is not granted)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
        }else
        {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:01034845622")));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001)
        {
            try {
                txtResult.setText(data.getDataString());
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(data.getDataString())));
            }catch (Exception e) {}
        }
        else if (requestCode == 1002)
        {
            try {
                InputStream i = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(i);
                i.close();
                imageView.setImageBitmap(img);
            }catch (Exception ignored) {}
        }
        else if (requestCode == 1003)
        {
            try {
                ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtResult.setText(results.get(0));
            }catch (Exception e) {}
        }
    }

    @Override   // CALL_PHONE 사용자가 허가를 했는지
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                callPhone();
        }
    }
}
