package com.sanskruti.volotek.custom.poster.views.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.sanskruti.volotek.custom.poster.views.Vector2D;

public class MultiTouchListener implements View.OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;
    Bitmap bitmap;


    boolean f135bt = false;


    GestureDetector f136gd = null;
    public boolean isRotateEnabled = true;
    public boolean isRotationEnabled = false;
    public boolean isTranslateEnabled = true;
    private TouchCallbackListener listener = null;
    private int mActivePointerId = -1;
    private Context mContext;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    public float maximumScale = 8.0f;
    public float minimumScale = 0.5f;

    public interface TouchCallbackListener {
        void onMidX(View view);

        void onMidXY(View view);

        void onMidY(View view);

        void onTouchCallback(View view);

        void onTouchMoveCallback(View view);

        void onTouchUpCallback(View view);

        void onTouchUpClick(View view);

        void onXY(View view);
    }

    public MultiTouchListener(Context context) {
        this.mContext = context;
        this.mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector;

        private ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D();
        }

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            this.mPivotX = detector.getFocusX();
            this.mPivotY = detector.getFocusY();
            this.mPrevSpanVector.set(detector.getCurrentSpanVector());
            return true;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            float f;
            float f2 = 0.0f;
            TransformInfo info = new TransformInfo();
            info.deltaAngle = MultiTouchListener.this.isRotateEnabled ? Vector2D.getAngle(this.mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            if (MultiTouchListener.this.isTranslateEnabled) {
                f = detector.getFocusX() - this.mPivotX;
            } else {
                f = 0.0f;
            }
            info.deltaX = f;
            if (MultiTouchListener.this.isTranslateEnabled) {
                f2 = detector.getFocusY() - this.mPivotY;
            }
            info.deltaY = f2;
            info.pivotX = this.mPivotX;
            info.pivotY = this.mPivotY;
            info.minimumScale = MultiTouchListener.this.minimumScale;
            info.maximumScale = MultiTouchListener.this.maximumScale;
            MultiTouchListener.this.move(view, info);
            return false;
        }
    }


    public class TransformInfo {
        public float deltaAngle;
        public float deltaScale;
        public float deltaX;
        public float deltaY;
        public float maximumScale;
        public float minimumScale;
        public float pivotX;
        public float pivotY;

        private TransformInfo() {
        }
    }

    public MultiTouchListener setGestureListener(GestureDetector gd2) {
        this.f136gd = gd2;
        return this;
    }

    public MultiTouchListener setOnTouchCallbackListener(TouchCallbackListener l) {
        this.listener = l;
        return this;
    }

    public MultiTouchListener enableRotation(boolean b) {
        this.isRotationEnabled = b;
        return this;
    }

    public MultiTouchListener setMinScale(float f) {
        this.minimumScale = f;
        return this;
    }

    public void move(View view, TransformInfo info) {
        if (this.isRotationEnabled) {
            view.setRotation(adjustAngle(view.getRotation() + info.deltaAngle));
        }
    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            return degrees - 360.0f;
        }
        if (degrees < -180.0f) {
            return 360.0f + degrees;
        }
        return degrees;
    }

    private void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
        this.mContext.getResources();
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() != pivotX || view.getPivotY() != pivotY) {
            float[] prevPoint = {0.0f, 0.0f};
            view.getMatrix().mapPoints(prevPoint);
            view.setPivotX(pivotX);
            view.setPivotY(pivotY);
            float[] currPoint = {0.0f, 0.0f};
            view.getMatrix().mapPoints(currPoint);
            float offsetY = currPoint[1] - prevPoint[1];
            view.setTranslationX(view.getTranslationX() - (currPoint[0] - prevPoint[0]));
            view.setTranslationY(view.getTranslationY() - offsetY);
        }
    }

    public boolean handleTransparency(View view, MotionEvent event) {
        try {
            boolean b = true;
            if (event.getAction() == 2 && this.f135bt) {
                return true;
            }
            if (event.getAction() == 1) {
                if (this.f135bt) {
                    this.f135bt = false;
                    if (this.bitmap != null) {
                        this.bitmap.recycle();
                    }
                    return true;
                }
            }
            int[] posXY = new int[2];
            view.getLocationOnScreen(posXY);
            float r = view.getRotation();
            Matrix mat = new Matrix();
            mat.postRotate(-r);
            float[] point = {(float) ((int) (event.getRawX() - ((float) posXY[0]))), (float) ((int) (event.getRawY() - ((float) posXY[1])))};
            mat.mapPoints(point);
            int rx2 = (int) point[0];
            int ry2 = (int) point[1];
            if (event.getAction() == 0) {
                this.f135bt = false;
                view.setDrawingCacheEnabled(true);
                this.bitmap = Bitmap.createBitmap(view.getDrawingCache());
                rx2 = (int) (((float) rx2) * (((float) this.bitmap.getWidth()) / (((float) this.bitmap.getWidth()) * view.getScaleX())));
                ry2 = (int) (((float) ry2) * (((float) this.bitmap.getWidth()) / (((float) this.bitmap.getHeight()) * view.getScaleX())));
                view.setDrawingCacheEnabled(false);
            }
            if (rx2 >= 0 && ry2 >= 0 && rx2 <= this.bitmap.getWidth()) {
                if (ry2 <= this.bitmap.getHeight()) {
                    if (this.bitmap.getPixel(rx2, ry2) != 0) {
                        b = false;
                    }
                    if (event.getAction() != 0) {
                        return b;
                    }
                    this.f135bt = b;
                    return b;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean onTouch(View view, MotionEvent event) {

        TouchCallbackListener touchCallbackListener;
        this.mScaleGestureDetector.onTouchEvent(view, event);
        RelativeLayout rl = (RelativeLayout) view.getParent();
        GestureDetector gestureDetector = this.f136gd;
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(event);
        }
        if (!this.isTranslateEnabled) {
            return true;
        }
        int action = event.getAction();
        int actionMasked = event.getActionMasked() & action;
        int newPointerIndex = 0;
        if (actionMasked == 0) {
            if (rl != null) {
                rl.requestDisallowInterceptTouchEvent(true);
            }
            TouchCallbackListener touchCallbackListener1 = this.listener;
            if (touchCallbackListener1 != null) {
                touchCallbackListener1.onTouchCallback(view);
            }
            if (view instanceof AutofitTextRel) {
                ((AutofitTextRel) view).setBorderVisibility(true);
            }
            this.mPrevX = event.getX();
            this.mPrevY = event.getY();
            this.mActivePointerId = event.getPointerId(0);
        } else
            if (actionMasked == 1) {
            this.mActivePointerId = -1;
            TouchCallbackListener touchCallbackListener2 = this.listener;
            if (touchCallbackListener2 != null) {
                touchCallbackListener2.onTouchUpCallback(view);
            }
            float rotation = view.getRotation();
            if (Math.abs(90.0f - Math.abs(rotation)) <= 5.0f) {
                if (rotation > 0.0f) {
                    rotation = 90.0f;
                } else {
                    rotation = -90.0f;
                }
            }
            if (Math.abs(0.0f - Math.abs(rotation)) <= 5.0f) {
                if (rotation > 0.0f) {
                    rotation = 0.0f;
                } else {
                    rotation = -0.0f;
                }
            }
            if (Math.abs(180.0f - Math.abs(rotation)) <= 5.0f) {
                if (rotation > 0.0f) {
                    rotation = 180.0f;
                } else {
                    rotation = -180.0f;
                }
            }
            view.setRotation(rotation);
            Log.i("testing", "Final Rotation : " + rotation);
        } else if (actionMasked == 2) {
            if (rl != null) {
                rl.requestDisallowInterceptTouchEvent(true);
            }
            TouchCallbackListener touchCallbackListener3 = this.listener;
            if (touchCallbackListener3 != null) {
                touchCallbackListener3.onTouchMoveCallback(view);
            }
            if ((touchCallbackListener = this.listener) != null) {
                touchCallbackListener.onTouchUpClick(view);
            }

            int pointerIndex = event.findPointerIndex(this.mActivePointerId);
            if (pointerIndex != -1) {
                float currX = event.getX(pointerIndex);
                float currY = event.getY(pointerIndex);
                if (!this.mScaleGestureDetector.isInProgress()) {
                    adjustTranslation(view, currX - this.mPrevX, currY - this.mPrevY);
                }
            }
        } else if (actionMasked == 3) {
            this.mActivePointerId = -1;
        } else if (actionMasked == 6) {
            int pointerIndex2 = (65280 & action) >> 8;
            if (event.getPointerId(pointerIndex2) == this.mActivePointerId) {
                if (pointerIndex2 == 0) {
                    newPointerIndex = 1;
                }
                this.mPrevX = event.getX(newPointerIndex);
                this.mPrevY = event.getY(newPointerIndex);
                this.mActivePointerId = event.getPointerId(newPointerIndex);
            }
        }
        return true;
    }
}