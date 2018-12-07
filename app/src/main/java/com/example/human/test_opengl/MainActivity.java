package com.example.human.test_opengl;

//import static com.example.human.Main_Camera;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.content.pm.ActivityInfo;
import android.view.SurfaceView;


public class MainActivity extends AppCompatActivity {
    TextView text;
    Main_Camera main_camera;
    private static Context context;
    private static Activity activity;

    public native String stringFromJNI();

    static {
        System.loadLibrary("native-lib");
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        MainActivity.activity = this;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);
        }
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {


        }else{
            text.setText(stringFromJNI());

        }
        main_camera = new Main_Camera();
        text = (TextView) findViewById(R.id.sample_text);
        SurfaceView preview = (SurfaceView) findViewById(R.id.surfaceView_0);
        Button shot_button = (Button) findViewById(R.id.Button_0);
        Switch switch_button = (Switch) findViewById(R.id.switch_0);
        SeekBar Seek_bar_resolution = (SeekBar) findViewById(R.id.seekBar_0);
        main_camera.InitSurface(preview, preview.getHolder(), shot_button, switch_button, Seek_bar_resolution);

        text.setText(stringFromJNI());

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public static Activity getMainActivity() {
        return MainActivity.activity;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Восстановите состояние UI из переменной savedInstanceState.
        // Этот объект типа Bundle также был передан в метод onCreate.
    }


    @Override
    public void onRestart(){
        super.onRestart();
        // Загрузите изменения, учитывая то, что Активность
        // уже стала "видимой" в рамках данного процесса.
    }

    // Вызывается в начале "видимого" состояния.
    @Override
    public void onStart(){
        super.onStart();
        // Примените к UI все необходимые изменения, так как
        // Активность теперь видна на экране.
    }

    // Вызывается в начале "активного" состояния.
    @Override
    public void onResume(){
        super.onResume();
        main_camera.OpenCamera();
        // Возобновите все приостановленные обновления UI,
        // потоки или процессы, которые были "заморожены",
        // когда данный объект был неактивным.
    }

    // Вызывается для того, чтобы сохранить пользовательский интерфейс
    // перед выходом из "активного" состояния.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Сохраните состояние UI в переменную savedInstanceState.
        // Она будет передана в метод onCreate при закрытии и
        // повторном запуске процесса.
        super.onSaveInstanceState(savedInstanceState);
    }

    // Вызывается перед выходом из "активного" состояния
    @Override
    public void onPause(){
        // "Замораживает" пользовательский интерфейс, потоки
        // или трудоемкие процессы, которые могут не обновляться,
        // пока Активность не находится на переднем плане.
        super.onPause();
        main_camera.CloseCamera();
    }

    // Вызывается перед тем, как Активность перестает быть "видимой".
    @Override
    public void onStop(){
        // "Замораживает" пользовательский интерфейс, потоки
        // или операции, которые могут подождать, пока Активность
        // не отображается на экране. Сохраняйте все введенные
        // данные и изменения в UI так, как будто после вызова
        // этого метода процесс должен быть закрыт.
        super.onStop();
    }

    // Вызывается перед выходом из "полноценного" состояния.
    @Override
    public void onDestroy(){
        // Очистите все ресурсы. Это касается завершения работы
        // потоков, закрытия соединений с базой данных и т. д.
        super.onDestroy();
    }

}
