package com.example.lksynthesizeapp.ChiFen.Base;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.interfaces.IPickerViewData;
import com.example.lksynthesizeapp.MyApplication;
import com.example.lksynthesizeapp.SharePreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 地址弹框封装
 */
public class RegionalChooseUtil {

    private static String sendTag = "";
    private static List<RegionalBean> options1Items = new ArrayList<>();
    private static ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private static ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();


    /**
     * 弹框展示
     *
     * @return
     */
    public static void showPickerView(final Context context, final MyCallBack callBack) {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt2tx + opt3tx;
                ResultBean bean = new ResultBean(opt1tx, opt2tx, opt3tx);
                callBack.callBack(opt1tx + opt2tx);

            }
        })

                .setTitleText("")
                .setLineSpacingMultiplier(2.5f)
                .setItemVisibleCount(4)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(18)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器*/
       if (sendTag.equals("pxq")){
           pvOptions.setPicker(options1Items, options2Items);
       }else if (sendTag.equals("resolving")){
           String s = new SharePreferencesUtils().getString(MyApplication.getContext(), "resolving", "");
           pvOptions.setPicker(options1Items);
           if (s.equals("640x480")){
               pvOptions.setSelectOptions(0);
           }else if (s.equals("960x540")){
               pvOptions.setSelectOptions(1);
           }else if (s.equals("1280x720")){
               pvOptions.setSelectOptions(2);
           }
       }else if (sendTag.equals("frames")){
           String s = new SharePreferencesUtils().getString(MyApplication.getContext(), "frames", "");
           pvOptions.setPicker(options1Items);
           if (s.equals("15")){
               pvOptions.setSelectOptions(0);
           }else if (s.equals("25")){
               pvOptions.setSelectOptions(1);
           }else if (s.equals("30")){
               pvOptions.setSelectOptions(2);
           }
       }
//        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();

    }

    /**
     * 初始化弹框
     */
    public static void initJsonData(Context context, String tag) {//解析数据
        sendTag = tag;
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         * */
        String JsonData = "";
        if (tag.equals("pxq")) {
            JsonData = getJson(context, "patternselect.json");//获取assets目录下的json文件数据
        }
        if (tag.equals("resolving")) {
            JsonData = getJson(context, "settingresolving.json");//获取assets目录下的json文件数据
        }
        if (tag.equals("frames")) {
            JsonData = getJson(context, "settingframes.json");//获取assets目录下的json文件数据
        }

        ArrayList<RegionalBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }


    }

    private static ArrayList<RegionalBean> parseData(String result) {//Gson 解析
        ArrayList<RegionalBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                RegionalBean entity = gson.fromJson(data.optJSONObject(i).toString(), RegionalBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    //    读取Json文件
    private static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 地区省份选择
     */
    private class RegionalBean implements IPickerViewData {


        /**
         * name : 省份
         * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
         */

        private String name;
        private List<CityBean> city;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CityBean> getCityList() {
            return city;
        }

        public void setCityList(List<CityBean> city) {
            this.city = city;
        }

        // 实现 IPickerViewData 接口，
        // 这个用来显示在PickerView上面的字符串，
        // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
        @Override
        public String getPickerViewText() {
            return this.name;
        }

    }

    /**
     * 城市对象
     */
    private class CityBean {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */

        private String name;
        private List<String> area;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getArea() {
            return area;
        }

        public void setArea(List<String> area) {
            this.area = area;
        }
    }

    /**
     * 选择结果
     */
    public static class ResultBean {
        private String province;
        private String city;
        private String area;

        public ResultBean(String province, String city, String area) {
            this.province = province;
            this.city = city;
            this.area = area;
        }


        public ResultBean() {
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        @Override
        public String toString() {
            return "ProvinceBean{" +
                    "province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", area='" + area + '\'' +
                    '}';
        }
    }

}
