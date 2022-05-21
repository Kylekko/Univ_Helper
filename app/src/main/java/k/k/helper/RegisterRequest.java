package k.k.helper;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import k.k.helper.SHA256;


public class RegisterRequest extends StringRequest {

    final static private String URL = "http://helper.dothome.co.kr/Register.php";
    private Map<String, String> map;

    SHA256 sha256 = new SHA256();

    public RegisterRequest(String id, String pw, String name, Response.Listener<String> listener) throws NoSuchAlgorithmException {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("id", id);
        map.put("pw", sha256.encrypt(pw));
        map.put("name", name);
    }

    @Override
    protected Map<String, String>getParams() throws AuthFailureError {
        return map;
    }
}