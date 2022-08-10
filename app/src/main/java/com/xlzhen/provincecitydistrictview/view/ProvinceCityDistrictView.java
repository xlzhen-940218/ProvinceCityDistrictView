package com.xlzhen.provincecitydistrictview.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.xlzhen.provincecitydistrictview.R;
import com.xlzhen.provincecitydistrictview.bean.CityBean;
import com.xlzhen.provincecitydistrictview.bean.DistrictBean;
import com.xlzhen.provincecitydistrictview.bean.ProvinceBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProvinceCityDistrictView extends View implements GestureDetector.OnGestureListener {
    public static final int UPDATE_UI = 1;
    private int width, height;
    private int centerX, centerY;
    private int dp1, dp5, dp10, dp12, dp14, dp17, dp20, dp25, dp40, dp120;
    private Paint linePaint, provinceCityDistrictPaint, textPaint;
    private String provinceName, cityName, districtName;
    private Map<String, Float> provinceMap;
    private Map<String, Float> cityMap;
    private Map<String, Float> districtMap;

    private List<ProvinceBean> provinceBeans;
    private List<CityBean> currentCityList;
    private List<DistrictBean> currentDistrictList;

    private String province, city, district;

    private GestureDetector gestureDetector;

    private Listener listener;

    private ViewHandler handler;

    public ProvinceCityDistrictView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        provinceName = getResources().getString(R.string.province);
        cityName = getResources().getString(R.string.city);
        districtName = getResources().getString(R.string.district);

        dp1 = dp2px(1);
        dp5 = dp2px(5);
        dp10 = dp2px(10);
        dp12 = dp2px(12);
        dp14 = dp2px(14);
        dp17 = dp2px(17);
        dp20 = dp2px(20);
        dp25 = dp2px(25);
        dp40 = dp2px(40);
        dp120 = dp2px(120);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(getResources().getColor(R.color.purple_200));
        linePaint.setStrokeWidth(dp1);

        provinceCityDistrictPaint = new TextPaint();
        provinceCityDistrictPaint.setColor(getResources().getColor(R.color.color_666));
        provinceCityDistrictPaint.setAntiAlias(true);
        provinceCityDistrictPaint.setTextAlign(Paint.Align.CENTER);
        provinceCityDistrictPaint.setTextSize(dp12);

        textPaint = new TextPaint();
        textPaint.setColor(getResources().getColor(R.color.color_333));
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(dp12);

        gestureDetector = new GestureDetector(context, this);
        handler = new ViewHandler();
        new Thread(this::initData).start();
    }

    private void initData() {
        try {
            InputStream inputStream = getContext().getAssets().open("province.json");
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            String json = new String(data, StandardCharsets.UTF_8);
            provinceBeans = JSON.parseArray(json, ProvinceBean.class);

            currentCityList = provinceBeans.get(0).getCity();
            currentDistrictList = currentCityList.get(0).getDistrict();

            provinceMap = new LinkedHashMap<>();
            for (int i = 0; i < provinceBeans.size(); i++) {
                provinceMap.put(provinceBeans.get(i).getN(), i * dp40 + 0f);
            }

            cityMap = new LinkedHashMap<>();
            for (int i = 0; i < currentCityList.size(); i++) {
                cityMap.put(currentCityList.get(i).getN(), i * dp40 + 0f);
            }

            districtMap = new LinkedHashMap<>();
            for (int i = 0; i < currentDistrictList.size(); i++) {
                districtMap.put(currentDistrictList.get(i).getN(), i * dp40 + 0f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(UPDATE_UI);
    }

    private class ViewHandler  extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_UI){
                touchUp(provinceBeans.get(0).getN(), currentCityList.get(0).getN(), currentDistrictList.get(0).getN());
                invalidate();
            }
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchUp(null, null, null);
            return false;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void touchUp(String province, String city, String district) {
        changeProvince(province);
        changeCity(city);
        changeDistrict(district);
        if (listener != null) {
            listener.selected(this.province, this.city, this.district);
        }
        invalidate();
    }

    private void changeDistrict(String district) {
        float differenceY = 0f;
        if (district == null) {
            for (String key : districtMap.keySet()) {
                float y = districtMap.get(key);
                if (Math.abs(height / 2f - y + dp5) <= dp20) {
                    differenceY = height / 2f - y + dp5;
                    this.district = key;

                    break;
                }
            }
        } else {
            differenceY = height / 2f - districtMap.get(district) + dp5;
            this.district = district;
        }

        if (differenceY == 0 && district != null) {
            differenceY = height / 2f - districtMap.get(this.district) + dp5;
        }

        for (String key : districtMap.keySet()) {
            districtMap.put(key, districtMap.get(key) + differenceY);
        }
    }

    private void changeCity(String city) {
        float differenceY = 0f;
        if (city == null) {
            for (String key : cityMap.keySet()) {
                float y = cityMap.get(key);
                if (Math.abs(height / 2f - y + dp5) <= dp20) {
                    differenceY = height / 2f - y + dp5;
                    if (!this.city.equals(key)) {
                        this.city = key;
                        for (CityBean bean : currentCityList) {
                            if (bean.getN().equals(this.city)) {
                                changeDistrictDataSource(bean);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        } else {
            differenceY = height / 2f - cityMap.get(city) + dp5;
            this.city = city;
        }

        if (differenceY == 0 && city != null) {
            differenceY = height / 2f - cityMap.get(this.city) + dp5;
        }

        for (String key : cityMap.keySet()) {
            cityMap.put(key, cityMap.get(key) + differenceY);
        }
    }

    /**
     * 更改区的数据源
     *
     * @param bean 城市
     */
    private void changeDistrictDataSource(CityBean bean) {
        currentDistrictList = bean.getDistrict();
        districtMap = new LinkedHashMap<>();
        for (int i = 0; i < currentDistrictList.size(); i++) {
            districtMap.put(currentDistrictList.get(i).getN(), i * dp40 + 0f);
        }
        changeDistrict(currentDistrictList.get(0).getN());
    }

    /**
     * 更改市的数据源
     *
     * @param bean 省
     */
    private void changeCityDataSource(ProvinceBean bean) {
        currentCityList = bean.getCity();
        cityMap = new LinkedHashMap<>();
        for (int i = 0; i < currentCityList.size(); i++) {
            cityMap.put(currentCityList.get(i).getN(), i * dp40 + 0f);
        }
        changeCity(currentCityList.get(0).getN());

        changeDistrictDataSource(currentCityList.get(0));
    }

    private void changeProvince(String province) {
        float differenceY = 0f;
        if (province == null) {
            for (String key : provinceMap.keySet()) {
                float y = provinceMap.get(key);
                if (Math.abs(height / 2f - y + dp5) <= dp20) {
                    differenceY = height / 2f - y + dp5;
                    if (!this.province.equals(key)) {
                        this.province = key;
                        for (ProvinceBean bean : provinceBeans) {
                            if (bean.getN().equals(this.province)) {
                                changeCityDataSource(bean);
                                break;
                            }
                        }
                    }

                    break;
                }
            }
        } else {
            differenceY = height / 2f - provinceMap.get(province) + dp5;
            this.province = province;
        }

        if (differenceY == 0 && province != null) {
            differenceY = height / 2f - provinceMap.get(this.province) + dp5;
        }

        for (String key : provinceMap.keySet()) {
            provinceMap.put(key, provinceMap.get(key) + differenceY);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawLine(0, centerY - dp20, width, centerY - dp20, linePaint);
        canvas.drawLine(0, centerY + dp20, width, centerY + dp20, linePaint);

        canvas.drawText(provinceName, width / 3f - dp25, height / 2f + dp5, provinceCityDistrictPaint);
        canvas.drawText(cityName, width - width / 3f - dp25, height / 2f + dp5, provinceCityDistrictPaint);
        canvas.drawText(districtName, width - dp25, height / 2f + dp5, provinceCityDistrictPaint);
        if(provinceMap==null)
            return;
        for (String key : provinceMap.keySet()) {
            float y = provinceMap.get(key);
            if (y < 0) {
                continue;
            }
            if (y > height) {
                break;
            }
            canvas.drawText(key.length() > 6 ? key.substring(0, 6) + "..." : key, width / 8f, y, textPaint);
        }

        for (String key : cityMap.keySet()) {
            float y = cityMap.get(key);
            if (y < 0) {
                continue;
            }
            if (y > height) {
                break;
            }
            canvas.drawText(key.length() > 6 ? key.substring(0, 6) + "..." : key, width / 2.3f, y, textPaint);
        }

        for (String key : districtMap.keySet()) {
            float y = districtMap.get(key);
            if (y < 0) {
                continue;
            }
            if (y > height) {
                break;
            }
            canvas.drawText(key.length() > 6 ? key.substring(0, 6) + "..." : key, width / 1.3f, y, textPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e2.getX() < width / 3f) {
            if (provinceMap.get(provinceBeans.get(0).getN()) > height / 2f && distanceY < 0) {
                return true;
            }
            if (provinceMap.get(provinceBeans.get(provinceBeans.size() - 1).getN()) < height / 2f && distanceY > 0) {
                return true;
            }
            for (String key : provinceMap.keySet()) {
                float y = provinceMap.get(key);
                provinceMap.put(key, y - distanceY);
            }
        } else if (e2.getX() < width - width / 3f) {
            if (cityMap.get(currentCityList.get(0).getN()) > height / 2f && distanceY < 0) {
                return true;
            }
            if (cityMap.get(currentCityList.get(currentCityList.size() - 1).getN()) < height / 2f && distanceY > 0) {
                return true;
            }
            for (String key : cityMap.keySet()) {
                float y = cityMap.get(key);
                cityMap.put(key, y - distanceY);
            }
        } else {
            if (districtMap.get(currentDistrictList.get(0).getN()) > height / 2f && distanceY < 0) {
                return true;
            }
            if (districtMap.get(currentDistrictList.get(currentDistrictList.size() - 1).getN()) < height / 2f
                    && distanceY > 0) {
                return true;
            }
            for (String key : districtMap.keySet()) {
                float y = districtMap.get(key);
                districtMap.put(key, y - distanceY);
            }
        }
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    public int dp2px(float dpVal) {
        try {
            return (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dpVal, Resources.getSystem()
                            .getDisplayMetrics());
        } catch (Exception ex) {
            return 0;
        }

    }

    public interface Listener {
        void selected(String province, String city, String district);
    }
}
