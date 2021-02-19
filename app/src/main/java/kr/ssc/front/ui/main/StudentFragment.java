package kr.ssc.front.ui.main;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.transition.Hold;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.ssc.front.HolderDBHelper;
import kr.ssc.front.JoinActivity;
import kr.ssc.front.MainActivity;
import kr.ssc.front.OnBackPressedListener;
import kr.ssc.front.R;
import kr.ssc.front.ui.join.Student;

// 학생증 눌렀을 경우 프래그먼트
public class StudentFragment extends Fragment implements OnBackPressedListener {
    private static final String TAG = "로그";
    private boolean first = true;
    private Student student;
    private String type;

    private View root;
    private MainActivity activity;


    private HolderDBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private String holder_id;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_student, container, false);
        activity = (MainActivity)getActivity();

        TextView tvUniversity = root.findViewById(R.id.text_main_university);
        TextView tvName = root.findViewById(R.id.text_main_name);
        TextView tvDepartment = root.findViewById(R.id.text_main_department);
        TextView tvNum = root.findViewById(R.id.text_main_num);
        TextView tvDate = root.findViewById(R.id.text_main_date);
        Button reissue = root.findViewById(R.id.btn_main_reissue);

        student = new Student();

        helper = new HolderDBHelper(getContext());

        // 학생증 조회
        parsingIssue();

        return root;
    }

    @Override
    public void onBackPressed() {
        if(first) {
            first = false;
            Toast.makeText(getActivity(), "한번 더 누르면 앱이 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            // 종료
            activity.finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
    }

    public void parsingIssue() {
        try{
            // 휴대폰 내장에 저장된 holder_id 가져오기
            cursor = db.rawQuery("select * from " + HolderDBHelper.getTableName(), null);
            cursor.moveToNext();

            holder_id = cursor.getString(cursor.getColumnIndex("holder_id"));
            cursor.close();
            helper.close();

            type = "issue";
            // http:localhost:6464/idcard/{holderId}
            new RestAPITask().execute(getResources().getString(R.string.apiaddress)+getResources().getString(R.string.idcard)+holder_id);


        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class RestAPITask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = null;

            try{
                result = downloadContents(strings[0]);
            }catch(Exception e){
                Log.e(TAG, "doInBackground: " + e.getMessage());
            }
            return result;
        }
    }

    // 주소에 접속하여 문자열 데이터를 한 후 반환
    private String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try{
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
//            stream = getNetworkConnection(conn);
//            result = readStreamToString(stream);
            if (stream != null) stream.close();

        }catch(Exception e){
            e.printStackTrace();
        } finally {
            if(conn != null) conn.disconnect();
        }
        return result;
    }
}