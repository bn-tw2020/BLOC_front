package kr.ssc.front.ui.main;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.ssc.front.HolderDBHelper;
import kr.ssc.front.MainActivity;
import kr.ssc.front.R;
import kr.ssc.front.ui.join.Student;

public class MainFragment extends Fragment {

    private static final String TAG = "로그";
    private View root;
    private MainActivity activity;
    private View layoutStudentCard;
    private View layoutCamera;
    private View layoutInfo;
    Dialog dialog;
    private Student student;

    HolderDBHelper helper = new HolderDBHelper(getContext());
    SQLiteDatabase db;
    Cursor cursor;
    private String type;
    String holder_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);
        activity = (MainActivity)getActivity();
        dialog = new Dialog(activity);

        layoutStudentCard = root.findViewById(R.id.layoutStudentCard);
        layoutStudentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "학생증을 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
//                Navigation.findNavController(root).navigate(R.id.action_nav_main_to_nav_student);
                openStudentCard();
            }
        });

        layoutCamera = root.findViewById(R.id.layoutCamera);
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "출석체크를 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });

        layoutInfo = root.findViewById(R.id.layoutInfo);
        layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "개발자정보를 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void openStudentCard() {
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvUniversity = dialog.findViewById(R.id.text_main_university);
        TextView tvName = dialog.findViewById(R.id.text_main_name);
        TextView tvDepartment = dialog.findViewById(R.id.text_main_department);
        TextView tvNum = dialog.findViewById(R.id.text_main_num);
        TextView tvDate = dialog.findViewById(R.id.text_main_date);
        Button reissue = dialog.findViewById(R.id.btn_main_reissue);
        Button back_btn = dialog.findViewById(R.id.back_btn);

        student = new Student();
        parsingIssue();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Snackbar.make(v, "뒤로가기 버튼을 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void parsingIssue() {
        try{
            // 휴대폰 내장에 저장된 holder_id 가져오기
            cursor = db.rawQuery("select * from " + HolderDBHelper.getTableName(), null);
            cursor.moveToNext();
            holder_id = cursor.getString(cursor.getColumnIndex("holder_id"));
            cursor.close();
            helper.close();

            Log.d(TAG, "MainFragment - parsingIssue() called");
            Log.d(TAG, holder_id);
            type = "issue";
            // http:localhost:6464/idcard/{holderId}
            new RestAPITask().execute(getResources().getString(R.string.apiaddress)+getResources().getString(R.string.idcard)+holder_id);


        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    class RestAPITask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = null;

            try{
                result = downloadContents(strings[0]);
                Log.d(TAG, "RestAPITask - doInBackground() called" + result);
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