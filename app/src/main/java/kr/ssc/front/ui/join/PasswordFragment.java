package kr.ssc.front.ui.join;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import kr.ssc.front.HolderDBHelper;
import kr.ssc.front.JoinActivity;
import kr.ssc.front.OnBackPressedListener;
import kr.ssc.front.PreferencesSettings;
import kr.ssc.front.R;
import kr.ssc.front.ui.join.Student;

public class PasswordFragment extends Fragment implements OnBackPressedListener {

    private View root;

    private PFLockScreenFragment fragment;
    private PFFLockScreenConfiguration.Builder builder;

    private JoinActivity activity;

    private Student student;
    private String body;
    private Integer id;
    private HolderDBHelper helper;
    private SQLiteDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_password, container, false);

        activity = (JoinActivity) getActivity();

        //입력 정보 받아오기
        student = (Student) getArguments().get("student");

        helper = new HolderDBHelper(getContext());

        fragment = new PFLockScreenFragment();
        builder = new PFFLockScreenConfiguration.Builder(getContext())
                .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                .setTitle("비밀번호 6자리를 입력해주십시오")
                .setNewCodeValidation(true)
                .setNewCodeValidationTitle("비밀번호를 다시 입력해주십시오")
                .setUseFingerprint(false)
                .setCodeLength(6);

        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                //TODO: save somewhere;
                PreferencesSettings.saveToPref(getActivity(), encodedCode);
                //비밀번호까지 입력 완료 후 회원 등
                parsing();
//                Navigation.findNavController(root).navigate(R.id.action_nav_join_password_to_nav_join_success);
            }

            @Override
            public void onNewCodeValidationFailed() {
                Toast.makeText(getContext(), "비밀번호가 불일치합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.pincode_container, fragment).commit();
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(null);
    }

    @Override
    public void onBackPressed() {

    }

    //회원 가입 + 학생증 생성
    public void parsing() {
        try {
            new RestAPITask().execute(getResources().getString(R.string.apiaddress)+getResources().getString(R.string.signup));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RestAPITask extends AsyncTask<String, Void, String> {

        //수행 전
        @Override
        protected void onPreExecute() {
            try {
                JSONObject json = new JSONObject();

                json.put("name", student.getName());
                json.put("studentId", student.getStudent_id());
                json.put("university", student.getUniversity());
                json.put("department", student.getDepartment());

                body = json.toString();
                System.out.println("BODY: "+body); //log
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... Strings) {
            String result = null;

            try {
                result = downloadContents(Strings[0]);
            }
            catch (Exception e) {
                // Error calling the rest api
                Log.e("REST_API", "POST method failed: " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        //작업 완료
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            int holderId = parse(result);
            Toast.makeText(getContext(), holderId+"", Toast.LENGTH_SHORT).show();

            //스마트폰 내부 db에 holder id 저장
            db = helper.getWritableDatabase();
            ContentValues row = new ContentValues();
            row.put(HolderDBHelper.getColHolderId(), holderId);
            db.insert(HolderDBHelper.getTableName(), null, row);
            helper.close();

            Navigation.findNavController(root).navigate(R.id.action_nav_join_password_to_nav_join_success);
        }
    }

    /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    // URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("content-type", "application/json");

        writeStream(conn);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_CREATED) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }

        return conn.getInputStream();
    }

    protected void writeStream(HttpURLConnection conn) {
        try {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(body); //json 형식의 메세지 전달
            wr.flush();
            wr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
    //json parsing
    public Integer parse(String json){
        try{
            ////{"success":"true" "holderId": 45}
            JSONObject object = new JSONObject(json);
            //인증번호
            id = object.getInt("HolderId");
        } catch (JSONException e){
            e.printStackTrace();
        }
        return id;
    }

}