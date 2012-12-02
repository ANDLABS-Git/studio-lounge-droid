package eu.andlabs.studiolounge.lobby.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class PlayParser {

    private static final int CONNECT_TIMEOUT = 15000 /* milliseconds */;

    private static final String PLAY_BASE_URL = "https://play.google.com/store/apps/details?id=";

    private static final String PLAY_PATTERN_START = "<div class=\"doc-banner-image-container\"><img src=";

    private static final String PLAY_PATTERN_END = "\" alt=\"";

    private Queue<QueryData> mQueries = new PriorityQueue<PlayParser.QueryData>();

    public List<PlayListener> mListener = new ArrayList<PlayParser.PlayListener>();

    private boolean mIsQuerying = false;

    private Context mContext;

    private static PlayParser sInstance;

    private PlayParser(Context context) {
        mContext = context;
    }

    public static PlayParser getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PlayParser(context);
        }
        return sInstance;
    }

    public void addListener(PlayListener listener) {
        mListener.add(listener);
    }

    public void removeListener(PlayListener listener) {
        mListener.remove(listener);
    }

    private void notifyListener(PlayResult result) {
        for (PlayListener listener : mListener) {
            listener.onPlayResult(result);
        }

        if (mQueries.size() > 0) {
            queryNext();
        } else {
            mIsQuerying = true;
        }
    }

    public void queryPlay(final String packageName, final int imageWidth) {
        mQueries.add(new QueryData(packageName, imageWidth));

        if (!mIsQuerying) {
            queryNext();
        }
    }

    private void queryNext() {
        if (mQueries.size() > 0) {
            mIsQuerying = true;

            final QueryData data = mQueries.poll();
            final String packageName = data.getPackageName();
            final int imageWidth = data.getWidth();

            Drawable cached = readFileFromInternalStorage(packageName);
            if (cached != null) {
                notifyListener(new PlayResult(cached, packageName));
            } else {
                DrawableDownloadTask drawableTask = new DrawableDownloadTask(
                        packageName);
                UrlDownloadTask urlTask = new UrlDownloadTask(drawableTask,
                        imageWidth);
                urlTask.execute(PLAY_BASE_URL + packageName);
            }
        }
    }

    private String parseImageUrl(String html) {
        Log.i("HTML", html);
        if (html.contains(PLAY_PATTERN_START)) {
            int start = html.indexOf(PLAY_PATTERN_START);
            start += PLAY_PATTERN_START.length() + 1;
            final String secondHalf = html.subSequence(start, start + 300)
                    .toString();

            int end = secondHalf.indexOf(PLAY_PATTERN_END);

            final String imageString = secondHalf.substring(0, end);

            String returnString = "";
            String[] strings = imageString.split("w");
            for (int i = 0; i < strings.length - 1; i++) {
                returnString += strings[i] + "w";
            }
            return returnString;
        }

        return null;
    }

    private String downloadUrl(String url) throws IOException {

        InputStream is = null;
        try {
            is = downloadStream(url);

            // Convert the InputStream into a string
            String contentAsString = convertStreamToString(is);
            return contentAsString;
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void writeFileToInternalStorage(InputStream is, String packageName) {
        FileOutputStream out = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();

            int input = is.read();
            while (input != -1) {
                byteArrayOutputStream.write(input);
                input = is.read();
            }
            byte[] returnByteArray = byteArrayOutputStream.toByteArray();

            out = mContext.openFileOutput(packageName, Context.MODE_PRIVATE);
            out.write(returnByteArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Drawable readFileFromInternalStorage(String packageName) {
        InputStream is = null;
        try {
            is = mContext.openFileInput(packageName);
            return new BitmapDrawable(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private String convertStreamToString(final InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    private InputStream downloadStream(String url) {
        InputStream inputStream = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(url);
        HttpResponse response = null;

        if (url == null) {
            return null;
        }

        HttpParams clientParams = client.getParams();
        HttpConnectionParams
                .setConnectionTimeout(clientParams, CONNECT_TIMEOUT);
        HttpConnectionParams.setSoTimeout(clientParams, CONNECT_TIMEOUT);

        try {
            response = client.execute(getRequest);

            HttpEntity entity = response.getEntity();
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == 200 && entity != null) {

                inputStream = response.getEntity().getContent();
                return inputStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public interface PlayListener {
        public void onPlayResult(PlayResult result);
    }

    private class QueryData {
        String mPackageName;
        int mWidth;

        public QueryData(String pPackageName, int pImageWidth) {
            super();
            mPackageName = pPackageName;
            mWidth = pImageWidth;
        }

        public String getPackageName() {
            return mPackageName;
        }

        public void setPackageName(String pPackageName) {
            mPackageName = pPackageName;
        }

        public int getWidth() {
            return mWidth;
        }

        public void setWidth(int pWidth) {
            mWidth = pWidth;
        }
    }

    private class DrawableDownloadTask extends
            AsyncTask<String, Void, Drawable> {

        private String mPackageName;

        public DrawableDownloadTask(String packageName) {
            mPackageName = packageName;
        }

        @Override
        protected Drawable doInBackground(String... pParams) {
            InputStream is = null;
            try {
                is = downloadStream(pParams[0]);
                if (is != null) {
                    writeFileToInternalStorage(is, mPackageName);
                    return readFileFromInternalStorage(mPackageName);
                }
                return null;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected void onPostExecute(Drawable result) {
            if (result != null) {
                notifyListener(new PlayResult(result, mPackageName));
            }
        }
    }

    private class UrlDownloadTask extends AsyncTask<String, Void, String> {

        private DrawableDownloadTask mDrawableTask;
        private int mImageWidth;

        public UrlDownloadTask(DrawableDownloadTask drawableTask, int imageWidth) {
            mDrawableTask = drawableTask;
            mImageWidth = imageWidth;
        }

        @Override
        protected String doInBackground(String... pParams) {
            String html = null;
            try {
                html = downloadUrl(pParams[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return parseImageUrl(html);
        }

        protected void onPostExecute(String result) {
            mDrawableTask.execute(result + mImageWidth);
        };
    }
}
