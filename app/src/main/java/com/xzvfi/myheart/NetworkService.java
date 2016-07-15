package com.xzvfi.myheart;

import com.xzvfi.myheart.model.Group;
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

    @GET("/auth/{token}")
    Call<Boolean> isUserExist(@Path("token") String token);

    // USER
    @POST("/user/{token}")
    Call<Boolean> createUser(@Path("token") String token, @Body User user);

    @GET("/user/{token}")
    Call<User> getUser(@Path("token") String token);

    @PUT("/user/{token}")
    Call<Boolean> updateUser(@Path("token") String token, @Body User user);

//    @DELETE("/user/{token}")
//    Call<User> getUser(@Path("token") String token);

    // GROUP
    @POST("/group/{group_id}")
    Call<Boolean> createGroup(@Path("group_id") int group_id, @Body Group group);

    @GET("/group/{group_id}")
    Call<Group> getGroup(@Path("group_id") int group_id);

    @PUT("/group/{group_id}")
    Call<Boolean> updateGroup(@Path("group_id") int group_id, @Body Group group);

//    @DELETE("/group/{group_id}")
//    Call<Boolean> removeGroup(@Path("group_id") String group_id);

    @GET("/group/{group_id}/users")
    Call<List<User>> getUsers(@Path("group_id") String group_id);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://172.16.101.163:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
