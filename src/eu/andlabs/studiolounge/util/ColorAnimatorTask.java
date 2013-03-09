package eu.andlabs.studiolounge.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import eu.andlabs.studiolounge.util.ColorAnimatorTask.ViewColorAnimationHolder;

public class ColorAnimatorTask extends
        AsyncTask<ViewColorAnimationHolder, Float, Void> {

    private Context context;

    private boolean running = false;

    private long lastTimestamp = -1;

    private long startTime = -1;

    private long iterationSteps = 50;

    private float startProportion;

    private float endProportion;

    private long intervallDuration;

    private Set<ViewColorAnimationHolder> views = new HashSet<ColorAnimatorTask.ViewColorAnimationHolder>();

    public ColorAnimatorTask(final Context context,
            final float startProportion, final float endProportion,
            final long intervallDuration) {

        this.context = context;
        this.startProportion = startProportion;
        this.endProportion = endProportion;
        this.intervallDuration = intervallDuration;
    }

    @Override
    protected Void doInBackground(ViewColorAnimationHolder... views) {

        this.views = new HashSet<ColorAnimatorTask.ViewColorAnimationHolder>(
                Arrays.asList(views));
        
        this.running = true;

        while (this.running) {
            if (this.lastTimestamp == -1) {
                this.lastTimestamp = System.currentTimeMillis();
                this.startTime = System.currentTimeMillis();
            }

            final float proportionDelta = this.endProportion
                    - this.startProportion;
            if (proportionDelta < 0) {
                throw new IllegalArgumentException(
                        "endProportion needs to be smaller than startProportion");
            }

            float deltaTime = System.currentTimeMillis() - this.lastTimestamp;
            float totalTime = System.currentTimeMillis() - this.startTime;
            float deltaTimeCurrentPeriod = deltaTime % this.intervallDuration;
            float progress = deltaTimeCurrentPeriod / this.intervallDuration;
            int period = (int) (totalTime / this.intervallDuration + 1);

            if (period % 2 == 0) { // count down
                publishProgress(1 - progress);
            } else { // count up
                publishProgress(progress);
            }

            try {
                Thread.sleep(iterationSteps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Float... progress) {
        super.onProgressUpdate(progress);

        synchronized (this.views) {
            for (ViewColorAnimationHolder view : this.views) {
                view.getView().setBackgroundColor(
                        Utils.ipc(this.context, view.getColorA(),
                                view.getColorB(), progress[0]));
            }
        }
    }

    public void add(ViewColorAnimationHolder holder) {
        synchronized (this.views) {
            this.views.add(holder);
        }
    }

    public ViewColorAnimationHolder add(View view, int colorA, int colorB) {
        ViewColorAnimationHolder holder = new ViewColorAnimationHolder(view,
                colorA, colorB);
        synchronized (this.views) {
            this.views.add(holder);
        }
        
        return holder;
    }

    public void remove(ViewColorAnimationHolder holder) {
        this.views.remove(holder);
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public void cancelTask() {
        cancel(true);
        this.running = false;
    }

    public static class ViewColorAnimationHolder {
        private View view;
        private int colorA;
        private int colorB;

        public ViewColorAnimationHolder(View view, int colorA, int colorB) {
            this.view = view;
            this.colorA = colorA;
            this.colorB = colorB;
        }

        public View getView() {
            return this.view;
        }

        public int getColorA() {
            return this.colorA;
        }

        public int getColorB() {
            return this.colorB;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + colorB;
            result = prime * result + colorA;
            result = prime * result + ((view == null) ? 0 : view.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ViewColorAnimationHolder other = (ViewColorAnimationHolder) obj;
            if (colorB != other.colorB)
                return false;
            if (colorA != other.colorA)
                return false;
            if (view == null) {
                if (other.view != null)
                    return false;
            } else if (!view.equals(other.view))
                return false;
            return true;
        }
    }
}
