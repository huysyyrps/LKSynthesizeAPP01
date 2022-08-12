package com.example.lksynthesizeapp.Constant.Module;

import com.example.lksynthesizeapp.Constant.Base.BaseEView;
import com.example.lksynthesizeapp.Constant.Base.BasePresenter;
import com.example.lksynthesizeapp.Constant.Bean.Defined;

/**
 * Created by Administrator on 2019/4/11.
 */

public interface DefinedContract {
    interface View extends BaseEView<presenter> {
        void setDefined(Defined defined);
        void setDefinedMessage(String message);
    }

    interface presenter extends BasePresenter {
        void getDefined(String pgd);
    }
}
