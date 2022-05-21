package k.k.helper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class SignUp extends AppCompatActivity {

    private EditText Name, Id, Pw, Pwck;
    private RadioGroup radioGroup;
    private RadioButton student, general;
    private String RadioValues;
    private Button sign_up;
    private AlertDialog dialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_sign_in);

        Name = findViewById(R.id.name);
        Id = findViewById(R.id.id);
        Pw = findViewById(R.id.pw);
        Pwck = findViewById(R.id.pwck);

        radioGroup = findViewById(R.id.radioGroup);
        student = findViewById(R.id.studentChecked);
        general = findViewById(R.id.generalChecked);

        sign_up = findViewById(R.id.signup);

        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View V) {
                final String name = Name.getText().toString();
                final String id = Id.getText().toString();
                final String pw = Pw.getText().toString();
                final String pwck = Pwck.getText().toString();

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if(i == R.id.studentChecked) {
                            RadioValues = student.getText().toString();
                        } else {
                            RadioValues = general.getText().toString();
                        }
                    }
                });


                if (name.equals("") || id.equals("") || pw.equals("") || pwck.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    dialog = builder.setMessage("빈칸이 있습니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (pw.equals(pwck)) {
                                if (success) {
                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", name), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish(); // 인텐트 종료
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                dialog = builder.setMessage("비밀번호를 확인해 주세요.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = null;
                try {
                    registerRequest = new RegisterRequest(id, pw, name, responseListener);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                RequestQueue queue = Volley.newRequestQueue(SignUp.this);
                queue.add(registerRequest);
            }
        });
    }

    public void goLogin(View v) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish(); // 인텐트 종료
        overridePendingTransition(0, 0); //페이지 넘김 이벤트 삭제
    }
}
