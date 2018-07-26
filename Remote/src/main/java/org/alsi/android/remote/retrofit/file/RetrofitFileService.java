package org.alsi.android.remote.retrofit.file;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created on 12/10/17.
 */

public interface RetrofitFileService
{
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

    @GET
    Call<ResponseBody> downloadFileSynchronously(@Url String fileUrl);
}
