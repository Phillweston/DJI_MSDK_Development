package com.ew.autofly.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flycloud.autofly.base.base.BaseFragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;



public abstract class BaseMvpFragment<V, P extends BaseMvpPresenter<V>> extends BaseFragment {

    protected Context mContext;

    protected P mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setRootViewId(), container, false);
        initRootView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onViewCreated();
    }

    protected abstract int setRootViewId();

    protected abstract void initRootView(View view);

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected abstract P createPresenter();

    /* protected P createPresenter() {

         P p = instantiatePresenter();
         if (p == null) {
             throw new IllegalArgumentException("创建Presenter失败：泛型参数P错误");
         }
         return p;
     }
 */
    public P getPresenter() {
        return mPresenter;
    }

    /**
     * 返回上下文对象
     *
     * @return
     */
    protected Context getBaseContext() {
        return mContext;
    }

    /**
     * Presenter实例化
     *
     * @return
     */
    private P instantiatePresenter() {
        P p = null;
        try {
            ParameterizedType type = (ParameterizedType) this.getClass()
                    .getGenericSuperclass();
            Type[] actualTypeArguments = type.getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                try {
                    Object object = ((Class) actualTypeArgument).newInstance();
                    if (object instanceof BaseMvpPresenter) {
                        p = (P) object;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (p == null) {
                for (Type actualTypeArgument : actualTypeArguments) {
                    try {
                        TypeVariable typeVariable = (TypeVariable) actualTypeArgument;
                        Type[] bounds = typeVariable.getBounds();
                        for (Type bound : bounds) {
                            Object object = ((Class) bound).newInstance();
                            if (object instanceof BaseMvpPresenter) {
                                p = (P) object;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

}
