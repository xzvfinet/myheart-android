package com.xzvfi.myheart;

import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.Heart;
import com.xzvfi.myheart.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by xzvfi on 2016-07-15.
 */
public interface NetworkService {
    String SERVER_IP = "http://172.16.101.181:3000/";

    // USER
    @POST("/user/{token}")
    Call<User> createUser(@Path("token") String token, @Body User user);

    @GET("/user/{token}")
    Call<User> getUser(@Path("token") String token);

//    @PUT("/user/{token}")
//    Call<Boolean> updateUser(@Path("token") String token, @Body User user);

//    @DELETE("/user/{token}")
//    Call<User> getUser(@Path("token") String token);

    // GROUP
    @POST("/group/{group_id}")
    Call<Group> createGroup(@Path("group_id") int group_id, @Body Group group);

    @GET("/group/{group_id}")
    Call<Group> getGroup(@Path("group_id") int group_id);

//    @PUT("/group/{group_id}")
//    Call<Boolean> updateGroup(@Path("group_id") int group_id, @Body Group group);

//    @DELETE("/group/{group_id}")
//    Call<Boolean> removeGroup(@Path("group_id") String group_id);

    @GET("/group/{group_id}/users")
    Call<List<User>> getUsers(@Path("group_id") int group_id);

    @POST("/heart")
    Call<Heart> sendHeart(@Body Heart heart);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
