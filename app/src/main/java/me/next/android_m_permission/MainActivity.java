package me.next.android_m_permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ANDROID_M_PERMISSION";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = (Button) findViewById(R.id.button_check_permission);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查当前 App 是否授予了对应的权限（传入的 Context 为当前 Activity 的 Context）
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestContactsPermissions();
                } else {
                    //TODO some method
                    doSomeThing();
                }
            }
        });
    }

    private void doSomeThing() {
        Snackbar.make(button, "以获取到相应权限，可以直接执行对应方法了~", Snackbar.LENGTH_INDEFINITE).show();
    }

    //请求权限
    private void requestContactsPermissions() {
        // 如果权限已经被拒绝，先解释权限的原因，再提示用户提供对应权限
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_CONTACTS) ||
                ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.WRITE_CONTACTS)) {
            Snackbar.make(button, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_CONTACT,
                                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // 无需向用户界面提示，直接请求权限
            ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_CONTACT,
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
//          判断获取单条权限
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 如果请求被取消， grantResults 列表总是为空
            if (PermissionUtil.verifyPermissions(grantResults)) {
                //请求权限通过
                Toast.makeText(MainActivity.this, "获取权限成功", Toast.LENGTH_SHORT).show();
                //TODO 调用方法
                doSomeThing();
            } else {
                //请求权限被拒
                Toast.makeText(MainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
