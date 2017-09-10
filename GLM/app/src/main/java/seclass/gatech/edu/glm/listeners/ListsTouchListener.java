package seclass.gatech.edu.glm.listeners;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class ListsTouchListener implements View.OnTouchListener {

    private DismissCallbacks mDismissCallbacks;
    private ClickCallbacks mClickCallbacks;

    /**
     * Static Variables
     */
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;
    private ListView mListView;
    private long mClickTimeThreshold;
    private float mClickDeltaThreshold;

    /**
     * State Variables
     */
    private int mSwipingSlop;
    private boolean mSwiping;
    private int mViewWidth = 1;
    private View mDownView;
    private float mDownX;
    private float mDownY;
    private VelocityTracker mVelocityTracker;
    private long mDownStartTime;
    private long mDownEndTime;

    public ListsTouchListener(ListView listView, DismissCallbacks dismissCallbacks, ClickCallbacks clickCallbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = listView.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mListView = listView;
        mDismissCallbacks = dismissCallbacks;
        mClickCallbacks = clickCallbacks;
        mClickTimeThreshold = 300;
        mClickDeltaThreshold = vc.getScaledMinimumFlingVelocity();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mViewWidth = mListView.getWidth();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                int[] listViewCoords = new int[2];
                mDownStartTime = System.currentTimeMillis();
                mListView.getLocationOnScreen(listViewCoords);
                int listViewX = listViewCoords[0], listViewY = listViewCoords[1];
                /** Coordinates of user-press within ListView */
                int x = (int) event.getRawX() - listViewX;
                int y = (int) event.getRawY() - listViewY;
                /** Check each listItem if it contained the hit */
                View listItem;
                Rect listItemArea = new Rect();
                for (int i = 0; i < mListView.getChildCount(); i++) {
                    listItem = mListView.getChildAt(i);
                    listItem.getHitRect(listItemArea);
                    if (listItemArea.contains(x, y)) {
                        mDownView = listItem;
                        break;
                    }
                }
                if (mDownView != null) {
                    mDownX = event.getRawX();
                    mDownY = event.getRawY();
                    Log.d("ListsTouchListener", "Position: " + mListView.getPositionForView(mDownView));
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(event);
                }
                /** Interested in user's gesture even when user's finger slides off the listView */
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                /** If no listItem selected, don't do anything */
                if (mVelocityTracker == null) {
                    break;
                }
                mVelocityTracker.addMovement(event);
                float deltaX = event.getRawX() - mDownX;
                float deltaY = event.getRawY() - mDownY;
                /** Perceive changes in X as a swipe. However, large changes in Y instead
                 * indicate that the user wants to scroll*/
                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true;
                    mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
                    mDownView.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (mSwiping) {
                    mDownView.setTranslationX(deltaX - mSwipingSlop);
                    mDownView.setAlpha(Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                /** If no listItem selected, don't do anything */
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = event.getRawX() - mDownX;
                float deltaY = event.getRawY() - mDownY;
                mDownEndTime = System.currentTimeMillis();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(deltaX) > mViewWidth / 2
                        && mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && mSwiping) {
                    dismiss = true;
                    dismissRight = velocityX > 0;
                }

                /** Handle ClickEvents here */
                if (mDownView != null && isClick(mDownStartTime, mDownEndTime, deltaX, deltaY)) {
                    mClickCallbacks.onClick((long) mDownView.getTag());
                    break;
                }

                /** If dismissing, set appropriae transition animations */
                /** Else, make selected view go back to where it belongs */
                if (dismiss) {
                    final View viewToDismiss = mDownView;
                    final int positionToDismiss = mListView.getPositionForView(mDownView);
                    mDownView.animate()
                            .translationX(dismissRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    dismissView(viewToDismiss, positionToDismiss);
                                }
                            });
                } else {
                    mDownView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime);
                }

                /** The user is no longer interacting with any ListItems */
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mDownView = null;
                mSwiping = false;
                break;
            }
        }
        /** By default, don't consume event */
        return false;
    }

    private void dismissView(final View v, final int pos) {
        long dismissedId = (long) v.getTag();
        mDismissCallbacks.onDismiss(mListView, dismissedId, pos);
        fixViews();
    }

    public interface DismissCallbacks {
        void onDismiss(ListView listView, long viewId, int position);
    }

    public interface ClickCallbacks {
        void onClick(long viewId);
    }

    /**
     * For some odd reason, the effects of the animation that swiped away the dismissed
     * view is still there, so must fix
     * TODO: --> Happens because in Adapter, I'm re-using the old view as "convertView"... should fix in there.
     */
    private void fixViews() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View child = mListView.getChildAt(i);
            child.setTranslationX(0);
            child.setAlpha(1);
        }
    }

    private boolean isClick(long mDownStartTime, long mDownEndTime, float deltaX, float deltaY) {
        return Math.abs(mDownEndTime - mDownStartTime) < mClickTimeThreshold
                && Math.abs(deltaX) < mClickDeltaThreshold
                && Math.abs(deltaY) < mClickDeltaThreshold;
    }
}
