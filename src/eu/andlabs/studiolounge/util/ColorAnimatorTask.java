package eu.andlabs.studiolounge.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import eu.andlabs.studiolounge.util.ColorAnimatorTask.ViewColorAnimationHolder;

/**
 * {@link AsyncTask} that changes the background color of views in a pulsing
 * manor. Uses a {@link HashSet} to iterate over views.
 * 
 * Here's how to use it: 1. Initiallize using one of the constructors. 2. Start
 * it using {@link #execute(ViewColorAnimationHolder...)}. You may want to check
 * whether the task is running using @{link {@link #isRunning()}. 3. Add
 * additional {@link ViewColorAnimationHolder}s using the
 * {@link #add(ViewColorAnimationHolder)} or {@link #add(View, int, int))}. 4.
 * Stop the Task using {@link #cancelTask()}, not (!) using
 * {@link #cancel(boolean)}.
 * 
 * @author johannesborchardt
 * 
 */
public class ColorAnimatorTask extends
        AsyncTask<ViewColorAnimationHolder, Float, Void> {

    private Context context;

    private boolean running = false;

    private long lastTimestamp = -1;
    private long startTime = -1;
    private long intervallDuration;

    private float startProportion;
    private float endProportion;

    private long ITERATION_STEPS = 50;

    private static float START_PROPORTION_DEFAULT = 0;
    private static float END_PROPORTION_DEFAULT = 0;
    private static long INTERVALL_DURATION_DEFAULT = 1000;

    private Set<ViewColorAnimationHolder> views = new HashSet<ColorAnimatorTask.ViewColorAnimationHolder>();

    /**
     * Constructor that initializes this task with default values.
     * 
     * @param context
     */
    public ColorAnimatorTask(final Context context) {
        this(context, START_PROPORTION_DEFAULT, END_PROPORTION_DEFAULT,
                INTERVALL_DURATION_DEFAULT);
    }

    /**
     * 
     * @param context
     * @param startProportion
     *            the color proportion to start with
     * @param endProportion
     *            the color proportion to end with
     * @param intervallDuration
     *            the length of the interval between startProportion and
     *            endProportion
     */
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
                Thread.sleep(ITERATION_STEPS);
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

    /**
     * Add a new {@link ViewColorAnimationHolder} to this animation task.
     * 
     * @param holder
     */
    public void add(ViewColorAnimationHolder holder) {
        synchronized (this.views) {
            this.views.add(holder);
        }
    }

    /**
     * Add a new {@link View} to this animation task. You might want to keep a
     * reference to the {@link ViewColorAnimationHolder} returned by this
     * method.
     * 
     * @param view
     * @param colorA
     * @param colorB
     * @return the created {@link ViewColorAnimationHolder}
     */
    public ViewColorAnimationHolder add(View view, int colorA, int colorB) {
        ViewColorAnimationHolder holder = new ViewColorAnimationHolder(view,
                colorA, colorB);
        synchronized (this.views) {
            this.views.add(holder);
        }

        return holder;
    }

    /**
     * Remove a {@link ViewColorAnimationHolder} from the animation set
     * 
     * @param holder
     */
    public void remove(ViewColorAnimationHolder holder) {
        this.views.remove(holder);
    }

    /**
     * Returns whether this Task is running or not. Works only when
     * {@link #cancelTask()} is used instead of {@link #cancel(boolean)}
     * 
     * @return
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Cancels this task forcefully
     */
    public void cancelTask() {
        cancel(true);
        this.running = false;
    }

    /**
     * A holder class for a view and its start and end color
     * 
     * @author johannesborchardt
     * 
     */
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
