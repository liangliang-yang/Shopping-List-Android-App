package seclass.gatech.edu.glm.listeners;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import seclass.gatech.edu.glm.adapters.ExpandableListAdapter;

public class ListItemTouchListener implements View.OnTouchListener {

    private View mListItemView;
    private DismissCallbacks mDismissCallbacks;

    /**
     * Static Variables
     */
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;
    private int mViewWidth = 1;

    /**
     * State Variables
     */
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private VelocityTracker mVelocityTracker;
    private int mGroupPos;
    private int mChildPos;

    public ListItemTouchListener(View listItemView,
                                 int groupPos, int childPos,
                                 DismissCallbacks dismissCallbacks) {
        ViewConfiguration vc = ViewConfiguration.get(listItemView.getContext());
        mListItemView = listItemView;
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity()*12;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = listItemView.getContext().getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mGroupPos = groupPos;
        mChildPos = childPos;
        mDismissCallbacks = dismissCallbacks;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mViewWidth = mListItemView.getWidth();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                /** Consume remaining events for this user-gesture */
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
                    /** Keep tracking finger movement, even if finger slides off of view */
                    mListItemView.getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (mSwiping) {
                    mListItemView.setTranslationX(deltaX - (deltaX > 0 ? mSlop : -mSlop));
                    mListItemView.setAlpha(Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.d("TouchListener", "ACTION_UP");
                /** If no listItem selected, don't do anything */
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaX = event.getRawX() - mDownX;
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();

                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());

                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
                    dismiss = true;
                    dismissRight = deltaX > 0;
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && absVelocityY < absVelocityX
                        && absVelocityY < absVelocityX && mSwiping) {
                    dismiss = (velocityX < 0) == (deltaX < 0);
                    dismissRight = mVelocityTracker.getXVelocity() > 0;
                }

                /** If dismissing, set appropriate transition animations */
                /** Else, make selected view go back to where it belongs */
                if (dismiss) {
                    mListItemView.animate()
                            .translationX(dismissRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    dismissView(mListItemView, mGroupPos, mChildPos);
                                }
                            });
                } else {
                    mListItemView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime);
                }

                /** The user is no longer interacting with any ListItems */
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mSwiping = false;
                break;
            }
        }
        return false;
    }

    void dismissView(final View v, final int groupPos, final int childPos) {
        Log.d("ListItemTouchListener", "dismissed tag is: " + v.getTag());
        long dismissedId = (long) v.getTag();
        Log.d("ListsTouchListener", "Dismissing ID: " + dismissedId);
        mDismissCallbacks.onDismiss(dismissedId, groupPos, childPos);
        fixViews();
    }

    public interface DismissCallbacks {
        void onDismiss(long viewId, int groupPos, int childPos);
    }

    /**
     * For some odd reason, the effects of the animation that swiped away the dismissed
     * view is still there, so must fix
     * --> Happens because in Adapter, I'm re-using the old view as "convertView"... should fix in there.
     */
    private void fixViews() {
        mListItemView.setTranslationX(0);
        mListItemView.setAlpha(1);
    }

}
