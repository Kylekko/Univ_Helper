package k.k.helper;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import k.k.helper.SHA256;

public class LoginRequest extends StringRequest {

    final static private String URL = "http://helper.dothome.co.kr/Login.php";
    private Map<String, String> map;

    SHA256 sha256 = new SHA256();

    public LoginRequest(String id, String pw, Response.Listener<String> listener) throws NoSuchAlgorithmException {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id", id);
        map.put("pw", sha256.encrypt(pw));
    }

    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}