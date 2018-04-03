package com.apps.waziup.ui.login;

import com.apps.waziup.data.model.AuthBody;
import com.apps.waziup.data.repo.user.UserRepoContract;
import com.apps.waziup.util.ActivityState;

import java.io.IOException;
import java.net.SocketTimeoutException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by KidusMT on 4/1/2018.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private UserRepoContract repository;
    private ActivityState state;
    LoginContract.View view;

    public LoginPresenter(UserRepoContract repository) {
        this.repository = repository;
        state = ActivityState.getInstance();
    }

    @Override
    public void start() {
        view.hideLoading();
    }

    @Override
    public void attachView(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public LoginContract.View getView() {
        return this.view;
    }

    @Override
    public void loginClicked(AuthBody authBody) {
        view.showLoading();

        repository
                .createUser(authBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        state.setCompleted();
                        if (view == null) return;
                        view.hideLoading();

                        state.reset();
                    }

                    @Override
                    public void onError(Throwable e) {
                        state.setError(e);

                        if (view == null) return;
                        view.showLoading();

                        if (e instanceof HttpException) {
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            view.onUnknownError(responseBody.toString());
                        } else if (e instanceof SocketTimeoutException) {
                            view.onTimeout();
                        } else if (e instanceof IOException) {
                            view.onNetworkError();
                        } else {
                            view.onUnknownError(e.getMessage());
                        }

                        e.printStackTrace();

                    }

                    @Override
                    public void onComplete() {
                        view.openHome();
                    }
                });
    }

    @Override
    public void registrationClicked() {
        view.openRegistration();
    }
}
