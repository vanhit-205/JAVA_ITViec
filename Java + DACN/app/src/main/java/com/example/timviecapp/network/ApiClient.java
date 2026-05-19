package com.example.timviecapp.network;

import android.content.Intent;
import android.util.Log;

import com.example.timviecapp.WorkHubApp;
import com.example.timviecapp.constants.Constants;
import com.example.timviecapp.models.auth.RefreshResponse;
import com.example.timviecapp.models.auth.RefreshTokenRequest;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.network.services.AuthApiService;
import com.example.timviecapp.storage.SharedPrefManager;
import com.example.timviecapp.ui.auth.LoginActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static Retrofit retrofit;

    public static synchronized Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder builder = original.newBuilder();

                            // Lấy token từ SharedPrefManager
                            SharedPrefManager prefManager = SharedPrefManager.getInstance(WorkHubApp.getInstance());
                            String token = prefManager.getToken();

                            // Bỏ qua đính kèm token cho các request auth cơ bản
                            String path = original.url().encodedPath();
                            boolean isAuthRequest = path.contains("/auth/login") 
                                    || path.contains("/auth/register") 
                                    || path.contains("/auth/refresh")
                                    || path.contains("/forgot-password");

                            if (token != null && !token.isEmpty() && !isAuthRequest) {
                                builder.addHeader("Authorization", "Bearer " + token);
                            }

                            Response response = chain.proceed(builder.build());

                            // Nếu gặp lỗi 401 Unauthorized và không phải request Auth, thực hiện refresh token
                            if (response.code() == 401 && !isAuthRequest) {
                                synchronized (ApiClient.class) {
                                    // Kiểm tra xem token đã được refresh bởi thread khác chưa
                                    String freshToken = prefManager.getToken();
                                    if (freshToken != null && !freshToken.equals(token)) {
                                        // Thử lại với token mới đã refresh
                                        response.close();
                                        return chain.proceed(newRequestWithToken(original, freshToken));
                                    }

                                    // Tiến hành refresh token đồng bộ
                                    String refreshToken = prefManager.getRefreshToken();
                                    if (refreshToken != null && !refreshToken.isEmpty()) {
                                        String newAccessToken = refreshAccessTokenSync(refreshToken);
                                        if (newAccessToken != null) {
                                            // Lưu token mới
                                            prefManager.saveToken(newAccessToken);
                                            response.close();
                                            // Thực hiện lại request gốc với token mới
                                            return chain.proceed(newRequestWithToken(original, newAccessToken));
                                        } else {
                                            // Refresh thất bại -> Đăng xuất người dùng
                                            logoutUser();
                                        }
                                    } else {
                                        logoutUser();
                                    }
                                }
                            }

                            return response;
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static Request newRequestWithToken(Request original, String token) {
        return original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * Refresh Token đồng bộ (Synchronous API Call)
     */
    private static String refreshAccessTokenSync(String refreshToken) {
        try {
            OkHttpClient baseClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();

            Retrofit basicRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(baseClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AuthApiService authService = basicRetrofit.create(AuthApiService.class);
            retrofit2.Response<ApiResponse<RefreshResponse>> response = 
                    authService.refreshToken(new RefreshTokenRequest(refreshToken)).execute();

            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                RefreshResponse refreshResponse = response.body().getData();
                if (refreshResponse != null) {
                    return refreshResponse.getAccessToken();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing token synchronously: " + e.getMessage());
        }
        return null;
    }

    /**
     * Clear session và điều hướng về LoginActivity khi token hết hạn
     */
    private static void logoutUser() {
        Log.w(TAG, "Session expired. Logging out user.");
        SharedPrefManager.getInstance(WorkHubApp.getInstance()).clear();

        Intent intent = new Intent(WorkHubApp.getInstance(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        WorkHubApp.getInstance().startActivity(intent);
    }
}
