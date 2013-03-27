package eu.andlabs.studiolounge.util.parser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class PlayParser {

    private static final int CONNECT_TIMEOUT = 15000 /* milliseconds */;

    private static final String PLAY_BASE_URL = "https://play.google.com/store/apps/details?id=";

    private static final String PLAY_PATTERN_START = "<div class=\"doc-banner-image-container\"><img src=";

    private static final String PLAY_PATTERN_END = "\" alt=\"";

    private static final int MAX_WIDTH = 1024;

    private static final int DEFAULT_HEIGHT_DP = 70;

    private Queue<QueryData> mQueries = new PriorityQueue<PlayParser.QueryData>();

    private List<PlayListener> mListener = new ArrayList<PlayParser.PlayListener>();

    private Map<String, Drawable> mResults = new HashMap<String, Drawable>();

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

    public Drawable getResult(String packageName) {
        return mResults.get(packageName);
    }

    private void notifyListener(PlayResult result) {
        for (PlayListener listener : mListener) {
            listener.onPlayResult(result);
        }

        if (mQueries.size() > 0) {
            queryNext();
        } else {
            mIsQuerying = false;
        }
    }

    public void queryPlay(final String packageName) {
        queryPlay(packageName, Math.min(MAX_WIDTH, mContext.getResources()
                .getDisplayMetrics().widthPixels),
                (int) (DEFAULT_HEIGHT_DP * mContext.getResources()
                        .getDisplayMetrics().density));
    }

    public void queryPlay(final String packageName, final int imageWidth,
            final int imageHeight) {

        if (packageName != null) {
            mQueries.add(new QueryData(packageName, imageWidth, imageHeight));

            if (!mIsQuerying) {
                queryNext();
            }
        }
    }

    private void queryNext() {
        if (mQueries.size() > 0) {
            mIsQuerying = true;

            final QueryData data = mQueries.poll();
            final String packageName = data.getPackageName();
            final int imageWidth = data.getWidth();
            final int imageHeight = data.getHeight();

            Drawable cached = readFileFromInternalStorage(packageName,
                    imageHeight);

            if (cached != null) {
                mResults.put(packageName, cached);
                notifyListener(new PlayResult(cached, packageName));
            } else {
                UrlDownloadTask urlTask = new UrlDownloadTask(packageName,
                        imageWidth, imageHeight);
                urlTask.execute(PLAY_BASE_URL + packageName);
            }
        } else {
            mIsQuerying = false;
        }
    }

    private String parseImageUrl(String html) {
        if (html != null && html.contains(PLAY_PATTERN_START)) {
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

            if (is != null) {

                // Convert the InputStream into a string
                String contentAsString = convertStreamToString(is);
                return contentAsString;
            }
            return null;
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

    private Drawable readFileFromInternalStorage(String packageName, int height) {
        InputStream is = null;
        try {
            is = mContext.openFileInput(packageName);
            final Bitmap bitmap = new BitmapDrawable(is).getBitmap();
            int y = (bitmap.getHeight() - height) / 2;

            return new BitmapDrawable(Bitmap.createBitmap(bitmap, 0, y,
                    bitmap.getWidth(), height));
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

    private class QueryData implements Comparable<QueryData> {
        String mPackageName;
        int mWidth;
        int mHeight;

        public QueryData(String pPackageName, int pImageWidth, int pImageHeight) {
            super();
            mPackageName = pPackageName;
            mWidth = pImageWidth;
            mHeight = pImageHeight;
        }

        public int getHeight() {
            return mHeight;
        }

        public void setHeight(int pHeight) {
            mHeight = pHeight;
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

        @Override
        public int compareTo(QueryData pAnother) {
            return 0;
        }
    }

    private class UrlDownloadTask extends AsyncTask<String, Void, Drawable> {

        private int mImageWidth;
        private int mImageHeight;
        private String mPackageName;

        public UrlDownloadTask(String packageName, int imageWidth,
                int imageHeight) {
            mPackageName = packageName;
            mImageWidth = imageWidth;
            mImageHeight = imageHeight;
        }

        @Override
        protected Drawable doInBackground(String... pParams) {
            String html = null;
            try {
                html = downloadUrl(pParams[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String url = parseImageUrl(html);

            if (url != null) {
                url += mImageWidth;
                InputStream is = null;
                try {
                    is = downloadStream(url);
                    if (is != null) {
                        writeFileToInternalStorage(is, mPackageName);
                        return readFileFromInternalStorage(mPackageName,
                                mImageHeight);
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

            return null;
        }

        protected void onPostExecute(Drawable result) {
            if (result != null) {
                notifyListener(new PlayResult(result, mPackageName));
            }
        };
    }
}
