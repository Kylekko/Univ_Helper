package k.k.helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class Login extends AppCompatActivity {

    private EditText editId, editPw;
    private Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_login);

        editId = findViewById(R.id.id);
        editPw = findViewById(R.id.pw);
        loginbtn = findViewById(R.id.login);

        loginbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View V) {
                String id = editId.getText().toString();
                String pw = editPw.getText().toString();

                if (id.equals("") || pw.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    AlertDialog dialog = builder.setMessage("아이디와 비밀번호는 공백일 수 없습니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if(success) {
                                String name = jsonObject.getString("name");
                                String id = jsonObject.getString("id");
                                String pw = jsonObject.getString("pw");

                                Toast.makeText(getApplicationContext(), String.format("%s님 환영합니다.", name), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity_Login.class);

                                intent.putExtra("name", name);
                                intent.putExtra("id", id);
                                intent.putExtra("pw", pw);

                                startActivity(intent);
                                finish(); // 인텐트 종료
                            } else {
                                Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = null;
                try {
                    loginRequest = new LoginRequest(id, pw, responseListener);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);
            }
        });
    }

    public void goSignup(View v) {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
        finish(); // 인텐트 종료
        overridePendingTransition(0, 0); //페이지 넘김 이벤트 삭제
    }
}
