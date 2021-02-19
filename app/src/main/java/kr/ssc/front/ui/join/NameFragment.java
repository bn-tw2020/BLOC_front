//회원가입 이름,학번 입력 fragment (회원가입 마지막)

package kr.ssc.front.ui.join;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.Serializable;

import kr.ssc.front.R;
import kr.ssc.front.JoinActivity;
import kr.ssc.front.OnBackPressedListener;

public class NameFragment extends Fragment implements OnBackPressedListener  {

    private View root;
    private Button btnNext;

    private EditText etSchool;
    private EditText etName;
    private EditText etNum;
    private EditText etDepartment;

    private JoinActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_join_name, container, false);

        activity = (JoinActivity)getActivity();

        etSchool = root.findViewById(R.id.edit_school);
        etName = root.findViewById(R.id.edit_name);
        etNum = root.findViewById(R.id.edit_num);
        etDepartment = root.findViewById(R.id.edit_department);
        btnNext = root.findViewById(R.id.btn_join_name);

        //삭제
        //etSchool.setText("길동대학교");
        //etName.setText("홍길동");
        //etDepartment.setText("컴퓨터학과");
        //etNum.setText("20200001");

        //가입하기
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Navigation.findNavController(root).navigate(R.id.action_nav_join_name_to_nav_join_password);
                Student student = new Student();
                student.setName(String.valueOf(etName.getText()));
                student.setStudent_id(String.valueOf(etNum.getText()));
                student.setUniversity(String.valueOf(etSchool.getText()));
                student.setDepartment(String.valueOf(etDepartment.getText()));

                if(student.getUniversity().equals("")){
                    Toast.makeText(getContext(), "학교를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(student.getStudent_id().equals("")){
                    Toast.makeText(getContext(), "학번 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(student.getDepartment().equals("")){
                    Toast.makeText(getContext(), "학과를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(student.getName().equals("")){
                    Toast.makeText(getContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("student", student);
                    Navigation.findNavController(root).navigate(R.id.action_nav_join_name_to_nav_join_password, bundle);
                }
            }
        });

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
}