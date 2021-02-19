package kr.ssc.front.data.api;

import kr.ssc.front.data.api.model.Student;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    //    @GET("posts/{post}") - 요청메소드 GET, baseUrl에 연결될 EndPoint 'posts/{post}
//    반환타입 Call<PostResult> - Call은 응답이 왔을때 Callback으로 불려질 타입
    //    PostResult - 요청GET에 대한 응답데이터를 받아서 DTO 객체화할 클래스 타입 지정
//    메소드명 "getPosts" - 자유롭게 설정, 통신에 영향 x
//    매개변수 '@Path("post") String post' - 매개변수 post가 @Path("post")를 보고 @GET 내부 {post}에 대입
    @GET("posts/{post}")
    Call<Student> getPosts(@Path("post") String post);
//                new RestAPITask().execute
//                (getResources().getString(R.string.apiaddress)+
//                getResources().getString(R.string.reIssue)+holder_id);
}
