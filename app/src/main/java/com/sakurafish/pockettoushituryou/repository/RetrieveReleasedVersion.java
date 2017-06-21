package com.sakurafish.pockettoushituryou.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.sakurafish.pockettoushituryou.R;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Google Playのサイトよりリリースされているアプリのバージョンを取得する
 */
public class RetrieveReleasedVersion extends AsyncTask<Void, Void, String> {
    final private Callback callback;
    final Context context;

    public RetrieveReleasedVersion(final Context context, final Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(final Void... params) {
        String new_version;
        try {
            new_version = Jsoup.connect(context.getString(R.string.app_url))
                    .timeout(30000)
                    .userAgent("Mozilla") //http://stackoverflow.com/a/36780250/2845202
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new_version;
    }

    @Override
    protected void onPostExecute(final String param) {
        super.onPostExecute(param);
        callback.onFinish(param);
    }

    @Override
    protected void onProgressUpdate(final Void... values) {
        super.onProgressUpdate(values);
    }

    public interface Callback {
        void onFinish(String version);
    }
}
