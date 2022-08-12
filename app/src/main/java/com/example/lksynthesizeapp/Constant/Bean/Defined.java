package com.example.lksynthesizeapp.Constant.Bean;

public class Defined {

    /**
     * result : {"companyName":"丹东通广射线仪器有限公司","deviceName":"充电交流磁粉探伤仪","deviceCode":"LKDAC-MT1"}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * companyName : 丹东通广射线仪器有限公司
         * deviceName : 充电交流磁粉探伤仪
         * deviceCode : LKDAC-MT1
         */

        private String companyName;
        private String deviceName;
        private String deviceCode;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }
    }
}
