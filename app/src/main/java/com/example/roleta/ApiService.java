package com.example.roleta;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/roletas")
    Call<List<Roulette>> getRoulettes();
}
