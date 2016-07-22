package com.xzvfi.myheart;

import com.xzvfi.myheart.model.Group;
import com.xzvfi.myheart.model.Heart;
import com.xzvfi.myheart.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by xzvfi on 2016-07-15.
 */
public interface NetworkService {
    String SERVER_IP = "http://211.249.63.188:3000/";

    // USER
    @POST("/user/{user_id}")
    Call<User> createUser(@Path("user_id") String user_id, @Body User user);

    @GET("/user/{user_id}")
    Call<User> getUser(@Path("user_id") String user_id);

    @PUT("/user/{token}")
    Call<User> updateUser(@Path("token") String token, @Body User user);

    @DELETE("/user/{token}")
    Call<User> removeUser(@Path("token") String token);

    // GROUP
    @POST("/group/{group_id}")
    Call<Group> createGroup(@Path("group_id") int group_id, @Body Group group);

    @GET("/group/{group_id}")
    Call<Group> getGroup(@Path("group_id") int group_id);

    @PUT("/group/{group_id}")
    Call<Group> updateGroup(@Path("group_id") int group_id, @Body Group group);

    @DELETE("/group/{group_id}")
    Call<Group> removeGroup(@Path("group_id") String group_id);

    @GET("/group/{group_id}/users/{except_id}")
    Call<List<User>> getUsers(@Path("group_id") int group_id, @Path("except_id") String except_id);

    // HEART
    @POST("/heart")
    Call<Heart> sendHeart(@Body Heart heart);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVER_IP)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
